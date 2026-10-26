
// ============================================================
// src/pages/Checkout.tsx
// ============================================================

import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { SANRUCHO_KEYS } from "../storage/claves";
import { escribirColeccion, leerColeccion } from "../storage/useColeccion";
import { formatearPrecio, imagenPorDefecto, rutaImagenProducto } from "../utils/formato";
import type { DatosDespacho, DatosPago, Pedido, ItemCarrito } from "../types";
import { toast } from "../utils/alertas";

interface Snapshot {
items: ItemCarrito[];
subtotal: number;
envio: number;
total: number;
creadoEn: string;
}

function generarCodigoBoleta(): string {
const fecha = new Date();
const año = fecha.getFullYear().toString().slice(-2);
const mes = String(fecha.getMonth() + 1).padStart(2, "0");
const dia = String(fecha.getDate()).padStart(2, "0");
const random = Math.random().toString(36).substring(2, 6).toUpperCase();
return `BOLETA-${año}${mes}${dia}-${random}`;
}

export default function Checkout() {
const { sesion } = useAuth();
const { vaciar } = useCart();
const navigate = useNavigate();

const [snapshot, setSnapshot] = useState<Snapshot | null>(null);
const [paso, setPaso] = useState<1 | 2>(1);

const [despacho, setDespacho] = useState<DatosDespacho>({
    nombre: sesion?.nombre || "",
    apellidos: sesion?.apellidos || "",
    correo: sesion?.correo || "",
    telefono: "",
    direccion: sesion?.direccion || "",
    region: "",
    comuna: "",
    fechaDespacho: "",
    horaDespacho: "",
    observaciones: "",
});

const [pago, setPago] = useState<DatosPago>({
    tipo: "debito",
    numeroTarjeta: "",
    nombreTitular: "",
    vencimiento: "",
    cvv: "",
});

useEffect(() => {
    const raw = localStorage.getItem(SANRUCHO_KEYS.checkoutSnapshot);
    if (!raw) {
    toast("No hay productos para procesar el pago", "error");
    navigate("/carrito");
    return;
    }
    try {
    setSnapshot(JSON.parse(raw));
    } catch {
    navigate("/carrito");
    }
}, [navigate]);

const cambiarDespacho = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setDespacho((prev) => ({ ...prev, [name]: value }));
};

const cambiarPago = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setPago((prev) => ({ ...prev, [name]: value }));
};

const seleccionarTipoPago = (tipo: "debito" | "credito" | "webpay") => {
    setPago((prev) => ({ ...prev, tipo }));
};

const validarPaso1 = () => {
    if (!despacho.nombre.trim()) return "Ingresa el nombre del cliente.";
    if (!despacho.apellidos.trim()) return "Ingresa los apellidos del cliente.";
    if (!despacho.correo.trim()) return "Ingresa el correo electrónico.";
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(despacho.correo)) return "Correo electrónico inválido.";
    if (!despacho.telefono.trim()) return "Ingresa el teléfono de contacto.";
    if (!/^\+?\d{8,12}$/.test(despacho.telefono.replace(/\s/g, ""))) return "Teléfono inválido (8 - 12 dígitos).";
    if (!despacho.direccion.trim()) return "Ingresa la dirección de despacho.";
    if (!despacho.fechaDespacho) return "Selecciona el día de despacho.";
    if (!despacho.horaDespacho) return "Selecciona la hora de despacho.";
    return null;
};

const validarPaso2 = () => {
    if (pago.tipo === "debito" || pago.tipo === "credito") {
    if (!pago.nombreTitular?.trim()) return "Ingresa el nombre del titular de la tarjeta.";
    if (!pago.numeroTarjeta?.replace(/\s/g, "")) return "Ingresa el número de tarjeta.";
    const num = pago.numeroTarjeta.replace(/\s/g, "");
    if (num.length < 13 || num.length > 19) return "Número de tarjeta inválido.";
    if (!pago.vencimiento) return "Ingresa la fecha de vencimiento (MM/AA).";
    if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(pago.vencimiento)) return "Formato de vencimiento inválido (MM/AA).";
    if (!pago.cvv || pago.cvv.length < 3 || pago.cvv.length > 4) return "Ingresa el CVV correcto.";
    }
    return null;
};

