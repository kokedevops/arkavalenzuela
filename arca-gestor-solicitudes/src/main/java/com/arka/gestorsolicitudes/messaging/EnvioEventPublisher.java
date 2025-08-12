package com.arka.gestorsolicitudes.messaging;

import com.arka.gestorsolicitudes.domain.model.CalculoEnvio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

/**
 * Servicio para publicar eventos de cálculo de envío usando Spring Cloud Stream
 */
@Service
public class EnvioEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(EnvioEventPublisher.class);
    
    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;
    
    public EnvioEventPublisher(StreamBridge streamBridge, ObjectMapper objectMapper) {
        this.streamBridge = streamBridge;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Publica un evento cuando se completa un cálculo de envío
     */
    public void publicarEventoCalculoCompletado(CalculoEnvio calculo) {
        try {
            EnvioEvent evento = new EnvioEvent(
                "CALCULO_COMPLETADO",
                calculo.getId(),
                calculo.getOrigen(),
                calculo.getDestino(),
                calculo.getCosto(),
                calculo.getTiempoEstimadoDias(),
                calculo.getEstado().toString(),
                calculo.getProveedorUtilizado()
            );
            
            boolean enviado = streamBridge.send("envio-events", evento);
            
            if (enviado) {
                logger.info("Evento de cálculo completado publicado: {}", evento);
            } else {
                logger.error("Error al publicar evento de cálculo completado: {}", evento);
            }
            
        } catch (Exception e) {
            logger.error("Error al crear evento de cálculo completado: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Publica un evento cuando se activa el Circuit Breaker
     */
    public void publicarEventoCircuitBreakerActivado(String servicioAfectado, String motivo) {
        try {
            CircuitBreakerEvent evento = new CircuitBreakerEvent(
                "CIRCUIT_BREAKER_ACTIVADO",
                servicioAfectado,
                motivo,
                System.currentTimeMillis()
            );
            
            boolean enviado = streamBridge.send("circuit-breaker-events", evento);
            
            if (enviado) {
                logger.warn("Evento de Circuit Breaker activado publicado: {}", evento);
            } else {
                logger.error("Error al publicar evento de Circuit Breaker: {}", evento);
            }
            
        } catch (Exception e) {
            logger.error("Error al crear evento de Circuit Breaker: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Publica métricas de rendimiento del sistema
     */
    public void publicarMetricasRendimiento(int llamadasExitosas, int llamadasFallidas, 
                                          double tiempoPromedioRespuesta) {
        try {
            MetricasEvent evento = new MetricasEvent(
                "METRICAS_RENDIMIENTO",
                llamadasExitosas,
                llamadasFallidas,
                tiempoPromedioRespuesta,
                System.currentTimeMillis()
            );
            
            boolean enviado = streamBridge.send("metricas-events", evento);
            
            if (enviado) {
                logger.debug("Métricas de rendimiento publicadas: {}", evento);
            } else {
                logger.error("Error al publicar métricas de rendimiento: {}", evento);
            }
            
        } catch (Exception e) {
            logger.error("Error al crear evento de métricas: {}", e.getMessage(), e);
        }
    }
}

/**
 * Evento de cálculo de envío
 */
record EnvioEvent(
    String tipo,
    String calculoId,
    String origen,
    String destino,
    java.math.BigDecimal costo,
    Integer tiempoEstimadoDias,
    String estado,
    String proveedor
) {}

/**
 * Evento de Circuit Breaker
 */
record CircuitBreakerEvent(
    String tipo,
    String servicio,
    String motivo,
    Long timestamp
) {}

/**
 * Evento de métricas
 */
record MetricasEvent(
    String tipo,
    Integer llamadasExitosas,
    Integer llamadasFallidas,
    Double tiempoPromedioRespuesta,
    Long timestamp
) {}
