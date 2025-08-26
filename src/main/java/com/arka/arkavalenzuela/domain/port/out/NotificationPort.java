package com.arka.arkavalenzuela.domain.port.out;

/**
 * Port for notification services
 * Defines contract for sending notifications (email, SMS, etc.)
 */
public interface NotificationPort {
    
    /**
     * Send email notification
     */
    void sendEmail(String to, String subject, String body);
    
    /**
     * Send abandoned cart reminder email
     */
    void sendAbandonedCartReminder(String customerEmail, String customerName, Long cartId);
    
    /**
     * Send order confirmation email
     */
    void sendOrderConfirmation(String customerEmail, String customerName, Long orderId);
    
    /**
     * Send stock alert notification
     */
    void sendStockAlert(String productName, Integer currentStock);
}
