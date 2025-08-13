package com.arka.gestorsolicitudes.infrastructure.controller;

import com.arka.security.dto.AuthRequest;
import com.arka.security.dto.AuthResponse;
import com.arka.security.dto.RefreshTokenRequest;
import com.arka.security.dto.RegisterRequest;
import com.arka.security.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para autenticación y autorización
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Registra un nuevo usuario
     */
    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(Exception.class, e -> 
                    Mono.just(ResponseEntity.badRequest()
                            .body(AuthResponse.builder()
                                    .build()))
                );
    }
    
    /**
     * Autentica un usuario
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        return authService.authenticate(request)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(Exception.class, e -> 
                    Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(AuthResponse.builder()
                                    .build()))
                );
    }
    
    /**
     * Refresca el token de acceso
     */
    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(Exception.class, e -> 
                    Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(AuthResponse.builder()
                                    .build()))
                );
    }
    
    /**
     * Cierra sesión (revoca refresh token)
     */
    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.logout(request.getRefreshToken())
                .then(Mono.just(ResponseEntity.ok("Sesión cerrada exitosamente")))
                .onErrorResume(Exception.class, e -> 
                    Mono.just(ResponseEntity.badRequest().body("Error al cerrar sesión"))
                );
    }
    
    /**
     * Cierra todas las sesiones de un usuario (requiere estar autenticado)
     */
    @PostMapping("/logout-all")
    public Mono<ResponseEntity<String>> logoutAll(@RequestHeader("X-User-Id") Long userId) {
        return authService.logoutAll(userId)
                .then(Mono.just(ResponseEntity.ok("Todas las sesiones cerradas exitosamente")))
                .onErrorResume(Exception.class, e -> 
                    Mono.just(ResponseEntity.badRequest().body("Error al cerrar sesiones"))
                );
    }
    
    /**
     * Valida el token actual (endpoint de utilidad)
     */
    @GetMapping("/validate")
    public Mono<ResponseEntity<String>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido"));
        }
        
        // El filtro JWT ya validó el token si llegamos aquí
        return Mono.just(ResponseEntity.ok("Token válido"));
    }
    
    /**
     * Obtiene información del usuario actual
     */
    @GetMapping("/me")
    public Mono<ResponseEntity<AuthResponse.UserInfo>> getCurrentUser(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String username,
            @RequestHeader("X-User-Role") String role) {
        
        // Construir información del usuario desde los headers
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(userId)
                .username(username)
                .rol(role)
                .build();
        
        return Mono.just(ResponseEntity.ok(userInfo));
    }
}
