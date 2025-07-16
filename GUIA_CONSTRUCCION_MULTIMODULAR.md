# 🏗️ GUÍA DE CONSTRUCCIÓN: PROYECTO MULTIMODULAR CON ARQUITECTURA HEXAGONAL

## 📋 Tabla de Contenidos
1. [Descripción General](#descripción-general)
2. [Prerrequisitos](#prerrequisitos)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Paso 1: Configuración del Proyecto Padre](#paso-1-configuración-del-proyecto-padre)
5. [Paso 2: Módulo Domain (Núcleo)](#paso-2-módulo-domain-núcleo)
6. [Paso 3: Módulo Application (Casos de Uso)](#paso-3-módulo-application-casos-de-uso)
7. [Paso 4: Módulo Infrastructure (Adaptadores)](#paso-4-módulo-infrastructure-adaptadores)
8. [Paso 5: Módulo Web (Punto de Entrada)](#paso-5-módulo-web-punto-de-entrada)
9. [Paso 6: Configuración y Validación](#paso-6-configuración-y-validación)
10. [Comandos de Construcción y Ejecución](#comandos-de-construcción-y-ejecución)

---

## 🎯 Descripción General

Esta guía te enseña cómo construir desde cero un proyecto Spring Boot con **arquitectura multimodular** siguiendo los principios de **Arquitectura Hexagonal (Clean Architecture)**. El resultado será un proyecto modular, mantenible y escalable.

### 🏛️ Principios de la Arquitectura Hexagonal
- **Dominio puro**: Sin dependencias externas
- **Inversión de dependencias**: El dominio define las interfaces
- **Separación de responsabilidades**: Cada módulo tiene un propósito específico
- **Testabilidad**: Módulos independientes y fáciles de probar

---

## 🔧 Prerrequisitos

- **Java 21+**
- **Gradle 8.x**
- **Spring Boot 3.5.x**
- **MySQL 8.x** (o H2 para testing)
- **IDE**: IntelliJ IDEA, VSCode, etc.

---

## 📁 Estructura del Proyecto

```
mi-proyecto-multimodular/
├── settings.gradle                 # Configuración de módulos
├── build.gradle                    # Configuración padre
├── domain/                         # 🟢 Módulo Domain
│   ├── src/main/java/
│   │   └── com/empresa/domain/
│   │       ├── model/              # Entidades de dominio
│   │       └── port/
│   │           ├── in/             # Casos de uso (interfaces)
│   │           └── out/            # Repositorios (interfaces)
│   └── build.gradle
├── application/                    # 🟡 Módulo Application
│   ├── src/main/java/
│   │   └── com/empresa/application/
│   │       └── usecase/            # Implementaciones de casos de uso
│   └── build.gradle
├── infrastructure/                 # 🔵 Módulo Infrastructure
│   ├── src/main/java/
│   │   └── com/empresa/infrastructure/
│   │       ├── adapter/
│   │       │   ├── in/web/         # Controladores REST
│   │       │   └── out/persistence/ # Repositorios JPA
│   │       └── config/             # Configuración Spring
│   └── build.gradle
└── web/                           # 🔴 Módulo Web
    ├── src/main/java/
    │   └── com/empresa/web/        # Aplicación principal
    ├── src/main/resources/
    │   └── application.properties
    └── build.gradle
```

---

## 📝 Paso 1: Configuración del Proyecto Padre

### 1.1 Crear `settings.gradle`
```gradle
rootProject.name = 'mi-proyecto-multimodular'

include 'domain'
include 'application'
include 'infrastructure'
include 'web'
```

### 1.2 Crear `build.gradle` (raíz)
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.3' apply false
    id 'io.spring.dependency-management' version '1.1.4' apply false
}

// Configuración común para todos los subproyectos
subprojects {
    apply plugin: 'java'
    apply plugin: 'io.spring.dependency-management'
    
    group = 'com.empresa'
    version = '1.0.0'
    
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
    
    // Configuración de Spring Boot BOM para gestionar versiones
    dependencyManagement {
        imports {
            mavenBom "org.springframework.boot:spring-boot-dependencies:3.5.3"
        }
    }
    
    repositories {
        mavenCentral()
    }
    
    // Dependencias comunes para testing
    dependencies {
        testImplementation 'org.junit.jupiter:junit-jupiter'
        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    }
    
    test {
        useJUnitPlatform()
    }
}
```

---

## 🟢 Paso 2: Módulo Domain (Núcleo)

### 2.1 Crear `domain/build.gradle`
```gradle
// Módulo de Dominio - Sin dependencias externas (Pure Java)
// Contiene las entidades de negocio y las interfaces (puertos)

dependencies {
    // ¡SIN DEPENDENCIAS EXTERNAS! Solo Java puro
}
```

### 2.2 Crear Entidades de Dominio

#### `domain/src/main/java/com/empresa/domain/model/Product.java`
```java
package com.empresa.domain.model;

import java.math.BigDecimal;

/**
 * Entidad de dominio: Producto
 * Representa un producto en el sistema de e-commerce
 */
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Category category;
    
    // Constructor por defecto
    public Product() {}
    
    // Constructor completo
    public Product(Long id, String name, String description, BigDecimal price, 
                   Integer stock, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
    
    // Métodos de negocio
    public boolean isAvailable() {
        return stock != null && stock > 0;
    }
    
    public void reduceStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (stock < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stock -= quantity;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
```

#### `domain/src/main/java/com/empresa/domain/model/Category.java`
```java
package com.empresa.domain.model;

/**
 * Entidad de dominio: Categoría
 */
public class Category {
    private Long id;
    private String name;
    private String description;
    
    // Constructores
    public Category() {}
    
    public Category(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
```

### 2.3 Crear Puertos (Interfaces)

#### `domain/src/main/java/com/empresa/domain/port/in/ProductUseCase.java`
```java
package com.empresa.domain.port.in;

import com.empresa.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada: Casos de uso de Producto
 * Define las operaciones de negocio disponibles
 */
public interface ProductUseCase {
    
    List<Product> getAllProducts();
    
    Optional<Product> getProductById(Long id);
    
    List<Product> getProductsByCategory(String categoryName);
    
    Product createProduct(Product product);
    
    Product updateProduct(Long id, Product product);
    
    void deleteProduct(Long id);
    
    List<Product> searchProducts(String searchTerm);
    
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> getAllProductsSorted();
}
```

#### `domain/src/main/java/com/empresa/domain/port/out/ProductRepositoryPort.java`
```java
package com.empresa.domain.port.out;

import com.empresa.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: Repositorio de Producto
 * Define las operaciones de persistencia requeridas
 */
public interface ProductRepositoryPort {
    
    List<Product> findAll();
    
    Optional<Product> findById(Long id);
    
    List<Product> findByCategory(String categoryName);
    
    Product save(Product product);
    
    void deleteById(Long id);
    
    List<Product> findByNameContaining(String searchTerm);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findAllOrderByName();
}
```

---

## 🟡 Paso 3: Módulo Application (Casos de Uso)

### 3.1 Crear `application/build.gradle`
```gradle
// Módulo de Aplicación - Implementa los casos de uso
// Depende solo del módulo domain

dependencies {
    implementation project(':domain')
    
    // Testing
    testImplementation 'org.junit.jupiter:junit-jupiter'
}
```

### 3.2 Implementar Casos de Uso

#### `application/src/main/java/com/empresa/application/usecase/ProductApplicationService.java`
```java
package com.empresa.application.usecase;

import com.empresa.domain.model.Product;
import com.empresa.domain.port.in.ProductUseCase;
import com.empresa.domain.port.out.ProductRepositoryPort;
import com.empresa.domain.port.out.CategoryRepositoryPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación: Implementa los casos de uso de Producto
 * Contiene la lógica de aplicación sin dependencias de frameworks
 */
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
    public Optional<Product> getProductById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return productRepository.findById(id);
    }
    
    @Override
    public List<Product> getProductsByCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        return productRepository.findByCategory(categoryName);
    }
    
    @Override
    public Product createProduct(Product product) {
        validateProduct(product);
        
        // Validar que la categoría existe
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        }
        
        return productRepository.save(product);
    }
    
    @Override
    public Product updateProduct(Long id, Product product) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        
        // Verificar que el producto existe
        productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        validateProduct(product);
        product.setId(id);
        
        return productRepository.save(product);
    }
    
    @Override
    public void deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        
        // Verificar que el producto existe
        productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        productRepository.deleteById(id);
    }
    
    @Override
    public List<Product> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContaining(searchTerm.trim());
    }
    
    @Override
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("Price range cannot be null");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    @Override
    public List<Product> getAllProductsSorted() {
        return productRepository.findAllOrderByName();
    }
    
    // Método privado para validaciones
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
    }
}
```

---

## 🔵 Paso 4: Módulo Infrastructure (Adaptadores)

### 4.1 Crear `infrastructure/build.gradle`
```gradle
// Módulo de Infraestructura - Adaptadores para persistencia y web
// Implementa los puertos definidos en el dominio

dependencies {
    implementation project(':domain')
    implementation project(':application')
    
    // Spring Boot Data JPA para persistencia
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    // Spring Web para controladores REST
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework:spring-context'
    
    // Base de datos
    runtimeOnly 'com.mysql:mysql-connector-j'
    testRuntimeOnly 'com.h2database:h2'
    
    // Testing
    testImplementation 'org.junit.jupiter:junit-jupiter'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### 4.2 Crear Adaptadores de Persistencia

#### `infrastructure/src/main/java/com/empresa/infrastructure/adapter/out/persistence/entity/ProductEntity.java`
```java
package com.empresa.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad JPA: Producto
 * Representa la tabla productos en la base de datos
 */
@Entity
@Table(name = "productos")
public class ProductEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String name;
    
    @Column(name = "descripcion")
    private String description;
    
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "stock")
    private Integer stock;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoryEntity category;
    
    // Constructores
    public ProductEntity() {}
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public CategoryEntity getCategory() { return category; }
    public void setCategory(CategoryEntity category) { this.category = category; }
}
```

#### `infrastructure/src/main/java/com/empresa/infrastructure/adapter/out/persistence/repository/ProductJpaRepository.java`
```java
package com.empresa.infrastructure.adapter.out.persistence.repository;

import com.empresa.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio JPA: Producto
 */
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    
    @Query("SELECT p FROM ProductEntity p WHERE p.category.name = :categoryName")
    List<ProductEntity> findByCategoryName(@Param("categoryName") String categoryName);
    
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    
    List<ProductEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<ProductEntity> findAllByOrderByNameAsc();
}
```

#### `infrastructure/src/main/java/com/empresa/infrastructure/adapter/out/persistence/mapper/ProductMapper.java`
```java
package com.empresa.infrastructure.adapter.out.persistence.mapper;

import com.empresa.domain.model.Product;
import com.empresa.infrastructure.adapter.out.persistence.entity.ProductEntity;

/**
 * Mapper: Convierte entre entidades de dominio y entidades JPA
 */
public class ProductMapper {
    
    private static final CategoryMapper categoryMapper = new CategoryMapper();
    
    public static Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        
        Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setPrice(entity.getPrice());
        product.setStock(entity.getStock());
        product.setCategory(categoryMapper.toDomain(entity.getCategory()));
        
        return product;
    }
    
    public static ProductEntity toEntity(Product domain) {
        if (domain == null) return null;
        
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setStock(domain.getStock());
        entity.setCategory(categoryMapper.toEntity(domain.getCategory()));
        
        return entity;
    }
}
```

#### `infrastructure/src/main/java/com/empresa/infrastructure/adapter/out/persistence/ProductPersistenceAdapter.java`
```java
package com.empresa.infrastructure.adapter.out.persistence;

import com.empresa.domain.model.Product;
import com.empresa.domain.port.out.ProductRepositoryPort;
import com.empresa.infrastructure.adapter.out.persistence.mapper.ProductMapper;
import com.empresa.infrastructure.adapter.out.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia: Implementa ProductRepositoryPort
 */
@Component
public class ProductPersistenceAdapter implements ProductRepositoryPort {
    
    private final ProductJpaRepository repository;
    
    public ProductPersistenceAdapter(ProductJpaRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public List<Product> findAll() {
        return repository.findAll()
                .stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return repository.findById(id)
                .map(ProductMapper::toDomain);
    }
    
    @Override
    public List<Product> findByCategory(String categoryName) {
        return repository.findByCategoryName(categoryName)
                .stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Product save(Product product) {
        var entity = ProductMapper.toEntity(product);
        var savedEntity = repository.save(entity);
        return ProductMapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    public List<Product> findByNameContaining(String searchTerm) {
        return repository.findByNameContainingIgnoreCase(searchTerm)
                .stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return repository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findAllOrderByName() {
        return repository.findAllByOrderByNameAsc()
                .stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
    }
}
```

### 4.3 Crear Adaptadores Web

#### `infrastructure/src/main/java/com/empresa/infrastructure/adapter/in/web/dto/ProductDto.java`
```java
package com.empresa.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

/**
 * DTO: Producto para transferencia de datos REST
 */
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private CategoryDto category;
    
    // Constructores
    public ProductDto() {}
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public CategoryDto getCategory() { return category; }
    public void setCategory(CategoryDto category) { this.category = category; }
}
```

#### `infrastructure/src/main/java/com/empresa/infrastructure/adapter/in/web/ProductController.java`
```java
package com.empresa.infrastructure.adapter.in.web;

import com.empresa.domain.port.in.ProductUseCase;
import com.empresa.infrastructure.adapter.in.web.dto.ProductDto;
import com.empresa.infrastructure.adapter.in.web.mapper.ProductWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST: Productos
 */
@RestController
@RequestMapping("/api/productos")
public class ProductController {
    
    private final ProductUseCase productUseCase;
    
    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }
    
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        var products = productUseCase.getAllProducts()
                .stream()
                .map(ProductWebMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return productUseCase.getProductById(id)
                .map(ProductWebMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/categoria/{nombre}")
    public ResponseEntity<List<ProductDto>> getByCategory(@PathVariable String nombre) {
        var products = productUseCase.getProductsByCategory(nombre)
                .stream()
                .map(ProductWebMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
    
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto dto) {
        try {
            var product = ProductWebMapper.toDomain(dto);
            var created = productUseCase.createProduct(product);
            var responseDto = ProductWebMapper.toDto(created);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto dto) {
        try {
            var product = ProductWebMapper.toDomain(dto);
            var updated = productUseCase.updateProduct(id, product);
            var responseDto = ProductWebMapper.toDto(updated);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productUseCase.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String term) {
        var products = productUseCase.searchProducts(term)
                .stream()
                .map(ProductWebMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/precio-rango")
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        var products = productUseCase.getProductsByPriceRange(minPrice, maxPrice)
                .stream()
                .map(ProductWebMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
}
```

### 4.4 Configuración Spring

#### `infrastructure/src/main/java/com/empresa/infrastructure/config/BeanConfiguration.java`
```java
package com.empresa.infrastructure.config;

import com.empresa.domain.port.in.ProductUseCase;
import com.empresa.domain.port.out.ProductRepositoryPort;
import com.empresa.domain.port.out.CategoryRepositoryPort;
import com.empresa.application.usecase.ProductApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuración de Beans para Arquitectura Hexagonal
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.empresa.infrastructure.adapter.out.persistence.repository")
public class BeanConfiguration {

    @Bean
    public ProductUseCase productUseCase(ProductRepositoryPort productRepository, 
                                       CategoryRepositoryPort categoryRepository) {
        return new ProductApplicationService(productRepository, categoryRepository);
    }
}
```

---

## 🔴 Paso 5: Módulo Web (Punto de Entrada)

### 5.1 Crear `web/build.gradle`
```gradle
// Módulo Web - Punto de entrada de la aplicación
// Contiene la configuración principal y arranca Spring Boot

plugins {
    id 'org.springframework.boot'
    id 'war'
}

dependencies {
    // Todos los módulos del proyecto
    implementation project(':domain')
    implementation project(':application')
    implementation project(':infrastructure')
    
    // Spring Boot Web
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    
    // Base de datos
    runtimeOnly 'com.mysql:mysql-connector-j'
    
    // Para despliegue en servidor de aplicaciones
    providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'com.h2database:h2'
}
```

### 5.2 Crear Aplicación Principal

#### `web/src/main/java/com/empresa/web/MiProyectoApplication.java`
```java
package com.empresa.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Aplicación principal Spring Boot con arquitectura multimodular
 */
@SpringBootApplication(scanBasePackages = {"com.empresa"})
@EntityScan(basePackages = "com.empresa.infrastructure.adapter.out.persistence.entity")
public class MiProyectoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiProyectoApplication.class, args);
    }
}
```

### 5.3 Configuración de Propiedades

#### `web/src/main/resources/application.properties`
```properties
# Configuración del servidor
server.port=8080
server.servlet.context-path=/

# Configuración de base de datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/mi_proyecto_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=tu_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

# Configuración de logging
logging.level.com.empresa=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# Configuración de conexión pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

#### `web/src/main/resources/application-test.properties`
```properties
# Configuración para testing con H2
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.h2.console.enabled=true

logging.level.com.empresa=DEBUG
```

---

## ✅ Paso 6: Configuración y Validación

### 6.1 Verificar Estructura de Directorios
```bash
# Crear estructura de directorios si no existe
mkdir -p domain/src/main/java/com/empresa/domain/{model,port/{in,out}}
mkdir -p application/src/main/java/com/empresa/application/usecase
mkdir -p infrastructure/src/main/java/com/empresa/infrastructure/{adapter/{in/web,out/persistence},config}
mkdir -p web/src/main/{java/com/empresa/web,resources}
```

### 6.2 Compilar y Validar
```bash
# Compilar todos los módulos
./gradlew clean build

# Verificar dependencias
./gradlew dependencies

# Ejecutar tests
./gradlew test
```

### 6.3 Base de Datos
```sql
-- Crear base de datos MySQL
CREATE DATABASE mi_proyecto_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario (opcional)
CREATE USER 'mi_proyecto_user'@'localhost' IDENTIFIED BY 'tu_password';
GRANT ALL PRIVILEGES ON mi_proyecto_db.* TO 'mi_proyecto_user'@'localhost';
FLUSH PRIVILEGES;
```

---

## 🚀 Comandos de Construcción y Ejecución

### Comandos Gradle Básicos
```bash
# Limpiar y compilar todo el proyecto
./gradlew clean build

# Compilar módulos individuales
./gradlew :domain:build
./gradlew :application:build
./gradlew :infrastructure:build
./gradlew :web:build

# Ejecutar la aplicación
./gradlew :web:bootRun

# Ejecutar con perfil específico
./gradlew :web:bootRun --args='--spring.profiles.active=test'

# Generar WAR para deployment
./gradlew :web:bootWar

# Ejecutar tests
./gradlew test

# Verificar dependencias
./gradlew :web:dependencies --configuration compileClasspath
```

### Comandos de Desarrollo
```bash
# Compilación continua (watch mode)
./gradlew -t :web:classes

# Ejecutar con debug
./gradlew :web:bootRun --debug-jvm

# Generar reporte de dependencias
./gradlew :web:dependencyInsight --dependency spring-boot-starter-web
```

### Verificar APIs
```bash
# Una vez que la aplicación esté ejecutándose:

# Obtener todos los productos
curl -X GET http://localhost:8080/api/productos

# Obtener producto por ID
curl -X GET http://localhost:8080/api/productos/1

# Crear nuevo producto
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"name":"Nuevo Producto","description":"Descripción","price":99.99,"stock":10}'

# Buscar productos
curl -X GET "http://localhost:8080/api/productos/buscar?term=producto"
```

---

## 📋 Checklist de Validación

### ✅ Estructura del Proyecto
- [ ] Módulos creados (domain, application, infrastructure, web)
- [ ] settings.gradle configurado
- [ ] build.gradle de cada módulo configurado
- [ ] Estructura de paquetes correcta

### ✅ Módulo Domain
- [ ] Entidades de dominio creadas
- [ ] Puertos de entrada (UseCase interfaces) definidos
- [ ] Puertos de salida (Repository interfaces) definidos
- [ ] Sin dependencias externas

### ✅ Módulo Application
- [ ] Servicios de aplicación implementados
- [ ] Lógica de negocio implementada
- [ ] Solo depende de domain

### ✅ Módulo Infrastructure
- [ ] Entidades JPA creadas
- [ ] Repositorios JPA implementados
- [ ] Adaptadores de persistencia implementados
- [ ] Controladores REST implementados
- [ ] Mappers implementados
- [ ] Configuración Spring creada

### ✅ Módulo Web
- [ ] Aplicación principal creada
- [ ] Configuración de escaneo correcta
- [ ] application.properties configurado
- [ ] Dependencias de todos los módulos incluidas

### ✅ Funcionalidad
- [ ] Compilación exitosa
- [ ] Aplicación se inicia sin errores
- [ ] Base de datos conecta correctamente
- [ ] APIs REST responden correctamente
- [ ] Tests pasan exitosamente

---

## 🎯 Beneficios de esta Arquitectura

### 🔧 **Mantenibilidad**
- Código organizado por responsabilidades
- Fácil localización de funcionalidades
- Cambios aislados por módulo

### 🧪 **Testabilidad**
- Módulos independientes
- Mocking fácil de dependencias
- Tests unitarios y de integración separados

### 📈 **Escalabilidad**
- Adición de nuevos módulos sencilla
- Deployment independiente posible
- Equipos pueden trabajar en módulos separados

### 🔄 **Flexibilidad**
- Cambio de tecnologías sin afectar lógica de negocio
- Implementaciones alternativas fáciles de agregar
- Migración gradual posible

---

¡Con esta guía tienes todo lo necesario para construir un proyecto Spring Boot robusto con arquitectura multimodular y principios de Clean Architecture!
