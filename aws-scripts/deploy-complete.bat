@echo off
echo ========================================
echo 🌟 DESPLIEGUE COMPLETO A AWS - WINDOWS
echo ========================================
echo.

echo 🔍 Verificando AWS CLI...
aws --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ AWS CLI no está instalado
    echo 💡 Instalar desde: https://aws.amazon.com/cli/
    pause
    exit /b 1
)

echo 🔍 Verificando credenciales AWS...
aws sts get-caller-identity >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ Credenciales AWS no configuradas
    echo 💡 Ejecutar: aws configure
    pause
    exit /b 1
)

for /f "tokens=*" %%i in ('aws sts get-caller-identity --output text --query Account') do set ACCOUNT_ID=%%i
echo ✅ Cuenta AWS: %ACCOUNT_ID%
echo.

echo 📍 INICIANDO DESPLIEGUE PASO A PASO...
echo.

echo 📍 PASO 1: Creando SNS Topics...
call create-sns-topics.bat
if %ERRORLEVEL% neq 0 (
    echo ❌ Error en PASO 1
    pause
    exit /b 1
)

echo 📍 PASO 2: Creando SQS Queues...
call create-sqs-queues.bat
if %ERRORLEVEL% neq 0 (
    echo ❌ Error en PASO 2
    pause
    exit /b 1
)

echo 📍 PASO 3: Creando IAM Roles...
call create-iam-roles.bat
if %ERRORLEVEL% neq 0 (
    echo ❌ Error en PASO 3
    pause
    exit /b 1
)

echo 📍 PASO 4: Compilando y empaquetando...
cd ..
call gradlew clean build -x test
if %ERRORLEVEL% neq 0 (
    echo ❌ Error compilando proyecto
    pause
    exit /b 1
)
cd aws-scripts

echo 📍 PASO 5: Desplegando Lambda Functions...
call deploy-lambdas.bat
if %ERRORLEVEL% neq 0 (
    echo ❌ Error en PASO 5
    pause
    exit /b 1
)

echo 📍 PASO 6: Configurando Triggers...
call configure-triggers.bat
if %ERRORLEVEL% neq 0 (
    echo ❌ Error en PASO 6
    pause
    exit /b 1
)

echo.
echo 🎉 DESPLIEGUE COMPLETADO EXITOSAMENTE
echo =====================================
echo.
echo 📋 RESUMEN:
echo - SNS Topics: Creados
echo - SQS Queues: Creadas
echo - Lambda Functions: Desplegadas
echo - IAM Roles: Configurados
echo - Triggers: Activos
echo.
echo 📝 Archivos generados:
echo - aws-config.txt (configuración)
echo - aws-environment.env (variables de entorno)
echo.
echo 📊 PRÓXIMOS PASOS:
echo 1. Revisar aws-config.txt
echo 2. Configurar base de datos RDS/DocumentDB
echo 3. Actualizar application-aws.properties
echo 4. Probar la aplicación
echo.
echo 💰 Costo estimado: $87-145 USD/mes
pause
