#!/bin/bash

# ARKA E-commerce Platform - Post-Deployment Validation Script
# Script para validar que todos los servicios estén funcionando correctamente

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Variables
NAMESPACE="arka-ecommerce"
MONITORING_NAMESPACE="arka-monitoring"
TIMEOUT=300

echo "🔍 ARKA E-commerce Platform - Post-Deployment Validation"
echo "======================================================="
echo ""

# Función para verificar si kubectl está disponible
check_kubectl() {
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl no está instalado o no está en el PATH"
        exit 1
    fi
    log_success "kubectl está disponible"
}

# Función para verificar conectividad al cluster
check_cluster_connectivity() {
    log_info "Verificando conectividad al cluster..."
    
    if ! kubectl cluster-info &> /dev/null; then
        log_error "No se puede conectar al cluster de Kubernetes"
        exit 1
    fi
    
    log_success "Conectado al cluster de Kubernetes"
    
    # Mostrar información del cluster
    echo ""
    echo "📋 Información del Cluster:"
    kubectl cluster-info
    echo ""
}

# Función para verificar namespaces
check_namespaces() {
    log_info "Verificando namespaces..."
    
    namespaces=("$NAMESPACE" "$MONITORING_NAMESPACE")
    
    for ns in "${namespaces[@]}"; do
        if kubectl get namespace "$ns" &> /dev/null; then
            log_success "Namespace '$ns' existe"
        else
            log_error "Namespace '$ns' no existe"
            exit 1
        fi
    done
}

# Función para verificar deployments
check_deployments() {
    log_info "Verificando deployments..."
    
    # Deployments esperados en el namespace principal
    expected_deployments=(
        "config-server"
        "eureka-server" 
        "api-gateway"
        "ecommerce-core"
        "arca-cotizador"
        "arca-gestor-solicitudes"
        "hello-world-service"
        "mysql-deployment"
        "redis-deployment"
    )
    
    for deployment in "${expected_deployments[@]}"; do
        log_info "Verificando deployment: $deployment"
        
        if kubectl get deployment "$deployment" -n "$NAMESPACE" &> /dev/null; then
            # Verificar que esté ready
            ready=$(kubectl get deployment "$deployment" -n "$NAMESPACE" -o jsonpath='{.status.readyReplicas}')
            desired=$(kubectl get deployment "$deployment" -n "$NAMESPACE" -o jsonpath='{.status.replicas}')
            
            if [ "$ready" = "$desired" ] && [ "$ready" != "0" ]; then
                log_success "✅ $deployment está listo ($ready/$desired replicas)"
            else
                log_warning "⚠️  $deployment no está completamente listo ($ready/$desired replicas)"
            fi
        else
            log_error "❌ Deployment '$deployment' no encontrado"
        fi
    done
    
    # Verificar deployments de monitoring
    monitoring_deployments=("prometheus" "grafana")
    
    for deployment in "${monitoring_deployments[@]}"; do
        log_info "Verificando deployment de monitoring: $deployment"
        
        if kubectl get deployment "$deployment" -n "$MONITORING_NAMESPACE" &> /dev/null; then
            ready=$(kubectl get deployment "$deployment" -n "$MONITORING_NAMESPACE" -o jsonpath='{.status.readyReplicas}')
            desired=$(kubectl get deployment "$deployment" -n "$MONITORING_NAMESPACE" -o jsonpath='{.status.replicas}')
            
            if [ "$ready" = "$desired" ] && [ "$ready" != "0" ]; then
                log_success "✅ $deployment está listo ($ready/$desired replicas)"
            else
                log_warning "⚠️  $deployment no está completamente listo ($ready/$desired replicas)"
            fi
        else
            log_warning "Deployment de monitoring '$deployment' no encontrado"
        fi
    done
}

