// ============================================================
// PRUEBA DE COMPONENTE
// src/components/common/DataTable.spec.tsx
//
// Verifica que el componente reutilizable DataTable renderice los
// encabezados, una fila por cada dato y el mensaje de "sin datos".
// ============================================================

import { act, type ReactElement } from "react";
import { createRoot, type Root } from "react-dom/client";
import DataTable, { type Columna } from "./DataTable";

// React 18 lo usa para reconocer que estamos dentro de act().
(globalThis as unknown as { IS_REACT_ACT_ENVIRONMENT: boolean })
  .IS_REACT_ACT_ENVIRONMENT = true;

interface Fila {
  id: number;
  nombre: string;
}

const columnas: Columna<Fila>[] = [
  { encabezado: "Código", celda: (fila) => fila.id },
  { encabezado: "Nombre", celda: (fila) => <strong>{fila.nombre}</strong> },
];

const filas: Fila[] = [
  { id: 1, nombre: "Peluche Hello Kitty" },
  { id: 2, nombre: "Taza Pompompurin" },
];

describe("DataTable (componente)", () => {
  let contenedor: HTMLDivElement;
  let root: Root;

  const renderizar = (elemento: ReactElement) => {
    act(() => {
      root.render(elemento);
    });
  };

  beforeEach(() => {
    contenedor = document.createElement("div");
    document.body.appendChild(contenedor);
    root = createRoot(contenedor);
  });

  afterEach(() => {
    act(() => {
      root.unmount();
    });
    contenedor.remove();
  });

  it("renderiza los encabezados y una fila por cada dato", () => {
    renderizar(
      <DataTable columnas={columnas} datos={filas} clave={(fila) => String(fila.id)} />
    );

    const encabezados = Array.from(
      contenedor.querySelectorAll("thead th")
    ).map((th) => th.textContent);

    expect(encabezados).toEqual(["Código", "Nombre"]);
    expect(contenedor.querySelectorAll("tbody tr").length).toBe(2);
    expect(contenedor.textContent).toContain("Peluche Hello Kitty");
    expect(contenedor.textContent).toContain("Taza Pompompurin");
  });

  it("muestra el mensaje configurado cuando no hay datos", () => {
    renderizar(
      <DataTable
        columnas={columnas}
        datos={[]}
        clave={(fila) => String(fila.id)}
        vacio="Todavía no hay registros"
      />
    );

    expect(contenedor.textContent).toContain("Todavía no hay registros");
    expect(contenedor.querySelector("table")).toBeNull();
  });
});
