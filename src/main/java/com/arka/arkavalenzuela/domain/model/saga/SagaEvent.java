package com.arka.arkavalenzuela.domain.model.saga;

import java.time.LocalDateTime;

/**
 * Evento del patrón Saga que será enviado a través de SNS
 */
public class SagaEvent {
    
    private final String eventId;
    private final String sagaId;
    private final String eventType;
    private final String payload;
    private final LocalDateTime timestamp;
    private final String source;
    
    public SagaEvent(String sagaId, String eventType, String payload, String source) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.sagaId = sagaId;
        this.eventType = eventType;
        this.payload = payload;
        this.source = source;
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor completo
    public SagaEvent(String eventId, String sagaId, String eventType, 
                    String payload, LocalDateTime timestamp, String source) {
        this.eventId = eventId;
        this.sagaId = sagaId;
        this.eventType = eventType;
        this.payload = payload;
        this.timestamp = timestamp;
        this.source = source;
    }
    
    // Getters
    public String getEventId() { return eventId; }
    public String getSagaId() { return sagaId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getSource() { return source; }
}
