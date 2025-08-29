# 🚀 ARKA E-COMMERCE PLATFORM - KUBERNETES DEPLOYMENT GUIDE

## 📋 Overview
Esta guía explica cómo desplegar la plataforma ARKA E-commerce en Kubernetes utilizando k3s o Rancher.

## 🎯 Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    KUBERNETES CLUSTER                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                 INGRESS LAYER                           ││
│  │         Traefik / NGINX Ingress Controller              ││
│  │     *.arka.local → Load Balancer → Services            ││
│  └─────────────────────────────────────────────────────────┘│
│                              │                              │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                APPLICATION LAYER                        ││
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    ││
│  │  │ API Gateway │  │Config Server│  │Eureka Server│    ││
│  │  │   (2 pods)  │  │   (1 pod)   │  │   (1 pod)   │    ││
│  │  └─────────────┘  └─────────────┘  └─────────────┘    ││
│  │                                                         ││
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    ││
│  │  │ E-commerce  │  │    Arca     │  │    Arca     │    ││
│  │  │    Core     │  │  Cotizador  │  │   Gestor    │    ││
│  │  │  (2 pods)   │  │  (2 pods)   │  │  (2 pods)   │    ││
│  │  └─────────────┘  └─────────────┘  └─────────────┘    ││
│  └─────────────────────────────────────────────────────────┘│
│                              │                              │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                  DATA LAYER                             ││
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    ││
│  │  │    MySQL    │  │   MongoDB   │  │    Redis    │    ││
│  │  │  (1 pod)    │  │   (1 pod)   │  │   (1 pod)   │    ││
│  │  │   10Gi PVC  │  │   10Gi PVC  │  │   2Gi PVC   │    ││
│  │  └─────────────┘  └─────────────┘  └─────────────┘    ││
│  └─────────────────────────────────────────────────────────┘│
│                              │                              │
│  ┌─────────────────────────────────────────────────────────┐│
│  │               MONITORING LAYER                          ││
│  │  ┌─────────────┐  ┌─────────────┐                      ││
│  │  │ Prometheus  │  │   Grafana   │                      ││
│  │  │   (1 pod)   │  │   (1 pod)   │                      ││
│  │  │   10Gi PVC  │  │   5Gi PVC   │                      ││
│  │  └─────────────┘  └─────────────┘                      ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

## 🔧 Prerequisites

### Hardware Requirements
- **Minimum**: 4 CPU cores, 8GB RAM, 50GB storage
- **Recommended**: 8 CPU cores, 16GB RAM, 100GB storage
- **Production**: 16 CPU cores, 32GB RAM, 200GB storage

### Software Requirements
- Kubernetes 1.24+ (k3s, k8s, or Rancher)
- kubectl client
- Helm 3.x (optional)
- Docker (for image building)

## 🚀 Quick Start

### Option 1: k3s Deployment
```bash
# 1. Install k3s
curl -sfL https://get.k3s.io | sh -

# 2. Setup kubeconfig
export KUBECONFIG=/etc/rancher/k3s/k3s.yaml

# 3. Deploy ARKA platform
chmod +x k8s/deploy-k8s.sh
./k8s/deploy-k8s.sh
```

### Option 2: Rancher Deployment
```bash
# 1. Install Rancher (see RANCHER-SETUP-GUIDE.md)
# 2. Create cluster in Rancher UI
# 3. Deploy ARKA platform
./k8s/deploy-k8s.sh
```

### Option 3: Standard Kubernetes
```bash
# 1. Ensure kubectl is configured
kubectl cluster-info

# 2. Deploy ARKA platform
./k8s/deploy-k8s.sh
```

## 📁 File Structure

