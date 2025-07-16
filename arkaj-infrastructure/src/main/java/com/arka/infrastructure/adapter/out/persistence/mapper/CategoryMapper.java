package com.arka.infrastructure.adapter.out.persistence.mapper;

import com.arka.domain.model.Category;
import com.arka.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Category category = new Category();
        category.setId(entity.getCategoriaId());
        category.setNombre(entity.getNombre());
        
        return category;
    }

    public CategoryEntity toEntity(Category domain) {
        if (domain == null) {
            return null;
        }
        
        CategoryEntity entity = new CategoryEntity();
        entity.setCategoriaId(domain.getId());
        entity.setNombre(domain.getNombre());
        
        return entity;
    }
}
