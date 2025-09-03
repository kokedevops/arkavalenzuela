package com.arka.lambda.notification;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Lambda 3: Función personalizada para notificación al cliente en el patrón Saga
 * Esta función se activa cuando se recibe el evento SHIPPING_GENERATED de SNS
 * y envía una notificación al cliente sobre el estado del pedido
 */
public class NotificationLambda implements RequestHandler<SNSEvent, String> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SnsClient snsClient = SnsClient.builder()
            .region(Region.US_EAST_1)
            .build();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    private static final String SNS_TOPIC_ARN = System.getenv("SNS_TOPIC_ARN");
    private static final String NOTIFICATION_SERVICE_URL = System.getenv("NOTIFICATION_SERVICE_URL");
    
    @Override
    public String handleRequest(SNSEvent input, Context context) {
        try {
            for (SNSEvent.SNSRecord record : input.getRecords()) {
                procesarEventoSNS(record, context);
            }
            return "Notificaciones procesadas exitosamente";
            
        } catch (Exception e) {
            context.getLogger().log("Error en NotificationLambda: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    private void procesarEventoSNS(SNSEvent.SNSRecord record, Context context) {
        try {
            String mensaje = record.getSNS().getMessage();
            JsonNode evento = objectMapper.readTree(mensaje);
            
            String eventType = evento.get("eventType").asText();
            
            // Solo procesar eventos de envío generado
            if (!"SHIPPING_GENERATED".equals(eventType)) {
                context.getLogger().log("Evento ignorado, tipo: " + eventType);
                return;
            }
            
            JsonNode payload = evento.get("payload");
            String pedidoId = payload.get("pedidoId").asText();
            String shippingOrderId = payload.get("shippingOrderId").asText();
            
            context.getLogger().log("Procesando notificación para pedido: " + pedidoId);
            
            // Enviar notificación al cliente
            boolean notificacionEnviada = enviarNotificacionCliente(pedidoId, shippingOrderId, context);
            
            // Generar informe de ventas (paso personalizado adicional)
            generarInformeVentas(pedidoId, context);
            
            // Preparar evento final para SNS
            Map<String, Object> sagaEvent = new HashMap<>();
            sagaEvent.put("eventId", java.util.UUID.randomUUID().toString());
            sagaEvent.put("sagaId", pedidoId);
            sagaEvent.put("source", "NotificationLambda");
            sagaEvent.put("timestamp", java.time.LocalDateTime.now().toString());
            
            if (notificacionEnviada) {
                // Notificación enviada exitosamente
                sagaEvent.put("eventType", "NOTIFICATION_SENT");
                sagaEvent.put("payload", Map.of(
                        "pedidoId", pedidoId,
                        "shippingOrderId", shippingOrderId,
                        "status", "NOTIFICATION_SENT",
                        "reportGenerated", true
                ));
                
                context.getLogger().log("Notificación enviada exitosamente para pedido: " + pedidoId);
                
            } else {
                // Fallo en notificación (no es crítico, completamos el saga)
                sagaEvent.put("eventType", "NOTIFICATION_SENT");
                sagaEvent.put("payload", Map.of(
                        "pedidoId", pedidoId,
                        "shippingOrderId", shippingOrderId,
                        "status", "NOTIFICATION_FAILED",
                        "reportGenerated", true,
                        "note", "Pedido completado pero notificación falló"
                ));
                
                context.getLogger().log("Pedido completado pero notificación falló para pedido: " + pedidoId);
            }
            
            // Publicar evento final en SNS
            publicarEventoSNS(sagaEvent, context);
            
        } catch (Exception e) {
            context.getLogger().log("Error al procesar evento SNS: " + e.getMessage());
        }
    }
    
    /**
     * Envía notificación al cliente sobre el estado del pedido
     */
    private boolean enviarNotificacionCliente(String pedidoId, String shippingOrderId, Context context) {
        try {
            context.getLogger().log("Enviando notificación para pedido: " + pedidoId);
            
            // Preparar mensaje de notificación
            Map<String, Object> notificationRequest = Map.of(
                    "pedidoId", pedidoId,
                    "shippingOrderId", shippingOrderId,
                    "mensaje", "¡Tu pedido " + pedidoId + " ha sido procesado exitosamente! " +
                              "Número de seguimiento: " + shippingOrderId,
                    "tipo", "PEDIDO_PROCESADO",
                    "timestamp", java.time.LocalDateTime.now().toString()
            );
            
            // Simular envío de notificación (email, SMS, push, etc.)
            String requestBody = objectMapper.writeValueAsString(notificationRequest);
            
            if (NOTIFICATION_SERVICE_URL != null && !NOTIFICATION_SERVICE_URL.isEmpty()) {
                // Enviar a servicio de notificaciones real
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(NOTIFICATION_SERVICE_URL + "/api/notifications/send"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, 
                        HttpResponse.BodyHandlers.ofString());
                
                context.getLogger().log("Respuesta del servicio de notificaciones: " + response.statusCode());
                return response.statusCode() == 200;
                
            } else {
                // Simulación para demo
                context.getLogger().log("Notificación simulada enviada: " + requestBody);
                return true;
            }
            
        } catch (Exception e) {
            context.getLogger().log("Error al enviar notificación: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Genera un informe de ventas (paso personalizado)
     */
    private void generarInformeVentas(String pedidoId, Context context) {
        try {
            context.getLogger().log("Generando informe de ventas para pedido: " + pedidoId);
            
            // Crear datos del informe
            Map<String, Object> salesReport = Map.of(
                    "reportId", "REPORT_" + java.util.UUID.randomUUID().toString().substring(0, 8),
                    "pedidoId", pedidoId,
                    "timestamp", java.time.LocalDateTime.now().toString(),
                    "tipo", "VENTA_COMPLETADA",
                    "generadoPor", "NotificationLambda"
            );
            
            // En un escenario real, aquí guardarías el informe en DynamoDB o S3
            context.getLogger().log("Informe de ventas generado: " + 
                                   objectMapper.writeValueAsString(salesReport));
            
        } catch (Exception e) {
            context.getLogger().log("Error al generar informe de ventas: " + e.getMessage());
        }
    }
    
    /**
     * Publica el evento final en SNS para completar el proceso Saga
     */
    private void publicarEventoSNS(Map<String, Object> evento, Context context) {
        try {
            String mensaje = objectMapper.writeValueAsString(evento);
            
            PublishRequest publishRequest = PublishRequest.builder()
                    .topicArn(SNS_TOPIC_ARN)
                    .message(mensaje)
                    .subject("SagaEvent-" + evento.get("eventType"))
                    .build();
            
            var result = snsClient.publish(publishRequest);
            context.getLogger().log("Evento final publicado en SNS: " + result.messageId());
            
        } catch (Exception e) {
            context.getLogger().log("Error al publicar en SNS: " + e.getMessage());
        }
    }
}
