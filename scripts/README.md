# ⚙️ SCRIPTS DE AUTOMATIZACIÓN - ARKA PLATFORM

Este directorio contiene todos los scripts necesarios para la gestión, despliegue y operación de la plataforma ARKA.

---

## 📋 **ÍNDICE DE SCRIPTS**

### 🚀 **Scripts de Inicio**
| Script | Plataforma | Descripción |
|--------|------------|-------------|
| `start-all-services.sh` | Linux/macOS | Inicia todos los microservicios |
| `start-all-services.bat` | Windows | Inicia todos los microservicios |
| `start-all-services-ubuntu.sh` | Ubuntu | Versión optimizada para Ubuntu |
| `arka-central.sh` | Linux/macOS | Script maestro de gestión |

### 🐳 **Scripts de Docker**
| Script | Descripción |
|--------|-------------|
| `docker-start.sh` | Despliegue completo con Docker Compose |
| `docker-stop.sh` | Detiene todos los contenedores |

### 🔧 **Scripts de Configuración**
| Script | Plataforma | Descripción |
|--------|------------|-------------|
| `start-with-config-server.sh` | Linux/macOS | Inicia con Config Server |
| `start-with-config-server.bat` | Windows | Inicia con Config Server |
| `upload-config.sh` | Linux/macOS | Sube configuración al servidor |
| `upload-config.bat` | Windows | Sube configuración al servidor |

### 🧪 **Scripts de Testing**
| Script | Plataforma | Descripción |
|--------|------------|-------------|
| `test-circuit-breaker.sh` | Linux/macOS | Prueba Circuit Breakers |
| `test-circuit-breaker.bat` | Windows | Prueba Circuit Breakers |
| `test-config-server.sh` | Linux/macOS | Prueba Config Server |
| `test-config-server.bat` | Windows | Prueba Config Server |
| `test-load-balancing.sh` | Linux/macOS | Prueba Load Balancing |
| `test-load-balancing.bat` | Windows | Prueba Load Balancing |

### 🔍 **Scripts de Monitoreo**
| Script | Descripción |
|--------|-------------|
| `check-services.sh` | Verifica estado de todos los servicios |
| `check-requirements.sh` | Verifica prerequisitos del sistema |

### 🛑 **Scripts de Parada**
| Script | Descripción |
|--------|-------------|
| `stop-all-services.sh` | Detiene todos los servicios |

### 🎯 **Scripts Especializados**
| Script | Plataforma | Descripción |
|--------|------------|-------------|
| `start-circuit-breaker-cli.sh` | Linux/macOS | Demo de Circuit Breakers |
| `start-circuit-breaker-cli.bat` | Windows | Demo de Circuit Breakers |
| `start-ecommerce-complete.sh` | Linux/macOS | E-commerce completo |
| `start-ecommerce-complete.bat` | Windows | E-commerce completo |

---

## 🚀 **GUÍA DE USO**

### ⚡ **Inicio Rápido - Docker (Recomendado)**

```bash
# 1. Despliegue completo con Docker
./scripts/docker-start.sh

# 2. Verificar servicios
./scripts/check-services.sh

# 3. Para detener todo
./scripts/docker-stop.sh
```

### 🛠️ **Desarrollo Local**

```bash
# 1. Verificar prerequisitos
./scripts/check-requirements.sh

# 2. Iniciar servicios base
./scripts/start-all-services.sh

# 3. Verificar funcionamiento
./scripts/check-services.sh

# 4. Detener servicios
./scripts/stop-all-services.sh
```

### 🏗️ **Con Config Server**

```bash
# 1. Subir configuración
./scripts/upload-config.sh

# 2. Iniciar con Config Server
./scripts/start-with-config-server.sh

# 3. Verificar configuración
./scripts/test-config-server.sh
```

---

## 📝 **DESCRIPCIÓN DETALLADA**

### 🎯 **arka-central.sh** - Script Maestro
**Función**: Script principal que gestiona todo el ciclo de vida de la plataforma
```bash
./arka-central.sh [comando]

Comandos disponibles:
  start     - Inicia todos los servicios
  stop      - Detiene todos los servicios  
  restart   - Reinicia todos los servicios
  status    - Muestra estado de servicios
  logs      - Muestra logs de servicios
  health    - Verifica health checks
  clean     - Limpia recursos temporales
```

### 🐳 **docker-start.sh** - Despliegue Docker
**Función**: Despliegue completo usando Docker Compose
```bash
# Funcionalidades:
✅ Verificación de Docker
✅ Construcción de imágenes
✅ Inicio de servicios
✅ Health checks automáticos
✅ Monitoreo de estado
✅ Información de acceso
```

### 🔍 **check-services.sh** - Verificación de Servicios
**Función**: Verifica estado y health de todos los microservicios
```bash
# Servicios verificados:
✅ Eureka Server (8761)
✅ API Gateway (8080)  
✅ Main Application (8888)
✅ Gestor Solicitudes (8082)
✅ Cotizador (8083)
✅ Hello World (8084)
✅ Bases de datos
✅ Herramientas de monitoreo
```

