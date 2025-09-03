package com.arka.arkavalenzuela.domain.port.in.saga;

import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import reactor.core.publisher.Mono;

/**
 * Puerto de entrada para el patrón Saga
 */
public interface SagaOrchestrator {
    
    /**
     * Inicia el proceso Saga para un pedido
     * @param pedido Pedido a procesar
     * @return Resultado del inicio del proceso
     */
    Mono<String> startSaga(SagaPedido pedido);
    
    /**
     * Maneja el evento de inventario reservado exitosamente
     * @param pedidoId ID del pedido
     * @return Resultado del procesamiento
     */
    Mono<String> handleInventoryReserved(String pedidoId);
    
    /**
     * Maneja el evento de fallo en reserva de inventario
     * @param pedidoId ID del pedido
     * @return Resultado del procesamiento
     */
    Mono<String> handleInventoryReservationFailed(String pedidoId);
    
    /**
     * Maneja el evento de envío generado exitosamente
     * @param pedidoId ID del pedido
     * @param shippingOrderId ID de la orden de envío
     * @return Resultado del procesamiento
     */
    Mono<String> handleShippingGenerated(String pedidoId, String shippingOrderId);
    
    /**
     * Maneja el evento de fallo en generación de envío
     * @param pedidoId ID del pedido
     * @return Resultado del procesamiento
     */
    Mono<String> handleShippingGenerationFailed(String pedidoId);
    
    /**
     * Maneja el evento de notificación enviada
     * @param pedidoId ID del pedido
     * @return Resultado del procesamiento
     */
    Mono<String> handleNotificationSent(String pedidoId);
}
