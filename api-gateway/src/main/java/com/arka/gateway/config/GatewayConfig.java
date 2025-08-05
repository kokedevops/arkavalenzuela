package com.arka.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Hello World Service routes
            .route("hello-world", r -> r
                .path("/api/hello/**")
                .filters(f -> f.stripPrefix(2))
                .uri("lb://hello-world-service")
            )
            // Cotizador Service routes
            .route("cotizador", r -> r
                .path("/api/cotizador/**")
                .filters(f -> f.stripPrefix(2))
                .uri("lb://arca-cotizador")
            )
            // Gestor Solicitudes Service routes
            .route("gestor-solicitudes", r -> r
                .path("/api/gestor/**")
                .filters(f -> f.stripPrefix(2))
                .uri("lb://arca-gestor-solicitudes")
            )
            // Eureka Dashboard route
            .route("eureka", r -> r
                .path("/eureka/**")
                .uri("http://localhost:8761")
            )
            .build();
    }
}
