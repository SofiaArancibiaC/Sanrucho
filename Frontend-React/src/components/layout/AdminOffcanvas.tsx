// ============================================================
// components/layout/AdminOffcanvas.tsx
// Reemplaza _admin-offcanvas.html. Menú lateral para móvil; se
// abre con data-bs-toggle (Bootstrap bundle cargado en main.tsx).
// ============================================================

import { Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export default function AdminOffcanvas() {
  const { sesion, logout } = useAuth();
  const puedeVerUsuarios = sesion?.rolId === 1;

  return (
    <>
      <button
        className="btn btn-accent d-lg-none mb-3"
        type="button"
        data-bs-toggle="offcanvas"
        data-bs-target="#adminOffcanvas"
        aria-controls="adminOffcanvas"
      >
        <i className="bi bi-list"></i> Menu de administracion
      </button>

      <div
        className="offcanvas offcanvas-start"
        tabIndex={-1}
        id="adminOffcanvas"
        aria-labelledby="adminOffcanvasLabel"
      >
        <div className="offcanvas-header">
          <h5 className="offcanvas-title" id="adminOffcanvasLabel">
            Sanrucho Admin
          </h5>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="offcanvas"
            aria-label="Cerrar"
          ></button>
        </div>
        <div className="offcanvas-body">
          <div className="list-group mb-3">
            <Link to="/admin" className="list-group-item list-group-item-action">
              <i className="bi bi-speedometer2 me-2"></i> Escritorio
            </Link>
            <Link
              to="/admin/productos"
              className="list-group-item list-group-item-action"
            >
              <i className="bi bi-box-seam me-2"></i> Productos
            </Link>
            {puedeVerUsuarios && (
              <Link
                to="/admin/usuarios"
                className="list-group-item list-group-item-action"
              >
                <i className="bi bi-people me-2"></i> Usuarios
              </Link>
            )}
            <Link
              to="/"
              className="list-group-item list-group-item-action text-primary"
            >
              <i className="bi bi-box-arrow-left me-2"></i> Volver al sitio
            </Link>
          </div>
          <button className="btn btn-outline-danger w-100" type="button" onClick={logout}>
            <i className="bi bi-box-arrow-right"></i> Cerrar sesion
          </button>
        </div>
      </div>
    </>
  );
}
