# Arquitectura Hexagonal - Proyecto Arka Valenzuela

## Descripción

Este proyecto ha sido refactorizado para implementar la **Arquitectura Hexagonal** (también conocida como Ports and Adapters), que separa la lógica de negocio de las implementaciones técnicas mediante el uso de puertos (interfaces) y adaptadores.

## Estructura de la Arquitectura Hexagonal

```
src/main/java/com/arka/arkavalenzuela/
├── domain/                          # NÚCLEO DEL DOMINIO
│   ├── model/                       # Entidades de dominio (sin anotaciones JPA)
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   └── Cart.java
│   ├── port/
│   │   ├── in/                      # Puertos de entrada (casos de uso)
│   │   │   ├── ProductUseCase.java
│   │   │   ├── CategoryUseCase.java
│   │   │   ├── CustomerUseCase.java
│   │   │   ├── OrderUseCase.java
│   │   │   └── CartUseCase.java
│   │   └── out/                     # Puertos de salida (interfaces de repositorios)
│   │       ├── ProductRepositoryPort.java
│   │       ├── CategoryRepositoryPort.java
│   │       ├── CustomerRepositoryPort.java
│   │       ├── OrderRepositoryPort.java
│   │       └── CartRepositoryPort.java
│   └── service/                     # Servicios de dominio (lógica de negocio)
│       ├── ProductDomainService.java
│       ├── CategoryDomainService.java
│       ├── CustomerDomainService.java
│       ├── OrderDomainService.java
│       └── CartDomainService.java
├── application/                     # CAPA DE APLICACIÓN
│   └── usecase/                     # Casos de uso específicos de la aplicación
├── infrastructure/                  # CAPA DE INFRAESTRUCTURA
│   ├── adapter/
│   │   ├── in/                      # Adaptadores de entrada
│   │   │   └── web/                 # Controladores REST
│   │   │       ├── ProductController.java
│   │   │       ├── CategoryController.java
│   │   │       ├── CustomerController.java
│   │   │       ├── dto/             # DTOs para la API REST
│   │   │       │   ├── ProductDto.java
│   │   │       │   ├── CategoryDto.java
│   │   │       │   └── CustomerDto.java
│   │   │       └── mapper/          # Mappers web (DTO ↔ Domain)
│   │   │           ├── ProductWebMapper.java
│   │   │           ├── CategoryWebMapper.java
│   │   │           └── CustomerWebMapper.java
│   │   └── out/                     # Adaptadores de salida
│   │       └── persistence/         # Adaptador de persistencia
│   │           ├── entity/          # Entidades JPA
│   │           │   ├── ProductEntity.java
│   │           │   ├── CategoryEntity.java
│   │           │   ├── CustomerEntity.java
│   │           │   ├── OrderEntity.java
│   │           │   └── CartEntity.java
│   │           ├── repository/      # Repositorios JPA
│   │           │   ├── ProductJpaRepository.java
│   │           │   ├── CategoryJpaRepository.java
│   │           │   ├── CustomerJpaRepository.java
│   │           │   ├── OrderJpaRepository.java
│   │           │   └── CartJpaRepository.java
│   │           ├── mapper/          # Mappers de persistencia (Entity ↔ Domain)
│   │           │   ├── ProductMapper.java
│   │           │   ├── CategoryMapper.java
│   │           │   ├── CustomerMapper.java
│   │           │   ├── OrderMapper.java
│   │           │   └── CartMapper.java
│   │           ├── ProductPersistenceAdapter.java
│   │           ├── CategoryPersistenceAdapter.java
│   │           ├── CustomerPersistenceAdapter.java
│   │           ├── OrderPersistenceAdapter.java
│   │           └── CartPersistenceAdapter.java
│   └── config/                      # Configuración de Spring
│       └── BeanConfiguration.java
└── ArkajvalenzuelaApplication.java  # Clase principal
```

## Principios de la Arquitectura Hexagonal

