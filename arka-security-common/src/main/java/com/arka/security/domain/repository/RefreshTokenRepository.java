package com.arka.security.domain.repository;

import com.arka.security.domain.model.RefreshToken;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para RefreshToken
 */
@Repository
public interface RefreshTokenRepository extends R2dbcRepository<RefreshToken, Long> {
    
    /**
     * Busca un refresh token por su valor
     */
    Mono<RefreshToken> findByToken(String token);
    
    /**
     * Busca tokens activos de un usuario
     */
    @Query("SELECT * FROM refresh_tokens WHERE usuario_id = :usuarioId AND activo = true ORDER BY fecha_creacion DESC")
    Flux<RefreshToken> findActiveTokensByUsuarioId(Long usuarioId);
    
    /**
     * Revoca todos los tokens de un usuario
     */
    @Query("UPDATE refresh_tokens SET activo = false WHERE usuario_id = :usuarioId")
    Mono<Void> revokeAllTokensByUsuarioId(Long usuarioId);
    
    /**
     * Elimina tokens expirados
     */
    @Query("DELETE FROM refresh_tokens WHERE fecha_expiracion < CURRENT_TIMESTAMP")
    Mono<Void> deleteExpiredTokens();
    
    /**
     * Busca token válido por token y usuario
     */
    @Query("SELECT * FROM refresh_tokens WHERE token = :token AND usuario_id = :usuarioId AND activo = true AND fecha_expiracion > CURRENT_TIMESTAMP")
    Mono<RefreshToken> findValidTokenByTokenAndUsuarioId(String token, Long usuarioId);
    
    /**
     * Cuenta tokens activos de un usuario
     */
    @Query("SELECT COUNT(*) FROM refresh_tokens WHERE usuario_id = :usuarioId AND activo = true")
    Mono<Long> countActiveTokensByUsuarioId(Long usuarioId);
    
    /**
     * Busca tokens por IP address
     */
    @Query("SELECT * FROM refresh_tokens WHERE ip_address = :ipAddress AND activo = true")
    Flux<RefreshToken> findActiveTokensByIpAddress(String ipAddress);
}
