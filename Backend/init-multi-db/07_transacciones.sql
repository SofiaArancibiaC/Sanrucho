-- 06_transacciones.sql
-- Esquema para Microservicio de Pagos (PostgreSQL)

CREATE TABLE Transacciones (
    id_transaccion SERIAL PRIMARY KEY,
    id_pedido INT NOT NULL, -- Referencia lógica al microservicio de Pedidos (SIN FOREIGN KEY física)
    monto NUMERIC(10, 2) NOT NULL,
    pasarela VARCHAR(50) NOT NULL,
    estado VARCHAR(50) DEFAULT 'Pendiente',
    codigo_autorizacion VARCHAR(100),
    fecha_transaccion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos de prueba para Transacciones
-- Corresponden a los pedidos generados en el archivo 04_pedidos.sql
INSERT INTO Transacciones (id_pedido, monto, pasarela, estado, codigo_autorizacion) VALUES
(1, 38490.00, 'Webpay Plus', 'Aprobado', 'AUTH-998877'),
(2, 12000.00, 'MercadoPago', 'Aprobado', 'MP-554433'),
(3, 15990.00, 'Webpay Plus', 'Rechazado', NULL),
(3, 15990.00, 'Webpay Plus', 'Pendiente', NULL); -- El cliente reintenta el pago del pedido 3
