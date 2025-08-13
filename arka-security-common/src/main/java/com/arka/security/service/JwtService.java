package com.arka.security.service;

import com.arka.security.domain.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para manejo de JWT (JSON Web Tokens)
 */
@Service
public class JwtService {
    
    @Value("${arka.security.jwt.secret:ArkaSecretKeyForJWTTokenGenerationAndValidation2024!}")
    private String secretKey;
    
    @Value("${arka.security.jwt.expiration:86400}")  // 24 horas en segundos
    private long jwtExpiration;
    
    @Value("${arka.security.jwt.refresh-expiration:604800}")  // 7 días en segundos
    private long refreshExpiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    
    /**
     * Extrae el username del token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extrae el ID del usuario del token
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }
    
    /**
     * Extrae el rol del usuario del token
     */
    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    /**
     * Extrae la fecha de expiración del token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extrae un claim específico del token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extrae todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Verifica si el token está expirado
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Genera token para usuario
     */
    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId());
        claims.put("email", usuario.getEmail());
        claims.put("role", usuario.getRol().name());
        claims.put("nombreCompleto", usuario.getNombreCompleto());
        claims.put("permissions", usuario.getRol().getPermisos());
        return generateToken(claims, usuario.getUsername());
    }
    
    /**
     * Genera token con claims adicionales
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return buildToken(extraClaims, username, jwtExpiration);
    }
    
    /**
     * Genera refresh token
     */
    public String generateRefreshToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId());
        claims.put("type", "refresh");
        return buildToken(claims, usuario.getUsername(), refreshExpiration);
    }
    
    /**
     * Construye el token JWT
     */
    private String buildToken(Map<String, Object> extraClaims, String username, long expiration) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusSeconds(expiration);
        
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .expiration(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Valida el token
     */
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Valida el refresh token
     */
    public Boolean isRefreshTokenValid(String refreshToken, Usuario usuario) {
        try {
            final String username = extractUsername(refreshToken);
            final String type = extractClaim(refreshToken, claims -> claims.get("type", String.class));
            return username.equals(usuario.getUsername()) && 
                   "refresh".equals(type) && 
                   !isTokenExpired(refreshToken);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtiene el tiempo de expiración en segundos
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }
    
    /**
     * Obtiene el tiempo de expiración del refresh token en segundos
     */
    public long getRefreshExpirationTime() {
        return refreshExpiration;
    }
    
    /**
     * Verifica si el token tiene un permiso específico
     */
    public boolean hasPermission(String token, String permission) {
        try {
            Claims claims = extractAllClaims(token);
            @SuppressWarnings("unchecked")
            java.util.List<String> permissions = (java.util.List<String>) claims.get("permissions");
            return permissions != null && permissions.contains(permission);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extrae información del token para logging/auditoría
     */
    public Map<String, Object> getTokenInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Map<String, Object> info = new HashMap<>();
            info.put("username", claims.getSubject());
            info.put("userId", claims.get("userId"));
            info.put("role", claims.get("role"));
            info.put("issuedAt", claims.getIssuedAt());
            info.put("expiration", claims.getExpiration());
            info.put("expired", isTokenExpired(token));
            return info;
        } catch (Exception e) {
            return Map.of("error", "Invalid token");
        }
    }
}
