package com.arka.arkavalenzuela.infrastructure.adapter.in.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Endpoint para login con JSON
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Usar "identifier" o "username" como campo de usuario
            String username = request.getIdentifier() != null ? request.getIdentifier() : request.getUsername();
            
            if (username == null || request.getPassword() == null) {
                response.put("success", false);
                response.put("message", "Username/identifier and password are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Autenticar con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities());
            
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            response.put("success", false);
            response.put("message", "Credenciales inválidas");
            return ResponseEntity.status(401).body(response);
        }
    }

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
        response.put("instruction", "POST /api/auth/login with JSON: {\"identifier\":\"admin\", \"password\":\"admin123\"}");
        response.put("alternative", "Use HTTP Basic Auth with these credentials");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clase para el request de login
     */
    public static class LoginRequest {
        private String identifier;
        private String username;
        private String password;
        
        // Getters y Setters
        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
