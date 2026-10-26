// ============================================================
// pages/admin/AdminUsuarioHistorial.tsx
// Historial de compras de un usuario. Se arma con las boletas
// guardadas en "sanrucho_pedidos", asociadas por el correo que
// el cliente usó en el checkout.
// ============================================================

import { useMemo } from "react";
import { Link, useParams } from "react-router-dom";
import { usePedidos } from "../../hooks/usePedidos";
import { useUsuarios } from "../../hooks/useUsuarios";
import DataTable, { type Columna } from "../../components/common/DataTable";
import type { Pedido } from "../../types";
import { formatearFecha, formatearPrecio } from "../../utils/formato";
import {
  etiquetaMetodoPago,
  formatearFechaHora,
  pedidosDeUsuario,
  unidadesPedido,
} from "../../utils/pedidos";

export default function AdminUsuarioHistorial() {
  const { run = "" } = useParams<{ run: string }>();
  const { obtenerPorRun, obtenerNombreRol } = useUsuarios();
  const { pedidos } = usePedidos();

  const usuario = obtenerPorRun(run);

  const compras = useMemo(
    () => (usuario ? pedidosDeUsuario(pedidos, usuario.correo) : []),
    [pedidos, usuario]
  );

  const aprobadas = compras.filter((p) => p.estadoPago === "aprobado");
  const totalComprado = aprobadas.reduce(
    (total, pedido) => total + pedido.total,
    0
  );
  const ultimaCompra = compras[0];

  if (!usuario) {
    return (
      <>
        <Link
          to="/admin/usuarios"
          className="btn-outline-accent text-decoration-none"
        >
          <i className="bi bi-arrow-left"></i> Volver a usuarios
        </Link>
        <div className="alert alert-warning mt-3 mb-0">
          No se encontró el usuario solicitado.{" "}
          <Link to="/admin/usuarios" className="alert-link">
            Volver al listado
          </Link>
          .
        </div>
      </>
    );
  }

  const columnas: Columna<Pedido>[] = [
    {
      encabezado: "Boleta",
      celda: (p) => <strong>{p.codigoBoleta}</strong>,
    },
    { encabezado: "N° pedido", celda: (p) => p.id },
    {
      encabezado: "Fecha",
      celda: (p) => formatearFechaHora(p.fechaCreacion),
    },
    { encabezado: "Pago", celda: (p) => etiquetaMetodoPago(p) },
    { encabezado: "Unidades", celda: (p) => unidadesPedido(p) },
    {
      encabezado: "Total",
      className: "text-nowrap",
      celda: (p) => <strong>{formatearPrecio(p.total)}</strong>,
    },
    {
      encabezado: "Estado",
      celda: (p) => (
        <span
          className={`badge ${
            p.estadoPago === "aprobado" ? "badge-activo" : "badge-inactivo"
          }`}
        >
          {p.estadoPago === "aprobado" ? "Aprobado" : "Rechazado"}
        </span>
      ),
    },
    {
      encabezado: "Acciones",
      className: "text-nowrap",
      celda: (p) => (
        <Link
          to={`/admin/boletas/${encodeURIComponent(p.id)}`}
          className="btn btn-sm btn-outline-primary"
          title="Ver boleta completa"
        >
          Ver boleta
        </Link>
      ),
    },
  ];

  return (
    <>
      <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
        <Link
          to="/admin/usuarios"
          className="btn-outline-accent text-decoration-none"
        >
          <i className="bi bi-arrow-left"></i> Volver a usuarios
        </Link>
        <Link
          to={`/admin/usuarios/form?run=${encodeURIComponent(usuario.run)}`}
          className="btn-accent text-decoration-none"
        >
          <i className="bi bi-pencil-square"></i> Editar usuario
        </Link>
      </div>

      <div className="mb-4 pb-2 border-bottom">
        <h1 className="fw-bold m-0" style={{ color: "var(--primary)" }}>
          Historial de compras
        </h1>
        <div className="text-muted mt-1">
          {usuario.nombre} {usuario.apellidos} · {usuario.correo}
        </div>
      </div>

      <div className="cards-grid">
        <div className="card kpi-card">
          <div className="card-title">Compras registradas</div>
          <div className="card-value">{compras.length}</div>
          <div className="card-desc">Incluye pagos rechazados</div>
        </div>
        <div className="card kpi-card">
          <div className="card-title">Compras pagadas</div>
          <div className="card-value">{aprobadas.length}</div>
          <div className="card-desc">Pagos aprobados</div>
        </div>
        <div className="card kpi-card">
          <div className="card-title">Total comprado</div>
          <div className="card-value" style={{ fontSize: "1.5rem" }}>
            {formatearPrecio(totalComprado)}
          </div>
          <div className="card-desc">Solo boletas aprobadas</div>
        </div>
        <div className="card kpi-card">
          <div className="card-title">Última compra</div>
          <div className="card-value" style={{ fontSize: "1.1rem" }}>
            {ultimaCompra
              ? formatearFechaHora(ultimaCompra.fechaCreacion)
              : "Sin compras"}
          </div>
          <div className="card-desc">Intento más reciente</div>
        </div>
      </div>

      <div className="card mb-3">
        <h2
          className="h5 fw-bold mb-3"
          style={{ color: "var(--primary-hover)" }}
        >
          <i className="bi bi-person-vcard"></i> Datos del usuario
        </h2>
        <div className="row g-3">
          <div className="col-6 col-md-3">
            <div className="small text-muted">RUN</div>
            <div className="fw-semibold">{usuario.run}</div>
          </div>
          <div className="col-6 col-md-3">
            <div className="small text-muted">Rol</div>
            <div>
              <span
                className={`badge ${
                  usuario.rolId === 3 ? "badge-cliente" : "badge-admin"
                }`}
              >
                {obtenerNombreRol(usuario.rolId)}
              </span>
            </div>
          </div>
          <div className="col-6 col-md-3">
            <div className="small text-muted">Estado</div>
            <div>
              <span
                className={`badge ${
                  usuario.estado === "Activo" ? "badge-activo" : "badge-inactivo"
                }`}
              >
                {usuario.estado}
              </span>
            </div>
          </div>
          <div className="col-6 col-md-3">
            <div className="small text-muted">Fecha de nacimiento</div>
            <div className="fw-semibold">
              {formatearFecha(usuario.fechaNacimiento)}
            </div>
          </div>
          <div className="col-12 col-md-6">
            <div className="small text-muted">Correo electrónico</div>
            <div className="fw-semibold">{usuario.correo}</div>
          </div>
          <div className="col-12 col-md-6">
            <div className="small text-muted">Dirección</div>
            <div className="fw-semibold">{usuario.direccion || "—"}</div>
          </div>
        </div>
      </div>

      <p className="small text-muted">
        Las compras se asocian por el correo usado en el checkout. Los pedidos
        rechazados también quedan guardados para poder revisarlos.
      </p>

      <div className="table-wrapper">
        <DataTable
          columnas={columnas}
          datos={compras}
          clave={(p) => p.id}
          vacio="Este usuario todavía no tiene compras registradas."
        />
      </div>
    </>
  );
}
