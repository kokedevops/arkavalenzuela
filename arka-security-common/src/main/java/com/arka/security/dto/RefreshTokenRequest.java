package com.arka.security.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitudes de refresh token
 */
public class RefreshTokenRequest {
    
    @NotBlank(message = "El refresh token es requerido")
    private String refreshToken;
    
    // Constructores
    public RefreshTokenRequest() {}
    
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getters y Setters
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    @Override
    public String toString() {
        return "RefreshTokenRequest{" +
                "refreshToken='***'" + // No mostrar el token completo en logs
                '}';
    }
}
