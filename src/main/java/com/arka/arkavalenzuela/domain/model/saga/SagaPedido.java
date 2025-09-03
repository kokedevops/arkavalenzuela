package com.arka.arkavalenzuela.domain.model.saga;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo de dominio para representar un pedido en el proceso Saga
 */
public class SagaPedido {
    
    private final String pedidoId;
    private final String clienteId;
    private final String productoId;
    private final Integer cantidad;
    private final Double precio;
    private final LocalDateTime fechaCreacion;
    private EstadoPedido estado;
    
    public SagaPedido(String clienteId, String productoId, Integer cantidad, Double precio) {
        this.pedidoId = UUID.randomUUID().toString();
        this.clienteId = clienteId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precio = precio;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoPedido.CREADO;
    }
    
    // Constructor completo para reconstrucción
    public SagaPedido(String pedidoId, String clienteId, String productoId, 
                     Integer cantidad, Double precio, LocalDateTime fechaCreacion, EstadoPedido estado) {
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precio = precio;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
    }
    
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        this.estado = nuevoEstado;
    }
    
    public Double calcularTotal() {
        return this.precio * this.cantidad;
    }
    
    // Getters
    public String getPedidoId() { return pedidoId; }
    public String getClienteId() { return clienteId; }
    public String getProductoId() { return productoId; }
    public Integer getCantidad() { return cantidad; }
    public Double getPrecio() { return precio; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public EstadoPedido getEstado() { return estado; }
}
