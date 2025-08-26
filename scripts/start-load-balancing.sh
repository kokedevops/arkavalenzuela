#!/bin/bash

echo "Starting additional instances for load balancing..."

# Function to start load balancing instance
start_lb_instance() {
    local service_name="$1"
    local gradle_task="$2"
    local port="$3"
    local instance_id="$4"
    
    echo ""
    echo "=========================================="
    echo "Starting $service_name (Port $port)..."
    echo "=========================================="
    
    cd "$(dirname "$0")/.."
    
    # Create terminal with specific instance configuration
    gnome-terminal --title="$service_name" --tab -- bash -c "./gradlew $gradle_task --args='--server.port=$port --eureka.instance.instance-id=$instance_id'; exec bash" 2>/dev/null || \
    xterm -title "$service_name" -e "bash -c './gradlew $gradle_task --args=\"--server.port=$port --eureka.instance.instance-id=$instance_id\"; exec bash'" 2>/dev/null || \
    konsole --title "$service_name" -e bash -c "./gradlew $gradle_task --args='--server.port=$port --eureka.instance.instance-id=$instance_id'; exec bash" 2>/dev/null || \
    {
        echo "No suitable terminal found. Starting in background..."
        nohup ./gradlew $gradle_task --args="--server.port=$port --eureka.instance.instance-id=$instance_id" > logs/${service_name,,}-instance2.log 2>&1 &
        echo "Logs available at: logs/${service_name,,}-instance2.log"
    }
}

# Create logs directory if it doesn't exist
mkdir -p logs

echo ""
echo "⚖️ ARKA Load Balancing Setup Script for Ubuntu/Linux"
echo "===================================================="
echo ""
echo "🔍 Prerequisites:"
echo "   - Eureka Server must be running (port 8761)"
echo "   - API Gateway must be running (port 8080)"
echo "   - First instances should already be running"
echo ""

# Check if Eureka is running
if ! curl -s http://localhost:8761/eureka/apps > /dev/null 2>&1; then
    echo "❌ ERROR: Eureka Server not accessible at http://localhost:8761"
    echo "   Please start Eureka Server first using start-all-services.sh"
    exit 1
fi

echo "✅ Eureka Server is accessible"
echo ""

# Start second instances
start_lb_instance "Arca Cotizador Instance 2" ":arca-cotizador:bootRun" "8091" "arca-cotizador:8091"
sleep 5

start_lb_instance "Arca Gestor Solicitudes Instance 2" ":arca-gestor-solicitudes:bootRun" "8092" "arca-gestor-solicitudes:8092"
sleep 5

start_lb_instance "Hello World Service Instance 2" ":hello-world-service:bootRun" "8084" "hello-world-service:8084"
sleep 5

echo ""
echo "=========================================="
echo "🎯 Load balancing instances started!"
echo ""
echo "📊 Service Instances:"
echo "Arca Cotizador:"
echo "  - Instance 1: http://localhost:8081"
echo "  - Instance 2: http://localhost:8091"
echo ""
echo "Arca Gestor Solicitudes:"
echo "  - Instance 1: http://localhost:8082"
echo "  - Instance 2: http://localhost:8092"
echo ""
echo "Hello World Service:"
echo "  - Instance 1: http://localhost:8083"
echo "  - Instance 2: http://localhost:8084"
echo ""
echo "🧪 Test load balancing with:"
echo "for i in {1..10}; do curl http://localhost:8080/api/hello; echo; done"
echo "for i in {1..10}; do curl http://localhost:8080/api/cotizador/health; echo; done"
echo "for i in {1..10}; do curl http://localhost:8080/api/gestor/health; echo; done"
echo ""
echo "🔍 Monitor Eureka Dashboard: http://localhost:8761"
echo "📝 Check logs in the 'logs/' directory"
echo "=========================================="

# Wait and check Eureka registration
echo ""
echo "⏳ Waiting for services to register with Eureka..."
sleep 30

echo ""
echo "🔍 Checking Eureka registrations..."
if curl -s http://localhost:8761/eureka/apps | grep -q "ARCA-COTIZADOR"; then
    echo "✅ Arca Cotizador instances registered"
else
    echo "⚠️  Arca Cotizador instances not yet registered"
fi

if curl -s http://localhost:8761/eureka/apps | grep -q "ARCA-GESTOR-SOLICITUDES"; then
    echo "✅ Arca Gestor Solicitudes instances registered"
else
    echo "⚠️  Arca Gestor Solicitudes instances not yet registered"
fi

if curl -s http://localhost:8761/eureka/apps | grep -q "HELLO-WORLD-SERVICE"; then
    echo "✅ Hello World Service instances registered"
else
    echo "⚠️  Hello World Service instances not yet registered"
fi

echo ""
echo "📖 Load balancing setup complete! Check Eureka Dashboard for all registered instances."
