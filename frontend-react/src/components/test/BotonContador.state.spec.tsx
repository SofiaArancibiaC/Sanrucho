
// src/components/test/BotonContador.state.spec.tsx
//verifica que el estado(state) del componente cambie correctamente
// cuando el usuario interactúa con él (al hacer clic en el botón).
import { createRoot } from "react-dom/client";
import { act } from "react";
import BotonContador from "./BotonContador";

describe("PRUEBA 4 - Prueba de State en BotonContador", () => {
let container: HTMLDivElement;

beforeEach(() => {
    container = document.createElement("div");
    document.body.appendChild(container);
});

afterEach(() => {
    document.body.removeChild(container);
    container.innerHTML = "";
});

it("Debe iniciar con el contador en 0 (estado inicial)", () => {
    const root = createRoot(container);
    act(() => {
    root.render(<BotonContador />);
    });

    const valor = container.querySelector('[data-testid="contador-valor"]');
    expect(valor?.textContent).toBe("0");
});

it("Debe incrementar el contador al hacer click (cambio de state)", () => {
    const root = createRoot(container);
    act(() => {
    root.render(<BotonContador />);
    });

    const boton = container.querySelector('[data-testid="btn-incrementar"]') as HTMLButtonElement;
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