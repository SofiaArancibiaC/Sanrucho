
// ============================================================
// src/pages/PagoExito.tsx
// ============================================================

import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import type { Pedido } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { formatearPrecio } from "../utils/formato";

export default function PagoExito() {
const navigate = useNavigate();
const [pedido, setPedido] = useState<Pedido | null>(null);

useEffect(() => {
    const raw = localStorage.getItem(SANRUCHO_KEYS.ultimoPedido);
    if (!raw) {
    navigate("/");
    return;
    }
    const p = JSON.parse(raw) as Pedido;
    if (p.estadoPago !== "aprobado") {
    navigate("/pago/error");
    return;
    }
    setPedido(p);
}, [navigate]);

if (!pedido) return null;

const getMetodoLabel = () => {
    if (pedido.metodoPago === "webpay") return "Webpay";
    if (pedido.tipoTarjeta === "debito") return "Tarjeta de débito";
    if (pedido.tipoTarjeta === "credito") return "Tarjeta de crédito";
    return "Tarjeta";
};

return (
    <div className="container main">
    <div className="resultado-pago card-exito">
        <div className="resultado-icono exito">
        <i className="bi bi-check-circle-fill"></i>
        </div>
        <h1>¡Pago realizado con éxito!</h1>
        <p>Tu compra ha sido procesada correctamente. Gracias por comprar en Sanrucho.</p>
        <div className="resultado-info">
        <div><strong>Código de compra / Boleta:</strong> {pedido.codigoBoleta}</div>
        <div><strong>N° de pedido:</strong> {pedido.id}</div>
        {pedido.codigoSeguimiento && <div><strong>Código de seguimiento:</strong> {pedido.codigoSeguimiento}</div>}
        <div><strong>Valor total pagado:</strong> {formatearPrecio(pedido.total)}</div>
        <div><strong>Método de pago:</strong> {getMetodoLabel()}</div>
        <div><strong>Cliente:</strong> {pedido.cliente.nombre} {pedido.cliente.apellidos}</div>
        <div><strong>Día de despacho:</strong> {pedido.cliente.fechaDespacho}</div>
        <div><strong>Hora de despacho:</strong> {pedido.cliente.horaDespacho}</div>
        </div>
        <div className="d-flex flex-wrap gap-2 justify-content-center mt-4">
        <Link to="/resumen-compra" className="btn-enviar">
            <i className="bi bi-receipt"></i> Ver resumen de compra
        </Link>
        <Link to="/productos" className="btn-outline-accent">
            Seguir comprando
        </Link>
        </div>
    </div>
    </div>
);
}



