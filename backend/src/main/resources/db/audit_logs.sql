-- =============================================================================
-- TABLA DE AUDITORÍA - audit_logs
-- =============================================================================
-- Registra todas las operaciones críticas para compliance y troubleshooting
-- =============================================================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    
    -- Usuario que realizó la acción
    user_id BIGINT,
    username VARCHAR(100),
    
    -- Información de la acción
    action_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    description TEXT,
    
    -- Información de la request
    ip_address VARCHAR(45), -- Soporta IPv6
    user_agent TEXT,
    
    -- Estado de la operación
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    metadata TEXT, -- JSON data adicional
    
    -- Timestamp automático
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key opcional (puede ser NULL para usuarios anónimos)
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE SET NULL
);

-- =============================================================================
-- ÍNDICES PARA PERFORMANCE DE CONSULTAS
-- =============================================================================

-- Por usuario (para ver historial de un usuario específico)
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_logs(user_id);

-- Por tipo de acción (para análisis de operaciones específicas)
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_logs(action_type);

-- Por timestamp (para búsquedas por fecha)
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_logs(timestamp DESC);

-- Por entidad (para ver historial de una orden/producto específico)
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_logs(entity_type, entity_id);

-- Por IP address (para detectar actividad sospechosa desde una IP)
CREATE INDEX IF NOT EXISTS idx_audit_ip ON audit_logs(ip_address);

-- Por status (para ver solo errores o warnings)
CREATE INDEX IF NOT EXISTS idx_audit_status ON audit_logs(status);

-- Índice compuesto para búsquedas de seguridad
CREATE INDEX IF NOT EXISTS idx_audit_security ON audit_logs(action_type, timestamp DESC) 
    WHERE action_type IN ('LOGIN_FAILED', 'UNAUTHORIZED_ACCESS', 'RATE_LIMIT_EXCEEDED', 'SECURITY_EVENT');

-- =============================================================================
-- COMMENTS
-- =============================================================================

COMMENT ON TABLE audit_logs IS 'Registro de auditoría de operaciones críticas';
COMMENT ON COLUMN audit_logs.user_id IS 'ID del usuario que realizó la acción (NULL si anónimo)';
COMMENT ON COLUMN audit_logs.action_type IS 'Tipo de acción: LOGIN, ORDER_CREATED, PAYMENT_COMPLETED, etc.';
COMMENT ON COLUMN audit_logs.entity_type IS 'Tipo de entidad afectada: Order, Payment, Product, User';
COMMENT ON COLUMN audit_logs.entity_id IS 'ID de la entidad afectada';
COMMENT ON COLUMN audit_logs.ip_address IS 'IP desde donde se realizó la acción';
COMMENT ON COLUMN audit_logs.status IS 'SUCCESS, FAILURE, o WARNING';
COMMENT ON COLUMN audit_logs.metadata IS 'Datos adicionales en formato JSON';

-- =============================================================================
-- EJEMPLO DE DATOS
-- =============================================================================

-- Login exitoso
-- INSERT INTO audit_logs (user_id, username, action_type, description, ip_address, status)
-- VALUES (1, 'admin@babycash.com', 'LOGIN', 'Usuario autenticado exitosamente', '192.168.1.100', 'SUCCESS');

-- Pago completado
-- INSERT INTO audit_logs (user_id, username, action_type, entity_type, entity_id, description, ip_address, status)
-- VALUES (2, 'cliente@example.com', 'PAYMENT_COMPLETED', 'Payment', 15, 'Pago procesado exitosamente', '192.168.1.101', 'SUCCESS');

-- Intento de login fallido
-- INSERT INTO audit_logs (username, action_type, description, ip_address, status, error_message)
-- VALUES ('hacker@evil.com', 'LOGIN_FAILED', 'Intento de login fallido', '192.168.1.200', 'FAILURE', 'Credenciales inválidas');

-- =============================================================================
-- MANTENIMIENTO
-- =============================================================================

-- Query para ver logs de los últimos 7 días
-- SELECT * FROM audit_logs WHERE timestamp > NOW() - INTERVAL '7 days' ORDER BY timestamp DESC;

-- Query para ver intentos de login fallidos
-- SELECT * FROM audit_logs WHERE action_type = 'LOGIN_FAILED' ORDER BY timestamp DESC LIMIT 50;

-- Query para ver actividad de un usuario específico
-- SELECT * FROM audit_logs WHERE user_id = 1 ORDER BY timestamp DESC;

-- Query para eliminar logs antiguos (más de 90 días)
-- DELETE FROM audit_logs WHERE timestamp < NOW() - INTERVAL '90 days';

-- Query para estadísticas de seguridad
-- SELECT 
--     action_type, 
--     status, 
--     COUNT(*) as count,
--     DATE_TRUNC('day', timestamp) as day
-- FROM audit_logs
-- WHERE timestamp > NOW() - INTERVAL '30 days'
-- GROUP BY action_type, status, day
-- ORDER BY day DESC, count DESC;
