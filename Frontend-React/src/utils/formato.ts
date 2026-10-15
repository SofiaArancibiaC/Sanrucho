// ============================================================
// utils/formato.ts
// Formateo de precios y rutas de imágenes (equivale a
// formatearPrecio() y rutaImagenProducto() de los .js viejos).
// ============================================================

import type { Producto } from "../types";

/** Da formato de moneda chilena: 24990 -> "$24.990". */
export function formatearPrecio(valor: number): string {
  return "$" + Number(valor).toLocaleString("es-CL");
}

/**
 * Normaliza la ruta de una imagen de producto.
 * - Si no hay imagen devuelve el logo.
 * - Si la ruta es absoluta (empieza con / o http) se respeta.
 * - Si es relativa se asume dentro de /static/img/.
 */
export function rutaImagenProducto(producto: Pick<Producto, "imagen"> | null): string {
  if (!producto || !producto.imagen) return "/static/img/logo-sanrucho.png";
  if (/^(https?:)?\//.test(producto.imagen)) return producto.imagen;
  return "/static/img/" + producto.imagen;
}

/** Da formato de fecha "dd de ... " simple a partir de un ISO (YYYY-MM-DD). */
export function formatearFecha(fechaISO: string): string {
  if (!fechaISO) return "—";
  const partes = fechaISO.split("-");
  if (partes.length !== 3) return fechaISO;
  const [anio, mes, dia] = partes;
  return `${dia}/${mes}/${anio}`;
}
