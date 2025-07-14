package com.arka.arkavalenzuela.repository;

import com.arka.arkavalenzuela.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByEstado(String estado);
}
