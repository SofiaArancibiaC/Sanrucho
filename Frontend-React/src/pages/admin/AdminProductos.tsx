// ============================================================
// pages/admin/AdminProductos.tsx
// Reemplaza admin-productos.html + la parte admin de productos.js.
// ============================================================

import { Link, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import { useProductos } from "../../hooks/useProductos";
import { useTablasMaestras } from "../../hooks/useTablasMaestras";
import DataTable, { type Columna } from "../../components/common/DataTable";
import type { Producto } from "../../types";
import { formatearPrecio, rutaImagenProducto } from "../../utils/formato";
import { toast } from "../../utils/alertas";

export default function AdminProductos() {
  const { datos, eliminarProducto } = useProductos();
  const { obtenerNombreCategoria } = useTablasMaestras();
  const navigate = useNavigate();

  const verProducto = (p: Producto) => {
    Swal.fire({
      title: p.nombre,
      html:
        `<img src="${rutaImagenProducto(p)}" class="img-fluid mb-3" style="max-height:160px;"><br>` +
        `<p class="text-start"><strong>Código:</strong> ${p.codigo}<br>` +
        `<strong>Categoría:</strong> ${obtenerNombreCategoria(p.categoriaId)}<br>` +
        `<strong>Precio:</strong> ${formatearPrecio(p.precio)}<br>` +
        `<strong>Stock:</strong> ${p.stock}<br>` +
        `<strong>Stock crítico:</strong> ${p.stockCritico != null ? p.stockCritico : "-"}<br>` +
        `<strong>Descripción:</strong> ${p.descripcion || "-"}</p>`,
      confirmButtonText: "Cerrar",
    });
  };

  const confirmarEliminar = async (p: Producto) => {
    const resultado = await Swal.fire({
      title: "¿Eliminar producto?",
      text: "Esta acción no se puede deshacer.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Eliminar",
      cancelButtonText: "Cancelar",
    });
    if (!resultado.isConfirmed) return;
    eliminarProducto(p.codigo);
    toast("Producto eliminado correctamente", "success");
  };

  const columnas: Columna<Producto>[] = [
    {
      encabezado: "Código",
      celda: (p) => <strong>{p.codigo}</strong>,
    },
    {
      encabezado: "Imagen",
      celda: (p) => (
        <img
          src={rutaImagenProducto(p)}
          alt={p.nombre}
          height={40}
          style={{ objectFit: "contain", borderRadius: 6 }}
        />
      ),
    },
    { encabezado: "Nombre", celda: (p) => p.nombre },
    { encabezado: "Categoría", celda: (p) => obtenerNombreCategoria(p.categoriaId) },
    { encabezado: "Precio", celda: (p) => formatearPrecio(p.precio) },
    {
      encabezado: "Stock",
      celda: (p) => {
        const critico = p.stockCritico != null && p.stock <= p.stockCritico;
        return (
          <span className={critico ? "text-danger fw-bold" : ""}>
            {p.stock}
            {critico && <i className="bi bi-exclamation-triangle-fill ms-1"></i>}
          </span>
        );
      },
    },
    {
      encabezado: "Estado",
      celda: (p) => (
        <span className={`badge ${p.estado === "Activo" ? "bg-success" : "bg-secondary"}`}>
          {p.estado}
        </span>
      ),
    },
    {
      encabezado: "Acciones",
      className: "text-nowrap",
      celda: (p) => (
        <div className="d-flex gap-1">
          <button
            type="button"
            className="btn btn-sm btn-outline-accent"
            title="Ver"
            onClick={() => verProducto(p)}
          >
            <i className="bi bi-eye"></i>
          </button>
          <button
            type="button"
            className="btn btn-sm btn-outline-primary"
            title="Editar"
            onClick={() =>
              navigate(`/admin/productos/form?codigo=${encodeURIComponent(p.codigo)}`)
            }
          >
            <i className="bi bi-pencil"></i>
          </button>
          <button
            type="button"
            className="btn btn-sm btn-outline-danger"
            title="Eliminar"
            onClick={() => confirmarEliminar(p)}
          >
            <i className="bi bi-trash"></i>
          </button>
        </div>
      ),
    },
  ];

  return (
    <>
      <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4 pb-2 border-bottom">
        <h1 className="fw-bold m-0" style={{ color: "var(--primary)" }}>
          Productos
        </h1>
        <Link to="/admin/productos/form" className="btn-accent text-decoration-none">
          <i className="bi bi-plus-lg"></i> Nuevo producto
        </Link>
      </div>

      <div className="table-wrapper">
        <DataTable
          columnas={columnas}
          datos={datos}
          clave={(p) => p.codigo}
          vacio="No hay productos registrados."
        />
      </div>
    </>
  );
}
