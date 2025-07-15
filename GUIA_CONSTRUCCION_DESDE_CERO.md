# 🏗️ Guía Completa: Creando Proyecto Arka Valenzuela desde Cero

## 📖 Introducción

Esta guía te llevará paso a paso desde un proyecto vacío hasta una aplicación completa con **Arquitectura Hexagonal**. Aprenderás a:

- ✅ Configurar un proyecto Spring Boot con Gradle
- ✅ Implementar Arquitectura Hexagonal desde cero
- ✅ Separar correctamente las capas (Domain, Application, Infrastructure)
- ✅ Crear APIs REST funcionales
- ✅ Configurar base de datos MySQL
- ✅ Implementar patrones Ports & Adapters

---

## 🎯 Resultado Final

Al finalizar tendrás:
- 🏗️ **Arquitectura Hexagonal** completamente implementada
- 🗃️ **Base de datos MySQL** con 5 entidades relacionadas
- 🌐 **API REST** con endpoints CRUD completos
- 🧪 **Tests** unitarios y de integración
- 📚 **Documentación** completa del sistema

---

## 📋 Prerequisitos

### 🛠️ Software Necesario
```bash
# Java 21 o superior
java -version

# Gradle 8.0+ (o usar wrapper)
gradle -version

# MySQL 8.0+
mysql --version

# Git
git --version

# IDE (IntelliJ IDEA, VS Code, Eclipse)
```

### 🗃️ Base de Datos
```sql
-- Crear base de datos
CREATE DATABASE arkabd;
CREATE USER 'arkauser'@'localhost' IDENTIFIED BY 'Clave2025*';
GRANT ALL PRIVILEGES ON arkabd.* TO 'arkauser'@'localhost';
FLUSH PRIVILEGES;
```

---

## 🚀 PASO 1: Iniciando el Proyecto

### 1.1 Crear Proyecto con Gradle

```bash
# Crear directorio del proyecto
mkdir arkavalenzuela
cd arkavalenzuela

# Inicializar proyecto Gradle
gradle init

# Seleccionar opciones:
# 1: application
# 2: Java
# 3: Groovy (para build.gradle)
# 4: JUnit Jupiter
# Nombre del proyecto: arkavalenzuela
# Package: com.arka.arkavalenzuela
```

### 1.2 Configurar build.gradle

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.3'
    id 'io.spring.dependency-management' version '1.1.6'
    id 'war'
}

group = 'com.arka'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // Base de datos
    runtimeOnly 'com.mysql:mysql-connector-j'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'com.h2database:h2'
    
    // Utilidades
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    // WAR deployment
    providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

### 1.3 Estructura Inicial de Directorios

```bash
# Crear estructura básica
mkdir -p src/main/java/com/arka/arkavalenzuela
mkdir -p src/main/resources
mkdir -p src/test/java/com/arka/arkavalenzuela
mkdir -p src/test/resources
```

---

## 🚀 PASO 2: Configuración Base de Spring Boot

### 2.1 Crear Aplicación Principal

**`src/main/java/com/arka/arkavalenzuela/ArkajvalenzuelaApplication.java`**
```java
package com.arka.arkavalenzuela;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArkajvalenzuelaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArkajvalenzuelaApplication.class, args);
    }
}
```

### 2.2 Configurar Propiedades

**`src/main/resources/application.properties`**
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

# Puerto del servidor
server.port=8080
```

### 2.3 Configuración para Tests

**`src/test/resources/application-test.properties`**
```properties
# Base de datos H2 para tests
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Configuración JPA para tests
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
```

### 2.4 Probar Configuración Inicial

```bash
# Compilar proyecto
./gradlew build

# Ejecutar aplicación
./gradlew bootRun

