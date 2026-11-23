// ============================================================
// hooks/usePedidos.ts (versión Axios)
// ============================================================

import { useCallback, useEffect, useMemo, useState } from "react";
import type { Pedido } from "../types";
import { pedidosApi } from "../api/pedidos";

export function usePedidos() {
  const [datos, setDatos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(true);

  const recargar = useCallback(async () => {
    setLoading(true);
    try {
      const res = await pedidosApi.listar({ size: 1000 });
      setDatos(res.data?.content ?? res.data ?? []);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    recargar();
  }, [recargar]);

  const pedidos = useMemo(
    () => [...datos].sort((a, b) => String(b.fechaCreacion ?? "").localeCompare(String(a.fechaCreacion ?? ""))),
    [datos]
  );

  const obtenerPorId = (id: string): Pedido | undefined =>
    pedidos.find((p) => String(p.id) === id) ?? pedidos.find((p) => (p as { codigoBoleta?: string }).codigoBoleta === id);

  const aprobados = useMemo(
    () => pedidos.filter((p) => (p as { estadoPago?: string }).estadoPago === "aprobado"),
    [pedidos]
  );
  const totalVendido = useMemo(
    () => aprobados.reduce((t, p) => t + (p.total ?? 0), 0),
    [aprobados]
  );

  return { pedidos, aprobados, totalVendido, loading, recargar, obtenerPorId, guardar: recargar };
}
