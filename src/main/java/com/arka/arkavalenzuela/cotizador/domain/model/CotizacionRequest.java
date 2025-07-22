package com.arka.arkavalenzuela.cotizador.domain.model;

import java.util.List;

public class CotizacionRequest {
    private String clienteId;
    private List<ProductoSolicitado> productos;
    private String tipoCliente;
    private String observaciones;

    public CotizacionRequest() {}

    public CotizacionRequest(String clienteId, List<ProductoSolicitado> productos, String tipoCliente, String observaciones) {
        this.clienteId = clienteId;
        this.productos = productos;
        this.tipoCliente = tipoCliente;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public List<ProductoSolicitado> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoSolicitado> productos) {
        this.productos = productos;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
