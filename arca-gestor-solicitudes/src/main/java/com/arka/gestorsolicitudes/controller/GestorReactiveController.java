package com.arka.gestorsolicitudes.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/")
public class GestorReactiveController {

    @Value("${server.port}")
    private String port;

    @GetMapping
    public Mono<String> home() {
        return Mono.just("Arca Gestor Solicitudes Service - Reactive with WebFlux!")
                .delayElement(Duration.ofMillis(100));
    }

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.fromCallable(() -> {
            try {
                String hostName = InetAddress.getLocalHost().getHostName();
                return String.format("Gestor Solicitudes Service is UP! Running on %s:%s (Reactive)", hostName, port);
            } catch (UnknownHostException e) {
                return "Gestor Solicitudes Service is UP! (Reactive)";
            }
        });
    }

    @GetMapping("/info")
    public Mono<GestorInfo> info() {
        return Mono.fromCallable(() -> {
            try {
                String hostName = InetAddress.getLocalHost().getHostName();
                return new GestorInfo(
                    "Arca Gestor Solicitudes Service",
                    "1.0.0",
                    "Servicio de gestión de solicitudes reactivo con WebFlux",
                    hostName,
                    port,
                    System.currentTimeMillis()
                );
            } catch (UnknownHostException e) {
                return new GestorInfo(
                    "Arca Gestor Solicitudes Service",
                    "1.0.0",
                    "Servicio de gestión de solicitudes reactivo con WebFlux",
                    "unknown",
                    port,
                    System.currentTimeMillis()
                );
            }
        });
    }

    @GetMapping("/stream")
    public Flux<String> stream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> "Gestor Event #" + i + " from port " + port)
                .take(10);
    }

    @GetMapping("/reactive-test")
    public Flux<SolicitudEvent> reactiveTest() {
        return Flux.range(1, 5)
                .delayElements(Duration.ofMillis(700))
                .map(i -> new SolicitudEvent(
                    "SOL-" + i,
                    "Solicitud #" + i,
                    "PROCESSING",
                    System.currentTimeMillis()
                ));
    }

    public static class GestorInfo {
        private String serviceName;
        private String version;
        private String description;
        private String hostname;
        private String port;
        private long timestamp;

        public GestorInfo(String serviceName, String version, String description, 
                         String hostname, String port, long timestamp) {
            this.serviceName = serviceName;
            this.version = version;
            this.description = description;
            this.hostname = hostname;
            this.port = port;
            this.timestamp = timestamp;
        }

        // Getters
        public String getServiceName() { return serviceName; }
        public String getVersion() { return version; }
        public String getDescription() { return description; }
        public String getHostname() { return hostname; }
        public String getPort() { return port; }
        public long getTimestamp() { return timestamp; }

        // Setters
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public void setVersion(String version) { this.version = version; }
        public void setDescription(String description) { this.description = description; }
        public void setHostname(String hostname) { this.hostname = hostname; }
        public void setPort(String port) { this.port = port; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class SolicitudEvent {
        private String id;
        private String description;
        private String status;
        private long timestamp;

        public SolicitudEvent(String id, String description, String status, long timestamp) {
            this.id = id;
            this.description = description;
            this.status = status;
            this.timestamp = timestamp;
        }

        // Getters
        public String getId() { return id; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public long getTimestamp() { return timestamp; }

        // Setters
        public void setId(String id) { this.id = id; }
        public void setDescription(String description) { this.description = description; }
        public void setStatus(String status) { this.status = status; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
