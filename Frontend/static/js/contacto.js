// contacto.js — Buzón de mensajes: guarda los mensajes enviados
// en la coleccion SANRUCHO_KEYS.contactos (localStorage) junto a storage.js.

document.addEventListener("DOMContentLoaded", function () {
    const formulario = document.querySelector(".formulario-contacto form");
    if (!formulario) return;

    formulario.addEventListener("submit", function (evento) {
        evento.preventDefault();
        evento.stopPropagation();

        if (!formulario.checkValidity()) {
            formulario.classList.add("was-validated");
            return;
        }

        const contactos = obtenerColeccion(SANRUCHO_KEYS.contactos);
        contactos.push({
            id: generarSiguienteId(contactos),
            nombre: document.getElementById("nombre").value.trim(),
            email: document.getElementById("email").value.trim().toLowerCase(),
            mensaje: document.getElementById("mensaje").value.trim(),
            fecha: new Date().toISOString().slice(0, 10)
        });
        guardarColeccion(SANRUCHO_KEYS.contactos, contactos);

        Swal.fire({
            title: "¡Mensaje enviado!",
            text: "Gracias por escribirnos, te responderemos pronto.",
            icon: "success",
            confirmButtonText: "Listo"
        });

        formulario.reset();
        formulario.classList.remove("was-validated");
    });
});