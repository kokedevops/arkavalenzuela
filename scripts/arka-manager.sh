#!/bin/bash

# 🚀 ARKA Microservices Manager - Script Maestro
# ===============================================

SCRIPT_DIR="$(dirname "$0")"
PROJECT_DIR="$SCRIPT_DIR/.."

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

show_banner() {
    echo -e "${CYAN}"
    echo "╔══════════════════════════════════════════════════════════╗"
    echo "║                 🚀 ARKA MICROSERVICES MANAGER           ║"
    echo "║                                                          ║"
    echo "║  Gestión completa de microservicios ARKA E-commerce     ║"
    echo "╚══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

show_usage() {
    echo -e "${YELLOW}Uso: $0 [comando]${NC}"
    echo ""
    echo "📋 Comandos disponibles:"
    echo ""
    echo -e "${GREEN}🔍 check${NC}     - Verificar requisitos del sistema"
    echo -e "${GREEN}🚀 start${NC}     - Iniciar todos los microservicios"
    echo -e "${GREEN}🛑 stop${NC}      - Detener todos los microservicios"
    echo -e "${GREEN}📊 status${NC}    - Ver estado de los servicios"
    echo -e "${GREEN}📝 logs${NC}      - Ver logs de los servicios"
    echo -e "${GREEN}🔄 restart${NC}   - Reiniciar todos los servicios"
    echo -e "${GREEN}🧹 clean${NC}     - Limpiar logs y archivos temporales"
    echo -e "${GREEN}🔧 setup${NC}     - Configuración inicial del proyecto"
    echo -e "${GREEN}❓ help${NC}      - Mostrar esta ayuda"
    echo ""
    echo "📝 Ejemplos:"
    echo "   $0 check     # Verificar si el sistema está listo"
    echo "   $0 start     # Iniciar todos los servicios"
    echo "   $0 status    # Ver estado actual"
    echo "   $0 logs      # Ver logs recientes"
    echo ""
}

make_scripts_executable() {
    chmod +x "$SCRIPT_DIR"/*.sh 2>/dev/null || true
    chmod +x "$PROJECT_DIR/gradlew" 2>/dev/null || true
}

run_command() {
    local cmd="$1"
    
    case $cmd in
        "check"|"requirements")
            echo -e "${BLUE}🔍 Verificando requisitos del sistema...${NC}"
            bash "$SCRIPT_DIR/check-requirements.sh"
            ;;
        "start"|"up")
            echo -e "${GREEN}🚀 Iniciando todos los microservicios...${NC}"
            bash "$SCRIPT_DIR/start-all-services-ubuntu.sh"
            ;;
        "stop"|"down")
            echo -e "${RED}🛑 Deteniendo todos los microservicios...${NC}"
            bash "$SCRIPT_DIR/start-all-services-ubuntu.sh" stop
            ;;
        "status"|"ps")
            echo -e "${CYAN}📊 Estado de los microservicios...${NC}"
            bash "$SCRIPT_DIR/start-all-services-ubuntu.sh" status
            ;;
        "restart"|"reboot")
            echo -e "${YELLOW}🔄 Reiniciando todos los microservicios...${NC}"
            bash "$SCRIPT_DIR/start-all-services-ubuntu.sh" stop
            sleep 5
            bash "$SCRIPT_DIR/start-all-services-ubuntu.sh"
            ;;
        "logs"|"log")
            echo -e "${BLUE}📝 Mostrando logs recientes...${NC}"
            if [ -d "$PROJECT_DIR/logs" ]; then
                echo "Logs disponibles:"
                ls -la "$PROJECT_DIR/logs/"
                echo ""
                echo "Para ver un log específico usa: tail -f logs/[servicio].log"
                echo ""
                echo "Logs recientes de todos los servicios:"
                for log_file in "$PROJECT_DIR/logs"/*.log; do
                    if [ -f "$log_file" ]; then
                        echo ""
                        echo "=== $(basename "$log_file") ==="
                        tail -n 10 "$log_file"
                    fi
                done
            else
                echo "No se encontraron logs. Los servicios no han sido iniciados."
            fi
            ;;
        "clean"|"cleanup")
            echo -e "${YELLOW}🧹 Limpiando archivos temporales...${NC}"
            rm -rf "$PROJECT_DIR/logs"/*.log 2>/dev/null || true
            rm -rf "$PROJECT_DIR/pids"/*.pid 2>/dev/null || true
            rm -rf "$PROJECT_DIR/build" 2>/dev/null || true
            find "$PROJECT_DIR" -name "*.log" -type f -delete 2>/dev/null || true
            echo "✅ Limpieza completada"
            ;;
        "setup"|"install")
            echo -e "${CYAN}🔧 Configuración inicial del proyecto...${NC}"
            make_scripts_executable
            
            echo "📦 Descargando dependencias de Gradle..."
            cd "$PROJECT_DIR"
            ./gradlew build -x test --no-daemon
            
            echo "✅ Configuración completada"
            ;;
        "help"|"-h"|"--help")
            show_usage
            ;;
        "")
            show_banner
            show_usage
            ;;
        *)
            echo -e "${RED}❌ Comando desconocido: $cmd${NC}"
            echo ""
            show_usage
            exit 1
            ;;
    esac
}

# Función principal
main() {
    # Hacer scripts ejecutables
    make_scripts_executable
    
    # Mostrar banner si no hay argumentos
    if [ $# -eq 0 ]; then
        show_banner
        show_usage
        exit 0
    fi
    
    # Procesar comando
    run_command "$1"
}

# Ejecutar función principal
main "$@"
