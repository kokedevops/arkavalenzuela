# 🏗️ Diagramas de Arquitectura - Proyecto ARKA

## 📊 Arquitectura General del Sistema

### Vista de Alto Nivel

```mermaid
graph TB
    subgraph "🌐 Frontend Layer"
        WEB[Web Application]
        MOBILE[Mobile App]
        EXTERNAL[External Clients]
    end
    
    subgraph "🚪 API Gateway Layer"
        GATEWAY[Spring Cloud Gateway :8080]
        JWT_FILTER[JWT Authentication Filter]
        RATE_LIMITER[Rate Limiter]
        LOAD_BALANCER[Load Balancer]
    end
    
    subgraph "🔍 Service Discovery"
        EUREKA[Eureka Server :8761]
        CONFIG[Config Server :9090]
    end
    
    subgraph "🏢 Business Services"
        GESTOR[Gestor Solicitudes :8082]
        COTIZADOR[Cotizador :8083]
        HELLO[Hello World :8084]
    end
    
    subgraph "🔐 Security Layer"
        SECURITY[Security Common Module]
        AUTH_SERVICE[Authentication Service]
        JWT_SERVICE[JWT Service]
    end
    
    subgraph "💾 Data Layer"
        H2_GESTOR[(H2 DB - Gestor)]
        H2_COTIZADOR[(H2 DB - Cotizador)]
        H2_SECURITY[(H2 DB - Security)]
    end
    
    subgraph "📡 Messaging"
        RABBITMQ[RabbitMQ]
        STREAM[Spring Cloud Stream]
    end
    
    subgraph "🔧 Infrastructure"
        DOCKER[Docker Containers]
        K8S[Kubernetes Cluster]
        MONITORING[Actuator + Metrics]
    end
    
    %% Connections
    WEB --> GATEWAY
    MOBILE --> GATEWAY
    EXTERNAL --> GATEWAY
    
    GATEWAY --> JWT_FILTER
    JWT_FILTER --> SECURITY
    
    GATEWAY --> GESTOR
    GATEWAY --> COTIZADOR
    GATEWAY --> HELLO
    
    GESTOR --> EUREKA
    COTIZADOR --> EUREKA
    HELLO --> EUREKA
    
    GESTOR --> CONFIG
    COTIZADOR --> CONFIG
    HELLO --> CONFIG
    
    GESTOR --> H2_GESTOR
    COTIZADOR --> H2_COTIZADOR
    SECURITY --> H2_SECURITY
    
    GESTOR --> RABBITMQ
    COTIZADOR --> RABBITMQ
    
    DOCKER --> K8S
    
    %% Styling
    classDef frontend fill:#e1f5fe
    classDef gateway fill:#f3e5f5
    classDef service fill:#e8f5e8
    classDef data fill:#fff3e0
    classDef security fill:#ffebee
    
    class WEB,MOBILE,EXTERNAL frontend
    class GATEWAY,JWT_FILTER,RATE_LIMITER,LOAD_BALANCER gateway
    class GESTOR,COTIZADOR,HELLO service
    class H2_GESTOR,H2_COTIZADOR,H2_SECURITY data
    class SECURITY,AUTH_SERVICE,JWT_SERVICE security
```

---

## 🏛️ Arquitectura Hexagonal por Microservicio

### Gestor de Solicitudes - Arquitectura Detallada

