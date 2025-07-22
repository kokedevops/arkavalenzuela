package com.arka.arkavalenzuela.cotizador.domain.model;

import java.math.BigDecimal;

public class ProductoSolicitado {
    private Long productoId;
    private Integer cantidad;
    private BigDecimal precioBase;
    private String observaciones;

    public ProductoSolicitado() {}

    public ProductoSolicitado(Long productoId, Integer cantidad, BigDecimal precioBase, String observaciones) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioBase = precioBase;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
