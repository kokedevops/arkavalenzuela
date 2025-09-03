package com.arka.arkavalenzuela.infrastructure.config.saga;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para clientes AWS
 * Solo se crean cuando aws.mock.enabled=false
 * 
 * NOTA: Implementación comentada debido a dependencias AWS
 * Para activar AWS real, descomentar este código y asegurar que el SDK está disponible
 */
@Configuration
@ConditionalOnProperty(name = "aws.mock.enabled", havingValue = "false")
public class AwsClientConfiguration {

    // TODO: Descomentar cuando AWS SDK esté completamente configurado
    
    /*
    @Bean
    @Primary
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    @Primary
    public LambdaClient lambdaClient() {
        return LambdaClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
    */
}
