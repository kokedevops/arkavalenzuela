@echo off
REM ARKA E-commerce Platform - Verificador de Estado
REM Script para verificar el estado de todos los microservicios

echo 🔍 ARKA E-commerce Platform - Estado de Microservicios
echo =====================================================

echo Verificando servicios...
echo.

REM Función para verificar un servicio
:check_service
set url=%1
set name=%2
echo Verificando %name%...
curl -s --max-time 5 %url% >nul 2>&1
if errorlevel 1 (
    echo ❌ %name% - No disponible
) else (
    echo ✅ %name% - Activo
)
exit /b

REM Verificar cada servicio
call :check_service "http://localhost:8888/actuator/health" "Config Server (8888)"
call :check_service "http://localhost:8761/actuator/health" "Eureka Server (8761)"
call :check_service "http://localhost:8080/actuator/health" "API Gateway (8080)"
call :check_service "http://localhost:8081/actuator/health" "Arca Cotizador (8081)"
call :check_service "http://localhost:8082/actuator/health" "Arca Gestor Solicitudes (8082)"
call :check_service "http://localhost:8083/actuator/health" "Hello World Service (8083)"

echo.
echo 📊 Puertos en uso:
netstat -an | findstr ":8888 :8761 :8080 :8081 :8082 :8083"

echo.
echo 🌐 URLs principales:
echo • Eureka Dashboard:           http://localhost:8761
echo • API Gateway:                http://localhost:8080
echo • Config Server:              http://localhost:8888
echo • ARKA E-commerce Core:       http://localhost:8888
echo.
echo 🔗 Endpoints de prueba:
echo • Health Check API Gateway:   curl http://localhost:8080/actuator/health
echo • Servicios registrados:      curl http://localhost:8761/eureka/apps
echo • Cotizador health:           curl http://localhost:8081/actuator/health
echo.

pause
