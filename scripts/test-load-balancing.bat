@echo off
echo Testing Spring Cloud Load Balancing...

echo.
echo Testing Hello World Service via Gateway (should show different instances):
for /l %%i in (1,1,5) do (
    echo Request %%i:
    curl -s http://localhost:8080/api/hello
    echo.
    timeout /t 1 /nobreak > nul
)

echo.
echo Testing Cotizador Service via Gateway (should show different instances):
for /l %%i in (1,1,5) do (
    echo Request %%i to Cotizador:
    curl -s http://localhost:8080/api/cotizador/health
    echo.
    timeout /t 1 /nobreak > nul
)

echo.
echo Testing Gestor Service via Gateway (should show different instances):
for /l %%i in (1,1,5) do (
    echo Request %%i to Gestor:
    curl -s http://localhost:8080/api/gestor/health
    echo.
    timeout /t 1 /nobreak > nul
)

echo.
echo Testing direct access to Eureka registered services:
echo.
echo Eureka Dashboard: http://localhost:8761
echo API Gateway Health: 
curl -s http://localhost:8080/actuator/health
echo.

pause
