# 📱 POSTMAN - ARKA VALENZUELA E-COMMERCE

## 🚀 **CONFIGURACIÓN INICIAL DE POSTMAN**

### **1️⃣ Crear Nueva Colección**
1. Abrir Postman
2. Click "New" → "Collection"
3. Nombre: `ARKA Valenzuela E-commerce`
4. Descripción: `API completa con JWT y API de Terceros`

### **2️⃣ Variables de Colección**
```json
{
  "baseUrl": "http://3.134.244.104:8888",
  "accessToken": "",
  "refreshToken": ""
}
```

**Cómo agregar variables:**
1. Click en tu colección
2. Tab "Variables"
3. Agregar:
   - `baseUrl`: `http://3.134.244.104:8888`
   - `accessToken`: (dejar vacío)
   - `refreshToken`: (dejar vacío)

---

## 🔑 **PARTE 1: AUTENTICACIÓN JWT**

### **📝 Request 1: Login JWT**

**Método:** `POST`
**URL:** `{{baseUrl}}/api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "identifier": "admin",
  "password": "admin123"
}
```

**Tests (Script para guardar token automáticamente):**
```javascript
// Verificar respuesta exitosa
pm.test("Login exitoso", function () {
    pm.response.to.have.status(200);
});

// Extraer y guardar token
if (pm.response.code === 200) {
    const responseJson = pm.response.json();
    
    // Guardar tokens en variables de colección
    pm.collectionVariables.set("accessToken", responseJson.accessToken);
    pm.collectionVariables.set("refreshToken", responseJson.refreshToken);
    
    console.log("✅ Access Token guardado:", responseJson.accessToken.substring(0, 20) + "...");
    console.log("✅ Refresh Token guardado:", responseJson.refreshToken.substring(0, 20) + "...");
}
```

### **📝 Request 2: Validar Token**

**Método:** `POST`
**URL:** `{{baseUrl}}/api/auth/validate`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "token": "{{accessToken}}"
}
```

### **📝 Request 3: Información de Usuario Demo**

**Método:** `GET`
**URL:** `{{baseUrl}}/api/auth/demo-users`

**Headers:** (ninguno necesario)

---

## 🌍 **PARTE 2: API DE TERCEROS (SIN AUTENTICACIÓN)**

### **📁 Folder: "API Terceros - Especificaciones"**

#### **📝 GET - Obtener Todas las Categorías**
**Método:** `GET`
**URL:** `{{baseUrl}}/api/terceros/ObtenerDatos/categorias`
**Headers:** (ninguno)

#### **📝 GET - Obtener Categoría por ID**
**Método:** `GET`
**URL:** `{{baseUrl}}/api/terceros/ObtenerDatos/categorias/1`
**Headers:** (ninguno)

#### **📝 POST - Guardar Nueva Categoría**
**Método:** `POST`
**URL:** `{{baseUrl}}/api/terceros/GuardarDatos/categorias`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nombre": "Electrónicos Postman",
  "descripcion": "Categoría creada desde Postman"
}
```

#### **📝 DELETE - Borrar Categoría**
**Método:** `DELETE`
**URL:** `{{baseUrl}}/api/terceros/BorrarDatos/categorias/1`
**Headers:** (ninguno)

#### **📝 GET - Obtener Todos los Productos**
**Método:** `GET`
**URL:** `{{baseUrl}}/api/terceros/ObtenerDatos/productos`
**Headers:** (ninguno)

#### **📝 POST - Guardar Nuevo Producto**
**Método:** `POST`
**URL:** `{{baseUrl}}/api/terceros/GuardarDatos/productos`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nombre": "iPhone 15 Pro",
  "descripcion": "Smartphone Apple última generación",
  "precio": 1199.99,
  "stock": 25
}
```

#### **📝 GET - Obtener Todos los Usuarios**
**Método:** `GET`
**URL:** `{{baseUrl}}/api/terceros/ObtenerDatos/usuarios`
**Headers:** (ninguno)

#### **📝 POST - Guardar Nuevo Usuario**
**Método:** `POST`
**URL:** `{{baseUrl}}/api/terceros/GuardarDatos/usuarios`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nombre": "Carlos Mendoza",
  "email": "carlos@postman.com",
  "telefono": "+56912345678"
}
```

