# Spring Cloud Microservices Implementation - ARKA Project

## 🎯 Objetivo del Proyecto

Este documento describe la implementación completa de los servicios Spring Cloud solicitados:
- **Spring Boot CLI** - Interfaz de línea de comandos interactiva
- **Spring Cloud Kubernetes** - Manifiestos y configuración para despliegue
- **Spring Cloud Contract** - Especificaciones de contratos de prueba
- **Spring Cloud Stream** - Mensajería asíncrona con RabbitMQ
- **Spring Cloud AWS** - Integración con servicios AWS (S3, SQS)
- **Circuit Breaker** - Patrón de tolerancia a fallos para cálculo de tiempo de envío

## 📁 Estructura del Proyecto

```
arkavalenzuela-1/
├── api-gateway/                    # Gateway de entrada
├── arca-cotizador/                # Servicio de cotizaciones
├── arca-gestor-solicitudes/       # Servicio principal con implementaciones
│   ├── src/main/java/com/arka/gestorsolicitudes/
│   │   ├── application/
│   │   │   ├── usecase/           # Casos de uso y servicios
│   │   │   └── cli/               # Spring Boot CLI
│   │   ├── domain/
│   │   │   └── model/             # Modelos de dominio
│   │   ├── infrastructure/
│   │   │   ├── adapter/           # Adaptadores externos
│   │   │   ├── config/            # Configuraciones
│   │   │   ├── controller/        # Controllers REST
│   │   │   ├── messaging/         # Spring Cloud Stream
│   │   │   └── aws/               # Integración AWS
│   │   └── resources/
│   │       └── application.yml    # Configuración Spring Cloud
│   ├── k8s/                       # Manifiestos Kubernetes
│   └── contracts/                 # Contratos de prueba
├── eureka-server/                 # Servidor de descubrimiento
└── hello-world-service/           # Servicio de ejemplo
```

## 🚀 Implementaciones Realizadas

### 1. Circuit Breaker para Cálculo de Tiempo de Envío

#### 📝 Archivos Creados/Modificados:
- `domain/model/CalculoEnvio.java` - Modelo de dominio
- `application/usecase/CalculoEnvioService.java` - Servicio principal
- `application/usecase/ProveedorEnvioExternoService.java` - Servicio externo protegido

#### 🔧 Características Implementadas:
```yaml
# Configuración Circuit Breaker en application.yml
resilience4j:
  circuitbreaker:
    instances:
      proveedorExterno:
        register-health-indicator: true
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        automatic-transition-from-open-to-half-open-enabled: true
```

#### 🎯 Estados de Cálculo:
- `PENDIENTE` - Cálculo en proceso
- `COMPLETADO` - Cálculo exitoso
- `FALLBACK` - Activado por Circuit Breaker
- `ERROR` - Error en el cálculo
- `TIMEOUT` - Timeout en la operación

### 2. Spring Boot CLI - Interfaz Interactiva

#### 📝 Archivo Principal:
- `application/cli/CircuitBreakerCLI.java`

#### 🖥️ Funcionalidades del CLI:
```
=== ARKA Circuit Breaker CLI ===
1. Realizar cálculo de envío
2. Ejecutar prueba de carga
3. Ver estado del Circuit Breaker
4. Ver métricas
5. Forzar cambio de estado
6. Salir
```

#### ⚡ Comandos Disponibles:
- **Cálculo individual**: Simula cálculo de envío con diferentes orígenes/destinos
- **Prueba de carga**: Ejecuta múltiples cálculos para probar Circuit Breaker
- **Monitoreo**: Visualiza estado y métricas en tiempo real
- **Control manual**: Permite forzar estados del Circuit Breaker

### 3. Spring Cloud Kubernetes

#### 📝 Manifiestos Creados:
```
k8s/
├── deployment.yaml        # Despliegue de la aplicación
├── service.yaml          # Servicio Kubernetes
├── configmap.yaml        # ConfigMap para configuración
└── secret.yaml           # Secrets para credenciales
```

