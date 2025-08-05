@echo off
echo Starting additional instances for load balancing...

echo.
echo ==========================================
echo Starting Arca Cotizador Instance 2 (Port 8091)...
echo ==========================================
start "Arca Cotizador Instance 2" cmd /k "cd /d %~dp0.. && gradlew :arca-cotizador:bootRun --args=\"--server.port=8091 --eureka.instance.instance-id=arca-cotizador:8091\""

echo.
echo ==========================================
echo Starting Arca Gestor Solicitudes Instance 2 (Port 8092)...
echo ==========================================
start "Arca Gestor Solicitudes Instance 2" cmd /k "cd /d %~dp0.. && gradlew :arca-gestor-solicitudes:bootRun --args=\"--server.port=8092 --eureka.instance.instance-id=arca-gestor-solicitudes:8092\""

echo.
echo ==========================================
echo Starting Hello World Service Instance 2 (Port 8084)...
echo ==========================================
start "Hello World Service Instance 2" cmd /k "cd /d %~dp0.. && gradlew :hello-world-service:bootRun --args=\"--server.port=8084 --eureka.instance.instance-id=hello-world-service:8084\""

echo.
echo Load balancing instances started!
echo.
echo Test load balancing with:
echo curl http://localhost:8080/api/hello
echo curl http://localhost:8080/api/cotizador
echo curl http://localhost:8080/api/gestor

pause
