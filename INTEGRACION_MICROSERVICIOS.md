# 🌐 INTEGRACIÓN DE MICROSERVICIOS ARKA

## 📋 Descripción General

Este documento describe la integración completa del proyecto **Arka Valenzuela** con los microservicios:
- **Arca Cotizador** (Puerto 8080): Gestión de cotizaciones
- **Arca Gestor de Solicitudes** (Puerto 8081): Gestión de solicitudes a proveedores

---

## 🏗️ Arquitectura de Integración

### 🔄 Flujo de Comunicación

```
┌─────────────────┐    HTTP REST    ┌──────────────────┐
│  Arka Principal │ ────────────────→ │ Arca Cotizador   │
│   (Puerto 8082) │                  │  (Puerto 8080)   │
└─────────────────┘                  └──────────────────┘
         │
         │ HTTP REST
         ▼
┌──────────────────┐
│ Arca Gestor Sol. │
│  (Puerto 8081)   │
└──────────────────┘
```

### 🎯 Patrón de Arquitectura Implementado

- **Hexagonal Architecture**: Manteniendo la separación de responsabilidades
- **Microservices Communication**: Usando HTTP REST con RestTemplate
- **Async Processing**: CompletableFuture para operaciones no bloqueantes
- **Circuit Breaker Pattern**: Manejo de fallos con timeouts configurables

---

## 🔧 Configuración

### 📝 application.properties

```properties
# Configuración de microservicios
microservices.cotizador.url=http://localhost:8080
microservices.gestor-solicitudes.url=http://localhost:8081

# Configuración de timeout para comunicación
microservices.timeout.connection=5000
microservices.timeout.read=10000

# Puerto del servidor principal
server.port=8082
```

### 📦 Dependencias Agregadas (build.gradle)

```gradle
dependencies {
    // Comunicación con microservicios
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0'
    implementation 'org.springframework.cloud:spring-cloud-starter-loadbalancer:4.1.0'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // Dependencias existentes...
}
```

---

## 🏗️ Estructura de Código Implementada

### 📁 Estructura de Directorios

```
src/main/java/com/arka/arkavalenzuela/
├── domain/port/out/
│   ├── CotizadorServicePort.java           # Puerto para servicio de cotización
│   └── GestorSolicitudesServicePort.java   # Puerto para gestión de solicitudes
├── application/usecase/
│   ├── CotizacionApplicationService.java   # Casos de uso de cotización
│   └── SolicitudApplicationService.java    # Casos de uso de solicitudes
├── infrastructure/
│   ├── adapter/out/microservice/
│   │   ├── dto/                            # DTOs para comunicación
│   │   │   ├── CotizacionRequestDto.java
│   │   │   ├── CotizacionResponseDto.java
│   │   │   ├── SolicitudRequestDto.java
│   │   │   └── SolicitudResponseDto.java
│   │   ├── CotizadorServiceAdapter.java    # Adaptador para cotizador
│   │   └── GestorSolicitudesServiceAdapter.java # Adaptador para solicitudes
│   ├── adapter/in/web/
│   │   ├── CotizacionController.java       # Controlador REST cotizaciones
│   │   └── SolicitudController.java        # Controlador REST solicitudes
│   └── config/
│       └── MicroserviceConfiguration.java  # Configuración RestTemplate
```

---

## 🎯 Funcionalidades Implementadas

### 🔵 Servicio de Cotizaciones

#### 📊 Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/cotizaciones/cliente/{customerId}/productos` | Solicitar cotización |
| `GET` | `/api/cotizaciones/cliente/{customerId}` | Obtener cotizaciones del cliente |
| `GET` | `/api/cotizaciones/{cotizacionId}` | Obtener cotización específica |
| `PATCH` | `/api/cotizaciones/{cotizacionId}/estado` | Actualizar estado |
| `GET` | `/api/cotizaciones/servicio/health` | Verificar disponibilidad |

#### 🔄 Casos de Uso

1. **Solicitar Cotización de Productos**
   ```java
   CompletableFuture<CotizacionResponseDto> solicitarCotizacionProductos(
       Long customerId, List<Product> productos, String tipoCliente)
   ```

