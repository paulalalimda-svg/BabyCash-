package com.babycash.backend.controller;

import com.babycash.backend.dto.contact.ContactMessageRequest;
import com.babycash.backend.dto.contact.ContactMessageResponse;
import com.babycash.backend.model.entity.ContactMessage.MessageStatus;
import com.babycash.backend.service.ContactMessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Controlador REST para mensajes de contacto
 */
@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    /**
     * Envía un mensaje de contacto (público)
     * POST /api/contact/send
     */
    @PostMapping("/send")
    public ResponseEntity<ContactMessageResponse> sendMessage(
            @Valid @RequestBody ContactMessageRequest request,
            HttpServletRequest servletRequest) {

        log.info("POST /api/contact/send - New contact message from: {}", request.getEmail());

        String ipAddress = getClientIpAddress(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        ContactMessageResponse response = contactMessageService.sendMessage(request, ipAddress, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene todos los mensajes (admin)
     * GET /api/contact/admin/messages
     */
    @GetMapping("/admin/messages")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<List<ContactMessageResponse>> getAllMessages() {
        log.info("GET /api/contact/admin/messages - Fetching all messages");
        List<ContactMessageResponse> messages = contactMessageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * Obtiene todos los mensajes paginados (admin)
     * GET /api/contact/admin/messages/paged
     */
    @GetMapping("/admin/messages/paged")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<Page<ContactMessageResponse>> getAllMessagesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/contact/admin/messages/paged - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "createdAt"));
        Page<ContactMessageResponse> messages = contactMessageService.getAllMessagesPaged(pageable);
        return ResponseEntity.ok(messages);
    }

    /**
     * Obtiene mensajes nuevos (admin)
     * GET /api/contact/admin/messages/new
     */
    @GetMapping("/admin/messages/new")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<List<ContactMessageResponse>> getNewMessages() {
        log.info("GET /api/contact/admin/messages/new - Fetching new messages");
        List<ContactMessageResponse> messages = contactMessageService.getNewMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * Cuenta mensajes nuevos (admin)
     * GET /api/contact/admin/messages/new/count
     */
    @GetMapping("/admin/messages/new/count")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<Map<String, Long>> countNewMessages() {
        log.info("GET /api/contact/admin/messages/new/count");
        long count = contactMessageService.countNewMessages();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Obtiene mensajes recientes (admin)
     * GET /api/contact/admin/messages/recent
     */
    @GetMapping("/admin/messages/recent")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<List<ContactMessageResponse>> getRecentMessages() {
        log.info("GET /api/contact/admin/messages/recent");
        List<ContactMessageResponse> messages = contactMessageService.getRecentMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * Obtiene un mensaje por ID (admin)
     * GET /api/contact/admin/messages/{id}
     */
    @GetMapping("/admin/messages/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<ContactMessageResponse> getMessageById(@PathVariable Long id) {
        log.info("GET /api/contact/admin/messages/{} - Fetching message", id);
        ContactMessageResponse message = contactMessageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }

    /**
     * Marca un mensaje como leído (admin)
     * POST /api/contact/admin/messages/{id}/read
     */
    @PostMapping("/admin/messages/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<ContactMessageResponse> markAsRead(@PathVariable Long id) {
        log.info("POST /api/contact/admin/messages/{}/read", id);
        ContactMessageResponse message = contactMessageService.markAsRead(id);
        return ResponseEntity.ok(message);
    }

    /**
     * Marca un mensaje como respondido (admin)
     * POST /api/contact/admin/messages/{id}/reply
     */
    @PostMapping("/admin/messages/{id}/reply")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<ContactMessageResponse> markAsReplied(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        log.info("POST /api/contact/admin/messages/{}/reply", id);
        String adminNotes = body != null ? body.get("adminNotes") : null;
        ContactMessageResponse message = contactMessageService.markAsReplied(id, adminNotes);
        return ResponseEntity.ok(message);
    }

    /**
     * Archiva un mensaje (admin)
     * POST /api/contact/admin/messages/{id}/archive
     */
    @PostMapping("/admin/messages/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<ContactMessageResponse> archiveMessage(@PathVariable Long id) {
        log.info("POST /api/contact/admin/messages/{}/archive", id);
        ContactMessageResponse message = contactMessageService.archiveMessage(id);
        return ResponseEntity.ok(message);
    }

    /**
     * Desarchiva un mensaje (admin)
     * POST /api/contact/admin/messages/{id}/unarchive
     */
    @PostMapping("/admin/messages/{id}/unarchive")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<ContactMessageResponse> unarchiveMessage(@PathVariable Long id) {
        log.info("POST /api/contact/admin/messages/{}/unarchive", id);
        ContactMessageResponse message = contactMessageService.unarchiveMessage(id);
        return ResponseEntity.ok(message);
    }

    /**
     * Elimina un mensaje (admin)
     * DELETE /api/contact/admin/messages/{id}
     */
    @DeleteMapping("/admin/messages/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        log.info("DELETE /api/contact/admin/messages/{}", id);
        contactMessageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    // Helper methods

    /**
     * Obtiene la IP del cliente
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
