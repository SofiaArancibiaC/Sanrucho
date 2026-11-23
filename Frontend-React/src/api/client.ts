// ============================================================
// api/client.ts
// Instancia Axios compartida para hablar con el backend.
// Base URL: gateway (9000) por defecto; configurable con
// VITE_API_URL en .env.
// ============================================================

import axios, { AxiosError, type AxiosInstance } from "axios";

/** Clave donde se guarda el JWT en localStorage. */
export const TOKEN_KEY = "sanrucho.token";

export const getToken = (): string | null => localStorage.getItem(TOKEN_KEY);
export const setToken = (token: string | null): void => {
  if (token) localStorage.setItem(TOKEN_KEY, token);
  else localStorage.removeItem(TOKEN_KEY);
};

const baseURL =
  (import.meta as { env?: Record<string, string> }).env?.VITE_API_URL ||
  "http://localhost:9000";

const client: AxiosInstance = axios.create({
  baseURL,
  timeout: 15000,
  headers: { "Content-Type": "application/json" },
});

// Inyecta el JWT en cada request si existe.
client.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Manejo centralizado de errores: 401 -> limpia token y redirige a /login.
client.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      setToken(null);
      if (typeof window !== "undefined" && !window.location.pathname.startsWith("/login")) {
        window.location.assign("/login");
      }
    }
    return Promise.reject(error);
  }
);

export default client;
