// ============================================================
// admin.js — Lógica del Panel de Administración Sanrucho.
// Utilidades de base.js: mostrarToast().
// ============================================================

const STAT_ENDPOINTS = [
    { id: 'stat-usuarios',  url: 'http://localhost:8082/api/usuarios' },
    { id: 'stat-productos', url: 'http://localhost:8081/api/productos' },
    { id: 'stat-pedidos',   url: 'http://localhost:8083/api/pedidos' },
    { id: 'stat-catalogo',  url: 'http://localhost:8084/api/catalogo' }
];

// ============================================================
// Carga de indicadores desde los microservicios
// ============================================================
async function cargarIndicadores() {
    for (const ep of STAT_ENDPOINTS) {
        const el = document.getElementById(ep.id);
        if (!el) continue;
        try {
            const resp = await fetch(ep.url);
            if (!resp.ok) throw new Error('HTTP ' + resp.status);
            const datos = await resp.json();
            el.textContent = Array.isArray(datos)
                ? datos.length
                : (datos && datos.total != null ? datos.total : '?');
        } catch (e) {
            el.textContent = 'N/A';
        }
    }
}

document.addEventListener('DOMContentLoaded', cargarIndicadores);