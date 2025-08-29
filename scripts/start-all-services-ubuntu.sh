#!/bin/bash

echo "🚀 ARKA Microservices Startup Script for Ubuntu Server"
echo "======================================================"

# Variables de configuración
PROJECT_DIR="$(dirname "$0")/.."
LOGS_DIR="$PROJECT_DIR/logs"
PID_DIR="$PROJECT_DIR/pids"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para mostrar estado
show_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

show_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

show_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

show_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Crear directorios necesarios
mkdir -p "$LOGS_DIR"
mkdir -p "$PID_DIR"

# Función para iniciar un servicio
start_service() {
    local service_name="$1"
    local gradle_task="$2"
    local port="$3"
    local wait_time="${4:-10}"
    
    local log_filename=$(echo "$service_name" | tr '[:upper:]' '[:lower:]' | tr ' ' '-')
    local pid_file="$PID_DIR/$log_filename.pid"
    local log_file="$LOGS_DIR/$log_filename.log"
    
    show_status "Iniciando $service_name en puerto $port..."
    
    # Verificar si el puerto está ocupado
    if netstat -tuln | grep -q ":$port "; then
        show_warning "Puerto $port ya está en uso. Continuando..."
    fi
    
    # Cambiar al directorio del proyecto
    cd "$PROJECT_DIR"
    
    # Iniciar el servicio en background
    show_status "Ejecutando: ./gradlew $gradle_task"
    nohup ./gradlew $gradle_task > "$log_file" 2>&1 &
    local pid=$!
    
    # Guardar PID
    echo $pid > "$pid_file"
    
    show_success "$service_name iniciado con PID $pid"
    show_status "Logs: $log_file"
    
    # Esperar un poco antes del siguiente servicio
    show_status "Esperando $wait_time segundos antes del siguiente servicio..."
    sleep $wait_time
}

# Función para verificar si un servicio está corriendo
check_service() {
    local service_name="$1"
    local port="$2"
    local log_filename=$(echo "$service_name" | tr '[:upper:]' '[:lower:]' | tr ' ' '-')
    local pid_file="$PID_DIR/$log_filename.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            if netstat -tuln | grep -q ":$port "; then
                show_success "$service_name: ✅ Corriendo (PID: $pid, Puerto: $port)"
                return 0
            else
                show_warning "$service_name: ⚠️ Proceso activo pero puerto no disponible"
                return 1
            fi
        else
            show_error "$service_name: ❌ Proceso no encontrado"
            rm -f "$pid_file"
            return 1
        fi
    else
        show_error "$service_name: ❌ PID file no encontrado"
        return 1
    fi
}

# Función para detener todos los servicios
stop_all_services() {
    show_status "Deteniendo todos los servicios..."
    
    for pid_file in "$PID_DIR"/*.pid; do
        if [ -f "$pid_file" ]; then
            local pid=$(cat "$pid_file")
            local service=$(basename "$pid_file" .pid)
            
            if ps -p $pid > /dev/null 2>&1; then
                show_status "Deteniendo $service (PID: $pid)..."
                kill $pid
                sleep 2
                
                # Force kill si es necesario
                if ps -p $pid > /dev/null 2>&1; then
                    show_warning "Forzando detención de $service..."
                    kill -9 $pid
                fi
                
                show_success "$service detenido"
            fi
            
            rm -f "$pid_file"
        fi
    done
    
    # Limpiar procesos gradle restantes
    pkill -f "gradle.*bootRun" 2>/dev/null || true
    show_success "Todos los servicios detenidos"
}

# Verificar argumentos
if [ "$1" = "stop" ]; then
    stop_all_services
    exit 0
elif [ "$1" = "status" ]; then
    echo "📊 Estado de los servicios:"
    check_service "Eureka Server" "8761"
    check_service "API Gateway" "8080"
    check_service "Hello World Service" "8084"
    check_service "Arca Cotizador" "8082"
    check_service "Arca Gestor Solicitudes" "8083"
    exit 0
fi

# Verificar que gradlew existe
if [ ! -f "$PROJECT_DIR/gradlew" ]; then
    show_error "gradlew no encontrado en $PROJECT_DIR"
    exit 1
fi

# Hacer gradlew ejecutable
chmod +x "$PROJECT_DIR/gradlew"

show_status "Iniciando todos los microservicios..."
show_status "Directorio del proyecto: $PROJECT_DIR"
show_status "Logs se guardarán en: $LOGS_DIR"

# Detener servicios anteriores si existen
if [ -d "$PID_DIR" ] && [ "$(ls -A $PID_DIR 2>/dev/null)" ]; then
    show_warning "Se detectaron servicios anteriores. Deteniéndolos..."
    stop_all_services
    sleep 5
fi

echo ""
show_status "🎬 Iniciando servicios en orden..."

# 1. Eureka Server (Service Discovery) - CRÍTICO PRIMERO
start_service "Eureka Server" ":eureka-server:bootRun" "8761" 45

# 2. API Gateway - Después de Eureka
start_service "API Gateway" ":api-gateway:bootRun" "8080" 30

# 3. Hello World Service - Servicio simple
start_service "Hello World Service" ":hello-world-service:bootRun" "8084" 20

# 4. Arca Cotizador - Microservicio de negocio
start_service "Arca Cotizador" ":arca-cotizador:bootRun" "8082" 25

# 5. Arca Gestor Solicitudes - Microservicio de negocio
start_service "Arca Gestor Solicitudes" ":arca-gestor-solicitudes:bootRun" "8083" 25

echo ""
show_status "⏳ Esperando estabilización de servicios..."
sleep 20

echo ""
show_success "🎉 Proceso de inicio completado!"
echo ""
echo "📊 Verificando estado de servicios:"
check_service "Eureka Server" "8761"
check_service "API Gateway" "8080"
check_service "Hello World Service" "8084"
check_service "Arca Cotizador" "8082"
check_service "Arca Gestor Solicitudes" "8083"

echo ""
echo "🔗 URLs de los servicios:"
echo "├── 🔍 Eureka Dashboard:     http://$(hostname -I | awk '{print $1}'):8761"
echo "├── 🌐 API Gateway:          http://$(hostname -I | awk '{print $1}'):8080"
echo "├── 👋 Hello World:          http://$(hostname -I | awk '{print $1}'):8084/hello"
echo "├── 💰 Cotizador:            http://$(hostname -I | awk '{print $1}'):8082/actuator/health"
echo "└── 📋 Gestor Solicitudes:   http://$(hostname -I | awk '{print $1}'):8083/actuator/health"

echo ""
echo "📖 Comandos útiles:"
echo "├── Ver estado:      $0 status"
echo "├── Detener todo:    $0 stop"
echo "├── Ver logs:        tail -f $LOGS_DIR/[servicio].log"
echo "└── Ver procesos:    ps aux | grep gradle"

echo ""
show_status "✅ Script completado. Los servicios están iniciándose en background."
