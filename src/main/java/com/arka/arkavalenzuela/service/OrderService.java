package com.arka.arkavalenzuela.service;

import com.arka.arkavalenzuela.model.Order;
import com.arka.arkavalenzuela.model.Product;
import com.arka.arkavalenzuela.model.Customer;
import com.arka.arkavalenzuela.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Order save(Order order) {
        return repository.save(order);
    }

    public Order findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Order> findByProduct(Product product) {
        return repository.findByProductosContaining(product);
    }

    public List<Order> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return repository.findByFechaBetween(start, end);
    }

    public List<Order> findByCustomer(Customer customer) {
        return repository.findByCliente(customer);
    }
}
