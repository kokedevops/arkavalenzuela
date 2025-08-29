package com.arka.gestorsolicitudes.infrastructure.config;

import com.arka.security.domain.model.Usuario;
import com.arka.security.domain.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos por defecto
 * TEMPORALMENTE DESHABILITADO - Para debug
 */
//@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Inicializando datos por defecto...");
        
        // Verificar si ya existen usuarios
        Long userCount = usuarioRepository.count().block();
        if (userCount != null && userCount > 0) {
            log.info("Los usuarios ya están inicializados. Total: {}", userCount);
            return;
        }
        
        // Crear usuario administrador
        Usuario admin = new Usuario(
                "admin",
                "admin@arka.com",
                passwordEncoder.encode("admin123"),
                "Administrador del Sistema",
                Usuario.Rol.ADMINISTRADOR
        );
        
        // Crear usuario gestor
        Usuario gestor = new Usuario(
                "gestor",
                "gestor@arka.com",
                passwordEncoder.encode("gestor123"),
                "Gestor de Solicitudes",
                Usuario.Rol.GESTOR
        );
        
        // Crear usuario operador
        Usuario operador = new Usuario(
                "operador",
                "operador@arka.com",
                passwordEncoder.encode("operador123"),
                "Operador del Sistema",
                Usuario.Rol.OPERADOR
        );
        
        // Crear usuario regular
        Usuario usuario = new Usuario(
                "usuario",
                "usuario@arka.com",
                passwordEncoder.encode("usuario123"),
                "Usuario Regular",
                Usuario.Rol.USUARIO
        );
        
        // Guardar usuarios
        try {
            usuarioRepository.save(admin).block();
            log.info("✅ Usuario administrador creado: admin/admin123");
            
            usuarioRepository.save(gestor).block();
            log.info("✅ Usuario gestor creado: gestor/gestor123");
            
            usuarioRepository.save(operador).block();
            log.info("✅ Usuario operador creado: operador/operador123");
            
            usuarioRepository.save(usuario).block();
            log.info("✅ Usuario regular creado: usuario/usuario123");
            
            log.info("🎉 Inicialización de datos completada exitosamente!");
            
        } catch (Exception e) {
            log.error("❌ Error al inicializar datos: {}", e.getMessage(), e);
        }
    }
}
