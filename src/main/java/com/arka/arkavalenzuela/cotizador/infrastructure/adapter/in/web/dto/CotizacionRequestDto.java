package com.arka.arkavalenzuela.cotizador.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class CotizacionRequestDto {
    private String clienteId;
    private List<ProductoSolicitadoDto> productos;
    private String tipoCliente;
    private String observaciones;

    public CotizacionRequestDto() {}

    public CotizacionRequestDto(String clienteId, List<ProductoSolicitadoDto> productos, String tipoCliente, String observaciones) {
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

    public List<ProductoSolicitadoDto> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoSolicitadoDto> productos) {
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

    public static class ProductoSolicitadoDto {
        private Long productoId;
        private Integer cantidad;
        private BigDecimal precioBase;
        private String observaciones;

        public ProductoSolicitadoDto() {}

        public ProductoSolicitadoDto(Long productoId, Integer cantidad, BigDecimal precioBase, String observaciones) {
            this.productoId = productoId;
            this.cantidad = cantidad;
            this.precioBase = precioBase;
            this.observaciones = observaciones;
        }

        // Getters y Setters
        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioBase() {
            return precioBase;
        }

        public void setPrecioBase(BigDecimal precioBase) {
            this.precioBase = precioBase;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
    }
}
