/* Sanrucho - usuarios.js
   Registro y login (companera) + funciones administrativas de usuarios
   (agregado con SANRUCHO_KEYS) usadas por las paginas admin. */

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

/* ==================== MANTENEDOR ADMINISTRATIVO DE USUARIOS ==================== */

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