# Función para verificar pods
check_pods() {
    log_info "Verificando estado de pods..."
    
    # Pods en namespace principal
    log_info "Pods en namespace $NAMESPACE:"
    kubectl get pods -n "$NAMESPACE" -o custom-columns="NAME:.metadata.name,STATUS:.status.phase,READY:.status.containerStatuses[*].ready,RESTARTS:.status.containerStatuses[*].restartCount"
    
    # Contar pods por estado
    total_pods=$(kubectl get pods -n "$NAMESPACE" --no-headers | wc -l)
    running_pods=$(kubectl get pods -n "$NAMESPACE" --no-headers | grep "Running" | wc -l)
    
    echo ""
    log_info "Resumen de pods en $NAMESPACE: $running_pods/$total_pods Running"
    
    # Verificar pods problemáticos
    problem_pods=$(kubectl get pods -n "$NAMESPACE" --no-headers | grep -v "Running\|Completed" | awk '{print $1}')
    
    if [ ! -z "$problem_pods" ]; then
        log_warning "Pods con problemas encontrados:"
        echo "$problem_pods"
        
        echo ""
        log_info "Logs de pods problemáticos (últimas 10 líneas):"
        for pod in $problem_pods; do
            echo "--- Logs de $pod ---"
            kubectl logs "$pod" -n "$NAMESPACE" --tail=10 2>/dev/null || echo "No se pudieron obtener logs"
            echo ""
        done
    fi
}

# Función para verificar servicios
check_services() {
    log_info "Verificando servicios..."
    
    expected_services=(
        "config-server-service"
        "eureka-service"
        "api-gateway-service"
        "ecommerce-core-service"
        "arca-cotizador-service"
        "arca-gestor-solicitudes-service"
        "hello-world-service"
        "mysql-service"
        "redis-service"
    )
    
    for service in "${expected_services[@]}"; do
        if kubectl get service "$service" -n "$NAMESPACE" &> /dev/null; then
            # Verificar endpoints
            endpoints=$(kubectl get endpoints "$service" -n "$NAMESPACE" -o jsonpath='{.subsets[*].addresses[*].ip}' | wc -w)
            if [ "$endpoints" -gt 0 ]; then
                log_success "✅ Servicio '$service' tiene $endpoints endpoints"
            else
                log_warning "⚠️  Servicio '$service' no tiene endpoints"
            fi
        else
            log_error "❌ Servicio '$service' no encontrado"
        fi
    done
}

# Función para verificar persistent volumes
check_storage() {
    log_info "Verificando almacenamiento..."
    
    # Verificar PVCs
    pvcs=$(kubectl get pvc -n "$NAMESPACE" --no-headers)
    
    if [ ! -z "$pvcs" ]; then
        log_info "Persistent Volume Claims:"
        kubectl get pvc -n "$NAMESPACE"
        
        # Verificar estado de PVCs
        bound_pvcs=$(kubectl get pvc -n "$NAMESPACE" --no-headers | grep "Bound" | wc -l)
        total_pvcs=$(kubectl get pvc -n "$NAMESPACE" --no-headers | wc -l)
        
        log_info "PVCs: $bound_pvcs/$total_pvcs Bound"
        
        if [ "$bound_pvcs" -eq "$total_pvcs" ]; then
            log_success "✅ Todos los PVCs están en estado Bound"
        else
            log_warning "⚠️  Algunos PVCs no están en estado Bound"
        fi
    else
        log_info "No se encontraron PVCs en el namespace $NAMESPACE"
    fi
}

# Función para verificar ingress
check_ingress() {
    log_info "Verificando Ingress..."
    
    if kubectl get ingress -n "$NAMESPACE" &> /dev/null; then
        log_info "Configuración de Ingress:"
        kubectl get ingress -n "$NAMESPACE"
        
        # Verificar que tenga direcciones IP
        ingress_ip=$(kubectl get ingress arka-ingress -n "$NAMESPACE" -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null)
        
        if [ ! -z "$ingress_ip" ]; then
            log_success "✅ Ingress tiene IP asignada: $ingress_ip"
        else
            log_info "Ingress configurado pero sin IP externa (normal en k3s local)"
        fi
    else
        log_warning "No se encontró configuración de Ingress"
    fi
}

# Función para probar conectividad de servicios
test_service_connectivity() {
    log_info "Probando conectividad de servicios..."
    
    # Crear pod temporal para pruebas
    kubectl run test-connectivity --image=busybox:1.35 --rm -i --restart=Never -n "$NAMESPACE" -- /bin/sh -c "
        echo 'Probando conectividad interna...'
        
        # Probar MySQL
        echo 'Probando MySQL...'
        nc -z mysql-service 3306 && echo 'MySQL: OK' || echo 'MySQL: FAIL'
        
        # Probar Redis
        echo 'Probando Redis...'
        nc -z redis-service 6379 && echo 'Redis: OK' || echo 'Redis: FAIL'
        
        # Probar Eureka
        echo 'Probando Eureka...'
        nc -z eureka-service 8761 && echo 'Eureka: OK' || echo 'Eureka: FAIL'
        
        # Probar API Gateway
        echo 'Probando API Gateway...'
        nc -z api-gateway-service 8080 && echo 'API Gateway: OK' || echo 'API Gateway: FAIL'
        
        echo 'Pruebas de conectividad completadas'
    " 2>/dev/null || log_warning "No se pudieron ejecutar pruebas de conectividad"
}

