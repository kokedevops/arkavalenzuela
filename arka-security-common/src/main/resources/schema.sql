-- Schema DDL para ARKA Security
-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'USUARIO',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_acceso TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_rol (rol),
    INDEX idx_activo (activo)
);

-- Tabla de refresh tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    fecha_expiracion TIMESTAMP NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(500) NULL,
    INDEX idx_token (token),
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_activo (activo),
    INDEX idx_expiracion (fecha_expiracion),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Datos de prueba - Usuario administrador por defecto
INSERT INTO usuarios (username, email, password, nombre_completo, rol, activo) 
VALUES (
    'admin', 
    'admin@arka.com', 
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LeGNeLGUWpHKuS.i2', -- password: admin123
    'Administrador del Sistema', 
    'ADMINISTRADOR', 
    TRUE
) ON DUPLICATE KEY UPDATE username = username;

-- Usuario gestor de prueba
INSERT INTO usuarios (username, email, password, nombre_completo, rol, activo) 
VALUES (
    'gestor', 
    'gestor@arka.com', 
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LeGNeLGUWpHKuS.i2', -- password: admin123
    'Gestor de Operaciones', 
    'GESTOR', 
    TRUE
) ON DUPLICATE KEY UPDATE username = username;

-- Usuario operador de prueba
INSERT INTO usuarios (username, email, password, nombre_completo, rol, activo) 
VALUES (
    'operador', 
    'operador@arka.com', 
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LeGNeLGUWpHKuS.i2', -- password: admin123
    'Operador de Cálculos', 
    'OPERADOR', 
    TRUE
) ON DUPLICATE KEY UPDATE username = username;

-- Usuario regular de prueba
INSERT INTO usuarios (username, email, password, nombre_completo, rol, activo) 
VALUES (
    'usuario', 
    'usuario@arka.com', 
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LeGNeLGUWpHKuS.i2', -- password: admin123
    'Usuario Regular', 
    'USUARIO', 
    TRUE
) ON DUPLICATE KEY UPDATE username = username;
