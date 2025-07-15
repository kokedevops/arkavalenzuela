package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository;

import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
}
