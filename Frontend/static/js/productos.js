/* catalogo.js - Renderizado y filtrado interactivo */

document.addEventListener("DOMContentLoaded", function () {
    const contenedor = document.getElementById("contenedorProductos");
    const filtroCat = document.getElementById("filtroCategoria");
    const filtroPrecio = document.getElementById("filtroPrecio");
    const precioEtiqueta = document.getElementById("precioEtiqueta");
    const buscarInput = document.getElementById("buscarInput");
    const btnLimpiar = document.getElementById("btnLimpiarFiltros");
    const contador = document.getElementById("contadorProductos");

    const categorias = obtenerColeccion(SANRUCHO_KEYS.categorias);
    const productos = obtenerColeccion(SANRUCHO_KEYS.productos);

    // Cargar select de categorías
    function poblarCategorias() {
        categorias.forEach(function (cat) {
            const opcion = document.createElement("option");
            opcion.value = cat.id;
            opcion.textContent = cat.nombre;
            filtroCat.appendChild(opcion);
        });
    }

    function obtenerNombreCategoria(catId) {
        const encontrada = categorias.find(c => c.id === catId);
        return encontrada ? encontrada.nombre : "General";
    }

    // Renderizar tarjetas en el DOM
    function renderizarProductos(lista) {
        contenedor.innerHTML = "";
        contador.textContent = `Mostrando ${lista.length} productos`;

        if (lista.length === 0) {
            contenedor.innerHTML = `
                <div class="col-12 text-center py-5">
                    <p class="text-muted fs-5">No se encontraron productos disponibles con los filtros aplicados.</p>
                </div>`;
            return;
        }

        lista.forEach(function (prod) {
            const agotado = prod.stock <= 0;
            const bajoStock = prod.stock <= prod.stockCritico && !agotado;

            const col = document.createElement("div");
            col.className = "col";
            col.innerHTML = `
                <div class="card h-100 shadow-sm border-0 product-card position-relative">
                    ${bajoStock ? '<span class="badge bg-warning text-dark position-absolute top-0 start-0 m-2">Últimas unidades</span>' : ''}
                    ${agotado ? '<span class="badge bg-danger position-absolute top-0 start-0 m-2">Agotado</span>' : ''}
                    
                    <!-- Imagen con enlace al detalle -->
                    <a href="/producto-detalle?codigo=${prod.codigo}" class="text-decoration-none">
                        <div class="card-img-wrapper bg-light text-center p-3">
                            <img src="${prod.imagen}" class="card-img-top product-img" alt="${prod.nombre}" onerror="this.src='https://placehold.co/300x200?text=Sin+Imagen'">
                        </div>
                    </a>

                    <div class="card-body d-flex flex-column">
                        <span class="card-category">${obtenerNombreCategoria(prod.categoriaId)}</span>
                        
                        <!-- Título con enlace al detalle -->
                        <a href="/producto-detalle?codigo=${prod.codigo}" class="text-decoration-none text-reset">
                            <h6 class="card-title fw-bold mt-1 mb-2 text-truncate" title="${prod.nombre}">${prod.nombre}</h6>
                        </a>
                        
                        <p class="card-text text-muted small flex-grow-1">${prod.descripcion}</p>
                        
                        <div class="card-price mb-2">${formatearPrecio(prod.precio)}</div>

                        <!-- Botones de acción: Ver detalle y Agregar -->
                        <div class="d-flex gap-2 mt-auto">
                            <a href="/producto-detalle?codigo=${prod.codigo}" class="btn btn-outline-secondary btn-sm rounded-pill flex-grow-1">
                                Ver detalle
                            </a>
                            <button 
                                class="btn btn-sm btn-product px-3 btn-agregar" 
                                data-codigo="${prod.codigo}" 
                                ${agotado ? 'disabled' : ''}>
                                ${agotado ? 'Sin stock' : 'Agregar'}
                            </button>
                        </div>
                    </div>
                </div>
            `;
            contenedor.appendChild(col);
        });

        asignarEventosCarrito();
    }

    // Filtrar elementos
    function aplicarFiltros() {
        const texto = buscarInput.value.toLowerCase().trim();
        const catSeleccionada = parseInt(filtroCat.value, 10);
        const precioMaximo = parseInt(filtroPrecio.value, 10);

        precioEtiqueta.textContent = formatearPrecio(precioMaximo);

        const filtrados = productos.filter(function (prod) {
            if (prod.estado !== "Activo") return false;

            const coincideTexto = prod.nombre.toLowerCase().includes(texto) || prod.codigo.toLowerCase().includes(texto);
            const coincideCat = (catSeleccionada === 0 || prod.categoriaId === catSeleccionada);
            const coincidePrecio = prod.precio <= precioMaximo;

            return coincideTexto && coincideCat && coincidePrecio;
        });

        renderizarProductos(filtrados);
    }

    // Agregar producto a SANRUCHO_KEYS.carrito
    function asignarEventosCarrito() {
        document.querySelectorAll(".btn-agregar").forEach(function (btn) {
            btn.addEventListener("click", function () {
                const codigo = this.getAttribute("data-codigo");
                const producto = productos.find(p => p.codigo === codigo);
                if (!producto) return;

                let carrito = obtenerColeccion(SANRUCHO_KEYS.carrito);
                const itemExistente = carrito.find(item => item.codigo === codigo);

                if (itemExistente) {
                    if (itemExistente.cantidad + 1 > producto.stock) {
                        Swal.fire({
                            title: "Límite de stock",
                            text: "No puedes añadir más unidades de las disponibles.",
                            icon: "warning"
                        });
                        return;
                    }
                    itemExistente.cantidad += 1;
                } else {
                    carrito.push({
                        codigo: producto.codigo,
                        nombre: producto.nombre,
                        precio: producto.precio,
                        imagen: producto.imagen,
                        cantidad: 1
                    });
                }

                guardarColeccion(SANRUCHO_KEYS.carrito, carrito);
                actualizarBadgeCarrito();

                Swal.fire({
                    toast: true,
                    position: "bottom-end",
                    icon: "success",
                    title: `¡${producto.nombre} agregado!`,
                    showConfirmButton: false,
                    timer: 2000
                });
            });
        });
    }

    // Inicialización y Listeners
    poblarCategorias();
    aplicarFiltros();

    buscarInput.addEventListener("input", aplicarFiltros);
    filtroCat.addEventListener("change", aplicarFiltros);
    filtroPrecio.addEventListener("input", aplicarFiltros);

    btnLimpiar.addEventListener("click", function () {
        buscarInput.value = "";
        filtroCat.value = "0";
        filtroPrecio.value = "100000";
        aplicarFiltros();
    });
});