package com.arka.arkavalenzuela.infrastructure.adapter.saga;

import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import com.arka.arkavalenzuela.domain.port.out.saga.ExternalServiceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * Adaptador para comunicación con servicios externos (S2 Inventario y ChipMen)
 */
@Component
public class ExternalServiceAdapterImpl implements ExternalServiceAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalServiceAdapterImpl.class);
    
    private final WebClient webClient;
    private final String inventoryServiceUrl;
    private final String shippingServiceUrl;
    
    public ExternalServiceAdapterImpl(@Value("${external.services.inventory.url}") String inventoryServiceUrl,
                                     @Value("${external.services.shipping.url}") String shippingServiceUrl,
                                     WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.inventoryServiceUrl = inventoryServiceUrl;
        this.shippingServiceUrl = shippingServiceUrl;
    }
    
    @Override
    public Mono<Boolean> reserveInventory(SagaPedido pedido) {
        logger.info("Reservando inventario para pedido: {} del producto: {}", 
                   pedido.getPedidoId(), pedido.getProductoId());
        
        Map<String, Object> reservationRequest = Map.of(
                "pedidoId", pedido.getPedidoId(),
                "productoId", pedido.getProductoId(),
                "cantidad", pedido.getCantidad(),
                "clienteId", pedido.getClienteId()
        );
        
        return webClient.post()
                .uri(inventoryServiceUrl + "/api/inventory/reserve")
                .bodyValue(reservationRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Boolean success = (Boolean) response.get("success");
                    logger.info("Respuesta de reserva de inventario para pedido {}: {}", 
                               pedido.getPedidoId(), success);
                    return success != null && success;
                })
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> logger.error("Error al reservar inventario para pedido {}: {}", 
                                                pedido.getPedidoId(), error.getMessage()))
                .onErrorReturn(false); // En caso de error, retornar false
    }
    
    @Override
    public Mono<String> generateShippingOrder(SagaPedido pedido) {
        logger.info("Generando orden de envío para pedido: {}", pedido.getPedidoId());
        
        Map<String, Object> shippingRequest = Map.of(
                "pedidoId", pedido.getPedidoId(),
                "clienteId", pedido.getClienteId(),
                "productoId", pedido.getProductoId(),
                "cantidad", pedido.getCantidad(),
                "direccionEnvio", "Dirección por defecto", // En un caso real vendría del pedido
                "tipoEnvio", "ESTANDAR"
        );
        
        return webClient.post()
                .uri(shippingServiceUrl + "/api/shipping/create")
                .bodyValue(shippingRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    String shippingOrderId = (String) response.get("shippingOrderId");
                    logger.info("Orden de envío generada para pedido {}: {}", 
                               pedido.getPedidoId(), shippingOrderId);
                    return shippingOrderId != null ? shippingOrderId : "SHIPPING_ERROR";
                })
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> logger.error("Error al generar orden de envío para pedido {}: {}", 
                                                pedido.getPedidoId(), error.getMessage()))
                .onErrorReturn("SHIPPING_ERROR");
    }
    
    @Override
    public Mono<Boolean> compensateInventory(SagaPedido pedido) {
        logger.info("Compensando inventario para pedido: {}", pedido.getPedidoId());
        
        Map<String, Object> compensationRequest = Map.of(
                "pedidoId", pedido.getPedidoId(),
                "productoId", pedido.getProductoId(),
                "cantidad", pedido.getCantidad(),
                "motivo", "FALLO_EN_ENVIO"
        );
        
        return webClient.post()
                .uri(inventoryServiceUrl + "/api/inventory/compensate")
                .bodyValue(compensationRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Boolean success = (Boolean) response.get("success");
                    logger.info("Respuesta de compensación de inventario para pedido {}: {}", 
                               pedido.getPedidoId(), success);
                    return success != null && success;
                })
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> logger.error("Error al compensar inventario para pedido {}: {}", 
                                                pedido.getPedidoId(), error.getMessage()))
                .onErrorReturn(false);
    }
}
