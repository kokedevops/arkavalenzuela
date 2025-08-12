package com.arka.gestorsolicitudes.application.service;

import com.arka.gestorsolicitudes.domain.model.CalculoEnvio;
import com.arka.gestorsolicitudes.infrastructure.adapter.external.ProveedorEnvioExternoService;
import com.arka.gestorsolicitudes.messaging.EnvioEventPublisher;
// import com.arka.gestorsolicitudes.aws.AWSIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final EnvioEventPublisher eventPublisher;
    // private final AWSIntegrationService awsService;
    private final ObjectMapper objectMapper;

    public CalculoEnvioService(ProveedorEnvioExternoService proveedorExternoService,
                               EnvioEventPublisher eventPublisher,
                               // @Autowired(required = false) AWSIntegrationService awsService,
                               ObjectMapper objectMapper) {
        this.proveedorExternoService = proveedorExternoService;
        this.eventPublisher = eventPublisher;
        // this.awsService = awsService;
        this.objectMapper = objectMapper;
    }/**
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
                // Publicar evento de Circuit Breaker activado
                eventPublisher.publicarEventoCircuitBreakerActivado("proveedor-externo-service", error.getMessage());
                
                return proveedorExternoService.calcularEnvioSimulado(origen, destino, peso)
                    .doOnNext(calculo -> {
                        calculo.setOrigen(origen);
                        calculo.setDestino(destino);
                        calculo.setPeso(peso);
                        calculo.setDimensiones(dimensiones);
                    });
            })
            .doOnSuccess(resultado -> {
                logger.info("Cálculo de envío completado. Estado: {}, Proveedor: {}", 
                    resultado.getEstado(), resultado.getProveedorUtilizado());
                
                // Publicar evento de cálculo completado
                eventPublisher.publicarEventoCalculoCompletado(resultado);
                
                // Guardar resultado en S3 si AWS está disponible
                // Commented out for testing without AWS dependencies
                /*
                if (awsService != null) {
                    try {
                        String json = objectMapper.writeValueAsString(resultado);
                        awsService.guardarCalculoEnS3(resultado.getId(), json);
                    } catch (Exception e) {
                        logger.error("Error al guardar en S3: {}", e.getMessage());
                    }
                    
                    // Enviar notificación a SQS
                    awsService.enviarNotificacionSQS(resultado.getId(), 
                        resultado.getEstado().toString(), 
                        "Cálculo de envío completado exitosamente");
                } else {
                    logger.debug("AWS Service no disponible - saltando respaldo S3 y notificación SQS");
                }
                */
                logger.debug("AWS integration disabled for testing - skipping S3 backup and SQS notification");
            })
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
