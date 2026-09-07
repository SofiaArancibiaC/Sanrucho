// ============================================================
// usuarios.js — Login, registro y mantenedor administrativo de usuarios.
// Todo funciona con las colecciones de SANRUCHO_KEYS (storage.js).
// Base.js aporta escaparHTML() y mostrarToast().
// ============================================================

/* =========================== LISTADO ADMIN (admin-usuarios.html) =========================== */

function cargarUsuarios() {
    const tbody = document.getElementById('tabla-usuarios');
    const usuarios = obtenerColeccion(SANRUCHO_KEYS.usuarios);

    document.getElementById('contador').textContent = usuarios.length + ' usuario(s)';

    if (usuarios.length === 0) {
        tbody.innerHTML = '<tr class="loading-row"><td colspan="8">No hay usuarios para mostrar.</td></tr>';
        return;
    }

    tbody.innerHTML = usuarios.map(function (u) {
        const rolNombre = u.rolId ? obtenerNombreRol(u.rolId) : (u.rol || '');
        const rolBadge = u.rolId === 3 ? 'badge-cliente' : 'badge-admin';
        const activo = u.estado === 'Activo';
        const estadoBadge = activo ? 'badge-activo' : 'badge-inactivo';
        return '<tr>' +
            '<td>' + escaparHTML(u.run) + '</td>' +
            '<td><strong>' + escaparHTML(u.nombre) + '</strong> ' + (u.apellidos ? escaparHTML(u.apellidos) : '') + '</td>' +
            '<td>' + escaparHTML(u.correo) + '</td>' +
            '<td>' + (u.direccion ? escaparHTML(u.direccion) : '—') + '</td>' +
            '<td><span class="badge ' + rolBadge + '">' + escaparHTML(rolNombre) + '</span></td>' +
            '<td><span class="badge ' + estadoBadge + '">' + (activo ? 'Activo' : 'Inactivo') + '</span></td>' +
            '<td>' + (u.fechaNacimiento || '—') + '</td>' +
            '<td><div class="actions">' +
            '<button class="btn btn-edit btn-sm" onclick="window.location.href=\'/admin/usuarios/form?run=' + encodeURIComponent(u.run) + '\'">Editar</button>' +
            '<button class="btn btn-danger btn-sm" onclick="eliminarUsuario(\'' + u.run + '\')">Eliminar</button>' +
            '</div></td>' +
            '</tr>';
    }).join('');
}

function filtrarTabla() {
    const termino = document.getElementById('busqueda').value.trim().toLowerCase();
    const usuarios = obtenerColeccion(SANRUCHO_KEYS.usuarios);
    if (!termino) {
        cargarUsuarios();
        return;
    }
    const filtrados = usuarios.filter(function (u) {
        return (u.nombre || '').toLowerCase().includes(termino) ||
            (u.apellidos || '').toLowerCase().includes(termino) ||
            (u.correo || '').toLowerCase().includes(termino) ||
            (u.run || '').toLowerCase().includes(termino) ||
            (u.direccion || '').toLowerCase().includes(termino) ||
            (obtenerNombreRol(u.rolId) || '').toLowerCase().includes(termino);
    });
    const tbody = document.getElementById('tabla-usuarios');
    document.getElementById('contador').textContent = filtrados.length + ' usuario(s)';
    if (filtrados.length === 0) {
        tbody.innerHTML = '<tr class="loading-row"><td colspan="8">Sin resultados para la búsqueda.</td></tr>';
        return;
    }
    const original = obtenerColeccion(SANRUCHO_KEYS.usuarios);
    const runSet = filtrados.map(function (u) { return u.run; });
    tbody.innerHTML = original.map(function (u) {
        if (runSet.indexOf(u.run) === -1) return '';
        const rolNombre = u.rolId ? obtenerNombreRol(u.rolId) : '';
        const rolBadge = u.rolId === 3 ? 'badge-cliente' : 'badge-admin';
        const activo = u.estado === 'Activo';
        return '<tr>' +
            '<td>' + escaparHTML(u.run) + '</td>' +
            '<td><strong>' + escaparHTML(u.nombre) + '</strong> ' + (u.apellidos ? escaparHTML(u.apellidos) : '') + '</td>' +
            '<td>' + escaparHTML(u.correo) + '</td>' +
            '<td>' + (u.direccion ? escaparHTML(u.direccion) : '—') + '</td>' +
            '<td><span class="badge ' + rolBadge + '">' + escaparHTML(rolNombre) + '</span></td>' +
            '<td><span class="badge ' + (activo ? 'badge-activo' : 'badge-inactivo') + '">' + (activo ? 'Activo' : 'Inactivo') + '</span></td>' +
            '<td>' + (u.fechaNacimiento || '—') + '</td>' +
            '<td><div class="actions">' +
            '<button class="btn btn-edit btn-sm" onclick="window.location.href=\'/admin/usuarios/form?run=' + encodeURIComponent(u.run) + '\'">Editar</button>' +
            '<button class="btn btn-danger btn-sm" onclick="eliminarUsuario(\'' + u.run + '\')">Eliminar</button>' +
            '</div></td>' +
            '</tr>';
    }).join('');
}

