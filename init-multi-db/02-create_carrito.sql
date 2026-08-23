-- Microservicio: ms-carrito
-- Base de datos para gestión de carritos de compra
-- Optimizado para Kafka + OpenFeign + PostgreSQL

\c carrito;

DROP TABLE IF EXISTS eventos_carrito CASCADE;
DROP TABLE IF EXISTS items_carrito CASCADE;
DROP TABLE IF EXISTS carritos CASCADE;

CREATE TABLE carritos (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    sesion_id VARCHAR(100),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'activo' CHECK (estado IN ('activo', 'abandonado', 'convertido', 'expirado')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE TABLE items_carrito (
    id SERIAL PRIMARY KEY,
    carrito_id INTEGER NOT NULL,  -- NO foreign key - referencia a tabla local
    producto_id INTEGER NOT NULL,  -- ID del producto en ms-catalogo
    sku VARCHAR(50) NOT NULL,
    nombre_producto VARCHAR(200) NOT NULL,  -- Snapshot
    personaje VARCHAR(50),  -- Snapshot para filtros
    precio_unitario DECIMAL(10, 2) NOT NULL,  -- Snapshot del precio al momento de agregar
    cantidad INTEGER NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    subtotal DECIMAL(10, 2) NOT NULL,
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE TABLE eventos_carrito (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(100) UNIQUE NOT NULL,  -- Para deduplicación en Kafka
    carrito_id INTEGER NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL CHECK (tipo_evento IN ('item_agregado', 'item_removido', 'item_actualizado', 'carrito_abandonado', 'carrito_convertido', 'carrito_vaciado')),
    payload JSONB NOT NULL,  -- Datos del evento
    publicado BOOLEAN DEFAULT FALSE,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_publicacion TIMESTAMP
);

CREATE INDEX idx_carritos_usuario ON carritos(usuario_id);
CREATE INDEX idx_carritos_sesion ON carritos(sesion_id);
CREATE INDEX idx_carritos_estado ON carritos(estado);
CREATE INDEX idx_items_carrito_carrito ON items_carrito(carrito_id);
CREATE INDEX idx_items_carrito_producto ON items_carrito(producto_id);
CREATE INDEX idx_eventos_carrito_event_id ON eventos_carrito(event_id);
CREATE INDEX idx_eventos_carrito_publicado ON eventos_carrito(publicado);

-- Datos de prueba
INSERT INTO carritos (id, usuario_id, sesion_id, estado, fecha_creacion, fecha_ultima_modificacion, version) VALUES
(1, 1, 'sess_a1b2c3d4e5f6', 'activo', '2026-08-22 10:30:00', '2026-08-22 10:30:00', 1),
(2, 2, 'sess_f6e5d4c3b2a1', 'activo', '2026-08-22 11:15:00', '2026-08-22 11:15:00', 1),
(3, 3, 'sess_1a2b3c4d5e6f', 'abandonado', '2026-08-21 16:45:00', '2026-08-21 16:45:00', 1),
(4, 4, 'sess_9z8y7x6w5v4u', 'activo', '2026-08-22 09:20:00', '2026-08-22 09:20:00', 1),
(5, 5, 'sess_5t6u7v8w9x0y', 'convertido', '2026-08-20 14:30:00', '2026-08-20 14:30:00', 1),
(6, 7, 'sess_p0o9i8u7y6t5', 'activo', '2026-08-22 12:00:00', '2026-08-22 12:00:00', 1);

INSERT INTO items_carrito (carrito_id, producto_id, sku, nombre_producto, personaje, precio_unitario, cantidad, subtotal, version) VALUES
(1, 1, 'HK-PLUSH-001', 'Peluche Hello Kitty Clásico 30cm', 'Hello Kitty', 24990, 1, 24990, 1),
(1, 5, 'HK-NOTE-101', 'Cuaderno Hello Kitty A5 Rayas', 'Hello Kitty', 5990, 2, 11980, 1),
(1, 9, 'HK-BAG-401', 'Mochila Hello Kitty Mini Rosa', 'Hello Kitty', 19990, 1, 19990, 1),
(2, 2, 'KR-PLUSH-002', 'Peluche Kuromi con Capucha 25cm', 'Kuromi', 22990, 1, 22990, 1),
(2, 6, 'KR-NOTE-102', 'Cuaderno Kuromi A5 Cuadriculado', 'Kuromi', 6490, 1, 6490, 1),
(3, 3, 'MM-PLUSH-003', 'Peluche My Melody Sleeping 35cm', 'My Melody', 27990, 2, 55980, 1),
(3, 7, 'MM-PEN-201', 'Set Bolígrafos Gel My Melody 6 unidades', 'My Melody', 8990, 1, 8990, 1),
(4, 4, 'CN-PLUSH-004', 'Peluche Cinnamoroll Nube 40cm', 'Cinnamoroll', 29990, 1, 29990, 1),
(4, 10, 'CN-PILLO-302', 'Cojín Cinnamoroll 40x40cm', 'Cinnamoroll', 16990, 2, 33980, 1),
(6, 11, 'LTS-PLUSH-005', 'Peluche Little Twin Stars Set', 'Little Twin Stars', 34990, 1, 34990, 1),
(6, 1, 'HK-PLUSH-001', 'Peluche Hello Kitty Clásico 30cm', 'Hello Kitty', 24990, 2, 49980, 1);

INSERT INTO eventos_carrito (event_id, carrito_id, tipo_evento, payload, publicado, fecha_publicacion) VALUES
('evt_carr_001', 1, 'item_agregado', '{"producto_id": 1, "sku": "HK-PLUSH-001", "cantidad": 1}', TRUE, '2026-08-22 10:30:05'),
('evt_carr_002', 1, 'item_agregado', '{"producto_id": 5, "sku": "HK-NOTE-101", "cantidad": 2}', TRUE, '2026-08-22 10:31:00'),
('evt_carr_003', 2, 'item_agregado', '{"producto_id": 2, "sku": "KR-PLUSH-002", "cantidad": 1}', TRUE, '2026-08-22 11:15:10'),
('evt_carr_004', 3, 'carrito_abandonado', '{"usuario_id": 3, "items_count": 2, "total": 64970}', TRUE, '2026-08-22 16:45:00');