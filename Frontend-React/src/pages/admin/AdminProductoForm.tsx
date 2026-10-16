// ============================================================
// pages/admin/AdminProductoForm.tsx
// Reemplaza admin-producto-form.html + inicializarFormularioProducto.
// Si la URL trae "?codigo=", pasa a modo edición.
// ============================================================

import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useProductos } from "../../hooks/useProductos";
import { useTablasMaestras } from "../../hooks/useTablasMaestras";
import type { Producto } from "../../types";
import { alertaError, alertaExito } from "../../utils/alertas";
import { enteroEnRango, numeroEnRango, textoRequerido } from "../../utils/validaciones";

export default function AdminProductoForm() {
  const [parametros] = useSearchParams();
  const codigoOriginal = parametros.get("codigo") ?? "";
  const { datos, guardarProducto } = useProductos();
  const { categorias } = useTablasMaestras();
  const navigate = useNavigate();

  const enEdicion = codigoOriginal !== "";
  const existente = datos.find((p) => p.codigo === codigoOriginal);

  const [codigo, setCodigo] = useState(existente?.codigo ?? "");
  const [nombre, setNombre] = useState(existente?.nombre ?? "");
  const [descripcion, setDescripcion] = useState(existente?.descripcion ?? "");
  const [precio, setPrecio] = useState(existente ? String(existente.precio) : "");
  const [stock, setStock] = useState(existente ? String(existente.stock) : "");
  const [stockCritico, setStockCritico] = useState(
    existente?.stockCritico != null ? String(existente.stockCritico) : ""
  );
  const [categoriaId, setCategoriaId] = useState(
    existente ? String(existente.categoriaId) : ""
  );
  const [imagen, setImagen] = useState(existente?.imagen ?? "");
  const [estado, setEstado] = useState<Producto["estado"]>(existente?.estado ?? "Activo");

  const guardar = async (e: FormEvent) => {
    e.preventDefault();

    const codigoLimpio = codigo.trim();
    if (!textoRequerido(codigoLimpio, null) || codigoLimpio.length < 3) {
      await alertaError("Código inválido", "Mínimo 3 caracteres.");
      return;
    }
    const duplicado = datos.some(
      (p) => p.codigo === codigoLimpio && p.codigo !== codigoOriginal
    );
    if (duplicado) {
      await alertaError("Código duplicado", "Ya existe un producto con ese código.");
      return;
    }
    if (!textoRequerido(nombre, 100)) {
      await alertaError("Nombre inválido", "El nombre es obligatorio (máx. 100).");
      return;
    }
    if (descripcion.length > 500) {
      await alertaError("Descripción muy larga", "Máximo 500 caracteres.");
      return;
    }
    if (!numeroEnRango(precio, 0)) {
      await alertaError("Precio inválido", "Debe ser mayor o igual a 0.");
      return;
    }
    if (!enteroEnRango(stock, 0)) {
      await alertaError("Stock inválido", "Debe ser un entero mayor o igual a 0.");
      return;
    }
    if (stockCritico !== "" && !enteroEnRango(stockCritico, 0)) {
      await alertaError("Stock crítico inválido", "Debe ser un entero mayor o igual a 0.");
      return;
    }
    if (!categoriaId) {
      await alertaError("Falta la categoría", "Selecciona una categoría.");
      return;
    }

    const producto: Producto = {
      codigo: codigoLimpio,
      nombre: nombre.trim(),
      descripcion: descripcion.trim(),
      precio: Number(precio),
      stock: Number(stock),
      stockCritico: stockCritico !== "" ? Number(stockCritico) : null,
      categoriaId: Number(categoriaId),
      imagen: imagen.trim() || "logo-sanrucho.png",
      estado,
    };

    guardarProducto(producto);

    const aviso =
      producto.stockCritico != null && producto.stock <= producto.stockCritico
        ? " El stock está en o bajo el nivel crítico."
        : "";
    await alertaExito("Correcto", "Producto guardado correctamente." + aviso);
    navigate("/admin/productos");
  };

  return (
    <>
      <h1 className="fw-bold mb-4" style={{ color: "var(--primary)" }}>
        {enEdicion ? "Editar producto" : "Nuevo producto"}
      </h1>

      <form onSubmit={guardar} className="card p-4" noValidate>
        <div className="row g-3">
          <div className="col-md-4">
            <label htmlFor="codigoProducto" className="form-label">
              Código
            </label>
            <input
              type="text"
              className="form-control form-control-custom"
              id="codigoProducto"
              placeholder="Ej: SR-009"
              value={codigo}
              readOnly={enEdicion}
              onChange={(e) => setCodigo(e.target.value)}
            />
          </div>
          <div className="col-md-8">
            <label htmlFor="nombreProducto" className="form-label">
              Nombre
            </label>
            <input
              type="text"
              className="form-control form-control-custom"
              id="nombreProducto"
              maxLength={100}
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
            />
          </div>
          <div className="col-12">
            <label htmlFor="descripcionProducto" className="form-label">
              Descripción
            </label>
            <textarea
              className="form-control form-control-custom"
              id="descripcionProducto"
              rows={3}
              maxLength={500}
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
            ></textarea>
          </div>
          <div className="col-md-3">
            <label htmlFor="precioProducto" className="form-label">
              Precio
            </label>
            <input
              type="number"
              className="form-control form-control-custom"
              id="precioProducto"
              min={0}
              step={1}
              value={precio}
              onChange={(e) => setPrecio(e.target.value)}
            />
          </div>
          <div className="col-md-3">
            <label htmlFor="stockProducto" className="form-label">
              Stock
            </label>
            <input
              type="number"
              className="form-control form-control-custom"
              id="stockProducto"
              min={0}
              step={1}
              value={stock}
              onChange={(e) => setStock(e.target.value)}
            />
          </div>
          <div className="col-md-3">
            <label htmlFor="stockCriticoProducto" className="form-label">
              Stock crítico
            </label>
            <input
              type="number"
              className="form-control form-control-custom"
              id="stockCriticoProducto"
              min={0}
              step={1}
              value={stockCritico}
              onChange={(e) => setStockCritico(e.target.value)}
            />
          </div>
          <div className="col-md-3">
            <label htmlFor="categoriaProducto" className="form-label">
              Categoría
            </label>
            <select
              className="form-select form-control-custom"
              id="categoriaProducto"
              value={categoriaId}
              onChange={(e) => setCategoriaId(e.target.value)}
            >
              <option value="" disabled>
                Selecciona una categoría...
              </option>
              {categorias.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="col-md-8">
            <label htmlFor="imagenProducto" className="form-label">
              Imagen (ruta completa o dentro de static/img)
            </label>
            <input
              type="text"
              className="form-control form-control-custom"
              id="imagenProducto"
              placeholder="/static/img/productos/mi-producto.webp"
              value={imagen}
              onChange={(e) => setImagen(e.target.value)}
            />
          </div>
          <div className="col-md-4">
            <label htmlFor="estadoProducto" className="form-label">
              Estado
            </label>
            <select
              className="form-select form-control-custom"
              id="estadoProducto"
              value={estado}
              onChange={(e) => setEstado(e.target.value as Producto["estado"])}
            >
              <option value="Activo">Activo</option>
              <option value="Inactivo">Inactivo</option>
            </select>
          </div>
        </div>
        <div className="d-flex gap-2 mt-4">
          <button type="submit" className="btn-accent">
            Guardar
          </button>
          <Link to="/admin/productos" className="btn btn-outline-accent">
            Cancelar
          </Link>
        </div>
      </form>
    </>
  );
}
