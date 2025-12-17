package com.babycash.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditStatus status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "user_id")
    private Long userId;

    private String username;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    public enum ActionType {
        ORDER_CREATED,
        ORDER_CANCELLED,
        PAYMENT_COMPLETED,
        PAYMENT_FAILED,
        PRODUCT_CREATED,
        PRODUCT_UPDATED,
        PRODUCT_DELETED,
        SECURITY_EVENT,
        LOGIN_FAILED,
        UNAUTHORIZED_ACCESS,
        RATE_LIMIT_EXCEEDED,
        LOGIN,
        LOGOUT
    }

    public enum AuditStatus {
        SUCCESS,
        FAILURE,
        WARNING
    }
}
