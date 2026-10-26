// ============================================================
// pages/admin/AdminBoletas.tsx
// Listado de boletas de los clientes para Dueño y Admin.
// Permite buscar por N° de boleta/pedido, cliente, correo o
// teléfono, y filtrar por estado de pago y rango de fechas.
// ============================================================

import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { usePedidos } from "../../hooks/usePedidos";
import DataTable, { type Columna } from "../../components/common/DataTable";
import { formatearPrecio } from "../../utils/formato";
import type { Pedido } from "../../types";
import {
  FILTROS_PEDIDOS_VACIOS,
  etiquetaMetodoPago,
  filtrarPedidos,
  formatearFechaHora,
  unidadesPedido,
  type FiltroEstadoPago,
} from "../../utils/pedidos";

export default function AdminBoletas() {
  const { pedidos, aprobados, totalVendido } = usePedidos();

  const [texto, setTexto] = useState(FILTROS_PEDIDOS_VACIOS.texto);
  const [estado, setEstado] = useState<FiltroEstadoPago>(
    FILTROS_PEDIDOS_VACIOS.estado
  );
  const [desde, setDesde] = useState(FILTROS_PEDIDOS_VACIOS.desde);
  const [hasta, setHasta] = useState(FILTROS_PEDIDOS_VACIOS.hasta);

  const filtradas = useMemo(
    () => filtrarPedidos(pedidos, { texto, estado, desde, hasta }),
    [pedidos, texto, estado, desde, hasta]
  );

  const ticketPromedio =
    aprobados.length > 0 ? Math.round(totalVendido / aprobados.length) : 0;

  const limpiarFiltros = () => {
    setTexto(FILTROS_PEDIDOS_VACIOS.texto);
    setEstado(FILTROS_PEDIDOS_VACIOS.estado);
    setDesde(FILTROS_PEDIDOS_VACIOS.desde);
    setHasta(FILTROS_PEDIDOS_VACIOS.hasta);
  };

  const columnas: Columna<Pedido>[] = [
    {
      encabezado: "Boleta",
      celda: (p) => <strong>{p.codigoBoleta}</strong>,
    },
    { encabezado: "N° pedido", celda: (p) => p.id },
    {
      encabezado: "Cliente",
      celda: (p) => (
        <div>
          <strong>
            {p.cliente.nombre} {p.cliente.apellidos}
          </strong>
          <div className="small text-muted">{p.cliente.correo}</div>
        </div>
      ),
    },
    { encabezado: "Fecha de compra", celda: (p) => formatearFechaHora(p.fechaCreacion) },
    { encabezado: "Pago", celda: (p) => etiquetaMetodoPago(p) },
    {
      encabezado: "Unidades",
      celda: (p) => unidadesPedido(p),
    },
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
      <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4 pb-2 border-bottom">
        <div>
          <h1 className="fw-bold m-0" style={{ color: "var(--primary)" }}>
            Boletas de clientes
          </h1>
          <div className="text-muted mt-1">
            Compras realizadas en la tienda y su detalle de boleta.
          </div>
        </div>
        <span className="badge-sugerencia mb-0">
          {filtradas.length}{" "}
          {filtradas.length === 1 ? "boleta encontrada" : "boletas encontradas"}
        </span>
      </div>

      <div className="cards-grid">
        <div className="card kpi-card">
          <div className="card-title">Boletas aprobadas</div>
          <div className="card-value">{aprobados.length}</div>
          <div className="card-desc">Pagos exitosos registrados</div>
        </div>
        <div className="card kpi-card">
          <div className="card-title">Ventas totales</div>
          <div className="card-value" style={{ fontSize: "1.5rem" }}>
            {formatearPrecio(totalVendido)}
          </div>
          <div className="card-desc">Monto de boletas aprobadas</div>
        </div>
        <div className="card kpi-card">
          <div className="card-title">Ticket promedio</div>
          <div className="card-value" style={{ fontSize: "1.5rem" }}>
            {formatearPrecio(ticketPromedio)}
          </div>
          <div className="card-desc">Promedio por boleta aprobada</div>
        </div>
        <div className="card kpi-card">
          <div className="card-title">Pagos rechazados</div>
          <div className="card-value">{pedidos.length - aprobados.length}</div>
          <div className="card-desc">Intentos de compra fallidos</div>
        </div>
      </div>

      <div className="card mb-3">
        <div className="row g-2 align-items-end">
          <div className="col-12 col-lg-5">
            <label className="form-label" htmlFor="filtro-boletas">
              Buscar boleta
            </label>
            <input
              id="filtro-boletas"
              type="text"
              className="form-control-custom"
              placeholder="N° de boleta, pedido, cliente, correo o teléfono"
              value={texto}
              onChange={(e) => setTexto(e.target.value)}
            />
          </div>
          <div className="col-6 col-md-3 col-lg-2">
            <label className="form-label" htmlFor="filtro-estado">
              Estado del pago
            </label>
            <select
              id="filtro-estado"
              className="form-control-custom"
              value={estado}
              onChange={(e) => setEstado(e.target.value as FiltroEstadoPago)}
            >
              <option value="aprobado">Aprobados</option>
              <option value="rechazado">Rechazados</option>
              <option value="todos">Todos</option>
            </select>
          </div>
          <div className="col-6 col-md-3 col-lg-2">
            <label className="form-label" htmlFor="filtro-desde">
              Desde
            </label>
            <input
              id="filtro-desde"
              type="date"
              className="form-control-custom"
              value={desde}
              onChange={(e) => setDesde(e.target.value)}
            />
          </div>
          <div className="col-6 col-md-3 col-lg-2">
            <label className="form-label" htmlFor="filtro-hasta">
              Hasta
            </label>
            <input
              id="filtro-hasta"
              type="date"
              className="form-control-custom"
              value={hasta}
              onChange={(e) => setHasta(e.target.value)}
            />
          </div>
          <div className="col-6 col-md-3 col-lg-1">
            <button
              type="button"
              className="btn-outline-accent w-100"
              style={{ padding: "10px 16px", fontSize: "0.95rem" }}
              onClick={limpiarFiltros}
              title="Restablecer los filtros"
            >
              Limpiar
            </button>
          </div>
        </div>
      </div>

      <div className="table-wrapper">
        <DataTable
          columnas={columnas}
          datos={filtradas}
          clave={(p) => p.id}
          vacio="No hay boletas que coincidan con los filtros seleccionados."
        />
      </div>
    </>
  );
}
