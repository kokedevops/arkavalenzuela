# 🏗️ Análisis de Bounded Contexts en ARKAVALENZUELA

## 📋 Definición de Bounded Context

Un **Bounded Context** es un límite conceptual dentro del cual un modelo de dominio particular es válido y consistente. Define el contexto en el que los términos del dominio tienen un significado específico y único.

---

## 🔍 Identificación de Bounded Contexts Actuales

### 📊 **Análisis del Proyecto ARKAVALENZUELA**

Basado en el análisis del código actual, se identifican los siguientes **Bounded Contexts**:

### 🛍️ **1. PRODUCT CATALOG CONTEXT (Catálogo de Productos)**

**🎯 Responsabilidad**: Gestión de productos y categorías

**📦 Entidades Principales**:
- `Product` - Producto con información comercial
- `Category` - Categoría de clasificación de productos

**🔄 Casos de Uso**:
- Crear/actualizar/eliminar productos
- Gestionar categorías
- Buscar productos por categoría
- Consultar stock y precios

**📁 Ubicación Actual**:
```
domain/model/
├── Product.java        🛍️ Entidad principal
├── Category.java       📂 Clasificación de productos

application/usecase/
├── ProductApplicationService.java     🔄 Lógica de productos
├── CategoryApplicationService.java    🔄 Lógica de categorías

infrastructure/adapter/in/web/
├── ProductController.java             🌐 API productos
├── CategoryController.java            🌐 API categorías
```

**🔑 Conceptos Únicos**:
- **Product**: Artículo comercial con precio, stock, descripción
- **Category**: Agrupación lógica de productos
- **Stock**: Cantidad disponible para venta
- **Precio**: Valor comercial del producto

---

### 👤 **2. CUSTOMER MANAGEMENT CONTEXT (Gestión de Clientes)**

**🎯 Responsabilidad**: Gestión de información de clientes

**📦 Entidades Principales**:
- `Customer` - Cliente con datos personales y de contacto

**🔄 Casos de Uso**:
- Registrar clientes
- Actualizar información de contacto
- Validar emails y perfiles

**📁 Ubicación Actual**:
```
domain/model/
├── Customer.java       👤 Entidad principal

application/usecase/
├── CustomerApplicationService.java    🔄 Lógica de clientes

infrastructure/adapter/in/web/
├── CustomerController.java            🌐 API clientes
```

**🔑 Conceptos Únicos**:
- **Customer**: Persona con datos de contacto
- **Email**: Identificador único del cliente
- **Profile**: Conjunto de datos personales

---

### 🛒 **3. SHOPPING CONTEXT (Contexto de Compras)**

**🎯 Responsabilidad**: Gestión del proceso de compra

**📦 Entidades Principales**:
- `Cart` - Carrito de compras temporal
- `Order` - Pedido confirmado

**🔄 Casos de Uso**:
- Gestionar carritos de compra
- Crear pedidos
- Calcular totales
- Procesar órdenes

**📁 Ubicación Actual**:
```
domain/model/
├── Cart.java           🛒 Carrito temporal
├── Order.java          📋 Pedido confirmado

application/usecase/
├── CartApplicationService.java        🔄 Lógica de carritos
├── OrderApplicationService.java       🔄 Lógica de pedidos

infrastructure/adapter/in/web/
├── CartController.java                🌐 API carritos
├── OrderController.java               🌐 API pedidos
```

**🔑 Conceptos Únicos**:
- **Cart**: Contenedor temporal de productos seleccionados
- **Order**: Solicitud formal de compra
- **Total**: Suma del valor de productos en carrito/pedido
- **Estado**: Fase del proceso de compra (ACTIVE, ABANDONED, CONFIRMED)

---

## 🚨 Entidades con Nombres Duplicados en Diferentes Contextos

### ⚠️ **Análisis de Conflictos de Nombres**

#### 🔍 **1. Product (Producto)**

