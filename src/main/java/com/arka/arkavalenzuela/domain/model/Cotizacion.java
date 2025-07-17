package com.arka.arkavalenzuela.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public class Cotizacion {
    private String id;
    private String clienteId;
    private List<ProductoCotizado> productos;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private LocalDateTime fechaCotizacion;
    private LocalDateTime fechaValidez;
    private String estado;
    private String observaciones;
    
    public Cotizacion {}

    public Cotizacion(String id, String clienteId, List<ProductoCotizado> productos, BigDecimal subtotal, BigDecimal impuestos, BigDecimal total, 
    LocalDateTime fechaCotizacion, LocalDateTime fechaValidez, String estado, String observaciones) {

        this.id = id;
        this.clienteID = clienteID;
        this



    }









}
