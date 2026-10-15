// ============================================================
// hooks/useProductos.ts
// Reemplaza obtenerProductos / obtenerProductoPorCodigo /
// guardarProducto / eliminarProducto de productos.js.
// ============================================================

import type { Producto } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { useColeccion } from "../storage/useColeccion";

export function useProductos() {
  const { datos, guardar } = useColeccion<Producto>(SANRUCHO_KEYS.productos);

  const obtenerPorCodigo = (codigo: string): Producto | undefined =>
    datos.find((p) => p.codigo === codigo);

  const guardarProducto = (producto: Producto): void => {
    const indice = datos.findIndex((p) => p.codigo === producto.codigo);
    if (indice === -1) {
      guardar([...datos, producto]);
    } else {
      const copia = [...datos];
      copia[indice] = producto;
      guardar(copia);
    }
  };

  const eliminarProducto = (codigo: string): void => {
    guardar(datos.filter((p) => p.codigo !== codigo));
  };

  /** Productos con estado "Activo" (los que se muestran al público). */
  const activos = datos.filter((p) => p.estado === "Activo");

  return { datos, activos, guardarProducto, eliminarProducto, obtenerPorCodigo };
}
