-- ============================================================
-- 06_detalle_pedidos.sql
-- Base de datos: detalle_pedidos
--
-- IMPORTANTE: id_pedido e id_producto son REFERENCIAS LOGICAS.
--   - pedidos esta en otra base de datos.
--   - productos pertenece a ms-catalogo.
-- Ninguna de las dos puede declararse como FOREIGN KEY en PostgreSQL.
-- La integridad se mantiene en PedidoService con validaciones y
-- compensaciones. precio_unitario congela el precio de compra.
-- ============================================================
DROP TABLE IF EXISTS detalle_pedidos CASCADE;

CREATE TABLE detalle_pedidos (
    id_detalle SERIAL PRIMARY KEY,
    id_pedido INT NOT NULL,   -- referencia logica a pedidos.id_pedido (otra BD)
    id_producto INT NOT NULL, -- referencia logica a productos.id_producto (ms-catalogo)
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10) NOT NULL
);

-- Datos de prueba para Detalle_Pedidos (coherentes con los productos)
-- Pedido 1 (Total = 25980): Peluche Hello Kitty (id=1, oferta 19990) + Cuaderno Hello Kitty (id=5, normal 5990)
INSERT INTO detalle_pedidos (id_pedido, id_producto, cantidad, precio_unitario) VALUES
(1, 1, 1, 19990),
(1, 5, 1, 5990);

-- Pedido 2 (Total = 13990): Cartera Badtz-Maru (id=7, normal 13990)
INSERT INTO detalle_pedidos (id_pedido, id_producto, cantidad, precio_unitario) VALUES
(2, 7, 1, 13990);

-- Pedido 3 (Total = 17990): Peluche Kuromi con Capucha (id=2, oferta 17990)
INSERT INTO detalle_pedidos (id_pedido, id_producto, cantidad, precio_unitario) VALUES
(3, 2, 1, 17990);