```
k8s/
├── namespace.yaml                    # Namespaces creation
├── mysql.yaml                       # MySQL database
├── mongodb.yaml                     # MongoDB database
├── redis.yaml                       # Redis cache
├── config-server.yaml              # Spring Cloud Config Server
├── eureka-server.yaml              # Service Discovery
├── api-gateway.yaml                # API Gateway
├── ecommerce-core.yaml             # Main application
├── arca-cotizador.yaml             # Cotizador microservice
├── arca-gestor-solicitudes.yaml    # Gestor microservice
├── hello-world-service.yaml        # Demo service
├── monitoring-prometheus.yaml      # Prometheus monitoring
├── monitoring-grafana.yaml         # Grafana dashboards
├── ingress-and-networking.yaml     # Ingress & Network policies
├── deploy-k8s.sh                   # Deployment script (Linux)
├── deploy-k8s.bat                  # Deployment script (Windows)
├── undeploy-k8s.sh                 # Cleanup script (Linux)
├── undeploy-k8s.bat                # Cleanup script (Windows)
├── K3S-SETUP-GUIDE.md             # k3s specific guide
├── RANCHER-SETUP-GUIDE.md         # Rancher specific guide
└── rancher/
    └── rancher-config.yaml         # Rancher-specific configurations
```

## ⚙️ Configuration Details

### Namespaces
- `arka-ecommerce`: Main application namespace
- `arka-monitoring`: Monitoring tools namespace
- `arka-dev`: Development environment namespace

### Storage
- **MySQL**: 10Gi persistent volume for transactional data
- **MongoDB**: 10Gi persistent volume for analytics data  
- **Redis**: 2Gi persistent volume for cache data
- **Prometheus**: 10Gi persistent volume for metrics
- **Grafana**: 5Gi persistent volume for dashboards

### Security
- Secrets for database passwords
- RBAC policies for service accounts
- Network policies for traffic control
- JWT tokens for service authentication

### Monitoring
- **Prometheus**: Metrics collection from all services
- **Grafana**: Dashboards with ARKA-specific visualizations
- **Health checks**: Liveness and readiness probes
- **Alerting**: Custom alert rules for ARKA platform

## 🔗 Service Endpoints

### Internal Services (ClusterIP)
- `config-server-service:8888` - Configuration server
- `eureka-service:8761` - Service discovery
- `mysql-service:3306` - MySQL database
- `mongodb-service:27017` - MongoDB database
- `redis-service:6379` - Redis cache
- `ecommerce-core-service:8888` - Main application
- `arca-cotizador-service:8081` - Cotizador service
- `arca-gestor-solicitudes-service:8082` - Gestor service

### External Access (NodePort/LoadBalancer)
- `api-gateway-lb:80/443` - Main API Gateway
- `eureka-nodeport:30761` - Eureka dashboard
- `grafana-service:30300` - Grafana dashboard
- `prometheus-service:30090` - Prometheus UI

### Ingress Routes
- `https://arka.local` - Main application
- `https://api.arka.local` - API Gateway
- `https://eureka.arka.local` - Eureka dashboard
- `https://monitoring.arka.local/grafana` - Grafana
- `https://monitoring.arka.local/prometheus` - Prometheus

## 🔧 Customization

### Environment Variables
```yaml
env:
- name: SPRING_PROFILES_ACTIVE
  value: "k8s"
- name: SPRING_CLOUD_CONFIG_URI  
  value: "http://config-server-service:8888"
- name: EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE
  value: "http://eureka-service:8761/eureka/"
```

### Resource Limits
```yaml
resources:
  limits:
    memory: "1Gi"
    cpu: "500m"
  requests:
    memory: "512Mi" 
    cpu: "250m"
```

### Scaling Configuration
```yaml
# Horizontal Pod Autoscaler
spec:
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

## 🛠️ Deployment Commands

### Deploy Platform
```bash
# Linux/macOS
./k8s/deploy-k8s.sh

# Windows
k8s\deploy-k8s.bat
```

### Check Status
```bash
# View all pods
kubectl get pods -n arka-ecommerce

# View services
kubectl get svc -n arka-ecommerce

