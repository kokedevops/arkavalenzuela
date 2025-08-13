package com.arka.security.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Entidad Usuario para el sistema de seguridad ARKA
 * Implementa UserDetails para integración con Spring Security
 */
@Table("usuarios")
public class Usuario implements UserDetails {
    
    @Id
    private Long id;
    
    @Column("username")
    private String username;
    
    @Column("email")
    private String email;
    
    @Column("password")
    private String password;
    
    @Column("nombre_completo")
    private String nombreCompleto;
    
    @Column("rol")
    private Rol rol;
    
    @Column("activo")
    private boolean activo = true;
    
    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column("fecha_ultimo_acceso")
    private LocalDateTime fechaUltimoAcceso;
    
    // Constructores
    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    public Usuario(String username, String email, String password, String nombreCompleto, Rol rol) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }
    
    // Factory methods
    public static Usuario crearAdministrador(String username, String email, String password, String nombreCompleto) {
        return new Usuario(username, email, password, nombreCompleto, Rol.ADMINISTRADOR);
    }
    
    public static Usuario crearUsuarioRegular(String username, String email, String password, String nombreCompleto) {
        return new Usuario(username, email, password, nombreCompleto, Rol.USUARIO);
    }
    
    public static Usuario crearOperador(String username, String email, String password, String nombreCompleto) {
        return new Usuario(username, email, password, nombreCompleto, Rol.OPERADOR);
    }
    
    public static Usuario crearGestor(String username, String email, String password, String nombreCompleto) {
        return new Usuario(username, email, password, nombreCompleto, Rol.GESTOR);
    }
    
    // Métodos de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return activo;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return activo;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return activo;
    }
    
    @Override
    public boolean isEnabled() {
        return activo;
    }
    
    // Métodos de negocio
    public void actualizarUltimoAcceso() {
        this.fechaUltimoAcceso = LocalDateTime.now();
    }
    
    public void activar() {
        this.activo = true;
    }
    
    public void desactivar() {
        this.activo = false;
    }
    
    public boolean esAdministrador() {
        return Rol.ADMINISTRADOR.equals(this.rol);
    }
    
    public boolean esOperador() {
        return Rol.OPERADOR.equals(this.rol);
    }
    
    public boolean esUsuarioRegular() {
        return Rol.USUARIO.equals(this.rol);
    }
    
    public boolean esGestor() {
        return Rol.GESTOR.equals(this.rol);
    }
    
    public boolean tienePermiso(String permiso) {
        return rol.tienePermiso(permiso);
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public Rol getRol() {
        return rol;
    }
    
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaUltimoAcceso() {
        return fechaUltimoAcceso;
    }
    
    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
        this.fechaUltimoAcceso = fechaUltimoAcceso;
    }
    
    // Enum para roles con permisos
    public enum Rol {
        ADMINISTRADOR("Administrador del sistema", List.of(
            "USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",
            "CALC_CREATE", "CALC_READ", "CALC_UPDATE", "CALC_DELETE",
            "QUOTE_CREATE", "QUOTE_READ", "QUOTE_UPDATE", "QUOTE_DELETE",
            "SYSTEM_CONFIG", "METRICS_VIEW", "AUDIT_VIEW"
        )),
        GESTOR("Gestor de operaciones", List.of(
            "USER_READ", "USER_UPDATE",
            "CALC_CREATE", "CALC_READ", "CALC_UPDATE",
            "QUOTE_CREATE", "QUOTE_READ", "QUOTE_UPDATE",
            "METRICS_VIEW"
        )),
        OPERADOR("Operador de cálculos", List.of(
            "CALC_CREATE", "CALC_READ", "CALC_UPDATE",
            "QUOTE_CREATE", "QUOTE_READ"
        )),
        USUARIO("Usuario regular", List.of(
            "CALC_READ", "QUOTE_READ"
        ));
        
        private final String descripcion;
        private final List<String> permisos;
        
        Rol(String descripcion, List<String> permisos) {
            this.descripcion = descripcion;
            this.permisos = permisos;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        public List<String> getPermisos() {
            return permisos;
        }
        
        public boolean tienePermiso(String permiso) {
            return permisos.contains(permiso);
        }
    }
    
    // equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) && Objects.equals(username, usuario.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", rol=" + rol +
                ", activo=" + activo +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
