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