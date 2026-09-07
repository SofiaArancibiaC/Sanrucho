/* Sanrucho - storage.js
   Maneja las colecciones de localStorage (base de datos simulada),
   la sesion del usuario y la barra de navegacion comun a todas las paginas. */

   const VERSION_DATOS = "v1.2";

if (localStorage.getItem("sanrucho_version") !== VERSION_DATOS) {
    localStorage.clear();
    localStorage.setItem("sanrucho_version", VERSION_DATOS);
    inicializarDatos();
}


const SANRUCHO_KEYS = {
    usuarios: "sanrucho_usuarios",
    productos: "sanrucho_productos",
    categorias: "sanrucho_categorias",
    regiones: "sanrucho_regiones",
    comunas: "sanrucho_comunas",
    roles: "sanrucho_roles",
    carrito: "sanrucho_carrito",
    contactos: "sanrucho_contactos",
    blog: "sanrucho_blog",
    resenas: "sanrucho_resenas", 
    sesion: "sanrucho_sesion"
};

function obtenerColeccion(clave) {
    return JSON.parse(localStorage.getItem(clave)) || [];
}

function guardarColeccion(clave, datos) {
    localStorage.setItem(clave, JSON.stringify(datos));
}

/* ---------- Datos iniciales de prueba ---------- */

