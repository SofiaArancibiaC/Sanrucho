// ============================================================
// pages/admin/AdminDashboard.tsx
// Reemplaza admin.html + admin.js. Indicadores (KPIs) y un listado
// de usuarios con buscador en React.
// ============================================================

import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useProductos } from "../../hooks/useProductos";
import { useUsuarios } from "../../hooks/useUsuarios";
import { usePedidos } from "../../hooks/usePedidos";
import { useTablasMaestras } from "../../hooks/useTablasMaestras";
import DataTable, { type Columna } from "../../components/common/DataTable";
import type { Usuario } from "../../types";
import { formatearPrecio } from "../../utils/formato";

export default function AdminDashboard() {
  const { datos: productos } = useProductos();
  const { datos: usuarios, obtenerNombreRol } = useUsuarios();
  const { pedidos, aprobados, totalVendido } = usePedidos();
  const { categorias } = useTablasMaestras();

  const [busqueda, setBusqueda] = useState("");

  const usuariosFiltrados = useMemo(() => {
    const texto = busqueda.trim().toLowerCase();
    if (texto === "") return usuarios;
    return usuarios.filter((u) =>
      Object.values(u).some((v) => String(v).toLowerCase().includes(texto))
    );
  }, [usuarios, busqueda]);

  const columnas: Columna<Usuario>[] = [
    { encabezado: "RUN", celda: (u) => u.run },
    {
      encabezado: "Nombre",
      celda: (u) => (
        <strong>
          {u.nombre} {u.apellidos}
        </strong>
      ),
    },
    { encabezado: "Correo", celda: (u) => u.correo },
    { encabezado: "Rol", celda: (u) => obtenerNombreRol(u.rolId) },
    {
      encabezado: "Estado",
      celda: (u) => (
        <span className={`badge ${u.estado === "Activo" ? "bg-success" : "bg-secondary"}`}>
          {u.estado}
        </span>
      ),
    },
  ];

  return (
    <>
      <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4 pb-2 border-bottom">
        <h1 className="fw-bold m-0" style={{ color: "var(--primary)" }}>
          Panel Administrativo
        </h1>
        <Link to="/admin/usuarios/form" className="btn-accent text-decoration-none">
          + Nuevo registro
        </Link>
      </div>

      <div className="cards-grid">
        <div className="card kpi-card">
          <div className="card-title">Usuarios</div>
          <div className="card-value">{usuarios.length}</div>
          <div className="card-desc">Usuarios registrados</div>
        </div>
        <div className="card kpi-card">
          <div className="card-title">Productos</div>
          <div className="card-value">{productos.length}</div>
          <div className="card-desc">Productos en catálogo</div>
        </div>
        <div className="card kpi-card">
          <div className="card-title">Pedidos</div>
          <div className="card-value">{pedidos.length}</div>
          <div className="card-desc">
            {aprobados.length} aprobados · {formatearPrecio(totalVendido)} vendidos
          </div>
        </div>
        <div className="card kpi-card">
          <div className="card-title">Catálogo</div>
          <div className="card-value">{categorias.length}</div>
          <div className="card-desc">Categorías activas</div>
        </div>
      </div>

      <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-3">
        <div className="flex-grow-1" style={{ maxWidth: 400 }}>
          <input
            type="text"
            className="form-control-custom"
            placeholder="Buscar registros..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>
      </div>

      <div className="table-wrapper">
        <DataTable
          columnas={columnas}
          datos={usuariosFiltrados}
          clave={(u) => u.run}
          vacio="No hay registros para mostrar."
        />
      </div>
    </>
  );
}
