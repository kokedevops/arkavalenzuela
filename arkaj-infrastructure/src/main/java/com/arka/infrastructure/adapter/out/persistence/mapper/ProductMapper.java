package com.arka.infrastructure.adapter.out.persistence.mapper;

import com.arka.domain.model.Product;
import com.arka.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    public ProductMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Product product = new Product();
        product.setId(entity.getProductoId());
        product.setNombre(entity.getNombre());
        product.setDescripcion(entity.getDescripcion());
        product.setCategoria(categoryMapper.toDomain(entity.getCategoria()));
        product.setMarca(entity.getMarca());
        product.setPrecioUnitario(entity.getPrecioUnitario());
        product.setStock(entity.getStock());
        
        return product;
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        
        ProductEntity entity = new ProductEntity();
        entity.setProductoId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setCategoria(categoryMapper.toEntity(domain.getCategoria()));
        entity.setMarca(domain.getMarca());
        entity.setPrecioUnitario(domain.getPrecioUnitario());
        entity.setStock(domain.getStock());
        
        return entity;
    }
}
