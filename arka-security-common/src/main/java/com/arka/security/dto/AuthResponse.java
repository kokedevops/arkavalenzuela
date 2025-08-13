package com.arka.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de autenticación
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserInfo usuario;
    
    // Constructores
    public AuthResponse() {}
    
    private AuthResponse(Builder builder) {
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;
        this.tokenType = builder.tokenType;
        this.expiresIn = builder.expiresIn;
        this.usuario = builder.usuario;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private long expiresIn;
        private UserInfo usuario;
        
        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
        
        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }
        
        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }
        
        public Builder expiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }
        
        public Builder usuario(UserInfo usuario) {
            this.usuario = usuario;
            return this;
        }
        
        public AuthResponse build() {
            return new AuthResponse(this);
        }
    }
    
    // UserInfo inner class
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String nombreCompleto;
        private String rol;
        private String rolDescripcion;
        private List<String> permisos;
        private boolean activo;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaUltimoAcceso;
        
        // Constructores
        public UserInfo() {}
        
        private UserInfo(UserInfoBuilder builder) {
            this.id = builder.id;
            this.username = builder.username;
            this.email = builder.email;
            this.nombreCompleto = builder.nombreCompleto;
            this.rol = builder.rol;
            this.rolDescripcion = builder.rolDescripcion;
            this.permisos = builder.permisos;
            this.activo = builder.activo;
            this.fechaCreacion = builder.fechaCreacion;
            this.fechaUltimoAcceso = builder.fechaUltimoAcceso;
        }
        
        public static UserInfoBuilder builder() {
            return new UserInfoBuilder();
        }
        
        public static class UserInfoBuilder {
            private Long id;
            private String username;
            private String email;
            private String nombreCompleto;
            private String rol;
            private String rolDescripcion;
            private List<String> permisos;
            private boolean activo;
            private LocalDateTime fechaCreacion;
            private LocalDateTime fechaUltimoAcceso;
            
            public UserInfoBuilder id(Long id) {
                this.id = id;
                return this;
            }
            
            public UserInfoBuilder username(String username) {
                this.username = username;
                return this;
            }
            
            public UserInfoBuilder email(String email) {
                this.email = email;
                return this;
            }
            
            public UserInfoBuilder nombreCompleto(String nombreCompleto) {
                this.nombreCompleto = nombreCompleto;
                return this;
            }
            
            public UserInfoBuilder rol(String rol) {
                this.rol = rol;
                return this;
            }
            
            public UserInfoBuilder rolDescripcion(String rolDescripcion) {
                this.rolDescripcion = rolDescripcion;
                return this;
            }
            
            public UserInfoBuilder permisos(List<String> permisos) {
                this.permisos = permisos;
                return this;
            }
            
            public UserInfoBuilder activo(boolean activo) {
                this.activo = activo;
                return this;
            }
            
            public UserInfoBuilder fechaCreacion(LocalDateTime fechaCreacion) {
                this.fechaCreacion = fechaCreacion;
                return this;
            }
            
            public UserInfoBuilder fechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
                this.fechaUltimoAcceso = fechaUltimoAcceso;
                return this;
            }
            
            public UserInfo build() {
                return new UserInfo(this);
            }
        }
        
        // Getters
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getNombreCompleto() { return nombreCompleto; }
        public String getRol() { return rol; }
        public String getRolDescripcion() { return rolDescripcion; }
        public List<String> getPermisos() { return permisos; }
        public boolean isActivo() { return activo; }
        public LocalDateTime getFechaCreacion() { return fechaCreacion; }
        public LocalDateTime getFechaUltimoAcceso() { return fechaUltimoAcceso; }
    }
    
    // Getters y Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public UserInfo getUsuario() {
        return usuario;
    }
    
    public void setUsuario(UserInfo usuario) {
        this.usuario = usuario;
    }
}
