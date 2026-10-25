// ============================================================
// hooks/useTablasMaestras.ts
// Categorías, regiones, comunas y roles (las "tablas maestras").
// Reemplaza llenarSelectCategorias/Regiones/Comunas/Roles.
// ============================================================

import type { Categoria, Comuna, Region, Rol } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { useColeccion } from "../storage/useColeccion";

export function useTablasMaestras() {
  const { datos: categorias } = useColeccion<Categoria>(SANRUCHO_KEYS.categorias);
  const { datos: regiones } = useColeccion<Region>(SANRUCHO_KEYS.regiones);
  const { datos: comunas } = useColeccion<Comuna>(SANRUCHO_KEYS.comunas);
  const { datos: roles } = useColeccion<Rol>(SANRUCHO_KEYS.roles);

  const obtenerNombreCategoria = (categoriaId: number): string => {
    const encontrada = categorias.find(
      (c) => Number(c.id) === Number(categoriaId)
    );
    return encontrada ? encontrada.nombre : "General";
  };

  const comunasDeRegion = (regionId: number): Comuna[] =>
    comunas.filter((c) => c.regionId === Number(regionId));

  return {
    categorias,
    regiones,
    comunas,
    roles,
    obtenerNombreCategoria,
    comunasDeRegion,
  };
}
