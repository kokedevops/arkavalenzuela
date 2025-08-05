# 🔄 README - Programación Reactiva en Arka Valenzuela

## 📋 Índice
- [¿Qué es la Programación Reactiva?](#qué-es-la-programación-reactiva)
- [Componentes Reactivos](#componentes-reactivos)
- [Arquitectura Reactiva del Proyecto](#arquitectura-reactiva-del-proyecto)
- [Microservicios Reactivos](#microservicios-reactivos)
- [Flujo de Datos Reactivo](#flujo-de-datos-reactivo)
- [Ejemplos de Código](#ejemplos-de-código)
- [Testing y Verificación](#testing-y-verificación)
- [Ventajas y Beneficios](#ventajas-y-beneficios)

---

## 🔄 ¿Qué es la Programación Reactiva?

La **Programación Reactiva** es un paradigma de programación orientado al **flujo de datos** y la **propagación de cambios**. Se basa en el patrón **Observer** y permite manejar **streams de datos asíncronos** de manera eficiente.

### 🎯 **Conceptos Clave**

| Concepto | Descripción | Ejemplo |
|----------|-------------|---------|
| **Non-blocking** | Operaciones que no bloquean el hilo | I/O, DB queries |
| **Asíncrono** | Ejecución independiente del hilo principal | Llamadas HTTP |
| **Streams** | Flujo continuo de datos | Eventos, mensajes |
| **Backpressure** | Control de flujo cuando el consumidor es lento | Rate limiting |

### 🆚 **Reactiva vs Imperativa**

```java
// ❌ Programación Imperativa (Blocking)
@GetMapping("/products")
public List<Product> getProducts() {
    List<Product> products = productRepository.findAll(); // BLOQUEA
    return products;
}

// ✅ Programación Reactiva (Non-blocking)
@GetMapping("/products")
public Flux<Product> getProducts() {
    return productRepository.findAll(); // NO BLOQUEA
}
```

---

## 🧩 Componentes Reactivos

### 1. 📡 **Reactive Types**

#### **Mono<T>** - 0 o 1 elemento
```java
// Representa 0 o 1 elemento asíncrono
Mono<String> mono = Mono.just("Hello World");
Mono<User> user = userRepository.findById(1L);
Mono<Void> empty = Mono.empty();
```

#### **Flux<T>** - 0 a N elementos
```java
// Representa una secuencia de 0 a N elementos
Flux<String> flux = Flux.just("A", "B", "C");
Flux<Product> products = productRepository.findAll();
Flux<String> infinite = Flux.interval(Duration.ofSeconds(1))
                           .map(i -> "Tick " + i);
```

### 2. 🔄 **Operators** (Operadores)

#### **Transformación**
```java
// map - Transforma cada elemento
Flux<String> names = users.map(user -> user.getName());

// filter - Filtra elementos
Flux<User> adults = users.filter(user -> user.getAge() >= 18);

// flatMap - Aplana operaciones asíncronas
Flux<Order> orders = users.flatMap(user -> orderService.findByUser(user));
```

#### **Combinación**
```java
// merge - Combina múltiples streams
Flux<String> combined = Flux.merge(stream1, stream2, stream3);

// zip - Combina elementos por posición
Flux<Tuple2<String, Integer>> zipped = Flux.zip(names, ages);
```

#### **Control de Flujo**
```java
// take - Toma los primeros N elementos
Flux<String> first5 = stream.take(5);

// delay - Introduce un delay
Mono<String> delayed = Mono.just("Hello").delayElement(Duration.ofSeconds(2));

// timeout - Define un timeout
Mono<String> withTimeout = service.getData().timeout(Duration.ofSeconds(5));
```

### 3. 🎯 **Subscribers** (Suscriptores)

```java
// Subscribe simple
mono.subscribe(value -> System.out.println("Received: " + value));

// Subscribe con manejo de errores
flux.subscribe(
    value -> System.out.println("Received: " + value),
    error -> System.err.println("Error: " + error),
    () -> System.out.println("Completed!")
);

// Subscribe con backpressure
flux.subscribe(new BaseSubscriber<String>() {
    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        request(1); // Solicita solo 1 elemento
    }
    
    @Override
    protected void hookOnNext(String value) {
        System.out.println("Processing: " + value);
        request(1); // Solicita el siguiente
    }
});
```

---

## 🏗️ Arquitectura Reactiva del Proyecto

### 📊 **Stack Tecnológico Reactivo**

```
┌─────────────────────────────────────────────────────────┐
│                   🌐 API Gateway                        │
│              (Spring Cloud Gateway)                     │
│                   Non-blocking                          │
└─────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
┌───────────────────▼──────────┐ ┌──────▼──────────────────┐
│    🔄 Arca Cotizador         │ │  🔄 Arca Gestor         │
│     (WebFlux + R2DBC)        │ │   (WebFlux + R2DBC)     │
│                              │ │                         │
│  ├─ Reactive Controllers     │ │  ├─ Reactive Controllers│
│  ├─ Reactive Services        │ │  ├─ Reactive Services   │
│  ├─ R2DBC Repositories       │ │  ├─ R2DBC Repositories  │
│  └─ Non-blocking I/O         │ │  └─ WebClient (Reactive)│
└──────────────────────────────┘ └─────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
               ┌────▼────┐         ┌────▼────┐
               │ 🗃️ H2DB │         │ 🗃️ H2DB │
               │(R2DBC)  │         │(R2DBC)  │
               └─────────┘         └─────────┘
```

### 🔧 **Configuración por Capas**

#### **1. Presentation Layer (Controllers)**
```java
@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {
    
    @GetMapping
    public Flux<CotizacionDto> getAllCotizaciones() {
        return cotizacionService.findAll()
                .map(cotizacionMapper::toDto);
    }
    
    @PostMapping
    public Mono<CotizacionDto> createCotizacion(@RequestBody CotizacionDto dto) {
        return Mono.just(dto)
                .map(cotizacionMapper::toDomain)
                .flatMap(cotizacionService::save)
                .map(cotizacionMapper::toDto);
    }
}
```

#### **2. Service Layer (Business Logic)**
```java
@Service
public class CotizacionServiceImpl implements CotizacionService {
    
    private final CotizacionRepository repository;
    private final WebClient webClient;
    
    @Override
    public Flux<Cotizacion> findAll() {
        return repository.findAll()
                .doOnNext(cotizacion -> log.info("Found: {}", cotizacion.getId()));
    }
    
    @Override
    public Mono<Cotizacion> save(Cotizacion cotizacion) {
        return repository.save(cotizacion)
                .doOnSuccess(saved -> log.info("Saved: {}", saved.getId()));
    }
    
    @Override
    public Mono<ExternalData> callExternalService(String id) {
        return webClient.get()
                .uri("/external-api/{id}", id)
                .retrieve()
                .bodyToMono(ExternalData.class)
                .timeout(Duration.ofSeconds(5))
                .retry(3);
    }
}
```

#### **3. Repository Layer (Data Access)**
```java
public interface CotizacionRepository extends ReactiveCrudRepository<CotizacionEntity, String> {
    
    @Query("SELECT * FROM cotizaciones WHERE status = :status")
    Flux<CotizacionEntity> findByStatus(String status);
    
    @Query("SELECT * FROM cotizaciones WHERE created_date >= :date")
    Flux<CotizacionEntity> findRecentCotizaciones(LocalDateTime date);
    
    Flux<CotizacionEntity> findByClienteIdOrderByCreatedDateDesc(String clienteId);
}
```

---

## 🚀 Microservicios Reactivos

### 🔄 **1. Arca Cotizador** (Puerto 8081/8091)

#### **Dependencias**
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    runtimeOnly 'io.r2dbc:r2dbc-h2'
    testImplementation 'io.projectreactor:reactor-test'
}
```

#### **Configuración**
```yaml
spring:
  application:
    name: arca-cotizador
  webflux:
    base-path: /
  r2dbc:
    url: r2dbc:h2:mem:///cotizador_db
    username: sa
    password: ""
```

#### **Endpoints Reactivos**
```java
@RestController
public class CotizadorReactiveController {
    
    // Retorna información básica
    @GetMapping("/")
    public Mono<String> home() {
        return Mono.just("Cotizador Service - Reactive!")
                .delayElement(Duration.ofMillis(100));
    }
    
    // Stream de eventos en tiempo real
    @GetMapping("/stream")
    public Flux<String> stream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> "Event #" + i + " from Cotizador")
                .take(10);
    }
    
    // Test de operaciones reactivas
    @GetMapping("/reactive-test")
    public Flux<CotizacionEvent> reactiveTest() {
        return Flux.range(1, 5)
                .delayElements(Duration.ofMillis(500))
                .map(i -> new CotizacionEvent("COTIZ-" + i, "PENDING"));
    }
}
```

### 🔄 **2. Arca Gestor Solicitudes** (Puerto 8082/8092)

#### **Características Especiales**
- **WebClient Reactivo** para comunicación entre servicios
- **Flujos de procesamiento** asíncrono
- **Manejo de respuestas** de múltiples proveedores

```java
@Service
public class GestorSolicitudesServiceImpl implements GestorSolicitudesService {
    
    private final WebClient webClient;
    
    @Override
    public Mono<SolicitudProveedor> crearSolicitud(SolicitudProveedor solicitud) {
        return Mono.just(solicitud)
                .doOnNext(s -> s.setId(UUID.randomUUID().toString()))
                .doOnNext(s -> s.setEstado("CREADA"))
                .doOnNext(s -> s.setFechaCreacion(LocalDateTime.now()))
                .flatMap(this::validarSolicitud)
                .flatMap(this::guardarSolicitud);
    }
    
    @Override
    public Flux<RespuestaProveedor> obtenerRespuestasProveedor(String solicitudId) {
        return webClient.get()
                .uri("/proveedores/{solicitudId}/respuestas", solicitudId)
                .retrieve()
                .bodyToFlux(RespuestaProveedor.class)
                .onErrorResume(error -> {
                    log.error("Error obtaining responses: {}", error.getMessage());
                    return Flux.empty();
                });
    }
    
    @Override
    public Mono<SolicitudProveedor> enviarSolicitudAProveedor(String solicitudId, String proveedorId) {
        return obtenerSolicitud(solicitudId)
                .flatMap(solicitud -> enviarAProveedor(solicitud, proveedorId))
                .flatMap(this::actualizarEstadoSolicitud);
    }
}
```

---

## 🌊 Flujo de Datos Reactivo

### 📊 **Flujo Complete Request-Response**

```
1. 🌐 HTTP Request
   ↓
2. 🎮 Reactive Controller
   ├─ Recibe Mono<RequestDto>
   ├─ Valida datos (Mono operators)
   └─ Transforma a Domain (map)
   ↓
3. 🔄 Reactive Service
   ├─ Aplica lógica de negocio
   ├─ Usa operadores (filter, flatMap, etc.)
   └─ Llama a repository
   ↓
4. 📊 R2DBC Repository  
   ├─ Operación non-blocking
   ├─ Retorna Flux/Mono
   └─ Sin bloqueo de threads
   ↓
5. 🗃️ Database (H2 with R2DBC)
   ├─ Query asíncrona
   ├─ Resultado reactivo
   └─ Propagación de datos
   ↓
6. 🔄 Response Flow (reverse)
   ├─ Domain → DTO (map)
   ├─ Aplicar transformaciones
   └─ Serialización JSON
   ↓
7. 🌐 HTTP Response (Flux/Mono)
```

### 🔄 **Ejemplo de Flujo Completo**

```java
@PostMapping("/cotizaciones")
public Mono<ResponseEntity<CotizacionResponseDto>> crearCotizacion(
        @RequestBody CotizacionRequestDto request) {
    
    return Mono.just(request)
            // 1. Validación reactiva
            .filter(this::isValidRequest)
            .switchIfEmpty(Mono.error(new ValidationException("Invalid request")))
            
            // 2. Transformación DTO → Domain
            .map(cotizacionMapper::requestToDomain)
            
            // 3. Enriquecimiento de datos
            .flatMap(this::enrichWithClientData)
            .flatMap(this::enrichWithProductData)
            
            // 4. Cálculos de negocio
            .flatMap(this::calculatePricing)
            .flatMap(this::applyDiscounts)
            
            // 5. Persistencia
            .flatMap(cotizacionRepository::save)
            
            // 6. Notificaciones asíncronas
            .doOnNext(this::sendNotificationAsync)
            
            // 7. Transformación Domain → DTO
            .map(cotizacionMapper::domainToResponse)
            
            // 8. Wrapping en ResponseEntity
            .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
            
            // 9. Manejo de errores
            .onErrorResume(ValidationException.class, 
                error -> Mono.just(ResponseEntity.badRequest().build()))
            .onErrorResume(Exception.class,
                error -> Mono.just(ResponseEntity.internalServerError().build()));
}
```

---

## 💻 Ejemplos de Código

### 🔄 **1. Operadores Básicos**

```java
@Service
public class ReactiveExamplesService {
    
    // Transformación simple
    public Flux<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .map(productMapper::toDto)
                .doOnNext(dto -> log.info("Transformed: {}", dto.getName()));
    }
    
    // Filtrado y transformación
    public Flux<ProductDto> getExpensiveProducts(BigDecimal minPrice) {
        return productRepository.findAll()
                .filter(product -> product.getPrice().compareTo(minPrice) >= 0)
                .map(productMapper::toDto)
                .sort((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
    }
    
    // Operaciones asíncronas anidadas
    public Mono<OrderSummaryDto> createOrderWithValidation(CreateOrderDto request) {
        return Mono.just(request)
                // Validar cliente
                .flatMap(req -> customerService.validateCustomer(req.getCustomerId())
                        .map(customer -> Tuples.of(req, customer)))
                
                // Validar productos
                .flatMap(tuple -> productService.validateProducts(tuple.getT1().getProductIds())
                        .collectList()
                        .map(products -> Tuples.of(tuple.getT1(), tuple.getT2(), products)))
                
                // Crear orden
                .flatMap(tuple -> {
                    CreateOrderDto dto = tuple.getT1();
                    Customer customer = tuple.getT2();
                    List<Product> products = tuple.getT3();
                    
                    Order order = Order.builder()
                            .customer(customer)
                            .products(products)
                            .total(calculateTotal(products))
                            .build();
                            
                    return orderRepository.save(order);
                })
                
                // Transformar a DTO
                .map(orderMapper::toSummaryDto);
    }
}
```

### 🌐 **2. Comunicación Entre Servicios**

```java
@Service
public class ReactiveIntegrationService {
    
    private final WebClient webClient;
    
    // Llamada a servicio externo con retry y timeout
    public Mono<ExternalApiResponse> callExternalService(String id) {
        return webClient
                .get()
                .uri("/api/external/{id}", id)
                .retrieve()
                .bodyToMono(ExternalApiResponse.class)
                .timeout(Duration.ofSeconds(10))
                .retry(3)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("External service error: {}", ex.getMessage());
                    return Mono.just(ExternalApiResponse.defaultResponse());
                });
    }
    
    // Paralelización de llamadas
    public Mono<AggregatedResponse> aggregateData(String entityId) {
        Mono<ServiceAResponse> serviceA = callServiceA(entityId);
        Mono<ServiceBResponse> serviceB = callServiceB(entityId);
        Mono<ServiceCResponse> serviceC = callServiceC(entityId);
        
        return Mono.zip(serviceA, serviceB, serviceC)
                .map(tuple -> AggregatedResponse.builder()
                        .dataA(tuple.getT1())
                        .dataB(tuple.getT2())
                        .dataC(tuple.getT3())
                        .build());
    }
    
    // Stream de eventos
    public Flux<NotificationEvent> getNotificationStream(String userId) {
        return webClient
                .get()
                .uri("/notifications/stream/{userId}", userId)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(NotificationEvent.class)
                .doOnNext(event -> log.info("Received notification: {}", event))
                .onErrorContinue((error, obj) -> 
                        log.warn("Error in notification stream: {}", error.getMessage()));
    }
}
```

### 🔄 **3. Manejo de Backpressure**

```java
@RestController
public class BackpressureController {
    
    // Stream con control de flujo
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<EventData>> getEventStream() {
        return eventService.getEventStream()
                // Control de backpressure
                .onBackpressureBuffer(1000) // Buffer hasta 1000 elementos
                .delayElements(Duration.ofMillis(100)) // Delay entre elementos
                .map(event -> ServerSentEvent.<EventData>builder()
                        .id(event.getId())
                        .event("data-update")
                        .data(event)
                        .build())
                .doOnCancel(() -> log.info("Client disconnected from event stream"));
    }
    
    // Paginación reactiva
    @GetMapping("/products/paged")
    public Flux<ProductDto> getProductsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return productRepository.findAllByOrderByCreatedDateDesc()
                .skip(page * size)
                .take(size)
                .map(productMapper::toDto)
                .doOnComplete(() -> log.info("Page {} completed", page));
    }
}
```

---

## 🧪 Testing y Verificación

### 🔍 **1. Testing con StepVerifier**

```java
@ExtendWith(SpringExtension.class)
@DataR2dbcTest
class ReactiveRepositoryTest {
    
    @Autowired
    private CotizacionRepository repository;
    
    @Test
    void shouldSaveAndFindCotizacion() {
        // Given
        Cotizacion cotizacion = Cotizacion.builder()
                .clienteId("CLIENT-001")
                .estado("PENDING")
                .build();
        
        // When & Then
        StepVerifier.create(repository.save(cotizacion))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getClienteId()).isEqualTo("CLIENT-001");
                })
                .verifyComplete();
    }
    
    @Test
    void shouldStreamCotizaciones() {
        // Given
        Flux<Cotizacion> cotizaciones = Flux.range(1, 5)
                .map(i -> Cotizacion.builder()
                        .clienteId("CLIENT-" + i)
                        .estado("PENDING")
                        .build())
                .flatMap(repository::save);
        
        // When & Then
        StepVerifier.create(cotizaciones)
                .expectNextCount(5)
                .verifyComplete();
        
        StepVerifier.create(repository.findAll())
                .expectNextCount(5)
                .verifyComplete();
    }
}
```

### 🌐 **2. Testing de Controllers**

```java
@WebFluxTest(CotizadorController.class)
class CotizadorControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private CotizacionService cotizacionService;
    
    @Test
    void shouldGetAllCotizaciones() {
        // Given
        Flux<Cotizacion> cotizaciones = Flux.just(
                Cotizacion.builder().id("1").clienteId("CLIENT-1").build(),
                Cotizacion.builder().id("2").clienteId("CLIENT-2").build()
        );
        
        when(cotizacionService.findAll()).thenReturn(cotizaciones);
        
        // When & Then
        webTestClient.get()
                .uri("/api/cotizaciones")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CotizacionDto.class)
                .hasSize(2);
    }
    
    @Test
    void shouldStreamEvents() {
        webTestClient.get()
                .uri("/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(String.class)
                .getResponseBody()
                .take(3)
                .as(StepVerifier::create)
                .expectNextMatches(event -> event.contains("Event #"))
                .expectNextMatches(event -> event.contains("Event #"))
                .expectNextMatches(event -> event.contains("Event #"))
                .thenCancel()
                .verify();
    }
}
```

### 🚀 **3. Testing de Performance**

```java
@Test
void performanceTest() {
    int numberOfRequests = 1000;
    
    // Test concurrencia
    Flux<String> responses = Flux.range(1, numberOfRequests)
            .flatMap(i -> webTestClient.get()
                    .uri("/api/cotizaciones/{id}", i)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(String.class)
                    .getResponseBody()
                    .next())
            .doOnNext(response -> log.info("Response: {}", response));
    
    StepVerifier.create(responses)
            .expectNextCount(numberOfRequests)
            .verifyComplete();
}
```

---

## 🌐 Testing y Verificación Manual

### 🧪 **Endpoints para Testing**

#### **1. Cotizador Service**
```bash
# Información básica
curl http://localhost:8080/api/cotizador/

