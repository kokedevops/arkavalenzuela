package com.arka.cotizador.infrastructure.adapter.in.web;

import com.arka.cotizador.application.service.CotizacionService;
import com.arka.cotizador.domain.model.CotizacionRequest;
import com.arka.cotizador.domain.model.CotizacionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cotizaciones")
@CrossOrigin(origins = "*")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    @Autowired
    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CotizacionResponse> generarCotizacion(@RequestBody CotizacionRequest request) {
        return cotizacionService.generarCotizacion(request);
    }

    @GetMapping("/{cotizacionId}")
    public Mono<CotizacionResponse> obtenerCotizacion(@PathVariable String cotizacionId) {
        return cotizacionService.obtenerCotizacion(cotizacionId);
    }

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("Arca Cotizador está funcionando correctamente");
    }
}
