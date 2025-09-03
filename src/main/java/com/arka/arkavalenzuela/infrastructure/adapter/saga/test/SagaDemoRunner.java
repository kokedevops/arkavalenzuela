package com.arka.arkavalenzuela.infrastructure.adapter.saga.test;

import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import com.arka.arkavalenzuela.domain.port.in.saga.SagaOrchestrator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Ejemplo de prueba del patrón Saga
 * Se ejecuta al iniciar la aplicación si se activa con: saga.demo.enabled=true
 */
@Component
@ConditionalOnProperty(name = "saga.demo.enabled", havingValue = "true")
public class SagaDemoRunner implements CommandLineRunner {
    
    private final SagaOrchestrator sagaOrchestrator;
    
    public SagaDemoRunner(SagaOrchestrator sagaOrchestrator) {
        this.sagaOrchestrator = sagaOrchestrator;
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=== DEMO DEL PATRÓN SAGA ===");
        System.out.println("Iniciando ejemplos de prueba...\n");
        
        // Caso 1: Pedido exitoso
        ejecutarCasoExitoso();
        
        Thread.sleep(2000);
        
        // Caso 2: Pedido con producto agotado
        ejecutarCasoInventarioAgotado();
        
        System.out.println("\n=== FIN DEL DEMO ===");
    }
    
    private void ejecutarCasoExitoso() {
        System.out.println("🟢 CASO 1: Pedido Exitoso");
        System.out.println("Cliente: CLI001, Producto: PROD123, Cantidad: 2");
        
        SagaPedido pedidoExitoso = new SagaPedido(
                "CLI001",
                "PROD123", // Producto con stock
                2,
                29.99
        );
        
        sagaOrchestrator.startSaga(pedidoExitoso)
                .doOnSuccess(result -> {
                    System.out.println("✅ Saga iniciado: " + result);
                    System.out.println("📋 Pedido ID: " + pedidoExitoso.getPedidoId());
                })
                .doOnError(error -> {
                    System.out.println("❌ Error en Saga: " + error.getMessage());
                })
                .subscribe();
    }
    
    private void ejecutarCasoInventarioAgotado() {
        System.out.println("\n🔴 CASO 2: Inventario Agotado");
        System.out.println("Cliente: CLI002, Producto: PROD999, Cantidad: 100");
        
        SagaPedido pedidoFallido = new SagaPedido(
                "CLI002",
                "PROD999", // Producto sin stock
                100, // Cantidad excesiva
                15.50
        );
        
        sagaOrchestrator.startSaga(pedidoFallido)
                .doOnSuccess(result -> {
                    System.out.println("✅ Saga iniciado: " + result);
                    System.out.println("📋 Pedido ID: " + pedidoFallido.getPedidoId());
                    System.out.println("⚠️  Este pedido debería fallar en la reserva de inventario");
                })
                .doOnError(error -> {
                    System.out.println("❌ Error en Saga: " + error.getMessage());
                })
                .subscribe();
    }
}
