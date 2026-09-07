/* catalogo.js - Renderizado y filtrado interactivo */
/* Sanrucho - productos.js
   Catalogo publico (companera) + mantenedor administrativo de productos
   (agregado con SANRUCHO_KEYS). En las paginas administrativas el catalogo
   no se renderiza (guard por existencia de elementos). */

/* ==================== CATALOGO PUBLICO ==================== */

document.addEventListener("DOMContentLoaded", function () {
    const contenedor = document.getElementById("contenedorProductos");
    const filtroCat = document.getElementById("filtroCategoria");
    const filtroPrecio = document.getElementById("filtroPrecio");
    const precioEtiqueta = document.getElementById("precioEtiqueta");
    const buscarInput = document.getElementById("buscarInput");
    const btnLimpiar = document.getElementById("btnLimpiarFiltros");
    const contador = document.getElementById("contadorProductos");

    // Guard: solo la pagina publica de productos tiene estos elementos.
    // En paginas administrativas no se ejecuta el catalogo.
    if (!contenedor || !filtroCat || !filtroPrecio || !precioEtiqueta || !buscarInput || !btnLimpiar || !contador) return;

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

/* ==================== MANTENEDOR ADMINISTRATIVO ==================== */

function obtenerProductos() {
    return obtenerColeccion(SANRUCHO_KEYS.productos);
}

function obtenerProductoPorCodigo(codigo) {
    return obtenerProductos().find(function (p) { return p.codigo === codigo; });
}

function obtenerNombreCategoria(categoriaId) {
    const categoria = obtenerColeccion(SANRUCHO_KEYS.categorias).find(function (c) { return c.id === Number(categoriaId); });
    return categoria ? categoria.nombre : "Sin categoria";
}

/* Las imagenes de los productos pueden venir con ruta completa (/static/img/...)
   o como una ruta relativa dentro de static/img. */
function rutaImagenProducto(producto) {
    if (!producto.imagen) return "/static/img/logo-sanrucho.png";
    if (/^(https?:)?\//.test(producto.imagen)) return producto.imagen;
    return "/static/img/" + producto.imagen;
}

function llenarSelectCategorias(idSelect, incluirTodas) {
    const select = document.getElementById(idSelect);
    if (!select) return;
    const categorias = obtenerColeccion(SANRUCHO_KEYS.categorias);

    select.innerHTML = "";
    if (incluirTodas) {
        select.innerHTML += '<option value="">Todas las categorias</option>';
    } else {
        select.innerHTML += '<option value="">Seleccione una categoria</option>';
    }
    categorias.forEach(function (categoria) {
        select.innerHTML += '<option value="' + categoria.id + '">' + categoria.nombre + '</option>';
    });
}

function guardarProducto(producto) {
    const productos = obtenerProductos();
    const indice = productos.findIndex(function (p) { return p.codigo === producto.codigo; });
    if (indice === -1) {
        productos.push(producto);
    } else {
        productos[indice] = producto;
    }
    guardarColeccion(SANRUCHO_KEYS.productos, productos);
}

function eliminarProducto(codigo) {
    const productos = obtenerProductos().filter(function (p) { return p.codigo !== codigo; });
    guardarColeccion(SANRUCHO_KEYS.productos, productos);
}

function cargarTablaProductosAdmin() {
    const productos = obtenerProductos();
    const cuerpoTabla = document.getElementById("cuerpoTablaProductos");
    cuerpoTabla.innerHTML = "";

    const sesion = obtenerSesion();
    // Rol 2 (Admin) con la misma restriccion de solo lectura del esquema del profe.
    const soloLectura = sesion && sesion.rolId === 2;
    const botonNuevo = document.getElementById("btnNuevoProducto");
    if (botonNuevo && soloLectura) botonNuevo.style.display = "none";

    productos.forEach(function (producto) {
        const critico = producto.stockCritico != null && producto.stock <= producto.stockCritico;
        const fila = document.createElement("tr");
        const acciones = soloLectura
            ? '<button class="btn btn-sm btn-outline-accent" onclick="verProductoAdmin(\'' + producto.codigo + '\')" title="Ver"><i class="bi bi-eye"></i></button>'
            : '<button class="btn btn-sm btn-outline-accent me-1" onclick="verProductoAdmin(\'' + producto.codigo + '\')" title="Ver"><i class="bi bi-eye"></i></button>' +
              '<button class="btn btn-sm btn-outline-light me-1" onclick="window.location.href=\'/admin/productos/form?codigo=' + producto.codigo + '\'" title="Editar"><i class="bi bi-pencil"></i></button>' +
              '<button class="btn btn-sm btn-outline-danger" onclick="confirmarEliminarProducto(\'' + producto.codigo + '\')" title="Eliminar"><i class="bi bi-trash"></i></button>';

        fila.innerHTML =
            '<td>' + producto.codigo + '</td>' +
            '<td><img src="' + rutaImagenProducto(producto) + '" alt="' + producto.nombre + '" height="40"></td>' +
            '<td>' + producto.nombre + '</td>' +
            '<td>' + obtenerNombreCategoria(producto.categoriaId) + '</td>' +
            '<td>' + formatearPrecio(producto.precio) + '</td>' +
            '<td class="' + (critico ? "stock-critico" : "") + '">' + producto.stock + (critico ? ' <i class="bi bi-exclamation-triangle-fill"></i>' : '') + '</td>' +
            '<td><span class="badge ' + (producto.estado === "Activo" ? "bg-success" : "bg-secondary") + '">' + producto.estado + '</span></td>' +
            '<td class="text-nowrap">' + acciones + '</td>';
        cuerpoTabla.appendChild(fila);
    });

    if ($.fn.DataTable.isDataTable("#tablaProductos")) {
        $("#tablaProductos").DataTable().destroy();
    }
    $("#tablaProductos").DataTable({
        language: { url: "https://cdn.datatables.net/plug-ins/1.13.11/i18n/es-ES.json" },
        order: []
    });
}

function verProductoAdmin(codigo) {
    const producto = obtenerProductoPorCodigo(codigo);
    if (!producto) return;
    Swal.fire({
        title: producto.nombre,
        html:
            '<img src="' + rutaImagenProducto(producto) + '" class="img-fluid mb-3" style="max-height:160px;"><br>' +
            '<p class="text-start"><strong>Codigo:</strong> ' + producto.codigo + '<br>' +
            '<strong>Categoria:</strong> ' + obtenerNombreCategoria(producto.categoriaId) + '<br>' +
            '<strong>Precio:</strong> ' + formatearPrecio(producto.precio) + '<br>' +
            '<strong>Stock:</strong> ' + producto.stock + '<br>' +
            '<strong>Stock critico:</strong> ' + (producto.stockCritico != null ? producto.stockCritico : "-") + '<br>' +
            '<strong>Descripcion:</strong> ' + (producto.descripcion || "-") + '</p>',
        confirmButtonText: "Cerrar"
    });
}

function confirmarEliminarProducto(codigo) {
    Swal.fire({
        title: "Eliminar producto?",
        text: "Esta accion no se puede deshacer.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Eliminar",
        cancelButtonText: "Cancelar"
    }).then(function (resultado) {
        if (resultado.isConfirmed) {
            eliminarProducto(codigo);
            Swal.fire("Eliminado", "El producto fue eliminado correctamente.", "success");
            cargarTablaProductosAdmin();
        }
    });
}

/* Prepara el formulario Nuevo/Editar segun exista ?codigo= en la URL. */
function inicializarFormularioProducto() {
    llenarSelectCategorias("categoriaProducto", false);

    const parametros = new URLSearchParams(window.location.search);
    const codigo = parametros.get("codigo");

    if (codigo) {
        const producto = obtenerProductoPorCodigo(codigo);
        if (producto) {
            document.getElementById("tituloFormularioProducto").textContent = "Editar producto";
            document.getElementById("codigoProducto").value = producto.codigo;
            document.getElementById("codigoProducto").readOnly = true;
            document.getElementById("nombreProducto").value = producto.nombre;
            document.getElementById("descripcionProducto").value = producto.descripcion || "";
            document.getElementById("precioProducto").value = producto.precio;
            document.getElementById("stockProducto").value = producto.stock;
            document.getElementById("stockCriticoProducto").value = producto.stockCritico != null ? producto.stockCritico : "";
            document.getElementById("categoriaProducto").value = producto.categoriaId;
            document.getElementById("imagenProducto").value = producto.imagen || "";
            document.getElementById("estadoProducto").value = producto.estado || "Activo";
        }
    }

    document.getElementById("formularioProducto").addEventListener("submit", function (evento) {
        evento.preventDefault();
        procesarFormularioProducto(codigo);
    });
}

function procesarFormularioProducto(codigoOriginal) {
    const codigo = document.getElementById("codigoProducto");
    const nombre = document.getElementById("nombreProducto");
    const descripcion = document.getElementById("descripcionProducto");
    const precio = document.getElementById("precioProducto");
    const stock = document.getElementById("stockProducto");
    const stockCritico = document.getElementById("stockCriticoProducto");
    const categoria = document.getElementById("categoriaProducto");
    const imagen = document.getElementById("imagenProducto");
    const estado = document.getElementById("estadoProducto");

    let esValido = true;

    if (textoRequerido(codigo.value, null) && codigo.value.trim().length >= 3) {
        marcarValido(codigo);
    } else {
        marcarInvalido(codigo);
        esValido = false;
    }

    const codigoDuplicado = obtenerProductos().some(function (p) {
        return p.codigo === codigo.value.trim() && p.codigo !== codigoOriginal;
    });
    if (codigoDuplicado) {
        marcarInvalido(codigo);
        esValido = false;
    }

    if (textoRequerido(nombre.value, 100)) {
        marcarValido(nombre);
    } else {
        marcarInvalido(nombre);
        esValido = false;
    }

    if (!descripcion.value || descripcion.value.trim().length <= 500) {
        marcarValido(descripcion);
    } else {
        marcarInvalido(descripcion);
        esValido = false;
    }

    if (numeroEnRango(precio.value, 0)) {
        marcarValido(precio);
    } else {
        marcarInvalido(precio);
        esValido = false;
    }

    if (enteroEnRango(stock.value, 0)) {
        marcarValido(stock);
    } else {
        marcarInvalido(stock);
        esValido = false;
    }

    if (!stockCritico.value || enteroEnRango(stockCritico.value, 0)) {
        marcarValido(stockCritico);
    } else {
        marcarInvalido(stockCritico);
        esValido = false;
    }

    if (categoria.value) {
        marcarValido(categoria);
    } else {
        marcarInvalido(categoria);
        esValido = false;
    }

    if (!esValido) {
        Swal.fire("Revisa el formulario", "Existen campos obligatorios o invalidos.", "error");
        return;
    }

    const producto = {
        codigo: codigo.value.trim(),
        nombre: nombre.value.trim(),
        descripcion: descripcion.value.trim(),
        precio: Number(precio.value),
        stock: Number(stock.value),
        stockCritico: stockCritico.value ? Number(stockCritico.value) : null,
        categoriaId: Number(categoria.value),
        imagen: imagen.value.trim() || "logo-sanrucho.png",
        estado: estado.value
    };

    guardarProducto(producto);

    let mensajeExtra = "";
    if (producto.stockCritico != null && producto.stock <= producto.stockCritico) {
        mensajeExtra = " El stock esta en o bajo el nivel critico.";
    }

    Swal.fire("Correcto", "Producto guardado correctamente." + mensajeExtra, "success").then(function () {
        window.location.href = "/admin/productos";
    });
}