---

## 🛒 **PARTE 3: API E-COMMERCE PRINCIPAL (CON JWT)**

### **⚙️ Configuración de Autorización**

**Para TODOS los requests de la API principal:**

1. Tab "Authorization"
2. Type: `Bearer Token`
3. Token: `{{accessToken}}`

### **📁 Folder: "Categorías"**

#### **📝 GET - Listar Categorías**
**Método:** `GET`
**URL:** `{{baseUrl}}/categorias`
**Authorization:** `Bearer {{accessToken}}`

#### **📝 GET - Categoría por ID**
**Método:** `GET`
**URL:** `{{baseUrl}}/categorias/1`
**Authorization:** `Bearer {{accessToken}}`

#### **📝 POST - Crear Categoría**
**Método:** `POST`
**URL:** `{{baseUrl}}/categorias`
**Authorization:** `Bearer {{accessToken}}`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nombre": "Gaming",
  "descripcion": "Productos para gaming"
}
```

#### **📝 PUT - Actualizar Categoría**
**Método:** `PUT`
**URL:** `{{baseUrl}}/categorias/1`
**Authorization:** `Bearer {{accessToken}}`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nombre": "Gaming Pro",
  "descripcion": "Productos profesionales para gaming"
}
```

#### **📝 DELETE - Eliminar Categoría**
**Método:** `DELETE`
**URL:** `{{baseUrl}}/categorias/1`
**Authorization:** `Bearer {{accessToken}}`

### **📁 Folder: "Productos"**

#### **📝 GET - Listar Productos (Público)**
**Método:** `GET`
**URL:** `{{baseUrl}}/productos`
**Authorization:** (ninguna - es público)

#### **📝 GET - Producto por ID (Público)**
**Método:** `GET`
**URL:** `{{baseUrl}}/productos/1`
**Authorization:** (ninguna - es público)

#### **📝 POST - Crear Producto**
**Método:** `POST`
**URL:** `{{baseUrl}}/productos`
**Authorization:** `Bearer {{accessToken}}`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nombre": "RTX 4090",
  "descripcion": "Tarjeta gráfica NVIDIA GeForce RTX 4090",
  "precio": 1599.99,
  "stock": 10,
  "categoria": {
    "id": 1
  }
}
```

#### **📝 PUT - Actualizar Producto**
**Método:** `PUT`
**URL:** `{{baseUrl}}/productos/1`
**Authorization:** `Bearer {{accessToken}}`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nombre": "RTX 4090 Ti",
  "descripcion": "Tarjeta gráfica NVIDIA GeForce RTX 4090 Ti",
  "precio": 1799.99,
  "stock": 15
}
```

#### **📝 DELETE - Eliminar Producto**
**Método:** `DELETE`
**URL:** `{{baseUrl}}/productos/1`
**Authorization:** `Bearer {{accessToken}}`

#### **📝 GET - Buscar Productos**
**Método:** `GET`
**URL:** `{{baseUrl}}/productos/buscar?nombre=rtx`
**Authorization:** (ninguna)

### **📁 Folder: "Usuarios"**

#### **📝 GET - Listar Usuarios**
**Método:** `GET`
**URL:** `{{baseUrl}}/usuarios`
**Authorization:** `Bearer {{accessToken}}`

#### **📝 POST - Crear Usuario**
**Método:** `POST`
**URL:** `{{baseUrl}}/usuarios`
**Authorization:** `Bearer {{accessToken}}`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "nombre": "Ana García",
  "email": "ana@postman.com",
  "telefono": "+56987654321"
}
```

### **📁 Folder: "Carritos"**

#### **📝 GET - Listar Carritos**
**Método:** `GET`
**URL:** `{{baseUrl}}/carritos`
**Authorization:** `Bearer {{accessToken}}`

#### **📝 POST - Crear Carrito**
**Método:** `POST`
**URL:** `{{baseUrl}}/carritos`
**Authorization:** `Bearer {{accessToken}}`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "cliente": {
    "id": 1
  },
  "estado": "ACTIVE"
}
```

