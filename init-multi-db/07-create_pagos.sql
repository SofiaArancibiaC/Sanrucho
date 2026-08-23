-- Microservicio: ms-pagos
-- Base de datos para procesamiento de pagos
-- Optimizado para Kafka + OpenFeign + PostgreSQL

\c pago;

-- Drop tables en orden correcto (primero las dependientes)
DROP TABLE IF EXISTS eventos_pago CASCADE;
DROP TABLE IF EXISTS detalles_pago CASCADE;
DROP TABLE IF EXISTS pagos CASCADE;

CREATE TABLE pagos (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER NOT NULL,
    numero_pedido VARCHAR(50) NOT NULL,
    usuario_id INTEGER NOT NULL,
    numero_transaccion VARCHAR(100) UNIQUE NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL CHECK (metodo_pago IN ('tarjeta_credito', 'tarjeta_debito', 'webpay', 'transferencia', 'paypal')),
    monto INTEGER NOT NULL,
    estado VARCHAR(30) DEFAULT 'pendiente' CHECK (estado IN ('pendiente', 'procesando', 'aprobado', 'rechazado', 'reembolsado', 'cancelado')),
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_aprobacion TIMESTAMP,
    codigo_autorizacion VARCHAR(50),
    monto_reembolso INTEGER,
    fecha_reembolso TIMESTAMP,
    motivo_reembolso TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 1
);

CREATE TABLE detalles_pago (
    id SERIAL PRIMARY KEY,
    pago_id INTEGER NOT NULL,
    tipo_tarjeta VARCHAR(20),
    ultimos_4_digitos VARCHAR(4),
    nombre_titular VARCHAR(200),
    banco VARCHAR(100),
    cuotas INTEGER DEFAULT 1,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE eventos_pago (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(100) UNIQUE NOT NULL,
    pago_id INTEGER NOT NULL,
    numero_transaccion VARCHAR(100) NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL CHECK (tipo_evento IN ('pago_iniciado', 'pago_procesando', 'pago_aprobado', 'pago_rechazado', 'pago_reembolsado', 'pago_cancelado')),
    payload JSONB NOT NULL,
    publicado BOOLEAN DEFAULT FALSE,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_publicacion TIMESTAMP
);

CREATE INDEX idx_pagos_pedido ON pagos(pedido_id);
CREATE INDEX idx_pagos_numero_pedido ON pagos(numero_pedido);
CREATE INDEX idx_pagos_usuario ON pagos(usuario_id);
CREATE INDEX idx_pagos_transaccion ON pagos(numero_transaccion);
CREATE INDEX idx_pagos_estado ON pagos(estado);
CREATE INDEX idx_detalles_pago ON detalles_pago(pago_id);
CREATE INDEX idx_eventos_pago_event_id ON eventos_pago(event_id);
CREATE INDEX idx_eventos_pago_publicado ON eventos_pago(publicado);

-- Datos de prueba
INSERT INTO pagos (id, pedido_id, numero_pedido, usuario_id, numero_transaccion, metodo_pago, monto, estado, fecha_pago, fecha_aprobacion, codigo_autorizacion, created_by, version) VALUES
(1, 1, 'ORD-2026-0001', 1, 'TXN-2026-0820-001', 'tarjeta_credito', 59960, 'aprobado', '2026-08-20 14:25:30', '2026-08-20 14:30:15', 'AUTH-123456', 'sistema', 1),
(2, 2, 'ORD-2026-0002', 2, 'TXN-2026-0821-002', 'webpay', 30980, 'aprobado', '2026-08-21 10:15:45', '2026-08-21 10:20:10', 'AUTH-123457', 'sistema', 1),
(3, 3, 'ORD-2026-0003', 3, 'TXN-2026-0821-003', 'tarjeta_debito', 63970, 'aprobado', '2026-08-21 16:30:20', '2026-08-21 16:35:05', 'AUTH-123458', 'sistema', 1),
(4, 4, 'ORD-2026-0004', 4, 'TXN-2026-0822-004', 'transferencia', 67470, 'aprobado', '2026-08-22 09:45:15', '2026-08-22 09:50:30', 'AUTH-123459', 'sistema', 1),
(5, 5, 'ORD-2026-0005', 5, 'TXN-2026-0820-005', 'tarjeta_credito', 41970, 'aprobado', '2026-08-20 11:20:10', '2026-08-20 11:25:45', 'AUTH-123460', 'sistema', 1),
(6, 6, 'ORD-2026-0006', 7, 'TXN-2026-0822-006', 'webpay', 74970, 'pendiente', '2026-08-22 12:30:00', NULL, NULL, 'sistema', 1),
(7, 7, 'ORD-2026-0007', 8, 'TXN-2026-0819-007', 'tarjeta_credito', 40480, 'reembolsado', '2026-08-19 15:40:20', NULL, NULL, 'sistema', 1),
(8, 8, 'ORD-2026-0008', 1, 'TXN-2026-0822-008', 'webpay', 27990, 'aprobado', '2026-08-22 08:15:30', '2026-08-22 08:20:15', 'AUTH-123461', 'sistema', 1);

UPDATE pagos SET monto_reembolso = 40480, fecha_reembolso = '2026-08-20 10:15:00', motivo_reembolso = 'Pedido cancelado por el cliente' WHERE id = 7;

INSERT INTO detalles_pago (pago_id, tipo_tarjeta, ultimos_4_digitos, nombre_titular, banco, cuotas) VALUES
(1, 'Visa', '4532', 'Maria Gonzalez', 'Banco de Chile', 3),
(2, 'Mastercard', '5412', 'Carlos Silva', 'Banco Santander', 1),
(3, 'Visa Débito', '4916', 'Ana Rodriguez', 'Banco Estado', 1),
(4, NULL, NULL, 'Pedro Martinez', 'Banco BCI', 1),
(5, 'Mastercard', '5234', 'Lucia Fernandez', 'Banco de Chile', 6),
(6, 'Visa', '4789', 'Sofia Morales', 'Banco Santander', 1),
(7, 'Visa', '4321', 'Jorge Ramirez', 'Banco Estado', 1),
(8, 'Mastercard', '5678', 'Maria Gonzalez', 'Banco de Chile', 1);

INSERT INTO eventos_pago (event_id, pago_id, numero_transaccion, tipo_evento, payload, publicado, fecha_publicacion) VALUES
('evt_pag_001', 1, 'TXN-2026-0820-001', 'pago_iniciado', '{"pedido_id": 1, "monto": 59960, "metodo": "tarjeta_credito"}', TRUE, '2026-08-20 14:25:35'),
('evt_pag_002', 1, 'TXN-2026-0820-001', 'pago_aprobado', '{"pedido_id": 1, "codigo_auth": "AUTH-123456"}', TRUE, '2026-08-20 14:30:20'),
('evt_pag_003', 7, 'TXN-2026-0819-007', 'pago_reembolsado', '{"pedido_id": 7, "monto_reembolso": 40480}', TRUE, '2026-08-20 10:15:10');