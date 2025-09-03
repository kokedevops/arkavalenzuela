package com.arka.arkavalenzuela.infrastructure.adapter.in.web.saga;

import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import com.arka.arkavalenzuela.domain.port.in.saga.SagaOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Controlador REST para el patrón Saga
 */
@RestController
@RequestMapping("/api/saga")
@CrossOrigin(origins = "*")
public class SagaController {
    
    private static final Logger logger = LoggerFactory.getLogger(SagaController.class);
    
    private final SagaOrchestrator sagaOrchestrator;
    
    public SagaController(SagaOrchestrator sagaOrchestrator) {
        this.sagaOrchestrator = sagaOrchestrator;
    }
    
    /**
     * Inicia un nuevo proceso Saga para un pedido
     */
    @PostMapping("/start")
    public Mono<ResponseEntity<Map<String, Object>>> startSaga(@RequestBody SagaStartRequest request) {
        logger.info("Iniciando Saga para cliente: {} con producto: {}", 
                   request.getClienteId(), request.getProductoId());
        
        SagaPedido pedido = new SagaPedido(
                request.getClienteId(),
                request.getProductoId(),
                request.getCantidad(),
                request.getPrecio()
        );
        
        return sagaOrchestrator.startSaga(pedido)
                .map(result -> {
                    Map<String, Object> response = Map.of(
                            "success", true,
                            "message", "Saga iniciado exitosamente",
                            "pedidoId", pedido.getPedidoId(),
                            "result", result
                    );
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    logger.error("Error al iniciar Saga", error);
                    Map<String, Object> errorResponse = Map.of(
                            "success", false,
                            "message", "Error al iniciar Saga: " + error.getMessage(),
                            "pedidoId", pedido.getPedidoId()
                    );
                    return Mono.just(ResponseEntity.badRequest().body(errorResponse));
                });
    }
    
    /**
     * Webhook para manejar eventos de inventario reservado
     */
    @PostMapping("/events/inventory-reserved")
    public Mono<ResponseEntity<Map<String, Object>>> handleInventoryReserved(@RequestBody Map<String, String> event) {
        String pedidoId = event.get("pedidoId");
        logger.info("Recibido evento de inventario reservado para pedido: {}", pedidoId);
        
        return sagaOrchestrator.handleInventoryReserved(pedidoId)
                .map(result -> {
                    Map<String, Object> response = Map.of(
                            "success", true,
                            "message", "Evento procesado exitosamente",
                            "result", result
                    );
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    logger.error("Error al procesar evento de inventario reservado", error);
                    Map<String, Object> errorResponse = Map.of(
                            "success", false,
                            "message", "Error al procesar evento: " + error.getMessage()
                    );
                    return Mono.just(ResponseEntity.badRequest().body(errorResponse));
                });
    }
    
    /**
     * Webhook para manejar eventos de envío generado
     */
    @PostMapping("/events/shipping-generated")
    public Mono<ResponseEntity<Map<String, Object>>> handleShippingGenerated(@RequestBody Map<String, String> event) {
        String pedidoId = event.get("pedidoId");
        String shippingOrderId = event.get("shippingOrderId");
        logger.info("Recibido evento de envío generado para pedido: {} con orden: {}", pedidoId, shippingOrderId);
        
        return sagaOrchestrator.handleShippingGenerated(pedidoId, shippingOrderId)
                .map(result -> {
                    Map<String, Object> response = Map.of(
                            "success", true,
                            "message", "Evento procesado exitosamente",
                            "result", result
                    );
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    logger.error("Error al procesar evento de envío generado", error);
                    Map<String, Object> errorResponse = Map.of(
                            "success", false,
                            "message", "Error al procesar evento: " + error.getMessage()
                    );
                    return Mono.just(ResponseEntity.badRequest().body(errorResponse));
                });
    }
    
    /**
     * Webhook para manejar eventos de notificación enviada
     */
    @PostMapping("/events/notification-sent")
    public Mono<ResponseEntity<Map<String, Object>>> handleNotificationSent(@RequestBody Map<String, String> event) {
        String pedidoId = event.get("pedidoId");
        logger.info("Recibido evento de notificación enviada para pedido: {}", pedidoId);
        
        return sagaOrchestrator.handleNotificationSent(pedidoId)
                .map(result -> {
                    Map<String, Object> response = Map.of(
                            "success", true,
                            "message", "Evento procesado exitosamente",
                            "result", result
                    );
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    logger.error("Error al procesar evento de notificación enviada", error);
                    Map<String, Object> errorResponse = Map.of(
                            "success", false,
                            "message", "Error al procesar evento: " + error.getMessage()
                    );
                    return Mono.just(ResponseEntity.badRequest().body(errorResponse));
                });
    }
    
    /**
     * DTO para la solicitud de inicio de Saga
     */
    public static class SagaStartRequest {
        private String clienteId;
        private String productoId;
        private Integer cantidad;
        private Double precio;
        
        // Constructors
        public SagaStartRequest() {}
        
        public SagaStartRequest(String clienteId, String productoId, Integer cantidad, Double precio) {
            this.clienteId = clienteId;
            this.productoId = productoId;
            this.cantidad = cantidad;
            this.precio = precio;
        }
        
        // Getters and Setters
        public String getClienteId() { return clienteId; }
        public void setClienteId(String clienteId) { this.clienteId = clienteId; }
        
        public String getProductoId() { return productoId; }
        public void setProductoId(String productoId) { this.productoId = productoId; }
        
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        
        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }
    }
}
