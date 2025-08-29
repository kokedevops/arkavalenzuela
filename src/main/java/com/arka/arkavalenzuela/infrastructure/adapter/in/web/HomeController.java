package com.arka.arkavalenzuela.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador principal para la página de inicio de la aplicación ARKA
 */
@RestController
@RequestMapping("/")
public class HomeController {

    /**
     * Página de inicio de la aplicación
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("application", "ARKA E-commerce Platform");
        response.put("version", "1.0.0");
        response.put("status", "Running");
        response.put("port", "8888");
        response.put("environment", "AWS");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Health Check", "/health");
        endpoints.put("Authentication Status", "/api/auth/status");
        endpoints.put("Demo Users", "/api/auth/demo-users");
        endpoints.put("Products", "/api/products");
        endpoints.put("Categories", "/api/categories");
        endpoints.put("Customers", "/api/customers");
        endpoints.put("Orders", "/api/orders");
        endpoints.put("Cart", "/api/cart");
        endpoints.put("Analytics", "/api/analytics");
        endpoints.put("Cotización", "/api/cotizacion");
        
        response.put("availableEndpoints", endpoints);
        
        Map<String, String> authentication = new HashMap<>();
        authentication.put("method", "HTTP Basic Auth");
        authentication.put("users", "admin/admin123, user/user123, demo/demo123");
        authentication.put("example", "curl -u admin:admin123 http://localhost:8888/api/auth/status");
        
        response.put("authentication", authentication);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de salud
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("status", "UP");
        response.put("application", "arkajvalenzuela");
        response.put("port", "8888");
        response.put("timestamp", System.currentTimeMillis());
        
        Map<String, String> database = new HashMap<>();
        database.put("mysql", "Connected to AWS RDS");
        database.put("mongodb", "Connected to AWS DocumentDB");
        
        response.put("database", database);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Información de la API
     */
    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("api", "ARKA E-commerce API");
        response.put("version", "v1");
        response.put("documentation", "Available endpoints listed in root path /");
        response.put("authentication", "HTTP Basic Auth required for most endpoints");
        
        return ResponseEntity.ok(response);
    }
}
