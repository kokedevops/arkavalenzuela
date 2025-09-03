package com.arka.lambda.inventory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Lambda 1: Función para reserva de inventario en el patrón Saga
 * Esta función simula la conexión al servicio S2 para reservar inventario
 */
public class InventoryReservationLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SnsClient snsClient = SnsClient.builder()
            .region(Region.US_EAST_1)
            .build();
    
    private static final String SNS_TOPIC_ARN = System.getenv("SNS_TOPIC_ARN");
    private static final String S2_SERVICE_URL = System.getenv("S2_SERVICE_URL");
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            // Parse del payload del pedido
            JsonNode pedido = objectMapper.readTree(input.getBody());
            String pedidoId = pedido.get("pedidoId").asText();
            String productoId = pedido.get("productoId").asText();
            int cantidad = pedido.get("cantidad").asInt();
            
            context.getLogger().log("Procesando reserva de inventario para pedido: " + pedidoId);
            
            // Simulación de llamada al servicio S2 (Inventario)
            boolean inventarioReservado = reservarInventarioS2(productoId, cantidad, context);
            
            // Preparar evento para SNS
            Map<String, Object> sagaEvent = new HashMap<>();
            sagaEvent.put("eventId", java.util.UUID.randomUUID().toString());
            sagaEvent.put("sagaId", pedidoId);
            sagaEvent.put("source", "InventoryLambda");
            sagaEvent.put("timestamp", java.time.LocalDateTime.now().toString());
            
            if (inventarioReservado) {
                // Inventario reservado exitosamente
                sagaEvent.put("eventType", "INVENTORY_RESERVED");
                sagaEvent.put("payload", Map.of(
                        "pedidoId", pedidoId,
                        "productoId", productoId,
                        "cantidad", cantidad,
                        "status", "RESERVED"
                ));
                
                context.getLogger().log("Inventario reservado exitosamente para pedido: " + pedidoId);
                
            } else {
                // Fallo en reserva de inventario
                sagaEvent.put("eventType", "INVENTORY_RESERVATION_FAILED");
                sagaEvent.put("payload", Map.of(
                        "pedidoId", pedidoId,
                        "productoId", productoId,
                        "cantidad", cantidad,
                        "status", "FAILED",
                        "reason", "INSUFFICIENT_STOCK"
                ));
                
                context.getLogger().log("Fallo en reserva de inventario para pedido: " + pedidoId);
            }
            
            // Publicar evento en SNS
            publicarEventoSNS(sagaEvent, context);
            
            // Respuesta de la Lambda
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(200);
            response.setBody(objectMapper.writeValueAsString(Map.of(
                    "success", inventarioReservado,
                    "pedidoId", pedidoId,
                    "message", inventarioReservado ? "Inventario reservado" : "Inventario insuficiente"
            )));
            
            return response;
            
        } catch (Exception e) {
            context.getLogger().log("Error en InventoryReservationLambda: " + e.getMessage());
            
            APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
            errorResponse.setStatusCode(500);
            errorResponse.setBody("{\"error\":\"Error interno del servidor\"}");
            return errorResponse;
        }
    }
    
    /**
     * Simula la conexión con el servicio S2 para reservar inventario
     */
    private boolean reservarInventarioS2(String productoId, int cantidad, Context context) {
        try {
            // En un escenario real, aquí harías una llamada HTTP al servicio S2
            // Por ahora simulamos la lógica de reserva
            
            context.getLogger().log("Conectando con servicio S2 para producto: " + productoId + ", cantidad: " + cantidad);
            
            // Simulación: productos con ID que termina en número par tienen stock
            boolean hayStock = productoId.hashCode() % 2 == 0;
            
            if (hayStock && cantidad <= 10) { // Simulamos stock máximo de 10
                // Simular tiempo de procesamiento
                Thread.sleep(1000);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            context.getLogger().log("Error al conectar con S2: " + e.getMessage());
            return false;
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
