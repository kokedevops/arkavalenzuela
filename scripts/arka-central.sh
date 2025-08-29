#!/bin/bash

# 🚀 Script para iniciar ARKA Central Application
# ===============================================

SCRIPT_DIR="$(dirname "$0")"
PROJECT_DIR="$SCRIPT_DIR/.."
LOGS_DIR="$PROJECT_DIR/logs"
PID_FILE="$PROJECT_DIR/pids/arka-central.pid"

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

show_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

show_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

show_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

show_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Crear directorios necesarios
mkdir -p "$LOGS_DIR"
mkdir -p "$(dirname "$PID_FILE")"

# Función para detener el servicio
stop_arka_central() {
    show_status "Deteniendo ARKA Central..."
    
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if ps -p $pid > /dev/null 2>&1; then
            show_status "Deteniendo proceso con PID $pid..."
            kill $pid
            sleep 3
            
            # Force kill si es necesario
            if ps -p $pid > /dev/null 2>&1; then
                show_warning "Forzando detención..."
                kill -9 $pid
            fi
            
            show_success "ARKA Central detenido"
        else
            show_warning "Proceso no encontrado"
        fi
        rm -f "$PID_FILE"
    else
        show_warning "PID file no encontrado"
    fi
    
    # Limpiar procesos gradle restantes
    pkill -f "gradle.*bootRun" 2>/dev/null || true
}

# Función para verificar estado
check_status() {
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if ps -p $pid > /dev/null 2>&1; then
            if netstat -tuln | grep -q ":8888 "; then
                show_success "ARKA Central: ✅ Corriendo (PID: $pid, Puerto: 8888)"
                echo ""
                echo "🔗 URLs disponibles:"
                echo "├── 🏠 Aplicación Principal:  http://$(hostname -I | awk '{print $1}'):8888"
                echo "├── ❤️ Health Check:          http://$(hostname -I | awk '{print $1}'):8888/actuator/health"
                echo "├── 🛒 API E-commerce:        http://$(hostname -I | awk '{print $1}'):8888/productos"
                echo "├── 🔐 API Auth:              http://$(hostname -I | awk '{print $1}'):8888/api/auth/login"
                echo "└── 🌐 API Terceros:          http://$(hostname -I | awk '{print $1}'):8888/api/terceros/ObtenerDatos/productos"
                return 0
            else
                show_warning "ARKA Central: ⚠️ Proceso activo pero puerto 8888 no disponible"
                return 1
            fi
        else
            show_error "ARKA Central: ❌ Proceso no encontrado"
            rm -f "$PID_FILE"
            return 1
        fi
    else
        show_error "ARKA Central: ❌ No está corriendo"
        return 1
    fi
}

# Función para construir JAR
build_jar() {
    show_status "Construyendo JAR de ARKA Central..."
    cd "$PROJECT_DIR"
    
    ./gradlew clean build -x test
    
    if [ $? -eq 0 ]; then
        show_success "JAR construido exitosamente"
        ls -la build/libs/arkajvalenzuela-*.jar
    else
        show_error "Error construyendo JAR"
        exit 1
    fi
}

# Función para construir WAR
build_war() {
    show_status "Construyendo WAR de ARKA Central..."
    cd "$PROJECT_DIR"
    
    ./gradlew clean war
    
    if [ $? -eq 0 ]; then
        show_success "WAR construido exitosamente"
        ls -la build/libs/arkajvalenzuela-*.war
        echo ""
        echo "💡 Para desplegar en Payara:"
        echo "   asadmin deploy build/libs/arkajvalenzuela-*.war"
    else
        show_error "Error construyendo WAR"
        exit 1
    fi
}

# Función para iniciar con Gradle
start_with_gradle() {
    local profile="${1:-aws}"
    local port="${2:-8888}"
    
    show_status "Iniciando ARKA Central con Gradle (Profile: $profile, Puerto: $port)..."
    
    # Verificar puerto
    if netstat -tuln | grep -q ":$port "; then
        show_error "Puerto $port ya está en uso"
        exit 1
    fi
    
    cd "$PROJECT_DIR"
    
    # Iniciar en background
    nohup ./gradlew bootRun --args="--spring.profiles.active=$profile --server.port=$port" > "$LOGS_DIR/arka-central.log" 2>&1 &
    local pid=$!
    
    echo $pid > "$PID_FILE"
    
    show_success "ARKA Central iniciado con PID $pid"
    show_status "Logs: $LOGS_DIR/arka-central.log"
    
    # Esperar un poco y verificar
    show_status "Esperando que el servicio esté listo..."
    sleep 30
    
    check_status
}

