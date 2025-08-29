@echo off
REM 🏗️ ARKA Config Server - Start Script (Windows)
REM Script para iniciar el Config Server con dependencias

echo 🏗️ Starting ARKA Config Server...

REM Verificar Java
java -version 2>nul | findstr "21" >nul
if errorlevel 1 (
    echo ❌ Java 21 is required
    exit /b 1
)

REM Verificar prerequisitos
echo 🔍 Checking prerequisites...

REM Verificar si Eureka está corriendo
echo 📡 Checking Eureka Server...
curl -s http://localhost:8761/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ⚠️  Eureka Server not running. Please start Eureka first
    echo 📡 Start Eureka: gradlew :eureka-server:bootRun
    REM Opcional: auto-start Eureka
    REM call scripts\start-eureka.bat
)

REM Compilar Config Server
echo 🔨 Building Config Server...
call gradlew.bat :config-server:clean :config-server:build

if errorlevel 1 (
    echo ❌ Build failed
    exit /b 1
)

REM Crear directorio de logs
if not exist logs mkdir logs

REM Verificar directorio de configuraciones
if not exist "config-repository" (
    echo ❌ config-repository directory not found
    exit /b 1
)

echo 📁 Configuration repository: %cd%\config-repository
echo 🔗 Eureka Discovery: http://localhost:8761

REM Iniciar Config Server
echo 🚀 Starting Config Server on port 8888...
echo 📊 Health check: http://localhost:8888/actuator/health
echo 🏠 Management: http://localhost:8888/actuator

set SPRING_PROFILES_ACTIVE=dev
set CONFIG_REPOSITORY_PATH=%cd%\config-repository

call gradlew.bat :config-server:bootRun ^
    --args="--spring.profiles.active=dev --spring.cloud.config.server.native.search-locations=file:./config-repository --logging.file.name=logs/config-server.log"
