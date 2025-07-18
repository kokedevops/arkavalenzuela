# 🧪 GUÍA DE TESTING Y VERIFICACIÓN

## 🚀 Pasos para Probar la Integración

### 1. 📋 Pre-requisitos

Antes de comenzar, asegúrate de tener:

- ✅ Java 21 instalado
- ✅ Los tres proyectos descargados:
  - `arkavalenzuela` (este proyecto)
  - `arca-cotizador`
  - `arca-gestor-solicitudes`
- ✅ MySQL ejecutándose en puerto 3306
- ✅ Base de datos `arkabd` creada

### 2. 🏃‍♂️ Iniciar los Servicios

#### Paso 1: Iniciar Arca Cotizador (Puerto 8080)
```bash
cd arca-cotizador
./gradlew bootRun
```
✅ Verifica que esté corriendo: `http://localhost:8080/actuator/health`

#### Paso 2: Iniciar Arca Gestor de Solicitudes (Puerto 8081)
```bash
cd arca-gestor-solicitudes  
./gradlew bootRun
```
✅ Verifica que esté corriendo: `http://localhost:8081/actuator/health`

#### Paso 3: Iniciar Arka Principal (Puerto 8082)
```bash
cd arkavalenzuela
./gradlew bootRun
```
✅ Verifica que esté corriendo: `http://localhost:8082/api/system/health`

---

## 🧪 Scripts de Testing

### 🔍 1. Verificar Estado del Sistema

```bash
# Verificar sistema principal
curl -X GET http://localhost:8082/api/system/health

# Verificar conexión con microservicios
curl -X GET http://localhost:8082/api/cotizaciones/servicio/health
curl -X GET http://localhost:8082/api/solicitudes/servicio/health
```

### 📦 2. Crear Datos de Prueba

#### Crear Categoría
```bash
curl -X POST http://localhost:8082/categorias \\
  -H "Content-Type: application/json" \\
  -d '{
    "nombre": "Electrónicos",
    "descripcion": "Productos electrónicos y tecnológicos"
  }'
```

#### Crear Productos
```bash
# Producto 1
curl -X POST http://localhost:8082/productos \\
  -H "Content-Type: application/json" \\
  -d '{
    "nombre": "Laptop Dell",
    "descripcion": "Laptop Dell Inspiron 15",
    "categoriaId": 1,
    "marca": "Dell",
    "precioUnitario": 800.00,
    "stock": 10
  }'

# Producto 2
curl -X POST http://localhost:8082/productos \\
  -H "Content-Type: application/json" \\
  -d '{
    "nombre": "Mouse Inalámbrico",
    "descripcion": "Mouse inalámbrico ergonómico",
    "categoriaId": 1,
    "marca": "Logitech",
    "precioUnitario": 25.00,
    "stock": 50
  }'
```

#### Crear Cliente
```bash
curl -X POST http://localhost:8082/usuarios \\
  -H "Content-Type: application/json" \\
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan.perez@email.com",
    "telefono": "123-456-7890",
    "direccion": "Calle Principal 123"
  }'
```

### 🔵 3. Probar Servicio de Cotizaciones

#### Solicitar Cotización
```bash
curl -X POST "http://localhost:8082/api/cotizaciones/cliente/1/productos?tipoCliente=PREMIUM" \\
  -H "Content-Type: application/json" \\
  -d '[1, 2]'
```

#### Obtener Cotizaciones del Cliente
```bash
curl -X GET http://localhost:8082/api/cotizaciones/cliente/1
```

### 🟢 4. Probar Servicio de Solicitudes

#### Crear Solicitud de Cotización
```bash
curl -X POST http://localhost:8082/api/solicitudes/cliente/1/cotizacion \\
  -H "Content-Type: application/json" \\
  -d '{
    "productIds": [1, 2],
    "observaciones": "Necesito cotización para proyecto corporativo"
  }'
```

#### Crear Solicitud Urgente
```bash
curl -X POST http://localhost:8082/api/solicitudes/cliente/1/urgente \\
  -H "Content-Type: application/json" \\
  -d '{
    "productIds": [1],
    "observaciones": "Solicitud urgente - proyecto crítico"
  }'
```

