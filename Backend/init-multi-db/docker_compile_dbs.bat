@echo off
REM ============================================================
REM Crea todas las bases de datos y carga cada script en su BD.
REM Requiere el contenedor "postgres-db" iniciado (puerto 5433).
REM ============================================================
cd /d "%~dp0"

echo [1/7] Creando bases de datos...
docker exec -i postgres-db psql -U postgres -d postgres -v ON_ERROR_STOP=1 < 01-init.sql || goto :error

echo [2/7] Cargando usuarios...
docker exec -i postgres-db psql -U postgres -d usuarios -v ON_ERROR_STOP=1 < 02_usuarios.sql || goto :error

echo [3/7] Cargando categorias...
docker exec -i postgres-db psql -U postgres -d categorias -v ON_ERROR_STOP=1 < 03_categorias.sql || goto :error

echo [4/7] Cargando productos...
docker exec -i postgres-db psql -U postgres -d productos -v ON_ERROR_STOP=1 < 04_productos.sql || goto :error

echo [5/7] Cargando pedidos...
docker exec -i postgres-db psql -U postgres -d pedidos -v ON_ERROR_STOP=1 < 05_pedidos.sql || goto :error

echo [6/7] Cargando detalle de pedidos...
docker exec -i postgres-db psql -U postgres -d detalle_pedidos -v ON_ERROR_STOP=1 < 06_detalle_pedidos.sql || goto :error

echo [7/7] Cargando transacciones...
docker exec -i postgres-db psql -U postgres -d transacciones -v ON_ERROR_STOP=1 < 07_transacciones.sql || goto :error

echo.
echo Bases de datos cargadas correctamente.
pause
exit /b 0

:error
echo.
echo ERROR al ejecutar los scripts SQL. Revise el mensaje anterior.
pause
exit /b 1
