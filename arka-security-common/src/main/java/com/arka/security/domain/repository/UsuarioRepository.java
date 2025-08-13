package com.arka.security.domain.repository;

import com.arka.security.domain.model.Usuario;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para Usuario
 */
@Repository
public interface UsuarioRepository extends R2dbcRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por su username
     */
    Mono<Usuario> findByUsername(String username);
    
    /**
     * Busca un usuario por su email
     */
    Mono<Usuario> findByEmail(String email);
    
    /**
     * Busca un usuario por username o email
     */
    @Query("SELECT * FROM usuarios WHERE (username = :identifier OR email = :identifier) AND activo = true")
    Mono<Usuario> findByUsernameOrEmail(String identifier);
    
    /**
     * Verifica si existe un usuario con el username dado
     */
    Mono<Boolean> existsByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el email dado
     */
    Mono<Boolean> existsByEmail(String email);
    
    /**
     * Busca usuarios por rol
     */
    @Query("SELECT * FROM usuarios WHERE rol = :rol AND activo = true")
    reactor.core.publisher.Flux<Usuario> findByRol(Usuario.Rol rol);
    
    /**
     * Cuenta usuarios activos
     */
    @Query("SELECT COUNT(*) FROM usuarios WHERE activo = true")
    Mono<Long> countActiveUsers();
    
    /**
     * Busca usuarios activos
     */
    @Query("SELECT * FROM usuarios WHERE activo = true ORDER BY fecha_creacion DESC")
    reactor.core.publisher.Flux<Usuario> findAllActive();
    
    /**
     * Actualiza la fecha de último acceso
     */
    @Query("UPDATE usuarios SET fecha_ultimo_acceso = CURRENT_TIMESTAMP WHERE id = :id")
    Mono<Void> updateLastAccess(Long id);
}
