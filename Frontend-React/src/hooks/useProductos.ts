// ============================================================
// hooks/useProductos.ts  (versión Axios + adaptador del backend)
// ============================================================

import { useCallback, useEffect, useState } from "react";
import type { Producto } from "../types";
import { productosApi } from "../api/productos";

/** Convierte el ProductoResponse del backend al Producto que usa la UI. */
function adaptarProducto(p: Producto): Producto {
  return {
    ...p,
    // El backend usa idCategoria/imagenUrl; la UI usa categoriaId/imagen.
    categoriaId: (p as Producto & { idCategoria?: number }).idCategoria ?? p.categoriaId,
    imagen: p.imagen ?? (p as Producto & { imagenUrl?: string }).imagenUrl ?? "",
    precio: Number(p.precio ?? 0),
    stock: Number(p.stock ?? 0),
    estado: p.estado ?? "Activo",
  };
}

export function useProductos() {
  const [datos, setDatos] = useState<Producto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const recargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await productosApi.listar({ size: 1000 });
      const raw = res.data?.content ?? [];
      setDatos(raw.map(adaptarProducto));
    } catch (e) {
      setDatos([]);
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
    // El backend espera idCategoria / imagenUrl y stockCritico obligatorio.
    const payload = {
      codigo: producto.codigo,
      idCategoria: producto.categoriaId,
      nombre: producto.nombre,
      descripcion: producto.descripcion ?? "",
      precio: Number(producto.precio),
      stock: Number(producto.stock),
      stockCritico: Number(producto.stockCritico ?? 0),
      imagenUrl: producto.imagen ?? "",
      estado: producto.estado,
    };

    if (producto.id) {
      await productosApi.actualizar(Number(producto.id), payload);
    } else {
      await productosApi.crear(payload);
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
