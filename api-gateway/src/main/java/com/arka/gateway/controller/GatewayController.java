package com.arka.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador principal para el API Gateway
 */
@RestController
public class GatewayController {

    /**
     * Página de inicio del API Gateway
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("service", "ARKA API Gateway");
        response.put("version", "1.0.0");
        response.put("status", "Running");
        response.put("port", "8080");
        response.put("description", "API Gateway for ARKA E-commerce Microservices");
        
        Map<String, String> availableServices = new HashMap<>();
        availableServices.put("Authentication", "/auth/** -> arca-gestor-solicitudes");
        availableServices.put("Admin", "/api/admin/** -> arca-gestor-solicitudes");
        availableServices.put("Users", "/api/users/** -> arca-gestor-solicitudes");
        availableServices.put("Quotes", "/api/cotizacion/** -> arca-cotizador");
        availableServices.put("Main App", "Direct access on port 8888");
        
        response.put("availableServices", availableServices);
        
        Map<String, String> instructions = new HashMap<>();
        instructions.put("authentication", "POST /auth/login with {\"identifier\":\"admin\", \"password\":\"admin123\"}");
        instructions.put("mainApp", "Access main e-commerce app on port 8888");
        instructions.put("health", "GET /actuator/health for gateway health");
        
        response.put("instructions", instructions);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check del API Gateway
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("status", "UP");
        response.put("service", "api-gateway");
        response.put("port", "8080");
        response.put("timestamp", System.currentTimeMillis());
        
        Map<String, String> services = new HashMap<>();
        services.put("eureka", "Service discovery enabled");
        services.put("gateway", "Routes configured");
        services.put("loadBalancer", "Enabled");
        
        response.put("services", services);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Información de rutas del gateway
     */
    @GetMapping("/routes")
    public ResponseEntity<Map<String, Object>> routes() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("service", "api-gateway");
        response.put("description", "Available routes through the gateway");
        
        Map<String, Object> routes = new HashMap<>();
        
        Map<String, String> authRoutes = new HashMap<>();
        authRoutes.put("path", "/auth/**");
        authRoutes.put("service", "arca-gestor-solicitudes");
        authRoutes.put("description", "Authentication endpoints");
        authRoutes.put("example", "POST /auth/login");
        routes.put("authentication", authRoutes);
        
        Map<String, String> adminRoutes = new HashMap<>();
        adminRoutes.put("path", "/api/admin/**");
        adminRoutes.put("service", "arca-gestor-solicitudes");
        adminRoutes.put("description", "Admin management endpoints");
        adminRoutes.put("auth", "Required");
        routes.put("admin", adminRoutes);
        
        Map<String, String> userRoutes = new HashMap<>();
        userRoutes.put("path", "/api/users/**");
        userRoutes.put("service", "arca-gestor-solicitudes");
        userRoutes.put("description", "User management endpoints");
        userRoutes.put("auth", "Required");
        routes.put("users", userRoutes);
        
        Map<String, String> quotesRoutes = new HashMap<>();
        quotesRoutes.put("path", "/api/cotizacion/**");
        quotesRoutes.put("service", "arca-cotizador");
        quotesRoutes.put("description", "Quotation service endpoints");
        quotesRoutes.put("auth", "Required");
        routes.put("quotes", quotesRoutes);
        
        response.put("routes", routes);
        
        return ResponseEntity.ok(response);
    }
}
