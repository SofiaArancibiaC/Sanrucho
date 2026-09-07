
// Un solo archivo para admin-usuarios.html y admin.html.
// Detecta la página según los elementos presentes en el DOM.
// Depende de validaciones.js y storage.js : escaparHTML(), mostrarToast()
// ============================================================

const API_URL = 'http://localhost:8082/api/usuarios';

const esPaginaUsuarios = () => !!document.getElementById('tabla-usuarios');
const esPaginaPanel    = () => !!document.getElementById('tabla-body');

let usuarios = [];
let editando = false;
let datosActuales = [];
let seccionActual = 'usuarios';

// ============================================================
// INICIALIZACIÓN SEGÚN LA PÁGINA
// ============================================================
document.addEventListener('DOMContentLoaded', () => {
    if (esPaginaUsuarios()) {
        cargarUsuarios();
    } else if (esPaginaPanel()) {
        cargarDatos();
        actualizarTarjetas();
        const bus = document.getElementById('busqueda');
        if (bus) bus.addEventListener('input', buscar);
    }
});

// ============================================================
// ADMIN-USUARIOS.HTML — tabla de usuarios + CRUD
// ============================================================
async function cargarUsuarios() {
    const tbody = document.getElementById('tabla-usuarios');
    tbody.innerHTML = '<tr class="loading-row"><td colspan="8">Cargando usuarios...</td></tr>';
    try {
        const resp = await fetch(API_URL);
        if (!resp.ok) throw new Error('Error HTTP ' + resp.status);
        usuarios = await resp.json();
        renderTabla(usuarios);
        mostrarToast('Usuarios cargados correctamente', 'success');
    } catch (e) {
        tbody.innerHTML = '<tr class="loading-row"><td colspan="8">Error al conectar con el servidor. ¿Está corriendo el microservicio? <br> <span class="text-muted">' + escaparHTML(e.message) + '</span></td></tr>';
        mostrarToast('No se pudo cargar: ' + e.message, 'error');
    }
}

function renderTabla(lista) {
    const tbody = document.getElementById('tabla-usuarios');
    document.getElementById('contador').textContent = lista.length + ' usuario(s)';

    if (lista.length === 0) {
        tbody.innerHTML = '<tr class="loading-row"><td colspan="8">No hay usuarios para mostrar.</td></tr>';
        return;
    }

    tbody.innerHTML = lista.map(u => {
        const rolBadge = u.rol === 'ADMIN' ? 'badge-admin' : 'badge-cliente';
        const estadoBadge = u.activo ? 'badge-activo' : 'badge-inactivo';
        const estadoTexto = u.activo ? 'Activo' : 'Inactivo';
        const fecha = u.fechaRegistro ? formatearFecha(u.fechaRegistro) : '—';
        return `
            <tr>
                <td>${u.id}</td>
                <td><strong>${escaparHTML(u.nombre)}</strong> ${u.apellido ? escaparHTML(' ' + u.apellido) : ''}</td>
                <td>${escaparHTML(u.email)}</td>
                <td>${u.direccion ? escaparHTML(u.direccion) : '—'}</td>
                <td><span class="badge ${rolBadge}">${escaparHTML(u.rol)}</span></td>
                <td><span class="badge ${estadoBadge}">${estadoTexto}</span></td>
                <td>${fecha}</td>
                <td>
                    <div class="actions">
                        <button class="btn btn-edit btn-sm" onclick="abrirModalEditar(${u.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="eliminarUsuario(${u.id})">Eliminar</button>
                    </div>
                </td>
            </tr>`;
    }).join('');
}

function filtrarTabla() {
    const termino = document.getElementById('busqueda').value.trim().toLowerCase();
    if (!termino) { renderTabla(usuarios); return; }
    const filtrados = usuarios.filter(u =>
        (u.nombre || '').toLowerCase().includes(termino) ||
        (u.apellido || '').toLowerCase().includes(termino) ||
        (u.email || '').toLowerCase().includes(termino) ||
        (u.rol || '').toLowerCase().includes(termino) ||
        (u.direccion || '').toLowerCase().includes(termino)
    );
    renderTabla(filtrados);
}

function abrirModalEditar(id) {
    const u = usuarios.find(x => x.id === id);
    if (!u) return;
    editando = true;
    document.getElementById('modal-titulo').textContent = 'Editar usuario #' + id;
    document.getElementById('usuario-id').value = u.id;
    document.getElementById('nombre').value = u.nombre || '';
    document.getElementById('apellido').value = u.apellido || '';
    document.getElementById('email').value = u.email || '';
    document.getElementById('direccion').value = u.direccion || '';
    document.getElementById('password').value = '';
    document.getElementById('rol').value = u.rol || 'CLIENTE';
    document.getElementById('activo').checked = u.activo;
    document.getElementById('nota-password').style.display = 'block';
    document.getElementById('modal-usuario').classList.add('show');
}

