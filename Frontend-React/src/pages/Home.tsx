// ============================================================
// pages/Home.tsx
// Reemplaza index.html. El banner de sesión ya no se alterna con
// getElementById: se renderiza según useAuth().sesion.
// ============================================================

import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useTablasMaestras } from "../hooks/useTablasMaestras";

export default function Home() {
  const { sesion } = useAuth();
  const { categorias } = useTablasMaestras();
  const categoriasDestacadas = categorias.slice(0, 4);

  return (
    <>
      <div className="bienvenidos">
        <img src="/static/img/index/Sanrucho-Bienvenidos.png" alt="Bienvenidos" />
      </div>

      <div
        id="carouselExampleAutoplaying"
        className="carousel carousel-dark slide w-75 mx-auto"
        data-bs-ride="carousel"
      >
        <div className="carousel-inner">
          <div className="carousel-item active">
            <img
              src="/static/img/index/blindbox-sanrio1.webp"
              className="d-block w-60 mx-auto"
              alt="blindbox"
            />
          </div>
          <div className="carousel-item">
            <img
              src="/static/img/index/sanrio-figuras.webp"
              className="d-block w-60 mx-auto"
              alt="figura"
            />
          </div>
          <div className="carousel-item">
            <img
              src="/static/img/index/llaveros-sanrio2.webp"
              className="d-block w-60 mx-auto"
              alt="llaveros"
            />
          </div>
        </div>
        <button
          className="carousel-control-prev"
          type="button"
          data-bs-target="#carouselExampleAutoplaying"
          data-bs-slide="prev"
        >
          <span className="carousel-control-prev-icon" aria-hidden="true"></span>
          <span className="visually-hidden">Anterior</span>
        </button>
        <button
          className="carousel-control-next"
          type="button"
          data-bs-target="#carouselExampleAutoplaying"
          data-bs-slide="next"
        >
          <span className="carousel-control-next-icon" aria-hidden="true"></span>
          <span className="visually-hidden">Siguiente</span>
        </button>
      </div>

      <div className="container my-5">
        <h3 className="text-center mb-3">Categorías destacadas</h3>
        <div className="row g-3 justify-content-center">
          {categoriasDestacadas.map((c) => (
            <div key={c.id} className="col-6 col-md-3">
              <Link
                to={`/productos?cat=${c.id}`}
                className="card text-decoration-none h-100 shadow-sm"
              >
                <div className="card-body text-center">
                  <i className="bi bi-tags fs-3 d-block mb-2"></i>
                  <span className="fw-semibold">{c.nombre}</span>
                </div>
              </Link>
            </div>
          ))}
        </div>
        <div className="text-center mt-3">
          <Link to="/categorias" className="shadow-effect btn-sm">
            Ver todas las categorías
          </Link>
        </div>
      </div>

      <div className="algunos-productos text-center my-4">
        <h1 data-text="Algunos de nuestros productos:">
          Algunos de nuestros productos:
        </h1>
      </div>

      <div className="container my-5">
        <div className="row g-4 justify-content-center">
          <div className="col-12 col-sm-6 col-lg-4 d-flex justify-content-center">
            <div className="flip-card">
              <div className="flip-card-inner">
                <div className="card flip-card-front shadow-sm">
                  <div className="card-body">
                    <img
                      src="/static/img/index/peluche-cinna-verano.webp"
                      alt="Peluche Cinnamoroll"
                    />
                  </div>
                </div>
                <div className="card flip-card-back shadow-sm">
                  <div className="card-body">
                    <h3 className="card-title">¡Nuevo!</h3>
                    <p className="card-text">
                      Peluche de Cinnamoroll, versión verano 2026.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-12 col-sm-6 col-lg-4 d-flex justify-content-center">
            <div className="flip-card">
              <div className="flip-card-inner">
                <div className="card flip-card-front shadow-sm">
                  <div className="card-body">
                    <img
                      src="/static/img/index/pochacco.webp"
                      alt="Figura Pochacco"
                    />
                  </div>
                </div>
                <div className="card flip-card-back shadow-sm">
                  <div className="card-body">
                    <h3 className="card-title">Un clásico.</h3>
                    <p className="card-text">
                      Bloques para armar una figura de Pochacco.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-12 col-sm-6 col-lg-4 d-flex justify-content-center">
            <div className="flip-card">
              <div className="flip-card-inner">
                <div className="card flip-card-front shadow-sm">
                  <div className="card-body">
                    <img
                      src="/static/img/index/blindbox-sanrio2.webp"
                      alt="Blindbox Sanrio"
                    />
                  </div>
                </div>
                <div className="card flip-card-back shadow-sm">
                  <div className="card-body">
                    <h3 className="card-title">¡Recién llegados!</h3>
                    <p className="card-text">
                      Nuevas blindbox de queridos personajes de Sanrio
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="container my-5">
        <div className="row align-items-center text-center">
          <div className="col-md-3 mb-3 mb-md-0">
            <img
              src="/static/img/index/gudetama1.png"
              alt="Gudetama"
              className="img-fluid rounded"
            />
          </div>
          <div className="col-md-6 mb-3 mb-md-0 sesion">
            {!sesion ? (
              <>
                <div className="mb-3">
                  <Link to="/login" className="btn shadow-effect">
                    <span className="h3 m-0 d-block">Inicia Sesión para comprar</span>
                  </Link>
                </div>
                <p className="crear-cuenta m-0">
                  <Link to="/registro" className="text-decoration-none">
                    O crea una cuenta
                  </Link>
                </p>
              </>
            ) : (
              <>
                <h2 className="mb-3">
                  ¡Qué bueno verte de nuevo,{" "}
                  <span className="fw-bold">{sesion.nombre}</span>! ✨
                </h2>
                <Link to="/productos" className="btn btn-primary shadow-effect">
                  <span className="h4 m-0 d-block">Explorar Catálogo</span>
                </Link>
              </>
            )}
          </div>
          <div className="col-md-3">
            <img
              src="/static/img/index/keroppi1.png"
              alt="Keroppi"
              className="img-fluid rounded"
            />
          </div>
        </div>
      </div>
    </>
  );
}
