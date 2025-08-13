# 🔐 Integración Completa de Spring Security en Proyecto ARKA

## 📋 Resumen de Implementación

Se ha implementado un sistema completo de **autenticación y autorización** con Spring Security para todo el proyecto ARKA, incluyendo:

### ✅ Funcionalidades Implementadas

1. **✨ Registro Seguro de Usuarios**
2. **🔑 Inicio de Sesión con JWT**
3. **🛡️ Protección de Rutas con JWT**
4. **🔄 Refresh Token para Renovación**
5. **🎭 Gestión de Roles y Permisos**
6. **👑 Diferentes Tipos de Usuarios**

---

## 🏗️ Arquitectura de Seguridad

### 🧩 Módulos Implementados

```
arka-security-common/          # Módulo compartido de seguridad
├── domain/
│   ├── model/
│   │   ├── Usuario.java       # Entidad usuario con UserDetails
│   │   └── RefreshToken.java  # Entidad refresh token
│   └── repository/
│       ├── UsuarioRepository.java
│       └── RefreshTokenRepository.java
├── service/
│   ├── JwtService.java        # Servicio JWT
│   └── AuthService.java       # Servicio de autenticación
├── dto/                       # DTOs de request/response
└── config/                    # Configuraciones de seguridad
```

### 🚧 Integración en Microservicios

- **🌐 API Gateway**: Filtro JWT global + configuración de rutas
- **📊 Gestor Solicitudes**: Controladores de auth + configuración de seguridad
- **💰 Cotizador**: Configuración de seguridad
- **🔍 Eureka Server**: Sin cambios (discovery service)

---

## 👥 Sistema de Roles y Permisos

### 🎯 Roles Disponibles

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **ADMINISTRADOR** | Control total del sistema | Todos los permisos |
| **GESTOR** | Gestión de operaciones | Gestión de usuarios, operaciones |
| **OPERADOR** | Operador de cálculos | Cálculos, cotizaciones |
| **USUARIO** | Usuario regular | Solo lectura de cálculos |

### 🔐 Matriz de Permisos

```yaml
ADMINISTRADOR:
  - USER_CREATE, USER_READ, USER_UPDATE, USER_DELETE
  - CALC_CREATE, CALC_READ, CALC_UPDATE, CALC_DELETE
  - QUOTE_CREATE, QUOTE_READ, QUOTE_UPDATE, QUOTE_DELETE
  - SYSTEM_CONFIG, METRICS_VIEW, AUDIT_VIEW

GESTOR:
  - USER_READ, USER_UPDATE
  - CALC_CREATE, CALC_READ, CALC_UPDATE
  - QUOTE_CREATE, QUOTE_READ, QUOTE_UPDATE
  - METRICS_VIEW

OPERADOR:
  - CALC_CREATE, CALC_READ, CALC_UPDATE
  - QUOTE_CREATE, QUOTE_READ

USUARIO:
  - CALC_READ, QUOTE_READ
```

---

## 🛣️ Configuración de Rutas y Seguridad

### 🌐 API Gateway Routes

```yaml
# Rutas públicas (sin autenticación)
/auth/**                    # Autenticación y registro
/actuator/health           # Health checks
/eureka/**                 # Eureka dashboard

# Rutas protegidas por rol
/api/admin/**              # Solo ADMINISTRADOR
/api/gestion/**            # ADMINISTRADOR, GESTOR
/api/operaciones/**        # ADMINISTRADOR, GESTOR, OPERADOR
/api/calculos/**           # Todos los roles autenticados
```

### 🔒 Seguridad por Microservicio

```java
// Ejemplo de configuración en controladores
@PreAuthorize("hasRole('ADMINISTRADOR')")          // Solo admin
@PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")     // Admin o gestor
@PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'OPERADOR', 'USUARIO')") // Todos
```

---

## 🔑 Autenticación JWT

### 📋 Flujo de Autenticación

1. **📝 Registro**: `POST /auth/register`
2. **🔐 Login**: `POST /auth/login`
3. **🎫 Recibir Tokens**: `access_token` + `refresh_token`
4. **📡 Usar Token**: Header `Authorization: Bearer <token>`
5. **🔄 Renovar**: `POST /auth/refresh`

