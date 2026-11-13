-- 01_usuarios.sql
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(50) DEFAULT 'Cliente' CHECK (rol IN ('Dueño', 'Admin', 'Cliente')),,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos de prueba para Usuarios
INSERT INTO usuarios (nombre, apellidos, email, password_hash, rol) VALUES
('Admin', 'Sanrucho', 'admin@gmail.com', '$2a$12$lXLUi6BTuDJBLkA84E2pq.Hqgce4H2zrRrqD.Ma/0CsMF.O.1ioNe', 'Admin'), --admin123
('Dueño', 'Sanrucho', 'dueno@gmail.com', '$2a$12$rG0R5Np7VaSgXVsWP1sHR.9bOxYc2pQzNySW3MiAXszvbgtk4W.HW', 'Dueño'), --dueno123
('Camila', 'Soto Perez', 'camila@gmail.com', '$2a$12$2rY1gd6tzOApMZnApRwRR.EsLrOnHbe8Ogu4HU55K2diZLyfQW7hC', 'Cliente'), --123456
('Diego', 'Fernandez Rojas', 'vale.soto@gmail.com', '$2a$12$2rY1gd6tzOApMZnApRwRR.EsLrOnHbe8Ogu4HU55K2diZLyfQW7hC', 'Cliente'); --123456
