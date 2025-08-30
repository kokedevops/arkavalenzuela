@echo off
REM ARKA E-commerce Platform - Ejecutor de Microservicios
REM Script para ejecutar todos los microservicios en el orden correcto

setlocal enabledelayedexpansion

echo 🚀 ARKA E-commerce Platform - Iniciando Microservicios
echo ====================================================

REM Variables de configuración
set JAVA_OPTS=-Xmx512m -Xms256m
set PROFILE=k8s
set BASE_DIR=%~dp0

echo Directorio base: %BASE_DIR%
echo Perfil: %PROFILE%
echo Opciones Java: %JAVA_OPTS%
echo.

REM Función para verificar si un puerto está disponible
:check_port
netstat -an | find ":%1 " >nul
if errorlevel 1 (
    echo ✅ Puerto %1 está disponible
    exit /b 0
) else (
    echo ❌ Puerto %1 está en uso
    exit /b 1
)

REM Función para esperar que un servicio esté listo
:wait_for_service
set url=%1
set service_name=%2
set max_attempts=30
set attempts=0

echo Esperando que %service_name% esté listo en %url%...
:wait_loop
curl -s %url% >nul 2>&1
if not errorlevel 1 (
    echo ✅ %service_name% está listo
    exit /b 0
)

set /a attempts+=1
if %attempts% geq %max_attempts% (
    echo ❌ Timeout esperando %service_name%
    exit /b 1
)

timeout /t 2 >nul
goto wait_loop

REM 1. Config Server (Puerto 8888)
echo.
echo 📋 [1/6] Iniciando Config Server...
call :check_port 8888
if errorlevel 1 (
    echo Deteniendo servicios en puerto 8888...
    taskkill /f /im java.exe 2>nul
    timeout /t 3 >nul
)

start "Config Server" cmd /c "cd /d "%BASE_DIR%" && java %JAVA_OPTS% -jar config-server/build/libs/config-server-1.0.0.jar --spring.profiles.active=%PROFILE% --server.port=8888"
call :wait_for_service "http://localhost:8888/actuator/health" "Config Server"
if errorlevel 1 exit /b 1

REM 2. Eureka Server (Puerto 8761)
echo.
echo 🔍 [2/6] Iniciando Eureka Server...
call :check_port 8761
if errorlevel 1 (
    echo El puerto 8761 está en uso. Continuando...
)

start "Eureka Server" cmd /c "cd /d "%BASE_DIR%" && java %JAVA_OPTS% -jar eureka-server/build/libs/eureka-server.jar --spring.profiles.active=%PROFILE% --server.port=8761"
call :wait_for_service "http://localhost:8761/actuator/health" "Eureka Server"
if errorlevel 1 exit /b 1

REM 3. API Gateway (Puerto 8080)
echo.
echo 🌐 [3/6] Iniciando API Gateway...
call :check_port 8080
if errorlevel 1 (
    echo El puerto 8080 está en uso. Continuando...
)

start "API Gateway" cmd /c "cd /d "%BASE_DIR%" && java %JAVA_OPTS% -jar api-gateway/build/libs/api-gateway.jar --spring.profiles.active=%PROFILE% --server.port=8080"
call :wait_for_service "http://localhost:8080/actuator/health" "API Gateway"
if errorlevel 1 exit /b 1

REM 4. Arca Cotizador (Puerto 8081)
echo.
echo 🧮 [4/6] Iniciando Arca Cotizador...
call :check_port 8081
if errorlevel 1 (
    echo El puerto 8081 está en uso. Continuando...
)

start "Arca Cotizador" cmd /c "cd /d "%BASE_DIR%" && java %JAVA_OPTS% -jar arca-cotizador/build/libs/arca-cotizador-0.0.1-SNAPSHOT.jar --spring.profiles.active=%PROFILE% --server.port=8081"
call :wait_for_service "http://localhost:8081/actuator/health" "Arca Cotizador"
if errorlevel 1 exit /b 1

REM 5. Arca Gestor Solicitudes (Puerto 8082)
echo.
echo 📋 [5/6] Iniciando Arca Gestor Solicitudes...
call :check_port 8082
if errorlevel 1 (
    echo El puerto 8082 está en uso. Continuando...
)

start "Arca Gestor Solicitudes" cmd /c "cd /d "%BASE_DIR%" && java %JAVA_OPTS% -jar arca-gestor-solicitudes/build/libs/arca-gestor-solicitudes-0.0.1-SNAPSHOT.jar --spring.profiles.active=%PROFILE% --server.port=8082"
call :wait_for_service "http://localhost:8082/actuator/health" "Arca Gestor Solicitudes"
if errorlevel 1 exit /b 1

REM 6. Hello World Service (Puerto 8083)
echo.
echo 👋 [6/6] Iniciando Hello World Service...
call :check_port 8083
if errorlevel 1 (
    echo El puerto 8083 está en uso. Continuando...
)

start "Hello World Service" cmd /c "cd /d "%BASE_DIR%" && java %JAVA_OPTS% -jar hello-world-service/build/libs/hello-world-service.jar --spring.profiles.active=%PROFILE% --server.port=8083"
call :wait_for_service "http://localhost:8083/actuator/health" "Hello World Service"
if errorlevel 1 exit /b 1

REM Información final
echo.
echo ✅ TODOS LOS MICROSERVICIOS INICIADOS CORRECTAMENTE
echo ==================================================
echo.
echo 📋 URLs de los servicios:
echo • Config Server:              http://localhost:8888
echo • Eureka Server:              http://localhost:8761  
echo • API Gateway:                http://localhost:8080
echo • Arca Cotizador:             http://localhost:8081
echo • Arca Gestor Solicitudes:    http://localhost:8082
echo • Hello World Service:        http://localhost:8083
echo.
echo 🔍 Para verificar el estado:
echo curl http://localhost:8761  (Eureka Dashboard)
echo curl http://localhost:8080/actuator/health
echo.
echo 🛑 Para detener todos los servicios:
echo ejecuta: stop-all-microservices.bat
echo.

pause
