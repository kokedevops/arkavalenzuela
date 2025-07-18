package com.arka.arkavalenzuela.infrastructure.adapter.out.microservice;

import com.arka.arkavalenzuela.domain.port.out.CotizadorServicePort;
import com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto.CotizacionRequestDto;
import com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto.CotizacionResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class CotizadorServiceAdapter implements CotizadorServicePort {
    
    private static final Logger logger = LoggerFactory.getLogger(CotizadorServiceAdapter.class);
    
    private final RestTemplate restTemplate;
    private final String cotizadorBaseUrl;
    
    public CotizadorServiceAdapter(RestTemplate restTemplate,
                                  @Value("${microservices.cotizador.url}") String cotizadorBaseUrl) {
        this.restTemplate = restTemplate;
        this.cotizadorBaseUrl = cotizadorBaseUrl;
    }
    
    @Override
    public CompletableFuture<CotizacionResponseDto> crearCotizacion(CotizacionRequestDto request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Creando cotización para cliente: {}", request.getCustomerId());
                
                String url = cotizadorBaseUrl + "/api/cotizaciones";
                HttpEntity<CotizacionRequestDto> entity = new HttpEntity<>(request);
                
                ResponseEntity<CotizacionResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, CotizacionResponseDto.class);
                
                CotizacionResponseDto result = response.getBody();
                if (result != null) {
                    logger.info("Cotización creada exitosamente: {}", result.getCotizacionId());
                }
                return result;
                
            } catch (RestClientException ex) {
                logger.error("Error al crear cotización: {}", ex.getMessage());
                throw new RuntimeException("Error al comunicarse con el servicio de cotización: " + ex.getMessage(), ex);
            }
        });
    }
    
    @Override
    public CompletableFuture<CotizacionResponseDto> obtenerCotizacion(String cotizacionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Obteniendo cotización: {}", cotizacionId);
                
                String url = cotizadorBaseUrl + "/api/cotizaciones/{id}";
                ResponseEntity<CotizacionResponseDto> response = restTemplate.getForEntity(url, CotizacionResponseDto.class, cotizacionId);
                
                logger.info("Cotización obtenida exitosamente: {}", cotizacionId);
                return response.getBody();
                
            } catch (RestClientException ex) {
                logger.error("Error al obtener cotización {}: {}", cotizacionId, ex.getMessage());
                throw new RuntimeException("Error al comunicarse con el servicio de cotización: " + ex.getMessage(), ex);
            }
        });
    }
    
    @Override
    public CompletableFuture<List<CotizacionResponseDto>> obtenerCotizacionesPorCliente(Long customerId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Obteniendo cotizaciones para cliente: {}", customerId);
                
                String url = cotizadorBaseUrl + "/api/cotizaciones/cliente/{customerId}";
                ResponseEntity<List<CotizacionResponseDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<CotizacionResponseDto>>(){}, customerId);
                
                List<CotizacionResponseDto> result = response.getBody();
                if (result != null) {
                    logger.info("Obtenidas {} cotizaciones para cliente {}", result.size(), customerId);
                }
                return result;
                
            } catch (RestClientException ex) {
                logger.error("Error al obtener cotizaciones para cliente {}: {}", customerId, ex.getMessage());
                throw new RuntimeException("Error al comunicarse con el servicio de cotización: " + ex.getMessage(), ex);
            }
        });
    }
    
    @Override
    public CompletableFuture<CotizacionResponseDto> actualizarEstadoCotizacion(String cotizacionId, String nuevoEstado) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Actualizando estado de cotización {} a: {}", cotizacionId, nuevoEstado);
                
                String url = cotizadorBaseUrl + "/api/cotizaciones/{id}/estado";
                HttpEntity<String> entity = new HttpEntity<>(nuevoEstado);
                
                ResponseEntity<CotizacionResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, entity, CotizacionResponseDto.class, cotizacionId);
                
                logger.info("Estado de cotización actualizado exitosamente: {}", cotizacionId);
                return response.getBody();
                
            } catch (RestClientException ex) {
                logger.error("Error al actualizar estado de cotización {}: {}", cotizacionId, ex.getMessage());
                throw new RuntimeException("Error al comunicarse con el servicio de cotización: " + ex.getMessage(), ex);
            }
        });
    }
    
    @Override
    public CompletableFuture<Boolean> isServiceAvailable() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Verificando disponibilidad del servicio de cotización");
                
                String url = cotizadorBaseUrl + "/actuator/health";
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                
                boolean available = response.getStatusCode().is2xxSuccessful();
                logger.debug("Servicio de cotización disponible: {}", available);
                return available;
                
            } catch (RestClientException ex) {
                logger.warn("Servicio de cotización no disponible: {}", ex.getMessage());
                return false;
            }
        });
    }
}
