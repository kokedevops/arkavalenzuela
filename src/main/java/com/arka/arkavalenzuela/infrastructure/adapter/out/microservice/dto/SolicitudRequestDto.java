package com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto;

import java.util.List;

public class SolicitudRequestDto {
    private Long customerId;
    private String tipoSolicitud;
    private String prioridad;
    private List<ProductoSolicitudDto> productos;
    private String observaciones;
    
    public SolicitudRequestDto() {}
    
    public SolicitudRequestDto(Long customerId, String tipoSolicitud, String prioridad, 
                              List<ProductoSolicitudDto> productos, String observaciones) {
        this.customerId = customerId;
        this.tipoSolicitud = tipoSolicitud;
        this.prioridad = prioridad;
        this.productos = productos;
        this.observaciones = observaciones;
    }
    
    // Getters y Setters
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
    
    public String getPrioridad() {
        return prioridad;
    }
    
    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
    
    public List<ProductoSolicitudDto> getProductos() {
        return productos;
    }
    
    public void setProductos(List<ProductoSolicitudDto> productos) {
        this.productos = productos;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public static class ProductoSolicitudDto {
        private Long productId;
        private String nombre;
        private Integer cantidadSolicitada;
        private String especificaciones;
        
        public ProductoSolicitudDto() {}
        
        public ProductoSolicitudDto(Long productId, String nombre, Integer cantidadSolicitada, String especificaciones) {
            this.productId = productId;
            this.nombre = nombre;
            this.cantidadSolicitada = cantidadSolicitada;
            this.especificaciones = especificaciones;
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
        
        public Integer getCantidadSolicitada() {
            return cantidadSolicitada;
        }
        
        public void setCantidadSolicitada(Integer cantidadSolicitada) {
            this.cantidadSolicitada = cantidadSolicitada;
        }
        
        public String getEspecificaciones() {
            return especificaciones;
        }
        
        public void setEspecificaciones(String especificaciones) {
            this.especificaciones = especificaciones;
        }
    }
}