const irPaso2 = (e: React.FormEvent) => {
    e.preventDefault();
    const err = validarPaso1();
    if (err) {
    toast(err, "error");
    return;
    }
    setPaso(2);
    window.scrollTo({ top: 0, behavior: "smooth" });
};

const confirmarPago = (e: React.FormEvent) => {
    e.preventDefault();
    const err = validarPaso2();
    if (err) {
    toast(err, "error");
    return;
    }
    if (!snapshot) return;

    const aprobado = Math.random() < 0.8;
    const codigoBoleta = generarCodigoBoleta();

    const pedido: Pedido = {
    id: "PED-" + Date.now().toString().slice(-8),
    codigoBoleta: codigoBoleta,
    fechaCreacion: new Date().toISOString(),
    cliente: despacho,
    items: snapshot.items,
    subtotal: snapshot.subtotal,
    envio: snapshot.envio,
    total: snapshot.total,
    metodoPago: pago.tipo === "webpay" ? "webpay" : "tarjeta",
    tipoTarjeta: pago.tipo === "debito" || pago.tipo === "credito" ? pago.tipo : undefined,
    estadoPago: aprobado ? "aprobado" : "rechazado",
    codigoSeguimiento: aprobado ? "SR-" + Math.random().toString(36).substring(2, 8).toUpperCase() : undefined,
    };

    localStorage.setItem(SANRUCHO_KEYS.ultimoPedido, JSON.stringify(pedido));

    // Historial de pedidos: alimenta la vista de boletas del administrador.
    // Se guardan también los rechazados para poder auditarlos; el listado
    // administrativo filtra por defecto solo los pagos aprobados.
    const pedidosPrevios = leerColeccion<Pedido>(SANRUCHO_KEYS.pedidos);
    escribirColeccion<Pedido>(SANRUCHO_KEYS.pedidos, [
    pedido,
    ...pedidosPrevios.filter((p) => p.id !== pedido.id),
    ]);

    vaciar();
    localStorage.removeItem(SANRUCHO_KEYS.checkoutSnapshot);

    if (aprobado) {
    navigate("/pago/exito");
    } else {
    navigate("/pago/error");
    }
};

if (!snapshot) return null;

const hoy = new Date().toISOString().split("T")[0];

