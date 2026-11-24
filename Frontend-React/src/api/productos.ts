// ============================================================
// api/productos.ts
// Ejemplo de servicio que usa el client Axios.
// ============================================================

import client from "./client";
import type { Producto } from "../types";

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export const productosApi = {
  listar: (params?: { nombre?: string; idCategoria?: number; page?: number; size?: number }) =>
    client.get<PageResponse<Producto>>("/api/v1/productos", { params }),
  obtener: (id: number) => client.get<Producto>(`/api/v1/productos/${id}`),
  porCodigo: (codigo: string) => client.get<Producto>(`/api/v1/productos/codigo/${codigo}`),
  crear: (data: object) => client.post<Producto>("/api/v1/productos", data),
  actualizar: (id: number, data: object) =>
    client.put<Producto>(`/api/v1/productos/${id}`, data),
  ajustarStock: (id: number, cantidad: number) =>
    client.patch(`/api/v1/productos/${id}/stock`, { cantidad }),
  eliminar: (id: number) => client.delete(`/api/v1/productos/${id}`),
};
