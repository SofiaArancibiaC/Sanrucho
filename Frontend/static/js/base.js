// ============================================================
// base.js — Utilidades compartidas por todas las paginas.
// Provee las funciones que base.html espera:
//   mostrarToast(mensaje, tipo), escaparHTML(texto), fetchJSON().
// ============================================================

function escaparHTML(texto) {
    return String(texto == null ? '' : texto)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function mostrarToast(mensaje, tipo) {
    const toast = document.getElementById('toast');
    if (!toast) return;
    toast.textContent = mensaje;
    toast.className = 'toast';
    if (tipo) toast.classList.add('toast-' + tipo);
    toast.classList.add('show');
    clearTimeout(toast._timer);
    toast._timer = setTimeout(function () {
        toast.classList.remove('show');
    }, 3200);
}

function fetchJSON(url, opciones) {
    return fetch(url, opciones).then(function (respuesta) {
        if (!respuesta.ok) throw new Error('Error HTTP ' + respuesta.status);
        return respuesta.json();
    });
}