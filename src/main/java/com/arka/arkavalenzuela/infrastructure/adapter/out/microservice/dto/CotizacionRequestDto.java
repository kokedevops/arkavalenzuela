package com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto;

import java.math.BigDecimal;
import java.util.List;

public class CotizacionRequestDto {
    private Long customerId;
    private List<ProductoCotizacionDto> productos;
    private String tipoCliente;
    
    public CotizacionRequestDto() {}
    
    public CotizacionRequestDto(Long customerId, List<ProductoCotizacionDto> productos, String tipoCliente) {
        this.customerId = customerId;
        this.productos = productos;
        this.tipoCliente = tipoCliente;
    }
    
    // Getters y Setters
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public List<ProductoCotizacionDto> getProductos() {
        return productos;
    }
    
    public void setProductos(List<ProductoCotizacionDto> productos) {
        this.productos = productos;
    }
    
    public String getTipoCliente() {
        return tipoCliente;
    }
    
    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }
    
    public static class ProductoCotizacionDto {
        private Long productId;
        private String nombre;
        private BigDecimal precioUnitario;
        private Integer cantidad;
        
        public ProductoCotizacionDto() {}
        
        public ProductoCotizacionDto(Long productId, String nombre, BigDecimal precioUnitario, Integer cantidad) {
            this.productId = productId;
            this.nombre = nombre;
            this.precioUnitario = precioUnitario;
            this.cantidad = cantidad;
        }
        
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
        
        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }
        
        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }
        
        public Integer getCantidad() {
            return cantidad;
        }
        
        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}
