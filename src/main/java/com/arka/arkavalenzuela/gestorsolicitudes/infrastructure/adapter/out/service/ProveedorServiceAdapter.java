package com.arka.arkavalenzuela.gestorsolicitudes.infrastructure.adapter.out.service;

import com.arka.arkavalenzuela.gestorsolicitudes.domain.model.Proveedor;
import com.arka.arkavalenzuela.gestorsolicitudes.domain.port.out.ProveedorServicePort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ProveedorServiceAdapter implements ProveedorServicePort {

    // Simulando una base de datos de proveedores
    private final Map<String, Proveedor> proveedores = new ConcurrentHashMap<>();

    public ProveedorServiceAdapter() {
        // Inicializar con algunos proveedores de ejemplo
        inicializarProveedores();
    }

    @Override
    public Proveedor obtenerProveedorPorId(String proveedorId) {
        return proveedores.get(proveedorId);
    }

    @Override
    public List<Proveedor> obtenerProveedoresActivos() {
        return proveedores.values().stream()
                .filter(proveedor -> "ACTIVO".equals(proveedor.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Proveedor> buscarProveedoresPorCategoria(String categoria) {
        // En una implementación real, esto filtrarías por categoría de productos que manejan
        return obtenerProveedoresActivos().stream()
                .filter(proveedor -> proveedor.getObservaciones() != null && 
                                   proveedor.getObservaciones().toLowerCase().contains(categoria.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean verificarProveedorActivo(String proveedorId) {
        Proveedor proveedor = obtenerProveedorPorId(proveedorId);
        return proveedor != null && "ACTIVO".equals(proveedor.getEstado());
    }

    private void inicializarProveedores() {
        Proveedor proveedor1 = new Proveedor(
                "PROV001",
                "Distribuidora TecnoMundo S.A.S",
                "900123456-1",
                "Juan Pérez",
                "+57 300 123 4567",
                "ventas@tecnomundo.com",
                "Calle 45 #12-34",
                "Bogotá",
                "Colombia",
                "ACTIVO",
                new BigDecimal("4.5"),
                "2 días",
                LocalDateTime.now().minusMonths(12),
                "Especializado en tecnología y electrónicos"
        );

        Proveedor proveedor2 = new Proveedor(
                "PROV002",
                "Suministros Industriales del Norte Ltda",
                "800987654-3",
                "María González",
                "+57 310 987 6543",
                "contacto@suministrosnorte.com",
                "Carrera 30 #78-90",
                "Medellín",
                "Colombia",
                "ACTIVO",
                new BigDecimal("4.2"),
                "3 días",
                LocalDateTime.now().minusMonths(18),
                "Especializado en herramientas industriales y maquinaria"
        );

        Proveedor proveedor3 = new Proveedor(
                "PROV003",
                "Comercial El Buen Precio E.U",
                "700456789-2",
                "Carlos Rodríguez",
                "+57 320 456 7890",
                "gerencia@elbuen precio.com",
                "Avenida 15 #25-67",
                "Cali",
                "Colombia",
                "ACTIVO",
                new BigDecimal("4.0"),
                "1 día",
                LocalDateTime.now().minusMonths(6),
                "Productos de consumo general y oficina"
        );

        proveedores.put(proveedor1.getProveedorId(), proveedor1);
        proveedores.put(proveedor2.getProveedorId(), proveedor2);
        proveedores.put(proveedor3.getProveedorId(), proveedor3);
    }
}
