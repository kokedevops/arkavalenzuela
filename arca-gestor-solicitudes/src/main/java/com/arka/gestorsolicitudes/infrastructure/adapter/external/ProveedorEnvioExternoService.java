package com.arka.gestorsolicitudes.infrastructure.adapter.external;

import com.arka.gestorsolicitudes.domain.model.CalculoEnvio;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Random;

/**
 * Servicio para comunicación con proveedores externos de envío
 * Implementa Circuit Breaker para manejar fallos de servicios externos
 */
@Service
public class ProveedorEnvioExternoService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProveedorEnvioExternoService.class);
    private final WebClient webClient;
    private final Random random = new Random();
    
    public ProveedorEnvioExternoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:9090") // URL del servicio externo simulado
                .build();
    }
    
    /**
     * Calcula el costo y tiempo de envío usando un proveedor externo
     * Protegido por Circuit Breaker, Retry y TimeLimiter
     */
    @CircuitBreaker(name = "proveedor-externo-service", fallbackMethod = "fallbackCalculoProveedorExterno")
    @Retry(name = "calculo-envio-service")
    @TimeLimiter(name = "calculo-envio-service")
    public Mono<CalculoEnvio> calcularEnvioProveedorExterno(String origen, String destino, BigDecimal peso) {
        logger.info("Llamando al proveedor externo para calcular envío de {} a {} con peso {}", origen, destino, peso);
        
        return webClient.post()
                .uri("/api/calcular-envio")
                .bodyValue(Map.of(
                    "origen", origen,
                    "destino", destino,
                    "peso", peso
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    BigDecimal costo = new BigDecimal(response.get("costo").toString());
                    Integer tiempoEstimado = Integer.valueOf(response.get("tiempoEstimadoDias").toString());
                    String proveedor = response.get("proveedor").toString();
                    
                    return CalculoEnvio.exitoso(
                        java.util.UUID.randomUUID().toString(),
                        costo,
                        tiempoEstimado,
                        proveedor
                    );
                })
                .doOnSuccess(result -> logger.info("Cálculo exitoso del proveedor externo: {}", result.getProveedorUtilizado()))
                .doOnError(error -> logger.error("Error en proveedor externo: {}", error.getMessage()));
    }
    
    /**
     * Método fallback cuando el proveedor externo falla
     */
    public Mono<CalculoEnvio> fallbackCalculoProveedorExterno(String origen, String destino, BigDecimal peso, Exception ex) {
        logger.warn("Circuit Breaker activado para proveedor externo. Usando valores por defecto. Error: {}", ex.getMessage());
        
        // Lógica de fallback con cálculo básico
        BigDecimal costoBase = calcularCostoBasico(origen, destino, peso);
        Integer tiempoEstimado = calcularTiempoBasico(origen, destino);
        
        CalculoEnvio calculoFallback = CalculoEnvio.fallback(
            "Servicio externo no disponible. Usando cálculo básico interno."
        );
        calculoFallback.setOrigen(origen);
        calculoFallback.setDestino(destino);
        calculoFallback.setPeso(peso);
        calculoFallback.setCosto(costoBase);
        calculoFallback.setTiempoEstimadoDias(tiempoEstimado);
        
        return Mono.just(calculoFallback);
    }
    
    /**
     * Simulador de servicio interno como alternativa (para pruebas)
     */
    @CircuitBreaker(name = "calculo-envio-service", fallbackMethod = "fallbackCalculoInterno")
    public Mono<CalculoEnvio> calcularEnvioSimulado(String origen, String destino, BigDecimal peso) {
        logger.info("Usando servicio interno simulado para calcular envío");
        
        return Mono.fromCallable(() -> {
            // Simular posible fallo aleatorio para pruebas
            if (random.nextDouble() < 0.3) { // 30% probabilidad de fallo
                throw new RuntimeException("Fallo simulado del servicio interno");
            }
            
            // Simular delay de procesamiento
            Thread.sleep(random.nextInt(3000) + 1000); // 1-4 segundos
            
            BigDecimal costo = calcularCostoBasico(origen, destino, peso);
            Integer tiempoEstimado = calcularTiempoBasico(origen, destino);
            
            return CalculoEnvio.exitoso(
                java.util.UUID.randomUUID().toString(),
                costo,
                tiempoEstimado,
                "SERVICIO_INTERNO_SIMULADO"
            );
        })
        .delayElement(Duration.ofMillis(500)) // Simular latencia de red
        .doOnSuccess(result -> logger.info("Cálculo exitoso del servicio interno"))
        .doOnError(error -> logger.error("Error en servicio interno: {}", error.getMessage()));
    }
    
    /**
     * Fallback cuando el servicio interno también falla
     */
    public Mono<CalculoEnvio> fallbackCalculoInterno(String origen, String destino, BigDecimal peso, Exception ex) {
        logger.error("Todos los servicios fallaron. Usando valores de emergencia. Error: {}", ex.getMessage());
        
        CalculoEnvio calculoEmergencia = CalculoEnvio.fallback(
            "Todos los servicios de cálculo no disponibles. Usando valores de emergencia."
        );
        calculoEmergencia.setOrigen(origen);
        calculoEmergencia.setDestino(destino);
        calculoEmergencia.setPeso(peso);
        calculoEmergencia.setCosto(BigDecimal.valueOf(75.0)); // Costo de emergencia más alto
        calculoEmergencia.setTiempoEstimadoDias(10); // Tiempo de emergencia más conservador
        
        return Mono.just(calculoEmergencia);
    }
    
    /**
     * Cálculo básico de costo basado en peso y distancia estimada
     */
    private BigDecimal calcularCostoBasico(String origen, String destino, BigDecimal peso) {
        // Lógica básica de cálculo
        BigDecimal costoBase = BigDecimal.valueOf(10.0);
        BigDecimal costoPorKg = BigDecimal.valueOf(5.0);
        BigDecimal multiplicadorDistancia = origen.equals(destino) ? 
            BigDecimal.valueOf(1.0) : BigDecimal.valueOf(1.5);
        
        return costoBase
            .add(peso.multiply(costoPorKg))
            .multiply(multiplicadorDistancia);
    }
    
    /**
     * Cálculo básico de tiempo basado en origen y destino
     */
    private Integer calcularTiempoBasico(String origen, String destino) {
        if (origen.equals(destino)) {
            return 1; // Mismo día
        } else if (origen.contains("Nacional") && destino.contains("Nacional")) {
            return 3; // Nacional
        } else {
            return 7; // Internacional
        }
    }
}
