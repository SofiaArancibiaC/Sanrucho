import client from "./client";

export const pedidosApi = {
  listar: (params?: { idUsuario?: number; estado?: string; page?: number; size?: number }) =>
    client.get("/api/v1/pedidos", { params }),
  obtener: (id: number) => client.get(`/api/v1/pedidos/${id}`),
  crear: (data: { idUsuario: number; items: { idProducto: number; cantidad: number }[] }) =>
    client.post("/api/v1/pedidos", data),
  cambiarEstado: (id: number, estado: string) =>
    client.patch(`/api/v1/pedidos/${id}/estado`, { estado }),
  eliminar: (id: number) => client.delete(`/api/v1/pedidos/${id}`),
};
