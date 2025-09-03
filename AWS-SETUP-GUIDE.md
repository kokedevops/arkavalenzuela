# 🌐 CONFIGURACIÓN COMPLETA EN AWS PARA PATRÓN SAGA

## 📋 SERVICIOS AWS REQUERIDOS

### 🔧 SERVICIOS PRINCIPALES
- **AWS Lambda** - Funciones para cada paso del Saga
- **Amazon SNS** - Notificaciones y eventos
- **Amazon SQS** - Colas de mensajes
- **AWS IAM** - Roles y permisos
- **Amazon CloudWatch** - Logs y monitoreo
- **AWS API Gateway** - Exposición de APIs (opcional)

### 🗄️ SERVICIOS DE DATOS
- **Amazon RDS** - Base de datos relacional
- **Amazon DocumentDB** - Base de datos de documentos
- **Amazon S3** - Almacenamiento de archivos

## 🚀 PASO A PASO - CONFIGURACIÓN AWS

### PASO 1: Configurar AWS CLI
```bash
# Instalar AWS CLI v2
# Descargar desde: https://aws.amazon.com/cli/

# Configurar credenciales
aws configure
```

**Datos requeridos:**
- AWS Access Key ID
- AWS Secret Access Key  
- Default region: `us-east-1`
- Default output format: `json`

### PASO 2: Crear SNS Topics

```bash
# Topic principal para eventos Saga
aws sns create-topic --name arka-saga-events --region us-east-1

# Topics específicos por servicio
aws sns create-topic --name arka-inventory-events --region us-east-1
aws sns create-topic --name arka-shipping-events --region us-east-1
aws sns create-topic --name arka-notification-events --region us-east-1
```

**Obtener ARNs:**
```bash
aws sns list-topics --region us-east-1
```

### PASO 3: Crear SQS Queues

```bash
# Cola para inventario
aws sqs create-queue --queue-name arka-inventory-queue --region us-east-1

# Cola para envíos
aws sqs create-queue --queue-name arka-shipping-queue --region us-east-1

# Cola para notificaciones
aws sqs create-queue --queue-name arka-notification-queue --region us-east-1

# Cola DLQ (Dead Letter Queue)
aws sqs create-queue --queue-name arka-saga-dlq --region us-east-1
```

### PASO 4: Configurar subscripciones SNS → SQS

```bash
# Obtener Queue URLs
aws sqs get-queue-url --queue-name arka-inventory-queue
aws sqs get-queue-url --queue-name arka-shipping-queue
aws sqs get-queue-url --queue-name arka-notification-queue

# Suscribir colas a topics
aws sns subscribe \
  --topic-arn arn:aws:sns:us-east-1:123456789012:arka-saga-events \
  --protocol sqs \
  --notification-endpoint arn:aws:sqs:us-east-1:123456789012:arka-inventory-queue
```

### PASO 5: Crear IAM Roles

**Role para Lambda:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

```bash
aws iam create-role \
  --role-name ArkaLambdaExecutionRole \
  --assume-role-policy-document file://lambda-trust-policy.json
```

**Políticas necesarias:**
```bash
# Política básica Lambda
aws iam attach-role-policy \
  --role-name ArkaLambdaExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# Política para SNS
aws iam attach-role-policy \
  --role-name ArkaLambdaExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/AmazonSNSFullAccess

# Política para SQS
aws iam attach-role-policy \
  --role-name ArkaLambdaExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/AmazonSQSFullAccess
```

### PASO 6: Compilar y empaquetar Lambda Functions

```bash
# Compilar el proyecto
cd c:\Users\valen\arkavalenzuela-1
.\gradlew clean build

# Crear directorio para Lambda
mkdir lambda-deployment
cd lambda-deployment

# Copiar JAR principal
copy ..\build\libs\arkajvalenzuela-0.0.1-SNAPSHOT.jar .\

# Crear estructura para cada Lambda
mkdir inventory-lambda
mkdir shipping-lambda  
mkdir notification-lambda
```

**Crear deployment packages:**
```bash
# Para cada Lambda, crear un ZIP con:
# 1. El JAR principal
# 2. Las dependencias
# 3. El handler específico

# Ejemplo para Inventory Lambda:
jar -cf inventory-lambda.zip \
  -C inventory-lambda . \
  arkajvalenzuela-0.0.1-SNAPSHOT.jar
```

### PASO 7: Desplegar Lambda Functions

