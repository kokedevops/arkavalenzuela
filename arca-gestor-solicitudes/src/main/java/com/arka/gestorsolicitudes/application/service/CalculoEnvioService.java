package com.arka.gestorsolicitudes.application.service;

import com.arka.gestorsolicitudes.domain.model.CalculoEnvio;
import com.arka.gestorsolicitudes.domain.model.EstadoCalculo;
import com.arka.gestorsolicitudes.infrastructure.adapter.external.ProveedorEnvioExternoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Servicio de aplicación para gestionar el cálculo de envíos
 * Orquesta las llamadas a diferentes proveedores con fallbacks
 */
@Service
public class CalculoEnvioService {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculoEnvioService.class);
    
    private final ProveedorEnvioExternoService proveedorExternoService;
    
    public CalculoEnvioService(ProveedorEnvioExternoService proveedorExternoService) {
        this.proveedorExternoService = proveedorExternoService;
    }
    
    /**
     * Calcula el envío intentando primero el proveedor externo
     * Si falla, usa el servicio interno simulado
     */
    public Mono<CalculoEnvio> calcularEnvio(String origen, String destino, BigDecimal peso, String dimensiones) {
        logger.info("Iniciando cálculo de envío de {} a {} con peso {}", origen, destino, peso);
        
        return proveedorExternoService.calcularEnvioProveedorExterno(origen, destino, peso)
            .doOnNext(calculo -> {
                calculo.setOrigen(origen);
                calculo.setDestino(destino);
                calculo.setPeso(peso);
                calculo.setDimensiones(dimensiones);
            })
            .onErrorResume(error -> {
                logger.warn("Proveedor externo falló, intentando con servicio interno: {}", error.getMessage());
                return proveedorExternoService.calcularEnvioSimulado(origen, destino, peso)
                    .doOnNext(calculo -> {
                        calculo.setOrigen(origen);
                        calculo.setDestino(destino);
                        calculo.setPeso(peso);
                        calculo.setDimensiones(dimensiones);
                    });
            })
            .doOnSuccess(resultado -> 
                logger.info("Cálculo de envío completado. Estado: {}, Proveedor: {}", 
                    resultado.getEstado(), resultado.getProveedorUtilizado()))
            .doOnError(error -> 
                logger.error("Error en el cálculo de envío: {}", error.getMessage()));
    }
    
    /**
     * Obtiene el estado del cálculo de envío
     */
    public Mono<String> obtenerEstadoCalculos() {
        return Mono.just("Servicio de cálculo de envíos operativo");
    }
    
    /**
     * Método para probar diferentes escenarios de fallback
     */
    public Mono<CalculoEnvio> probarCircuitBreaker(String escenario, String origen, String destino, BigDecimal peso) {
        logger.info("Probando Circuit Breaker con escenario: {}", escenario);
        
        switch (escenario.toLowerCase()) {
            case "externo":
                return proveedorExternoService.calcularEnvioProveedorExterno(origen, destino, peso);
            case "interno":
                return proveedorExternoService.calcularEnvioSimulado(origen, destino, peso);
            case "completo":
                return calcularEnvio(origen, destino, peso, "100x50x30");
            default:
                return Mono.just(CalculoEnvio.error("Escenario no válido: " + escenario));
        }
    }
}
