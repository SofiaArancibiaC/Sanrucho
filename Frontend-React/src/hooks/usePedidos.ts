// ============================================================
// hooks/usePedidos.ts
// Historial de pedidos/boletas guardado en localStorage.
// Lo alimenta Checkout y lo consume el panel de administración.
// ============================================================

import { useMemo } from "react";
import type { Pedido } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { useColeccion } from "../storage/useColeccion";

export function usePedidos() {
  const { datos, guardar } = useColeccion<Pedido>(SANRUCHO_KEYS.pedidos);

  const pedidos = useMemo(
    () =>
      [...datos].sort((a, b) =>
        b.fechaCreacion.localeCompare(a.fechaCreacion)
      ),
    [datos]
  );

  const aprobados = useMemo(
    () => pedidos.filter((p) => p.estadoPago === "aprobado"),
    [pedidos]
  );

  const totalVendido = useMemo(
    () => aprobados.reduce((total, p) => total + p.total, 0),
    [aprobados]
  );

  /** Busca por id interno o por el código de boleta visible al cliente. */
  const obtenerPorId = (id: string): Pedido | undefined =>
    pedidos.find((p) => p.id === id) ?? pedidos.find((p) => p.codigoBoleta === id);

  return { pedidos, aprobados, totalVendido, obtenerPorId, guardar };
}
