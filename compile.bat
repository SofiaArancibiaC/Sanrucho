@echo off
echo.
echo === COMPILANDO MICROSERVICIOS ===
echo.
call cd C:\Sanrucho\ms-usuarios
call mvn clean install -U
call cd C:\Sanrucho\ms-catalogo
call mvn clean install -U
call cd C:\Sanrucho\ms-inventario
call mvn clean install -U
call cd C:\Sanrucho\ms-carrito
call mvn clean install -U
call cd C:\Sanrucho\ms-pedidos
call mvn clean install -U
call cd C:\Sanrucho\ms-pagos
call mvn clean install -U
call cd C:\Sanrucho\ms-despacho
call mvn clean install -U
call cd C:\Sanrucho\ms-notificaciones
call mvn clean install -U
call cd C:\Sanrucho\ms-reportes
call mvn clean install -U
call cd C:\Sanrucho\ms-reviews
call mvn clean install -U
call cd C:\Sanrucho\ms-common
call mvn clean install -U
call cd C:\Sanrucho\ms-api-gateway
call mvn clean install -U
echo.
echo === COMPILACION COMPLETADA ===
pause
