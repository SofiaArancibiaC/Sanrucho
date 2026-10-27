
// src/components/test/BadgeOferta.props.spec.tsx
//es un inspector automático que simula el renderizado
//con datos de prueba para asegurar que el componente funcione bien antes de llevarlo a producción.
import { createRoot } from "react-dom/client";
import BadgeOferta from "./BadgeOferta";

describe("PRUEBA 3 - Prueba de Props en BadgeOferta", () => {
let container: HTMLDivElement;

beforeEach(() => {
    container = document.createElement("div");
    document.body.appendChild(container);
});

afterEach(() => {
    document.body.removeChild(container);
    container.innerHTML = "";
});

it("Debe mostrar el texto recibido por props correctamente", () => {
    const root = createRoot(container);
    root.render(<BadgeOferta texto="OFERTA" />);

    const badge = container.querySelector('[data-testid="badge-oferta"]');
    expect(badge).not.toBeNull();
    expect(badge?.textContent).toContain("OFERTA");
});

it("Debe mostrar el descuento recibido por props junto al texto", () => {
    const root = createRoot(container);
    root.render(<BadgeOferta texto="OFERTA" descuento={20} />);

    const badge = container.querySelector('[data-testid="badge-oferta"]');
    expect(badge?.textContent).toContain("OFERTA");
    expect(badge?.textContent).toContain("-20%");
});
});

//Su trabajo es ejecutar el componente de forma aislada
//  para comprobar que funciona correctamente y que muestra las props tal como fue programado.
//