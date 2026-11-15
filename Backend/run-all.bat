@echo off
REM ============================================================
REM Levanta el stack completo usando los JAR ya compilados.
REM Requisitos: mvn -B -DskipTests package
REM Orden: Eureka -> microservicios -> gateway
REM ============================================================

echo ===== Iniciando Eureka Server =====
start "eureka" java -jar eureka\target\cl-sanrucho-eureka-1.0-SNAPSHOT.jar

timeout /t 15 /nobreak > nul

echo ===== Iniciando Microservicios =====
start "ms-catalogo" java -jar ms-catalogo\target\cl-sanrucho-catalogo-0.0.1-SNAPSHOT.jar
start "ms-pedidos"  java -jar ms-pedidos\target\cl-sanrucho-pedidos-0.0.1-SNAPSHOT.jar

timeout /t 20 /nobreak > nul

echo ===== Iniciando API Gateway =====
start "api-gateway" java -jar api-gateway\target\cl-sanrucho-gateway-0.0.1-SNAPSHOT.jar

echo.
echo Servicios lanzados:
echo   Eureka      http://localhost:8761
echo   Catalogo    http://localhost:9002/swagger-ui.html
echo   Pedidos     http://localhost:9003/swagger-ui.html
echo   Gateway     http://localhost:9000
