# Microservicios Arca Valenzuela

Este documento describe los dos microservicios implementados en la aplicación Arka Valenzuela:

## 1. Microservicio Arca Cotizador

### Descripción
El microservicio Arca Cotizador se encarga de recibir solicitudes de cotización y generar cotizaciones completas con precios, descuentos, impuestos y condiciones comerciales.

### Funcionalidades
- ✅ Generación de cotizaciones automáticas
- ✅ Cálculo de precios con descuentos por tipo de cliente
- ✅ Cálculo de descuentos por volumen
- ✅ Aplicación de impuestos (19% IVA)
- ✅ Verificación de disponibilidad de productos
- ✅ Consulta de cotizaciones existentes
- ✅ Actualización de estados de cotización

### Endpoints REST

#### POST `/api/v1/cotizador/cotizaciones`
Genera una nueva cotización.

**Request Body:**
```json
{
  "clienteId": "CLI001",
  "tipoCliente": "MAYORISTA",
  "observaciones": "Cotización para compra mensual",
  "productos": [
    {
      "productoId": 1,
      "cantidad": 50,
      "precioBase": 100.00,
      "observaciones": "Producto urgente"
    }
  ]
}
```

**Response:**
```json
{
  "cotizacionId": "uuid-generado",
  "clienteId": "CLI001",
  "subtotal": 5000.00,
  "descuentos": 500.00,
  "impuestos": 855.00,
  "total": 5355.00,
  "fechaCotizacion": "2025-07-22T10:30:00",
  "fechaVencimiento": "2025-08-21T10:30:00",
  "estado": "PENDIENTE",
  "condicionesPago": "30 días",
  "tiempoEntrega": 5,
  "productos": [...]
}
```

#### GET `/api/v1/cotizador/cotizaciones/{cotizacionId}`
Consulta una cotización específica.

#### PUT `/api/v1/cotizador/cotizaciones/{cotizacionId}/estado`
Actualiza el estado de una cotización.

### Tipos de Cliente y Descuentos
- **RETAIL**: Sin descuento (0%)
- **MAYORISTA**: Descuento del 10%
- **DISTRIBUIDOR**: Descuento del 15%

### Descuentos por Volumen
- 20+ productos: 5% descuento
- 50+ productos: 10% descuento
- 100+ productos: 15% descuento

---

## 2. Microservicio Arca Gestor de Solicitudes

### Descripción
El microservicio Arca Gestor de Solicitudes se encarga de gestionar las solicitudes a proveedores de productos, incluyendo el envío masivo, seguimiento y procesamiento de respuestas.

### Funcionalidades
- ✅ Creación de solicitudes a proveedores individuales
- ✅ Envío masivo de solicitudes a múltiples proveedores
- ✅ Seguimiento del estado de solicitudes
- ✅ Procesamiento de respuestas de proveedores
- ✅ Sistema de notificaciones por email (simulado)
- ✅ Gestión de proveedores activos

### Endpoints REST

#### POST `/api/v1/gestor-solicitudes/solicitudes`
Crea una solicitud para un proveedor específico.

**Request Body:**
```json
{
  "proveedorId": "PROV001",
  "tipoSolicitud": "COTIZACION",
  "prioridad": "ALTA",
  "observaciones": "Solicitud urgente para proyecto",
  "productos": [
    {
      "productoId": 1,
      "nombreProducto": "Laptop Dell",
      "cantidadSolicitada": 25,
      "precioReferencia": 2500000.00,
      "especificaciones": "Core i7, 16GB RAM, SSD 512GB",
      "urgente": true
    }
  ]
}
```

#### POST `/api/v1/gestor-solicitudes/solicitudes/multiple`
Envía la misma solicitud a múltiples proveedores.

**Query Params:** `proveedorIds=PROV001,PROV002,PROV003`

#### GET `/api/v1/gestor-solicitudes/solicitudes/{solicitudId}`
Consulta una solicitud específica.

#### GET `/api/v1/gestor-solicitudes/solicitudes?estado=ENVIADA`
Obtiene todas las solicitudes por estado.

Estados disponibles:
- `PENDIENTE`: Solicitud creada pero no enviada
- `ENVIADA`: Solicitud enviada al proveedor
- `RESPONDIDA`: Proveedor ha respondido
- `COMPLETADA`: Proceso completado
- `CANCELADA`: Solicitud cancelada

#### PUT `/api/v1/gestor-solicitudes/solicitudes/{solicitudId}/estado`
Actualiza el estado de una solicitud.

#### POST `/api/v1/gestor-solicitudes/respuestas`
Procesa la respuesta de un proveedor.

**Request Body:**
```json
{
  "solicitudId": "uuid-solicitud",
  "proveedorId": "PROV001",
  "estado": "ACEPTADA",
  "precioOfertado": 2300000.00,
  "tiempoEntrega": 15,
  "condicionesPago": "30 días",
  "garantia": "12 meses",
  "observaciones": "Precio especial por volumen"
}
```

#### GET `/api/v1/gestor-solicitudes/solicitudes/{solicitudId}/respuestas`
Obtiene todas las respuestas para una solicitud.

### Proveedores Precargados
El sistema incluye 3 proveedores de ejemplo:
- **PROV001**: Distribuidora TecnoMundo S.A.S (Tecnología)
- **PROV002**: Suministros Industriales del Norte Ltda (Industrial)
- **PROV003**: Comercial El Buen Precio E.U (General)

---

## Arquitectura

Ambos microservicios siguen **Arquitectura Hexagonal (Ports & Adapters)**:

```
src/main/java/com/arka/arkavalenzuela/
├── cotizador/
│   ├── domain/
│   │   ├── model/          # Entidades de dominio
│   │   └── port/
│   │       ├── in/         # Casos de uso (interfaces)
│   │       └── out/        # Puertos de salida (interfaces)
│   ├── application/
│   │   └── usecase/        # Implementación de casos de uso
│   └── infrastructure/
│       └── adapter/
│           ├── in/web/     # Controladores REST
│           └── out/        # Adaptadores de infraestructura
└── gestorsolicitudes/
    ├── domain/
    ├── application/
    └── infrastructure/
```

## Tecnologías Utilizadas
- **Spring Boot 3.5.3**
- **Java 21**
- **Spring Web** (REST APIs)
- **Spring Data JPA** (para futura persistencia)
- **Spring WebFlux** (para llamadas reactivas)

## Datos en Memoria
Actualmente ambos microservicios utilizan almacenamiento en memoria con `ConcurrentHashMap` para persistir datos durante la ejecución. En un entorno de producción, esto se reemplazaría con bases de datos reales.

## Pruebas
Para probar los microservicios, puedes usar herramientas como Postman o curl:

### Health Check
```bash
GET http://localhost:8080/api/v1/cotizador/health
GET http://localhost:8080/api/v1/gestor-solicitudes/health
```

### Ejemplo de flujo completo:
1. Crear una solicitud a proveedores
2. Los proveedores responden con ofertas
3. Generar cotización basada en las mejores ofertas
4. Seguir el estado de todo el proceso

## Próximas Mejoras
- [ ] Integración con base de datos real
- [ ] Sistema de autenticación y autorización
- [ ] Notificaciones por email reales
- [ ] Dashboard web para seguimiento
- [ ] Integración con sistemas de proveedores externos
- [ ] Análisis de mejor precio/tiempo de entrega
- [ ] Sistema de aprobaciones
- [ ] Reportes y métricas
