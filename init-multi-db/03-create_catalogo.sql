-- Microservicio: ms-catalogo
-- Base de datos para catálogo de productos
-- Optimizado para Kafka + OpenFeign + PostgreSQL

\c catalogo;

-- Drop tables en orden correcto (primero las dependientes)
DROP TABLE IF EXISTS eventos_catalogo CASCADE;
DROP TABLE IF EXISTS especificaciones_producto CASCADE;
DROP TABLE IF EXISTS productos CASCADE;

CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    sku VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    personaje VARCHAR(50) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    imagen_url VARCHAR(500),
    estado VARCHAR(20) DEFAULT 'activo' CHECK (estado IN ('activo', 'inactivo', 'descontinuado')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 1
);

CREATE TABLE especificaciones_producto (
    id SERIAL PRIMARY KEY,
    producto_id INTEGER NOT NULL,
    atributo VARCHAR(100) NOT NULL,
    valor VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE eventos_catalogo (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(100) UNIQUE NOT NULL,
    producto_id INTEGER NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL CHECK (tipo_evento IN ('producto_creado', 'producto_actualizado', 'precio_cambiado', 'stock_cambiado', 'producto_descontinuado')),
    payload JSONB NOT NULL,
    publicado BOOLEAN DEFAULT FALSE,
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_publicacion TIMESTAMP
);

CREATE INDEX idx_productos_sku ON productos(sku);
CREATE INDEX idx_productos_personaje ON productos(personaje);
CREATE INDEX idx_productos_categoria ON productos(categoria);
CREATE INDEX idx_productos_estado ON productos(estado);
CREATE INDEX idx_especificaciones_producto ON especificaciones_producto(producto_id);
CREATE INDEX idx_eventos_catalogo_event_id ON eventos_catalogo(event_id);
CREATE INDEX idx_eventos_catalogo_publicado ON eventos_catalogo(publicado);

-- Datos de prueba
INSERT INTO productos (id, sku, nombre, descripcion, precio, personaje, categoria, imagen_url, estado, created_by, version) VALUES
(1, 'HK-PLUSH-001', 'Peluche Hello Kitty Clásico 30cm', 'Peluche suave de Hello Kitty con su icónico moño rojo', 24990, 'Hello Kitty', 'Peluches', '/images/hello-kitty-plush-30.jpg', 'activo', 'admin', 1),
(2, 'KR-PLUSH-002', 'Peluche Kuromi con Capucha 25cm', 'Kuromi adorable con su capucha de conejito negra', 22990, 'Kuromi', 'Peluches', '/images/kuromi-plush-25.jpg', 'activo', 'admin', 1),
(3, 'MM-PLUSH-003', 'Peluche My Melody Sleeping 35cm', 'My Melody en posición de dormir, súper suave', 27990, 'My Melody', 'Peluches', '/images/mymelody-sleep-35.jpg', 'activo', 'admin', 1),
(4, 'CN-PLUSH-004', 'Peluche Cinnamoroll Nube 40cm', 'Cinnamoroll esponjoso con orejas largas', 29990, 'Cinnamoroll', 'Peluches', '/images/cinnamoroll-cloud-40.jpg', 'activo', 'admin', 1),
(5, 'HK-NOTE-101', 'Cuaderno Hello Kitty A5 Rayas', 'Cuaderno con rayas, 80 hojas, diseño Hello Kitty', 5990, 'Hello Kitty', 'Papelería', '/images/hello-kitty-notebook.jpg', 'activo', 'admin', 1),
(6, 'KR-NOTE-102', 'Cuaderno Kuromi A5 Cuadriculado', 'Cuaderno cuadriculado, 100 hojas, diseño Kuromi punk', 6490, 'Kuromi', 'Papelería', '/images/kuromi-notebook.jpg', 'activo', 'admin', 1),
(7, 'MM-PEN-201', 'Set Bolígrafos Gel My Melody 6 unidades', 'Set de 6 bolígrafos de colores pastel con diseño My Melody', 8990, 'My Melody', 'Papelería', '/images/mymelody-pen-set.jpg', 'activo', 'admin', 1),
(8, 'PP-MUG-301', 'Taza Cerámica Pompompurin 350ml', 'Taza de cerámica con diseño 3D de Pompompurin', 12990, 'Pompompurin', 'Hogar', '/images/pompompurin-mug.jpg', 'activo', 'admin', 1),
(9, 'HK-BAG-401', 'Mochila Hello Kitty Mini Rosa', 'Mochila mini de 25cm perfecta para salidas', 19990, 'Hello Kitty', 'Accesorios', '/images/hello-kitty-backpack.jpg', 'activo', 'admin', 1),
(10, 'CN-PILLO-302', 'Cojín Cinnamoroll 40x40cm', 'Cojín decorativo suave con forma de Cinnamoroll', 16990, 'Cinnamoroll', 'Hogar', '/images/cinnamoroll-pillow.jpg', 'activo', 'admin', 1),
(11, 'LTS-PLUSH-005', 'Peluche Little Twin Stars Set', 'Set de Kiki y Lala 20cm cada uno', 34990, 'Little Twin Stars', 'Peluches', '/images/little-twin-stars-set.jpg', 'activo', 'admin', 1),
(12, 'BM-WALLET-402', 'Cartera Badtz-Maru Negro', 'Cartera de material sintético con múltiples compartimientos', 13990, 'Badtz-Maru', 'Accesorios', '/images/badtz-maru-wallet.jpg', 'activo', 'admin', 1);

INSERT INTO especificaciones_producto (producto_id, atributo, valor) VALUES
(1, 'Material', 'Poliéster 100%'),
(1, 'Altura', '30cm'),
(1, 'Cuidado', 'Lavar a mano'),
(2, 'Material', 'Poliéster suave'),
(2, 'Altura', '25cm'),
(3, 'Material', 'Felpa premium'),
(3, 'Dimensiones', '35x20cm'),
(4, 'Material', 'Poliéster esponjoso'),
(4, 'Altura', '40cm'),
(5, 'Páginas', '80 hojas'),
(5, 'Tamaño', 'A5 (14.8 x 21cm)'),
(6, 'Páginas', '100 hojas'),
(7, 'Cantidad', '6 unidades'),
(8, 'Capacidad', '350ml'),
(9, 'Dimensiones', '25 x 20 x 10cm'),
(10, 'Dimensiones', '40 x 40cm'),
(11, 'Cantidad', '2 peluches'),
(12, 'Dimensiones', '11 x 9 x 2cm');

INSERT INTO eventos_catalogo (event_id, producto_id, tipo_evento, payload, publicado, fecha_publicacion) VALUES
('evt_cat_001', 1, 'producto_creado', '{"sku": "HK-PLUSH-001", "nombre": "Peluche Hello Kitty Clásico 30cm", "precio": 24990}', TRUE, '2026-08-01 10:00:00'),
('evt_cat_002', 8, 'precio_cambiado', '{"sku": "PP-MUG-301", "precio_anterior": 14990, "precio_nuevo": 12990}', TRUE, '2026-08-15 14:30:00');