return (
    <div className="container main">
    <div className="carrito-header">
        <div>
        <h1>Proceso de Pago</h1>
        <div className="subtitle">Paso 1: Datos del cliente y despacho | Paso 2: Método de pago</div>
        </div>
        <div className="d-flex gap-2 flex-wrap">
        <span className={`carrito-badge-count ${paso === 1 ? "active-step" : ""}`}>
            1. Datos del cliente
        </span>
        <span className={`carrito-badge-count ${paso === 2 ? "active-step" : ""}`}>
            2. Método de pago
        </span>
        </div>
    </div>

    <div className="carrito-layout">
        <section>
        {paso === 1 && (
            <form onSubmit={irPaso2} className="checkout-card">
            <h2 className="checkout-title">
                <i className="bi bi-person-lines-fill"></i> Datos del cliente y despacho
            </h2>
            <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label">Nombre *</label>
                <input className="form-control-custom" name="nombre" value={despacho.nombre} onChange={cambiarDespacho} />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Apellidos *</label>
                <input className="form-control-custom" name="apellidos" value={despacho.apellidos} onChange={cambiarDespacho} />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Correo electrónico *</label>
                <input className="form-control-custom" type="email" name="correo" value={despacho.correo} onChange={cambiarDespacho} />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Teléfono de contacto *</label>
                <input className="form-control-custom" name="telefono" placeholder="+56 9 1234 5678" value={despacho.telefono} onChange={cambiarDespacho} />
                </div>
                <div className="col-12">
                  <label className="form-label">Dirección de despacho *</label>
                <input className="form-control-custom" name="direccion" placeholder="Calle, N° Casa/Departamento, Comuna" value={despacho.direccion} onChange={cambiarDespacho} />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Día de despacho *</label>
                <input className="form-control-custom" type="date" name="fechaDespacho" min={hoy} value={despacho.fechaDespacho} onChange={cambiarDespacho} />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Hora de despacho *</label>
                <input className="form-control-custom" type="time" name="horaDespacho" value={despacho.horaDespacho} onChange={cambiarDespacho} />
                </div>
                <div className="col-12">
                <label className="form-label">Observaciones para el despacho (opcional)</label>
                <textarea className="form-control-custom" name="observaciones" rows={3} value={despacho.observaciones} onChange={cambiarDespacho} placeholder="Ej: Llamar al llegar, dejar en conserjería..." />
                </div>
            </div>
            <div className="d-flex justify-content-between flex-wrap gap-2 mt-4">
                <button type="button" className="btn-outline-accent" onClick={() => navigate("/carrito")}>
                <i className="bi bi-arrow-left"></i> Volver al carrito
                </button>
                <button type="submit" className="btn-enviar">
                Ir a pagar <i className="bi bi-arrow-right"></i>
                </button>
            </div>
            </form>
        )}

        {paso === 2 && (
            <form onSubmit={confirmarPago} className="checkout-card">
            <h2 className="checkout-title">
                <i className="bi bi-wallet2"></i> Método de pago
            </h2>

            <div className="metodo-pago-grid mb-4">
                <div
                className={`metodo-pago-card ${pago.tipo === "debito" ? "active" : ""}`}
                onClick={() => seleccionarTipoPago("debito")}
                >
                <div className="metodo-pago-icon">
                    <i className="bi bi-credit-card"></i>
                </div>
                <div className="metodo-pago-info">
                    <div className="metodo-pago-titulo">Tarjeta de débito</div>
                    <div className="metodo-pago-sub">Pago directo desde tu cuenta</div>
                </div>
                <div className="metodo-pago-radio">
                    <input type="radio" checked={pago.tipo === "debito"} readOnly />
                </div>
                </div>

                <div
                className={`metodo-pago-card ${pago.tipo === "credito" ? "active" : ""}`}
                onClick={() => seleccionarTipoPago("credito")}
                >
                <div className="metodo-pago-icon">
                    <i className="bi bi-credit-card-2-front"></i>
                </div>
                <div className="metodo-pago-info">
                    <div className="metodo-pago-titulo">Tarjeta de crédito</div>
                    <div className="metodo-pago-sub">Pago con tu tarjeta de crédito</div>
                </div>
                <div className="metodo-pago-radio">
                    <input type="radio" checked={pago.tipo === "credito"} readOnly />
                </div>
                </div>

                <div
                className={`metodo-pago-card ${pago.tipo === "webpay" ? "active" : ""}`}
                onClick={() => seleccionarTipoPago("webpay")}
                >
                <div className="metodo-pago-icon">
                    <i className="bi bi-shield-check"></i>
                </div>
                <div className="metodo-pago-info">
                    <div className="metodo-pago-titulo">Webpay</div>
                    <div className="metodo-pago-sub">Pago seguro a través de Webpay</div>
                </div>
                <div className="metodo-pago-radio">
                    <input type="radio" checked={pago.tipo === "webpay"} readOnly />
                </div>
                </div>
            </div>

            {(pago.tipo === "debito" || pago.tipo === "credito") && (
                <>
                <h5 className="mb-3" style={{ color: "var(--primary-hover)", fontWeight: 700 }}>
                    Datos de la tarjeta
                </h5>
                <div className="row g-3">
                    <div className="col-12">
                      <label className="form-label">Nombre del titular de la tarjeta *</label>
                    <input className="form-control-custom" name="nombreTitular" value={pago.nombreTitular} onChange={cambiarPago} placeholder="Tal como aparece en la tarjeta" />
                    </div>
                    <div className="col-12">
                      <label className="form-label">Número de tarjeta *</label>
                    <input className="form-control-custom" name="numeroTarjeta" value={pago.numeroTarjeta} onChange={cambiarPago} placeholder="1234 5678 9012 3456" maxLength={19} />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Fecha de vencimiento *</label>
                    <input className="form-control-custom" name="vencimiento" value={pago.vencimiento} onChange={cambiarPago} placeholder="MM/AA" maxLength={5} />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">CVV / CVC *</label>
                    <input className="form-control-custom" name="cvv" value={pago.cvv} onChange={cambiarPago} placeholder="123" maxLength={4} />
                    </div>
                </div>
                </>
            )}

            {pago.tipo === "webpay" && (
                <div className="info-pago-box">
                <i className="bi bi-shield-lock-fill"></i>
                <div>
                    <strong>Pago con Webpay</strong>
                    <div>Al seleccionar "Realizar pago", se simulará el proceso de pago a través de Webpay de forma segura.</div>
                </div>
                </div>
            )}

            <div className="info-pago-box mt-3">
                <i className="bi bi-shield-check"></i>
                <div>
                <strong>Transacción protegida</strong>
                <div>Esta es una simulación con fines demostrativos. El resultado será aleatorio entre pago exitoso o pago incorrecto.</div>
                </div>
            </div>

            <div className="d-flex justify-content-between flex-wrap gap-2 mt-4">
                <button type="button" className="btn-outline-accent" onClick={() => setPaso(1)}>
                <i className="bi bi-arrow-left"></i> Volver a datos del cliente
                </button>
                <button type="submit" className="btn-enviar">
                <i className="bi bi-check2-circle"></i> Realizar pago
                </button>
            </div>
            </form>
        )}
        </section>

        <aside className="resumen">
        <h2>Resumen de compra</h2>
        <div className="resumen-items">
            {snapshot.items.map((item) => (
            <div className="resumen-item" key={item.codigo}>
                <img
                src={rutaImagenProducto(item)}
                alt={item.nombre}
                className="resumen-item-img"
                onError={imagenPorDefecto}
                />
                <div className="resumen-item-info">
                <div className="resumen-item-nombre">{item.nombre}</div>
                <div className="resumen-item-det">Cantidad: {item.cantidad}</div>
                </div>
                <div className="resumen-item-sub">{formatearPrecio(item.precio * item.cantidad)}</div>
            </div>
            ))}
        </div>
        <hr />
        <div className="resumen-fila">
            <span>Subtotal</span>
            <strong>{formatearPrecio(snapshot.subtotal)}</strong>
        </div>
        <div className="resumen-fila">
            <span>Envío</span>
            <span style={{ color: "var(--color-success)", fontWeight: 700 }}>
            {snapshot.envio === 0 ? "Gratis" : formatearPrecio(snapshot.envio)}
            </span>
        </div>
        <hr />
        <div className="resumen-fila resumen-total">
            <span>Valor total de la compra</span>
            <span>{formatearPrecio(snapshot.total)}</span>
        </div>
        <div className="checkout-mini">
            <div><strong>Cliente:</strong> {despacho.nombre} {despacho.apellidos}</div>
            <div><strong>Teléfono:</strong> {despacho.telefono || "-"}</div>
            <div><strong>Correo:</strong> {despacho.correo || "-"}</div>
            <div><strong>Dirección:</strong> {despacho.direccion || "-"}</div>
            <div><strong>Día de despacho:</strong> {despacho.fechaDespacho || "-"}</div>
            <div><strong>Hora de despacho:</strong> {despacho.horaDespacho || "-"}</div>
            <div><strong>Método de pago:</strong> {pago.tipo === "debito" ? "Tarjeta de débito" : pago.tipo === "credito" ? "Tarjeta de crédito" : "Webpay"}</div>
        </div>
        </aside>
    </div>
    </div>
);
}