**Inventory Lambda:**
```bash
aws lambda create-function \
  --function-name arka-saga-inventory \
  --runtime java21 \
  --role arn:aws:iam::123456789012:role/ArkaLambdaExecutionRole \
  --handler com.arka.arkavalenzuela.aws.lambda.InventoryReservationLambda::handleRequest \
  --zip-file fileb://inventory-lambda.zip \
  --timeout 30 \
  --memory-size 512 \
  --region us-east-1
```

**Shipping Lambda:**
```bash
aws lambda create-function \
  --function-name arka-saga-shipping \
  --runtime java21 \
  --role arn:aws:iam::123456789012:role/ArkaLambdaExecutionRole \
  --handler com.arka.arkavalenzuela.aws.lambda.ShippingGenerationLambda::handleRequest \
  --zip-file fileb://shipping-lambda.zip \
  --timeout 30 \
  --memory-size 512 \
  --region us-east-1
```

**Notification Lambda:**
```bash
aws lambda create-function \
  --function-name arka-saga-notification \
  --runtime java21 \
  --role arn:aws:iam::123456789012:role/ArkaLambdaExecutionRole \
  --handler com.arka.arkavalenzuela.aws.lambda.NotificationLambda::handleRequest \
  --zip-file fileb://notification-lambda.zip \
  --timeout 30 \
  --memory-size 512 \
  --region us-east-1
```

### PASO 8: Configurar triggers SQS → Lambda

```bash
# Inventory Lambda trigger
aws lambda create-event-source-mapping \
  --function-name arka-saga-inventory \
  --event-source-arn arn:aws:sqs:us-east-1:123456789012:arka-inventory-queue \
  --batch-size 10

# Shipping Lambda trigger  
aws lambda create-event-source-mapping \
  --function-name arka-saga-shipping \
  --event-source-arn arn:aws:sqs:us-east-1:123456789012:arka-shipping-queue \
  --batch-size 10

# Notification Lambda trigger
aws lambda create-event-source-mapping \
  --function-name arka-saga-notification \
  --event-source-arn arn:aws:sqs:us-east-1:123456789012:arka-notification-queue \
  --batch-size 10
```

### PASO 9: Configurar RDS (Base de datos)

```bash
# Crear subnet group
aws rds create-db-subnet-group \
  --db-subnet-group-name arka-subnet-group \
  --db-subnet-group-description "Subnet group for Arka database" \
  --subnet-ids subnet-12345678 subnet-87654321

# Crear instancia RDS MySQL
aws rds create-db-instance \
  --db-instance-identifier arka-database \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --master-username admin \
  --master-user-password "YourSecurePassword123!" \
  --allocated-storage 20 \
  --db-subnet-group-name arka-subnet-group \
  --vpc-security-group-ids sg-12345678 \
  --backup-retention-period 7 \
  --multi-az \
  --storage-encrypted
```

### PASO 10: Configurar DocumentDB (MongoDB compatible)

```bash
# Crear cluster DocumentDB
aws docdb create-db-cluster \
  --db-cluster-identifier arka-docdb-cluster \
  --engine docdb \
  --master-username admin \
  --master-user-password "YourSecurePassword123!" \
  --db-subnet-group-name arka-subnet-group \
  --vpc-security-group-ids sg-12345678 \
  --storage-encrypted

# Crear instancia DocumentDB
aws docdb create-db-instance \
  --db-instance-identifier arka-docdb-instance \
  --db-instance-class db.t3.medium \
  --engine docdb \
  --db-cluster-identifier arka-docdb-cluster
```

### PASO 11: Configurar S3 para almacenamiento

```bash
# Crear bucket S3
aws s3 mb s3://arka-saga-storage --region us-east-1

# Configurar versionado
aws s3api put-bucket-versioning \
  --bucket arka-saga-storage \
  --versioning-configuration Status=Enabled

# Configurar cifrado
aws s3api put-bucket-encryption \
  --bucket arka-saga-storage \
  --server-side-encryption-configuration '{
    "Rules": [
      {
        "ApplyServerSideEncryptionByDefault": {
          "SSEAlgorithm": "AES256"
        }
      }
    ]
  }'
```

### PASO 12: Configurar CloudWatch para monitoreo

```bash
# Crear dashboard
aws cloudwatch put-dashboard \
  --dashboard-name ArkaSagaDashboard \
  --dashboard-body file://dashboard-config.json

# Configurar alarmas
aws cloudwatch put-metric-alarm \
  --alarm-name "Arka-Lambda-Errors" \
  --alarm-description "Lambda function errors" \
  --metric-name Errors \
  --namespace AWS/Lambda \
  --statistic Sum \
  --period 300 \
  --threshold 5 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2
```