```mermaid
graph TB
    subgraph "🌐 External World"
        WEB_CLIENT[Web Client]
        API_GATEWAY[API Gateway]
        RABBITMQ[RabbitMQ]
        EXTERNAL_API[External APIs]
    end
    
    subgraph "📡 Infrastructure Layer (Adapters)"
        subgraph "🔌 Input Adapters"
            REST_CONTROLLER[REST Controllers]
            MESSAGE_HANDLER[Message Handlers]
            SCHEDULED_TASKS[Scheduled Tasks]
        end
        
        subgraph "🔌 Output Adapters"
            JPA_REPO[R2DBC Repositories]
            MESSAGE_PUBLISHER[Message Publishers]
            HTTP_CLIENT[HTTP Clients]
            EMAIL_SERVICE[Email Service]
        end
        
        subgraph "⚙️ Configuration"
            SECURITY_CONFIG[Security Config]
            DATABASE_CONFIG[Database Config]
            MESSAGING_CONFIG[Messaging Config]
        end
    end
    
    subgraph "🎯 Application Layer"
        subgraph "📋 Use Cases"
            CREATE_SOLICITUD[Create Solicitud Use Case]
            UPDATE_SOLICITUD[Update Solicitud Use Case]
            GENERATE_COTIZACION[Generate Cotizacion Use Case]
            TRACK_ENVIO[Track Envio Use Case]
        end
        
        subgraph "🎭 Application Services"
            SOLICITUD_APP_SERVICE[Solicitud Application Service]
            COTIZACION_APP_SERVICE[Cotizacion Application Service]
            NOTIFICATION_SERVICE[Notification Service]
        end
    end
    
    subgraph "🏛️ Domain Layer (Core)"
        subgraph "🎯 Domain Model"
            SOLICITUD[Solicitud Entity]
            COTIZACION[Cotizacion Entity]
            ENVIO[Envio Entity]
            CLIENTE[Cliente Value Object]
            DIRECCION[Direccion Value Object]
        end
        
        subgraph "🔍 Domain Services"
            COTIZACION_DOMAIN_SERVICE[Cotizacion Domain Service]
            VALIDACION_SERVICE[Validacion Service]
            CALCULO_SERVICE[Calculo Service]
        end
        
        subgraph "🚪 Ports (Interfaces)"
            SOLICITUD_PORT[Solicitud Repository Port]
            COTIZACION_PORT[Cotizacion Repository Port]
            NOTIFICATION_PORT[Notification Port]
            PAYMENT_PORT[Payment Port]
        end
    end
    
    %% External to Input Adapters
    WEB_CLIENT --> REST_CONTROLLER
    API_GATEWAY --> REST_CONTROLLER
    RABBITMQ --> MESSAGE_HANDLER
    
    %% Input Adapters to Application Layer
    REST_CONTROLLER --> CREATE_SOLICITUD
    REST_CONTROLLER --> UPDATE_SOLICITUD
    REST_CONTROLLER --> GENERATE_COTIZACION
    MESSAGE_HANDLER --> TRACK_ENVIO
    
    %% Application Layer to Domain
    CREATE_SOLICITUD --> SOLICITUD_APP_SERVICE
    UPDATE_SOLICITUD --> SOLICITUD_APP_SERVICE
    GENERATE_COTIZACION --> COTIZACION_APP_SERVICE
    
    SOLICITUD_APP_SERVICE --> SOLICITUD
    COTIZACION_APP_SERVICE --> COTIZACION
    COTIZACION_APP_SERVICE --> COTIZACION_DOMAIN_SERVICE
    
    %% Domain to Ports
    SOLICITUD --> SOLICITUD_PORT
    COTIZACION --> COTIZACION_PORT
    NOTIFICATION_SERVICE --> NOTIFICATION_PORT
    
    %% Ports to Output Adapters
    SOLICITUD_PORT -.-> JPA_REPO
    COTIZACION_PORT -.-> JPA_REPO
    NOTIFICATION_PORT -.-> MESSAGE_PUBLISHER
    NOTIFICATION_PORT -.-> EMAIL_SERVICE
    PAYMENT_PORT -.-> HTTP_CLIENT
    
    %% Output Adapters to External
    JPA_REPO --> H2_DB[(H2 Database)]
    MESSAGE_PUBLISHER --> RABBITMQ
    HTTP_CLIENT --> EXTERNAL_API
    
    %% Styling
    classDef domain fill:#e8f5e8,stroke:#4caf50,stroke-width:3px
    classDef application fill:#fff3e0,stroke:#ff9800,stroke-width:2px
    classDef infrastructure fill:#e1f5fe,stroke:#2196f3,stroke-width:2px
    classDef external fill:#f3e5f5,stroke:#9c27b0,stroke-width:2px
    
    class SOLICITUD,COTIZACION,ENVIO,CLIENTE,DIRECCION,COTIZACION_DOMAIN_SERVICE,VALIDACION_SERVICE,CALCULO_SERVICE,SOLICITUD_PORT,COTIZACION_PORT,NOTIFICATION_PORT,PAYMENT_PORT domain
    
    class CREATE_SOLICITUD,UPDATE_SOLICITUD,GENERATE_COTIZACION,TRACK_ENVIO,SOLICITUD_APP_SERVICE,COTIZACION_APP_SERVICE,NOTIFICATION_SERVICE application
    
    class REST_CONTROLLER,MESSAGE_HANDLER,SCHEDULED_TASKS,JPA_REPO,MESSAGE_PUBLISHER,HTTP_CLIENT,EMAIL_SERVICE,SECURITY_CONFIG,DATABASE_CONFIG,MESSAGING_CONFIG infrastructure
    
    class WEB_CLIENT,API_GATEWAY,RABBITMQ,EXTERNAL_API external
```

