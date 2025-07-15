package com.arka.arkavalenzuela.domain.port.out;

import com.arka.arkavalenzuela.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
    List<Product> findByCategoriaNombre(String categoriaNombre);
    List<Product> findByNombreContainingIgnoreCase(String nombre);
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
    boolean existsById(Long id);
}
