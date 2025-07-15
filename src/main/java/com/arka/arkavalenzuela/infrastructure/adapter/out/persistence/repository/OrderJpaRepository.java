package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository;

import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.OrderEntity;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByProductosContaining(ProductEntity product);

    List<OrderEntity> findByFechaBetween(LocalDateTime start, LocalDateTime end);

    List<OrderEntity> findByCliente(CustomerEntity cliente);
}
