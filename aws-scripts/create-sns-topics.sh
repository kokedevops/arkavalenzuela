#!/bin/bash
# create-sns-topics.sh

echo "🔔 Creando SNS Topics..."

# Topic principal para eventos Saga
echo "Creando topic principal..."
SAGA_TOPIC_ARN=$(aws sns create-topic --name arka-saga-events --region us-east-1 --output text --query 'TopicArn')
echo "✅ Saga Topic ARN: $SAGA_TOPIC_ARN"

# Topics específicos por servicio
echo "Creando topics por servicio..."
INVENTORY_TOPIC_ARN=$(aws sns create-topic --name arka-inventory-events --region us-east-1 --output text --query 'TopicArn')
SHIPPING_TOPIC_ARN=$(aws sns create-topic --name arka-shipping-events --region us-east-1 --output text --query 'TopicArn')
NOTIFICATION_TOPIC_ARN=$(aws sns create-topic --name arka-notification-events --region us-east-1 --output text --query 'TopicArn')

echo "✅ Inventory Topic ARN: $INVENTORY_TOPIC_ARN"
echo "✅ Shipping Topic ARN: $SHIPPING_TOPIC_ARN"
echo "✅ Notification Topic ARN: $NOTIFICATION_TOPIC_ARN"

# Guardar ARNs en archivo de configuración
cat > aws-config.txt << EOF
SAGA_TOPIC_ARN=$SAGA_TOPIC_ARN
INVENTORY_TOPIC_ARN=$INVENTORY_TOPIC_ARN
SHIPPING_TOPIC_ARN=$SHIPPING_TOPIC_ARN
NOTIFICATION_TOPIC_ARN=$NOTIFICATION_TOPIC_ARN
EOF

echo "📄 Configuración guardada en aws-config.txt"
echo "🔔 SNS Topics creados exitosamente"