**📍 En Product Catalog Context**:
```java
// domain/model/Product.java
public class Product {
    private Long id;
    private String nombre;
    private Category categoria;       // 🔸 Relación con categoría
    private BigDecimal precioUnitario; // 🔸 Precio de venta
    private Integer stock;            // 🔸 Inventario disponible
    
    // 🎯 Lógica de negocio: catálogo
    public boolean isAvailable() { ... }
    public boolean hasValidPrice() { ... }
}
```

**📍 En Shopping Context** (Referenciado en Order/Cart):
```java
// Usado en Order.java
public class Order {
    private Set<Product> productos;   // 🔸 Productos en el pedido
    
    // 🎯 Lógica de negocio: compra
    public BigDecimal calculateTotal() {
        return productos.stream()
            .map(Product::getPrecioUnitario) // 🚨 Accede a precio
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

**⚠️ Conflicto Identificado**:
- **Misma entidad, diferentes perspectivas**
- En **Catalog**: Enfoque en gestión de inventario y categorización
- En **Shopping**: Enfoque en valor comercial y proceso de compra

#### 🔍 **2. Customer (Cliente)**

**📍 En Customer Management Context**:
```java
// domain/model/Customer.java
public class Customer {
    private String email;             // 🔸 Identificación
    private String telefono;          // 🔸 Contacto
    private String ciudad, pais;      // 🔸 Ubicación
    
    // 🎯 Lógica de negocio: perfil
    public boolean isValidEmail() { ... }
    public boolean hasCompleteProfile() { ... }
}
```

**📍 En Shopping Context** (Referenciado en Order/Cart):
```java
// Usado en Order.java y Cart.java
public class Order {
    private Customer cliente;         // 🔸 Comprador del pedido
}

public class Cart {
    private Customer cliente;         // 🔸 Propietario del carrito
}
```

**⚠️ Conflicto Identificado**:
- **Misma entidad, diferentes responsabilidades**
- En **Customer Management**: Enfoque en datos personales y contacto
- En **Shopping**: Enfoque en identificación del comprador

#### 🔍 **3. Category (Categoría)**

**📍 En Product Catalog Context**:
```java
// domain/model/Category.java
public class Category {
    private String nombre;
    
    // 🎯 Lógica de negocio: clasificación
    public boolean isValidName() { ... }
}

// Referenciada en Product.java
public class Product {
    private Category categoria;       // 🔸 Clasificación del producto
}
```

**⚠️ Conflicto Potencial**:
- **Actualmente NO hay conflicto directo**
- **Riesgo futuro**: Si se implementan categorías de clientes o categorías de pedidos

---

## 📊 Análisis del Enfoque Actual del Modelado

### 🎯 **Estado Actual: Modelo Monolítico**

#### ✅ **Aspectos Positivos**:
1. **Simplicidad inicial**: Fácil de entender y desarrollar
2. **Consistencia**: Un solo modelo para toda la aplicación
3. **Transacciones simples**: ACID en una sola base de datos
4. **Desarrollo rápido**: Menos complejidad arquitectónica

#### ⚠️ **Problemas Identificados**:

##### 🚨 **1. Acoplamiento Alto**
```java
// ProductApplicationService.java - Dependencia cruzada
public class ProductApplicationService {
    private final CategoryRepositoryPort categoryRepository; // 🔸 Depende de Category
    
    public Product createProduct(Product product) {
        validateCategoryExists(product.getCategoria().getId()); // 🚨 Validación cruzada
    }
}
```

##### 🚨 **2. Responsabilidades Mezcladas**
```java
// Order.java - Múltiples responsabilidades
public class Order {
    private Customer cliente;          // 🔸 Gestión de clientes
    private Set<Product> productos;    // 🔸 Catálogo de productos
    private BigDecimal total;          // 🔸 Cálculos financieros
    
