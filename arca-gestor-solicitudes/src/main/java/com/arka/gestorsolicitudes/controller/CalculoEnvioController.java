package com.arka.gestorsolicitudes.controller;

import com.arka.gestorsolicitudes.application.service.CalculoEnvioService;
import com.arka.gestorsolicitudes.domain.model.CalculoEnvio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controlador REST para el cálculo de envíos con Circuit Breaker
 */
@RestController
@RequestMapping("/api/calculo-envio")
public class CalculoEnvioController {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculoEnvioController.class);
    
    private final CalculoEnvioService calculoEnvioService;
    
    public CalculoEnvioController(CalculoEnvioService calculoEnvioService) {
        this.calculoEnvioService = calculoEnvioService;
    }
    
    /**
     * Endpoint principal para calcular envío
     */
    @PostMapping("/calcular")
    public Mono<ResponseEntity<CalculoEnvio>> calcularEnvio(@RequestBody Map<String, Object> request) {
        logger.info("Recibida solicitud de cálculo de envío: {}", request);
        
        String origen = (String) request.get("origen");
        String destino = (String) request.get("destino");
        BigDecimal peso = new BigDecimal(request.get("peso").toString());
        String dimensiones = (String) request.getOrDefault("dimensiones", "50x30x20");
        
        return calculoEnvioService.calcularEnvio(origen, destino, peso, dimensiones)
            .map(ResponseEntity::ok)
            .onErrorReturn(ResponseEntity.internalServerError().build());
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
