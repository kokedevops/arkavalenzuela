package com.arka.gestorsolicitudes.cli;

import com.arka.gestorsolicitudes.application.service.CalculoEnvioService;
import com.arka.gestorsolicitudes.domain.model.CalculoEnvio;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades CLI para Circuit Breaker que pueden ser llamadas desde REST
 */
@Component
public class CircuitBreakerCLIUtils {

    private final CalculoEnvioService calculoEnvioService;

    public CircuitBreakerCLIUtils(CalculoEnvioService calculoEnvioService) {
        this.calculoEnvioService = calculoEnvioService;
    }

    /**
     * Ejecuta una prueba de carga programática
     */
    public String ejecutarPruebaDeCarga(int numLlamadas, String escenario) {
        StringBuilder resultado = new StringBuilder();
        resultado.append("🔄 PRUEBA DE CARGA - Circuit Breaker\n");
        resultado.append("════════════════════════════════════════\n");
        resultado.append(String.format("📊 Llamadas: %d | Escenario: %s\n", numLlamadas, escenario));
        resultado.append(String.format("🕐 Inicio: %s\n\n", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));

        int exitosos = 0, fallbacks = 0, errores = 0;
        long tiempoInicio = System.currentTimeMillis();

        for (int i = 1; i <= numLlamadas; i++) {
            try {
                CalculoEnvio calculo = calculoEnvioService
                        .probarCircuitBreaker(escenario, "Lima", "Arequipa", BigDecimal.valueOf(1.0))
                        .block();

                if (calculo != null) {
                    switch (calculo.getEstado()) {
                        case COMPLETADO -> {
                            exitosos++;
                            resultado.append(String.format("✅ %02d: %s\n", i, calculo.getProveedorUtilizado()));
                        }
                        case FALLBACK -> {
                            fallbacks++;
                            resultado.append(String.format("🔄 %02d: %s (Fallback)\n", i, calculo.getProveedorUtilizado()));
                        }
                        default -> {
                            errores++;
                            resultado.append(String.format("❌ %02d: %s\n", i, calculo.getEstado()));
                        }
                    }
                } else {
                    errores++;
                    resultado.append(String.format("❌ %02d: No response\n", i));
                }

                Thread.sleep(100); // Pausa entre llamadas
            } catch (Exception e) {
                errores++;
                resultado.append(String.format("❌ %02d: %s\n", i, e.getMessage()));
            }
        }

        long tiempoTotal = System.currentTimeMillis() - tiempoInicio;

        resultado.append("\n📈 RESUMEN DE RESULTADOS:\n");
        resultado.append("════════════════════════════════════════\n");
        resultado.append(String.format("✅ Exitosos: %d (%.1f%%)\n", exitosos, (double) exitosos / numLlamadas * 100));
        resultado.append(String.format("🔄 Fallbacks: %d (%.1f%%)\n", fallbacks, (double) fallbacks / numLlamadas * 100));
        resultado.append(String.format("❌ Errores: %d (%.1f%%)\n", errores, (double) errores / numLlamadas * 100));
        resultado.append(String.format("⏱️  Tiempo total: %d ms\n", tiempoTotal));
        resultado.append(String.format("📊 Promedio por llamada: %.1f ms\n", (double) tiempoTotal / numLlamadas));

        // Análisis del Circuit Breaker
        resultado.append("\n🔍 ANÁLISIS CIRCUIT BREAKER:\n");
        resultado.append("════════════════════════════════════════\n");
        if (fallbacks > 0) {
            resultado.append("🔴 Circuit Breaker ACTIVADO durante la prueba\n");
            resultado.append("🛡️  Fallbacks protegieron el sistema de fallos en cascada\n");
        } else {
            resultado.append("🟢 Circuit Breaker en estado NORMAL\n");
            resultado.append("✨ Todos los servicios funcionaron correctamente\n");
        }

        return resultado.toString();
    }

