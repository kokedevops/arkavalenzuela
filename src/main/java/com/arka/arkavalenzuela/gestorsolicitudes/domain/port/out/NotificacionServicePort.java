package com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.SolicitudProveedor;

public interface NotificacionServicePort {
    
    /**
     * Envía notificación de solicitud al proveedor
     * @param solicitud Datos de la solicitud
     * @param proveedorEmail Email del proveedor
     * @return true si se envió correctamente
     */
    boolean enviarNotificacionSolicitud(SolicitudProveedor solicitud, String proveedorEmail);
    
    /**
     * Envía recordatorio al proveedor
     * @param solicitudId ID de la solicitud
     * @param proveedorEmail Email del proveedor
     * @return true si se envió correctamente
     */
    boolean enviarRecordatorio(String solicitudId, String proveedorEmail);
    
    /**
     * Notifica cambio de estado de solicitud
     * @param solicitud Solicitud actualizada
     * @return true si se envió correctamente
     */
    boolean notificarCambioEstado(SolicitudProveedor solicitud);
}
