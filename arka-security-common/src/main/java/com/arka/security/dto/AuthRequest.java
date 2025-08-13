package com.arka.security.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitudes de autenticación
 */
public class AuthRequest {
    
    @NotBlank(message = "El username o email es requerido")
    private String identifier; // Puede ser username o email
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
    
    private boolean rememberMe = false;
    
    // Constructores
    public AuthRequest() {}
    
    public AuthRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }
    
    public AuthRequest(String identifier, String password, boolean rememberMe) {
        this.identifier = identifier;
        this.password = password;
        this.rememberMe = rememberMe;
    }
    
    // Getters y Setters
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isRememberMe() {
        return rememberMe;
    }
    
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
    
    @Override
    public String toString() {
        return "AuthRequest{" +
                "identifier='" + identifier + '\'' +
                ", rememberMe=" + rememberMe +
                '}';
    }
}
