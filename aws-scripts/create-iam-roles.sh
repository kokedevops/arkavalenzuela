#!/bin/bash
# create-iam-roles.sh

echo "🔐 Creando IAM Roles y Políticas..."

# Crear archivo de trust policy para Lambda
cat > lambda-trust-policy.json << EOF
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

# Crear role para Lambda
echo "Creando role de ejecución para Lambda..."
aws iam create-role \
  --role-name ArkaLambdaExecutionRole \
  --assume-role-policy-document file://lambda-trust-policy.json \
  --description "Role for Arka Saga Lambda functions"

# Crear política personalizada para Saga
cat > saga-policy.json << EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "sns:Publish",
        "sns:GetTopicAttributes",
        "sqs:SendMessage",
        "sqs:ReceiveMessage",
        "sqs:DeleteMessage",
        "sqs:GetQueueAttributes",
        "lambda:InvokeFunction",
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents",
        "rds:DescribeDBInstances",
        "rds:Connect"
      ],
      "Resource": "*"
    }
  ]
}
EOF

# Crear política personalizada
echo "Creando política personalizada..."
aws iam create-policy \
  --policy-name ArkaSagaPolicy \
  --policy-document file://saga-policy.json \
  --description "Custom policy for Arka Saga pattern"

# Obtener ARN de la cuenta
ACCOUNT_ID=$(aws sts get-caller-identity --output text --query 'Account')
SAGA_POLICY_ARN="arn:aws:iam::$ACCOUNT_ID:policy/ArkaSagaPolicy"

# Adjuntar políticas al role
echo "Adjuntando políticas..."
aws iam attach-role-policy \
  --role-name ArkaLambdaExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

aws iam attach-role-policy \
  --role-name ArkaLambdaExecutionRole \
  --policy-arn $SAGA_POLICY_ARN

# Obtener ARN del role
LAMBDA_ROLE_ARN=$(aws iam get-role --role-name ArkaLambdaExecutionRole --output text --query 'Role.Arn')

echo "✅ Lambda Role ARN: $LAMBDA_ROLE_ARN"

# Agregar a configuración
cat >> aws-config.txt << EOF
ACCOUNT_ID=$ACCOUNT_ID
LAMBDA_ROLE_ARN=$LAMBDA_ROLE_ARN
SAGA_POLICY_ARN=$SAGA_POLICY_ARN
EOF

# Limpiar archivos temporales
rm lambda-trust-policy.json saga-policy.json

echo "📄 Configuración actualizada en aws-config.txt"
echo "🔐 IAM Roles creados exitosamente"
echo "⏳ Esperando 10 segundos para propagación de IAM..."
sleep 10
