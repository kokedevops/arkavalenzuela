package com.arka.arkavalenzuela.service;

import com.arka.arkavalenzuela.model.Product;
import com.arka.arkavalenzuela.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product save(Product product) {
        return repository.save(product);
    }

    public Product findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Product> findByCategoria(String categoria) {
        return repository.findByCategoria(categoria);
    }
}
