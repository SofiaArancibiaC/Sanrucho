@echo off
echo ===== Iniciando Eureka Server =====
start "EUREKA" java -jar eureka\target\cl.sanrucho-eureka-1.0-SNAPSHOT.jar --spring.profiles.active=test

timeout /t 5 /nobreak > nul

echo ===== Iniciando Microservicios =====
start "MS-USUARIOS" java -jar ms-usuarios\target\cl.sanrucho-usuarios-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-CATALOGO" java -jar ms-catalogo\target\cl.sanrucho-catalogo-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
start "MS-PEDIDOS" java -jar ms-pedidos\target\cl.sanrucho-pedidos-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
rem Agrega aqui los demas microservicios si necesitas

echo Todos los servicios han sido lanzados.
