# ARKA E-commerce Platform - Rancher Setup and Management Guide

## 🚀 Rancher Installation and ARKA Platform Deployment

### Prerequisites
- Docker installed
- 4GB+ RAM
- 20GB+ disk space
- Domain or IP for Rancher access

### 1. Install Rancher Server

#### Option A: Docker Installation (Single Node)
```bash
# Run Rancher server
docker run -d --restart=unless-stopped \
  -p 80:80 -p 443:443 \
  --privileged \
  --name rancher \
  -v rancher-data:/var/lib/rancher \
  rancher/rancher:latest
```

#### Option B: Kubernetes Installation (Production)
```bash
# Add Rancher Helm repository
helm repo add rancher-latest https://releases.rancher.com/server-charts/latest
helm repo update

# Create namespace
kubectl create namespace cattle-system

# Install cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.11.0/cert-manager.yaml

# Install Rancher
helm install rancher rancher-latest/rancher \
  --namespace cattle-system \
  --set hostname=rancher.arka.local \
  --set replicas=1 \
  --set bootstrapPassword=ArkaRancher2025
```

### 2. Access Rancher
1. Open browser and go to `https://rancher.arka.local` (or your server IP)
2. Set admin password: `ArkaRancher2025`
3. Configure Rancher URL

### 3. Create/Import Kubernetes Cluster

#### Option A: Create New k3s Cluster
1. In Rancher UI: **Cluster Management** > **Create**
2. Select **Custom**
3. Cluster Name: `arka-k3s-cluster`
4. Kubernetes Version: Latest
5. Copy and run the command on your k3s node

#### Option B: Import Existing Cluster
1. **Cluster Management** > **Import Existing**
2. Select **Generic**
3. Cluster Name: `arka-existing-cluster`
4. Run the kubectl command provided

### 4. Create ARKA Project

#### Via Rancher UI:
1. Go to your cluster
2. **Projects/Namespaces** > **Create Project**
3. Project Name: `ARKA E-commerce Platform`
4. Description: `Microservices e-commerce platform with Spring Boot`
5. Create Namespaces:
   - `arka-ecommerce`
   - `arka-dev` 
   - `arka-monitoring`

#### Via kubectl:
```bash
kubectl apply -f k8s/rancher/rancher-config.yaml
```

### 5. Deploy ARKA Platform via Rancher

#### Method 1: Rancher Apps & Marketplace
1. **Apps & Marketplace** > **Charts**
2. Upload ARKA Helm chart
3. Configure values:
   ```yaml
   global:
     namespace: arka-ecommerce
     imageRegistry: docker.io
     imagePullSecrets: []
   
   mysql:
     enabled: true
     auth:
       rootPassword: ArkaMySQL2025
       database: arkaecommerce
   
   mongodb:
     enabled: true
     auth:
       rootPassword: ArkaMonog2025
   
   redis:
     enabled: true
     auth:
       password: ArkaRedis2025
   
   configServer:
     image:
       repository: arka/config-server
       tag: latest
   
   ecommerceCore:
     replicas: 2
     image:
       repository: arka/ecommerce-core
       tag: latest
   ```

#### Method 2: Rancher Fleet (GitOps)
Create `fleet.yaml`:
```yaml
namespace: fleet-default
helm:
  chart: ./k8s/helm-chart
  values:
    global:
      namespace: arka-ecommerce
    mysql:
      enabled: true
      auth:
        rootPassword: ArkaMySQL2025
    mongodb:
      enabled: true
      auth:
        rootPassword: ArkaMonog2025
```

#### Method 3: Direct kubectl
```bash
# Ensure kubectl context is set to Rancher-managed cluster
kubectl config current-context

# Deploy ARKA platform
./k8s/deploy-k8s.sh
```

### 6. Configure Rancher Monitoring

#### Enable Monitoring
1. **Cluster Tools** > **Monitoring**
2. Install **rancher-monitoring**
3. Configure Prometheus:
   ```yaml
   prometheus:
     prometheusSpec:
       retention: 30d
       storageSpec:
         volumeClaimTemplate:
           spec:
             storageClassName: local-path
             resources:
               requests:
                 storage: 10Gi
   ```

#### Import ARKA Grafana Dashboards
1. Access Grafana via Rancher
2. **Import Dashboard**
3. Upload `k8s/grafana-dashboards/arka-overview.json`

### 7. Configure Rancher Logging

#### Enable Logging
1. **Cluster Tools** > **Logging**
2. Install **rancher-logging**
3. Configure Outputs:
   ```yaml
   apiVersion: logging.coreos.com/v1beta1
   kind: Output
   metadata:
     name: arka-elasticsearch
     namespace: cattle-logging-system
   spec:
     elasticsearch:
       host: elasticsearch.arka.local
       port: 9200
       index_name: arka-logs
   ```

### 8. Set up Rancher Backup

#### Configure Backup
```yaml
apiVersion: resources.cattle.io/v1
kind: Backup
metadata:
  name: arka-platform-backup
spec:
  resourceSetName: rancher-resource-set
  schedule: "0 2 * * *"  # Daily at 2 AM
  retentionCount: 7
  storageLocation:
    s3:
      credentialSecretName: s3-creds
      credentialSecretNamespace: default
      bucketName: arka-backups
      folder: kubernetes-backups
      region: us-west-2
      endpoint: s3.amazonaws.com
```

### 9. Rancher Pipeline for CI/CD

