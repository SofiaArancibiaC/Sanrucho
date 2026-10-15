// ============================================================
// data/seed.ts
// Datos iniciales de prueba. Es el equivalente de inicializarDatos()
// de storage.js: se ejecuta una sola vez al arrancar App.tsx y solo
// crea cada colección si todavía no existe en localStorage.
// ============================================================

import { SANRUCHO_KEYS } from "../storage/claves";

function sembrar<T>(clave: string, datos: T[]): void {
  if (!localStorage.getItem(clave)) {
    localStorage.setItem(clave, JSON.stringify(datos));
  }
}

export function inicializarDatos(): void {
  sembrar(SANRUCHO_KEYS.roles, [
    { id: 1, nombre: "Dueño" },
    { id: 2, nombre: "Admin" },
    { id: 3, nombre: "Cliente" },
  ]);

  sembrar(SANRUCHO_KEYS.categorias, [
    { id: 1, nombre: "Maquillaje" },
    { id: 2, nombre: "Peluches" },
    { id: 3, nombre: "Llaveros" },
    { id: 4, nombre: "Blind-box" },
    { id: 5, nombre: "Papelería" },
    { id: 6, nombre: "Tecnología" },
    { id: 7, nombre: "Hogar" },
  ]);

  sembrar(SANRUCHO_KEYS.regiones, [
    { id: 1, nombre: "Region Metropolitana" },
    { id: 2, nombre: "Valparaiso" },
    { id: 3, nombre: "Biobio" },
  ]);

  sembrar(SANRUCHO_KEYS.comunas, [
    { id: 1, regionId: 1, nombre: "Santiago" },
    { id: 2, regionId: 1, nombre: "Providencia" },
    { id: 3, regionId: 1, nombre: "Maipu" },
    { id: 4, regionId: 2, nombre: "Valparaiso" },
    { id: 5, regionId: 2, nombre: "Vina del Mar" },
    { id: 6, regionId: 3, nombre: "Concepcion" },
    { id: 7, regionId: 3, nombre: "Talcahuano" },
  ]);

  sembrar(SANRUCHO_KEYS.usuarios, [
    {
      run: "123456785",
      nombre: "Dueño",
      apellidos: "Sanrucho",
      correo: "dueno@gmail.com",
      password: "dueno123",
      fechaNacimiento: "1990-01-01",
      rolId: 1,
      regionId: 1,
      comunaId: 1,
      direccion: "Av. Principal 123",
      estado: "Activo",
    },
    {
      run: "987654325",
      nombre: "Admin",
      apellidos: "Sanrucho",
      correo: "admin@gmail.com",
      password: "admin123",
      fechaNacimiento: "1992-05-14",
      rolId: 2,
      regionId: 1,
      comunaId: 2,
      direccion: "Calle Venta 456",
      estado: "Activo",
    },
    {
      run: "112223339",
      nombre: "Camila",
      apellidos: "Soto Perez",
      correo: "camila@gmail.com",
      password: "camila123",
      fechaNacimiento: "1998-03-22",
      rolId: 3,
      regionId: 2,
      comunaId: 4,
      direccion: "Los Aromos 789",
      estado: "Activo",
    },
    {
      run: "201113334",
      nombre: "Diego",
      apellidos: "Fernandez Rojas",
      correo: "diego@gmail.cl",
      password: "diego123",
      fechaNacimiento: "2001-11-09",
      rolId: 3,
      regionId: 3,
      comunaId: 6,
      direccion: "Las Rosas 321",
      estado: "Activo",
    },
  ]);

  sembrar(SANRUCHO_KEYS.productos, [
    { codigo: "HK-PLUSH-001", nombre: "Peluche Hello Kitty Clásico 30cm", descripcion: "Peluche suave de Hello Kitty con su icónico moño rojo.", precio: 24990, stock: 15, stockCritico: 3, categoriaId: 2, imagen: "/static/img/productos/hello-kitty1.webp", estado: "Activo" },
    { codigo: "KR-PLUSH-002", nombre: "Peluche Kuromi con Capucha 25cm", descripcion: "Kuromi adorable con su capucha de conejito negra.", precio: 22990, stock: 20, stockCritico: 5, categoriaId: 2, imagen: "/static/img/productos/kuromi-peluche1.webp", estado: "Activo" },
    { codigo: "MM-PLUSH-003", nombre: "Peluche My Melody Sleeping 35cm", descripcion: "My Melody en posición de dormir, súper suave.", precio: 27990, stock: 8, stockCritico: 2, categoriaId: 2, imagen: "/static/img/productos/melody-peluche1.webp", estado: "Activo" },
    { codigo: "CN-PLUSH-004", nombre: "Peluche Cinnamoroll Nube 40cm", descripcion: "Cinnamoroll esponjoso con orejas largas.", precio: 29990, stock: 12, stockCritico: 4, categoriaId: 2, imagen: "/static/img/productos/peluche-cinna-verano.webp", estado: "Activo" },
    { codigo: "HK-NOTE-101", nombre: "Cuaderno Hello Kitty A5 Rayas", descripcion: "Cuaderno con rayas, 80 hojas, diseño Hello Kitty.", precio: 5990, stock: 10, stockCritico: 3, categoriaId: 5, imagen: "/static/img/productos/cuaderno-hk1.jpg", estado: "Activo" },
    { codigo: "PP-MUG-301", nombre: "Taza Cerámica Pompompurin 350ml", descripcion: "Taza de cerámica con diseño 3D de Pompompurin.", precio: 12990, stock: 25, stockCritico: 5, categoriaId: 7, imagen: "/static/img/productos/tazon-ppm1.jpg", estado: "Activo" },
    { codigo: "BM-WALLET-402", nombre: "Cartera Badtz-Maru Negro", descripcion: "Cartera de material sintético con múltiples compartimientos.", precio: 13990, stock: 2, stockCritico: 3, categoriaId: 7, imagen: "/static/img/productos/monedero-bm1.webp", estado: "Activo" },
    { codigo: "LTS-PLUSH-005", nombre: "Peluche Little Twin Stars Set", descripcion: "Set de Kiki y Lala 20cm cada uno.", precio: 34990, stock: 6, stockCritico: 2, categoriaId: 2, imagen: "/static/img/productos/lts-peluche1.jpg", estado: "Activo" },
  ]);

  sembrar(SANRUCHO_KEYS.blog, [
    {
      id: 1,
      titulo: "Sanrucho les da la bienvenida",
      resumen:
        "Después de meses de trabajo, hoy nace Sanrucho, una nueva tienda online pensada para la comodidad de nuestros clientes.",
      imagen: "/static/img/logo-sanrucho.png",
      fecha: "2026-09-01",
      slug: "detalle-1",
    },
    {
      id: 2,
      titulo: "Cómo crear tu cuenta y hacer tu primer pedido en Sanrucho",
      resumen:
        "Una guía paso a paso para crear tu cuenta, recorrer el catálogo y completar tu primer pedido sin complicaciones.",
      imagen: "/static/img/comprar.png",
      fecha: "2026-09-04",
      slug: "detalle-2",
    },
  ]);

  sembrar(SANRUCHO_KEYS.carrito, []);
  sembrar(SANRUCHO_KEYS.contactos, []);

  sembrar(SANRUCHO_KEYS.resenas, [
    {
      id: 1,
      productoCodigo: "HK-PLUSH-001",
      usuarioNombre: "Camila S.",
      calificacion: 5,
      comentario:
        "Hermoso peluche, la calidad de la tela es muy buena y llegó bien empacado.",
      fecha: "2026-07-15",
    },
    {
      id: 2,
      productoCodigo: "HK-PLUSH-001",
      usuarioNombre: "Diego F.",
      calificacion: 4,
      comentario:
        "Muy lindo, un poco más pequeño de lo que esperaba pero cumple.",
      fecha: "2026-07-28",
    },
    {
      id: 3,
      productoCodigo: "CN-PLUSH-004",
      usuarioNombre: "Camila S.",
      calificacion: 5,
      comentario:
        "Cinnamoroll quedó perfecto en mi pieza, se ve igual que en la foto.",
      fecha: "2026-08-02",
    },
  ]);
}

/** Genera el siguiente id disponible de una colección (equivalente a generarSiguienteId). */
export function generarSiguienteId<T extends { id: number }>(coleccion: T[]): number {
  if (coleccion.length === 0) return 1;
  return Math.max(...coleccion.map((item) => item.id)) + 1;
}
