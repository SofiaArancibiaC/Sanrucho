// ============================================================
// hooks/useProductos.ts  (versión Axios)
// ============================================================

import { useCallback, useEffect, useState } from "react";
import type { Producto } from "../types";
import { productosApi } from "../api/productos";

export function useProductos() {
  const [datos, setDatos] = useState<Producto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const recargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await productosApi.listar({ size: 1000 });
      const raw = res.data?.content ?? res.data ?? [];
      setDatos(raw.map((p: any) => ({ ...p, imagen: p.imagen ?? p.imagenUrl })));
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    recargar();
  }, [recargar]);

  const obtenerPorCodigo = (codigo: string): Producto | undefined =>
    datos.find((p) => p.codigo === codigo);

  const guardarProducto = async (producto: Producto): Promise<void> => {
    if (producto.id) {
      await productosApi.actualizar(Number(producto.id), producto);
    } else {
      await productosApi.crear(producto);
    }
    await recargar();
  };

  const eliminarProducto = async (codigo: string): Promise<void> => {
    const p = datos.find((x) => x.codigo === codigo);
    if (p?.id) await productosApi.eliminar(Number(p.id));
    await recargar();
  };

  const activos = datos.filter((p) => p.estado === "Activo");

  return { datos, activos, loading, error, recargar, guardarProducto, eliminarProducto, obtenerPorCodigo };
}
