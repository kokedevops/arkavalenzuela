package com.arka.arkavalenzuela.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuración JPA para MySQL en Kubernetes
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository"
)
@EntityScan(basePackages = "com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity")
@EnableTransactionManagement
public class JpaConfiguration {
    // La configuración del DataSource se maneja automáticamente 
    // a través de spring.datasource.* properties
}
