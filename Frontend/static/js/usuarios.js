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