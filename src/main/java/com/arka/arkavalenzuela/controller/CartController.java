package com.arka.arkavalenzuela.controller;

import com.arka.arkavalenzuela.model.Cart;
import com.arka.arkavalenzuela.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carritos")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/abandonados")
    public ResponseEntity<List<Cart>> getAbandonedCarts() {
        return ResponseEntity.ok(service.findByEstado("ABANDONED"));
    }
}
