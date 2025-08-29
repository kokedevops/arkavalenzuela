package com.arka.arkavalenzuela.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

/**
 * Controlador de autenticación para la aplicación principal ARKA
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Endpoint para verificar el estado de autenticación
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            response.put("authenticated", true);
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities());
        } else {
            response.put("authenticated", false);
            response.put("username", null);
            response.put("authorities", null);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para login básico (usando HTTP Basic Auth)
     * Este endpoint simplemente verifica si las credenciales son correctas
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        
        if (auth != null && auth.isAuthenticated()) {
            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities());
        } else {
            response.put("success", false);
            response.put("message", "Credenciales inválidas");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        SecurityContextHolder.clearContext();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint público para obtener información de usuarios demo
     */
    @GetMapping("/demo-users")
    public ResponseEntity<Map<String, Object>> getDemoUsers() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, String> users = new HashMap<>();
        users.put("admin", "admin123 (ADMIN, USER roles)");
        users.put("user", "user123 (USER role)");
        users.put("demo", "demo123 (USER role)");
        
        response.put("demoUsers", users);
        response.put("instruction", "Use HTTP Basic Auth with these credentials");
        
        return ResponseEntity.ok(response);
    }
}
