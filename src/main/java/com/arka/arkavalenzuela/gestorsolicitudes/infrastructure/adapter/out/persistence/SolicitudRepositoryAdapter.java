package com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.out.persistence;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.SolicitudProveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out.SolicitudRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SolicitudRepositoryAdapter implements SolicitudRepositoryPort {

    // Simulando una base de datos en memoria para las solicitudes
    private final Map<String, SolicitudProveedor> solicitudes = new ConcurrentHashMap<>();

    @Override
    public SolicitudProveedor guardarSolicitud(SolicitudProveedor solicitud) {
        solicitudes.put(solicitud.getSolicitudId(), solicitud);
        return solicitud;
    }

    @Override
    public SolicitudProveedor buscarPorId(String solicitudId) {
        return solicitudes.get(solicitudId);
    }

    @Override
    public List<SolicitudProveedor> buscarPorEstado(String estado) {
        return solicitudes.values().stream()
                .filter(solicitud -> estado.equals(solicitud.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SolicitudProveedor> buscarPorProveedor(String proveedorId) {
        return solicitudes.values().stream()
                .filter(solicitud -> proveedorId.equals(solicitud.getProveedorId()))
                .collect(Collectors.toList());
    }

    @Override
    public SolicitudProveedor actualizarSolicitud(SolicitudProveedor solicitud) {
        if (solicitudes.containsKey(solicitud.getSolicitudId())) {
            solicitudes.put(solicitud.getSolicitudId(), solicitud);
            return solicitud;
        }
        return null;
    }

    @Override
    public void eliminarSolicitud(String solicitudId) {
        solicitudes.remove(solicitudId);
    }
}
