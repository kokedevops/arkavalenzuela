package com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CotizacionResponseDto {
    private String cotizacionId;
    private Long customerId;
    private BigDecimal totalOriginal;
    private BigDecimal descuentoAplicado;
    private BigDecimal totalConDescuento;
    private String tipoDescuento;
    private LocalDateTime fechaCotizacion;
    private LocalDateTime fechaVencimiento;
    private String estado;
    private List<ProductoCotizadoDto> productos;
    
    public CotizacionResponseDto() {}
    
    // Getters y Setters
    public String getCotizacionId() {
        return cotizacionId;
    }
    
    public void setCotizacionId(String cotizacionId) {
        this.cotizacionId = cotizacionId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public BigDecimal getTotalOriginal() {
        return totalOriginal;
    }
    
    public void setTotalOriginal(BigDecimal totalOriginal) {
        this.totalOriginal = totalOriginal;
    }
    
    public BigDecimal getDescuentoAplicado() {
        return descuentoAplicado;
    }
    
    public void setDescuentoAplicado(BigDecimal descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }
    
    public BigDecimal getTotalConDescuento() {
        return totalConDescuento;
    }
    
    public void setTotalConDescuento(BigDecimal totalConDescuento) {
        this.totalConDescuento = totalConDescuento;
    }
    
    public String getTipoDescuento() {
        return tipoDescuento;
    }
    
    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
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
    
    public List<ProductoCotizadoDto> getProductos() {
        return productos;
    }
    
    public void setProductos(List<ProductoCotizadoDto> productos) {
        this.productos = productos;
    }
    
    public static class ProductoCotizadoDto {
        private Long productId;
        private String nombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuentoUnitario;
        private BigDecimal precioFinal;
        private BigDecimal subtotal;
        
        public ProductoCotizadoDto() {}
        
        // Getters y Setters
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public String getNombre() {
            return nombre;
        }
        
        public void setNombre(String nombre) {
            this.nombre = nombre;
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
        
        public BigDecimal getDescuentoUnitario() {
            return descuentoUnitario;
        }
        
        public void setDescuentoUnitario(BigDecimal descuentoUnitario) {
            this.descuentoUnitario = descuentoUnitario;
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
    }
}
