package com.arka.arkavalenzuela.domain.port.out;

import com.arka.arkavalenzuela.domain.model.Cart;
import java.util.List;
import java.util.Optional;

public interface CartRepositoryPort {
    List<Cart> findAll();
    Optional<Cart> findById(Long id);
    Cart save(Cart cart);
    void deleteById(Long id);
    List<Cart> findByEstado(String estado);
    boolean existsById(Long id);
}
