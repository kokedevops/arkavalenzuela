package com.arka.arkavalenzuela.gestorsolicitudes.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class SolicitudProveedor {
    private String solicitudId;
    private String proveedorId;
    private String tipoSolicitud;
    private List<ProductoSolicitado> productos;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;
    private String observaciones;
    private String prioridad;

    public SolicitudProveedor() {}

    public SolicitudProveedor(String solicitudId, String proveedorId, String tipoSolicitud, 
                             List<ProductoSolicitado> productos, String estado, LocalDateTime fechaSolicitud,
                             LocalDateTime fechaRespuesta, String observaciones, String prioridad) {
        this.solicitudId = solicitudId;
        this.proveedorId = proveedorId;
        this.tipoSolicitud = tipoSolicitud;
        this.productos = productos;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaRespuesta = fechaRespuesta;
        this.observaciones = observaciones;
        this.prioridad = prioridad;
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

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public List<ProductoSolicitado> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoSolicitado> productos) {
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
