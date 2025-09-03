#!/bin/bash

# Script de despliegue para el patrón Saga en AWS
# Autor: Sistema Arka
# Versión: 1.0

set -e

echo "=== Iniciando despliegue del patrón Saga en AWS ==="

# Variables de configuración
AWS_REGION=${AWS_REGION:-us-east-1}
STACK_NAME="arka-saga-stack"
S3_BUCKET="arka-lambda-deployments"
SNS_TOPIC_NAME="arka-saga-topic"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar AWS CLI
check_aws_cli() {
    if ! command -v aws &> /dev/null; then
        log_error "AWS CLI no está instalado"
        exit 1
    fi
    
    if ! aws sts get-caller-identity &> /dev/null; then
        log_error "AWS CLI no está configurado correctamente"
        exit 1
    fi
    
    log_info "AWS CLI configurado correctamente"
}

# Crear bucket S3 para lambdas
create_s3_bucket() {
    log_info "Creando bucket S3 para deployments..."
    
    if aws s3 ls "s3://$S3_BUCKET" 2>&1 | grep -q 'NoSuchBucket'; then
        aws s3 mb "s3://$S3_BUCKET" --region $AWS_REGION
        log_info "Bucket S3 creado: $S3_BUCKET"
    else
        log_warn "Bucket S3 ya existe: $S3_BUCKET"
    fi
}

# Crear topic SNS
create_sns_topic() {
    log_info "Creando topic SNS..."
    
    SNS_TOPIC_ARN=$(aws sns create-topic \
        --name $SNS_TOPIC_NAME \
        --region $AWS_REGION \
        --query 'TopicArn' \
        --output text)
    
    log_info "Topic SNS creado: $SNS_TOPIC_ARN"
    echo $SNS_TOPIC_ARN > sns-topic-arn.txt
}

# Empaquetar Lambdas
package_lambdas() {
    log_info "Empaquetando funciones Lambda..."
    
    # Crear directorio temporal
    mkdir -p lambda-packages
    
    # Lambda de Inventario
    log_info "Empaquetando Lambda de Inventario..."
    cd lambda-functions/inventory-lambda
    zip -r ../../lambda-packages/inventory-lambda.zip .
    cd ../..
    
    # Lambda de Envío
    log_info "Empaquetando Lambda de Envío..."
    cd lambda-functions/shipping-lambda
    zip -r ../../lambda-packages/shipping-lambda.zip .
    cd ../..
    
    # Lambda de Notificación
    log_info "Empaquetando Lambda de Notificación..."
    cd lambda-functions/notification-lambda
    zip -r ../../lambda-packages/notification-lambda.zip .
    cd ../..
    
    log_info "Lambdas empaquetadas exitosamente"
}

# Subir Lambdas a S3
upload_lambdas() {
    log_info "Subiendo Lambdas a S3..."
    
    aws s3 cp lambda-packages/inventory-lambda.zip "s3://$S3_BUCKET/"
    aws s3 cp lambda-packages/shipping-lambda.zip "s3://$S3_BUCKET/"
    aws s3 cp lambda-packages/notification-lambda.zip "s3://$S3_BUCKET/"
    
    log_info "Lambdas subidas a S3"
}

# Crear IAM Role para Lambdas
create_lambda_role() {
    log_info "Creando IAM Role para Lambdas..."
    
    cat > lambda-trust-policy.json << 'EOF'
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
EOF

    cat > lambda-policy.json << 'EOF'
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "sns:Publish",
        "sns:Subscribe",
        "sns:Unsubscribe"
      ],
      "Resource": "*"
    }
  ]
}
EOF

    # Crear role
    aws iam create-role \
        --role-name arka-saga-lambda-role \
        --assume-role-policy-document file://lambda-trust-policy.json \
        --region $AWS_REGION || log_warn "Role ya existe"
    
    # Adjuntar políticas
    aws iam put-role-policy \
        --role-name arka-saga-lambda-role \
        --policy-name arka-saga-lambda-policy \
        --policy-document file://lambda-policy.json
    
    # Adjuntar política managed para ejecución básica
    aws iam attach-role-policy \
        --role-name arka-saga-lambda-role \
        --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
    
    log_info "IAM Role creado"
    
    # Limpiar archivos temporales
    rm lambda-trust-policy.json lambda-policy.json
}

# Desplegar Lambda de Inventario
deploy_inventory_lambda() {
    log_info "Desplegando Lambda de Inventario..."
    
    ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
    LAMBDA_ROLE_ARN="arn:aws:iam::$ACCOUNT_ID:role/arka-saga-lambda-role"
    SNS_TOPIC_ARN=$(cat sns-topic-arn.txt)
    
    aws lambda create-function \
        --function-name arka-saga-inventory \
        --runtime java21 \
        --role $LAMBDA_ROLE_ARN \
        --handler com.arka.lambda.inventory.InventoryReservationLambda::handleRequest \
        --code S3Bucket=$S3_BUCKET,S3Key=inventory-lambda.zip \
        --timeout 30 \
        --memory-size 512 \
        --environment Variables="{SNS_TOPIC_ARN=$SNS_TOPIC_ARN,S2_SERVICE_URL=https://api-s2.com}" \
        --region $AWS_REGION || log_warn "Lambda de Inventario ya existe"
    
    log_info "Lambda de Inventario desplegada"
}

