package com.babycash.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad para almacenar mensajes del formulario de contacto
 */
@Entity
@Table(name = "contact_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 150)
    private String subject;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MessageStatus status = MessageStatus.NEW;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @Column(name = "admin_notes", length = 500)
    private String adminNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods

    /**
     * Marca el mensaje como leído
     */
    public void markAsRead() {
        if (this.status == MessageStatus.NEW) {
            this.status = MessageStatus.READ;
            this.readAt = LocalDateTime.now();
        }
    }

    /**
     * Marca el mensaje como respondido
     */
    public void markAsReplied() {
        this.status = MessageStatus.REPLIED;
        this.repliedAt = LocalDateTime.now();
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }

    /**
     * Verifica si el mensaje es nuevo
     */
    public boolean isNew() {
        return this.status == MessageStatus.NEW;
    }

    /**
     * Verifica si el mensaje ha sido respondido
     */
    public boolean isReplied() {
        return this.status == MessageStatus.REPLIED;
    }

    /**
     * Estado del mensaje de contacto
     */
    public enum MessageStatus {
        NEW,        // Nuevo, no leído
        READ,       // Leído por admin
        REPLIED,    // Respondido
        ARCHIVED    // Archivado
    }
}
