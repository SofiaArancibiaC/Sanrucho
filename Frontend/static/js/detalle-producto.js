document.addEventListener("DOMContentLoaded", function () {
    const contenedor = document.getElementById("contenedorDetalle");

    // 1. Obtener el código desde los parámetros URL (?codigo=HK-PLUSH-001)
    const parametros = new URLSearchParams(window.location.search);
    const codigoProducto = parametros.get("codigo");

    if (!codigoProducto) {
        mostrarError("No se especificó un producto.");
        return;
    }

    // 2. Buscar producto y categoría asociada en el storage
    const productos = obtenerColeccion(SANRUCHO_KEYS.productos);
    const categorias = obtenerColeccion(SANRUCHO_KEYS.categorias);

    const producto = productos.find(p => p.codigo === codigoProducto && p.estado === "Activo");

    if (!producto) {
        mostrarError("El producto solicitado no existe o no se encuentra disponible.");
        return;
    }

    const categoria = categorias.find(c => c.id === producto.categoriaId);
    const nombreCategoria = categoria ? categoria.nombre : "Sin categoría";

    // 3. Renderizar la vista de detalle
    renderizarDetalle(producto, nombreCategoria);
});

function renderizarDetalle(producto, nombreCategoria) {
    const contenedor = document.getElementById("contenedorDetalle");
    const stockDisponible = producto.stock > 0;

    contenedor.innerHTML = `
        <div class="col-lg-10">
            <div class="product-detail-card">
                <div class="row g-4 align-items-center">
                    
                    <!-- Imagen -->
                    <div class="col-md-6">
                        <div class="detail-img-wrapper">
                            <img src="${producto.imagen}" alt="${producto.nombre}" 
                                 onerror="this.src='https://placehold.co/400x400?text=Sin+Imagen'">
                        </div>
                    </div>

                    <!-- Datos y Compra -->
                    <div class="col-md-6">
                        <span class="card-category">${nombreCategoria}</span>
                        <h2 class="detail-title mt-2">${producto.nombre}</h2>
                        <p class="text-muted small mb-2">Código: <span class="fw-semibold">${producto.codigo}</span></p>
                        
                        <div class="detail-price my-3">${formatearPrecio(producto.precio)}</div>
                        
                        <p class="detail-desc mb-4">${producto.descripcion}</p>

                        <div class="mb-4">
                            <span class="badge ${stockDisponible ? 'bg-success-subtle text-success border border-success-subtle' : 'bg-danger-subtle text-danger border border-danger-subtle'} px-3 py-2 rounded-pill">
                                ${stockDisponible ? `Stock disponible: ${producto.stock} un.` : 'Agotado'}
                            </span>
                        </div>

                        ${stockDisponible ? `
                            <div class="d-flex align-items-center gap-3">
                                <div>
                                    <input type="number" id="inputCantidad" class="form-control input-qty" value="1" min="1" max="${producto.stock}">
                                </div>
                                <button class="btn btn-add-cart flex-grow-1" id="btnAgregarCarrito">
                                    Añadir al carrito
                                </button>
                            </div>
                        ` : `
                            <button class="btn btn-secondary rounded-pill w-100 py-2" disabled>Sin stock</button>
                        `}
                    </div>

                </div>
            </div>
        </div>
    `;

    if (stockDisponible) {
        document.getElementById("btnAgregarCarrito").addEventListener("click", function () {
            agregarAlCarrito(producto);
        });
    }
}

function agregarAlCarrito(producto) {
    const inputCantidad = document.getElementById("inputCantidad");
    const cantidad = parseInt(inputCantidad.value);

    if (isNaN(cantidad) || cantidad <= 0) {
        Swal.fire({
            title: "Cantidad inválida",
            text: "Por favor ingresa un número válido mayor a 0.",
            icon: "warning"
        });
        return;
    }

    if (cantidad > producto.stock) {
        Swal.fire({
            title: "Stock insuficiente",
            text: `Solo quedan ${producto.stock} unidades disponibles.`,
            icon: "warning"
        });
        return;
    }

    // Manejo de la colección del carrito en storage.js
    let carrito = obtenerColeccion(SANRUCHO_KEYS.carrito);
    const itemIndex = carrito.findIndex(item => item.codigo === producto.codigo);

    if (itemIndex > -1) {
        const nuevaCantidad = carrito[itemIndex].cantidad + cantidad;
        if (nuevaCantidad > producto.stock) {
            Swal.fire({
                title: "Límite superado",
                text: "No puedes agregar más unidades del stock disponible.",
                icon: "warning"
            });
            return;
        }
        carrito[itemIndex].cantidad = nuevaCantidad;
    } else {
        carrito.push({
            codigo: producto.codigo,
            nombre: producto.nombre,
            precio: producto.precio,
            imagen: producto.imagen,
            cantidad: cantidad
        });
    }

    guardarColeccion(SANRUCHO_KEYS.carrito, carrito);
    actualizarBadgeCarrito();

    Swal.fire({
        title: "¡Agregado!",
        text: `Se agregaron ${cantidad} unidad(es) al carrito.`,
        icon: "success",
        showConfirmButton: false,
        timer: 1500
    });
}

function mostrarError(mensaje) {
    const contenedor = document.getElementById("contenedorDetalle");
    contenedor.innerHTML = `
        <div class="col-md-8 text-center py-5">
            <div class="alert alert-danger rounded-4 py-4" role="alert">
                <h4 class="alert-heading fw-bold mb-3">Ups, ocurrió un problema</h4>
                <p class="mb-3">${mensaje}</p>
                <a href="/productos" class="btn btn-outline-danger rounded-pill px-4">Volver al catálogo</a>
            </div>
        </div>
    `;
}