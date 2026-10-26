// ============================================================
// context/CartContext.tsx
// Reemplaza a carrito.js. Expone el carrito, los totales y las
// operaciones (agregar / cambiar cantidad / eliminar / vaciar /
// finalizar). Al cambiar el estado, TODA la UI que use useCart()
// (badge del navbar, página del carrito) se actualiza sola.
// ============================================================

import { createContext, useContext, useEffect } from "react";
import type { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import type { ItemCarrito, Producto } from "../types";
import { SANRUCHO_KEYS } from "../storage/claves";
import { useColeccion } from "../storage/useColeccion";
import { alertaAviso, toast } from "../utils/alertas";
import { useAuth } from "./AuthContext";

interface CartContextValue {
  carrito: ItemCarrito[];
  totalItems: number;
  subtotal: number;
  envio: number;
  total: number;
  agregar: (producto: Producto, cantidad?: number) => void;
  cambiarCantidad: (codigo: string, delta: number) => void;
  eliminar: (codigo: string) => void;
  vaciar: () => void;
  irACheckout: () => void;
}

const CartContext = createContext<CartContextValue | null>(null);

export function CartProvider({ children }: { children: ReactNode }) {
  const { datos: carrito, guardar } = useColeccion<ItemCarrito>(SANRUCHO_KEYS.carrito);
  const { sesion } = useAuth();

  // El carrito pertenece a la sesión: si no hay usuario logueado
  // (por ejemplo, al cerrar sesión), se vacía automáticamente.
  const navigate = useNavigate();

  useEffect(() => {
    if (!sesion) guardar([]);
  }, [sesion, guardar]);

  const exigirSesion = (): boolean => {
    if (!sesion) {
      alertaAviso(
        "Inicia sesión",
        "Debes iniciar sesión para agregar productos al carrito o continuar con el proceso de pago."
      ).then(() => {
        window.location.href = "/login";
      });
      return false;
    }
    return true;
  };

  const agregar = (producto: Producto, cantidad = 1) => {
    if (!exigirSesion()) return;

    if (isNaN(cantidad) || cantidad <= 0) {
      alertaAviso("Cantidad inválida", "Por favor ingresa un número válido mayor a 0.");
      return;
    }
    if (cantidad > producto.stock) {
      alertaAviso("Stock insuficiente", `Solo quedan ${producto.stock} unidades disponibles.`);
      return;
    }

    const existente = carrito.find((i) => i.codigo === producto.codigo);
    let nuevoCarrito: ItemCarrito[];

    if (existente) {
      const nuevaCantidad = existente.cantidad + cantidad;
      if (nuevaCantidad > producto.stock) {
        alertaAviso(
          "Límite superado",
          "No puedes agregar más unidades del stock disponible."
        );
        return;
      }
      nuevoCarrito = carrito.map((i) =>
        i.codigo === producto.codigo ? { ...i, cantidad: nuevaCantidad } : i
      );
    } else {
      nuevoCarrito = [
        ...carrito,
        {
          codigo: producto.codigo,
          nombre: producto.nombre,
          precio: producto.precioOferta && producto.oferta ? producto.precioOferta : producto.precio,
          imagen: producto.imagen,
          cantidad,
        },
      ];
    }

    guardar(nuevoCarrito);
    toast(`¡${producto.nombre} agregado!`, "success");
  };

  const cambiarCantidad = (codigo: string, delta: number) => {
    const item = carrito.find((i) => i.codigo === codigo);
    if (!item) return;

    const nuevaCantidad = item.cantidad + delta;
    if (nuevaCantidad <= 0) {
      eliminar(codigo);
      return;
    }

    const productos = JSON.parse(
      localStorage.getItem(SANRUCHO_KEYS.productos) ?? "[]"
    ) as Producto[];
    const producto = productos.find((p) => p.codigo === codigo);
    if (producto && nuevaCantidad > producto.stock) {
      toast("No hay más stock disponible", "error");
      return;
    }

    guardar(
      carrito.map((i) => (i.codigo === codigo ? { ...i, cantidad: nuevaCantidad } : i))
    );
    toast("Cantidad actualizada", "info");
  };

  const eliminar = (codigo: string) => {
    guardar(carrito.filter((i) => i.codigo !== codigo));
    toast("Artículo eliminado del carrito", "info");
  };

  const vaciar = () => {
    guardar([]);
  };

  const irACheckout = () => {
    if (!exigirSesion()) return;
    if (carrito.length === 0) {
      toast("Tu carrito está vacío", "error");
      return;
    }

    const snapshot = {
      items: carrito,
      subtotal,
      envio,
      total,
      creadoEn: new Date().toISOString(),
    };

    localStorage.setItem(SANRUCHO_KEYS.checkoutSnapshot, JSON.stringify(snapshot));
    navigate("/checkout");
  };

  const totalItems = carrito.reduce((acc, i) => acc + i.cantidad, 0);
  const subtotal = carrito.reduce((acc, i) => acc + i.precio * i.cantidad, 0);
  const envio = subtotal === 0 || subtotal >= 30000 ? 0 : 3990;
  const total = subtotal + envio;

  return (
    <CartContext.Provider
      value={{
        carrito,
        totalItems,
        subtotal,
        envio,
        total,
        agregar,
        cambiarCantidad,
        eliminar,
        vaciar,
        irACheckout,
      }}
    >
      {children}
    </CartContext.Provider>
  );
}

export function useCart(): CartContextValue {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error("useCart debe usarse dentro de <CartProvider>");
  return ctx;
}
