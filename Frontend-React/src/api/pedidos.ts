// ============================================================
// api/pedidos.ts
// Llama a ms-pedidos y ADAPTA sus respuestas al tipo `Pedido`
// que consumen las vistas (Perfil, AdminBoletas, BoletaDetalle).
//
// El backend devuelve:
//   resumen -> { id, idUsuario, fechaCompra, total, estado }
//   detalle -> { id, idUsuario, fechaCompra, total, estado, detalles[] }
// La UI espera:
//   { id, codigoBoleta, fechaCreacion, cliente, items, subtotal,
//     envio, total, metodoPago, estadoPago }
// ============================================================

import client, { type PageResponse } from "./client";
import { usuariosApi, type UsuarioBackend } from "./usuarios";
import type { DatosDespacho, ItemCarrito, Pedido } from "../types";

export interface DetallePedidoBackend {
  idDetalle?: number;
  idProducto: number;
  nombreProducto?: string | null;
  cantidad: number;
  precioUnitario?: number | string;
  subtotal?: number | string;
}

export interface PedidoResumenBackend {
  id: number;
  idUsuario: number;
  fechaCompra: string;
  total: number | string;
  estado: string;
}

export interface PedidoDetalleBackend extends PedidoResumenBackend {
  detalles?: DetallePedidoBackend[] | null;
}

const numero = (valor: unknown): number => {
  const n = Number(valor);
  return Number.isFinite(n) ? n : 0;
};

/** El backend usa Pendiente/Pagado/...; la UI solo maneja aprobado/rechazado. */
export function estadoPagoDeBackend(estado?: string | null): "aprobado" | "rechazado" {
  const e = (estado ?? "").toLowerCase();
  return e === "cancelado" || e === "rechazado" ? "rechazado" : "aprobado";
}

function clienteDeBackend(usuario?: UsuarioBackend | null, idUsuario?: number): DatosDespacho {
  return {
    nombre: usuario?.nombre || "Cliente",
    apellidos: usuario?.apellidos || (idUsuario ? `#${idUsuario}` : ""),
    correo: usuario?.email || "",
    telefono: usuario?.telefono || "",
    direccion: usuario?.direccion || "",
    // ms-pedidos no guarda datos de despacho; no inventamos información.
    fechaDespacho: "",
    horaDespacho: "",
  };
}

function itemsDeBackend(detalles: DetallePedidoBackend[] = []): ItemCarrito[] {
  return detalles.map((d) => ({
    id: d.idProducto,
    codigo: `PROD-${d.idProducto}`,
    nombre: d.nombreProducto || `Producto ${d.idProducto}`,
    precio: numero(d.precioUnitario ?? d.subtotal),
    imagen: "",
    cantidad: numero(d.cantidad),
  }));
}

/** Convierte la respuesta del backend al Pedido que esperan las vistas. */
export function adaptarPedido(
  resumen: PedidoResumenBackend,
  detalle?: PedidoDetalleBackend | null,
  usuario?: UsuarioBackend | null
): Pedido {
  const id = String(resumen.id);
  const items = itemsDeBackend(detalle?.detalles ?? []);
  const total = numero(resumen.total);
  const subtotalCalculado = items.reduce((t, i) => t + i.precio * i.cantidad, 0);

  return {
    id,
    codigoBoleta: `B-${id.padStart(6, "0")}`,
    fechaCreacion: resumen.fechaCompra,
    cliente: clienteDeBackend(usuario, resumen.idUsuario),
    items,
    subtotal: items.length > 0 ? subtotalCalculado : total,
    envio: 0,
    total,
    metodoPago: "tarjeta",
    estadoPago: estadoPagoDeBackend(detalle?.estado ?? resumen.estado),
  };
}

/**
 * Carga los usuarios para mostrar el nombre del cliente en cada boleta.
 * El gateway solo permite listar usuarios al rol DUEÑO; si falla, se usa
 * un nombre genérico en vez de romper la vista.
 */
async function cargarClientes(): Promise<Map<number, UsuarioBackend>> {
  const mapa = new Map<number, UsuarioBackend>();
  try {
    const res = await usuariosApi.listar({ size: 1000 });
    const lista = Array.isArray(res.data) ? res.data : (res.data?.content ?? []);
    lista.forEach((u: UsuarioBackend) => mapa.set(u.idUsuario, u));
  } catch {
    // Sin permisos para usuarios: se continúa con datos genéricos.
  }
  return mapa;
}

export const pedidosApi = {
  /** GET /api/v1/pedidos (crudo, tal como lo devuelve el backend). */
  listar: (params?: { idUsuario?: number; estado?: string; page?: number; size?: number }) =>
    client.get<PageResponse<PedidoResumenBackend>>("/api/v1/pedidos", { params }),

  /** GET /api/v1/pedidos/{id} (crudo). */
  obtener: (id: number) =>
    client.get<PedidoDetalleBackend>(`/api/v1/pedidos/${id}`),

  crear: (data: { idUsuario: number; items: { idProducto: number; cantidad: number }[] }) =>
    client.post<PedidoDetalleBackend>("/api/v1/pedidos", data),

  cambiarEstado: (id: number, estado: string) =>
    client.patch<PedidoDetalleBackend>(`/api/v1/pedidos/${id}/estado`, { estado }),

  eliminar: (id: number) => client.delete(`/api/v1/pedidos/${id}`),

  /**
   * Lista pedidos ya convertidos a `Pedido`, incluyendo sus items.
   * La vista de boletas necesita los detalles para mostrar productos y unidades.
   */
  listarAdaptados: async (params?: {
    idUsuario?: number;
    estado?: string;
    page?: number;
    size?: number;
  }): Promise<Pedido[]> => {
    const [respuesta, clientes] = await Promise.all([
      pedidosApi.listar(params),
      cargarClientes(),
    ]);

    const resumenes = respuesta.data?.content ?? [];
    return Promise.all(
      resumenes.map(async (resumen) => {
        let detalle: PedidoDetalleBackend | null = null;
        try {
          detalle = (await pedidosApi.obtener(resumen.id)).data;
        } catch {
          // Si el detalle falla, igual mostramos el resumen de la boleta.
        }
        return adaptarPedido(resumen, detalle, clientes.get(resumen.idUsuario));
      })
    );
  },

  /** Obtiene un pedido convertido a `Pedido` por su id. */
  obtenerAdaptado: async (id: number): Promise<Pedido | null> => {
    try {
      const [respuesta, clientes] = await Promise.all([
        pedidosApi.obtener(id),
        cargarClientes(),
      ]);
      const detalle = respuesta.data;
      return adaptarPedido(detalle, detalle, clientes.get(detalle.idUsuario));
    } catch {
      return null;
    }
  },
};
