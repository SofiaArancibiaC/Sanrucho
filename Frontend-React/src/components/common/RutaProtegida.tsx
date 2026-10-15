// ============================================================
// components/common/RutaProtegida.tsx
// Envuelve las rutas de /admin/*. Si no hay sesión redirige a
// /login; si el rol no está permitido redirige al inicio.
// ============================================================

import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

interface Props {
  rolesPermitidos: number[];
}

export default function RutaProtegida({ rolesPermitidos }: Props) {
  const { sesion } = useAuth();
  const ubicacion = useLocation();

  if (!sesion) {
    return <Navigate to="/login" replace state={{ desde: ubicacion.pathname }} />;
  }

  if (!rolesPermitidos.includes(sesion.rolId)) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
