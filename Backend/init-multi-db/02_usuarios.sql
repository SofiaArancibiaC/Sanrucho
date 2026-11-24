\c usuarios;

-- 01_usuarios.sql
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    run VARCHAR(20),
    nombre VARCHAR(150) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE,
    region_id INTEGER,
    comuna_id INTEGER,
    direccion VARCHAR(255),
    telefono VARCHAR(30),
    estado VARCHAR(20) DEFAULT 'Activo',
    rol VARCHAR(50) DEFAULT 'CLIENTE' CHECK (rol IN ('DUENO', 'ADMIN', 'CLIENTE')),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos de prueba para Usuarios
INSERT INTO usuarios (run, nombre, apellidos, email, password_hash, fecha_nacimiento, region_id, comuna_id, direccion, telefono, estado, rol) VALUES
('11111111-1', 'Admin', 'Sanrucho', 'admin@gmail.com', '$2a$12$lXLUi6BTuDJBLkA84E2pq.Hqgce4H2zrRrqD.Ma/0CsMF.O.1ioNe', '1990-01-01', 1, 1, 'Direccion admin', '912345678', 'Activo', 'ADMIN'),
('22222222-2', 'Dueño', 'Sanrucho', 'dueno@gmail.com', '$2a$12$rG0R5Np7VaSgXVsWP1sHR.9bOxYc2pQzNySW3MiAXszvbgtk4W.HW', '1985-05-10', 1, 1, 'Direccion dueno', '923456789', 'Activo', 'DUENO'),
('33333333-3', 'Camila', 'Soto Perez', 'camila@gmail.com', '$2a$12$0Qn/X60lE2Vh7h5td7qH0OSdJHKQBh/cgQWsB6Oz1RP3eBqzPoaxO', '2000-03-15', 1, 1, 'Direccion camila', '934567890', 'Activo', 'CLIENTE'),
('44444444-4', 'Diego', 'Fernandez Rojas', 'vale.soto@gmail.com', '$2a$12$2rY1gd6tzOApMZnApRwRR.EsLrOnHbe8Ogu4HU55K2diZLyfQW7hC', '1998-07-20', 1, 1, 'Direccion diego', '945678901', 'Activo', 'CLIENTE');
