@echo off
REM ARKA E-commerce Platform - Post-Deployment Validation Script for Windows
REM Script para validar que todos los servicios estén funcionando correctamente

setlocal enabledelayedexpansion

echo 🔍 ARKA E-commerce Platform - Post-Deployment Validation
echo =======================================================
echo.

REM Variables
set NAMESPACE=arka-ecommerce
set MONITORING_NAMESPACE=arka-monitoring

REM Verificar que kubectl esté disponible
echo [INFO] Verificando kubectl...
kubectl version --client >nul 2>&1
if errorlevel 1 (
    echo [ERROR] kubectl no está instalado o no está en el PATH
    exit /b 1
)
echo [SUCCESS] kubectl está disponible

REM Verificar conectividad al cluster
echo [INFO] Verificando conectividad al cluster...
kubectl cluster-info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] No se puede conectar al cluster de Kubernetes
    exit /b 1
)
echo [SUCCESS] Conectado al cluster de Kubernetes

REM Mostrar información del cluster
echo.
echo 📋 Información del Cluster:
kubectl cluster-info
echo.

REM Verificar namespaces
echo [INFO] Verificando namespaces...
kubectl get namespace %NAMESPACE% >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Namespace '%NAMESPACE%' no existe
    exit /b 1
) else (
    echo [SUCCESS] Namespace '%NAMESPACE%' existe
)

kubectl get namespace %MONITORING_NAMESPACE% >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Namespace '%MONITORING_NAMESPACE%' no existe
) else (
    echo [SUCCESS] Namespace '%MONITORING_NAMESPACE%' existe
)

REM Verificar deployments
echo [INFO] Verificando deployments en %NAMESPACE%...
echo.

REM Lista de deployments esperados
set deployments=config-server eureka-server api-gateway ecommerce-core arca-cotizador arca-gestor-solicitudes hello-world-service mysql-deployment redis-deployment

for %%d in (%deployments%) do (
    echo [INFO] Verificando deployment: %%d
    kubectl get deployment %%d -n %NAMESPACE% >nul 2>&1
    if errorlevel 1 (
        echo [ERROR] ❌ Deployment '%%d' no encontrado
    ) else (
        REM Obtener estado del deployment
        for /f "tokens=2,3" %%a in ('kubectl get deployment %%d -n %NAMESPACE% --no-headers') do (
            if "%%a"=="%%b" (
                echo [SUCCESS] ✅ %%d está listo ^(%%a/%%b replicas^)
            ) else (
                echo [WARNING] ⚠️  %%d no está completamente listo ^(%%a/%%b replicas^)
            )
        )
    )
)

REM Verificar pods
echo.
echo [INFO] Verificando estado de pods en %NAMESPACE%...
kubectl get pods -n %NAMESPACE%

echo.
REM Contar pods por estado
for /f %%i in ('kubectl get pods -n %NAMESPACE% --no-headers ^| find /c /v ""') do set total_pods=%%i
for /f %%i in ('kubectl get pods -n %NAMESPACE% --no-headers ^| find /c "Running"') do set running_pods=%%i

echo [INFO] Resumen de pods en %NAMESPACE%: !running_pods!/!total_pods! Running

REM Verificar servicios
echo.
echo [INFO] Verificando servicios en %NAMESPACE%...

set services=config-server-service eureka-service api-gateway-service ecommerce-core-service arca-cotizador-service arca-gestor-solicitudes-service hello-world-service mysql-service redis-service

for %%s in (%services%) do (
    kubectl get service %%s -n %NAMESPACE% >nul 2>&1
    if errorlevel 1 (
        echo [ERROR] ❌ Servicio '%%s' no encontrado
    ) else (
        echo [SUCCESS] ✅ Servicio '%%s' encontrado
    )
)

REM Verificar PVCs
echo.
echo [INFO] Verificando Persistent Volume Claims...
kubectl get pvc -n %NAMESPACE% >nul 2>&1
if not errorlevel 1 (
    kubectl get pvc -n %NAMESPACE%
) else (
    echo [INFO] No se encontraron PVCs en el namespace %NAMESPACE%
)

REM Verificar Ingress
echo.
echo [INFO] Verificando Ingress...
kubectl get ingress -n %NAMESPACE% >nul 2>&1
if not errorlevel 1 (
    kubectl get ingress -n %NAMESPACE%
    echo [SUCCESS] ✅ Ingress configurado
) else (
    echo [WARNING] No se encontró configuración de Ingress
)

REM Verificar monitoreo
echo.
echo [INFO] Verificando servicios de monitoreo...
kubectl get deployment prometheus -n %MONITORING_NAMESPACE% >nul 2>&1
if not errorlevel 1 (
    echo [SUCCESS] ✅ Prometheus deployment encontrado
) else (
    echo [WARNING] ⚠️  Prometheus no encontrado
)

kubectl get deployment grafana -n %MONITORING_NAMESPACE% >nul 2>&1
if not errorlevel 1 (
    echo [SUCCESS] ✅ Grafana deployment encontrado
) else (
    echo [WARNING] ⚠️  Grafana no encontrado
)

REM Mostrar eventos recientes
echo.
echo [INFO] Eventos recientes del sistema:
kubectl get events -n %NAMESPACE% --sort-by=.metadata.creationTimestamp | tail -5

REM Resumen final
echo.
echo 📊 RESUMEN DE VALIDACIÓN
echo ========================
echo.
echo Namespace principal: %NAMESPACE%
echo Namespace de monitoreo: %MONITORING_NAMESPACE%
echo.

REM URLs de acceso
echo 🌐 URLs DE ACCESO
echo ==================
echo.
echo Para acceso local ^(configurar hosts file^):
echo 127.0.0.1 arka-ecommerce.local
echo 127.0.0.1 grafana.local
echo.
echo URLs de la aplicación:
echo • ARKA E-commerce: http://arka-ecommerce.local
echo • Eureka Dashboard: http://arka-ecommerce.local:8761
echo • API Gateway: http://arka-ecommerce.local/api
echo.
echo URLs de monitoreo:
echo • Grafana: http://grafana.local ^(admin/arka123^)
echo • Prometheus: kubectl port-forward -n arka-monitoring svc/prometheus 9090:9090
echo.

REM Comandos útiles
echo 🛠️  COMANDOS ÚTILES
echo ===================
echo.
echo Ver logs de un servicio:
echo kubectl logs -f deployment/ecommerce-core -n %NAMESPACE%
echo.
echo Acceder a MySQL:
echo kubectl exec -it deployment/mysql-deployment -n %NAMESPACE% -- mysql -u root -parka123
echo.
echo Port-forward para desarrollo:
echo kubectl port-forward svc/api-gateway-service 8080:8080 -n %NAMESPACE%
echo.
echo Monitorear recursos:
echo kubectl top pods -n %NAMESPACE%
echo.

echo [SUCCESS] 🎉 Validación post-despliegue completada!
echo.
echo Si encontraste problemas, revisa:
echo • Los logs de los pods: kubectl logs ^<pod-name^> -n %NAMESPACE%
echo • El estado de los recursos: kubectl describe ^<resource^> ^<name^> -n %NAMESPACE%
echo • La documentación de troubleshooting en README.md

pause
