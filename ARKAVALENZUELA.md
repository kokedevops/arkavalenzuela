# 📋 Proyecto Arka Valenzuela - Documentación Completa

## 📖 Descripción General

**Arka Valenzuela** es una aplicación de comercio electrónico desarrollada en **Spring Boot** que implementa la **Arquitectura Hexagonal** (Ports and Adapters). El sistema gestiona productos, categorías, clientes, pedidos y carritos de compra con una separación clara entre la lógica de negocio y los detalles técnicos.

---

## 🏗️ Arquitectura del Sistema

### 🛠️ Tecnologías Utilizadas
- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **MySQL** (Base de datos principal)
- **H2** (Base de datos para testing)
- **Gradle** (Gestión de dependencias y build)

### 🎯 Patrón Arquitectónico
- **Arquitectura Hexagonal** (Clean Architecture/Ports and Adapters)
- **Domain-Driven Design (DDD)**
- **Principios SOLID**
- **Inversión de Dependencias**

---

## 📁 Estructura Completa del Proyecto

```
src/main/java/com/arka/arkavalenzuela/
│
├── 🟡 DOMAIN (Núcleo del Negocio)
│   ├── model/                           📊 Entidades de Dominio
│   │   ├── Product.java                 🛍️ Producto del sistema
│   │   ├── Category.java                📂 Categoría de productos
│   │   ├── Customer.java                👤 Cliente del sistema
│   │   ├── Order.java                   📋 Pedido realizado
│   │   └── Cart.java                    🛒 Carrito de compras
│   └── port/                            🔌 Contratos/Interfaces
│       ├── in/                          📥 Puertos de Entrada (Use Cases)
│       │   ├── ProductUseCase.java      🛍️ Casos de uso de Producto
│       │   ├── CategoryUseCase.java     📂 Casos de uso de Categoría
│       │   ├── CustomerUseCase.java     👤 Casos de uso de Cliente
│       │   ├── OrderUseCase.java        📋 Casos de uso de Pedido
│       │   └── CartUseCase.java         🛒 Casos de uso de Carrito
│       └── out/                         📤 Puertos de Salida (Repository)
│           ├── ProductRepositoryPort.java    🗃️ Contrato repo Producto
│           ├── CategoryRepositoryPort.java   🗃️ Contrato repo Categoría
│           ├── CustomerRepositoryPort.java   🗃️ Contrato repo Cliente
│           ├── OrderRepositoryPort.java      🗃️ Contrato repo Pedido
│           └── CartRepositoryPort.java       🗃️ Contrato repo Carrito
│
├── 🟢 APPLICATION (Casos de Uso)
│   └── usecase/                         🎯 Servicios de Aplicación
│       ├── ProductApplicationService.java    🛍️ Servicio app Producto
│       ├── CategoryApplicationService.java   📂 Servicio app Categoría
│       ├── CustomerApplicationService.java   👤 Servicio app Cliente
│       ├── OrderApplicationService.java      📋 Servicio app Pedido
│       └── CartApplicationService.java       🛒 Servicio app Carrito
│
├── 🔵 INFRASTRUCTURE (Detalles Técnicos)
│   ├── adapter/
│   │   ├── in/                          📥 Adaptadores de Entrada
│   │   │   └── web/                     🌐 Capa Web (REST API)
│   │   │       ├── ProductController.java     🎮 Controlador Producto
│   │   │       ├── CategoryController.java    🎮 Controlador Categoría
│   │   │       ├── CustomerController.java    🎮 Controlador Cliente
│   │   │       ├── dto/                 📋 Data Transfer Objects
│   │   │       │   ├── ProductDTO.java       📄 DTO Producto
│   │   │       │   ├── CategoryDTO.java      📄 DTO Categoría
│   │   │       │   └── CustomerDTO.java      📄 DTO Cliente
│   │   │       └── mapper/              🔄 Mappers Web
│   │   │           ├── ProductWebMapper.java   🔄 Mapper DTO→Domain
│   │   │           ├── CategoryWebMapper.java  🔄 Mapper DTO→Domain
│   │   │           └── CustomerWebMapper.java  🔄 Mapper DTO→Domain
│   │   └── out/                         📤 Adaptadores de Salida
│   │       └── persistence/             🗄️ Capa de Persistencia
│   │           ├── entity/              🗃️ Entidades JPA
│   │           │   ├── ProductJpaEntity.java   🗃️ Entidad JPA Producto
│   │           │   ├── CategoryJpaEntity.java  🗃️ Entidad JPA Categoría
│   │           │   ├── CustomerJpaEntity.java  🗃️ Entidad JPA Cliente
│   │           │   ├── OrderJpaEntity.java     🗃️ Entidad JPA Pedido
│   │           │   └── CartJpaEntity.java      🗃️ Entidad JPA Carrito
│   │           ├── repository/          🗂️ Repositorios JPA
│   │           │   ├── ProductJpaRepository.java    📚 Repo JPA Producto
│   │           │   ├── CategoryJpaRepository.java   📚 Repo JPA Categoría
│   │           │   ├── CustomerJpaRepository.java   📚 Repo JPA Cliente
│   │           │   ├── OrderJpaRepository.java      📚 Repo JPA Pedido
│   │           │   └── CartJpaRepository.java       📚 Repo JPA Carrito
│   │           ├── mapper/              🔄 Mappers Persistencia
│   │           │   ├── ProductPersistenceMapper.java   🔄 Domain↔Entity
│   │           │   ├── CategoryPersistenceMapper.java  🔄 Domain↔Entity
│   │           │   ├── CustomerPersistenceMapper.java  🔄 Domain↔Entity
│   │           │   ├── OrderPersistenceMapper.java     🔄 Domain↔Entity
│   │           │   └── CartPersistenceMapper.java      🔄 Domain↔Entity
│   │           ├── ProductPersistenceAdapter.java      🔌 Adaptador Producto
│   │           ├── CategoryPersistenceAdapter.java     🔌 Adaptador Categoría
│   │           ├── CustomerPersistenceAdapter.java     🔌 Adaptador Cliente
│   │           ├── OrderPersistenceAdapter.java        🔌 Adaptador Pedido
│   │           └── CartPersistenceAdapter.java         🔌 Adaptador Carrito
│   └── config/                          ⚙️ Configuración
│       └── BeanConfiguration.java       🔧 Configuración Spring Beans
│
├── 🔴 MAIN (Punto de Entrada)
│   ├── ArkajvalenzuelaApplication.java  🚀 Aplicación Principal
│   └── ServletInitializer.java         🌐 Inicializador WAR
│
└── resources/
    ├── application.properties           ⚙️ Configuración App
    └── application-test.properties      🧪 Configuración Tests
```

