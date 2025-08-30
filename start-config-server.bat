@echo off
REM Config Server - Puerto 8888
echo 📋 Iniciando Config Server en puerto 8888...

java -Xmx512m -Xms256m -jar config-server/build/libs/config-server-1.0.0.jar --spring.profiles.active=k8s --server.port=8888

pause
