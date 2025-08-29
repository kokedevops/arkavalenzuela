# ARKA E-commerce Platform - k3s Installation and Setup Guide

## 🚀 Quick k3s Setup for ARKA Platform

### Prerequisites
- Linux server (Ubuntu 20.04+ recommended)
- 4GB+ RAM
- 20GB+ disk space
- Root or sudo access

### 1. Install k3s
```bash
# Install k3s with Traefik disabled (we'll use our own ingress)
curl -sfL https://get.k3s.io | INSTALL_K3S_EXEC="--disable traefik" sh -

# Start k3s service
sudo systemctl enable k3s
sudo systemctl start k3s

# Get kubeconfig
sudo cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
sudo chown $(id -u):$(id -g) ~/.kube/config
```

### 2. Install Traefik Ingress Controller
```bash
# Add Traefik Helm repository
helm repo add traefik https://helm.traefik.io/traefik
helm repo update

# Install Traefik
helm install traefik traefik/traefik \
  --namespace traefik-system \
  --create-namespace \
  --set service.type=LoadBalancer \
  --set ports.web.exposedPort=80 \
  --set ports.websecure.exposedPort=443
```

### 3. Verify k3s Installation
```bash
# Check nodes
kubectl get nodes

# Check system pods
kubectl get pods -A

# Check if local-path provisioner is working
kubectl get storageclass
```

### 4. Deploy ARKA Platform
```bash
# Make scripts executable
chmod +x k8s/deploy-k8s.sh
chmod +x k8s/undeploy-k8s.sh

# Deploy the platform
./k8s/deploy-k8s.sh
```

### 5. Configure Local DNS (Optional)
Add to `/etc/hosts`:
```
<k3s-server-ip> arka.local
<k3s-server-ip> api.arka.local
<k3s-server-ip> eureka.arka.local
<k3s-server-ip> monitoring.arka.local
```

### 6. Access Services

#### Via LoadBalancer (if available)
```bash
# Get LoadBalancer IP
kubectl get svc api-gateway-lb -n arka-ecommerce
```

#### Via Port Forwarding
```bash
# API Gateway
kubectl port-forward svc/api-gateway-service 8080:8080 -n arka-ecommerce

# Eureka Dashboard
kubectl port-forward svc/eureka-service 8761:8761 -n arka-ecommerce

# Grafana
kubectl port-forward svc/grafana-service 3000:3000 -n arka-monitoring

# Prometheus
kubectl port-forward svc/prometheus-service 9090:9090 -n arka-monitoring
```

#### Via NodePort
```bash
# Get node IP
kubectl get nodes -o wide

# Access services on NodePort
# Eureka: http://<node-ip>:30761
# Grafana: http://<node-ip>:30300
# Prometheus: http://<node-ip>:30090
```

### 7. Monitoring and Troubleshooting

#### Check Pod Status
```bash
kubectl get pods -n arka-ecommerce
kubectl get pods -n arka-monitoring
```

#### View Logs
```bash
kubectl logs -f deployment/api-gateway -n arka-ecommerce
kubectl logs -f deployment/eureka-server -n arka-ecommerce
```

#### Check Services
```bash
kubectl get svc -n arka-ecommerce
kubectl get ingress -n arka-ecommerce
```

#### Resource Usage
```bash
kubectl top pods -n arka-ecommerce
kubectl top nodes
```

### 8. Scaling Services
```bash
# Scale API Gateway
kubectl scale deployment api-gateway --replicas=3 -n arka-ecommerce

# Scale E-commerce Core
kubectl scale deployment ecommerce-core --replicas=3 -n arka-ecommerce
```

### 9. Backup and Restore

#### Backup Configuration
```bash
# Export all configurations
kubectl get all,pvc,secrets,configmaps -n arka-ecommerce -o yaml > arka-backup.yaml
kubectl get all,pvc,secrets,configmaps -n arka-monitoring -o yaml > monitoring-backup.yaml
```

#### Restore from Backup
```bash
kubectl apply -f arka-backup.yaml
kubectl apply -f monitoring-backup.yaml
```

### 10. Uninstall
```bash
# Remove ARKA platform
./k8s/undeploy-k8s.sh

# Uninstall k3s completely
/usr/local/bin/k3s-uninstall.sh
```

## 🔧 k3s Specific Configurations

### Storage Class
k3s comes with `local-path` storage class by default:
```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: local-path
provisioner: rancher.io/local-path
volumeBindingMode: WaitForFirstConsumer
reclaimPolicy: Delete
```

### Network Policy
k3s supports network policies with Calico:
```bash
# Install Calico for network policies
kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml
```

### Load Balancer
For local development, use MetalLB:
```bash
# Install MetalLB
kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.13.7/config/manifests/metallb-native.yaml

# Configure IP pool
cat <<EOF | kubectl apply -f -
apiVersion: metallb.io/v1beta1
kind: IPAddressPool
metadata:
  name: default-pool
  namespace: metallb-system
spec:
  addresses:
  - 192.168.1.240-192.168.1.250
---
apiVersion: metallb.io/v1beta1
kind: L2Advertisement
metadata:
  name: default
  namespace: metallb-system
spec:
  ipAddressPools:
  - default-pool
EOF
```

## 🎯 Performance Tuning for k3s

### System Limits
```bash
# Increase file limits
echo "fs.file-max = 2097152" >> /etc/sysctl.conf
echo "* soft nofile 65536" >> /etc/security/limits.conf
echo "* hard nofile 65536" >> /etc/security/limits.conf
```

### k3s Configuration
Create `/etc/rancher/k3s/config.yaml`:
```yaml
write-kubeconfig-mode: "0644"
tls-san:
  - "arka.local"
  - "api.arka.local"
disable:
  - traefik
  - servicelb
node-label:
  - "workload=arka-platform"
kube-controller-manager-arg:
  - "bind-address=0.0.0.0"
kube-proxy-arg:
  - "metrics-bind-address=0.0.0.0"
kube-scheduler-arg:
  - "bind-address=0.0.0.0"
```

### Resource Limits
For development environment:
```yaml
resources:
  limits:
    memory: "1Gi"
    cpu: "500m"
  requests:
    memory: "512Mi"
    cpu: "250m"
```

## 🔍 Useful Commands

### Cluster Information
```bash
# Cluster info
kubectl cluster-info
kubectl version

# Node information
kubectl describe node

# k3s status
sudo systemctl status k3s
```

### Debugging
```bash
# Check k3s logs
sudo journalctl -u k3s -f

# Check containerd
sudo ctr containers list
sudo ctr images list
```

### Cleanup
```bash
# Clean unused images
sudo ctr images prune

# Clean unused containers
sudo ctr containers list | grep -v RUNNING | awk '{print $1}' | xargs sudo ctr containers rm
```
