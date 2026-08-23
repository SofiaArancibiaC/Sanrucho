-- Microservicio: ms-reportes
-- Base de datos para reportería y analytics
-- Optimizado para Kafka + OpenFeign + PostgreSQL

\c reporte;

-- Drop tables en orden correcto (primero las dependientes)
DROP TABLE IF EXISTS eventos_reporte CASCADE;
DROP TABLE IF EXISTS productos_mas_vendidos CASCADE;
DROP TABLE IF EXISTS metricas_ventas CASCADE;

CREATE TABLE metricas_ventas (
    id SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    total_ventas DECIMAL(12, 2) NOT NULL,
    cantidad_pedidos INTEGER NOT NULL,
    ticket_promedio DECIMAL(10, 2) NOT NULL,
    productos_vendidos INTEGER NOT NULL,
    nuevos_clientes INTEGER DEFAULT 0,
    tasa_conversion DECIMAL(5, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1,
    UNIQUE(fecha)
);

CREATE TABLE productos_mas_vendidos (
    id SERIAL PRIMARY KEY,
    producto_id INTEGER NOT NULL,
    sku VARCHAR(50) NOT NULL,
    nombre_producto VARCHAR(200) NOT NULL,
    personaje VARCHAR(50) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    cantidad_vendida INTEGER NOT NULL,
    ingresos_totales DECIMAL(12, 2) NOT NULL,
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE TABLE eventos_reporte (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(100) UNIQUE NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL CHECK (tipo_evento IN ('reporte_solicitado', 'reporte_generado', 'metricas_calculadas', 'reporte_fallido')),
    payload JSONB NOT NULL,
    publicado BOOLEAN DEFAULT FALSE,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_publicacion TIMESTAMP
);

CREATE INDEX idx_metricas_ventas_fecha ON metricas_ventas(fecha);
CREATE INDEX idx_productos_vendidos_periodo ON productos_mas_vendidos(periodo_inicio, periodo_fin);
CREATE INDEX idx_productos_vendidos_personaje ON productos_mas_vendidos(personaje);
CREATE INDEX idx_productos_vendidos_cantidad ON productos_mas_vendidos(cantidad_vendida DESC);
CREATE INDEX idx_eventos_reporte_event_id ON eventos_reporte(event_id);
CREATE INDEX idx_eventos_reporte_publicado ON eventos_reporte(publicado);

-- Datos de prueba
INSERT INTO metricas_ventas (fecha, total_ventas, cantidad_pedidos, ticket_promedio, productos_vendidos, nuevos_clientes, tasa_conversion, version) VALUES
('2026-08-19', 40480, 1, 40480, 2, 0, 2.5, 1),
('2026-08-20', 101930, 2, 50965, 7, 1, 3.2, 1),
('2026-08-21', 94950, 2, 47475, 5, 1, 2.8, 1),
('2026-08-22', 170430, 3, 56810, 9, 0, 4.1, 1);

INSERT INTO productos_mas_vendidos (producto_id, sku, nombre_producto, personaje, categoria, cantidad_vendida, ingresos_totales, periodo_inicio, periodo_fin, version) VALUES
(1, 'HK-PLUSH-001', 'Peluche Hello Kitty Clásico 30cm', 'Hello Kitty', 'Peluches', 5, 124950, '2026-08-19', '2026-08-22', 1),
(2, 'KR-PLUSH-002', 'Peluche Kuromi con Capucha 25cm', 'Kuromi', 'Peluches', 3, 68970, '2026-08-19', '2026-08-22', 1),
(3, 'MM-PLUSH-003', 'Peluche My Melody Sleeping 35cm', 'My Melody', 'Peluches', 2, 55980, '2026-08-19', '2026-08-22', 1),
(4, 'CN-PLUSH-004', 'Peluche Cinnamoroll Nube 40cm', 'Cinnamoroll', 'Peluches', 1, 29990, '2026-08-19', '2026-08-22', 1),
(8, 'PP-MUG-301', 'Taza Cerámica Pompompurin 350ml', 'Pompompurin', 'Hogar', 3, 38970, '2026-08-19', '2026-08-22', 1),
(10, 'CN-PILLO-302', 'Cojín Cinnamoroll 40x40cm', 'Cinnamoroll', 'Hogar', 2, 33980, '2026-08-19', '2026-08-22', 1),
(5, 'HK-NOTE-101', 'Cuaderno Hello Kitty A5 Rayas', 'Hello Kitty', 'Papelería', 2, 11980, '2026-08-19', '2026-08-22', 1),
(6, 'KR-NOTE-102', 'Cuaderno Kuromi A5 Cuadriculado', 'Kuromi', 'Papelería', 1, 6490, '2026-08-19', '2026-08-22', 1),
(7, 'MM-PEN-201', 'Set Bolígrafos Gel My Melody 6 unidades', 'My Melody', 'Papelería', 1, 8990, '2026-08-19', '2026-08-22', 1),
(9, 'HK-BAG-401', 'Mochila Hello Kitty Mini Rosa', 'Hello Kitty', 'Accesorios', 1, 19990, '2026-08-19', '2026-08-22', 1),
(11, 'LTS-PLUSH-005', 'Peluche Little Twin Stars Set', 'Little Twin Stars', 'Peluches', 1, 34990, '2026-08-19', '2026-08-22', 1),
(12, 'BM-WALLET-402', 'Cartera Badtz-Maru Negro', 'Badtz-Maru', 'Accesorios', 1, 13990, '2026-08-19', '2026-08-22', 1);

INSERT INTO eventos_reporte (event_id, tipo_evento, payload, publicado, fecha_publicacion) VALUES
('evt_rep_001', 'metricas_calculadas', '{"fecha": "2026-08-22", "total_ventas": 170430}', TRUE, '2026-08-22 23:59:00'),
('evt_rep_002', 'reporte_generado', '{"tipo": "ventas_diarias", "archivo": "ventas_diarias_20260819-20260822.xlsx"}', TRUE, '2026-08-22 23:00:10');