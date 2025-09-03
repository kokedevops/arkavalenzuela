@echo off
echo ========================================
echo 🧪 PROBANDO PATRÓN SAGA - ARKA VALENZUELA  
echo ========================================
echo.

echo 🔍 Verificando que la aplicación esté ejecutándose...
curl -s http://localhost:8888/actuator/health > nul
if %ERRORLEVEL% neq 0 (
    echo ❌ La aplicación no está ejecutándose
    echo 💡 Ejecuta: start-saga.bat
    pause
    exit /b 1
)

echo ✅ Aplicación ejecutándose en puerto 8888
echo.

echo 📋 CASO 1: Pedido exitoso
echo ========================
curl -X POST http://localhost:8888/api/saga/start ^
  -H "Content-Type: application/json" ^
  -d "{\"pedidoId\":\"PEDIDO_SUCCESS_001\",\"clienteId\":\"CLIENTE_123\",\"productoId\":\"PRODUCTO_ABC\",\"cantidad\":2,\"precio\":150.00}"

echo.
echo.
timeout /t 5 > nul

echo 📋 CASO 2: Error en inventario (contiene 999)
echo ===============================================
curl -X POST http://localhost:8888/api/saga/start ^
  -H "Content-Type: application/json" ^
  -d "{\"pedidoId\":\"PEDIDO_999_ERROR\",\"clienteId\":\"CLIENTE_123\",\"productoId\":\"PRODUCTO_999\",\"cantidad\":2,\"precio\":150.00}"

echo.
echo.
timeout /t 5 > nul

echo 📋 CASO 3: Error en envío (contiene 888)
echo ==========================================
curl -X POST http://localhost:8888/api/saga/start ^
  -H "Content-Type: application/json" ^
  -d "{\"pedidoId\":\"PEDIDO_888_ERROR\",\"clienteId\":\"CLIENTE_123\",\"productoId\":\"PRODUCTO_888\",\"cantidad\":2,\"precio\":150.00}"

echo.
echo.

echo ✅ Pruebas completadas
echo 📊 Revisa los logs de la aplicación para ver el flujo completo del patrón Saga
echo.
pause
