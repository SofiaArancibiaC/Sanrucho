/*verifica el renderizado correcto de la página "Nosotros".
- Comprueba: 
- Existe un elemento <h1> en el DOM
- El h1 contiene el texto "Somos un equipo de tres mujeres"
- Existe un elemento <p> en el DOM
- El p contiene el texto "Este amor nos inspiró"
*/

import { act } from "react";
import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import Nosotros from "./Nosotros";

describe("Nosotros (render)", () => {
  let container: HTMLDivElement;
  let root: ReactDOM.Root;

  beforeEach(() => {
    container = document.createElement("div");
    document.body.appendChild(container);
    root = ReactDOM.createRoot(container);
  });

  afterEach(() => {
    root.unmount();
    document.body.removeChild(container);
  });

  it("debe renderizar el título y la descripción", () => {
    act(() => {
      root.render(
        <BrowserRouter>
          <Nosotros />
        </BrowserRouter>
      );
    });

    const h1 = container.querySelector("h1");
    expect(h1).toBeTruthy();
    expect(h1?.textContent).toContain("Somos un equipo de tres mujeres");

    const p = container.querySelector("p");
    expect(p).toBeTruthy();
    expect(p?.textContent).toContain("Este amor nos inspiró");
  });

  it("debe renderizar las tres avatares del equipo", () => {
    act(() => {
      root.render(
        <BrowserRouter>
          <Nosotros />
        </BrowserRouter>
      );
    });

    expect(container.querySelector('img[alt="Badtz Maru"]')).toBeTruthy();
    expect(container.querySelector('img[alt="Gudetama"]')).toBeTruthy();
    expect(container.querySelector('img[alt="Cinnamoroll"]')).toBeTruthy();
  });

  it("debe renderizar el enlace al formulario", () => {
    act(() => {
      root.render(
        <BrowserRouter>
          <Nosotros />
        </BrowserRouter>
      );
    });

    const a = container.querySelector("a");
    expect(a).toBeTruthy();
    expect(a?.textContent).toContain("Ir al formulario");
  });
});