#### 🐳 Configuración Docker:
```dockerfile
# Dockerfile implícito generado por Spring Boot
FROM openjdk:21-jre-slim
COPY build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### ☸️ Características Kubernetes:
- **Health Checks**: Liveness y Readiness probes configurados
- **Resource Limits**: CPU y memoria optimizados
- **ConfigMap**: Configuración externalizada
- **Secrets**: Credenciales AWS y bases de datos

### 4. Spring Cloud Contract

#### 📝 Contratos de Prueba:
- `contracts/calculo_envio_contract.groovy`
- `contracts/circuit_breaker_contract.groovy`

#### 🧪 Especificaciones Implementadas:
```groovy
// Ejemplo de contrato para cálculo de envío
Contract.make {
    description "Debería calcular tiempo de envío correctamente"
    request {
        method POST()
        url "/api/calculos/envio"
        body(
            origen: "Bogotá",
            destino: "Medellín",
            peso: 2.5
        )
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status OK()
        body(
            id: anyPositiveInt(),
            tiempoEstimado: 24,
            estado: "COMPLETADO"
        )
        headers {
            contentType(applicationJson())
        }
    }
}
```

### 5. Spring Cloud Stream

#### 📝 Implementación de Mensajería:
- `infrastructure/messaging/EnvioEventPublisher.java`
- `infrastructure/messaging/EnvioEventListener.java`

#### 🔄 Eventos Configurados:
```yaml
spring:
  cloud:
    stream:
      bindings:
        calculo-events-out-0:
          destination: calculo.events
        circuit-breaker-events-out-0:
          destination: circuit.breaker.events
        metrics-events-out-0:
          destination: metrics.events
```

#### 📨 Tipos de Eventos:
- **Eventos de Cálculo**: Publicados en cada operación
- **Eventos Circuit Breaker**: Estado y transiciones
- **Eventos de Métricas**: Estadísticas de rendimiento

### 6. Spring Cloud AWS Integration

#### 📝 Servicios AWS Implementados:
- `infrastructure/aws/AWSIntegrationService.java`
- `infrastructure/aws/S3Service.java`
- `infrastructure/aws/SQSService.java`

#### ☁️ Características AWS:
```yaml
spring:
  cloud:
    aws:
      region:
        static: us-east-1
      s3:
        bucket: arka-calculos-backup
      sqs:
        queue: arka-notifications
```

#### 🔧 Funcionalidades:
- **S3**: Backup de cálculos y logs
- **SQS**: Cola de notificaciones
- **Integration opcional**: Deshabilitada para entorno local

## 🛠️ Configuración y Despliegue

### 1. Prerrequisitos

```bash
# Java 21
java -version

# Gradle 8.x
./gradlew --version

# Docker (opcional para Kubernetes)
docker --version

# kubectl (para Kubernetes)
kubectl version
```

### 2. Construcción del Proyecto

```bash
# Limpiar y construir todo el proyecto
./gradlew clean build -x test

# Construir solo el servicio gestor
./gradlew :arca-gestor-solicitudes:build -x test
```

### 3. Ejecución Local

```bash
# Ejecutar con CLI habilitado
./gradlew :arca-gestor-solicitudes:bootRun --args="--arka.cli.enabled=true --logging.level.com.arka=INFO"

# O ejecutar JAR directamente
cd arca-gestor-solicitudes
java -jar build/libs/arca-gestor-solicitudes-0.0.1-SNAPSHOT.jar --arka.cli.enabled=true
```

### 4. Despliegue en Kubernetes

```bash
# Aplicar manifiestos
kubectl apply -f arca-gestor-solicitudes/k8s/

# Verificar despliegue
kubectl get pods -l app=arca-gestor-solicitudes
kubectl logs -f deployment/arca-gestor-solicitudes
```

## 📊 Verificación y Pruebas

### 1. Health Checks

```bash
# Verificar salud de la aplicación
curl http://localhost:8082/actuator/health

