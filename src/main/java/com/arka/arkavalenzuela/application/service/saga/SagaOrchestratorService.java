package com.arka.arkavalenzuela.application.service.saga;

import com.arka.arkavalenzuela.domain.model.saga.EstadoPedido;
import com.arka.arkavalenzuela.domain.model.saga.SagaEvent;
import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import com.arka.arkavalenzuela.domain.port.in.saga.SagaOrchestrator;
import com.arka.arkavalenzuela.domain.port.out.saga.ExternalServiceAdapter;
import com.arka.arkavalenzuela.domain.port.out.saga.LambdaInvoker;
import com.arka.arkavalenzuela.domain.port.out.saga.SagaEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que implementa la orquestación del patrón Saga
 */
@Service
public class SagaOrchestratorService implements SagaOrchestrator {
    
    private static final Logger logger = LoggerFactory.getLogger(SagaOrchestratorService.class);
    
    // Store temporal para el estado del saga (en producción usar Redis o DynamoDB)
    private final Map<String, SagaPedido> sagaState = new ConcurrentHashMap<>();
    
    private final SagaEventPublisher eventPublisher;
    private final LambdaInvoker lambdaInvoker;
    private final ExternalServiceAdapter externalServiceAdapter;
    private final ObjectMapper objectMapper;
    
