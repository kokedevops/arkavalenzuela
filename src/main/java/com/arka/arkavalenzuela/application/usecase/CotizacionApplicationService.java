package com.arka.arkavalenzuela.application.usecase;

import com.arka.arkavalenzuela.domain.model.Order;
import com.arka.arkavalenzuela.domain.model.Product;
import com.arka.arkavalenzuela.domain.port.out.CotizadorServicePort;
import com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto.CotizacionRequestDto;
import com.arka.arkavalenzuela.infrastructure.adapter.out.microservice.dto.CotizacionResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CotizacionApplicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CotizacionApplicationService.class);
    
    private final CotizadorServicePort cotizadorServicePort;
    
    public CotizacionApplicationService(CotizadorServicePort cotizadorServicePort) {
        this.cotizadorServicePort = cotizadorServicePort;
    }
    
    /**
     * Solicita una cotización basada en una orden
     */
    public CompletableFuture<CotizacionResponseDto> solicitarCotizacion(Order order) {
        try {
            logger.info("Solicitando cotización para orden del cliente: {}", order.getCliente().getId());
            
            CotizacionRequestDto request = crearCotizacionRequest(order);
            
            return cotizadorServicePort.crearCotizacion(request)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Error al solicitar cotización: {}", throwable.getMessage());
                    } else {
                        logger.info("Cotización solicitada exitosamente: {}", result.getCotizacionId());
                    }
                });
                
        } catch (Exception ex) {
            logger.error("Error al procesar solicitud de cotización: {}", ex.getMessage());
            CompletableFuture<CotizacionResponseDto> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(ex);
            return failedFuture;
        }
    }
    
    /**
     * Solicita cotización para productos específicos
     */
    public CompletableFuture<CotizacionResponseDto> solicitarCotizacionProductos(Long customerId, List<Product> productos, String tipoCliente) {
        try {
            logger.info("Solicitando cotización para {} productos del cliente: {}", productos.size(), customerId);
            
            List<CotizacionRequestDto.ProductoCotizacionDto> productosDto = productos.stream()
                .map(producto -> new CotizacionRequestDto.ProductoCotizacionDto(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getPrecioUnitario(),
                    1 // Cantidad por defecto
                ))
                .collect(Collectors.toList());
            
            CotizacionRequestDto request = new CotizacionRequestDto(customerId, productosDto, tipoCliente);
            
            return cotizadorServicePort.crearCotizacion(request);
            
        } catch (Exception ex) {
            logger.error("Error al procesar solicitud de cotización para productos: {}", ex.getMessage());
            CompletableFuture<CotizacionResponseDto> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(ex);
            return failedFuture;
        }
    }
    
    /**
     * Obtiene cotizaciones de un cliente
     */
    public CompletableFuture<List<CotizacionResponseDto>> obtenerCotizacionesCliente(Long customerId) {
        logger.info("Obteniendo cotizaciones para cliente: {}", customerId);
        return cotizadorServicePort.obtenerCotizacionesPorCliente(customerId);
    }
    
    /**
     * Obtiene una cotización específica
     */
    public CompletableFuture<CotizacionResponseDto> obtenerCotizacion(String cotizacionId) {
        logger.info("Obteniendo cotización: {}", cotizacionId);
        return cotizadorServicePort.obtenerCotizacion(cotizacionId);
    }
    
    /**
     * Actualiza el estado de una cotización
     */
    public CompletableFuture<CotizacionResponseDto> actualizarEstadoCotizacion(String cotizacionId, String nuevoEstado) {
        logger.info("Actualizando estado de cotización {} a: {}", cotizacionId, nuevoEstado);
        return cotizadorServicePort.actualizarEstadoCotizacion(cotizacionId, nuevoEstado);
    }
    
    /**
     * Verifica disponibilidad del servicio
     */
    public CompletableFuture<Boolean> verificarDisponibilidadServicio() {
        return cotizadorServicePort.isServiceAvailable();
    }
    
    private CotizacionRequestDto crearCotizacionRequest(Order order) {
        List<CotizacionRequestDto.ProductoCotizacionDto> productosDto = order.getProductos().stream()
            .map(producto -> new CotizacionRequestDto.ProductoCotizacionDto(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecioUnitario(),
                1 // En el modelo actual no tenemos cantidad, usamos 1 por defecto
            ))
            .collect(Collectors.toList());
        
        // Determinar tipo de cliente (podría ser un campo en Customer)
        String tipoCliente = "REGULAR"; // Por defecto
        
        return new CotizacionRequestDto(order.getCliente().getId(), productosDto, tipoCliente);
    }
}
