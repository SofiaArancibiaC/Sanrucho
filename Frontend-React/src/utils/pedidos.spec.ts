// ============================================================
// Pruebas unitarias de los filtros de boletas
// (src/utils/pedidos.ts)
// ============================================================

import type { Pedido } from "../types";
import {
  etiquetaMetodoPago,
  filtrarPedidos,
  pedidosDeUsuario,
  type FiltrosPedidos,
} from "./pedidos";

function crearPedido(cambios: Partial<Pedido> = {}): Pedido {
  return {
    id: "PED-00000001",
    codigoBoleta: "BOLETA-261025-AAAA",
    fechaCreacion: "2026-10-25T10:30:00.000Z",
    cliente: {
      nombre: "Camila",
      apellidos: "Soto Perez",
      correo: "camila@gmail.com",
      telefono: "+56912345678",
      direccion: "Los Aromos 789",
      fechaDespacho: "2026-10-26",
      horaDespacho: "15:00",
    },
    items: [
      {
        codigo: "HK-PLUSH-001",
        nombre: "Peluche Hello Kitty",
        precio: 10000,
        imagen: "",
        cantidad: 2,
      },
    ],
    subtotal: 20000,
    envio: 0,
    total: 20000,
    metodoPago: "tarjeta",
    tipoTarjeta: "debito",
    estadoPago: "aprobado",
    ...cambios,
  };
}

const filtros = (cambios: Partial<FiltrosPedidos> = {}): FiltrosPedidos => ({
  texto: "",
  estado: "aprobado",
  desde: "",
  hasta: "",
  ...cambios,
});

describe("pedidos", () => {
  describe("filtrarPedidos", () => {
    it("por defecto deja solo los pagos aprobados", () => {
      const aprobado = crearPedido();
      const rechazado = crearPedido({
        id: "PED-00000002",
        estadoPago: "rechazado",
      });

      expect(filtrarPedidos([aprobado, rechazado], filtros())).toEqual([
        aprobado,
      ]);
      expect(
        filtrarPedidos([aprobado, rechazado], filtros({ estado: "todos" }))
          .length
      ).toBe(2);
    });

    it("busca por boleta, número de pedido, cliente, correo y teléfono", () => {
      const pedido = crearPedido();
      const otros = [
        crearPedido({
          id: "PED-00000003",
          codigoBoleta: "BOLETA-261025-BBBB",
          cliente: {
            nombre: "Diego",
            apellidos: "Fernandez Rojas",
            correo: "diego@gmail.cl",
            telefono: "+56987654321",
            direccion: "Las Rosas 321",
            fechaDespacho: "2026-10-27",
            horaDespacho: "12:00",
          },
        }),
      ];

      expect(filtrarPedidos([pedido, ...otros], filtros({ texto: "AAAA" }))).toEqual([pedido]);
      expect(filtrarPedidos([pedido, ...otros], filtros({ texto: "00000001" }))).toEqual([pedido]);
      expect(filtrarPedidos([pedido, ...otros], filtros({ texto: "camila soto" }))).toEqual([pedido]);
      expect(filtrarPedidos([pedido, ...otros], filtros({ texto: "camila@gmail.com" }))).toEqual([pedido]);
      expect(filtrarPedidos([pedido, ...otros], filtros({ texto: "912345678" }))).toEqual([pedido]);
    });

    it("aplica el rango de fechas de forma inclusiva", () => {
      const antes = crearPedido({
        id: "PED-00000004",
        fechaCreacion: "2026-10-24T23:00:00.000Z",
      });
      const dentro = crearPedido({
        id: "PED-00000005",
        fechaCreacion: "2026-10-25T08:00:00.000Z",
      });

      const resultado = filtrarPedidos(
        [antes, dentro],
        filtros({ desde: "2026-10-25", hasta: "2026-10-25" })
      );

      expect(resultado).toEqual([dentro]);
    });

    it("ordena desde la compra más reciente a la más antigua", () => {
      const viejo = crearPedido({
        id: "PED-00000006",
        fechaCreacion: "2026-10-01T10:00:00.000Z",
      });
      const nuevo = crearPedido({
        id: "PED-00000007",
        fechaCreacion: "2026-10-20T10:00:00.000Z",
      });

      expect(
        filtrarPedidos([viejo, nuevo], filtros({ estado: "todos" })).map(
          (p) => p.id
        )
      ).toEqual(["PED-00000007", "PED-00000006"]);
    });
  });

  describe("pedidosDeUsuario", () => {
    it("asocia las compras por correo sin distinguir mayúsculas", () => {
      const delUsuario = crearPedido({
        cliente: {
          ...crearPedido().cliente,
          correo: "Camila@Gmail.com",
        },
      });
      const deOtro = crearPedido({
        id: "PED-00000008",
        cliente: {
          ...crearPedido().cliente,
          correo: "diego@gmail.cl",
        },
      });

      expect(
        pedidosDeUsuario([delUsuario, deOtro], " camila@gmail.com ")
      ).toEqual([delUsuario]);
      expect(pedidosDeUsuario([delUsuario], "")).toEqual([]);
    });
  });

  describe("etiquetaMetodoPago", () => {
    it("traduce el método de pago a un texto legible", () => {
      expect(etiquetaMetodoPago({ metodoPago: "webpay" })).toBe("Webpay");
      expect(
        etiquetaMetodoPago({ metodoPago: "tarjeta", tipoTarjeta: "debito" })
      ).toBe("Tarjeta de débito");
      expect(
        etiquetaMetodoPago({ metodoPago: "tarjeta", tipoTarjeta: "credito" })
      ).toBe("Tarjeta de crédito");
    });
  });
});
