-- Microservicio: ms-despacho
-- Base de datos para gestión de envíos
-- Optimizado para Kafka + OpenFeign + PostgreSQL

\c despacho;

-- Drop tables en orden correcto (primero las dependientes)
DROP TABLE IF EXISTS eventos_despacho CASCADE;
DROP TABLE IF EXISTS seguimiento_despacho CASCADE;
DROP TABLE IF EXISTS despachos CASCADE;

CREATE TABLE despachos (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER NOT NULL UNIQUE,
    numero_pedido VARCHAR(50) NOT NULL,
    usuario_id INTEGER NOT NULL,
    nombre_usuario VARCHAR(200) NOT NULL,
    numero_seguimiento VARCHAR(100) UNIQUE NOT NULL,
    courier VARCHAR(100) NOT NULL,
    estado VARCHAR(30) DEFAULT 'preparando' CHECK (estado IN ('preparando', 'listo_para_envio', 'en_transito', 'en_reparto', 'entregado', 'fallido', 'devuelto')),
    direccion_destino TEXT NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(20),
    fecha_despacho TIMESTAMP,
    fecha_entrega_estimada DATE,
    fecha_entrega_real TIMESTAMP,
    intentos_entrega INTEGER DEFAULT 0,
    peso_kg DECIMAL(5, 2),
    dimensiones VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 1
);

CREATE TABLE seguimiento_despacho (
    id SERIAL PRIMARY KEY,
    despacho_id INTEGER NOT NULL,
    estado VARCHAR(30) NOT NULL,
    ubicacion VARCHAR(200),
    descripcion TEXT,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE eventos_despacho (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(100) UNIQUE NOT NULL,
    despacho_id INTEGER NOT NULL,
    numero_seguimiento VARCHAR(100) NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL CHECK (tipo_evento IN ('despacho_creado', 'despacho_listo', 'despacho_en_transito', 'despacho_en_reparto', 'despacho_entregado', 'despacho_fallido', 'despacho_devuelto')),
    payload JSONB NOT NULL,
    publicado BOOLEAN DEFAULT FALSE,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_publicacion TIMESTAMP
);

CREATE INDEX idx_despachos_pedido ON despachos(pedido_id);
CREATE INDEX idx_despachos_numero_pedido ON despachos(numero_pedido);
CREATE INDEX idx_despachos_usuario ON despachos(usuario_id);
CREATE INDEX idx_despachos_seguimiento ON despachos(numero_seguimiento);
CREATE INDEX idx_despachos_estado ON despachos(estado);
CREATE INDEX idx_despachos_courier ON despachos(courier);
CREATE INDEX idx_seguimiento_despacho ON seguimiento_despacho(despacho_id);
CREATE INDEX idx_seguimiento_fecha ON seguimiento_despacho(fecha_evento);
CREATE INDEX idx_eventos_despacho_event_id ON eventos_despacho(event_id);
CREATE INDEX idx_eventos_despacho_publicado ON eventos_despacho(publicado);

-- Datos de prueba
INSERT INTO despachos (id, pedido_id, numero_pedido, usuario_id, nombre_usuario, numero_seguimiento, courier, estado, direccion_destino, ciudad, region, codigo_postal, fecha_despacho, fecha_entrega_estimada, fecha_entrega_real, intentos_entrega, peso_kg, dimensiones, created_by, version) VALUES
(1, 1, 'ORD-2026-0001', 1, 'María González', 'TRACK-2026-001', 'Chilexpress', 'entregado', 'Av. Providencia 1234, Depto 501', 'Santiago', 'Región Metropolitana', '7500000', '2026-08-21 09:00:00', '2026-08-22', '2026-08-22 11:30:00', 1, 1.2, '40x30x20cm', 'sistema', 1),
(2, 2, 'ORD-2026-0002', 2, 'Carlos Silva', 'TRACK-2026-002', 'Starken', 'en_reparto', 'Calle Los Aromos 567', 'Viña del Mar', 'Valparaíso', '2520000', '2026-08-22 08:30:00', '2026-08-23', NULL, 1, 0.8, '35x25x15cm', 'sistema', 1),
(3, 3, 'ORD-2026-0003', 3, 'Ana Rodríguez', 'TRACK-2026-003', 'BluExpress', 'en_transito', 'Pasaje La Florida 890', 'Concepción', 'Biobío', '4030000', '2026-08-22 10:00:00', '2026-08-24', NULL, 0, 1.5, '45x35x25cm', 'sistema', 1),
(4, 4, 'ORD-2026-0004', 4, 'Pedro Martínez', 'TRACK-2026-004', 'Chilexpress', 'listo_para_envio', 'Av. Libertador Bernardo O''Higgins 2345', 'Santiago', 'Región Metropolitana', '8320000', NULL, '2026-08-23', NULL, 0, 1.3, '40x30x22cm', 'sistema', 1),
(5, 5, 'ORD-2026-0005', 5, 'Lucía Fernández', 'TRACK-2026-005', 'Starken', 'entregado', 'Los Castaños 456', 'La Serena', 'Coquimbo', '1700000', '2026-08-21 09:00:00', '2026-08-21', '2026-08-21 17:45:00', 1, 0.9, '30x25x20cm', 'sistema', 1),
(6, 8, 'ORD-2026-0008', 1, 'María González', 'TRACK-2026-008', 'Chilexpress', 'preparando', 'Av. Providencia 1234, Depto 501', 'Santiago', 'Región Metropolitana', '7500000', NULL, '2026-08-23', NULL, 0, 0.6, '30x20x15cm', 'sistema', 1);

INSERT INTO seguimiento_despacho (despacho_id, estado, ubicacion, descripcion, fecha_evento) VALUES
(1, 'preparando', 'Centro de Distribución Santiago', 'Pedido empacado', '2026-08-20 16:30:00'),
(1, 'listo_para_envio', 'Centro de Distribución Santiago', 'Listo para despacho', '2026-08-21 08:30:00'),
(1, 'en_transito', 'Centro de Distribución Santiago', 'Paquete despachado', '2026-08-21 09:00:00'),
(1, 'en_reparto', 'Centro de Distribución Providencia', 'En ruta de entrega local', '2026-08-22 09:00:00'),
(1, 'entregado', 'Av. Providencia 1234, Depto 501', 'Entregado al destinatario', '2026-08-22 11:30:00'),
(2, 'preparando', 'Centro de Distribución Santiago', 'Pedido empacado', '2026-08-21 14:30:00'),
(2, 'listo_para_envio', 'Centro de Distribución Santiago', 'Listo para despacho', '2026-08-22 08:00:00'),
(2, 'en_transito', 'Centro de Distribución Santiago', 'En camino a Viña del Mar', '2026-08-22 08:30:00'),
(2, 'en_reparto', 'Centro de Distribución Viña del Mar', 'En ruta de entrega', '2026-08-22 14:00:00');

INSERT INTO eventos_despacho (event_id, despacho_id, numero_seguimiento, tipo_evento, payload, publicado, fecha_publicacion) VALUES
('evt_desp_001', 1, 'TRACK-2026-001', 'despacho_creado', '{"pedido_id": 1, "courier": "Chilexpress"}', TRUE, '2026-08-20 16:30:10'),
('evt_desp_002', 1, 'TRACK-2026-001', 'despacho_en_transito', '{"ubicacion": "Centro de Distribución Santiago"}', TRUE, '2026-08-21 09:00:15'),
('evt_desp_003', 1, 'TRACK-2026-001', 'despacho_entregado', '{"fecha_entrega": "2026-08-22 11:30:00"}', TRUE, '2026-08-22 11:30:20');