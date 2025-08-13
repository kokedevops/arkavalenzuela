package com.arka.gestorsolicitudes.infrastructure.controller;

import com.arka.security.domain.model.Usuario;
import com.arka.security.domain.repository.UsuarioRepository;
import com.arka.security.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador para gestión de usuarios (solo administradores)
 */
@RestController
@RequestMapping("/api/admin/usuarios")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioAdminController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Listar todos los usuarios (solo administradores)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Flux<Usuario> listarUsuarios() {
        return usuarioRepository.findAllActive();
    }
    
    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Mono<ResponseEntity<Usuario>> obtenerUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> ResponseEntity.ok(usuario))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Crear usuario con rol específico (solo administradores)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Mono<ResponseEntity<Usuario>> crearUsuario(@RequestBody CreateUserRequest request) {
        Usuario usuario = new Usuario(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNombreCompleto(),
                request.getRol()
        );
        
        return usuarioRepository.existsByUsername(request.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.just(ResponseEntity.badRequest().<Usuario>build());
                    }
                    return usuarioRepository.save(usuario)
                            .map(savedUser -> ResponseEntity.ok(savedUser));
                });
    }
    
    /**
     * Actualizar rol de usuario
     */
    @PutMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Mono<ResponseEntity<Usuario>> actualizarRol(@PathVariable Long id, @RequestBody RolUpdateRequest request) {
        return usuarioRepository.findById(id)
                .flatMap(usuario -> {
                    usuario.setRol(request.getRol());
                    return usuarioRepository.save(usuario);
                })
                .map(usuario -> ResponseEntity.ok(usuario))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Activar/Desactivar usuario
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Mono<ResponseEntity<Usuario>> cambiarEstado(@PathVariable Long id, @RequestBody EstadoUpdateRequest request) {
        return usuarioRepository.findById(id)
                .flatMap(usuario -> {
                    usuario.setActivo(request.isActivo());
                    return usuarioRepository.save(usuario);
                })
                .map(usuario -> ResponseEntity.ok(usuario))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Eliminar usuario (desactivar)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Mono<ResponseEntity<Void>> eliminarUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .flatMap(usuario -> {
                    usuario.desactivar();
                    return usuarioRepository.save(usuario);
                })
                .map(usuario -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtener estadísticas de usuarios
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Mono<ResponseEntity<UserStats>> obtenerEstadisticas() {
        return usuarioRepository.countActiveUsers()
                .zipWith(usuarioRepository.findByRol(Usuario.Rol.ADMINISTRADOR).count())
                .zipWith(usuarioRepository.findByRol(Usuario.Rol.GESTOR).count())
                .zipWith(usuarioRepository.findByRol(Usuario.Rol.OPERADOR).count())
                .zipWith(usuarioRepository.findByRol(Usuario.Rol.USUARIO).count())
                .map(tuple -> {
                    Long totalActivos = tuple.getT1().getT1().getT1().getT1();
                    Long admins = tuple.getT1().getT1().getT1().getT2();
                    Long gestores = tuple.getT1().getT1().getT2();
                    Long operadores = tuple.getT1().getT2();
                    Long usuarios = tuple.getT2();
                    
                    UserStats stats = new UserStats();
                    stats.setTotalActivos(totalActivos);
                    stats.setAdministradores(admins);
                    stats.setGestores(gestores);
                    stats.setOperadores(operadores);
                    stats.setUsuarios(usuarios);
                    
                    return ResponseEntity.ok(stats);
                });
    }
    
    // DTOs internos
    public static class CreateUserRequest extends RegisterRequest {
        private Usuario.Rol rol;
        
        public Usuario.Rol getRol() {
            return rol;
        }
        
        public void setRol(Usuario.Rol rol) {
            this.rol = rol;
        }
    }
    
    public static class RolUpdateRequest {
        private Usuario.Rol rol;
        
        public Usuario.Rol getRol() {
            return rol;
        }
        
        public void setRol(Usuario.Rol rol) {
            this.rol = rol;
        }
    }
    
    public static class EstadoUpdateRequest {
        private boolean activo;
        
        public boolean isActivo() {
            return activo;
        }
        
        public void setActivo(boolean activo) {
            this.activo = activo;
        }
    }
    
    public static class UserStats {
        private Long totalActivos;
        private Long administradores;
        private Long gestores;
        private Long operadores;
        private Long usuarios;
        
        // Getters y setters
        public Long getTotalActivos() { return totalActivos; }
        public void setTotalActivos(Long totalActivos) { this.totalActivos = totalActivos; }
        
        public Long getAdministradores() { return administradores; }
        public void setAdministradores(Long administradores) { this.administradores = administradores; }
        
        public Long getGestores() { return gestores; }
        public void setGestores(Long gestores) { this.gestores = gestores; }
        
        public Long getOperadores() { return operadores; }
        public void setOperadores(Long operadores) { this.operadores = operadores; }
        
        public Long getUsuarios() { return usuarios; }
        public void setUsuarios(Long usuarios) { this.usuarios = usuarios; }
    }
}