---

## 🟡 CAPA DE DOMINIO (DOMAIN)

### 🎯 **Propósito**: Contiene la lógica de negocio pura, reglas del dominio y entidades sin dependencias técnicas.

### 📊 **1. Modelos de Dominio** (`domain/model/`)

Entidades que representan conceptos del negocio sin anotaciones técnicas (sin JPA, sin Spring):

#### 🛍️ **Product.java**
```java
public class Product {
    private Long id;
    private String nombre;
    private String descripcion;
    private Category categoria;      // Relación con categoría
    private String marca;
    private BigDecimal precioUnitario;
    private Integer stock;
    
    // Lógica de negocio pura
    public boolean hasValidPrice() {
        return precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isInStock() {
        return stock != null && stock > 0;
    }
}
```
**🎯 Función**: Representa un producto del catálogo con su lógica de negocio.

#### 📂 **Category.java**
```java
public class Category {
    private Long id;
    private String nombre;
    
    // Validaciones de dominio
    public boolean isValidName() {
        return nombre != null && !nombre.trim().isEmpty();
    }
}
```
**🎯 Función**: Representa una categoría de productos.

#### 👤 **Customer.java**
```java
public class Customer {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String ciudad;
    private String pais;
    
    // Lógica de negocio
    public boolean isValidEmail() {
        return email != null && email.contains("@");
    }
}
```
**🎯 Función**: Representa un cliente del sistema.

#### 📋 **Order.java**
```java
public class Order {
    private Long id;
    private Customer cliente;
    private List<Product> productos;
    private LocalDateTime fecha;
    private BigDecimal total;
    
    // Lógica de negocio
    public BigDecimal calculateTotal() {
        return productos.stream()
            .map(Product::getPrecioUnitario)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean isValidOrder() {
        return productos != null && !productos.isEmpty() && cliente != null;
    }
}
```
**🎯 Función**: Representa un pedido realizado por un cliente.

#### 🛒 **Cart.java**
```java
public class Cart {
    private Long id;
    private Customer cliente;
    private LocalDateTime fechaCreacion;
    private String estado;
    
    // Estados válidos del carrito
    public boolean isActive() {
        return "ACTIVE".equals(estado);
    }
}
```
**🎯 Función**: Representa un carrito de compras.

### 🔌 **2. Puertos (Contratos)** (`domain/port/`)

#### 📥 **Puertos de Entrada** (`domain/port/in/`)
Definen **QUÉ** puede hacer el sistema (casos de uso):

#### 🛍️ **ProductUseCase.java**
```java
public interface ProductUseCase {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    List<Product> getProductsByCategory(String categoryName);
    List<Product> searchProductsByName(String name);
    List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max);
    List<Product> getAllProductsSorted();
}
```
**🎯 Función**: Define todos los casos de uso relacionados con productos.

#### 📂 **CategoryUseCase.java**
```java
public interface CategoryUseCase {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
}
```
**🎯 Función**: Define casos de uso para gestión de categorías.

#### 👤 **CustomerUseCase.java**
```java
public interface CustomerUseCase {
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);
    List<Customer> searchCustomersByName(String name);
    List<Customer> getAllCustomersSorted();
}
```
**🎯 Función**: Define casos de uso para gestión de clientes.

#### 📤 **Puertos de Salida** (`domain/port/out/`)
Definen **CÓMO** el dominio accede a datos externos:

#### 🗃️ **ProductRepositoryPort.java**
```java
public interface ProductRepositoryPort {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Product> findByCategoriaNombre(String categoryName);
    List<Product> findByNombreContainingIgnoreCase(String name);
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
}
```
**🎯 Función**: Contrato para persistencia de productos (sin implementación).

---

## 🟢 CAPA DE APLICACIÓN (APPLICATION)

### 🎯 **Propósito**: Orquesta casos de uso, coordina servicios de dominio, implementa la lógica de aplicación.

### 🎯 **Servicios de Aplicación** (`application/usecase/`)

#### 🛍️ **ProductApplicationService.java**
```java
@Service
public class ProductApplicationService implements ProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    
    public ProductApplicationService(ProductRepositoryPort productRepository, 
                                   CategoryRepositoryPort categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Product createProduct(Product product) {
        // 1. Validar producto (lógica de aplicación)
        validateProduct(product);
        
        // 2. Validar que categoría existe
        validateCategoryExists(product.getCategoria().getId());
        
        // 3. Guardar producto
        return productRepository.save(product);
    }
    
    // Lógica de validación de aplicación
    private void validateProduct(Product product) {
        if (product.getNombre() == null || product.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (!product.hasValidPrice()) {
            throw new IllegalArgumentException("Product must have a valid price");
        }
    }
}
```
**🎯 Función**: Implementa casos de uso de productos, orquesta validaciones y persistencia.

#### 📂 **CategoryApplicationService.java**
```java
@Service
public class CategoryApplicationService implements CategoryUseCase {
    
    private final CategoryRepositoryPort categoryRepository;
    
    @Override
    public Category createCategory(Category category) {
        validateCategory(category);
        return categoryRepository.save(category);
    }
    
    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (category.getNombre() == null || category.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
    }
}
```
**🎯 Función**: Implementa casos de uso de categorías.

