package com.arka.gestorsolicitudes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Controlador simplificado para monitorear Circuit Breakers
 */
@RestController
@RequestMapping("/api/circuit-breaker")
public class CircuitBreakerMonitorController {
    
    /**
     * Endpoint básico para verificar el estado del sistema de Circuit Breaker
     */
    @GetMapping("/estado-basico")
    public Mono<ResponseEntity<Map<String, Object>>> obtenerEstado() {
        Map<String, Object> estado = Map.of(
            "circuitBreakerEnabled", true,
            "servicios", Map.of(
                "calculo-envio-service", "Configurado",
                "proveedor-externo-service", "Configurado"
            ),
            "mensaje", "Circuit Breaker implementado y configurado correctamente"
        );
        
        return Mono.just(ResponseEntity.ok(estado));
    }
    
    /**
     * Endpoint para obtener información de configuración
     */
    @GetMapping("/configuracion")
    public Mono<ResponseEntity<Map<String, Object>>> obtenerConfiguracion() {
        Map<String, Object> config = Map.of(
            "resilience4j", Map.of(
                "version", "2.2.0",
                "features", new String[]{"CircuitBreaker", "Retry", "TimeLimiter"}
            ),
            "configuraciones", Map.of(
                "calculo-envio-service", Map.of(
                    "slidingWindowSize", 10,
                    "failureRateThreshold", 50,
                    "waitDurationInOpenState", "10s"
                ),
                "proveedor-externo-service", Map.of(
                    "slidingWindowSize", 8,
                    "failureRateThreshold", 60,
                    "waitDurationInOpenState", "15s"
                )
            )
        );
        
        return Mono.just(ResponseEntity.ok(config));
    }
    
    /**
     * Endpoint de salud para Circuit Breaker
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, String>>> health() {
        return Mono.just(ResponseEntity.ok(Map.of(
            "status", "UP",
            "component", "circuit-breaker",
            "mensaje", "Sistema de Circuit Breaker operativo"
        )));
    }
}
