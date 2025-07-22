package com.arka.arkavalenzuela.gestorsolicitudes.application.usecase;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.RespuestaProveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.SolicitudProveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.port.in.GestorSolicitudesUseCase;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out.NotificacionServicePort;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out.ProveedorServicePort;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out.RespuestaRepositoryPort;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out.SolicitudRepositoryPort;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.Proveedor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GestorSolicitudesApplicationService implements GestorSolicitudesUseCase {

    private final SolicitudRepositoryPort solicitudRepository;
    private final RespuestaRepositoryPort respuestaRepository;
    private final ProveedorServicePort proveedorService;
    private final NotificacionServicePort notificacionService;

    public GestorSolicitudesApplicationService(SolicitudRepositoryPort solicitudRepository,
                                             RespuestaRepositoryPort respuestaRepository,
                                             ProveedorServicePort proveedorService,
                                             NotificacionServicePort notificacionService) {
        this.solicitudRepository = solicitudRepository;
        this.respuestaRepository = respuestaRepository;
        this.proveedorService = proveedorService;
        this.notificacionService = notificacionService;
    }

    @Override
    public SolicitudProveedor crearSolicitud(SolicitudProveedor solicitud) {
        // Generar ID único
        solicitud.setSolicitudId(UUID.randomUUID().toString());
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstado("ENVIADA");

        // Verificar que el proveedor esté activo
        if (!proveedorService.verificarProveedorActivo(solicitud.getProveedorId())) {
            throw new RuntimeException("El proveedor no está activo");
        }

        // Guardar solicitud
        SolicitudProveedor solicitudGuardada = solicitudRepository.guardarSolicitud(solicitud);

        // Enviar notificación al proveedor
        Proveedor proveedor = proveedorService.obtenerProveedorPorId(solicitud.getProveedorId());
        if (proveedor != null) {
            notificacionService.enviarNotificacionSolicitud(solicitudGuardada, proveedor.getEmail());
        }

        return solicitudGuardada;
    }

    @Override
    public List<SolicitudProveedor> enviarSolicitudMultiple(SolicitudProveedor solicitudBase, List<String> proveedorIds) {
        List<SolicitudProveedor> solicitudesEnviadas = new ArrayList<>();

        for (String proveedorId : proveedorIds) {
            // Crear una copia de la solicitud base para cada proveedor
            SolicitudProveedor solicitud = new SolicitudProveedor(
                    null, // Se generará automáticamente
                    proveedorId,
                    solicitudBase.getTipoSolicitud(),
                    solicitudBase.getProductos(),
                    "ENVIADA",
                    LocalDateTime.now(),
                    null,
                    solicitudBase.getObservaciones(),
                    solicitudBase.getPrioridad()
            );

            try {
                SolicitudProveedor solicitudCreada = crearSolicitud(solicitud);
                solicitudesEnviadas.add(solicitudCreada);
            } catch (Exception e) {
                // Log error pero continúa con los demás proveedores
                System.err.println("Error enviando solicitud a proveedor " + proveedorId + ": " + e.getMessage());
            }
        }

        return solicitudesEnviadas;
    }

    @Override
    public SolicitudProveedor consultarSolicitud(String solicitudId) {
        return solicitudRepository.buscarPorId(solicitudId);
    }

    @Override
    public List<SolicitudProveedor> obtenerSolicitudesPorEstado(String estado) {
        return solicitudRepository.buscarPorEstado(estado);
    }

    @Override
    public SolicitudProveedor actualizarEstadoSolicitud(String solicitudId, String nuevoEstado) {
        SolicitudProveedor solicitud = solicitudRepository.buscarPorId(solicitudId);
        if (solicitud != null) {
            String estadoAnterior = solicitud.getEstado();
            solicitud.setEstado(nuevoEstado);
            
            // Si se completa o cancela, actualizar fecha de respuesta
            if ("COMPLETADA".equals(nuevoEstado) || "CANCELADA".equals(nuevoEstado)) {
                solicitud.setFechaRespuesta(LocalDateTime.now());
            }

            SolicitudProveedor solicitudActualizada = solicitudRepository.actualizarSolicitud(solicitud);
            
            // Notificar cambio de estado si es significativo
            if (!estadoAnterior.equals(nuevoEstado)) {
                notificacionService.notificarCambioEstado(solicitudActualizada);
            }
            
            return solicitudActualizada;
        }
        return null;
    }

    @Override
    public RespuestaProveedor procesarRespuestaProveedor(RespuestaProveedor respuesta) {
        // Generar ID único para la respuesta
        respuesta.setRespuestaId(UUID.randomUUID().toString());
        respuesta.setFechaRespuesta(LocalDateTime.now());

        // Guardar respuesta
        RespuestaProveedor respuestaGuardada = respuestaRepository.guardarRespuesta(respuesta);

        // Actualizar estado de la solicitud
        actualizarEstadoSolicitud(respuesta.getSolicitudId(), "RESPONDIDA");

        return respuestaGuardada;
    }

    @Override
    public List<RespuestaProveedor> obtenerRespuestasSolicitud(String solicitudId) {
        return respuestaRepository.buscarPorSolicitud(solicitudId);
    }
}