# View ingress
kubectl get ingress -n arka-ecommerce

# Check logs
kubectl logs -f deployment/api-gateway -n arka-ecommerce
```

### Scale Services
```bash
# Scale API Gateway
kubectl scale deployment api-gateway --replicas=3 -n arka-ecommerce

# Scale E-commerce Core
kubectl scale deployment ecommerce-core --replicas=4 -n arka-ecommerce
```

### Port Forwarding for Local Access
```bash
# API Gateway
kubectl port-forward svc/api-gateway-service 8080:8080 -n arka-ecommerce

# Grafana
kubectl port-forward svc/grafana-service 3000:3000 -n arka-monitoring

# Prometheus  
kubectl port-forward svc/prometheus-service 9090:9090 -n arka-monitoring
```

## 🗑️ Cleanup

### Remove ARKA Platform
```bash
# Linux/macOS
./k8s/undeploy-k8s.sh

# Windows
k8s\undeploy-k8s.bat
```

### Manual Cleanup
```bash
# Delete namespaces (this removes everything)
kubectl delete namespace arka-ecommerce
kubectl delete namespace arka-monitoring

# Delete persistent volumes (if needed)
kubectl delete pv --all
```

## 🔍 Troubleshooting

### Common Issues

#### Pods Stuck in Pending
```bash
# Check node resources
kubectl describe nodes

# Check PVC status
kubectl get pvc -n arka-ecommerce
```

#### Service Discovery Issues
```bash
# Check Eureka logs
kubectl logs deployment/eureka-server -n arka-ecommerce

# Verify service registration
kubectl port-forward svc/eureka-service 8761:8761 -n arka-ecommerce
# Visit http://localhost:8761
```

#### Database Connection Issues
```bash
# Check database pods
kubectl get pods -l tier=database -n arka-ecommerce

# Check database logs  
kubectl logs deployment/mysql -n arka-ecommerce
```

### Useful Debug Commands
```bash
# Describe pod for events
kubectl describe pod <pod-name> -n arka-ecommerce

# Execute into pod
kubectl exec -it <pod-name> -n arka-ecommerce -- /bin/bash

# Check resource usage
kubectl top pods -n arka-ecommerce
kubectl top nodes
```

## 📊 Performance Monitoring

### Key Metrics to Monitor
- **CPU Usage**: Target < 70%
- **Memory Usage**: Target < 80%
- **Response Time**: Target < 500ms
- **Error Rate**: Target < 1%
- **Database Connections**: Monitor pool usage

### Grafana Dashboards
- ARKA Platform Overview
- Service Performance
- Database Metrics
- Infrastructure Health

### Prometheus Alerts
- Service Down
- High Memory Usage
- High CPU Usage
- Database Connection Failures
- High Response Times

## 🔐 Security Considerations

### Network Security
- Network policies restrict inter-pod communication
- Ingress controller handles TLS termination
- Services use ClusterIP for internal communication

### Data Security
- Database passwords stored in Kubernetes secrets
- JWT tokens for service authentication
- Regular security updates for base images

### Access Control
- RBAC policies for service accounts
- Namespace isolation
- Pod security standards

## 🚀 Production Considerations

### High Availability
- Multiple replicas for stateless services
- Pod disruption budgets
- Anti-affinity rules for pod placement

### Backup Strategy
- Database backups to external storage
- Configuration backups
- Disaster recovery procedures

### Monitoring & Alerting
- 24/7 monitoring setup
- Alert routing to appropriate teams
- Incident response procedures

### Performance Optimization
- Resource limits and requests tuning
- JVM optimization for Spring Boot apps
- Database connection pooling
- Cache configuration optimization

## 📞 Support

For issues and questions:
1. Check the troubleshooting section
2. Review logs using kubectl commands
3. Consult specific setup guides (K3S-SETUP-GUIDE.md, RANCHER-SETUP-GUIDE.md)
4. Open GitHub issue with detailed logs and environment information
