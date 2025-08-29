package com.arka.arkavalenzuela.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuración para múltiples stores de datos
 * - JPA para MySQL (Kubernetes) - configurado en JpaConfiguration
 * - MongoDB para Kubernetes - configurado en MongoConfiguration
 * - R2DBC para acceso reactivo a MySQL - deshabilitado por defecto
 */
@Configuration
public class MultiStoreConfiguration {
}
