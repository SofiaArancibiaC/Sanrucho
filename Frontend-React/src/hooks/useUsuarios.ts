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
      // El backend devuelve un array plano (no { content: [...] }).
      const lista = Array.isArray(res.data)
        ? res.data
        : (res.data?.content ?? []);
      setDatos(lista.map(adaptarUsuario));
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
    const rol = usuario.rolId === 1 ? "DUENO" : usuario.rolId === 2 ? "ADMIN" : "CLIENTE";
    // El backend espera email/rol y una contraseña vacía significa "no cambiarla".
    const payload = {
      run: usuario.run,
      nombre: usuario.nombre,
      apellidos: usuario.apellidos,
      email: usuario.correo,
      password: usuario.password ?? "",
      fechaNacimiento: usuario.fechaNacimiento || null,
      regionId: usuario.regionId,
      comunaId: usuario.comunaId,
      direccion: usuario.direccion,
      telefono: usuario.telefono ?? "",
      estado: usuario.estado,
      rol,
    };

    if (usuario.idUsuario) {
      await usuariosApi.actualizar(usuario.idUsuario, payload);
    } else {
      await usuariosApi.crear(payload);
    }
    await recargar();
  };

  const eliminarUsuario = async (run: string): Promise<void> => {
    const u = datos.find((x) => x.run === run);
    if (u?.idUsuario) await usuariosApi.eliminar(u.idUsuario);
    await recargar();
  };

  const obtenerNombreRol = (rolId: number): string =>
    roles.datos.find((r) => r.id === rolId)?.nombre ?? "";

  return { datos, loading, recargar, guardarUsuario, eliminarUsuario, obtenerPorRun, obtenerNombreRol };
}
