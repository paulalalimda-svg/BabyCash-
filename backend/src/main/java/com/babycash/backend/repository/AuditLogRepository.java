package com.babycash.backend.repository;

import com.babycash.backend.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Buscar logs por usuario
     */
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    /**
     * Buscar logs por tipo de acción
     */
    Page<AuditLog> findByActionType(AuditLog.ActionType actionType, Pageable pageable);

    /**
     * Buscar logs por rango de fechas
     */
    Page<AuditLog> findByTimestampBetween(
        LocalDateTime start, 
        LocalDateTime end, 
        Pageable pageable
    );

    /**
     * Buscar logs de una entidad específica
     */
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(
        String entityType, 
        Long entityId
    );

    /**
     * Buscar intentos de login fallidos por IP
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.ipAddress = :ip " +
           "AND a.actionType = 'LOGIN_FAILED' " +
           "AND a.timestamp > :since")
    long countFailedLoginsByIpSince(
        @Param("ip") String ipAddress, 
        @Param("since") LocalDateTime since
    );

    /**
     * Buscar eventos de seguridad recientes
     */
    @Query("SELECT a FROM AuditLog a WHERE a.actionType IN " +
           "('LOGIN_FAILED', 'UNAUTHORIZED_ACCESS', 'RATE_LIMIT_EXCEEDED', 'SECURITY_EVENT') " +
           "AND a.timestamp > :since ORDER BY a.timestamp DESC")
    List<AuditLog> findRecentSecurityEvents(@Param("since") LocalDateTime since);

    /**
     * Buscar logs por usuario y tipo de acción
     */
    Page<AuditLog> findByUserIdAndActionType(
        Long userId, 
        AuditLog.ActionType actionType, 
        Pageable pageable
    );

    /**
     * Eliminar logs antiguos (para limpieza periódica)
     */
    void deleteByTimestampBefore(LocalDateTime timestamp);
}