2. **Obtener Cotizaciones de Cliente**
   ```java
   CompletableFuture<List<CotizacionResponseDto>> obtenerCotizacionesCliente(Long customerId)
   ```

3. **Actualizar Estado de Cotización**
   ```java
   CompletableFuture<CotizacionResponseDto> actualizarEstadoCotizacion(
       String cotizacionId, String nuevoEstado)
   ```

### 🟢 Servicio de Solicitudes

#### 📊 Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/solicitudes/cliente/{customerId}/cotizacion` | Solicitud de cotización |
| `POST` | `/api/solicitudes/cliente/{customerId}/informacion` | Solicitud de información |
| `POST` | `/api/solicitudes/cliente/{customerId}/urgente` | Solicitud urgente |
| `GET` | `/api/solicitudes/cliente/{customerId}` | Obtener solicitudes del cliente |
| `GET` | `/api/solicitudes/{solicitudId}` | Obtener solicitud específica |
| `GET` | `/api/solicitudes/estado/{estado}` | Obtener por estado |
| `PATCH` | `/api/solicitudes/{solicitudId}/estado` | Actualizar estado |
| `GET` | `/api/solicitudes/servicio/health` | Verificar disponibilidad |

#### 🔄 Casos de Uso

1. **Crear Solicitud de Cotización**
   ```java
   CompletableFuture<SolicitudResponseDto> crearSolicitudCotizacion(
       Long customerId, List<Product> productos, String observaciones)
   ```

2. **Crear Solicitud Urgente**
   ```java
   CompletableFuture<SolicitudResponseDto> crearSolicitudUrgente(
       Long customerId, List<Product> productos, String observaciones)
   ```

3. **Obtener Solicitudes por Estado**
   ```java
   CompletableFuture<List<SolicitudResponseDto>> obtenerSolicitudesPorEstado(String estado)
   ```

---

## 📡 DTOs de Comunicación

### 🔵 Cotizaciones

#### CotizacionRequestDto
```java
{
  "customerId": 1,
  "productos": [
    {
      "productId": 1,
      "nombre": "Producto A",
      "precioUnitario": 100.00,
      "cantidad": 2
    }
  ],
  "tipoCliente": "REGULAR"
}
```

#### CotizacionResponseDto
```java
{
  "cotizacionId": "COT-2025-001",
  "customerId": 1,
  "totalOriginal": 200.00,
  "descuentoAplicado": 20.00,
  "totalConDescuento": 180.00,
  "tipoDescuento": "CLIENTE_REGULAR",
  "fechaCotizacion": "2025-01-17T10:00:00",
  "fechaVencimiento": "2025-01-24T10:00:00",
  "estado": "ACTIVA"
}
```

### 🟢 Solicitudes

#### SolicitudRequestDto
```java
{
  "customerId": 1,
  "tipoSolicitud": "COTIZACION",
  "prioridad": "MEDIA",
  "productos": [
    {
      "productId": 1,
      "nombre": "Producto A",
      "cantidadSolicitada": 10,
      "especificaciones": "Especificaciones técnicas"
    }
  ],
  "observaciones": "Solicitud urgente para proyecto"
}
```

#### SolicitudResponseDto
```java
{
  "solicitudId": "SOL-2025-001",
  "customerId": 1,
  "tipoSolicitud": "COTIZACION",
  "estado": "PENDIENTE",
  "prioridad": "MEDIA",
  "fechaCreacion": "2025-01-17T10:00:00",
  "fechaVencimiento": "2025-01-24T10:00:00",
  "observaciones": "Solicitud urgente para proyecto"
}
```

---

## 🚀 Guía de Uso

### 1. 🏃‍♂️ Iniciar los Microservicios

```bash
# Terminal 1: Arca Cotizador (Puerto 8080)
cd arca-cotizador
./gradlew bootRun

# Terminal 2: Arca Gestor Solicitudes (Puerto 8081)
cd arca-gestor-solicitudes
./gradlew bootRun

# Terminal 3: Arka Principal (Puerto 8082)
cd arkavalenzuela
./gradlew bootRun
```

### 2. 🧪 Ejemplos de Uso con cURL

