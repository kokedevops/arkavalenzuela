package com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto;

import java.time.LocalDateTime;

public class SolicitudResponseDto {
    private String solicitudId;
    private Long customerId;
    private String tipoSolicitud;
    private String estado;
    private String prioridad;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaVencimiento;
    private String observaciones;
    private String respuestaProveedor;
    
    public SolicitudResponseDto() {}
    
    // Getters y Setters
    public String getSolicitudId() {
        return solicitudId;
    }
    
    public void setSolicitudId(String solicitudId) {
        this.solicitudId = solicitudId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getTipoSolicitud() {
        return tipoSolicitud;
    }
    
    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getPrioridad() {
        return prioridad;
    }
    
    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public String getRespuestaProveedor() {
        return respuestaProveedor;
    }
    
    public void setRespuestaProveedor(String respuestaProveedor) {
        this.respuestaProveedor = respuestaProveedor;
    }
}
