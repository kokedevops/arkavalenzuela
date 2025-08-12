@echo off
echo ==========================================
echo INICIANDO MICROSERVICIOS CON CONFIG SERVER
echo ==========================================
echo.

echo IMPORTANTE: Asegurate de que el Config Server esté ejecutándose en:
echo http://127.0.0.1:9090
echo.
echo Presiona cualquier tecla para continuar o Ctrl+C para cancelar...
pause

echo.
echo 1. Iniciando Eureka Server...
start "Eureka Server" cmd /k "cd /d eureka-server && gradlew bootRun"

echo Esperando 30 segundos para que Eureka Server se inicie completamente...
timeout /t 30 /nobreak

echo.
echo 2. Iniciando API Gateway...
start "API Gateway" cmd /k "cd /d api-gateway && gradlew bootRun"

echo Esperando 20 segundos para que API Gateway se registre...
timeout /t 20 /nobreak

echo.
echo 3. Iniciando Arca Cotizador...
start "Arca Cotizador" cmd /k "cd /d arca-cotizador && gradlew bootRun"

echo Esperando 15 segundos...
timeout /t 15 /nobreak

echo.
echo 4. Iniciando Arca Gestor Solicitudes...
start "Arca Gestor Solicitudes" cmd /k "cd /d arca-gestor-solicitudes && gradlew bootRun"

echo Esperando 15 segundos...
timeout /t 15 /nobreak

echo.
echo 5. Iniciando Hello World Service...
start "Hello World Service" cmd /k "cd /d hello-world-service && gradlew bootRun"

echo.
echo ==========================================
echo TODOS LOS SERVICIOS INICIADOS!
echo ==========================================
echo.
echo URLs importantes:
echo • Config Server: http://127.0.0.1:9090
echo • Eureka Server: http://localhost:8761
echo • API Gateway: http://localhost:8080
echo • Arca Cotizador: http://localhost:8081
echo • Arca Gestor Solicitudes: http://localhost:8082
echo • Hello World Service: http://localhost:8083
echo.
echo Endpoints de gestión:
echo • Refresh configs: POST /actuator/refresh en cada servicio
echo • Health check: GET /actuator/health en cada servicio
echo.
pause
