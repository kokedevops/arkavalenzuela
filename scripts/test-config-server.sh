#!/bin/bash

echo "=========================================="
echo "PROBANDO CONEXIÓN CON CONFIG SERVER"
echo "=========================================="
echo ""

# Config Server details
CONFIG_SERVER_URL="http://127.0.0.1:9090"
CONFIG_USER="config-client"
CONFIG_PASS="arka-client-2025"

# Function to test configuration endpoint
test_config() {
    local service_name="$1"
    local service_port="$2"
    local config_endpoint="$3"
    
    echo "🔍 Verificando configuración de $service_name:"
    
    # Try to get configuration
    local response=$(curl -s -u "$CONFIG_USER:$CONFIG_PASS" "$config_endpoint" 2>/dev/null)
    
    if [[ $? -eq 0 && -n "$response" ]]; then
        echo "✅ Configuración obtenida exitosamente"
        echo "$response" | jq . 2>/dev/null || echo "$response"
    else
        echo "❌ Error obteniendo configuración"
        echo "   Endpoint: $config_endpoint"
        
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
