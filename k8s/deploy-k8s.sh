#!/bin/bash

# ARKA E-commerce Platform - Kubernetes Deployment Script
# Compatible with k3s and standard Kubernetes clusters

set -e

echo "🚀 ARKA E-commerce Platform - Kubernetes Deployment"
echo "=================================================="

# Variables de configuración
NAMESPACE="arka-ecommerce"
MONITORING_NAMESPACE="arka-monitoring"
KUBECTL_TIMEOUT="600s"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para logging
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

# Verificar si kubectl está disponible
check_kubectl() {
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl no está instalado. Por favor instala kubectl."
        exit 1
    fi
    log_success "kubectl está disponible"
}

# Verificar conexión al cluster
check_cluster_connection() {
    log_info "Verificando conexión al cluster Kubernetes..."
    if kubectl cluster-info &> /dev/null; then
        log_success "Conexión al cluster establecida"
        kubectl cluster-info
    else
        log_error "No se puede conectar al cluster Kubernetes"
        exit 1
    fi
}

# Crear namespaces
create_namespaces() {
    log_info "Creando namespaces..."
    kubectl apply -f k8s/namespace.yaml
    log_success "Namespaces creados exitosamente"
}

# Desplegar bases de datos
deploy_databases() {
    log_info "Desplegando bases de datos..."
    
    # MySQL
    log_info "Desplegando MySQL..."
    kubectl apply -f k8s/mysql.yaml
    
    # MongoDB
    log_info "Desplegando MongoDB..."
    kubectl apply -f k8s/mongodb.yaml
    
    # Redis
    log_info "Desplegando Redis..."
    kubectl apply -f k8s/redis.yaml
    
    log_success "Bases de datos desplegadas"
    
    # Esperar a que las bases de datos estén listas
    log_info "Esperando a que las bases de datos estén listas..."
    kubectl wait --for=condition=ready pod -l app=mysql -n $NAMESPACE --timeout=$KUBECTL_TIMEOUT
    kubectl wait --for=condition=ready pod -l app=mongodb -n $NAMESPACE --timeout=$KUBECTL_TIMEOUT
    kubectl wait --for=condition=ready pod -l app=redis -n $NAMESPACE --timeout=$KUBECTL_TIMEOUT
    log_success "Todas las bases de datos están listas"
}

# Desplegar infraestructura (Config Server, Eureka)
deploy_infrastructure() {
    log_info "Desplegando servicios de infraestructura..."
    
    # Config Server
    log_info "Desplegando Config Server..."
    kubectl apply -f k8s/config-server.yaml
    
    # Esperar a que Config Server esté listo
    log_info "Esperando a que Config Server esté listo..."
    kubectl wait --for=condition=ready pod -l app=config-server -n $NAMESPACE --timeout=$KUBECTL_TIMEOUT
    
    # Eureka Server
    log_info "Desplegando Eureka Server..."
    kubectl apply -f k8s/eureka-server.yaml
    
    # Esperar a que Eureka esté listo
    log_info "Esperando a que Eureka Server esté listo..."
    kubectl wait --for=condition=ready pod -l app=eureka-server -n $NAMESPACE --timeout=$KUBECTL_TIMEOUT
    
    log_success "Servicios de infraestructura desplegados y listos"
}

# Desplegar API Gateway
deploy_gateway() {
    log_info "Desplegando API Gateway..."
    kubectl apply -f k8s/api-gateway.yaml
    
    # Esperar a que API Gateway esté listo
    log_info "Esperando a que API Gateway esté listo..."
    kubectl wait --for=condition=ready pod -l app=api-gateway -n $NAMESPACE --timeout=$KUBECTL_TIMEOUT
    
    log_success "API Gateway desplegado y listo"
}

# Desplegar microservicios
deploy_microservices() {
    log_info "Desplegando microservicios de aplicación..."
    
    # E-commerce Core
    log_info "Desplegando E-commerce Core..."
    kubectl apply -f k8s/ecommerce-core.yaml
    
    # Arca Cotizador
    log_info "Desplegando Arca Cotizador..."
    kubectl apply -f k8s/arca-cotizador.yaml
    
    # Arca Gestor Solicitudes
    log_info "Desplegando Arca Gestor Solicitudes..."
    kubectl apply -f k8s/arca-gestor-solicitudes.yaml
    
    # Hello World Service
    log_info "Desplegando Hello World Service..."
    kubectl apply -f k8s/hello-world-service.yaml
    
    # Esperar a que todos los microservicios estén listos
    log_info "Esperando a que todos los microservicios estén listos..."
    kubectl wait --for=condition=ready pod -l app=ecommerce-core -n $NAMESPACE --timeout=$KUBECTL_TIMEOUT
    kubectl wait --for=condition=ready pod -l app=arca-cotizador -n $NAMESPACE --timeout=$KUBECTL_TIMEOUT
    kubectl wait --for=condition=ready pod -l app=arca-gestor-solicitudes -n $NAMESPACE --timeout=$KUBECTL_TIMEOUT
    kubectl wait --for=condition=ready pod -l app=hello-world-service -n $NAMESPACE --timeout=$KUBECTL_TIMEOUT
    
    log_success "Todos los microservicios están desplegados y listos"
}

