// ============================================================
// Pruebas unitarias de las funciones de validación
// (src/utils/validaciones.ts)
//
// Jasmine se estructura con:
//   describe("grupo", () => { it("caso", () => { expect(...)... }) })
// ============================================================

import {
  validarCorreo,
  validarPassword,
  validarRun,
} from "./validaciones";

describe("validaciones", () => {
  describe("validarCorreo", () => {
    it("acepta correos con dominios permitidos (@duoc.cl, @profesor.duoc.cl, @gmail.com)", () => {
      expect(validarCorreo("alumno@duoc.cl")).toBe(true);
      expect(validarCorreo("profesor@profesor.duoc.cl")).toBe(true);
      expect(validarCorreo("usuario@gmail.com")).toBe(true);
    });

    it("rechaza correos con dominios no permitidos", () => {
      expect(validarCorreo("usuario@hotmail.com")).toBe(false);
      expect(validarCorreo("usuario@outlook.cl")).toBe(false);
    });

    it("rechaza correos vacíos o de más de 100 caracteres", () => {
      expect(validarCorreo("")).toBe(false);

      const correoLargo = "a".repeat(95) + "@gmail.com"; // 105 caracteres
      expect(correoLargo.length).toBeGreaterThan(100);
      expect(validarCorreo(correoLargo)).toBe(false);
    });
  });

  describe("validarPassword", () => {
    it("acepta contraseñas de entre 4 y 10 caracteres", () => {
      expect(validarPassword("1234")).toBe(true);
      expect(validarPassword("abcdef")).toBe(true);
      expect(validarPassword("1234567890")).toBe(true);
    });

    it("rechaza contraseñas con menos de 4 o más de 10 caracteres", () => {
      expect(validarPassword("123")).toBe(false);
      expect(validarPassword("12345678901")).toBe(false);
    });
  });

  describe("validarRun", () => {
    it("acepta un RUN con dígito verificador correcto", () => {
      // 12345678-5 es un RUN válido según el algoritmo módulo 11.
      expect(validarRun("123456785")).toBe(true);
    });

    it("rechaza un RUN con dígito verificador incorrecto o formato inválido", () => {
      expect(validarRun("123456784")).toBe(false); // el DV correcto es 5
      expect(validarRun("123")).toBe(false); // demasiado corto
    });
  });
});
