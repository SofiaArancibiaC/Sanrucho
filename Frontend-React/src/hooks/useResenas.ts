// ============================================================
// hooks/useResenas.ts
// Reemplaza a resenas.js: reseñas de un producto y su creación.
// ============================================================

import type { Resena } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { useColeccion } from "../storage/useColeccion";
import { generarSiguienteId } from "../data/seed";

export function useResenas(codigoProducto?: string) {
  const { datos, guardar } = useColeccion<Resena>(SANRUCHO_KEYS.resenas);

  const delProducto = datos
    .filter((r) => r.productoCodigo === codigoProducto)
    .sort((a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime());

  const agregar = (resena: Omit<Resena, "id">): void => {
    const nueva: Resena = { id: generarSiguienteId(datos), ...resena };
    guardar([...datos, nueva]);
  };

  return { delProducto, agregar };
}
