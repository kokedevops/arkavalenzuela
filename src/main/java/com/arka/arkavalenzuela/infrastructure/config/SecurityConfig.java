package com.arka.arkavalenzuela.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

/**
 * Configuración de seguridad para la aplicación principal ARKA
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para APIs REST
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/error", "/login").permitAll() // Página de inicio, errores y login públicos
                .requestMatchers("/health", "/actuator/**").permitAll() // Health checks
                .requestMatchers("/api", "/api/info").permitAll() // Información de la API
                .requestMatchers("/api/auth/**").permitAll() // Endpoints de autenticación públicos
                .requestMatchers("/api/public/**").permitAll() // Endpoints públicos
                .requestMatchers("/api/products/**").permitAll() // Catálogo de productos público
                .requestMatchers("/api/categories/**").permitAll() // Categorías públicas
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger
                .anyRequest().authenticated() // Todo lo demás requiere autenticación
            )
            .httpBasic(Customizer.withDefaults()) // Habilitamos autenticación básica HTTP
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/api/dashboard")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );
        
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Usuarios en memoria para desarrollo/testing
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN", "USER")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user123"))
            .roles("USER")
            .build();

        UserDetails demo = User.builder()
            .username("demo")
            .password(passwordEncoder().encode("demo123"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, user, demo);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength 12 para mayor seguridad
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
