package com.arka.arkavalenzuela.domain.port.out.saga;

import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para invocar funciones Lambda
 */
public interface LambdaInvoker {
    
    /**
     * Invoca la Lambda de reserva de inventario
     * @param pedido Pedido para procesar
     * @return Resultado de la invocación
     */
    Mono<String> invokeInventoryReservation(SagaPedido pedido);
    
    /**
     * Invoca la Lambda de generación de envío
     * @param pedido Pedido para procesar
     * @return Resultado de la invocación
     */
    Mono<String> invokeShippingGeneration(SagaPedido pedido);
    
    /**
     * Invoca la Lambda personalizada (notificación)
     * @param pedido Pedido para procesar
     * @return Resultado de la invocación
     */
    Mono<String> invokeCustomNotification(SagaPedido pedido);
    
    /**
     * Invoca una función Lambda específica
     * @param functionName Nombre de la función Lambda
     * @param payload Payload a enviar
     * @return Resultado de la invocación
     */
    Mono<String> invokeLambda(String functionName, String payload);
}
