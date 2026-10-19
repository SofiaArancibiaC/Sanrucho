// ============================================================
// Pruebas unitarias de las funciones de formato
// (src/utils/formato.ts)
// ============================================================

import { formatearPrecio, rutaImagenProducto } from "./formato";

describe("formato", () => {
  describe("formatearPrecio", () => {
    it("antepone el signo $ y usa el separador de miles", () => {
      expect(formatearPrecio(24990)).toBe("$24.990");
      expect(formatearPrecio(0)).toBe("$0");
    });
  });

  describe("rutaImagenProducto", () => {
    it("devuelve el logo cuando el producto no tiene imagen", () => {
      expect(rutaImagenProducto(null)).toBe("/static/img/logo-sanrucho.png");
      expect(rutaImagenProducto({ imagen: "" })).toBe(
        "/static/img/logo-sanrucho.png"
      );
    });

    it("respeta las rutas absolutas y arma la ruta de las relativas", () => {
      expect(rutaImagenProducto({ imagen: "/static/img/productos/x.webp" })).toBe(
        "/static/img/productos/x.webp"
      );
      expect(rutaImagenProducto({ imagen: "https://cdn.com/x.png" })).toBe(
        "https://cdn.com/x.png"
      );
      expect(rutaImagenProducto({ imagen: "productos/y.webp" })).toBe(
        "/static/img/productos/y.webp"
      );
    });
  });
});
