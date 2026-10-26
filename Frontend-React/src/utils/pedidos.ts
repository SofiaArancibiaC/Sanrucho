// ============================================================
// utils/pedidos.ts
// Funciones puras para listar y filtrar las boletas del panel
// de administración. Se mantienen fuera de los componentes para
// poder probarlas de forma aislada.
// ============================================================

import type { Pedido } from "../types";

export type FiltroEstadoPago = "todos" | "aprobado" | "rechazado";

export interface FiltrosPedidos {
  /** Busca en código de boleta, número de pedido, cliente, correo y teléfono. */
  texto: string;
  estado: FiltroEstadoPago;
  /** Fecha inicial en formato YYYY-MM-DD (inclusive). */
  desde: string;
  /** Fecha final en formato YYYY-MM-DD (inclusive). */
  hasta: string;
}

export const FILTROS_PEDIDOS_VACIOS: FiltrosPedidos = {
  texto: "",
  estado: "aprobado",
  desde: "",
  hasta: "",
};

/** Texto legible del método de pago de un pedido. */
export function etiquetaMetodoPago(
  pedido: Pick<Pedido, "metodoPago" | "tipoTarjeta">
): string {
  if (pedido.metodoPago === "webpay") return "Webpay";
  if (pedido.tipoTarjeta === "debito") return "Tarjeta de débito";
  if (pedido.tipoTarjeta === "credito") return "Tarjeta de crédito";
  return "Tarjeta de crédito/débito";
}

/** Muestra una fecha ISO como "dd-mm-aaaa hh:mm". */
export function formatearFechaHora(fechaISO: string): string {
  if (!fechaISO) return "—";
  const fecha = new Date(fechaISO);
  if (Number.isNaN(fecha.getTime())) return fechaISO;
  return fecha.toLocaleString("es-CL", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

/** Cantidad total de unidades compradas en un pedido. */
export function unidadesPedido(pedido: Pedido): number {
  return pedido.items.reduce((total, item) => total + item.cantidad, 0);
}

/**
 * Pedidos asociados al correo de un usuario. El checkout guarda el
 * correo del cliente, así que es la clave que une usuarios y boletas.
 */
export function pedidosDeUsuario(pedidos: Pedido[], correo: string): Pedido[] {
  const buscado = correo.trim().toLowerCase();
  if (!buscado) return [];
  return pedidos.filter(
    (pedido) => pedido.cliente.correo.trim().toLowerCase() === buscado
  );
}

/**
 * Aplica los filtros de la vista de boletas y devuelve el resultado
 * ordenado desde la compra más reciente a la más antigua.
 */
export function filtrarPedidos(
  pedidos: Pedido[],
  filtros: FiltrosPedidos
): Pedido[] {
  const texto = filtros.texto.trim().toLowerCase();

  return pedidos
    .filter((pedido) => {
      if (filtros.estado !== "todos" && pedido.estadoPago !== filtros.estado) {
        return false;
      }

      // Las fechas se guardan como ISO (YYYY-MM-DD...), por lo que los
      // primeros 10 caracteres permiten comparar solo el día.
      const fecha = pedido.fechaCreacion.slice(0, 10);
      if (filtros.desde && fecha < filtros.desde) return false;
      if (filtros.hasta && fecha > filtros.hasta) return false;

      if (!texto) return true;

      const campos = [
        pedido.codigoBoleta,
        pedido.id,
        pedido.cliente.nombre,
        pedido.cliente.apellidos,
        `${pedido.cliente.nombre} ${pedido.cliente.apellidos}`,
        pedido.cliente.correo,
        pedido.cliente.telefono,
      ];
      return campos.some((campo) =>
        (campo ?? "").toLowerCase().includes(texto)
      );
    })
    .sort((a, b) => b.fechaCreacion.localeCompare(a.fechaCreacion));
}
