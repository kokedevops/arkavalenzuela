package com.arka.arkavalenzuela.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Arka Valenzuela");
        health.put("version", "1.0.0");
        health.put("port", 8082);
        
        Map<String, String> microservices = new HashMap<>();
        microservices.put("cotizador", "http://localhost:8080");
        microservices.put("gestor-solicitudes", "http://localhost:8081");
        health.put("connectedServices", microservices);
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Arka Valenzuela - Sistema Principal");
        info.put("description", "Sistema de gestión de productos con integración de microservicios");
        info.put("architecture", "Hexagonal Architecture + Microservices");
        
        Map<String, String> features = new HashMap<>();
        features.put("products", "Gestión de productos y categorías");
        features.put("customers", "Gestión de clientes");
        features.put("orders", "Gestión de pedidos");
        features.put("quotations", "Integración con servicio de cotizaciones");
        features.put("requests", "Integración con gestor de solicitudes");
        info.put("features", features);
        
        return ResponseEntity.ok(info);
    }
}
