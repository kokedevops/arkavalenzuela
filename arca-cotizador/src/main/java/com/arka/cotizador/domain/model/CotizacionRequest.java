package com.arka.cotizador.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CotizacionRequest {
    private String clienteId;
    private List<ItemCotizacion> items;
    private LocalDateTime fechaSolicitud;
    
    public CotizacionRequest() {}
    
    public CotizacionRequest(String clienteId, List<ItemCotizacion> items) {
        this.clienteId = clienteId;
        this.items = items;
        this.fechaSolicitud = LocalDateTime.now();
    }
    
    // Getters y Setters
    public String getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }
    
    public List<ItemCotizacion> getItems() {
        return items;
    }
    
    public void setItems(List<ItemCotizacion> items) {
        this.items = items;
    }
    
    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }
    
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
}
