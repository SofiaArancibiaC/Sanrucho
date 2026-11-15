-- ============================================================
-- 05_pedidos.sql
-- Base de datos: pedidos
--
-- IMPORTANTE: id_usuario es una REFERENCIA LOGICA a la base de datos
-- "usuarios". No se declara FOREIGN KEY porque las FK de PostgreSQL no
-- pueden cruzar bases de datos. La integridad se valida en la capa de
-- servicio (o por el microservicio de usuarios en una etapa posterior).
-- ============================================================
DROP TABLE IF EXISTS pedidos CASCADE;

CREATE TABLE pedidos (
    id_pedido SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL, -- referencia logica a usuarios.id_usuario (otra BD, sin FK fisica)
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(10) NOT NULL,
    estado VARCHAR(50) DEFAULT 'Pendiente'
);

-- Datos de prueba para Pedidos.
-- Los totales coinciden con la suma de sus lineas en detalle_pedidos.
INSERT INTO pedidos (id_usuario, fecha_compra, total, estado) VALUES
(2, '2026-11-10 14:30:00', 25980, 'Pagado'),   -- producto 1 (19990) + producto 5 (5990)
(3, '2026-11-11 09:15:00', 13990, 'Enviado'),  -- producto 7 (13990)
(2, '2026-11-12 11:45:00', 17990, 'Pendiente'); -- producto 2 (17990)
