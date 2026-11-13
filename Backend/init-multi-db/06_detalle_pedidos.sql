-- 05_detalle_pedidos.sql
CREATE TABLE detalle_pedidos (
    id_detalle SERIAL PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10) NOT NULL,
    CONSTRAINT fk_pedido
        FOREIGN KEY(id_pedido) 
        REFERENCES Pedidos(id_pedido)
        ON DELETE CASCADE,
    CONSTRAINT fk_producto
        FOREIGN KEY(id_producto) 
        REFERENCES Productos(id_producto)
        ON DELETE RESTRICT
);

-- Datos de prueba para Detalle_Pedidos (Coherentes con los nuevos productos)
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
