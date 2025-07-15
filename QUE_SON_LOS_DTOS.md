# 📋 DTOs - Data Transfer Objects

## 📖 ¿Qué significa DTO?

**DTO** significa **Data Transfer Object** (Objeto de Transferencia de Datos). Es un patrón de diseño que define objetos simples cuyo único propósito es transportar datos entre diferentes capas o sistemas, sin contener lógica de negocio.

---

## 🎯 Definición y Propósito

### 🔍 **Concepto**
Un DTO es una estructura de datos plana que:
- **Transporta datos** entre capas de la aplicación
- **No contiene lógica de negocio**
- **Agrupa datos relacionados** en una sola estructura
- **Optimiza la transferencia** de información

### 🎯 **Propósito Principal**
- ✅ **Desacoplar** las capas de la aplicación
- ✅ **Controlar** qué datos se exponen al exterior
- ✅ **Optimizar** la transferencia de datos
- ✅ **Estabilizar** las interfaces entre capas

---

## 🏗️ DTO en Arquitectura Hexagonal

### 📊 **Ubicación en el Proyecto**
```
🔵 INFRASTRUCTURE
├── adapter/
│   └── in/
│       └── web/
│           ├── dto/              📋 DTOs AQUÍ
│           │   ├── CategoryDTO.java
│           │   ├── ProductDTO.java
│           │   └── CustomerDTO.java
│           ├── mapper/           🔄 Mappers para convertir
│           └── controller/       🎮 Controladores que usan DTOs
```

### 🔄 **Flujo de Datos con DTOs**
```
🌐 Cliente HTTP
    ↓
📋 DTO (JSON) ← Entrada del sistema
    ↓
🔄 WebMapper: DTO → Domain
    ↓
🟡 Domain Model ← Lógica de negocio
    ↓
🟢 Application Service
    ↓
🟡 Domain Model (resultado)
    ↓
🔄 WebMapper: Domain → DTO
    ↓
📋 DTO (JSON) ← Salida del sistema
    ↓
🌐 Cliente HTTP
```

---

## 📝 Ejemplos Prácticos en el Proyecto

### 🔵 **CategoryDTO.java**
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

    // Solo getters y setters - SIN LÓGICA DE NEGOCIO
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
```

**🎯 Características:**
- ✅ **Solo datos**: id, nombre
- ✅ **Sin lógica**: No hay métodos como `isValidName()`
- ✅ **Serializable**: Para JSON automático
- ✅ **Simple**: Fácil de entender y usar

### 🛍️ **ProductDTO.java**
```java
package com.arka.arkavalenzuela.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public class ProductDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long categoriaId;        // 📌 Solo ID, no objeto completo
    private String marca;
    private BigDecimal precioUnitario;
    private Integer stock;

    // Solo constructores y getters/setters
    public ProductDTO() {}

    // Getters y setters...
}
```

**🎯 Diferencias con Domain Model:**
```java
// 🟡 DOMAIN MODEL (Product.java)
public class Product {
    private Category categoria;      // 📌 Objeto completo
    
    // ✅ CON lógica de negocio
    public boolean hasValidPrice() {
        return precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) > 0;
    }
}

// 📋 DTO (ProductDTO.java)  
public class ProductDTO {
    private Long categoriaId;        // 📌 Solo ID para transferencia
    
    // ❌ SIN lógica de negocio
    // Solo getters y setters
}
```

---

## 🔄 Mappers: Convertidores DTO ↔ Domain

### 🎯 **Propósito de los Mappers**
Los mappers son responsables de convertir entre DTOs y Domain Models:

```java
DTO ←→ Domain Model
```

### 📝 **CategoryWebMapper.java**
```java
@Component
public class CategoryWebMapper {
    
    // DTO → Domain
    public Category toDomain(CategoryDTO dto) {
        if (dto == null) return null;
        
        Category category = new Category();
        category.setId(dto.getId());
        category.setNombre(dto.getNombre());
        return category;
    }
    
    // Domain → DTO
    public CategoryDTO toDTO(Category category) {
        if (category == null) return null;
        
        return new CategoryDTO(
            category.getId(), 
            category.getNombre()
        );
    }
}
```

### 🛍️ **ProductWebMapper.java** (Más Complejo)
```java
@Component
public class ProductWebMapper {
    
    private final CategoryRepositoryPort categoryRepository;

    public ProductWebMapper(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    // DTO → Domain (Resuelve relaciones)
    public Product toDomain(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setNombre(dto.getNombre());
        product.setPrecioUnitario(dto.getPrecioUnitario());
        
        // 🔍 Cargar objeto Category completo desde ID
        if (dto.getCategoriaId() != null) {
            Category categoria = categoryRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategoria(categoria);
        }
        
        return product;
    }
    
    // Domain → DTO (Aplana relaciones)
    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setNombre(product.getNombre());
        dto.setPrecioUnitario(product.getPrecioUnitario());
        
        // 🔍 Solo el ID de la categoría, no el objeto completo
        if (product.getCategoria() != null) {
            dto.setCategoriaId(product.getCategoria().getId());
        }
        
        return dto;
    }
}
```

---

## 🌐 DTOs en APIs REST

### 📥 **Request (Cliente → Servidor)**
```json
POST /productos
Content-Type: application/json

