// ============================================================
// blog.js — Lógica de la página Blog (listado de artículos).
// Utilidades de base.js: mostrarToast(). --> ver  cual js tiene las funciones--
// ============================================================

document.addEventListener('DOMContentLoaded', function () {
    filtrarArticulos();
});

// ============================================================
// Búsqueda en el listado de artículos
// ============================================================
function filtrarArticulos() {
    const input = document.getElementById('busqueda');
    const tarjetas = document.querySelectorAll('.post-card');
    const contador = document.getElementById('contador');
    const vacio = document.getElementById('sin-resultados');

    const termino = (input && input.value.trim().toLowerCase()) || '';
    let visibles = 0;

    tarjetas.forEach(tarjeta => {
        const coincide = !termino || tarjeta.textContent.toLowerCase().includes(termino);
        tarjeta.style.display = coincide ? '' : 'none';
        if (coincide) visibles++;
    });

    if (contador) contador.textContent = visibles + ' artículo(s)';
    if (vacio) vacio.style.display = visibles === 0 ? 'block' : 'none';
}