package com.arka.gestorsolicitudes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo de dominio para el cálculo de envío
 */
public class CalculoEnvio {
    
    private String id;
    private String origen;
    private String destino;
    private BigDecimal peso;
    private String dimensiones;
    private BigDecimal costo;
    private Integer tiempoEstimadoDias;
    private EstadoCalculo estado;
    private LocalDateTime fechaCalculo;
    private String proveedorUtilizado;
    private String mensajeError;
    
    public CalculoEnvio() {}
    
    public CalculoEnvio(String origen, String destino, BigDecimal peso, String dimensiones) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.dimensiones = dimensiones;
        this.estado = EstadoCalculo.PENDIENTE;
        this.fechaCalculo = LocalDateTime.now();
    }
    
    // Métodos de conveniencia
    public static CalculoEnvio exitoso(String id, BigDecimal costo, Integer tiempoEstimado, String proveedor) {
        CalculoEnvio calculo = new CalculoEnvio();
        calculo.id = id;
        calculo.costo = costo;
        calculo.tiempoEstimadoDias = tiempoEstimado;
        calculo.estado = EstadoCalculo.COMPLETADO;
        calculo.proveedorUtilizado = proveedor;
        calculo.fechaCalculo = LocalDateTime.now();
        return calculo;
    }
    
    public static CalculoEnvio fallback(String mensaje) {
        CalculoEnvio calculo = new CalculoEnvio();
        calculo.estado = EstadoCalculo.FALLBACK;
        calculo.mensajeError = mensaje;
        calculo.costo = BigDecimal.valueOf(50.0); // Costo por defecto
        calculo.tiempoEstimadoDias = 7; // Tiempo por defecto
        calculo.proveedorUtilizado = "SERVICIO_BACKUP";
        calculo.fechaCalculo = LocalDateTime.now();
        return calculo;
    }
    
    public static CalculoEnvio error(String mensaje) {
        CalculoEnvio calculo = new CalculoEnvio();
        calculo.estado = EstadoCalculo.ERROR;
        calculo.mensajeError = mensaje;
        calculo.fechaCalculo = LocalDateTime.now();
        return calculo;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    
    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }
    
    public String getDimensiones() { return dimensiones; }
    public void setDimensiones(String dimensiones) { this.dimensiones = dimensiones; }
    
    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }
    
    public Integer getTiempoEstimadoDias() { return tiempoEstimadoDias; }
    public void setTiempoEstimadoDias(Integer tiempoEstimadoDias) { this.tiempoEstimadoDias = tiempoEstimadoDias; }
    
    public EstadoCalculo getEstado() { return estado; }
    public void setEstado(EstadoCalculo estado) { this.estado = estado; }
    
    public LocalDateTime getFechaCalculo() { return fechaCalculo; }
    public void setFechaCalculo(LocalDateTime fechaCalculo) { this.fechaCalculo = fechaCalculo; }
    
    public String getProveedorUtilizado() { return proveedorUtilizado; }
    public void setProveedorUtilizado(String proveedorUtilizado) { this.proveedorUtilizado = proveedorUtilizado; }
    
    public String getMensajeError() { return mensajeError; }
    public void setMensajeError(String mensajeError) { this.mensajeError = mensajeError; }
}
