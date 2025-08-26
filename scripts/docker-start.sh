#!/bin/bash

echo "🐳 ARKA - Docker Compose Management"
echo "===================================="
echo ""

# Check if docker-compose.yml exists
if [[ ! -f "$(dirname "$0")/../docker-compose.yml" ]]; then
    echo "❌ ERROR: docker-compose.yml no encontrado"
    echo "   Por favor, asegúrate de que el archivo docker-compose.yml esté en el directorio raíz"
    exit 1
fi

# Navigate to project root
cd "$(dirname "$0")/.."

echo "📁 Directorio del proyecto: $(pwd)"
echo ""

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ ERROR: Docker no está corriendo"
    echo "   Por favor, inicia Docker Desktop y vuelve a intentar"
    exit 1
fi

echo "✅ Docker está disponible"
echo ""

# Function to check if services are already running
check_running_services() {
    echo "🔍 Verificando servicios en ejecución..."
    
    if docker-compose ps | grep -q "Up"; then
        echo "⚠️  Algunos servicios ya están corriendo:"
        docker-compose ps
        echo ""
        read -p "¿Quieres parar los servicios existentes y reiniciar? (y/N): " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo "🛑 Parando servicios existentes..."
            docker-compose down
            echo ""
        else
            echo "Continuando con servicios existentes..."
            return 1
        fi
    fi
    return 0
}

# Main execution
echo "🚀 Iniciando ARKA con Docker Compose..."
echo ""

# Check for running services
check_running_services

# Build images if needed
echo "🔨 Construyendo imágenes Docker..."
if docker-compose build; then
    echo "✅ Imágenes construidas exitosamente"
else
    echo "❌ ERROR: Falló la construcción de imágenes"
    exit 1
fi

echo ""
echo "🌟 Levantando servicios..."

# Start services
if docker-compose up -d; then
    echo "✅ Servicios iniciados en modo detached"
else
    echo "❌ ERROR: Falló el inicio de servicios"
    exit 1
fi

echo ""
echo "⏳ Esperando que los servicios estén listos..."
sleep 30

# Check service health
echo ""
echo "🔍 Verificando estado de servicios..."
docker-compose ps

echo ""
echo "🏥 Verificando health checks..."

# Check each service health
services=("eureka-server:8761" "api-gateway:8080" "arca-cotizador:8081" "arca-gestor-solicitudes:8082" "hello-world-service:8083")

for service in "${services[@]}"; do
    IFS=':' read -r service_name port <<< "$service"
    
    echo -n "  $service_name (puerto $port): "
    
    # Wait a bit and try health check
    sleep 2
    
    if curl -s "http://localhost:$port/actuator/health" >/dev/null 2>&1; then
        echo "✅ OK"
    else
        echo "⚠️  No responde aún"
    fi
done

echo ""
echo "🎯 ARKA está corriendo con Docker!"
echo ""
echo "🔗 URLs de Acceso:"
echo "• Eureka Dashboard: http://localhost:8761"
echo "• API Gateway: http://localhost:8080"
echo "• Arca Cotizador: http://localhost:8081"
echo "• Arca Gestor Solicitudes: http://localhost:8082"
echo "• Hello World Service: http://localhost:8083"

echo ""
echo "🛠️  Comandos útiles:"
echo "docker-compose logs -f [servicio]     # Ver logs"
echo "docker-compose ps                     # Estado de servicios"
echo "docker-compose restart [servicio]     # Reiniciar servicio"
echo "docker-compose down                   # Parar todo"

echo ""
echo "🧪 Pruebas rápidas:"
echo "curl http://localhost:8080/api/hello"
echo "curl http://localhost:8761/eureka/apps"

echo ""
echo "📝 Para ver logs en tiempo real:"
echo "docker-compose logs -f"
