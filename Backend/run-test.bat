@echo off
echo ===== Iniciando Eureka Server =====
start "EUREKA" java -jar eureka\target\cl-sanrucho-eureka-1.0-SNAPSHOT.jar

timeout /t 15 /nobreak > nul

echo ===== Iniciando Microservicios =====
start "MS-CATALOGO" java -jar ms-catalogo\target\cl-sanrucho-catalogo-0.0.1-SNAPSHOT.jar
start "MS-PEDIDOS"  java -jar ms-pedidos\target\cl-sanrucho-pedidos-0.0.1-SNAPSHOT.jar

timeout /t 20 /nobreak > nul

echo ===== Iniciando API Gateway =====
start "API-GATEWAY" java -jar api-gateway\target\cl-sanrucho-gateway-0.0.1-SNAPSHOT.jar

echo Todos los servicios han sido lanzados.
