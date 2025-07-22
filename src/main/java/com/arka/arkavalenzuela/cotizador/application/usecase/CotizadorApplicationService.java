package com.arka.arkavalenzuela.cotizador.application.usecase;

import com.arka.arkavalenzuela.cotizador.domain.model.CotizacionRequest;
import com.arka.arkavalenzuela.cotizador.domain.model.CotizacionResponse;
import com.arka.arkavalenzuela.cotizador.domain.model.ProductoCotizado;
import com.arka.arkavalenzuela.cotizador.domain.model.ProductoSolicitado;
import com.arka.arkavalenzuela.cotizador.domain.port.in.CotizadorUseCase;
import com.arka.arkavalenzuela.cotizador.domain.port.out.CalculadoraPreciosPort;
import com.arka.arkavalenzuela.cotizador.domain.port.out.CotizacionRepositoryPort;
import com.arka.arkavalenzuela.cotizador.domain.port.out.ProductoServicePort;
import com.arka.arkavalenzuela.domain.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CotizadorApplicationService implements CotizadorUseCase {

    private final CotizacionRepositoryPort cotizacionRepository;
    private final ProductoServicePort productoService;
    private final CalculadoraPreciosPort calculadoraPrecios;

    public CotizadorApplicationService(CotizacionRepositoryPort cotizacionRepository,
                                     ProductoServicePort productoService,
                                     CalculadoraPreciosPort calculadoraPrecios) {
        this.cotizacionRepository = cotizacionRepository;
        this.productoService = productoService;
        this.calculadoraPrecios = calculadoraPrecios;
    }

    @Override
    public CotizacionResponse generarCotizacion(CotizacionRequest request) {
        // Generar ID único para la cotización
        String cotizacionId = UUID.randomUUID().toString();
        
        // Obtener información de productos solicitados
        List<Long> productosIds = request.getProductos().stream()
                .map(ProductoSolicitado::getProductoId)
                .toList();
        
        List<Product> productos = productoService.obtenerProductosPorIds(productosIds);
        
        // Procesar cada producto y calcular precios
        List<ProductoCotizado> productosCotizados = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (ProductoSolicitado productSolicitud : request.getProductos()) {
            Product producto = productos.stream()
                    .filter(p -> p.getId().equals(productSolicitud.getProductoId()))
                    .findFirst()
                    .orElse(null);
            
            if (producto != null) {
                // Verificar disponibilidad
                boolean disponible = productoService.verificarDisponibilidad(
                        producto.getId(), 
                        productSolicitud.getCantidad()
                );
                
                // Calcular precios
                BigDecimal precioConDescuento = calculadoraPrecios.calcularPrecioConDescuento(
                        producto.getPrecioUnitario(),
                        productSolicitud.getCantidad(),
                        request.getTipoCliente()
                );
                
                BigDecimal subtotalProducto = precioConDescuento.multiply(
                        BigDecimal.valueOf(productSolicitud.getCantidad())
                );
                
                ProductoCotizado productoCotizado = new ProductoCotizado(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getDescripcion(),
                        productSolicitud.getCantidad(),
                        producto.getPrecioUnitario(),
                        producto.getPrecioUnitario().subtract(precioConDescuento),
                        precioConDescuento,
                        subtotalProducto,
                        productSolicitud.getObservaciones(),
                        disponible,
                        disponible ? 5 : 15 // días de entrega
                );
                
                productosCotizados.add(productoCotizado);
                subtotal = subtotal.add(subtotalProducto);
            }
        }
        
        // Calcular totales
        BigDecimal descuentos = calculadoraPrecios.calcularDescuentoTotal(subtotal, request.getTipoCliente());
        BigDecimal impuestos = calculadoraPrecios.calcularImpuestos(subtotal.subtract(descuentos));
        BigDecimal total = subtotal.subtract(descuentos).add(impuestos);
        
        // Crear respuesta de cotización
        CotizacionResponse cotizacion = new CotizacionResponse(
                cotizacionId,
                request.getClienteId(),
                productosCotizados,
                subtotal,
                descuentos,
                impuestos,
                total,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30), // Válida por 30 días
                "PENDIENTE",
                request.getObservaciones(),
                determinarCondicionesPago(request.getTipoCliente()),
                calcularTiempoEntregaTotal(productosCotizados)
        );
        
        // Guardar cotización
        return cotizacionRepository.guardarCotizacion(cotizacion);
    }

    @Override
    public CotizacionResponse consultarCotizacion(String cotizacionId) {
        return cotizacionRepository.buscarPorId(cotizacionId);
    }

    @Override
    public CotizacionResponse actualizarEstadoCotizacion(String cotizacionId, String nuevoEstado) {
        CotizacionResponse cotizacion = cotizacionRepository.buscarPorId(cotizacionId);
        if (cotizacion != null) {
            cotizacion.setEstado(nuevoEstado);
            return cotizacionRepository.actualizarCotizacion(cotizacion);
        }
        return null;
    }

    private String determinarCondicionesPago(String tipoCliente) {
        return switch (tipoCliente.toUpperCase()) {
            case "RETAIL" -> "Pago contra entrega";
            case "MAYORISTA" -> "30 días";
            case "DISTRIBUIDOR" -> "45 días";
            default -> "Pago anticipado";
        };
    }

    private Integer calcularTiempoEntregaTotal(List<ProductoCotizado> productos) {
        return productos.stream()
                .mapToInt(ProductoCotizado::getTiempoEntrega)
                .max()
                .orElse(5);
    }
}
