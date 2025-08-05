# Spring Cloud Configuration - Arka Valenzuela (CORREGIDO)

## Arquitectura de Microservicios con Spring Cloud

Este proyecto ha sido migrado para utilizar Spring Cloud con las siguientes características:

### Componentes Principales

1. **Eureka Server** (puerto 8761)
   - Registro y descubrimiento de servicios
   - Dashboard de monitoreo en http://localhost:8761
   - Autenticación básica: admin/admin123

2. **API Gateway** (puerto 8080)
   - Enrutamiento de solicitudes con Spring Cloud Gateway
   - Punto de entrada único para todos los servicios
   - Load balancing automático

3. **Microservicios**:
   - **arca-cotizador**: Puerto 8081 (instancia 1), 8091 (instancia 2)
   - **arca-gestor-solicitudes**: Puerto 8082 (instancia 1), 8092 (instancia 2)
   - **hello-world-service**: Puerto 8083 (instancia 1), 8084 (instancia 2)
   - **arkajvalenzuela**: Puerto por defecto (aplicación principal)

## Configuración de Puertos

| Servicio | Puerto Principal | Puerto Secundario | Descripción |
|----------|------------------|-------------------|-------------|
| Eureka Server | 8761 | - | Registro de servicios |
| API Gateway | 8080 | - | Punto de entrada único |
| Arca Cotizador | 8081 | 8091 | Servicio de cotización (WebFlux) |
| Arca Gestor | 8082 | 8092 | Gestor de solicitudes (WebFlux) |
| Hello World | 8083 | 8084 | Servicio de prueba |

## Rutas del API Gateway

| Ruta | Servicio Destino | Descripción |
|------|------------------|-------------|
| `/api/hello/**` | hello-world-service | Servicio de prueba |
| `/api/cotizador/**` | arca-cotizador | Servicio de cotización |
| `/api/gestor/**` | arca-gestor-solicitudes | Gestor de solicitudes |
| `/eureka/**` | eureka-server | Dashboard de Eureka |

## Instrucciones de Inicio

### Opción 1: Script Automático (RECOMENDADO)
```bash
# Ejecutar desde la raíz del proyecto
.\scripts\start-all-services.bat

# Para load balancing (ejecutar después del script anterior)
.\scripts\start-load-balancing.bat
```

### Opción 2: Inicio Manual Secuencial
```bash
# 1. Iniciar Eureka Server
./gradlew :eureka-server:bootRun

# 2. Esperar 30 segundos, luego iniciar API Gateway
./gradlew :api-gateway:bootRun

# 3. Iniciar servicios principales
./gradlew :hello-world-service:bootRun
./gradlew :arca-cotizador:bootRun
./gradlew :arca-gestor-solicitudes:bootRun

# 4. Iniciar instancias adicionales (para load balancing)
./gradlew :arca-cotizador:bootRun --args="--server.port=8091 --eureka.instance.instance-id=arca-cotizador:8091"
./gradlew :arca-gestor-solicitudes:bootRun --args="--server.port=8092 --eureka.instance.instance-id=arca-gestor-solicitudes:8092"
./gradlew :hello-world-service:bootRun --args="--server.port=8084 --eureka.instance.instance-id=hello-world-service:8084"
```

### Opción 3: Usando VS Code Tasks
1. Abrir VS Code en la raíz del proyecto
2. Ejecutar el task "Build All Modules" para compilar todo
3. Ejecutar cada servicio individualmente usando los tasks correspondientes

## Verificación del Sistema

### URLs importantes:
- **Eureka Dashboard**: http://localhost:8761 (admin/admin123)
- **API Gateway Health**: http://localhost:8080/actuator/health
- **Hello World (via Gateway)**: http://localhost:8080/api/hello
- **Cotizador (via Gateway)**: http://localhost:8080/api/cotizador
- **Gestor (via Gateway)**: http://localhost:8080/api/gestor

### Test de Load Balancing:
```powershell
# Ejecutar múltiples veces para ver diferentes instancias
for ($i=1; $i -le 10; $i++) {
    Write-Host "Request $i:"
    Invoke-RestMethod -Uri "http://localhost:8080/api/hello" -Method GET
    Start-Sleep -Seconds 1
}
```

## Configuraciones Corregidas

### 1. Versión de Java unificada a 21
### 2. Configuraciones de Eureka simplificadas
### 3. Properties limpios sin duplicados
### 4. Controladores básicos agregados a todos los servicios
### 5. Scripts de inicio automatizados
### 6. Documentación actualizada

## Estructura de Archivos Importantes

```
arkajvalenzuela/
├── eureka-server/              # Servidor de registro
├── api-gateway/                # Gateway de entrada
├── hello-world-service/        # Servicio de prueba
├── arca-cotizador/            # Servicio de cotización
├── arca-gestor-solicitudes/   # Servicio gestor
├── scripts/                   # Scripts de inicio
│   ├── start-all-services.bat
│   └── start-load-balancing.bat
└── build.gradle              # Configuración principal
```

## Resolución de Problemas

1. **Si Eureka no se conecta**: Verificar que el puerto 8761 esté libre
2. **Si Gateway no funciona**: Asegurar que Eureka esté funcionando primero
3. **Si los servicios no aparecen en Eureka**: Verificar las configuraciones de `eureka.client.service-url.defaultZone`
4. **Error de compilación**: Ejecutar `./gradlew clean build` desde la raíz

## Próximos Pasos

1. Probar todos los endpoints usando el archivo `TEST_ENDPOINTS.md`
2. Verificar el load balancing con múltiples instancias
3. Monitorear servicios desde el dashboard de Eureka
4. Implementar funcionalidades específicas en cada microservicio
