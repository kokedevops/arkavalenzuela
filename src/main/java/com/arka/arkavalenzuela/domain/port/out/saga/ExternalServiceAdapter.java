package com.arka.arkavalenzuela.domain.port.out.saga;

import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para comunicación con servicios externos
 */
public interface ExternalServiceAdapter {
    
    /**
     * Conecta con el servicio de inventario (S2) para reservar productos
     * @param pedido Pedido con información del producto
     * @return Resultado de la reserva
     */
    Mono<Boolean> reserveInventory(SagaPedido pedido);
    
    /**
     * Conecta con el servicio ChipMen para generar orden de envío
     * @param pedido Pedido para generar envío
     * @return ID de la orden de envío generada
     */
    Mono<String> generateShippingOrder(SagaPedido pedido);
    
    /**
     * Compensa la reserva de inventario en caso de fallo
     * @param pedido Pedido a compensar
     * @return Resultado de la compensación
     */
    Mono<Boolean> compensateInventory(SagaPedido pedido);
}
