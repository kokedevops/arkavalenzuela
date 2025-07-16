package com.arka.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.arka.application",
    "com.arka.infrastructure", 
    "com.arka.web"
})
public class MultiModuleConfiguration {
    // Esta clase integra todos los módulos del proyecto
}