### 1. **Separación de Responsabilidades**
- **Domain**: Contiene la lógica de negocio pura, sin dependencias externas
- **Application**: Orquesta los casos de uso
- **Infrastructure**: Maneja detalles técnicos (base de datos, API REST, etc.)

### 2. **Inversión de Dependencias**
- El dominio define interfaces (puertos) que la infraestructura implementa
- El dominio NO depende de la infraestructura
- La infraestructura SÍ depende del dominio

### 3. **Puertos y Adaptadores**
- **Puertos de Entrada (In)**: Definen casos de uso (interfaces que expone el dominio)
- **Puertos de Salida (Out)**: Definen servicios externos necesarios (interfaces que necesita el dominio)
- **Adaptadores**: Implementan los puertos conectando con tecnologías específicas

## Beneficios de esta Arquitectura

### ✅ **Testabilidad**
- Fácil creación de tests unitarios para la lógica de negocio
- Posibilidad de usar mocks para las dependencias externas

### ✅ **Mantenibilidad**
- Código más organizado y fácil de entender
- Cambios en la infraestructura no afectan el dominio

### ✅ **Flexibilidad**
- Fácil intercambio de implementaciones (ej: cambiar de MySQL a PostgreSQL)
- Posibilidad de agregar nuevos adaptadores (ej: API GraphQL)

### ✅ **Cumplimiento de SOLID**
- **S**ingle Responsibility: Cada clase tiene una responsabilidad única
- **O**pen/Closed: Abierto para extensión, cerrado para modificación
- **L**iskov Substitution: Los adaptadores pueden sustituirse
- **I**nterface Segregation: Interfaces específicas y pequeñas
- **D**ependency Inversion: Dependencias invertidas hacia abstracciones

## Flujo de Datos

### Entrada (Request):
```
Cliente → REST Controller → Web Mapper → Domain Service → Repository Port → Repository Adapter → JPA Repository → Base de Datos
```

### Salida (Response):
```
Base de Datos → JPA Repository → Repository Adapter → Repository Port → Domain Service → Web Mapper → REST Controller → Cliente
```

## Cómo Usar esta Arquitectura

### Agregar Nueva Funcionalidad:

1. **Crear entidad de dominio** en `domain/model/`
2. **Definir puerto de entrada** en `domain/port/in/`
3. **Definir puerto de salida** en `domain/port/out/`
4. **Implementar servicio de dominio** en `domain/service/`
5. **Crear entidad JPA** en `infrastructure/adapter/out/persistence/entity/`
6. **Crear repositorio JPA** en `infrastructure/adapter/out/persistence/repository/`
7. **Crear adaptador de persistencia** en `infrastructure/adapter/out/persistence/`
8. **Crear DTOs y mappers web** en `infrastructure/adapter/in/web/`
9. **Crear controlador** en `infrastructure/adapter/in/web/`
10. **Configurar beans** en `infrastructure/config/BeanConfiguration.java`

## Comparación con la Arquitectura Anterior

### ❌ **Antes (Arquitectura en Capas)**
```
Controller → Service → Repository → Entity
```
- Acoplamiento fuerte entre capas
- Difícil testing
- Dominio contaminado con detalles técnicos

### ✅ **Ahora (Arquitectura Hexagonal)**
```
Web Adapter → Use Case → Domain Service → Repository Port → Repository Adapter
```
- Bajo acoplamiento
- Alta cohesión
- Dominio limpio y testeable
- Flexibilidad para cambios

## Comandos para Ejecutar

```bash
# Compilar el proyecto
./gradlew build

# Ejecutar tests
./gradlew test

# Ejecutar aplicación
./gradlew bootRun
```

## Notas Importantes

- Las entidades de dominio **NO** tienen anotaciones JPA
- Los servicios de dominio contienen la **lógica de negocio**
- Los adaptadores son los únicos que conocen detalles técnicos
- Los puertos definen **contratos** sin implementación
- La configuración de Spring está centralizada en `BeanConfiguration.java`

Esta arquitectura hace el código más robusto, testeable y mantenible a largo plazo.
