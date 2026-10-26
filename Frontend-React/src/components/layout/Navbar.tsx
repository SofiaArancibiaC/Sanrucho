// ============================================================
// components/layout/Navbar.tsx
// Reemplaza la barra superior de base.html. Ya no manipula el DOM:
// muestra el bloque de invitado o de usuario según useAuth(), y el
// badge del carrito según useCart().
// ============================================================

import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { esAdministrativo, useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";

export default function Navbar() {
  const { sesion, logout } = useAuth();
  const { totalItems } = useCart();
  const navigate = useNavigate();
  const [menuAbierto, setMenuAbierto] = useState(false);

  const cerrarSesion = () => {
    logout();
    setMenuAbierto(false);
    navigate("/");
  };

  const alternarMenu = () => setMenuAbierto((abierto) => !abierto);
  const cerrarMenu = () => setMenuAbierto(false);

  return (
    <header className={"topbar" + (menuAbierto ? " menu-abierto" : "")}>
      <div className="topbar-brand d-flex align-items-center">
        <Link
          to="/"
          className="d-flex align-items-center text-decoration-none"
          onClick={cerrarMenu}
        >
          <img
            src="/static/img/logo-sanrucho.png"
            alt="Sanrucho"
            style={{ height: 48, width: "auto", objectFit: "contain" }}
          />
        </Link>
      </div>

      <button
        type="button"
        className="topbar-toggle"
        aria-label={menuAbierto ? "Cerrar menú" : "Abrir menú"}
        aria-expanded={menuAbierto}
        onClick={alternarMenu}
      >
        <i className={"bi " + (menuAbierto ? "bi-x-lg" : "bi-list")}></i>
      </button>

      <nav className="topbar-nav align-items-center">
        <NavLink to="/" end onClick={cerrarMenu}>
          Inicio
        </NavLink>
        <NavLink to="/productos" onClick={cerrarMenu}>
          Productos
        </NavLink>
        <NavLink to="/ofertas" onClick={cerrarMenu} style={{ color: "#ff476d", fontWeight: 700 }}>
          <i className="bi bi-tags-fill me-1"></i> Ofertas
        </NavLink>
        <NavLink to="/categorias" onClick={cerrarMenu}>
          Categorías
        </NavLink>
        <NavLink to="/blog" onClick={cerrarMenu}>
          Blog
        </NavLink>
        <NavLink to="/nosotros" onClick={cerrarMenu}>
          Nosotros
        </NavLink>
        <NavLink to="/contacto" onClick={cerrarMenu}>
          Contacto
        </NavLink>
        {esAdministrativo(sesion) && (
          <NavLink
            to="/admin"
            style={{ color: "var(--primary)", fontWeight: 700 }}
            onClick={cerrarMenu}
          >
            Panel Admin
          </NavLink>
        )}
      </nav>

      <Link
        to="/carrito"
        className="topbar-cart btn btn-sm btn-outline-accent position-relative d-inline-flex align-items-center me-2"
        title="Ver carrito"
        onClick={cerrarMenu}
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
        <div className="topbar-auth align-items-center gap-2">
          <Link
            to="/login"
            className="btn btn-sm btn-outline-accent"
            onClick={cerrarMenu}
          >
            Iniciar sesión
          </Link>
          <Link to="/registro" className="btn btn-sm btn-accent" onClick={cerrarMenu}>
            Registrarse
          </Link>
        </div>
      ) : (
        <div className="topbar-auth align-items-center gap-2">
          {!esAdministrativo(sesion) && (
            <Link to="/perfil" className="btn btn-sm btn-outline-accent me-2" onClick={cerrarMenu}>
              Mi perfil
            </Link>
          )}
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
    </header>
  );
}
