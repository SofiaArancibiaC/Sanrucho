// ============================================================
// components/layout/AdminSidebar.tsx
// Reemplaza _admin-sidebar.html. El enlace "Usuarios" solo se
// muestra al Dueño (rolId === 1); el Admin (rolId === 2) no lo ve.
// ============================================================

import { Link, NavLink } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const claseEnlace = ({ isActive }: { isActive: boolean }) =>
  "list-group-item list-group-item-action" + (isActive ? " active" : "");

export default function AdminSidebar() {
  const { sesion } = useAuth();
  const puedeVerUsuarios = sesion?.rolId === 1;

  return (
    <div className="list-group shadow-sm">
      <NavLink to="/admin" end className={claseEnlace}>
        <i className="bi bi-speedometer2 me-2"></i> Escritorio
      </NavLink>
      <NavLink to="/admin/productos" className={claseEnlace}>
        <i className="bi bi-box-seam me-2"></i> Productos
      </NavLink>
      <NavLink to="/admin/reportes" className={claseEnlace}>
        <i className="bi bi-bar-chart me-2"></i> Reportes
      </NavLink>
      <NavLink to="/admin/categorias" className={claseEnlace}>
        <i className="bi bi-tags me-2"></i> Categorías
      </NavLink>
      <NavLink to="/admin/perfil" className={claseEnlace}>
        <i className="bi bi-person me-2"></i> Mi perfil
      </NavLink>
      {puedeVerUsuarios && (
        <NavLink to="/admin/usuarios" className={claseEnlace}>
          <i className="bi bi-people me-2"></i> Usuarios
        </NavLink>
      )}
      <Link
        to="/"
        className="list-group-item list-group-item-action text-primary"
      >
        <i className="bi bi-box-arrow-left me-2"></i> Volver al sitio
      </Link>
    </div>
  );
}
