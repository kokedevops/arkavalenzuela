package com.arka.gestorsolicitudes.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controlador de prueba para verificar conectividad
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/ping")
    public Mono<ResponseEntity<String>> ping() {
        return Mono.just(ResponseEntity.ok("¡Arca-Gestor-Solicitudes está funcionando! 🎉"));
    }
    
    @GetMapping("/status")
    public Mono<ResponseEntity<Object>> status() {
        return Mono.just(ResponseEntity.ok(new Object() {
            public final String service = "arca-gestor-solicitudes";
            public final String status = "UP";
            public final String timestamp = java.time.LocalDateTime.now().toString();
            public final String version = "1.0.0";
        }));
    }
}