### 🎫 Estructura del JWT

```json
{
  "sub": "username",
  "userId": 123,
  "email": "user@arka.com",
  "role": "ADMINISTRADOR",
  "nombreCompleto": "Juan Pérez",
  "permissions": ["USER_CREATE", "CALC_READ", ...],
  "iat": 1703123456,
  "exp": 1703209856
}
```

### ⏱️ Configuración de Expiración

- **Access Token**: 24 horas (configurable)
- **Refresh Token**: 7 días (configurable)
- **Limpieza automática**: Tokens expirados se eliminan

---

## 📡 Endpoints de Autenticación

### 🔓 Endpoints Públicos

#### 📝 Registro de Usuario
```http
POST /auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@arka.com",
  "password": "password123",
  "nombreCompleto": "Nuevo Usuario"
}
```

**Respuesta:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "usuario": {
    "id": 123,
    "username": "newuser",
    "email": "newuser@arka.com",
    "nombreCompleto": "Nuevo Usuario",
    "rol": "USUARIO",
    "permisos": ["CALC_READ", "QUOTE_READ"]
  }
}
```

#### 🔐 Inicio de Sesión
```http
POST /auth/login
Content-Type: application/json

{
  "identifier": "admin",  // username o email
  "password": "admin123"
}
```

#### 🔄 Renovar Token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

### 🔒 Endpoints Protegidos

#### ✅ Validar Token
```http
GET /auth/validate
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

#### 👤 Información del Usuario
```http
GET /auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

#### 🚪 Cerrar Sesión
```http
POST /auth/logout
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

## 👑 Gestión de Usuarios (Solo Administradores)

### 📊 Listar Usuarios
```http
GET /api/admin/usuarios
Authorization: Bearer <admin_token>
```

### ➕ Crear Usuario con Rol Específico
```http
POST /api/admin/usuarios
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "username": "operador1",
  "email": "operador1@arka.com",
  "password": "password123",
  "nombreCompleto": "Operador 1",
  "rol": "OPERADOR"
}
```

### 🔄 Cambiar Rol de Usuario
```http
PUT /api/admin/usuarios/123/rol
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "rol": "GESTOR"
}
```

### 📈 Estadísticas de Usuarios
```http
GET /api/admin/usuarios/estadisticas
Authorization: Bearer <admin_token>
```

**Respuesta:**
```json
{
  "totalActivos": 15,
  "administradores": 2,
  "gestores": 3,
  "operadores": 5,
  "usuarios": 5
}
```

---

## 💾 Base de Datos

### 🗃️ Esquema de Tablas

#### 👤 Tabla `usuarios`
```sql
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'USUARIO',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_acceso TIMESTAMP NULL
);
```

#### 🎫 Tabla `refresh_tokens`
```sql
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    fecha_expiracion TIMESTAMP NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(500) NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
```

### 👤 Usuarios de Prueba

| Username | Email | Password | Rol |
|----------|-------|----------|-----|
| admin | admin@arka.com | admin123 | ADMINISTRADOR |
| gestor | gestor@arka.com | admin123 | GESTOR |
| operador | operador@arka.com | admin123 | OPERADOR |
| usuario | usuario@arka.com | admin123 | USUARIO |

---

## 🚀 Despliegue y Configuración

### ⚙️ Variables de Entorno

```bash
# JWT Configuration
ARKA_JWT_SECRET=ArkaSecretKeyForJWTTokenGenerationAndValidation2024!
ARKA_JWT_EXPIRATION=86400          # 24 horas
ARKA_REFRESH_EXPIRATION=604800     # 7 días

# Database
DB_URL=jdbc:h2:mem:arkasecurity
DB_USERNAME=sa
DB_PASSWORD=password
```

### 🐳 Docker Configuration

```yaml
# docker-compose.yml (ejemplo)
version: '3.8'
services:
  eureka:
    build: ./eureka-server
    ports:
      - "8761:8761"
      
  gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka
    environment:
      - EUREKA_CLIENT_SERVICE_URL=http://eureka:8761/eureka
      
  gestor:
    build: ./arca-gestor-solicitudes
    ports:
      - "8082:8082"
    depends_on:
      - eureka
    environment:
      - ARKA_JWT_SECRET=${ARKA_JWT_SECRET}
```

