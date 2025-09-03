package com.arka.arkavalenzuela.infrastructure.adapter.saga.mock;

import com.arka.arkavalenzuela.domain.model.saga.SagaEvent;
import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import com.arka.arkavalenzuela.domain.port.out.saga.SagaEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Implementación mock del publicador de eventos SNS para desarrollo local
 * Se activa cuando aws.mock.enabled=true
 */
@Component
@ConditionalOnProperty(name = "aws.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockSagaEventPublisher implements SagaEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(MockSagaEventPublisher.class);
    
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;
    
    public MockSagaEventPublisher(ObjectMapper objectMapper, ApplicationContext applicationContext) {
        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;
        logger.info("🔧 MockSagaEventPublisher inicializado - Modo desarrollo");
    }
    
    @Override
    public Mono<String> publishEvent(SagaEvent event) {
        return publishEventToTopic("mock-topic", event);
    }
    
    @Override
    public Mono<String> publishEventToTopic(String topicArn, SagaEvent event) {
        return Mono.fromCallable(() -> {
            try {
                String message = objectMapper.writeValueAsString(event);
                String mockMessageId = "MOCK_MSG_" + java.util.UUID.randomUUID().toString().substring(0, 8);
                
                logger.info("📢 [MOCK SNS] Evento publicado:");
                logger.info("   📋 Topic: {}", topicArn);
                logger.info("   🆔 MessageId: {}", mockMessageId);
                logger.info("   📦 EventType: {}", event.getEventType());
                logger.info("   🎯 SagaId: {}", event.getSagaId());
                logger.info("   📄 Payload: {}", message);
                
                // Simular procesamiento asíncrono
                Thread.sleep(100);
                
                return mockMessageId;
                
            } catch (JsonProcessingException e) {
                logger.error("❌ Error al serializar evento mock", e);
                throw new RuntimeException("Error al serializar evento mock", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrumpido durante simulación", e);
            }
        })
        .doOnError(error -> logger.error("❌ Error en MockSagaEventPublisher: {}", error.getMessage()));
    }
}