    public SagaOrchestratorService(SagaEventPublisher eventPublisher,
                                  LambdaInvoker lambdaInvoker,
                                  ExternalServiceAdapter externalServiceAdapter,
                                  ObjectMapper objectMapper) {
        this.eventPublisher = eventPublisher;
        this.lambdaInvoker = lambdaInvoker;
        this.externalServiceAdapter = externalServiceAdapter;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Mono<String> startSaga(SagaPedido pedido) {
        logger.info("Iniciando Saga para pedido: {}", pedido.getPedidoId());
        
        // Guardar estado inicial
        sagaState.put(pedido.getPedidoId(), pedido);
        
        // Fase 1: Reserva de Inventario
        return invokeInventoryReservation(pedido)
                .doOnSuccess(result -> logger.info("Saga iniciado exitosamente para pedido: {}", pedido.getPedidoId()))
                .doOnError(error -> logger.error("Error al iniciar Saga para pedido: {}", pedido.getPedidoId(), error));
    }
    
    @Override
    public Mono<String> handleInventoryReserved(String pedidoId) {
        logger.info("Manejando inventario reservado para pedido: {}", pedidoId);
        
        return Mono.fromCallable(() -> sagaState.get(pedidoId))
                .flatMap(pedido -> {
                    if (pedido == null) {
                        return Mono.error(new RuntimeException("Pedido no encontrado: " + pedidoId));
                    }
                    
                    pedido.cambiarEstado(EstadoPedido.INVENTARIO_RESERVADO);
                    sagaState.put(pedidoId, pedido);
                    
                    // Fase 2: Generar orden de envío
                    return invokeShippingGeneration(pedido);
                });
    }
    
    @Override
    public Mono<String> handleInventoryReservationFailed(String pedidoId) {
        logger.error("Fallo en reserva de inventario para pedido: {}", pedidoId);
        
        return Mono.fromCallable(() -> sagaState.get(pedidoId))
                .flatMap(pedido -> {
                    if (pedido == null) {
                        return Mono.error(new RuntimeException("Pedido no encontrado: " + pedidoId));
                    }
                    
                    pedido.cambiarEstado(EstadoPedido.INVENTARIO_FALLIDO);
                    sagaState.put(pedidoId, pedido);
                    
                    // Publicar evento de fallo
                    SagaEvent failEvent = new SagaEvent(pedidoId, "INVENTORY_RESERVATION_FAILED", 
                                                       serializePedido(pedido), "SagaOrchestrator");
                    return eventPublisher.publishEvent(failEvent)
                            .map(messageId -> "Saga fallido - Inventario no disponible");
                });
    }
    
    @Override
    public Mono<String> handleShippingGenerated(String pedidoId, String shippingOrderId) {
        logger.info("Manejando envío generado para pedido: {} con orden: {}", pedidoId, shippingOrderId);
        
        return Mono.fromCallable(() -> sagaState.get(pedidoId))
                .flatMap(pedido -> {
                    if (pedido == null) {
                        return Mono.error(new RuntimeException("Pedido no encontrado: " + pedidoId));
                    }
                    
                    pedido.cambiarEstado(EstadoPedido.ENVIO_GENERADO);
                    sagaState.put(pedidoId, pedido);
                    
                    // Fase 3: Paso personalizado - Enviar notificación
                    return invokeCustomNotification(pedido);
                });
    }
    
    @Override
    public Mono<String> handleShippingGenerationFailed(String pedidoId) {
        logger.error("Fallo en generación de envío para pedido: {}", pedidoId);
        
        return Mono.fromCallable(() -> sagaState.get(pedidoId))
                .flatMap(pedido -> {
                    if (pedido == null) {
                        return Mono.error(new RuntimeException("Pedido no encontrado: " + pedidoId));
                    }
                    
                    pedido.cambiarEstado(EstadoPedido.ENVIO_FALLIDO);
                    sagaState.put(pedidoId, pedido);
                    
                    // Compensar inventario
                    return compensateInventoryReservation(pedido);
                });
    }
    
    @Override
    public Mono<String> handleNotificationSent(String pedidoId) {
        logger.info("Manejando notificación enviada para pedido: {}", pedidoId);
        
        return Mono.fromCallable(() -> sagaState.get(pedidoId))
                .map(pedido -> {
                    if (pedido == null) {
                        throw new RuntimeException("Pedido no encontrado: " + pedidoId);
                    }
                    
                    pedido.cambiarEstado(EstadoPedido.COMPLETADO);
                    sagaState.put(pedidoId, pedido);
                    
                    logger.info("Saga completado exitosamente para pedido: {}", pedidoId);
                    return "Saga completado exitosamente";
                });
    }
    
    private Mono<String> invokeInventoryReservation(SagaPedido pedido) {
        logger.info("Invocando reserva de inventario para pedido: {}", pedido.getPedidoId());
        
        return lambdaInvoker.invokeInventoryReservation(pedido)
                .doOnSuccess(result -> {
                    SagaEvent event = new SagaEvent(pedido.getPedidoId(), "INVENTORY_RESERVATION_INITIATED", 
                                                   serializePedido(pedido), "SagaOrchestrator");
                    eventPublisher.publishEvent(event).subscribe();
                });
    }
    
    private Mono<String> invokeShippingGeneration(SagaPedido pedido) {
        logger.info("Invocando generación de envío para pedido: {}", pedido.getPedidoId());
        
        return lambdaInvoker.invokeShippingGeneration(pedido)
                .doOnSuccess(result -> {
                    SagaEvent event = new SagaEvent(pedido.getPedidoId(), "SHIPPING_GENERATION_INITIATED", 
                                                   serializePedido(pedido), "SagaOrchestrator");
                    eventPublisher.publishEvent(event).subscribe();
                });
    }
    
    private Mono<String> invokeCustomNotification(SagaPedido pedido) {
        logger.info("Invocando notificación personalizada para pedido: {}", pedido.getPedidoId());
        
        return lambdaInvoker.invokeCustomNotification(pedido)
                .doOnSuccess(result -> {
                    SagaEvent event = new SagaEvent(pedido.getPedidoId(), "CUSTOM_NOTIFICATION_INITIATED", 
                                                   serializePedido(pedido), "SagaOrchestrator");
                    eventPublisher.publishEvent(event).subscribe();
                });
    }
    
    private Mono<String> compensateInventoryReservation(SagaPedido pedido) {
        logger.info("Compensando reserva de inventario para pedido: {}", pedido.getPedidoId());
        
        return externalServiceAdapter.compensateInventory(pedido)
                .map(success -> {
                    if (success) {
                        pedido.cambiarEstado(EstadoPedido.COMPENSADO);
                        logger.info("Inventario compensado para pedido: {}", pedido.getPedidoId());
                        return "Inventario compensado exitosamente";
                    } else {
                        pedido.cambiarEstado(EstadoPedido.FALLIDO);
                        logger.error("Fallo en compensación de inventario para pedido: {}", pedido.getPedidoId());
                        return "Fallo en compensación de inventario";
                    }
                })
                .doFinally(signal -> sagaState.put(pedido.getPedidoId(), pedido));
    }
    
    private String serializePedido(SagaPedido pedido) {
        try {
            return objectMapper.writeValueAsString(pedido);
        } catch (JsonProcessingException e) {
            logger.error("Error al serializar pedido", e);
            return "{}";
        }
    }
}