function eliminarUsuario(run) {
    const sesion = obtenerSesion();
    if (sesion && sesion.run === run) {
        mostrarToast('No puedes eliminar tu propia cuenta', 'error');
        return;
    }
    if (!confirm('¿Deseas eliminar al usuario ' + run + '?')) return;

    const usuarios = obtenerColeccion(SANRUCHO_KEYS.usuarios).filter(function (u) { return u.run !== run; });
    guardarColeccion(SANRUCHO_KEYS.usuarios, usuarios);
    mostrarToast('Usuario eliminado', 'success');
    cargarUsuarios();
}

/* =========================== REGISTRO Y LOGIN (paginas publicas) =========================== */

document.addEventListener("DOMContentLoaded", function () {
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

            if (usuarios.find(u => u.correo.toLowerCase() === correo)) {
                alert("El correo electrónico ya se encuentra registrado.");
                inputEmail.focus();
                return;
            }

            const nuevoUsuario = {
                run: Date.now().toString().slice(-9),
                nombre: nombre,
                apellidos: "",
                correo: correo,
                password: password,
                fechaNacimiento: "",
                rolId: 3,
                regionId: 1,
                comunaId: 1,
                direccion: "",
                estado: "Activo"
            };

            usuarios.push(nuevoUsuario);
            guardarColeccion(SANRUCHO_KEYS.usuarios, usuarios);
            guardarSesion(nuevoUsuario);

            alert("¡Registro exitoso! Bienvenido a Sanrucho.");
            window.location.href = "/";
        });
    }

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

            guardarSesion(usuarioValido);
            alert(`¡Hola de nuevo, ${usuarioValido.nombre}!`);

            if (usuarioValido.rolId === 1 || usuarioValido.rolId === 2) {
                window.location.href = "/admin";
            } else {
                window.location.href = "/";
            }
        });
    }
});

/* ==================== MANTENEDOR ADMINISTRATIVO DE USUARIOS (admin-usuario-form.html) ==================== */

function obtenerUsuarios() {
    return obtenerColeccion(SANRUCHO_KEYS.usuarios);
}

function obtenerUsuarioPorRun(run) {
    return obtenerUsuarios().find(function (u) { return u.run === run; });
}

function llenarSelectRoles(idSelect) {
    const select = document.getElementById(idSelect);
    if (!select) return;
    const roles = obtenerColeccion(SANRUCHO_KEYS.roles);
    select.innerHTML = '<option value="">Seleccione un tipo de usuario</option>';
    roles.forEach(function (rol) {
        select.innerHTML += '<option value="' + rol.id + '">' + rol.nombre + '</option>';
    });
}

function llenarSelectRegiones(idSelect) {
    const select = document.getElementById(idSelect);
    if (!select) return;
    const regiones = obtenerColeccion(SANRUCHO_KEYS.regiones);
    select.innerHTML = '<option value="">Seleccione una region</option>';
    regiones.forEach(function (region) {
        select.innerHTML += '<option value="' + region.id + '">' + region.nombre + '</option>';
    });
}

/* Filtra las comunas segun la region seleccionada. */
function llenarSelectComunas(idSelectRegion, idSelectComuna, comunaSeleccionada) {
    const selectRegion = document.getElementById(idSelectRegion);
    const selectComuna = document.getElementById(idSelectComuna);
    if (!selectRegion || !selectComuna) return;

    const regionId = Number(selectRegion.value);
    const comunas = obtenerColeccion(SANRUCHO_KEYS.comunas).filter(function (c) { return c.regionId === regionId; });

    selectComuna.innerHTML = '<option value="">Seleccione una comuna</option>';
    comunas.forEach(function (comuna) {
        selectComuna.innerHTML += '<option value="' + comuna.id + '">' + comuna.nombre + '</option>';
    });

    if (comunaSeleccionada) {
        selectComuna.value = comunaSeleccionada;
    }
}

function guardarUsuario(usuario) {
    const usuarios = obtenerUsuarios();
    const indice = usuarios.findIndex(function (u) { return u.run === usuario.run; });
    if (indice === -1) {
        usuarios.push(usuario);
    } else {
        usuarios[indice] = usuario;
    }
    guardarColeccion(SANRUCHO_KEYS.usuarios, usuarios);
}

