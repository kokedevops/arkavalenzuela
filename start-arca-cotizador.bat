@echo off
REM Arca Cotizador - Puerto 8081
echo 🧮 Iniciando Arca Cotizador en puerto 8081...

java -Xmx512m -Xms256m -jar arca-cotizador/build/libs/arca-cotizador-0.0.1-SNAPSHOT.jar --spring.profiles.active=k8s --server.port=8081

pause
