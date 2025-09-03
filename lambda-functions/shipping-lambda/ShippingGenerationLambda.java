package com.arka.lambda.shipping;

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
 * Lambda 2: Función para generación de orden de envío en el patrón Saga
 * Esta función se activa cuando se recibe el evento INVENTORY_RESERVED de SNS
 * y se conecta con el servicio ChipMen para generar la orden de envío
 */
public class ShippingGenerationLambda implements RequestHandler<SNSEvent, String> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SnsClient snsClient = SnsClient.builder()
            .region(Region.US_EAST_1)
            .build();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    private static final String SNS_TOPIC_ARN = System.getenv("SNS_TOPIC_ARN");
    private static final String CHIPMEN_SERVICE_URL = System.getenv("CHIPMEN_SERVICE_URL");
    
    @Override
    public String handleRequest(SNSEvent input, Context context) {
        try {
            for (SNSEvent.SNSRecord record : input.getRecords()) {
                procesarEventoSNS(record, context);
            }
            return "Eventos procesados exitosamente";
            
        } catch (Exception e) {
            context.getLogger().log("Error en ShippingGenerationLambda: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    private void procesarEventoSNS(SNSEvent.SNSRecord record, Context context) {
        try {
            String mensaje = record.getSNS().getMessage();
            JsonNode evento = objectMapper.readTree(mensaje);
            
            String eventType = evento.get("eventType").asText();
            
            // Solo procesar eventos de inventario reservado
            if (!"INVENTORY_RESERVED".equals(eventType)) {
                context.getLogger().log("Evento ignorado, tipo: " + eventType);
                return;
            }
            
            JsonNode payload = evento.get("payload");
            String pedidoId = payload.get("pedidoId").asText();
            String productoId = payload.get("productoId").asText();
            int cantidad = payload.get("cantidad").asInt();
            
            context.getLogger().log("Procesando generación de envío para pedido: " + pedidoId);
            
            // Generar orden de envío con ChipMen
            String shippingOrderId = generarOrdenEnvioChipMen(pedidoId, productoId, cantidad, context);
            
            // Preparar evento para SNS
            Map<String, Object> sagaEvent = new HashMap<>();
            sagaEvent.put("eventId", java.util.UUID.randomUUID().toString());
            sagaEvent.put("sagaId", pedidoId);
            sagaEvent.put("source", "ShippingLambda");
            sagaEvent.put("timestamp", java.time.LocalDateTime.now().toString());
            
            if (shippingOrderId != null && !shippingOrderId.equals("ERROR")) {
                // Envío generado exitosamente
                sagaEvent.put("eventType", "SHIPPING_GENERATED");
                sagaEvent.put("payload", Map.of(
                        "pedidoId", pedidoId,
                        "shippingOrderId", shippingOrderId,
                        "status", "GENERATED"
                ));
                
                context.getLogger().log("Orden de envío generada: " + shippingOrderId + " para pedido: " + pedidoId);
                
            } else {
                // Fallo en generación de envío
                sagaEvent.put("eventType", "SHIPPING_GENERATION_FAILED");
                sagaEvent.put("payload", Map.of(
                        "pedidoId", pedidoId,
                        "status", "FAILED",
                        "reason", "CHIPMEN_ERROR"
                ));
                
                context.getLogger().log("Fallo en generación de envío para pedido: " + pedidoId);
            }
            
            // Publicar evento en SNS
            publicarEventoSNS(sagaEvent, context);
            
        } catch (Exception e) {
            context.getLogger().log("Error al procesar evento SNS: " + e.getMessage());
        }
    }
    
    /**
     * Conecta con el servicio ChipMen para generar orden de envío
     */
    private String generarOrdenEnvioChipMen(String pedidoId, String productoId, int cantidad, Context context) {
        try {
            context.getLogger().log("Conectando con ChipMen para pedido: " + pedidoId);
            
            // Preparar payload para ChipMen
            Map<String, Object> chipmenRequest = Map.of(
                    "pedidoId", pedidoId,
                    "productoId", productoId,
                    "cantidad", cantidad,
                    "tipoEnvio", "ESTANDAR",
                    "direccionEnvio", "Dirección por defecto"
            );
            
            String requestBody = objectMapper.writeValueAsString(chipmenRequest);
            
            // Realizar llamada HTTP a ChipMen
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CHIPMEN_SERVICE_URL + "/api/shipping/create"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                String shippingOrderId = responseJson.get("shippingOrderId").asText();
                
                context.getLogger().log("ChipMen respondió exitosamente: " + shippingOrderId);
                return shippingOrderId;
                
            } else {
                context.getLogger().log("ChipMen respondió con error: " + response.statusCode());
                return "ERROR";
            }
            
        } catch (Exception e) {
            context.getLogger().log("Error al conectar con ChipMen: " + e.getMessage());
            // En caso de error, simular ID de envío para continuar el demo
            return "SHIPPING_" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
    }
    
    /**
     * Publica el evento en SNS para continuar el proceso Saga
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
            context.getLogger().log("Evento publicado en SNS: " + result.messageId());
            
        } catch (Exception e) {
            context.getLogger().log("Error al publicar en SNS: " + e.getMessage());
        }
    }
}
