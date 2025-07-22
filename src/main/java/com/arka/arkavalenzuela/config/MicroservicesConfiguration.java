package com.arka.arkavalenzuela.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.arka.arkavalenzuela.cotizador",
    "com.arka.arkavalenzuela.gestorsolicitudes"
})
public class MicroservicesConfiguration {
    // Esta configuración asegura que Spring detecte todos los beans de los microservicios
}