---

## 🔵 CAPA DE INFRAESTRUCTURA (INFRASTRUCTURE)

### 🎯 **Propósito**: Implementa detalles técnicos, frameworks, bases de datos, APIs REST.

### 📥 **Adaptadores de Entrada** (`infrastructure/adapter/in/web/`)

#### 🎮 **Controladores REST**

#### 🛍️ **ProductController.java**
```java
@RestController
@RequestMapping("/productos")
public class ProductController {
    
    private final ProductUseCase productUseCase;
    private final ProductWebMapper webMapper;
    
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productUseCase.getAllProducts();
        return webMapper.toDTO(products);
    }
    
    @PostMapping
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) {
        Product product = webMapper.toDomain(productDTO);
        Product savedProduct = productUseCase.createProduct(product);
        return webMapper.toDTO(savedProduct);
    }
}
```
**🎯 Función**: Expone endpoints REST para productos, convierte DTOs ↔ Domain.

#### 📋 **DTOs (Data Transfer Objects)** (`infrastructure/adapter/in/web/dto/`)

#### 🛍️ **ProductDTO.java**
```java
public class ProductDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long categoriaId;    // Solo ID, no objeto completo
    private String marca;
    private BigDecimal precioUnitario;
    private Integer stock;
    
    // Getters y setters
}
```
**🎯 Función**: Estructura de datos para API REST, optimizada para transferencia.

#### 🔄 **Mappers Web** (`infrastructure/adapter/in/web/mapper/`)

#### 🛍️ **ProductWebMapper.java**
```java
@Component
public class ProductWebMapper {
    
    private final CategoryRepositoryPort categoryRepository;
    
    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setNombre(product.getNombre());
        dto.setCategoriaId(product.getCategoria().getId());
        return dto;
    }
    
    public Product toDomain(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setNombre(dto.getNombre());
        
        // Cargar categoría completa
        Category categoria = categoryRepository.findById(dto.getCategoriaId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategoria(categoria);
        
        return product;
    }
}
```
**🎯 Función**: Convierte entre DTOs (API) y entidades de Dominio.

### 📤 **Adaptadores de Salida** (`infrastructure/adapter/out/persistence/`)

#### 🗃️ **Entidades JPA** (`infrastructure/adapter/out/persistence/entity/`)

#### 🛍️ **ProductJpaEntity.java**
```java
@Entity
@Table(name = "productos")
public class ProductJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoryJpaEntity categoria;
    
    @Column(name = "precio_unitario", precision = 12, scale = 2)
    private BigDecimal precioUnitario;
    
    // Getters y setters
}
```
**🎯 Función**: Entidad JPA con anotaciones de base de datos, separada del dominio.

#### 📚 **Repositorios JPA** (`infrastructure/adapter/out/persistence/repository/`)

#### 🛍️ **ProductJpaRepository.java**
```java
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
    
    List<ProductJpaEntity> findByCategoriaNombre(String categoryName);
    
    List<ProductJpaEntity> findByNombreContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.precioUnitario BETWEEN :min AND :max")
    List<ProductJpaEntity> findByPriceRange(@Param("min") BigDecimal min, 
                                          @Param("max") BigDecimal max);
}
```
**🎯 Función**: Repositorio JPA con consultas específicas de base de datos.

#### 🔄 **Mappers de Persistencia** (`infrastructure/adapter/out/persistence/mapper/`)

#### 🛍️ **ProductPersistenceMapper.java**
```java
@Component
public class ProductPersistenceMapper {
    
    private final CategoryPersistenceMapper categoryMapper;
    
    public ProductJpaEntity toEntity(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(product.getId());
        entity.setNombre(product.getNombre());
        entity.setCategoria(categoryMapper.toEntity(product.getCategoria()));
        entity.setPrecioUnitario(product.getPrecioUnitario());
        return entity;
    }
    
    public Product toDomain(ProductJpaEntity entity) {
        Product product = new Product();
        product.setId(entity.getId());
        product.setNombre(entity.getNombre());
        product.setCategoria(categoryMapper.toDomain(entity.getCategoria()));
        product.setPrecioUnitario(entity.getPrecioUnitario());
        return product;
    }
}
```
**🎯 Función**: Convierte entre entidades de Dominio y entidades JPA.

#### 🔌 **Adaptadores de Persistencia** (`infrastructure/adapter/out/persistence/`)

#### 🛍️ **ProductPersistenceAdapter.java**
```java
@Component
public class ProductPersistenceAdapter implements ProductRepositoryPort {
    
    private final ProductJpaRepository jpaRepository;
    private final ProductPersistenceMapper mapper;
    
    @Override
    public List<Product> findAll() {
        List<ProductJpaEntity> entities = jpaRepository.findAll();
        return entities.stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = mapper.toEntity(product);
        ProductJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
```
**🎯 Función**: Implementa los puertos de salida del dominio usando JPA.

### ⚙️ **Configuración** (`infrastructure/config/`)

#### 🔧 **BeanConfiguration.java**
```java
@Configuration
public class BeanConfiguration {
    
    @Bean
    public ProductUseCase productUseCase(ProductRepositoryPort productRepository, 
                                       CategoryRepositoryPort categoryRepository) {
        return new ProductApplicationService(productRepository, categoryRepository);
    }
    
    @Bean
    public CategoryUseCase categoryUseCase(CategoryRepositoryPort categoryRepository) {
        return new CategoryApplicationService(categoryRepository);
    }
}
```
**🎯 Función**: Configura la inyección de dependencias, conecta puertos con adaptadores.

---

## 🔄 FLUJO DE DATOS COMPLETO

### 📊 **Flujo de Creación de Producto**

