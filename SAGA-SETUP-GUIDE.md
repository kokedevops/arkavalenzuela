# 🚀 GUÍA PASO A PASO - PATRÓN SAGA CON AWS LAMBDA, SQS Y SNS

## 📋 PRERREQUISITOS

### 1. Software Requerido
- **Java 21** ✅ (ya instalado)
- **Gradle** ✅ (ya configurado)
- **Git** ✅ (ya configurado)
- **Postman o similar** (para pruebas de API)

### 2. Herramientas opcionales
- **AWS CLI** (para despliegue en AWS)
- **Docker** (para entorno local completo)

## 🛠️ CONFIGURACIÓN INICIAL

### PASO 1: Verificar que la aplicación compile
```bash
cd c:\Users\valen\arkavalenzuela-1
.\gradlew clean build -x test
```

### PASO 2: Agregar configuración de modo mock
Editar `src/main/resources/application-aws.properties` y agregar:

```properties
# Configuración para el modo mock (desarrollo local)
aws.mock.enabled=true

# Configuración de Saga
saga.timeout.minutes=10
saga.retry.max-attempts=3
saga.retry.delay-seconds=2

# External Services (mock)
external.s2.enabled=true
external.chipmen.enabled=true

# Actuator para monitoreo
management.endpoints.web.exposure.include=health,info,metrics,saga
management.endpoint.health.show-details=always
```

## 🔧 EJECUCIÓN LOCAL (MODO DESARROLLO)

### PASO 3: Iniciar la aplicación
```bash
.\gradlew bootRun --args="--spring.profiles.active=aws"
```

**✅ Salida esperada:**
- Puerto: 8888
- Logs: "MockSagaEventPublisher inicializado"
- Logs: "MockLambdaInvoker inicializado" 
- Logs: "MockExternalServiceAdapter inicializado"
- Estado: "Started ArkajvalenzuelaApplication"

### PASO 4: Verificar endpoints disponibles
```bash
curl http://localhost:8888/actuator/health
```

## 🧪 PRUEBAS DEL PATRÓN SAGA

### PASO 5: Crear pedido exitoso
```bash
curl -X POST http://localhost:8888/api/saga/start \
  -H "Content-Type: application/json" \
  -d '{
    "pedidoId": "PEDIDO_SUCCESS_001", 
    "clienteId": "CLIENTE_123",
    "productoId": "PRODUCTO_ABC",
    "cantidad": 2,
    "precio": 150.00
  }'
```

**✅ Flujo esperado (logs):**
1. "Iniciando Saga para pedido: PEDIDO_SUCCESS_001"
2. "📢 [MOCK SNS] Evento publicado: inventory-request"
3. "🏭 [MOCK S2] Conectando con servicio de inventario"
4. "✅ S2 confirmó reserva para 2 unidades"
5. "🚚 [MOCK CHIPMEN] Generando orden de envío"
6. "✅ ChipMen generó orden de envío: CHIPMEN_xxxxx"
7. "📧 [MOCK] Enviando notificación al cliente"

### PASO 6: Probar casos de error

**Error en inventario (contiene "999"):**
```bash
curl -X POST http://localhost:8888/api/saga/start \
  -H "Content-Type: application/json" \
  -d '{
    "pedidoId": "PEDIDO_999_ERROR", 
    "clienteId": "CLIENTE_123",
    "productoId": "PRODUCTO_999",
    "cantidad": 2,
    "precio": 150.00
  }'
```

**Error en envío (contiene "888"):**
```bash
curl -X POST http://localhost:8888/api/saga/start \
  -H "Content-Type: application/json" \
  -d '{
    "pedidoId": "PEDIDO_888_ERROR", 
    "clienteId": "CLIENTE_123",
    "productoId": "PRODUCTO_888",
    "cantidad": 2,
    "precio": 150.00
  }'
```

## 📊 MONITOREO Y VALIDACIÓN

### PASO 7: Verificar logs
Los logs mostrarán todo el flujo del patrón Saga:

