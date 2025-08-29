@echo off
REM ARKA E-commerce Platform - Kubernetes Deployment Script for Windows
REM Compatible with k3s and standard Kubernetes clusters

echo 🚀 ARKA E-commerce Platform - Kubernetes Deployment
echo ==================================================

REM Variables de configuración
set NAMESPACE=arka-ecommerce
set MONITORING_NAMESPACE=arka-monitoring
set KUBECTL_TIMEOUT=600s

REM Verificar si kubectl está disponible
kubectl version --client >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] kubectl no está instalado. Por favor instala kubectl.
    exit /b 1
)
echo [SUCCESS] kubectl está disponible

REM Verificar conexión al cluster
echo [INFO] Verificando conexión al cluster Kubernetes...
kubectl cluster-info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] No se puede conectar al cluster Kubernetes
    exit /b 1
)
echo [SUCCESS] Conexión al cluster establecida

REM Crear namespaces
echo [INFO] Creando namespaces...
kubectl apply -f k8s/namespace.yaml
echo [SUCCESS] Namespaces creados exitosamente

REM Desplegar bases de datos
echo [INFO] Desplegando bases de datos...
kubectl apply -f k8s/mysql.yaml
kubectl apply -f k8s/mongodb.yaml
kubectl apply -f k8s/redis.yaml
echo [SUCCESS] Bases de datos desplegadas

REM Esperar a que las bases de datos estén listas
echo [INFO] Esperando a que las bases de datos estén listas...
kubectl wait --for=condition=ready pod -l app=mysql -n %NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
kubectl wait --for=condition=ready pod -l app=mongodb -n %NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
kubectl wait --for=condition=ready pod -l app=redis -n %NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
echo [SUCCESS] Todas las bases de datos están listas

REM Desplegar infraestructura
echo [INFO] Desplegando servicios de infraestructura...
kubectl apply -f k8s/config-server.yaml
echo [INFO] Esperando a que Config Server esté listo...
kubectl wait --for=condition=ready pod -l app=config-server -n %NAMESPACE% --timeout=%KUBECTL_TIMEOUT%

kubectl apply -f k8s/eureka-server.yaml
echo [INFO] Esperando a que Eureka Server esté listo...
kubectl wait --for=condition=ready pod -l app=eureka-server -n %NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
echo [SUCCESS] Servicios de infraestructura desplegados y listos

REM Desplegar API Gateway
echo [INFO] Desplegando API Gateway...
kubectl apply -f k8s/api-gateway.yaml
kubectl wait --for=condition=ready pod -l app=api-gateway -n %NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
echo [SUCCESS] API Gateway desplegado y listo

REM Desplegar microservicios
echo [INFO] Desplegando microservicios de aplicación...
kubectl apply -f k8s/ecommerce-core.yaml
kubectl apply -f k8s/arca-cotizador.yaml
kubectl apply -f k8s/arca-gestor-solicitudes.yaml
kubectl apply -f k8s/hello-world-service.yaml

echo [INFO] Esperando a que todos los microservicios estén listos...
kubectl wait --for=condition=ready pod -l app=ecommerce-core -n %NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
kubectl wait --for=condition=ready pod -l app=arca-cotizador -n %NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
kubectl wait --for=condition=ready pod -l app=arca-gestor-solicitudes -n %NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
kubectl wait --for=condition=ready pod -l app=hello-world-service -n %NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
echo [SUCCESS] Todos los microservicios están desplegados y listos

REM Desplegar monitoreo
echo [INFO] Desplegando sistema de monitoreo...
kubectl apply -f k8s/monitoring-prometheus.yaml
kubectl apply -f k8s/monitoring-grafana.yaml

kubectl wait --for=condition=ready pod -l app=prometheus -n %MONITORING_NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
kubectl wait --for=condition=ready pod -l app=grafana -n %MONITORING_NAMESPACE% --timeout=%KUBECTL_TIMEOUT%
echo [SUCCESS] Sistema de monitoreo desplegado y listo

REM Desplegar networking
echo [INFO] Desplegando configuración de red e Ingress...
kubectl apply -f k8s/ingress-and-networking.yaml
echo [SUCCESS] Configuración de red desplegada

REM Verificar estado
echo [INFO] Verificando estado del deployment...
echo.
echo 📊 Estado de los Pods:
kubectl get pods -n %NAMESPACE% -o wide

echo.
echo 🔗 Estado de los Servicios:
kubectl get services -n %NAMESPACE%

echo.
echo 📈 Estado del Monitoreo:
kubectl get pods -n %MONITORING_NAMESPACE% -o wide

echo.
echo 🌐 Ingress:
kubectl get ingress -n %NAMESPACE%

echo.
echo 🎉 ¡Deployment completado exitosamente!
echo ========================================
echo.
echo 📍 URLs de Acceso:
echo - Para acceder a los servicios, usar port-forward:
echo   kubectl port-forward svc/api-gateway-service 8080:8080 -n %NAMESPACE%
echo   kubectl port-forward svc/eureka-service 8761:8761 -n %NAMESPACE%
echo   kubectl port-forward svc/grafana-service 3000:3000 -n %MONITORING_NAMESPACE%
echo   kubectl port-forward svc/prometheus-service 9090:9090 -n %MONITORING_NAMESPACE%
echo.
echo 🔍 Comandos útiles:
echo - Ver logs: kubectl logs -f deployment/[service-name] -n %NAMESPACE%
echo - Ver pods: kubectl get pods -n %NAMESPACE%
echo - Escalar servicio: kubectl scale deployment [service-name] --replicas=3 -n %NAMESPACE%
echo - Eliminar deployment: k8s-undeploy.bat

echo.
echo [SUCCESS] ¡Deployment de ARKA E-commerce Platform completado!

pause