# Verificar que inicie en http://localhost:8080
curl http://localhost:8080
```

---

## 🏗️ PASO 3: Implementando Arquitectura Hexagonal

### 3.1 Crear Estructura de Directorios Hexagonal

```bash
# Crear estructura hexagonal
mkdir -p src/main/java/com/arka/arkavalenzuela/domain/model
mkdir -p src/main/java/com/arka/arkavalenzuela/domain/port/in
mkdir -p src/main/java/com/arka/arkavalenzuela/domain/port/out
mkdir -p src/main/java/com/arka/arkavalenzuela/application/usecase
mkdir -p src/main/java/com/arka/arkavalenzuela/infrastructure/adapter/in/web
mkdir -p src/main/java/com/arka/arkavalenzuela/infrastructure/adapter/in/web/dto
mkdir -p src/main/java/com/arka/arkavalenzuela/infrastructure/adapter/in/web/mapper
mkdir -p src/main/java/com/arka/arkavalenzuela/infrastructure/adapter/out/persistence/entity
mkdir -p src/main/java/com/arka/arkavalenzuela/infrastructure/adapter/out/persistence/repository
mkdir -p src/main/java/com/arka/arkavalenzuela/infrastructure/adapter/out/persistence/mapper
mkdir -p src/main/java/com/arka/arkavalenzuela/infrastructure/config
```

### 3.2 Explicación de la Estructura

```
🟡 DOMAIN (Núcleo del Negocio)
├── model/          → Entidades de dominio puras
├── port/in/        → Casos de uso (lo que puede hacer el sistema)
└── port/out/       → Contratos para datos externos

🟢 APPLICATION (Casos de Uso)
└── usecase/        → Servicios que implementan la lógica de aplicación

🔵 INFRASTRUCTURE (Detalles Técnicos)
├── adapter/in/     → REST controllers, DTOs
├── adapter/out/    → JPA entities, repositories
└── config/         → Configuración Spring
```

---

## 🟡 PASO 4: Creando el DOMINIO

### 4.1 Modelos de Dominio

#### **Category.java** (Empezar por la entidad más simple)
```java
package com.arka.arkavalenzuela.domain.model;

public class Category {
    private Long id;
    private String nombre;

    // Constructores
    public Category() {}

    public Category(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Lógica de negocio
    public boolean isValidName() {
        return nombre != null && !nombre.trim().isEmpty();
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return "Category{id=" + id + ", nombre='" + nombre + "'}";
    }
}
```

#### **Product.java**
```java
package com.arka.arkavalenzuela.domain.model;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String nombre;
    private String descripcion;
    private Category categoria;
    private String marca;
    private BigDecimal precioUnitario;
    private Integer stock;

    // Constructores
    public Product() {}

