
// src/components/test/BotonContador.state.spec.tsx
// Prueba de STATE: verifica que el estado del componente cambie
// correctamente cuando el usuario hace clic en el botón.
import { act } from "react";
import { createRoot, type Root } from "react-dom/client";
import BotonContador from "./BotonContador";

// React 18 lo usa para reconocer que estamos dentro de act().
(globalThis as unknown as { IS_REACT_ACT_ENVIRONMENT: boolean })
.IS_REACT_ACT_ENVIRONMENT = true;

describe("PRUEBA 4 - Prueba de State en BotonContador", () => {
let container: HTMLDivElement;
let root: Root;

beforeEach(() => {
    container = document.createElement("div");
    document.body.appendChild(container);
    root = createRoot(container);
});

afterEach(() => {
    act(() => {
    root.unmount();
    });
    container.remove();
});

  // Renderiza dentro de act() para esperar a que React termine el pintado.
const renderizar = () => {
    act(() => {
    root.render(<BotonContador />);
    });
};

it("Debe iniciar con el contador en 0 (estado inicial)", () => {
    renderizar();

    const valor = container.querySelector('[data-testid="contador-valor"]');
    expect(valor?.textContent).toBe("0");
});

it("Debe incrementar el contador al hacer click (cambio de state)", () => {
    renderizar();

    const boton = container.querySelector(
    '[data-testid="btn-incrementar"]'
    ) as HTMLButtonElement;
    const valor = container.querySelector('[data-testid="contador-valor"]');

    expect(valor?.textContent).toBe("0");

    act(() => {
    boton.click();
    });
    expect(valor?.textContent).toBe("1");

    act(() => {
    boton.click();
    });
    expect(valor?.textContent).toBe("2");
});
});








