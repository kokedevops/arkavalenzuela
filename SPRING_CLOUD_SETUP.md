# Spring Cloud Configuration - Arka Valenzuela

## Arquitectura de Microservicios con Spring Cloud

Este proyecto ha sido migrado para utilizar Spring Cloud con las siguientes características:

### Componentes Principales

1. **Eureka Server** (puerto 8761)
   - Registro y descubrimiento de servicios
   - Dashboard de monitoreo en http://localhost:8761

2. **API Gateway** (puerto 8080)
   - Enrutamiento de solicitudes
   - Load balancing automático
   - Punto de entrada único para todos los servicios

3. **Microservicios**:
   - **arca-cotizador**: Puerto 8081 (instancia 1), 8091 (instancia 2)
   - **arca-gestor-solicitudes**: Puerto 8082 (instancia 1), 8092 (instancia 2)
   - **hello-world-service**: Puerto 8083 (servicio de prueba)

## Configuración de Puertos

| Servicio | Puerto Principal | Puerto Secundario | Descripción |
|----------|------------------|-------------------|-------------|
| Eureka Server | 8761 | - | Registro de servicios |
| API Gateway | 8080 | - | Punto de entrada único |
| Arca Cotizador | 8081 | 8091 | Servicio de cotización |
| Arca Gestor | 8082 | 8092 | Gestor de solicitudes |
| Hello World | 8083 | 8084 | Servicio de prueba |

## Rutas del API Gateway

| Ruta | Servicio Destino | Descripción |
|------|------------------|-------------|
| `/api/hello/**` | hello-world-service | Servicio de prueba |
| `/api/cotizador/**` | arca-cotizador | Servicio de cotización |
| `/api/gestor/**` | arca-gestor-solicitudes | Gestor de solicitudes |
| `/eureka/**` | eureka-server | Dashboard de Eureka |

## Instrucciones de Inicio

### Opción 1: Inicio Manual Secuencial
```bash
# 1. Iniciar Eureka Server
./gradlew :eureka-server:bootRun

# 2. Esperar 30 segundos o mas hasta que inicie Eureka, luego iniciar API Gateway
./gradlew :api-gateway:bootRun

# 3. Iniciar servicios principales
./gradlew :hello-world-service:bootRun
./gradlew :arca-cotizador:bootRun
./gradlew :arca-gestor-solicitudes:bootRun

# 4. Iniciar instancias adicionales (para load balancing)
./gradlew :arca-cotizador:bootRun --args="--server.port=8091 --eureka.instance.instance-id=arca-cotizador:8091"
./gradlew :arca-gestor-solicitudes:bootRun --args="--server.port=8092 --eureka.instance.instance-id=arca-gestor-solicitudes:8092"
```

### Opción 2: Usando VS Code Tasks
1. Abrir VS Code en la raíz del proyecto
2. Ejecutar el task "Build All Modules" para compilar todo
3. Ejecutar cada servicio individualmente usando los tasks correspondientes

## Load Balancing

El sistema implementa load balancing automático a través del API Gateway:

### Verificar Load Balancing
```bash
# Ejecutar script de prueba
./scripts/test-load-balancing.bat

# O hacer peticiones manuales
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/cotizador/health
curl http://localhost:8080/api/gestor/health
```

Las peticiones se distribuirán automáticamente entre las instancias disponibles.

## URLs de Acceso

### A través del API Gateway (Recomendado)
- **Hello World**: http://localhost:8080/api/hello
- **Cotizador**: http://localhost:8080/api/cotizador
- **Gestor**: http://localhost:8080/api/gestor

### Acceso Directo a Servicios
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway Health**: http://localhost:8080/actuator/health
- **Cotizador Instancia 1**: http://localhost:8081
- **Cotizador Instancia 2**: http://localhost:8091
- **Gestor Instancia 1**: http://localhost:8082
- **Gestor Instancia 2**: http://localhost:8092
- **Hello World**: http://localhost:8083

## Endpoints de Monitoreo

Todos los servicios incluyen endpoints de Actuator:

- `/actuator/health` - Estado del servicio
- `/actuator/info` - Información del servicio
- `/actuator/metrics` - Métricas
- `/actuator/env` - Variables de entorno

## Configuración de Desarrollo

### Variables de Entorno
```yaml
# Eureka Server URL (por defecto)
EUREKA_URL=http://localhost:8761/eureka/

# Configuración de puertos personalizados
SERVER_PORT=8081
```

### Profiles
- **default**: Configuración estándar
- **instance2**: Para ejecutar segunda instancia en puerto diferente

## Troubleshooting

### Problemas Comunes

1. **Servicios no se registran en Eureka**
   - Verificar que Eureka Server esté ejecutándose
   - Revisar logs de connectivity
   - Confirmar configuración de `eureka.client.service-url.defaultZone`

2. **API Gateway no encuentra servicios**
   - Verificar que los servicios estén registrados en Eureka
   - Revisar nombres de servicios en configuración de rutas
   - Confirmar que load balancer esté habilitado

3. **Load Balancing no funciona**
   - Asegurar que hay múltiples instancias registradas
   - Verificar que cada instancia tiene un `instance-id` único
   - Revisar logs del API Gateway

### Logs Útiles
```bash
# Ver servicios registrados en Eureka
curl http://localhost:8761/eureka/apps

# Ver rutas del Gateway
curl http://localhost:8080/actuator/gateway/routes
```

## Próximos Pasos

1. **Configuración de Producción**
   - Configurar múltiples instancias de Eureka Server
   - Implementar autenticación y autorización
   - Configurar SSL/TLS

2. **Monitoreo Avanzado**
   - Integrar Zipkin para distributed tracing
   - Configurar Prometheus y Grafana
   - Implementar alertas

3. **Resilience Patterns**
   - Circuit Breaker con Resilience4j
   - Retry policies
   - Bulkhead patterns

## Dependencias Principales

```gradle
// Spring Cloud
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server'
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
implementation 'org.springframework.cloud:spring-cloud-starter-gateway'
implementation 'org.springframework.cloud:spring-cloud-starter-loadbalancer'

// Monitoring
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

## Estructura del Proyecto

```
arkajvalenzuela/
├── eureka-server/          # Servidor de registro
├── api-gateway/            # Gateway principal
├── hello-world-service/    # Servicio de prueba
├── arca-cotizador/        # Servicio de cotización
├── arca-gestor-solicitudes/ # Gestor de solicitudes
└── scripts/               # Scripts de inicio y prueba
```
