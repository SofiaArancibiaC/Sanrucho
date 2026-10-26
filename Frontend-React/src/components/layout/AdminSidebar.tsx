// ============================================================
// components/layout/AdminSidebar.tsx
// Reemplaza _admin-sidebar.html. Dueño (rolId 1) y Admin
// (rolId 2) ven los mismos módulos del panel.
// ============================================================

import { Link, NavLink } from "react-router-dom";

const claseEnlace = ({ isActive }: { isActive: boolean }) =>
  "list-group-item list-group-item-action" + (isActive ? " active" : "");

export default function AdminSidebar() {
  return (
    <div className="list-group shadow-sm">
      <NavLink to="/admin" end className={claseEnlace}>
        <i className="bi bi-speedometer2 me-2"></i> Escritorio
      </NavLink>
      <NavLink to="/admin/productos" className={claseEnlace}>
        <i className="bi bi-box-seam me-2"></i> Productos
      </NavLink>
      <NavLink to="/admin/boletas" className={claseEnlace}>
        <i className="bi bi-receipt me-2"></i> Boletas
      </NavLink>
      <NavLink to="/admin/usuarios" className={claseEnlace}>
        <i className="bi bi-people me-2"></i> Usuarios
      </NavLink>
      <Link
        to="/"
        className="list-group-item list-group-item-action text-primary"
      >
        <i className="bi bi-box-arrow-left me-2"></i> Volver al sitio
      </Link>
    </div>
  );
}
