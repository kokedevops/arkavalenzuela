#!/bin/bash

echo "========================================="
echo "ARKA CIRCUIT BREAKER CLI"
echo "========================================="
echo ""

echo "🚀 Iniciando CLI interactiva para Circuit Breaker..."
echo ""

# Set environment variables for CLI
export SPRING_PROFILES_ACTIVE="dev"
export ARKA_CLI_ENABLED="true"

echo "⚙️  Configuración:"
echo "• Profile: $SPRING_PROFILES_ACTIVE"
echo "• CLI Enabled: $ARKA_CLI_ENABLED"
echo ""

# Navigate to project directory
PROJECT_DIR="$(dirname "$0")/.."
cd "$PROJECT_DIR" || {
    echo "❌ ERROR: No se pudo navegar al directorio del proyecto"
    exit 1
}

echo "📁 Directorio del proyecto: $(pwd)"
echo ""

# Check if the service is already running
if pgrep -f "arca-gestor-solicitudes" > /dev/null; then
    echo "⚠️  WARNING: Arca Gestor Solicitudes ya está corriendo"
    echo "   Para usar la CLI, detén la instancia actual primero:"
    echo "   pkill -f arca-gestor-solicitudes"
    echo ""
    read -p "¿Quieres detener la instancia actual y continuar? (y/N): " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "🛑 Deteniendo instancia actual..."
        pkill -f arca-gestor-solicitudes
        sleep 3
    else
        echo "Cancelando..."
        exit 1
    fi
fi

echo "🔧 Ejecutando aplicación con CLI habilitada..."
echo ""
echo "⚡ CLI Commands disponibles:"
echo "   • help - Mostrar ayuda"
echo "   • status - Estado de Circuit Breakers"
echo "   • open <name> - Abrir Circuit Breaker"
echo "   • close <name> - Cerrar Circuit Breaker"
echo "   • reset <name> - Reset métricas"
echo "   • test <scenario> - Ejecutar test"
echo "   • exit - Salir"
echo ""

