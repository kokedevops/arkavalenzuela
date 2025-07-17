package com.arka.gestorsolicitudes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RespuestaProveedor {
    private String respuestaId;
    private String solicitudId;
    private String proveedorId;
    private List<ItemRespuesta> items;
    private BigDecimal totalOferta;
    private String observaciones;
    private LocalDateTime fechaRespuesta;
    private String estado;
    
    public RespuestaProveedor() {}
    
    public RespuestaProveedor(String respuestaId, String solicitudId, String proveedorId, List<ItemRespuesta> items, BigDecimal totalOferta) {
        this.respuestaId = respuestaId;
        this.solicitudId = solicitudId;
        this.proveedorId = proveedorId;
        this.items = items;
        this.totalOferta = totalOferta;
        this.fechaRespuesta = LocalDateTime.now();
        this.estado = "RECIBIDA";
    }
    
    // Getters y Setters
    public String getRespuestaId() {
        return respuestaId;
    }
    
    public void setRespuestaId(String respuestaId) {
        this.respuestaId = respuestaId;
    }
    
    public String getSolicitudId() {
        return solicitudId;
    }
    
    public void setSolicitudId(String solicitudId) {
        this.solicitudId = solicitudId;
    }
    
    public String getProveedorId() {
        return proveedorId;
    }
    
    public void setProveedorId(String proveedorId) {
        this.proveedorId = proveedorId;
    }
    
    public List<ItemRespuesta> getItems() {
        return items;
    }
    
    public void setItems(List<ItemRespuesta> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalOferta() {
        return totalOferta;
    }
    
    public void setTotalOferta(BigDecimal totalOferta) {
        this.totalOferta = totalOferta;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }
    
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
