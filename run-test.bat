@echo off
echo ===== Iniciando Eureka Server =====
start "EUREKA" java -jar eureka\target\cl.sanrucho-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=test

timeout /t 5 /nobreak > nul

echo ===== Iniciando Microservicios =====
start "MS-USUARIOS" java -jar ms-usuarios\target\cl.sanrucho-usuarios-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-CATALOGO" java -jar ms-catalogo\target\cl.sanrucho-catalogo-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-INVENTARIO" java -jar ms-inventario\target\cl.sanrucho-inventario-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-CARRITO" java -jar ms-carrito\target\cl.sanrucho-carrito-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-PEDIDOS" java -jar ms-pedidos\target\cl.sanrucho-pedidos-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-PAGOS" java -jar ms-pagos\target\cl.sanrucho-pagos-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-DESPACHO" java -jar ms-despacho\target\cl.sanrucho-despacho-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-NOTIFICACIONES" java -jar ms-notificaciones\target\cl.sanrucho-notificaciones-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-REPORTES" java -jar ms-reportes\target\cl.sanrucho-reportes-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-REVIEWS" java -jar ms-reviews\target\cl.sanrucho-reviews-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-COMMON" java -jar ms-common\target\cl.sanrucho-common-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-API-GATEWAY" java -jar ms-api-gateway\target\cl.sanrucho-api-gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
rem Agrega aqui los demas microservicios si necesitas

echo Todos los servicios han sido lanzados.