# Health check reactivo
curl http://localhost:8080/api/cotizador/health

# Stream de eventos (Server-Sent Events)
curl http://localhost:8080/api/cotizador/stream

# Test de operaciones reactivas
curl http://localhost:8080/api/cotizador/reactive-test
```

#### **2. Gestor Solicitudes Service**
```bash
# Información básica
curl http://localhost:8080/api/gestor/

# Health check reactivo
curl http://localhost:8080/api/gestor/health

# Stream de eventos
curl http://localhost:8080/api/gestor/stream

# Test de operaciones reactivas
curl http://localhost:8080/api/gestor/reactive-test
```

### 📊 **Scripts de Testing**

#### **PowerShell - Test de Concurrencia**
```powershell
# Test de múltiples requests concurrentes
$jobs = @()
for ($i = 1; $i -le 10; $i++) {
    $jobs += Start-Job -ScriptBlock {
        param($url)
        Invoke-RestMethod -Uri $url -Method GET
    } -ArgumentList "http://localhost:8080/api/cotizador/reactive-test"
}

# Esperar y mostrar resultados
$jobs | Wait-Job | Receive-Job
$jobs | Remove-Job
```

#### **Bash - Test de Streaming**
```bash
#!/bin/bash
# Test de streaming reactivo

echo "Testing Cotizador Stream:"
curl -N http://localhost:8080/api/cotizador/stream &
PID1=$!

