@echo off
echo.
echo === COMPILANDO MICROSERVICIOS ===
echo.
call cd C:\Sanrucho\ms-usuarios
call mvn clean install -U
call cd C:\Sanrucho\ms-catalogo
call mvn clean install -U
call cd C:\Sanrucho\ms-pedidos
call mvn clean install -U
echo.
echo === COMPILACION COMPLETADA ===
pause
