@echo off
echo Starting Arka Microservices with Spring Cloud...

echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd /d %~dp0.. && gradlew :eureka-server:bootRun"

echo Waiting for Eureka Server to start...
timeout /t 30 /nobreak

echo Starting API Gateway...
start "API Gateway" cmd /k "cd /d %~dp0.. && gradlew :api-gateway:bootRun"

echo Starting Hello World Service...
start "Hello World Service" cmd /k "cd /d %~dp0.. && gradlew :hello-world-service:bootRun"

echo Starting Arca Cotizador (Instance 1)...
start "Arca Cotizador 1" cmd /k "cd /d %~dp0.. && gradlew :arca-cotizador:bootRun"

echo Starting Arca Gestor Solicitudes (Instance 1)...
start "Arca Gestor 1" cmd /k "cd /d %~dp0.. && gradlew :arca-gestor-solicitudes:bootRun"

echo Waiting for services to start...
timeout /t 15 /nobreak

echo Starting Arca Cotizador (Instance 2) on port 8091...
start "Arca Cotizador 2" cmd /k "cd /d %~dp0.. && gradlew :arca-cotizador:bootRun --args='--server.port=8091 --eureka.instance.instance-id=arca-cotizador:8091'"

echo Starting Arca Gestor Solicitudes (Instance 2) on port 8092...
start "Arca Gestor 2" cmd /k "cd /d %~dp0.. && gradlew :arca-gestor-solicitudes:bootRun --args='--server.port=8092 --eureka.instance.instance-id=arca-gestor-solicitudes:8092'"

echo.
echo All services are starting...
echo.
echo Service URLs:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - Hello World via Gateway: http://localhost:8080/api/hello
echo - Cotizador via Gateway: http://localhost:8080/api/cotizador
echo - Gestor via Gateway: http://localhost:8080/api/gestor
echo.
echo Direct Service URLs:
echo - Arca Cotizador 1: http://localhost:8081
echo - Arca Cotizador 2: http://localhost:8091
echo - Arca Gestor 1: http://localhost:8082
echo - Arca Gestor 2: http://localhost:8092
echo - Hello World: http://localhost:8083
echo.

pause