#### Create Pipeline
1. **Workloads** > **Pipelines** > **Configure Repositories**
2. Connect to GitHub repository
3. Create `.rancher-pipeline.yml`:

```yaml
stages:
  - name: Build
    steps:
    - runScriptConfig:
        image: gradle:8.5-jdk21
        shellScript: |
          gradle clean build
  
  - name: Build Images
    steps:
    - publishImageConfig:
        dockerfilePath: ./Dockerfile
        buildContext: .
        tag: arka/ecommerce-core:${CICD_EXECUTION_SEQUENCE}
        registry: index.docker.io
  
  - name: Deploy
    steps:
    - applyYamlConfig:
        path: ./k8s/
    when:
      branch: main
```

### 10. Rancher Security & RBAC

#### Create Custom Roles
```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: arka-developer
  namespace: arka-ecommerce
rules:
- apiGroups: [""]
  resources: ["pods", "services", "configmaps"]
  verbs: ["get", "list", "watch", "create", "update", "patch"]
- apiGroups: ["apps"]
  resources: ["deployments", "replicasets"]
  verbs: ["get", "list", "watch", "create", "update", "patch"]
```

#### Assign Users to Project
1. **Projects/Namespaces** > Select ARKA project
2. **Members** > **Add Member**
3. Select users and assign roles:
   - `Project Owner` - DevOps team
   - `Project Member` - Developers
   - `Read Only` - QA team

### 11. Rancher Marketplace Apps

#### Install Additional Tools
```bash
# Cert-Manager for TLS
helm install cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --set installCRDs=true

# External DNS
helm install external-dns bitnami/external-dns \
  --namespace external-dns \
  --create-namespace \
  --set provider=aws \
  --set aws.zoneType=public \
  --set txtOwnerId=arka-cluster
```

### 12. Multi-Cluster Management

#### Register Additional Clusters
```bash
# For each additional cluster
curl --insecure -sfL https://rancher.arka.local/v3/import/CLUSTER_TOKEN.yaml | kubectl apply -f -
```

#### Fleet GitOps Configuration
```yaml
# gitrepo.yaml
apiVersion: fleet.cattle.io/v1alpha1
kind: GitRepo
metadata:
  name: arka-platform
  namespace: fleet-default
spec:
  repo: https://github.com/kokedevops/arkavalenzuela
  branch: main
  paths:
  - k8s/
  targets:
  - name: production
    clusterSelector:
      matchLabels:
        env: production
  - name: staging
    clusterSelector:
      matchLabels:
        env: staging
```

### 13. Monitoring & Alerting

#### Prometheus Rules
```yaml
apiVersion: monitoring.coreos.com/v1
kind: PrometheusRule
metadata:
  name: arka-alerts
  namespace: arka-ecommerce
spec:
  groups:
  - name: arka.rules
    rules:
    - alert: ArkaServiceDown
      expr: up{job=~".*arka.*"} == 0
      for: 1m
      labels:
        severity: critical
      annotations:
        summary: "ARKA service {{ $labels.job }} is down"
```

#### Alert Manager
```yaml
global:
  smtp_smarthost: 'smtp.gmail.com:587'
  smtp_from: 'alerts@arka.local'

route:
  group_by: ['alertname']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'arka-team'

receivers:
- name: 'arka-team'
  email_configs:
  - to: 'devops@arka.local'
    subject: 'ARKA Platform Alert: {{ .GroupLabels.alertname }}'
```

### 14. Troubleshooting

#### Common Issues
```bash
# Check Rancher logs
kubectl logs -n cattle-system deployment/rancher

# Check cluster connectivity
kubectl get nodes
kubectl get pods -A

# Reset Rancher admin password
kubectl -n cattle-system exec $(kubectl -n cattle-system get pods -l app=rancher --no-headers | head -1 | awk '{ print $1 }') -- reset-password

# Check certificate issues
kubectl get certificates -A
kubectl describe certificate rancher -n cattle-system
```

#### Performance Tuning
```yaml
# Increase Rancher resources
resources:
  limits:
    cpu: 2000m
    memory: 4Gi
  requests:
    cpu: 1000m
    memory: 2Gi
```

### 15. Backup and Disaster Recovery

#### Backup Rancher
```bash
# Create backup
kubectl create -f - <<EOF
apiVersion: resources.cattle.io/v1
kind: Backup
metadata:
  name: rancher-backup-$(date +%Y%m%d-%H%M%S)
spec:
  resourceSetName: rancher-resource-set
EOF
```

#### Restore Rancher
```bash
# Restore from backup
kubectl create -f - <<EOF
apiVersion: resources.cattle.io/v1
kind: Restore
metadata:
  name: restore-rancher
spec:
  backupFilename: "rancher-backup-YYYYMMDD-HHMMSS.tar.gz"
  prune: false
EOF
```

### 16. Useful Rancher CLI Commands

#### Install Rancher CLI
```bash
# Download and install
wget https://github.com/rancher/cli/releases/download/v2.7.0/rancher-linux-amd64-v2.7.0.tar.gz
tar -xzf rancher-linux-amd64-v2.7.0.tar.gz
sudo mv rancher-v2.7.0/rancher /usr/local/bin/

# Login to Rancher
rancher login https://rancher.arka.local --token $RANCHER_TOKEN
```

#### Useful Commands
```bash
# List clusters
rancher clusters ls

# Switch context
rancher context switch

# List projects
rancher projects ls

# Deploy app
rancher app install arka-platform
```
