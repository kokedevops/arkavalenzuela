package com.arka.arkavalenzuela.gestorsolicitudes.domain.model;

import java.math.BigDecimal;

public class ProductoSolicitado {
    private Long productoId;
    private String nombreProducto;
    private Integer cantidadSolicitada;
    private BigDecimal precioReferencia;
    private String especificaciones;
    private String observaciones;
    private Boolean urgente;

    public ProductoSolicitado() {}

    public ProductoSolicitado(Long productoId, String nombreProducto, Integer cantidadSolicitada,
                             BigDecimal precioReferencia, String especificaciones, String observaciones, Boolean urgente) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidadSolicitada = cantidadSolicitada;
        this.precioReferencia = precioReferencia;
        this.especificaciones = especificaciones;
        this.observaciones = observaciones;
        this.urgente = urgente;
    }

    // Getters y Setters
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public void setCantidadSolicitada(Integer cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public BigDecimal getPrecioReferencia() {
        return precioReferencia;
    }

    public void setPrecioReferencia(BigDecimal precioReferencia) {
        this.precioReferencia = precioReferencia;
    }

    public String getEspecificaciones() {
        return especificaciones;
    }

    public void setEspecificaciones(String especificaciones) {
        this.especificaciones = especificaciones;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getUrgente() {
        return urgente;
    }

    public void setUrgente(Boolean urgente) {
        this.urgente = urgente;
    }
}
