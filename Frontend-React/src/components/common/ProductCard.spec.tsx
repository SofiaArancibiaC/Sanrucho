// ============================================================
// ProductCard.spec.tsx
// Pruebas de renderizado, estados de stock y evento "Agregar".
// ============================================================

import { act, type ReactElement } from "react";
import { createRoot, type Root } from "react-dom/client";
import { MemoryRouter } from "react-router-dom";
import { AuthProvider } from "../../context/AuthContext";
import { CartProvider } from "../../context/CartContext";
import { SANRUCHO_KEYS } from "../../storage/claves";
import type { Producto, Usuario } from "../../types";
import ProductCard from "./ProductCard";

(globalThis as unknown as { IS_REACT_ACT_ENVIRONMENT: boolean })
  .IS_REACT_ACT_ENVIRONMENT = true;

const SESION: Usuario = {
  idUsuario: 1,
  run: "11111111-1",
  nombre: "Admin",
  apellidos: "Sanrucho",
  correo: "admin@gmail.com",
  password: "",
  fechaNacimiento: "",
  rolId: 2,
  regionId: 1,
  comunaId: 1,
  direccion: "",
  estado: "Activo",
};

const PRODUCTO: Producto = {
  id: 5,
  codigo: "HK-CUAD-005",
  nombre: "Cuaderno Hello Kitty",
  descripcion: "Cuaderno A5 de rayas.",
  precio: 5990,
  stock: 10,
  stockCritico: 2,
  categoriaId: 1,
  imagen: "cuaderno.png",
  estado: "Activo",
};

describe("ProductCard (componente)", () => {
  let contenedor: HTMLDivElement;
  let root: Root;

  const renderizar = (elemento: ReactElement) =>
    act(() => {
      root.render(elemento);
    });

  const montar = (producto: Producto) =>
    renderizar(
      <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
        <AuthProvider>
          <CartProvider>
            <ProductCard producto={producto} />
          </CartProvider>
        </AuthProvider>
      </MemoryRouter>
    );

  beforeEach(() => {
    localStorage.setItem(SANRUCHO_KEYS.sesion, JSON.stringify(SESION));
    localStorage.setItem(SANRUCHO_KEYS.carrito, "[]");
    contenedor = document.createElement("div");
    document.body.appendChild(contenedor);
    root = createRoot(contenedor);
  });

  afterEach(() => {
    act(() => root.unmount());
    contenedor.remove();
    localStorage.removeItem(SANRUCHO_KEYS.sesion);
    localStorage.removeItem(SANRUCHO_KEYS.carrito);
  });

  it("muestra nombre, precio formateado y estado disponible", () => {
    montar(PRODUCTO);
    expect(contenedor.textContent).toContain("Cuaderno Hello Kitty");
    expect(contenedor.textContent).toContain("$5.990");
    expect(contenedor.textContent).toContain("Disponible");
    const boton = contenedor.querySelector("button") as HTMLButtonElement;
    expect(boton.disabled).toBeFalse();
  });

  it("deshabilita el botón y muestra Agotado cuando no hay stock", () => {
    montar({ ...PRODUCTO, stock: 0 });
    expect(contenedor.textContent).toContain("Agotado");
    const boton = contenedor.querySelector("button") as HTMLButtonElement;
    expect(boton.disabled).toBeTrue();
  });

  it("muestra la insignia de oferta y el precio anterior", () => {
    montar({ ...PRODUCTO, oferta: true, precioOferta: 3990 });
    expect(contenedor.textContent).toContain("Oferta");
    expect(contenedor.textContent).toContain("$3.990");
    expect(contenedor.textContent).toContain("$5.990");
  });

  it("al hacer click en Agregar guarda el producto en el carrito", () => {
    montar(PRODUCTO);
    const boton = Array.from(contenedor.querySelectorAll("button")).find((b) =>
      b.textContent?.includes("Agregar al carrito")
    ) as HTMLButtonElement;

    act(() => boton.click());

    const carrito = JSON.parse(
      localStorage.getItem(SANRUCHO_KEYS.carrito) ?? "[]"
    ) as { codigo: string; cantidad: number }[];
    expect(carrito.length).toBe(1);
    expect(carrito[0].codigo).toBe("HK-CUAD-005");
    expect(carrito[0].cantidad).toBe(1);
  });
});
