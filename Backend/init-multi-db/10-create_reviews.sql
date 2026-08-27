-- Microservicio: ms-reviews
-- Base de datos para reseñas de productos
-- Optimizado para Kafka + OpenFeign + PostgreSQL

\c review;

-- Drop tables en orden correcto (primero las dependientes)
DROP TABLE IF EXISTS eventos_review CASCADE;
DROP TABLE IF EXISTS respuestas_reviews CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;

CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    producto_id INTEGER NOT NULL,
    sku VARCHAR(50) NOT NULL,
    nombre_producto VARCHAR(200) NOT NULL,
    usuario_id INTEGER NOT NULL,
    nombre_usuario VARCHAR(200) NOT NULL,
    pedido_id INTEGER NOT NULL,
    numero_pedido VARCHAR(50) NOT NULL,
    calificacion INTEGER NOT NULL CHECK (calificacion >= 1 AND calificacion <= 5),
    titulo VARCHAR(200),
    comentario TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    verificado BOOLEAN DEFAULT FALSE,
    estado VARCHAR(20) DEFAULT 'publicado' CHECK (estado IN ('publicado', 'pendiente', 'rechazado', 'reportado')),
    votos_utiles INTEGER DEFAULT 0,
    votos_no_utiles INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 1
);

CREATE TABLE respuestas_reviews (
    id SERIAL PRIMARY KEY,
    review_id INTEGER NOT NULL,
    usuario_id INTEGER,
    nombre_usuario VARCHAR(200),
    es_vendedor BOOLEAN DEFAULT FALSE,
    comentario TEXT NOT NULL,
    fecha_respuesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE eventos_review (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(100) UNIQUE NOT NULL,
    review_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL CHECK (tipo_evento IN ('review_creada', 'review_actualizada', 'review_respondida', 'review_reportada', 'review_eliminada')),
    payload JSONB NOT NULL,
    publicado BOOLEAN DEFAULT FALSE,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_publicacion TIMESTAMP
);

CREATE INDEX idx_reviews_producto ON reviews(producto_id);
CREATE INDEX idx_reviews_usuario ON reviews(usuario_id);
CREATE INDEX idx_reviews_pedido ON reviews(pedido_id);
CREATE INDEX idx_reviews_estado ON reviews(estado);
CREATE INDEX idx_reviews_calificacion ON reviews(calificacion);
CREATE INDEX idx_reviews_verificado ON reviews(verificado);
CREATE INDEX idx_respuestas_review ON respuestas_reviews(review_id);
CREATE INDEX idx_eventos_review_event_id ON eventos_review(event_id);
CREATE INDEX idx_eventos_review_publicado ON eventos_review(publicado);

-- Datos de prueba
INSERT INTO reviews (id, producto_id, sku, nombre_producto, usuario_id, nombre_usuario, pedido_id, numero_pedido, calificacion, titulo, comentario, verificado, estado, votos_utiles, votos_no_utiles, created_by, version) VALUES
(1, 1, 'HK-PLUSH-001', 'Peluche Hello Kitty Clásico 30cm', 1, 'María González', 1, 'ORD-2026-0001', 5, 'Hermoso peluche!', 'Me encantó el peluche de Hello Kitty. La calidad es excelente, muy suave y el tamaño perfecto. Mi sobrina quedó feliz!', TRUE, 'publicado', 3, 0, 'sistema', 1),
(2, 5, 'HK-NOTE-101', 'Cuaderno Hello Kitty A5 Rayas', 1, 'María González', 1, 'ORD-2026-0001', 4, 'Lindo cuaderno pero algo caro', 'El diseño es precioso y la calidad del papel es buena, pero el precio me pareció un poco alto para un cuaderno A5.', TRUE, 'publicado', 2, 0, 'sistema', 1),
(3, 2, 'KR-PLUSH-002', 'Peluche Kuromi con Capucha 25cm', 2, 'Carlos Silva', 2, 'ORD-2026-0002', 5, 'Kuromi perfecta!', 'Llegó super rápido y es tal cual las fotos. La capucha es adorable y los detalles están muy bien hechos. 100% recomendado!', TRUE, 'publicado', 3, 0, 'sistema', 1),
(4, 3, 'MM-PLUSH-003', 'Peluche My Melody Sleeping 35cm', 3, 'Ana Rodríguez', 3, 'ORD-2026-0003', 5, 'La más linda!', 'My Melody es mi personaje favorito y este peluche superó mis expectativas. Es grande, suave y perfecta para dormir con ella.', TRUE, 'publicado', 3, 0, 'sistema', 1),
(5, 8, 'PP-MUG-301', 'Taza Cerámica Pompompurin 350ml', 5, 'Lucía Fernández', 5, 'ORD-2026-0005', 4, 'Buena taza pero pequeña', 'El diseño 3D de Pompompurin es súper lindo, pero la capacidad es menor de lo que esperaba. Igual la recomiendo.', TRUE, 'publicado', 2, 1, 'sistema', 1),
(6, 2, 'KR-PLUSH-002', 'Peluche Kuromi con Capucha 25cm', 8, 'Jorge Ramírez', 7, 'ORD-2026-0007', 2, 'Llegó con defecto', 'El peluche tenía una costura despegada en la oreja. Tuve que devolverlo.', TRUE, 'publicado', 2, 0, 'sistema', 1);

INSERT INTO respuestas_reviews (review_id, usuario_id, nombre_usuario, es_vendedor, comentario, fecha_respuesta) VALUES
(5, NULL, 'Sanrio Shop', TRUE, 'Muchas gracias por tu comentario! Nos alegra que te haya gustado. La capacidad es de 350ml, ideal para un café o té. 😊', '2026-08-21 15:30:00'),
(6, NULL, 'Sanrio Shop', TRUE, 'Lamentamos mucho este inconveniente. Por favor escríbenos a soporte@sanrioshop.cl con tu número de pedido para gestionar el cambio inmediatamente.', '2026-08-19 17:00:00'),
(3, 4, 'Pedro Martínez', FALSE, 'Yo también compré ese peluche y es precioso! Totalmente de acuerdo con tu reseña.', '2026-08-22 10:30:00');

INSERT INTO eventos_review (event_id, review_id, producto_id, tipo_evento, payload, publicado, fecha_publicacion) VALUES
('evt_rev_001', 1, 1, 'review_creada', '{"usuario_id": 1, "producto_id": 1, "calificacion": 5}', TRUE, '2026-08-22 12:00:00'),
('evt_rev_002', 5, 8, 'review_respondida', '{"review_id": 5, "respondido_por": "vendedor"}', TRUE, '2026-08-21 15:30:10');