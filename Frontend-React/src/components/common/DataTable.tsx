// ============================================================
// components/common/DataTable.tsx
// Tabla reutilizable para los listados del panel de administración.
// Evita repetir el markup de <table> en cada página.
// ============================================================

import type { ReactNode } from "react";

export interface Columna<T> {
  encabezado: string;
  celda: (fila: T) => ReactNode;
  className?: string;
}

interface Props<T> {
  columnas: Columna<T>[];
  datos: T[];
  clave: (fila: T) => string;
  vacio?: string;
}

export default function DataTable<T>({
  columnas,
  datos,
  clave,
  vacio = "No hay registros para mostrar.",
}: Props<T>) {
  if (datos.length === 0) {
    return <div className="alert alert-info mb-0">{vacio}</div>;
  }

  return (
    <div className="table-responsive">
      <table className="table table-hover align-middle">
        <thead className="table-light">
          <tr>
            {columnas.map((col) => (
              <th key={col.encabezado} className={col.className}>
                {col.encabezado}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {datos.map((fila) => (
            <tr key={clave(fila)}>
              {columnas.map((col) => (
                <td key={col.encabezado} className={col.className}>
                  {col.celda(fila)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