/* Prepara el formulario Nuevo/Editar segun exista ?run= en la URL. */
function inicializarFormularioUsuarioAdmin() {
    llenarSelectRoles("rolUsuario");
    llenarSelectRegiones("regionUsuario");

    document.getElementById("regionUsuario").addEventListener("change", function () {
        llenarSelectComunas("regionUsuario", "comunaUsuario");
    });

    const parametros = new URLSearchParams(window.location.search);
    const run = parametros.get("run");

    if (run) {
        const usuario = obtenerUsuarioPorRun(run);
        if (usuario) {
            document.getElementById("tituloFormularioUsuario").textContent = "Editar usuario";
            document.getElementById("runUsuario").value = usuario.run;
            document.getElementById("runUsuario").readOnly = true;
            document.getElementById("nombreUsuario").value = usuario.nombre;
            document.getElementById("apellidosUsuario").value = usuario.apellidos;
            document.getElementById("correoUsuario").value = usuario.correo;
            document.getElementById("passwordUsuario").value = usuario.password;
            document.getElementById("fechaNacimientoUsuario").value = usuario.fechaNacimiento || "";
            document.getElementById("rolUsuario").value = usuario.rolId;
            document.getElementById("regionUsuario").value = usuario.regionId;
            llenarSelectComunas("regionUsuario", "comunaUsuario", usuario.comunaId);
            document.getElementById("direccionUsuario").value = usuario.direccion;
            document.getElementById("estadoUsuario").value = usuario.estado || "Activo";
        }
    }

    document.getElementById("formularioUsuario").addEventListener("submit", function (evento) {
        evento.preventDefault();
        procesarFormularioUsuarioAdmin(run);
    });
}

function procesarFormularioUsuarioAdmin(runOriginal) {
    const run = document.getElementById("runUsuario");
    const nombre = document.getElementById("nombreUsuario");
    const apellidos = document.getElementById("apellidosUsuario");
    const correo = document.getElementById("correoUsuario");
    const password = document.getElementById("passwordUsuario");
    const fechaNacimiento = document.getElementById("fechaNacimientoUsuario");
    const rol = document.getElementById("rolUsuario");
    const region = document.getElementById("regionUsuario");
    const comuna = document.getElementById("comunaUsuario");
    const direccion = document.getElementById("direccionUsuario");
    const estado = document.getElementById("estadoUsuario");

    let esValido = true;

    if (validarRun(run.value)) {
        marcarValido(run);
    } else {
        marcarInvalido(run);
        esValido = false;
    }

    const runDuplicado = obtenerUsuarios().some(function (u) {
        return u.run === run.value.toUpperCase().trim() && u.run !== runOriginal;
    });
    if (runDuplicado) {
        marcarInvalido(run);
        esValido = false;
    }

    if (textoRequerido(nombre.value, 50)) marcarValido(nombre); else { marcarInvalido(nombre); esValido = false; }
    if (textoRequerido(apellidos.value, 100)) marcarValido(apellidos); else { marcarInvalido(apellidos); esValido = false; }
    if (validarCorreo(correo.value)) marcarValido(correo); else { marcarInvalido(correo); esValido = false; }
    if (validarPassword(password.value)) marcarValido(password); else { marcarInvalido(password); esValido = false; }
    if (rol.value) marcarValido(rol); else { marcarInvalido(rol); esValido = false; }
    if (region.value) marcarValido(region); else { marcarInvalido(region); esValido = false; }
    if (comuna.value) marcarValido(comuna); else { marcarInvalido(comuna); esValido = false; }
    if (textoRequerido(direccion.value, 300)) marcarValido(direccion); else { marcarInvalido(direccion); esValido = false; }

    if (!esValido) {
        Swal.fire("Revisa el formulario", "Existen campos obligatorios o invalidos.", "error");
        return;
    }

    const usuario = {
        run: run.value.toUpperCase().trim(),
        nombre: nombre.value.trim(),
        apellidos: apellidos.value.trim(),
        correo: correo.value.trim(),
        password: password.value,
        fechaNacimiento: fechaNacimiento.value || "",
        rolId: Number(rol.value),
        regionId: Number(region.value),
        comunaId: Number(comuna.value),
        direccion: direccion.value.trim(),
        estado: estado.value
    };

    guardarUsuario(usuario);

    Swal.fire("Correcto", "Usuario guardado correctamente.", "success").then(function () {
        window.location.href = "/admin/usuarios";
    });
}