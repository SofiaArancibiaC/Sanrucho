// ============================================================
// carrito.js — Lógica de la página Carrito de compras.
// Utilidades de base.js: escaparHTML(), mostrarToast(), fetchJSON().
// ============================================================

// Productos de ejemplo mientras no exista un endpoint del carrito
const PRODUCTOS_DE_PRUEBA = [
    { id: 1,  nombre: 'Auriculares Sanrucho Pro',   categoria: 'Audio',  precio: 89.90 },
    { id: 2,  nombre: 'Teclado mecánico RGB',   categoria: 'Accesorios', precio: 64.50 },
    { id: 3,  nombre: 'Ratón inalámbrico',      categoria: 'Accesorios', precio: 39.90 },
    { id: 4,  nombre: 'Pad de gran tamaño',     categoria: 'Accesorios', precio: 19.90 },
];

const STORAGE_KEY = 'sanrucho-carrito';
let carrito = [];

// ============================================================
// Carga y persistencia del carrito (localStorage)
// ============================================================
function cargarCarrito() {
    try {
        carrito = JSON.parse(localStorage.getItem(STORAGE_KEY)) || inicializarDemo();
    } catch (e) {
        carrito = inicializarDemo();
    }
    guardarCarrito();
    return carrito;
}

function guardarCarrito() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(carrito));
}

function inicializarDemo() {
    return [
        { id: PRODUCTOS_DE_PRUEBA[0].id, cantidad: 1 },
        { id: PRODUCTOS_DE_PRUEBA[2].id, cantidad: 2 },
    ];
}

function buscarProducto(id) {
    return PRODUCTOS_DE_PRUEBA.find(p => p.id === id);
}

// ============================================================
// Render de la tabla del carrito
// ============================================================
function renderCarrito() {
    const tbody = document.getElementById('carrito-body');
    const vacio = document.getElementById('carrito-vacio');
    const resumenArticulos = document.getElementById('resumen-articulos');
    const tabla = document.querySelector('.carrito-articulos table');

    if (carrito.length === 0) {
        tbody.style.display = 'none';
        vacio.style.display = 'flex';
        resumenArticulos.textContent = '0 artículos';
        vaciarResumen();
        return;
    }

    tbody.style.display = '';
    vacio.style.display = 'none';
    resumenArticulos.textContent = contadorArticulos() + ' artículos';

    tbody.innerHTML = carrito.map((item) => {
        const p = buscarProducto(item.id);
        const subtotal = p.precio * item.cantidad;
        return '<tr>' +
            '<td><div class="carrito-producto">' +
                '<div><div class="nombre">' + escaparHTML(p.nombre) + '</div>' +
                '<div class="categoria">' + escaparHTML(p.categoria) + '</div></div>' +
            '</div></td>' +
            '<td>$' + formatearPrecio(p.precio) + '</td>' +
            '<td><div class="qty-control">' +
                '<button onclick="cambiarCantidad(' + p.id + ', -1)">-</button>' +
                '<span class="qty-valor">' + item.cantidad + '</span>' +
                '<button onclick="cambiarCantidad(' + p.id + ', 1)">+</button>' +
            '</div></td>' +
            '<td>$' + formatearPrecio(subtotal) + '</td>' +
            '<td><button class="btn btn-danger btn-sm" onclick="eliminarArticulo(' + p.id + ')">Quitar</button></td>' +
        '</tr>';
    }).join('');

    actualizarResumen();
}

// ============================================================
// Operaciones sobre el carrito
// ============================================================
function cambiarCantidad(id, delta) {
    const item = carrito.find(i => i.id === id);
    if (!item) return;
    item.cantidad += delta;
    if (item.cantidad <= 0) {
        eliminarArticulo(id);
        return;
    }
    guardarCarrito();
    renderCarrito();
    mostrarToast('Cantidad actualizada', 'info');
}

function eliminarArticulo(id) {
    carrito = carrito.filter(i => i.id !== id);
    guardarCarrito();
    renderCarrito();
    mostrarToast('Artículo eliminado del carrito', 'info');
}

// ============================================================
// Resumen del pedido
// ============================================================
function contadorArticulos() {
    return carrito.reduce((total, i) => total + i.cantidad, 0);
}

function subtotalTotal() {
    return carrito.reduce((total, i) => {
        const p = buscarProducto(i.id);
        return total + (p ? p.precio * i.cantidad : 0);
    }, 0);
}

function vaciarResumen() {
    document.getElementById('resumen-subtotal').textContent = '$0,00';
    document.getElementById('resumen-envio').textContent = 'Gratis';
    document.getElementById('resumen-total').textContent = '$0,00';
}

function actualizarResumen() {
    const sub = subtotalTotal();
    const envio = sub >= 50 || sub === 0 ? 0 : 6.90;
    document.getElementById('resumen-subtotal').textContent = '$' + formatearPrecio(sub);
    document.getElementById('resumen-envio').textContent =
        envio === 0 ? 'Gratis' : '$' + formatearPrecio(envio);
    document.getElementById('resumen-total').textContent =
        '$' + formatearPrecio(sub + envio);
}

function formatearPrecio(valor) {
    return valor.toFixed(2).replace('.', ',');
}

// ============================================================
// Finalizar compra
// ============================================================
function finalizarCompra() {
    if (carrito.length === 0) {
        mostrarToast('Tu carrito está vacío', 'error');
        return;
    }
    guardarCarrito();
    mostrarToast('Pedido enviado (demo): total $' + formatearPrecio(subtotalTotal()), 'success');
}

// ============================================================
// Inicialización
// ============================================================
document.addEventListener('DOMContentLoaded', function () {
    cargarCarrito();
    renderCarrito();
});