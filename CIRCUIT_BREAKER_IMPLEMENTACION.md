# 🔒 IMPLEMENTACIÓN DE CIRCUIT BREAKER PARA CÁLCULO DE ENVÍO

## 📋 DESCRIPCIÓN

Se ha implementado un Circuit Breaker para proteger el microservicio de cálculo de envío contra fallos en cascada. Esta implementación utiliza **Resilience4j** para manejar fallos de servicios externos de manera elegante y proporcionar valores de fallback.

## 🏗️ ARQUITECTURA

```
[Cliente] → [API Gateway] → [Arca Gestor Solicitudes]
                                    ↓
                          [Circuit Breaker Layer]
                                    ↓
                         [Proveedor Externo Service]
                         [Servicio Interno Backup]
                         [Valores por Defecto]
```

## 🛠️ COMPONENTES IMPLEMENTADOS

### 1. **Dependencias Agregadas**

```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j'
implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.2.0'
implementation 'io.github.resilience4j:resilience4j-reactor:2.2.0'
```

### 2. **Modelos de Dominio**

#### **CalculoEnvio.java**
- Modelo principal para el cálculo de envío
- Estados: `PENDIENTE`, `COMPLETADO`, `FALLBACK`, `ERROR`, `TIMEOUT`
- Métodos factory para diferentes escenarios

#### **EstadoCalculo.java**
- Enum para los diferentes estados del cálculo
- Proporciona descripción legible de cada estado

### 3. **Servicios**

#### **ProveedorEnvioExternoService.java**
- Servicio protegido con Circuit Breaker
- Implementa fallbacks para diferentes niveles de fallo
- Configuraciones:
  - `@CircuitBreaker(name = "proveedor-externo-service")`
  - `@Retry(name = "calculo-envio-service")`
  - `@TimeLimiter(name = "calculo-envio-service")`

#### **CalculoEnvioService.java**
- Orquesta las llamadas a diferentes proveedores
- Maneja la lógica de fallback entre servicios

### 4. **Controladores**

#### **CalculoEnvioController.java**
- Endpoints para calcular envíos
- Endpoint de pruebas para diferentes escenarios

#### **CircuitBreakerDemoController.java**
- Monitoreo y control de Circuit Breakers
- Endpoints para forzar estados y reiniciar métricas

## ⚙️ CONFIGURACIÓN

### **application.yml**

```yaml
resilience4j:
  circuitbreaker:
    instances:
      calculo-envio-service:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        waitDurationInOpenState: 10s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
      proveedor-externo-service:
        registerHealthIndicator: true
        slidingWindowSize: 8
        minimumNumberOfCalls: 3
        permittedNumberOfCallsInHalfOpenState: 2
        waitDurationInOpenState: 15s
        failureRateThreshold: 60
  retry:
    instances:
      calculo-envio-service:
        maxAttempts: 3
        waitDuration: 1s
        enableExponentialBackoff: true
  timeout:
    instances:
      calculo-envio-service:
        timeoutDuration: 5s
```

## 🔄 FLUJO DE FALLBACK

1. **Proveedor Externo** → Si falla → **Servicio Interno**
2. **Servicio Interno** → Si falla → **Valores por Defecto**
3. **Circuit Breaker** → Evita llamadas innecesarias cuando está ABIERTO

## 📊 ESTADOS DEL CIRCUIT BREAKER

### **CLOSED (Cerrado)**
- Estado normal, permite todas las llamadas
- Monitorea las respuestas para detectar fallos

### **OPEN (Abierto)**
- Rechaza todas las llamadas inmediatamente
- Ejecuta método de fallback directamente
- Se activa cuando se supera el `failureRateThreshold`

### **HALF_OPEN (Semi-abierto)**
- Permite un número limitado de llamadas de prueba
- Si las pruebas tienen éxito → vuelve a CLOSED
- Si las pruebas fallan → vuelve a OPEN