echo "Testing Gestor Stream:"
curl -N http://localhost:8080/api/gestor/stream &
PID2=$!

# Esperar 30 segundos y terminar
sleep 30
kill $PID1 $PID2
```

### 📈 **Métricas de Performance**

```bash
# Test de throughput con Apache Bench
ab -n 1000 -c 10 http://localhost:8080/api/cotizador/health

# Test de streaming con curl y tiempo
time curl -N http://localhost:8080/api/cotizador/stream

# Monitoreo de memoria y CPU
top -p $(pgrep -f "arca-cotizador")
```

---

## 🎯 Ventajas y Beneficios

### ⚡ **Performance**

| Aspecto | Tradicional (MVC) | Reactivo (WebFlux) |
|---------|-------------------|-------------------|
| **Threading Model** | Thread per Request | Event Loop |
| **Memory Usage** | Alto (1 thread = ~2MB) | Bajo (Event Loop) |
| **Throughput** | Limitado por threads | Alto |
| **Latency** | Bloqueante | Non-blocking |
| **Scalability** | Vertical | Horizontal |

### 🔄 **Concurrencia**

```java
// ❌ Blocking (MVC tradicional)
@GetMapping("/slow")
public ResponseEntity<String> slowEndpoint() {
    Thread.sleep(5000); // BLOQUEA el thread por 5 segundos
    return ResponseEntity.ok("Done");
}
// Con 200 requests concurrentes = 200 threads bloqueados

