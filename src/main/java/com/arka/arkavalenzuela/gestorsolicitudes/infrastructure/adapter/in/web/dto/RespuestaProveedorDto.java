package com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RespuestaProveedorDto {
    private String respuestaId;
    private String solicitudId;
    private String proveedorId;
    private String estado;
    private BigDecimal precioOfertado;
    private Integer tiempoEntrega;
    private String condicionesPago;
    private String garantia;
    private String observaciones;
    private LocalDateTime fechaRespuesta;
    private LocalDateTime fechaVencimiento;
    private Boolean aceptada;

    public RespuestaProveedorDto() {}

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getPrecioOfertado() {
        return precioOfertado;
    }

    public void setPrecioOfertado(BigDecimal precioOfertado) {
        this.precioOfertado = precioOfertado;
    }

    public Integer getTiempoEntrega() {
        return tiempoEntrega;
    }

    public void setTiempoEntrega(Integer tiempoEntrega) {
        this.tiempoEntrega = tiempoEntrega;
    }

    public String getCondicionesPago() {
        return condicionesPago;
    }

    public void setCondicionesPago(String condicionesPago) {
        this.condicionesPago = condicionesPago;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
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

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getAceptada() {
        return aceptada;
    }

    public void setAceptada(Boolean aceptada) {
        this.aceptada = aceptada;
    }
}