#### Obtener Solicitudes del Cliente
```bash
curl -X GET http://localhost:8082/api/solicitudes/cliente/1
```

---

## 🔍 Verificaciones de Funcionalidad

### ✅ Checklist de Verificación

#### Sistema Principal
- [ ] El sistema inicia en puerto 8082
- [ ] Endpoint de salud responde: `/api/system/health`
- [ ] Se pueden crear categorías, productos y clientes
- [ ] Los endpoints REST funcionan correctamente

#### Integración con Cotizador
- [ ] Health check responde: `/api/cotizaciones/servicio/health`
- [ ] Se pueden solicitar cotizaciones
- [ ] Se obtienen cotizaciones por cliente
- [ ] Se pueden actualizar estados de cotización

#### Integración con Gestor de Solicitudes
- [ ] Health check responde: `/api/solicitudes/servicio/health`
- [ ] Se pueden crear solicitudes de diferentes tipos
- [ ] Se obtienen solicitudes por cliente
- [ ] Se pueden actualizar estados de solicitud

---

## 🐛 Solución de Problemas

### ❌ Error: "Connection refused"

**Problema**: No se puede conectar a un microservicio
**Solución**: 
1. Verificar que el microservicio esté ejecutándose
2. Comprobar que esté en el puerto correcto
3. Verificar la configuración en `application.properties`

### ❌ Error: "Timeout"

**Problema**: Las requests tardan mucho tiempo
**Solución**:
1. Aumentar timeouts en `application.properties`:
   ```properties
   microservices.timeout.connection=10000
   microservices.timeout.read=20000
   ```

### ❌ Error: "Service unavailable"

**Problema**: El health check retorna false
**Solución**:
1. Verificar logs del microservicio
2. Comprobar endpoints de actuator:
   - `http://localhost:8080/actuator/health`
   - `http://localhost:8081/actuator/health`

---

## 📊 Monitoring y Logs

### Ver Logs en Tiempo Real

```bash
# Sistema principal
tail -f arkavalenzuela/logs/application.log

# Filtrar logs de microservicios
grep "CotizadorServiceAdapter" arkavalenzuela/logs/application.log
grep "GestorSolicitudesServiceAdapter" arkavalenzuela/logs/application.log
```

### Métricas Básicas

```bash
# Ver información del sistema
curl -X GET http://localhost:8082/api/system/info

# Verificar memoria y CPU
curl -X GET http://localhost:8082/actuator/metrics
```

---

## 🎯 Escenarios de Prueba Completos

### Escenario 1: Flujo Completo de Cotización

1. **Crear productos y cliente** (scripts anteriores)
2. **Solicitar cotización**:
   ```bash
   curl -X POST "http://localhost:8082/api/cotizaciones/cliente/1/productos?tipoCliente=PREMIUM" \\
     -H "Content-Type: application/json" \\
     -d '[1, 2]'
   ```
3. **Obtener cotización creada** (usar ID retornado)
4. **Actualizar estado de cotización**

### Escenario 2: Flujo Completo de Solicitudes

1. **Crear solicitud urgente**
2. **Obtener solicitudes del cliente**
3. **Filtrar por estado**
4. **Actualizar estado de solicitud**

### Escenario 3: Prueba de Resiliencia

1. **Parar un microservicio**
2. **Intentar usar funcionalidad**
3. **Verificar que se maneja el error gracefully**
4. **Reiniciar servicio y verificar recuperación**

---

## 📈 Métricas de Éxito

Una integración exitosa debe mostrar:

- ✅ **Latencia < 2 segundos** para operaciones normales
- ✅ **Health checks en verde** para todos los servicios
- ✅ **Logs sin errores** durante operaciones normales
- ✅ **Manejo graceful** de fallos de comunicación
- ✅ **Recuperación automática** cuando los servicios vuelven online

---

¡Con esta guía puedes verificar que toda la integración de microservicios funciona correctamente! 🎉
