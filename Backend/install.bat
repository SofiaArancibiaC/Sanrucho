@echo off
echo.
echo === REINSTALACION DE DEPENDENCIAS MAVEN ===
echo.

echo Eliminando carpetas target de los modulos ...
for %%M in (common eureka ms-catalogo ms-pedidos api-gateway) do (
    if exist "%%M\target" rmdir /s /q "%%M\target"
)

echo Descargando dependencias y compilando ...
call mvn -B clean install -U -DskipTests

echo.
echo === PROCESO COMPLETADO ===
pause
