// ============================================================
// context/AuthContext.tsx
// Reemplaza la sesión de storage.js (obtenerSesion/guardarSesion/
// cerrarSesion) y ofrece login()/registrar()/logout() reactivos.
// ============================================================

import { createContext, useCallback, useContext, useState } from "react";
import type { ReactNode } from "react";
import type { Sesion, Usuario } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { leerColeccion } from "../storage/useColeccion";

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
  login: (correo: string, password: string) => ResultadoAuth;
  registrar: (datos: DatosRegistro) => ResultadoAuth;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

function leerSesionInicial(): Sesion | null {
  try {
    return JSON.parse(localStorage.getItem(SANRUCHO_KEYS.sesion) ?? "null") as Sesion | null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [sesion, setSesion] = useState<Sesion | null>(leerSesionInicial);

  const guardarSesion = (usuario: Sesion) => {
    localStorage.setItem(SANRUCHO_KEYS.sesion, JSON.stringify(usuario));
    setSesion(usuario);
  };

  const login = useCallback((correo: string, password: string): ResultadoAuth => {
    const usuarios = leerColeccion<Usuario>(SANRUCHO_KEYS.usuarios);
    const usuario = usuarios.find(
      (u) =>
        u.correo.toLowerCase() === correo.trim().toLowerCase() &&
        u.password === password
    );

    if (!usuario) {
      return { ok: false, mensaje: "Correo electrónico o contraseña incorrectos." };
    }
    if (usuario.estado !== "Activo") {
      return { ok: false, mensaje: "Tu cuenta está inactiva o suspendida." };
    }

    guardarSesion(usuario);
    return { ok: true, mensaje: `¡Hola de nuevo, ${usuario.nombre}!`, usuario };
  }, []);

  const registrar = useCallback((datos: DatosRegistro): ResultadoAuth => {
    const usuarios = leerColeccion<Usuario>(SANRUCHO_KEYS.usuarios);
    const correo = datos.correo.trim().toLowerCase();

    if (usuarios.find((u) => u.correo.toLowerCase() === correo)) {
      return { ok: false, mensaje: "El correo electrónico ya se encuentra registrado." };
    }

    const nuevoUsuario: Usuario = {
      run: Date.now().toString().slice(-9),
      nombre: datos.nombre.trim(),
      apellidos: datos.apellidos.trim(),
      correo,
      password: datos.password,
      fechaNacimiento: "",
      rolId: 3,
      regionId: 1,
      comunaId: 1,
      direccion: "",
      estado: "Activo",
    };

    localStorage.setItem(
      SANRUCHO_KEYS.usuarios,
      JSON.stringify([...usuarios, nuevoUsuario])
    );
    guardarSesion(nuevoUsuario);
    return { ok: true, mensaje: "¡Registro exitoso! Bienvenido a Sanrucho.", usuario: nuevoUsuario };
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(SANRUCHO_KEYS.sesion);
    setSesion(null);
  }, []);

  return (
    <AuthContext.Provider value={{ sesion, login, registrar, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth debe usarse dentro de <AuthProvider>");
  return ctx;
}

/** Roles: 1 = Dueño, 2 = Admin, 3 = Cliente. */
export function esAdministrativo(sesion: Sesion | null): boolean {
  return !!sesion && (sesion.rolId === 1 || sesion.rolId === 2);
}
