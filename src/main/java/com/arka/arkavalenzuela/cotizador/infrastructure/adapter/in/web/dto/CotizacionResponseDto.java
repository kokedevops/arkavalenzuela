package com.arka.arkavalenzuela.cotizador.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CotizacionResponseDto {
    private String cotizacionId;
    private String clienteId;
    private List<ProductoCotizadoDto> productos;
    private BigDecimal subtotal;
    private BigDecimal descuentos;
    private BigDecimal impuestos;
    private BigDecimal total;
    private LocalDateTime fechaCotizacion;
    private LocalDateTime fechaVencimiento;
    private String estado;
    private String observaciones;
    private String condicionesPago;
    private Integer tiempoEntrega;

    public CotizacionResponseDto() {}

    // Getters y Setters
    public String getCotizacionId() {
        return cotizacionId;
    }

    public void setCotizacionId(String cotizacionId) {
        this.cotizacionId = cotizacionId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public List<ProductoCotizadoDto> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoCotizadoDto> productos) {
        this.productos = productos;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(BigDecimal descuentos) {
        this.descuentos = descuentos;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getFechaCotizacion() {
        return fechaCotizacion;
    }

    public void setFechaCotizacion(LocalDateTime fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCondicionesPago() {
        return condicionesPago;
    }

    public void setCondicionesPago(String condicionesPago) {
        this.condicionesPago = condicionesPago;
    }

    public Integer getTiempoEntrega() {
        return tiempoEntrega;
    }

    public void setTiempoEntrega(Integer tiempoEntrega) {
        this.tiempoEntrega = tiempoEntrega;
    }

    public static class ProductoCotizadoDto {
        private Long productoId;
        private String nombre;
        private String descripcion;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
        private BigDecimal precioFinal;
        private BigDecimal subtotal;
        private String observaciones;
        private Boolean disponible;
        private Integer tiempoEntrega;

        public ProductoCotizadoDto() {}

        // Getters y Setters
        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public BigDecimal getDescuento() {
            return descuento;
        }

        public void setDescuento(BigDecimal descuento) {
            this.descuento = descuento;
        }

        public BigDecimal getPrecioFinal() {
            return precioFinal;
        }

        public void setPrecioFinal(BigDecimal precioFinal) {
            this.precioFinal = precioFinal;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }

        public Boolean getDisponible() {
            return disponible;
        }

        public void setDisponible(Boolean disponible) {
            this.disponible = disponible;
        }

        public Integer getTiempoEntrega() {
            return tiempoEntrega;
        }

        public void setTiempoEntrega(Integer tiempoEntrega) {
            this.tiempoEntrega = tiempoEntrega;
        }
    }
}
