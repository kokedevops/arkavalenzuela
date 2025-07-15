package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository;

import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartJpaRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByEstado(String estado);
}
