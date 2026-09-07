//----------------------Validación para inicio de sesión en index--------------------------//

(function inicioSesion() {
        'use strict'
        // Obtener todos los formularios a los que queremos aplicar estilos de validación
        var forms = document.querySelectorAll('.needs-validation')

        // Bucle sobre ellos y evitar el envío si no son válidos
        Array.prototype.slice.call(forms)
            .forEach(function (form) {
                form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault()
                    event.stopPropagation()
                }
                form.classList.add('was-validated')
                }, false)
            })
        })()

//-----------------------Validación para formulario de contacto---------------------------------//

document.addEventListener("DOMContentLoaded", function () {
    const formContacto = document.querySelector("form.needs-validation");

    if (!formContacto) return;

    formContacto.addEventListener("submit", function (evento) {
        evento.preventDefault();
        evento.stopPropagation();

        const inputNombre = document.getElementById("nombre");
        const inputEmail = document.getElementById("email");
        const inputMensaje = document.getElementById("mensaje");

        const nombreValor = inputNombre.value.trim();
        const emailValor = inputEmail.value.trim();
        const mensajeValor = inputMensaje.value.trim();

        // Validar si cumple los requisitos y que no sean solo espacios
        let formularioValido = formContacto.checkValidity();

        if (nombreValor === "" || mensajeValor === "") {
            formularioValido = false;
        }

        if (!formularioValido) {
            formContacto.classList.add("was-validated");
            return;
        }

        Swal.fire({
            icon: 'success',
            title: '¡Mensaje Enviado!',
            text: `Gracias por contactarnos, ${nombreValor}. Te responderemos a la brevedad.`,
            confirmButtonColor: '#ff69b4'
        });

        // Limpia el formulario
        formContacto.reset();
        formContacto.classList.remove("was-validated");
    });
});