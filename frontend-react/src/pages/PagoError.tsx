
// ============================================================
// src/pages/PagoError.tsx
// ============================================================

import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import type { Pedido } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";

export default function PagoError() {
const navigate = useNavigate();
const [pedido, setPedido] = useState<Pedido | null>(null);

useEffect(() => {
    const raw = localStorage.getItem(SANRUCHO_KEYS.ultimoPedido);
    if (raw) {
    setPedido(JSON.parse(raw) as Pedido);
    }
}, []);

return (
    <div className="container main">
    <div className="resultado-pago card-error">
        <div className="resultado-icono error">
        <i className="bi bi-x-circle-fill"></i>
        </div>
        <h1>Error en el pago</h1>
        <p>No se pudo procesar tu pago con los datos de la tarjeta ingresados.</p>
        <div className="resultado-info text-center">
        <div>Posibles causas: Fondos insuficientes, tarjeta vencida o datos incorrectos.</div>
        {pedido && <div><strong>Código de intento de compra:</strong> {pedido.codigoBoleta}</div>}
        </div>
        <div className="d-flex flex-wrap gap-2 justify-content-center mt-4">
        <button type="button" className="btn-enviar" onClick={() => navigate("/checkout")}>
            <i className="bi bi-arrow-clockwise"></i> Volver a ingresar datos de tarjeta
        </button>
        <Link to="/carrito" className="btn-outline-accent">
            Volver al carrito
        </Link>
        </div>
    </div>
    </div>
);
}