function inicializarDatos() {
    if (!localStorage.getItem(SANRUCHO_KEYS.roles)) {
        guardarColeccion(SANRUCHO_KEYS.roles, [
            { id: 1, nombre: "Dueño" },
            { id: 2, nombre: "Admin" },
            { id: 3, nombre: "Cliente" }
        ]);
    }

    if (!localStorage.getItem(SANRUCHO_KEYS.categorias)) {
        guardarColeccion(SANRUCHO_KEYS.categorias, [
            { id: 1, nombre: "Maquillaje" },
            { id: 2, nombre: "Peluches" },
            { id: 3, nombre: "Llaveros" },
            { id: 4, nombre: "Blind-box" },
            { id: 5, nombre: "Papelería" },
            { id: 6, nombre: "Tecnología" },
            { id: 7, nombre: "Hogar" }
        ]);
    }

    if (!localStorage.getItem(SANRUCHO_KEYS.regiones)) {
        guardarColeccion(SANRUCHO_KEYS.regiones, [
            { id: 1, nombre: "Region Metropolitana" },
            { id: 2, nombre: "Valparaiso" },
            { id: 3, nombre: "Biobio" }
        ]);
    }

    if (!localStorage.getItem(SANRUCHO_KEYS.comunas)) {
        guardarColeccion(SANRUCHO_KEYS.comunas, [
            { id: 1, regionId: 1, nombre: "Santiago" },
            { id: 2, regionId: 1, nombre: "Providencia" },
            { id: 3, regionId: 1, nombre: "Maipu" },
            { id: 4, regionId: 2, nombre: "Valparaiso" },
            { id: 5, regionId: 2, nombre: "Vina del Mar" },
            { id: 6, regionId: 3, nombre: "Concepcion" },
            { id: 7, regionId: 3, nombre: "Talcahuano" }
        ]);
    }

    if (!localStorage.getItem(SANRUCHO_KEYS.usuarios)) {
        guardarColeccion(SANRUCHO_KEYS.usuarios, [
            {
                run: "123456785", nombre: "Dueño", apellidos: "Sanrucho",
                correo: "dueño@gmail.com", password: "dueño123",
                fechaNacimiento: "1990-01-01", rolId: 1,
                regionId: 1, comunaId: 1, direccion: "Av. Principal 123",
                estado: "Activo"
            },
            {
                run: "987654325", nombre: "Admin", apellidos: "Sanrucho",
                correo: "admin@gmail.com", password: "admin123",
                fechaNacimiento: "1992-05-14", rolId: 2,
                regionId: 1, comunaId: 2, direccion: "Calle Venta 456",
                estado: "Activo"
            },
            {
                run: "112223339", nombre: "Camila", apellidos: "Soto Perez",
                correo: "camila@gmail.com", password: "camila123",
                fechaNacimiento: "1998-03-22", rolId: 3,
                regionId: 2, comunaId: 4, direccion: "Los Aromos 789",
                estado: "Activo"
            },
            {
                run: "201113334", nombre: "Diego", apellidos: "Fernandez Rojas",
                correo: "diego@gmail.cl", password: "diego123",
                fechaNacimiento: "2001-11-09", rolId: 3,
                regionId: 3, comunaId: 6, direccion: "Las Rosas 321",
                estado: "Activo"
            }
        ]);
    }

    if (!localStorage.getItem(SANRUCHO_KEYS.productos)) {
        guardarColeccion(SANRUCHO_KEYS.productos, [
            { codigo: "HK-PLUSH-001", nombre: "Peluche Hello Kitty Clásico 30cm", descripcion: "Peluche suave de Hello Kitty con su icónico moño rojo.", precio: 24990, stock: 15, stockCritico: 3, categoriaId: 2, imagen: "/static/img/productos/hello-kitty1.webp", estado: "Activo" },
            { codigo: "KR-PLUSH-002", nombre: "Peluche Kuromi con Capucha 25cm", descripcion: "Kuromi adorable con su capucha de conejito negra.", precio: 22990, stock: 20, stockCritico: 5, categoriaId: 2, imagen: "/static/img/productos/kuromi-peluche1.webp", estado: "Activo" },
            { codigo: "MM-PLUSH-003", nombre: "Peluche My Melody Sleeping 35cm", descripcion: "My Melody en posición de dormir, súper suave.", precio: 27990, stock: 8, stockCritico: 2, categoriaId: 2, imagen: "/static/img/productos/melody-peluche1.webp", estado: "Activo" },
            { codigo: "CN-PLUSH-004", nombre: "Peluche Cinnamoroll Nube 40cm", descripcion: "Cinnamoroll esponjoso con orejas largas.", precio: 29990, stock: 12, stockCritico: 4, categoriaId: 2, imagen: "/static/img/productos/peluche-cinna-verano.webp", estado: "Activo" },
            { codigo: "HK-NOTE-101", nombre: "Cuaderno Hello Kitty A5 Rayas", descripcion: "Cuaderno con rayas, 80 hojas, diseño Hello Kitty.", precio: 5990, stock: 10, stockCritico: 3, categoriaId: 5, imagen: "/static/img/productos/cuaderno-hk1.jpg", estado: "Activo" },
            { codigo: "PP-MUG-301", nombre: "Taza Cerámica Pompompurin 350ml", descripcion: "Taza de cerámica con diseño 3D de Pompompurin.", precio: 12990, stock: 25, stockCritico: 5, categoriaId: 7, imagen: "/static/img/productos/tazon-ppm1.jpg", estado: "Activo" },
            { codigo: "BM-WALLET-402", nombre: "Cartera Badtz-Maru Negro", descripcion: "Cartera de material sintético con múltiples compartimientos.", precio: 13990, stock: 2, stockCritico: 3, categoriaId: 7, imagen: "/static/img/productos/monedero-bm1.webp", estado: "Activo" },
            { codigo: "LTS-PLUSH-005", nombre: "Peluche Little Twin Stars Set", descripcion: "Set de Kiki y Lala 20cm cada uno.", precio: 34990, stock: 6, stockCritico: 2, categoriaId: 2, imagen: "/static/img/productos/lts-peluche1.jpg", estado: "Activo" }
        ]);
    }

    if (!localStorage.getItem(SANRUCHO_KEYS.blog)) {
        guardarColeccion(SANRUCHO_KEYS.blog, [
            { id: 1, titulo: "Nexo Gaming abre sus puertas", resumen: "Nace una nueva tienda online pensada por y para la comunidad gamer de Chile.", imagen: "blog/apertura.svg", fecha: "2026-08-01", slug: "detalle-1" },
            { id: 2, titulo: "Los lanzamientos mas esperados del ano", resumen: "Repasamos los titulos que marcaran tendencia en los proximos meses.", imagen: "blog/lanzamientos.svg", fecha: "2026-08-10", slug: "detalle-2" },
            { id: 3, titulo: "5 curiosidades del mundo gamer", resumen: "Datos curiosos que quizas no conocias sobre la industria de los videojuegos.", imagen: "blog/curiosidades.svg", fecha: "2026-08-20", slug: "detalle-1" }
        ]);
    }

    if (!localStorage.getItem(SANRUCHO_KEYS.carrito)) {
        guardarColeccion(SANRUCHO_KEYS.carrito, []);
    }

    if (!localStorage.getItem(SANRUCHO_KEYS.contactos)) {
        guardarColeccion(SANRUCHO_KEYS.contactos, []);
    }

    if (!localStorage.getItem(SANRUCHO_KEYS.resenas)) {
    guardarColeccion(SANRUCHO_KEYS.resenas, [
        {
            id: 1,
            productoCodigo: "HK-PLUSH-001",
            usuarioNombre: "Camila S.",
            calificacion: 5,
            comentario: "Hermoso peluche, la calidad de la tela es muy buena y llegó bien empacado.",
            fecha: "2026-07-15"
        },
        {
            id: 2,
            productoCodigo: "HK-PLUSH-001",
            usuarioNombre: "Diego F.",
            calificacion: 4,
            comentario: "Muy lindo, un poco más pequeño de lo que esperaba pero cumple.",
            fecha: "2026-07-28"
        },
        {
            id: 3,
            productoCodigo: "CN-PLUSH-004",
            usuarioNombre: "Camila S.",
            calificacion: 5,
            comentario: "Cinnamoroll quedó perfecto en mi pieza, se ve igual que en la foto.",
            fecha: "2026-08-02"
        }
    ]);
}
}

/* ---------- Sesion ---------- */

function obtenerSesion() {
    return JSON.parse(localStorage.getItem(SANRUCHO_KEYS.sesion)) || null;
}

function guardarSesion(usuario) {
    localStorage.setItem(SANRUCHO_KEYS.sesion, JSON.stringify(usuario));
}

