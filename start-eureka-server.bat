@echo off
REM Eureka Server - Puerto 8761
echo 🔍 Iniciando Eureka Server en puerto 8761...

java -Xmx512m -Xms256m -jar eureka-server/build/libs/eureka-server.jar --spring.profiles.active=k8s --server.port=8761

pause