#### **📝 GET - Carritos Abandonados**
**Método:** `GET`
**URL:** `{{baseUrl}}/carritos/abandonados`
**Authorization:** `Bearer {{accessToken}}`

### **📁 Folder: "Pedidos"**

#### **📝 GET - Listar Pedidos**
**Método:** `GET`
**URL:** `{{baseUrl}}/pedidos`
**Authorization:** `Bearer {{accessToken}}`

#### **📝 POST - Crear Pedido**
**Método:** `POST`
**URL:** `{{baseUrl}}/pedidos`
**Authorization:** `Bearer {{accessToken}}`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "cliente": {
    "id": 1
  },
  "productos": ["RTX 4090", "Gaming Mouse"],
  "total": 1699.99,
  "estado": "PENDING"
}
```

---

## 🔧 **PARTE 4: MICROSERVICIOS**

### **📁 Folder: "Microservicios"**

#### **📝 API Gateway**
**Método:** `GET`
**URL:** `http://3.134.244.104:8080/`

#### **📝 Eureka Server**
**Método:** `GET`
**URL:** `http://3.134.244.104:8761/`

#### **📝 Cotizador - Health**
**Método:** `GET`
**URL:** `http://3.134.244.104:8082/health`

#### **📝 Cotizador - Crear Cotización**
**Método:** `POST`
**URL:** `http://3.134.244.104:8082/api/cotizacion`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "producto": "Laptop Gaming",
  "cantidad": 1,
  "precioUnitario": 2500.00
}
```

#### **📝 Gestor Solicitudes - Health**
**Método:** `GET`
**URL:** `http://3.134.244.104:8083/api/solicitudes/health`

#### **📝 Hello World Service**
**Método:** `GET`
**URL:** `http://3.134.244.104:8084/hello`

---

## 📱 **PARTE 5: BFF (Backend for Frontend)**

### **📁 Folder: "BFF"**

#### **📝 Web BFF - Health**
**Método:** `GET`
**URL:** `{{baseUrl}}/web/api/health`

#### **📝 Web BFF - Productos Completos**
**Método:** `GET`
**URL:** `{{baseUrl}}/web/api/productos/completo`

#### **📝 Mobile BFF - Health**
**Método:** `GET`
**URL:** `{{baseUrl}}/mobile/api/health`

#### **📝 Mobile BFF - Producto Quick**
**Método:** `GET`
**URL:** `{{baseUrl}}/mobile/api/productos/1/quick`

---

## 🎯 **SOLUCIÓN A TU ERROR 401**

**El error que recibiste:**
```json
{
    "error": true,
    "message": "Authentication required",
    "status": 401
}
```

**Significa que estás usando un endpoint que requiere autenticación.**

### **✅ SOLUCIONES:**

#### **Opción 1: Usar API de Terceros (SIN autenticación)**
```
GET http://3.134.244.104:8888/api/terceros/ObtenerDatos/categorias/1
```

#### **Opción 2: Autenticarte primero**
1. **Ejecutar Login:** `POST /api/auth/login`
2. **Copiar accessToken** de la respuesta
3. **Usar en Authorization:** `Bearer {token}`

### **🔧 Configuración Rápida en Postman:**

1. **Crear request de Login**
2. **Agregar script de Tests:**
```javascript
pm.collectionVariables.set("accessToken", pm.response.json().accessToken);
```
3. **En otros requests usar:** `{{accessToken}}`

---

## 📋 **EXPORT DE COLECCIÓN POSTMAN**

```json
{
  "info": {
    "name": "ARKA Valenzuela E-commerce",
    "description": "API completa con JWT y especificaciones de terceros",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://3.134.244.104:8888"
    },
    {
      "key": "accessToken",
      "value": ""
    },
    {
      "key": "refreshToken",
      "value": ""
    }
  ],
  "auth": {
    "type": "bearer",
    "bearer": [
      {
        "key": "token",
        "value": "{{accessToken}}"
      }
    ]
  }
}
```

**🎉 Con esta configuración tendrás TODO funcionando perfectamente en Postman!**
