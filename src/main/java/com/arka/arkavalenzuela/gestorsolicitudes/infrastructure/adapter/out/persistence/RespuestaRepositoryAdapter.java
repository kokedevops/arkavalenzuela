package com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.out.persistence;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.RespuestaProveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out.RespuestaRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class RespuestaRepositoryAdapter implements RespuestaRepositoryPort {

    // Simulando una base de datos en memoria para las respuestas
    private final Map<String, RespuestaProveedor> respuestas = new ConcurrentHashMap<>();

    @Override
    public RespuestaProveedor guardarRespuesta(RespuestaProveedor respuesta) {
        respuestas.put(respuesta.getRespuestaId(), respuesta);
        return respuesta;
    }

    @Override
    public List<RespuestaProveedor> buscarPorSolicitud(String solicitudId) {
        return respuestas.values().stream()
                .filter(respuesta -> solicitudId.equals(respuesta.getSolicitudId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RespuestaProveedor> buscarPorProveedor(String proveedorId) {
        return respuestas.values().stream()
                .filter(respuesta -> proveedorId.equals(respuesta.getProveedorId()))
                .collect(Collectors.toList());
    }

    @Override
    public RespuestaProveedor actualizarRespuesta(RespuestaProveedor respuesta) {
        if (respuestas.containsKey(respuesta.getRespuestaId())) {
            respuestas.put(respuesta.getRespuestaId(), respuesta);
            return respuesta;
        }
        return null;
    }
}
