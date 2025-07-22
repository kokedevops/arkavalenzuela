package com.arka.arkavalenzuela.cotizador.infrastructure.adapter.out.persistence;

import com.arka.arkavalenzuela.cotizador.domain.model.CotizacionResponse;
import com.arka.arkavalenzuela.cotizador.domain.port.out.CotizacionRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CotizacionRepositoryAdapter implements CotizacionRepositoryPort {

    // Simulando una base de datos en memoria para las cotizaciones
    private final Map<String, CotizacionResponse> cotizaciones = new ConcurrentHashMap<>();

    @Override
    public CotizacionResponse guardarCotizacion(CotizacionResponse cotizacion) {
        cotizaciones.put(cotizacion.getCotizacionId(), cotizacion);
        return cotizacion;
    }

    @Override
    public CotizacionResponse buscarPorId(String cotizacionId) {
        return cotizaciones.get(cotizacionId);
    }

    @Override
    public CotizacionResponse actualizarCotizacion(CotizacionResponse cotizacion) {
        if (cotizaciones.containsKey(cotizacion.getCotizacionId())) {
            cotizaciones.put(cotizacion.getCotizacionId(), cotizacion);
            return cotizacion;
        }
        return null;
    }
}
