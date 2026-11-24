// Stub de axios usado SOLO por Karma/Jasmine. En la app real se usa axios.
// Evita que el bundler de karma-typescript intente cargar el build ESM de axios.
export type AxiosError = Error;
export interface AxiosRequestConfig {
  headers?: Record<string, string>;
  params?: Record<string, unknown>;
}
export interface AxiosInstance {
  get: (url: string, config?: AxiosRequestConfig) => Promise<{ data: unknown }>;
  post: (url: string, data?: unknown, config?: AxiosRequestConfig) => Promise<{ data: unknown }>;
  put: (url: string, data?: unknown, config?: AxiosRequestConfig) => Promise<{ data: unknown }>;
  patch: (url: string, data?: unknown, config?: AxiosRequestConfig) => Promise<{ data: unknown }>;
  delete: (url: string, config?: AxiosRequestConfig) => Promise<{ data: unknown }>;
  interceptors: {
    request: { use: (fn: (config: AxiosRequestConfig) => AxiosRequestConfig) => void };
    response: { use: (ok: (r: unknown) => unknown, err: (e: AxiosError) => unknown) => void };
  };
}

const instancia: AxiosInstance = {
  get: () => Promise.resolve({ data: {} }),
  post: () => Promise.resolve({ data: {} }),
  put: () => Promise.resolve({ data: {} }),
  patch: () => Promise.resolve({ data: {} }),
  delete: () => Promise.resolve({ data: {} }),
  interceptors: {
    request: { use: () => undefined },
    response: { use: () => undefined },
  },
};

export default {
  create: () => instancia,
  ...instancia,
};