    // Lógica de negocio pura
    public boolean hasValidPrice() {
        return precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isInStock() {
        return stock != null && stock > 0;
    }

    public boolean isAvailable() {
        return hasValidPrice() && isInStock();
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Category getCategoria() { return categoria; }
    public void setCategoria(Category categoria) { this.categoria = categoria; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
```

#### **Customer.java**
```java
package com.arka.arkavalenzuela.domain.model;

public class Customer {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String ciudad;
    private String pais;

    // Constructores
    public Customer() {}

    // Lógica de negocio
    public boolean isValidEmail() {
        return email != null && email.contains("@") && email.contains(".");
    }

    public boolean hasCompleteProfile() {
        return nombre != null && email != null && telefono != null;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}
```

### 4.2 Puertos de Entrada (Use Cases)

#### **CategoryUseCase.java**
```java
package com.arka.arkavalenzuela.domain.port.in;

import com.arka.arkavalenzuela.domain.model.Category;
import java.util.List;

public interface CategoryUseCase {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
}
```

#### **ProductUseCase.java**
```java
package com.arka.arkavalenzuela.domain.port.in;

import com.arka.arkavalenzuela.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;

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

### 4.3 Puertos de Salida (Repository Ports)

#### **CategoryRepositoryPort.java**
```java
package com.arka.arkavalenzuela.domain.port.out;

import com.arka.arkavalenzuela.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Category save(Category category);
    void deleteById(Long id);
    boolean existsById(Long id);
}
```

#### **ProductRepositoryPort.java**
```java
package com.arka.arkavalenzuela.domain.port.out;

import com.arka.arkavalenzuela.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

---

## 🟢 PASO 5: Creando la APLICACIÓN

### 5.1 Servicios de Aplicación

#### **CategoryApplicationService.java**
```java
package com.arka.arkavalenzuela.application.usecase;

import com.arka.arkavalenzuela.domain.model.Category;
import com.arka.arkavalenzuela.domain.port.in.CategoryUseCase;
import com.arka.arkavalenzuela.domain.port.out.CategoryRepositoryPort;

import java.util.List;

public class CategoryApplicationService implements CategoryUseCase {
    
    private final CategoryRepositoryPort categoryRepository;

    public CategoryApplicationService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Override
    public Category createCategory(Category category) {
        validateCategory(category);
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        validateCategory(category);
        category.setId(id);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    // Validaciones de aplicación
    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        if (category.getNombre() == null || category.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        
        if (category.getNombre().length() > 100) {
            throw new IllegalArgumentException("Category name cannot exceed 100 characters");
        }
    }
}
```

#### **ProductApplicationService.java**
```java
package com.arka.arkavalenzuela.application.usecase;

import com.arka.arkavalenzuela.domain.model.Product;
import com.arka.arkavalenzuela.domain.port.in.ProductUseCase;
import com.arka.arkavalenzuela.domain.port.out.ProductRepositoryPort;
import com.arka.arkavalenzuela.domain.port.out.CategoryRepositoryPort;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductApplicationService implements ProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;

    public ProductApplicationService(ProductRepositoryPort productRepository, 
                                   CategoryRepositoryPort categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public Product createProduct(Product product) {
        validateProduct(product);
        validateCategoryExists(product.getCategoria().getId());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        validateProduct(product);
        product.setId(id);
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoriaNombre(categoryName);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNombreContainingIgnoreCase(name);
    }

    @Override
    public List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
        return productRepository.findByPriceRange(min, max);
    }

    @Override
    public List<Product> getAllProductsSorted() {
        return productRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Product::getNombre, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    // Validaciones de aplicación
    private void validateProduct(Product product) {
        if (product.getNombre() == null || product.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (!product.hasValidPrice()) {
            throw new IllegalArgumentException("Product must have a valid price");
        }
        if (product.getCategoria() == null || product.getCategoria().getId() == null) {
            throw new IllegalArgumentException("Product must have a valid category");
        }
    }

    private void validateCategoryExists(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
    }
}
```

---

## 🔵 PASO 6: Creando la INFRAESTRUCTURA

### 6.1 Entidades JPA

#### **CategoryJpaEntity.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class CategoryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    // Constructores
    public CategoryJpaEntity() {}

    public CategoryJpaEntity(String nombre) {
        this.nombre = nombre;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
```

#### **ProductJpaEntity.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class ProductJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoryJpaEntity categoria;

    @Column(name = "marca", length = 255)
    private String marca;

    @Column(name = "precio_unitario", precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "stock")
    private Integer stock;

    // Constructores
    public ProductJpaEntity() {}

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public CategoryJpaEntity getCategoria() { return categoria; }
    public void setCategoria(CategoryJpaEntity categoria) { this.categoria = categoria; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
```

### 6.2 Repositorios JPA

#### **CategoryJpaRepository.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository;

import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {
}
```

#### **ProductJpaRepository.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository;

import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
    
    List<ProductJpaEntity> findByCategoriaNombre(String categoryName);
    
    List<ProductJpaEntity> findByNombreContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM ProductJpaEntity p WHERE p.precioUnitario BETWEEN :min AND :max")
    List<ProductJpaEntity> findByPriceRange(@Param("min") BigDecimal min, 
                                          @Param("max") BigDecimal max);
}
```

### 6.3 Mappers de Persistencia

#### **CategoryPersistenceMapper.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper;

import com.arka.arkavalenzuela.domain.model.Category;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.CategoryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryPersistenceMapper {
    
    public CategoryJpaEntity toEntity(Category category) {
        if (category == null) {
            return null;
        }
        
        CategoryJpaEntity entity = new CategoryJpaEntity();
        entity.setId(category.getId());
        entity.setNombre(category.getNombre());
        return entity;
    }
    
    public Category toDomain(CategoryJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Category category = new Category();
        category.setId(entity.getId());
        category.setNombre(entity.getNombre());
        return category;
    }
}
```

#### **ProductPersistenceMapper.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper;

import com.arka.arkavalenzuela.domain.model.Product;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.ProductJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {
    
    private final CategoryPersistenceMapper categoryMapper;

    public ProductPersistenceMapper(CategoryPersistenceMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }
    
    public ProductJpaEntity toEntity(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(product.getId());
        entity.setNombre(product.getNombre());
        entity.setDescripcion(product.getDescripcion());
        entity.setCategoria(categoryMapper.toEntity(product.getCategoria()));
        entity.setMarca(product.getMarca());
        entity.setPrecioUnitario(product.getPrecioUnitario());
        entity.setStock(product.getStock());
        return entity;
    }
    
    public Product toDomain(ProductJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Product product = new Product();
        product.setId(entity.getId());
        product.setNombre(entity.getNombre());
        product.setDescripcion(entity.getDescripcion());
        product.setCategoria(categoryMapper.toDomain(entity.getCategoria()));
        product.setMarca(entity.getMarca());
        product.setPrecioUnitario(entity.getPrecioUnitario());
        product.setStock(entity.getStock());
        return product;
    }
}
```

### 6.4 Adaptadores de Persistencia

#### **CategoryPersistenceAdapter.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence;

import com.arka.arkavalenzuela.domain.model.Category;
import com.arka.arkavalenzuela.domain.port.out.CategoryRepositoryPort;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.CategoryJpaEntity;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper.CategoryPersistenceMapper;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository.CategoryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {
    
    private final CategoryJpaRepository jpaRepository;
    private final CategoryPersistenceMapper mapper;

    public CategoryPersistenceAdapter(CategoryJpaRepository jpaRepository, 
                                    CategoryPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Category> findAll() {
        List<CategoryJpaEntity> entities = jpaRepository.findAll();
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = mapper.toEntity(category);
        CategoryJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
```

#### **ProductPersistenceAdapter.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.out.persistence;

import com.arka.arkavalenzuela.domain.model.Product;
import com.arka.arkavalenzuela.domain.port.out.ProductRepositoryPort;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.entity.ProductJpaEntity;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;
import com.arka.arkavalenzuela.infrastructure.adapter.out.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductPersistenceAdapter implements ProductRepositoryPort {
    
    private final ProductJpaRepository jpaRepository;
    private final ProductPersistenceMapper mapper;

    public ProductPersistenceAdapter(ProductJpaRepository jpaRepository, 
                                   ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Product> findAll() {
        List<ProductJpaEntity> entities = jpaRepository.findAll();
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = mapper.toEntity(product);
        ProductJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Product> findByCategoriaNombre(String categoryName) {
        List<ProductJpaEntity> entities = jpaRepository.findByCategoriaNombre(categoryName);
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNombreContainingIgnoreCase(String name) {
        List<ProductJpaEntity> entities = jpaRepository.findByNombreContainingIgnoreCase(name);
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        List<ProductJpaEntity> entities = jpaRepository.findByPriceRange(min, max);
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
```

---

## 🌐 PASO 7: Creando la API REST

### 7.1 DTOs

#### **CategoryDTO.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.in.web.dto;

public class CategoryDTO {
    private Long id;
    private String nombre;

    // Constructores
    public CategoryDTO() {}

    public CategoryDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
```

#### **ProductDTO.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public class ProductDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long categoriaId;  // Solo el ID, no el objeto completo
    private String marca;
    private BigDecimal precioUnitario;
    private Integer stock;

    // Constructores
    public ProductDTO() {}

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
```

### 7.2 Web Mappers

#### **CategoryWebMapper.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.in.web.mapper;

import com.arka.arkavalenzuela.domain.model.Category;
import com.arka.arkavalenzuela.infrastructure.adapter.in.web.dto.CategoryDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryWebMapper {
    
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDTO(category.getId(), category.getNombre());
    }
    
    public Category toDomain(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Category category = new Category();
        category.setId(dto.getId());
        category.setNombre(dto.getNombre());
        return category;
    }
    
    public List<CategoryDTO> toDTO(List<Category> categories) {
        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
```

#### **ProductWebMapper.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.in.web.mapper;

import com.arka.arkavalenzuela.domain.model.Category;
import com.arka.arkavalenzuela.domain.model.Product;
import com.arka.arkavalenzuela.domain.port.out.CategoryRepositoryPort;
import com.arka.arkavalenzuela.infrastructure.adapter.in.web.dto.ProductDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductWebMapper {
    
    private final CategoryRepositoryPort categoryRepository;

    public ProductWebMapper(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setNombre(product.getNombre());
        dto.setDescripcion(product.getDescripcion());
        dto.setCategoriaId(product.getCategoria() != null ? product.getCategoria().getId() : null);
        dto.setMarca(product.getMarca());
        dto.setPrecioUnitario(product.getPrecioUnitario());
        dto.setStock(product.getStock());
        return dto;
    }
    
    public Product toDomain(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Product product = new Product();
        product.setId(dto.getId());
        product.setNombre(dto.getNombre());
        product.setDescripcion(dto.getDescripcion());
        product.setMarca(dto.getMarca());
        product.setPrecioUnitario(dto.getPrecioUnitario());
        product.setStock(dto.getStock());
        
        // Cargar la categoría completa si se proporciona el ID
        if (dto.getCategoriaId() != null) {
            Category categoria = categoryRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoriaId()));
            product.setCategoria(categoria);
        }
        
        return product;
    }
    
    public List<ProductDTO> toDTO(List<Product> products) {
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
```

### 7.3 Controladores REST

#### **CategoryController.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.in.web;

import com.arka.arkavalenzuela.domain.model.Category;
import com.arka.arkavalenzuela.domain.port.in.CategoryUseCase;
import com.arka.arkavalenzuela.infrastructure.adapter.in.web.dto.CategoryDTO;
import com.arka.arkavalenzuela.infrastructure.adapter.in.web.mapper.CategoryWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoryController {
    
    private final CategoryUseCase categoryUseCase;
    private final CategoryWebMapper webMapper;

    public CategoryController(CategoryUseCase categoryUseCase, CategoryWebMapper webMapper) {
        this.categoryUseCase = categoryUseCase;
        this.webMapper = webMapper;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryUseCase.getAllCategories();
        List<CategoryDTO> categoriesDTO = webMapper.toDTO(categories);
        return ResponseEntity.ok(categoriesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Category category = categoryUseCase.getCategoryById(id);
        CategoryDTO categoryDTO = webMapper.toDTO(category);
        return ResponseEntity.ok(categoryDTO);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = webMapper.toDomain(categoryDTO);
        Category savedCategory = categoryUseCase.createCategory(category);
        CategoryDTO savedCategoryDTO = webMapper.toDTO(savedCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, 
                                                    @RequestBody CategoryDTO categoryDTO) {
        Category category = webMapper.toDomain(categoryDTO);
        Category updatedCategory = categoryUseCase.updateCategory(id, category);
        CategoryDTO updatedCategoryDTO = webMapper.toDTO(updatedCategory);
        return ResponseEntity.ok(updatedCategoryDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryUseCase.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
```

#### **ProductController.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.in.web;

import com.arka.arkavalenzuela.domain.model.Product;
import com.arka.arkavalenzuela.domain.port.in.ProductUseCase;
import com.arka.arkavalenzuela.infrastructure.adapter.in.web.dto.ProductDTO;
import com.arka.arkavalenzuela.infrastructure.adapter.in.web.mapper.ProductWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductController {
    
    private final ProductUseCase productUseCase;
    private final ProductWebMapper webMapper;

    public ProductController(ProductUseCase productUseCase, ProductWebMapper webMapper) {
        this.productUseCase = productUseCase;
        this.webMapper = webMapper;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productUseCase.getAllProducts();
        List<ProductDTO> productsDTO = webMapper.toDTO(products);
        return ResponseEntity.ok(productsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productUseCase.getProductById(id);
        ProductDTO productDTO = webMapper.toDTO(product);
        return ResponseEntity.ok(productDTO);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        Product product = webMapper.toDomain(productDTO);
        Product savedProduct = productUseCase.createProduct(product);
        ProductDTO savedProductDTO = webMapper.toDTO(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProductDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, 
                                                  @RequestBody ProductDTO productDTO) {
        Product product = webMapper.toDomain(productDTO);
        Product updatedProduct = productUseCase.updateProduct(id, product);
        ProductDTO updatedProductDTO = webMapper.toDTO(updatedProduct);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{nombre}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable String nombre) {
        List<Product> products = productUseCase.getProductsByCategory(nombre);
        List<ProductDTO> productsDTO = webMapper.toDTO(products);
        return ResponseEntity.ok(productsDTO);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String term) {
        List<Product> products = productUseCase.searchProductsByName(term);
        List<ProductDTO> productsDTO = webMapper.toDTO(products);
        return ResponseEntity.ok(productsDTO);
    }

    @GetMapping("/ordenados")
    public ResponseEntity<List<ProductDTO>> getAllProductsSorted() {
        List<Product> products = productUseCase.getAllProductsSorted();
        List<ProductDTO> productsDTO = webMapper.toDTO(products);
        return ResponseEntity.ok(productsDTO);
    }

    @GetMapping("/rango")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @RequestParam BigDecimal min, 
            @RequestParam BigDecimal max) {
        List<Product> products = productUseCase.getProductsByPriceRange(min, max);
        List<ProductDTO> productsDTO = webMapper.toDTO(products);
        return ResponseEntity.ok(productsDTO);
    }
}
```

---

## ⚙️ PASO 8: Configuración de Beans

### 8.1 BeanConfiguration.java

```java
package com.arka.arkavalenzuela.infrastructure.config;

import com.arka.arkavalenzuela.domain.port.in.*;
import com.arka.arkavalenzuela.domain.port.out.*;
import com.arka.arkavalenzuela.application.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

---

## 🧪 PASO 9: Testing

### 9.1 Test de Compilación

```bash
# Compilar el proyecto
./gradlew build

# Si hay errores, revisar:
# 1. Imports correctos
# 2. Clases en paquetes correctos
# 3. Dependencias en build.gradle
```

### 9.2 Test de Inicio

```bash
# Iniciar aplicación
./gradlew bootRun

# Verificar logs:
# - Spring Boot inicia correctamente
# - Conexión a MySQL exitosa
# - Tomcat en puerto 8080
```

### 9.3 Test de Base de Datos

```bash
# Verificar que las tablas se crean
mysql -u jvalenzuela -p

USE arkabd;
SHOW TABLES;
# Debe mostrar: categorias, productos

DESCRIBE categorias;
DESCRIBE productos;
```

### 9.4 Test de API

```bash
# Test de categorías
curl -X GET http://localhost:8080/categorias

# Crear categoría
curl -X POST http://localhost:8080/categorias \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Electrónicos"}'

# Crear producto
curl -X POST http://localhost:8080/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"iPhone 15",
    "descripcion":"Smartphone Apple",
    "categoriaId":1,
    "marca":"Apple",
    "precioUnitario":999.99,
    "stock":10
  }'
```

---

## 🚀 PASO 10: Extendiendo el Sistema

### 10.1 Agregar Customer (Siguiendo el mismo patrón)

#### **1. Domain Model**
```java
// domain/model/Customer.java
public class Customer {
    private Long id;
    private String nombre;
    private String email;
    // ... resto de campos y lógica
}
```

#### **2. Use Case**
```java
// domain/port/in/CustomerUseCase.java
public interface CustomerUseCase {
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    // ... resto de métodos
}
```

#### **3. Repository Port**
```java
// domain/port/out/CustomerRepositoryPort.java
public interface CustomerRepositoryPort {
    List<Customer> findAll();
    Optional<Customer> findById(Long id);
    // ... resto de métodos
}
```

#### **4. Application Service**
```java
// application/usecase/CustomerApplicationService.java
public class CustomerApplicationService implements CustomerUseCase {
    // ... implementación
}
```

#### **5. JPA Entity**
```java
// infrastructure/.../entity/CustomerJpaEntity.java
@Entity
@Table(name = "clientes")
public class CustomerJpaEntity {
    // ... campos JPA
}
```

#### **6. JPA Repository**
```java
// infrastructure/.../repository/CustomerJpaRepository.java
public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, Long> {
    // ... métodos específicos
}
```

#### **7. Persistence Adapter**
```java
// infrastructure/.../CustomerPersistenceAdapter.java
@Component
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {
    // ... implementación
}
```

#### **8. Web Components**
```java
// infrastructure/.../web/dto/CustomerDTO.java
// infrastructure/.../web/mapper/CustomerWebMapper.java
// infrastructure/.../web/CustomerController.java
```

#### **9. Bean Configuration**
```java
// En BeanConfiguration.java
@Bean
public CustomerUseCase customerUseCase(CustomerRepositoryPort customerRepository) {
    return new CustomerApplicationService(customerRepository);
}
```

### 10.2 Repetir para Order y Cart

El mismo patrón se aplica para `Order` y `Cart`, creando las 9 partes para cada entidad.

---

## 📊 PASO 11: Verificación Final

### 11.1 Estructura Completa
```
src/main/java/com/arka/arkavalenzuela/
├── 🟡 domain/
│   ├── model/ (5 clases)
│   └── port/
│       ├── in/ (5 interfaces)
│       └── out/ (5 interfaces)
├── 🟢 application/
│   └── usecase/ (5 servicios)
├── 🔵 infrastructure/
│   ├── adapter/
│   │   ├── in/web/ (controladores, DTOs, mappers)
│   │   └── out/persistence/ (entidades, repos, adaptadores)
│   └── config/ (configuración)
└── ArkajvalenzuelaApplication.java
```

### 11.2 Tests Funcionales

```bash
# 1. Compilación exitosa
./gradlew build

# 2. Inicio sin errores
./gradlew bootRun

# 3. API funcionando
curl http://localhost:8080/categorias
curl http://localhost:8080/productos

# 4. Base de datos actualizada
mysql -u jvalenzuela -p -e "USE arkabd; SHOW TABLES;"
```

### 11.3 Beneficios Logrados

✅ **Arquitectura Hexagonal Completa**
- Dominio separado de infraestructura
- Puertos y adaptadores implementados
- Inversión de dependencias correcta

✅ **Mantenibilidad**
- Código organizado por responsabilidades
- Fácil testing y modificación
- Patrones consistentes

✅ **Flexibilidad**
- Intercambio de adaptadores
- Múltiples interfaces posibles
- Extensibilidad garantizada

---

## 🎯 Puntos Clave Aprendidos

### 1. **Orden de Implementación**
1. 🟡 Domain (models, ports)
2. 🟢 Application (use cases)
3. 🔵 Infrastructure (adapters)
4. ⚙️ Configuration (beans)

### 2. **Separación de Responsabilidades**
- **Domain**: Qué hace el sistema
- **Application**: Cómo se orquesta
- **Infrastructure**: Con qué tecnologías

### 3. **Patrones Críticos**
- **Ports & Adapters**: Interfaces para aislar dominio
- **Dependency Inversion**: Dependencias hacia abstracciones
- **Single Responsibility**: Una responsabilidad por clase

### 4. **Flujo de Datos**
```
HTTP → Controller → DTO→Domain → UseCase → 
Port → Adapter → Entity → Database → Response
```

---

## 📚 Recursos Adicionales

### Documentación
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)

### Comandos Útiles
```bash
# Desarrollo
./gradlew bootRun          # Ejecutar app
./gradlew build            # Compilar
./gradlew test            # Tests
./gradlew clean           # Limpiar

# Base de datos
mysql -u usuario -p        # Conectar
SHOW DATABASES;           # Ver BDs
USE arkabd;              # Usar BD
SHOW TABLES;             # Ver tablas
```

---

## 🏆 Conclusión

Has creado exitosamente una aplicación completa con **Arquitectura Hexagonal**:

1. ✅ **Estructura clara** en 3 capas bien definidas
2. ✅ **Separación de responsabilidades** correcta
3. ✅ **API REST** completamente funcional
4. ✅ **Base de datos** integrada y funcionando
5. ✅ **Patrones** de diseño implementados correctamente
6. ✅ **Preparado** para crecimiento y mantenimiento

**¡El proyecto está listo para desarrollo continuo y producción!**
