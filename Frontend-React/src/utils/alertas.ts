// ============================================================
// utils/alertas.ts
// Envoltorios de SweetAlert2 para no repetir opciones. Reemplaza
// al Swal.fire(...) suelto que usaban los .js originales.
// ============================================================

import Swal from "sweetalert2";

const CONFIRM_COLOR = "#ff69b4";

export function alertaExito(titulo: string, texto?: string): Promise<unknown> {
  return Swal.fire({
    icon: "success",
    title: titulo,
    text: texto,
    confirmButtonColor: CONFIRM_COLOR,
  });
}

export function alertaError(titulo: string, texto?: string): Promise<unknown> {
  return Swal.fire({
    icon: "error",
    title: titulo,
    text: texto,
    confirmButtonColor: CONFIRM_COLOR,
  });
}

export function alertaAviso(titulo: string, texto?: string): Promise<unknown> {
  return Swal.fire({
    icon: "warning",
    title: titulo,
    text: texto,
    confirmButtonColor: CONFIRM_COLOR,
  });
}

/** Pide confirmación. Devuelve true solo si el usuario confirma. */
export async function confirmar(
  titulo: string,
  texto?: string,
  confirmButtonText = "Eliminar"
): Promise<boolean> {
  const resultado = await Swal.fire({
    title: titulo,
    text: texto,
    icon: "warning",
    showCancelButton: true,
    confirmButtonText,
    cancelButtonText: "Cancelar",
    confirmButtonColor: CONFIRM_COLOR,
  });
  return resultado.isConfirmed;
}

/** Notificación pequeña arriba a la derecha (para acciones rápidas). */
export function toast(titulo: string, icono: "success" | "error" | "info" = "success"): void {
  Swal.fire({
    toast: true,
    position: "bottom-end",
    icon: icono,
    title: titulo,
    showConfirmButton: false,
    timer: 2000,
  });
}

/**
 * Toast del borde inferior con la maqueta original (#toast de base.js).
 * Se mantiene por si se quiere usar el estilo propio de Sanrucho.
 */
export function mostrarToast(mensaje: string, tipo?: "success" | "error" | "info" | "warning"): void {
  const contenedor = document.getElementById("toast");
  if (!contenedor) return;
  contenedor.textContent = mensaje;
  contenedor.className = "toast";
  if (tipo) contenedor.classList.add("toast-" + tipo);
  contenedor.classList.add("show");
  const cualquierToast = contenedor as HTMLElement & { _timer?: number };
  window.clearTimeout(cualquierToast._timer);
  cualquierToast._timer = window.setTimeout(() => {
    contenedor.classList.remove("show");
  }, 3200);
}
