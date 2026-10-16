// ============================================================
// components/common/ProductCard.tsx
// Reemplaza la tarjeta que generaba renderizarProductos() en
// productos.js. Maneja el botón "Agregar" según stock y sesión.
// ============================================================

import { Link } from "react-router-dom";
import type { Producto } from "../../types";
import { useCart } from "../../context/CartContext";
import { useTablasMaestras } from "../../hooks/useTablasMaestras";
import { formatearPrecio, rutaImagenProducto } from "../../utils/formato";

export default function ProductCard({ producto }: { producto: Producto }) {
  const { agregar } = useCart();
  const { obtenerNombreCategoria } = useTablasMaestras();

  const agotado = producto.stock <= 0;
  const stockBajo =
    !agotado &&
    producto.stockCritico != null &&
    producto.stock <= producto.stockCritico;

  return (
    <div className="col-12 col-sm-6 col-lg-4 mb-4">
      <div className="card h-100 shadow-sm producto-card">
        <img
          src={rutaImagenProducto(producto)}
          className="card-img-top producto-img"
          alt={producto.nombre}
        />
        <div className="card-body d-flex flex-column">
          <span className="badge bg-secondary mb-2">
            {obtenerNombreCategoria(producto.categoriaId)}
          </span>
          <h5 className="card-title">{producto.nombre}</h5>
          <p className="card-text small text-muted flex-grow-1">
            {producto.descripcion}
          </p>

          <div className="d-flex justify-content-between align-items-center mb-2">
            <span className="fs-5 fw-bold text-accent">
              {formatearPrecio(producto.precio)}
            </span>
            {agotado ? (
              <span className="badge text-bg-danger">Agotado</span>
            ) : stockBajo ? (
              <span className="badge text-bg-warning">Últimas unidades</span>
            ) : (
              <span className="badge text-bg-success">Disponible</span>
            )}
          </div>

          <div className="d-grid gap-2">
            <Link
              to={`/producto-detalle?codigo=${encodeURIComponent(producto.codigo)}`}
              className="btn btn-outline-accent btn-sm"
            >
              Ver detalle
            </Link>
            <button
              type="button"
              className="btn btn-accent btn-sm"
              disabled={agotado}
              onClick={() => agregar(producto, 1)}
            >
              <i className="bi bi-bag-plus"></i> Agregar al carrito
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
