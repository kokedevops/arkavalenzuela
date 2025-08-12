@echo off
echo ==========================================
echo PROBANDO CONEXIÓN CON CONFIG SERVER
echo ==========================================
echo.

echo Probando conexión con el Config Server remoto...
echo.

echo 1. Verificando configuración de eureka-server:
curl -u config-client:arka-client-2025 "http://127.0.0.1:8761/eureka-server/dev"
echo.
echo.

echo 2. Verificando configuración de api-gateway:
curl -u config-client:arka-client-2025 "http://127.0.0.1:8080/api-gateway/dev"
echo.
echo.

echo 3. Verificando configuración de arca-cotizador:
curl -u config-client:arka-client-2025 "http://127.0.0.1:8081/arca-cotizador/dev"
echo.
echo.

echo 4. Verificando configuración de arca-gestor-solicitudes:
curl -u config-client:arka-client-2025 "http://127.0.0.1:8082/arca-gestor-solicitudes/dev"
echo.
echo.

echo 5. Verificando configuración de hello-world-service:
curl -u config-client:arka-client-2025 "http://127.0.0.1:9090/hello-world-service/dev"
echo.
echo.

echo ==========================================
echo PRUEBAS COMPLETADAS!
echo ==========================================
echo.
echo Si ves las configuraciones JSON arriba, la conexión es exitosa.
echo Si hay errores, revisa:
echo 1. Conectividad a internet
echo 2. Credenciales del Config Server
echo 3. Que el Config Server esté ejecutándose
echo.
pause
