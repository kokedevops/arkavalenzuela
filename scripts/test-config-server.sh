#!/bin/bash

# 🧪 ARKA Config Server - Complete Test Script
# Script para probar el Config Server y sus configuraciones

echo "🧪 Testing ARKA Config Server..."

CONFIG_SERVER_URL="http://localhost:8888"
EUREKA_URL="http://localhost:8761"

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para test con timeout
test_endpoint() {
    local url=$1
    local description=$2
    local expected_status=${3:-200}
    
    echo -n "Testing $description... "
    
    response=$(curl -s -w "%{http_code}" --max-time 10 "$url" 2>/dev/null)
    status_code="${response: -3}"
    
    if [ "$status_code" -eq "$expected_status" ]; then
        echo -e "${GREEN}✅ OK${NC} (Status: $status_code)"
        return 0
    else
        echo -e "${RED}❌ FAIL${NC} (Status: $status_code)"
        return 1
    fi
}

# Función para test con contenido específico
test_content() {
    local url=$1
    local description=$2
    local expected_content=$3
    
    echo -n "Testing $description... "
    
    response=$(curl -s --max-time 10 "$url" 2>/dev/null)
    
    if echo "$response" | grep -q "$expected_content"; then
        echo -e "${GREEN}✅ OK${NC}"
        return 0
    else
        echo -e "${RED}❌ FAIL${NC}"
        echo "Expected: $expected_content"
        echo "Response: $response"
        return 1
    fi
}

echo "🔍 Starting Config Server tests..."
echo "=================================================="

# Test 1: Health Check
test_endpoint "$CONFIG_SERVER_URL/actuator/health" "Health Check"

# Test 2: Application Info
test_endpoint "$CONFIG_SERVER_URL/actuator/info" "Application Info"

# Test 3: Environment
test_endpoint "$CONFIG_SERVER_URL/actuator/env" "Environment"

# Test 4: Configuration Properties
test_endpoint "$CONFIG_SERVER_URL/actuator/configprops" "Configuration Properties"

echo ""
echo "🔧 Testing Configuration Endpoints..."
echo "=================================================="

# Test 5: Default Application Configuration
test_content "$CONFIG_SERVER_URL/application/default" "Default Application Config" "spring"

# Test 6: Eureka Server Configuration
test_content "$CONFIG_SERVER_URL/eureka-server/dev" "Eureka Server Config" "eureka"

# Test 7: API Gateway Configuration
test_content "$CONFIG_SERVER_URL/api-gateway/dev" "API Gateway Config" "gateway"

# Test 8: Config Server own configuration
test_content "$CONFIG_SERVER_URL/config-server/dev" "Config Server Config" "config"

echo ""
echo "🌐 Testing Service Discovery Integration..."
echo "=================================================="

# Test 9: Check if Config Server is registered in Eureka
if curl -s "$EUREKA_URL/eureka/apps" | grep -q "CONFIG-SERVER"; then
    echo -e "${GREEN}✅ Config Server registered in Eureka${NC}"
else
    echo -e "${YELLOW}⚠️  Config Server not found in Eureka${NC}"
fi

echo ""
echo "🧪 Testing Configuration Refresh..."
echo "=================================================="

# Test 10: Refresh endpoint
test_endpoint "$CONFIG_SERVER_URL/actuator/refresh" "Configuration Refresh" 200

echo ""
echo "📊 Test Summary"
echo "=================================================="

# Final health check with detailed info
echo "🏥 Final health check:"
health_response=$(curl -s "$CONFIG_SERVER_URL/actuator/health" 2>/dev/null)
echo "$health_response" | jq . 2>/dev/null || echo "$health_response"

echo ""
echo "📋 Available Configuration Profiles:"
echo "- application (default)"
echo "- eureka-server-dev"
echo "- api-gateway-dev"
echo "- config-server-dev"
echo "- arca-cotizador-dev"
echo "- arca-gestor-solicitudes-dev"
echo "- hello-world-service-dev"

echo ""
echo "🔗 Useful URLs:"
echo "- Health: $CONFIG_SERVER_URL/actuator/health"
echo "- Info: $CONFIG_SERVER_URL/actuator/info"
echo "- Environment: $CONFIG_SERVER_URL/actuator/env"
echo "- Default Config: $CONFIG_SERVER_URL/application/default"
echo "- Service Config: $CONFIG_SERVER_URL/{service-name}/{profile}"

echo ""
echo -e "${GREEN}🎉 Config Server testing completed!${NC}"
        
        # Try to check if service is running
        if curl -s "http://localhost:$service_port/actuator/health" > /dev/null 2>&1; then
            echo "   ℹ️  Servicio está corriendo en puerto $service_port"
        else
            echo "   ⚠️  Servicio no está disponible en puerto $service_port"
        fi
    fi
    echo ""
    echo ""
}

echo "🚀 Probando conexión con el Config Server..."
echo ""

# First, check if Config Server is running
echo "🔍 Verificando disponibilidad del Config Server..."
if curl -s "$CONFIG_SERVER_URL/actuator/health" > /dev/null 2>&1; then
    echo "✅ Config Server está disponible en $CONFIG_SERVER_URL"
else
    echo "❌ Config Server no está disponible en $CONFIG_SERVER_URL"
    echo ""
    echo "📋 Para resolver este problema:"
    echo "1. Asegúrate de que el Config Server esté corriendo"
    echo "2. Verifica que esté en el puerto 9090"
    echo "3. Verifica conectividad de red"
    echo ""
    read -p "¿Continuar con las pruebas de todos modos? (y/N): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Cancelando pruebas..."
        exit 1
    fi
fi
echo ""

# Test each service configuration
test_config "eureka-server" "8761" "$CONFIG_SERVER_URL/eureka-server/dev"
test_config "api-gateway" "8080" "$CONFIG_SERVER_URL/api-gateway/dev"
test_config "arca-cotizador" "8081" "$CONFIG_SERVER_URL/arca-cotizador/dev"
test_config "arca-gestor-solicitudes" "8082" "$CONFIG_SERVER_URL/arca-gestor-solicitudes/dev"
test_config "hello-world-service" "8083" "$CONFIG_SERVER_URL/hello-world-service/dev"

echo "=========================================="
echo "🎉 PRUEBAS COMPLETADAS!"
echo "=========================================="
echo ""
echo "📋 Resumen:"
echo "Si viste las configuraciones JSON arriba, la conexión es exitosa."
echo ""
echo "🔧 Si hay errores, revisa:"
echo "1. 🌐 Conectividad a internet"
echo "2. 🔑 Credenciales del Config Server (config-client:arka-client-2025)"
echo "3. 🏃 Que el Config Server esté ejecutándose en puerto 9090"
echo "4. 🔗 URLs de configuración correctas"
echo ""
echo "🛠️  Comandos útiles para debugging:"
echo ""
echo "# Verificar Config Server:"
echo "curl $CONFIG_SERVER_URL/actuator/health"
echo ""
echo "# Verificar configuraciones manualmente:"
echo "curl -u $CONFIG_USER:$CONFIG_PASS $CONFIG_SERVER_URL/arca-cotizador/dev | jq ."
echo ""
echo "# Refrescar configuración en un servicio:"
echo "curl -X POST http://localhost:8081/actuator/refresh"
echo ""
echo "# Verificar propiedades activas en un servicio:"
echo "curl http://localhost:8081/actuator/configprops | jq ."
