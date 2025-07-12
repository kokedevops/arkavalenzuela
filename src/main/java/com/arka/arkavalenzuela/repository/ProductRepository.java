package com.arka.arkavalenzuela.repository;

import com.arka.arkavalenzuela.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Ejemplo de consulta personalizada:
    List<Product> findByCategoria(String categoria);

    List<Product> findByNombreContainingIgnoreCase(String nombre);
}
