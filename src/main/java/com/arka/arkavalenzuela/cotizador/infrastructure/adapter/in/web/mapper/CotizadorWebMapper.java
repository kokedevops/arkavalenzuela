package com.arka.arkavalenzuela.cotizador.infrastructure.adapter.in.web.mapper;

import com.arka.arkavalenzuela.cotizador.domain.model.CotizacionRequest;
import com.arka.arkavalenzuela.cotizador.domain.model.CotizacionResponse;
import com.arka.arkavalenzuela.cotizador.domain.model.ProductoCotizado;
import com.arka.arkavalenzuela.cotizador.domain.model.ProductoSolicitado;
import com.arka.arkavalenzuela.cotizador.infrastructure.adapter.in.web.dto.CotizacionRequestDto;
import com.arka.arkavalenzuela.cotizador.infrastructure.adapter.in.web.dto.CotizacionResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CotizadorWebMapper {

    public CotizacionRequest toDomain(CotizacionRequestDto dto) {
        if (dto == null) return null;

        List<ProductoSolicitado> productos = dto.getProductos().stream()
                .map(this::toProductoSolicitadoDomain)
                .collect(Collectors.toList());

        return new CotizacionRequest(
                dto.getClienteId(),
                productos,
                dto.getTipoCliente(),
                dto.getObservaciones()
        );
    }

    public CotizacionResponseDto toDto(CotizacionResponse domain) {
        if (domain == null) return null;

        CotizacionResponseDto dto = new CotizacionResponseDto();
        dto.setCotizacionId(domain.getCotizacionId());
        dto.setClienteId(domain.getClienteId());
        dto.setSubtotal(domain.getSubtotal());
        dto.setDescuentos(domain.getDescuentos());
        dto.setImpuestos(domain.getImpuestos());
        dto.setTotal(domain.getTotal());
        dto.setFechaCotizacion(domain.getFechaCotizacion());
        dto.setFechaVencimiento(domain.getFechaVencimiento());
        dto.setEstado(domain.getEstado());
        dto.setObservaciones(domain.getObservaciones());
        dto.setCondicionesPago(domain.getCondicionesPago());
        dto.setTiempoEntrega(domain.getTiempoEntrega());

        List<CotizacionResponseDto.ProductoCotizadoDto> productosDto = domain.getProductos().stream()
                .map(this::toProductoCotizadoDto)
                .collect(Collectors.toList());
        dto.setProductos(productosDto);

        return dto;
    }

    private ProductoSolicitado toProductoSolicitadoDomain(CotizacionRequestDto.ProductoSolicitadoDto dto) {
        return new ProductoSolicitado(
                dto.getProductoId(),
                dto.getCantidad(),
                dto.getPrecioBase(),
                dto.getObservaciones()
        );
    }

    private CotizacionResponseDto.ProductoCotizadoDto toProductoCotizadoDto(ProductoCotizado domain) {
        CotizacionResponseDto.ProductoCotizadoDto dto = new CotizacionResponseDto.ProductoCotizadoDto();
        dto.setProductoId(domain.getProductoId());
        dto.setNombre(domain.getNombre());
        dto.setDescripcion(domain.getDescripcion());
        dto.setCantidad(domain.getCantidad());
        dto.setPrecioUnitario(domain.getPrecioUnitario());
        dto.setDescuento(domain.getDescuento());
        dto.setPrecioFinal(domain.getPrecioFinal());
        dto.setSubtotal(domain.getSubtotal());
        dto.setObservaciones(domain.getObservaciones());
        dto.setDisponible(domain.getDisponible());
        dto.setTiempoEntrega(domain.getTiempoEntrega());
        return dto;
    }
}
