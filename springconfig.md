# 🏗️ CREACIÓN DEL PROYECTO ARKA CONFIG SERVER SEPARADO

## 📋 INSTRUCCIONES PARA CREAR EL PROYECTO INDEPENDIENTE

### 🎯 **PASO 1: Crear la estructura del proyecto**

```powershell
# Navegar al directorio padre
cd c:\Users\valen\arka\

# Crear nuevo proyecto Config Server
mkdir arka-config-server
cd arka-config-server

# Crear estructura de directorios
mkdir src\main\java\com\arka\configserver
mkdir src\main\resources
mkdir src\test\java\com\arka\configserver
mkdir config-repository
mkdir scripts
```

### 🔧 **PASO 2: Archivos del proyecto**

#### **build.gradle**
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.3'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.arka'
version = '1.0.0'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom 'org.springframework.cloud:spring-cloud-dependencies:2023.0.1'
    }
}

dependencies {
    // Spring Cloud Config Server
    implementation 'org.springframework.cloud:spring-cloud-config-server'
    
    // Service Discovery
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    
    // Security
    implementation 'org.springframework.boot:spring-boot-starter-security'
    
    // HashiCorp Vault integration
    implementation 'org.springframework.cloud:spring-cloud-starter-vault-config'
    
    // Spring Cloud Bus for dynamic refresh
    implementation 'org.springframework.cloud:spring-cloud-starter-bus-amqp'
    
    // Monitoring and Management
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Web (for REST endpoints)
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.cloud:spring-cloud-starter-contract-verifier'
}

tasks.named('test') {
    useJUnitPlatform()
}

jar {
    enabled = false
}

bootJar {
    enabled = true
    archiveFileName = 'arka-config-server.jar'
}
```

#### **settings.gradle**
```gradle
rootProject.name = 'arka-config-server'
```

#### **gradle.properties**
```properties
# Gradle properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
org.gradle.parallel=true
org.gradle.caching=true

# Application properties
app.name=Arka Config Server
app.version=1.0.0
app.description=Centralized configuration server for Arka microservices
```

### 🚀 **PASO 3: Aplicación principal**

#### **src/main/java/com/arka/configserver/ArkaConfigServerApplication.java**
```java
package com.arka.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Arka Config Server Application
 * 
 * Servidor de configuración centralizada para todos los microservicios de Arka.
 * Proporciona configuración por entorno (dev, test, prod) integrada con HashiCorp Vault
 * para gestión segura de secretos.
 * 
 * @author Arka Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
