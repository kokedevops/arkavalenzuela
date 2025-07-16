package com.arka.domain.port.out;

import com.arka.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Category save(Category category);
    void deleteById(Long id);
    boolean existsById(Long id);
}
