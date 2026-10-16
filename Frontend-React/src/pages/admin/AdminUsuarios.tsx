// ============================================================
// pages/admin/AdminUsuarios.tsx
// Reemplaza admin-usuarios.html + cargarUsuarios() de usuarios.js.
// ============================================================

import { useNavigate } from "react-router-dom";
import { useUsuarios } from "../../hooks/useUsuarios";
import { useAuth } from "../../context/AuthContext";
import DataTable, { type Columna } from "../../components/common/DataTable";
import type { Usuario } from "../../types";
import { formatearFecha } from "../../utils/formato";
import { confirmar, toast } from "../../utils/alertas";
import { Link } from "react-router-dom";

export default function AdminUsuarios() {
  const { datos, eliminarUsuario, obtenerNombreRol } = useUsuarios();
  const { sesion } = useAuth();
  const navigate = useNavigate();

  const eliminar = async (u: Usuario) => {
    if (sesion && sesion.run === u.run) {
      toast("No puedes eliminar tu propia cuenta", "error");
      return;
    }
    const ok = await confirmar(
      `¿Deseas eliminar al usuario ${u.run}?`,
      "Esta acción no se puede deshacer."
    );
    if (!ok) return;
    eliminarUsuario(u.run);
    toast("Usuario eliminado", "success");
  };

  const columnas: Columna<Usuario>[] = [
    { encabezado: "RUN", celda: (u) => u.run },
    {
      encabezado: "Nombre",
      celda: (u) => (
        <span>
          <strong>{u.nombre}</strong> {u.apellidos}
        </span>
      ),
    },
    { encabezado: "Email", celda: (u) => u.correo },
    { encabezado: "Dirección", celda: (u) => u.direccion || "—" },
    {
      encabezado: "Rol",
      celda: (u) => (
        <span className={`badge ${u.rolId === 3 ? "badge-cliente" : "badge-admin"}`}>
          {obtenerNombreRol(u.rolId)}
        </span>
      ),
    },
    {
      encabezado: "Estado",
      celda: (u) => (
        <span className={`badge ${u.estado === "Activo" ? "badge-activo" : "badge-inactivo"}`}>
          {u.estado}
        </span>
      ),
    },
    { encabezado: "Fecha Nac.", celda: (u) => formatearFecha(u.fechaNacimiento) },
    {
      encabezado: "Acciones",
      className: "text-nowrap",
      celda: (u) => (
        <div className="d-flex gap-1">
          <button
            type="button"
            className="btn btn-sm btn-outline-primary"
            title="Editar"
            onClick={() =>
              navigate(`/admin/usuarios/form?run=${encodeURIComponent(u.run)}`)
            }
          >
            Editar
          </button>
          <button
            type="button"
            className="btn btn-sm btn-danger"
            title="Eliminar"
            onClick={() => eliminar(u)}
          >
            Eliminar
          </button>
        </div>
      ),
    },
  ];

  return (
    <>
      <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4 pb-2 border-bottom">
        <h1 className="fw-bold m-0" style={{ color: "var(--primary)" }}>
          Administración de Usuarios
        </h1>
        <Link to="/admin/usuarios/form" className="btn-accent text-decoration-none">
          + Nuevo usuario
        </Link>
      </div>

      <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-3">
        <span className="badge-sugerencia mb-0">Listado de usuarios</span>
      </div>

      <div className="table-wrapper">
        <DataTable
          columnas={columnas}
          datos={datos}
          clave={(u) => u.run}
          vacio="No hay usuarios registrados."
        />
      </div>
    </>
  );
}
