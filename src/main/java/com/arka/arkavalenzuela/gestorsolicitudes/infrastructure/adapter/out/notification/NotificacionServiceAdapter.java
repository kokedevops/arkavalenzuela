package com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.out.notification;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.SolicitudProveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out.NotificacionServicePort;
import org.springframework.stereotype.Service;

@Service
public class NotificacionServiceAdapter implements NotificacionServicePort {

    @Override
    public boolean enviarNotificacionSolicitud(SolicitudProveedor solicitud, String proveedorEmail) {
        try {
            // Simular envío de email
            System.out.println("=== NOTIFICACIÓN ENVIADA ===");
            System.out.println("Para: " + proveedorEmail);
            System.out.println("Asunto: Nueva Solicitud de Productos - ID: " + solicitud.getSolicitudId());
            System.out.println("Estimado proveedor,");
            System.out.println("Se ha generado una nueva solicitud de productos con ID: " + solicitud.getSolicitudId());
            System.out.println("Tipo de solicitud: " + solicitud.getTipoSolicitud());
            System.out.println("Prioridad: " + solicitud.getPrioridad());
            System.out.println("Productos solicitados: " + solicitud.getProductos().size());
            if (solicitud.getObservaciones() != null) {
                System.out.println("Observaciones: " + solicitud.getObservaciones());
            }
            System.out.println("Por favor, revise y responda a esta solicitud a la brevedad posible.");
            System.out.println("=========================");
            
            return true; // Simular envío exitoso
        } catch (Exception e) {
            System.err.println("Error enviando notificación: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean enviarRecordatorio(String solicitudId, String proveedorEmail) {
        try {
            // Simular envío de recordatorio
            System.out.println("=== RECORDATORIO ENVIADO ===");
            System.out.println("Para: " + proveedorEmail);
            System.out.println("Asunto: Recordatorio - Solicitud Pendiente ID: " + solicitudId);
            System.out.println("Estimado proveedor,");
            System.out.println("Le recordamos que tiene una solicitud pendiente de respuesta con ID: " + solicitudId);
            System.out.println("Por favor, procese esta solicitud lo antes posible.");
            System.out.println("===========================");
            
            return true;
        } catch (Exception e) {
            System.err.println("Error enviando recordatorio: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean notificarCambioEstado(SolicitudProveedor solicitud) {
        try {
            // Simular notificación interna de cambio de estado
            System.out.println("=== CAMBIO DE ESTADO ===");
            System.out.println("Solicitud ID: " + solicitud.getSolicitudId());
            System.out.println("Nuevo estado: " + solicitud.getEstado());
            System.out.println("Proveedor: " + solicitud.getProveedorId());
            System.out.println("Fecha: " + solicitud.getFechaSolicitud());
            System.out.println("=======================");
            
            return true;
        } catch (Exception e) {
            System.err.println("Error notificando cambio de estado: " + e.getMessage());
            return false;
        }
    }
}
