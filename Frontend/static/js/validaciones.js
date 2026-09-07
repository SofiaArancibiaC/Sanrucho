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
const DOMINIOS_CORREO_VALIDOS = ["@duoc.cl", "@profesor.duoc.cl", "@gmail.com"];

function validarCorreo(correo) {
    if (!correo || correo.length === 0 || correo.length > 100) return false;
    return DOMINIOS_CORREO_VALIDOS.some(function (dominio) {
        return correo.toLowerCase().endsWith(dominio);
    });
}

function validarPassword(password) {
    return typeof password === "string" && password.length >= 4 && password.length <= 10;
}

/* Valida un RUN chileno sin puntos ni guion (ej: 123456785) usando el algoritmo modulo 11. */
function validarRun(run) {
    if (!run) return false;
    run = run.toUpperCase().trim();
    if (run.length < 7 || run.length > 9) return false;

    const cuerpo = run.slice(0, -1);
    const dv = run.slice(-1);

    if (!/^[0-9]+$/.test(cuerpo)) return false;
    if (!/^[0-9K]$/.test(dv)) return false;

    let suma = 0;
    let multiplicador = 2;
    for (let i = cuerpo.length - 1; i >= 0; i--) {
        suma += parseInt(cuerpo.charAt(i), 10) * multiplicador;
        multiplicador = multiplicador === 7 ? 2 : multiplicador + 1;
    }

    const resto = 11 - (suma % 11);
    let dvEsperado;
    if (resto === 11) dvEsperado = "0";
    else if (resto === 10) dvEsperado = "K";
    else dvEsperado = String(resto);

    return dv === dvEsperado;
}

function marcarValido(input) {
    input.classList.remove("is-invalid");
    input.classList.add("is-valid");
}

function marcarInvalido(input) {
    input.classList.remove("is-valid");
    input.classList.add("is-invalid");
}

function textoRequerido(valor, maximo) {
    if (!valor || valor.trim().length === 0) return false;
    if (maximo && valor.trim().length > maximo) return false;
    return true;
}

function numeroEnRango(valor, minimo) {
    const numero = Number(valor);
    if (isNaN(numero)) return false;
    return numero >= minimo;
}

function enteroEnRango(valor, minimo) {
    const numero = Number(valor);
    if (isNaN(numero)) return false;
    return Number.isInteger(numero) && numero >= minimo;
}
