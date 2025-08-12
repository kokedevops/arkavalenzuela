package com.arka.gestorsolicitudes.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para monitorear y controlar el estado de los Circuit Breakers
 */
@RestController
@RequestMapping("/api/circuit-breaker")
public class CircuitBreakerDemoController {
    
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    
    public CircuitBreakerDemoController(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }
    
    /**
     * Obtiene el estado de todos los Circuit Breakers
     */
    @GetMapping("/estado")
    public Mono<ResponseEntity<Map<String, Object>>> obtenerEstadoCircuitBreakers() {
        Map<String, Object> estados = new HashMap<>();
        
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            Map<String, Object> info = new HashMap<>();
            info.put("estado", cb.getState().toString());
            info.put("metricas", cb.getMetrics());
            info.put("configuracion", Map.of(
                "failureRateThreshold", cb.getCircuitBreakerConfig().getFailureRateThreshold(),
                "slidingWindowSize", cb.getCircuitBreakerConfig().getSlidingWindowSize(),
                "minimumNumberOfCalls", cb.getCircuitBreakerConfig().getMinimumNumberOfCalls()
            ));
            estados.put(cb.getName(), info);
        });
        
        return Mono.just(ResponseEntity.ok(estados));
    }
    
    /**
     * Obtiene el estado de un Circuit Breaker específico
     */
    @GetMapping("/estado/{nombre}")
    public Mono<ResponseEntity<Map<String, Object>>> obtenerEstadoCircuitBreaker(@PathVariable String nombre) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(nombre);
            
            Map<String, Object> info = new HashMap<>();
            info.put("nombre", nombre);
            info.put("estado", circuitBreaker.getState().toString());
            info.put("metricas", circuitBreaker.getMetrics());
            info.put("configuracion", Map.of(
                "failureRateThreshold", circuitBreaker.getCircuitBreakerConfig().getFailureRateThreshold(),
                "slidingWindowSize", circuitBreaker.getCircuitBreakerConfig().getSlidingWindowSize(),
                "minimumNumberOfCalls", circuitBreaker.getCircuitBreakerConfig().getMinimumNumberOfCalls()
            ));
            
            return Mono.just(ResponseEntity.ok(info));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.notFound().build());
        }
    }
    
    /**
     * Fuerza la transición del Circuit Breaker a estado OPEN (para pruebas)
     */
    @PostMapping("/forzar-apertura/{nombre}")
    public Mono<ResponseEntity<Map<String, String>>> forzarApertura(@PathVariable String nombre) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(nombre);
            circuitBreaker.transitionToOpenState();
            
            return Mono.just(ResponseEntity.ok(Map.of(
                "mensaje", "Circuit Breaker forzado a estado OPEN",
                "nombre", nombre,
                "estadoActual", circuitBreaker.getState().toString()
            )));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.badRequest()
                .body(Map.of("error", "No se pudo forzar apertura", "detalle", e.getMessage())));
        }
    }
    
    /**
     * Fuerza la transición del Circuit Breaker a estado CLOSED (para pruebas)
     */
    @PostMapping("/forzar-cierre/{nombre}")
    public Mono<ResponseEntity<Map<String, String>>> forzarCierre(@PathVariable String nombre) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(nombre);
            circuitBreaker.transitionToClosedState();
            
            return Mono.just(ResponseEntity.ok(Map.of(
                "mensaje", "Circuit Breaker forzado a estado CLOSED",
                "nombre", nombre,
                "estadoActual", circuitBreaker.getState().toString()
            )));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.badRequest()
                .body(Map.of("error", "No se pudo forzar cierre", "detalle", e.getMessage())));
        }
    }
    
    /**
     * Reinicia las métricas del Circuit Breaker
     */
    @PostMapping("/reiniciar-metricas/{nombre}")
    public Mono<ResponseEntity<Map<String, String>>> reiniciarMetricas(@PathVariable String nombre) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(nombre);
            circuitBreaker.reset();
            
            return Mono.just(ResponseEntity.ok(Map.of(
                "mensaje", "Métricas del Circuit Breaker reiniciadas",
                "nombre", nombre,
                "estadoActual", circuitBreaker.getState().toString()
            )));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.badRequest()
                .body(Map.of("error", "No se pudieron reiniciar las métricas", "detalle", e.getMessage())));
        }
    }
}
