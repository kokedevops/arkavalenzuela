package com.arka.arkavalenzuela.infrastructure.adapter.in.web;

import com.arka.arkavalenzuela.application.usecase.SolicitudApplicationService;
import com.arka.arkavalenzuela.domain.model.Product;
import com.arka.arkavalenzuela.domain.port.in.ProductUseCase;
import com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto.SolicitudResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    
    private static final Logger logger = LoggerFactory.getLogger(SolicitudController.class);
    
    private final SolicitudApplicationService solicitudService;
    private final ProductUseCase productUseCase;
    
    public SolicitudController(SolicitudApplicationService solicitudService, ProductUseCase productUseCase) {
        this.solicitudService = solicitudService;
        this.productUseCase = productUseCase;
    }
    
    @PostMapping("/cliente/{customerId}/cotizacion")
    public CompletableFuture<ResponseEntity<SolicitudResponseDto>> crearSolicitudCotizacion(
            @PathVariable Long customerId,
            @RequestBody SolicitudRequest request) {
        
        logger.info("Creando solicitud de cotización para cliente {} con productos: {}", customerId, request.getProductIds());
        
        try {
            List<Product> productos = request.getProductIds().stream()
                .map(productUseCase::getProductById)
                .toList();
            
            return solicitudService.crearSolicitudCotizacion(customerId, productos, request.getObservaciones())
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    logger.error("Error al crear solicitud de cotización: {}", throwable.getMessage());
                    return ResponseEntity.internalServerError().build();
                });
                
        } catch (Exception ex) {
            logger.error("Error al procesar solicitud de cotización: {}", ex.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
        }
    }
    
    @PostMapping("/cliente/{customerId}/informacion")
    public CompletableFuture<ResponseEntity<SolicitudResponseDto>> crearSolicitudInformacion(
            @PathVariable Long customerId,
            @RequestBody SolicitudRequest request) {
        
        logger.info("Creando solicitud de información para cliente {} con productos: {}", customerId, request.getProductIds());
        
        try {
            List<Product> productos = request.getProductIds().stream()
                .map(productUseCase::getProductById)
                .toList();
            
            return solicitudService.crearSolicitudInformacion(customerId, productos, request.getObservaciones())
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    logger.error("Error al crear solicitud de información: {}", throwable.getMessage());
                    return ResponseEntity.internalServerError().build();
                });
                
        } catch (Exception ex) {
            logger.error("Error al procesar solicitud de información: {}", ex.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
        }
    }
    
    @PostMapping("/cliente/{customerId}/urgente")
    public CompletableFuture<ResponseEntity<SolicitudResponseDto>> crearSolicitudUrgente(
            @PathVariable Long customerId,
            @RequestBody SolicitudRequest request) {
        
        logger.info("Creando solicitud urgente para cliente {} con productos: {}", customerId, request.getProductIds());
        
        try {
            List<Product> productos = request.getProductIds().stream()
                .map(productUseCase::getProductById)
                .toList();
            
            return solicitudService.crearSolicitudUrgente(customerId, productos, request.getObservaciones())
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    logger.error("Error al crear solicitud urgente: {}", throwable.getMessage());
                    return ResponseEntity.internalServerError().build();
                });
                
        } catch (Exception ex) {
            logger.error("Error al procesar solicitud urgente: {}", ex.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
        }
    }
    
    @GetMapping("/cliente/{customerId}")
    public CompletableFuture<ResponseEntity<List<SolicitudResponseDto>>> obtenerSolicitudesCliente(
            @PathVariable Long customerId) {
        
        logger.info("Obteniendo solicitudes para cliente: {}", customerId);
        
        return solicitudService.obtenerSolicitudesCliente(customerId)
            .thenApply(ResponseEntity::ok)
            .exceptionally(throwable -> {
                logger.error("Error al obtener solicitudes del cliente {}: {}", customerId, throwable.getMessage());
                return ResponseEntity.internalServerError().build();
            });
    }
    
    @GetMapping("/{solicitudId}")
    public CompletableFuture<ResponseEntity<SolicitudResponseDto>> obtenerSolicitud(
            @PathVariable String solicitudId) {
        
        logger.info("Obteniendo solicitud: {}", solicitudId);
        
        return solicitudService.obtenerSolicitud(solicitudId)
            .thenApply(ResponseEntity::ok)
            .exceptionally(throwable -> {
                logger.error("Error al obtener solicitud {}: {}", solicitudId, throwable.getMessage());
                return ResponseEntity.notFound().build();
            });
    }
    
    @GetMapping("/estado/{estado}")
    public CompletableFuture<ResponseEntity<List<SolicitudResponseDto>>> obtenerSolicitudesPorEstado(
            @PathVariable String estado) {
        
        logger.info("Obteniendo solicitudes por estado: {}", estado);
        
        return solicitudService.obtenerSolicitudesPorEstado(estado)
            .thenApply(ResponseEntity::ok)
            .exceptionally(throwable -> {
                logger.error("Error al obtener solicitudes por estado {}: {}", estado, throwable.getMessage());
                return ResponseEntity.internalServerError().build();
            });
    }
    
    @PatchMapping("/{solicitudId}/estado")
    public CompletableFuture<ResponseEntity<SolicitudResponseDto>> actualizarEstadoSolicitud(
            @PathVariable String solicitudId,
            @RequestBody String nuevoEstado) {
        
        logger.info("Actualizando estado de solicitud {} a: {}", solicitudId, nuevoEstado);
        
        return solicitudService.actualizarEstadoSolicitud(solicitudId, nuevoEstado)
            .thenApply(ResponseEntity::ok)
            .exceptionally(throwable -> {
                logger.error("Error al actualizar estado de solicitud {}: {}", solicitudId, throwable.getMessage());
                return ResponseEntity.internalServerError().build();
            });
    }
    
    @GetMapping("/servicio/health")
    public CompletableFuture<ResponseEntity<String>> verificarServicioSolicitudes() {
        return solicitudService.verificarDisponibilidadServicio()
            .thenApply(disponible -> {
                if (disponible) {
                    return ResponseEntity.ok("Servicio de gestión de solicitudes disponible");
                } else {
                    return ResponseEntity.status(503).body("Servicio de gestión de solicitudes no disponible");
                }
            });
    }
    
    // DTO para las requests
    public static class SolicitudRequest {
        private List<Long> productIds;
        private String observaciones;
        
        public SolicitudRequest() {}
        
        public List<Long> getProductIds() {
            return productIds;
        }
        
        public void setProductIds(List<Long> productIds) {
            this.productIds = productIds;
        }
        
        public String getObservaciones() {
            return observaciones;
        }
        
        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
    }
}
