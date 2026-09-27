document.addEventListener("DOMContentLoaded", function () {
    const contenedorResenas = document.getElementById("contenedorResenas");
    if (!contenedorResenas) return;

    const params = new URLSearchParams(window.location.search);
    const codigoProducto = params.get("codigo");

    const formResena = document.getElementById("formResena");
    const inputCalificacion = document.getElementById("inputCalificacion");
    const inputComentario = document.getElementById("inputComentario");
    const avisoLogin = document.getElementById("avisoLoginResena");

    function obtenerResenasProducto() {
        return obtenerColeccion(SANRUCHO_KEYS.resenas)
            .filter(r => r.productoCodigo === codigoProducto)
            .sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
    }

    function renderizarEstrellas(calificacion) {
        let html = "";
        for (let i = 1; i <= 5; i++) {
            html += i <= calificacion
                ? '<i class="bi bi-star-fill text-warning"></i>'
                : '<i class="bi bi-star text-warning"></i>';
        }
        return html;
    }

    function renderizarResumen(resenas) {
        const resumen = document.getElementById("resumenResenas");
        if (!resumen) return;

        if (resenas.length === 0) {
            resumen.innerHTML = `<p class="text-muted mb-0">Este producto todavía no tiene reseñas.</p>`;
            return;
        }

        const promedio = resenas.reduce((acc, r) => acc + r.calificacion, 0) / resenas.length;

        resumen.innerHTML = `
            <div class="d-flex align-items-center gap-2">
                <span class="fs-4 fw-bold">${promedio.toFixed(1)}</span>
                <div>${renderizarEstrellas(Math.round(promedio))}</div>
                <span class="text-muted">(${resenas.length} reseña${resenas.length === 1 ? "" : "s"})</span>
            </div>
        `;
    }

    function renderizarLista(resenas) {
        if (resenas.length === 0) {
            contenedorResenas.innerHTML = `
                <div class="text-center py-4">
                    <p class="text-muted">Sé el primero en dejar una reseña de este producto.</p>
                </div>`;
            return;
        }

        contenedorResenas.innerHTML = resenas.map(r => `
            <div class="resena-card mb-3">
                <div class="d-flex justify-content-between align-items-start">
                    <strong>${r.usuarioNombre}</strong>
                    <small class="text-muted">${new Date(r.fecha).toLocaleDateString("es-CL")}</small>
                </div>
                <div class="mb-2">${renderizarEstrellas(r.calificacion)}</div>
                <p class="mb-0 text-muted">${r.comentario}</p>
            </div>
        `).join("");
    }

    function refrescarResenas() {
        const resenas = obtenerResenasProducto();
        renderizarResumen(resenas);
        renderizarLista(resenas);
    }

    const sesion = obtenerSesion();
    if (formResena) {
        if (!sesion) {
            formResena.classList.add("d-none");
            if (avisoLogin) avisoLogin.classList.remove("d-none");
        } else {
            formResena.classList.remove("d-none");
            if (avisoLogin) avisoLogin.classList.add("d-none");
        }

        formResena.addEventListener("submit", function (evento) {
            evento.preventDefault();
            evento.stopPropagation();

            const calificacion = parseInt(inputCalificacion.value, 10);
            const comentario = inputComentario.value.trim();

            if (!calificacion || comentario === "") {
                formResena.classList.add("was-validated");
                return;
            }

            const resenas = obtenerColeccion(SANRUCHO_KEYS.resenas);

            const nuevaResena = {
                id: generarSiguienteId(resenas),
                productoCodigo: codigoProducto,
                usuarioNombre: sesion.nombre,
                calificacion: calificacion,
                comentario: comentario,
                fecha: new Date().toISOString().slice(0, 10)
            };

            resenas.push(nuevaResena);
            guardarColeccion(SANRUCHO_KEYS.resenas, resenas);

            formResena.reset();
            formResena.classList.remove("was-validated");
            inputCalificacion.value = "5";

            refrescarResenas();

            Swal.fire({
                toast: true,
                position: "bottom-end",
                icon: "success",
                title: "¡Gracias por tu reseña!",
                showConfirmButton: false,
                timer: 2000
            });
        });
    }

    refrescarResenas();
});