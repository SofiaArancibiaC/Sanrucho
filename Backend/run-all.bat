@echo off
echo ===== Iniciando Eureka Server =====
start "eureka" mvn -f eureka spring-boot:run

timeout /t 5 /nobreak > nul

echo ===== Iniciando Microservicios =====
start "ms-usuarios" mvn -f ms-usuarios spring-boot:run
start "ms-catalogo" mvn -f ms-catalogo spring-boot:run
start "ms-inventario" mvn -f ms-inventario spring-boot:run
start "ms-carrito" mvn -f ms-carrito spring-boot:run
start "ms-pedidos" mvn -f ms-pedidos spring-boot:run
start "ms-pagos" mvn -f ms-pagos spring-boot:run
start "ms-despacho" mvn -f ms-despacho spring-boot:run
start "ms-notificaciones" mvn -f ms-notificaciones spring-boot:run
start "ms-reportes" mvn -f ms-reportes spring-boot:run
start "ms-reviews" mvn -f ms-reviews spring-boot:run
start "ms-common" mvn -f ms-common spring-boot:run
start "ms-api-gateway" mvn -f ms-api-gateway spring-boot:run
rem Agrega aqui los demas microservicios si necesitas

echo Todos los servicios han sido lanzados.