# Función para verificar salud de APIs
check_api_health() {
    log_info "Verificando salud de APIs..."
    
    # Port-forward temporal para probar APIs
    services=(
        "config-server-service:8888"
        "eureka-service:8761"
        "api-gateway-service:8080"
    )
    
    for service_port in "${services[@]}"; do
        service=$(echo $service_port | cut -d: -f1)
        port=$(echo $service_port | cut -d: -f2)
        
        log_info "Probando $service en puerto $port..."
        
        # Port-forward en background
        kubectl port-forward "svc/$service" "$port:$port" -n "$NAMESPACE" &
        pf_pid=$!
        
        # Esperar un momento para que se establezca el port-forward
        sleep 3
        
        # Probar endpoint de salud
        if curl -s --max-time 5 "http://localhost:$port/actuator/health" &> /dev/null; then
            log_success "✅ $service responde correctamente"
        elif curl -s --max-time 5 "http://localhost:$port/health" &> /dev/null; then
            log_success "✅ $service responde correctamente"
        elif curl -s --max-time 5 "http://localhost:$port/" &> /dev/null; then
            log_success "✅ $service responde correctamente"
        else
            log_warning "⚠️  $service no responde o no tiene endpoint de salud"
        fi
        
        # Terminar port-forward
        kill $pf_pid 2>/dev/null || true
        sleep 1
    done
}

# Función para verificar monitoreo
check_monitoring() {
    log_info "Verificando servicios de monitoreo..."
    
    # Verificar Prometheus
    if kubectl get deployment prometheus -n "$MONITORING_NAMESPACE" &> /dev/null; then
        log_success "✅ Prometheus deployment encontrado"
        
        # Verificar configuración
        if kubectl get configmap prometheus-config -n "$MONITORING_NAMESPACE" &> /dev/null; then
            log_success "✅ Configuración de Prometheus encontrada"
        fi
    else
        log_warning "⚠️  Prometheus no encontrado"
    fi
    
    # Verificar Grafana
    if kubectl get deployment grafana -n "$MONITORING_NAMESPACE" &> /dev/null; then
        log_success "✅ Grafana deployment encontrado"
        
        # Verificar configuración
        if kubectl get configmap grafana-config -n "$MONITORING_NAMESPACE" &> /dev/null; then
            log_success "✅ Configuración de Grafana encontrada"
        fi
    else
        log_warning "⚠️  Grafana no encontrado"
    fi
}

# Función para mostrar resumen final
show_summary() {
    echo ""
    echo "📊 RESUMEN DE VALIDACIÓN"
    echo "========================"
    
    # Contar recursos
    total_deployments=$(kubectl get deployments -n "$NAMESPACE" --no-headers | wc -l)
    ready_deployments=$(kubectl get deployments -n "$NAMESPACE" --no-headers | awk '{if($2==$4) print $1}' | wc -l)
    
    total_pods=$(kubectl get pods -n "$NAMESPACE" --no-headers | wc -l)
    running_pods=$(kubectl get pods -n "$NAMESPACE" --no-headers | grep "Running" | wc -l)
    
    total_services=$(kubectl get services -n "$NAMESPACE" --no-headers | wc -l)
    
    echo "Deployments: $ready_deployments/$total_deployments listos"
    echo "Pods: $running_pods/$total_pods ejecutándose"
    echo "Servicios: $total_services configurados"
    
    # URLs de acceso
    echo ""
    echo "🌐 URLs DE ACCESO"
    echo "=================="
    echo ""
    echo "Para acceso local (configurar /etc/hosts):"
    echo "127.0.0.1 arka-ecommerce.local"
    echo "127.0.0.1 grafana.local"
    echo ""
    echo "URLs de la aplicación:"
    echo "• ARKA E-commerce: http://arka-ecommerce.local"
    echo "• Eureka Dashboard: http://arka-ecommerce.local:8761"
    echo "• API Gateway: http://arka-ecommerce.local/api"
    echo ""
    echo "URLs de monitoreo:"
    echo "• Grafana: http://grafana.local (admin/arka123)"
    echo "• Prometheus: kubectl port-forward -n arka-monitoring svc/prometheus 9090:9090"
    echo ""
    
    # Comandos útiles
    echo "🛠️  COMANDOS ÚTILES"
    echo "==================="
    echo ""
    echo "Ver logs de un servicio:"
    echo "kubectl logs -f deployment/ecommerce-core -n $NAMESPACE"
    echo ""
    echo "Acceder a MySQL:"
    echo "kubectl exec -it deployment/mysql-deployment -n $NAMESPACE -- mysql -u root -parka123"
    echo ""
    echo "Port-forward para desarrollo:"
    echo "kubectl port-forward svc/api-gateway-service 8080:8080 -n $NAMESPACE"
    echo ""
    echo "Monitorear recursos:"
    echo "watch kubectl top pods -n $NAMESPACE"
}

