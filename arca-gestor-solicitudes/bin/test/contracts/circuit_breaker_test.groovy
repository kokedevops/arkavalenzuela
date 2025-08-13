package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Debe activar Circuit Breaker cuando el servicio externo falla"
    request {
        method POST()
        url "/api/calculo-envio/probar-circuit-breaker"
        body(
            escenario: "externo",
            origen: "Lima",
            destino: "Cusco",
            peso: 1.5
        )
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status OK()
        body(
            id: anyNonBlankString(),
            origen: "Lima", 
            destino: "Cusco",
            peso: 1.5,
            costo: anyPositiveDouble(),
            tiempoEstimadoDias: anyPositiveInt(),
            estado: anyOf("COMPLETADO", "FALLBACK"),
            proveedorUtilizado: anyNonBlankString(),
            fechaCalculo: anyNonBlankString()
        )
        headers {
            contentType(applicationJson())
        }
    }
}
