package com.arka.arkavalenzuela.cotizador.domain.port.out;

import java.math.BigDecimal;

public interface CalculadoraPreciosPort {
    
    /**
     * Calcula el precio con descuentos aplicados según el tipo de cliente
     * @param precioBase Precio base del producto
     * @param cantidad Cantidad del producto
     * @param tipoCliente Tipo de cliente (RETAIL, MAYORISTA, DISTRIBUIDOR)
     * @return Precio con descuento aplicado
     */
    BigDecimal calcularPrecioConDescuento(BigDecimal precioBase, Integer cantidad, String tipoCliente);
    
    /**
     * Calcula los impuestos aplicables
     * @param subtotal Subtotal de la cotización
     * @return Monto de impuestos
     */
    BigDecimal calcularImpuestos(BigDecimal subtotal);
    
    /**
     * Calcula el descuento total aplicable
     * @param subtotal Subtotal antes de descuentos
     * @param tipoCliente Tipo de cliente
     * @return Monto de descuento
     */
    BigDecimal calcularDescuentoTotal(BigDecimal subtotal, String tipoCliente);
}
