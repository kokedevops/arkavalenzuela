package com.arka.gestorsolicitudes.application.service;

import com.arka.gestorsolicitudes.domain.model.SolicitudProveedor;
import com.arka.gestorsolicitudes.domain.model.RespuestaProveedor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class GestorSolicitudesServiceImpl implements GestorSolicitudesService {

    private final WebClient webClient;

    public GestorSolicitudesServiceImpl() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8083") // URL base de proveedores simulados
                .build();
    }

    @Override
    public Mono<SolicitudProveedor> crearSolicitud(SolicitudProveedor solicitud) {
        return Mono.fromCallable(() -> {
            // Asignar ID único si no tiene
            if (solicitud.getSolicitudId() == null) {
                solicitud.setSolicitudId(UUID.randomUUID().toString());
            }
            
            // En una implementación real, guardaríamos en base de datos
            return solicitud;
        });
    }

    @Override
    public Mono<SolicitudProveedor> enviarSolicitudAProveedor(String solicitudId, String proveedorId) {
        return Mono.fromCallable(() -> {
            // Simulación de envío a proveedor
            // En una implementación real, haríamos una llamada HTTP al proveedor
            
            // Simulamos una solicitud
            var solicitud = new SolicitudProveedor(solicitudId, proveedorId, "cliente-demo", new ArrayList<>());
            solicitud.setEstado("ENVIADA");
            
            return solicitud;
        });
    }

    @Override
    public Flux<RespuestaProveedor> obtenerRespuestasProveedor(String solicitudId) {
        return Flux.fromIterable(new ArrayList<RespuestaProveedor>())
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<RespuestaProveedor> procesarRespuestaProveedor(RespuestaProveedor respuesta) {
        return Mono.fromCallable(() -> {
            // Procesar respuesta del proveedor
            // En una implementación real, validaríamos y guardaríamos la respuesta
            respuesta.setEstado("PROCESADA");
            return respuesta;
        });
    }
}
