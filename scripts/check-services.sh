#!/bin/bash

echo "🔍 ARKA - Verificación de Estado de Servicios"
echo "============================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to check service health
check_service() {
    local service_name="$1"
    local port="$2"
    local health_endpoint="$3"
    local expected_process="$4"
    
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}🔍 Verificando: $service_name${NC}"
    echo ""
    
    # Check if process is running
    if pgrep -f "$expected_process" > /dev/null; then
        echo -e "✅ Proceso: ${GREEN}Corriendo${NC}"
        local pid=$(pgrep -f "$expected_process" | head -1)
        echo "   PID: $pid"
    else
        echo -e "❌ Proceso: ${RED}No está corriendo${NC}"
        echo ""
        return 1
    fi
    
    # Check if port is listening
    if netstat -tuln 2>/dev/null | grep -q ":$port " || ss -tuln 2>/dev/null | grep -q ":$port "; then
        echo -e "✅ Puerto $port: ${GREEN}Abierto${NC}"
    else
        echo -e "❌ Puerto $port: ${RED}Cerrado${NC}"
        echo ""
        return 1
    fi
    
    # Check HTTP health endpoint
    if [[ -n "$health_endpoint" ]]; then
        local response=$(curl -s -w "%{http_code}" -o /tmp/health_check "$health_endpoint" 2>/dev/null)
        
        if [[ "$response" == "200" ]]; then
            echo -e "✅ Health Check: ${GREEN}OK${NC}"
            
            # Try to parse health response
            if command -v jq >/dev/null 2>&1; then
                local status=$(cat /tmp/health_check | jq -r '.status // "UNKNOWN"' 2>/dev/null)
                if [[ "$status" == "UP" ]]; then
                    echo -e "   Estado: ${GREEN}UP${NC}"
                else
                    echo -e "   Estado: ${YELLOW}$status${NC}"
                fi
            fi
        else
            echo -e "❌ Health Check: ${RED}Failed (HTTP $response)${NC}"
            echo ""
            return 1
        fi
    fi
    
    echo ""
    return 0
}

# Function to check service registration in Eureka
check_eureka_registration() {
    local service_name="$1"
    
    echo -e "${BLUE}🔍 Verificando registro en Eureka: $service_name${NC}"
    
    if curl -s http://localhost:8761/eureka/apps 2>/dev/null | grep -q "$service_name"; then
        echo -e "✅ Registro Eureka: ${GREEN}Registrado${NC}"
        
        # Get instance count
        local instances=$(curl -s http://localhost:8761/eureka/apps 2>/dev/null | grep -o "$service_name" | wc -l)
        echo "   Instancias: $instances"
    else
        echo -e "❌ Registro Eureka: ${RED}No registrado${NC}"
    fi
    echo ""
}

# Create logs directory if it doesn't exist
mkdir -p logs

echo "⏰ Iniciando verificación de servicios... $(date)"
echo ""

# Check each service
services_ok=0
total_services=5

echo -e "${YELLOW}📊 VERIFICACIÓN DE SERVICIOS PRINCIPALES${NC}"
echo ""

if check_service "Eureka Server" "8761" "http://localhost:8761/actuator/health" "eureka-server"; then
    ((services_ok++))
fi

if check_service "API Gateway" "8080" "http://localhost:8080/actuator/health" "api-gateway"; then
    ((services_ok++))
fi

if check_service "Arca Cotizador" "8081" "http://localhost:8081/actuator/health" "arca-cotizador"; then
    ((services_ok++))
fi

if check_service "Arca Gestor Solicitudes" "8082" "http://localhost:8082/actuator/health" "arca-gestor-solicitudes"; then
    ((services_ok++))
fi

if check_service "Hello World Service" "8083" "http://localhost:8083/actuator/health" "hello-world-service"; then
    ((services_ok++))
fi

# Check Eureka registrations if Eureka is running
if pgrep -f "eureka-server" > /dev/null; then
    echo -e "${YELLOW}📋 VERIFICACIÓN DE REGISTROS EN EUREKA${NC}"
    echo ""
    
    sleep 2  # Give some time for the check
    
    check_eureka_registration "ARCA-COTIZADOR"
    check_eureka_registration "ARCA-GESTOR-SOLICITUDES"
    check_eureka_registration "HELLO-WORLD-SERVICE"
    check_eureka_registration "API-GATEWAY"
fi

# Check for load balancing instances
echo -e "${YELLOW}⚖️  VERIFICACIÓN DE INSTANCIAS DE LOAD BALANCING${NC}"
echo ""

lb_instances=0

if netstat -tuln 2>/dev/null | grep -q ":8084 " || ss -tuln 2>/dev/null | grep -q ":8084 "; then
    echo -e "✅ Hello World Instance 2: ${GREEN}Puerto 8084 activo${NC}"
    ((lb_instances++))
else
    echo -e "ℹ️  Hello World Instance 2: ${YELLOW}No está corriendo${NC}"
fi

if netstat -tuln 2>/dev/null | grep -q ":8091 " || ss -tuln 2>/dev/null | grep -q ":8091 "; then
    echo -e "✅ Arca Cotizador Instance 2: ${GREEN}Puerto 8091 activo${NC}"
    ((lb_instances++))
else
    echo -e "ℹ️  Arca Cotizador Instance 2: ${YELLOW}No está corriendo${NC}"
fi

if netstat -tuln 2>/dev/null | grep -q ":8092 " || ss -tuln 2>/dev/null | grep -q ":8092 "; then
    echo -e "✅ Arca Gestor Instance 2: ${GREEN}Puerto 8092 activo${NC}"
    ((lb_instances++))
else
    echo -e "ℹ️  Arca Gestor Instance 2: ${YELLOW}No está corriendo${NC}"
fi

# Summary
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}📊 RESUMEN FINAL${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

if [[ $services_ok -eq $total_services ]]; then
    echo -e "🎉 Estado General: ${GREEN}TODOS LOS SERVICIOS OK${NC} ($services_ok/$total_services)"
else
    echo -e "⚠️  Estado General: ${YELLOW}ALGUNOS SERVICIOS CON PROBLEMAS${NC} ($services_ok/$total_services)"
fi

if [[ $lb_instances -gt 0 ]]; then
    echo -e "⚖️  Load Balancing: ${GREEN}$lb_instances instancias adicionales activas${NC}"
else
    echo -e "⚖️  Load Balancing: ${YELLOW}No hay instancias adicionales${NC}"
fi

echo ""
echo -e "${BLUE}🔗 URLs de Acceso:${NC}"
echo "• Eureka Dashboard: http://localhost:8761"
echo "• API Gateway: http://localhost:8080"
echo "• Arca Cotizador: http://localhost:8081"
echo "• Arca Gestor Solicitudes: http://localhost:8082"
echo "• Hello World Service: http://localhost:8083"

echo ""
echo -e "${BLUE}🧪 Comandos de Prueba Rápida:${NC}"
echo "curl http://localhost:8080/api/hello"
echo "curl http://localhost:8081/actuator/health"
echo "curl http://localhost:8082/actuator/health"

echo ""
echo -e "${BLUE}🛠️  Herramientas de Diagnóstico:${NC}"
echo "./test-circuit-breaker.sh     # Probar Circuit Breakers"
echo "./start-load-balancing.sh     # Configurar Load Balancing"
echo "./stop-all-services.sh        # Parar todos los servicios"

# Clean up temp files
rm -f /tmp/health_check

echo ""
echo "⏰ Verificación completada: $(date)"
