# 🚀 GUÍA DE EJECUCIÓN DE MICROSERVICIOS ARKA

Esta guía te ayuda a ejecutar todos los microservicios ARKA individualmente usando los JARs generados.

## 📋 **MICROSERVICIOS DISPONIBLES**

| Microservicio | Puerto | JAR | Descripción |
|---------------|--------|-----|-------------|
| **Config Server** | 8888 | `config-server-1.0.0.jar` | Configuración centralizada |
| **Eureka Server** | 8761 | `eureka-server.jar` | Service Discovery |
| **API Gateway** | 8080 | `api-gateway.jar` | Puerta de entrada |
| **Arca Cotizador** | 8081 | `arca-cotizador-0.0.1-SNAPSHOT.jar` | Servicio de cotizaciones |
| **Arca Gestor Solicitudes** | 8082 | `arca-gestor-solicitudes-0.0.1-SNAPSHOT.jar` | Gestión de solicitudes |
| **Hello World Service** | 8083 | `hello-world-service.jar` | Servicio de prueba |
| **ARKA E-commerce Core** | 8888 | `arkajvalenzuela-0.0.1-SNAPSHOT.war` | Aplicación principal |

## 🎯 **OPCIÓN 1: EJECUCIÓN AUTOMÁTICA (RECOMENDADO)**

### Iniciar todos los microservicios:
```bash
start-all-microservices.bat
```

### Detener todos los microservicios:
```bash
stop-all-microservices.bat
```

### Verificar estado:
```bash
check-microservices-status.bat
```

## 🔧 **OPCIÓN 2: EJECUCIÓN INDIVIDUAL**

### 1. Config Server (OBLIGATORIO - Iniciar primero)
```bash
start-config-server.bat
# o manualmente:
java -jar config-server/build/libs/config-server-1.0.0.jar --spring.profiles.active=k8s --server.port=8888
```

### 2. Eureka Server (OBLIGATORIO - Iniciar segundo)
```bash
start-eureka-server.bat
# o manualmente:
java -jar eureka-server/build/libs/eureka-server.jar --spring.profiles.active=k8s --server.port=8761
```

### 3. API Gateway (RECOMENDADO)
```bash
start-api-gateway.bat
# o manualmente:
java -jar api-gateway/build/libs/api-gateway.jar --spring.profiles.active=k8s --server.port=8080
```

### 4. Microservicios de Negocio
```bash
# Arca Cotizador
start-arca-cotizador.bat

# Arca Gestor Solicitudes  
start-arca-gestor-solicitudes.bat

# Hello World Service
start-hello-world-service.bat
```

### 5. Aplicación Principal ARKA
```bash
start-arka-core.bat
# o manualmente:
java -jar build/libs/arkajvalenzuela-0.0.1-SNAPSHOT.war --spring.profiles.active=k8s --server.port=8888
```

## 📊 **ORDEN DE EJECUCIÓN RECOMENDADO**

1. **Config Server** (8888) - ⚡ Obligatorio primero
2. **Eureka Server** (8761) - ⚡ Obligatorio segundo  
3. **API Gateway** (8080) - 🌐 Puerta de entrada
4. **Microservicios de negocio** (8081, 8082, 8083) - 🔧 Servicios
5. **ARKA Core** (8888) - 🛒 Aplicación principal

## 🌐 **URLs DE ACCESO**

### Dashboard y Monitoreo:
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway Health**: http://localhost:8080/actuator/health
- **Config Server**: http://localhost:8888

### APIs de Microservicios:
- **Arca Cotizador**: http://localhost:8081/actuator/health
- **Arca Gestor Solicitudes**: http://localhost:8082/actuator/health  
- **Hello World Service**: http://localhost:8083/actuator/health

### Aplicación Principal:
- **ARKA E-commerce**: http://localhost:8888
- **ARKA Health**: http://localhost:8888/health
- **ARKA API**: http://localhost:8888/api/

## 🔍 **VERIFICACIÓN Y TROUBLESHOOTING**

### Verificar servicios registrados en Eureka:
```bash
curl http://localhost:8761/eureka/apps
```

### Verificar health de todos los servicios:
```bash
curl http://localhost:8888/actuator/health
curl http://localhost:8761/actuator/health
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

### Ver puertos en uso:
```bash
netstat -an | findstr ":8888 :8761 :8080 :8081 :8082 :8083"
```

### Si un puerto está ocupado:
```bash
# Encontrar el proceso
netstat -ano | findstr ":8080"

# Matar el proceso
taskkill /f /pid [PID_NUMBER]
```

## ⚙️ **CONFIGURACIÓN**

### Perfiles disponibles:
- `k8s` - Para despliegue en Kubernetes (por defecto)
- `dev` - Para desarrollo local
- `prod` - Para producción

### Cambiar perfil:
```bash
java -jar microservicio.jar --spring.profiles.active=dev
```

### Cambiar puerto:
```bash
java -jar microservicio.jar --server.port=9090
```

### Opciones de memoria:
```bash
java -Xmx1024m -Xms512m -jar microservicio.jar
```

## 🚨 **PROBLEMAS COMUNES**

### Error "Port already in use":
```bash
# Detener todos los servicios Java
taskkill /f /im java.exe

# O usar el script
stop-all-microservices.bat
```

### Error "Unable to connect to Config Server":
- Asegúrate de que Config Server esté ejecutándose en puerto 8888
- Verifica: `curl http://localhost:8888/actuator/health`

### Error "Eureka registration failed":
- Asegúrate de que Eureka Server esté ejecutándose en puerto 8761
- Verifica: `curl http://localhost:8761`

### Microservicio no aparece en Eureka:
- Espera 30-60 segundos para el registro automático
- Verifica el dashboard: http://localhost:8761

## 📈 **MONITOREO**

### Ver logs en tiempo real:
- Los logs aparecen en la consola donde ejecutaste el JAR
- Para logs en background, redirige la salida:
```bash
java -jar microservicio.jar > logs/microservicio.log 2>&1 &
```

### Endpoints de monitoreo:
- `/actuator/health` - Estado del servicio
- `/actuator/info` - Información del servicio
- `/actuator/metrics` - Métricas de rendimiento

## 🎉 **¡LISTO!**

Con estos scripts y comandos puedes:
- ✅ Ejecutar todos los microservicios automáticamente
- ✅ Ejecutar microservicios individualmente
- ✅ Monitorear el estado de todos los servicios
- ✅ Troubleshoot problemas comunes
- ✅ Desplegar en diferentes entornos

Para cualquier duda, revisa los logs de cada microservicio o usa `check-microservices-status.bat` para verificar el estado.
