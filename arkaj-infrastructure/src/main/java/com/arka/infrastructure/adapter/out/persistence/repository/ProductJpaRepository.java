package com.arka.infrastructure.adapter.out.persistence.repository;

import com.arka.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCategoriaNombre(String categoriaNombre);

    List<ProductEntity> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT p FROM ProductEntity p WHERE p.precioUnitario BETWEEN :min AND :max")
    List<ProductEntity> findByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
}
