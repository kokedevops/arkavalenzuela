@echo off
REM ARKA E-commerce Platform - Docker Images Build Script for Windows
REM Script para construir todas las imágenes Docker para Kubernetes

setlocal enabledelayedexpansion

echo 🐳 ARKA E-commerce Platform - Docker Images Build
echo ================================================

REM Variables de configuración
set REGISTRY=arka
set VERSION=%1
if "%VERSION%"=="" set VERSION=latest
set BUILD_ALL=%2
if "%BUILD_ALL%"=="" set BUILD_ALL=true

echo Iniciando construcción de imágenes Docker para ARKA Platform...
echo Versión: %VERSION%
echo Registry: %REGISTRY%
echo.

REM Verificar que Docker esté funcionando
echo [INFO] Verificando Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker no está funcionando. Por favor inicia Docker.
    exit /b 1
)
echo [SUCCESS] Docker está disponible

REM Compilar aplicaciones
echo [INFO] Compilando aplicaciones...

echo [INFO] Compilando E-commerce Core...
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo [ERROR] Error compilando E-commerce Core
    exit /b 1
)

REM Compilar microservicios
for %%s in (config-server eureka-server api-gateway arca-cotizador arca-gestor-solicitudes hello-world-service) do (
    if exist "%%s" (
        echo [INFO] Compilando %%s...
        cd %%s
        call ..\gradlew.bat clean build -x test
        if errorlevel 1 (
            echo [ERROR] Error compilando %%s
            cd ..
            exit /b 1
        )
        cd ..
    )
)

echo [SUCCESS] Todas las aplicaciones compiladas exitosamente

REM Construir imagen base si existe
if exist "Dockerfile.base" (
    echo [INFO] Construyendo imagen base...
    docker build -f Dockerfile.base -t %REGISTRY%/base:%VERSION% .
    if errorlevel 1 (
        echo [ERROR] Error construyendo imagen base
        exit /b 1
    )
    echo [SUCCESS] Imagen base construida: %REGISTRY%/base:%VERSION%
)

REM Construir E-commerce Core
echo [INFO] Construyendo E-commerce Core...
docker build -t %REGISTRY%/ecommerce-core:%VERSION% --build-arg JAR_FILE=build/libs/arkavalenzuela-0.0.1-SNAPSHOT.jar .
if errorlevel 1 (
    echo [ERROR] Error construyendo E-commerce Core
    exit /b 1
)
echo [SUCCESS] E-commerce Core imagen construida: %REGISTRY%/ecommerce-core:%VERSION%

REM Construir microservicios
for %%s in (config-server eureka-server api-gateway arca-cotizador arca-gestor-solicitudes hello-world-service) do (
    if exist "%%s" (
        echo [INFO] Construyendo %%s...
        cd %%s
        docker build -t %REGISTRY%/%%s:%VERSION% .
        if errorlevel 1 (
            echo [ERROR] Error construyendo %%s
            cd ..
            exit /b 1
        )
        cd ..
        echo [SUCCESS] %%s imagen construida: %REGISTRY%/%%s:%VERSION%
    )
)

REM Probar imágenes
echo [INFO] Probando imágenes construidas...
for %%s in (ecommerce-core config-server eureka-server api-gateway arca-cotizador arca-gestor-solicitudes hello-world-service) do (
    docker images | findstr "%REGISTRY%/%%s" >nul
    if not errorlevel 1 (
        echo [INFO] Probando imagen %REGISTRY%/%%s:%VERSION%...
        docker run --rm %REGISTRY%/%%s:%VERSION% echo Image test successful >nul 2>&1
        if not errorlevel 1 (
            echo [SUCCESS] ✅ %REGISTRY%/%%s:%VERSION% - OK
        ) else (
            echo [WARNING] ⚠️  %REGISTRY%/%%s:%VERSION% - Posible problema
        )
    )
)

REM Mostrar imágenes construidas
echo.
echo [INFO] Imágenes construidas:
docker images | findstr "^%REGISTRY%"

REM Mostrar estadísticas
echo.
echo [INFO] Estadísticas de imágenes construidas:
echo.
echo IMAGEN                         TAMAÑO          CREADA
echo ------------------------------------------------------
for %%s in (ecommerce-core config-server eureka-server api-gateway arca-cotizador arca-gestor-solicitudes hello-world-service) do (
    docker images | findstr "%REGISTRY%/%%s" >nul
    if not errorlevel 1 (
        for /f "tokens=1,2,4" %%a in ('docker images --format "table {{.Repository}}:{{.Tag}} {{.Size}} {{.CreatedSince}}" ^| findstr "%REGISTRY%/%%s:%VERSION%"') do (
            echo %%a %%b %%c
        )
    )
)

REM Limpieza opcional
echo.
set /p cleanup="¿Deseas limpiar imágenes viejas? (y/n): "
if /i "%cleanup%"=="y" (
    echo [INFO] Limpiando imágenes viejas...
    docker image prune -f >nul
    docker container prune -f >nul
    echo [SUCCESS] Limpieza completada
)

REM Push opcional
echo.
set /p registry_url="¿Deseas pushear las imágenes a un registry? (ingresa URL o presiona Enter para saltar): "
if not "%registry_url%"=="" (
    echo [INFO] Pusheando imágenes a registry: %registry_url%
    for %%s in (ecommerce-core config-server eureka-server api-gateway arca-cotizador arca-gestor-solicitudes hello-world-service) do (
        docker images | findstr "%REGISTRY%/%%s" >nul
        if not errorlevel 1 (
            echo [INFO] Pusheando %REGISTRY%/%%s:%VERSION%...
            docker tag %REGISTRY%/%%s:%VERSION% %registry_url%/%%s:%VERSION%
            docker tag %REGISTRY%/%%s:%VERSION% %registry_url%/%%s:latest
            docker push %registry_url%/%%s:%VERSION%
            docker push %registry_url%/%%s:latest
            echo [SUCCESS] ✅ %registry_url%/%%s pushed
        )
    )
)

echo.
echo [SUCCESS] 🎉 Construcción de imágenes Docker completada!
echo.
echo 📋 Para desplegar en Kubernetes:
echo    k8s\deploy-k8s.bat
echo.
echo 🔍 Para verificar imágenes:
echo    docker images ^| findstr arka

REM Mostrar ayuda si se solicita
if "%1"=="--help" goto :help
if "%1"=="-h" goto :help
goto :end

:help
echo ARKA E-commerce Platform - Docker Build Script
echo.
echo Uso: %0 [VERSION] [BUILD_ALL]
echo.
echo Parámetros:
echo   VERSION     Versión de las imágenes (default: latest)
echo   BUILD_ALL   Construir todas las imágenes (default: true)
echo.
echo Ejemplos:
echo   %0                    # Construir con version 'latest'
echo   %0 v1.0.0            # Construir con version 'v1.0.0'
echo   %0 dev false         # Construir solo imágenes modificadas
echo.
echo Servicios que se construyen:
echo   • ecommerce-core
echo   • config-server
echo   • eureka-server
echo   • api-gateway
echo   • arca-cotizador
echo   • arca-gestor-solicitudes
echo   • hello-world-service

:end
pause
