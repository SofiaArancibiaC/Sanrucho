-- 01_usuarios.sql
CREATE TABLE Usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre_completo VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(50) DEFAULT 'Cliente',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos de prueba para Usuarios
INSERT INTO Usuarios (nombre_completo, email, password_hash, rol) VALUES
('Sofía Arancibia', 'sofia.arancibia@example.com', '$2y$10$hashdepruebaadmin123', 'Admin'),
('Camila Rojas', 'camila.rojas@example.com', '$2y$10$hashdepruebacliente1', 'Cliente'),
('Valentina Soto', 'vale.soto99@example.com', '$2y$10$hashdepruebacliente2', 'Cliente');
