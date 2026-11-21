-- 05_pedidos.sql
-- Microservicio de Pedidos (PostgreSQL)
-- id_usuario es una referencia lógica al microservicio de Usuarios (SIN FOREIGN KEY física)

CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL, -- Referencia lógica a usuarios.id_usuario
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(10) NOT NULL,
    estado VARCHAR(50) DEFAULT 'Pendiente'
);

-- Datos de prueba para Pedidos (totales coherentes con detalle_pedidos)
INSERT INTO pedidos (id_usuario, fecha_compra, total, estado) VALUES
(3, '2026-11-10 14:30:00', 25980, 'Pagado'),
(4, '2026-11-11 09:15:00', 13990, 'Enviado'),
(3, '2026-11-12 11:45:00', 17990, 'Pendiente');