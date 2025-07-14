package com.arka.arkavalenzuela.repository;

import com.arka.arkavalenzuela.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Consultas personalizadas:
    List<Product> findByCategoriaNombre(String categoriaNombre);

    List<Product> findByNombreContainingIgnoreCase(String nombre);

}
