// ============================================================
// types/index.ts
// Tipos del dominio de Sanrucho. Se derivan directamente de la
// forma de los objetos que guardaba storage.js en localStorage.
// ============================================================

export type Estado = "Activo" | "Inactivo";

export interface Rol {
  id: number;
  nombre: string;
}

export interface Categoria {
  id: number;
  nombre: string;
}

export interface Region {
  id: number;
  nombre: string;
}

export interface Comuna {
  id: number;
  regionId: number;
  nombre: string;
}

export interface Usuario {
  run: string;
  nombre: string;
  apellidos: string;
  correo: string;
  password: string;
  fechaNacimiento: string;
  rolId: number;
  regionId: number;
  comunaId: number;
  direccion: string;
  estado: Estado;
}

// La sesión guardada es simplemente el usuario completo.
export type Sesion = Usuario;

export interface Producto {
  codigo: string;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  stockCritico: number | null;
  categoriaId: number;
  imagen: string;
  estado: Estado;
}

// El carrito guarda una copia reducida del producto + la cantidad.
export interface ItemCarrito {
  codigo: string;
  nombre: string;
  precio: number;
  imagen: string;
  cantidad: number;
}

export interface Noticia {
  id: number;
  titulo: string;
  resumen: string;
  imagen: string;
  fecha: string;
  slug: string;
}

export interface Mensaje {
  id: number;
  nombre: string;
  email: string;
  mensaje: string;
  fecha: string;
}

export interface Resena {
  id: number;
  productoCodigo: string;
  usuarioNombre: string;
  calificacion: number;
  comentario: string;
  fecha: string;
}
