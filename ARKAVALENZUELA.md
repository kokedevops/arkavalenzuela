# 📋 Proyecto Arka Valenzuela - Documentación Completa

## 📖 Descripción General

**Arka Valenzuela** es una aplicación de comercio electrónico desarrollada en **Spring Boot** que implementa la **Arquitectura Hexagonal** (Ports and Adapters). El sistema gestiona productos, categorías, clientes, pedidos y carritos de compra con una separación clara entre la lógica de negocio y los detalles técnicos.

---

## 🏗️ Arquitectura del Sistema

### Tecnologías Utilizadas
- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **MySQL** (Base de datos principal)
- **H2** (Base de datos para testing)
- **Gradle** (Gestión de dependencias)

### Patrón Arquitectónico
- **Arquitectura Hexagonal** (Clean Architecture)
- **Domain-Driven Design (DDD)**
- **Principios SOLID**

---

## 📁 Estructura del Proyecto

```
src/main/java/com/arka/arkavalenzuela/
├── 🟡 DOMAIN (Núcleo del Negocio)
├── 🟢 APPLICATION (Casos de Uso)
├── 🔵 INFRASTRUCTURE (Detalles Técnicos)
└── 🔴 MAIN (Punto de Entrada)
```

---

## 🟡 CAPA DE DOMINIO

### 📊 Modelos de Dominio

#### 1. **Product.java**
```java
public class Product {
    private Long id;
    private String nombre;
    private String descripcion;
    private Category categoria;
    private String marca;
    private BigDecimal precioUnitario;
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