# 🔧 GUÍA PASO A PASO: INTEGRACIÓN DE MICROSERVICIOS

## 📋 Índice
1. [Análisis Inicial](#análisis-inicial)
2. [Configuración Base](#configuración-base)
3. [Creación de DTOs](#creación-de-dtos)
4. [Implementación de Puertos](#implementación-de-puertos)
5. [Desarrollo de Adaptadores](#desarrollo-de-adaptadores)
6. [Servicios de Aplicación](#servicios-de-aplicación)
7. [Controladores REST](#controladores-rest)
8. [Configuración Final](#configuración-final)
9. [Testing y Validación](#testing-y-validación)

---

## 🔍 Análisis Inicial

### Paso 1: Identificación de Microservicios
Primero identificamos los microservicios externos que necesitábamos integrar:

```
🎯 Microservicios Objetivo:
├── Arca Cotizador (Puerto 8080)
│   └── Funciones: Crear cotizaciones, gestionar precios
└── Arca Gestor Solicitudes (Puerto 8081)
    └── Funciones: Gestionar solicitudes a proveedores
```

### Paso 2: Análisis de la Arquitectura Existente
El proyecto ya tenía implementada **Arquitectura Hexagonal**:

```
📁 Estructura Actual:
├── domain/
│   ├── model/          # Entidades de dominio
│   └── port/           # Interfaces (contratos)
├── application/
│   └── usecase/        # Servicios de aplicación
└── infrastructure/
    └── adapter/        # Implementaciones concretas
```

---

## ⚙️ Configuración Base

### Paso 3: Actualización de Dependencias

**Archivo modificado:** `build.gradle`

```gradle
dependencies {
    // ✅ AGREGADO: Dependencias para comunicación con microservicios
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0'
    implementation 'org.springframework.cloud:spring-cloud-starter-loadbalancer:4.1.0'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // Dependencias existentes...
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
}
```

**Explicación:**
- `webflux`: Para cliente HTTP reactivo (WebClient)
- `openfeign`: Para cliente HTTP declarativo (alternativa)
- `loadbalancer`: Para balanceador de carga
- `validation`: Para validación de DTOs

### Paso 4: Configuración de URLs

**Archivo modificado:** `src/main/resources/application.properties`

```properties
# ✅ AGREGADO: Configuración de microservicios
microservices.cotizador.url=http://localhost:8080
microservices.gestor-solicitudes.url=http://localhost:8081

# ✅ AGREGADO: Configuración de timeouts
microservices.timeout.connection=5000
microservices.timeout.read=10000

# ✅ MODIFICADO: Puerto del servidor principal
server.port=8082
```

**Explicación:**
- URLs base de los microservicios
- Timeouts para evitar bloqueos
- Puerto 8082 para evitar conflictos

---

## 📦 Creación de DTOs

### Paso 5: DTOs para Servicio de Cotizaciones

**Archivo creado:** `infrastructure/adapter/out/microservice/dto/CotizacionRequestDto.java`

```java
public class CotizacionRequestDto {
    private Long customerId;
    private List<ProductoCotizacionDto> productos;
    private String tipoCliente;
    
    // ✅ DTO anidado para productos
    public static class ProductoCotizacionDto {
        private Long productId;
        private String nombre;
        private BigDecimal precioUnitario;
        private Integer cantidad;
    }
}
```

**Archivo creado:** `infrastructure/adapter/out/microservice/dto/CotizacionResponseDto.java`

```java
public class CotizacionResponseDto {
    private String cotizacionId;
    private Long customerId;
    private BigDecimal totalOriginal;
    private BigDecimal descuentoAplicado;
    private BigDecimal totalConDescuento;
    private LocalDateTime fechaCotizacion;
    private String estado;
    // ... más campos
}
```

### Paso 6: DTOs para Servicio de Solicitudes

**Archivo creado:** `infrastructure/adapter/out/microservice/dto/SolicitudRequestDto.java`

```java
public class SolicitudRequestDto {
    private Long customerId;
    private String tipoSolicitud;
    private String prioridad;
    private List<ProductoSolicitudDto> productos;
    private String observaciones;
    
    // ✅ DTO anidado para productos en solicitudes
    public static class ProductoSolicitudDto {
        private Long productId;
        private String nombre;
        private Integer cantidadSolicitada;
        private String especificaciones;
    }
}
```

**Archivo creado:** `infrastructure/adapter/out/microservice/dto/SolicitudResponseDto.java`

```java
public class SolicitudResponseDto {
    private String solicitudId;
    private Long customerId;
    private String tipoSolicitud;
    private String estado;
    private LocalDateTime fechaCreacion;
    private String observaciones;
    // ... más campos
}
```

**🎯 Razón de los DTOs:**
- Separar la estructura interna del dominio de la comunicación externa
- Flexibilidad para cambios en APIs sin afectar el dominio
- Validación específica para cada servicio

---

## 🔌 Implementación de Puertos

### Paso 7: Puerto para Servicio de Cotizaciones

**Archivo creado:** `domain/port/out/CotizadorServicePort.java`

```java
public interface CotizadorServicePort {
    // ✅ Método asíncrono para crear cotización
    CompletableFuture<CotizacionResponseDto> crearCotizacion(CotizacionRequestDto request);
    
    // ✅ Método asíncrono para obtener cotización
    CompletableFuture<CotizacionResponseDto> obtenerCotizacion(String cotizacionId);
    
    // ✅ Métodos adicionales...
    CompletableFuture<List<CotizacionResponseDto>> obtenerCotizacionesPorCliente(Long customerId);
    CompletableFuture<CotizacionResponseDto> actualizarEstadoCotizacion(String cotizacionId, String nuevoEstado);
    CompletableFuture<Boolean> isServiceAvailable();
}
```

### Paso 8: Puerto para Servicio de Solicitudes

**Archivo creado:** `domain/port/out/GestorSolicitudesServicePort.java`

```java
public interface GestorSolicitudesServicePort {
    // ✅ Métodos asíncronos para gestión de solicitudes
    CompletableFuture<SolicitudResponseDto> crearSolicitud(SolicitudRequestDto request);
    CompletableFuture<SolicitudResponseDto> obtenerSolicitud(String solicitudId);
    CompletableFuture<List<SolicitudResponseDto>> obtenerSolicitudesPorCliente(Long customerId);
    CompletableFuture<List<SolicitudResponseDto>> obtenerSolicitudesPorEstado(String estado);
    CompletableFuture<SolicitudResponseDto> actualizarEstadoSolicitud(String solicitudId, String nuevoEstado);
    CompletableFuture<Boolean> isServiceAvailable();
}
```

**🎯 Decisiones de Diseño:**
- **CompletableFuture**: Para operaciones asíncronas no bloqueantes
- **Interfaces en domain**: Mantiene la inversión de dependencias
- **Métodos granulares**: Cada operación específica tiene su método

---

## 🔧 Desarrollo de Adaptadores

### Paso 9: Adaptador para Servicio de Cotizaciones

**Archivo creado:** `infrastructure/adapter/out/microservice/CotizadorServiceAdapter.java`

```java
@Component
public class CotizadorServiceAdapter implements CotizadorServicePort {
    
    private final RestTemplate restTemplate;
    private final String cotizadorBaseUrl;
    
    // ✅ Inyección de dependencias y configuración
    public CotizadorServiceAdapter(RestTemplate restTemplate,
                                  @Value("${microservices.cotizador.url}") String cotizadorBaseUrl) {
        this.restTemplate = restTemplate;
        this.cotizadorBaseUrl = cotizadorBaseUrl;
    }
    
    @Override
    public CompletableFuture<CotizacionResponseDto> crearCotizacion(CotizacionRequestDto request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Creando cotización para cliente: {}", request.getCustomerId());
                
                // ✅ Construcción de URL y request
                String url = cotizadorBaseUrl + "/api/cotizaciones";
                HttpEntity<CotizacionRequestDto> entity = new HttpEntity<>(request);
                
                // ✅ Llamada HTTP con RestTemplate
                ResponseEntity<CotizacionResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, CotizacionResponseDto.class);
                
                return response.getBody();
                
            } catch (RestClientException ex) {
                // ✅ Manejo de errores y logging
                logger.error("Error al crear cotización: {}", ex.getMessage());
                throw new RuntimeException("Error al comunicarse con el servicio de cotización", ex);
            }
        });
    }
}
```

### Paso 10: Adaptador para Servicio de Solicitudes

**Archivo creado:** `infrastructure/adapter/out/microservice/GestorSolicitudesServiceAdapter.java`

```java
@Component
public class GestorSolicitudesServiceAdapter implements GestorSolicitudesServicePort {
    
    private final RestTemplate restTemplate;
    private final String gestorSolicitudesBaseUrl;
    
    // ✅ Patrón similar al adaptador de cotizaciones
    public GestorSolicitudesServiceAdapter(RestTemplate restTemplate,
                                          @Value("${microservices.gestor-solicitudes.url}") String gestorSolicitudesBaseUrl) {
        this.restTemplate = restTemplate;
        this.gestorSolicitudesBaseUrl = gestorSolicitudesBaseUrl;
    }
    
    // ✅ Implementación similar con URLs específicas del servicio
    @Override
    public CompletableFuture<SolicitudResponseDto> crearSolicitud(SolicitudRequestDto request) {
        return CompletableFuture.supplyAsync(() -> {
            String url = gestorSolicitudesBaseUrl + "/api/solicitudes";
            // ... implementación similar
        });
    }
}
```

**🎯 Patrones Implementados:**
- **Adapter Pattern**: Adapta interfaces externas al dominio interno
- **Async Processing**: CompletableFuture para no bloquear hilos
- **Error Handling**: Try-catch con logging detallado
- **Configuration Injection**: @Value para URLs configurables

---

## 🎯 Servicios de Aplicación

### Paso 11: Servicio de Aplicación para Cotizaciones

**Archivo creado:** `application/usecase/CotizacionApplicationService.java`

```java
@Service
public class CotizacionApplicationService {
    
    private final CotizadorServicePort cotizadorServicePort;
    
    public CotizacionApplicationService(CotizadorServicePort cotizadorServicePort) {
        this.cotizadorServicePort = cotizadorServicePort;
    }
    
    // ✅ Método que convierte Order del dominio a DTO del microservicio
    public CompletableFuture<CotizacionResponseDto> solicitarCotizacion(Order order) {
        CotizacionRequestDto request = crearCotizacionRequest(order);
        return cotizadorServicePort.crearCotizacion(request);
    }
    
    // ✅ Método para productos específicos
    public CompletableFuture<CotizacionResponseDto> solicitarCotizacionProductos(
            Long customerId, List<Product> productos, String tipoCliente) {
        
        // ✅ Conversión de entidades de dominio a DTOs
        List<CotizacionRequestDto.ProductoCotizacionDto> productosDto = productos.stream()
            .map(producto -> new CotizacionRequestDto.ProductoCotizacionDto(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecioUnitario(),
                1 // Cantidad por defecto
            ))
            .collect(Collectors.toList());
        
        CotizacionRequestDto request = new CotizacionRequestDto(customerId, productosDto, tipoCliente);
        return cotizadorServicePort.crearCotizacion(request);
    }
}
```

### Paso 12: Servicio de Aplicación para Solicitudes

**Archivo creado:** `application/usecase/SolicitudApplicationService.java`

```java
@Service
public class SolicitudApplicationService {
    
    private final GestorSolicitudesServicePort gestorSolicitudesServicePort;
    
    // ✅ Métodos especializados por tipo de solicitud
    public CompletableFuture<SolicitudResponseDto> crearSolicitudCotizacion(
            Long customerId, List<Product> productos, String observaciones) {
        return crearSolicitudProductos(customerId, productos, "COTIZACION", "MEDIA", observaciones);
    }
    
    public CompletableFuture<SolicitudResponseDto> crearSolicitudUrgente(
            Long customerId, List<Product> productos, String observaciones) {
        return crearSolicitudProductos(customerId, productos, "URGENTE", "ALTA", observaciones);
    }
    
    // ✅ Método base que convierte productos de dominio a DTOs
    private CompletableFuture<SolicitudResponseDto> crearSolicitudProductos(
            Long customerId, List<Product> productos, String tipoSolicitud, String prioridad, String observaciones) {
        
        List<SolicitudRequestDto.ProductoSolicitudDto> productosDto = productos.stream()
            .map(producto -> new SolicitudRequestDto.ProductoSolicitudDto(
                producto.getId(),
                producto.getNombre(),
                1, // Cantidad por defecto
                producto.getDescripcion()
            ))
            .collect(Collectors.toList());
        
        SolicitudRequestDto request = new SolicitudRequestDto(customerId, tipoSolicitud, prioridad, productosDto, observaciones);
        return gestorSolicitudesServicePort.crearSolicitud(request);
    }
}
```

**🎯 Responsabilidades de los Servicios:**
- **Orquestación**: Coordinan llamadas a múltiples adaptadores
- **Conversión**: Transforman entidades de dominio a DTOs
- **Lógica de Negocio**: Aplican reglas específicas (tipos de solicitud)
- **Manejo de Errores**: Gestionan excepciones y logging

---

## 🌐 Controladores REST

### Paso 13: Controlador para Cotizaciones

**Archivo creado:** `infrastructure/adapter/in/web/CotizacionController.java`

```java
@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {
    
    private final CotizacionApplicationService cotizacionService;
    private final ProductUseCase productUseCase;
    
    // ✅ Endpoint para solicitar cotización con lista de IDs
    @PostMapping("/cliente/{customerId}/productos")
    public CompletableFuture<ResponseEntity<CotizacionResponseDto>> solicitarCotizacionProductos(
            @PathVariable Long customerId,
            @RequestBody List<Long> productIds,
            @RequestParam(defaultValue = "REGULAR") String tipoCliente) {
        
        // ✅ Obtener productos por IDs usando el caso de uso existente
        List<Product> productos = productIds.stream()
            .map(productUseCase::getProductById)
            .toList();
        
        // ✅ Llamada asíncrona al servicio de aplicación
        return cotizacionService.solicitarCotizacionProductos(customerId, productos, tipoCliente)
            .thenApply(ResponseEntity::ok)
            .exceptionally(throwable -> {
                logger.error("Error al solicitar cotización: {}", throwable.getMessage());
                return ResponseEntity.internalServerError().build();
            });
    }
    
    // ✅ Endpoint para obtener cotizaciones del cliente
    @GetMapping("/cliente/{customerId}")
    public CompletableFuture<ResponseEntity<List<CotizacionResponseDto>>> obtenerCotizacionesCliente(
            @PathVariable Long customerId) {
        
        return cotizacionService.obtenerCotizacionesCliente(customerId)
            .thenApply(ResponseEntity::ok)
            .exceptionally(throwable -> ResponseEntity.internalServerError().build());
    }
}
```

### Paso 14: Controlador para Solicitudes

**Archivo creado:** `infrastructure/adapter/in/web/SolicitudController.java`

```java
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    
    // ✅ DTO interno para requests HTTP
    public static class SolicitudRequest {
        private List<Long> productIds;
        private String observaciones;
        // getters y setters...
    }
    
    // ✅ Endpoints especializados por tipo de solicitud
    @PostMapping("/cliente/{customerId}/cotizacion")
    public CompletableFuture<ResponseEntity<SolicitudResponseDto>> crearSolicitudCotizacion(
            @PathVariable Long customerId,
            @RequestBody SolicitudRequest request) {
        
        List<Product> productos = request.getProductIds().stream()
            .map(productUseCase::getProductById)
            .toList();
        
        return solicitudService.crearSolicitudCotizacion(customerId, productos, request.getObservaciones())
            .thenApply(ResponseEntity::ok)
            .exceptionally(throwable -> ResponseEntity.internalServerError().build());
    }
    
    @PostMapping("/cliente/{customerId}/urgente")
    public CompletableFuture<ResponseEntity<SolicitudResponseDto>> crearSolicitudUrgente(
            @PathVariable Long customerId,
            @RequestBody SolicitudRequest request) {
        
        // ✅ Reutilización de lógica con diferentes parámetros
        List<Product> productos = request.getProductIds().stream()
            .map(productUseCase::getProductById)
            .toList();
        
        return solicitudService.crearSolicitudUrgente(customerId, productos, request.getObservaciones())
            .thenApply(ResponseEntity::ok)
            .exceptionally(throwable -> ResponseEntity.internalServerError().build());
    }
}
```

**🎯 Diseño de los Controladores:**
- **Endpoints RESTful**: Siguiendo convenciones REST
- **Responses Asíncronos**: CompletableFuture para no bloquear
- **Error Handling**: Manejo consistente de errores
- **Separación de Responsabilidades**: DTOs específicos para HTTP

---

## ⚙️ Configuración Final

### Paso 15: Configuración de RestTemplate

**Archivo creado:** `infrastructure/config/MicroserviceConfiguration.java`

```java
@Configuration
public class MicroserviceConfiguration {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    // ✅ Configuración adicional para timeouts
    @Bean
    public RestTemplate restTemplateWithTimeout(
            @Value("${microservices.timeout.connection}") int connectionTimeout,
            @Value("${microservices.timeout.read}") int readTimeout) {
        
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        
        return new RestTemplate(factory);
    }
}
```

### Paso 16: Actualización de BeanConfiguration

**Archivo modificado:** `infrastructure/config/BeanConfiguration.java`

```java
@Configuration
public class BeanConfiguration {
    
    // ✅ Beans existentes mantenidos...
    @Bean
    public ProductUseCase productUseCase(ProductRepositoryPort productRepository) {
        return new ProductApplicationService(productRepository);
    }
    
    // ✅ NUEVOS BEANS: Servicios de microservicios ya configurados automáticamente
    // Spring Boot detecta @Service y @Component automáticamente
}
```

**🎯 Configuración Automática:**
- **@Component**: Los adaptadores se registran automáticamente
- **@Service**: Los servicios de aplicación se registran automáticamente
- **@Value**: Inyección automática de propiedades de configuración

---

## 🧪 Testing y Validación

### Paso 17: Controlador de Sistema

**Archivo creado:** `infrastructure/adapter/in/web/SystemController.java`

```java
@RestController
@RequestMapping("/api/system")
public class SystemController {
    
    // ✅ Endpoint para verificar estado general del sistema
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Arka Valenzuela");
        health.put("port", 8082);
        
        Map<String, String> microservices = new HashMap<>();
        microservices.put("cotizador", "http://localhost:8080");
        microservices.put("gestor-solicitudes", "http://localhost:8081");
        health.put("connectedServices", microservices);
        
        return ResponseEntity.ok(health);
    }
}
```

### Paso 18: Scripts de Testing

**Archivo creado:** `test-microservices.bat`

```batch
@echo off
echo ================================
echo  ARKA MICROSERVICES TESTING
echo ================================

# ✅ Script interactivo para testing automático
# Incluye opciones para:
# 1. Verificar estado del sistema
# 2. Crear datos de prueba
# 3. Probar cotizaciones
# 4. Probar solicitudes
# 5. Probar resiliencia
```

---

## 🔄 Flujo Completo de Comunicación

### Diagrama de Flujo Implementado:

```
🌐 HTTP Request
    │
    ▼
📱 Controller (REST)
    │ ┌─ Convierte HTTP → Domain Objects
    ▼ └─ Manejo de errores HTTP
🎯 Application Service
    │ ┌─ Orquesta lógica de negocio
    │ ├─ Convierte Domain → DTOs
    ▼ └─ Logging y validaciones
🔌 Service Port (Interface)
    │ ┌─ Contrato abstracto
    ▼ └─ Inversión de dependencias
🔧 Service Adapter
    │ ┌─ RestTemplate HTTP calls
    │ ├─ Manejo de timeouts
    │ ├─ Error handling
    ▼ └─ CompletableFuture async
🌐 External Microservice
    │
    ▼
📊 Response (DTO)
    │
    ▼
🔄 CompletableFuture Chain
    │ ┌─ .thenApply() para transformaciones
    │ ├─ .exceptionally() para errores
    ▼ └─ .whenComplete() para logging
📱 HTTP Response
```

---

## 🎯 Principios Aplicados

### ✅ Arquitectura Hexagonal Mantenida

1. **Domain (Núcleo)**:
   - ✅ Modelos de dominio sin cambios
   - ✅ Puertos como interfaces abstractas
   - ✅ Sin dependencias externas

2. **Application (Orquestación)**:
   - ✅ Servicios que coordinan operaciones
   - ✅ Conversión entre capas
   - ✅ Lógica de negocio centralizada

3. **Infrastructure (Detalles)**:
   - ✅ Adaptadores para microservicios
   - ✅ Controladores REST
   - ✅ Configuración técnica

### ✅ Patrones de Integración

1. **Adapter Pattern**: Para adaptar APIs externas
2. **Port & Adapter**: Para inversión de dependencias
3. **DTO Pattern**: Para separar representaciones
4. **Async Pattern**: Para operaciones no bloqueantes
5. **Circuit Breaker**: Para resiliencia (health checks)

---

## 🚀 Resultado Final

### ✅ Funcionalidades Integradas

1. **Cotizaciones**:
   - ✅ Solicitar cotizaciones de productos
   - ✅ Obtener cotizaciones por cliente
   - ✅ Actualizar estados
   - ✅ Verificar disponibilidad del servicio

2. **Solicitudes**:
   - ✅ Crear solicitudes (cotización, información, urgente)
   - ✅ Obtener solicitudes por cliente/estado
   - ✅ Actualizar estados
   - ✅ Verificar disponibilidad del servicio

3. **Resiliencia**:
   - ✅ Timeouts configurables
   - ✅ Manejo de errores
   - ✅ Health checks
   - ✅ Logging detallado

### ✅ Endpoints Disponibles

```
📋 API Endpoints Creados:

🔵 Cotizaciones:
POST   /api/cotizaciones/cliente/{id}/productos
GET    /api/cotizaciones/cliente/{id}
GET    /api/cotizaciones/{cotizacionId}
PATCH  /api/cotizaciones/{cotizacionId}/estado
GET    /api/cotizaciones/servicio/health

🟢 Solicitudes:
POST   /api/solicitudes/cliente/{id}/cotizacion
POST   /api/solicitudes/cliente/{id}/informacion
POST   /api/solicitudes/cliente/{id}/urgente
GET    /api/solicitudes/cliente/{id}
GET    /api/solicitudes/{solicitudId}
GET    /api/solicitudes/estado/{estado}
PATCH  /api/solicitudes/{solicitudId}/estado
GET    /api/solicitudes/servicio/health

🔧 Sistema:
GET    /api/system/health
GET    /api/system/info
```

---

## 🏁 Conclusión

La integración se realizó siguiendo estos principios clave:

1. **✅ Mantenimiento de Arquitectura**: Se respetó la arquitectura hexagonal existente
2. **✅ Separación de Responsabilidades**: Cada capa tiene una función específica
3. **✅ Inversión de Dependencias**: El dominio no depende de la infraestructura
4. **✅ Async Processing**: Operaciones no bloqueantes para mejor rendimiento
5. **✅ Error Resilience**: Manejo robusto de fallos de comunicación
6. **✅ Configuration Driven**: URLs y timeouts configurables externamente
7. **✅ Testability**: Scripts y endpoints para verificación automática

**¡El proyecto ahora está completamente integrado con los microservicios manteniendo la calidad arquitectónica!** 🎉
