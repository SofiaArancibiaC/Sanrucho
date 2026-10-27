// ============================================================
// PRUEBA DE EVENTO
// src/components/layout/Navbar.spec.tsx
//
// Simula un click real en el botón "Cerrar sesión" del Navbar y
// comprueba que se borre la sesión de localStorage y que la barra
// cambie del bloque de usuario al bloque de invitado.
// ============================================================

import { act, type ReactElement } from "react";
import { createRoot, type Root } from "react-dom/client";
import { MemoryRouter } from "react-router-dom";
import { AuthProvider } from "../../context/AuthContext";
import { CartProvider } from "../../context/CartContext";
import { SANRUCHO_KEYS } from "../../storage/claves";
import type { Usuario } from "../../types";
import Navbar from "./Navbar";

// React 18 lo usa para reconocer que estamos dentro de act().
(globalThis as unknown as { IS_REACT_ACT_ENVIRONMENT: boolean })
  .IS_REACT_ACT_ENVIRONMENT = true;

const USUARIO: Usuario = {
  run: "123456785",
  nombre: "Dueño",
  apellidos: "Sanrucho",
  correo: "dueno@gmail.com",
  password: "dueno123",
  fechaNacimiento: "1990-01-01",
  rolId: 1,
  regionId: 1,
  comunaId: 1,
  direccion: "Av. Principal 123",
  estado: "Activo",
};

describe("Navbar (evento click en Cerrar sesión)", () => {
  let contenedor: HTMLDivElement;
  let root: Root;

  const renderizar = (elemento: ReactElement) => {
    act(() => {
      root.render(elemento);
    });
  };

  beforeEach(() => {
    localStorage.setItem(SANRUCHO_KEYS.sesion, JSON.stringify(USUARIO));
    localStorage.setItem(SANRUCHO_KEYS.carrito, "[]");

    contenedor = document.createElement("div");
    document.body.appendChild(contenedor);
    root = createRoot(contenedor);
  });

  afterEach(() => {
    act(() => {
      root.unmount();
    });
    contenedor.remove();
    localStorage.removeItem(SANRUCHO_KEYS.sesion);
    localStorage.removeItem(SANRUCHO_KEYS.carrito);
  });

  it("al hacer click en 'Cerrar sesión' elimina la sesión y muestra al invitado", () => {
    renderizar(
      <MemoryRouter
        future={{ v7_startTransition: true, v7_relativeSplatPath: true }}
      >
        <AuthProvider>
          <CartProvider>
            <Navbar />
          </CartProvider>
        </AuthProvider>
      </MemoryRouter>
    );

    expect(contenedor.textContent).toContain("Hola, Dueño");

    const botonCerrarSesion = Array.from(
      contenedor.querySelectorAll("button")
    ).find((boton) => boton.textContent?.includes("Cerrar sesión"));

    expect(botonCerrarSesion).toBeDefined();

    act(() => {
      botonCerrarSesion!.click();
    });

    expect(localStorage.getItem(SANRUCHO_KEYS.sesion)).toBeNull();
    expect(contenedor.textContent).toContain("Iniciar sesión");
    expect(contenedor.textContent).not.toContain("Cerrar sesión");
  });
});
