package com.arka.arkavalenzuela.cotizador.infrastructure.adapter.in.web;

import com.arka.arkavalenzuela.cotizador.domain.model.CotizacionRequest;
import com.arka.arkavalenzuela.cotizador.domain.model.CotizacionResponse;
import com.arka.arkavalenzuela.cotizador.domain.port.in.CotizadorUseCase;
import com.arka.arkavalenzuela.cotizador.infrastructure.adapter.in.web.dto.CotizacionRequestDto;
import com.arka.arkavalenzuela.cotizador.infrastructure.adapter.in.web.dto.CotizacionResponseDto;
import com.arka.arkavalenzuela.cotizador.infrastructure.adapter.in.web.mapper.CotizadorWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cotizador")
@CrossOrigin(origins = "*")
public class CotizadorController {

    private final CotizadorUseCase cotizadorUseCase;
    private final CotizadorWebMapper mapper;

    public CotizadorController(CotizadorUseCase cotizadorUseCase, CotizadorWebMapper mapper) {
        this.cotizadorUseCase = cotizadorUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/cotizaciones")
    public ResponseEntity<CotizacionResponseDto> generarCotizacion(@RequestBody CotizacionRequestDto requestDto) {
        try {
            CotizacionRequest request = mapper.toDomain(requestDto);
            CotizacionResponse response = cotizadorUseCase.generarCotizacion(request);
            CotizacionResponseDto responseDto = mapper.toDto(response);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cotizaciones/{cotizacionId}")
    public ResponseEntity<CotizacionResponseDto> consultarCotizacion(@PathVariable String cotizacionId) {
        try {
            CotizacionResponse response = cotizadorUseCase.consultarCotizacion(cotizacionId);
            if (response != null) {
                CotizacionResponseDto responseDto = mapper.toDto(response);
                return ResponseEntity.ok(responseDto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/cotizaciones/{cotizacionId}/estado")
    public ResponseEntity<CotizacionResponseDto> actualizarEstado(
            @PathVariable String cotizacionId,
            @RequestParam String nuevoEstado) {
        try {
            CotizacionResponse response = cotizadorUseCase.actualizarEstadoCotizacion(cotizacionId, nuevoEstado);
            if (response != null) {
                CotizacionResponseDto responseDto = mapper.toDto(response);
                return ResponseEntity.ok(responseDto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Cotizador service is running");
    }
}
