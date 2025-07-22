package com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.RespuestaProveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.SolicitudProveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.port.in.GestorSolicitudesUseCase;
import com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web.dto.RespuestaProveedorDto;
import com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web.dto.SolicitudProveedorRequestDto;
import com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web.dto.SolicitudProveedorResponseDto;
import com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.in.web.mapper.GestorSolicitudesWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/gestor-solicitudes")
@CrossOrigin(origins = "*")
public class GestorSolicitudesController {

    private final GestorSolicitudesUseCase gestorSolicitudesUseCase;
    private final GestorSolicitudesWebMapper mapper;

    public GestorSolicitudesController(GestorSolicitudesUseCase gestorSolicitudesUseCase, 
                                     GestorSolicitudesWebMapper mapper) {
        this.gestorSolicitudesUseCase = gestorSolicitudesUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<SolicitudProveedorResponseDto> crearSolicitud(@RequestBody SolicitudProveedorRequestDto requestDto) {
        try {
            SolicitudProveedor solicitud = mapper.toDomain(requestDto);
            SolicitudProveedor solicitudCreada = gestorSolicitudesUseCase.crearSolicitud(solicitud);
            SolicitudProveedorResponseDto responseDto = mapper.toResponseDto(solicitudCreada);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/solicitudes/multiple")
    public ResponseEntity<List<SolicitudProveedorResponseDto>> enviarSolicitudMultiple(
            @RequestBody SolicitudProveedorRequestDto requestDto,
            @RequestParam List<String> proveedorIds) {
        try {
            SolicitudProveedor solicitudBase = mapper.toDomain(requestDto);
            List<SolicitudProveedor> solicitudesEnviadas = gestorSolicitudesUseCase.enviarSolicitudMultiple(solicitudBase, proveedorIds);
            
            List<SolicitudProveedorResponseDto> responseDtos = solicitudesEnviadas.stream()
                    .map(mapper::toResponseDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/solicitudes/{solicitudId}")
    public ResponseEntity<SolicitudProveedorResponseDto> consultarSolicitud(@PathVariable String solicitudId) {
        try {
            SolicitudProveedor solicitud = gestorSolicitudesUseCase.consultarSolicitud(solicitudId);
            if (solicitud != null) {
                SolicitudProveedorResponseDto responseDto = mapper.toResponseDto(solicitud);
                return ResponseEntity.ok(responseDto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/solicitudes")
    public ResponseEntity<List<SolicitudProveedorResponseDto>> obtenerSolicitudesPorEstado(@RequestParam String estado) {
        try {
            List<SolicitudProveedor> solicitudes = gestorSolicitudesUseCase.obtenerSolicitudesPorEstado(estado);
            List<SolicitudProveedorResponseDto> responseDtos = solicitudes.stream()
                    .map(mapper::toResponseDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/solicitudes/{solicitudId}/estado")
    public ResponseEntity<SolicitudProveedorResponseDto> actualizarEstado(
            @PathVariable String solicitudId,
            @RequestParam String nuevoEstado) {
        try {
            SolicitudProveedor solicitud = gestorSolicitudesUseCase.actualizarEstadoSolicitud(solicitudId, nuevoEstado);
            if (solicitud != null) {
                SolicitudProveedorResponseDto responseDto = mapper.toResponseDto(solicitud);
                return ResponseEntity.ok(responseDto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/respuestas")
    public ResponseEntity<RespuestaProveedorDto> procesarRespuesta(@RequestBody RespuestaProveedorDto respuestaDto) {
        try {
            RespuestaProveedor respuesta = mapper.toRespuestaDomain(respuestaDto);
            RespuestaProveedor respuestaProcesada = gestorSolicitudesUseCase.procesarRespuestaProveedor(respuesta);
            RespuestaProveedorDto responseDto = mapper.toRespuestaDto(respuestaProcesada);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/solicitudes/{solicitudId}/respuestas")
    public ResponseEntity<List<RespuestaProveedorDto>> obtenerRespuestasSolicitud(@PathVariable String solicitudId) {
        try {
            List<RespuestaProveedor> respuestas = gestorSolicitudesUseCase.obtenerRespuestasSolicitud(solicitudId);
            List<RespuestaProveedorDto> responseDtos = respuestas.stream()
                    .map(mapper::toRespuestaDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Gestor Solicitudes service is running");
    }
}
