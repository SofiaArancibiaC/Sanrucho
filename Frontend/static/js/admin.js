// ============================================================
// admin.js — Panel de administracion (admin.html).
// Usa las colecciones de SANRUCHO_KEYS (storage.js).
// ============================================================

const SECCIONES = {
    usuarios:  { clave: 'usuarios',  nombre: 'Usuarios'  },
    productos: { clave: 'productos', nombre: 'Productos' },
    pedidos:   { clave: 'pedidos',   nombre: 'Pedidos'   },
    catalogo:  { clave: 'categorias', nombre: 'Catalogo' }
};

let seccionActual = 'usuarios';
let datosActuales = [];

function obtenerDatosSeccion(seccion) {
    const info = SECCIONES[seccion] || SECCIONES.usuarios;
    if (seccion === 'pedidos') return []; // modulo de pedidos aun no implementado
    return obtenerColeccion(SANRUCHO_KEYS[info.clave]);
}

function cargarDatos() {
    const tbody = document.getElementById('tabla-body');
    tbody.innerHTML = '<tr class="loading-row"><td colspan="6">Cargando datos...</td></tr>';

    datosActuales = obtenerDatosSeccion(seccionActual);
    renderTablaPanel(datosActuales);
    actualizarTarjetas();
    mostrarToast('Datos de ' + SECCIONES[seccionActual].nombre + ' cargados', 'success');
}

function renderTablaPanel(lista) {
    const head = document.getElementById('tabla-head');
    const tbody = document.getElementById('tabla-body');

    if (lista.length === 0) {
        head.innerHTML = '';
        tbody.innerHTML = '<tr class="loading-row"><td colspan="6">No hay registros para mostrar.</td></tr>';
        return;
    }

    const columnas = Object.keys(lista[0]).slice(0, 5);
    head.innerHTML = '<tr>' + columnas.map(function (c) { return '<th>' + c + '</th>'; }).join('') + '<th>Acciones</th></tr>';

    tbody.innerHTML = lista.map(function (item, indice) {
        const celdas = columnas.map(function (c) {
            const valor = item[c];
            return '<td>' + (valor == null || valor === '' ? '—' : escaparHTML(String(valor))) + '</td>';
        }).join('');
        return '<tr>' + celdas +
            '<td><button class="btn btn-danger btn-sm" data-indice="' + indice + '" onclick="eliminarRegistro(this.dataset.indice)">Eliminar</button></td></tr>';
    }).join('');
}

function buscar() {
    const termino = document.getElementById('busqueda').value.trim().toLowerCase();
    if (!termino) {
        renderTablaPanel(datosActuales);
        return;
    }
    const filtrados = datosActuales.filter(function (item) {
        return Object.values(item).some(function (v) {
            return v != null && String(v).toLowerCase().includes(termino);
        });
    });
    renderTablaPanel(filtrados);
}

function cambiarSeccion(valor) {
    seccionActual = valor;
    document.getElementById('seccion-actual').textContent = valor;
    document.getElementById('busqueda').value = '';
    cargarDatos();
}

function actualizarTarjetas() {
    Object.keys(SECCIONES).forEach(function (seccion) {
        const el = document.querySelector('[data-card="' + seccion + '"]');
        if (!el) return;
        el.textContent = obtenerDatosSeccion(seccion).length;
    });
}

function abrirModalNuevo() {
    document.getElementById('modal-titulo').textContent = 'Nuevo registro en ' + SECCIONES[seccionActual].nombre;
    document.getElementById('campo-principal').value = '';
    document.getElementById('modal-registro').classList.add('show');
    document.getElementById('campo-principal').focus();
}

function cerrarModal() {
    const modal = document.getElementById('modal-registro');
    if (modal) modal.classList.remove('show');
}

function guardarRegistro() {
    cerrarModal();
    mostrarToast('Guardado simulado: el alta se maneja desde cada seccion.', 'info');
}

function eliminarRegistro(indice) {
    const item = datosActuales[indice];
    if (!item) return;
    if (!confirm('¿Deseas eliminar el registro?')) return;

    const info = SECCIONES[seccionActual];
    const coleccion = obtenerColeccion(SANRUCHO_KEYS[info.clave]);
    const claves = Object.keys(item);

    let nuevos;
    if (claves.length === 0) {
        nuevos = coleccion.filter(function (r) { return r !== item; });
    } else {
        const clave = claves[0];
        nuevos = coleccion.filter(function (r) { return String(r[clave]) !== String(item[clave]); });
    }

    guardarColeccion(SANRUCHO_KEYS[info.clave], nuevos);
    mostrarToast('Registro eliminado', 'success');
    cargarDatos();
}

document.addEventListener('DOMContentLoaded', function () {
    const tabla = document.getElementById('tabla-body');
    if (!tabla) return;
    const busqueda = document.getElementById('busqueda');
    buscar();
    if (busqueda) busqueda.addEventListener('input', buscar);
    cargarDatos();
});