    // 🚨 Lógica de múltiples dominios en una clase
    public BigDecimal calculateTotal() { ... }
    public void addProduct(Product product) { ... }
}
```

##### 🚨 **3. Entidades Compartidas**
```java
// Product es usado en múltiples contextos sin límites claros
// Catalog Context: gestión de inventario
// Shopping Context: proceso de compra
// ¿Qué pasa si necesitamos diferentes validaciones por contexto?
```

### 🎯 **Evaluación: ¿Un Modelo o Varios?**

#### 📋 **Veredicto: MODELO ÚNICO CON RIESGOS**

El proyecto **actualmente funciona con un modelo único**, pero presenta las siguientes características:

##### ✅ **Funcional para el Estado Actual**:
- Alcance limitado del dominio
- Equipo pequeño
- Lógica de negocio simple
- Pocos casos de uso

##### ⚠️ **Riesgos de Escalabilidad**:
- **Entidades sobrecargadas**: Product y Customer tienen múltiples responsabilidades
- **Acoplamiento creciente**: Cambios en un contexto afectan otros
- **Dificultad de evolución**: Nuevos requisitos pueden romper abstracciones existentes

---

## 🔄 Estrategia de División en Microservicios/Componentes

### 🎯 **Propuesta de Refactorización**

#### 🏗️ **FASE 1: Separación de Bounded Contexts (Modular Monolith)**

##### 📦 **1. Product Catalog Service**
```
📁 product-catalog/
├── domain/
│   ├── model/
│   │   ├── CatalogProduct.java     🛍️ Producto del catálogo
│   │   └── ProductCategory.java    📂 Categoría de productos
│   ├── port/in/
│   │   ├── CatalogUseCase.java
│   │   └── CategoryUseCase.java
│   └── port/out/
│       ├── CatalogRepositoryPort.java
│       └── CategoryRepositoryPort.java
├── application/
│   ├── CatalogApplicationService.java
│   └── CategoryApplicationService.java
└── infrastructure/
    ├── adapter/in/web/
    │   ├── CatalogController.java
    │   └── CategoryController.java
    └── adapter/out/persistence/
```

**🎯 Responsabilidades**:
- ✅ Gestión del catálogo de productos
- ✅ Administración de categorías
- ✅ Control de inventario y stock
- ✅ Búsquedas y filtros de productos

**📊 Datos Propios**:
```java
public class CatalogProduct {
    private ProductId id;               // 🔑 Identity
    private String name;
    private String description;
    private CategoryId categoryId;      // 🔗 Referencia débil
    private Money price;
    private Stock stock;
    private String brand;
}
```

##### 👤 **2. Customer Management Service**
```
📁 customer-management/
├── domain/
│   ├── model/
│   │   ├── Customer.java           👤 Cliente
│   │   └── ContactInfo.java        📞 Información de contacto
│   ├── port/in/
│   │   └── CustomerUseCase.java
│   └── port/out/
│       └── CustomerRepositoryPort.java
├── application/
│   └── CustomerApplicationService.java
└── infrastructure/
```

**🎯 Responsabilidades**:
- ✅ Registro y actualización de clientes
- ✅ Validación de datos personales
- ✅ Gestión de información de contacto
- ✅ Autenticación y autorización

**📊 Datos Propios**:
```java
public class Customer {
    private CustomerId id;             // 🔑 Identity
    private PersonalInfo personal;
    private ContactInfo contact;
    private Address address;
    private RegistrationDate registered;
}
```

##### 🛒 **3. Shopping Service**
```
📁 shopping/
├── domain/
│   ├── model/
│   │   ├── ShoppingCart.java       🛒 Carrito
│   │   ├── Order.java              📋 Pedido
│   │   ├── CartItem.java           📦 Artículo en carrito
│   │   └── OrderLine.java          📋 Línea de pedido
│   ├── port/in/
│   │   ├── ShoppingUseCase.java
│   │   └── OrderUseCase.java
│   └── port/out/
│       ├── CartRepositoryPort.java
│       ├── OrderRepositoryPort.java
│       ├── CatalogServicePort.java   // 🔌 Puerto externo
│       └── CustomerServicePort.java  // 🔌 Puerto externo
```

**🎯 Responsabilidades**:
- ✅ Gestión de carritos de compra
- ✅ Procesamiento de pedidos
- ✅ Cálculo de totales y descuentos
- ✅ Estados del proceso de compra

**📊 Datos Propios**:
```java
public class ShoppingCart {
    private CartId id;
    private CustomerId customerId;      // 🔗 Referencia externa
    private List<CartItem> items;
    private CartStatus status;
    private CreatedAt created;
}

