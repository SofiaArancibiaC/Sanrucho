import { useMemo } from "react";
import { useProductos } from "../hooks/useProductos";
import ProductCard from "../components/common/ProductCard";

export default function Ofertas() {
  const { activos } = useProductos();
  const ofertas = useMemo(() => activos.filter((p) => p.oferta), [activos]);

  return (
    <main className="container my-5">
      <h2 className="fw-bold mb-4">Ofertas</h2>
      <div className="row">
        {ofertas.length === 0 ? (
          <div className="col-12">
            <div className="alert alert-info">No hay ofertas disponibles.</div>
          </div>
        ) : (
          ofertas.map((producto) => (
            <ProductCard key={producto.codigo} producto={producto} />
          ))
        )}
      </div>
    </main>
  );
}
