// ============================================================
// pedidos.spec.ts
// Pruebas del adaptador entre el DTO del backend y el tipo Pedido.
// ============================================================

import { adaptarPedido, estadoPagoDeBackend } from "./pedidos";

describe("adaptador de pedidos", () => {
  const resumen = {
    id: 7,
    idUsuario: 3,
    fechaCompra: "2026-11-10T14:30:00",
    total: 25980,
    estado: "Pagado",
  };

  it("traduce los estados del backend a aprobado/rechazado", () => {
    expect(estadoPagoDeBackend("Pagado")).toBe("aprobado");
    expect(estadoPagoDeBackend("Enviado")).toBe("aprobado");
    expect(estadoPagoDeBackend("Pendiente")).toBe("aprobado");
    expect(estadoPagoDeBackend("Cancelado")).toBe("rechazado");
  });

  it("convierte un resumen sin detalles en un Pedido válido", () => {
    const pedido = adaptarPedido(resumen);
    expect(pedido.id).toBe("7");
    expect(pedido.codigoBoleta).toBe("B-000007");
    expect(pedido.fechaCreacion).toBe("2026-11-10T14:30:00");
    expect(pedido.total).toBe(25980);
    expect(pedido.subtotal).toBe(25980);
    expect(pedido.estadoPago).toBe("aprobado");
    expect(pedido.items).toEqual([]);
  });

  it("convierte los detalles del backend en items con nombre y precio", () => {
    const pedido = adaptarPedido(resumen, {
      ...resumen,
      detalles: [
        { idProducto: 1, nombreProducto: "Peluche", cantidad: 1, precioUnitario: 19990 },
        { idProducto: 5, nombreProducto: "Cuaderno", cantidad: 1, precioUnitario: 5990 },
      ],
    });
    expect(pedido.items.length).toBe(2);
    expect(pedido.items[0].nombre).toBe("Peluche");
    expect(pedido.items[0].precio).toBe(19990);
    expect(pedido.subtotal).toBe(25980);
  });

  it("usa datos del cliente cuando el backend los entrega", () => {
    const pedido = adaptarPedido(resumen, null, {
      idUsuario: 3,
      nombre: "Camila",
      apellidos: "Soto",
      email: "camila@gmail.com",
    });
    expect(pedido.cliente.nombre).toBe("Camila");
    expect(pedido.cliente.correo).toBe("camila@gmail.com");
  });
});
