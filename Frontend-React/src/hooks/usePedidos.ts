// ============================================================
// hooks/usePedidos.ts (versión Axios + adaptadores del backend)
// ============================================================

import { useCallback, useEffect, useMemo, useState } from "react";
import type { Pedido } from "../types";
import { pedidosApi } from "../api/pedidos";

export function usePedidos() {
  const [datos, setDatos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const recargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setDatos(await pedidosApi.listarAdaptados({ size: 1000 }));
    } catch (e) {
      setDatos([]);
      setError((e as Error).message || "No se pudieron cargar los pedidos.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    recargar();
  }, [recargar]);

  const pedidos = useMemo(
    () => [...datos].sort((a, b) =>
      String(b.fechaCreacion ?? "").localeCompare(String(a.fechaCreacion ?? ""))
    ),
    [datos]
  );

  const obtenerPorId = (id: string): Pedido | undefined =>
    pedidos.find((p) => String(p.id) === id) ??
    pedidos.find((p) => p.codigoBoleta === id);

  const aprobados = useMemo(
    () => pedidos.filter((p) => p.estadoPago === "aprobado"),
    [pedidos]
  );
  const totalVendido = useMemo(
    () => aprobados.reduce((t, p) => t + (p.total ?? 0), 0),
    [aprobados]
  );

  return { pedidos, aprobados, totalVendido, loading, error, recargar, obtenerPorId, guardar: recargar };
}
