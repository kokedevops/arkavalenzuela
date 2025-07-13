package com.arka.arkavalenzuela.repository;

import com.arka.arkavalenzuela.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
