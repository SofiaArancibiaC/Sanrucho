// ============================================================
// pages/Carrito.tsx
// Reemplaza carrito.html + carrito.js. Todo sale de useCart(),
// así que los totales y el badge del navbar quedan sincronizados.
// ============================================================

import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { formatearPrecio, rutaImagenProducto } from "../utils/formato";

export default function Carrito() {
  const {
    carrito,
    totalItems,
    subtotal,
    envio,
    total,
    cambiarCantidad,
    eliminar,
    finalizarCompra,
  } = useCart();

  return (
    <div className="container main">
      <div className="carrito-header">
        <div>
          <h1>Tu Carrito de Compras</h1>
          <div className="subtitle">
            Revisa tus productos elegidos antes de finalizar el pedido
          </div>
        </div>
        <span className="carrito-badge-count">
          <i className="bi bi-bag-heart"></i> {totalItems} artículos
        </span>
      </div>

      <div className="carrito-layout">
        <section className="carrito-articulos">
          {carrito.length === 0 ? (
            <div className="carrito-vacio-box">
              <i className="bi bi-bag-x"></i>
              <p>Tu carrito de compras aún está vacío</p>
              <Link className="btn-enviar" to="/productos">
                Explorar la tienda
              </Link>
            </div>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>Precio</th>
                    <th>Cantidad</th>
                    <th>Subtotal</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {carrito.map((item) => (
                    <tr key={item.codigo}>
                      <td>
                        <div className="carrito-producto">
                          <img
                            src={rutaImagenProducto(item)}
                            className="carrito-producto-img"
                            alt={item.nombre}
                          />
                          <div className="carrito-producto-info">
                            <span className="item-name">{item.nombre}</span>
                            <span className="item-cat">{item.codigo}</span>
                          </div>
                        </div>
                      </td>
                      <td className="carrito-precio">{formatearPrecio(item.precio)}</td>
                      <td>
                        <div className="d-flex align-items-center gap-2">
                          <button
                            type="button"
                            className="btn btn-sm btn-outline-secondary"
                            onClick={() => cambiarCantidad(item.codigo, -1)}
                            aria-label="Menos"
                          >
                            <i className="bi bi-dash"></i>
                          </button>
                          <span>{item.cantidad}</span>
                          <button
                            type="button"
                            className="btn btn-sm btn-outline-secondary"
                            onClick={() => cambiarCantidad(item.codigo, 1)}
                            aria-label="Más"
                          >
                            <i className="bi bi-plus"></i>
                          </button>
                        </div>
                      </td>
                      <td className="carrito-subtotal">
                        {formatearPrecio(item.precio * item.cantidad)}
                      </td>
                      <td>
                        <button
                          type="button"
                          className="btn btn-sm btn-outline-danger"
                          onClick={() => eliminar(item.codigo)}
                          aria-label="Eliminar"
                        >
                          <i className="bi bi-trash"></i>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <aside className="resumen">
          <h2>Resumen del Pedido</h2>
          <div className="resumen-fila">
            <span>Subtotal</span>
            <strong>{formatearPrecio(subtotal)}</strong>
          </div>
          <div className="resumen-fila">
            <span>Envío</span>
            <span style={{ color: "var(--color-success)", fontWeight: 700 }}>
              {envio === 0 ? "Gratis" : formatearPrecio(envio)}
            </span>
          </div>
          <hr />
          <div className="resumen-fila resumen-total">
            <span>Total</span>
            <span>{formatearPrecio(total)}</span>
          </div>

          <button
            type="button"
            className="btn-enviar btn-block"
            onClick={finalizarCompra}
            disabled={carrito.length === 0}
          >
            Finalizar Compra
          </button>
          <Link className="seguir-comprando" to="/productos">
            &larr; Seguir comprando
          </Link>
        </aside>
      </div>
    </div>
  );
}