    /**
     * Genera un reporte de estado del Circuit Breaker
     */
    public String generarReporteEstado() {
        StringBuilder reporte = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        reporte.append("📊 REPORTE DE ESTADO - CIRCUIT BREAKER\n");
        reporte.append("════════════════════════════════════════════════════════════════\n");
        reporte.append(String.format("🕐 Fecha: %s\n", LocalDateTime.now().format(formatter)));
        reporte.append("🏢 Servicio: Arca Gestor Solicitudes\n");
        reporte.append("🔒 Componente: Circuit Breaker para Cálculo de Envío\n\n");

        try {
            String estadoServicio = calculoEnvioService.obtenerEstadoCalculos().block();
            reporte.append("✅ Estado del Servicio: ").append(estadoServicio).append("\n");
        } catch (Exception e) {
            reporte.append("❌ Error al obtener estado: ").append(e.getMessage()).append("\n");
        }

        reporte.append("\n🛠️  CONFIGURACIÓN ACTUAL:\n");
        reporte.append("────────────────────────────────────────\n");
        reporte.append("• Proveedor Externo Service:\n");
        reporte.append("  - Sliding Window: 8 llamadas\n");
        reporte.append("  - Failure Rate Threshold: 60%\n");
        reporte.append("  - Wait Duration: 15 segundos\n\n");
        reporte.append("• Calculo Envio Service:\n");
        reporte.append("  - Sliding Window: 10 llamadas\n");
        reporte.append("  - Failure Rate Threshold: 50%\n");
        reporte.append("  - Wait Duration: 10 segundos\n");
        reporte.append("  - Retry: 3 intentos\n");
        reporte.append("  - Timeout: 5 segundos\n");

        reporte.append("\n💡 RECOMENDACIONES:\n");
        reporte.append("────────────────────────────────────────\n");
        reporte.append("• Monitoree las métricas regularmente\n");
        reporte.append("• Ejecute pruebas de carga periódicamente\n");
        reporte.append("• Verifique los logs para detectar patrones\n");
        reporte.append("• Ajuste los umbrales según el comportamiento observado\n");

        return reporte.toString();
    }

    /**
     * Ejecuta una demostración completa del Circuit Breaker
     */
    public String ejecutarDemostracion() {
        StringBuilder demo = new StringBuilder();
        demo.append("🎭 DEMOSTRACIÓN CIRCUIT BREAKER\n");
        demo.append("════════════════════════════════════════════════════════════════\n\n");

        // Prueba 1: Servicio normal
        demo.append("🔹 FASE 1: Funcionamiento Normal\n");
        demo.append("────────────────────────────────\n");
        try {
            CalculoEnvio resultado1 = calculoEnvioService
                    .calcularEnvio("Lima", "Arequipa", BigDecimal.valueOf(2.0), "50x30x20")
                    .block();
            if (resultado1 != null) {
                demo.append(String.format("✅ Resultado: %s - %s\n", 
                    resultado1.getEstado(), resultado1.getProveedorUtilizado()));
            }
        } catch (Exception e) {
            demo.append(String.format("❌ Error: %s\n", e.getMessage()));
        }

        // Prueba 2: Forzar fallos
        demo.append("\n🔹 FASE 2: Simulación de Fallos\n");
        demo.append("────────────────────────────────\n");
        for (int i = 1; i <= 5; i++) {
            try {
                CalculoEnvio resultado = calculoEnvioService
                        .probarCircuitBreaker("externo", "Lima", "Cusco", BigDecimal.valueOf(1.0))
                        .block();
                if (resultado != null) {
                    demo.append(String.format("🔄 Intento %d: %s\n", i, resultado.getEstado()));
                }
                Thread.sleep(500);
            } catch (Exception e) {
                demo.append(String.format("❌ Intento %d: %s\n", i, e.getMessage()));
            }
        }

        // Prueba 3: Fallback
        demo.append("\n🔹 FASE 3: Activación de Fallbacks\n");
        demo.append("──────────────────────────────────\n");
        try {
            CalculoEnvio resultado3 = calculoEnvioService
                    .probarCircuitBreaker("completo", "Lima", "Trujillo", BigDecimal.valueOf(3.0))
                    .block();
            if (resultado3 != null) {
                demo.append(String.format("🛡️  Fallback activado: %s - %s\n", 
                    resultado3.getEstado(), resultado3.getProveedorUtilizado()));
                if (resultado3.getMensajeError() != null) {
                    demo.append(String.format("💬 Mensaje: %s\n", resultado3.getMensajeError()));
                }
            }
        } catch (Exception e) {
            demo.append(String.format("❌ Error: %s\n", e.getMessage()));
        }

        demo.append("\n🎯 CONCLUSIÓN:\n");
        demo.append("────────────────────────────────\n");
        demo.append("✨ El Circuit Breaker protege el sistema contra fallos en cascada\n");
        demo.append("🔄 Los fallbacks garantizan la disponibilidad del servicio\n");
        demo.append("📊 El sistema mantiene funcionalidad básica incluso con fallos\n");

        return demo.toString();
    }
}
