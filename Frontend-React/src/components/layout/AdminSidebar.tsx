// ============================================================
// components/layout/AdminSidebar.tsx
// Reemplaza _admin-sidebar.html. El enlace "Usuarios" solo se
// muestra al Dueño (rolId === 1); el Admin (rolId === 2) no lo ve.
// ============================================================

import { NavLink } from "react-router-dom";
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
      {puedeVerUsuarios && (
        <NavLink to="/admin/usuarios" className={claseEnlace}>
          <i className="bi bi-people me-2"></i> Usuarios
        </NavLink>
      )}
    </div>
  );
}
