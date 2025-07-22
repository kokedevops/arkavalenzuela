package com.arka.arkavalenzuela.cotizador.domain.port.in;

import com.arka.arkavalenzuela.cotizador.domain.model.CotizacionRequest;
import com.arka.arkavalenzuela.cotizador.domain.model.CotizacionResponse;

public interface CotizadorUseCase {
    
    /**
     * Genera una cotización basada en el request recibido
     * @param request Datos de la solicitud de cotización
     * @return Cotización generada
     */
    CotizacionResponse generarCotizacion(CotizacionRequest request);
    
    /**
     * Consulta una cotización por su ID
     * @param cotizacionId ID de la cotización
     * @return Cotización encontrada
     */
    CotizacionResponse consultarCotizacion(String cotizacionId);
    
    /**
     * Actualiza el estado de una cotización
     * @param cotizacionId ID de la cotización
     * @param nuevoEstado Nuevo estado
     * @return Cotización actualizada
     */
    CotizacionResponse actualizarEstadoCotizacion(String cotizacionId, String nuevoEstado);
}
