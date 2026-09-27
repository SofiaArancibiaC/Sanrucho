// ============================================================
// carrito.js — Logica de la pagina Carrito de compras.
// Integrado con SANRUCHO_KEYS.carrito (storage.js) y el catalogo
// real de productos (productos.js). Base.js aporta escaparHTML() y mostrarToast().
// ============================================================

let carrito = [];

function cargarCarrito() {
    carrito = obtenerColeccion(SANRUCHO_KEYS.carrito) || [];
    return carrito;
}

function guardarCarrito() {
    guardarColeccion(SANRUCHO_KEYS.carrito, carrito);
}

function buscarProducto(codigo) {
    return obtenerProductos().find(function (p) { return p.codigo === codigo; });
}

// ============================================================
// Render de la tabla del carrito
// ============================================================
function renderCarrito() {
    const tbody = document.getElementById('carrito-body');
    const vacio = document.getElementById('carrito-vacio');
    const resumenArticulos = document.getElementById('resumen-articulos');

    if (carrito.length === 0) {
        tbody.style.display = 'none';
        tbody.innerHTML = '';
        vacio.style.display = 'flex';
        resumenArticulos.textContent = '0 artículos';
        vaciarResumen();
        return;
    }

    tbody.style.display = '';
    vacio.style.display = 'none';
    resumenArticulos.textContent = contadorArticulos() + ' artículos';

    tbody.innerHTML = carrito.map(function (item) {
        const p = buscarProducto(item.codigo);
        if (!p) return '';
        const subtotal = p.precio * item.cantidad;
        return '<tr>' +
            '<td><div class="carrito-producto">' +
            '<img src="' + rutaImagenProducto(p) + '" alt="' + escaparHTML(p.nombre) + '">' +
            '<div><div class="nombre">' + escaparHTML(p.nombre) + '</div>' +
            '<div class="categoria">' + escaparHTML(obtenerNombreCategoria(p.categoriaId)) + '</div></div>' +
            '</div></td>' +
            '<td>' + formatearPrecio(p.precio) + '</td>' +
            '<td><div class="qty-control">' +
            '<button onclick="cambiarCantidad(\'' + p.codigo + '\', -1)">-</button>' +
            '<span class="qty-valor">' + item.cantidad + '</span>' +
            '<button onclick="cambiarCantidad(\'' + p.codigo + '\', 1)">+</button>' +
            '</div></td>' +
            '<td>' + formatearPrecio(subtotal) + '</td>' +
            '<td><button class="btn btn-danger btn-sm" onclick="eliminarArticulo(\'' + p.codigo + '\')">Quitar</button></td>' +
            '</tr>';
    }).join('');

    actualizarResumen();
}

// ============================================================
// Operaciones sobre el carrito
// ============================================================
function cambiarCantidad(codigo, delta) {
    const item = carrito.find(function (i) { return i.codigo === codigo; });
    if (!item) return;
    const p = buscarProducto(codigo);

    const nuevaCantidad = item.cantidad + delta;
    if (nuevaCantidad <= 0) {
        eliminarArticulo(codigo);
        return;
    }
    if (p && nuevaCantidad > p.stock) {
        mostrarToast('No hay más stock disponible', 'error');
        return;
    }

    item.cantidad = nuevaCantidad;
    guardarCarrito();
    renderCarrito();
    actualizarBadgeCarrito();
    mostrarToast('Cantidad actualizada', 'info');
}

function eliminarArticulo(codigo) {
    carrito = carrito.filter(function (i) { return i.codigo !== codigo; });
    guardarCarrito();
    renderCarrito();
    actualizarBadgeCarrito();
    mostrarToast('Artículo eliminado del carrito', 'info');
}

// ============================================================
// Resumen del pedido
// ============================================================
function contadorArticulos() {
    return carrito.reduce(function (total, i) { return total + i.cantidad; }, 0);
}

function subtotalTotal() {
    return carrito.reduce(function (total, i) {
        const p = buscarProducto(i.codigo);
        return total + (p ? p.precio * i.cantidad : 0);
    }, 0);
}

function vaciarResumen() {
    document.getElementById('resumen-subtotal').textContent = formatearPrecio(0);
    document.getElementById('resumen-envio').textContent = 'Gratis';
    document.getElementById('resumen-total').textContent = formatearPrecio(0);
}

function actualizarResumen() {
    const sub = subtotalTotal();
    const envio = sub === 0 || sub >= 30000 ? 0 : 3990;
    document.getElementById('resumen-subtotal').textContent = formatearPrecio(sub);
    document.getElementById('resumen-envio').textContent =
        envio === 0 ? 'Gratis' : formatearPrecio(envio);
    document.getElementById('resumen-total').textContent =
        formatearPrecio(sub + envio);
}

// ============================================================
// Finalizar compra
// ============================================================
function finalizarCompra() {
    if (carrito.length === 0) {
        mostrarToast('Tu carrito está vacío', 'error');
        return;
    }

    const total = subtotalTotal();
    mostrarToast('¡Pedido enviado! Total: ' + formatearPrecio(total), 'success');

    carrito = [];
    guardarCarrito();
    actualizarBadgeCarrito();
    setTimeout(renderCarrito, 350);
}

// ============================================================
// Inicializacion
// ============================================================
document.addEventListener('DOMContentLoaded', function () {
    cargarCarrito();
    renderCarrito();
});