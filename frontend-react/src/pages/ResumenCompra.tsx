// ============================================================
// src/pages/ResumenCompra.tsx
// Resumen que ve el cliente después de pagar. Reutiliza
// BoletaDetalle para compartir el diseño con el panel admin.
// ============================================================

import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import type { Pedido } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import BoletaDetalle from "../components/common/BoletaDetalle";

export default function ResumenCompra() {
  const navigate = useNavigate();
  const [pedido, setPedido] = useState<Pedido | null>(null);

  useEffect(() => {
    const raw = localStorage.getItem(SANRUCHO_KEYS.ultimoPedido);
    if (!raw) {
      navigate("/");
      return;
    }
    try {
      setPedido(JSON.parse(raw) as Pedido);
    } catch {
      navigate("/");
    }
  }, [navigate]);

  if (!pedido) return null;

  return (
    <div className="container main">
      <BoletaDetalle pedido={pedido} />

      <div className="d-flex flex-wrap gap-2 justify-content-center mt-4">
        <Link to="/productos" className="btn-enviar">
          Ir a la tienda
        </Link>
        <Link to="/" className="btn-outline-accent">
          Volver al inicio
        </Link>
      </div>
    </div>
  );
}
