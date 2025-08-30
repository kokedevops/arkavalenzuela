@echo off
REM API Gateway - Puerto 8080
echo 🌐 Iniciando API Gateway en puerto 8080...

java -Xmx512m -Xms256m -jar api-gateway/build/libs/api-gateway.jar --spring.profiles.active=k8s --server.port=8080

pause
