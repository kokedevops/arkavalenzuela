#!/bin/bash

# ARKA E-commerce Platform - Kubernetes Undeploy Script
# Script para eliminar todos los recursos de Kubernetes

set -e

echo "🗑️  ARKA E-commerce Platform - Kubernetes Cleanup"
echo "================================================="

# Variables
NAMESPACE="arka-ecommerce"
MONITORING_NAMESPACE="arka-monitoring"

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

# Función para confirmar la eliminación
confirm_deletion() {
    echo ""
    log_warning "⚠️  ADVERTENCIA: Esta operación eliminará TODOS los recursos de ARKA E-commerce Platform"
    log_warning "Esto incluye:"
    echo "   • Todos los microservicios"
    echo "   • Todas las bases de datos y sus datos"
    echo "   • Sistema de monitoreo"
    echo "   • Configuraciones de red"
    echo "   • Volúmenes persistentes"
    echo ""
    read -p "¿Estás seguro de que quieres continuar? (yes/no): " -r
    echo ""
    
    if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
        log_info "Operación cancelada por el usuario"
        exit 0
    fi
}

# Eliminar recursos de aplicación
delete_applications() {
    log_info "Eliminando microservicios de aplicación..."
    
    kubectl delete -f k8s/hello-world-service.yaml --ignore-not-found=true
    kubectl delete -f k8s/arca-gestor-solicitudes.yaml --ignore-not-found=true
    kubectl delete -f k8s/arca-cotizador.yaml --ignore-not-found=true
    kubectl delete -f k8s/ecommerce-core.yaml --ignore-not-found=true
    
    log_success "Microservicios eliminados"
}

# Eliminar gateway e infraestructura
delete_infrastructure() {
    log_info "Eliminando API Gateway e infraestructura..."
    
    kubectl delete -f k8s/api-gateway.yaml --ignore-not-found=true
    kubectl delete -f k8s/eureka-server.yaml --ignore-not-found=true
    kubectl delete -f k8s/config-server.yaml --ignore-not-found=true
    
    log_success "Infraestructura eliminada"
}

# Eliminar bases de datos
delete_databases() {
    log_info "Eliminando bases de datos..."
    
    kubectl delete -f k8s/redis.yaml --ignore-not-found=true
    kubectl delete -f k8s/mongodb.yaml --ignore-not-found=true
    kubectl delete -f k8s/mysql.yaml --ignore-not-found=true
    
    log_success "Bases de datos eliminadas"
}

# Eliminar monitoreo
delete_monitoring() {
    log_info "Eliminando sistema de monitoreo..."
    
    kubectl delete -f k8s/monitoring-grafana.yaml --ignore-not-found=true
    kubectl delete -f k8s/monitoring-prometheus.yaml --ignore-not-found=true
    
    log_success "Sistema de monitoreo eliminado"
}

# Eliminar networking
delete_networking() {
    log_info "Eliminando configuración de red..."
    
    kubectl delete -f k8s/ingress-and-networking.yaml --ignore-not-found=true
    
    log_success "Configuración de red eliminada"
}

# Eliminar PVCs y volúmenes persistentes
delete_persistent_volumes() {
    log_info "Eliminando volúmenes persistentes..."
    
    # Eliminar PVCs en namespace principal
    kubectl delete pvc --all -n $NAMESPACE --ignore-not-found=true
    
    # Eliminar PVCs en namespace de monitoreo
    kubectl delete pvc --all -n $MONITORING_NAMESPACE --ignore-not-found=true
    
    log_success "Volúmenes persistentes eliminados"
}

# Eliminar secrets y configmaps
delete_configs() {
    log_info "Eliminando configuraciones y secretos..."
    
    # Eliminar todos los secrets (excepto los del sistema)
    kubectl delete secrets --all -n $NAMESPACE --ignore-not-found=true
    kubectl delete secrets --all -n $MONITORING_NAMESPACE --ignore-not-found=true
    
    # Eliminar todos los configmaps (excepto los del sistema)
    kubectl delete configmaps --all -n $NAMESPACE --ignore-not-found=true
    kubectl delete configmaps --all -n $MONITORING_NAMESPACE --ignore-not-found=true
    
    log_success "Configuraciones eliminadas"
}

