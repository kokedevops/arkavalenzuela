package com.arka.arkavalenzuela.domain.service;

import com.arka.arkavalenzuela.domain.model.Category;
import com.arka.arkavalenzuela.domain.port.in.CategoryUseCase;
import com.arka.arkavalenzuela.domain.port.out.CategoryRepositoryPort;

import java.util.List;

public class CategoryDomainService implements CategoryUseCase {
    
    private final CategoryRepositoryPort categoryRepository;

    public CategoryDomainService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Override
    public Category createCategory(Category category) {
        validateCategory(category);
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        validateCategory(category);
        category.setId(id);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private void validateCategory(Category category) {
        if (!category.isValidName()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
    }
}
