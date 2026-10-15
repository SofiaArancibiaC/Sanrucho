// ============================================================
// pages/Blog.tsx
// Reemplaza blog.html + blog.js. El buscador es estado de React.
// ============================================================

import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useNoticias } from "../hooks/useNoticias";
import { formatearFecha } from "../utils/formato";

export default function Blog() {
  const { noticias } = useNoticias();
  const [busqueda, setBusqueda] = useState("");

  const filtradas = useMemo(() => {
    const texto = busqueda.trim().toLowerCase();
    if (texto === "") return noticias;
    return noticias.filter(
      (n) =>
        n.titulo.toLowerCase().includes(texto) ||
        n.resumen.toLowerCase().includes(texto)
    );
  }, [noticias, busqueda]);

  return (
    <div className="container blog-container">
      <header className="blog-header">
        <div>
          <span className="badge-sugerencia">
            <i className="bi bi-journal-text"></i> Novedades y Guías
          </span>
          <h1 className="blog-title">Blog Sanrucho</h1>
          <p className="blog-subtitle">
            Consejos, anuncios y novedades pensadas para tu comodidad.
          </p>
        </div>
      </header>

      <div className="blog-toolbar">
        <div className="blog-search-box">
          <i className="bi bi-search search-icon"></i>
          <input
            type="text"
            id="busqueda"
            className="form-control-custom blog-input"
            placeholder="Buscar artículos por título o contenido..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>
        <span className="blog-badge-count">{filtradas.length} artículos</span>
      </div>

      {filtradas.length === 0 ? (
        <div className="sin-resultados-box">
          <i className="bi bi-search fs-1 text-muted mb-2"></i>
          <p>No se encontraron artículos que coincidan con tu búsqueda.</p>
        </div>
      ) : (
        <div className="blog-grid">
          {filtradas.map((n) => (
            <article className="post-card" key={n.id}>
              <div className="post-thumb-wrapper">
                <img className="post-thumb" src={n.imagen} alt={n.titulo} />
              </div>
              <div className="post-card-body">
                <span className="post-date">
                  <i className="bi bi-calendar3"></i> {formatearFecha(n.fecha)}
                </span>
                <h2 className="post-card-title">{n.titulo}</h2>
                <p className="post-card-excerpt">{n.resumen}</p>
                <Link className="btn-accent post-btn" to={`/blog/${n.slug}`}>
                  Leer más <i className="bi bi-arrow-right"></i>
                </Link>
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
