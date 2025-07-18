@echo off
echo ================================
echo  ARKA MICROSERVICES TESTING
echo ================================
echo.

:menu
echo Seleccione una opcion:
echo 1. Verificar estado del sistema
echo 2. Crear datos de prueba
echo 3. Probar cotizaciones
echo 4. Probar solicitudes
echo 5. Probar resiliencia
echo 6. Ver logs
echo 7. Salir
echo.

set /p choice="Ingrese su opcion (1-7): "

if "%choice%"=="1" goto check_health
if "%choice%"=="2" goto create_test_data
if "%choice%"=="3" goto test_quotations
if "%choice%"=="4" goto test_requests
if "%choice%"=="5" goto test_resilience
if "%choice%"=="6" goto view_logs
if "%choice%"=="7" goto exit
goto menu

:check_health
echo.
echo ================================
echo  VERIFICANDO ESTADO DEL SISTEMA
echo ================================
echo.

echo Verificando sistema principal (puerto 8082)...
curl -X GET http://localhost:8082/api/system/health
echo.

echo Verificando servicio de cotizaciones...
curl -X GET http://localhost:8082/api/cotizaciones/servicio/health
echo.

echo Verificando servicio de solicitudes...
curl -X GET http://localhost:8082/api/solicitudes/servicio/health
echo.

pause
goto menu

:create_test_data
echo.
echo ================================
echo  CREANDO DATOS DE PRUEBA
echo ================================
echo.

echo Creando categoria...
curl -X POST http://localhost:8082/categorias -H "Content-Type: application/json" -d "{\"nombre\": \"Electronicos\", \"descripcion\": \"Productos electronicos y tecnologicos\"}"
echo.

echo Creando producto 1...
curl -X POST http://localhost:8082/productos -H "Content-Type: application/json" -d "{\"nombre\": \"Laptop Dell\", \"descripcion\": \"Laptop Dell Inspiron 15\", \"categoriaId\": 1, \"marca\": \"Dell\", \"precioUnitario\": 800.00, \"stock\": 10}"
echo.

echo Creando producto 2...
curl -X POST http://localhost:8082/productos -H "Content-Type: application/json" -d "{\"nombre\": \"Mouse Inalambrico\", \"descripcion\": \"Mouse inalambrico ergonomico\", \"categoriaId\": 1, \"marca\": \"Logitech\", \"precioUnitario\": 25.00, \"stock\": 50}"
echo.

echo Creando cliente...
curl -X POST http://localhost:8082/usuarios -H "Content-Type: application/json" -d "{\"nombre\": \"Juan Perez\", \"email\": \"juan.perez@email.com\", \"telefono\": \"123-456-7890\", \"direccion\": \"Calle Principal 123\"}"
echo.

echo Datos de prueba creados exitosamente!
pause
goto menu

:test_quotations
echo.
echo ================================
echo  PROBANDO COTIZACIONES
echo ================================
echo.

echo Solicitando cotizacion...
curl -X POST "http://localhost:8082/api/cotizaciones/cliente/1/productos?tipoCliente=PREMIUM" -H "Content-Type: application/json" -d "[1, 2]"
echo.

echo Obteniendo cotizaciones del cliente...
curl -X GET http://localhost:8082/api/cotizaciones/cliente/1
echo.

pause
goto menu

:test_requests
echo.
echo ================================
echo  PROBANDO SOLICITUDES
echo ================================
echo.

echo Creando solicitud de cotizacion...
curl -X POST http://localhost:8082/api/solicitudes/cliente/1/cotizacion -H "Content-Type: application/json" -d "{\"productIds\": [1, 2], \"observaciones\": \"Necesito cotizacion para proyecto corporativo\"}"
echo.

echo Creando solicitud urgente...
curl -X POST http://localhost:8082/api/solicitudes/cliente/1/urgente -H "Content-Type: application/json" -d "{\"productIds\": [1], \"observaciones\": \"Solicitud urgente - proyecto critico\"}"
echo.

echo Obteniendo solicitudes del cliente...
curl -X GET http://localhost:8082/api/solicitudes/cliente/1
echo.

pause
goto menu

:test_resilience
echo.
echo ================================
echo  PROBANDO RESILIENCIA
echo ================================
echo.

echo INSTRUCCIONES:
echo 1. Detenga uno de los microservicios (Ctrl+C en su terminal)
echo 2. Luego presione cualquier tecla para probar la resiliencia
pause

echo Probando comunicacion con servicios (algunos pueden fallar)...
curl -X GET http://localhost:8082/api/cotizaciones/servicio/health
echo.
curl -X GET http://localhost:8082/api/solicitudes/servicio/health
echo.

echo Reinicie el servicio detenido y pruebe nuevamente
pause
goto menu

:view_logs
echo.
echo ================================
echo  INSTRUCCIONES PARA VER LOGS
echo ================================
echo.

echo Para ver logs en tiempo real, abra terminales separados y ejecute:
echo.
echo 1. Logs del sistema principal:
echo    cd arkavalenzuela
echo    tail -f logs/spring.log
echo.
echo 2. Logs filtrados de microservicios:
echo    findstr "CotizadorServiceAdapter" logs/spring.log
echo    findstr "GestorSolicitudesServiceAdapter" logs/spring.log
echo.

pause
goto menu

:exit
echo.
echo Gracias por usar el sistema de testing de Arka Microservices!
echo.
exit