### 🏃‍♂️ Iniciar Servicios

```bash
# 1. Iniciar Eureka Server
./gradlew :eureka-server:bootRun

# 2. Iniciar API Gateway (en otra terminal)
./gradlew :api-gateway:bootRun

# 3. Iniciar Gestor de Solicitudes (en otra terminal)
./gradlew :arca-gestor-solicitudes:bootRun

# 4. Iniciar Cotizador (opcional)
./gradlew :arca-cotizador:bootRun
```

---

## 🧪 Pruebas de Seguridad

### 📝 Script de Pruebas

Ejecutar el script de pruebas PowerShell:

```powershell
# Ejecutar pruebas completas
.\test-security.ps1
```

### 🔍 Casos de Prueba

1. **❌ Acceso sin autenticación**: Debe ser denegado
2. **✅ Registro de usuario**: Debe generar tokens
3. **✅ Login admin**: Debe permitir acceso completo
4. **✅ Login usuario**: Debe permitir acceso limitado
5. **❌ Usuario regular → Admin**: Debe ser denegado
6. **✅ Refresh token**: Debe generar nuevo access token
7. **✅ Validación de token**: Debe confirmar validez
8. **✅ Logout**: Debe revocar refresh token

### 📊 Endpoints de Monitoreo

```bash
# Health check
curl http://localhost:8080/actuator/health

# Métricas de seguridad
curl http://localhost:8082/actuator/metrics | grep security

# Estado de la aplicación
curl http://localhost:8082/actuator/info
```

---

## 🔧 Troubleshooting

### ❗ Problemas Comunes

#### 1. **Error "JWT signature does not match"**
```yaml
# Verificar configuración del secret
arka:
  security:
    jwt:
      secret: ${ARKA_JWT_SECRET:ArkaSecretKeyDefault}
```

#### 2. **Error "Access denied"**
```java
// Verificar anotaciones de seguridad
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GESTOR')")
```

#### 3. **Error "Token expired"**
```bash
# Usar refresh token para obtener nuevo access token
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "your_refresh_token"}'
```

### 🔍 Logs de Debugging

```yaml
# application.yml
logging:
  level:
    com.arka.security: DEBUG
    org.springframework.security: DEBUG
    org.springframework.cloud.gateway: DEBUG
```

---

## 📈 Métricas y Monitoreo

### 📊 Actuator Endpoints

- `/actuator/health` - Estado del sistema
- `/actuator/metrics` - Métricas de rendimiento
- `/actuator/info` - Información de la aplicación
- `/actuator/env` - Variables de entorno

### 🔐 Métricas de Seguridad

- Número de logins exitosos/fallidos
- Tokens activos/expirados
- Usuarios por rol
- Intentos de acceso no autorizado

---

## 🎯 Conclusión

### ✅ Objetivos Cumplidos

1. **✨ Registro seguro de usuarios**: ✅ Implementado con validación
2. **🔐 Inicio de sesión JWT**: ✅ Implementado con roles
3. **🛡️ Protección de rutas**: ✅ Configurado en Gateway y microservicios
4. **🔄 Refresh tokens**: ✅ Implementado con renovación automática
5. **👑 Gestión de roles**: ✅ Sistema completo de permisos

### 🚀 Características Adicionales

- **🎭 Múltiples tipos de usuarios** con permisos granulares
- **🔒 Autorización a nivel de método** con anotaciones
- **📡 Filtros JWT globales** en API Gateway
- **💾 Persistencia de tokens** con limpieza automática
- **🧪 Scripts de prueba** automatizados
- **📊 Monitoreo y métricas** integrados

### 🔮 Próximos Pasos

1. **🛡️ Integrar con OAuth2/OIDC** para SSO empresarial
2. **📱 Implementar 2FA** para mayor seguridad
3. **📋 Auditoría completa** de acciones de usuarios
4. **🔐 Rotación automática** de secrets JWT
5. **🌐 Rate limiting** por usuario/rol

---

*Documentación de Spring Security para Proyecto ARKA - Sistema completo de autenticación y autorización implementado*
