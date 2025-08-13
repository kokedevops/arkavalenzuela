package com.arka.security.service;

import com.arka.security.domain.model.RefreshToken;
import com.arka.security.domain.model.Usuario;
import com.arka.security.domain.repository.RefreshTokenRepository;
import com.arka.security.domain.repository.UsuarioRepository;
import com.arka.security.dto.AuthRequest;
import com.arka.security.dto.AuthResponse;
import com.arka.security.dto.RefreshTokenRequest;
import com.arka.security.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Servicio de autenticación y autorización
 */
@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Registra un nuevo usuario
     */
    public Mono<AuthResponse> register(RegisterRequest request) {
        return usuarioRepository.existsByUsername(request.getUsername())
                .flatMap(usernameExists -> {
                    if (usernameExists) {
                        return Mono.error(new RuntimeException("El username ya existe"));
                    }
                    
                    return usuarioRepository.existsByEmail(request.getEmail())
                            .flatMap(emailExists -> {
                                if (emailExists) {
                                    return Mono.error(new RuntimeException("El email ya está registrado"));
                                }
                                
                                // Crear nuevo usuario
                                Usuario usuario = new Usuario(
                                        request.getUsername(),
                                        request.getEmail(),
                                        passwordEncoder.encode(request.getPassword()),
                                        request.getNombreCompleto(),
                                        Usuario.Rol.USUARIO // Por defecto usuarios regulares
                                );
                                
                                return usuarioRepository.save(usuario)
                                        .flatMap(this::generateAuthResponse);
                            });
                });
    }
    
    /**
     * Autentica un usuario
     */
    public Mono<AuthResponse> authenticate(AuthRequest request) {
        return usuarioRepository.findByUsernameOrEmail(request.getIdentifier())
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(usuario -> {
                    if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
                        return Mono.error(new RuntimeException("Credenciales incorrectas"));
                    }
                    
                    if (!usuario.isActivo()) {
                        return Mono.error(new RuntimeException("Usuario inactivo"));
                    }
                    
                    // Actualizar último acceso
                    usuario.actualizarUltimoAcceso();
                    return usuarioRepository.save(usuario)
                            .flatMap(this::generateAuthResponse);
                });
    }
    
    /**
     * Refresca el token usando refresh token
     */
    public Mono<AuthResponse> refreshToken(RefreshTokenRequest request) {
        return refreshTokenRepository.findByToken(request.getRefreshToken())
                .switchIfEmpty(Mono.error(new RuntimeException("Refresh token no válido")))
                .flatMap(refreshToken -> {
                    if (!refreshToken.esValido()) {
                        return Mono.error(new RuntimeException("Refresh token expirado o inválido"));
                    }
                    
                    return usuarioRepository.findById(refreshToken.getUsuarioId())
                            .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                            .flatMap(usuario -> {
                                if (!usuario.isActivo()) {
                                    return Mono.error(new RuntimeException("Usuario inactivo"));
                                }
                                
                                // Generar nuevo access token
                                String newAccessToken = jwtService.generateToken(usuario);
                                
                                // Opcionalmente, generar nuevo refresh token
                                String newRefreshToken = jwtService.generateRefreshToken(usuario);
                                
                                // Revocar el refresh token usado
                                refreshToken.revocar();
                                
                                // Crear nuevo refresh token
                                RefreshToken newRefreshTokenEntity = RefreshToken.crear(
                                        usuario.getId(), 
                                        7 // 7 días
                                );
                                newRefreshTokenEntity.setToken(newRefreshToken);
                                
                                return refreshTokenRepository.save(refreshToken)
                                        .then(refreshTokenRepository.save(newRefreshTokenEntity))
                                        .then(Mono.just(AuthResponse.builder()
                                                .accessToken(newAccessToken)
                                                .refreshToken(newRefreshToken)
                                                .tokenType("Bearer")
                                                .expiresIn(jwtService.getExpirationTime())
                                                .usuario(toUserInfo(usuario))
                                                .build()));
                            });
                });
    }
    
    /**
     * Cierra sesión revocando el refresh token
     */
    public Mono<Void> logout(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .flatMap(token -> {
                    token.revocar();
                    return refreshTokenRepository.save(token);
                })
                .then();
    }
    
    /**
     * Cierra todas las sesiones de un usuario
     */
    public Mono<Void> logoutAll(Long usuarioId) {
        return refreshTokenRepository.revokeAllTokensByUsuarioId(usuarioId);
    }
    
    /**
     * Genera respuesta de autenticación completa
     */
    private Mono<AuthResponse> generateAuthResponse(Usuario usuario) {
        String accessToken = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);
        
        RefreshToken refreshTokenEntity = RefreshToken.crear(usuario.getId(), 7); // 7 días
        refreshTokenEntity.setToken(refreshToken);
        
        return refreshTokenRepository.save(refreshTokenEntity)
                .then(Mono.just(AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(jwtService.getExpirationTime())
                        .usuario(toUserInfo(usuario))
                        .build()));
    }
    
    /**
     * Convierte Usuario a información pública
     */
    private AuthResponse.UserInfo toUserInfo(Usuario usuario) {
        return AuthResponse.UserInfo.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol().name())
                .rolDescripcion(usuario.getRol().getDescripcion())
                .permisos(usuario.getRol().getPermisos())
                .activo(usuario.isActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .fechaUltimoAcceso(usuario.getFechaUltimoAcceso())
                .build();
    }
    
    /**
     * Valida un token de acceso
     */
    public Mono<Boolean> validateToken(String token, Usuario usuario) {
        return Mono.fromCallable(() -> jwtService.isTokenValid(token, usuario));
    }
    
    /**
     * Obtiene información del usuario desde el token
     */
    public Mono<Usuario> getUserFromToken(String token) {
        return Mono.fromCallable(() -> jwtService.extractUsername(token))
                .flatMap(usuarioRepository::findByUsername);
    }
    
    /**
     * Limpia tokens expirados (tarea de mantenimiento)
     */
    public Mono<Void> cleanupExpiredTokens() {
        return refreshTokenRepository.deleteExpiredTokens();
    }
}