### PASO 13: Configurar API Gateway (opcional)

```bash
# Crear API REST
aws apigateway create-rest-api \
  --name "ArkaValenzuelaAPI" \
  --description "API for Arka Valenzuela Saga Pattern"

# Crear recursos y métodos
aws apigateway create-resource \
  --rest-api-id abc123 \
  --parent-id xyz789 \
  --path-part "saga"
```

## 🔐 CONFIGURACIÓN DE SEGURIDAD

### VPC y Security Groups

```bash
# Crear VPC
aws ec2 create-vpc --cidr-block 10.0.0.0/16

# Crear subnets
aws ec2 create-subnet --vpc-id vpc-12345678 --cidr-block 10.0.1.0/24
aws ec2 create-subnet --vpc-id vpc-12345678 --cidr-block 10.0.2.0/24

# Crear security group
aws ec2 create-security-group \
  --group-name arka-sg \
  --description "Security group for Arka services" \
  --vpc-id vpc-12345678
```

### IAM Políticas personalizadas

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "sns:Publish",
        "sqs:SendMessage",
        "sqs:ReceiveMessage",
        "sqs:DeleteMessage",
        "lambda:InvokeFunction",
        "rds:DescribeDBInstances",
        "docdb:DescribeDBClusters"
      ],
      "Resource": "*"
    }
  ]
}
```

## 📊 VARIABLES DE ENTORNO

### Para la aplicación Spring Boot:

```bash
export AWS_REGION=us-east-1
export AWS_SNS_SAGA_TOPIC=arn:aws:sns:us-east-1:123456789012:arka-saga-events
export AWS_LAMBDA_INVENTORY=arka-saga-inventory
export AWS_LAMBDA_SHIPPING=arka-saga-shipping
export AWS_LAMBDA_NOTIFICATION=arka-saga-notification
export RDS_ENDPOINT=arka-database.xyz.us-east-1.rds.amazonaws.com
export DOCDB_ENDPOINT=arka-docdb-cluster.cluster-xyz.us-east-1.docdb.amazonaws.com
export S3_BUCKET=arka-saga-storage
```

## 🔄 SCRIPT DE DESPLIEGUE COMPLETO

```bash
#!/bin/bash
# deploy-to-aws.sh

echo "🚀 Desplegando Patrón Saga a AWS..."

# 1. Crear recursos básicos
./scripts/create-sns-topics.sh
./scripts/create-sqs-queues.sh
./scripts/create-iam-roles.sh

# 2. Compilar y empaquetar
./gradlew clean build
./scripts/package-lambdas.sh

# 3. Desplegar Lambda functions
./scripts/deploy-lambdas.sh

# 4. Configurar triggers
./scripts/configure-triggers.sh

# 5. Crear bases de datos
./scripts/create-databases.sh

# 6. Configurar monitoreo
./scripts/setup-monitoring.sh

echo "✅ Despliegue completado"
```

## 💰 ESTIMACIÓN DE COSTOS

### Costos aproximados mensuales:

- **Lambda Functions**: $5-20 (según uso)
- **SNS/SQS**: $1-5 (según volumen)
- **RDS db.t3.micro**: $15-25
- **DocumentDB db.t3.medium**: $60-80
- **S3**: $1-5 (según almacenamiento)
- **CloudWatch**: $5-10

**Total estimado: $87-145/mes**

## 🚨 CONSIDERACIONES IMPORTANTES

### Límites de AWS:
- Lambda: 15 minutos máximo por ejecución
- SNS: 256KB máximo por mensaje
- SQS: 14 días máximo de retención

### Monitoreo requerido:
- Latencia de Lambda
- Errores en SQS/SNS
- Conexiones de BD
- Costos diarios

### Escalabilidad:
- Auto Scaling para RDS
- Reserved capacity para DocumentDB
- Lambda concurrency limits

## ✅ CHECKLIST DE VALIDACIÓN

- [ ] SNS Topics creados
- [ ] SQS Queues creadas
- [ ] Lambda Functions desplegadas
- [ ] IAM Roles configurados
- [ ] RDS instancia corriendo
- [ ] DocumentDB cluster activo
- [ ] S3 bucket configurado
- [ ] CloudWatch dashboard creado
- [ ] Security Groups configurados
- [ ] Variables de entorno establecidas
