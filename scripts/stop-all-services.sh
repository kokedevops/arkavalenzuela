#!/bin/bash

echo "🛑 Deteniendo todos los servicios ARKA..."
echo "========================================"
echo ""

# Function to stop services gracefully
stop_service() {
    local service_name="$1"
    local search_pattern="$2"
    
    echo "🔍 Buscando proceso: $service_name"
    
    local pids=$(pgrep -f "$search_pattern")
    
    if [[ -n "$pids" ]]; then
        echo "📦 Deteniendo $service_name (PIDs: $pids)"
        
        # Try graceful shutdown first
        kill $pids 2>/dev/null
        
        # Wait a few seconds
        sleep 3
        
        # Check if still running
        local remaining_pids=$(pgrep -f "$search_pattern")
        if [[ -n "$remaining_pids" ]]; then
            echo "⚡ Forzando cierre de $service_name (PIDs: $remaining_pids)"
            kill -9 $remaining_pids 2>/dev/null
        fi
        
        echo "✅ $service_name detenido"
    else
        echo "ℹ️  $service_name no está corriendo"
    fi
    echo ""
}

# Stop all ARKA services
stop_service "Eureka Server" "eureka-server"
stop_service "API Gateway" "api-gateway"
stop_service "Arca Cotizador" "arca-cotizador"
stop_service "Arca Gestor Solicitudes" "arca-gestor-solicitudes"
stop_service "Hello World Service" "hello-world-service"

# Stop any remaining gradle processes
echo "🔍 Buscando procesos Gradle restantes..."
remaining_gradle=$(pgrep -f "gradle.*bootRun")
if [[ -n "$remaining_gradle" ]]; then
    echo "⚡ Deteniendo procesos Gradle restantes (PIDs: $remaining_gradle)"
    kill $remaining_gradle 2>/dev/null
    sleep 2
    kill -9 $remaining_gradle 2>/dev/null
    echo "✅ Procesos Gradle detenidos"
else
    echo "ℹ️  No hay procesos Gradle corriendo"
fi

echo ""
echo "🔍 Verificando puertos..."

# Check if ports are still in use
check_port() {
    local port="$1"
    local service="$2"
    
    if netstat -tuln 2>/dev/null | grep -q ":$port " || ss -tuln 2>/dev/null | grep -q ":$port "; then
        echo "⚠️  Puerto $port ($service) aún en uso"
        
        # Try to find and kill the process using the port
        local pid=$(lsof -ti:$port 2>/dev/null || fuser $port/tcp 2>/dev/null | awk '{print $1}')
        if [[ -n "$pid" ]]; then
            echo "   Liberando puerto $port (PID: $pid)"
            kill -9 $pid 2>/dev/null
        fi
    else
        echo "✅ Puerto $port ($service) libre"
    fi
}

check_port "8761" "Eureka Server"
check_port "8080" "API Gateway"
check_port "8081" "Arca Cotizador"
check_port "8082" "Arca Gestor Solicitudes"
check_port "8083" "Hello World Service"
check_port "8084" "Hello World Service Instance 2"
check_port "8091" "Arca Cotizador Instance 2"
check_port "8092" "Arca Gestor Solicitudes Instance 2"

echo ""
echo "🧹 Limpiando archivos temporales..."

# Clean up log files if they exist
if [[ -d "logs" ]]; then
    echo "📝 Archivando logs antiguos..."
    timestamp=$(date +"%Y%m%d_%H%M%S")
    mkdir -p "logs/archive"
    if ls logs/*.log 1> /dev/null 2>&1; then
        mv logs/*.log "logs/archive/logs_$timestamp/" 2>/dev/null || true
    fi
    echo "✅ Logs archivados en logs/archive/logs_$timestamp/"
fi

# Clean up nohup files
if ls nohup.out 1> /dev/null 2>&1; then
    rm -f nohup.out
    echo "✅ Archivos nohup.out eliminados"
fi

echo ""
echo "========================================"
echo "🎉 Todos los servicios han sido detenidos"
echo "========================================"
echo ""
echo "📊 Resumen:"
echo "✅ Procesos ARKA detenidos"
echo "✅ Puertos liberados"
echo "✅ Archivos temporales limpiados"
echo ""
echo "🔄 Para volver a iniciar:"
echo "./start-all-services.sh"
echo ""
echo "🐳 Para parar Docker (si está corriendo):"
echo "docker-compose down"
