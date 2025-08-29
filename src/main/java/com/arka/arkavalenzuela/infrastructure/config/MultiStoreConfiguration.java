package com.arka.arkavalenzuela.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Configuración para múltiples stores de datos
 * - JPA para MySQL (RDS)
 * - MongoDB para DocumentDB
 * - R2DBC para acceso reactivo a MySQL
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository",
    repositoryImplementationPostfix = "Impl"
)
@EnableMongoRepositories(
    basePackages = "com.arka.arkavalenzuela.infrastructure.adapter.out.mongodb.repository"
)
@EnableR2dbcRepositories(
    basePackages = "com.arka.arkavalenzuela.infrastructure.adapter.out.r2dbc.repository"
)
public class MultiStoreConfiguration {
}
