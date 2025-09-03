package com.arka.arkavalenzuela.infrastructure.adapter.saga.mock;

import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import com.arka.arkavalenzuela.domain.port.out.saga.ExternalServiceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Implementación mock del adaptador de servicios externos para desarrollo local
 * Se activa cuando aws.mock.enabled=true
 */
@Component
@ConditionalOnProperty(name = "aws.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockExternalServiceAdapter implements ExternalServiceAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(MockExternalServiceAdapter.class);
    
    public MockExternalServiceAdapter() {
        logger.info("🔧 MockExternalServiceAdapter inicializado - Modo desarrollo");
    }
    
    @Override
    public Mono<Boolean> reserveInventory(SagaPedido pedido) {
        return Mono.fromCallable(() -> {
            logger.info("🏭 [MOCK S2] Conectando con servicio de inventario para producto: {}", pedido.getProductoId());
            
            try {
                // Simular tiempo de conexión con S2
                Thread.sleep(400);
                
                // Simular lógica de inventario: productos que no contienen "999" tienen stock
                boolean hasStock = !pedido.getProductoId().contains("999") && pedido.getCantidad() <= 10;
                
                if (hasStock) {
                    logger.info("   ✅ S2 confirmó reserva para {} unidades del producto {}", 
                               pedido.getCantidad(), pedido.getProductoId());
                } else {
                    logger.warn("   ❌ S2 rechazó reserva - Stock insuficiente para producto {}", 
                               pedido.getProductoId());
                }
                
                return hasStock;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("❌ Conexión con S2 interrumpida");
                throw new RuntimeException("Conexión con S2 interrumpida", e);
            }
        });
    }
    
    @Override
    public Mono<String> generateShippingOrder(SagaPedido pedido) {
        return Mono.fromCallable(() -> {
            logger.info("🚚 [MOCK CHIPMEN] Generando orden de envío para pedido: {}", pedido.getPedidoId());
            
            try {
                // Simular tiempo de conexión con ChipMen
                Thread.sleep(350);
                
                String shippingOrderId = "CHIPMEN_" + java.util.UUID.randomUUID().toString().substring(0, 10);
                
                logger.info("   ✅ ChipMen generó orden de envío: {}", shippingOrderId);
                logger.info("   📦 Detalles: {} unidades del producto {} para cliente {}", 
                           pedido.getCantidad(), pedido.getProductoId(), pedido.getClienteId());
                
                return shippingOrderId;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("❌ Conexión con ChipMen interrumpida");
                throw new RuntimeException("Conexión con ChipMen interrumpida", e);
            }
        });
    }
    
    @Override
    public Mono<Boolean> compensateInventory(SagaPedido pedido) {
        return Mono.fromCallable(() -> {
            logger.info("🔄 [MOCK S2] Compensando reserva de inventario para pedido: {}", pedido.getPedidoId());
            
            try {
                // Simular tiempo de compensación con S2
                Thread.sleep(200);
                
                logger.info("   ✅ S2 confirmó compensación de inventario para producto {}", 
                           pedido.getProductoId());
                logger.info("   📈 {} unidades devueltas al stock", pedido.getCantidad());
                
                return true;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("❌ Compensación con S2 interrumpida");
                throw new RuntimeException("Compensación con S2 interrumpida", e);
            }
        });
    }
}
