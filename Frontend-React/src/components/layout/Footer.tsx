// ============================================================
// components/layout/Footer.tsx
// Reemplaza _footer.html. Contenido estático; solo los enlaces
// cambian de <a href> a <Link to>.
// ============================================================

import { Link } from "react-router-dom";

export default function Footer() {
  return (
    <div style={{ backgroundColor: "rgba(255, 255, 255, 0.75)" }}>
      <footer>
        <div className="text-center">
          <div style={{ backgroundColor: "rgba(95, 190, 213, 0.927)" }}>
            <p className="cherry-bomb-one-regular mb-2">
              Inspirados por lo cute, impulsados por el código 💻🎀
            </p>
          </div>
        </div>

        <div className="container pt-3 pb-4">
          <section className="text-center">
            <div className="row justify-content-center align-items-center">
              <div className="col-md-3">
                <img src="/static/img/cinnamon.png" style={{ width: 90 }} alt="Cinnamoroll" />
              </div>
              <div className="col-md-3">
                <img src="/static/img/Gudetama.png" style={{ width: 90 }} alt="Gudetama" />
              </div>
              <div className="col-md-3">
                <img src="/static/img/badz_maru.png" style={{ width: 90 }} alt="Badtz Maru" />
              </div>
            </div>

            <div className="row text-center justify-content-center mt-3">
              <div className="col-md-2">
                <h6 className="text-uppercase fw-bold mb-0">
                  <Link to="/nosotros" className="text-black">
                    Nosotros
                  </Link>
                </h6>
              </div>
              <div className="col-md-2">
                <h6 className="text-uppercase fw-bold mb-0">
                  <Link to="/productos" className="text-black">
                    Productos
                  </Link>
                </h6>
              </div>
              <div className="col-md-2">
                <h6 className="text-uppercase fw-bold mb-0">
                  <Link to="/blog" className="text-black">
                    Blog
                  </Link>
                </h6>
              </div>
              <div className="col-md-2">
                <h6 className="text-uppercase fw-bold mb-0">
                  <Link to="/contacto" className="text-black">
                    Contacto
                  </Link>
                </h6>
              </div>
            </div>

            <div className="text-center mt-3 mb-3">
              <a href="#" className="text-black me-4" aria-label="Facebook">
                <i className="bi bi-facebook"></i>
              </a>
              <a href="#" className="text-black me-4" aria-label="Twitter">
                <i className="bi bi-twitter"></i>
              </a>
              <a href="#" className="text-black me-4" aria-label="Instagram">
                <i className="bi bi-instagram"></i>
              </a>
              <a href="#" className="text-black me-4" aria-label="Github">
                <i className="bi bi-github"></i>
              </a>
            </div>
          </section>

          <div
            className="text-center p-3"
            style={{ backgroundColor: "rgba(227, 218, 140, 0.927)" }}
          >
            Parecidos, pero legalmente distintos™
          </div>
        </div>
      </footer>
    </div>
  );
}
