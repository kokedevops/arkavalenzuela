package com.arka.gateway.controller;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Manejador global de errores para el API Gateway
 */
@Configuration
@Order(-1)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        var response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        String errorResponse = "{\n" +
            "  \"error\": true,\n" +
            "  \"status\": 404,\n" +
            "  \"message\": \"Route not found in API Gateway\",\n" +
            "  \"service\": \"api-gateway\",\n" +
            "  \"timestamp\": " + System.currentTimeMillis() + ",\n" +
            "  \"suggestion\": \"Visit / for available routes and services\",\n" +
            "  \"availableEndpoints\": {\n" +
            "    \"home\": \"/\",\n" +
            "    \"health\": \"/health\",\n" +
            "    \"routes\": \"/routes\",\n" +
            "    \"authentication\": \"/auth/login\",\n" +
            "    \"mainApp\": \"http://localhost:8888\"\n" +
            "  }\n" +
            "}";
        
        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
