# Dockerfile para la aplicación principal ARKA E-commerce
FROM openjdk:17-jdk-slim

# Metadata
LABEL maintainer="ARKA Development Team"
LABEL version="1.0"
LABEL description="ARKA E-commerce Platform - Main Application"

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8888

# Directorio de trabajo
WORKDIR /app

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y \
    curl \
    netcat-traditional \
    && rm -rf /var/lib/apt/lists/*

# Copiar archivos de Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copiar código fuente
COPY src src

# Hacer ejecutable el gradlew
RUN chmod +x ./gradlew

# Construir la aplicación
RUN ./gradlew clean build -x test

# Copiar el JAR generado
RUN cp build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8888

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
    CMD curl -f http://localhost:8888/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
