#!/bin/bash

echo "Starting all microservices..."

# Function to start a service in background
start_service() {
    local service_name="$1"
    local gradle_task="$2"
    local port="$3"
    
    echo ""
    echo "=========================================="
    echo "Starting $service_name (Port $port)..."
    echo "=========================================="
    
    # Convert service name to lowercase for log filename
    local log_filename=$(echo "$service_name" | tr '[:upper:]' '[:lower:]' | tr ' ' '-')
    
    # Start service in background with logs
    cd "$(dirname "$0")/.."
    gnome-terminal --title="$service_name" --tab -- bash -c "./gradlew $gradle_task; exec bash" 2>/dev/null || \
    xterm -title "$service_name" -e "bash -c './gradlew $gradle_task; exec bash'" 2>/dev/null || \
    konsole --title "$service_name" -e bash -c "./gradlew $gradle_task; exec bash" 2>/dev/null || \
    {
        echo "No suitable terminal found. Starting in background..."
        nohup ./gradlew $gradle_task > "logs/$log_filename.log" 2>&1 &
        echo "Logs available at: logs/$log_filename.log"
    }
}

# Create logs directory if it doesn't exist
mkdir -p logs

echo ""
echo "🚀 ARKA Microservices Startup Script for Ubuntu/Linux"
echo "======================================================"

# Start Eureka Server first
start_service "Eureka Server" ":eureka-server:bootRun" "8761"
echo "Waiting for Eureka Server to start..."
sleep 30

# Start API Gateway
start_service "API Gateway" ":api-gateway:bootRun" "8080"
echo "Waiting for API Gateway to start..."
sleep 15

# Start Hello World Service
start_service "Hello World Service" ":hello-world-service:bootRun" "8083"
sleep 5

# Start Arca Cotizador
start_service "Arca Cotizador" ":arca-cotizador:bootRun" "8081"
sleep 5

# Start Arca Gestor Solicitudes
start_service "Arca Gestor Solicitudes" ":arca-gestor-solicitudes:bootRun" "8082"
sleep 5

echo ""
echo "=========================================="
echo "🎉 All services are starting..."
echo ""
echo "📊 Service URLs:"
echo "Eureka Dashboard: http://localhost:8761"
echo "API Gateway: http://localhost:8080"
echo "Hello World: http://localhost:8080/api/hello"
echo "Cotizador: http://localhost:8080/api/cotizador"
echo "Gestor: http://localhost:8080/api/gestor"
echo ""
echo "📝 Check logs in the 'logs/' directory"
echo "🔍 Monitor services: ps aux | grep gradle"
echo "🛑 Stop services: pkill -f gradle"
echo "=========================================="

# Wait a bit more and check if services are running
sleep 10
echo ""
echo "🔍 Checking running services..."
if pgrep -f "eureka-server" > /dev/null; then
    echo "✅ Eureka Server: Running"
else
    echo "❌ Eureka Server: Not detected"
fi

if pgrep -f "api-gateway" > /dev/null; then
    echo "✅ API Gateway: Running"
else
    echo "❌ API Gateway: Not detected"
fi

if pgrep -f "hello-world-service" > /dev/null; then
    echo "✅ Hello World Service: Running"
else
    echo "❌ Hello World Service: Not detected"
fi

if pgrep -f "arca-cotizador" > /dev/null; then
    echo "✅ Arca Cotizador: Running"
else
    echo "❌ Arca Cotizador: Not detected"
fi

if pgrep -f "arca-gestor-solicitudes" > /dev/null; then
    echo "✅ Arca Gestor Solicitudes: Running"
else
    echo "❌ Arca Gestor Solicitudes: Not detected"
fi

echo ""
echo "📖 For troubleshooting, check individual service logs in terminal tabs or logs/ directory"
