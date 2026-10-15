// ============================================================
// components/layout/Navbar.tsx
// Reemplaza la barra superior de base.html. Ya no manipula el DOM:
// muestra el bloque de invitado o de usuario según useAuth(), y el
// badge del carrito según useCart().
// ============================================================

import { Link, NavLink, useNavigate } from "react-router-dom";
import { esAdministrativo, useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";

export default function Navbar() {
  const { sesion, logout } = useAuth();
  const { totalItems } = useCart();
  const navigate = useNavigate();

  const cerrarSesion = () => {
    logout();
    navigate("/");
  };

  return (
    <header className="topbar">
      <div className="topbar-brand d-flex align-items-center">
        <Link to="/" className="d-flex align-items-center text-decoration-none">
          <img
            src="/static/img/logo-sanrucho.png"
            alt="Sanrucho"
            style={{ height: 48, width: "auto", objectFit: "contain" }}
          />
        </Link>
      </div>

      <nav className="topbar-nav d-flex align-items-center">
        <NavLink to="/" end>
          Inicio
        </NavLink>
        <NavLink to="/productos">Productos</NavLink>
        <NavLink to="/blog">Blog</NavLink>
        <NavLink to="/nosotros">Nosotros</NavLink>
        <NavLink to="/contacto">Contacto</NavLink>
        {esAdministrativo(sesion) && (
          <NavLink to="/admin" style={{ color: "var(--primary)", fontWeight: 700 }}>
            Panel Admin
          </NavLink>
        )}
      </nav>

      <div className="topbar-user d-flex align-items-center gap-3">
        <Link
          to="/carrito"
          className="btn btn-sm btn-outline-accent position-relative d-inline-flex align-items-center me-2"
          title="Ver carrito"
        >
          <i className="bi bi-cart3 fs-5"></i>
          {totalItems > 0 && (
            <span
              className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
              style={{ fontSize: "0.72rem" }}
            >
              {totalItems}
            </span>
          )}
        </Link>

        {!sesion ? (
          <div className="d-flex align-items-center gap-2">
            <Link to="/login" className="btn btn-sm btn-outline-accent">
              Iniciar sesión
            </Link>
            <Link to="/registro" className="btn btn-sm btn-accent">
              Registrarse
            </Link>
          </div>
        ) : (
          <div className="d-flex align-items-center gap-2">
            <span className="me-2">
              Hola, <strong>{sesion.nombre}</strong>
            </span>
            <button
              type="button"
              className="btn btn-sm btn-outline-danger"
              onClick={cerrarSesion}
            >
              Cerrar sesión
            </button>
          </div>
        )}
      </div>
    </header>
  );
}
