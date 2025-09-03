package com.arka.arkavalenzuela.infrastructure.config.saga;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración para el procesamiento asíncrono de Saga
 */
@Configuration
@EnableAsync
public class SagaAsyncConfiguration {

    @Bean(name = "sagaExecutor")
    @ConditionalOnProperty(name = "aws.mock.enabled", havingValue = "true", matchIfMissing = true)
    public Executor sagaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Saga-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