## 🚀 ENDPOINTS DISPONIBLES

### **Cálculo de Envío**

```bash
#SALIENDO POR PUERTO 8082 DEL GESTOR DE SOLICITUDES
# Cálculo normal
POST /api/calculo-envio/calcular
{
  "origen": "Lima",
  "destino": "Arequipa",
  "peso": 2.5,
  "dimensiones": "50x30x20"
}

# Prueba rápida
GET /api/calculo-envio/prueba-rapida?origen=Lima&destino=Cusco&peso=2.0

# Estado del servicio
GET /api/calculo-envio/estado
```

### **Monitoreo Circuit Breaker**

```bash
# Estado de todos los Circuit Breakers
GET /api/circuit-breaker/estado

# Estado específico
GET /api/circuit-breaker/estado/{nombre}

# Forzar apertura (para pruebas)
POST /api/circuit-breaker/forzar-apertura/{nombre}

# Forzar cierre
POST /api/circuit-breaker/forzar-cierre/{nombre}

# Reiniciar métricas
POST /api/circuit-breaker/reiniciar-metricas/{nombre}
```

### **Pruebas de Escenarios**

```bash
# Probar diferentes escenarios
POST /api/calculo-envio/probar-circuit-breaker
{
  "escenario": "externo|interno|completo",
  "origen": "Lima",
  "destino": "Cusco",
  "peso": 1.5
}
```

## 📈 MONITOREO

### **Actuator Endpoints**

```bash
# Salud general
GET /actuator/health

# Salud de Circuit Breakers
GET /actuator/health/circuitBreakers

# Métricas
GET /actuator/metrics
```

### **Métricas Disponibles**

- Número total de llamadas
- Número de llamadas exitosas/fallidas
- Tasa de fallos actual
- Estado actual del Circuit Breaker
- Tiempo en cada estado

## 🧪 TESTING

### **Script de Pruebas**

```bash
# Ejecutar script de pruebas automatizadas
scripts/test-circuit-breaker.bat
```

### **Escenarios de Prueba**

1. **Llamadas Normales** - Verificar funcionamiento básico
2. **Fallos Simulados** - Activar Circuit Breaker
3. **Múltiples Fallos** - Probar transición a estado OPEN
4. **Recuperación** - Probar transición a HALF_OPEN y CLOSED
5. **Fallbacks** - Verificar valores por defecto

## 🔧 CONFIGURACIÓN DE DESARROLLO

### **Variables de Entorno**

```bash
# Para desarrollo local
SPRING_PROFILES_ACTIVE=dev
RESILIENCE4J_ENABLED=true
CIRCUIT_BREAKER_MONITORING=true
```

### **Logs de Debug**

```yaml
logging:
  level:
    io.github.resilience4j: DEBUG
    com.arka.gestorsolicitudes: DEBUG
```

## 🚨 MANEJO DE ERRORES

### **Tipos de Errores Manejados**

- `IOException` - Problemas de conectividad
- `TimeoutException` - Timeouts de servicios
- `WebClientException` - Errores de cliente HTTP
- Errores genéricos de servicios externos

### **Respuestas de Fallback**

```json
{
  "id": "generated-uuid",
  "estado": "FALLBACK",
  "costo": 50.0,
  "tiempoEstimadoDias": 7,
  "proveedorUtilizado": "SERVICIO_BACKUP",
  "mensajeError": "Servicio externo no disponible"
}
```

## 📋 VENTAJAS DE LA IMPLEMENTACIÓN

1. **Resiliencia** - Evita fallos en cascada
2. **Disponibilidad** - Servicio siempre disponible con fallbacks
3. **Monitoreo** - Visibilidad completa del estado del sistema
4. **Configurabilidad** - Parámetros ajustables por ambiente
5. **Testing** - Endpoints dedicados para pruebas
6. **Métricas** - Integración con Actuator y sistemas de monitoreo