```
1. 📱 REQUEST HTTP POST /productos
   ↓
2. 🎮 ProductController.createProduct()
   ↓
3. 📋 ProductDTO → Product (ProductWebMapper)
   ↓
4. 🟢 ProductApplicationService.createProduct()
   ├── Validar producto
   ├── Validar categoría
   └── Llamar a puerto de salida
   ↓
5. 🔌 ProductPersistenceAdapter.save()
   ↓
6. 🔄 Product → ProductJpaEntity (PersistenceMapper)
   ↓
7. 📚 ProductJpaRepository.save()
   ↓
8. 🗃️ Base de Datos MySQL
   ↓
9. 🔄 ProductJpaEntity → Product (PersistenceMapper)
   ↓
10. 🔄 Product → ProductDTO (WebMapper)
    ↓
11. 📱 RESPONSE HTTP JSON
```

---

## 🎯 SEPARACIÓN DE RESPONSABILIDADES

### 🟡 **DOMAIN** - ¿QUÉ hace el sistema?
- **📊 Modelos**: Entidades de negocio puras
- **📥 Puertos IN**: Casos de uso que el sistema puede realizar
- **📤 Puertos OUT**: Contratos para obtener/guardar datos
- **🚫 NO depende**: De frameworks, bases de datos, tecnologías

### 🟢 **APPLICATION** - ¿CÓMO orquesta el sistema?
- **🎯 Use Cases**: Implementan los casos de uso del dominio
- **🔄 Orquestación**: Coordina validaciones y persistencia
- **📋 Lógica de Aplicación**: Flujos de trabajo, transacciones
- **🔗 Depende**: Solo del dominio (puertos)

### 🔵 **INFRASTRUCTURE** - ¿CON QUÉ tecnologías?
- **📥 Adaptadores IN**: REST API, controladores, DTOs
- **📤 Adaptadores OUT**: JPA, bases de datos, mappers
- **⚙️ Configuración**: Spring, beans, propiedades
- **🔗 Depende**: De frameworks y tecnologías externas

---

## 🚀 ENDPOINTS DISPONIBLES

### 📂 **Categorías** (`/categorias`)
```
GET    /categorias              → Listar todas las categorías
GET    /categorias/{id}         → Obtener categoría por ID
POST   /categorias              → Crear nueva categoría
PUT    /categorias/{id}         → Actualizar categoría
DELETE /categorias/{id}         → Eliminar categoría
```

### 🛍️ **Productos** (`/productos`)
```
GET    /productos                        → Listar todos los productos
GET    /productos/{id}                   → Obtener producto por ID
GET    /productos/categoria/{nombre}     → Productos por categoría
GET    /productos/buscar?term=X          → Buscar por nombre
GET    /productos/ordenados              → Productos ordenados A-Z
GET    /productos/rango?min=X&max=Y      → Productos por rango de precio
POST   /productos                        → Crear nuevo producto
PUT    /productos/{id}                   → Actualizar producto
DELETE /productos/{id}                   → Eliminar producto
```

### 👤 **Clientes** (`/usuarios`)
```
GET    /usuarios                → Listar todos los clientes
GET    /usuarios/{id}           → Obtener cliente por ID
GET    /usuarios/buscar?nombre=X → Buscar por nombre
GET    /usuarios/ordenados      → Clientes ordenados A-Z
POST   /usuarios               → Crear nuevo cliente
PUT    /usuarios/{id}          → Actualizar cliente
DELETE /usuarios/{id}          → Eliminar cliente
```

---

## 🗃️ MODELO DE BASE DE DATOS

### 📊 **Tablas Principales**

#### 🗂️ **categorias**
```sql
categoria_id BIGINT PRIMARY KEY AUTO_INCREMENT
nombre VARCHAR(255) NOT NULL
```

#### 🛍️ **productos**
```sql
producto_id BIGINT PRIMARY KEY AUTO_INCREMENT
nombre VARCHAR(255) NOT NULL
descripcion VARCHAR(255)
marca VARCHAR(255)
precio_unitario DECIMAL(12,2)
stock INTEGER
categoria_id BIGINT (FK → categorias)
```

#### 👤 **clientes**
```sql
cliente_id BIGINT PRIMARY KEY AUTO_INCREMENT
nombre VARCHAR(255) NOT NULL
email VARCHAR(255)
telefono VARCHAR(255)
ciudad VARCHAR(255)
pais VARCHAR(255)
```

#### 📋 **pedidos**
```sql
pedido_id BIGINT PRIMARY KEY AUTO_INCREMENT
fecha DATETIME
total DECIMAL(38,2)
cliente_id BIGINT (FK → clientes)
```

#### 🛒 **carritos**
```sql
carrito_id BIGINT PRIMARY KEY AUTO_INCREMENT
estado VARCHAR(255)
fecha_creacion DATETIME
cliente_id BIGINT (FK → clientes)
```

---

## 🎯 BENEFICIOS DE LA ARQUITECTURA HEXAGONAL

### ✅ **Testabilidad**
- **🧪 Unit Tests**: Dominio testeable sin dependencias
- **🔧 Integration Tests**: Adaptadores testeables por separado
- **🎭 Mocks**: Fácil inyección de dependencias ficticias

### 🔄 **Flexibilidad**
- **🔌 Intercambio de Adaptadores**: MySQL ↔ PostgreSQL sin tocar dominio
- **📱 Múltiples Interfaces**: REST, GraphQL, gRPC
- **🧩 Evolución Independiente**: Cada capa evoluciona por separado

### 🧹 **Mantenibilidad**
- **📦 Separación Clara**: Cada capa con responsabilidad específica
- **🔍 Fácil Debugging**: Flujo predecible entre capas
- **📖 Código Legible**: Estructura estándar y predecible

### 🏗️ **Escalabilidad**
- **⚡ Performance**: Optimizaciones por capa
- **🔀 Microservicios**: Preparado para división
- **📈 Crecimiento**: Agregado de funcionalidades sin refactoring

---

## 🛠️ COMANDOS DE DESARROLLO

### 🔨 **Build y Compilación**
```bash
# Compilar proyecto
./gradlew build

# Compilar sin tests
./gradlew build -x test

# Limpiar proyecto
./gradlew clean
```

