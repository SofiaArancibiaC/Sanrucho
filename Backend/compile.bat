@echo off
echo.
echo === COMPILANDO MICROSERVICIOS ===
echo.

REM %~dp0 = carpeta donde esta este script (no depende de C:\Sanrucho)
call cd /d "%~dp0"
call mvn clean install -U

echo.
echo === COMPILACION COMPLETADA ===
pause
