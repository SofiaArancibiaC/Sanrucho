-- ============================================================
-- 07_transacciones.sql
-- Base de datos: transacciones
-- Esquema para el futuro microservicio de Pagos.
-- id_pedido es una referencia logica (sin FK fisica entre BD).
-- ============================================================
DROP TABLE IF EXISTS transacciones CASCADE;

CREATE TABLE transacciones (
    id_transaccion SERIAL PRIMARY KEY,
    id_pedido INT NOT NULL, -- referencia logica a pedidos.id_pedido (otra BD)
    monto NUMERIC(10) NOT NULL,
    pasarela VARCHAR(50) NOT NULL,
    estado VARCHAR(50) DEFAULT 'Pendiente',
    codigo_autorizacion VARCHAR(100),
    fecha_transaccion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos de prueba para Transacciones
INSERT INTO transacciones (id_pedido, monto, pasarela, estado, codigo_autorizacion) VALUES
(1, 25980, 'Webpay Plus', 'Aprobado', 'AUTH-998877'),
(2, 13990, 'MercadoPago', 'Aprobado', 'MP-554433'),
(3, 17990, 'Webpay Plus', 'Rechazado', NULL),
(3, 17990, 'Webpay Plus', 'Pendiente', NULL); -- El cliente reintenta el pago del pedido 3