async function guardarUsuario() {
    const nombre = document.getElementById('nombre').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const id = document.getElementById('usuario-id').value;

    if (!nombre || !email) {
        mostrarToast('Nombre y email son obligatorios', 'error');
        return;
    }
    if (!editando && !password) {
        mostrarToast('La contraseña es obligatoria para un nuevo usuario', 'error');
        return;
    }

    const usuario = {
        nombre,
        apellido: document.getElementById('apellido').value.trim(),
        email,
        direccion: document.getElementById('direccion').value.trim(),
        rol: document.getElementById('rol').value,
        activo: document.getElementById('activo').checked
    };
    if (password) usuario.password = password;

    try {
        let resp;
        if (editando) {
            resp = await fetch(API_URL + '/' + id, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(usuario)
            });
        } else {
            resp = await fetch(API_URL + '/registro', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(usuario)
            });
        }
        if (!resp.ok) throw new Error(await resp.text() || 'Error del servidor');

        cerrarModal();
        await cargarUsuarios();
        mostrarToast(editando ? 'Usuario actualizado' : 'Usuario creado', 'success');
    } catch (e) {
        mostrarToast('Error al guardar: ' + e.message, 'error');
    }
}

async function eliminarUsuario(id) {
    const u = usuarios.find(x => x.id === id);
    const confirmacion = confirm('¿Deseas eliminar al usuario "' + (u ? u.email : id) + '"?');
    if (!confirmacion) return;

    try {
        const resp = await fetch(API_URL + '/' + id, { method: 'DELETE' });
        if (!resp.ok) throw new Error('Error HTTP ' + resp.status);
        await cargarUsuarios();
        mostrarToast('Usuario eliminado', 'success');
    } catch (e) {
        mostrarToast('Error al eliminar: ' + e.message, 'error');
    }
}

// ============================================================
// ADMIN.HTML — panel (indicadores, secciones, tabla dinámica)
// ============================================================
async function cargarDatos() {
    const tbody = document.getElementById('tabla-body');
    tbody.innerHTML = '<tr class="loading-row"><td colspan="6">Cargando datos...</td></tr>';
    try {
        const resp = await fetch('/api/proxy/' + seccionActual);
        if (!resp.ok) throw new Error('Error HTTP ' + resp.status);
        datosActuales = await resp.json();
        if (!Array.isArray(datosActuales)) throw new Error('El microservicio no devolvió una lista');
        renderTablaPanel(datosActuales);
        actualizarTarjetas();
        mostrarToast('Datos de ' + seccionActual + ' cargados', 'success');
    } catch (e) {
        tbody.innerHTML = '<tr class="loading-row"><td colspan="6">Error al conectar: ' + e.message + '</td></tr>';
        mostrarToast('No se pudo cargar: ' + e.message, 'error');
    }
}

function renderTablaPanel(lista) {
    const head = document.getElementById('tabla-head');
    const tbody = document.getElementById('tabla-body');

    if (lista.length === 0) {
        head.innerHTML = '';
        tbody.innerHTML = '<tr class="loading-row"><td colspan="6">No hay registros para mostrar.</td></tr>';
        return;
    }

    const columnas = Object.keys(lista[0]).slice(0, 5);
    head.innerHTML = '<tr>' + columnas.map(c => '<th>' + c + '</th>').join('') + '<th>Acciones</th></tr>';

    tbody.innerHTML = lista.map(item => {
        const celdas = columnas.map(c => {
            const valor = item[c];
            return '<td>' + (valor == null || valor === '' ? '—' : escaparHTML(String(valor))) + '</td>';
        }).join('');
        return '<tr>' + celdas +
            '<td><button class="btn btn-danger btn-sm" onclick="eliminarRegistro(' + JSON.stringify(item) + ')">Eliminar</button></td></tr>';
    }).join('');
}

function buscar() {
    const termino = document.getElementById('busqueda').value.trim().toLowerCase();
    if (!termino) { renderTablaPanel(datosActuales); return; }
    const filtrados = datosActuales.filter(item =>
        Object.values(item).some(v => v != null && String(v).toLowerCase().includes(termino))
    );
    renderTablaPanel(filtrados);
}

function cambiarSeccion(valor) {
    seccionActual = valor;
    document.getElementById('seccion-actual').textContent = valor;
    document.getElementById('busqueda').value = '';
    cargarDatos();
}

async function actualizarTarjetas() {
    const secciones = ['usuarios', 'productos', 'pedidos', 'catalogo'];
    for (const sec of secciones) {
        const el = document.querySelector('[data-card="' + sec + '"]');
        if (!el) continue;
        try {
            const resp = await fetch('/api/proxy/' + sec);
            if (!resp.ok) throw new Error('HTTP ' + resp.status);
            const datos = await resp.json();
            el.textContent = Array.isArray(datos) ? datos.length : '?';
        } catch (e) {
            el.textContent = 'N/A';
        }
    }
}

function guardarRegistro() {
    cerrarModal();
    mostrarToast('Guardado simulado: el proxy aún no implementa POST/PUT', 'info');
}

