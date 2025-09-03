#!/bin/bash
# deploy-complete.sh

echo "🌟 DESPLIEGUE COMPLETO DEL PATRÓN SAGA A AWS"
echo "=============================================="
echo ""

# Verificar AWS CLI
if ! command -v aws &> /dev/null; then
    echo "❌ AWS CLI no está instalado"
    echo "💡 Instalar desde: https://aws.amazon.com/cli/"
    exit 1
fi

# Verificar credenciales AWS
echo "🔍 Verificando credenciales AWS..."
if ! aws sts get-caller-identity &> /dev/null; then
    echo "❌ Credenciales AWS no configuradas"
    echo "💡 Ejecutar: aws configure"
    exit 1
fi

ACCOUNT_ID=$(aws sts get-caller-identity --output text --query 'Account')
echo "✅ Cuenta AWS: $ACCOUNT_ID"
echo ""

# Paso 1: Crear SNS Topics
echo "📍 PASO 1: Creando SNS Topics..."
./create-sns-topics.sh
if [ $? -ne 0 ]; then
    echo "❌ Error creando SNS Topics"
    exit 1
fi
echo ""

# Paso 2: Crear SQS Queues
echo "📍 PASO 2: Creando SQS Queues..."
./create-sqs-queues.sh
if [ $? -ne 0 ]; then
    echo "❌ Error creando SQS Queues"
    exit 1
fi
echo ""

# Paso 3: Crear IAM Roles
echo "📍 PASO 3: Creando IAM Roles..."
./create-iam-roles.sh
if [ $? -ne 0 ]; then
    echo "❌ Error creando IAM Roles"
    exit 1
fi
echo ""

# Paso 4: Desplegar Lambda Functions
echo "📍 PASO 4: Desplegando Lambda Functions..."
./deploy-lambdas.sh
if [ $? -ne 0 ]; then
    echo "❌ Error desplegando Lambda Functions"
    exit 1
fi
echo ""

# Paso 5: Configurar Triggers
echo "📍 PASO 5: Configurando Triggers..."
./configure-triggers.sh
if [ $? -ne 0 ]; then
    echo "❌ Error configurando Triggers"
    exit 1
fi
echo ""

# Generar resumen de configuración
echo "📊 RESUMEN DE CONFIGURACIÓN"
echo "============================"
echo ""

# Cargar configuración final
source aws-config.txt

echo "🔔 SNS Topics:"
echo "  - Saga Events: $SAGA_TOPIC_ARN"
echo ""

echo "📨 SQS Queues:"
echo "  - Inventory: $INVENTORY_QUEUE_URL"
echo "  - Shipping: $SHIPPING_QUEUE_URL"
echo "  - Notification: $NOTIFICATION_QUEUE_URL"
echo "  - Dead Letter: $DLQ_URL"
echo ""

echo "🚀 Lambda Functions:"
echo "  - Inventory: arka-saga-inventory"
echo "  - Shipping: arka-saga-shipping"
echo "  - Notification: arka-saga-notification"
echo ""

echo "🔐 IAM Role:"
echo "  - Role: ArkaLambdaExecutionRole"
echo "  - ARN: $LAMBDA_ROLE_ARN"
echo ""

# Generar archivo de variables de entorno
echo "📝 Generando variables de entorno..."
cat > ../aws-environment.env << EOF
# Variables de entorno para aplicación Spring Boot
export AWS_REGION=us-east-1
export AWS_SNS_SAGA_TOPIC=$SAGA_TOPIC_ARN
export AWS_LAMBDA_INVENTORY=arka-saga-inventory
export AWS_LAMBDA_SHIPPING=arka-saga-shipping
export AWS_LAMBDA_NOTIFICATION=arka-saga-notification
export AWS_SQS_INVENTORY_QUEUE=$INVENTORY_QUEUE_URL
export AWS_SQS_SHIPPING_QUEUE=$SHIPPING_QUEUE_URL
export AWS_SQS_NOTIFICATION_QUEUE=$NOTIFICATION_QUEUE_URL
export AWS_ACCOUNT_ID=$ACCOUNT_ID

# Para usar en aplicación:
# source aws-environment.env
EOF

# Actualizar application-aws.properties
echo "⚙️ Actualizando configuración de aplicación..."
cat >> ../src/main/resources/application-aws.properties << EOF

# Configuración AWS actualizada desde despliegue
aws.mock.enabled=false
aws.sns.saga-topic-arn=$SAGA_TOPIC_ARN
aws.lambda.inventory-function=arka-saga-inventory
aws.lambda.shipping-function=arka-saga-shipping
aws.lambda.notification-function=arka-saga-notification
EOF

echo ""
echo "🎉 DESPLIEGUE COMPLETADO EXITOSAMENTE"
echo "====================================="
echo ""
echo "📋 PRÓXIMOS PASOS:"
echo "1. Cargar variables de entorno: source aws-environment.env"
echo "2. Actualizar application-aws.properties con endpoints de BD"
echo "3. Ejecutar aplicación: ./gradlew bootRun --args=\"--spring.profiles.active=aws\""
echo "4. Probar endpoints de Saga"
echo ""
echo "📊 MONITOREO:"
echo "- CloudWatch Logs: https://console.aws.amazon.com/cloudwatch/home?region=us-east-1#logsV2:log-groups"
echo "- Lambda Functions: https://console.aws.amazon.com/lambda/home?region=us-east-1#/functions"
echo "- SNS Topics: https://console.aws.amazon.com/sns/v3/home?region=us-east-1#/topics"
echo "- SQS Queues: https://console.aws.amazon.com/sqs/v2/home?region=us-east-1#/queues"
echo ""
echo "💰 COSTO ESTIMADO: $87-145 USD/mes"
echo ""
