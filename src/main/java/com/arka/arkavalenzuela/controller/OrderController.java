package com.arka.arkavalenzuela.controller;

import com.arka.arkavalenzuela.model.Order;
import com.arka.arkavalenzuela.model.Product;
import com.arka.arkavalenzuela.model.Customer;
import com.arka.arkavalenzuela.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<List<Order>> getByProduct(@PathVariable Long id) {
        Product p = new Product();
        p.setProductoId(id);
        return ResponseEntity.ok(service.findByProduct(p));
    }

    @GetMapping("/rango")
    public ResponseEntity<List<Order>> getByDateRange(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDateTime s = LocalDateTime.parse(start);
        LocalDateTime e = LocalDateTime.parse(end);
        return ResponseEntity.ok(service.findByDateRange(s, e));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Order>> getByCustomer(@PathVariable Long clienteId) {
        Customer c = new Customer();
        c.setClienteId(clienteId);
        return ResponseEntity.ok(service.findByCustomer(c));
    }
}
