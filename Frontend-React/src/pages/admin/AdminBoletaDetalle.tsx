// ============================================================
// pages/admin/AdminBoletaDetalle.tsx
// Detalle completo de una boleta del historial. Reutiliza el
// mismo componente que ve el cliente en /resumen-compra.
// ============================================================

import { Link, useParams } from "react-router-dom";
import { usePedidos } from "../../hooks/usePedidos";
import BoletaDetalle from "../../components/common/BoletaDetalle";

export default function AdminBoletaDetalle() {
  const { id } = useParams<{ id: string }>();
  const { obtenerPorId } = usePedidos();
  const pedido = id ? obtenerPorId(id) : undefined;

  return (
    <>
      <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-3">
        <Link
          to="/admin/boletas"
          className="btn-outline-accent text-decoration-none"
        >
          <i className="bi bi-arrow-left"></i> Volver a las boletas
        </Link>
        {pedido && (
          <span className="badge-sugerencia mb-0">
            N° de pedido: {pedido.id}
          </span>
        )}
      </div>

      {pedido ? (
        <BoletaDetalle
          pedido={pedido}
          titulo={`Boleta ${pedido.codigoBoleta}`}
          subtitulo={`Compra de ${pedido.cliente.nombre} ${pedido.cliente.apellidos}`}
        />
      ) : (
        <div className="alert alert-warning mb-0">
          No se encontró la boleta solicitada.{" "}
          <Link to="/admin/boletas" className="alert-link">
            Volver al listado
          </Link>
          .
        </div>
      )}
    </>
  );
}
