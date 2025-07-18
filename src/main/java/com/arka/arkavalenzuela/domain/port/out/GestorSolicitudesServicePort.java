package com.arka.arkavalenzuela.domain.port.out;

import com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto.SolicitudRequestDto;
import com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto.SolicitudResponseDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GestorSolicitudesServicePort {
    
    /**
     * Crea una nueva solicitud
     */
    CompletableFuture<SolicitudResponseDto> crearSolicitud(SolicitudRequestDto request);
    
    /**
     * Obtiene una solicitud por ID
     */
    CompletableFuture<SolicitudResponseDto> obtenerSolicitud(String solicitudId);
    
    /**
     * Obtiene todas las solicitudes de un cliente
     */
    CompletableFuture<List<SolicitudResponseDto>> obtenerSolicitudesPorCliente(Long customerId);
    
    /**
     * Actualiza el estado de una solicitud
     */
    CompletableFuture<SolicitudResponseDto> actualizarEstadoSolicitud(String solicitudId, String nuevoEstado);
    
    /**
     * Obtiene solicitudes por estado
     */
    CompletableFuture<List<SolicitudResponseDto>> obtenerSolicitudesPorEstado(String estado);
    
    /**
     * Verifica si el servicio está disponible
     */
    CompletableFuture<Boolean> isServiceAvailable();
}
