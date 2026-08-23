SELECT 'CREATE DATABASE carrito'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'carrito') \gexec

SELECT 'CREATE DATABASE catalogo'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'catalogo') \gexec

SELECT 'CREATE DATABASE despacho'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'despacho') \gexec

SELECT 'CREATE DATABASE inventario'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'inventario') \gexec

SELECT 'CREATE DATABASE notificacion'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'notificacion') \gexec

SELECT 'CREATE DATABASE pago'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'pago') \gexec

SELECT 'CREATE DATABASE pedido'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'pedido') \gexec

SELECT 'CREATE DATABASE reporte'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'reporte') \gexec

SELECT 'CREATE DATABASE review'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'review') \gexec

SELECT 'CREATE DATABASE usuario'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'usuario') \gexec