### 🧪 **Testing**
```bash
# Ejecutar todos los tests
./gradlew test

# Tests con reporte
./gradlew test --info
```

### 🚀 **Ejecución**
```bash
# Iniciar aplicación
./gradlew bootRun

# Ejecutar en background
./gradlew bootRun &
```

### 🌐 **Pruebas de API**
```powershell
# Listar categorías
Invoke-RestMethod -Uri "http://localhost:8080/categorias" -Method Get

# Crear categoría
Invoke-RestMethod -Uri "http://localhost:8080/categorias" -Method Post -ContentType "application/json" -Body '{"nombre":"Nueva Categoria"}'

# Crear producto
Invoke-RestMethod -Uri "http://localhost:8080/productos" -Method Post -ContentType "application/json" -Body '{"nombre":"Nuevo Producto","categoriaId":1,"precioUnitario":50.0,"stock":100}'
```

---

## 📈 MÉTRICAS DEL PROYECTO

### 📊 **Estadísticas de Código**
- **🟡 Domain**: 5 modelos + 10 puertos = 15 clases
- **🟢 Application**: 5 servicios de aplicación = 5 clases
- **🔵 Infrastructure**: 20+ adaptadores + mappers + configs
- **📝 Total**: ~40 clases organizadas en arquitectura hexagonal

### 🎯 **Cobertura Funcional**
- ✅ **CRUD Completo**: Para todas las entidades
- ✅ **Validaciones**: Reglas de negocio implementadas
- ✅ **Búsquedas**: Por múltiples criterios
- ✅ **Relaciones**: Entre entidades correctamente mapeadas

---

## 🏆 CONCLUSIÓN

El proyecto **Arka Valenzuela** implementa exitosamente la **Arquitectura Hexagonal**, logrando:

1. **🎯 Separación Clara**: Dominio independiente de tecnologías
2. **🔄 Flexibilidad**: Fácil intercambio de adaptadores
3. **🧪 Testabilidad**: Cada capa testeable independientemente
4. **📈 Escalabilidad**: Preparado para crecimiento futuro
5. **🧹 Mantenibilidad**: Código limpio y organizado

### 🚀 **Listo para**:
- ✅ Desarrollo continuo
- ✅ Testing exhaustivo  
- ✅ Despliegue en producción
- ✅ Evolución arquitectónica

---

