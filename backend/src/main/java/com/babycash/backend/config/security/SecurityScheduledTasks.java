package com.babycash.backend.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.babycash.backend.service.AuditService;
import com.babycash.backend.service.RefreshTokenService;

/**
 * Tareas programadas para mantenimiento de seguridad
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityScheduledTasks {

    private final RateLimitConfig rateLimitConfig;
    private final AuditService auditService;
    private final RefreshTokenService refreshTokenService;

    /**
     * Limpia buckets de rate limiting cada hora
     * Previene memory leaks en entornos de alta carga
     */
    @Scheduled(cron = "0 0 * * * *") // Cada hora
    public void cleanupRateLimitBuckets() {
        try {
            log.info("Starting rate limit buckets cleanup...");
            rateLimitConfig.cleanupOldBuckets();
            log.info("Rate limit buckets cleanup completed");
        } catch (Exception e) {
            log.error("Error cleaning up rate limit buckets", e);
        }
    }

    /**
     * Limpia logs de auditoría antiguos cada día a las 2 AM
     * Mantiene solo últimos 90 días por regulaciones
     */
    @Scheduled(cron = "0 0 2 * * *") // 2 AM cada día
    public void cleanupOldAuditLogs() {
        try {
            log.info("Starting old audit logs cleanup...");
            auditService.cleanupOldLogs();
            log.info("Old audit logs cleanup completed");
        } catch (Exception e) {
            log.error("Error cleaning up old audit logs", e);
        }
    }

    /**
     * Reporta eventos de seguridad cada día a las 8 AM
     * Para revisión del equipo de seguridad
     */
    @Scheduled(cron = "0 0 8 * * *") // 8 AM cada día
    public void reportSecurityEvents() {
        try {
            var securityEvents = auditService.getRecentSecurityEvents();
            
            if (!securityEvents.isEmpty()) {
                log.warn("Security Events Report - Found {} events in last 24 hours", 
                    securityEvents.size());
                
                securityEvents.forEach(event -> 
                    log.warn("Security Event: {} - {} from IP: {}", 
                        event.getActionType(),
                        event.getDescription(),
                        event.getIpAddress())
                );
            } else {
                log.info("Security Events Report - No security events in last 24 hours");
            }
        } catch (Exception e) {
            log.error("Error generating security events report", e);
        }
    }

    /**
     * Limpia refresh tokens expirados y revocados cada día a las 3 AM
     * Elimina tokens más antiguos de 30 días
     */
    @Scheduled(cron = "0 0 3 * * *") // 3 AM cada día
    public void cleanupOldRefreshTokens() {
        try {
            log.info("Starting old refresh tokens cleanup...");
            int deletedCount = refreshTokenService.cleanupOldTokens();
            log.info("Old refresh tokens cleanup completed - Deleted {} tokens", deletedCount);
        } catch (Exception e) {
            log.error("Error cleaning up old refresh tokens", e);
        }
    }
}