### 🧪 **Scripts de Testing**

#### **test-circuit-breaker.sh**
```bash
# Prueba Circuit Breakers:
✅ Estado inicial
✅ Simulación de fallos
✅ Transición a OPEN
✅ Recovery automático
✅ Métricas y reportes
```

#### **test-load-balancing.sh**
```bash
# Prueba Load Balancing:
✅ Múltiples instancias
✅ Distribución de carga
✅ Failover automático
✅ Métricas de distribución
```

---

## 🛠️ **CONFIGURACIÓN AVANZADA**

### 🎛️ **Variables de Entorno**

```bash
# Configuración de puertos
export EUREKA_PORT=8761
export GATEWAY_PORT=8080
export MAIN_APP_PORT=8888
export GESTOR_PORT=8082
export COTIZADOR_PORT=8083

# Configuración de profiles
export SPRING_PROFILES_ACTIVE=dev

# Configuración de logging
export LOG_LEVEL=INFO
```

### 📊 **Configuración de Monitoring**

```bash
# Configuración de health checks
export HEALTH_CHECK_TIMEOUT=30
export HEALTH_CHECK_RETRIES=3
export HEALTH_CHECK_INTERVAL=10

# Configuración de métricas
export METRICS_ENABLED=true
export METRICS_EXPORT_INTERVAL=60
```

---

## 🔧 **SOLUCIÓN DE PROBLEMAS**

### ❌ **Problemas Comunes**

#### **Puerto en uso**
```bash
# Verificar puertos ocupados
netstat -tulpn | grep :8080

# Solución: Cambiar puerto o matar proceso
kill -9 $(lsof -t -i:8080)
```

#### **Docker no responde**
```bash
# Verificar Docker
docker --version
docker-compose --version

# Reiniciar Docker
sudo systemctl restart docker
```

#### **Servicios no se registran en Eureka**
```bash
# Verificar Eureka
curl http://localhost:8761/eureka/apps

# Revisar logs
docker-compose logs eureka-server
```

### 🔍 **Debug Mode**

```bash
# Activar debug en scripts
export DEBUG=true
./start-all-services.sh

# Logs detallados
export LOG_LEVEL=DEBUG
export VERBOSE=true
```

---

## 📱 **Scripts por Plataforma**

### 🐧 **Linux/macOS**
```bash
# Hacer ejecutables
chmod +x scripts/*.sh

# Ejecutar
./scripts/arka-central.sh start
```

### 🪟 **Windows**
```cmd
# PowerShell
PowerShell -ExecutionPolicy Bypass -File scripts/start-all-services.bat

# Command Prompt
scripts\start-all-services.bat
```

### 🐳 **Docker (Cualquier plataforma)**
```bash
# Universal con Docker
docker-compose up -d
```

---

## 📊 **Métricas y Logs**

### 📈 **Monitoreo Automático**
```bash
# Los scripts generan logs en:
logs/
├── services/
│   ├── eureka-server.log
│   ├── api-gateway.log
│   ├── main-app.log
│   └── microservices.log
├── docker/
│   ├── docker-compose.log
│   └── containers.log
└── health/
    ├── health-checks.log
    └── performance.log
```

### 🔍 **Análisis de Logs**
```bash
# Ver logs en tiempo real
tail -f logs/services/main-app.log

# Buscar errores
grep -i error logs/services/*.log

# Estadísticas de health checks
awk '/Health Check/ {print $0}' logs/health/health-checks.log
```

---

## 🤝 **Contribución**

### 📋 **Agregar Nuevos Scripts**
1. Crear script en directorio correspondiente
2. Agregar documentación en este README
3. Incluir en `arka-central.sh` si es necesario
4. Añadir tests correspondientes

### 📏 **Estándares de Scripts**
```bash
#!/bin/bash

# Configuración de error handling
set -e
set -u
set -o pipefail

# Funciones de logging
log_info() { echo "[INFO] $1"; }
log_error() { echo "[ERROR] $1" >&2; }
log_success() { echo "[SUCCESS] $1"; }

# Funciones de cleanup
cleanup() {
    # Limpieza al salir
    log_info "Cleaning up..."
}
trap cleanup EXIT
```

---

## 📞 **Soporte**

### 🆘 **Ayuda Rápida**
```bash
# Ayuda de cualquier script
./script-name.sh --help

# Información del sistema
./check-requirements.sh

# Estado completo
./arka-central.sh status
```

### 🔗 **Enlaces Útiles**
- **Documentación Principal**: [README.md](../README.md)
- **Guía Docker**: [DOCKER-DEPLOYMENT-GUIDE.md](../DOCKER-DEPLOYMENT-GUIDE.md)
- **API Testing**: [API-ENDPOINTS-TESTING.md](../API-ENDPOINTS-TESTING.md)

---

<div align="center">
  <strong>⚙️ Scripts automatizados para máxima eficiencia</strong><br/>
  <em>ARKA Platform - DevOps Excellence</em>
</div>
