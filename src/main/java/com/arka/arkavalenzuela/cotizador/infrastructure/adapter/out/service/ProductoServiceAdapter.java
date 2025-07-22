package com.arka.arkavalenzuela.cotizador.infrastructure.adapter.out.service;

import com.arka.arkavalenzuela.cotizador.domain.port.out.ProductoServicePort;
import com.arka.arkavalenzuela.domain.model.Product;
import com.arka.arkavalenzuela.domain.port.out.ProductRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceAdapter implements ProductoServicePort {

    private final ProductRepositoryPort productRepository;

    public ProductoServiceAdapter(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> obtenerProductosPorIds(List<Long> productoIds) {
        return productoIds.stream()
                .map(productRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean verificarDisponibilidad(Long productoId, Integer cantidad) {
        Optional<Product> productOpt = productRepository.findById(productoId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return product.getStock() != null && product.getStock() >= cantidad;
        }
        return false;
    }
}
