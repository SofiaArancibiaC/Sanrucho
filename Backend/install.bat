@echo off
echo.
echo === REINSTALACION DE DEPENDENCIAS MAVEN ===
echo.

REM Paso 1: Eliminar carpeta local de dependencias
echo Eliminando carpeta .m2 ...
rmdir /s /q %USERPROFILE%\.m2

REM Paso 2: Eliminar carpetas target de los proyectos
echo Eliminando carpetas target ...
rmdir /s /q C:\Sanrucho\eureka\target
rmdir /s /q C:\Sanrucho\ms-usuarios\target
rmdir /s /q C:\Sanrucho\ms-catalogo\target
rmdir /s /q C:\Sanrucho\ms-inventario\target
rmdir /s /q C:\Sanrucho\ms-carrito\target
rmdir /s /q C:\Sanrucho\ms-pedidos\target
rmdir /s /q C:\Sanrucho\ms-pagos\target
rmdir /s /q C:\Sanrucho\ms-despacho\target
rmdir /s /q C:\Sanrucho\ms-notificaciones\target
rmdir /s /q C:\Sanrucho\ms-reportes\target
rmdir /s /q C:\Sanrucho\ms-reviews\target
rmdir /s /q C:\Sanrucho\ms-common\target
rmdir /s /q C:\Sanrucho\ms-api-gateway\target

REM Paso 3: Instalar todas las dependencias forzadamente
echo Descargando dependencias nuevamente con Maven ...
mvn clean install -U -DskipTests

echo.
echo === PROCESO COMPLETADO ===
pause
