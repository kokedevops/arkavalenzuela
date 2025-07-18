package com.arka.arkavalenzuela.infrastructure.adapter.out.microservice;

import com.arka.arkavalenzuela.domain.port.out.GestorSolicitudesServicePort;
import com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto.SolicitudRequestDto;
import com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto.SolicitudResponseDto;
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
public class GestorSolicitudesServiceAdapter implements GestorSolicitudesServicePort {
    
    private static final Logger logger = LoggerFactory.getLogger(GestorSolicitudesServiceAdapter.class);
    
    private final RestTemplate restTemplate;
    private final String gestorSolicitudesBaseUrl;
    
    public GestorSolicitudesServiceAdapter(RestTemplate restTemplate,
                                          @Value("${microservices.gestor-solicitudes.url}") String gestorSolicitudesBaseUrl) {
        this.restTemplate = restTemplate;
        this.gestorSolicitudesBaseUrl = gestorSolicitudesBaseUrl;
    }
    
    @Override
    public CompletableFuture<SolicitudResponseDto> crearSolicitud(SolicitudRequestDto request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Creando solicitud para cliente: {}", request.getCustomerId());
                
                String url = gestorSolicitudesBaseUrl + "/api/solicitudes";
                HttpEntity<SolicitudRequestDto> entity = new HttpEntity<>(request);
                
                ResponseEntity<SolicitudResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, SolicitudResponseDto.class);
                
                SolicitudResponseDto result = response.getBody();
                if (result != null) {
                    logger.info("Solicitud creada exitosamente: {}", result.getSolicitudId());
                }
                return result;
                
            } catch (RestClientException ex) {
                logger.error("Error al crear solicitud: {}", ex.getMessage());
                throw new RuntimeException("Error al comunicarse con el servicio de gestión de solicitudes: " + ex.getMessage(), ex);
            }
        });
    }
    
    @Override
    public CompletableFuture<SolicitudResponseDto> obtenerSolicitud(String solicitudId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Obteniendo solicitud: {}", solicitudId);
                
                String url = gestorSolicitudesBaseUrl + "/api/solicitudes/{id}";
                ResponseEntity<SolicitudResponseDto> response = restTemplate.getForEntity(url, SolicitudResponseDto.class, solicitudId);
                
                logger.info("Solicitud obtenida exitosamente: {}", solicitudId);
                return response.getBody();
                
            } catch (RestClientException ex) {
                logger.error("Error al obtener solicitud {}: {}", solicitudId, ex.getMessage());
                throw new RuntimeException("Error al comunicarse con el servicio de gestión de solicitudes: " + ex.getMessage(), ex);
            }
        });
    }
    
    @Override
    public CompletableFuture<List<SolicitudResponseDto>> obtenerSolicitudesPorCliente(Long customerId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Obteniendo solicitudes para cliente: {}", customerId);
                
                String url = gestorSolicitudesBaseUrl + "/api/solicitudes/cliente/{customerId}";
                ResponseEntity<List<SolicitudResponseDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<SolicitudResponseDto>>(){}, customerId);
                
                List<SolicitudResponseDto> result = response.getBody();
                if (result != null) {
                    logger.info("Obtenidas {} solicitudes para cliente {}", result.size(), customerId);
                }
                return result;
                
            } catch (RestClientException ex) {
                logger.error("Error al obtener solicitudes para cliente {}: {}", customerId, ex.getMessage());
                throw new RuntimeException("Error al comunicarse con el servicio de gestión de solicitudes: " + ex.getMessage(), ex);
            }
        });
    }
    
    @Override
    public CompletableFuture<SolicitudResponseDto> actualizarEstadoSolicitud(String solicitudId, String nuevoEstado) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Actualizando estado de solicitud {} a: {}", solicitudId, nuevoEstado);
                
                String url = gestorSolicitudesBaseUrl + "/api/solicitudes/{id}/estado";
                HttpEntity<String> entity = new HttpEntity<>(nuevoEstado);
                
                ResponseEntity<SolicitudResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, entity, SolicitudResponseDto.class, solicitudId);
                
                logger.info("Estado de solicitud actualizado exitosamente: {}", solicitudId);
                return response.getBody();
                
            } catch (RestClientException ex) {
                logger.error("Error al actualizar estado de solicitud {}: {}", solicitudId, ex.getMessage());
                throw new RuntimeException("Error al comunicarse con el servicio de gestión de solicitudes: " + ex.getMessage(), ex);
            }
        });
    }
    
    @Override
    public CompletableFuture<List<SolicitudResponseDto>> obtenerSolicitudesPorEstado(String estado) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Obteniendo solicitudes por estado: {}", estado);
                
                String url = gestorSolicitudesBaseUrl + "/api/solicitudes/estado/{estado}";
                ResponseEntity<List<SolicitudResponseDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<SolicitudResponseDto>>(){}, estado);
                
                List<SolicitudResponseDto> result = response.getBody();
                if (result != null) {
                    logger.info("Obtenidas {} solicitudes con estado {}", result.size(), estado);
                }
                return result;
                
            } catch (RestClientException ex) {
                logger.error("Error al obtener solicitudes por estado {}: {}", estado, ex.getMessage());
                throw new RuntimeException("Error al comunicarse con el servicio de gestión de solicitudes: " + ex.getMessage(), ex);
            }
        });
    }
    
    @Override
    public CompletableFuture<Boolean> isServiceAvailable() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Verificando disponibilidad del servicio de gestión de solicitudes");
                
                String url = gestorSolicitudesBaseUrl + "/actuator/health";
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                
                boolean available = response.getStatusCode().is2xxSuccessful();
                logger.debug("Servicio de gestión de solicitudes disponible: {}", available);
                return available;
                
            } catch (RestClientException ex) {
                logger.warn("Servicio de gestión de solicitudes no disponible: {}", ex.getMessage());
                return false;
            }
        });
    }
}