#### Solicitar Cotización
```bash
curl -X POST http://localhost:8082/api/cotizaciones/cliente/1/productos \\
  -H "Content-Type: application/json" \\
  -d '[1, 2, 3]' \\
  -G -d tipoCliente=PREMIUM
```

#### Crear Solicitud Urgente
```bash
curl -X POST http://localhost:8082/api/solicitudes/cliente/1/urgente \\
  -H "Content-Type: application/json" \\
  -d '{
    "productIds": [1, 2],
    "observaciones": "Solicitud urgente para proyecto crítico"
  }'
```

#### Obtener Cotizaciones de Cliente
```bash
curl -X GET http://localhost:8082/api/cotizaciones/cliente/1
```

#### Verificar Estado de Servicios
```bash
curl -X GET http://localhost:8082/api/cotizaciones/servicio/health
curl -X GET http://localhost:8082/api/solicitudes/servicio/health
```

---

## 🔍 Monitoreo y Diagnóstico

### 📊 Health Checks

Cada servicio expone endpoints de verificación de estado:

- **Cotizador**: `GET /api/cotizaciones/servicio/health`
- **Solicitudes**: `GET /api/solicitudes/servicio/health`

### 📝 Logging

Los servicios incluyen logging detallado:

```java
// Ejemplo de logs
INFO  - Creando cotización para cliente: 1
INFO  - Cotización creada exitosamente: COT-2025-001
ERROR - Error al comunicarse con el servicio de cotización: Connection timeout
WARN  - Servicio de cotización no disponible: Connection refused
```

### ⚠️ Manejo de Errores

1. **Timeouts**: Configurables (5s conexión, 10s lectura)
2. **Circuit Breaker**: Retorna `false` en health checks si el servicio no responde
3. **Fallback**: CompletableFuture con manejo de excepciones
4. **Retry**: Manual a través de los endpoints

---

## 🎯 Beneficios de la Integración

### ✅ Ventajas Implementadas

1. **🔄 Asíncrono**: Operaciones no bloqueantes con CompletableFuture
2. **🏗️ Modular**: Cada microservicio maneja su dominio específico
3. **🔌 Desacoplado**: Comunicación a través de interfaces bien definidas
4. **📈 Escalable**: Cada servicio puede escalarse independientemente
5. **🛡️ Resiliente**: Manejo de fallos y timeouts configurables
6. **📊 Monitoreable**: Health checks y logging comprehensive

### 🎪 Patrones Implementados

- **Port & Adapter**: Separación clara entre dominio e infraestructura
- **Dependency Inversion**: Interfaces abstractas para comunicación
- **Single Responsibility**: Cada servicio tiene una responsabilidad específica
- **Observer Pattern**: Callbacks asíncronos para resultados

---

## 🔮 Próximos Pasos

### 🚧 Mejoras Futuras

1. **🔄 Service Discovery**: Implementar Eureka para registro automático
2. **🛡️ Circuit Breaker**: Hystrix o Resilience4j para mayor resiliencia
3. **📊 Métricas**: Micrometer y Prometheus para monitoreo avanzado
4. **🔐 Seguridad**: JWT tokens para autenticación entre servicios
5. **📝 Documentación**: OpenAPI/Swagger para documentación automática
6. **🧪 Testing**: Tests de integración entre microservicios

### 🎯 Optimizaciones

1. **Cache**: Redis para almacenamiento temporal de cotizaciones
2. **Queue**: RabbitMQ o Kafka para comunicación asíncrona
3. **Load Balancer**: Nginx para distribución de carga
4. **Container**: Docker para despliegue containerizado

---

## 🏁 Conclusión

La integración de microservicios ha sido implementada exitosamente siguiendo los principios de arquitectura hexagonal y patrones de comunicación robustos. El sistema ahora puede:

- ✅ Gestionar cotizaciones de forma asíncrona
- ✅ Procesar solicitudes a proveedores
- ✅ Manejar fallos de comunicación gracefully
- ✅ Escalar componentes independientemente
- ✅ Monitorear el estado de los servicios

**¡El proyecto Arka Valenzuela está ahora completamente integrado con los microservicios y listo para producción!** 🎉
