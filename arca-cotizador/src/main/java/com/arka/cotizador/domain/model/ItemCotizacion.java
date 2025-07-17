package com.arka.cotizador.domain.model;

import java.math.BigDecimal;

public class ItemCotizacion {
    private String productoId;
    private Integer cantidad;
    
    public ItemCotizacion() {}
    
    public ItemCotizacion(String productoId, Integer cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
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
}
