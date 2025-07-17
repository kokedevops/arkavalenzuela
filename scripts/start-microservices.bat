@echo off
echo ================================
echo   INICIANDO MICROSERVICIOS ARCA
echo ================================

echo.
echo 1. Compilando proyecto...
call gradlew build

echo.
echo 2. Iniciando Arca Cotizador (Puerto 8081)...
start "Arca Cotizador" cmd /k "gradlew :arca-cotizador:bootRun"

echo.
echo 3. Esperando 10 segundos...
timeout /t 10 /nobreak

echo.
echo 4. Iniciando Arca Gestor de Solicitudes (Puerto 8082)...
start "Arca Gestor Solicitudes" cmd /k "gradlew :arca-gestor-solicitudes:bootRun"

echo.
echo 5. Esperando 10 segundos...
timeout /t 10 /nobreak

echo.
echo 6. Iniciando Aplicación Principal (Puerto 8080)...
start "Aplicación Principal" cmd /k "gradlew bootRun"

echo.
echo ================================
echo   MICROSERVICIOS INICIADOS
echo ================================
echo.
echo - Aplicación Principal: http://localhost:8080
echo - Arca Cotizador: http://localhost:8081
echo - Arca Gestor Solicitudes: http://localhost:8082
echo.
echo Health Checks:
echo - curl http://localhost:8081/api/cotizaciones/health
echo - curl http://localhost:8082/api/solicitudes/health
echo.
pause
