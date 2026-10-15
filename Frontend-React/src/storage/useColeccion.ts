// ============================================================
// storage/useColeccion.ts
// Reemplazo genérico de obtenerColeccion() / guardarColeccion().
//
// Lee y escribe una colección de localStorage y mantiene la
// pantalla sincronizada: si dos componentes usan la misma clave,
// ambos se actualizan cuando alguno guarda.
// ============================================================

import { useCallback, useEffect, useState } from "react";

type Listener = () => void;

// Pequeño sistema de suscripción: clave de localStorage -> oyentes.
const oyentes = new Map<string, Set<Listener>>();

function notificar(clave: string): void {
  oyentes.get(clave)?.forEach((listener) => listener());
}

/** Lee una colección sin React (usar dentro de eventos o contextos). */
export function leerColeccion<T>(clave: string): T[] {
  try {
    return JSON.parse(localStorage.getItem(clave) ?? "[]") as T[];
  } catch {
    return [];
  }
}

/** Escribe una colección sin React y avisa a los componentes suscritos. */
export function escribirColeccion<T>(clave: string, datos: T[]): void {
  localStorage.setItem(clave, JSON.stringify(datos));
  notificar(clave);
}

/**
 * Hook: devuelve `datos` (la colección actual) y `guardar` (para
 * reemplazarla entera). Cada cambio se refleja en el estado de React.
 */
export function useColeccion<T>(clave: string) {
  const [datos, setDatos] = useState<T[]>(() => leerColeccion<T>(clave));

  useEffect(() => {
    const actualizar = () => setDatos(leerColeccion<T>(clave));
    actualizar();

    if (!oyentes.has(clave)) oyentes.set(clave, new Set());
    oyentes.get(clave)!.add(actualizar);

    return () => {
      oyentes.get(clave)!.delete(actualizar);
    };
  }, [clave]);

  const guardar = useCallback(
    (nuevos: T[]) => escribirColeccion<T>(clave, nuevos),
    [clave]
  );

  return { datos, guardar };
}
