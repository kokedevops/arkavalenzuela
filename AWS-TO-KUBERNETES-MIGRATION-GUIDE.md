# ARKA E-commerce Platform - Guía de Migración AWS a Kubernetes

Esta guía documenta la migración completa del proyecto ARKA E-commerce de AWS a Kubernetes (k3s/Rancher).

## 📋 Tabla de Contenido

1. [Resumen de la Migración](#resumen-de-la-migración)
2. [Comparación: Antes vs Después](#comparación-antes-vs-después)
3. [Arquitectura AWS vs Kubernetes](#arquitectura-aws-vs-kubernetes)
4. [Guía de Migración Paso a Paso](#guía-de-migración-paso-a-paso)
5. [Configuración de Servicios](#configuración-de-servicios)
6. [Monitoreo y Logging](#monitoreo-y-logging)
7. [Seguridad](#seguridad)
8. [Networking](#networking)
9. [Almacenamiento](#almacenamiento)
10. [CI/CD Pipeline](#cicd-pipeline)
11. [Troubleshooting](#troubleshooting)

## 🔄 Resumen de la Migración

### Objetivos de la Migración
- **Costo**: Reducir costos operativos eliminando servicios AWS gestionados
- **Control**: Mayor control sobre la infraestructura y configuración
- **Portabilidad**: Capacidad de ejecutar en cualquier proveedor de nube o on-premise
- **Escalabilidad**: Mejor control de auto-escalado y recursos
- **DevOps**: Mejorar flujos de CI/CD con GitOps

### Estado Actual
✅ **Completado**: Migración completa a Kubernetes
- Todos los servicios containerizados
- Configuración declarativa con YAML
- Scripts de automatización
- Monitoreo y logging
- Documentación completa

## 📊 Comparación: Antes vs Después

### Infraestructura AWS (Antes)

| Componente | AWS Service | Costo Mensual Estimado |
|------------|-------------|-------------------------|
| Compute | EC2 instances | $200-500 |
| Database | RDS MySQL | $150-300 |
| Cache | ElastiCache Redis | $100-200 |
| Load Balancer | ALB/ELB | $25-50 |
| Service Discovery | Route 53 | $10-20 |
| Monitoring | CloudWatch | $50-100 |
| Storage | EBS/S3 | $50-100 |
| **Total** |  | **$585-1270** |

### Kubernetes (Después)

| Componente | K8s Resource | Costo Mensual Estimado |
|------------|--------------|-------------------------|
| Compute | Worker Nodes | $100-300 |
| Database | MySQL Pod + PVC | $0 (incluido) |
| Cache | Redis Pod | $0 (incluido) |
| Load Balancer | Ingress Controller | $0 (incluido) |
| Service Discovery | K8s Services | $0 (incluido) |
| Monitoring | Prometheus/Grafana | $0 (incluido) |
| Storage | Local PVC | $0-50 |
| **Total** |  | **$100-350** |

**💰 Ahorro Estimado: 60-75% en costos operativos**

## 🏗️ Arquitectura AWS vs Kubernetes

### Arquitectura AWS (Antes)
```
┌─────────────────────────────────────────────────────────────┐
│                         AWS Cloud                          │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │     ELB     │    │   Route53   │    │ CloudWatch  │    │
│  │ Load Balancer│    │   DNS       │    │ Monitoring  │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    │
│           │                 │                   │         │
│  ┌─────────────────────────────────────────────────────────┤
│  │                    VPC Network                         │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │  │    EC2      │  │    EC2      │  │    EC2      │   │
│  │  │ API Gateway │  │   Services  │  │Config Server│   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘   │
│  │                                                      │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │  │ RDS MySQL   │  │ElastiCache  │  │     S3      │   │
│  │  │  Database   │  │   Redis     │  │   Storage   │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘   │
│  └─────────────────────────────────────────────────────────┤
└─────────────────────────────────────────────────────────────┘
```

### Arquitectura Kubernetes (Después)
```
┌─────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                      │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │   Traefik   │    │  CoreDNS    │    │ Prometheus  │    │
│  │   Ingress   │    │   Service   │    │  Grafana    │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    │
│           │                 │                   │         │
│  ┌─────────────────────────────────────────────────────────┤
│  │              arka-ecommerce namespace                  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │  │API Gateway  │  │ E-commerce  │  │   Config    │   │
│  │  │    Pod      │  │    Core     │  │   Server    │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘   │
│  │                                                      │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │  │MySQL StatefulSet│   Redis    │  │  Eureka     │   │
│  │  │+ PersistentVolume│    Pod     │  │  Server     │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘   │
│  └─────────────────────────────────────────────────────────┤
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Guía de Migración Paso a Paso

### Paso 1: Preparación del Entorno

#### 1.1 Instalar Herramientas Requeridas
```bash
# kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"

# k3s (Opción 1)
curl -sfL https://get.k3s.io | sh -

# Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
```

#### 1.2 Configurar Cluster k3s
```bash
# Inicializar k3s cluster
sudo k3s server --write-kubeconfig-mode 644

# Verificar cluster
kubectl get nodes
kubectl get pods --all-namespaces
```

### Paso 2: Migración de Datos

#### 2.1 Backup de Base de Datos AWS RDS
```sql
-- Crear backup de MySQL en AWS RDS
mysqldump -h your-rds-endpoint.amazonaws.com \
  -u your-username -p your-database > arka_backup.sql
```

#### 2.2 Migrar Datos a Kubernetes
```bash
# Aplicar configuración de MySQL
kubectl apply -f k8s/mysql.yaml

# Esperar que MySQL esté listo
kubectl wait --for=condition=ready pod -l app=mysql --timeout=300s

# Restaurar datos
kubectl exec -i mysql-deployment-xxx -- mysql -u root -parka123 arka_ecommerce < arka_backup.sql
```

### Paso 3: Containerización de Aplicaciones

#### 3.1 Crear Imágenes Docker
```bash
# Construir todas las imágenes
./k8s/build-images.sh

# Verificar imágenes creadas
docker images | grep arka
```

#### 3.2 Configurar Registry (Opcional)
```bash
# Para clusters multi-nodo, configurar registry
docker run -d -p 5000:5000 --name registry registry:2

# Tag y push imágenes
./k8s/build-images.sh latest registry
```

### Paso 4: Despliegue en Kubernetes

#### 4.1 Aplicar Configuraciones
```bash
# Desplegar todo el stack
./k8s/deploy-k8s.sh

# Verificar despliegue
kubectl get pods -n arka-ecommerce
kubectl get services -n arka-ecommerce
```

#### 4.2 Configurar Ingress
```bash
# Verificar Ingress
kubectl get ingress -n arka-ecommerce

# Configurar DNS local (desarrollo)
echo "127.0.0.1 arka-ecommerce.local" >> /etc/hosts
echo "127.0.0.1 grafana.local" >> /etc/hosts
```

### Paso 5: Configuración de Monitoreo

#### 5.1 Verificar Prometheus
```bash
# Acceder a Prometheus
kubectl port-forward -n arka-monitoring svc/prometheus 9090:9090

# URL: http://localhost:9090
```

#### 5.2 Configurar Grafana
```bash
# Acceder a Grafana
kubectl port-forward -n arka-monitoring svc/grafana 3000:3000

# URL: http://localhost:3000
# Usuario: admin / Contraseña: arka123
```

### Paso 6: Pruebas de Funcionalidad

#### 6.1 Probar APIs
```bash
# Verificar API Gateway
curl http://arka-ecommerce.local/api/health

# Probar servicios
curl http://arka-ecommerce.local/api/cotizador/health
curl http://arka-ecommerce.local/api/solicitudes/health
```

#### 6.2 Verificar Base de Datos
```bash
# Conectar a MySQL
kubectl exec -it mysql-deployment-xxx -n arka-ecommerce -- mysql -u root -parka123

# Verificar datos migrados
USE arka_ecommerce;
SHOW TABLES;
SELECT COUNT(*) FROM products;
```

## ⚙️ Configuración de Servicios

### Spring Boot → Kubernetes

#### application.yml (Antes - AWS)
```yaml
spring:
  datasource:
    url: jdbc:mysql://your-rds-endpoint.amazonaws.com:3306/arka_ecommerce
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  redis:
    host: your-redis-cluster.amazonaws.com
    port: 6379
eureka:
  client:
    service-url:
      defaultZone: http://ec2-instance:8761/eureka/
```

#### application.yml (Después - K8s)
```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql-service.arka-ecommerce.svc.cluster.local:3306/arka_ecommerce
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  redis:
    host: redis-service.arka-ecommerce.svc.cluster.local
    port: 6379
eureka:
  client:
    service-url:
      defaultZone: http://eureka-service.arka-ecommerce.svc.cluster.local:8761/eureka/
```

### Variables de Entorno

#### ConfigMap vs AWS Parameter Store
```yaml
# k8s/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: arka-config
data:
  DATABASE_URL: "jdbc:mysql://mysql-service:3306/arka_ecommerce"
  REDIS_URL: "redis://redis-service:6379"
  EUREKA_URL: "http://eureka-service:8761/eureka/"
  MONITORING_ENABLED: "true"
  LOG_LEVEL: "INFO"
```

#### Secrets vs AWS Secrets Manager
```yaml
# k8s/secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: arka-secrets
type: Opaque
data:
  DB_PASSWORD: YXJrYTEyMw==  # base64 encoded
  REDIS_PASSWORD: ""
  JWT_SECRET: c2VjcmV0a2V5MTIzNDU2Nzg=
```

## 📊 Monitoreo y Logging

### Prometheus vs CloudWatch

#### Métricas Personalizadas
```yaml
# prometheus-config.yaml
scrape_configs:
  - job_name: 'arka-ecommerce'
    kubernetes_sd_configs:
      - role: pod
        namespaces:
          names:
          - arka-ecommerce
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
```

#### Dashboard Grafana
```json
{
  "dashboard": {
    "title": "ARKA E-commerce Metrics",
    "panels": [
      {
        "title": "Request Rate",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "{{ service }}"
          }
        ]
      },
      {
        "title": "Error Rate",
        "targets": [
          {
            "expr": "rate(http_requests_total{status=~\"4..|5..\"}[5m])",
            "legendFormat": "{{ service }} errors"
          }
        ]
      }
    ]
  }
}
```

### Logging

#### Fluentd Configuration
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: fluentd-config
data:
  fluent.conf: |
    <source>
      @type kubernetes_logs
      path /var/log/containers/*.log
      pos_file /var/log/fluentd-containers.log.pos
      tag kubernetes.*
    </source>
    
    <filter kubernetes.**>
      @type kubernetes_metadata
    </filter>
    
    <match kubernetes.**>
      @type elasticsearch
      host elasticsearch-service
      port 9200
      index_name arka-logs
    </match>
```

## 🔒 Seguridad

### IAM vs RBAC

#### AWS IAM Policy (Antes)
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "rds:DescribeDBInstances",
        "elasticache:DescribeCacheClusters",
        "ec2:DescribeInstances"
      ],
      "Resource": "*"
    }
  ]
}
```

#### Kubernetes RBAC (Después)
```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  namespace: arka-ecommerce
  name: arka-pod-reader
rules:
- apiGroups: [""]
  resources: ["pods", "services", "configmaps"]
  verbs: ["get", "watch", "list"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: read-pods
  namespace: arka-ecommerce
subjects:
- kind: ServiceAccount
  name: arka-service-account
  namespace: arka-ecommerce
roleRef:
  kind: Role
  name: arka-pod-reader
  apiGroup: rbac.authorization.k8s.io
```

### Network Security

#### Security Groups → Network Policies
```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: arka-network-policy
  namespace: arka-ecommerce
spec:
  podSelector:
    matchLabels:
      app: ecommerce-core
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: api-gateway
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: mysql
    ports:
    - protocol: TCP
      port: 3306
```

## 🌐 Networking

### ELB vs Ingress Controller

#### AWS ELB Configuration (Antes)
```json
{
  "LoadBalancerName": "arka-elb",
  "Listeners": [
    {
      "Protocol": "HTTP",
      "LoadBalancerPort": 80,
      "InstancePort": 8080
    },
    {
      "Protocol": "HTTPS",
      "LoadBalancerPort": 443,
      "InstancePort": 8080,
      "SSLCertificateId": "arn:aws:acm:..."
    }
  ],
  "HealthCheck": {
    "Target": "HTTP:8080/health",
    "Interval": 30,
    "Timeout": 5,
    "HealthyThreshold": 3,
    "UnhealthyThreshold": 2
  }
}
```

#### Kubernetes Ingress (Después)
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: arka-ingress
  namespace: arka-ecommerce
  annotations:
    kubernetes.io/ingress.class: traefik
    traefik.ingress.kubernetes.io/redirect-entry-point: https
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  tls:
  - hosts:
    - arka-ecommerce.local
    secretName: arka-tls
  rules:
  - host: arka-ecommerce.local
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: api-gateway-service
            port:
              number: 8080
```

### Service Discovery

#### Route 53 → CoreDNS
```yaml
# Automático con Kubernetes Services
apiVersion: v1
kind: Service
metadata:
  name: eureka-service
  namespace: arka-ecommerce
spec:
  selector:
    app: eureka-server
  ports:
  - port: 8761
    targetPort: 8761
  type: ClusterIP

# DNS automático: eureka-service.arka-ecommerce.svc.cluster.local
```

## 💾 Almacenamiento

### EBS vs Persistent Volumes

#### AWS EBS (Antes)
```json
{
  "VolumeType": "gp3",
  "Size": 100,
  "AvailabilityZone": "us-west-2a",
  "Encrypted": true,
  "Tags": [
    {
      "Key": "Name",
      "Value": "arka-database-volume"
    }
  ]
}
```

#### Kubernetes PV (Después)
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-pvc
  namespace: arka-ecommerce
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: local-path
  resources:
    requests:
      storage: 10Gi
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: arca-files-pvc
  namespace: arka-ecommerce
spec:
  accessModes:
    - ReadWriteMany
  storageClassName: local-path
  resources:
    requests:
      storage: 5Gi
```

## 🔄 CI/CD Pipeline

### AWS CodePipeline vs GitOps

#### GitHub Actions + ArgoCD
```yaml
# .github/workflows/deploy.yml
name: Deploy to Kubernetes
on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Build Docker Images
      run: |
        ./k8s/build-images.sh ${{ github.sha }}
        
    - name: Push to Registry
      run: |
        echo ${{ secrets.REGISTRY_PASSWORD }} | docker login -u ${{ secrets.REGISTRY_USER }} --password-stdin
        docker push ${{ secrets.REGISTRY_URL }}/arka/*:${{ github.sha }}
        
    - name: Update Kubernetes Manifests
      run: |
        sed -i 's|image: arka/|image: ${{ secrets.REGISTRY_URL }}/arka/|g' k8s/*.yaml
        sed -i 's|:latest|:${{ github.sha }}|g' k8s/*.yaml
        
    - name: Deploy to Kubernetes
      run: |
        kubectl apply -f k8s/
```

#### ArgoCD Application
```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: arka-ecommerce
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.com/your-org/arka-ecommerce
    targetRevision: HEAD
    path: k8s
  destination:
    server: https://kubernetes.default.svc
    namespace: arka-ecommerce
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
```

## 🔧 Troubleshooting

### Problemas Comunes y Soluciones

#### 1. Pods en Estado Pending
```bash
# Diagnóstico
kubectl describe pod <pod-name> -n arka-ecommerce

# Posibles causas:
# - Recursos insuficientes
# - PVC no disponible
# - Node selector no coincide

# Solución: Verificar recursos
kubectl top nodes
kubectl get pvc -n arka-ecommerce
```

#### 2. Servicios No Disponibles
```bash
# Verificar endpoints
kubectl get endpoints -n arka-ecommerce

# Verificar selectores
kubectl get pods --show-labels -n arka-ecommerce

# Probar conectividad
kubectl run debug --image=busybox -it --rm -- wget -qO- http://service-name:port/health
```

#### 3. Problemas de Base de Datos
```bash
# Verificar MySQL
kubectl logs mysql-deployment-xxx -n arka-ecommerce

# Conectar para debug
kubectl exec -it mysql-deployment-xxx -n arka-ecommerce -- mysql -u root -parka123

# Verificar datos
USE arka_ecommerce;
SHOW TABLES;
```

#### 4. Problemas de Networking
```bash
# Verificar Ingress
kubectl get ingress -n arka-ecommerce
kubectl describe ingress arka-ingress -n arka-ecommerce

# Verificar DNS
nslookup arka-ecommerce.local

# Probar conectividad
curl -v http://arka-ecommerce.local/health
```

### Comandos Útiles de Monitoreo

```bash
# Ver logs en tiempo real
kubectl logs -f deployment/ecommerce-core -n arka-ecommerce

# Monitorear recursos
watch kubectl top pods -n arka-ecommerce

# Verificar eventos del cluster
kubectl get events --sort-by=.metadata.creationTimestamp -n arka-ecommerce

# Backup de configuración
kubectl get all -o yaml -n arka-ecommerce > arka-backup.yaml
```

### Rollback en Caso de Problemas

```bash
# Ver historial de deployments
kubectl rollout history deployment/ecommerce-core -n arka-ecommerce

# Rollback a versión anterior
kubectl rollout undo deployment/ecommerce-core -n arka-ecommerce

# Rollback a versión específica
kubectl rollout undo deployment/ecommerce-core --to-revision=2 -n arka-ecommerce

# Verificar rollback
kubectl rollout status deployment/ecommerce-core -n arka-ecommerce
```

## 📈 Optimización Post-Migración

### Performance Tuning

#### Resource Limits Optimization
```yaml
resources:
  requests:
    cpu: 250m
    memory: 512Mi
  limits:
    cpu: 500m
    memory: 1Gi
```

#### Auto-scaling Configuration
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: ecommerce-core-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: ecommerce-core
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

### Cost Optimization

#### Node Scheduling
```yaml
# Usar spot instances para workloads no críticos
apiVersion: apps/v1
kind: Deployment
metadata:
  name: background-jobs
spec:
  template:
    spec:
      nodeSelector:
        node-type: spot
      tolerations:
      - key: spot
        operator: Equal
        value: "true"
        effect: NoSchedule
```

#### Resource Quotas
```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: arka-ecommerce-quota
  namespace: arka-ecommerce
spec:
  hard:
    requests.cpu: "4"
    requests.memory: 8Gi
    limits.cpu: "8"
    limits.memory: 16Gi
    persistentvolumeclaims: "5"
```

## 🎯 Conclusión

La migración de AWS a Kubernetes proporciona:

### Beneficios Obtenidos
- **💰 Reducción de costos**: 60-75% menos en gastos operativos
- **🔧 Mayor control**: Configuración granular de todos los componentes
- **📱 Portabilidad**: Capacidad de ejecutar en cualquier entorno
- **⚡ Escalabilidad**: Auto-scaling más eficiente y personalizable
- **🔍 Observabilidad**: Monitoreo integral con Prometheus/Grafana

### Próximos Pasos Recomendados
1. **Implementar GitOps** con ArgoCD para deployments automáticos
2. **Configurar backup automático** de base de datos y configuraciones
3. **Implementar disaster recovery** para alta disponibilidad
4. **Optimizar performance** basado en métricas de producción
5. **Configurar alertas avanzadas** para monitoreo proactivo

### Recursos Adicionales
- [Documentación de k3s](https://k3s.io/)
- [Rancher Documentation](https://rancher.com/docs/)
- [Kubernetes Best Practices](https://kubernetes.io/docs/concepts/)
- [Prometheus Monitoring](https://prometheus.io/docs/)

---

**🎉 ¡Migración Completada con Éxito!**

Para desplegar la plataforma ARKA en Kubernetes:
```bash
./k8s/deploy-k8s.sh
```

Para acceder a los servicios:
- **ARKA E-commerce**: http://arka-ecommerce.local
- **Grafana Dashboard**: http://grafana.local (admin/arka123)
- **Prometheus**: http://localhost:9090 (port-forward)
