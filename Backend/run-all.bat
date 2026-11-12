@echo off
echo ===== Iniciando Eureka Server =====
start "eureka" mvn -f eureka spring-boot:run

timeout /t 5 /nobreak > nul

echo ===== Iniciando Microservicios =====
start "ms-usuarios" mvn -f ms-usuarios spring-boot:run
start "ms-catalogo" mvn -f ms-catalogo spring-boot:run
start "ms-pedidos" mvn -f ms-pedidos spring-boot:run
rem Agrega aqui los demas microservicios si necesitas

echo Todos los servicios han sido lanzados.
