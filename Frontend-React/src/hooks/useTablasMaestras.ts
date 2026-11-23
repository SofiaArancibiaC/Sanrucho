// ============================================================
// hooks/useTablasMaestras.ts
// Categorías, regiones, comunas y roles (las "tablas maestras").
// Las categorías vienen del backend (ms-catalogo); las demás se
// mantienen en localStorage por compatibilidad con el proyecto.
// ============================================================

import { useEffect, useState } from "react";
import type { Categoria, Comuna, Region, Rol } from "../types";
import { categoriasApi } from "../api/categorias";
import { SANRUCHO_KEYS } from "../storage/claves";
import { useColeccion } from "../storage/useColeccion";

export function useTablasMaestras() {
  const { datos: categoriasLocales } = useColeccion<Categoria>(SANRUCHO_KEYS.categorias);
  const { datos: regiones } = useColeccion<Region>(SANRUCHO_KEYS.regiones);
  const { datos: comunas } = useColeccion<Comuna>(SANRUCHO_KEYS.comunas);
  const { datos: roles } = useColeccion<Rol>(SANRUCHO_KEYS.roles);

  const [categoriasBackend, setCategoriasBackend] = useState<Categoria[]>([]);

  useEffect(() => {
    let activo = true;
    categoriasApi.listar({ size: 1000 })
      .then((res) => {
        const lista = (res.data?.content ?? []).map((c) => ({
          id: c.id,
          nombre: c.nombre,
        }));
        if (activo) setCategoriasBackend(lista);
      })
      .catch(() => {
        // Si el backend no está disponible se usan las categorías locales.
      });
    return () => { activo = false; };
  }, []);

  const categorias = categoriasBackend.length > 0 ? categoriasBackend : categoriasLocales;

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
