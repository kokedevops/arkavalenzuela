package com.arka.arkavalenzuela.cotizador.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CotizacionResponse {
    private String cotizacionId;
    private String clienteId;
    private List<ProductoCotizado> productos;
    private BigDecimal subtotal;
    private BigDecimal descuentos;
    private BigDecimal impuestos;
    private BigDecimal total;
    private LocalDateTime fechaCotizacion;
    private LocalDateTime fechaVencimiento;
    private String estado;
    private String observaciones;
    private String condicionesPago;
    private Integer tiempoEntrega;

    public CotizacionResponse() {}

    public CotizacionResponse(String cotizacionId, String clienteId, List<ProductoCotizado> productos, 
                             BigDecimal subtotal, BigDecimal descuentos, BigDecimal impuestos, BigDecimal total,
                             LocalDateTime fechaCotizacion, LocalDateTime fechaVencimiento, String estado,
                             String observaciones, String condicionesPago, Integer tiempoEntrega) {
        this.cotizacionId = cotizacionId;
        this.clienteId = clienteId;
        this.productos = productos;
        this.subtotal = subtotal;
        this.descuentos = descuentos;
        this.impuestos = impuestos;
        this.total = total;
        this.fechaCotizacion = fechaCotizacion;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.observaciones = observaciones;
        this.condicionesPago = condicionesPago;
        this.tiempoEntrega = tiempoEntrega;
    }

    // Getters y Setters
    public String getCotizacionId() {
        return cotizacionId;
    }

    public void setCotizacionId(String cotizacionId) {
        this.cotizacionId = cotizacionId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public List<ProductoCotizado> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoCotizado> productos) {
        this.productos = productos;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(BigDecimal descuentos) {
        this.descuentos = descuentos;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getFechaCotizacion() {
        return fechaCotizacion;
    }

    public void setFechaCotizacion(LocalDateTime fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCondicionesPago() {
        return condicionesPago;
    }

    public void setCondicionesPago(String condicionesPago) {
        this.condicionesPago = condicionesPago;
    }

    public Integer getTiempoEntrega() {
        return tiempoEntrega;
    }

    public void setTiempoEntrega(Integer tiempoEntrega) {
        this.tiempoEntrega = tiempoEntrega;
    }
}
