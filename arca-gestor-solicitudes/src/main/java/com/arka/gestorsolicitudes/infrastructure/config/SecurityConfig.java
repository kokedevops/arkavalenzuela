package com.arka.gestorsolicitudes.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad para el microservicio de gestión de solicitudes
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints de autenticación - públicos
                        .pathMatchers("/auth/register", "/auth/login", "/auth/refresh").permitAll()
                        
                        // Endpoints de salud - públicos
                        .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                        
                        // Endpoints de administración - solo ADMINISTRADOR
                        .pathMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                        
                        // Endpoints de gestión - ADMINISTRADOR y GESTOR
                        .pathMatchers("/api/gestion/**").hasAnyRole("ADMINISTRADOR", "GESTOR")
                        
                        // Endpoints de operaciones - ADMINISTRADOR, GESTOR y OPERADOR
                        .pathMatchers("/api/operaciones/**").hasAnyRole("ADMINISTRADOR", "GESTOR", "OPERADOR")
                        
                        // Endpoints de cálculos - todos los roles autenticados
                        .pathMatchers("/api/calculos/**").hasAnyRole("ADMINISTRADOR", "GESTOR", "OPERADOR", "USUARIO")
                        
                        // Todos los demás endpoints requieren autenticación
                        .anyExchange().authenticated()
                )
                // Deshabilitar autenticación automática ya que usamos headers del Gateway
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength 12 para mayor seguridad
    }
}