// ✅ Non-blocking (WebFlux)
@GetMapping("/slow")
public Mono<String> slowEndpoint() {
    return Mono.delay(Duration.ofSeconds(5))
            .map(delay -> "Done");
}
// Con 200 requests concurrentes = 1 thread, múltiples operaciones
```

### 📊 **Uso de Recursos**

```
Escenario: 1000 requests concurrentes con 5s de processing cada una

┌─────────────────┬──────────────┬─────────────────┐
│                 │   MVC        │   WebFlux       │
├─────────────────┼──────────────┼─────────────────┤
│ Threads         │ 1000         │ ~10             │
│ Memory          │ ~2GB         │ ~100MB          │
│ CPU Usage       │ High         │ Low             │
│ Context Switch  │ High         │ Minimal         │
│ Total Time      │ 5s           │ 5s              │
│ Memory/Request  │ 2MB          │ 0.1MB           │
└─────────────────┴──────────────┴─────────────────┘
```

### 🌊 **Streaming y Real-time**

```java
// Stream de datos en tiempo real
@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<PriceUpdate> getPriceUpdates() {
    return Flux.interval(Duration.ofSeconds(1))
            .flatMap(tick -> priceService.getCurrentPrices())
            .share(); // Comparte el stream entre múltiples suscriptores
}

