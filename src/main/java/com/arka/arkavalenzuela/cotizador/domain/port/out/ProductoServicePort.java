package com.arka.arkavalenzuela.cotizador.domain.port.out;

import com.arka.arkavalenzuela.domain.model.Product;
import java.util.List;

public interface ProductoServicePort {
    
    /**
     * Obtiene información de productos por sus IDs
     * @param productoIds Lista de IDs de productos
     * @return Lista de productos encontrados
     */
    List<Product> obtenerProductosPorIds(List<Long> productoIds);
    
    /**
     * Verifica disponibilidad de stock de productos
     * @param productoId ID del producto
     * @param cantidad Cantidad requerida
     * @return true si hay stock suficiente
     */
    boolean verificarDisponibilidad(Long productoId, Integer cantidad);
}
