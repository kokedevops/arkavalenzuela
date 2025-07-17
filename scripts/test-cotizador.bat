@echo off
echo ================================
echo   PROBANDO ARCA COTIZADOR
echo ================================

echo.
echo Esperando que el servicio esté disponible...
timeout /t 5 /nobreak

echo.
echo 1. Health Check...
curl -X GET http://localhost:8081/api/cotizaciones/health

echo.
echo.
echo 2. Generando cotización de ejemplo...
curl -X POST http://localhost:8081/api/cotizaciones ^
  -H "Content-Type: application/json" ^
  -d "{\"clienteId\": \"cliente-123\", \"items\": [{\"productoId\": \"prod-001\", \"cantidad\": 5}, {\"productoId\": \"prod-002\", \"cantidad\": 3}]}"

echo.
echo.
echo ================================
echo   PRUEBA COMPLETADA
echo ================================
pause
