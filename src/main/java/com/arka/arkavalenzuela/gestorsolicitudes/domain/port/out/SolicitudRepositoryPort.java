package com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.SolicitudProveedor;

import java.util.List;

public interface SolicitudRepositoryPort {
    
    /**
     * Guarda una solicitud
     * @param solicitud Solicitud a guardar
     * @return Solicitud guardada
     */
    SolicitudProveedor guardarSolicitud(SolicitudProveedor solicitud);
    
    /**
     * Busca una solicitud por ID
     * @param solicitudId ID de la solicitud
     * @return Solicitud encontrada o null
     */
    SolicitudProveedor buscarPorId(String solicitudId);
    
    /**
     * Busca solicitudes por estado
     * @param estado Estado de las solicitudes
     * @return Lista de solicitudes
     */
    List<SolicitudProveedor> buscarPorEstado(String estado);
    
    /**
     * Busca solicitudes por proveedor
     * @param proveedorId ID del proveedor
     * @return Lista de solicitudes
     */
    List<SolicitudProveedor> buscarPorProveedor(String proveedorId);
    
    /**
     * Actualiza una solicitud
     * @param solicitud Solicitud con datos actualizados
     * @return Solicitud actualizada
     */
    SolicitudProveedor actualizarSolicitud(SolicitudProveedor solicitud);
    
    /**
     * Elimina una solicitud
     * @param solicitudId ID de la solicitud a eliminar
     */
    void eliminarSolicitud(String solicitudId);
}
