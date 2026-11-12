-- 02_categorias.sql
CREATE TABLE Categorias (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

-- Datos de prueba para Categorias
INSERT INTO Categorias (nombre, descripcion) VALUES
('Hello Kitty', 'Productos clásicos y exclusivos de Hello Kitty.'),
('Kuromi', 'Artículos con temática de Kuromi, estilo punk y tierno.'),
('Cinnamoroll', 'Accesorios y peluches del perrito blanco Cinnamoroll.'),
('Pompompurin', 'Mercancía del adorable perrito golden retriever Pompompurin.');