// Client-side JavaScript
const eventSource = new EventSource('/api/price-updates');
eventSource.onmessage = function(event) {
    const priceUpdate = JSON.parse(event.data);
    updateUI(priceUpdate);
};
```

### 🔧 **Manejo de Errores**

```java
public Mono<Result> resilientOperation() {
    return externalService.callApi()
            // Retry con backoff exponencial
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
            
            // Fallback en caso de error
            .onErrorResume(exception -> {
                log.warn("Service unavailable, using fallback");
                return fallbackService.getDefaultResult();
            })
            
            // Timeout
            .timeout(Duration.ofSeconds(10))
            
            // Circuit breaker pattern
            .transform(CircuitBreakerOperator.of(circuitBreaker));
}
```

### 📈 **Escalabilidad**

```yaml
# Configuración para alta concurrencia
spring:
  webflux:
    # Buffer para streams
    max-in-memory-size: 256KB
  
  r2dbc:
    pool:
      # Pool de conexiones reactivas
      initial-size: 10
      max-size: 50
      max-idle-time: 30m
      
server:
  # Configuración del servidor Netty
  netty:
    # Número de worker threads
    worker-threads: 16
    # Tamaño del buffer
    connection-timeout: 20s
```

---

## 🎓 Conclusión

### ✅ **Beneficios Implementados**

1. **🚀 Alta Performance**: Non-blocking I/O permite manejar más requests con menos recursos
2. **⚡ Escalabilidad**: Modelo de threading eficiente para alta concurrencia
3. **🌊 Real-time**: Streaming de datos con Server-Sent Events
4. **🔄 Resiliente**: Retry, timeout, circuit breaker patterns
5. **📊 Eficiencia**: Menor uso de memoria y CPU
6. **🧪 Testeable**: Herramientas como StepVerifier para testing reactivo

### 🎯 **Casos de Uso Ideales**

- **APIs de alto tráfico** con operaciones I/O intensivas
- **Streaming de datos** en tiempo real
- **Integración con servicios externos** con alta latencia
- **Aplicaciones con picos de tráfico** variables
- **Sistemas de notificaciones** en tiempo real

### 📚 **Recursos Adicionales**

- [Project Reactor Documentation](https://projectreactor.io/docs)
- [Spring WebFlux Reference](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [R2DBC Documentation](https://r2dbc.io/)
- [Reactive Streams Specification](http://www.reactive-streams.org/)

---

**🚀 ¡El proyecto Arka Valenzuela está completamente optimizado con programación reactiva para máximo rendimiento y escalabilidad!**