# Función para verificar problemas comunes
check_common_issues() {
    log_info "Verificando problemas comunes..."
    
    # Verificar recursos de nodos
    log_info "Recursos de nodos:"
    kubectl top nodes 2>/dev/null || log_warning "No se pueden obtener métricas de nodos (metrics-server no instalado)"
    
    # Verificar eventos del sistema
    log_info "Eventos recientes del sistema:"
    kubectl get events -n "$NAMESPACE" --sort-by=.metadata.creationTimestamp | tail -5
    
    # Verificar imagenes que no se pueden descargar
    image_pull_errors=$(kubectl get pods -n "$NAMESPACE" -o jsonpath='{.items[*].status.containerStatuses[*].state.waiting.reason}' | grep -o "ImagePullBackOff\|ErrImagePull" | wc -l)
    
    if [ "$image_pull_errors" -gt 0 ]; then
        log_warning "⚠️  Se detectaron $image_pull_errors errores de descarga de imágenes"
        log_info "Pods con errores de imagen:"
        kubectl get pods -n "$NAMESPACE" | grep -E "ImagePullBackOff|ErrImagePull"
    fi
}

# Función principal
main() {
    echo "Iniciando validación post-despliegue..."
    echo "Namespace principal: $NAMESPACE"
    echo "Namespace de monitoreo: $MONITORING_NAMESPACE"
    echo ""
    
    # Ejecutar todas las verificaciones
    check_kubectl
    check_cluster_connectivity
    check_namespaces
    check_deployments
    check_pods
    check_services
    check_storage
    check_ingress
    test_service_connectivity
    check_api_health
    check_monitoring
    check_common_issues
    
    # Mostrar resumen
    show_summary
    
    echo ""
    log_success "🎉 Validación post-despliegue completada!"
    echo ""
    echo "Si encontraste problemas, revisa:"
    echo "• Los logs de los pods: kubectl logs <pod-name> -n $NAMESPACE"
    echo "• El estado de los recursos: kubectl describe <resource> <name> -n $NAMESPACE"
    echo "• La documentación de troubleshooting en README.md"
}

# Mostrar ayuda
show_help() {
    echo "ARKA E-commerce Platform - Post-Deployment Validation"
    echo ""
    echo "Este script valida que todos los servicios estén funcionando correctamente"
    echo "después del despliegue en Kubernetes."
    echo ""
    echo "Uso: $0 [OPTIONS]"
    echo ""
    echo "Opciones:"
    echo "  -h, --help    Mostrar esta ayuda"
    echo "  -n NAMESPACE  Especificar namespace (default: arka-ecommerce)"
    echo "  -m NAMESPACE  Especificar namespace de monitoreo (default: arka-monitoring)"
    echo ""
    echo "Ejemplo:"
    echo "  $0                      # Validación completa con namespaces por defecto"
    echo "  $0 -n production       # Validar namespace 'production'"
}

# Procesar argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -n)
            NAMESPACE="$2"
            shift 2
            ;;
        -m)
            MONITORING_NAMESPACE="$2"
            shift 2
            ;;
        *)
            log_error "Opción desconocida: $1"
            show_help
            exit 1
            ;;
    esac
done

# Ejecutar función principal
main "$@"
