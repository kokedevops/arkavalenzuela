package com.arka.arkavalenzuela.infrastructure.adapter.saga.mock;

import com.arka.arkavalenzuela.application.service.saga.SagaOrchestratorService;
import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Simulador de eventos Saga para desarrollo local
 * Simula las respuestas asíncronas de AWS Lambda/SNS
 */
@Component
public class MockSagaEventSimulator {
    
    private static final Logger logger = LoggerFactory.getLogger(MockSagaEventSimulator.class);
    
    private final SagaOrchestratorService orchestratorService;
    
    public MockSagaEventSimulator(SagaOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
        logger.info("🎭 MockSagaEventSimulator inicializado");
    }
    
    /**
     * Simula evento de inventario reservado
     */
    public void simulateInventoryReservedEvent(SagaPedido pedido, boolean success) {
        CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS).execute(() -> {
            try {
                logger.info("🎬 Simulando evento de inventario reservado para pedido: {}", pedido.getPedidoId());
                
                if (success) {
                    orchestratorService.handleInventoryReserved(pedido.getPedidoId()).subscribe();
                } else {
                    // En caso de fallo, manejar la compensación
                    logger.warn("❌ Simulando fallo en reserva de inventario");
                    // orchestratorService.handleInventoryFailed(pedido.getPedidoId()).subscribe();
                }
                
            } catch (Exception e) {
                logger.error("❌ Error simulando evento de inventario", e);
            }
        });
    }
    
    /**
     * Simula evento de envío generado
     */
    public void simulateShippingGeneratedEvent(SagaPedido pedido, boolean success) {
        CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> {
            try {
                logger.info("🎬 Simulando evento de envío generado para pedido: {}", pedido.getPedidoId());
                
                if (success) {
                    String shippingOrderId = "SHIPMENT_" + java.util.UUID.randomUUID().toString().substring(0, 8);
                    orchestratorService.handleShippingGenerated(pedido.getPedidoId(), shippingOrderId).subscribe();
                } else {
                    logger.warn("❌ Simulando fallo en generación de envío");
                    // orchestratorService.handleShippingFailed(pedido.getPedidoId()).subscribe();
                }
                
            } catch (Exception e) {
                logger.error("❌ Error simulando evento de envío", e);
            }
        });
    }
    
    /**
     * Simula evento de notificación enviada
     */
    public void simulateNotificationSentEvent(SagaPedido pedido, boolean success) {
        CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
            try {
                logger.info("🎬 Simulando evento de notificación para pedido: {}", pedido.getPedidoId());
                
                if (success) {
                    orchestratorService.handleNotificationSent(pedido.getPedidoId()).subscribe();
                } else {
                    logger.warn("❌ Simulando fallo en notificación");
                    // Las notificaciones generalmente no requieren compensación
                }
                
            } catch (Exception e) {
                logger.error("❌ Error simulando evento de notificación", e);
            }
        });
    }
}
