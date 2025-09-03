package com.arka.arkavalenzuela.infrastructure.adapter.saga.mock;

import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import com.arka.arkavalenzuela.domain.port.out.saga.LambdaInvoker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Implementación mock del invocador de Lambda para desarrollo local
 * Se activa cuando aws.mock.enabled=true
 */
@Component
@ConditionalOnProperty(name = "aws.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockLambdaInvoker implements LambdaInvoker {
    
    private static final Logger logger = LoggerFactory.getLogger(MockLambdaInvoker.class);
    
    private final ObjectMapper objectMapper;
    
    public MockLambdaInvoker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        logger.info("🔧 MockLambdaInvoker inicializado - Modo desarrollo");
    }
    
    @Override
    public Mono<String> invokeInventoryReservation(SagaPedido pedido) {
        logger.info("🏭 [MOCK LAMBDA] Invocando reserva de inventario para pedido: {}", pedido.getPedidoId());
        return simulateInventoryLambda(pedido);
    }
    
    @Override
    public Mono<String> invokeShippingGeneration(SagaPedido pedido) {
        logger.info("🚚 [MOCK LAMBDA] Invocando generación de envío para pedido: {}", pedido.getPedidoId());
        return simulateShippingLambda(pedido);
    }
    
    @Override
    public Mono<String> invokeCustomNotification(SagaPedido pedido) {
        logger.info("📨 [MOCK LAMBDA] Invocando notificación personalizada para pedido: {}", pedido.getPedidoId());
        return simulateNotificationLambda(pedido);
    }
    
    @Override
    public Mono<String> invokeLambda(String functionName, String payload) {
        return Mono.fromCallable(() -> {
            logger.info("⚡ [MOCK LAMBDA] Invocando función: {}", functionName);
            logger.info("   📦 Payload: {}", payload);
            
            // Simular tiempo de procesamiento
            Thread.sleep(200);
            
            String mockResponse = "{ \"status\": \"success\", \"mockResponse\": true, \"timestamp\": \"" + 
                                java.time.LocalDateTime.now() + "\" }";
            
            logger.info("   ✅ Respuesta mock: {}", mockResponse);
            return mockResponse;
            
        }).onErrorMap(InterruptedException.class, e -> {
            Thread.currentThread().interrupt();
            return new RuntimeException("Simulación interrumpida", e);
        });
    }
    
    private Mono<String> simulateInventoryLambda(SagaPedido pedido) {
        return Mono.fromCallable(() -> {
            try {
                // Simular lógica de inventario
                boolean hasStock = !pedido.getProductoId().contains("999") && pedido.getCantidad() <= 10;
                
                Thread.sleep(300); // Simular tiempo de procesamiento
                
                String mockResponse;
                if (hasStock) {
                    mockResponse = String.format("{ \"status\": \"INVENTORY_RESERVED\", \"pedidoId\": \"%s\", \"reserved\": true }", 
                                                pedido.getPedidoId());
                    logger.info("   ✅ Inventario reservado exitosamente");
                } else {
                    mockResponse = String.format("{ \"status\": \"INVENTORY_FAILED\", \"pedidoId\": \"%s\", \"reason\": \"INSUFFICIENT_STOCK\" }", 
                                                pedido.getPedidoId());
                    logger.warn("   ❌ Inventario insuficiente");
                }
                
                return mockResponse;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Simulación interrumpida", e);
            }
        });
    }
    
    private Mono<String> simulateShippingLambda(SagaPedido pedido) {
        return Mono.fromCallable(() -> {
            try {
                Thread.sleep(250); // Simular tiempo de procesamiento
                
                String shippingOrderId = "SHIP_" + java.util.UUID.randomUUID().toString().substring(0, 8);
                String mockResponse = String.format("{ \"status\": \"SHIPPING_GENERATED\", \"pedidoId\": \"%s\", \"shippingOrderId\": \"%s\" }", 
                                                   pedido.getPedidoId(), shippingOrderId);
                
                logger.info("   ✅ Orden de envío generada: {}", shippingOrderId);
                return mockResponse;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Simulación interrumpida", e);
            }
        });
    }
    
    private Mono<String> simulateNotificationLambda(SagaPedido pedido) {
        return Mono.fromCallable(() -> {
            try {
                Thread.sleep(150); // Simular tiempo de procesamiento
                
                String notificationId = "NOTIF_" + java.util.UUID.randomUUID().toString().substring(0, 8);
                String mockResponse = String.format("{ \"status\": \"NOTIFICATION_SENT\", \"pedidoId\": \"%s\", \"notificationId\": \"%s\" }", 
                                                   pedido.getPedidoId(), notificationId);
                
                logger.info("   ✅ Notificación enviada: {}", notificationId);
                return mockResponse;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Simulación interrumpida", e);
            }
        });
    }
}