```
🎬 Simulando evento de inventario reservado para pedido: PEDIDO_SUCCESS_001
🎬 Simulando evento de envío generado para pedido: PEDIDO_SUCCESS_001  
🎬 Simulando evento de notificación para pedido: PEDIDO_SUCCESS_001
```

### PASO 8: Verificar estado de la aplicación
```bash
curl http://localhost:8888/actuator/health
curl http://localhost:8888/actuator/metrics
```

## 🌐 DESPLIEGUE A AWS (PRODUCCIÓN)

### PASO 9: Configurar AWS CLI
```bash
# Instalar AWS CLI v2 desde: https://aws.amazon.com/cli/
aws configure
```

**Datos requeridos:**
- AWS Access Key ID
- AWS Secret Access Key  
- Default region: `us-east-1`
- Default output format: `json`

### PASO 10: Ejecutar despliegue automático

**En Windows:**
```bash
cd aws-scripts
deploy-complete.bat
```

**En Linux/Mac:**
```bash
cd aws-scripts
chmod +x *.sh
./deploy-complete.sh
```

### PASO 11: Verificar despliegue

El script automático creará:
- ✅ **SNS Topics** para eventos Saga
- ✅ **SQS Queues** con Dead Letter Queues
- ✅ **Lambda Functions** (3 funciones)
- ✅ **IAM Roles** y políticas
- ✅ **Triggers** SNS→SQS→Lambda
- ✅ **Archivo de configuración** (aws-config.txt)
- ✅ **Variables de entorno** (aws-environment.env)

### PASO 12: Cambiar a modo producción

1. **Cargar variables de entorno:**
```bash
source aws-environment.env
```

2. **Actualizar application-aws.properties:**
```properties
aws.mock.enabled=false
# Las demás configuraciones se actualizan automáticamente
```

3. **Configurar bases de datos** (ver AWS-SETUP-GUIDE.md)

### PASO 13: Probar en AWS

```bash
# Iniciar aplicación en modo AWS
.\gradlew bootRun --args="--spring.profiles.active=aws"

# Probar endpoint (mismo comando que en desarrollo)
curl -X POST http://localhost:8888/api/saga/start \
  -H "Content-Type: application/json" \
  -d '{...}'
```

## 🔍 TROUBLESHOOTING

### Problema: Bean conflicts
**Solución:** Verificar que `aws.mock.enabled=true` esté configurado

### Problema: Puerto ocupado
**Solución:** Cambiar puerto en `application-aws.properties`:
```properties
server.port=8889
```

### Problema: Base de datos
**Solución:** Los errores de BD se ignoran en desarrollo local

### Problema: AWS dependencies
**Solución:** Las implementaciones mock evitan dependencias AWS

## 📈 CASOS DE USO IMPLEMENTADOS

### ✅ Flujo Exitoso
1. Cliente crea pedido
2. Sistema reserva inventario (S2)
3. Sistema genera envío (ChipMen)
4. Sistema envía notificación
5. Pedido completado

### ✅ Flujo con Error - Compensación
1. Cliente crea pedido
2. Error en algún paso
3. Sistema ejecuta compensaciones
4. Pedido marcado como fallido

### ✅ Integración de Servicios Externos
- **S2 (Inventario):** Reserva y liberación de stock
- **ChipMen (Envíos):** Generación de órdenes de envío
- **Notificaciones:** Email/SMS al cliente

## 🎯 RESULTADOS ESPERADOS

Al seguir estos pasos tendrás:

1. ✅ **Patrón Saga funcionando** en modo desarrollo
2. ✅ **Simulación completa** de AWS Lambda, SNS, SQS
3. ✅ **Integración con servicios externos** (mock)
4. ✅ **Compensaciones automáticas** en caso de error
5. ✅ **Monitoreo y logging** detallado
6. ✅ **Preparado para despliegue** a AWS

## 📞 CONTACTO

Si tienes problemas siguiendo estos pasos, revisa:
1. Los logs de la aplicación
2. La configuración en `application-aws.properties`
3. Que el puerto 8888 esté libre
4. Que Java 21 esté instalado correctamente
