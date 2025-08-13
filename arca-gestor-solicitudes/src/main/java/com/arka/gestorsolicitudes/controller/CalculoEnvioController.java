package com.arka.gestorsolicitudes.controller;

import com.arka.gestorsolicitudes.application.service.CalculoEnvioService;
import com.arka.gestorsolicitudes.cli.CircuitBreakerCLIUtils;
import com.arka.gestorsolicitudes.domain.model.CalculoEnvio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controlador REST para el cálculo de envíos con Circuit Breaker
 * Aplicando seguridad basada en roles
 */
@RestController
@RequestMapping("/api/calculos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CalculoEnvioController {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculoEnvioController.class);
    
    private final CalculoEnvioService calculoEnvioService;
    private final CircuitBreakerCLIUtils cliUtils;
    
    public CalculoEnvioController(CalculoEnvioService calculoEnvioService, CircuitBreakerCLIUtils cliUtils) {
        this.calculoEnvioService = calculoEnvioService;
        this.cliUtils = cliUtils;
    }
    
    /**
     * Endpoint principal para calcular envío - Todos los roles autenticados
     */
    @PostMapping("/envio")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GESTOR', 'OPERADOR', 'USUARIO')")
    public Mono<ResponseEntity<CalculoEnvio>> calcularEnvio(
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Name") String username) {
        
        logger.info("Usuario {} (ID: {}) solicita cálculo de envío: {}", username, userId, request);
        
        String origen = (String) request.get("origen");
        String destino = (String) request.get("destino");
        BigDecimal peso = new BigDecimal(request.get("peso").toString());
        String dimensiones = (String) request.getOrDefault("dimensiones", "50x30x20");
        
        return calculoEnvioService.calcularEnvio(origen, destino, peso, dimensiones)
            .map(ResponseEntity::ok)
            .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    /**
     * Endpoint CLI para ejecutar pruebas de carga
     */
    @PostMapping("/cli/prueba-carga")
    public Mono<ResponseEntity<String>> ejecutarPruebaDeCarga(@RequestBody Map<String, Object> request) {
        logger.info("Ejecutando prueba de carga CLI: {}", request);
        
        int numLlamadas = Integer.parseInt(request.getOrDefault("llamadas", "10").toString());
        String escenario = (String) request.getOrDefault("escenario", "externo");
        
        return Mono.fromCallable(() -> cliUtils.ejecutarPruebaDeCarga(numLlamadas, escenario))
            .map(resultado -> ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(resultado))
            .onErrorReturn(ResponseEntity.internalServerError()
                .body("Error ejecutando prueba de carga"));
    }
    
    /**
     * Endpoint CLI para generar reporte de estado
     */
    @GetMapping("/cli/reporte-estado")
    public Mono<ResponseEntity<String>> generarReporteEstado() {
        return Mono.fromCallable(() -> cliUtils.generarReporteEstado())
            .map(reporte -> ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(reporte))
            .onErrorReturn(ResponseEntity.internalServerError()
                .body("Error generando reporte"));
    }
    
    /**
     * Endpoint CLI para ejecutar demostración completa
     */
    @PostMapping("/cli/demostracion")
    public Mono<ResponseEntity<String>> ejecutarDemostracion() {
        logger.info("Ejecutando demostración completa de Circuit Breaker");
        
        return Mono.fromCallable(() -> cliUtils.ejecutarDemostracion())
            .map(demo -> ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(demo))
            .onErrorReturn(ResponseEntity.internalServerError()
                .body("Error ejecutando demostración"));
    }
    
    /**
     * Endpoint para obtener el estado del servicio
     */
    @GetMapping("/estado")
    public Mono<ResponseEntity<Map<String, String>>> obtenerEstado() {
        return calculoEnvioService.obtenerEstadoCalculos()
            .map(estado -> ResponseEntity.ok(Map.of("estado", estado, "servicio", "calculo-envio")));
    }
    
    /**
     * Endpoint para probar diferentes escenarios de Circuit Breaker
     */
    @PostMapping("/probar-circuit-breaker")
    public Mono<ResponseEntity<CalculoEnvio>> probarCircuitBreaker(@RequestBody Map<String, Object> request) {
        logger.info("Probando Circuit Breaker con request: {}", request);
        
        String escenario = (String) request.get("escenario");
        String origen = (String) request.getOrDefault("origen", "Lima");
        String destino = (String) request.getOrDefault("destino", "Arequipa");
        BigDecimal peso = new BigDecimal(request.getOrDefault("peso", "1.5").toString());
        
        return calculoEnvioService.probarCircuitBreaker(escenario, origen, destino, peso)
            .map(ResponseEntity::ok)
            .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    /**
     * Endpoint simple para pruebas rápidas
     */
    @GetMapping("/prueba-rapida")
    public Mono<ResponseEntity<CalculoEnvio>> pruebaRapida(
            @RequestParam(defaultValue = "Lima") String origen,
            @RequestParam(defaultValue = "Cusco") String destino,
            @RequestParam(defaultValue = "2.0") BigDecimal peso) {
        
        logger.info("Prueba rápida: {} -> {}, peso: {}", origen, destino, peso);
        
        return calculoEnvioService.calcularEnvio(origen, destino, peso, "40x30x20")
            .map(ResponseEntity::ok)
            .onErrorReturn(ResponseEntity.internalServerError().build());
    }
}
