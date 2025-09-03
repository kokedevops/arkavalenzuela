#!/bin/bash
# create-sqs-queues.sh

echo "📨 Creando SQS Queues..."

# Cola DLQ (Dead Letter Queue) primero
echo "Creando Dead Letter Queue..."
DLQ_URL=$(aws sqs create-queue --queue-name arka-saga-dlq --region us-east-1 --output text --query 'QueueUrl')
DLQ_ARN=$(aws sqs get-queue-attributes --queue-url $DLQ_URL --attribute-names QueueArn --region us-east-1 --output text --query 'Attributes.QueueArn')
echo "✅ DLQ URL: $DLQ_URL"
echo "✅ DLQ ARN: $DLQ_ARN"

# Configuración de redrive policy
REDRIVE_POLICY="{\"deadLetterTargetArn\":\"$DLQ_ARN\",\"maxReceiveCount\":3}"

# Cola para inventario
echo "Creando cola de inventario..."
INVENTORY_QUEUE_URL=$(aws sqs create-queue \
  --queue-name arka-inventory-queue \
  --attributes "RedrivePolicy=$REDRIVE_POLICY,VisibilityTimeoutSeconds=300" \
  --region us-east-1 \
  --output text --query 'QueueUrl')
INVENTORY_QUEUE_ARN=$(aws sqs get-queue-attributes --queue-url $INVENTORY_QUEUE_URL --attribute-names QueueArn --region us-east-1 --output text --query 'Attributes.QueueArn')

# Cola para envíos
echo "Creando cola de envíos..."
SHIPPING_QUEUE_URL=$(aws sqs create-queue \
  --queue-name arka-shipping-queue \
  --attributes "RedrivePolicy=$REDRIVE_POLICY,VisibilityTimeoutSeconds=300" \
  --region us-east-1 \
  --output text --query 'QueueUrl')
SHIPPING_QUEUE_ARN=$(aws sqs get-queue-attributes --queue-url $SHIPPING_QUEUE_URL --attribute-names QueueArn --region us-east-1 --output text --query 'Attributes.QueueArn')

# Cola para notificaciones
echo "Creando cola de notificaciones..."
NOTIFICATION_QUEUE_URL=$(aws sqs create-queue \
  --queue-name arka-notification-queue \
  --attributes "RedrivePolicy=$REDRIVE_POLICY,VisibilityTimeoutSeconds=300" \
  --region us-east-1 \
  --output text --query 'QueueUrl')
NOTIFICATION_QUEUE_ARN=$(aws sqs get-queue-attributes --queue-url $NOTIFICATION_QUEUE_URL --attribute-names QueueArn --region us-east-1 --output text --query 'Attributes.QueueArn')

echo "✅ Inventory Queue URL: $INVENTORY_QUEUE_URL"
echo "✅ Shipping Queue URL: $SHIPPING_QUEUE_URL"
echo "✅ Notification Queue URL: $NOTIFICATION_QUEUE_URL"

# Agregar a archivo de configuración
cat >> aws-config.txt << EOF
DLQ_URL=$DLQ_URL
DLQ_ARN=$DLQ_ARN
INVENTORY_QUEUE_URL=$INVENTORY_QUEUE_URL
INVENTORY_QUEUE_ARN=$INVENTORY_QUEUE_ARN
SHIPPING_QUEUE_URL=$SHIPPING_QUEUE_URL
SHIPPING_QUEUE_ARN=$SHIPPING_QUEUE_ARN
NOTIFICATION_QUEUE_URL=$NOTIFICATION_QUEUE_URL
NOTIFICATION_QUEUE_ARN=$NOTIFICATION_QUEUE_ARN
EOF

echo "📄 Configuración actualizada en aws-config.txt"
echo "📨 SQS Queues creadas exitosamente"