---

## 🔐 Arquitectura de Seguridad

### JWT Authentication Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant AG as API Gateway
    participant AS as Auth Service
    participant JS as JWT Service
    participant DB as Database
    participant MS as Microservice
    
    Note over C,MS: Authentication Process
    
    C->>AG: POST /auth/login {username, password}
    AG->>AS: Forward login request
    AS->>DB: Validate credentials
    DB-->>AS: User found
    AS->>JS: Generate JWT & Refresh Token
    JS-->>AS: Tokens created
    AS->>DB: Store refresh token
    AS-->>AG: {accessToken, refreshToken, expiresIn}
    AG-->>C: Authentication response
    
    Note over C,MS: Protected Request
    
    C->>AG: GET /solicitudes [Authorization: Bearer JWT]
    AG->>AG: Extract JWT from header
    AG->>JS: Validate JWT
    JS-->>AG: JWT valid + user info
    AG->>MS: Forward request + user headers
    MS->>MS: Process business logic
    MS-->>AG: Business response
    AG-->>C: Final response
    
    Note over C,MS: Token Refresh
    
    C->>AG: POST /auth/refresh {refreshToken}
    AG->>AS: Forward refresh request
    AS->>DB: Validate refresh token
    DB-->>AS: Refresh token valid
    AS->>JS: Generate new JWT
    JS-->>AS: New JWT created
    AS-->>AG: {accessToken, expiresIn}
    AG-->>C: New token response
```

### Security Filter Chain

```mermaid
graph TB
    subgraph "🚪 API Gateway Security Chain"
        REQUEST[Incoming Request]
        CORS[CORS Filter]
        JWT_FILTER[JWT Authentication Filter]
        AUTHZ[Authorization Filter]
        RATE_LIMIT[Rate Limiting Filter]
    end
    
    subgraph "🔐 JWT Processing"
        EXTRACT[Extract Token from Header]
        VALIDATE[Validate Token Signature]
        CLAIMS[Extract Claims]
        USER_CONTEXT[Set User Context]
    end
    
    subgraph "🎭 Authorization"
        ROLE_CHECK[Check User Roles]
        PERMISSION_CHECK[Check Permissions]
        RESOURCE_ACCESS[Resource Access Control]
    end
    
    subgraph "🏢 Microservice Security"
        MS_SECURITY[Microservice Security Config]
        METHOD_SECURITY[Method Level Security]
        BUSINESS_LOGIC[Business Logic]
    end
    
    REQUEST --> CORS
    CORS --> JWT_FILTER
    JWT_FILTER --> EXTRACT
    EXTRACT --> VALIDATE
    VALIDATE --> CLAIMS
    CLAIMS --> USER_CONTEXT
    USER_CONTEXT --> AUTHZ
    AUTHZ --> ROLE_CHECK
    ROLE_CHECK --> PERMISSION_CHECK
    PERMISSION_CHECK --> RESOURCE_ACCESS
    RESOURCE_ACCESS --> RATE_LIMIT
    RATE_LIMIT --> MS_SECURITY
    MS_SECURITY --> METHOD_SECURITY
    METHOD_SECURITY --> BUSINESS_LOGIC
    
    %% Error Paths
    VALIDATE -.->|Invalid| ERROR_RESPONSE[401 Unauthorized]
    ROLE_CHECK -.->|Insufficient| ERROR_RESPONSE
    PERMISSION_CHECK -.->|Denied| FORBIDDEN[403 Forbidden]
    RATE_LIMIT -.->|Exceeded| RATE_EXCEEDED[429 Too Many Requests]
