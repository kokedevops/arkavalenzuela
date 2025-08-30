@echo off
REM Arca Gestor Solicitudes - Puerto 8082
echo 📋 Iniciando Arca Gestor Solicitudes en puerto 8082...

java -Xmx512m -Xms256m -jar arca-gestor-solicitudes/build/libs/arca-gestor-solicitudes-0.0.1-SNAPSHOT.jar --spring.profiles.active=k8s --server.port=8082

pause
