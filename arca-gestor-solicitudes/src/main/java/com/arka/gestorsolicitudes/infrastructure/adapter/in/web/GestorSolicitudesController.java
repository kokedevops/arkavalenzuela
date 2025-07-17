package com.arka.gestorsolicitudes.infrastructure.adapter.in.web;

import com.arka.gestorsolicitudes.application.service.GestorSolicitudesService;
import com.arka.gestorsolicitudes.domain.model.SolicitudProveedor;
import com.arka.gestorsolicitudes.domain.model.RespuestaProveedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class GestorSolicitudesController {

    private final GestorSolicitudesService gestorService;

    @Autowired
    public GestorSolicitudesController(GestorSolicitudesService gestorService) {
        this.gestorService = gestorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SolicitudProveedor> crearSolicitud(@RequestBody SolicitudProveedor solicitud) {
        return gestorService.crearSolicitud(solicitud);
    }

    @PostMapping("/{solicitudId}/enviar/{proveedorId}")
    public Mono<SolicitudProveedor> enviarSolicitudAProveedor(
            @PathVariable String solicitudId,
            @PathVariable String proveedorId) {
        return gestorService.enviarSolicitudAProveedor(solicitudId, proveedorId);
    }

    @GetMapping("/{solicitudId}/respuestas")
    public Flux<RespuestaProveedor> obtenerRespuestasProveedor(@PathVariable String solicitudId) {
        return gestorService.obtenerRespuestasProveedor(solicitudId);
    }

    @PostMapping("/respuestas")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<RespuestaProveedor> procesarRespuestaProveedor(@RequestBody RespuestaProveedor respuesta) {
        return gestorService.procesarRespuestaProveedor(respuesta);
    }

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("Arca Gestor de Solicitudes está funcionando correctamente");
    }
}