# Ver métricas de Circuit Breaker
curl http://localhost:8082/actuator/circuitbreakers
```

### 2. Endpoints Disponibles

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/calculos/envio` | POST | Crear cálculo de envío |
| `/api/calculos/{id}` | GET | Obtener cálculo por ID |
| `/api/calculos` | GET | Listar todos los cálculos |
| `/actuator/health` | GET | Estado de salud |
| `/actuator/circuitbreakers` | GET | Estado Circuit Breakers |
| `/actuator/metrics` | GET | Métricas de la aplicación |

### 3. Pruebas con CLI

1. **Iniciar la aplicación** con CLI habilitado
2. **Acceder al menú interactivo** en la consola
3. **Ejecutar pruebas de carga** para activar Circuit Breaker
4. **Monitorear estados** y métricas en tiempo real

## 🔧 Configuración Avanzada

### Variables de Entorno

```bash
# AWS (opcional)
export AWS_REGION=us-east-1
export AWS_ACCESS_KEY_ID=your-key
export AWS_SECRET_ACCESS_KEY=your-secret

# Base de datos
export DB_URL=jdbc:h2:mem:testdb
export DB_USERNAME=sa
export DB_PASSWORD=password

# Circuit Breaker
export CIRCUIT_BREAKER_FAILURE_RATE=50
export CIRCUIT_BREAKER_WAIT_DURATION=30s
```

### Perfiles de Configuración

```yaml
# application-prod.yml
spring:
  profiles:
    active: prod
  cloud:
    aws:
      credentials:
        access-key: ${AWS_ACCESS_KEY_ID}
        secret-key: ${AWS_SECRET_ACCESS_KEY}
```

## 🚨 Troubleshooting

### Problemas Comunes

1. **Error de conexión a Eureka**:
   ```
   Solution: Iniciar eureka-server primero o deshabilitar en application.yml
   ```

2. **RabbitMQ no disponible**:
   ```
   Solution: Instalar RabbitMQ local o usar configuración embedded
   ```

3. **AWS credentials no configuradas**:
   ```
   Solution: La integración AWS está deshabilitada por defecto para desarrollo local
   ```

## 📈 Métricas y Monitoreo

### Actuator Endpoints Habilitados

- `/actuator/health` - Estado de salud
- `/actuator/info` - Información de la aplicación
- `/actuator/metrics` - Métricas de rendimiento
- `/actuator/circuitbreakers` - Estado de Circuit Breakers
- `/actuator/prometheus` - Métricas para Prometheus

### Dashboard de Monitoreo

El CLI incluye un dashboard en tiempo real que muestra:
- Estado actual del Circuit Breaker
- Número de llamadas exitosas/fallidas
- Tiempo de respuesta promedio
- Eventos de transición de estado

## 🎉 Conclusión

Se han implementado exitosamente todos los componentes Spring Cloud solicitados:

✅ **Circuit Breaker** - Implementado con Resilience4j para cálculo de tiempo de envío
✅ **Spring Boot CLI** - Interfaz interactiva completa para pruebas y monitoreo
✅ **Spring Cloud Kubernetes** - Manifiestos completos para despliegue en K8s
✅ **Spring Cloud Contract** - Especificaciones de contratos para testing
✅ **Spring Cloud Stream** - Sistema de mensajería asíncrona con RabbitMQ
✅ **Spring Cloud AWS** - Integración con S3 y SQS (opcional para desarrollo)

El proyecto está listo para:
- **Desarrollo local** con todas las funcionalidades
- **Despliegue en Kubernetes** con alta disponibilidad
- **Integración con ecosistema Spring Cloud** completo
- **Monitoreo y observabilidad** con Actuator y métricas

## 📞 Soporte

Para más información sobre las implementaciones específicas, consultar:
- Código fuente en `arca-gestor-solicitudes/src/`
- Configuraciones en `arca-gestor-solicitudes/src/main/resources/`
- Manifiestos K8s en `arca-gestor-solicitudes/k8s/`
- Contratos en `arca-gestor-solicitudes/contracts/`

---
*Documentación generada para el proyecto ARKA - Sistema de Microservicios con Spring Cloud*
