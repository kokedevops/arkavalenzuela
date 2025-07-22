package com.arka.arkavalenzuela.gestorsolicitudes.domain.port.in;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.RespuestaProveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.SolicitudProveedor;

import java.util.List;

public interface GestorSolicitudesUseCase {
    
    /**
     * Crea y envía una solicitud a un proveedor específico
     * @param solicitud Datos de la solicitud
     * @return Solicitud creada
     */
    SolicitudProveedor crearSolicitud(SolicitudProveedor solicitud);
    
    /**
     * Envía una solicitud a múltiples proveedores
     * @param solicitud Datos de la solicitud base
     * @param proveedorIds Lista de IDs de proveedores
     * @return Lista de solicitudes enviadas
     */
    List<SolicitudProveedor> enviarSolicitudMultiple(SolicitudProveedor solicitud, List<String> proveedorIds);
    
    /**
     * Consulta el estado de una solicitud
     * @param solicitudId ID de la solicitud
     * @return Solicitud encontrada
     */
    SolicitudProveedor consultarSolicitud(String solicitudId);
    
    /**
     * Obtiene todas las solicitudes por estado
     * @param estado Estado de las solicitudes
     * @return Lista de solicitudes
     */
    List<SolicitudProveedor> obtenerSolicitudesPorEstado(String estado);
    
    /**
     * Actualiza el estado de una solicitud
     * @param solicitudId ID de la solicitud
     * @param nuevoEstado Nuevo estado
     * @return Solicitud actualizada
     */
    SolicitudProveedor actualizarEstadoSolicitud(String solicitudId, String nuevoEstado);
    
    /**
     * Procesa la respuesta de un proveedor
     * @param respuesta Respuesta del proveedor
     * @return Respuesta procesada
     */
    RespuestaProveedor procesarRespuestaProveedor(RespuestaProveedor respuesta);
    
    /**
     * Obtiene todas las respuestas de una solicitud
     * @param solicitudId ID de la solicitud
     * @return Lista de respuestas
     */
    List<RespuestaProveedor> obtenerRespuestasSolicitud(String solicitudId);
}