```

---

## 📊 Diagrama de Flujo de Datos

### Flujo Completo de Creación de Solicitud

```mermaid
flowchart TD
    START([Cliente inicia solicitud])
    
    subgraph "🌐 Frontend"
        FORM[Llenar formulario de solicitud]
        VALIDATE_FORM[Validar datos en frontend]
    end
    
    subgraph "🚪 API Gateway"
        AUTH_CHECK[Verificar JWT]
        ROUTE[Enrutar a Gestor Solicitudes]
    end
    
    subgraph "📊 Gestor Solicitudes"
        RECEIVE[Recibir solicitud]
        VALIDATE_DATA[Validar datos de dominio]
        CREATE_ENTITY[Crear entidad Solicitud]
        SAVE_DB[Guardar en base de datos]
        CALL_COTIZADOR[Llamar servicio Cotizador]
    end
    
    subgraph "💰 Cotizador"
        CALC_PRICE[Calcular precio base]
        APPLY_DISCOUNTS[Aplicar descuentos]
        CHECK_RULES[Verificar reglas de negocio]
        CREATE_COTIZACION[Crear cotización]
        RETURN_PRICE[Retornar precio]
    end
    
    subgraph "📡 Notificaciones"
        SEND_EMAIL[Enviar email confirmación]
        PUBLISH_EVENT[Publicar evento en RabbitMQ]
        LOG_ACTIVITY[Registrar actividad]
    end
    
    subgraph "📱 Response"
        BUILD_RESPONSE[Construir respuesta]
        RETURN_CLIENT[Retornar al cliente]
    end
    
    START --> FORM
    FORM --> VALIDATE_FORM
    VALIDATE_FORM -->|Válido| AUTH_CHECK
    VALIDATE_FORM -->|Inválido| FORM_ERROR[Error en formulario]
    
    AUTH_CHECK -->|Autenticado| ROUTE
    AUTH_CHECK -->|No autenticado| AUTH_ERROR[401 - No autorizado]
    
    ROUTE --> RECEIVE
    RECEIVE --> VALIDATE_DATA
    VALIDATE_DATA -->|Válido| CREATE_ENTITY
    VALIDATE_DATA -->|Inválido| VALIDATION_ERROR[400 - Datos inválidos]
    
    CREATE_ENTITY --> SAVE_DB
    SAVE_DB --> CALL_COTIZADOR
    
    CALL_COTIZADOR --> CALC_PRICE
    CALC_PRICE --> APPLY_DISCOUNTS
    APPLY_DISCOUNTS --> CHECK_RULES
    CHECK_RULES --> CREATE_COTIZACION
    CREATE_COTIZACION --> RETURN_PRICE
    
    RETURN_PRICE --> SEND_EMAIL
    SEND_EMAIL --> PUBLISH_EVENT
    PUBLISH_EVENT --> LOG_ACTIVITY
    LOG_ACTIVITY --> BUILD_RESPONSE
    BUILD_RESPONSE --> RETURN_CLIENT
    
    RETURN_CLIENT --> END([Solicitud creada exitosamente])
    
    %% Error handling
    FORM_ERROR --> END_ERROR([Error: Formulario inválido])
    AUTH_ERROR --> END_ERROR
    VALIDATION_ERROR --> END_ERROR
    
    %% Styling
    classDef process fill:#e8f5e8
    classDef decision fill:#fff3e0
    classDef error fill:#ffebee
    classDef success fill:#e1f5fe
    
    class VALIDATE_FORM,AUTH_CHECK,VALIDATE_DATA,CHECK_RULES decision
    class FORM_ERROR,AUTH_ERROR,VALIDATION_ERROR,END_ERROR error
    class END success
