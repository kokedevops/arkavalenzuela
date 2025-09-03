# 🎯 INSTRUCCIONES COMPLETAS - PATRÓN SAGA AWS

## 📚 DOCUMENTACIÓN DISPONIBLE

1. **[SAGA-SETUP-GUIDE.md](SAGA-SETUP-GUIDE.md)** - Guía paso a paso para desarrollo local
2. **[AWS-SETUP-GUIDE.md](AWS-SETUP-GUIDE.md)** - Configuración completa en AWS
3. **[test-saga.http](test-saga.http)** - Casos de prueba para el API

## 🚀 INICIO RÁPIDO

### 1. DESARROLLO LOCAL (RECOMENDADO PARA EMPEZAR)

```bash
# 1. Iniciar aplicación en modo desarrollo
start-saga.bat

# 2. En otra terminal, probar la funcionalidad
test-saga.bat
```

### 2. DESPLIEGUE A AWS (PRODUCCIÓN)

```bash
# 1. Configurar AWS CLI
aws configure

# 2. Ejecutar despliegue automático
cd aws-scripts
deploy-complete.bat
```

## 📋 SCRIPTS DISPONIBLES

### Scripts para Windows:
- **`start-saga.bat`** - Inicia la aplicación en modo desarrollo
- **`test-saga.bat`** - Ejecuta pruebas del patrón Saga
- **`aws-scripts/deploy-complete.bat`** - Despliegue completo a AWS

### Scripts para Linux/Mac:
- **`aws-scripts/deploy-complete.sh`** - Despliegue completo a AWS
- **`aws-scripts/create-sns-topics.sh`** - Crear topics SNS
- **`aws-scripts/create-sqs-queues.sh`** - Crear colas SQS
- **`aws-scripts/create-iam-roles.sh`** - Crear roles IAM
- **`aws-scripts/deploy-lambdas.sh`** - Desplegar funciones Lambda
- **`aws-scripts/configure-triggers.sh`** - Configurar triggers

## 🔧 CONFIGURACIÓN ACTUAL

### Modo Desarrollo (Local)
- ✅ **MockSagaEventPublisher** - Simula AWS SNS
- ✅ **MockLambdaInvoker** - Simula AWS Lambda
- ✅ **MockExternalServiceAdapter** - Simula servicios S2 y ChipMen
- ✅ **MockSagaEventSimulator** - Simula respuestas asíncronas
- ✅ **Base de datos H2** en memoria
- ✅ **Puerto:** 8888

### Modo Producción (AWS)
- 🌐 **AWS SNS** para eventos
- 🌐 **AWS SQS** para colas
- 🌐 **AWS Lambda** para procesamiento
- 🌐 **Amazon RDS** para base de datos
- 🌐 **Amazon DocumentDB** para notificaciones

## 🧪 CASOS DE PRUEBA IMPLEMENTADOS

### ✅ Caso Exitoso
```json
{
  "pedidoId": "PEDIDO_SUCCESS_001", 
  "clienteId": "CLIENTE_123",
  "productoId": "PRODUCTO_ABC",
  "cantidad": 2,
  "precio": 150.00
}
```

**Flujo esperado:**
1. Reserva inventario (S2) ✅
2. Genera envío (ChipMen) ✅
3. Envía notificación ✅
4. Pedido completado ✅

### ❌ Casos de Error (para probar compensaciones)

**Error en inventario (contiene "999"):**
```json
{
  "pedidoId": "PEDIDO_999_ERROR",
  "productoId": "PRODUCTO_999"
}
```

**Error en envío (contiene "888"):**
```json
{
  "pedidoId": "PEDIDO_888_ERROR", 
  "productoId": "PRODUCTO_888"
}
```

**Error en notificación (contiene "777"):**
```json
{
  "pedidoId": "PEDIDO_777_ERROR",
  "productoId": "PRODUCTO_777"
}
```

## 📊 ENDPOINTS DISPONIBLES

### API Principal
- **POST** `/api/saga/start` - Iniciar nuevo pedido Saga

### Webhooks (para respuestas de servicios externos)
- **POST** `/api/saga/webhook/inventory` - Respuesta de S2
- **POST** `/api/saga/webhook/shipping` - Respuesta de ChipMen
- **POST** `/api/saga/webhook/notification` - Respuesta de notificación

### Monitoreo
- **GET** `/actuator/health` - Estado de la aplicación
- **GET** `/actuator/metrics` - Métricas
- **GET** `/h2-console` - Consola de base de datos (solo desarrollo)

## 🔍 MONITOREO Y LOGS

### Logs importantes a revisar:
```
🎭 MockSagaEventPublisher inicializado - Modo desarrollo
🔧 MockLambdaInvoker inicializado - Modo desarrollo
🏭 MockExternalServiceAdapter inicializado - Modo desarrollo
🎡 MockSagaEventSimulator inicializado

📢 [MOCK SNS] Evento publicado: inventory-request
🏭 [MOCK S2] Conectando con servicio de inventario
✅ S2 confirmó reserva para X unidades
🚚 [MOCK CHIPMEN] Generando orden de envío
✅ ChipMen generó orden de envío: CHIPMEN_xxxxx
📧 [MOCK] Enviando notificación al cliente
```

### En AWS CloudWatch:
- Lambda execution logs
- SNS delivery reports
- SQS message metrics
- Application performance

## 💡 SOLUCIÓN DE PROBLEMAS

### Problema: Puerto 8888 ocupado
```bash
# Cambiar puerto en application-aws.properties
server.port=8889
```

### Problema: Bean conflicts
```bash
# Verificar en application-aws.properties
aws.mock.enabled=true  # para desarrollo
aws.mock.enabled=false # para AWS
```

### Problema: Errores de base de datos
✅ **Ignorar en desarrollo local** - los errores de MySQL/MongoDB son normales

### Problema: AWS deployment falló
```bash
# Verificar credenciales
aws sts get-caller-identity

# Verificar permisos IAM
aws iam list-attached-role-policies --role-name ArkaLambdaExecutionRole
```

## 📞 CONTACTO Y SOPORTE

### Archivos de log importantes:
- Logs de aplicación Spring Boot
- `aws-config.txt` (configuración AWS)
- CloudWatch Logs (en AWS)

### Comandos de diagnóstico:
```bash
# Verificar estado de la aplicación
curl http://localhost:8888/actuator/health

# Verificar métricas
curl http://localhost:8888/actuator/metrics

# Verificar recursos AWS
aws sns list-topics
aws sqs list-queues
aws lambda list-functions
```

## 🎯 RESULTADO FINAL

Al seguir estas instrucciones tendrás:

1. ✅ **Patrón Saga completo** funcionando
2. ✅ **Simulación local** de AWS Lambda, SNS, SQS
3. ✅ **Integración con servicios externos** (S2, ChipMen)
4. ✅ **Compensaciones automáticas** en caso de error
5. ✅ **Monitoreo y logging** detallado
6. ✅ **Scripts de despliegue** a AWS
7. ✅ **Documentación completa**

## 📈 MÉTRICAS DE ÉXITO

- ✅ Aplicación inicia sin errores
- ✅ Endpoints responden correctamente
- ✅ Logs muestran flujo completo del Saga
- ✅ Casos de error ejecutan compensaciones
- ✅ Monitoreo funciona correctamente

**¡Tu patrón Saga está listo para producción! 🚀**
