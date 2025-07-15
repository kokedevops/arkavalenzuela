package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence;

import com.arka.arkavalenzuela.domain.model.Category;
import com.arka.arkavalenzuela.domain.port.out.CategoryRepositoryPort;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper.CategoryMapper;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository.CategoryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository repository;
    private final CategoryMapper mapper;

    public CategoryPersistenceAdapter(CategoryJpaRepository repository, CategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = mapper.toEntity(category);
        CategoryEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
