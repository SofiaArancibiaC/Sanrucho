-- 05_detalle_pedidos.sql
CREATE TABLE Detalle_Pedidos (
    id_detalle SERIAL PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_pedido
        FOREIGN KEY(id_pedido) 
        REFERENCES Pedidos(id_pedido)
        ON DELETE CASCADE,
    CONSTRAINT fk_producto
        FOREIGN KEY(id_producto) 
        REFERENCES Productos(id_producto)
        ON DELETE RESTRICT
);

-- Datos de prueba para Detalle_Pedidos (Coherentes con el total de los pedidos)
-- Pedido 1 (id_pedido = 1, total = 38490): Mochila Kuromi (29990) + Taza Hello Kitty (8500)
INSERT INTO Detalle_Pedidos (id_pedido, id_producto, cantidad, precio_unitario) VALUES
(1, 3, 1, 29990.00),
(1, 2, 1, 8500.00);

-- Pedido 2 (id_pedido = 2, total = 12000): Cosmetiquero Kuromi (12000)
INSERT INTO Detalle_Pedidos (id_pedido, id_producto, cantidad, precio_unitario) VALUES
(2, 4, 1, 12000.00);

-- Pedido 3 (id_pedido = 3, total = 15990): Peluche Hello Kitty (15990)
INSERT INTO Detalle_Pedidos (id_pedido, id_producto, cantidad, precio_unitario) VALUES
(3, 1, 1, 15990.00);
