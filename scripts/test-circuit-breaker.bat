@echo off
echo =========================================
echo PROBANDO CIRCUIT BREAKER - CALCULO ENVIO
echo =========================================
echo.

REM URL base del servicio
set SERVICE_URL=http://localhost:8082

echo 1. Verificando estado del servicio...
curl -X GET "%SERVICE_URL%/api/calculo-envio/estado" -H "Content-Type: application/json"
echo.
echo.

echo 2. Realizando cálculo de envío normal...
curl -X POST "%SERVICE_URL%/api/calculo-envio/calcular" ^
  -H "Content-Type: application/json" ^
  -d "{\"origen\":\"Lima\",\"destino\":\"Arequipa\",\"peso\":2.5,\"dimensiones\":\"50x30x20\"}"
echo.
echo.

echo 3. Probando múltiples llamadas para activar Circuit Breaker...
for /L %%i in (1,1,10) do (
  echo Llamada %%i:
  curl -X POST "%SERVICE_URL%/api/calculo-envio/probar-circuit-breaker" ^
    -H "Content-Type: application/json" ^
    -d "{\"escenario\":\"externo\",\"origen\":\"Lima\",\"destino\":\"Cusco\",\"peso\":1.5}"
  echo.
  timeout /t 1 /nobreak >nul
)

echo.
echo 4. Verificando estado de Circuit Breakers...
curl -X GET "%SERVICE_URL%/api/circuit-breaker/estado" -H "Content-Type: application/json"
echo.
echo.

echo 5. Probando servicio interno simulado...
curl -X POST "%SERVICE_URL%/api/calculo-envio/probar-circuit-breaker" ^
  -H "Content-Type: application/json" ^
  -d "{\"escenario\":\"interno\",\"origen\":\"Lima\",\"destino\":\"Trujillo\",\"peso\":3.0}"
echo.
echo.

echo 6. Probando flujo completo con fallbacks...
curl -X POST "%SERVICE_URL%/api/calculo-envio/probar-circuit-breaker" ^
  -H "Content-Type: application/json" ^
  -d "{\"escenario\":\"completo\",\"origen\":\"Lima\",\"destino\":\"Iquitos\",\"peso\":4.5}"
echo.
echo.

echo 7. Forzando apertura de Circuit Breaker para pruebas...
curl -X POST "%SERVICE_URL%/api/circuit-breaker/forzar-apertura/proveedor-externo-service" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 8. Probando cálculo con Circuit Breaker abierto...
curl -X POST "%SERVICE_URL%/api/calculo-envio/calcular" ^
  -H "Content-Type: application/json" ^
  -d "{\"origen\":\"Lima\",\"destino\":\"Piura\",\"peso\":2.0,\"dimensiones\":\"40x30x25\"}"
echo.
echo.

echo 9. Verificando estado específico de Circuit Breaker...
curl -X GET "%SERVICE_URL%/api/circuit-breaker/estado/proveedor-externo-service" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 10. Reiniciando Circuit Breaker...
curl -X POST "%SERVICE_URL%/api/circuit-breaker/reiniciar-metricas/proveedor-externo-service" ^
  -H "Content-Type: application/json"
echo.
echo.

echo 11. Prueba rápida final...
curl -X GET "%SERVICE_URL%/api/calculo-envio/prueba-rapida?origen=Lima&destino=Cusco&peso=1.0" ^
  -H "Content-Type: application/json"
echo.
echo.

echo =========================================
echo PRUEBAS DE CIRCUIT BREAKER COMPLETADAS
echo =========================================
echo.
echo Para monitorear el estado de los Circuit Breakers en tiempo real:
echo curl -X GET "%SERVICE_URL%/api/circuit-breaker/estado"
echo.
echo Para verificar métricas de Actuator:
echo curl -X GET "%SERVICE_URL%/actuator/health"
echo curl -X GET "%SERVICE_URL%/actuator/circuitbreakers"
echo.
pause
