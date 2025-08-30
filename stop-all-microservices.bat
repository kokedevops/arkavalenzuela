@echo off
REM ARKA E-commerce Platform - Detenedor de Microservicios
REM Script para detener todos los microservicios Java

echo 🛑 ARKA E-commerce Platform - Deteniendo Microservicios
echo ======================================================

echo Buscando procesos Java...

REM Obtener lista de procesos Java
for /f "tokens=1,2" %%a in ('tasklist ^| findstr "java.exe"') do (
    echo Encontrado proceso Java: %%a con PID %%b
)

echo.
echo ¿Deseas detener TODOS los procesos Java? (s/n):
set /p choice=

if /i "%choice%"=="s" (
    echo Deteniendo todos los procesos Java...
    taskkill /f /im java.exe 2>nul
    if errorlevel 1 (
        echo ❌ No se encontraron procesos Java ejecutándose
    ) else (
        echo ✅ Todos los procesos Java han sido detenidos
    )
) else (
    echo Operación cancelada
)

echo.
echo Verificando puertos de microservicios...

REM Verificar puertos específicos
set ports=8888 8761 8080 8081 8082 8083

for %%p in (%ports%) do (
    netstat -an | find ":%%p " >nul
    if errorlevel 1 (
        echo ✅ Puerto %%p está libre
    ) else (
        echo ⚠️  Puerto %%p aún está en uso
    )
)

echo.
echo 🔍 Microservicios ARKA y sus puertos:
echo • Config Server:              8888
echo • Eureka Server:              8761  
echo • API Gateway:                8080
echo • Arca Cotizador:             8081
echo • Arca Gestor Solicitudes:    8082
echo • Hello World Service:        8083
echo.

pause
