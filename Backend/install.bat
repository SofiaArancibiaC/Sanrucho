@echo off
echo.
echo === REINSTALACION DE DEPENDENCIAS MAVEN ===
echo.

REM %~dp0 = carpeta donde esta este script (no depende de C:\Sanrucho)

REM Paso 1: Eliminar carpeta local de dependencias
echo Eliminando carpeta .m2 ...
rmdir /s /q %USERPROFILE%\.m2

REM Paso 2: Eliminar carpetas target de los proyectos
echo Eliminando carpetas target ...
rmdir /s /q "%~dp0common\target"
rmdir /s /q "%~dp0eureka\target"
rmdir /s /q "%~dp0ms-catalogo\target"
rmdir /s /q "%~dp0ms-pedidos\target"
rmdir /s /q "%~dp0ms-pagos\target"
rmdir /s /q "%~dp0api-gateway\target"

REM Paso 3: Instalar todas las dependencias forzadamente
echo Descargando dependencias nuevamente con Maven ...
call cd /d "%~dp0"
call mvn clean install -U -DskipTests

echo.
echo === PROCESO COMPLETADO ===
pause
