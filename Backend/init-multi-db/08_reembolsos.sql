-- 07_reembolsos.sql
CREATE TABLE Reembolsos (
    id_reembolso SERIAL PRIMARY KEY,
    id_transaccion INT NOT NULL,
    monto_reembolsado NUMERIC(10, 2) NOT NULL CHECK (monto_reembolsado > 0),
    motivo TEXT,
    fecha_reembolso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaccion
        FOREIGN KEY(id_transaccion) 
        REFERENCES Transacciones(id_transaccion)
        ON DELETE RESTRICT
);

-- Datos de prueba para Reembolsos
-- Se realiza un reembolso total del pedido 2, pagado con MercadoPago
INSERT INTO Reembolsos (id_transaccion, monto_reembolsado, motivo) VALUES
(2, 12000.00, 'Cliente se arrepintió de la compra antes del despacho.');
