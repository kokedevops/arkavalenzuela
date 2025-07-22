package com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.Proveedor;

import java.util.List;

public interface ProveedorServicePort {
    
    /**
     * Obtiene información de un proveedor por ID
     * @param proveedorId ID del proveedor
     * @return Proveedor encontrado
     */
    Proveedor obtenerProveedorPorId(String proveedorId);
    
    /**
     * Obtiene todos los proveedores activos
     * @return Lista de proveedores activos
     */
    List<Proveedor> obtenerProveedoresActivos();
    
    /**
     * Busca proveedores por categoria de producto
     * @param categoria Categoria del producto
     * @return Lista de proveedores especializados
     */
    List<Proveedor> buscarProveedoresPorCategoria(String categoria);
    
    /**
     * Verifica si un proveedor está activo
     * @param proveedorId ID del proveedor
     * @return true si está activo
     */
    boolean verificarProveedorActivo(String proveedorId);
}
