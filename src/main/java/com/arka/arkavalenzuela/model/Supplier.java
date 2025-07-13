package com.arka.arkavalenzuela.model;

import jakarta.persistence.*;

@Entity
@Table(name = "proveedores")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proveedorId;

    private String nombre;
    private String emailContacto;
    private String telefono;
    private String pais;

    // Getters y setters
}