# Desplegar Lambda de Envío
deploy_shipping_lambda() {
    log_info "Desplegando Lambda de Envío..."
    
    ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
    LAMBDA_ROLE_ARN="arn:aws:iam::$ACCOUNT_ID:role/arka-saga-lambda-role"
    SNS_TOPIC_ARN=$(cat sns-topic-arn.txt)
    
    aws lambda create-function \
        --function-name arka-saga-shipping \
        --runtime java21 \
        --role $LAMBDA_ROLE_ARN \
        --handler com.arka.lambda.shipping.ShippingGenerationLambda::handleRequest \
        --code S3Bucket=$S3_BUCKET,S3Key=shipping-lambda.zip \
        --timeout 30 \
        --memory-size 512 \
        --environment Variables="{SNS_TOPIC_ARN=$SNS_TOPIC_ARN,CHIPMEN_SERVICE_URL=https://chipmen.com}" \
        --region $AWS_REGION || log_warn "Lambda de Envío ya existe"
    
    # Dar permisos a SNS para invocar la Lambda
    aws lambda add-permission \
        --function-name arka-saga-shipping \
        --statement-id sns-invoke \
        --action lambda:InvokeFunction \
        --principal sns.amazonaws.com \
        --source-arn $SNS_TOPIC_ARN \
        --region $AWS_REGION || log_warn "Permisos ya existen"
    
    log_info "Lambda de Envío desplegada"
}

# Desplegar Lambda de Notificación
deploy_notification_lambda() {
    log_info "Desplegando Lambda de Notificación..."
    
    ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
    LAMBDA_ROLE_ARN="arn:aws:iam::$ACCOUNT_ID:role/arka-saga-lambda-role"
    SNS_TOPIC_ARN=$(cat sns-topic-arn.txt)
    
    aws lambda create-function \
        --function-name arka-saga-notification \
        --runtime java21 \
        --role $LAMBDA_ROLE_ARN \
        --handler com.arka.lambda.notification.NotificationLambda::handleRequest \
        --code S3Bucket=$S3_BUCKET,S3Key=notification-lambda.zip \
        --timeout 30 \
        --memory-size 512 \
        --environment Variables="{SNS_TOPIC_ARN=$SNS_TOPIC_ARN,NOTIFICATION_SERVICE_URL=}" \
        --region $AWS_REGION || log_warn "Lambda de Notificación ya existe"
    
    # Dar permisos a SNS para invocar la Lambda
    aws lambda add-permission \
        --function-name arka-saga-notification \
        --statement-id sns-invoke \
        --action lambda:InvokeFunction \
        --principal sns.amazonaws.com \
        --source-arn $SNS_TOPIC_ARN \
        --region $AWS_REGION || log_warn "Permisos ya existen"
    
    log_info "Lambda de Notificación desplegada"
}

# Configurar suscripciones SNS
configure_sns_subscriptions() {
    log_info "Configurando suscripciones SNS..."
    
    ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
    SNS_TOPIC_ARN=$(cat sns-topic-arn.txt)
    
    # Suscribir Lambda de Envío
    aws sns subscribe \
        --topic-arn $SNS_TOPIC_ARN \
        --protocol lambda \
        --notification-endpoint "arn:aws:lambda:$AWS_REGION:$ACCOUNT_ID:function:arka-saga-shipping" \
        --region $AWS_REGION
    
    # Suscribir Lambda de Notificación
    aws sns subscribe \
        --topic-arn $SNS_TOPIC_ARN \
        --protocol lambda \
        --notification-endpoint "arn:aws:lambda:$AWS_REGION:$ACCOUNT_ID:function:arka-saga-notification" \
        --region $AWS_REGION
    
    log_info "Suscripciones SNS configuradas"
}

# Limpiar archivos temporales
cleanup() {
    log_info "Limpiando archivos temporales..."
    rm -rf lambda-packages
    rm -f sns-topic-arn.txt
}

# Mostrar información final
show_deployment_info() {
    log_info "=== Despliegue completado exitosamente ==="
    echo ""
    echo "Recursos creados:"
    echo "- Topic SNS: $(cat sns-topic-arn.txt 2>/dev/null || echo 'Error al leer ARN')"
    echo "- Lambda Inventario: arka-saga-inventory"
    echo "- Lambda Envío: arka-saga-shipping"
    echo "- Lambda Notificación: arka-saga-notification"
    echo "- Bucket S3: $S3_BUCKET"
    echo ""
    echo "Próximos pasos:"
    echo "1. Configurar las variables de entorno en application.properties"
    echo "2. Actualizar URLs de servicios externos"
    echo "3. Probar el endpoint: POST /api/saga/start"
    echo ""
}

# Función principal
main() {
    log_info "Iniciando despliegue en región: $AWS_REGION"
    
    check_aws_cli
    create_s3_bucket
    create_sns_topic
    package_lambdas
    upload_lambdas
    create_lambda_role
    
    # Esperar a que el role se propague
    log_info "Esperando propagación del IAM Role..."
    sleep 10
    
    deploy_inventory_lambda
    deploy_shipping_lambda
    deploy_notification_lambda
    configure_sns_subscriptions
    
    cleanup
    show_deployment_info
}

# Ejecutar función principal
main "$@"
