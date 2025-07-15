# Category - Modelo de Dominio

## Descripción General

La clase `Category` representa una categoría en el dominio de negocio del sistema. Es una entidad del dominio que encapsula la lógica de negocio relacionada con las categorías de productos.

## Ubicación

```
src/main/java/com/arka/arkavalenzuela/domain/model/Category.java
```

## Estructura de la Clase

### Atributos

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `id` | `Long` | Identificador único de la categoría |
| `nombre` | `String` | Nombre de la categoría |

### Constructores

#### Constructor por defecto
```java
public Category()
```
- Constructor sin parámetros requerido para frameworks como JPA

#### Constructor con parámetros
```java
public Category(Long id, String nombre)
```
- Constructor que inicializa todos los atributos de la categoría

### Métodos de Negocio

#### `isValidName()`
```java
public boolean isValidName()
```
- **Propósito**: Valida que el nombre de la categoría sea válido
- **Lógica**: Verifica que el nombre no sea nulo y no esté vacío (sin espacios)
- **Retorna**: `true` si el nombre es válido, `false` en caso contrario

### Métodos de Acceso (Getters y Setters)

#### Getters
- `getId()`: Retorna el identificador de la categoría
- `getNombre()`: Retorna el nombre de la categoría

#### Setters
- `setId(Long id)`: Establece el identificador de la categoría
- `setNombre(String nombre)`: Establece el nombre de la categoría

## Código Completo

```java
package com.arka.arkavalenzuela.domain.model;

import java.util.List;

public class Category {
    private Long id;
    private String nombre;

    public Category() {}

    public Category(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Métodos de negocio
    public boolean isValidName() {
        return nombre != null && !nombre.trim().isEmpty();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
```

## Características de Diseño

### Principios de Arquitectura Hexagonal

1. **Modelo de Dominio Puro**: La clase no tiene dependencias externas, representa únicamente la lógica de negocio
2. **Encapsulación**: Los atributos son privados con acceso controlado a través de métodos
3. **Validación de Negocio**: Incluye métodos para validar reglas de negocio (`isValidName()`)
4. **Inmutabilidad Controlada**: Permite modificación a través de setters, pero mantiene control sobre los datos

### Reglas de Negocio Implementadas

- **Validación de Nombre**: Una categoría debe tener un nombre válido (no nulo y no vacío)
- **Identificación Única**: Cada categoría tiene un identificador único

## Uso en la Arquitectura

### En el Dominio
- Representa la entidad central de categoría
- Define las reglas de negocio relacionadas con categorías

### En la Aplicación
- Utilizada por los casos de uso para operaciones con categorías
- Pasada entre las diferentes capas manteniendo la integridad del dominio

### En la Infraestructura
- Mapeada a entidades JPA para persistencia
- Convertida a DTOs para comunicación con el exterior

## Relaciones

- **Con Product**: Una categoría puede tener múltiples productos asociados
- **Con CategoryRepository**: Utilizada para operaciones de persistencia a través del puerto

## Notas de Implementación

1. **Framework Compatibility**: El constructor por defecto es necesario para JPA y otros frameworks
2. **Validation Logic**: La validación está centralizada en métodos del dominio
3. **Business Rules**: Las reglas de negocio están encapsuladas en la propia entidad
4. **Clean Architecture**: No tiene dependencias hacia capas externas, mantiene la pureza del dominio

## Ejemplo de Uso

```java
// Crear una nueva categoría
Category category = new Category(1L, "Electrónicos");

// Validar antes de procesar
if (category.isValidName()) {
    // Procesar la categoría válida
    System.out.println("Categoría válida: " + category.getNombre());
} else {
    // Manejar categoría inválida
    System.out.println("Nombre de categoría inválido");
}
```

## Consideraciones de Mantenimiento

- **Extensibilidad**: Se puede extender agregando nuevos métodos de validación
- **Evolución**: Puede evolucionar agregando nuevos atributos manteniendo compatibilidad
- **Testing**: Fácil de testear al ser una clase POJO con lógica encapsulada
