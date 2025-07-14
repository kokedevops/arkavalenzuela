package com.arka.arkavalenzuela.repository;

import com.arka.arkavalenzuela.model.Order;
import com.arka.arkavalenzuela.model.Product;
import com.arka.arkavalenzuela.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByProductosContaining(Product product);

    List<Order> findByFechaBetween(LocalDateTime start, LocalDateTime end);

    List<Order> findByCliente(Customer cliente);
}
