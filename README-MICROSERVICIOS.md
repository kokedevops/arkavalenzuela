# Proyecto Arka Valenzuela - Microservicios

Este proyecto ha sido restructurado como una aplicación multimódulo que incluye:

## Módulos

### 1. **Módulo Principal (arkavalenzuela)**
- **Puerto**: 8080
- **Descripción**: Aplicación principal con arquitectura hexagonal
- **Tecnología**: Spring Boot MVC + JPA
- **Base de datos**: MySQL

### 2. **Arca Cotizador**
- **Puerto**: 8081
- **Descripción**: Microservicio para generar cotizaciones
- **Tecnología**: Spring Boot WebFlux + R2DBC
- **Base de datos**: H2 (desarrollo)

### 3. **Arca Gestor de Solicitudes**
- **Puerto**: 8082
- **Descripción**: Microservicio para gestionar solicitudes a proveedores
- **Tecnología**: Spring Boot WebFlux + R2DBC
- **Base de datos**: H2 (desarrollo)

## Estructura del Proyecto

```
arkajvalenzuela/
├── src/main/java/                    # Módulo principal
├── arca-cotizador/                   # Microservicio cotizador
│   └── src/main/java/
│       └── com/arka/cotizador/
├── arca-gestor-solicitudes/          # Microservicio gestor
│   └── src/main/java/
│       └── com/arka/gestorsolicitudes/
├── build.gradle                      # Configuración padre
└── settings.gradle                   # Configuración multimódulo
```

## Cómo ejecutar

### Compilar todo el proyecto
```bash
./gradlew build
```

### Ejecutar módulo principal
```bash
./gradlew bootRun
```

### Ejecutar Arca Cotizador
```bash
./gradlew :arca-cotizador:bootRun
```

### Ejecutar Arca Gestor de Solicitudes
```bash
./gradlew :arca-gestor-solicitudes:bootRun
```

## APIs Disponibles

### Arca Cotizador (Puerto 8081)
- **POST** `/api/cotizaciones` - Generar cotización
- **GET** `/api/cotizaciones/{id}` - Obtener cotización
- **GET** `/api/cotizaciones/health` - Health check

### Arca Gestor de Solicitudes (Puerto 8082)
- **POST** `/api/solicitudes` - Crear solicitud
- **POST** `/api/solicitudes/{id}/enviar/{proveedorId}` - Enviar a proveedor
- **GET** `/api/solicitudes/{id}/respuestas` - Obtener respuestas
- **POST** `/api/solicitudes/respuestas` - Procesar respuesta proveedor
- **GET** `/api/solicitudes/health` - Health check

## Ejemplo de Uso

### Generar Cotización
```bash
curl -X POST http://localhost:8081/api/cotizaciones \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": "cliente-123",
    "items": [
      {
        "productoId": "prod-001",
        "cantidad": 5
      }
    ]
  }'
```

### Crear Solicitud a Proveedor
```bash
curl -X POST http://localhost:8082/api/solicitudes \
  -H "Content-Type: application/json" \
  -d '{
    "proveedorId": "prov-001",
    "clienteId": "cliente-123",
    "items": [
      {
        "productoId": "prod-001",
        "cantidad": 10,
        "especificaciones": "Calidad premium"
      }
    ]
  }'
```

## Tecnologías Utilizadas

- **Spring Boot 3.5.3**
- **Spring WebFlux** (Programación reactiva)
- **R2DBC** (Acceso reactivo a base de datos)
- **H2 Database** (Desarrollo)
- **MySQL** (Producción para módulo principal)
- **Java 21**
- **Gradle** (Gestión de dependencias)

## Próximos Pasos

1. Implementar persistencia real con R2DBC
2. Añadir seguridad (JWT, OAuth2)
3. Configurar service discovery (Eureka)
4. Implementar circuit breakers (Resilience4j)
5. Añadir monitoreo (Micrometer + Prometheus)
6. Dockerizar los microservicios
