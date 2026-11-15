@echo off
echo.
echo === COMPILANDO MICROSERVICIOS (reactor Maven) ===
echo.
call mvn -B clean install -DskipTests
echo.
echo === COMPILACION COMPLETADA ===
pause
