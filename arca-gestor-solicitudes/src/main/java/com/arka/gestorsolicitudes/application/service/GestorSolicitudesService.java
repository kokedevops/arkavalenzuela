package com.arka.gestorsolicitudes.application.service;

import com.arka.gestorsolicitudes.domain.model.SolicitudProveedor;
import com.arka.gestorsolicitudes.domain.model.RespuestaProveedor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GestorSolicitudesService {
    Mono<SolicitudProveedor> crearSolicitud(SolicitudProveedor solicitud);
    Mono<SolicitudProveedor> enviarSolicitudAProveedor(String solicitudId, String proveedorId);
    Flux<RespuestaProveedor> obtenerRespuestasProveedor(String solicitudId);
    Mono<RespuestaProveedor> procesarRespuestaProveedor(RespuestaProveedor respuesta);
}
