-- Microservicio: ms-pedidos
-- Base de datos para gestión de órdenes de compra
-- Optimizado para Kafka + OpenFeign + PostgreSQL

\c pedido;

-- Drop tables en orden correcto (primero las dependientes)
DROP TABLE IF EXISTS eventos_pedido CASCADE;
DROP TABLE IF EXISTS items_pedido CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;

CREATE TABLE pedidos (
    id SERIAL PRIMARY KEY,
    numero_pedido VARCHAR(50) UNIQUE NOT NULL,
    usuario_id INTEGER NOT NULL,
    nombre_usuario VARCHAR(200) NOT NULL,
    email_usuario VARCHAR(255) NOT NULL,
    fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(30) DEFAULT 'pendiente' CHECK (estado IN ('pendiente', 'confirmado', 'procesando', 'enviado', 'entregado', 'cancelado', 'devuelto')),
    subtotal DECIMAL(10, 2) NOT NULL,
    descuento DECIMAL(10, 2) DEFAULT 0,
    costo_envio DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    direccion_envio_id INTEGER NOT NULL,
    direccion_envio_snapshot TEXT NOT NULL,
    ciudad_envio VARCHAR(100) NOT NULL,
    region_envio VARCHAR(100) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    notas TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 1
);

CREATE TABLE items_pedido (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    sku VARCHAR(50) NOT NULL,
    nombre_producto VARCHAR(200) NOT NULL,
    personaje VARCHAR(50),
    precio_unitario DECIMAL(10, 2) NOT NULL,
    cantidad INTEGER NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE TABLE eventos_pedido (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(100) UNIQUE NOT NULL,
    pedido_id INTEGER NOT NULL,
    numero_pedido VARCHAR(50) NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL CHECK (tipo_evento IN ('pedido_creado', 'pedido_confirmado', 'pedido_procesando', 'pedido_enviado', 'pedido_entregado', 'pedido_cancelado', 'pedido_devuelto')),
    payload JSONB NOT NULL,
    publicado BOOLEAN DEFAULT FALSE,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_publicacion TIMESTAMP
);

CREATE INDEX idx_pedidos_numero ON pedidos(numero_pedido);
CREATE INDEX idx_pedidos_usuario ON pedidos(usuario_id);
CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_pedidos_fecha ON pedidos(fecha_pedido);
CREATE INDEX idx_items_pedido ON items_pedido(pedido_id);
CREATE INDEX idx_items_producto ON items_pedido(producto_id);
CREATE INDEX idx_eventos_pedido_event_id ON eventos_pedido(event_id);
CREATE INDEX idx_eventos_pedido_publicado ON eventos_pedido(publicado);

-- Datos de prueba
INSERT INTO pedidos (id, numero_pedido, usuario_id, nombre_usuario, email_usuario, fecha_pedido, estado, subtotal, descuento, costo_envio, total, direccion_envio_id, direccion_envio_snapshot, ciudad_envio, region_envio, metodo_pago, notas, created_by, version) VALUES
(1, 'ORD-2026-0001', 1, 'María González', 'maria.gonzalez@email.com', '2026-08-20 14:25:00', 'entregado', 56960, 0, 3000, 59960, 1, 'Av. Providencia 1234, Depto 501, Santiago, Región Metropolitana, 7500000', 'Santiago', 'Región Metropolitana', 'tarjeta_credito', NULL, 'sistema', 1),
(2, 'ORD-2026-0002', 2, 'Carlos Silva', 'carlos.silva@email.com', '2026-08-21 10:15:00', 'enviado', 29480, 2000, 3500, 30980, 3, 'Calle Los Aromos 567, Viña del Mar, Valparaíso, 2520000', 'Viña del Mar', 'Valparaíso', 'webpay', 'Entregar en horario de tarde', 'sistema', 1),
(3, 'ORD-2026-0003', 3, 'Ana Rodríguez', 'ana.rodriguez@email.com', '2026-08-21 16:30:00', 'procesando', 64970, 5000, 4000, 63970, 4, 'Pasaje La Florida 890, Concepción, Biobío, 4030000', 'Concepción', 'Biobío', 'tarjeta_debito', NULL, 'sistema', 1),
(4, 'ORD-2026-0004', 4, 'Pedro Martínez', 'pedro.martinez@email.com', '2026-08-22 09:45:00', 'confirmado', 63970, 0, 3500, 67470, 5, 'Av. Libertador Bernardo O''Higgins 2345, Santiago, Región Metropolitana, 8320000', 'Santiago', 'Región Metropolitana', 'transferencia', NULL, 'sistema', 1),
(5, 'ORD-2026-0005', 5, 'Lucía Fernández', 'lucia.fernandez@email.com', '2026-08-20 11:20:00', 'entregado', 38970, 0, 3000, 41970, 6, 'Los Castaños 456, La Serena, Coquimbo, 1700000', 'La Serena', 'Coquimbo', 'tarjeta_credito', NULL, 'sistema', 1),
(6, 'ORD-2026-0006', 7, 'Sofía Morales', 'sofia.morales@email.com', '2026-08-22 12:30:00', 'pendiente', 84970, 10000, 0, 74970, 7, 'Av. Apoquindo 3456, Santiago, Región Metropolitana, 7550000', 'Santiago', 'Región Metropolitana', 'webpay', 'Compra sobre 50.000 - envío gratis', 'sistema', 1),
(7, 'ORD-2026-0007', 8, 'Jorge Ramírez', 'jorge.ramirez@email.com', '2026-08-19 15:40:00', 'cancelado', 36980, 0, 3500, 40480, 8, 'Calle Zenteno 789, Valdivia, Los Ríos, 5090000', 'Valdivia', 'Los Ríos', 'tarjeta_credito', 'Cancelado por el cliente', 'sistema', 1),
(8, 'ORD-2026-0008', 1, 'María González', 'maria.gonzalez@email.com', '2026-08-22 08:15:00', 'confirmado', 24990, 0, 3000, 27990, 1, 'Av. Providencia 1234, Depto 501, Santiago, Región Metropolitana, 7500000', 'Santiago', 'Región Metropolitana', 'webpay', NULL, 'sistema', 1);

INSERT INTO items_pedido (pedido_id, producto_id, sku, nombre_producto, personaje, precio_unitario, cantidad, subtotal, version) VALUES
(1, 1, 'HK-PLUSH-001', 'Peluche Hello Kitty Clásico 30cm', 'Hello Kitty', 24990, 1, 24990, 1),
(1, 5, 'HK-NOTE-101', 'Cuaderno Hello Kitty A5 Rayas', 'Hello Kitty', 5990, 2, 11980, 1),
(1, 9, 'HK-BAG-401', 'Mochila Hello Kitty Mini Rosa', 'Hello Kitty', 19990, 1, 19990, 1),
(2, 2, 'KR-PLUSH-002', 'Peluche Kuromi con Capucha 25cm', 'Kuromi', 22990, 1, 22990, 1),
(2, 6, 'KR-NOTE-102', 'Cuaderno Kuromi A5 Cuadriculado', 'Kuromi', 6490, 1, 6490, 1),
(3, 3, 'MM-PLUSH-003', 'Peluche My Melody Sleeping 35cm', 'My Melody', 27990, 2, 55980, 1),
(3, 7, 'MM-PEN-201', 'Set Bolígrafos Gel My Melody 6 unidades', 'My Melody', 8990, 1, 8990, 1),
(4, 4, 'CN-PLUSH-004', 'Peluche Cinnamoroll Nube 40cm', 'Cinnamoroll', 29990, 1, 29990, 1),
(4, 10, 'CN-PILLO-302', 'Cojín Cinnamoroll 40x40cm', 'Cinnamoroll', 16990, 2, 33980, 1),
(5, 8, 'PP-MUG-301', 'Taza Cerámica Pompompurin 350ml', 'Pompompurin', 12990, 3, 38970, 1),
(6, 11, 'LTS-PLUSH-005', 'Peluche Little Twin Stars Set', 'Little Twin Stars', 34990, 1, 34990, 1),
(6, 1, 'HK-PLUSH-001', 'Peluche Hello Kitty Clásico 30cm', 'Hello Kitty', 24990, 2, 49980, 1),
(7, 12, 'BM-WALLET-402', 'Cartera Badtz-Maru Negro', 'Badtz-Maru', 13990, 1, 13990, 1),
(7, 2, 'KR-PLUSH-002', 'Peluche Kuromi con Capucha 25cm', 'Kuromi', 22990, 1, 22990, 1),
(8, 1, 'HK-PLUSH-001', 'Peluche Hello Kitty Clásico 30cm', 'Hello Kitty', 24990, 1, 24990, 1);

INSERT INTO eventos_pedido (event_id, pedido_id, numero_pedido, tipo_evento, payload, publicado, fecha_publicacion) VALUES
('evt_ped_001', 1, 'ORD-2026-0001', 'pedido_creado', '{"usuario_id": 1, "total": 59960, "items": 3}', TRUE, '2026-08-20 14:25:05'),
('evt_ped_002', 1, 'ORD-2026-0001', 'pedido_confirmado', '{"usuario_id": 1, "metodo_pago": "tarjeta_credito"}', TRUE, '2026-08-20 14:30:05'),
('evt_ped_003', 1, 'ORD-2026-0001', 'pedido_entregado', '{"usuario_id": 1, "fecha_entrega": "2026-08-22 11:30:00"}', TRUE, '2026-08-22 11:30:30'),
('evt_ped_004', 2, 'ORD-2026-0002', 'pedido_enviado', '{"usuario_id": 2, "tracking": "TRACK-2026-002"}', TRUE, '2026-08-22 08:30:15');