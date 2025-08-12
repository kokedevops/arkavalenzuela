package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Debe calcular envío exitosamente"
    request {
        method POST()
        url "/api/calculo-envio/calcular"
        body(
            origen: "Lima",
            destino: "Arequipa", 
            peso: 2.5,
            dimensiones: "50x30x20"
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
            destino: "Arequipa",
            peso: 2.5,
            dimensiones: "50x30x20",
            costo: anyPositiveDouble(),
            tiempoEstimadoDias: anyPositiveInt(),
            estado: "COMPLETADO",
            proveedorUtilizado: anyNonBlankString(),
            fechaCalculo: anyNonBlankString()
        )
        headers {
            contentType(applicationJson())
        }
    }
}
