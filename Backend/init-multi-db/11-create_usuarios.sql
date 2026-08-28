-- Microservicio: ms-usuarios
-- Base de datos para gestión de usuarios

\c usuario;

-- Drop tables en orden correcto (primero las dependientes)
DROP TABLE IF EXISTS eventos_usuario CASCADE;
DROP TABLE IF EXISTS direcciones CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    nombre_completo VARCHAR(201) GENERATED ALWAYS AS (nombre || ' ' || apellido) STORED,
    telefono VARCHAR(20),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'activo' CHECK (estado IN ('activo', 'inactivo', 'suspendido')),
    notificaciones_email BOOLEAN DEFAULT TRUE,
    notificaciones_push BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 1,
    rol VARCHAR(100) CHECK (rol IN ('ADMIN', 'CLIENTE', 'DUEÑO'))
);

CREATE TABLE direcciones (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    tipo VARCHAR(20) CHECK (tipo IN ('envio', 'facturacion')),
    direccion TEXT NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(20),
    pais VARCHAR(100) DEFAULT 'Chile',
    es_principal BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE TABLE eventos_usuario (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(100) UNIQUE NOT NULL,
    usuario_id INTEGER NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL CHECK (tipo_evento IN ('usuario_registrado', 'usuario_actualizado', 'direccion_agregada', 'preferencias_actualizadas', 'usuario_suspendido')),
    payload JSONB NOT NULL,
    publicado BOOLEAN DEFAULT FALSE,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_publicacion TIMESTAMP
);

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_estado ON usuarios(estado);
CREATE INDEX idx_direcciones_usuario ON direcciones(usuario_id);
CREATE INDEX idx_direcciones_principal ON direcciones(es_principal);
CREATE INDEX idx_eventos_usuario_event_id ON eventos_usuario(event_id);
CREATE INDEX idx_eventos_usuario_publicado ON eventos_usuario(publicado);

-- Datos de prueba
INSERT INTO usuarios (id, email, nombre, apellido, telefono, estado, created_by, version, rol) VALUES
(1, 'maria.gonzalez@email.com', 'María', 'González', '+56912345678', 'activo', 'sistema', 1, 'CLIENTE'),
(2, 'carlos.silva@email.com', 'Carlos', 'Silva', '+56987654321', 'activo', 'sistema', 1, 'CLIENTE'),
(3, 'ana.rodriguez@email.com', 'Ana', 'Rodríguez', '+56923456789', 'activo', 'sistema', 1, 'CLIENTE'),
(4, 'pedro.martinez@email.com', 'Pedro', 'Martínez', '+56934567890', 'activo', 'sistema', 1, 'CLIENTE'),
(5, 'lucia.fernandez@email.com', 'Lucía', 'Fernández', '+56945678901', 'activo', 'sistema', 1, 'ADMIN'),
(6, 'diego.torres@email.com', 'Diego', 'Torres', '+56956789012', 'inactivo', 'sistema', 1, 'ADMIN'),
(7, 'sofia.morales@email.com', 'Sofía', 'Morales', '+56967890123', 'activo', 'sistema', 1, 'DUEÑO'),
(8, 'jorge.ramirez@email.com', 'Jorge', 'Ramírez', '+56978901234', 'activo', 'sistema', 1, 'DUEÑO');

INSERT INTO direcciones (id, usuario_id, tipo, direccion, ciudad, region, codigo_postal, es_principal, version) VALUES
(1, 1, 'envio', 'Av. Providencia 1234, Depto 501', 'Santiago', 'Región Metropolitana', '7500000', TRUE, 1),
(2, 1, 'facturacion', 'Av. Providencia 1234, Depto 501', 'Santiago', 'Región Metropolitana', '7500000', FALSE, 1),
(3, 2, 'envio', 'Calle Los Aromos 567', 'Viña del Mar', 'Valparaíso', '2520000', TRUE, 1),
(4, 3, 'envio', 'Pasaje La Florida 890', 'Concepción', 'Biobío', '4030000', TRUE, 1),
(5, 4, 'envio', 'Av. Libertador Bernardo O''Higgins 2345', 'Santiago', 'Región Metropolitana', '8320000', TRUE, 1),
(6, 5, 'envio', 'Los Castaños 456', 'La Serena', 'Coquimbo', '1700000', TRUE, 1),
(7, 7, 'envio', 'Av. Apoquindo 3456', 'Santiago', 'Región Metropolitana', '7550000', TRUE, 1),
(8, 8, 'envio', 'Calle Zenteno 789', 'Valdivia', 'Los Ríos', '5090000', TRUE, 1);

INSERT INTO eventos_usuario (event_id, usuario_id, tipo_evento, payload, publicado, fecha_publicacion) VALUES
('evt_usr_001', 1, 'usuario_registrado', '{"email": "maria.gonzalez@email.com", "nombre": "María González"}', TRUE, '2026-07-15 10:00:00'),
('evt_usr_002', 1, 'direccion_agregada', '{"direccion_id": 1, "tipo": "envio", "ciudad": "Santiago"}', TRUE, '2026-07-15 10:05:00');