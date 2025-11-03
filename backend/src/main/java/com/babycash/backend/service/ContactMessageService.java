package com.babycash.backend.service;

import com.babycash.backend.dto.contact.ContactMessageRequest;
import com.babycash.backend.dto.contact.ContactMessageResponse;
import com.babycash.backend.model.entity.ContactMessage;
import com.babycash.backend.model.entity.ContactMessage.MessageStatus;
import com.babycash.backend.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de mensajes de contacto
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final EmailService emailService;

    /**
     * Envía un mensaje de contacto
     */
    @Transactional
    public ContactMessageResponse sendMessage(ContactMessageRequest request, String ipAddress, String userAgent) {
        log.info("Processing contact message from: {}", request.getEmail());

        // Guardar mensaje en base de datos
        ContactMessage message = ContactMessage.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .subject(request.getSubject())
                .message(request.getMessage())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status(MessageStatus.NEW)
                .build();

        ContactMessage saved = contactMessageRepository.save(message);
        log.info("Contact message saved with ID: {}", saved.getId());

        // Enviar email al administrador (async)
        try {
            emailService.sendContactFormEmail(request, ipAddress);
            log.info("Admin notification email sent for message ID: {}", saved.getId());
        } catch (Exception e) {
            log.error("Error sending admin notification email: {}", e.getMessage(), e);
            // No fallar la transacción si el email falla
        }

        // Enviar email de confirmación al usuario (async)
        try {
            emailService.sendConfirmationEmail(request.getEmail(), request.getName());
            log.info("Confirmation email sent to user: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Error sending confirmation email: {}", e.getMessage(), e);
            // No fallar la transacción si el email falla
        }

        return mapToResponse(saved);
    }

    /**
     * Obtiene todos los mensajes
     */
    @Transactional(readOnly = true)
    public List<ContactMessageResponse> getAllMessages() {
        log.info("Fetching all contact messages");
        return contactMessageRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los mensajes paginados
     */
    @Transactional(readOnly = true)
    public Page<ContactMessageResponse> getAllMessagesPaged(Pageable pageable) {
        log.info("Fetching all contact messages paged");
        Page<ContactMessage> messages = contactMessageRepository.findAll(pageable);
        return messages.map(this::mapToResponse);
    }

    /**
     * Obtiene mensajes por estado
     */
    @Transactional(readOnly = true)
    public List<ContactMessageResponse> getMessagesByStatus(MessageStatus status) {
        log.info("Fetching messages with status: {}", status);
        return contactMessageRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene mensajes nuevos
     */
    @Transactional(readOnly = true)
    public List<ContactMessageResponse> getNewMessages() {
        return getMessagesByStatus(MessageStatus.NEW);
    }

    /**
     * Cuenta mensajes nuevos
     */
    @Transactional(readOnly = true)
    public long countNewMessages() {
        return contactMessageRepository.countNewMessages();
    }

    /**
     * Obtiene un mensaje por ID
     */
    @Transactional(readOnly = true)
    public ContactMessageResponse getMessageById(Long id) {
        log.info("Fetching message with ID: {}", id);
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje no encontrado con ID: " + id));
        return mapToResponse(message);
    }

    /**
     * Marca un mensaje como leído
     */
    @Transactional
    public ContactMessageResponse markAsRead(Long id) {
        log.info("Marking message as read: {}", id);
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje no encontrado con ID: " + id));
        
        message.markAsRead();
        ContactMessage updated = contactMessageRepository.save(message);
        return mapToResponse(updated);
    }

    /**
     * Marca un mensaje como respondido
     */
    @Transactional
    public ContactMessageResponse markAsReplied(Long id, String adminNotes) {
        log.info("Marking message as replied: {}", id);
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje no encontrado con ID: " + id));
        
        message.markAsReplied();
        if (adminNotes != null && !adminNotes.isBlank()) {
            message.setAdminNotes(adminNotes);
        }
        
        ContactMessage updated = contactMessageRepository.save(message);
        return mapToResponse(updated);
    }

    /**
     * Archiva un mensaje
     */
    @Transactional
    public ContactMessageResponse archiveMessage(Long id) {
        log.info("Archiving message: {}", id);
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje no encontrado con ID: " + id));
        
        message.setStatus(MessageStatus.ARCHIVED);
        ContactMessage updated = contactMessageRepository.save(message);
        return mapToResponse(updated);
    }

    /**
     * Desarchiva un mensaje
     */
    @Transactional
    public ContactMessageResponse unarchiveMessage(Long id) {
        log.info("Unarchiving message: {}", id);
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje no encontrado con ID: " + id));
        
        message.setStatus(MessageStatus.READ);
        ContactMessage updated = contactMessageRepository.save(message);
        return mapToResponse(updated);
    }

    /**
     * Elimina un mensaje
     */
    @Transactional
    public void deleteMessage(Long id) {
        log.info("Deleting message: {}", id);
        if (!contactMessageRepository.existsById(id)) {
            throw new IllegalArgumentException("Mensaje no encontrado con ID: " + id);
        }
        contactMessageRepository.deleteById(id);
    }

    /**
     * Obtiene mensajes recientes (últimas 24 horas)
     */
    @Transactional(readOnly = true)
    public List<ContactMessageResponse> getRecentMessages() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        return contactMessageRepository.findRecentMessages(yesterday).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper methods

    /**
     * Mapea entidad a DTO de respuesta
     */
    private ContactMessageResponse mapToResponse(ContactMessage message) {
        return ContactMessageResponse.builder()
                .id(message.getId())
                .name(message.getName())
                .email(message.getEmail())
                .phone(message.getPhone())
                .subject(message.getSubject())
                .message(message.getMessage())
                .ipAddress(message.getIpAddress())
                .status(message.getStatus())
                .readAt(message.getReadAt())
                .repliedAt(message.getRepliedAt())
                .adminNotes(message.getAdminNotes())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}