public class ArkaConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArkaConfigServerApplication.class, args);
    }
}
```

#### **src/main/java/com/arka/configserver/config/SecurityConfig.java**
```java
package com.arka.configserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {});
        
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails configUser = User.builder()
            .username("config-admin")
            .password(passwordEncoder().encode("arka-config-2025"))
            .roles("ADMIN")
            .build();

        UserDetails clientUser = User.builder()
            .username("config-client")
            .password(passwordEncoder().encode("arka-client-2025"))
            .roles("CLIENT")
            .build();

        return new InMemoryUserDetailsManager(configUser, clientUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### ⚙️ **PASO 4: Configuración del servidor**

#### **src/main/resources/application.yml**
```yml
server:
  port: 8888

spring:
  application:
    name: arka-config-server
  
  profiles:
    active: native,vault
  
  cloud:
    config:
      server:
        native:
          search-locations: file:///./config-repository/
          order: 1
        vault:
          host: ${VAULT_HOST:localhost}
          port: ${VAULT_PORT:8200}
          scheme: ${VAULT_SCHEME:http}
          authentication: TOKEN
          token: ${VAULT_TOKEN:hvs.6j9pKFSKSNV4jklm8ql2HP1v}
          kv-version: 2
          backend: secret
          default-key: config
          profile-separator: '/'
          order: 2
        git:
          uri: ${CONFIG_GIT_URI:https://github.com/kokedevops/arka-config-repo.git}
          default-label: main
          search-paths: '{application}'
          clone-on-start: false
          order: 3
          timeout: 10
    bus:
      enabled: true
      refresh:
        enabled: true
      env:
        enabled: true

  # RabbitMQ configuration for Spring Cloud Bus
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USER:admin}
    password: ${RABBITMQ_PASSWORD:admin123}
    virtual-host: /

# Eureka Client Configuration
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}

# Management endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,refresh,busrefresh,env,configprops,metrics
  endpoint:
    health:
      show-details: always
    refresh:
      enabled: true
    busrefresh:
      enabled: true
  security:
    enabled: false

# Security credentials
security:
  user:
    name: config-admin
    password: arka-config-2025

# Info endpoint
info:
  app:
    name: Arka Config Server
    description: Centralized configuration server with Vault integration
    version: 1.0.0
    java:
      version: ${java.version}
    profiles: ${spring.profiles.active}

# Logging
logging:
  level:
    com.arka: DEBUG
    org.springframework.cloud.config: DEBUG
    org.springframework.cloud.vault: DEBUG
    org.springframework.cloud.bus: DEBUG
    org.springframework.security: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/arka-config-server.log
```

### 📁 **PASO 5: Configuraciones por servicio y entorno**

Crear la siguiente estructura en `config-repository/`:

```
config-repository/
├── arka-valenzuela-dev.yml
├── arka-valenzuela-test.yml
├── arka-valenzuela-prod.yml
├── arca-cotizador-dev.yml
├── arca-cotizador-test.yml
├── arca-cotizador-prod.yml
├── arca-gestor-solicitudes-dev.yml
├── arca-gestor-solicitudes-test.yml
├── arca-gestor-solicitudes-prod.yml
├── api-gateway-dev.yml
├── api-gateway-test.yml
├── api-gateway-prod.yml
├── eureka-server-dev.yml
├── eureka-server-prod.yml
└── shared-common.yml
```

### 🚀 **PASO 6: Scripts de automatización**

#### **scripts/start-config-server.bat**
```bat
@echo off
echo =========================================
echo INICIANDO ARKA CONFIG SERVER
echo =========================================
echo.

REM Configurar variables de entorno
set SPRING_PROFILES_ACTIVE=native,vault
set VAULT_TOKEN=hvs.6j9pKFSKSNV4jklm8ql2HP1v
set VAULT_HOST=localhost
set VAULT_PORT=8200

echo Configuración:
echo • Profiles: %SPRING_PROFILES_ACTIVE%
echo • Vault Token: %VAULT_TOKEN%
echo • Vault Host: %VAULT_HOST%:%VAULT_PORT%
echo.

echo [1/3] Verificando dependencias...
if not exist "config-repository\" (
    echo ❌ ERROR: Directorio config-repository no encontrado
    echo Ejecutar: scripts\setup-config-repository.bat
    pause
    exit /b 1
)

echo [2/3] Compilando proyecto...
call gradlew clean build

echo [3/3] Iniciando Config Server...
call gradlew bootRun

pause
```

#### **scripts/setup-config-repository.bat**
```bat
@echo off
echo =========================================
echo CONFIGURANDO REPOSITORIO DE CONFIGURACIONES
echo =========================================
echo.

mkdir config-repository
cd config-repository

echo Creando archivo .gitignore...
echo # Logs > .gitignore
echo *.log >> .gitignore
echo logs/ >> .gitignore
echo.
echo # Temporary files >> .gitignore
echo *.tmp >> .gitignore
echo temp/ >> .gitignore

echo Inicializando repositorio Git...
git init
git add .gitignore
git commit -m "Initial config repository setup"

echo.
echo ✅ Repositorio de configuraciones creado
echo 📁 Ubicación: %CD%
echo.
echo 📝 Próximos pasos:
echo 1. Crear archivos <servicio>-<entorno>.yml
echo 2. Configurar secrets en Vault
echo 3. Iniciar Config Server
echo.
pause

cd ..
```

#### **scripts/test-config-endpoints.bat**
```bat
@echo off
echo =========================================
echo TESTING CONFIG SERVER ENDPOINTS
echo =========================================
echo.

set CONFIG_USER=config-client
set CONFIG_PASS=arka-client-2025
set CONFIG_URL=http://localhost:8888

echo Testing Config Server health...
curl -s %CONFIG_URL%/actuator/health

echo.
echo Testing service configurations...
echo.

echo • arka-valenzuela (dev):
curl -u %CONFIG_USER%:%CONFIG_PASS% -s %CONFIG_URL%/arka-valenzuela/dev | head -10

echo.
echo • arca-cotizador (prod):
curl -u %CONFIG_USER%:%CONFIG_PASS% -s %CONFIG_URL%/arca-cotizador/prod | head -10

echo.
echo • api-gateway (test):
curl -u %CONFIG_USER%:%CONFIG_PASS% -s %CONFIG_URL%/api-gateway/test | head -10

echo.
echo Testing refresh endpoint...
curl -X POST -u %CONFIG_USER%:%CONFIG_PASS% %CONFIG_URL%/actuator/busrefresh

echo.
echo ✅ Tests completados
pause
```

### 🔧 **PASO 7: Ejemplos de configuración**

#### **config-repository/arka-valenzuela-dev.yml**
```yml
# ARKA VALENZUELA - DESARROLLO
server:
  port: 8086

spring:
  application:
    name: arka-valenzuela
  
  datasource:
    url: jdbc:mysql://localhost:3306/arkavalenzuela_dev
    username: ${db.username:dev_user}
    password: ${db.password:dev_pass}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# Configuración específica para desarrollo
app:
  environment: development
  debug-mode: true
  business:
    max-orders-per-customer: 50
    default-tax-rate: 0.19
  
  security:
    jwt:
      secret: ${jwt.secret:dev-jwt-secret}
      expiration: 86400000  # 24 hours

# Resilience4j para desarrollo (más permisivo)
resilience4j:
  circuitbreaker:
    instances:
      default:
        failure-rate-threshold: 60
        wait-duration-in-open-state: 10s
        sliding-window-size: 5

# Logging para desarrollo
logging:
  level:
    com.arka: DEBUG
    org.springframework.cloud.config: DEBUG
```

#### **config-repository/arka-valenzuela-prod.yml**
```yml
# ARKA VALENZUELA - PRODUCCIÓN
server:
  port: 8086

spring:
  application:
    name: arka-valenzuela
  
  datasource:
    url: jdbc:mysql://prod-db.arka.com:3306/arkavalenzuela_prod
    username: ${db.username}
    password: ${db.password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

# Configuración específica para producción
app:
  environment: production
  debug-mode: false
  business:
    max-orders-per-customer: 1000
    default-tax-rate: 0.19
  
  security:
    jwt:
      secret: ${jwt.secret}
      expiration: 3600000  # 1 hour

# Resilience4j para producción (más restrictivo)
resilience4j:
  circuitbreaker:
    instances:
      default:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        sliding-window-size: 20

# Logging para producción
logging:
  level:
    com.arka: INFO
    org.springframework.cloud.config: WARN
```

### 🔐 **PASO 8: Configuración de Vault**

#### **scripts/setup-vault-secrets.bat**
```bat
@echo off
echo =========================================
echo CONFIGURANDO SECRETOS EN VAULT
echo =========================================
echo.

set VAULT_ADDR=http://localhost:8200
set VAULT_TOKEN=hvs.6j9pKFSKSNV4jklm8ql2HP1v

echo Configurando secretos para cada servicio y entorno...
echo.

REM Arka Valenzuela
vault kv put secret/arka-valenzuela/dev db.username="dev_user" db.password="dev_pass" jwt.secret="dev-jwt-secret-12345"
vault kv put secret/arka-valenzuela/prod db.username="prod_user" db.password="prod_super_secure_pass" jwt.secret="prod-jwt-secret-67890"

REM Arca Cotizador
vault kv put secret/arca-cotizador/dev external.api.key="dev-cotizador-key" external.api.url="https://api-dev.cotizador.com"
vault kv put secret/arca-cotizador/prod external.api.key="prod-cotizador-key" external.api.url="https://api.cotizador.com"

REM Arca Gestor Solicitudes
vault kv put secret/arca-gestor-solicitudes/dev sms.api.key="dev-sms-key" email.api.key="dev-email-key"
vault kv put secret/arca-gestor-solicitudes/prod sms.api.key="prod-sms-key" email.api.key="prod-email-key"

REM API Gateway
vault kv put secret/api-gateway/dev gateway.security.key="dev-gateway-key" rate.limit.requests="200"
vault kv put secret/api-gateway/prod gateway.security.key="prod-gateway-key" rate.limit.requests="100"

echo.
echo ✅ Secretos configurados en Vault
echo.
echo 🔍 Verificar con:
echo vault kv get secret/arka-valenzuela/dev
echo vault kv get secret/arca-cotizador/prod
echo.
pause
```

### 📚 **PASO 9: Documentación**

#### **README.md**
```markdown
# 🏗️ Arka Config Server

Servidor de configuración centralizada para todos los microservicios del ecosistema Arka.

## 🚀 Inicio Rápido

```bash
# 1. Clonar el proyecto
git clone <repo-url>
cd arka-config-server

# 2. Configurar repositorio de configuraciones
./scripts/setup-config-repository.bat

# 3. Iniciar Vault y configurar secretos
vault server -dev
./scripts/setup-vault-secrets.bat

# 4. Iniciar Config Server
./scripts/start-config-server.bat
```

## 📁 Estructura de Configuraciones

- `<servicio>-dev.yml` - Entorno de desarrollo
- `<servicio>-test.yml` - Entorno de testing
- `<servicio>-prod.yml` - Entorno de producción

## 🔗 Endpoints

- Config Server: http://localhost:8888
- Health Check: http://localhost:8888/actuator/health
- Configuración: http://localhost:8888/{service}/{profile}

## 🔐 Credenciales

- Admin: `config-admin` / `arka-config-2025`
- Client: `config-client` / `arka-client-2025`
```

## 🎯 **RESUMEN DE PASOS**

1. **Crear directorio**: `mkdir c:\Users\valen\arka\arka-config-server`
2. **Copiar archivos**: Usar las plantillas proporcionadas arriba
3. **Configurar Vault**: Ejecutar script de setup de secretos
4. **Iniciar servidor**: `./scripts/start-config-server.bat`
5. **Testear**: `./scripts/test-config-endpoints.bat`

Este proyecto independiente te permitirá gestionar todas las configuraciones de manera centralizada y segura! 🎉


 Solución paso a paso para Windows
1. Descargar Vault
Ve a la página oficial de Vault:
👉 https://developer.hashicorp.com/vault/downloads

Descarga la versión de Windows adecuada para tu arquitectura (amd64 usualmente).

2. Extraer y mover Vault
Extrae el .zip descargado.

Mueve el ejecutable vault.exe a una carpeta, por ejemplo:
C:\Program Files\Vault\ o C:\HashiCorp\Vault\

3. Agregar la ruta al PATH
Abre el menú inicio → escribe variables de entorno → clic en "Editar las variables de entorno del sistema".

En la ventana que se abre, clic en "Variables de entorno..."

En "Variables del sistema", busca Path y haz clic en "Editar..."

Agrega la ruta donde pusiste vault.exe, por ejemplo:
C:\Program Files\Vault\
