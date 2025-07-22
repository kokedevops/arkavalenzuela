package com.arka.arkavalenzuela.gestorsolicitudes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Proveedor {
    private String proveedorId;
    private String nombre;
    private String nit;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
    private String ciudad;
    private String pais;
    private String estado;
    private BigDecimal calificacion;
    private String tiempoRespuestaPromedio;
    private LocalDateTime fechaRegistro;
    private String observaciones;

    public Proveedor() {}

    public Proveedor(String proveedorId, String nombre, String nit, String contacto, String telefono,
                    String email, String direccion, String ciudad, String pais, String estado,
                    BigDecimal calificacion, String tiempoRespuestaPromedio, LocalDateTime fechaRegistro, String observaciones) {
        this.proveedorId = proveedorId;
        this.nombre = nombre;
        this.nit = nit;
        this.contacto = contacto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.pais = pais;
        this.estado = estado;
        this.calificacion = calificacion;
        this.tiempoRespuestaPromedio = tiempoRespuestaPromedio;
        this.fechaRegistro = fechaRegistro;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public String getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(String proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(BigDecimal calificacion) {
        this.calificacion = calificacion;
    }

    public String getTiempoRespuestaPromedio() {
        return tiempoRespuestaPromedio;
    }

    public void setTiempoRespuestaPromedio(String tiempoRespuestaPromedio) {
        this.tiempoRespuestaPromedio = tiempoRespuestaPromedio;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
