-- 03_productos.sql
CREATE TABLE productos (
    id_producto SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    id_categoria INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10) NOT NULL,
    oferta BOOLEAN DEFAULT FALSE,
    precio_oferta NUMERIC(10),
    descuento INT,
    stock INT NOT NULL DEFAULT 0,
    stock_critico INT NOT NULL DEFAULT 1,
    imagen_url VARCHAR(255),
    estado VARCHAR(30) DEFAULT 'Activo' CHECK (estado IN ('Activo', 'Inactivo')),
    CONSTRAINT fk_categoria
        FOREIGN KEY(id_categoria) 
        REFERENCES Categorias(id_categoria)
        ON DELETE RESTRICT
);

-- Datos de prueba para Productos extraídos de PRODUCTOS_BASE
INSERT INTO productos (
    codigo, id_categoria, nombre, descripcion, precio, 
    oferta, precio_oferta, descuento, stock, stock_critico, 
    imagen_url, estado
) VALUES
('HK-PLUSH-001', 2, 'Peluche Hello Kitty Clásico 30cm', 'Peluche suave de Hello Kitty con su icónico moño rojo.', 24990, TRUE, 19990, 20, 15, 3, '/static/img/productos/hello-kitty1.webp', 'Activo'),
('KR-PLUSH-002', 2, 'Peluche Kuromi con Capucha 25cm', 'Kuromi adorable con su capucha de conejito negra.', 22990, TRUE, 17990, 22, 20, 5, '/static/img/productos/kuromi-peluche1.webp', 'Activo'),
('MM-PLUSH-003', 2, 'Peluche My Melody Sleeping 35cm', 'My Melody en posición de dormir, súper suave.', 27990, TRUE, 21990, 21, 8, 2, '/static/img/productos/melody-peluche1.webp', 'Activo'),
('CN-PLUSH-004', 2, 'Peluche Cinnamoroll Nube 40cm', 'Cinnamoroll esponjoso con orejas largas.', 29990, FALSE, NULL, NULL, 12, 4, '/static/img/productos/peluche-cinna-verano.webp', 'Activo'),
('HK-NOTE-101', 5, 'Cuaderno Hello Kitty A5 Rayas', 'Cuaderno con rayas, 80 hojas, diseño Hello Kitty.', 5990, FALSE, NULL, NULL, 10, 3, '/static/img/productos/cuaderno-hk1.jpg', 'Activo'),
('PP-MUG-301', 7, 'Taza Cerámica Pompompurin 350ml', 'Taza de cerámica con diseño 3D de Pompompurin.', 12990, FALSE, NULL, NULL, 25, 5, '/static/img/productos/tazon-ppm1.jpg', 'Activo'),
('BM-WALLET-402', 7, 'Cartera Badtz-Maru Negro', 'Cartera de material sintético con múltiples compartimientos.', 13990, FALSE, NULL, NULL, 2, 3, '/static/img/productos/monedero-bm1.webp', 'Activo'),
('LTS-PLUSH-005', 2, 'Peluche Little Twin Stars Set', 'Set de Kiki y Lala 20cm cada uno.', 34990, FALSE, NULL, NULL, 6, 2, '/static/img/productos/lts-peluche1.jpg', 'Activo');