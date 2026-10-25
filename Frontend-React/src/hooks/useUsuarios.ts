// ============================================================
// hooks/useUsuarios.ts
// Reemplaza el mantenedor administrativo de usuarios de usuarios.js.
// ============================================================

import type { Usuario } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { useColeccion } from "../storage/useColeccion";

export function useUsuarios() {
  const { datos, guardar } = useColeccion<Usuario>(SANRUCHO_KEYS.usuarios);

  const obtenerPorRun = (run: string): Usuario | undefined =>
    datos.find((u) => u.run === run);

  const guardarUsuario = (usuario: Usuario): void => {
    const indice = datos.findIndex((u) => u.run === usuario.run);
    if (indice === -1) {
      guardar([...datos, usuario]);
    } else {
      const copia = [...datos];
      copia[indice] = usuario;
      guardar(copia);
    }
  };

  const eliminarUsuario = (run: string): void => {
    guardar(datos.filter((u) => u.run !== run));
  };

  const roles = useColeccion<{ id: number; nombre: string }>(SANRUCHO_KEYS.roles);
  const obtenerNombreRol = (rolId: number): string =>
    roles.datos.find((r) => r.id === rolId)?.nombre ?? "";

  return { datos, guardarUsuario, eliminarUsuario, obtenerPorRun, obtenerNombreRol };
}
