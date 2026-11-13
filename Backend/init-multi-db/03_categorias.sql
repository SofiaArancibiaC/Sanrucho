-- 02_categorias.sql
CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

-- Datos de prueba para Categorias
INSERT INTO categorias (nombre, descripcion) VALUES
('Maquillaje', 'Maquillaje de los distintos personajes de Sanrio.'),
('Peluches', 'Los peluches más tiernos del universo.'),
('Llaveros', 'Accesorios para decorar tus llaves.'),
('Blind-box', 'Cajas misteriosas de los personajes de Sanrio.'),
('Papelería', 'Artículos de papelería de los personajes de Sanrio.'),
('Tecnología', 'Productos tecnológicos de los personajes más tiernos.'),
('Hogar', 'Artículos para decorar tu hogar.');

