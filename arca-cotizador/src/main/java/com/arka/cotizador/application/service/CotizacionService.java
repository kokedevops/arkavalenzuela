package com.arka.cotizador.application.service;

import com.arka.cotizador.domain.model.CotizacionRequest;
import com.arka.cotizador.domain.model.CotizacionResponse;
import reactor.core.publisher.Mono;

public interface CotizacionService {
    Mono<CotizacionResponse> generarCotizacion(CotizacionRequest request);
    Mono<CotizacionResponse> obtenerCotizacion(String cotizacionId);
}
