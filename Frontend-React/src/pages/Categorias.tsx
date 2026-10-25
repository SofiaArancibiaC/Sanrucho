import { Link } from "react-router-dom";
import { useTablasMaestras } from "../hooks/useTablasMaestras";

export default function Categorias() {
  const { categorias } = useTablasMaestras();

  return (
    <div className="container my-5">
      <h2 className="mb-4">Categorías</h2>
      <div className="row g-3">
        {categorias.map((c) => (
          <div key={c.id} className="col-6 col-md-4 col-lg-3">
            <Link
              to={`/productos?cat=${c.id}`}
              className="card text-decoration-none h-100 shadow-sm"
            >
              <div className="card-body text-center">
                <i className="bi bi-tags fs-2 d-block mb-2"></i>
                <h5 className="card-title mb-0">{c.nombre}</h5>
              </div>
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}