*Documentación actualizada el 15 de Julio de 2025*  
*Proyecto: Arka Valenzuela - Arquitectura Hexagonal Completa*
    private Integer stock;
}
```

**Métodos de Negocio:**
- `isAvailable()` - Verifica si el producto está disponible
- `hasValidPrice()` - Valida que el precio sea mayor a cero
- `updateStock(Integer newStock)` - Actualiza el stock
- `reduceStock(Integer quantity)` - Reduce el stock por cantidad

#### 2. **Category.java**
```java
public class Category {
    private Long id;
    private String nombre;
}
```

**Métodos de Negocio:**
- `isValidName()` - Valida que el nombre no esté vacío

#### 3. **Customer.java**
```java
public class Customer {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String pais;
    private String ciudad;
}
```

**Métodos de Negocio:**
- `isValidEmail()` - Valida formato de email
- `hasCompleteProfile()` - Verifica perfil completo

#### 4. **Order.java**
```java
public class Order {
    private Long id;
    private Customer cliente;
    private LocalDateTime fecha;
    private BigDecimal total;
    private Set<Product> productos;
}
```

**Métodos de Negocio:**
- `calculateTotal()` - Calcula el total del pedido
- `isValidOrder()` - Valida que el pedido sea correcto
- `addProduct(Product product)` - Añade producto al pedido
- `removeProduct(Product product)` - Remueve producto del pedido

#### 5. **Cart.java**
```java
public class Cart {
    private Long id;
    private Customer cliente;
    private LocalDateTime fechaCreacion;
    private String estado;
}
```

**Métodos de Negocio:**
- `isAbandoned()` - Verifica si está abandonado
- `isActive()` - Verifica si está activo
- `abandon()` - Marca como abandonado
- `activate()` - Activa el carrito

### 🔌 Puertos de Entrada (Use Cases)

#### 1. **ProductUseCase.java**
```java
public interface ProductUseCase {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    List<Product> getProductsByCategory(String categoryName);
    List<Product> searchProductsByName(String name);
    List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max);
    List<Product> getAllProductsSorted();
}
```

#### 2. **CategoryUseCase.java**
```java
public interface CategoryUseCase {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
}
```

#### 3. **CustomerUseCase.java**
```java
public interface CustomerUseCase {
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);
    List<Customer> searchCustomersByName(String name);
    List<Customer> getAllCustomersSorted();
}
```

#### 4. **OrderUseCase.java**
```java
public interface OrderUseCase {
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order createOrder(Order order);
    Order updateOrder(Long id, Order order);
    void deleteOrder(Long id);
    List<Order> getOrdersByProduct(Product product);
    List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);
    List<Order> getOrdersByCustomer(Customer customer);
}
```

#### 5. **CartUseCase.java**
```java
public interface CartUseCase {
    List<Cart> getAllCarts();
    Cart getCartById(Long id);
    Cart createCart(Cart cart);
    Cart updateCart(Long id, Cart cart);
    void deleteCart(Long id);
    List<Cart> getAbandonedCarts();
}
```

### 🔌 Puertos de Salida (Repository Ports)

#### 1. **ProductRepositoryPort.java**
```java
public interface ProductRepositoryPort {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
    List<Product> findByCategoriaNombre(String categoriaNombre);
    List<Product> findByNombreContainingIgnoreCase(String nombre);
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
    boolean existsById(Long id);
}
```

#### 2. **CategoryRepositoryPort.java**
```java
public interface CategoryRepositoryPort {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Category save(Category category);
    void deleteById(Long id);
    boolean existsById(Long id);
}
```

#### 3. **CustomerRepositoryPort.java**
```java
public interface CustomerRepositoryPort {
    List<Customer> findAll();
    Optional<Customer> findById(Long id);
    Customer save(Customer customer);
    void deleteById(Long id);
    List<Customer> findByNombreStartingWith(String letra);
    boolean existsById(Long id);
}
```

#### 4. **OrderRepositoryPort.java**
```java
public interface OrderRepositoryPort {
    List<Order> findAll();
    Optional<Order> findById(Long id);
    Order save(Order order);
    void deleteById(Long id);
    List<Order> findByProductosContaining(Product product);
    List<Order> findByFechaBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByCliente(Customer cliente);
    boolean existsById(Long id);
}
```

#### 5. **CartRepositoryPort.java**
```java
public interface CartRepositoryPort {
    List<Cart> findAll();
    Optional<Cart> findById(Long id);
    Cart save(Cart cart);
    void deleteById(Long id);
    List<Cart> findByEstado(String estado);
    boolean existsById(Long id);
}
```

### ⚙️ Servicios de Dominio

#### 1. **ProductDomainService.java**
- Implementa `ProductUseCase`
- Contiene lógica de negocio para productos
- Valida reglas de negocio
- Coordina operaciones entre puertos

#### 2. **CategoryDomainService.java**
- Implementa `CategoryUseCase`
- Gestiona categorías de productos
- Valida nombres únicos

#### 3. **CustomerDomainService.java**
- Implementa `CustomerUseCase`
- Gestiona clientes del sistema
- Valida emails y perfiles

#### 4. **OrderDomainService.java**
- Implementa `OrderUseCase`
- Gestiona pedidos
- Calcula totales automáticamente

#### 5. **CartDomainService.java**
- Implementa `CartUseCase`
- Gestiona carritos de compra
- Maneja estados del carrito

---

## 🔵 CAPA DE INFRAESTRUCTURA

### 🌐 Adaptadores de Entrada (Web)

#### 1. **ProductController.java**
```java
@RestController
@RequestMapping("/productos")
public class ProductController {
    // Endpoints REST para productos
}
```

**Endpoints Disponibles:**
- `GET /productos` - Listar todos los productos
- `GET /productos/{id}` - Obtener producto por ID
- `GET /productos/categoria/{nombre}` - Productos por categoría
- `POST /productos` - Crear producto
- `PUT /productos/{id}` - Actualizar producto
- `DELETE /productos/{id}` - Eliminar producto
- `GET /productos/buscar?term=X` - Buscar productos
- `GET /productos/ordenados` - Productos ordenados
- `GET /productos/rango?min=X&max=Y` - Productos por rango de precio

#### 2. **CategoryController.java**
```java
@RestController
@RequestMapping("/categorias")
public class CategoryController {
    // Endpoints REST para categorías
}
```

**Endpoints Disponibles:**
- `GET /categorias` - Listar todas las categorías
- `GET /categorias/{id}` - Obtener categoría por ID
- `POST /categorias` - Crear categoría
- `PUT /categorias/{id}` - Actualizar categoría
- `DELETE /categorias/{id}` - Eliminar categoría

#### 3. **CustomerController.java**
```java
@RestController
@RequestMapping("/usuarios")
public class CustomerController {
    // Endpoints REST para clientes
}
```

**Endpoints Disponibles:**
- `GET /usuarios` - Listar todos los usuarios
- `GET /usuarios/{id}` - Obtener usuario por ID
- `POST /usuarios` - Crear usuario
- `PUT /usuarios/{id}` - Actualizar usuario
- `DELETE /usuarios/{id}` - Eliminar usuario
- `GET /usuarios/buscar?nombre=X` - Buscar usuarios
- `GET /usuarios/ordenados` - Usuarios ordenados

### 📦 DTOs (Data Transfer Objects)

#### 1. **ProductDto.java**
```java
public class ProductDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long categoriaId;
    private String marca;
    private BigDecimal precioUnitario;
    private Integer stock;
}
```

#### 2. **CategoryDto.java**
```java
public class CategoryDto {
    private Long id;
    private String nombre;
}
```

#### 3. **CustomerDto.java**
```java
public class CustomerDto {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String pais;
    private String ciudad;
}
```

### 🔄 Mappers Web

#### 1. **ProductWebMapper.java**
- Convierte entre `Product` (dominio) ↔ `ProductDto` (web)
- Maneja la relación con categorías

#### 2. **CustomerWebMapper.java**
- Convierte entre `Customer` (dominio) ↔ `CustomerDto` (web)

### 💾 Adaptadores de Salida (Persistencia)

#### Entidades JPA

#### 1. **ProductEntity.java**
```java
@Entity
@Table(name = "productos")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long productoId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoryEntity categoria;
    // ... otros campos
}
```

#### 2. **CategoryEntity.java**
```java
@Entity
@Table(name = "categorias")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Long categoriaId;
    
    @OneToMany(mappedBy = "categoria")
    private List<ProductEntity> productos;
    // ... otros campos
}
```

#### 3. **CustomerEntity.java**
```java
@Entity
@Table(name = "clientes")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Long clienteId;
    // ... otros campos
}
```

#### 4. **OrderEntity.java**
```java
@Entity
@Table(name = "pedidos")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    private Long pedidoId;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private CustomerEntity cliente;
    
    @ManyToMany
    @JoinTable(
        name = "pedido_producto",
        joinColumns = @JoinColumn(name = "pedido_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private Set<ProductEntity> productos;
    // ... otros campos
}
```

#### 5. **CartEntity.java**
```java
@Entity
@Table(name = "carritos")
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrito_id")
    private Long carritoId;
    
    @OneToOne
    @JoinColumn(name = "cliente_id")
    private CustomerEntity cliente;
    // ... otros campos
}
```

#### Repositorios JPA

#### 1. **ProductJpaRepository.java**
```java
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByCategoriaNombre(String categoriaNombre);
    List<ProductEntity> findByNombreContainingIgnoreCase(String nombre);
    @Query("SELECT p FROM ProductEntity p WHERE p.precioUnitario BETWEEN :min AND :max")
    List<ProductEntity> findByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
}
```

#### 2. **CategoryJpaRepository.java**
```java
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
}
```

#### 3. **CustomerJpaRepository.java**
```java
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {
    List<CustomerEntity> findByNombreStartingWith(String letra);
}
```

#### 4. **OrderJpaRepository.java**
```java
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByProductosContaining(ProductEntity product);
    List<OrderEntity> findByFechaBetween(LocalDateTime start, LocalDateTime end);
    List<OrderEntity> findByCliente(CustomerEntity cliente);
}
```

#### 5. **CartJpaRepository.java**
```java
public interface CartJpaRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByEstado(String estado);
}
```

#### Adaptadores de Persistencia

#### 1. **ProductPersistenceAdapter.java**
- Implementa `ProductRepositoryPort`
- Convierte entre entidades JPA y modelos de dominio
- Gestiona operaciones CRUD

#### 2. **CategoryPersistenceAdapter.java**
- Implementa `CategoryRepositoryPort`
- Gestiona persistencia de categorías

#### 3. **CustomerPersistenceAdapter.java**
- Implementa `CustomerRepositoryPort`
- Gestiona persistencia de clientes

#### 4. **OrderPersistenceAdapter.java**
- Implementa `OrderRepositoryPort`
- Gestiona persistencia de pedidos

#### 5. **CartPersistenceAdapter.java**
- Implementa `CartRepositoryPort`
- Gestiona persistencia de carritos

#### Mappers de Persistencia

#### 1. **ProductMapper.java**
- Convierte `Product` ↔ `ProductEntity`
- Maneja relaciones con categorías

#### 2. **CategoryMapper.java**
- Convierte `Category` ↔ `CategoryEntity`

#### 3. **CustomerMapper.java**
- Convierte `Customer` ↔ `CustomerEntity`

#### 4. **OrderMapper.java**
- Convierte `Order` ↔ `OrderEntity`
- Maneja relaciones complejas con productos y clientes

#### 5. **CartMapper.java**
- Convierte `Cart` ↔ `CartEntity`

### ⚙️ Configuración

#### **BeanConfiguration.java**
```java
@Configuration
public class BeanConfiguration {
    @Bean
    public ProductUseCase productUseCase(ProductRepositoryPort productRepository, 
                                       CategoryRepositoryPort categoryRepository) {
        return new ProductDomainService(productRepository, categoryRepository);
    }
    // ... otros beans
}
```

**Beans Configurados:**
- `ProductUseCase` → `ProductDomainService`
- `CategoryUseCase` → `CategoryDomainService`
- `CustomerUseCase` → `CustomerDomainService`
- `OrderUseCase` → `OrderDomainService`
- `CartUseCase` → `CartDomainService`

---

## 📊 Base de Datos

### Tablas Principales

#### 1. **productos**
```sql
CREATE TABLE productos (
    producto_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    categoria_id BIGINT,
    marca VARCHAR(255),
    precio_unitario DECIMAL(12,2),
    stock INT,
    FOREIGN KEY (categoria_id) REFERENCES categorias(categoria_id)
);
```

#### 2. **categorias**
```sql
CREATE TABLE categorias (
    categoria_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);
