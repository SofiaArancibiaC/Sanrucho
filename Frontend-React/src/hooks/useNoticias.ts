// ============================================================
// hooks/useNoticias.ts
// Reemplaza a blog.js: entrega las noticias del blog guardadas
// en localStorage (colección sanrucho_blog).
// ============================================================

import type { Noticia } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { useColeccion } from "../storage/useColeccion";

export function useNoticias() {
  const { datos, guardar } = useColeccion<Noticia>(SANRUCHO_KEYS.blog);

  const obtenerPorSlug = (slug: string): Noticia | undefined =>
    datos.find((n) => n.slug === slug);

  return { noticias: datos, guardar, obtenerPorSlug };
}
