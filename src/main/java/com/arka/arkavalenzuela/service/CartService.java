package com.arka.arkavalenzuela.service;

import com.arka.arkavalenzuela.model.Cart;
import com.arka.arkavalenzuela.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository repository;

    public CartService(CartRepository repository) {
        this.repository = repository;
    }

    public List<Cart> findAll() {
        return repository.findAll();
    }

    public Cart save(Cart cart) {
        return repository.save(cart);
    }

    public Cart findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Cart> findByEstado(String estado) {
        return repository.findByEstado(estado);
    }
}