function cerrarSesion() {
    localStorage.removeItem(SANRUCHO_KEYS.sesion);
    window.location.href = "/login";
}

function obtenerNombreRol(rolId) {
    const rol = obtenerColeccion(SANRUCHO_KEYS.roles).find(function (r) { return r.id === rolId; });
    return rol ? rol.nombre : "";
}

/* Protege paginas administrativas segun el rol permitido. */
function protegerPaginaAdmin(rolesPermitidos) {
    const sesion = obtenerSesion();
    if (!sesion || rolesPermitidos.indexOf(sesion.rolId) === -1) {
        Swal.fire({
            title: "Acceso restringido",
            text: "Debes iniciar sesion con una cuenta autorizada para ver esta pagina.",
            icon: "warning",
            confirmButtonText: "Ir a Iniciar sesion"
        }).then(function () {
            window.location.href = "/login";
        });
        return false;
    }
    return true;
}

/* ---------- Navbar dinamica ---------- */

function actualizarNavbar() {
    const sesion = obtenerSesion();
    const navInvitado = document.getElementById("navInvitado");
    const navUsuario = document.getElementById("navUsuario");
    const navUsuarioNombre = document.getElementById("navUsuarioNombre");
    const navAdminItem = document.getElementById("navAdminItem");
    const btnCerrarSesion = document.getElementById("btnCerrarSesion");

    if (!navInvitado || !navUsuario) return;

    if (sesion) {
        navInvitado.style.display = "none";
        navUsuario.style.display = "block";
        if (navUsuarioNombre) navUsuarioNombre.textContent = sesion.nombre;
        if (navAdminItem && (sesion.rolId === 1 || sesion.rolId === 2)) {
            navAdminItem.style.display = "block";
        }
    } else {
        navInvitado.style.display = "flex";
        navUsuario.style.display = "none";
        if (navAdminItem) navAdminItem.style.display = "none";
    }

    if (btnCerrarSesion) {
        btnCerrarSesion.addEventListener("click", function (evento) {
            evento.preventDefault();
            cerrarSesion();
        });
    }
}

/* ---------- Badge del carrito ---------- */

function actualizarBadgeCarrito() {
    const carrito = obtenerColeccion(SANRUCHO_KEYS.carrito);
    let totalItems = 0;
    for (let i = 0; i < carrito.length; i++) {
        totalItems += carrito[i].cantidad;
    }
    const badge = document.getElementById("carritoBadge");
    if (!badge) return;
    if (totalItems > 0) {
        badge.textContent = totalItems;
        badge.style.display = "block";
    } else {
        badge.style.display = "none";
    }
}

/* Oculta la seccion de Usuarios en el menu administrativo para el rol Vendedor. */
function ocultarUsuariosSiVendedor() {
    const sesion = obtenerSesion();
    if (!sesion || sesion.rolId !== 2) return;
    const enlaceSidebar = document.getElementById("sidebarUsuariosLink");
    const enlaceOffcanvas = document.getElementById("offcanvasUsuariosLink");
    if (enlaceSidebar) enlaceSidebar.style.display = "none";
    if (enlaceOffcanvas) enlaceOffcanvas.style.display = "none";
}

/* Marca como activo el enlace del navbar/sidebar que corresponde a la pagina actual. */
function marcarEnlaceActivo() {
    const rutaActual = window.location.pathname;
    document.querySelectorAll(".nav-link").forEach(function (enlace) {
        const href = enlace.getAttribute("href");
        if (href && href !== "#" && href === rutaActual) {
            enlace.classList.add("active");
        }
    });
}

function formatearPrecio(valor) {
    return "$" + Number(valor).toLocaleString("es-CL");
}

function actualizarBannerIndex() {
    const sesion = obtenerSesion();
    const bannerInvitado = document.getElementById("bannerInvitado");
    const bannerLogueado = document.getElementById("bannerLogueado");
    const bannerNombreUsuario = document.getElementById("bannerNombreUsuario");

    if (!bannerInvitado) return; // Si no estamos en el index, no hace nada

    if (sesion) {
        // Si hay sesión iniciada: ocultar botón de login y mostrar saludo
        bannerInvitado.style.display = "none";
        if (bannerLogueado) bannerLogueado.style.display = "block";
        if (bannerNombreUsuario) bannerNombreUsuario.textContent = sesion.nombre;
    } else {
        // Si es invitado: mostrar botón de login
        bannerInvitado.style.display = "block";
        if (bannerLogueado) bannerLogueado.style.display = "none";
    }
}

function generarSiguienteId(coleccion) {
    if (coleccion.length === 0) return 1;
    return Math.max(...coleccion.map(item => item.id)) + 1;
}

document.addEventListener("DOMContentLoaded", function () {
    inicializarDatos();
    actualizarNavbar();
    actualizarBadgeCarrito();
    marcarEnlaceActivo();
});


document.addEventListener("DOMContentLoaded", function () {
    inicializarDatos();
    actualizarNavbar();
    actualizarBannerIndex(); // <-- Agregar aquí
    actualizarBadgeCarrito();
    marcarEnlaceActivo();
});