```

#### 3. **clientes**
```sql
CREATE TABLE clientes (
    cliente_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    telefono VARCHAR(255),
    pais VARCHAR(255),
    ciudad VARCHAR(255)
);
```

#### 4. **pedidos**
```sql
CREATE TABLE pedidos (
    pedido_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT,
    fecha DATETIME,
    total DECIMAL(12,2),
    FOREIGN KEY (cliente_id) REFERENCES clientes(cliente_id)
);
```

#### 5. **carritos**
```sql
CREATE TABLE carritos (
    carrito_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT,
    fecha_creacion DATETIME,
    estado VARCHAR(50),
    FOREIGN KEY (cliente_id) REFERENCES clientes(cliente_id)
);
```

#### 6. **pedido_producto** (Tabla de relación)
```sql
CREATE TABLE pedido_producto (
    pedido_id BIGINT,
    producto_id BIGINT,
    PRIMARY KEY (pedido_id, producto_id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(pedido_id),
    FOREIGN KEY (producto_id) REFERENCES productos(producto_id)
);
```

---

## 🚀 Uso del Sistema

### Configuración de Base de Datos

#### **application.properties**
```properties
spring.application.name=arkajvalenzuela

# Configuración de la base de datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/arkabd
spring.datasource.username=jvalenzuela
spring.datasource.password=Koke1988*
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración de JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### Comandos de Ejecución

```bash
# Compilar el proyecto
./gradlew build

# Ejecutar tests
./gradlew test

# Ejecutar aplicación
./gradlew bootRun

# Limpiar proyecto
./gradlew clean

# Generar JAR
./gradlew bootJar
```

### Ejemplos de Uso de API

#### 1. **Gestión de Categorías**

**Crear Categoría:**
```bash
POST /categorias
Content-Type: application/json

{
    "nombre": "Electrónicos"
}
```

**Obtener Todas las Categorías:**
```bash
GET /categorias
```

**Respuesta:**
```json
[
    {
        "id": 1,
        "nombre": "Electrónicos"
    },
    {
        "id": 2,
        "nombre": "Ropa"
    }
]
```

#### 2. **Gestión de Productos**

**Crear Producto:**
```bash
POST /productos
Content-Type: application/json

{
    "nombre": "iPhone 15",
    "descripcion": "Smartphone Apple última generación",
    "categoriaId": 1,
    "marca": "Apple",
    "precioUnitario": 999.99,
    "stock": 50
}
```

**Buscar Productos por Categoría:**
```bash
GET /productos/categoria/Electrónicos
```

**Buscar Productos por Rango de Precio:**
```bash
GET /productos/rango?min=100&max=1000
```

**Buscar Productos por Nombre:**
```bash
GET /productos/buscar?term=iPhone
```

#### 3. **Gestión de Clientes**

**Crear Cliente:**
```bash
POST /usuarios
Content-Type: application/json

{
    "nombre": "Juan Pérez",
    "email": "juan.perez@example.com",
    "telefono": "+56912345678",
    "pais": "Chile",
    "ciudad": "Santiago"
}
```

**Buscar Clientes por Nombre:**
```bash
GET /usuarios/buscar?nombre=Juan
```

**Obtener Clientes Ordenados:**
```bash
GET /usuarios/ordenados
```

### Casos de Uso Comunes

#### 1. **Flujo de Compra Completo**

1. **Listar Categorías**: `GET /categorias`
2. **Listar Productos por Categoría**: `GET /productos/categoria/{nombre}`
3. **Ver Detalles del Producto**: `GET /productos/{id}`
4. **Crear Cliente**: `POST /usuarios`
5. **Crear Pedido**: `POST /pedidos`

#### 2. **Gestión de Inventario**

1. **Listar Productos**: `GET /productos`
2. **Actualizar Stock**: `PUT /productos/{id}`
3. **Buscar Productos con Bajo Stock**: Custom query
4. **Agregar Nuevos Productos**: `POST /productos`

#### 3. **Análisis de Ventas**

1. **Obtener Pedidos por Rango de Fechas**: `GET /pedidos/rango?start=X&end=Y`
2. **Obtener Pedidos por Cliente**: `GET /pedidos/cliente/{clienteId}`
3. **Obtener Pedidos por Producto**: `GET /pedidos/producto/{id}`

---

## 🧪 Testing

### Estructura de Tests

```
src/test/java/com/arka/arkavalenzuela/
├── domain/service/          # Tests de servicios de dominio
├── infrastructure/adapter/  # Tests de adaptadores
└── integration/            # Tests de integración
```

### Ejemplos de Tests

#### **Test de Servicio de Dominio:**
```java
@ExtendWith(MockitoExtension.class)
class ProductDomainServiceTest {
    
    @Mock
    private ProductRepositoryPort productRepository;
    
    @Mock
    private CategoryRepositoryPort categoryRepository;
    
    @InjectMocks
    private ProductDomainService productService;
    
    @Test
    void shouldCreateProduct() {
        // Given
        Product product = new Product();
        product.setNombre("Test Product");
        // ... setup
        
        // When
        Product result = productService.createProduct(product);
        
        // Then
        assertThat(result.getNombre()).isEqualTo("Test Product");
    }
}
```

### Configuración para Tests

#### **application-test.properties**
```properties
# Configuración para tests con MySQL
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/arkabd?useSSL=false&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=arkauser
spring.datasource.password=Clave2025*

# Configuración JPA para tests
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

---

## 📈 Beneficios de la Arquitectura Hexagonal

### ✅ **Ventajas Implementadas**

1. **Separación de Responsabilidades**
   - El dominio está completamente aislado de detalles técnicos
   - Cada capa tiene una responsabilidad específica

2. **Testabilidad Mejorada**
   - Fácil creación de tests unitarios para la lógica de negocio
   - Uso de mocks para dependencias externas

3. **Flexibilidad Tecnológica**
   - Fácil cambio de base de datos (MySQL → PostgreSQL)
   - Posibilidad de agregar nuevos adaptadores (GraphQL, gRPC)

4. **Mantenibilidad**
   - Código más organizado y fácil de entender
   - Cambios en infraestructura no afectan el dominio

5. **Cumplimiento SOLID**
   - **Single Responsibility**: Cada clase tiene una responsabilidad
   - **Open/Closed**: Abierto para extensión, cerrado para modificación
   - **Liskov Substitution**: Los adaptadores son intercambiables
   - **Interface Segregation**: Interfaces específicas y pequeñas
   - **Dependency Inversion**: Dependencias hacia abstracciones

### 🔄 **Flujo de Datos**

```
📱 Cliente
    ↓
🌐 REST Controller (Infrastructure/In)
    ↓
🔄 Web Mapper (DTO → Domain)
    ↓
⚙️ Domain Service (Business Logic)
    ↓
🔌 Repository Port (Interface)
    ↓
💾 Persistence Adapter (Infrastructure/Out)
    ↓
🔄 Persistence Mapper (Domain → Entity)
    ↓
📊 JPA Repository
    ↓
🗄️ Base de Datos MySQL
```

---

## 🛠️ Extensibilidad

### Agregar Nueva Funcionalidad

#### **Ejemplo: Agregar Gestión de Proveedores**

1. **Crear Modelo de Dominio**: `Supplier.java`
2. **Definir Use Case**: `SupplierUseCase.java`
3. **Definir Repository Port**: `SupplierRepositoryPort.java`
4. **Implementar Servicio**: `SupplierDomainService.java`
5. **Crear Entidad JPA**: `SupplierEntity.java`
6. **Crear Repositorio JPA**: `SupplierJpaRepository.java`
7. **Crear Adaptador**: `SupplierPersistenceAdapter.java`
8. **Crear DTO y Mapper**: `SupplierDto.java`, `SupplierWebMapper.java`
9. **Crear Controlador**: `SupplierController.java`
10. **Configurar Bean**: Agregar a `BeanConfiguration.java`

### Posibles Extensiones

- **Sistema de Autenticación** (JWT, OAuth2)
- **Sistema de Notificaciones** (Email, SMS)
- **Integración con Pagos** (PayPal, Stripe)
- **Sistema de Reportes** (PDF, Excel)
- **Cache Redis** para mejor performance
- **API GraphQL** como alternativa a REST
- **Microservicios** dividiendo por contextos

---

## 📚 Documentación Adicional

### Archivos de Documentación

- **HEXAGONAL_ARCHITECTURE.md** - Guía detallada de la arquitectura
- **build.gradle** - Configuración de dependencias
- **HELP.md** - Documentación de Spring Boot

### Recursos Útiles

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/books/evans.html)

---