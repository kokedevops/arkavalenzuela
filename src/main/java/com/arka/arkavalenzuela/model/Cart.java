package com.arka.arkavalenzuela.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "carritos")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carritoId;

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Customer cliente;

    private LocalDateTime fechaCreacion;

    private String estado; // ej.: ACTIVE, ABANDONED

    // Getters y setters
}
