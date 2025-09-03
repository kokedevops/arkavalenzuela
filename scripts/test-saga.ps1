# Script de pruebas para el patrón Saga
# Autor: Sistema Arka
# Versión: 1.0

param(
    [string]$Action = "start",
    [string]$BaseUrl = "http://localhost:8888"
)

# Configuración
$Headers = @{
    "Content-Type" = "application/json"
    "Accept" = "application/json"
}

function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

function Test-SagaStart {
    Write-ColorOutput "=== INICIANDO PRUEBA DEL PATRÓN SAGA ===" "Cyan"
    Write-ColorOutput ""
    
    # Caso 1: Pedido exitoso
    Write-ColorOutput "🟢 CASO 1: Pedido Exitoso" "Green"
    $pedidoExitoso = @{
        clienteId = "CLI001"
        productoId = "PROD123"
        cantidad = 2
        precio = 29.99
    } | ConvertTo-Json
    
    try {
        $response1 = Invoke-RestMethod -Uri "$BaseUrl/api/saga/start" -Method Post -Body $pedidoExitoso -Headers $Headers
        Write-ColorOutput "✅ Respuesta: $($response1 | ConvertTo-Json -Depth 3)" "Green"
        Write-ColorOutput "📋 Pedido ID: $($response1.pedidoId)" "Yellow"
    }
    catch {
        Write-ColorOutput "❌ Error: $($_.Exception.Message)" "Red"
    }
    
    Write-ColorOutput ""
    Start-Sleep -Seconds 2
    
    # Caso 2: Pedido con inventario agotado
    Write-ColorOutput "🔴 CASO 2: Inventario Agotado" "Red"
    $pedidoFallido = @{
        clienteId = "CLI002"
        productoId = "PROD999"
        cantidad = 100
        precio = 15.50
    } | ConvertTo-Json
    
    try {
        $response2 = Invoke-RestMethod -Uri "$BaseUrl/api/saga/start" -Method Post -Body $pedidoFallido -Headers $Headers
        Write-ColorOutput "✅ Respuesta: $($response2 | ConvertTo-Json -Depth 3)" "Green"
        Write-ColorOutput "📋 Pedido ID: $($response2.pedidoId)" "Yellow"
        Write-ColorOutput "⚠️  Este pedido debería fallar en la reserva de inventario" "Yellow"
    }
    catch {
        Write-ColorOutput "❌ Error: $($_.Exception.Message)" "Red"
    }
    
    Write-ColorOutput ""
    Write-ColorOutput "=== PRUEBAS COMPLETADAS ===" "Cyan"
}

function Test-SagaWebhook {
    param([string]$EventType)
    
    Write-ColorOutput "=== PROBANDO WEBHOOK: $EventType ===" "Cyan"
    
    switch ($EventType) {
        "inventory-reserved" {
            $webhook = @{
                pedidoId = "TEST_PEDIDO_001"
            } | ConvertTo-Json
            $url = "$BaseUrl/api/saga/events/inventory-reserved"
        }
        "shipping-generated" {
            $webhook = @{
                pedidoId = "TEST_PEDIDO_001"
                shippingOrderId = "SHIP_12345"
            } | ConvertTo-Json
            $url = "$BaseUrl/api/saga/events/shipping-generated"
        }
        "notification-sent" {
            $webhook = @{
                pedidoId = "TEST_PEDIDO_001"
            } | ConvertTo-Json
            $url = "$BaseUrl/api/saga/events/notification-sent"
        }
        default {
            Write-ColorOutput "❌ Tipo de evento no válido: $EventType" "Red"
            return
        }
    }
    
    try {
        $response = Invoke-RestMethod -Uri $url -Method Post -Body $webhook -Headers $Headers
        Write-ColorOutput "✅ Webhook ejecutado correctamente" "Green"
        Write-ColorOutput "📄 Respuesta: $($response | ConvertTo-Json -Depth 3)" "White"
    }
    catch {
        Write-ColorOutput "❌ Error en webhook: $($_.Exception.Message)" "Red"
    }
}

function Show-Help {
    Write-ColorOutput "=== SCRIPT DE PRUEBAS DEL PATRÓN SAGA ===" "Cyan"
    Write-ColorOutput ""
    Write-ColorOutput "Uso:" "Yellow"
    Write-ColorOutput "  .\test-saga.ps1 -Action start                    # Probar inicio de Saga" "White"
    Write-ColorOutput "  .\test-saga.ps1 -Action webhook inventory-reserved  # Probar webhook" "White"
    Write-ColorOutput "  .\test-saga.ps1 -Action webhook shipping-generated  # Probar webhook" "White"
    Write-ColorOutput "  .\test-saga.ps1 -Action webhook notification-sent   # Probar webhook" "White"
    Write-ColorOutput "  .\test-saga.ps1 -Action help                     # Mostrar ayuda" "White"
    Write-ColorOutput ""
    Write-ColorOutput "Opciones:" "Yellow"
    Write-ColorOutput "  -BaseUrl    URL base del servicio (default: http://localhost:8888)" "White"
    Write-ColorOutput ""
    Write-ColorOutput "Ejemplos:" "Yellow"
    Write-ColorOutput "  .\test-saga.ps1 -Action start -BaseUrl http://localhost:8080" "White"
    Write-ColorOutput "  .\test-saga.ps1 -Action webhook inventory-reserved" "White"
}

function Test-ServiceHealth {
    Write-ColorOutput "=== VERIFICANDO SALUD DEL SERVICIO ===" "Cyan"
    
    try {
        $healthResponse = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -Method Get -Headers $Headers -ErrorAction SilentlyContinue
        Write-ColorOutput "✅ Servicio disponible" "Green"
        Write-ColorOutput "📊 Estado: $($healthResponse.status)" "White"
    }
    catch {
        try {
            # Intentar endpoint básico si actuator no está disponible
            $response = Invoke-WebRequest -Uri $BaseUrl -Method Get -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) {
                Write-ColorOutput "✅ Servicio disponible (sin actuator)" "Green"
            }
        }
        catch {
            Write-ColorOutput "❌ Servicio no disponible en $BaseUrl" "Red"
            Write-ColorOutput "💡 Asegúrate de que la aplicación esté ejecutándose" "Yellow"
            exit 1
        }
    }
}

# Función principal
switch ($Action.ToLower()) {
    "start" {
        Test-ServiceHealth
        Test-SagaStart
    }
    "webhook" {
        if ($args.Count -eq 0) {
            Write-ColorOutput "❌ Especifica el tipo de webhook" "Red"
            Show-Help
            exit 1
        }
        Test-ServiceHealth
        Test-SagaWebhook -EventType $args[0]
    }
    "health" {
        Test-ServiceHealth
    }
    "help" {
        Show-Help
    }
    default {
        Write-ColorOutput "❌ Acción no válida: $Action" "Red"
        Show-Help
        exit 1
    }
}

Write-ColorOutput ""
Write-ColorOutput "=== SCRIPT COMPLETADO ===" "Cyan"
