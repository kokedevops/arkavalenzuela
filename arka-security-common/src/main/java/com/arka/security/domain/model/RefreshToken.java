package com.arka.security.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad RefreshToken para manejo de tokens de actualización
 */
@Table("refresh_tokens")
public class RefreshToken {
    
    @Id
    private Long id;
    
    @Column("token")
    private String token;
    
    @Column("usuario_id")
    private Long usuarioId;
    
    @Column("fecha_expiracion")
    private LocalDateTime fechaExpiracion;
    
    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column("activo")
    private boolean activo = true;
    
    @Column("ip_address")
    private String ipAddress;
    
    @Column("user_agent")
    private String userAgent;
    
    // Constructores
    public RefreshToken() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    public RefreshToken(Long usuarioId, int diasExpiracion) {
        this();
        this.usuarioId = usuarioId;
        this.token = UUID.randomUUID().toString();
        this.fechaExpiracion = LocalDateTime.now().plusDays(diasExpiracion);
    }
    
    public RefreshToken(Long usuarioId, int diasExpiracion, String ipAddress, String userAgent) {
        this(usuarioId, diasExpiracion);
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
    
    // Factory methods
    public static RefreshToken crear(Long usuarioId, int diasExpiracion) {
        return new RefreshToken(usuarioId, diasExpiracion);
    }
    
    public static RefreshToken crearConDetalles(Long usuarioId, int diasExpiracion, String ipAddress, String userAgent) {
        return new RefreshToken(usuarioId, diasExpiracion, ipAddress, userAgent);
    }
    
    // Métodos de negocio
    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }
    
    public void revocar() {
        this.activo = false;
    }
    
    public void renovar(int diasExpiracion) {
        this.fechaExpiracion = LocalDateTime.now().plusDays(diasExpiracion);
        this.token = UUID.randomUUID().toString();
    }
    
    public boolean esValido() {
        return activo && !estaExpirado();
    }
    
    public long diasParaExpiracion() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), fechaExpiracion);
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }
    
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    // equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id) && Objects.equals(token, that.token);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, token);
    }
    
    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", fechaExpiracion=" + fechaExpiracion +
                ", activo=" + activo +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
