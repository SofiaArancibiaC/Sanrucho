

// src/components/test/BadgeOferta.props.spec.tsx
// Prueba de PROPS: renderiza el componente con datos de prueba y verifica
// que el texto y el descuento recibidos por props se muestren en pantalla.
import { act, type ReactElement } from "react";
import { createRoot, type Root } from "react-dom/client";
import BadgeOferta from "./BadgeOferta";

// React 18 lo usa para reconocer que estamos dentro de act().
(globalThis as unknown as { IS_REACT_ACT_ENVIRONMENT: boolean })
.IS_REACT_ACT_ENVIRONMENT = true;

describe("PRUEBA 3 - Prueba de Props en BadgeOferta", () => {
let container: HTMLDivElement;
let root: Root;

  // Se ejecuta antes de cada prueba: crea el contenedor y la raíz de React.
beforeEach(() => {
    container = document.createElement("div");
    document.body.appendChild(container);
    root = createRoot(container);
});

  // Se ejecuta después de cada prueba: limpia React y el DOM.
afterEach(() => {
    act(() => {
    root.unmount();
    });
    container.remove();
});

  // Renderiza dentro de act() para que React termine de pintar
  // antes de hacer las aserciones (evita el error de "null").
const renderizar = (elemento: ReactElement) => {
    act(() => {
    root.render(elemento);
    });
};

it("Debe mostrar el texto recibido por props correctamente", () => {
    renderizar(<BadgeOferta texto="OFERTA" />);

    const badge = container.querySelector('[data-testid="badge-oferta"]');
    expect(badge).not.toBeNull();
    expect(badge?.textContent).toContain("OFERTA");
});

it("Debe mostrar el descuento recibido por props junto al texto", () => {
    renderizar(<BadgeOferta texto="OFERTA" descuento={20} />);

    const badge = container.querySelector('[data-testid="badge-oferta"]');
    expect(badge).not.toBeNull();
    expect(badge?.textContent).toContain("OFERTA");
    expect(badge?.textContent).toContain("-20%");
});
});

// La prueba aísla el componente y comprueba que muestra las props
// tal como fueron programadas.



