package com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web.mapper;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.ProductoSolicitado;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.RespuestaProveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.SolicitudProveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web.dto.RespuestaProveedorDto;
import com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web.dto.SolicitudProveedorRequestDto;
import com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web.dto.SolicitudProveedorResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GestorSolicitudesWebMapper {

    public SolicitudProveedor toDomain(SolicitudProveedorRequestDto dto) {
        if (dto == null) return null;

        List<ProductoSolicitado> productos = dto.getProductos().stream()
                .map(this::toProductoSolicitadoDomain)
                .collect(Collectors.toList());

        return new SolicitudProveedor(
                null, // Se generará automáticamente
                dto.getProveedorId(),
                dto.getTipoSolicitud(),
                productos,
                "PENDIENTE", // Estado inicial
                LocalDateTime.now(),
                null,
                dto.getObservaciones(),
                dto.getPrioridad()
        );
    }

    public SolicitudProveedorResponseDto toResponseDto(SolicitudProveedor domain) {
        if (domain == null) return null;

        SolicitudProveedorResponseDto dto = new SolicitudProveedorResponseDto();
        dto.setSolicitudId(domain.getSolicitudId());
        dto.setProveedorId(domain.getProveedorId());
        dto.setTipoSolicitud(domain.getTipoSolicitud());
        dto.setEstado(domain.getEstado());
        dto.setFechaSolicitud(domain.getFechaSolicitud());
        dto.setFechaRespuesta(domain.getFechaRespuesta());
        dto.setObservaciones(domain.getObservaciones());
        dto.setPrioridad(domain.getPrioridad());

        List<SolicitudProveedorRequestDto.ProductoSolicitadoDto> productosDto = domain.getProductos().stream()
                .map(this::toProductoSolicitadoDto)
                .collect(Collectors.toList());
        dto.setProductos(productosDto);

        return dto;
    }

    public RespuestaProveedor toRespuestaDomain(RespuestaProveedorDto dto) {
        if (dto == null) return null;

        return new RespuestaProveedor(
                dto.getRespuestaId(),
                dto.getSolicitudId(),
                dto.getProveedorId(),
                dto.getEstado(),
                dto.getPrecioOfertado(),
                dto.getTiempoEntrega(),
                dto.getCondicionesPago(),
                dto.getGarantia(),
                dto.getObservaciones(),
                dto.getFechaRespuesta(),
                dto.getFechaVencimiento(),
                dto.getAceptada()
        );
    }

    public RespuestaProveedorDto toRespuestaDto(RespuestaProveedor domain) {
        if (domain == null) return null;

        RespuestaProveedorDto dto = new RespuestaProveedorDto();
        dto.setRespuestaId(domain.getRespuestaId());
        dto.setSolicitudId(domain.getSolicitudId());
        dto.setProveedorId(domain.getProveedorId());
        dto.setEstado(domain.getEstado());
        dto.setPrecioOfertado(domain.getPrecioOfertado());
        dto.setTiempoEntrega(domain.getTiempoEntrega());
        dto.setCondicionesPago(domain.getCondicionesPago());
        dto.setGarantia(domain.getGarantia());
        dto.setObservaciones(domain.getObservaciones());
        dto.setFechaRespuesta(domain.getFechaRespuesta());
        dto.setFechaVencimiento(domain.getFechaVencimiento());
        dto.setAceptada(domain.getAceptada());

        return dto;
    }

    private ProductoSolicitado toProductoSolicitadoDomain(SolicitudProveedorRequestDto.ProductoSolicitadoDto dto) {
        return new ProductoSolicitado(
                dto.getProductoId(),
                dto.getNombreProducto(),
                dto.getCantidadSolicitada(),
                dto.getPrecioReferencia(),
                dto.getEspecificaciones(),
                dto.getObservaciones(),
                dto.getUrgente()
        );
    }

    private SolicitudProveedorRequestDto.ProductoSolicitadoDto toProductoSolicitadoDto(ProductoSolicitado domain) {
        SolicitudProveedorRequestDto.ProductoSolicitadoDto dto = new SolicitudProveedorRequestDto.ProductoSolicitadoDto();
        dto.setProductoId(domain.getProductoId());
        dto.setNombreProducto(domain.getNombreProducto());
        dto.setCantidadSolicitada(domain.getCantidadSolicitada());
        dto.setPrecioReferencia(domain.getPrecioReferencia());
        dto.setEspecificaciones(domain.getEspecificaciones());
        dto.setObservaciones(domain.getObservaciones());
        dto.setUrgente(domain.getUrgente());
        return dto;
    }
}
