@echo off
REM Hello World Service - Puerto 8083
echo 👋 Iniciando Hello World Service en puerto 8083...

java -Xmx512m -Xms256m -jar hello-world-service/build/libs/hello-world-service.jar --spring.profiles.active=k8s --server.port=8083

pause
