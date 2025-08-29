#!/bin/bash

# ARKA E-commerce Platform - Docker Images Build Script
# Script para construir todas las imágenes Docker para Kubernetes

set -e

echo "🐳 ARKA E-commerce Platform - Docker Images Build"
echo "================================================"

# Variables de configuración
REGISTRY="arka"
VERSION=${1:-latest}
BUILD_ALL=${2:-true}

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

# Verificar que Docker esté funcionando
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker no está funcionando. Por favor inicia Docker."
        exit 1
    fi
    log_success "Docker está disponible"
}

# Construir imagen base si es necesario
build_base_image() {
    if [ -f "Dockerfile.base" ]; then
        log_info "Construyendo imagen base..."
        docker build -f Dockerfile.base -t ${REGISTRY}/base:${VERSION} .
        log_success "Imagen base construida: ${REGISTRY}/base:${VERSION}"
    fi
}

# Construir todas las aplicaciones Spring Boot
build_spring_boot_images() {
    log_info "Construyendo imágenes de aplicaciones Spring Boot..."
    
    # E-commerce Core (aplicación principal)
    log_info "Construyendo E-commerce Core..."
    docker build -t ${REGISTRY}/ecommerce-core:${VERSION} \
        --build-arg JAR_FILE=build/libs/arkavalenzuela-0.0.1-SNAPSHOT.jar \
        .
    log_success "E-commerce Core imagen construida: ${REGISTRY}/ecommerce-core:${VERSION}"
    
    # Config Server
    if [ -d "config-server" ]; then
        log_info "Construyendo Config Server..."
        cd config-server
        docker build -t ${REGISTRY}/config-server:${VERSION} .
        cd ..
        log_success "Config Server imagen construida: ${REGISTRY}/config-server:${VERSION}"
    fi
    
    # Eureka Server
    if [ -d "eureka-server" ]; then
        log_info "Construyendo Eureka Server..."
        cd eureka-server
        docker build -t ${REGISTRY}/eureka-server:${VERSION} .
        cd ..
        log_success "Eureka Server imagen construida: ${REGISTRY}/eureka-server:${VERSION}"
    fi
    
    # API Gateway
    if [ -d "api-gateway" ]; then
        log_info "Construyendo API Gateway..."
        cd api-gateway
        docker build -t ${REGISTRY}/api-gateway:${VERSION} .
        cd ..
        log_success "API Gateway imagen construida: ${REGISTRY}/api-gateway:${VERSION}"
    fi
    
    # Arca Cotizador
    if [ -d "arca-cotizador" ]; then
        log_info "Construyendo Arca Cotizador..."
        cd arca-cotizador
        docker build -t ${REGISTRY}/arca-cotizador:${VERSION} .
        cd ..
        log_success "Arca Cotizador imagen construida: ${REGISTRY}/arca-cotizador:${VERSION}"
    fi
    
    # Arca Gestor Solicitudes
    if [ -d "arca-gestor-solicitudes" ]; then
        log_info "Construyendo Arca Gestor Solicitudes..."
        cd arca-gestor-solicitudes
        docker build -t ${REGISTRY}/arca-gestor-solicitudes:${VERSION} .
        cd ..
        log_success "Arca Gestor Solicitudes imagen construida: ${REGISTRY}/arca-gestor-solicitudes:${VERSION}"
    fi
    
    # Hello World Service
    if [ -d "hello-world-service" ]; then
        log_info "Construyendo Hello World Service..."
        cd hello-world-service
        docker build -t ${REGISTRY}/hello-world-service:${VERSION} .
        cd ..
        log_success "Hello World Service imagen construida: ${REGISTRY}/hello-world-service:${VERSION}"
    fi
}

# Compilar aplicaciones antes de construir imágenes
compile_applications() {
    log_info "Compilando aplicaciones..."
    
    # Compilar aplicación principal
    log_info "Compilando E-commerce Core..."
    ./gradlew clean build -x test
    
    # Compilar microservicios
    for service in config-server eureka-server api-gateway arca-cotizador arca-gestor-solicitudes hello-world-service; do
        if [ -d "$service" ]; then
            log_info "Compilando $service..."
            cd $service
            ../gradlew clean build -x test
            cd ..
        fi
    done
    
    log_success "Todas las aplicaciones compiladas exitosamente"
}

# Listar imágenes construidas
list_built_images() {
    log_info "Imágenes construidas:"
    docker images | grep "^${REGISTRY}"
}

# Función para probar las imágenes
test_images() {
    log_info "Probando imágenes construidas..."
    
    # Probar que las imágenes se pueden ejecutar
    for image in ecommerce-core config-server eureka-server api-gateway arca-cotizador arca-gestor-solicitudes hello-world-service; do
        if docker images | grep -q "${REGISTRY}/${image}"; then
            log_info "Probando imagen ${REGISTRY}/${image}:${VERSION}..."
            # Ejecutar un contenedor de prueba que se detiene inmediatamente
            if docker run --rm ${REGISTRY}/${image}:${VERSION} echo "Image test successful" > /dev/null 2>&1; then
                log_success "✅ ${REGISTRY}/${image}:${VERSION} - OK"
            else
                log_warning "⚠️  ${REGISTRY}/${image}:${VERSION} - Posible problema"
            fi
        fi
    done
}

