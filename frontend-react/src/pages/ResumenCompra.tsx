
// ============================================================
// src/pages/ResumenCompra.tsx
// ============================================================

import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import type { Pedido } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { formatearPrecio, imagenPorDefecto, rutaImagenProducto } from "../utils/formato";

export default function ResumenCompra() {
const navigate = useNavigate();
const [pedido, setPedido] = useState<Pedido | null>(null);

useEffect(() => {
    const raw = localStorage.getItem(SANRUCHO_KEYS.ultimoPedido);
    if (!raw) {
    navigate("/");
    return;
    }
    setPedido(JSON.parse(raw) as Pedido);
}, [navigate]);

if (!pedido) return null;

const fechaCompra = new Date(pedido.fechaCreacion).toLocaleString("es-CL");
const fechaDespacho = pedido.cliente.fechaDespacho
    ? new Date(pedido.cliente.fechaDespacho + "T00:00:00").toLocaleDateString("es-CL")
    : "-";

const getMetodoLabel = () => {
    if (pedido.metodoPago === "webpay") return "Webpay";
    if (pedido.tipoTarjeta === "debito") return "Tarjeta de débito";
    if (pedido.tipoTarjeta === "credito") return "Tarjeta de crédito";
    return "Tarjeta de crédito/débito";
};

return (
    <div className="container main">
    <div className="carrito-header">
        <div>
        <h1>Resumen de Compra</h1>
        <div className="subtitle">Detalle completo de tu compra</div>
        </div>
        <span className={`carrito-badge-count ${pedido.estadoPago === "aprobado" ? "badge-aprobado" : "badge-rechazado"}`}>
        <i className={`bi ${pedido.estadoPago === "aprobado" ? "bi-check-circle" : "bi-x-circle"}`}></i>{" "}
        {pedido.estadoPago === "aprobado" ? "PAGO APROBADO" : "PAGO RECHAZADO"}
        </span>
    </div>

    <div className="resumen-grid">
        <section className="resumen-seccion">
        <div className="resumen-seccion-header flex-wrap gap-2">
            <h2><i className="bi bi-bag-check"></i> Detalle de lo comprado</h2>
            <span className="carrito-badge-count">Código de compra / Boleta: {pedido.codigoBoleta}</span>
        </div>
        <div className="table-wrapper">
            <table>
            <thead>
                <tr>
                <th>Producto</th>
                <th>Precio unitario</th>
                <th>Cantidad</th>
                <th>Subtotal</th>
                </tr>
            </thead>
            <tbody>
                {pedido.items.map((item) => (
                <tr key={item.codigo}>
                    <td>
                    <div className="carrito-producto">
                        <img
                        src={rutaImagenProducto(item)}
                        alt={item.nombre}
                        className="carrito-producto-img"
                        onError={imagenPorDefecto}
                        />
                        <div className="carrito-producto-info">
                        <span className="item-name">{item.nombre}</span>
                        <span className="item-cat">Código: {item.codigo}</span>
                        </div>
                    </div>
                    </td>
                    <td className="carrito-precio">{formatearPrecio(item.precio)}</td>
                    <td>{item.cantidad}</td>
                    <td className="carrito-subtotal">{formatearPrecio(item.precio * item.cantidad)}</td>
                </tr>
                ))}
            </tbody>
            </table>
        </div>
        </section>

        <div className="resumen-lateral">
        <div className="resumen-seccion">
            <h2><i className="bi bi-calculator"></i> Totales</h2>
            <div className="resumen-fila"><span>Subtotal</span><strong>{formatearPrecio(pedido.subtotal)}</strong></div>
            <div className="resumen-fila"><span>Envío</span><span style={{ color: "var(--color-success)", fontWeight: 700 }}>{pedido.envio === 0 ? "Gratis" : formatearPrecio(pedido.envio)}</span></div>
            <hr />
            <div className="resumen-fila resumen-total"><span>Valor total de la compra</span><span>{formatearPrecio(pedido.total)}</span></div>
        </div>

        <div className="resumen-seccion">
            <h2><i className="bi bi-person-circle"></i> Datos del cliente</h2>
            <div className="resumen-dato"><strong>Nombre completo:</strong> {pedido.cliente.nombre} {pedido.cliente.apellidos}</div>
            <div className="resumen-dato"><strong>Correo electrónico:</strong> {pedido.cliente.correo}</div>
            <div className="resumen-dato"><strong>Teléfono:</strong> {pedido.cliente.telefono}</div>
        </div>

        <div className="resumen-seccion">
            <h2><i className="bi bi-truck"></i> Datos de despacho</h2>
            <div className="resumen-dato"><strong>Dirección de despacho:</strong> {pedido.cliente.direccion}</div>
            <div className="resumen-dato"><strong>Día de despacho:</strong> {fechaDespacho}</div>
            <div className="resumen-dato"><strong>Hora de despacho:</strong> {pedido.cliente.horaDespacho}</div>
            {pedido.cliente.observaciones && (
            <div className="resumen-dato"><strong>Observaciones:</strong> {pedido.cliente.observaciones}</div>
            )}
        </div>

        <div className="resumen-seccion">
            <h2><i className="bi bi-receipt-cutoff"></i> Información de la boleta</h2>
            <div className="resumen-dato"><strong>Código de compra / Boleta:</strong> {pedido.codigoBoleta}</div>
            <div className="resumen-dato"><strong>N° de pedido:</strong> {pedido.id}</div>
            <div className="resumen-dato"><strong>Fecha y hora de compra:</strong> {fechaCompra}</div>
            <div className="resumen-dato"><strong>Método de pago:</strong> {getMetodoLabel()}</div>
            <div className="resumen-dato"><strong>Estado del pago:</strong> {pedido.estadoPago === "aprobado" ? "APROBADO" : "RECHAZADO"}</div>
            {pedido.codigoSeguimiento && <div className="resumen-dato"><strong>Código de seguimiento:</strong> {pedido.codigoSeguimiento}</div>}
        </div>
        </div>
    </div>

    <div className="d-flex flex-wrap gap-2 justify-content-center mt-4">
        <Link to="/productos" className="btn-enviar">Ir a la tienda</Link>
        <Link to="/" className="btn-outline-accent">Volver al inicio</Link>
    </div>
    </div>
);
}