```

---

## 🐳 Arquitectura de Contenedores

### Docker Compose Structure

```mermaid
graph TB
    subgraph "🐳 Docker Network: arka-network"
        subgraph "Service Discovery"
            EUREKA_CONTAINER[eureka-server:8761]
            CONFIG_CONTAINER[config-server:9090]
        end
        
        subgraph "Gateway Layer"
            GATEWAY_CONTAINER[api-gateway:8080]
        end
        
        subgraph "Business Services"
            GESTOR_CONTAINER[gestor-solicitudes:8082]
            COTIZADOR_CONTAINER[cotizador:8083]
            HELLO_CONTAINER[hello-world:8084]
        end
        
        subgraph "Data Layer"
            H2_VOLUME[(H2 Data Volume)]
        end
        
        subgraph "Message Broker"
            RABBITMQ_CONTAINER[rabbitmq:5672/15672]
        end
        
        subgraph "Monitoring"
            PROMETHEUS[prometheus:9090]
            GRAFANA[grafana:3000]
        end
    end
    
    subgraph "🌐 External Access"
        HOST_NETWORK[Host Network]
        LOAD_BALANCER[Load Balancer]
    end
    
    %% Service dependencies
    GATEWAY_CONTAINER --> EUREKA_CONTAINER
    GESTOR_CONTAINER --> EUREKA_CONTAINER
    COTIZADOR_CONTAINER --> EUREKA_CONTAINER
    HELLO_CONTAINER --> EUREKA_CONTAINER
    
    GESTOR_CONTAINER --> CONFIG_CONTAINER
    COTIZADOR_CONTAINER --> CONFIG_CONTAINER
    
    GESTOR_CONTAINER --> H2_VOLUME
    COTIZADOR_CONTAINER --> H2_VOLUME
    
    GESTOR_CONTAINER --> RABBITMQ_CONTAINER
    COTIZADOR_CONTAINER --> RABBITMQ_CONTAINER
    
    %% External access
    HOST_NETWORK --> LOAD_BALANCER
    LOAD_BALANCER --> GATEWAY_CONTAINER
    HOST_NETWORK --> GRAFANA
    HOST_NETWORK --> RABBITMQ_CONTAINER
    
    %% Monitoring
    PROMETHEUS --> GESTOR_CONTAINER
    PROMETHEUS --> COTIZADOR_CONTAINER
    PROMETHEUS --> GATEWAY_CONTAINER
    GRAFANA --> PROMETHEUS
```

---

## ☁️ Arquitectura AWS (Propuesta)

### Infrastructure Diagram

```mermaid
graph TB
    subgraph "🌐 Internet"
        USERS[Users/Clients]
        EXTERNAL_APIS[External APIs]
    end
    
    subgraph "🛡️ AWS Cloud - VPC"
        subgraph "Public Subnet - AZ1"
            ALB[Application Load Balancer]
            NAT_GW1[NAT Gateway]
        end
        
        subgraph "Public Subnet - AZ2"
            NAT_GW2[NAT Gateway]
        end
        
        subgraph "Private Subnet - AZ1"
            ECS_1[ECS Tasks - Services]
            LAMBDA_1[Lambda Functions]
        end
        
        subgraph "Private Subnet - AZ2"
            ECS_2[ECS Tasks - Services]
            LAMBDA_2[Lambda Functions]
        end
        
        subgraph "Database Subnet Group"
            RDS_PRIMARY[(RDS PostgreSQL - Primary)]
            RDS_REPLICA[(RDS PostgreSQL - Read Replica)]
        end
        
        subgraph "Storage"
            S3_BUCKET[S3 Bucket - Documents]
            EFS[EFS - Shared Storage]
        end
        
        subgraph "Messaging & Caching"
            SQS[SQS Queues]
            SNS[SNS Topics]
            ELASTICACHE[ElastiCache Redis]
        end
        
        subgraph "Security & Monitoring"
            WAF[AWS WAF]
            CLOUDWATCH[CloudWatch]
            IAM[IAM Roles/Policies]
        end
    end
    
    subgraph "🔧 CI/CD Pipeline"
        CODECOMMIT[CodeCommit]
        CODEBUILD[CodeBuild]
        ECR[ECR Repository]
        CODEPIPELINE[CodePipeline]
    end
    
    %% User traffic flow
    USERS --> WAF
    WAF --> ALB
    ALB --> ECS_1
    ALB --> ECS_2
    
    %% Service connections
    ECS_1 --> RDS_PRIMARY
    ECS_2 --> RDS_REPLICA
    ECS_1 --> S3_BUCKET
    ECS_2 --> S3_BUCKET
    ECS_1 --> ELASTICACHE
    ECS_2 --> ELASTICACHE
    
    %% Messaging
    ECS_1 --> SQS
    ECS_2 --> SQS
    LAMBDA_1 --> SQS
    LAMBDA_2 --> SQS
    
    %% External APIs
    ECS_1 --> EXTERNAL_APIS
    ECS_2 --> EXTERNAL_APIS
    
    %% Monitoring
    ECS_1 --> CLOUDWATCH
    ECS_2 --> CLOUDWATCH
    LAMBDA_1 --> CLOUDWATCH
    LAMBDA_2 --> CLOUDWATCH
    
    %% CI/CD
    CODECOMMIT --> CODEPIPELINE
    CODEPIPELINE --> CODEBUILD
    CODEBUILD --> ECR
    ECR --> ECS_1
    ECR --> ECS_2
    
    %% High Availability
    RDS_PRIMARY -.-> RDS_REPLICA
    ECS_1 -.-> ECS_2
    
    %% Internet Gateway
    ALB -.-> INTERNET_GW[Internet Gateway]
    NAT_GW1 -.-> INTERNET_GW
    NAT_GW2 -.-> INTERNET_GW
