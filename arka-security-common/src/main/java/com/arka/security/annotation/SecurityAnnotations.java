package com.arka.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotaciones personalizadas para autorización basada en roles y permisos
 */
public class SecurityAnnotations {
    
    /**
     * Solo administradores pueden acceder
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public @interface AdminOnly {
    }
    
    /**
     * Administradores y gestores pueden acceder
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GESTOR')")
    public @interface ManagementAccess {
    }
    
    /**
     * Administradores, gestores y operadores pueden acceder
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GESTOR', 'OPERADOR')")
    public @interface OperationalAccess {
    }
    
    /**
     * Todos los usuarios autenticados pueden acceder
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GESTOR', 'OPERADOR', 'USUARIO')")
    public @interface AuthenticatedAccess {
    }
    
    /**
     * Acceso basado en permiso específico
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequirePermission {
        String value(); // Nombre del permiso requerido
    }
    
    /**
     * Solo el propietario del recurso o un administrador pueden acceder
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('ADMINISTRADOR') or @securityService.isOwner(authentication.name, #id)")
    public @interface OwnerOrAdminAccess {
    }
}
