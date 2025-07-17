package com.arka.gestorsolicitudes.domain.model;

public class ItemSolicitud {
    private String productoId;
    private Integer cantidad;
    private String especificaciones;
    
    public ItemSolicitud() {}
    
    public ItemSolicitud(String productoId, Integer cantidad, String especificaciones) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.especificaciones = especificaciones;
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
    
    public String getEspecificaciones() {
        return especificaciones;
    }
    
    public void setEspecificaciones(String especificaciones) {
        this.especificaciones = especificaciones;
    }
}