# Función para iniciar con JAR
start_with_jar() {
    local profile="${1:-aws}"
    local port="${2:-8888}"
    
    show_status "Iniciando ARKA Central con JAR (Profile: $profile, Puerto: $port)..."
    
    cd "$PROJECT_DIR"
    
    # Buscar JAR
    local jar_file=$(ls build/libs/arkajvalenzuela-*.jar 2>/dev/null | head -1)
    
    if [ ! -f "$jar_file" ]; then
        show_warning "JAR no encontrado. Construyendo..."
        build_jar
        jar_file=$(ls build/libs/arkajvalenzuela-*.jar | head -1)
    fi
    
    # Verificar puerto
    if netstat -tuln | grep -q ":$port "; then
        show_error "Puerto $port ya está en uso"
        exit 1
    fi
    
    # Iniciar en background
    nohup java -Xmx2g -Xms1g -jar "$jar_file" --spring.profiles.active=$profile --server.port=$port > "$LOGS_DIR/arka-central.log" 2>&1 &
    local pid=$!
    
    echo $pid > "$PID_FILE"
    
    show_success "ARKA Central iniciado con PID $pid"
    show_status "JAR: $jar_file"
    show_status "Logs: $LOGS_DIR/arka-central.log"
    
    # Esperar un poco y verificar
    show_status "Esperando que el servicio esté listo..."
    sleep 30
    
    check_status
}

# Función para mostrar logs
show_logs() {
    if [ -f "$LOGS_DIR/arka-central.log" ]; then
        echo "📝 Logs de ARKA Central:"
        echo "========================"
        tail -n 50 "$LOGS_DIR/arka-central.log"
    else
        show_error "No se encontraron logs"
    fi
}

# Función de ayuda
show_usage() {
    echo "🚀 ARKA Central Manager"
    echo "======================="
    echo ""
    echo "Uso: $0 [comando] [opciones]"
    echo ""
    echo "📋 Comandos:"
    echo "  start-gradle [profile] [port]  - Iniciar con Gradle (default: aws, 8888)"
    echo "  start-jar [profile] [port]     - Iniciar con JAR (default: aws, 8888)"
    echo "  stop                           - Detener ARKA Central"
    echo "  status                         - Ver estado"
    echo "  logs                           - Ver logs"
    echo "  build-jar                      - Construir JAR"
    echo "  build-war                      - Construir WAR"
    echo "  restart-gradle [profile]       - Reiniciar con Gradle"
    echo "  restart-jar [profile]          - Reiniciar con JAR"
    echo ""
    echo "📝 Ejemplos:"
    echo "  $0 start-gradle                # Iniciar con Gradle (perfil aws)"
    echo "  $0 start-gradle dev 8889       # Iniciar con perfil dev en puerto 8889"
    echo "  $0 start-jar aws               # Iniciar con JAR (perfil aws)"
    echo "  $0 build-war                   # Construir WAR para Payara"
    echo ""
}

# Función principal
main() {
    local cmd="${1:-help}"
    
    case $cmd in
        "start-gradle"|"start"|"gradle")
            start_with_gradle "$2" "$3"
            ;;
        "start-jar"|"jar")
            start_with_jar "$2" "$3"
            ;;
        "stop"|"down")
            stop_arka_central
            ;;
        "status"|"ps")
            check_status
            ;;
        "logs"|"log")
            show_logs
            ;;
        "build-jar"|"jar-build")
            build_jar
            ;;
        "build-war"|"war-build")
            build_war
            ;;
        "restart-gradle"|"restart")
            stop_arka_central
            sleep 3
            start_with_gradle "$2" "$3"
            ;;
        "restart-jar")
            stop_arka_central
            sleep 3
            start_with_jar "$2" "$3"
            ;;
        "help"|"-h"|"--help"|*)
            show_usage
            ;;
    esac
}

# Ejecutar
main "$@"
