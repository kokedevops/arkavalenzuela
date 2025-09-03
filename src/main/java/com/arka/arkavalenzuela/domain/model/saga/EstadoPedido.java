package com.arka.arkavalenzuela.domain.model.saga;

/**
 * Estados posibles del pedido en el proceso Saga
 */
public enum EstadoPedido {
    CREADO("Pedido creado"),
    INVENTARIO_RESERVADO("Inventario reservado"),
    INVENTARIO_FALLIDO("Fallo en reserva de inventario"),
    ENVIO_GENERADO("Orden de envío generada"),
    ENVIO_FALLIDO("Fallo en generación de envío"),
    NOTIFICACION_ENVIADA("Notificación enviada"),
    COMPLETADO("Pedido completado"),
    COMPENSADO("Pedido compensado"),
    FALLIDO("Pedido fallido");
    
    private final String descripcion;
    
    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
