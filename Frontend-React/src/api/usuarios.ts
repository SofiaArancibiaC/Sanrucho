// ============================================================
// api/usuarios.ts
// Llamadas a ms-usuarios. Los tipos reflejan los DTOs reales del
// backend (UsuarioResponse / AuthResponse), que usan nombres en
// inglés (email, idUsuario, ...).
// ============================================================

import client, { setToken, type PageResponse } from "./client";

export interface UsuarioBackend {
  idUsuario: number;
  run?: string;
  nombre?: string;
  apellidos?: string;
  email?: string;
  fechaNacimiento?: string;
  regionId?: number;
  comunaId?: number;
  direccion?: string;
  telefono?: string;
  estado?: string;
  rol?: string;
}

export interface AuthResponseBackend {
  token: string;
  usuario: UsuarioBackend;
}

export const authApi = {
  login: (email: string, password: string) =>
    client.post<AuthResponseBackend>("/api/v1/auth/login", { email, password }),
  register: (data: unknown) =>
    client.post<AuthResponseBackend>("/api/v1/auth/register", data),
};

export const usuariosApi = {
  listar: (params?: { page?: number; size?: number }) =>
    client.get<PageResponse<UsuarioBackend> | UsuarioBackend[]>("/api/v1/usuarios", { params }),
  obtener: (id: number) =>
    client.get<UsuarioBackend>(`/api/v1/usuarios/${id}`),
  porRun: (run: string) =>
    client.get<UsuarioBackend>(`/api/v1/usuarios/run/${run}`),
  crear: (data: object) => client.post<UsuarioBackend>("/api/v1/usuarios", data),
  actualizar: (id: number, data: object) =>
    client.put<UsuarioBackend>(`/api/v1/usuarios/${id}`, data),
  eliminar: (id: number) => client.delete(`/api/v1/usuarios/${id}`),
};

export { setToken };
