@echo off
echo ================================
echo   PROBANDO GESTOR SOLICITUDES
echo ================================

echo.
echo Esperando que el servicio esté disponible...
timeout /t 5 /nobreak

echo.
echo 1. Health Check...
curl -X GET http://localhost:8082/api/solicitudes/health

echo.
echo.
echo 2. Creando solicitud de ejemplo...
curl -X POST http://localhost:8082/api/solicitudes ^
  -H "Content-Type: application/json" ^
  -d "{\"proveedorId\": \"prov-001\", \"clienteId\": \"cliente-123\", \"items\": [{\"productoId\": \"prod-001\", \"cantidad\": 10, \"especificaciones\": \"Calidad premium\"}]}"

echo.
echo.
echo ================================
echo   PRUEBA COMPLETADA
echo ================================
pause
