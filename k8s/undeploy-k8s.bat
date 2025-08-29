@echo off
REM ARKA E-commerce Platform - Kubernetes Undeploy Script for Windows

echo 🗑️  ARKA E-commerce Platform - Kubernetes Cleanup
echo =================================================

set NAMESPACE=arka-ecommerce
set MONITORING_NAMESPACE=arka-monitoring

echo.
echo [WARNING] ⚠️  ADVERTENCIA: Esta operación eliminará TODOS los recursos de ARKA E-commerce Platform
echo Esto incluye:
echo    • Todos los microservicios
echo    • Todas las bases de datos y sus datos
echo    • Sistema de monitoreo
echo    • Configuraciones de red
echo    • Volúmenes persistentes
echo.

set /p CONFIRM=¿Estás seguro de que quieres continuar? (yes/no): 
if /i not "%CONFIRM%"=="yes" (
    echo [INFO] Operación cancelada por el usuario
    exit /b 0
)

echo [INFO] Verificando kubectl...
kubectl version --client >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] kubectl no está instalado
    exit /b 1
)

echo [INFO] Verificando conexión al cluster...
kubectl cluster-info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] No se puede conectar al cluster Kubernetes
    exit /b 1
)

echo [INFO] Eliminando configuración de red...
kubectl delete -f k8s/ingress-and-networking.yaml --ignore-not-found=true
echo [SUCCESS] Configuración de red eliminada

echo [INFO] Eliminando microservicios de aplicación...
kubectl delete -f k8s/hello-world-service.yaml --ignore-not-found=true
kubectl delete -f k8s/arca-gestor-solicitudes.yaml --ignore-not-found=true
kubectl delete -f k8s/arca-cotizador.yaml --ignore-not-found=true
kubectl delete -f k8s/ecommerce-core.yaml --ignore-not-found=true
echo [SUCCESS] Microservicios eliminados

echo [INFO] Eliminando API Gateway e infraestructura...
kubectl delete -f k8s/api-gateway.yaml --ignore-not-found=true
kubectl delete -f k8s/eureka-server.yaml --ignore-not-found=true
kubectl delete -f k8s/config-server.yaml --ignore-not-found=true
echo [SUCCESS] Infraestructura eliminada

echo [INFO] Eliminando bases de datos...
kubectl delete -f k8s/redis.yaml --ignore-not-found=true
kubectl delete -f k8s/mongodb.yaml --ignore-not-found=true
kubectl delete -f k8s/mysql.yaml --ignore-not-found=true
echo [SUCCESS] Bases de datos eliminadas

echo [INFO] Eliminando sistema de monitoreo...
kubectl delete -f k8s/monitoring-grafana.yaml --ignore-not-found=true
kubectl delete -f k8s/monitoring-prometheus.yaml --ignore-not-found=true
echo [SUCCESS] Sistema de monitoreo eliminado

echo [INFO] Eliminando volúmenes persistentes...
kubectl delete pvc --all -n %NAMESPACE% --ignore-not-found=true
kubectl delete pvc --all -n %MONITORING_NAMESPACE% --ignore-not-found=true
echo [SUCCESS] Volúmenes persistentes eliminados

echo [INFO] Eliminando configuraciones y secretos...
kubectl delete secrets --all -n %NAMESPACE% --ignore-not-found=true
kubectl delete secrets --all -n %MONITORING_NAMESPACE% --ignore-not-found=true
kubectl delete configmaps --all -n %NAMESPACE% --ignore-not-found=true
kubectl delete configmaps --all -n %MONITORING_NAMESPACE% --ignore-not-found=true
echo [SUCCESS] Configuraciones eliminadas

echo [INFO] Eliminando namespaces...
kubectl delete namespace %NAMESPACE% --ignore-not-found=true
kubectl delete namespace %MONITORING_NAMESPACE% --ignore-not-found=true
echo [SUCCESS] Namespaces eliminados

echo [INFO] Esperando a que los namespaces se eliminen completamente...
timeout /t 30 >nul

echo.
echo [INFO] Verificando estado final de la limpieza...

echo.
echo 📊 Verificando recursos restantes:
kubectl get pods --all-namespaces | findstr "arka"
kubectl get services --all-namespaces | findstr "arka"
kubectl get namespaces | findstr "arka"

echo.
echo [SUCCESS] 🎉 Limpieza de ARKA E-commerce Platform completada
echo.
echo [INFO] Para volver a desplegar la plataforma, ejecuta: deploy-k8s.bat

pause
