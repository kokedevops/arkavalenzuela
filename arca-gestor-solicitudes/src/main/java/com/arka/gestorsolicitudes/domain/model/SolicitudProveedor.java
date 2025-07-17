package com.arka.gestorsolicitudes.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class SolicitudProveedor {
    private String solicitudId;
    private String proveedorId;
    private String clienteId;
    private List<ItemSolicitud> items;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaRespuesta;
    
    public SolicitudProveedor() {}
    
    public SolicitudProveedor(String solicitudId, String proveedorId, String clienteId, List<ItemSolicitud> items) {
        this.solicitudId = solicitudId;
        this.proveedorId = proveedorId;
        this.clienteId = clienteId;
        this.items = items;
        this.estado = "PENDIENTE";
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Getters y Setters
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
    
    public String getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }
    
    public List<ItemSolicitud> getItems() {
        return items;
    }
    
    public void setItems(List<ItemSolicitud> items) {
        this.items = items;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }
    
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}
