package com.babycash.backend.dto.contact;

import com.babycash.backend.model.entity.ContactMessage.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para mensajes de contacto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String subject;
    private String message;
    private String ipAddress;
    private MessageStatus status;
    private LocalDateTime readAt;
    private LocalDateTime repliedAt;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
