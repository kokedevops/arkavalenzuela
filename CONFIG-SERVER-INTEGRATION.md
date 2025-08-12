# 🏗️ INTEGRACIÓN CON ARKA CONFIG SERVER

## 📋 DESCRIPCIÓN

Este proyecto ha sido integrado con el **ARKA CONFIG SERVER** para centralizar la gestión de configuraciones de todos los microservicios.

**Config Server Repository:** https://github.com/kokedevops/arka-config-server.git

## 🚀 CONFIGURACIÓN IMPLEMENTADA

### Dependencias Agregadas
Todos los microservicios ahora incluyen:
```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-config'
implementation 'org.springframework.cloud:spring-cloud-starter-bootstrap'
```

### Archivos Bootstrap
Cada microservicio tiene un archivo `bootstrap.yml` que se conecta al Config Server:
- **URI del Config Server:** `http://127.0.0.1:9090`
- **Usuario:** `config-client`
- **Contraseña:** `arka-client-2025`
- **Perfil activo:** `dev`

### Configuraciones por Servicio
Se crearon archivos de configuración específicos en `config-repository/`:
- `eureka-server-dev.yml`
- `api-gateway-dev.yml`
- `arca-cotizador-dev.yml`
- `arca-gestor-solicitudes-dev.yml`
- `hello-world-service-dev.yml`
- `application.yml` (configuración compartida)

## 🛠️ SCRIPTS DISPONIBLES

### 1. `scripts/test-config-server.bat`
Prueba la conectividad con el Config Server y verifica que las configuraciones se estén sirviendo correctamente.

### 2. `scripts/upload-config.bat`
Sube las configuraciones locales al repositorio de GitHub del Config Server.

### 3. `scripts/start-with-config-server.bat`
Inicia todos los microservicios en el orden correcto, asegurando que se conecten al Config Server.

## 📊 PUERTOS Y SERVICIOS

| Servicio | Puerto | URL |
|----------|---------|-----|
| Config Server | - | http://127.0.0.1:9090|
| Eureka Server | 8761 | http://localhost:8761 |
| API Gateway | 8080 | http://localhost:8080 |
| Arca Cotizador | 8081 | http://localhost:8081 |
| Arca Gestor Solicitudes | 8082 | http://localhost:8082 |
| Hello World Service | 8083 | http://localhost:8083 |

## 🔄 ACTUALIZACIÓN DE CONFIGURACIONES

### En Tiempo Real (sin reiniciar servicios)
1. Modifica las configuraciones en el repositorio del Config Server
2. Llama al endpoint de refresh en cada servicio:
   ```bash
   curl -X POST http://localhost:8081/actuator/refresh
   curl -X POST http://localhost:8082/actuator/refresh
   # ... para cada servicio
   ```

### Desarrollo Local
Para desarrollo local, puedes cambiar en los archivos `bootstrap.yml`:
```yaml
# Comentar esta línea:
# uri: https://arka-config-server.herokuapp.com
# Descomentar esta línea:
uri: http://localhost:8888
```

## 🔍 VERIFICACIÓN

### 1. Health Checks
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
# ... para cada servicio
```

### 2. Configuración Actual
```bash
curl http://localhost:8081/actuator/env
curl http://localhost:8082/actuator/env
# ... para cada servicio
```

### 3. Config Server Directo
```bash
curl -u config-client:arka-client-2025 "https://arka-config-server.herokuapp.com/arca-cotizador/dev"
```

## 📝 NOTAS IMPORTANTES

1. **Orden de Inicio:** Es importante iniciar Eureka Server primero, luego los demás servicios.
2. **Conectividad:** Los servicios necesitan acceso a internet para conectarse al Config Server remoto.
3. **Credenciales:** Las credenciales están configuradas para el entorno de desarrollo.
4. **Profiles:** Actualmente todos los servicios usan el profile `dev`.