# Función para pushear imágenes a un registry
push_images() {
    local registry_url=$1
    
    if [ -z "$registry_url" ]; then
        log_warning "No se especificó registry. Saltando push de imágenes."
        return
    fi
    
    log_info "Pusheando imágenes a registry: $registry_url"
    
    for image in ecommerce-core config-server eureka-server api-gateway arca-cotizador arca-gestor-solicitudes hello-world-service; do
        if docker images | grep -q "${REGISTRY}/${image}"; then
            log_info "Pusheando ${REGISTRY}/${image}:${VERSION}..."
            
            # Tag para el registry remoto
            docker tag ${REGISTRY}/${image}:${VERSION} ${registry_url}/${image}:${VERSION}
            docker tag ${REGISTRY}/${image}:${VERSION} ${registry_url}/${image}:latest
            
            # Push de las imágenes
            docker push ${registry_url}/${image}:${VERSION}
            docker push ${registry_url}/${image}:latest
            
            log_success "✅ ${registry_url}/${image} pushed"
        fi
    done
}

# Función para limpiar imágenes viejas
cleanup_old_images() {
    log_info "Limpiando imágenes viejas..."
    
    # Eliminar imágenes no utilizadas
    docker image prune -f
    
    # Eliminar contenedores parados
    docker container prune -f
    
    log_success "Limpieza completada"
}

# Función para mostrar estadísticas de las imágenes
show_image_stats() {
    log_info "Estadísticas de imágenes construidas:"
    echo ""
    printf "%-30s %-15s %-15s\n" "IMAGEN" "TAMAÑO" "CREADA"
    printf "%-30s %-15s %-15s\n" "-----------------------------" "----------" "----------"
    
    for image in ecommerce-core config-server eureka-server api-gateway arca-cotizador arca-gestor-solicitudes hello-world-service; do
        if docker images | grep -q "${REGISTRY}/${image}"; then
            size=$(docker images --format "table {{.Size}}" ${REGISTRY}/${image}:${VERSION} | tail -n 1)
            created=$(docker images --format "table {{.CreatedSince}}" ${REGISTRY}/${image}:${VERSION} | tail -n 1)
            printf "%-30s %-15s %-15s\n" "${REGISTRY}/${image}:${VERSION}" "$size" "$created"
        fi
    done
    echo ""
}

# Función principal
main() {
    echo "Iniciando construcción de imágenes Docker para ARKA Platform..."
    echo "Versión: $VERSION"
    echo "Registry: $REGISTRY"
    echo ""
    
    # Verificaciones iniciales
    check_docker
    
    # Compilar aplicaciones
    compile_applications
    
    # Construir imágenes
    build_base_image
    build_spring_boot_images
    
    # Probar imágenes
    test_images
    
    # Mostrar resultados
    echo ""
    list_built_images
    echo ""
    show_image_stats
    
    # Cleanup opcional
    echo ""
    read -p "¿Deseas limpiar imágenes viejas? (y/n): " -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        cleanup_old_images
    fi
    
    # Push opcional
    echo ""
    read -p "¿Deseas pushear las imágenes a un registry? (ingresa URL o presiona Enter para saltar): " -r registry_url
    if [ ! -z "$registry_url" ]; then
        push_images "$registry_url"
    fi
    
    echo ""
    log_success "🎉 Construcción de imágenes Docker completada!"
    echo ""
    echo "📋 Para desplegar en Kubernetes:"
    echo "   ./k8s/deploy-k8s.sh"
    echo ""
    echo "🔍 Para verificar imágenes:"
    echo "   docker images | grep arka"
}

# Mostrar ayuda
show_help() {
    echo "ARKA E-commerce Platform - Docker Build Script"
    echo ""
    echo "Uso: $0 [VERSION] [BUILD_ALL]"
    echo ""
    echo "Parámetros:"
    echo "  VERSION     Versión de las imágenes (default: latest)"
    echo "  BUILD_ALL   Construir todas las imágenes (default: true)"
    echo ""
    echo "Ejemplos:"
    echo "  $0                    # Construir con version 'latest'"
    echo "  $0 v1.0.0            # Construir con version 'v1.0.0'"
    echo "  $0 dev false         # Construir solo imágenes modificadas"
    echo ""
    echo "Servicios que se construyen:"
    echo "  • ecommerce-core"
    echo "  • config-server"
    echo "  • eureka-server"
    echo "  • api-gateway"
    echo "  • arca-cotizador"
    echo "  • arca-gestor-solicitudes"
    echo "  • hello-world-service"
}

# Verificar argumentos
if [[ $1 == "--help" ]] || [[ $1 == "-h" ]]; then
    show_help
    exit 0
fi

# Ejecutar función principal
main "$@"
