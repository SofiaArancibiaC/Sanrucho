-- 04_pedidos.sql
CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(10) NOT NULL,
    estado VARCHAR(50) DEFAULT 'Pendiente',
    CONSTRAINT fk_usuario
        FOREIGN KEY(id_usuario) 
        REFERENCES Usuarios(id_usuario)
        ON DELETE CASCADE
);

-- Datos de prueba para Pedidos
INSERT INTO pedidos (id_usuario, fecha_compra, total, estado) VALUES
(2, '2026-11-10 14:30:00', 38490, 'Pagado'),
(3, '2026-11-11 09:15:00', 12000, 'Enviado'),
(2, '2026-11-12 11:45:00', 15990, 'Pendiente');