{
    "nombre": "iPhone 15",
    "descripcion": "Smartphone Apple",
    "categoriaId": 1,                    // 📌 Solo ID
    "marca": "Apple",
    "precioUnitario": 999.99,
    "stock": 50
}
```

### 📤 **Response (Servidor → Cliente)**
```json
HTTP 201 Created
Content-Type: application/json

{
    "id": 15,
    "nombre": "iPhone 15",
    "descripcion": "Smartphone Apple",
    "categoriaId": 1,                    // 📌 Solo ID
    "marca": "Apple", 
    "precioUnitario": 999.99,
    "stock": 50
}
```

### 🎮 **En el Controlador**
```java
@RestController
@RequestMapping("/productos")
public class ProductController {
    
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        // 1. DTO viene del JSON automáticamente
        
        // 2. Convertir DTO → Domain
        Product product = webMapper.toDomain(productDTO);
        
        // 3. Lógica de negocio en Domain
        Product savedProduct = productUseCase.createProduct(product);
        
        // 4. Convertir Domain → DTO
        ProductDTO response = webMapper.toDTO(savedProduct);
        
        // 5. DTO se convierte a JSON automáticamente
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

---

## 🎯 Ventajas de Usar DTOs

### ✅ **1. Desacoplamiento**
```java
// ❌ SIN DTOs (Malo)
@PostMapping
public Product create(@RequestBody Product product) {
    return productService.create(product);
}
// 🚨 Problema: Domain Model expuesto directamente
```

```java
// ✅ CON DTOs (Bueno)
@PostMapping
public ProductDTO create(@RequestBody ProductDTO dto) {
    Product product = mapper.toDomain(dto);
    Product saved = productService.create(product);
    return mapper.toDTO(saved);
}
// ✅ Beneficio: Domain protegido, control total
```

### ✅ **2. Control de Datos**
```java
// 🟡 Domain Model
public class Product {
    private Long id;
    private String nombre;
    private BigDecimal costoInterno;      // 🔒 PRIVADO - no exponer
    private String codigoSecreto;         // 🔒 PRIVADO - no exponer
    private Category categoria;           // 🔄 COMPLEJO - simplificar
}

// 📋 DTO 
public class ProductDTO {
    private Long id;
    private String nombre;
    // ❌ costoInterno NO incluido
    // ❌ codigoSecreto NO incluido  
    private Long categoriaId;             // ✅ Simplificado
}
```

### ✅ **3. Optimización de Red**
```json
// ❌ Sin DTO (Ineficiente)
{
    "id": 1,
    "nombre": "iPhone",
    "categoria": {
        "id": 1,
        "nombre": "Electrónicos",
        "productos": [...]                // 🚨 Referencia circular
    }
}

// ✅ Con DTO (Eficiente)
{
    "id": 1,
    "nombre": "iPhone", 
    "categoriaId": 1                     // ✅ Solo lo necesario
}
```

### ✅ **4. Versionado de API**
```java
// ✅ DTOv1
public class ProductDTOv1 {
    private String nombre;
    private BigDecimal precio;
}

// ✅ DTOv2 (Nueva versión sin romper clientes)
public class ProductDTOv2 {
    private String nombre;
    private BigDecimal precio;
    private String categoria;            // ✅ Campo nuevo
}

// 🟡 Domain Model (Sin cambios)
public class Product {
    // El modelo de dominio evoluciona independientemente
}
```

---

## 🔄 Diferentes Tipos de DTOs

### 🎯 **1. Web DTOs (API REST)**
```java
// Para transferencia HTTP
public class ProductDTO {
    private Long id;
    private String nombre;
    private Long categoriaId;           // Simplificado para JSON
}
```

### 🎯 **2. Persistence DTOs (Base de Datos)**
```java
// Para queries específicas
public class ProductSummaryDTO {
    private String nombre;
    private BigDecimal precio;
    private String categoriaNombre;
    
    // Constructor para consultas JPQL
    public ProductSummaryDTO(String nombre, BigDecimal precio, String categoriaNombre) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoriaNombre = categoriaNombre;
    }
}
```

### 🎯 **3. Integration DTOs (Microservicios)**
```java
// Para comunicación entre servicios
public class ProductEventDTO {
    private Long productId;
    private String eventType;
    private LocalDateTime timestamp;
    private Map<String, Object> payload;
}
```

---

## 🚫 Errores Comunes con DTOs

### ❌ **1. Lógica de Negocio en DTOs**
```java
// ❌ MALO - DTO con lógica de negocio
public class ProductDTO {
    private BigDecimal precio;
    
    // 🚨 ERROR: Lógica en DTO
    public boolean isExpensive() {
        return precio.compareTo(new BigDecimal("1000")) > 0;
    }
}

// ✅ BUENO - Lógica en Domain
public class Product {
    private BigDecimal precio;
    
    // ✅ CORRECTO: Lógica en Domain
    public boolean isExpensive() {
        return precio.compareTo(new BigDecimal("1000")) > 0;
    }
}
```

### ❌ **2. DTOs Anémicos (Sin Propósito)**
```java
// ❌ MALO - DTO idéntico al Domain
public class CategoryDTO {
    private Long id;
    private String nombre;
    // Exactamente igual al Domain... ¿para qué?
}
```

### ❌ **3. DTOs Demasiado Complejos**
```java
// ❌ MALO - DTO muy complejo
public class OrderDTO {
    private CustomerDTO customer;
    private List<ProductDTO> products;
    private PaymentDTO payment;
    private ShippingDTO shipping;
    // Demasiada información anidada
}
```

---

## 💡 Mejores Prácticas

### ✅ **1. DTOs Planos y Simples**
```java
// ✅ BUENO
public class CreateProductRequest {
    private String nombre;
    private Long categoriaId;           // Solo ID, no objeto
    private BigDecimal precio;
}
```

### ✅ **2. DTOs Específicos por Uso**
```java
// ✅ Para crear
public class CreateProductRequest {
    private String nombre;
    private Long categoriaId;
}

// ✅ Para respuesta
public class ProductResponse {
    private Long id;
    private String nombre;
    private String categoriaNombre;     // Información adicional
}

// ✅ Para listado
public class ProductSummary {
    private Long id;
    private String nombre;
    private BigDecimal precio;          // Solo lo esencial
}
```

### ✅ **3. Mappers Dedicados**
```java
// ✅ Un mapper por cada conversión
@Component
public class ProductWebMapper {
    // Web DTO ↔ Domain
}

@Component  
public class ProductPersistenceMapper {
    // JPA Entity ↔ Domain
}
```

### ✅ **4. Validaciones en DTOs**
```java
public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    private String nombre;
    
    @NotNull(message = "Category is required")
    private Long categoriaId;
    
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal precio;
}
```

---

## 🔍 DTOs vs Otros Patrones

### 📋 **DTO vs Domain Model**
| Aspecto | DTO | Domain Model |
|---------|-----|--------------|
| **Propósito** | Transferir datos | Lógica de negocio |
| **Lógica** | ❌ Sin lógica | ✅ Con lógica |
| **Ubicación** | Infrastructure | Domain |
| **Dependencias** | Mínimas | Reglas de negocio |
| **Estabilidad** | Cambia por API | Cambia por negocio |

### 📋 **DTO vs Value Object**
| Aspecto | DTO | Value Object |
|---------|-----|--------------|
| **Mutabilidad** | Mutable | Inmutable |
| **Identidad** | Sin identidad | Sin identidad |
| **Propósito** | Transferencia | Conceptos de dominio |
| **Validaciones** | Básicas | Complejas |

### 📋 **DTO vs Entity**
| Aspecto | DTO | Entity |
|---------|-----|---------|
| **Persistencia** | ❌ No persiste | ✅ Persiste |
| **Identidad** | Sin identidad | Con ID único |
| **Ciclo de vida** | Temporal | Persistente |
| **Relaciones** | Referencias simples | Relaciones complejas |

---

## 📊 Ejemplo Completo en el Proyecto

### 🔄 **Flujo Completo: Crear Producto**

#### **1. Request JSON → DTO**
```json
POST /productos
{
    "nombre": "MacBook Pro",
    "categoriaId": 1,
    "precio": 2500.00
}
```

#### **2. Controller recibe DTO**
```java
@PostMapping
public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
```

#### **3. Mapper: DTO → Domain**
```java
Product product = webMapper.toDomain(dto);
// ProductDTO → Product (carga Category completa)
```

#### **4. Use Case con Domain**
```java
Product savedProduct = productUseCase.createProduct(product);
// Validaciones de negocio, persistencia, etc.
```

#### **5. Mapper: Domain → DTO**
```java
ProductDTO response = webMapper.toDTO(savedProduct);
// Product → ProductDTO (aplana Category a ID)
```

#### **6. Response DTO → JSON**
```json
HTTP 201 Created
{
    "id": 25,
    "nombre": "MacBook Pro",
    "categoriaId": 1,
    "precio": 2500.00
}
```

---

## 🎯 Conclusión

### 🏆 **DTOs son Esenciales para:**
1. ✅ **Desacoplar** capas de la aplicación
2. ✅ **Controlar** qué datos se exponen
3. ✅ **Optimizar** transferencia de datos
4. ✅ **Estabilizar** interfaces de API
5. ✅ **Facilitar** versionado y evolución

### 📋 **Reglas de Oro:**
- 🔸 **DTOs = Solo Datos** (sin lógica de negocio)
- 🔸 **Domain = Solo Lógica** (sin preocupaciones de transferencia)
- 🔸 **Mappers = Traductores** (convierten entre mundos)
- 🔸 **Simplicidad** sobre complejidad
- 🔸 **Específico** sobre genérico

### 🚀 **Resultado:**
Una arquitectura limpia, mantenible y flexible donde cada componente tiene una responsabilidad clara y bien definida.

---

*Documentación creada el 15 de Julio de 2025*  
*Proyecto: Arka Valenzuela - Entendiendo DTOs*