# Desplegar monitoreo
deploy_monitoring() {
    log_info "Desplegando sistema de monitoreo..."
    
    # Prometheus
    log_info "Desplegando Prometheus..."
    kubectl apply -f k8s/monitoring-prometheus.yaml
    
    # Grafana
    log_info "Desplegando Grafana..."
    kubectl apply -f k8s/monitoring-grafana.yaml
    
    # Esperar a que el monitoreo esté listo
    log_info "Esperando a que el sistema de monitoreo esté listo..."
    kubectl wait --for=condition=ready pod -l app=prometheus -n $MONITORING_NAMESPACE --timeout=$KUBECTL_TIMEOUT
    kubectl wait --for=condition=ready pod -l app=grafana -n $MONITORING_NAMESPACE --timeout=$KUBECTL_TIMEOUT
    
    log_success "Sistema de monitoreo desplegado y listo"
}

# Desplegar Ingress y networking
deploy_networking() {
    log_info "Desplegando configuración de red e Ingress..."
    kubectl apply -f k8s/ingress-and-networking.yaml
    log_success "Configuración de red desplegada"
}

# Verificar el estado del deployment
check_deployment_status() {
    log_info "Verificando estado del deployment..."
    
    echo ""
    echo "📊 Estado de los Pods:"
    kubectl get pods -n $NAMESPACE -o wide
    
    echo ""
    echo "🔗 Estado de los Servicios:"
    kubectl get services -n $NAMESPACE
    
    echo ""
    echo "📈 Estado del Monitoreo:"
    kubectl get pods -n $MONITORING_NAMESPACE -o wide
    
    echo ""
    echo "🌐 Ingress:"
    kubectl get ingress -n $NAMESPACE
    
    echo ""
    echo "📋 Información de acceso:"
    echo "- API Gateway (LoadBalancer): kubectl get svc api-gateway-lb -n $NAMESPACE"
    echo "- Eureka Dashboard: kubectl port-forward svc/eureka-service 8761:8761 -n $NAMESPACE"
    echo "- Grafana Dashboard: kubectl port-forward svc/grafana-service 3000:3000 -n $MONITORING_NAMESPACE"
    echo "- Prometheus: kubectl port-forward svc/prometheus-service 9090:9090 -n $MONITORING_NAMESPACE"
}

# Función para mostrar información de acceso
show_access_info() {
    log_info "Obteniendo información de acceso..."
    
    # Obtener IP del LoadBalancer si está disponible
    LB_IP=$(kubectl get svc api-gateway-lb -n $NAMESPACE -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "Pending")
    
    echo ""
    echo "🎉 ¡Deployment completado exitosamente!"
    echo "========================================"
    echo ""
    echo "📍 URLs de Acceso:"
    if [ "$LB_IP" != "Pending" ] && [ "$LB_IP" != "" ]; then
        echo "- API Gateway: http://$LB_IP"
        echo "- Eureka Dashboard: http://$LB_IP:30761"
        echo "- Grafana: http://$LB_IP:30300 (admin/ArkaGrafana2025)"
        echo "- Prometheus: http://$LB_IP:30090"
    else
        echo "- Para acceder a los servicios, usar port-forward:"
        echo "  kubectl port-forward svc/api-gateway-service 8080:8080 -n $NAMESPACE"
        echo "  kubectl port-forward svc/eureka-service 8761:8761 -n $NAMESPACE"
        echo "  kubectl port-forward svc/grafana-service 3000:3000 -n $MONITORING_NAMESPACE"
        echo "  kubectl port-forward svc/prometheus-service 9090:9090 -n $MONITORING_NAMESPACE"
    fi
    
    echo ""
    echo "🔍 Comandos útiles:"
    echo "- Ver logs: kubectl logs -f deployment/[service-name] -n $NAMESPACE"
    echo "- Ver pods: kubectl get pods -n $NAMESPACE"
    echo "- Escalar servicio: kubectl scale deployment [service-name] --replicas=3 -n $NAMESPACE"
    echo "- Eliminar deployment: ./k8s-undeploy.sh"
}

# Función principal
main() {
    echo "Iniciando deployment de ARKA E-commerce Platform..."
    
    # Verificaciones iniciales
    check_kubectl
    check_cluster_connection
    
    # Deployment secuencial
    create_namespaces
    deploy_databases
    deploy_infrastructure
    deploy_gateway
    deploy_microservices
    deploy_monitoring
    deploy_networking
    
    # Verificaciones finales
    check_deployment_status
    show_access_info
    
    log_success "¡Deployment de ARKA E-commerce Platform completado!"
}

# Ejecutar función principal
main "$@"
