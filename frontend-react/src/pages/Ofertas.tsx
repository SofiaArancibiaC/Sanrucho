
// ============================================================
// src/pages/Ofertas.tsx
// ============================================================

import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import type { Producto } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { formatearPrecio, imagenPorDefecto, rutaImagenProducto } from "../utils/formato";
import { useCart } from "../context/CartContext";

export default function Ofertas() {
const [productosOferta, setProductosOferta] = useState<Producto[]>([]);
const { agregar } = useCart();

useEffect(() => {
    const data = JSON.parse(localStorage.getItem(SANRUCHO_KEYS.productos) ?? "[]") as Producto[];
    const ofertas = data.filter((p) => p.estado === "Activo" && p.oferta === true);
    setProductosOferta(ofertas);
}, []);

return (
    <div className="container main">
    <div className="blog-header">
        <div>
        <span className="badge-sugerencia">
            <i className="bi bi-tags-fill"></i> Ofertas Sanrucho
        </span>
        <h1 className="blog-title">Productos en Oferta</h1>
        <p className="blog-subtitle">
            Aprovecha nuestros precios rebajados por tiempo limitado
        </p>
        </div>
        <span className="blog-badge-count">
        {productosOferta.length} producto{productosOferta.length !== 1 ? "s" : ""} en oferta
        </span>
    </div>

    {productosOferta.length === 0 ? (
        <div className="sin-resultados-box">
        <i className="bi bi-gift"></i> Por el momento no hay productos en oferta
        </div>
    ) : (
        <div className="row g-4">
        {productosOferta.map((prod) => {
            const precioNormal = prod.precio;
            const precioRebajado = prod.precioOferta ?? precioNormal;
            return (
            <div className="col-12 col-sm-6 col-lg-3" key={prod.codigo}>
                <article className="product-card h-100 position-relative">
                {prod.descuento && (
                    <span
                    className="position-absolute top-0 start-0 m-2 badge"
                    style={{ background: "var(--color-error)", borderRadius: "var(--radius-pill)" }}
                    >
                    -{prod.descuento}%
                    </span>
                )}
                <div className="card-img-wrapper">
                    <img
                    src={rutaImagenProducto(prod)}
                    alt={prod.nombre}
                    className="product-img"
                    onError={imagenPorDefecto}
                    />
                </div>
                <div className="d-flex flex-column flex-grow-1">
                    <h5 className="card-title">{prod.nombre}</h5>
                    <div className="d-flex align-items-baseline gap-2 mb-2">
                    <span className="card-price">{formatearPrecio(precioRebajado)}</span>
                    <span style={{ textDecoration: "line-through", color: "var(--text-soft)", fontSize: "0.9rem" }}>
                        {formatearPrecio(precioNormal)}
                    </span>
                    </div>
                    <span className="card-category">EN OFERTA</span>
                    <div className="mt-auto d-flex flex-column gap-2">
                    <Link to={`/producto-detalle?codigo=${prod.codigo}`} className="btn-product text-center">
                        Ver detalle
                    </Link>
                    <button
                        type="button"
                        className="btn-enviar w-100"
                        onClick={() => agregar(prod, 1)}
                        disabled={prod.stock <= 0}
                    >
                        {prod.stock <= 0 ? "Sin stock" : "Agregar al carrito"}
                    </button>
                    </div>
                </div>
                </article>
            </div>
            );
        })}
        </div>
    )}
    </div>
);
}