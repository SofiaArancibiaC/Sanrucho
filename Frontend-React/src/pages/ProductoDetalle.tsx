// ============================================================
// pages/ProductoDetalle.tsx
// Reemplaza detalle-producto.html + detalle-producto.js + resenas.js.
// El "?codigo=" se lee con useSearchParams de React Router.
// ============================================================

import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { useProductos } from "../hooks/useProductos";
import { useTablasMaestras } from "../hooks/useTablasMaestras";
import { useResenas } from "../hooks/useResenas";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { formatearPrecio, rutaImagenProducto } from "../utils/formato";
import { toast } from "../utils/alertas";

export default function ProductoDetalle() {
  const [parametros] = useSearchParams();
  const codigo = parametros.get("codigo") ?? "";
  const { obtenerPorCodigo } = useProductos();
  const { obtenerNombreCategoria } = useTablasMaestras();
  const { sesion } = useAuth();
  const { agregar } = useCart();

  const producto = obtenerPorCodigo(codigo);
  const { delProducto, agregar: agregarResena } = useResenas(codigo);

  const [cantidad, setCantidad] = useState(1);
  const [calificacion, setCalificacion] = useState(5);
  const [comentario, setComentario] = useState("");

  if (!producto) {
    return (
      <main className="container my-5 text-center">
        <h2>Producto no encontrado</h2>
        <p className="text-muted">El producto que buscas no existe o fue retirado.</p>
        <Link to="/productos" className="btn btn-accent">
          &larr; Volver al catálogo
        </Link>
      </main>
    );
  }

  const agotado = producto.stock <= 0;
  const promedio =
    delProducto.length > 0
      ? delProducto.reduce((acc, r) => acc + r.calificacion, 0) / delProducto.length
      : 0;

  const enviarResena = (e: FormEvent) => {
    e.preventDefault();
    if (!comentario.trim()) {
      toast("Escribe un comentario para publicar", "error");
      return;
    }
    agregarResena({
      productoCodigo: producto.codigo,
      usuarioNombre: sesion ? sesion.nombre : "Anónimo",
      calificacion,
      comentario: comentario.trim(),
      fecha: new Date().toISOString().slice(0, 10),
    });
    setComentario("");
    setCalificacion(5);
    toast("¡Gracias por tu reseña!", "success");
  };

  return (
    <>
      <main className="container my-5">
        <div className="mb-4">
          <Link to="/productos" className="btn btn-outline-accent">
            &larr; Volver al catálogo
          </Link>
        </div>

        <div className="row justify-content-center g-4">
          <div className="col-md-5">
            <img
              src={rutaImagenProducto(producto)}
              className="img-fluid rounded shadow-sm"
              alt={producto.nombre}
            />
          </div>
          <div className="col-md-6">
            <span className="badge bg-secondary mb-2">
              {obtenerNombreCategoria(producto.categoriaId)}
            </span>
            <h1 className="h3 fw-bold">{producto.nombre}</h1>
            <p className="text-muted">Código: {producto.codigo}</p>
            <p className="fs-3 fw-bold text-accent">
              {formatearPrecio(producto.precio)}
            </p>
            <p>{producto.descripcion}</p>
            <p>
              {agotado ? (
                <span className="badge text-bg-danger">Agotado</span>
              ) : (
                <span className="badge text-bg-success">
                  {producto.stock} unidades disponibles
                </span>
              )}
            </p>

            <div className="d-flex align-items-center gap-2 mt-3" style={{ maxWidth: 320 }}>
              <input
                type="number"
                className="form-control"
                min={1}
                max={producto.stock}
                value={cantidad}
                onChange={(e) => setCantidad(Number(e.target.value))}
              />
              <button
                type="button"
                className="btn btn-accent text-nowrap"
                disabled={agotado}
                onClick={() => agregar(producto, cantidad)}
              >
                <i className="bi bi-bag-plus"></i> Agregar
              </button>
            </div>
          </div>
        </div>
      </main>

      <section className="seccion-resenas my-5">
        <div className="container">
          <div className="text-center mb-5">
            <h3 className="resenas-titulo">Opiniones de nuestros clientes</h3>
            <p className="text-muted">
              Conoce lo que opinan quienes ya compraron este producto
            </p>
          </div>

          <div className="row g-4 justify-content-center">
            <div className="col-lg-7 col-md-12">
              <div className="resenas-resumen-box mb-4">
                <span className="fs-3 fw-bold">{promedio.toFixed(1)}</span>{" "}
                <i className="bi bi-star-fill text-warning"></i>
                <span className="text-muted ms-2">
                  ({delProducto.length} reseñas)
                </span>
              </div>
              <div className="d-flex flex-column gap-3">
                {delProducto.length === 0 ? (
                  <p className="text-muted">
                    Sé el primero en dejar una reseña de este producto.
                  </p>
                ) : (
                  delProducto.map((r) => (
                    <div key={r.id} className="card p-3 shadow-sm border-0">
                      <div className="d-flex justify-content-between">
                        <strong>{r.usuarioNombre}</strong>
                        <span className="text-warning">
                          {Array.from({ length: r.calificacion }).map((_, i) => (
                            <i key={i} className="bi bi-star-fill"></i>
                          ))}
                        </span>
                      </div>
                      <p className="mb-1 mt-2">{r.comentario}</p>
                      <small className="text-muted">{r.fecha}</small>
                    </div>
                  ))
                )}
              </div>
            </div>

            <div className="col-lg-5 col-md-8">
              <div className="card p-4 shadow-sm border-0 sticky-top" style={{ top: "2rem" }}>
                <h5 className="mb-2">Deja tu reseña</h5>
                <p className="text-muted small mb-4">
                  Comparte tu experiencia para ayudar a otros usuarios.
                </p>

                {!sesion ? (
                  <div className="alert alert-light text-center">
                    Debes{" "}
                    <Link to="/login" className="fw-bold">
                      iniciar sesión
                    </Link>{" "}
                    para dejar una opinión.
                  </div>
                ) : (
                  <form onSubmit={enviarResena}>
                    <div className="mb-3">
                      <label className="form-label d-block">Tu calificación</label>
                      <div className="d-inline-flex gap-1 fs-4">
                        {[1, 2, 3, 4, 5].map((n) => (
                          <i
                            key={n}
                            className={
                              "bi " + (n <= calificacion ? "bi-star-fill" : "bi-star") +
                              " text-warning"
                            }
                            role="button"
                            onClick={() => setCalificacion(n)}
                          ></i>
                        ))}
                      </div>
                    </div>
                    <div className="mb-3">
                      <label className="form-label" htmlFor="inputComentario">
                        Tu opinión
                      </label>
                      <textarea
                        className="form-control"
                        id="inputComentario"
                        rows={4}
                        placeholder="¿Qué te gustó o disgustó del producto?"
                        value={comentario}
                        onChange={(e) => setComentario(e.target.value)}
                      ></textarea>
                    </div>
                    <button type="submit" className="btn btn-accent w-100 py-2">
                      Publicar reseña
                    </button>
                  </form>
                )}
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  );
}
