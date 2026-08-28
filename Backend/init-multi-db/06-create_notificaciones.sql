-- Microservicio: ms-notificaciones
-- Base de datos para sistema de notificaciones
-- Optimizado para Kafka + OpenFeign + PostgreSQL

\c notificacion;

-- Drop tables en orden correcto (primero las dependientes)
DROP TABLE IF EXISTS eventos_notificacion CASCADE;
DROP TABLE IF EXISTS configuracion_notificaciones CASCADE;
DROP TABLE IF EXISTS notificaciones CASCADE;

CREATE TABLE notificaciones (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    nombre_usuario VARCHAR(200) NOT NULL,
    email_usuario VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('pedido_confirmado', 'pedido_enviado', 'pedido_entregado', 'cambio_precio', 'stock_disponible', 'promocion', 'review_respuesta', 'newsletter')),
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    canal VARCHAR(20) NOT NULL CHECK (canal IN ('email', 'push', 'sms', 'in_app')),
    prioridad VARCHAR(20) DEFAULT 'normal' CHECK (prioridad IN ('baja', 'normal', 'alta', 'urgente')),
    estado VARCHAR(20) DEFAULT 'pendiente' CHECK (estado IN ('pendiente', 'enviada', 'fallida', 'leida')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_envio TIMESTAMP,
    fecha_lectura TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE TABLE configuracion_notificaciones (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL UNIQUE,
    email_pedidos BOOLEAN DEFAULT TRUE,
    email_promociones BOOLEAN DEFAULT TRUE,
    email_newsletter BOOLEAN DEFAULT FALSE,
    push_pedidos BOOLEAN DEFAULT TRUE,
    push_promociones BOOLEAN DEFAULT FALSE,
    sms_pedidos BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE TABLE eventos_notificacion (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(100) UNIQUE NOT NULL,
    notificacion_id INTEGER NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL CHECK (tipo_evento IN ('notificacion_creada', 'notificacion_enviada', 'notificacion_fallida', 'notificacion_leida')),
    payload JSONB NOT NULL,
    publicado BOOLEAN DEFAULT FALSE,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_publicacion TIMESTAMP
);

CREATE INDEX idx_notificaciones_usuario ON notificaciones(usuario_id);
CREATE INDEX idx_notificaciones_tipo ON notificaciones(tipo);
CREATE INDEX idx_notificaciones_estado ON notificaciones(estado);
CREATE INDEX idx_notificaciones_canal ON notificaciones(canal);
CREATE INDEX idx_notificaciones_fecha ON notificaciones(fecha_creacion);
CREATE INDEX idx_configuracion_usuario ON configuracion_notificaciones(usuario_id);
CREATE INDEX idx_eventos_notificacion_event_id ON eventos_notificacion(event_id);
CREATE INDEX idx_eventos_notificacion_publicado ON eventos_notificacion(publicado);

-- Datos de prueba
INSERT INTO notificaciones (id, usuario_id, nombre_usuario, email_usuario, tipo, titulo, mensaje, canal, prioridad, estado, fecha_creacion, fecha_envio, fecha_lectura, metadata, version) VALUES
(1, 1, 'María González', 'maria.gonzalez@email.com', 'pedido_confirmado', 'Pedido confirmado #ORD-2026-0001', 'Tu pedido ha sido confirmado y está siendo procesado. Recibirás una notificación cuando sea enviado.', 'email', 'alta', 'leida', '2026-08-20 14:30:00', '2026-08-20 14:30:15', '2026-08-20 15:45:00', '{"pedido_id": 1, "monto": 59960}', 1),
(2, 1, 'María González', 'maria.gonzalez@email.com', 'pedido_enviado', 'Tu pedido va en camino! 📦', 'Tu pedido #ORD-2026-0001 ha sido despachado. Número de seguimiento: TRACK-2026-001', 'push', 'alta', 'leida', '2026-08-21 09:00:00', '2026-08-21 09:00:30', '2026-08-21 10:15:00', '{"pedido_id": 1, "tracking": "TRACK-2026-001"}', 1),
(3, 1, 'María González', 'maria.gonzalez@email.com', 'pedido_entregado', 'Pedido entregado ✅', 'Tu pedido #ORD-2026-0001 ha sido entregado exitosamente. Gracias por tu compra!', 'email', 'normal', 'leida', '2026-08-22 11:30:00', '2026-08-22 11:31:00', '2026-08-22 12:00:00', '{"pedido_id": 1}', 1),
(4, 2, 'Carlos Silva', 'carlos.silva@email.com', 'pedido_confirmado', 'Pedido confirmado #ORD-2026-0002', 'Gracias por tu compra! Tu pedido está siendo preparado para el envío.', 'email', 'alta', 'enviada', '2026-08-21 10:20:00', '2026-08-21 10:20:15', NULL, '{"pedido_id": 2, "monto": 30980}', 1),
(5, 2, 'Carlos Silva', 'carlos.silva@email.com', 'pedido_enviado', 'Tu pedido está en camino 🚚', 'Tu pedido #ORD-2026-0002 ha sido despachado. Seguimiento: TRACK-2026-002', 'push', 'alta', 'leida', '2026-08-22 08:30:00', '2026-08-22 08:30:45', '2026-08-22 09:00:00', '{"pedido_id": 2, "tracking": "TRACK-2026-002"}', 1),
(6, 3, 'Ana Rodríguez', 'ana.rodriguez@email.com', 'pedido_confirmado', 'Pedido confirmado #ORD-2026-0003', 'Tu pedido ha sido confirmado. Estamos preparando tus productos Sanrio favoritos!', 'email', 'alta', 'enviada', '2026-08-21 16:35:00', '2026-08-21 16:35:20', NULL, '{"pedido_id": 3, "monto": 63970}', 1);

INSERT INTO configuracion_notificaciones (usuario_id, email_pedidos, email_promociones, email_newsletter, push_pedidos, push_promociones, sms_pedidos, version) VALUES
(1, TRUE, TRUE, FALSE, TRUE, FALSE, FALSE, 1),
(2, TRUE, FALSE, FALSE, TRUE, FALSE, FALSE, 1),
(3, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, 1),
(4, TRUE, FALSE, FALSE, FALSE, FALSE, FALSE, 1),
(5, TRUE, TRUE, FALSE, TRUE, TRUE, FALSE, 1),
(7, TRUE, TRUE, FALSE, TRUE, FALSE, FALSE, 1),
(8, TRUE, FALSE, FALSE, FALSE, FALSE, FALSE, 1);

INSERT INTO eventos_notificacion (event_id, notificacion_id, tipo_evento, payload, publicado, fecha_publicacion) VALUES
('evt_not_001', 1, 'notificacion_creada', '{"usuario_id": 1, "tipo": "pedido_confirmado"}', TRUE, '2026-08-20 14:30:05'),
('evt_not_002', 1, 'notificacion_enviada', '{"canal": "email", "estado": "enviada"}', TRUE, '2026-08-20 14:30:20'),
('evt_not_003', 1, 'notificacion_leida', '{"fecha_lectura": "2026-08-20 15:45:00"}', TRUE, '2026-08-20 15:45:10');