public class CartItem {
    private ProductId productId;        // 🔗 Referencia externa
    private ProductName productName;    // 📋 Datos denormalizados
    private Money unitPrice;            // 📋 Datos denormalizados
    private Quantity quantity;
}
```

#### 🏗️ **FASE 2: Microservicios Independientes**

##### 🌐 **1. Comunicación entre Servicios**

###### **Event-Driven Communication**
```java
// Eventos de dominio
public class ProductPriceChanged {
    private ProductId productId;
    private Money oldPrice;
    private Money newPrice;
    private Timestamp changedAt;
}

public class CustomerRegistered {
    private CustomerId customerId;
    private CustomerName name;
    private Email email;
    private Timestamp registeredAt;
}

public class OrderPlaced {
    private OrderId orderId;
    private CustomerId customerId;
    private List<OrderLineItem> items;
    private Money total;
    private Timestamp placedAt;
}
```

###### **Anti-Corruption Layers (ACL)**
```java
// En Shopping Service
@Component
public class CatalogServiceAdapter implements CatalogServicePort {
    
    private final CatalogServiceClient catalogClient;
    
    @Override
    public Optional<ProductInfo> getProductInfo(ProductId productId) {
        try {
            CatalogProductDto dto = catalogClient.getProduct(productId.getValue());
            return Optional.of(mapToProductInfo(dto));
        } catch (ProductNotFoundException e) {
            return Optional.empty();
        }
    }
    
    private ProductInfo mapToProductInfo(CatalogProductDto dto) {
        return ProductInfo.builder()
                .productId(ProductId.of(dto.getId()))
                .name(ProductName.of(dto.getName()))
                .price(Money.of(dto.getPrice()))
                .availability(dto.getStock() > 0)
                .build();
    }
}
```

##### 📊 **2. Estrategia de Datos**

###### **Database per Service**
```
🗄️ catalog-db (PostgreSQL)
├── products
├── categories
└── inventory

🗄️ customer-db (PostgreSQL)
├── customers
├── contact_info
└── addresses

🗄️ shopping-db (PostgreSQL)
├── carts
├── cart_items
├── orders
└── order_lines

🗄️ events-db (MongoDB)
├── domain_events
└── event_projections
```

###### **Eventual Consistency**
```java
// En Shopping Service - Manejador de eventos
@EventHandler
public class ProductPriceChangedHandler {
    
    @Autowired
    private CartRepository cartRepository;
    
    public void handle(ProductPriceChanged event) {
        // Actualizar precio en carritos activos
        List<Cart> cartsWithProduct = cartRepository
                .findActiveCartsWithProduct(event.getProductId());
        
        cartsWithProduct.forEach(cart -> {
            cart.updateItemPrice(event.getProductId(), event.getNewPrice());
            cartRepository.save(cart);
        });
    }
}
```

### 🔧 **Pasos para la División**

#### 🎯 **PASO 1: Identificar Límites Claros**
```java
// Definir agregados por contexto
// Catalog Context
public class Product {         // Aggregate Root
    private List<ProductVariant> variants;
    private Inventory inventory;
}

// Shopping Context  
public class Order {           // Aggregate Root
    private List<OrderLine> lines;
    private OrderStatus status;
}

public class ShoppingCart {    // Aggregate Root
    private List<CartItem> items;
    private CartStatus status;
}
```

#### 🎯 **PASO 2: Crear Interfaces de Comunicación**
```java
// Definir contratos entre contextos
public interface CatalogServicePort {
    Optional<ProductInfo> getProductInfo(ProductId productId);
    boolean isProductAvailable(ProductId productId, Quantity quantity);
    void reserveProduct(ProductId productId, Quantity quantity);
}

