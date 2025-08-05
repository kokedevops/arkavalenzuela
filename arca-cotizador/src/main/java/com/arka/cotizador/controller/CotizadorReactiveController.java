package com.arka.cotizador.controller;

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
public class CotizadorReactiveController {

    @Value("${server.port}")
    private String port;

    @GetMapping
    public Mono<String> home() {
        return Mono.just("Arca Cotizador Service - Reactive with WebFlux!")
                .delayElement(Duration.ofMillis(100)); // Simula procesamiento asíncrono
    }

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.fromCallable(() -> {
            try {
                String hostName = InetAddress.getLocalHost().getHostName();
                return String.format("Cotizador Service is UP! Running on %s:%s (Reactive)", hostName, port);
            } catch (UnknownHostException e) {
                return "Cotizador Service is UP! (Reactive)";
            }
        });
    }

    @GetMapping("/info")
    public Mono<CotizadorInfo> info() {
        return Mono.fromCallable(() -> {
            try {
                String hostName = InetAddress.getLocalHost().getHostName();
                return new CotizadorInfo(
                    "Arca Cotizador Service",
                    "1.0.0",
                    "Servicio de cotización reactivo con WebFlux",
                    hostName,
                    port,
                    System.currentTimeMillis()
                );
            } catch (UnknownHostException e) {
                return new CotizadorInfo(
                    "Arca Cotizador Service",
                    "1.0.0",
                    "Servicio de cotización reactivo con WebFlux",
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
                .map(i -> "Cotizador Event #" + i + " from port " + port)
                .take(10); // Solo envía 10 eventos
    }

    @GetMapping("/reactive-test")
    public Flux<CotizacionEvent> reactiveTest() {
        return Flux.range(1, 5)
                .delayElements(Duration.ofMillis(500))
                .map(i -> new CotizacionEvent(
                    "COTIZ-" + i,
                    "Cotización #" + i,
                    "PENDING",
                    System.currentTimeMillis()
                ));
    }

    public static class CotizadorInfo {
        private String serviceName;
        private String version;
        private String description;
        private String hostname;
        private String port;
        private long timestamp;

        public CotizadorInfo(String serviceName, String version, String description, 
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

    public static class CotizacionEvent {
        private String id;
        private String description;
        private String status;
        private long timestamp;

        public CotizacionEvent(String id, String description, String status, long timestamp) {
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
