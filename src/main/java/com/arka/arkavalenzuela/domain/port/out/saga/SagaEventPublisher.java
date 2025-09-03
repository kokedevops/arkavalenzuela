package com.arka.arkavalenzuela.domain.port.out.saga;

import com.arka.arkavalenzuela.domain.model.saga.SagaEvent;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para publicar eventos en SNS
 */
public interface SagaEventPublisher {
    
    /**
     * Publica un evento en el topic SNS correspondiente
     * @param event Evento a publicar
     * @return Mono con el ID del mensaje publicado
     */
    Mono<String> publishEvent(SagaEvent event);
    
    /**
     * Publica un evento en un topic específico
     * @param topicArn ARN del topic SNS
     * @param event Evento a publicar
     * @return Mono con el ID del mensaje publicado
     */
    Mono<String> publishEventToTopic(String topicArn, SagaEvent event);
}
