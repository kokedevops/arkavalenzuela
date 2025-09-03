package com.arka.arkavalenzuela.infrastructure.config;


import com.arka.arkavalenzuela.domain.port.in.ManageTemplatesUseCase;
import com.arka.arkavalenzuela.domain.port.out.TemplateRepository;
import com.arka.arkavalenzuela.application.service.TemplateManagementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración de Spring para la aplicación.
 * Define cómo se instancian y conectan los componentes de la arquitectura hexagonal.
 */
@Configuration
public class AppConfig {

    /**
     * Define el bean para el caso de uso ManageTemplatesUseCase.
     * Spring inyectará automáticamente la implementación de TemplateRepository (S3TemplateAdapter)
     * ya que está marcada con @Component.
     * @param templateRepository El adaptador de infraestructura para el repositorio de plantillas.
     * @return Una instancia de TemplateManagementService que implementa el caso de uso.
     */
    @Bean
    public ManageTemplatesUseCase manageTemplatesUseCase(TemplateRepository templateRepository) {
        return new TemplateManagementService(templateRepository);
    }
}