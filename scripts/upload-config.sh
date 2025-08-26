#!/bin/bash

echo "=========================================="
echo "SUBIENDO CONFIGURACIONES AL GITHUB REPO"
echo "=========================================="
echo ""

# Configuration repository details
REMOTE_REPO="https://github.com/kokedevops/arka-config-server.git"
CONFIG_DIR="config-repository"

# Check if we're in the right directory
if [[ ! -d "$CONFIG_DIR" ]]; then
    echo "❌ ERROR: Directorio $CONFIG_DIR no encontrado"
    echo "   Por favor, ejecuta este script desde el directorio raíz del proyecto"
    exit 1
fi

echo "🔍 Verificando estado del repositorio Git..."

# Check if .git exists
if [[ ! -d ".git" ]]; then
    echo "📁 Inicializando repositorio Git..."
    git init
    git remote add origin "$REMOTE_REPO"
    echo "✅ Repositorio Git inicializado"
else
    echo "✅ Repositorio Git ya existe"
    
    # Check if remote origin exists
    if ! git remote get-url origin >/dev/null 2>&1; then
        echo "🔗 Agregando remote origin..."
        git remote add origin "$REMOTE_REPO"
    else
        echo "🔗 Remote origin ya configurado: $(git remote get-url origin)"
    fi
fi

echo ""
echo "📝 Verificando archivos de configuración..."

# Check if config files exist
if [[ ! "$(ls -A $CONFIG_DIR 2>/dev/null)" ]]; then
    echo "⚠️  WARNING: El directorio $CONFIG_DIR está vacío"
    echo "   Creando archivos de configuración de ejemplo..."
    
    mkdir -p "$CONFIG_DIR"
    
    # Create sample configuration files
    cat > "$CONFIG_DIR/application.yml" << EOF
# Configuración global para todos los servicios
spring:
  application:
    name: arka-microservices
  
logging:
  level:
    com.arka: DEBUG
    org.springframework.cloud: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,info,refresh,configprops
  endpoint:
    health:
      show-details: always
EOF

    cat > "$CONFIG_DIR/eureka-server.yml" << EOF
# Configuración específica para Eureka Server
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false
EOF

    cat > "$CONFIG_DIR/api-gateway.yml" << EOF
# Configuración específica para API Gateway
server:
  port: 8080

spring:
  cloud:
    gateway:
      routes:
        - id: arca-cotizador
          uri: lb://arca-cotizador
          predicates:
            - Path=/api/cotizador/**
        - id: arca-gestor-solicitudes
          uri: lb://arca-gestor-solicitudes
          predicates:
            - Path=/api/gestor/**
EOF

    echo "✅ Archivos de configuración de ejemplo creados"
fi

echo ""
echo "📤 Preparando archivos para subir..."

# Check git status
git status --porcelain

echo ""
echo "📦 Agregando archivos de configuración..."
git add "$CONFIG_DIR/"

# Check if there are changes to commit
if git diff --cached --quiet; then
    echo "ℹ️  No hay cambios nuevos para commitear"
else
    echo "💾 Realizando commit..."
    
    # Generate commit message with timestamp
    TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')
    COMMIT_MSG="feat: actualizar configuraciones de microservicios - $TIMESTAMP"
    
    git commit -m "$COMMIT_MSG"
    echo "✅ Commit realizado: $COMMIT_MSG"
fi

echo ""
echo "🚀 Subiendo al repositorio remoto..."

# Check if we have commits to push
if git log origin/main..HEAD --oneline 2>/dev/null | grep -q .; then
    echo "📡 Subiendo cambios..."
    
    if git push origin main; then
        echo "✅ Configuraciones subidas exitosamente!"
    else
        echo "❌ Error al subir configuraciones"
        echo ""
        echo "🔧 Posibles soluciones:"
        echo "1. Verificar credenciales de Git"
        echo "2. Verificar conectividad a internet"
        echo "3. Verificar permisos del repositorio"
        echo ""
        echo "💡 Comandos útiles:"
        echo "git config --global user.name 'Tu Nombre'"
        echo "git config --global user.email 'tu-email@example.com'"
        echo "git remote -v  # Verificar remote URL"
        exit 1
    fi
else
    echo "ℹ️  No hay commits nuevos para subir"
fi

echo ""
echo "=========================================="
echo "🎉 CONFIGURACIONES SUBIDAS EXITOSAMENTE!"
echo "=========================================="
echo ""
echo "🔄 Para actualizar configuraciones en tiempo real:"
echo ""
echo "1️⃣ Modifica los archivos en $CONFIG_DIR/"
echo "2️⃣ Ejecuta este script nuevamente"
echo "3️⃣ Llama al endpoint de refresh en cada servicio:"
echo ""
echo "# Refrescar configuración de Arca Cotizador:"
echo "curl -X POST http://localhost:8081/actuator/refresh"
echo ""
echo "# Refrescar configuración de Arca Gestor Solicitudes:"
echo "curl -X POST http://localhost:8082/actuator/refresh"
echo ""
echo "# Refrescar configuración de API Gateway:"
echo "curl -X POST http://localhost:8080/actuator/refresh"
echo ""
echo "# Refrescar configuración de Hello World Service:"
echo "curl -X POST http://localhost:8083/actuator/refresh"
echo ""
echo "🔍 Verificar que las configuraciones se aplicaron:"
echo "curl http://localhost:8081/actuator/configprops | jq ."
echo ""
echo "📋 Repositorio de configuración: $REMOTE_REPO"
echo "📁 Directorio local: $CONFIG_DIR/"
