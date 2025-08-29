#!/bin/bash

echo "🔍 VERIFICACIÓN DE REQUISITOS PARA ARKA MICROSERVICES"
echo "===================================================="

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

check_ok() {
    echo -e "${GREEN}✅ $1${NC}"
}

check_error() {
    echo -e "${RED}❌ $1${NC}"
}

check_warning() {
    echo -e "${YELLOW}⚠️ $1${NC}"
}

check_info() {
    echo -e "${BLUE}ℹ️ $1${NC}"
}

# Variables para el resumen
errors=0
warnings=0

echo ""
echo "🐧 Sistema Operativo:"
if [ -f /etc/os-release ]; then
    . /etc/os-release
    check_ok "OS: $PRETTY_NAME"
else
    check_warning "No se pudo determinar el OS"
    warnings=$((warnings + 1))
fi

echo ""
echo "☕ Java:"
if command -v java &> /dev/null; then
    java_version=$(java -version 2>&1 | head -n 1)
    check_ok "Java instalado: $java_version"
    
    # Verificar versión de Java
    java_major=$(java -version 2>&1 | grep -oP 'version "\K\d+' | head -1)
    if [ "$java_major" -ge 17 ]; then
        check_ok "Versión de Java compatible (>= 17)"
    else
        check_error "Versión de Java insuficiente. Se requiere Java 17 o superior"
        errors=$((errors + 1))
    fi
else
    check_error "Java no está instalado"
    errors=$((errors + 1))
fi

echo ""
echo "🐘 PostgreSQL Cliente:"
if command -v psql &> /dev/null; then
    psql_version=$(psql --version)
    check_ok "PostgreSQL cliente: $psql_version"
else
    check_warning "PostgreSQL cliente no instalado (opcional para testing)"
    warnings=$((warnings + 1))
fi

echo ""
echo "🔧 Herramientas del Sistema:"

# Git
if command -v git &> /dev/null; then
    git_version=$(git --version)
    check_ok "Git: $git_version"
else
    check_error "Git no está instalado"
    errors=$((errors + 1))
fi

# curl
if command -v curl &> /dev/null; then
    curl_version=$(curl --version | head -n 1)
    check_ok "curl: $curl_version"
else
    check_error "curl no está instalado"
    errors=$((errors + 1))
fi

# netstat
if command -v netstat &> /dev/null; then
    check_ok "netstat disponible"
elif command -v ss &> /dev/null; then
    check_ok "ss disponible (alternativa a netstat)"
else
    check_warning "netstat/ss no disponible (para verificar puertos)"
    warnings=$((warnings + 1))
fi

echo ""
echo "🔌 Puertos requeridos:"
required_ports=(8761 8080 8082 8083 8084)

for port in "${required_ports[@]}"; do
    if netstat -tuln 2>/dev/null | grep -q ":$port " || ss -tuln 2>/dev/null | grep -q ":$port "; then
        check_warning "Puerto $port está ocupado"
        warnings=$((warnings + 1))
    else
        check_ok "Puerto $port disponible"
    fi
done

echo ""
echo "💾 Espacio en disco:"
disk_usage=$(df -h . | tail -1 | awk '{print $4}')
check_info "Espacio disponible: $disk_usage"

echo ""
echo "🧠 Memoria RAM:"
if command -v free &> /dev/null; then
    ram_total=$(free -h | grep '^Mem:' | awk '{print $2}')
    ram_available=$(free -h | grep '^Mem:' | awk '{print $7}')
    check_info "RAM Total: $ram_total"
    check_info "RAM Disponible: $ram_available"
else
    check_warning "No se pudo verificar la memoria RAM"
    warnings=$((warnings + 1))
fi

echo ""
echo "📁 Archivos del proyecto:"
project_dir="$(dirname "$0")/.."

if [ -f "$project_dir/gradlew" ]; then
    check_ok "gradlew encontrado"
    
    # Verificar permisos
    if [ -x "$project_dir/gradlew" ]; then
        check_ok "gradlew es ejecutable"
    else
        check_warning "gradlew no es ejecutable (se corregirá automáticamente)"
        warnings=$((warnings + 1))
    fi
else
    check_error "gradlew no encontrado en $project_dir"
    errors=$((errors + 1))
fi

if [ -f "$project_dir/settings.gradle" ]; then
    check_ok "settings.gradle encontrado"
else
    check_error "settings.gradle no encontrado"
    errors=$((errors + 1))
fi

# Verificar módulos
modules=("eureka-server" "api-gateway" "arca-cotizador" "arca-gestor-solicitudes" "hello-world-service")
for module in "${modules[@]}"; do
    if [ -d "$project_dir/$module" ]; then
        check_ok "Módulo $module encontrado"
    else
        check_error "Módulo $module no encontrado"
        errors=$((errors + 1))
    fi
done

echo ""
echo "🔐 Variables de entorno:"
if [ -z "$JAVA_HOME" ]; then
    check_warning "JAVA_HOME no está definido"
    warnings=$((warnings + 1))
else
    check_ok "JAVA_HOME: $JAVA_HOME"
fi

echo ""
echo "📊 RESUMEN:"
echo "==========="

if [ $errors -eq 0 ] && [ $warnings -eq 0 ]; then
    check_ok "¡Todos los requisitos están satisfechos! Listo para iniciar."
    echo ""
    echo "🚀 Para iniciar los servicios ejecuta:"
    echo "   bash start-all-services-ubuntu.sh"
elif [ $errors -eq 0 ]; then
    check_warning "Requisitos básicos OK, pero hay $warnings advertencias."
    echo ""
    echo "🚀 Puedes intentar iniciar los servicios:"
    echo "   bash start-all-services-ubuntu.sh"
else
    check_error "Se encontraron $errors errores críticos y $warnings advertencias."
    echo ""
    echo "🔧 Soluciones sugeridas:"
    
    if ! command -v java &> /dev/null; then
        echo "   • Instalar Java 17+: sudo apt update && sudo apt install openjdk-17-jdk"
    fi
    
    if ! command -v git &> /dev/null; then
        echo "   • Instalar Git: sudo apt install git"
    fi
    
    if ! command -v curl &> /dev/null; then
        echo "   • Instalar curl: sudo apt install curl"
    fi
    
    if [ ! -x "$project_dir/gradlew" ]; then
        echo "   • Hacer gradlew ejecutable: chmod +x gradlew"
    fi
fi

echo ""
echo "💡 Comandos útiles:"
echo "   • Ver procesos Java: jps -v"
echo "   • Ver puertos ocupados: netstat -tuln | grep LISTEN"
echo "   • Verificar conectividad: curl -f http://localhost:8761/actuator/health"

exit $errors
