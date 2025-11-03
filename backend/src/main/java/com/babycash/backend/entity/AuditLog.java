package com.babycash.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad para registrar auditoría de operaciones críticas
 * 
 * Registra:
 * - Quién realizó la acción (usuario)
 * - Qué acción se realizó (tipo)
 * - Sobre qué entidad (entityType, entityId)
 * - Cuándo se realizó (timestamp)
 * - Desde dónde (IP address)
 * - Detalles adicionales (description)
 * - Si fue exitosa o falló (status)
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_action", columnList = "action_type"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que realizó la acción
     */
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 100)
    private String username;

    /**
     * Tipo de acción realizada
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ActionType actionType;

    /**
     * Tipo de entidad afectada (Order, Payment, Product, etc.)
     */
    @Column(name = "entity_type", length = 50)
    private String entityType;

    /**
     * ID de la entidad afectada
     */
    @Column(name = "entity_id")
    private Long entityId;

    /**
     * Descripción detallada de la acción
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * IP desde donde se realizó la acción
     */
    @Column(name = "ip_address", length = 45) // IPv6 max length
    private String ipAddress;

    /**
     * User-Agent del cliente
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * Estado de la operación
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AuditStatus status;

    /**
     * Mensaje de error si la operación falló
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Datos adicionales en formato JSON
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * Timestamp automático de creación
     */
    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    /**
     * Tipos de acciones auditables
     */
    public enum ActionType {
        // Autenticación
        LOGIN,
        LOGOUT,
        LOGIN_FAILED,
        REGISTER,
        
        // Órdenes
        ORDER_CREATED,
        ORDER_CANCELLED,
        ORDER_STATUS_CHANGED,
        
        // Pagos
        PAYMENT_INITIATED,
        PAYMENT_COMPLETED,
        PAYMENT_FAILED,
        PAYMENT_REFUNDED,
        
        // Productos
        PRODUCT_CREATED,
        PRODUCT_UPDATED,
        PRODUCT_DELETED,
        PRODUCT_STOCK_CHANGED,
        
        // Usuarios
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        PASSWORD_CHANGED,
        
        // Admin
        ADMIN_ACTION,
        SECURITY_EVENT,
        DATA_EXPORT,
        
        // Sistema
        SYSTEM_ERROR,
        RATE_LIMIT_EXCEEDED,
        UNAUTHORIZED_ACCESS
    }

    /**
     * Estados de auditoría
     */
    public enum AuditStatus {
        SUCCESS,
        FAILURE,
        WARNING
    }
}
