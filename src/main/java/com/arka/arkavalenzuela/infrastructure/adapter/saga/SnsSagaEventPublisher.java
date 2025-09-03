package com.arka.arkavalenzuela.infrastructure.adapter.saga;

import com.arka.arkavalenzuela.domain.model.saga.SagaEvent;
import com.arka.arkavalenzuela.domain.port.out.saga.SagaEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

/**
 * Adaptador para publicar eventos en Amazon SNS
 */
@Component
public class SnsSagaEventPublisher implements SagaEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(SnsSagaEventPublisher.class);
    
    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    private final String defaultTopicArn;
    
    public SnsSagaEventPublisher(@Value("${aws.region}") String awsRegion,
                                @Value("${aws.sns.saga-topic-arn}") String defaultTopicArn,
                                ObjectMapper objectMapper) {
        this.snsClient = SnsClient.builder()
                .region(Region.of(awsRegion))
                .build();
        this.defaultTopicArn = defaultTopicArn;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Mono<String> publishEvent(SagaEvent event) {
        return publishEventToTopic(defaultTopicArn, event);
    }
    
    @Override
    public Mono<String> publishEventToTopic(String topicArn, SagaEvent event) {
        return Mono.fromCallable(() -> {
            try {
                String message = objectMapper.writeValueAsString(event);
                
                PublishRequest publishRequest = PublishRequest.builder()
                        .topicArn(topicArn)
                        .message(message)
                        .subject("SagaEvent-" + event.getEventType())
                        .messageAttributes(java.util.Map.of(
                                "eventType", software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                                        .dataType("String")
                                        .stringValue(event.getEventType())
                                        .build(),
                                "sagaId", software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                                        .dataType("String")
                                        .stringValue(event.getSagaId())
                                        .build(),
                                "source", software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                                        .dataType("String")
                                        .stringValue(event.getSource())
                                        .build()
                        ))
                        .build();
                
                PublishResponse response = snsClient.publish(publishRequest);
                
                logger.info("Evento publicado en SNS - MessageId: {}, EventType: {}, SagaId: {}", 
                           response.messageId(), event.getEventType(), event.getSagaId());
                
                return response.messageId();
                
            } catch (JsonProcessingException e) {
                logger.error("Error al serializar evento para SNS", e);
                throw new RuntimeException("Error al serializar evento", e);
            } catch (SdkException e) {
                logger.error("Error al publicar en SNS", e);
                throw new RuntimeException("Error al publicar en SNS", e);
            }
        })
        .doOnError(error -> logger.error("Error al publicar evento en SNS: {}", error.getMessage()));
    }
}
