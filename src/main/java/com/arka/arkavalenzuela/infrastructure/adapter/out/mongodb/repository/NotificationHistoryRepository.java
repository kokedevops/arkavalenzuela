package com.arka.arkavalenzuela.infrastructure.adapter.out.mongodb.repository;

import com.arka.arkavalenzuela.infrastructure.adapter.out.mongodb.entity.NotificationHistoryEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Reactive MongoDB Repository for Notification History
 * Provides reactive access to notification data
 */
public interface NotificationHistoryRepository extends ReactiveMongoRepository<NotificationHistoryEntity, String> {
    
    /**
     * Find notifications by type
     */
    Flux<NotificationHistoryEntity> findByType(String type);
    
    /**
     * Find notifications by recipient
     */
    Flux<NotificationHistoryEntity> findByRecipient(String recipient);
    
    /**
     * Find notifications by status
     */
    Flux<NotificationHistoryEntity> findByStatus(String status);
    
    /**
     * Find notifications by related entity
     */
    Flux<NotificationHistoryEntity> findByRelatedEntityTypeAndRelatedEntityId(String entityType, String entityId);
    
    /**
     * Find notifications sent in date range
     */
    Flux<NotificationHistoryEntity> findBySentAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Count notifications by status
     */
    Mono<Long> countByStatus(String status);
    
    /**
     * Count notifications sent today
     */
    @Query("{ 'sentAt': { $gte: ?0, $lt: ?1 } }")
    Mono<Long> countNotificationsSentToday(LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    /**
     * Find recent failed notifications
     */
    @Query("{ 'status': 'FAILED', 'createdAt': { $gte: ?0 } }")
    Flux<NotificationHistoryEntity> findRecentFailedNotifications(LocalDateTime since);
}
