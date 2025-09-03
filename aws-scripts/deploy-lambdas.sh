#!/bin/bash
# deploy-lambdas.sh

echo "🚀 Desplegando Lambda Functions..."

# Cargar configuración
source aws-config.txt

# Compilar el proyecto
echo "📦 Compilando proyecto..."
cd ..
./gradlew clean build -x test
cd aws-scripts

# Crear directorio para deployment
echo "📁 Preparando deployment packages..."
mkdir -p lambda-packages
cd lambda-packages

# Copiar JAR principal
cp ../../build/libs/*.jar ./app.jar

# Crear package para Inventory Lambda
echo "📦 Creando package para Inventory Lambda..."
mkdir inventory-lambda
cp app.jar inventory-lambda/
cd inventory-lambda
zip -r ../inventory-lambda.zip .
cd ..

# Crear package para Shipping Lambda
echo "📦 Creando package para Shipping Lambda..."
mkdir shipping-lambda
cp app.jar shipping-lambda/
cd shipping-lambda
zip -r ../shipping-lambda.zip .
cd ..

# Crear package para Notification Lambda
echo "📦 Creando package para Notification Lambda..."
mkdir notification-lambda
cp app.jar notification-lambda/
cd notification-lambda
zip -r ../notification-lambda.zip .
cd ..

# Desplegar Inventory Lambda
echo "🚀 Desplegando Inventory Lambda..."
aws lambda create-function \
  --function-name arka-saga-inventory \
  --runtime java21 \
  --role $LAMBDA_ROLE_ARN \
  --handler com.arka.arkavalenzuela.aws.lambda.InventoryReservationLambda::handleRequest \
  --zip-file fileb://inventory-lambda.zip \
  --timeout 30 \
  --memory-size 512 \
  --region us-east-1 \
  --environment Variables="{AWS_REGION=us-east-1,SNS_TOPIC_ARN=$SAGA_TOPIC_ARN}" \
  --description "Lambda function for inventory reservation in Saga pattern"

# Desplegar Shipping Lambda
echo "🚀 Desplegando Shipping Lambda..."
aws lambda create-function \
  --function-name arka-saga-shipping \
  --runtime java21 \
  --role $LAMBDA_ROLE_ARN \
  --handler com.arka.arkavalenzuela.aws.lambda.ShippingGenerationLambda::handleRequest \
  --zip-file fileb://shipping-lambda.zip \
  --timeout 30 \
  --memory-size 512 \
  --region us-east-1 \
  --environment Variables="{AWS_REGION=us-east-1,SNS_TOPIC_ARN=$SAGA_TOPIC_ARN}" \
  --description "Lambda function for shipping generation in Saga pattern"

# Desplegar Notification Lambda
echo "🚀 Desplegando Notification Lambda..."
aws lambda create-function \
  --function-name arka-saga-notification \
  --runtime java21 \
  --role $LAMBDA_ROLE_ARN \
  --handler com.arka.arkavalenzuela.aws.lambda.NotificationLambda::handleRequest \
  --zip-file fileb://notification-lambda.zip \
  --timeout 30 \
  --memory-size 512 \
  --region us-east-1 \
  --environment Variables="{AWS_REGION=us-east-1,SNS_TOPIC_ARN=$SAGA_TOPIC_ARN}" \
  --description "Lambda function for notifications in Saga pattern"

# Obtener ARNs de las funciones
INVENTORY_LAMBDA_ARN=$(aws lambda get-function --function-name arka-saga-inventory --region us-east-1 --output text --query 'Configuration.FunctionArn')
SHIPPING_LAMBDA_ARN=$(aws lambda get-function --function-name arka-saga-shipping --region us-east-1 --output text --query 'Configuration.FunctionArn')
NOTIFICATION_LAMBDA_ARN=$(aws lambda get-function --function-name arka-saga-notification --region us-east-1 --output text --query 'Configuration.FunctionArn')

echo "✅ Inventory Lambda ARN: $INVENTORY_LAMBDA_ARN"
echo "✅ Shipping Lambda ARN: $SHIPPING_LAMBDA_ARN"
echo "✅ Notification Lambda ARN: $NOTIFICATION_LAMBDA_ARN"

# Agregar a configuración
cat >> ../aws-config.txt << EOF
INVENTORY_LAMBDA_ARN=$INVENTORY_LAMBDA_ARN
SHIPPING_LAMBDA_ARN=$SHIPPING_LAMBDA_ARN
NOTIFICATION_LAMBDA_ARN=$NOTIFICATION_LAMBDA_ARN
EOF

# Limpiar
cd ..
rm -rf lambda-packages

echo "📄 Configuración actualizada en aws-config.txt"
echo "🚀 Lambda Functions desplegadas exitosamente"
