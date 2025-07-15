package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence;

import com.arka.arkavalenzuela.domain.model.Order;
import com.arka.arkavalenzuela.domain.model.Product;
import com.arka.arkavalenzuela.domain.model.Customer;
import com.arka.arkavalenzuela.domain.port.out.OrderRepositoryPort;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.OrderEntity;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper.OrderMapper;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper.ProductMapper;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper.CustomerMapper;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository repository;
    private final OrderMapper mapper;
    private final ProductMapper productMapper;
    private final CustomerMapper customerMapper;

    public OrderPersistenceAdapter(OrderJpaRepository repository, OrderMapper mapper,
                                   ProductMapper productMapper, CustomerMapper customerMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.productMapper = productMapper;
        this.customerMapper = customerMapper;
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Order> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Order> findByProductosContaining(Product product) {
        return repository.findByProductosContaining(productMapper.toEntity(product)).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByFechaBetween(LocalDateTime start, LocalDateTime end) {
        return repository.findByFechaBetween(start, end).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByCliente(Customer cliente) {
        return repository.findByCliente(customerMapper.toEntity(cliente)).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
