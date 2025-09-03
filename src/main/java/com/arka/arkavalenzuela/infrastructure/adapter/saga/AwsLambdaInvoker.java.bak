package com.arka.arkavalenzuela.infrastructure.adapter.saga;

import com.arka.arkavalenzuela.domain.model.saga.SagaPedido;
import com.arka.arkavalenzuela.domain.port.out.saga.LambdaInvoker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.nio.charset.StandardCharsets;

/**
 * Adaptador para invocar funciones AWS Lambda
 */
@Component
public class AwsLambdaInvoker implements LambdaInvoker {
    
    private static final Logger logger = LoggerFactory.getLogger(AwsLambdaInvoker.class);
    
    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;
    private final String inventoryLambdaName;
    private final String shippingLambdaName;
    private final String notificationLambdaName;
    
    public AwsLambdaInvoker(@Value("${aws.region}") String awsRegion,
                           @Value("${aws.lambda.inventory-function-name}") String inventoryLambdaName,
                           @Value("${aws.lambda.shipping-function-name}") String shippingLambdaName,
                           @Value("${aws.lambda.notification-function-name}") String notificationLambdaName,
                           ObjectMapper objectMapper) {
        this.lambdaClient = LambdaClient.builder()
                .region(Region.of(awsRegion))
                .build();
        this.objectMapper = objectMapper;
        this.inventoryLambdaName = inventoryLambdaName;
        this.shippingLambdaName = shippingLambdaName;
        this.notificationLambdaName = notificationLambdaName;
    }
    
    @Override
    public Mono<String> invokeInventoryReservation(SagaPedido pedido) {
        logger.info("Invocando Lambda de inventario para pedido: {}", pedido.getPedidoId());
        return invokeLambda(inventoryLambdaName, serializePedido(pedido));
    }
    
    @Override
    public Mono<String> invokeShippingGeneration(SagaPedido pedido) {
        logger.info("Invocando Lambda de envío para pedido: {}", pedido.getPedidoId());
        return invokeLambda(shippingLambdaName, serializePedido(pedido));
    }
    
    @Override
    public Mono<String> invokeCustomNotification(SagaPedido pedido) {
        logger.info("Invocando Lambda de notificación para pedido: {}", pedido.getPedidoId());
        return invokeLambda(notificationLambdaName, serializePedido(pedido));
    }
    
    @Override
    public Mono<String> invokeLambda(String functionName, String payload) {
        return Mono.fromCallable(() -> {
            try {
                InvokeRequest invokeRequest = InvokeRequest.builder()
                        .functionName(functionName)
                        .invocationType(InvocationType.EVENT) // Invocación asíncrona
                        .payload(SdkBytes.fromString(payload, StandardCharsets.UTF_8))
                        .build();
                
                InvokeResponse response = lambdaClient.invoke(invokeRequest);
                
                String responsePayload = response.payload().asUtf8String();
                
                logger.info("Lambda {} invocada exitosamente. Status: {}, Payload: {}", 
                           functionName, response.statusCode(), responsePayload);
                
                return responsePayload;
                
            } catch (SdkException e) {
                logger.error("Error al invocar Lambda {}: {}", functionName, e.getMessage());
                throw new RuntimeException("Error al invocar Lambda: " + functionName, e);
            }
        })
        .doOnError(error -> logger.error("Error en invocación de Lambda {}: {}", functionName, error.getMessage()));
    }
    
    private String serializePedido(SagaPedido pedido) {
        try {
            return objectMapper.writeValueAsString(pedido);
        } catch (JsonProcessingException e) {
            logger.error("Error al serializar pedido para Lambda", e);
            throw new RuntimeException("Error al serializar pedido", e);
        }
    }
}
