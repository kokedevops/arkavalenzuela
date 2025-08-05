@echo off
echo Starting all microservices...

echo.
echo ==========================================
echo Starting Eureka Server (Port 8761)...
echo ==========================================
start "Eureka Server" cmd /k "cd /d %~dp0.. && gradlew :eureka-server:bootRun"

echo Waiting for Eureka Server to start...
timeout /t 30 /nobreak

echo.
echo ==========================================
echo Starting API Gateway (Port 8080)...
echo ==========================================
start "API Gateway" cmd /k "cd /d %~dp0.. && gradlew :api-gateway:bootRun"

echo Waiting for API Gateway to start...
timeout /t 15 /nobreak

echo.
echo ==========================================
echo Starting Hello World Service (Port 8083)...
echo ==========================================
start "Hello World Service" cmd /k "cd /d %~dp0.. && gradlew :hello-world-service:bootRun"

echo.
echo ==========================================
echo Starting Arca Cotizador (Port 8081)...
echo ==========================================
start "Arca Cotizador" cmd /k "cd /d %~dp0.. && gradlew :arca-cotizador:bootRun"

echo.
echo ==========================================
echo Starting Arca Gestor Solicitudes (Port 8082)...
echo ==========================================
start "Arca Gestor Solicitudes" cmd /k "cd /d %~dp0.. && gradlew :arca-gestor-solicitudes:bootRun"

echo.
echo ==========================================
echo All services are starting...
echo.
echo Eureka Dashboard: http://localhost:8761
echo API Gateway: http://localhost:8080
echo Hello World: http://localhost:8080/api/hello
echo Cotizador: http://localhost:8080/api/cotizador
echo Gestor: http://localhost:8080/api/gestor
echo ==========================================

pause
