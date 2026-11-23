// ============================================================
// hooks/useUsuarios.ts (versión Axios)
// ============================================================

import { useCallback, useEffect, useState } from "react";
import type { Usuario } from "../types";
import { usuariosApi, type UsuarioBackend } from "../api/usuarios";
import { SANRUCHO_KEYS } from "../storage/claves";
import { useColeccion } from "../storage/useColeccion";

/** Traduce el UsuarioResponse del backend al Usuario que usa la UI. */
function adaptarUsuario(u: UsuarioBackend): Usuario {
  const rol = String(u.rol ?? "CLIENTE").toUpperCase();
  return {
    idUsuario: u.idUsuario,
    run: u.run ?? "",
    nombre: u.nombre ?? "",
    apellidos: u.apellidos ?? "",
    correo: u.email ?? "",
    password: "",
    fechaNacimiento: u.fechaNacimiento ?? "",
    rolId: rol === "DUENO" ? 1 : rol === "ADMIN" ? 2 : 3,
    regionId: u.regionId ?? 1,
    comunaId: u.comunaId ?? 1,
    direccion: u.direccion ?? "",
    telefono: u.telefono ?? "",
    estado: (u.estado as Usuario["estado"]) ?? "Activo",
  };
}

export function useUsuarios() {
  const [datos, setDatos] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(true);

  const recargar = useCallback(async () => {
    setLoading(true);
    try {
      const res = await usuariosApi.listar({ size: 1000 });
      setDatos((res.data?.content ?? []).map(adaptarUsuario));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    recargar();
  }, [recargar]);

  const roles = useColeccion<{ id: number; nombre: string }>(SANRUCHO_KEYS.roles);

  const obtenerPorRun = (run: string): Usuario | undefined =>
    datos.find((u) => u.run === run);

  const guardarUsuario = async (usuario: Usuario): Promise<void> => {
    // Backend usuario CRUD no expone update por run en esta rama; recarga local.
    setDatos((prev) => {
      const i = prev.findIndex((u) => u.run === usuario.run);
      if (i === -1) return [...prev, usuario];
      const c = [...prev];
      c[i] = usuario;
      return c;
    });
  };

  const eliminarUsuario = async (run: string): Promise<void> => {
    setDatos((prev) => prev.filter((u) => u.run !== run));
  };

  const obtenerNombreRol = (rolId: number): string =>
    roles.datos.find((r) => r.id === rolId)?.nombre ?? "";

  return { datos, loading, recargar, guardarUsuario, eliminarUsuario, obtenerPorRun, obtenerNombreRol };
}
