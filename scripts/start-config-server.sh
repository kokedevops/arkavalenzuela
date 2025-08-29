#!/bin/bash

# 🏗️ ARKA Config Server - Start Script
# Script para iniciar el Config Server con dependencias

echo "🏗️ Starting ARKA Config Server..."

# Verificar Java
if ! java -version 2>&1 | grep -q "21"; then
    echo "❌ Java 21 is required"
    exit 1
fi

# Verificar prerequisitos
echo "🔍 Checking prerequisites..."

# Verificar si Eureka está corriendo
echo "📡 Checking Eureka Server..."
if ! curl -s http://localhost:8761/actuator/health > /dev/null; then
    echo "⚠️  Eureka Server not running. Starting Eureka first..."
    # Puedes descomentar la siguiente línea si quieres auto-start Eureka
    # ./scripts/start-eureka.sh
    echo "📡 Please start Eureka Server first: ./gradlew :eureka-server:bootRun"
fi

# Compilar Config Server
echo "🔨 Building Config Server..."
./gradlew :config-server:clean :config-server:build

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

# Crear directorio de logs
mkdir -p logs

# Verificar directorio de configuraciones
if [ ! -d "config-repository" ]; then
    echo "❌ config-repository directory not found"
    exit 1
fi

echo "📁 Configuration repository: $(pwd)/config-repository"
echo "🔗 Eureka Discovery: http://localhost:8761"

# Iniciar Config Server
echo "🚀 Starting Config Server on port 8888..."
echo "📊 Health check: http://localhost:8888/actuator/health"
echo "🏠 Management: http://localhost:8888/actuator"

export SPRING_PROFILES_ACTIVE=dev
export CONFIG_REPOSITORY_PATH=$(pwd)/config-repository

./gradlew :config-server:bootRun \
    --args="--spring.profiles.active=dev \
            --spring.cloud.config.server.native.search-locations=file:./config-repository \
            --logging.file.name=logs/config-server.log"