# Create a simple CLI wrapper
create_cli_wrapper() {
    cat > /tmp/arka-cli.sh << 'EOF'
#!/bin/bash

GESTOR_URL="http://localhost:8082"

show_help() {
    echo ""
    echo "🛠️  ARKA Circuit Breaker CLI Commands:"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "  status                           - Ver estado de todos los Circuit Breakers"
    echo "  status <name>                    - Ver estado de un Circuit Breaker específico"
    echo "  open <name>                      - Forzar apertura de Circuit Breaker"
    echo "  close <name>                     - Forzar cierre de Circuit Breaker"
    echo "  reset <name>                     - Reiniciar métricas de Circuit Breaker"
    echo "  test external                    - Probar servicio externo"
    echo "  test internal                    - Probar servicio interno"
    echo "  test multiple                    - Ejecutar múltiples pruebas"
    echo "  health                           - Ver estado de salud del servicio"
    echo "  metrics                          - Ver métricas de Actuator"
    echo "  help                             - Mostrar esta ayuda"
    echo "  exit                             - Salir de CLI"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
}

circuit_breaker_cli() {
    echo "🎯 ARKA Circuit Breaker CLI iniciada"
    echo "   Servicio: $GESTOR_URL"
    echo "   Escribe 'help' para ver comandos disponibles"
    echo ""
    
    while true; do
        echo -n "arka-cli> "
        read -r command args
        
        case "$command" in
            "help"|"h")
                show_help
                ;;
            "status"|"st")
                if [[ -n "$args" ]]; then
                    echo "🔍 Estado de Circuit Breaker: $args"
                    curl -s "$GESTOR_URL/api/circuit-breaker/estado/$args" | jq . 2>/dev/null || curl -s "$GESTOR_URL/api/circuit-breaker/estado/$args"
                else
                    echo "🔍 Estado de todos los Circuit Breakers:"
                    curl -s "$GESTOR_URL/api/circuit-breaker/estado" | jq . 2>/dev/null || curl -s "$GESTOR_URL/api/circuit-breaker/estado"
                fi
                echo ""
                ;;
            "open")
                if [[ -n "$args" ]]; then
                    echo "🔓 Abriendo Circuit Breaker: $args"
                    curl -s -X POST "$GESTOR_URL/api/circuit-breaker/forzar-apertura/$args"
                    echo ""
                else
                    echo "❌ Error: Especifica el nombre del Circuit Breaker"
                    echo "   Ejemplo: open proveedor-externo-service"
                fi
                ;;
            "close")
                if [[ -n "$args" ]]; then
                    echo "🔒 Cerrando Circuit Breaker: $args"
                    curl -s -X POST "$GESTOR_URL/api/circuit-breaker/cerrar/$args"
                    echo ""
                else
                    echo "❌ Error: Especifica el nombre del Circuit Breaker"
                fi
                ;;
            "reset")
                if [[ -n "$args" ]]; then
                    echo "🔄 Reiniciando métricas de Circuit Breaker: $args"
                    curl -s -X POST "$GESTOR_URL/api/circuit-breaker/reiniciar-metricas/$args"
                    echo ""
                else
                    echo "❌ Error: Especifica el nombre del Circuit Breaker"
                fi
                ;;
            "test")
                case "$args" in
                    "external")
                        echo "🧪 Probando servicio externo..."
                        curl -s -X POST "$GESTOR_URL/api/calculo-envio/probar-circuit-breaker" \
                             -H "Content-Type: application/json" \
                             -d '{"escenario":"externo","origen":"Lima","destino":"Cusco","peso":1.5}' | jq . 2>/dev/null
                        ;;
                    "internal")
                        echo "🧪 Probando servicio interno..."
                        curl -s -X POST "$GESTOR_URL/api/calculo-envio/probar-circuit-breaker" \
                             -H "Content-Type: application/json" \
                             -d '{"escenario":"interno","origen":"Lima","destino":"Trujillo","peso":3.0}' | jq . 2>/dev/null
                        ;;
                    "multiple")
                        echo "🧪 Ejecutando múltiples pruebas..."
                        for i in {1..5}; do
                            echo "  Prueba $i:"
                            curl -s -X POST "$GESTOR_URL/api/calculo-envio/probar-circuit-breaker" \
                                 -H "Content-Type: application/json" \
                                 -d '{"escenario":"externo","origen":"Lima","destino":"Test","peso":1.0}' | jq -r '.mensaje // .error // .' 2>/dev/null
                            sleep 1
                        done
                        ;;
                    *)
                        echo "❌ Error: Escenario no válido"
                        echo "   Opciones: external, internal, multiple"
                        ;;
                esac
                echo ""
                ;;
            "health"|"h")
                echo "🏥 Estado de salud del servicio:"
                curl -s "$GESTOR_URL/actuator/health" | jq . 2>/dev/null || curl -s "$GESTOR_URL/actuator/health"
                echo ""
                ;;
            "metrics"|"m")
                echo "📊 Métricas de Circuit Breakers:"
                curl -s "$GESTOR_URL/actuator/circuitbreakers" | jq . 2>/dev/null || curl -s "$GESTOR_URL/actuator/circuitbreakers"
                echo ""
                ;;
            "exit"|"quit"|"q")
                echo "👋 Saliendo de ARKA CLI..."
                break
                ;;
            "")
                # Empty command, do nothing
                ;;
            *)
                echo "❌ Comando no reconocido: $command"
                echo "   Escribe 'help' para ver comandos disponibles"
                ;;
        esac
    done
}

# Wait for service to be ready
echo "⏳ Esperando que el servicio esté listo..."
sleep 10

# Check if service is accessible
if curl -s "$GESTOR_URL/actuator/health" > /dev/null 2>&1; then
    echo "✅ Servicio listo"
    circuit_breaker_cli
else
    echo "❌ Error: Servicio no accesible en $GESTOR_URL"
    echo "   Verifica que Arca Gestor Solicitudes esté corriendo"
fi
EOF

    chmod +x /tmp/arka-cli.sh
}

# Start the service in background and then launch CLI
echo "🔄 Iniciando Arca Gestor Solicitudes con CLI habilitada..."

# Start service in background
gnome-terminal --title="Arca Gestor Solicitudes (CLI Mode)" --tab -- bash -c "./gradlew :arca-gestor-solicitudes:bootRun --args='--cli' -Darka.cli.enabled=true; exec bash" 2>/dev/null || \
xterm -title "Arca Gestor Solicitudes (CLI Mode)" -e "bash -c './gradlew :arca-gestor-solicitudes:bootRun --args=\"--cli\" -Darka.cli.enabled=true; exec bash'" 2>/dev/null || \
konsole --title "Arca Gestor Solicitudes (CLI Mode)" -e bash -c "./gradlew :arca-gestor-solicitudes:bootRun --args='--cli' -Darka.cli.enabled=true; exec bash" 2>/dev/null || \
{
    echo "No suitable terminal found. Starting service in background..."
    nohup ./gradlew :arca-gestor-solicitudes:bootRun --args="--cli" -Darka.cli.enabled=true > logs/arca-gestor-solicitudes-cli.log 2>&1 &
    echo "Logs available at: logs/arca-gestor-solicitudes-cli.log"
}

# Create and launch CLI
create_cli_wrapper
/tmp/arka-cli.sh

echo ""
echo "========================================="
echo "🏁 CLI CIRCUIT BREAKER FINALIZADA"
echo "========================================="
