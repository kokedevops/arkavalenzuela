package com.arka.gestorsolicitudes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class GestorController {

    @GetMapping
    public Mono<String> home() {
        return Mono.just("Gestor Solicitudes Service is running!");
    }

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("Gestor Solicitudes Service is UP!");
    }

    @GetMapping("/info")
    public Mono<GestorInfo> info() {
        return Mono.just(new GestorInfo(
            "Arca Gestor Solicitudes Service",
            "1.0.0",
            "Servicio de gestión de solicitudes reactivo",
            System.currentTimeMillis()
        ));
    }

    public static class GestorInfo {
        private String serviceName;
        private String version;
        private String description;
        private long timestamp;

        public GestorInfo(String serviceName, String version, String description, long timestamp) {
            this.serviceName = serviceName;
            this.version = version;
            this.description = description;
            this.timestamp = timestamp;
        }

        // Getters
        public String getServiceName() { return serviceName; }
        public String getVersion() { return version; }
        public String getDescription() { return description; }
        public long getTimestamp() { return timestamp; }

        // Setters
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public void setVersion(String version) { this.version = version; }
        public void setDescription(String description) { this.description = description; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
