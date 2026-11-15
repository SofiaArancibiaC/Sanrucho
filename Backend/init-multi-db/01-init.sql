-- ============================================================
-- 01-init.sql
-- Crea una base de datos por tabla (diseno elegido para el proyecto).
-- Ejecutar conectado a la base "postgres".
-- ============================================================

-- Usuarios (autenticacion)
SELECT 'CREATE DATABASE usuarios'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'usuarios') \gexec

-- Categorias (ms-catalogo - datasource secundario)
SELECT 'CREATE DATABASE categorias'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'categorias') \gexec

-- Productos (ms-catalogo - datasource primario)
SELECT 'CREATE DATABASE productos'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'productos') \gexec

-- Pedidos (ms-pedidos - datasource primario)
SELECT 'CREATE DATABASE pedidos'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'pedidos') \gexec

-- Detalle de pedidos (ms-pedidos - datasource secundario)
SELECT 'CREATE DATABASE detalle_pedidos'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'detalle_pedidos') \gexec

-- Transacciones (futuro microservicio de pagos)
SELECT 'CREATE DATABASE transacciones'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'transacciones') \gexec
