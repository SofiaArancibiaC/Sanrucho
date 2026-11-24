// ============================================================
// BoletaDetalle.spec.tsx
// Verifica que la boleta muestre íntegramente el pedido en el DOM.
// ============================================================

import { act, type ReactElement } from "react";
import { createRoot, type Root } from "react-dom/client";
import type { Pedido } from "../../types";
import BoletaDetalle from "./BoletaDetalle";

(globalThis as unknown as { IS_REACT_ACT_ENVIRONMENT: boolean })
  .IS_REACT_ACT_ENVIRONMENT = true;

const PEDIDO: Pedido = {
  id: "1",
  codigoBoleta: "BOLETA-001",
  fechaCreacion: "2026-11-10T14:30:00",
  cliente: {
    nombre: "Camila",
    apellidos: "Soto",
    correo: "camila@gmail.com",
    telefono: "934567890",
    direccion: "Calle Falsa 123",
    fechaDespacho: "2026-11-15",
    horaDespacho: "10:30",
  },
  items: [
    { codigo: "HK-001", nombre: "Peluche Hello Kitty", precio: 19990, imagen: "", cantidad: 2 },
    { codigo: "HK-005", nombre: "Cuaderno Hello Kitty", precio: 5990, imagen: "", cantidad: 1 },
  ],
  subtotal: 45970,
  envio: 0,
  total: 45970,
  metodoPago: "tarjeta",
  tipoTarjeta: "debito",
  estadoPago: "aprobado",
};

describe("BoletaDetalle (componente)", () => {
  let contenedor: HTMLDivElement;
  let root: Root;

  const renderizar = (elemento: ReactElement) =>
    act(() => root.render(elemento));

  beforeEach(() => {
    contenedor = document.createElement("div");
    document.body.appendChild(contenedor);
    root = createRoot(contenedor);
  });

  afterEach(() => {
    act(() => root.unmount());
    contenedor.remove();
  });

  it("muestra el título, la boleta y el estado aprobado", () => {
    renderizar(<BoletaDetalle pedido={PEDIDO} titulo="Boleta BOLETA-001" />);
    expect(contenedor.querySelector("h1")?.textContent).toBe("Boleta BOLETA-001");
    expect(contenedor.textContent).toContain("BOLETA-001");
    expect(contenedor.textContent).toContain("PAGO APROBADO");
  });

  it("renderiza una fila por cada producto comprado", () => {
    renderizar(<BoletaDetalle pedido={PEDIDO} />);
    const filas = contenedor.querySelectorAll("tbody tr");
    expect(filas.length).toBe(2);
    expect(filas[0].textContent).toContain("Peluche Hello Kitty");
    expect(filas[1].textContent).toContain("Cuaderno Hello Kitty");
  });

  it("muestra los datos del cliente y del despacho", () => {
    renderizar(<BoletaDetalle pedido={PEDIDO} />);
    expect(contenedor.textContent).toContain("Camila Soto");
    expect(contenedor.textContent).toContain("camila@gmail.com");
    expect(contenedor.textContent).toContain("Calle Falsa 123");
    expect(contenedor.textContent).toContain("10:30");
  });

  it("cambia la insignia a rechazado cuando el pago no fue aprobado", () => {
    renderizar(<BoletaDetalle pedido={{ ...PEDIDO, estadoPago: "rechazado" }} />);
    expect(contenedor.textContent).toContain("PAGO RECHAZADO");
    expect(contenedor.textContent).not.toContain("PAGO APROBADO");
  });
});
