// ============================================================
// api/categorias.ts
// Llamadas a ms-catalogo para las categorías de productos.
// ============================================================

import client, { type PageResponse } from "./client";
import type { Categoria } from "../types";

export interface CategoriaBackend {
  id: number;
  nombre: string;
  descripcion?: string;
  cantidadProductos?: number;
}

export const categoriasApi = {
  listar: (params?: { nombre?: string; page?: number; size?: number }) =>
    client.get<PageResponse<CategoriaBackend>>("/api/v1/categorias", { params }),
  obtener: (id: number) => client.get<CategoriaBackend>(`/api/v1/categorias/${id}`),
  crear: (data: Partial<Categoria>) => client.post<CategoriaBackend>("/api/v1/categorias", data),
  actualizar: (id: number, data: Partial<Categoria>) =>
    client.put<CategoriaBackend>(`/api/v1/categorias/${id}`, data),
  eliminar: (id: number) => client.delete(`/api/v1/categorias/${id}`),
};
