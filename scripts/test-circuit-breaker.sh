#!/bin/bash

echo "========================================="
echo "PROBANDO CIRCUIT BREAKER - CALCULO ENVIO"
echo "========================================="
echo ""

# URL base del servicio
SERVICE_URL="http://localhost:8082"

# Function to make HTTP requests with error handling
make_request() {
    local method="$1"
    local url="$2"
    local data="$3"
    local description="$4"
    
    echo "🔄 $description"
    if [[ -n "$data" ]]; then
        curl -s -X "$method" "$url" \
             -H "Content-Type: application/json" \
             -d "$data" | jq . 2>/dev/null || curl -s -X "$method" "$url" -H "Content-Type: application/json" -d "$data"
    else
        curl -s -X "$method" "$url" \
             -H "Content-Type: application/json" | jq . 2>/dev/null || curl -s -X "$method" "$url" -H "Content-Type: application/json"
    fi
    echo ""
    echo ""
}

# Check if service is running
echo "🔍 Verificando que el servicio esté disponible..."
if ! curl -s "$SERVICE_URL/actuator/health" > /dev/null 2>&1; then
    echo "❌ ERROR: El servicio no está disponible en $SERVICE_URL"
    echo "   Por favor, asegúrate de que Arca Gestor Solicitudes esté corriendo en el puerto 8082"
    exit 1
fi
echo "✅ Servicio disponible"
echo ""

echo "1️⃣ Verificando estado del servicio..."
make_request "GET" "$SERVICE_URL/api/calculo-envio/estado" "" "Estado del servicio"

echo "2️⃣ Realizando cálculo de envío normal..."
make_request "POST" "$SERVICE_URL/api/calculo-envio/calcular" \
    '{"origen":"Lima","destino":"Arequipa","peso":2.5,"dimensiones":"50x30x20"}' \
    "Cálculo de envío normal"

echo "3️⃣ Probando múltiples llamadas para activar Circuit Breaker..."
echo "🔄 Ejecutando 10 llamadas consecutivas..."
for i in {1..10}; do
    echo "   Llamada $i:"
    make_request "POST" "$SERVICE_URL/api/calculo-envio/probar-circuit-breaker" \
        '{"escenario":"externo","origen":"Lima","destino":"Cusco","peso":1.5}' \
        "Prueba Circuit Breaker - Llamada $i"
    sleep 1
done

echo "4️⃣ Verificando estado de Circuit Breakers..."
make_request "GET" "$SERVICE_URL/api/circuit-breaker/estado" "" "Estado de Circuit Breakers"

echo "5️⃣ Probando servicio interno simulado..."
make_request "POST" "$SERVICE_URL/api/calculo-envio/probar-circuit-breaker" \
    '{"escenario":"interno","origen":"Lima","destino":"Trujillo","peso":3.0}' \
    "Servicio interno simulado"

echo "6️⃣ Probando flujo completo con fallbacks..."
make_request "POST" "$SERVICE_URL/api/calculo-envio/probar-circuit-breaker" \
    '{"escenario":"completo","origen":"Lima","destino":"Iquitos","peso":4.5}' \
    "Flujo completo con fallbacks"

echo "7️⃣ Forzando apertura de Circuit Breaker para pruebas..."
make_request "POST" "$SERVICE_URL/api/circuit-breaker/forzar-apertura/proveedor-externo-service" \
    "" "Forzar apertura Circuit Breaker"

echo "8️⃣ Probando cálculo con Circuit Breaker abierto..."
make_request "POST" "$SERVICE_URL/api/calculo-envio/calcular" \
    '{"origen":"Lima","destino":"Piura","peso":2.0,"dimensiones":"40x30x25"}' \
    "Cálculo con Circuit Breaker abierto"

echo "9️⃣ Verificando estado específico de Circuit Breaker..."
make_request "GET" "$SERVICE_URL/api/circuit-breaker/estado/proveedor-externo-service" \
    "" "Estado específico Circuit Breaker"

echo "🔟 Reiniciando Circuit Breaker..."
make_request "POST" "$SERVICE_URL/api/circuit-breaker/reiniciar-metricas/proveedor-externo-service" \
    "" "Reiniciar Circuit Breaker"

echo "1️⃣1️⃣ Prueba rápida final..."
make_request "GET" "$SERVICE_URL/api/calculo-envio/prueba-rapida?origen=Lima&destino=Cusco&peso=1.0" \
    "" "Prueba rápida final"

echo "========================================="
echo "🎉 PRUEBAS DE CIRCUIT BREAKER COMPLETADAS"
echo "========================================="
echo ""
echo "📊 Para monitorear el estado de los Circuit Breakers en tiempo real:"
echo "curl -X GET \"$SERVICE_URL/api/circuit-breaker/estado\" | jq ."
echo ""
echo "🔍 Para verificar métricas de Actuator:"
echo "curl -X GET \"$SERVICE_URL/actuator/health\" | jq ."
echo "curl -X GET \"$SERVICE_URL/actuator/circuitbreakers\" | jq ."
echo ""
echo "🛠️  Comandos útiles para debugging:"
echo "# Ver logs en tiempo real:"
echo "tail -f logs/arca-gestor-solicitudes.log"
echo ""
echo "# Monitorear Circuit Breakers continuamente:"
echo "watch -n 2 'curl -s \"$SERVICE_URL/api/circuit-breaker/estado\" | jq .'"
echo ""
echo "# Probar múltiples requests simultáneos:"
echo "for i in {1..20}; do curl -s \"$SERVICE_URL/api/calculo-envio/prueba-rapida?origen=Lima&destino=Cusco&peso=1.0\" & done; wait"
