package com.arka.gateway.config;

import com.arka.security.service.JwtService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

/**
 * Filtro global de autenticación JWT para el API Gateway
 * TEMPORALMENTE DESACTIVADO para permitir arranque sin dependencias de seguridad
 */
//@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    //@Autowired
    private JwtService jwtService;
    
    // Rutas que no requieren autenticación
    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/refresh",
            "/actuator/health",
            "/actuator/info",
            "/eureka"
    );
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Verificar si la ruta está en la lista de endpoints abiertos
        if (isOpenEndpoint(request.getPath().value())) {
            return chain.filter(exchange);
        }
        
        // Extraer el token del header Authorization
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Token de autorización requerido", HttpStatus.UNAUTHORIZED);
        }
        
        String token = authHeader.substring(7);
        
        try {
            // Validar el token
            if (jwtService.isTokenExpired(token)) {
                return onError(exchange, "Token expirado", HttpStatus.UNAUTHORIZED);
            }
            
            // Extraer información del usuario
            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);
            String userRole = jwtService.extractUserRole(token);
            
            // Agregar headers con información del usuario para los microservicios
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Name", username)
                    .header("X-User-Role", userRole)
                    .build();
            
            // Continuar con la cadena de filtros
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
            
        } catch (Exception e) {
            return onError(exchange, "Token inválido", HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * Verifica si un endpoint está en la lista de endpoints abiertos
     */
    private boolean isOpenEndpoint(String path) {
        return OPEN_API_ENDPOINTS.stream()
                .anyMatch(openPath -> path.contains(openPath));
    }
    
    /**
     * Maneja errores de autenticación
     */
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        
        String errorResponse = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"status\":%d}", 
                httpStatus.getReasonPhrase(), 
                err, 
                httpStatus.value()
        );
        
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes()))
        );
    }
    
    @Override
    public int getOrder() {
        return -1; // Ejecutar antes que otros filtros
    }
}
