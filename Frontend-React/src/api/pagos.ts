import client from "./client";

export const pagosApi = {
  listar: (params?: { idPedido?: number; estado?: string; page?: number; size?: number }) =>
    client.get("/api/v1/pagos", { params }),
  obtener: (id: number) => client.get(`/api/v1/pagos/${id}`),
  porPedido: (idPedido: number) => client.get(`/api/v1/pagos/pedido/${idPedido}`),
  crear: (data: { idPedido: number; pasarela: string }) => client.post("/api/v1/pagos", data),
  cambiarEstado: (id: number, estado: string) =>
    client.patch(`/api/v1/pagos/${id}/estado`, { estado }),
  eliminar: (id: number) => client.delete(`/api/v1/pagos/${id}`),
};
