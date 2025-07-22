package com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.RespuestaProveedor;

import java.util.List;

public interface RespuestaRepositoryPort {
    
    /**
     * Guarda una respuesta de proveedor
     * @param respuesta Respuesta a guardar
     * @return Respuesta guardada
     */
    RespuestaProveedor guardarRespuesta(RespuestaProveedor respuesta);
    
    /**
     * Busca respuestas por solicitud
     * @param solicitudId ID de la solicitud
     * @return Lista de respuestas
     */
    List<RespuestaProveedor> buscarPorSolicitud(String solicitudId);
    
    /**
     * Busca respuestas por proveedor
     * @param proveedorId ID del proveedor
     * @return Lista de respuestas
     */
    List<RespuestaProveedor> buscarPorProveedor(String proveedorId);
    
    /**
     * Actualiza una respuesta
     * @param respuesta Respuesta con datos actualizados
     * @return Respuesta actualizada
     */
    RespuestaProveedor actualizarRespuesta(RespuestaProveedor respuesta);
}
