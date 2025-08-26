#!/bin/bash

echo "=========================================="
echo "INICIANDO MICROSERVICIOS CON CONFIG SERVER"
echo "=========================================="
echo ""

echo "⚠️  IMPORTANTE: Asegúrate de que el Config Server esté ejecutándose en:"
echo "   http://127.0.0.1:9090"
echo ""
echo "🔍 Verificando Config Server..."

# Check if Config Server is running
if curl -s http://127.0.0.1:9090/actuator/health > /dev/null 2>&1; then
    echo "✅ Config Server está accesible"
else
    echo "❌ ERROR: Config Server no está accesible en http://127.0.0.1:9090"
    echo ""
    echo "Por favor:"
    echo "1. Inicia el Config Server primero"
    echo "2. Verifica que esté corriendo en el puerto 9090"
    echo "3. Ejecuta este script nuevamente"
    echo ""
    read -p "¿Continuar de todos modos? (y/N): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Cancelando..."
        exit 1
    fi
fi

echo ""
echo "🚀 Iniciando servicios con configuración centralizada..."

# Function to start service with config server
start_service_with_config() {
    local service_name="$1"
    local service_dir="$2"
    local port="$3"
    
    echo ""
    echo "📦 $service_name..."
    cd "$(dirname "$0")/../$service_dir"
    
    gnome-terminal --title="$service_name" --tab -- bash -c "./gradlew bootRun; exec bash" 2>/dev/null || \
    xterm -title "$service_name" -e "bash -c './gradlew bootRun; exec bash'" 2>/dev/null || \
    konsole --title "$service_name" -e bash -c "./gradlew bootRun; exec bash" 2>/dev/null || \
    {
        echo "No suitable terminal found. Starting in background..."
        mkdir -p ../logs
        nohup ./gradlew bootRun > ../logs/${service_name,,}.log 2>&1 &
        echo "Logs available at: logs/${service_name,,}.log"
    }
    
    cd - > /dev/null
}

echo ""
echo "1️⃣ Iniciando Eureka Server..."
start_service_with_config "Eureka Server" "eureka-server" "8761"

echo "⏳ Esperando 30 segundos para que Eureka Server se inicie completamente..."
sleep 30

# Verify Eureka is running
if curl -s http://localhost:8761/eureka/apps > /dev/null 2>&1; then
    echo "✅ Eureka Server iniciado correctamente"
else
    echo "⚠️  Eureka Server puede no haber iniciado completamente"
fi

echo ""
echo "2️⃣ Iniciando API Gateway..."
start_service_with_config "API Gateway" "api-gateway" "8080"

echo "⏳ Esperando 20 segundos para que API Gateway se registre..."
sleep 20

echo ""
echo "3️⃣ Iniciando Arca Cotizador..."
start_service_with_config "Arca Cotizador" "arca-cotizador" "8081"

echo "⏳ Esperando 15 segundos..."
sleep 15

echo ""
echo "4️⃣ Iniciando Arca Gestor Solicitudes..."
start_service_with_config "Arca Gestor Solicitudes" "arca-gestor-solicitudes" "8082"

echo "⏳ Esperando 15 segundos..."
sleep 15

echo ""
echo "5️⃣ Iniciando Hello World Service..."
start_service_with_config "Hello World Service" "hello-world-service" "8083"

echo ""
echo "=========================================="
echo "🎉 TODOS LOS SERVICIOS INICIADOS!"
echo "=========================================="
echo ""
echo "🔗 URLs importantes:"
echo "• Config Server: http://127.0.0.1:9090"
echo "• Eureka Server: http://localhost:8761"
echo "• API Gateway: http://localhost:8080"
echo "• Arca Cotizador: http://localhost:8081"
echo "• Arca Gestor Solicitudes: http://localhost:8082"
echo "• Hello World Service: http://localhost:8083"
echo ""
echo "🛠️  Endpoints de gestión:"
echo "• Refresh configs: POST /actuator/refresh en cada servicio"
echo "• Health check: GET /actuator/health en cada servicio"
echo ""
echo "🧪 Comandos de prueba:"
echo "curl http://127.0.0.1:9090/arca-cotizador/default  # Config del cotizador"
echo "curl http://localhost:8080/api/hello               # Via Gateway"
echo "curl http://localhost:8761/eureka/apps             # Servicios registrados"
echo ""
echo "📝 Para refrescar configuración en un servicio:"
echo "curl -X POST http://localhost:8081/actuator/refresh"
echo ""

# Wait and check service registration
echo "⏳ Esperando registración de servicios en Eureka..."
sleep 20

echo ""
echo "🔍 Verificando estado de servicios..."

services=("arca-cotizador:8081" "arca-gestor-solicitudes:8082" "hello-world-service:8083" "api-gateway:8080")

for service in "${services[@]}"; do
    IFS=':' read -r service_name port <<< "$service"
    if curl -s "http://localhost:$port/actuator/health" | grep -q "UP" 2>/dev/null; then
        echo "✅ $service_name: Health check OK"
    else
        echo "⚠️  $service_name: Health check failed or service not ready"
    fi
done

echo ""
echo "📖 Configuración centralizada activa. Los servicios obtienen su configuración del Config Server."
