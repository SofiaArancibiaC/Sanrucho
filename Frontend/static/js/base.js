// ============================================================
// base.js — Utilidades globales compartidas por todas las páginas.
// Se carga antes que los scripts de cada vista en base.html.
// ============================================================

// Escapa caracteres HTML antes de inyectar texto dinámico
function escaparHTML(texto) {
    return String(texto).replace(/[&<>"']/g, c => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
}

// Muestra un toast de notificación usando el div#toast de base.html
// tipos: 'success' | 'error' | 'info'
let toastTimeout;
function mostrarToast(mensaje, tipo) {
    const toast = document.getElementById('toast');
    if (!toast) return;
    toast.textContent = mensaje;
    toast.className = 'toast toast-' + (tipo || 'info');
    toast.classList.add('show');
    clearTimeout(toastTimeout);
    toastTimeout = setTimeout(() => toast.classList.remove('show'), 3500);
}

// Fetch wrapper con manejo de errores: devuelve el JSON parseado
// y lanza un Error si la respuesta no es OK.
async function fetchJSON(url, opciones) {
    const resp = await fetch(url, opciones);
    if (!resp.ok) throw new Error('Error HTTP ' + resp.status);
    return resp.json();
}