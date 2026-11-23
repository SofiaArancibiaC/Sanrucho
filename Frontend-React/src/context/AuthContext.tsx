// ============================================================
// context/AuthContext.tsx  (versión JWT)
// ============================================================

import { createContext, useCallback, useContext, useState } from "react";
import type { ReactNode } from "react";
import type { Sesion } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { authApi, setToken } from "../api/usuarios";
import { getToken } from "../api/client";

interface ResultadoAuth {
  ok: boolean;
  mensaje: string;
  usuario?: Sesion;
}

interface DatosRegistro {
  nombre: string;
  apellidos: string;
  correo: string;
  password: string;
}

interface AuthContextValue {
  sesion: Sesion | null;
  login: (correo: string, password: string) => Promise<ResultadoAuth>;
  registrar: (datos: DatosRegistro) => Promise<ResultadoAuth>;
  logout: () => void;
  tieneRol: (...roles: string[]) => boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

function decodificarToken(token: string): Sesion | null {
  try {
    const payload = token.split(".")[1];
    const json = JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")));
    return {
      run: json.sub ?? "",
      nombre: json.nombre ?? json.sub ?? "",
      apellidos: "",
      correo: json.sub ?? "",
      password: "",
      fechaNacimiento: "",
      rolId: json.rol === "Admin" || json.rol === "Dueño" ? 2 : 3,
      regionId: 1,
      comunaId: 1,
      direccion: "",
      estado: "Activo",
      // guardamos el rol textual para las restricciones
      rol: json.rol,
    } as Sesion & { rol?: string };
  } catch {
    return null;
  }
}

function leerSesionInicial(): Sesion | null {
  const token = getToken();
  if (token) {
    const s = decodificarToken(token);
    if (s) return s;
  }
  try {
    return JSON.parse(localStorage.getItem(SANRUCHO_KEYS.sesion) ?? "null") as Sesion | null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [sesion, setSesion] = useState<Sesion | null>(leerSesionInicial);

  const login = useCallback(async (correo: string, password: string): Promise<ResultadoAuth> => {
    try {
      const res = await authApi.login(correo, password);
      const token = res.data?.token;
      if (!token) return { ok: false, mensaje: "Respuesta sin token." };
      setToken(token);
      const s = decodificarToken(token);
      if (!s) return { ok: false, mensaje: "Token inválido." };
      localStorage.setItem(SANRUCHO_KEYS.sesion, JSON.stringify(s));
      setSesion(s);
      return { ok: true, mensaje: `¡Hola de nuevo, ${s.nombre}!`, usuario: s };
    } catch (e) {
      return { ok: false, mensaje: (e as Error).message || "Credenciales incorrectas." };
    }
  }, []);

  const registrar = useCallback(async (datos: DatosRegistro): Promise<ResultadoAuth> => {
    try {
      await authApi.register(datos);
      return { ok: true, mensaje: "Registro exitoso." };
    } catch (e) {
      return { ok: false, mensaje: (e as Error).message || "Error al registrar." };
    }
  }, []);

  const logout = useCallback(() => {
    setToken(null);
    localStorage.removeItem(SANRUCHO_KEYS.sesion);
    setSesion(null);
  }, []);

  const tieneRol = useCallback(
    (...roles: string[]) => {
      const rol = (sesion as (Sesion & { rol?: string }) | null)?.rol;
      return !!rol && roles.includes(rol);
    },
    [sesion]
  );

  return (
    <AuthContext.Provider value={{ sesion, login, registrar, logout, tieneRol }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth debe usarse dentro de <AuthProvider>");
  return ctx;
}

export function esAdministrativo(sesion: Sesion | null): boolean {
  const rol = (sesion as (Sesion & { rol?: string }) | null)?.rol;
  return rol === "Admin" || rol === "Dueño";
}
