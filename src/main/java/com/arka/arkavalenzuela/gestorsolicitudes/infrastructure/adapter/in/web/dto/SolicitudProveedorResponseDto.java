package com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SolicitudProveedorResponseDto {
    private String solicitudId;
    private String proveedorId;
    private String tipoSolicitud;
    private List<SolicitudProveedorRequestDto.ProductoSolicitadoDto> productos;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;
    private String observaciones;
    private String prioridad;

    public SolicitudProveedorResponseDto() {}

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

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public List<SolicitudProveedorRequestDto.ProductoSolicitadoDto> getProductos() {
        return productos;
    }

    public void setProductos(List<SolicitudProveedorRequestDto.ProductoSolicitadoDto> productos) {
        this.productos = productos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
}