public interface CustomerServicePort {
    Optional<CustomerInfo> getCustomerInfo(CustomerId customerId);
    boolean isCustomerActive(CustomerId customerId);
}
```

#### 🎯 **PASO 3: Implementar Event Sourcing (Opcional)**
```java
// Para casos complejos, considerar Event Sourcing
public class OrderAggregate {
    
    public void placeOrder(List<OrderLine> lines, CustomerId customerId) {
        // Validaciones
        validateCustomer(customerId);
        validateOrderLines(lines);
        
        // Aplicar evento
        OrderPlaced event = new OrderPlaced(orderId, customerId, lines, total);
        apply(event);
    }
    
    private void apply(OrderPlaced event) {
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.status = OrderStatus.PLACED;
        this.total = event.getTotal();
        
        // Registrar evento para procesamiento asíncrono
        registerEvent(event);
    }
}
```

#### 🎯 **PASO 4: Estrategia de Migración**

##### **Opción A: Strangler Fig Pattern**
```java
// Gradualmente reemplazar funcionalidad
@RestController
public class ProductController {
    
    @Autowired
    private LegacyProductService legacyService;     // 🔗 Sistema actual
    
    @Autowired
    private NewCatalogService newCatalogService;    // 🆕 Nuevo servicio
    
    @GetMapping("/products/{id}")
    public ProductDto getProduct(@PathVariable Long id) {
        if (featureToggle.isNewCatalogEnabled()) {
            return newCatalogService.getProduct(ProductId.of(id));
        } else {
            return legacyService.getProduct(id);
        }
    }
}
```

##### **Opción B: Event Interception Pattern**
```java
// Interceptar eventos del sistema actual
@EventListener
public class DomainEventPublisher {
    
    @Autowired
    private EventBus eventBus;
    
    @Async
    @TransactionalEventListener
    public void handleProductUpdated(ProductUpdatedEvent event) {
        // Transformar evento legacy a evento de dominio
        ProductChanged domainEvent = ProductChanged.builder()
                .productId(ProductId.of(event.getProductId()))
                .changes(mapChanges(event.getChanges()))
                .timestamp(event.getTimestamp())
                .build();
        
        eventBus.publish(domainEvent);
    }
}
```

---

## 🏆 Recomendaciones Finales

### 🎯 **Para el Estado Actual**

#### ✅ **Mantener Monolito Modular (Corto Plazo)**
- **Separar paquetes** por bounded context
- **Crear interfaces claras** entre contextos
- **Implementar ports y adapters** bien definidos
- **Evitar dependencias directas** entre contextos

#### 🔄 **Preparar para Microservicios (Mediano Plazo)**
- **Definir eventos de dominio** entre contextos
- **Implementar saga patterns** para transacciones distribuidas
- **Crear APIs internas** entre módulos
- **Establecer contratos** de comunicación

### 🚀 **Próximos Pasos Sugeridos**

1. **🔧 Refactorizar modelos**: Separar responsabilidades por contexto
2. **📨 Implementar eventos**: Comunicación asíncrona entre contextos
3. **🔌 Crear adapters**: Anti-corruption layers para cada contexto
4. **🗄️ Separar esquemas**: Base de datos por contexto
5. **📊 Métricas y monitoring**: Observabilidad por servicio

### 📋 **Criterios de Decisión para Microservicios**

**✅ Considera microservicios cuando**:
- Equipos crecen >8 personas
- Contextos evolucionan independientemente
- Diferentes tecnologías por dominio
- Escalabilidad diferenciada por contexto

**❌ Mantén monolito cuando**:
- Equipo <5 personas
- Dominio simple y estable
- Transacciones ACID críticas
- Overhead operacional alto

---

*Análisis creado el 15 de Julio de 2025*  
*Proyecto: Arka Valenzuela - Bounded Contexts y Estrategia de División*
