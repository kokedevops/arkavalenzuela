package com.arka.gestorsolicitudes.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para integración con AWS S3 y SQS
 */
@Service
public class AWSIntegrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AWSIntegrationService.class);
    
    private final S3Client s3Client;
    private final SqsClient sqsClient;
    
    @Value("${aws.s3.bucket-name:arka-envios-bucket}")
    private String bucketName;
    
    @Value("${aws.sqs.queue-url:https://sqs.us-east-1.amazonaws.com/123456789/arka-envios-queue}")
    private String queueUrl;
    
    public AWSIntegrationService(S3Client s3Client, SqsClient sqsClient) {
        this.s3Client = s3Client;
        this.sqsClient = sqsClient;
    }
    
    /**
     * Guarda el resultado del cálculo de envío en S3
     */
    public String guardarCalculoEnS3(String calculoId, String contenido) {
        try {
            String nombreArchivo = String.format("calculos-envio/%s-%s.json", 
                calculoId, 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(nombreArchivo)
                .contentType("application/json")
                .metadata(java.util.Map.of(
                    "calculoId", calculoId,
                    "timestamp", LocalDateTime.now().toString(),
                    "servicio", "arca-gestor-solicitudes"
                ))
                .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromString(contenido));
            
            String s3Url = String.format("s3://%s/%s", bucketName, nombreArchivo);
            logger.info("Cálculo de envío guardado en S3: {}", s3Url);
            
            return s3Url;
            
        } catch (Exception e) {
            logger.error("Error al guardar cálculo en S3: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Envía notificación a SQS cuando se completa un cálculo
     */
    public boolean enviarNotificacionSQS(String calculoId, String estado, String mensaje) {
        try {
            String mensajeSQS = String.format("""
                {
                    "tipo": "CALCULO_ENVIO_COMPLETADO",
                    "calculoId": "%s",
                    "estado": "%s",
                    "mensaje": "%s",
                    "timestamp": "%s",
                    "servicio": "arca-gestor-solicitudes"
                }
                """, calculoId, estado, mensaje, LocalDateTime.now());
            
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(mensajeSQS)
                .messageAttributes(java.util.Map.of(
                    "calculoId", software.amazon.awssdk.services.sqs.model.MessageAttributeValue.builder()
                        .stringValue(calculoId)
                        .dataType("String")
                        .build(),
                    "estado", software.amazon.awssdk.services.sqs.model.MessageAttributeValue.builder()
                        .stringValue(estado)
                        .dataType("String")
                        .build()
                ))
                .build();
            
            sqsClient.sendMessage(sendMessageRequest);
            
            logger.info("Notificación enviada a SQS para cálculo: {}", calculoId);
            return true;
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación a SQS: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Envía métricas de Circuit Breaker a SQS para procesamiento
     */
    public boolean enviarMetricasCircuitBreaker(String servicioAfectado, String estado, int numeroFallos) {
        try {
            String mensajeMetricas = String.format("""
                {
                    "tipo": "CIRCUIT_BREAKER_METRICS",
                    "servicio": "%s",
                    "estado": "%s",
                    "numeroFallos": %d,
                    "timestamp": "%s",
                    "origen": "arca-gestor-solicitudes"
                }
                """, servicioAfectado, estado, numeroFallos, LocalDateTime.now());
            
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(mensajeMetricas)
                .messageAttributes(java.util.Map.of(
                    "tipo", software.amazon.awssdk.services.sqs.model.MessageAttributeValue.builder()
                        .stringValue("CIRCUIT_BREAKER_METRICS")
                        .dataType("String")
                        .build(),
                    "servicio", software.amazon.awssdk.services.sqs.model.MessageAttributeValue.builder()
                        .stringValue(servicioAfectado)
                        .dataType("String")
                        .build()
                ))
                .build();
            
            sqsClient.sendMessage(sendMessageRequest);
            
            logger.info("Métricas de Circuit Breaker enviadas a SQS para servicio: {}", servicioAfectado);
            return true;
            
        } catch (Exception e) {
            logger.error("Error al enviar métricas a SQS: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Crea un backup de las configuraciones del Circuit Breaker en S3
     */
    public String crearBackupConfiguracion(String configuracion) {
        try {
            String nombreArchivo = String.format("backups/circuit-breaker-config-%s.json", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(nombreArchivo)
                .contentType("application/json")
                .metadata(java.util.Map.of(
                    "tipo", "circuit-breaker-config-backup",
                    "timestamp", LocalDateTime.now().toString(),
                    "servicio", "arca-gestor-solicitudes"
                ))
                .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromString(configuracion));
            
            String s3Url = String.format("s3://%s/%s", bucketName, nombreArchivo);
            logger.info("Backup de configuración guardado en S3: {}", s3Url);
            
            return s3Url;
            
        } catch (Exception e) {
            logger.error("Error al crear backup de configuración: {}", e.getMessage(), e);
            return null;
        }
    }
}
