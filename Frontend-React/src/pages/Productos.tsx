// ============================================================
// pages/Productos.tsx
// Reemplaza productos.html + productos.js. Los filtros son estado
// de React (useState): al cambiar, la lista se recalcula sola.
// ============================================================

import { useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { useProductos } from "../hooks/useProductos";
import { useTablasMaestras } from "../hooks/useTablasMaestras";
import ProductCard from "../components/common/ProductCard";
import { formatearPrecio } from "../utils/formato";

const PRECIO_MAXIMO = 100000;

export default function Productos() {
  const { activos } = useProductos();
  const { categorias } = useTablasMaestras();

  const [searchParams] = useSearchParams();
  const [busqueda, setBusqueda] = useState("");
  const [categoriaId, setCategoriaId] = useState(Number(searchParams.get("cat") || 0));
  const [precioMaximo, setPrecioMaximo] = useState(PRECIO_MAXIMO);

  const filtrados = useMemo(() => {
    const texto = busqueda.trim().toLowerCase();
    return activos.filter((p) => {
      const coincideTexto =
        texto === "" ||
        p.nombre.toLowerCase().includes(texto) ||
        p.codigo.toLowerCase().includes(texto);
      const coincideCategoria =
        categoriaId === 0 || p.categoriaId === categoriaId;
      const coincidePrecio = p.precio <= precioMaximo;
      return coincideTexto && coincideCategoria && coincidePrecio;
    });
  }, [activos, busqueda, categoriaId, precioMaximo]);

  const limpiarFiltros = () => {
    setBusqueda("");
    setCategoriaId(0);
    setPrecioMaximo(PRECIO_MAXIMO);
  };

  return (
    <main className="container my-5">
      <div className="row">
        <aside className="col-lg-3 mb-4">
          <div className="card p-3 shadow-sm border-0 filter-card">
            <h5 className="fw-bold mb-3">Filtros</h5>

            <div className="mb-3">
              <label htmlFor="buscarInput" className="form-label fw-semibold">
                Buscar
              </label>
              <input
                type="text"
                id="buscarInput"
                className="form-control"
                placeholder="Nombre o código..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="filtroCategoria" className="form-label fw-semibold">
                Categoría
              </label>
              <select
                id="filtroCategoria"
                className="form-select"
                value={categoriaId}
                onChange={(e) => setCategoriaId(Number(e.target.value))}
              >
                <option value={0}>Todas las categorías</option>
                {categorias.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.nombre}
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-3">
              <label
                htmlFor="filtroPrecio"
                className="form-label fw-semibold d-flex justify-content-between"
              >
                <span>Precio máximo:</span>
                <span className="fw-bold text-primary">
                  {formatearPrecio(precioMaximo)}
                </span>
              </label>
              <input
                type="range"
                className="form-range"
                id="filtroPrecio"
                min={0}
                max={PRECIO_MAXIMO}
                step={1000}
                value={precioMaximo}
                onChange={(e) => setPrecioMaximo(Number(e.target.value))}
              />
            </div>

            <button
              type="button"
              className="btn btn-outline-secondary w-100 btn-sm"
              onClick={limpiarFiltros}
            >
              Limpiar Filtros
            </button>
          </div>
        </aside>

        <section className="col-lg-9">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h4 className="fw-bold mb-0">Catálogo</h4>
            <span className="text-muted small">
              Mostrando {filtrados.length} productos
            </span>
          </div>

          <div className="row">
            {filtrados.length === 0 ? (
              <div className="col-12">
                <div className="alert alert-info">
                  No se encontraron productos con esos filtros.
                </div>
              </div>
            ) : (
              filtrados.map((producto) => (
                <ProductCard key={producto.codigo} producto={producto} />
              ))
            )}
          </div>
        </section>
      </div>
    </main>
  );
}
