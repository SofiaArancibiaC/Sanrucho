/* ============================================================
   Sanrucho - productos.js
   Catálogo público + mantenedor administrativo de productos
   ============================================================ */

/* ==================== FUNCIONES AUXILIARES ==================== */

function obtenerProductos() {
    if (typeof obtenerColeccion !== "function" || typeof SANRUCHO_KEYS === "undefined") return [];
    return obtenerColeccion(SANRUCHO_KEYS.productos);
}

function obtenerProductoPorCodigo(codigo) {
    return obtenerProductos().find(function (p) { return p.codigo === codigo; });
}

function obtenerColeccionCategorias() {
    let cats = [];
    if (typeof obtenerColeccion === "function" && typeof SANRUCHO_KEYS !== "undefined") {
        cats = obtenerColeccion(SANRUCHO_KEYS.categorias || SANRUCHO_KEYS.catalogo || "categorias");
    }
    // Respaldo base en caso de que aún no existan en localStorage
    if (!cats || cats.length === 0) {
        cats = [
            { id: 1, nombre: "Anillos" },
            { id: 2, nombre: "Collares" },
            { id: 3, nombre: "Aros" },
            { id: 4, nombre: "Pulseras" }
        ];
    }
    return cats;
}

function obtenerNombreCategoria(categoriaId) {
    const cats = obtenerColeccionCategorias();
    const encontrada = cats.find(function (c) { 
        return Number(c.id) === Number(categoriaId) || c.nombre === categoriaId; 
    });
    return encontrada ? encontrada.nombre : "General";
}

