# Test Endpoints for Spring Cloud Services

## Basic Service Health Checks

### Eureka Server
GET http://localhost:8761/

### API Gateway Health
GET http://localhost:8080/actuator/health

### Hello World Service (via Gateway)
GET http://localhost:8080/api/hello
GET http://localhost:8080/api/hello/info
GET http://localhost:8080/api/hello/health

### Cotizador Service (via Gateway)
GET http://localhost:8080/api/cotizador/actuator/health

### Gestor Service (via Gateway)
GET http://localhost:8080/api/gestor/actuator/health

## Direct Service Access (for testing load balancing)

### Cotizador Instances
GET http://localhost:8081/actuator/health
GET http://localhost:8091/actuator/health

### Gestor Instances
GET http://localhost:8082/actuator/health
GET http://localhost:8092/actuator/health

### Hello World Instance
GET http://localhost:8083/
GET http://localhost:8083/info
GET http://localhost:8083/health

## Eureka Service Registry
GET http://localhost:8761/eureka/apps

## Gateway Routes
GET http://localhost:8080/actuator/gateway/routes

## Load Balancing Test (run multiple times to see different instances)
GET http://localhost:8080/api/hello
GET http://localhost:8080/api/hello/info

## Example PowerShell Commands for Testing

```powershell
# Test load balancing with multiple requests
for ($i=1; $i -le 10; $i++) {
    Write-Host "Request $i:"
    Invoke-RestMethod -Uri "http://localhost:8080/api/hello" -Method GET
    Start-Sleep -Seconds 1
}

# Check all registered services
Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Method GET -Headers @{"Accept"="application/json"}

# Check gateway routes
Invoke-RestMethod -Uri "http://localhost:8080/actuator/gateway/routes" -Method GET
```
