@echo off
echo ========================================
echo 🚀 INICIANDO PATRÓN SAGA - ARKA VALENZUELA
echo ========================================
echo.

echo 📋 Verificando aplicación...
.\gradlew clean build -x test
if %ERRORLEVEL% neq 0 (
    echo ❌ Error en build - Revisar código
    pause
    exit /b 1
)

echo ✅ Build exitoso
echo.

echo 🚀 Iniciando aplicación en modo AWS (mock)...
echo 📍 URL: http://localhost:8888
echo 📊 Health: http://localhost:8888/actuator/health
echo 🔍 Métricas: http://localhost:8888/actuator/metrics
echo.

echo ⏳ Esperando a que la aplicación inicie...
.\gradlew bootRun --args="--spring.profiles.active=aws"
