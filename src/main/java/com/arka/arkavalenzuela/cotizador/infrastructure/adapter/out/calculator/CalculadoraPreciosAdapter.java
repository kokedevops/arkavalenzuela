package com.arka.arkavalenzuela.cotizador.infrastructure.adapter.out.calculator;

import com.arka.arkavalenzuela.cotizador.domain.port.out.CalculadoraPreciosPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculadoraPreciosAdapter implements CalculadoraPreciosPort {

    private static final BigDecimal TASA_IMPUESTO = new BigDecimal("0.19"); // 19% IVA

    @Override
    public BigDecimal calcularPrecioConDescuento(BigDecimal precioBase, Integer cantidad, String tipoCliente) {
        BigDecimal descuentoPorcentaje = obtenerDescuentoPorTipoCliente(tipoCliente);
        BigDecimal descuentoPorVolumen = obtenerDescuentoPorVolumen(cantidad);
        
        // Aplicar el mayor descuento disponible
        BigDecimal descuentoTotal = descuentoPorcentaje.max(descuentoPorVolumen);
        
        BigDecimal descuento = precioBase.multiply(descuentoTotal)
                .setScale(2, RoundingMode.HALF_UP);
        
        return precioBase.subtract(descuento);
    }

    @Override
    public BigDecimal calcularImpuestos(BigDecimal subtotal) {
        return subtotal.multiply(TASA_IMPUESTO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularDescuentoTotal(BigDecimal subtotal, String tipoCliente) {
        BigDecimal descuentoPorcentaje = obtenerDescuentoPorTipoCliente(tipoCliente);
        return subtotal.multiply(descuentoPorcentaje)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal obtenerDescuentoPorTipoCliente(String tipoCliente) {
        return switch (tipoCliente.toUpperCase()) {
            case "RETAIL" -> BigDecimal.ZERO;
            case "MAYORISTA" -> new BigDecimal("0.10"); // 10%
            case "DISTRIBUIDOR" -> new BigDecimal("0.15"); // 15%
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal obtenerDescuentoPorVolumen(Integer cantidad) {
        if (cantidad >= 100) {
            return new BigDecimal("0.15"); // 15%
        } else if (cantidad >= 50) {
            return new BigDecimal("0.10"); // 10%
        } else if (cantidad >= 20) {
            return new BigDecimal("0.05"); // 5%
        }
        return BigDecimal.ZERO;
    }
}
