package com.arka.cotizador.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CotizacionResponse {
    private String cotizacionId;
    private String clienteId;
    private List<ItemCotizado> items;
    private BigDecimal totalCotizacion;
    private LocalDateTime fechaCotizacion;
    private LocalDateTime fechaVencimiento;
    private String estado;
    
    public CotizacionResponse() {}
    
    public CotizacionResponse(String cotizacionId, String clienteId, List<ItemCotizado> items, BigDecimal totalCotizacion) {
        this.cotizacionId = cotizacionId;
        this.clienteId = clienteId;
        this.items = items;
        this.totalCotizacion = totalCotizacion;
        this.fechaCotizacion = LocalDateTime.now();
        this.fechaVencimiento = LocalDateTime.now().plusDays(30);
        this.estado = "ACTIVA";
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
    
    public List<ItemCotizado> getItems() {
        return items;
    }
    
    public void setItems(List<ItemCotizado> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalCotizacion() {
        return totalCotizacion;
    }
    
    public void setTotalCotizacion(BigDecimal totalCotizacion) {
        this.totalCotizacion = totalCotizacion;
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
}
