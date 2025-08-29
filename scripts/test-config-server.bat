@echo off
REM 🧪 ARKA Config Server - Complete Test Script (Windows)
REM Script para probar el Config Server y sus configuraciones

echo 🧪 Testing ARKA Config Server...

set CONFIG_SERVER_URL=http://localhost:8888
set EUREKA_URL=http://localhost:8761

echo 🔍 Starting Config Server tests...
echo ==================================================

REM Test 1: Health Check
echo Testing Health Check...
curl -s "%CONFIG_SERVER_URL%/actuator/health" >nul 2>&1
if errorlevel 1 (
    echo ❌ Health Check FAILED
) else (
    echo ✅ Health Check OK
)

REM Test 2: Application Info
echo Testing Application Info...
curl -s "%CONFIG_SERVER_URL%/actuator/info" >nul 2>&1
if errorlevel 1 (
    echo ❌ Application Info FAILED
) else (
    echo ✅ Application Info OK
)

echo.
echo 🔧 Testing Configuration Endpoints...
echo ==================================================

REM Test 3: Default Application Configuration
echo Testing Default Application Config...
curl -s "%CONFIG_SERVER_URL%/application/default" | findstr "spring" >nul 2>&1
if errorlevel 1 (
    echo ❌ Default Application Config FAILED
) else (
    echo ✅ Default Application Config OK
)

REM Test 4: Eureka Server Configuration
echo Testing Eureka Server Config...
curl -s "%CONFIG_SERVER_URL%/eureka-server/dev" | findstr "eureka" >nul 2>&1
if errorlevel 1 (
    echo ❌ Eureka Server Config FAILED
) else (
    echo ✅ Eureka Server Config OK
)

REM Test 5: Config Server own configuration
echo Testing Config Server Config...
curl -s "%CONFIG_SERVER_URL%/config-server/dev" | findstr "config" >nul 2>&1
if errorlevel 1 (
    echo ❌ Config Server Config FAILED
) else (
    echo ✅ Config Server Config OK
)

echo.
echo 🌐 Testing Service Discovery Integration...
echo ==================================================

REM Test 6: Check if Config Server is registered in Eureka
echo Testing Eureka Registration...
curl -s "%EUREKA_URL%/eureka/apps" | findstr "CONFIG-SERVER" >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Config Server not found in Eureka
) else (
    echo ✅ Config Server registered in Eureka
)

echo.
echo 📊 Test Summary
echo ==================================================

REM Final health check
echo 🏥 Final health check:
curl -s "%CONFIG_SERVER_URL%/actuator/health"

echo.
echo 📋 Available Configuration Profiles:
echo - application (default)
echo - eureka-server-dev
echo - api-gateway-dev
echo - config-server-dev
echo - arca-cotizador-dev
echo - arca-gestor-solicitudes-dev
echo - hello-world-service-dev

echo.
echo 🔗 Useful URLs:
echo - Health: %CONFIG_SERVER_URL%/actuator/health
echo - Info: %CONFIG_SERVER_URL%/actuator/info
echo - Environment: %CONFIG_SERVER_URL%/actuator/env
echo - Default Config: %CONFIG_SERVER_URL%/application/default
echo - Service Config: %CONFIG_SERVER_URL%/{service-name}/{profile}

echo.
echo 🎉 Config Server testing completed!
echo ==========================================
echo PROBANDO CONEXIÓN CON CONFIG SERVER
echo ==========================================
echo.

echo Probando conexión con el Config Server remoto...
echo.

echo 1. Verificando configuración de eureka-server:
curl -u config-client:arka-client-2025 "http://127.0.0.1:8761/eureka-server/dev"
echo.
echo.

echo 2. Verificando configuración de api-gateway:
curl -u config-client:arka-client-2025 "http://127.0.0.1:8080/api-gateway/dev"
echo.
echo.

echo 3. Verificando configuración de arca-cotizador:
curl -u config-client:arka-client-2025 "http://127.0.0.1:8081/arca-cotizador/dev"
echo.
echo.

echo 4. Verificando configuración de arca-gestor-solicitudes:
curl -u config-client:arka-client-2025 "http://127.0.0.1:8082/arca-gestor-solicitudes/dev"
echo.
echo.

echo 5. Verificando configuración de hello-world-service:
curl -u config-client:arka-client-2025 "http://127.0.0.1:9090/hello-world-service/dev"
echo.
echo.

echo ==========================================
echo PRUEBAS COMPLETADAS!
echo ==========================================
echo.
echo Si ves las configuraciones JSON arriba, la conexión es exitosa.
echo Si hay errores, revisa:
echo 1. Conectividad a internet
echo 2. Credenciales del Config Server
echo 3. Que el Config Server esté ejecutándose
echo.
pause
