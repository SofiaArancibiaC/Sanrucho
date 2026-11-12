-- 03_productos.sql
CREATE TABLE Productos (
    id_producto SERIAL PRIMARY KEY,
    id_categoria INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    imagen_url VARCHAR(255),
    CONSTRAINT fk_categoria
        FOREIGN KEY(id_categoria) 
        REFERENCES Categorias(id_categoria)
        ON DELETE RESTRICT
);

-- Datos de prueba para Productos (relacionados a las categorías anteriores)
INSERT INTO Productos (id_categoria, nombre, descripcion, precio, stock, imagen_url) VALUES
(1, 'Peluche Hello Kitty Clásico 30cm', 'Peluche suave de Hello Kitty con su icónico lazo rojo.', 15990.00, 25, 'https://sanrucho.com/img/hk_peluche.jpg'),
(1, 'Taza de Cerámica Hello Kitty', 'Taza de 350ml apta para microondas.', 8500.00, 40, 'https://sanrucho.com/img/hk_taza.jpg'),
(2, 'Mochila Kuromi Negra', 'Mochila de cuero sintético con detalles morados y orejas de Kuromi.', 29990.00, 15, 'https://sanrucho.com/img/kuromi_mochila.jpg'),
(2, 'Cosmetiquero Kuromi', 'Estuche para maquillaje con cierre y diseño de Kuromi.', 12000.00, 30, 'https://sanrucho.com/img/kuromi_cosmetiquero.jpg'),
(3, 'Pijama Cinnamoroll Talla M', 'Pijama de dos piezas de algodón con estampado de Cinnamoroll.', 24500.00, 20, 'https://sanrucho.com/img/cinna_pijama.jpg'),
(4, 'Lámpara de Noche Pompompurin', 'Lámpara LED recargable con forma de Pompompurin.', 18900.00, 10, 'https://sanrucho.com/img/pom_lampara.jpg');