# Eliminar namespaces
delete_namespaces() {
    log_info "Eliminando namespaces..."
    
    kubectl delete namespace $NAMESPACE --ignore-not-found=true
    kubectl delete namespace $MONITORING_NAMESPACE --ignore-not-found=true
    
    # Esperar a que los namespaces se eliminen completamente
    log_info "Esperando a que los namespaces se eliminen completamente..."
    kubectl wait --for=delete namespace/$NAMESPACE --timeout=300s 2>/dev/null || true
    kubectl wait --for=delete namespace/$MONITORING_NAMESPACE --timeout=300s 2>/dev/null || true
    
    log_success "Namespaces eliminados"
}

# Limpiar recursos huérfanos
cleanup_orphaned_resources() {
    log_info "Limpiando recursos huérfanos..."
    
    # Eliminar pods huérfanos
    kubectl get pods --all-namespaces | grep -E "(arka-|config-server|eureka-)" | awk '{print $1 " " $2}' | xargs -r -n2 kubectl delete pod --ignore-not-found=true -n 2>/dev/null || true
    
    # Eliminar servicios huérfanos
    kubectl get services --all-namespaces | grep -E "(arka-|config-server|eureka-)" | awk '{print $1 " " $2}' | xargs -r -n2 kubectl delete service --ignore-not-found=true -n 2>/dev/null || true
    
    log_success "Recursos huérfanos limpiados"
}

# Verificar estado final
verify_cleanup() {
    log_info "Verificando estado final de la limpieza..."
    
    echo ""
    echo "📊 Recursos restantes relacionados con ARKA:"
    
    # Verificar pods
    REMAINING_PODS=$(kubectl get pods --all-namespaces | grep -E "(arka-|config-server|eureka-)" | wc -l)
    echo "   • Pods: $REMAINING_PODS"
    
    # Verificar servicios
    REMAINING_SERVICES=$(kubectl get services --all-namespaces | grep -E "(arka-|config-server|eureka-)" | wc -l)
    echo "   • Services: $REMAINING_SERVICES"
    
    # Verificar namespaces
    REMAINING_NAMESPACES=$(kubectl get namespaces | grep -E "(arka-)" | wc -l)
    echo "   • Namespaces: $REMAINING_NAMESPACES"
    
    # Verificar PVCs
    REMAINING_PVCS=$(kubectl get pvc --all-namespaces | grep -E "(mysql|mongodb|redis|prometheus|grafana)" | wc -l)
    echo "   • PVCs: $REMAINING_PVCS"
    
    echo ""
    
    if [ "$REMAINING_PODS" -eq 0 ] && [ "$REMAINING_SERVICES" -eq 0 ] && [ "$REMAINING_NAMESPACES" -eq 0 ]; then
        log_success "✅ Limpieza completada exitosamente - No quedan recursos de ARKA"
    else
        log_warning "⚠️  Algunos recursos pueden necesitar más tiempo para eliminarse"
        log_info "Puedes verificar el estado con: kubectl get all --all-namespaces | grep arka"
    fi
}

# Función principal
main() {
    echo "Iniciando limpieza de ARKA E-commerce Platform..."
    
    # Verificar kubectl
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl no está instalado"
        exit 1
    fi
    
    # Verificar conexión al cluster
    if ! kubectl cluster-info &> /dev/null; then
        log_error "No se puede conectar al cluster Kubernetes"
        exit 1
    fi
    
    # Confirmar eliminación
    confirm_deletion
    
    # Ejecutar limpieza en orden
    delete_networking
    delete_applications
    delete_infrastructure
    delete_databases
    delete_monitoring
    delete_persistent_volumes
    delete_configs
    cleanup_orphaned_resources
    delete_namespaces
    
    # Verificar resultado
    verify_cleanup
    
    echo ""
    log_success "🎉 Limpieza de ARKA E-commerce Platform completada"
    echo ""
    log_info "Para volver a desplegar la plataforma, ejecuta: ./deploy-k8s.sh"
}

# Ejecutar función principal
main "$@"
