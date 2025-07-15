package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence;

import com.arka.arkavalenzuela.domain.model.Product;
import com.arka.arkavalenzuela.domain.port.out.ProductRepositoryPort;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper.ProductMapper;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository repository;
    private final ProductMapper mapper;

    public ProductPersistenceAdapter(ProductJpaRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Product> findByCategoriaNombre(String categoriaNombre) {
        return repository.findByCategoriaNombre(categoriaNombre).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNombreContainingIgnoreCase(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return repository.findByPriceRange(min, max).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
