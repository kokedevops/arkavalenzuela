#!/bin/bash
# configure-triggers.sh

echo "🔗 Configurando triggers SNS → SQS → Lambda..."

# Cargar configuración
source aws-config.txt

# 1. Configurar SNS → SQS subscriptions
echo "📧 Configurando subscripciones SNS → SQS..."

# Permitir que SNS publique en SQS queues
echo "🔐 Configurando permisos SQS..."

# Policy para permitir SNS → SQS
SQS_POLICY=$(cat <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "sns.amazonaws.com"
      },
      "Action": "sqs:SendMessage",
      "Resource": "*",
      "Condition": {
        "ArnEquals": {
          "aws:SourceArn": "$SAGA_TOPIC_ARN"
        }
      }
    }
  ]
}
EOF
)

# Aplicar política a las colas
aws sqs set-queue-attributes \
  --queue-url $INVENTORY_QUEUE_URL \
  --attributes "Policy=$SQS_POLICY"

aws sqs set-queue-attributes \
  --queue-url $SHIPPING_QUEUE_URL \
  --attributes "Policy=$SQS_POLICY"

aws sqs set-queue-attributes \
  --queue-url $NOTIFICATION_QUEUE_URL \
  --attributes "Policy=$SQS_POLICY"

# 2. Crear subscripciones SNS → SQS
echo "📨 Creando subscripciones..."

# Subscription para inventory
aws sns subscribe \
  --topic-arn $SAGA_TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $INVENTORY_QUEUE_ARN \
  --attributes "FilterPolicy={\"eventType\":[\"inventory-request\"]}"

# Subscription para shipping
aws sns subscribe \
  --topic-arn $SAGA_TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $SHIPPING_QUEUE_ARN \
  --attributes "FilterPolicy={\"eventType\":[\"shipping-request\"]}"

# Subscription para notification
aws sns subscribe \
  --topic-arn $SAGA_TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $NOTIFICATION_QUEUE_ARN \
  --attributes "FilterPolicy={\"eventType\":[\"notification-request\"]}"

# 3. Configurar SQS → Lambda triggers
echo "⚡ Configurando triggers SQS → Lambda..."

# Dar permisos a Lambda para leer de SQS
aws lambda add-permission \
  --function-name arka-saga-inventory \
  --statement-id sqs-trigger-inventory \
  --action lambda:InvokeFunction \
  --principal sqs.amazonaws.com \
  --source-arn $INVENTORY_QUEUE_ARN

aws lambda add-permission \
  --function-name arka-saga-shipping \
  --statement-id sqs-trigger-shipping \
  --action lambda:InvokeFunction \
  --principal sqs.amazonaws.com \
  --source-arn $SHIPPING_QUEUE_ARN

aws lambda add-permission \
  --function-name arka-saga-notification \
  --statement-id sqs-trigger-notification \
  --action lambda:InvokeFunction \
  --principal sqs.amazonaws.com \
  --source-arn $NOTIFICATION_QUEUE_ARN

# Crear event source mappings
echo "🔄 Creando event source mappings..."

INVENTORY_MAPPING_UUID=$(aws lambda create-event-source-mapping \
  --function-name arka-saga-inventory \
  --event-source-arn $INVENTORY_QUEUE_ARN \
  --batch-size 10 \
  --maximum-batching-window-in-seconds 5 \
  --region us-east-1 \
  --output text --query 'UUID')

SHIPPING_MAPPING_UUID=$(aws lambda create-event-source-mapping \
  --function-name arka-saga-shipping \
  --event-source-arn $SHIPPING_QUEUE_ARN \
  --batch-size 10 \
  --maximum-batching-window-in-seconds 5 \
  --region us-east-1 \
  --output text --query 'UUID')

NOTIFICATION_MAPPING_UUID=$(aws lambda create-event-source-mapping \
  --function-name arka-saga-notification \
  --event-source-arn $NOTIFICATION_QUEUE_ARN \
  --batch-size 10 \
  --maximum-batching-window-in-seconds 5 \
  --region us-east-1 \
  --output text --query 'UUID')

echo "✅ Inventory Mapping UUID: $INVENTORY_MAPPING_UUID"
echo "✅ Shipping Mapping UUID: $SHIPPING_MAPPING_UUID"
echo "✅ Notification Mapping UUID: $NOTIFICATION_MAPPING_UUID"

# Agregar a configuración
cat >> aws-config.txt << EOF
INVENTORY_MAPPING_UUID=$INVENTORY_MAPPING_UUID
SHIPPING_MAPPING_UUID=$SHIPPING_MAPPING_UUID
NOTIFICATION_MAPPING_UUID=$NOTIFICATION_MAPPING_UUID
EOF

echo "📄 Configuración actualizada en aws-config.txt"
echo "🔗 Triggers configurados exitosamente"

# 4. Verificar configuración
echo "✅ Verificando configuración..."
aws sns list-subscriptions-by-topic --topic-arn $SAGA_TOPIC_ARN
aws lambda list-event-source-mappings --function-name arka-saga-inventory

echo "🎉 Configuración de triggers completada"
