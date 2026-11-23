import client from "./client";
import { setToken } from "./client";

export const authApi = {
  login: (email: string, password: string) =>
    client.post<{ token: string }>("/api/v1/auth/login", { email, password }),
  register: (data: unknown) => client.post("/api/v1/auth/register", data),
};

export const usuariosApi = {
  listar: (params?: { page?: number; size?: number }) => client.get("/api/v1/usuarios", { params }),
  obtener: (id: number) => client.get(`/api/v1/usuarios/${id}`),
  porRun: (run: string) => client.get(`/api/v1/usuarios/run/${run}`),
};

export { setToken };
