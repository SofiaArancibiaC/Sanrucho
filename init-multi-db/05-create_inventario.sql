-- Microservicio: ms-inventario
-- Base de datos para control de stock
-- Optimizado para Kafka + OpenFeign + PostgreSQL

\c inventario;

-- Drop tables en orden correcto (primero las dependientes)
DROP TABLE IF EXISTS eventos_inventario CASCADE;
DROP TABLE IF EXISTS movimientos_inventario CASCADE;
DROP TABLE IF EXISTS inventario CASCADE;

CREATE TABLE inventario (
    id SERIAL PRIMARY KEY,
    producto_id INTEGER NOT NULL UNIQUE,
    sku VARCHAR(50) NOT NULL UNIQUE,
    nombre_producto VARCHAR(200) NOT NULL,
    stock_actual INTEGER NOT NULL DEFAULT 0,
    stock_reservado INTEGER NOT NULL DEFAULT 0,
    stock_disponible INTEGER GENERATED ALWAYS AS (stock_actual - stock_reservado) STORED,
    stock_minimo INTEGER NOT NULL DEFAULT 5,
    stock_maximo INTEGER NOT NULL DEFAULT 100,
    ubicacion_bodega VARCHAR(50),
    alerta_stock_bajo BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE TABLE movimientos_inventario (
    id SERIAL PRIMARY KEY,
    inventario_id INTEGER NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('entrada', 'salida', 'ajuste', 'reserva', 'liberacion')),
    cantidad INTEGER NOT NULL,
    cantidad_anterior INTEGER NOT NULL,
    cantidad_nueva INTEGER NOT NULL,
    referencia VARCHAR(100),
    motivo TEXT,
    realizado_por VARCHAR(100),
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE eventos_inventario (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(100) UNIQUE NOT NULL,
    producto_id INTEGER NOT NULL,
    sku VARCHAR(50) NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL CHECK (tipo_evento IN ('stock_actualizado', 'stock_bajo', 'sin_stock', 'stock_reservado', 'stock_liberado')),
    payload JSONB NOT NULL,
    publicado BOOLEAN DEFAULT FALSE,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_publicacion TIMESTAMP
);

CREATE INDEX idx_inventario_producto ON inventario(producto_id);
CREATE INDEX idx_inventario_sku ON inventario(sku);
CREATE INDEX idx_inventario_stock_disponible ON inventario(stock_disponible);
CREATE INDEX idx_inventario_alerta ON inventario(alerta_stock_bajo);
CREATE INDEX idx_movimientos_inventario ON movimientos_inventario(inventario_id);
CREATE INDEX idx_movimientos_fecha ON movimientos_inventario(fecha_movimiento);
CREATE INDEX idx_eventos_inventario_event_id ON eventos_inventario(event_id);
CREATE INDEX idx_eventos_inventario_publicado ON eventos_inventario(publicado);

-- Datos de prueba
INSERT INTO inventario (id, producto_id, sku, nombre_producto, stock_actual, stock_reservado, stock_minimo, stock_maximo, ubicacion_bodega, alerta_stock_bajo, version) VALUES
(1, 1, 'HK-PLUSH-001', 'Peluche Hello Kitty Clásico 30cm', 45, 3, 10, 100, 'A-12-3', FALSE, 1),
(2, 2, 'KR-PLUSH-002', 'Peluche Kuromi con Capucha 25cm', 38, 2, 10, 100, 'A-12-4', FALSE, 1),
(3, 3, 'MM-PLUSH-003', 'Peluche My Melody Sleeping 35cm', 22, 2, 8, 80, 'A-13-1', FALSE, 1),
(4, 4, 'CN-PLUSH-004', 'Peluche Cinnamoroll Nube 40cm', 15, 1, 8, 80, 'A-13-2', FALSE, 1),
(5, 5, 'HK-NOTE-101', 'Cuaderno Hello Kitty A5 Rayas', 120, 2, 20, 200, 'B-05-1', FALSE, 1),
(6, 6, 'KR-NOTE-102', 'Cuaderno Kuromi A5 Cuadriculado', 95, 1, 20, 200, 'B-05-2', FALSE, 1),
(7, 7, 'MM-PEN-201', 'Set Bolígrafos Gel My Melody 6 unidades', 67, 1, 15, 150, 'B-06-3', FALSE, 1),
(8, 8, 'PP-MUG-301', 'Taza Cerámica Pompompurin 350ml', 8, 0, 10, 60, 'C-08-1', TRUE, 1),
(9, 9, 'HK-BAG-401', 'Mochila Hello Kitty Mini Rosa', 31, 1, 10, 80, 'C-10-2', FALSE, 1),
(10, 10, 'CN-PILLO-302', 'Cojín Cinnamoroll 40x40cm', 18, 2, 8, 70, 'C-09-1', FALSE, 1),
(11, 11, 'LTS-PLUSH-005', 'Peluche Little Twin Stars Set', 12, 1, 5, 50, 'A-14-1', FALSE, 1),
(12, 12, 'BM-WALLET-402', 'Cartera Badtz-Maru Negro', 3, 0, 8, 60, 'C-10-5', TRUE, 1);

INSERT INTO movimientos_inventario (inventario_id, tipo_movimiento, cantidad, cantidad_anterior, cantidad_nueva, referencia, motivo, realizado_por) VALUES
(1, 'entrada', 50, 0, 50, 'PO-2026-001', 'Ingreso de mercadería nueva', 'bodega_user'),
(1, 'salida', 5, 50, 45, 'ORDER-1001', 'Venta a cliente', 'sistema'),
(2, 'entrada', 40, 0, 40, 'PO-2026-001', 'Ingreso de mercadería nueva', 'bodega_user'),
(2, 'salida', 2, 40, 38, 'ORDER-1002', 'Venta a cliente', 'sistema'),
(8, 'entrada', 20, 0, 20, 'PO-2026-005', 'Ingreso artículos hogar', 'bodega_user'),
(8, 'salida', 12, 20, 8, 'ORDER-1008', 'Ventas múltiples', 'sistema'),
(12, 'entrada', 10, 0, 10, 'PO-2026-006', 'Ingreso accesorios', 'bodega_user'),
(12, 'salida', 7, 10, 3, 'ORDER-1012', 'Ventas múltiples', 'sistema');

INSERT INTO eventos_inventario (event_id, producto_id, sku, tipo_evento, payload, publicado, fecha_publicacion) VALUES
('evt_inv_001', 8, 'PP-MUG-301', 'stock_bajo', '{"stock_actual": 8, "stock_minimo": 10}', TRUE, '2026-08-22 10:00:00'),
('evt_inv_002', 12, 'BM-WALLET-402', 'stock_bajo', '{"stock_actual": 3, "stock_minimo": 8}', TRUE, '2026-08-22 10:05:00'),
('evt_inv_003', 1, 'HK-PLUSH-001', 'stock_reservado', '{"cantidad": 1, "carrito_id": 1}', TRUE, '2026-08-22 10:30:00');