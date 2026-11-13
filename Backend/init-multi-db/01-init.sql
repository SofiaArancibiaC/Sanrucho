-- ES FUNDAMENTAL EJECUTAR ESTE SCRIPT QUE PERMITE ELIMINAR LAS BASES DE DATOS
-- SI ES QUE EXISTEN, PARA LUEGO CREARLAS LIMPIAS SIN TABLAS Y DESDE CERO

SELECT 'CREATE DATABASE usuarios'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'usuarios') \gexec

SELECT 'CREATE DATABASE categorias'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'catalogo') \gexec

SELECT 'CREATE DATABASE pedidos'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'pedidos') \gexec

SELECT 'CREATE DATABASE productos'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'productos') \gexec

SELECT 'CREATE DATABASE detalle_pedidos'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'detalle_pedidos') \gexec

SELECT 'CREATE DATABASE transacciones'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'transacciones') \gexec
