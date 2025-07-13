package com.arka.arkavalenzuela.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoriaId;

    private String nombre;

    @OneToMany(mappedBy = "categoria")
    private List<Product> productos;

    // Getters y setters
}
