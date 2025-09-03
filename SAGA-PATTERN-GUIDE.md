# Patrón Saga con AWS Lambda, SQS y SNS

## Descripción del Proyecto

Este proyecto implementa un patrón Saga completo usando AWS Lambda, SNS y comunicación con servicios externos para simular un proceso de pedido, reserva de inventario y envío.

## Arquitectura del Saga

### Flujo del Proceso

```
[Inicio Saga] -> [Lambda 1: Inventario] -> [Lambda 2: Envío] -> [Lambda 3: Notificación] -> [Completado]
      |                    |                       |                      |
      v                    v                       v                      v
  [SNS Topic]         [Servicio S2]         [Servicio ChipMen]    [Informe Ventas]
```

### Componentes Implementados

#### 1. Modelos de Dominio
- **SagaPedido**: Modelo principal que representa un pedido en el proceso
- **EstadoPedido**: Enumeración de estados posibles del pedido
- **SagaEvent**: Evento que se propaga a través de SNS

#### 2. Servicios de Aplicación
- **SagaOrchestratorService**: Orquestador principal del proceso Saga

#### 3. Adaptadores de Infraestructura
- **SnsSagaEventPublisher**: Publicador de eventos en Amazon SNS
- **AwsLambdaInvoker**: Invocador de funciones Lambda
- **ExternalServiceAdapterImpl**: Adaptador para servicios externos (S2 y ChipMen)

#### 4. Controladores Web
- **SagaController**: API REST para iniciar y manejar eventos del Saga

## Funciones Lambda

### Lambda 1: Reserva de Inventario
- **Función**: `InventoryReservationLambda`
- **Propósito**: Conecta con el servicio S2 para reservar inventario
- **Evento de entrada**: Pedido JSON
- **Evento de salida**: `INVENTORY_RESERVED` o `INVENTORY_RESERVATION_FAILED`

### Lambda 2: Generación de Envío
- **Función**: `ShippingGenerationLambda`
- **Propósito**: Conecta con ChipMen para generar orden de envío
- **Evento de entrada**: `INVENTORY_RESERVED` (vía SNS)
- **Evento de salida**: `SHIPPING_GENERATED` o `SHIPPING_GENERATION_FAILED`

### Lambda 3: Notificación Personalizada
- **Función**: `NotificationLambda`
- **Propósito**: Envía notificación al cliente y genera informe de ventas
- **Evento de entrada**: `SHIPPING_GENERATED` (vía SNS)
- **Evento de salida**: `NOTIFICATION_SENT`

## Configuración de AWS

### Variables de Entorno para Lambdas

```bash
# Para todas las Lambdas
SNS_TOPIC_ARN=arn:aws:sns:us-east-1:123456789:arka-saga-topic

# Para Lambda de Inventario
S2_SERVICE_URL=https://tu-servicio-s2.com

# Para Lambda de Envío
CHIPMEN_SERVICE_URL=https://chipmen.com

# Para Lambda de Notificación
NOTIFICATION_SERVICE_URL=https://tu-servicio-notificaciones.com
```

### Configuración de SNS Topic

```bash
# Crear topic SNS
aws sns create-topic --name arka-saga-topic

# Suscribir Lambdas al topic
aws sns subscribe --topic-arn arn:aws:sns:us-east-1:123456789:arka-saga-topic \
  --protocol lambda --notification-endpoint arn:aws:lambda:us-east-1:123456789:function:arka-saga-shipping

aws sns subscribe --topic-arn arn:aws:sns:us-east-1:123456789:arka-saga-topic \
  --protocol lambda --notification-endpoint arn:aws:lambda:us-east-1:123456789:function:arka-saga-notification
```

## API Endpoints

### Iniciar Saga
```http
POST /api/saga/start
Content-Type: application/json

{
  "clienteId": "CLIENTE_001",
  "productoId": "PRODUCTO_123",
  "cantidad": 2,
  "precio": 99.99
}
```

### Webhooks para Eventos
```http
POST /api/saga/events/inventory-reserved
POST /api/saga/events/shipping-generated
POST /api/saga/events/notification-sent
```

## Ejecución Local

### Prerrequisitos
- Java 21
- Gradle
- AWS CLI configurado
- Docker (opcional para servicios simulados)

### Configuración de Propiedades

En `application.properties`:
```properties
# AWS Configuration
aws.region=us-east-1
aws.sns.saga-topic-arn=arn:aws:sns:us-east-1:123456789:arka-saga-topic
aws.lambda.inventory-function-name=arka-saga-inventory
aws.lambda.shipping-function-name=arka-saga-shipping
aws.lambda.notification-function-name=arka-saga-notification

# External Services
external.services.inventory.url=http://localhost:8081
external.services.shipping.url=http://localhost:8082
```

### Compilar y Ejecutar
```bash
# Compilar proyecto
./gradlew clean build

# Ejecutar aplicación
./gradlew bootRun
```

## Casos de Uso de Prueba

### Caso Exitoso
```bash
curl -X POST http://localhost:8888/api/saga/start \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": "CLI001",
    "productoId": "PROD123",
    "cantidad": 1,
    "precio": 29.99
  }'
```

### Caso con Fallo de Inventario
```bash
curl -X POST http://localhost:8888/api/saga/start \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": "CLI002",
    "productoId": "PROD999",
    "cantidad": 100,
    "precio": 29.99
  }'
```

## Beneficios de la Implementación

1. **Desacoplamiento**: Cada función Lambda es independiente
2. **Escalabilidad**: Las Lambdas escalan automáticamente
3. **Resiliencia**: Manejo de errores y compensación
4. **Observabilidad**: Logs detallados en CloudWatch
5. **Flexibilidad**: Fácil agregar nuevos pasos al proceso

## Monitoreo y Logging

- **CloudWatch Logs**: Cada Lambda genera logs detallados
- **SNS Metrics**: Métricas de mensajes publicados/fallidos
- **Lambda Metrics**: Duración, errores, invocaciones
- **Custom Metrics**: Estados del Saga por pedido

## Mejoras Futuras

1. **Persistencia**: Usar DynamoDB para estado del Saga
2. **Dead Letter Queues**: Para manejo de fallos
3. **Timeouts**: Implementar timeouts por paso
4. **Retry Logic**: Reintentos automáticos
5. **Dashboard**: UI para monitorear Sagas
6. **Testing**: Tests de integración con AWS LocalStack