```

### AWS Services Mapping

| Componente ARKA | Servicio AWS | Configuración |
|------------------|--------------|---------------|
| **API Gateway** | Application Load Balancer + WAF | Multi-AZ, SSL/TLS, Rate limiting |
| **Microservicios** | ECS Fargate + ECR | Auto-scaling, Service discovery |
| **Base de Datos** | RDS PostgreSQL | Multi-AZ, Read replicas, Backup |
| **Configuración** | Systems Manager Parameter Store | Encrypted parameters |
| **Archivos** | S3 | Versioning, Encryption, Lifecycle |
| **Caché** | ElastiCache Redis | Cluster mode, Multi-AZ |
| **Mensajería** | SQS + SNS | Dead letter queues, Encryption |
| **Funciones** | Lambda | Serverless processing |
| **Monitoreo** | CloudWatch + X-Ray | Logs, Metrics, Tracing |
| **Seguridad** | IAM + Secrets Manager | Least privilege, Rotation |

---

## 📈 Diagramas de Monitoreo y Observabilidad

### Health Check Flow

```mermaid
graph LR
    subgraph "🏥 Health Monitoring"
        ACTUATOR[Spring Actuator]
        HEALTH_ENDPOINT[/actuator/health]
        METRICS_ENDPOINT[/actuator/metrics]
        INFO_ENDPOINT[/actuator/info]
    end
    
    subgraph "📊 Monitoring Tools"
        PROMETHEUS[Prometheus]
        GRAFANA[Grafana]
        ALERT_MANAGER[Alert Manager]
    end
    
    subgraph "📱 Notifications"
        SLACK[Slack]
        EMAIL[Email]
        PAGERDUTY[PagerDuty]
    end
    
    ACTUATOR --> HEALTH_ENDPOINT
    ACTUATOR --> METRICS_ENDPOINT
    ACTUATOR --> INFO_ENDPOINT
    
    PROMETHEUS --> HEALTH_ENDPOINT
    PROMETHEUS --> METRICS_ENDPOINT
    
    GRAFANA --> PROMETHEUS
    ALERT_MANAGER --> PROMETHEUS
    
    ALERT_MANAGER --> SLACK
    ALERT_MANAGER --> EMAIL
    ALERT_MANAGER --> PAGERDUTY
```

---

## 📝 Conclusiones de Arquitectura

### ✅ Fortalezas del Diseño

1. **Separación de Responsabilidades**: Clara separación entre capas y dominios
2. **Escalabilidad**: Arquitectura preparada para crecimiento horizontal
3. **Mantenibilidad**: Código organizado y principios SOLID aplicados
4. **Testabilidad**: Interfaces que permiten testing unitario e integración
5. **Flexibilidad**: Adaptadores intercambiables sin afectar el dominio

### 🎯 Recomendaciones de Mejora

1. **Implementar Circuit Breaker** más robusto con Hystrix Dashboard
2. **Agregar Service Mesh** (Istio) para comunicación inter-servicios
3. **Implementar CQRS** para casos de uso complejos
4. **Mejorar observabilidad** con distributed tracing
5. **Automatizar deployment** con GitOps (ArgoCD)

### 📊 Métricas de Calidad Arquitectural

- **Acoplamiento**: Bajo ✅
- **Cohesión**: Alta ✅  
- **Complejidad**: Moderada ✅
- **Mantenibilidad**: Alta ✅
- **Testabilidad**: Alta ✅
- **Escalabilidad**: Alta ✅
