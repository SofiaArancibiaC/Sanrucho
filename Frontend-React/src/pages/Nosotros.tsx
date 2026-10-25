// ============================================================
// pages/Nosotros.tsx
// Reemplaza nosotros.html. Contenido estático (imágenes + enlaces).
// ============================================================

import { Link } from "react-router-dom";

export default function Nosotros() {
  return (
    <main className="my-5">
      <div className="sobre-nosotros">
        <img src="/static/img/nosotros/Hola.png" alt="Hola" />
        <h1>
          Somos un equipo de tres mujeres <br /> que aman Sanrio.
        </h1>
        <p>
          Este amor nos inspiró a hacer una tienda que pudiera extender el mismo
          sentimiento a más personas en Chile. <br />
          No nos limitamos a traer solo los personajes más conocidos, sino que
          queremos que todos puedan disfrutar de productos de sus personajes
          favoritos, <br /> por muy poco conocidos que sean.
        </p>
      </div>

      <div className="container-personajes my-5">
        <div className="row align-items-center text-center">
          <div className="col-md-3 mb-4 mb-md-0">
            <div className="avatar-circle maru">
              <img src="/static/img/nosotros/badtz-maru2.png" alt="Badtz Maru" />
            </div>
            <p>Nancy</p>
          </div>

          <div className="col-md-6 mb-4 mb-md-0">
            <div className="avatar-circle gudetama">
              <img src="/static/img/nosotros/gudetama2.png" alt="Gudetama" />
            </div>
            <p>Camila</p>
          </div>

          <div className="col-md-3 mb-4 mb-md-0">
            <div className="avatar-circle cinna">
              <img src="/static/img/nosotros/cinnamoroll1.png" alt="Cinnamoroll" />
            </div>
            <p>Sofía</p>
          </div>
        </div>
      </div>

      <div className="container my-4">
        <div className="row g-4">
          <aside className="col-md-6">
            <div className="info-card">
              <div className="card-header-custom">
                <span className="card-badge">
                  <i className="bi bi-geo-alt-fill"></i>
                </span>
                <h5 className="card-title-custom">Encuéntranos</h5>
              </div>

              <p className="card-description">
                Avenida Libertador Bernardo O'Higgins 123
              </p>

              <div className="social-wrapper">
                <img
                  src="/static/img/nosotros/instagram.png"
                  alt="Instagram"
                  className="social-icon"
                />
                <span>@sanrucho</span>
              </div>
            </div>
          </aside>

          <aside className="col-md-6">
            <div className="info-card contact-card">
              <div>
                <div className="card-header-custom">
                  <span className="card-badge">
                    <i className="bi bi-envelope-fill"></i>
                  </span>
                  <h5 className="card-title-custom">¿Necesitas contactarnos?</h5>
                </div>
                <p className="card-description">
                  Llena nuestro formulario y nos pondremos en contacto contigo.
                </p>
              </div>

              <div className="card-action">
                <Link to="/contacto" className="btn-custom">
                  Ir al formulario
                </Link>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </main>
  );
}
