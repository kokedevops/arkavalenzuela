# 🚀 ARKA VALENZUELA - GUÍA RÁPIDA DE INICIO

## ⚡ **INICIO RÁPIDO (3 pasos)**

### 1️⃣ **Iniciar Infrastructure**
```bash
# Windows
scripts\start-ecommerce-complete.bat

# Linux/Mac
chmod +x scripts/start-ecommerce-complete.sh
./scripts/start-ecommerce-complete.sh
```

### 2️⃣ **Verificar Servicios**
```bash
# Health checks
curl http://localhost:8080/mobile/api/health    # 📱 Mobile BFF
curl http://localhost:8080/web/api/health       # 💻 Web BFF  
curl http://localhost:8080/analytics/health     # 📊 Analytics
```

### 3️⃣ **Probar E-commerce**
```bash
# Dashboard completo
curl http://localhost:8080/web/api/dashboard

# Estadísticas de negocio
curl http://localhost:8080/analytics/statistics

# Carritos abandonados
curl http://localhost:8080/carritos/abandonados
```

---

## 🌐 **URLS IMPORTANTES**

| Servicio | URL | Descripción |
|----------|-----|-------------|
| 🛒 **E-commerce API** | http://localhost:8080 | API principal |
| 🍃 **MongoDB Web** | http://localhost:8081 | Mongo Express UI |
| 📧 **Email Testing** | http://localhost:8025 | MailHog Web UI |
| 📊 **Monitoring** | http://localhost:3000 | Grafana Dashboard |
| 🏠 **Service Discovery** | http://localhost:8761 | Eureka Server |

---

## 🧪 **TESTS E-COMMERCE**

### 🛒 **Crear y Abandonar Carrito**
```bash
# 1. Crear carrito
curl -X POST http://localhost:8080/carritos \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "clienteNombre": "Juan Pérez",
    "estado": "ACTIVE"
  }'

# 2. Abandonar carrito
curl -X PUT http://localhost:8080/carritos/1/abandonar

# 3. Ver carritos abandonados
curl http://localhost:8080/carritos/abandonados
```

### 📱 **Mobile BFF**
```bash
# Productos destacados para móvil
curl http://localhost:8080/mobile/api/productos/destacados

# Búsqueda optimizada móvil
curl "http://localhost:8080/mobile/api/productos/buscar?query=laptop"
```

### 💻 **Web BFF**
```bash
# Dashboard con métricas completas
curl http://localhost:8080/web/api/dashboard

# Detalle producto para web
curl http://localhost:8080/web/api/productos/1/detalle
```

### 📊 **Analytics**
```bash
# Estadísticas de e-commerce
curl http://localhost:8080/analytics/statistics

# Insights de carritos abandonados
curl http://localhost:8080/analytics/abandoned-carts
```

---

## 🔔 **NOTIFICACIONES AUTOMÁTICAS**

### 📧 **Email Testing con MailHog**
1. Abrir http://localhost:8025
2. Crear carrito y abandonarlo
3. Esperar 5 minutos (demo) o 1 hora (producción)
4. Ver email recibido en MailHog

### 🍃 **MongoDB Logs**
1. Abrir http://localhost:8081
2. Usuario: `arka_admin` / Password: `Arca2025*`
3. Ver colección `notification_history`

---

## ⚙️ **CONFIGURACIÓN RÁPIDA**

### 📧 **Activar Emails Reales**
```properties
# En application-dev.properties
arka.mail.enabled=true
spring.mail.host=smtp.gmail.com
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
```

### 🍃 **Cambiar MongoDB**
```properties
# Para MongoDB remoto
spring.data.mongodb.uri=mongodb://usuario:password@host:27017/base_datos
```

---

## 🎯 **ENDPOINTS COMPLETOS**

### 🛒 **Carritos**
- `GET /carritos` - Listar carritos
- `POST /carritos` - Crear carrito
- `GET /carritos/abandonados` - ⭐ Carritos abandonados
- `PUT /carritos/{id}/abandonar` - Abandonar carrito

### 📋 **Pedidos**
- `GET /pedidos` - Listar pedidos
- `POST /pedidos` - Crear pedido
- `GET /pedidos/cliente/{id}` - Pedidos por cliente

### 📱 **Mobile BFF**
- `GET /mobile/api/productos/destacados` - Destacados móvil
- `GET /mobile/api/productos/buscar` - Búsqueda móvil
- `GET /mobile/api/carrito/{id}/resumen` - Resumen carrito

### 💻 **Web BFF**
- `GET /web/api/dashboard` - ⭐ Dashboard completo
- `GET /web/api/productos/{id}/detalle` - Detalle producto
- `GET /web/api/productos/completo` - Productos web

### 📊 **Analytics**
- `GET /analytics/statistics` - ⭐ Estadísticas completas
- `GET /analytics/abandoned-carts` - ⭐ Insights carritos

---

## 🎉 **¡LISTO PARA PRODUCCIÓN!**

### ✅ **FEATURES COMPLETADAS (100%)**
- 🛒 **E-commerce completo** con carritos y pedidos
- 📱💻 **BFF Architecture** para móvil y web
- 🔔 **Notificaciones automáticas** con scheduler
- 📧 **Sistema de emails** con SMTP real
- 🍃 **MongoDB** para logs y analytics
- 📊 **Business Intelligence** con métricas
- 🐳 **Docker** con toda la infraestructura
- 📈 **Monitoring** con Prometheus y Grafana
