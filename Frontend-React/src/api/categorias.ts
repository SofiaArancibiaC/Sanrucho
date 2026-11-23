import client from "./client";
import type { Categoria } from "../types";

export const categoriasApi = {
  listar: (params?: { nombre?: string; page?: number; size?: number }) =>
    client.get("/api/v1/categorias", { params }),
  obtener: (id: number) => client.get<Categoria>(`/api/v1/categorias/${id}`),
  crear: (data: Partial<Categoria>) => client.post<Categoria>("/api/v1/categorias", data),
  actualizar: (id: number, data: Partial<Categoria>) =>
    client.put<Categoria>(`/api/v1/categorias/${id}`, data),
  eliminar: (id: number) => client.delete(`/api/v1/categorias/${id}`),
};
