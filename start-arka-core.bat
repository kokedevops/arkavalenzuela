@echo off
REM ARKA E-commerce Core - Aplicación Principal
echo 🛒 Iniciando ARKA E-commerce Core (Aplicación Principal)...
echo ====================================================

set JAVA_OPTS=-Xmx1024m -Xms512m
set PROFILE=k8s
set PORT=8888

echo Configuración:
echo • Perfil: %PROFILE%
echo • Puerto: %PORT%
echo • Memoria: %JAVA_OPTS%
echo.

REM Verificar si el puerto está disponible
netstat -an | find ":%PORT% " >nul
if not errorlevel 1 (
    echo ⚠️  Puerto %PORT% está en uso
    echo ¿Deseas continuar de todas formas? (s/n):
    set /p choice=
    if /i not "%choice%"=="s" (
        echo Operación cancelada
        pause
        exit /b 1
    )
)

echo Ejecutando ARKA E-commerce Core...
echo.

REM Ejecutar el WAR (Spring Boot executable)
java %JAVA_OPTS% -jar build/libs/arkajvalenzuela-0.0.1-SNAPSHOT.war --spring.profiles.active=%PROFILE% --server.port=%PORT%

pause