function eliminarRegistro(item) {
    const ok = confirm('¿Eliminar el registro? (El proxy aún no implementa DELETE)');
    if (ok) mostrarToast('Eliminado simulado', 'info');
}

// ============================================================
// FUNCIONES COMPARTIDAS POR AMBAS PÁGINAS
// ============================================================
function abrirModalNuevo() {
    if (esPaginaUsuarios()) {
        editando = false;
        document.getElementById('modal-titulo').textContent = 'Nuevo usuario';
        document.getElementById('usuario-id').value = '';
        document.getElementById('nombre').value = '';
        document.getElementById('apellido').value = '';
        document.getElementById('email').value = '';
        document.getElementById('direccion').value = '';
        document.getElementById('password').value = '';
        document.getElementById('rol').value = 'CLIENTE';
        document.getElementById('activo').checked = true;
        document.getElementById('nota-password').style.display = 'none';
        document.getElementById('modal-usuario').classList.add('show');
        document.getElementById('nombre').focus();
    } else {
        document.getElementById('modal-titulo').textContent = 'Nuevo registro en ' + seccionActual;
        document.getElementById('campo-principal').value = '';
        document.getElementById('modal-registro').classList.add('show');
        document.getElementById('campo-principal').focus();
    }
}

function cerrarModal() {
    ['modal-usuario', 'modal-registro'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.classList.remove('show');
    });
}

function formatearFecha(fechaStr) {
    const f = new Date(fechaStr);
    if (isNaN(f.getTime())) return fechaStr;
    return f.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' }) +
        ' ' + f.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
}
document.addEventListener("DOMContentLoaded", function () {


    // REGISTRO -------------------------------------------------------------------------------------------------
    const formRegistro = document.querySelector(".registro-container form");

    if (formRegistro) {
        formRegistro.addEventListener("submit", function (evento) {
            evento.preventDefault(); 
            evento.stopPropagation();

            const inputNombre = document.getElementById("nombre");
            const inputEmail = document.getElementById("email");
            const inputPassword = document.getElementById("password");
            const inputConfirm = document.getElementById("confirm_password");

            if (!formRegistro.checkValidity()) {
                formRegistro.classList.add("was-validated");
                return;
            }

            const nombre = inputNombre.value.trim();
            const correo = inputEmail.value.trim().toLowerCase();
            const password = inputPassword.value;
            const confirmPassword = inputConfirm.value;

            if (password !== confirmPassword) {
                alert("Las contraseñas no coinciden.");
                inputConfirm.focus();
                return;
            }

            const usuarios = obtenerColeccion(SANRUCHO_KEYS.usuarios);

            const usuarioExistente = usuarios.find(u => u.correo.toLowerCase() === correo);
            if (usuarioExistente) {
                alert("El correo electrónico ya se encuentra registrado.");
                inputEmail.focus();
                return;
            }

            // Crear el nuevo usuario (rolId: 3 corresponde a Cliente)
            const nuevoUsuario = {
                run: Date.now().toString().slice(-9), // RUN provisorio único
                nombre: nombre,
                apellidos: "",
                correo: correo,
                password: password,
                fechaNacimiento: "",
                rolId: 3, // Rol "Cliente"
                regionId: 1,
                comunaId: 1,
                direccion: "",
                estado: "Activo"
            };

            // Guardar en storage
            usuarios.push(nuevoUsuario);
            guardarColeccion(SANRUCHO_KEYS.usuarios, usuarios);


            guardarSesion(nuevoUsuario);

            alert("¡Registro exitoso! Bienvenido a Sanrucho.");
            window.location.href = "/"; 
        });
    }


    // LOGIN------------------------------------------------------------------------------------------------
    const formLogin = document.querySelector(".login-container:not(.registro-container) form");

    if (formLogin) {
        formLogin.addEventListener("submit", function (evento) {
            evento.preventDefault(); 
            evento.stopPropagation();

            const inputEmail = document.getElementById("email");
            const inputPassword = document.getElementById("password");

            if (!formLogin.checkValidity()) {
                formLogin.classList.add("was-validated");
                return;
            }

            const correo = inputEmail.value.trim().toLowerCase();
            const password = inputPassword.value;

            // Buscar en la colección de usuarios
            const usuarios = obtenerColeccion(SANRUCHO_KEYS.usuarios);
            const usuarioValido = usuarios.find(u => 
                u.correo.toLowerCase() === correo && u.password === password
            );

            if (!usuarioValido) {
                alert("Correo electrónico o contraseña incorrectos.");
                return;
            }

            if (usuarioValido.estado !== "Activo") {
                alert("Tu cuenta está inactiva o suspendida.");
                return;
            }

            // Guardar usuario en sesión (función de storage.js)
            guardarSesion(usuarioValido);

            alert(`¡Hola de nuevo, ${usuarioValido.nombre}!`);

            // Redirección según rol (1: Dueño, 2: Admin, 3: Cliente)
            if (usuarioValido.rolId === 1 || usuarioValido.rolId === 2) {
                window.location.href = "/admin";
            } else {
                window.location.href = "/";
            }
        });
    }
});
