package com.arka.arkavalenzuela.cotizador.domain.port.out;

import com.arka.arkavalenzuela.cotizador.domain.model.CotizacionResponse;

public interface CotizacionRepositoryPort {
    
    /**
     * Guarda una cotización
     * @param cotizacion Cotización a guardar
     * @return Cotización guardada
     */
    CotizacionResponse guardarCotizacion(CotizacionResponse cotizacion);
    
    /**
     * Busca una cotización por ID
     * @param cotizacionId ID de la cotización
     * @return Cotización encontrada o null
     */
    CotizacionResponse buscarPorId(String cotizacionId);
    
    /**
     * Actualiza una cotización existente
     * @param cotizacion Cotización con datos actualizados
     * @return Cotización actualizada
     */
    CotizacionResponse actualizarCotizacion(CotizacionResponse cotizacion);
}
