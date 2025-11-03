package com.babycash.backend.service;

import com.babycash.backend.entity.AuditLog;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de auditoría
 * 
 * Registra automáticamente operaciones críticas en la base de datos
 * para trazabilidad y compliance
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Registra una acción de auditoría de forma asíncrona
     * No bloquea la operación principal
     */
    @Async
    @Transactional
    public void logAction(
        AuditLog.ActionType actionType,
        String entityType,
        Long entityId,
        String description
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            HttpServletRequest request = getCurrentRequest();

            AuditLog auditLog = AuditLog.builder()
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .status(AuditLog.AuditStatus.SUCCESS)
                .timestamp(LocalDateTime.now())
                .build();

            // Agregar información del usuario si está autenticado
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                if (auth.getPrincipal() instanceof User user) {
                    auditLog.setUserId(user.getId());
                    auditLog.setUsername(user.getEmail());
                }
            }

            // Agregar información de la request
            if (request != null) {
                auditLog.setIpAddress(getClientIP(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
            }

            auditLogRepository.save(auditLog);
            
            log.debug("Audit log created: {} - {}", actionType, description);
        } catch (Exception e) {
            log.error("Error creating audit log: {}", e.getMessage(), e);
        }
    }

    /**
     * Registra una acción fallida
     */
    @Async
    @Transactional
    public void logFailure(
        AuditLog.ActionType actionType,
        String entityType,
        Long entityId,
        String description,
        String errorMessage
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            HttpServletRequest request = getCurrentRequest();

            AuditLog auditLog = AuditLog.builder()
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .errorMessage(errorMessage)
                .status(AuditLog.AuditStatus.FAILURE)
                .timestamp(LocalDateTime.now())
                .build();

            if (auth != null && auth.getPrincipal() instanceof User user) {
                auditLog.setUserId(user.getId());
                auditLog.setUsername(user.getEmail());
            }

            if (request != null) {
                auditLog.setIpAddress(getClientIP(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
            }

            auditLogRepository.save(auditLog);
            
            log.warn("Audit failure logged: {} - {}", actionType, errorMessage);
        } catch (Exception e) {
            log.error("Error creating audit failure log: {}", e.getMessage(), e);
        }
    }

    /**
     * Registra un evento de seguridad
     */
    @Async
    @Transactional
    public void logSecurityEvent(String description, String details) {
        try {
            HttpServletRequest request = getCurrentRequest();

            AuditLog auditLog = AuditLog.builder()
                .actionType(AuditLog.ActionType.SECURITY_EVENT)
                .description(description)
                .metadata(details)
                .status(AuditLog.AuditStatus.WARNING)
                .timestamp(LocalDateTime.now())
                .build();

            if (request != null) {
                auditLog.setIpAddress(getClientIP(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
            }

            auditLogRepository.save(auditLog);
            
            log.warn("Security event logged: {}", description);
        } catch (Exception e) {
            log.error("Error logging security event: {}", e.getMessage(), e);
        }
    }

    /**
     * Registra intento de login fallido
     */
    @Async
    @Transactional
    public void logFailedLogin(String username, String reason) {
        try {
            HttpServletRequest request = getCurrentRequest();

            AuditLog auditLog = AuditLog.builder()
                .actionType(AuditLog.ActionType.LOGIN_FAILED)
                .username(username)
                .description("Intento de login fallido: " + reason)
                .errorMessage(reason)
                .status(AuditLog.AuditStatus.FAILURE)
                .timestamp(LocalDateTime.now())
                .build();

            if (request != null) {
                String ip = getClientIP(request);
                auditLog.setIpAddress(ip);
                auditLog.setUserAgent(request.getHeader("User-Agent"));

                // Verificar intentos fallidos recientes desde esta IP
                long recentFailures = auditLogRepository.countFailedLoginsByIpSince(
                    ip, 
                    LocalDateTime.now().minusMinutes(15)
                );

                if (recentFailures >= 5) {
                    logSecurityEvent(
                        "Múltiples intentos de login fallidos",
                        String.format("IP %s ha tenido %d intentos fallidos en 15 minutos", ip, recentFailures)
                    );
                }
            }

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Error logging failed login: {}", e.getMessage(), e);
        }
    }

    /**
     * Obtiene logs por usuario
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getUserLogs(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }

    /**
     * Obtiene logs por tipo de acción
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getLogsByAction(AuditLog.ActionType actionType, Pageable pageable) {
        return auditLogRepository.findByActionType(actionType, pageable);
    }

    /**
     * Obtiene logs de una entidad específica
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getEntityLogs(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    /**
     * Obtiene eventos de seguridad recientes (últimas 24 horas)
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentSecurityEvents() {
        return auditLogRepository.findRecentSecurityEvents(LocalDateTime.now().minusHours(24));
    }

    /**
     * Limpia logs antiguos (más de 90 días)
     */
    @Transactional
    public void cleanupOldLogs() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
            auditLogRepository.deleteByTimestampBefore(cutoffDate);
            log.info("Old audit logs cleaned up (before {})", cutoffDate);
        } catch (Exception e) {
            log.error("Error cleaning up old logs: {}", e.getMessage(), e);
        }
    }

    /**
     * Obtiene la request HTTP actual
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * Obtiene la IP real del cliente
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }

        String xrHeader = request.getHeader("X-Real-IP");
        if (xrHeader != null && !xrHeader.isEmpty()) {
            return xrHeader;
        }

        return request.getRemoteAddr();
    }
}
