package com.arka.arkavalenzuela.infrastructure.adapter.out.mongodb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB Entity for Notification History
 * Stores notification logs for tracking and analytics
 */
@Document(collection = "notification_history")
public class NotificationHistoryEntity {
    
    @Id
    private String id;
    
    private String type; // EMAIL, SMS, PUSH
    private String recipient;
    private String subject;
    private String content;
    private String status; // SENT, FAILED, PENDING
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private String relatedEntityType; // CART, ORDER, PRODUCT
    private String relatedEntityId;
    private String errorMessage;
    
    public NotificationHistoryEntity() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public NotificationHistoryEntity(String type, String recipient, String subject, 
                                   String content, String relatedEntityType, String relatedEntityId) {
        this();
        this.type = type;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }
    
    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    // Helper methods
    public void markAsSent() {
        this.status = "SENT";
        this.sentAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String error) {
        this.status = "FAILED";
        this.errorMessage = error;
    }
}