function rutaImagenProducto(producto) {
    if (!producto || !producto.imagen) return "/static/img/logo-sanrucho.png";
    if (/^(https?:)?\//.test(producto.imagen)) return producto.imagen;
    return "/static/img/" + producto.imagen;
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

/* ==================== MANTENEDOR ADMINISTRATIVO ==================== */

function llenarSelectCategorias(idSelect, incluirTodas) {
    const select = document.getElementById(idSelect);
    if (!select) return;

    const categorias = obtenerColeccionCategorias();

    select.innerHTML = "";
    if (incluirTodas) {
        select.innerHTML += '<option value="">Todas las categorías</option>';
    } else {
        select.innerHTML += '<option value="" selected disabled>Selecciona una categoría...</option>';
    }

    categorias.forEach(function (cat) {
        const id = cat.id != null ? cat.id : cat.nombre;
        const nombre = cat.nombre || cat.titulo || cat.id;
        select.innerHTML += '<option value="' + id + '">' + nombre + '</option>';
    });
}

function cargarTablaProductosAdmin() {
    const productos = obtenerProductos();
    const cuerpoTabla = document.getElementById("cuerpoTablaProductos");
    if (!cuerpoTabla) return;

    cuerpoTabla.innerHTML = "";

    // Asegurar que el botón nuevo esté siempre visible para administradores
    const botonNuevo = document.getElementById("btnNuevoProducto");
    if (botonNuevo) botonNuevo.style.display = "inline-block";

    if (productos.length === 0) {
        cuerpoTabla.innerHTML = '<tr><td colspan="8" class="text-center py-4 text-muted">No hay productos registrados.</td></tr>';
        return;
    }

    productos.forEach(function (producto) {
        const critico = producto.stockCritico != null && producto.stock <= producto.stockCritico;
        const fila = document.createElement("tr");

        const acciones = 
            '<button class="btn btn-sm btn-outline-accent me-1" onclick="verProductoAdmin(\'' + producto.codigo + '\')" title="Ver"><i class="bi bi-eye"></i></button>' +
            '<button class="btn btn-sm btn-outline-primary me-1" onclick="window.location.href=\'/admin/productos/form?codigo=' + producto.codigo + '\'" title="Editar"><i class="bi bi-pencil"></i></button>' +
            '<button class="btn btn-sm btn-outline-danger" onclick="confirmarEliminarProducto(\'' + producto.codigo + '\')" title="Eliminar"><i class="bi bi-trash"></i></button>';

        fila.innerHTML =
            '<td><strong>' + producto.codigo + '</strong></td>' +
            '<td><img src="' + rutaImagenProducto(producto) + '" alt="' + producto.nombre + '" height="40" style="object-fit:contain; border-radius:6px;"></td>' +
            '<td>' + producto.nombre + '</td>' +
            '<td>' + obtenerNombreCategoria(producto.categoriaId) + '</td>' +
            '<td>' + (typeof formatearPrecio === "function" ? formatearPrecio(producto.precio) : "$" + producto.precio) + '</td>' +
            '<td class="' + (critico ? "stock-critico text-danger fw-bold" : "") + '">' + producto.stock + (critico ? ' <i class="bi bi-exclamation-triangle-fill"></i>' : '') + '</td>' +
            '<td><span class="badge ' + (producto.estado === "Activo" ? "bg-success" : "bg-secondary") + '">' + producto.estado + '</span></td>' +
            '<td class="text-nowrap">' + acciones + '</td>';

        cuerpoTabla.appendChild(fila);
    });

    if (window.jQuery && $.fn && $.fn.DataTable) {
        if ($.fn.DataTable.isDataTable("#tablaProductos")) {
            $("#tablaProductos").DataTable().destroy();
        }
        $("#tablaProductos").DataTable({
            language: { url: "https://cdn.datatables.net/plug-ins/1.13.11/i18n/es-ES.json" },
            order: []
        });
    }
}

function verProductoAdmin(codigo) {
    const producto = obtenerProductoPorCodigo(codigo);
    if (!producto) return;
    Swal.fire({
        title: producto.nombre,
        html:
            '<img src="' + rutaImagenProducto(producto) + '" class="img-fluid mb-3" style="max-height:160px;"><br>' +
            '<p class="text-start"><strong>Código:</strong> ' + producto.codigo + '<br>' +
            '<strong>Categoría:</strong> ' + obtenerNombreCategoria(producto.categoriaId) + '<br>' +
            '<strong>Precio:</strong> ' + (typeof formatearPrecio === "function" ? formatearPrecio(producto.precio) : "$" + producto.precio) + '<br>' +
            '<strong>Stock:</strong> ' + producto.stock + '<br>' +
            '<strong>Stock crítico:</strong> ' + (producto.stockCritico != null ? producto.stockCritico : "-") + '<br>' +
            '<strong>Descripción:</strong> ' + (producto.descripcion || "-") + '</p>',
        confirmButtonText: "Cerrar"
    });
}

function confirmarEliminarProducto(codigo) {
    Swal.fire({
        title: "¿Eliminar producto?",
        text: "Esta acción no se puede deshacer.",
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

function inicializarFormularioProducto() {
    llenarSelectCategorias("categoriaProducto", false);

    const parametros = new URLSearchParams(window.location.search);
    const codigo = parametros.get("codigo");

    if (codigo) {
        const producto = obtenerProductoPorCodigo(codigo);
        if (producto) {
            const titulo = document.getElementById("tituloFormularioProducto");
            if (titulo) titulo.textContent = "Editar producto";
            
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

    const form = document.getElementById("formularioProducto");
    if (form) {
        form.addEventListener("submit", function (evento) {
            evento.preventDefault();
            procesarFormularioProducto(codigo);
        });
    }
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

    if (typeof textoRequerido === "function" ? (textoRequerido(codigo.value, null) && codigo.value.trim().length >= 3) : codigo.value.trim().length >= 3) {
        if (typeof marcarValido === "function") marcarValido(codigo);
    } else {
        if (typeof marcarInvalido === "function") marcarInvalido(codigo);
        esValido = false;
    }

    const codigoDuplicado = obtenerProductos().some(function (p) {
        return p.codigo === codigo.value.trim() && p.codigo !== codigoOriginal;
    });
    if (codigoDuplicado) {
        if (typeof marcarInvalido === "function") marcarInvalido(codigo);
        esValido = false;
    }

    if (typeof textoRequerido === "function" ? textoRequerido(nombre.value, 100) : (nombre.value.trim() !== "")) {
        if (typeof marcarValido === "function") marcarValido(nombre);
    } else {
        if (typeof marcarInvalido === "function") marcarInvalido(nombre);
        esValido = false;
    }

    if (!descripcion.value || descripcion.value.trim().length <= 500) {
        if (typeof marcarValido === "function") marcarValido(descripcion);
    } else {
        if (typeof marcarInvalido === "function") marcarInvalido(descripcion);
        esValido = false;
    }

    if (precio.value !== "" && Number(precio.value) >= 0) {
        if (typeof marcarValido === "function") marcarValido(precio);
    } else {
        if (typeof marcarInvalido === "function") marcarInvalido(precio);
        esValido = false;
    }

    if (stock.value !== "" && Number(stock.value) >= 0) {
        if (typeof marcarValido === "function") marcarValido(stock);
    } else {
        if (typeof marcarInvalido === "function") marcarInvalido(stock);
        esValido = false;
    }

    if (!stockCritico.value || Number(stockCritico.value) >= 0) {
        if (typeof marcarValido === "function") marcarValido(stockCritico);
    } else {
        if (typeof marcarInvalido === "function") marcarInvalido(stockCritico);
        esValido = false;
    }

    if (categoria.value) {
        if (typeof marcarValido === "function") marcarValido(categoria);
    } else {
        if (typeof marcarInvalido === "function") marcarInvalido(categoria);
        esValido = false;
    }

    if (!esValido) {
        Swal.fire("Revisa el formulario", "Existen campos obligatorios o inválidos.", "error");
        return;
    }

    const producto = {
        codigo: codigo.value.trim(),
        nombre: nombre.value.trim(),
        descripcion: descripcion.value.trim(),
        precio: Number(precio.value),
        stock: Number(stock.value),
        stockCritico: stockCritico.value ? Number(stockCritico.value) : null,
        categoriaId: isNaN(Number(categoria.value)) ? categoria.value : Number(categoria.value),
        imagen: imagen.value.trim() || "logo-sanrucho.png",
        estado: estado.value
    };

    guardarProducto(producto);

    let mensajeExtra = "";
    if (producto.stockCritico != null && producto.stock <= producto.stockCritico) {
        mensajeExtra = " El stock está en o bajo el nivel crítico.";
    }

    Swal.fire("Correcto", "Producto guardado correctamente." + mensajeExtra, "success").then(function () {
        window.location.href = "/admin/productos";
    });
}

/* ==================== CATALOGO PUBLICO ==================== */

document.addEventListener("DOMContentLoaded", function () {
    const contenedor = document.getElementById("contenedorProductos");
    const filtroCat = document.getElementById("filtroCategoria");
    const filtroPrecio = document.getElementById("filtroPrecio");
    const precioEtiqueta = document.getElementById("precioEtiqueta");
    const buscarInput = document.getElementById("buscarInput");
    const btnLimpiar = document.getElementById("btnLimpiarFiltros");
    const contador = document.getElementById("contadorProductos");

    if (!contenedor || !filtroCat || !filtroPrecio || !precioEtiqueta || !buscarInput || !btnLimpiar || !contador) return;

    const categorias = obtenerColeccionCategorias();
    const productos = obtenerProductos();

    function poblarCategorias() {
        categorias.forEach(function (cat) {
            const opcion = document.createElement("option");
            opcion.value = cat.id;
            opcion.textContent = cat.nombre;
            filtroCat.appendChild(opcion);
        });
    }

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
                    <a href="/producto-detalle?codigo=${prod.codigo}" class="text-decoration-none">
                        <div class="card-img-wrapper bg-light text-center p-3">
                            <img src="${rutaImagenProducto(prod)}" class="card-img-top product-img" alt="${prod.nombre}" onerror="this.src='https://placehold.co/300x200?text=Sin+Imagen'">
                        </div>
                    </a>
                    <div class="card-body d-flex flex-column">
                        <span class="card-category">${obtenerNombreCategoria(prod.categoriaId)}</span>
                        <a href="/producto-detalle?codigo=${prod.codigo}" class="text-decoration-none text-reset">
                            <h6 class="card-title fw-bold mt-1 mb-2 text-truncate" title="${prod.nombre}">${prod.nombre}</h6>
                        </a>
                        <p class="card-text text-muted small flex-grow-1">${prod.descripcion || ""}</p>
                        <div class="card-price mb-2">${typeof formatearPrecio === "function" ? formatearPrecio(prod.precio) : "$" + prod.precio}</div>
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
                </div>`;
            contenedor.appendChild(col);
        });

        asignarEventosCarrito();
    }

    function aplicarFiltros() {
        const texto = buscarInput.value.toLowerCase().trim();
        const catSeleccionada = parseInt(filtroCat.value, 10);
        const precioMaximo = parseInt(filtroPrecio.value, 10);

        if (precioEtiqueta) {
            precioEtiqueta.textContent = typeof formatearPrecio === "function" ? formatearPrecio(precioMaximo) : "$" + precioMaximo;
        }

        const filtrados = productos.filter(function (prod) {
            if (prod.estado !== "Activo") return false;
            const coincideTexto = prod.nombre.toLowerCase().includes(texto) || prod.codigo.toLowerCase().includes(texto);
            const coincideCat = (catSeleccionada === 0 || isNaN(catSeleccionada) || prod.categoriaId === catSeleccionada);
            const coincidePrecio = prod.precio <= precioMaximo;
            return coincideTexto && coincideCat && coincidePrecio;
        });

        renderizarProductos(filtrados);
    }

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
                if (typeof actualizarBadgeCarrito === "function") actualizarBadgeCarrito();

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