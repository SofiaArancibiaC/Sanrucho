// ============================================================
// storage/claves.ts
// Nombres de las claves de localStorage. Es el equivalente
// exacto del objeto SANRUCHO_KEYS de storage.js.
//agrege checkoutSnapshot: "sanrucho_checkout_snapshot" : Guarda los productos, totales y fecha/hora en que se inicia el pago,
//ultimoPedido: "sanrucho_ultimo_pedido" :Guarda el pedido generado con éxito o con intento fallido para mostrarlo en ResumenCompra.,
// ============================================================

export const SANRUCHO_KEYS = {
  usuarios: "sanrucho_usuarios",
  productos: "sanrucho_productos",
  categorias: "sanrucho_categorias",
  regiones: "sanrucho_regiones",
  comunas: "sanrucho_comunas",
  roles: "sanrucho_roles",
  carrito: "sanrucho_carrito",
  contactos: "sanrucho_contactos",
  blog: "sanrucho_blog",
  resenas: "sanrucho_resenas",
  sesion: "sanrucho_sesion",
  checkoutSnapshot: "sanrucho_checkout_snapshot",
  ultimoPedido: "sanrucho_ultimo_pedido",
} as const;

export type ClaveColeccion = (typeof SANRUCHO_KEYS)[keyof typeof SANRUCHO_KEYS];

