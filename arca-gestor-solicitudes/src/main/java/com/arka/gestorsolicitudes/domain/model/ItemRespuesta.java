package com.arka.gestorsolicitudes.domain.model;

import java.math.BigDecimal;

public class ItemRespuesta {
    private String productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private Integer tiempoEntregaDias;
    private String disponibilidad;
    
    public ItemRespuesta() {}
    
    public ItemRespuesta(String productoId, Integer cantidad, BigDecimal precioUnitario, Integer tiempoEntregaDias) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        this.tiempoEntregaDias = tiempoEntregaDias;
        this.disponibilidad = "DISPONIBLE";
    }
    
    // Getters y Setters
    public String getProductoId() {
        return productoId;
    }
    
    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public Integer getTiempoEntregaDias() {
        return tiempoEntregaDias;
    }
    
    public void setTiempoEntregaDias(Integer tiempoEntregaDias) {
        this.tiempoEntregaDias = tiempoEntregaDias;
    }
    
    public String getDisponibilidad() {
        return disponibilidad;
    }
    
    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}
