package com.babycash.backend.controller;

import com.babycash.backend.dto.contact.ContactInfoRequest;
import com.babycash.backend.dto.contact.ContactInfoResponse;
import com.babycash.backend.service.ContactInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de información de contacto
 */
@RestController
@RequestMapping("/api/contact-info")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ContactInfoController {

    private final ContactInfoService contactInfoService;

    /**
     * Obtiene la información de contacto (público)
     * GET /api/contact-info
     */
    @GetMapping
    public ResponseEntity<ContactInfoResponse> getContactInfo() {
        log.info("GET /api/contact-info - Fetching contact information");
        ContactInfoResponse response = contactInfoService.getContactInfo();
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza la información de contacto (admin)
     * PUT /api/contact-info
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactInfoResponse> updateContactInfo(
            @Valid @RequestBody ContactInfoRequest request) {
        log.info("PUT /api/contact-info - Updating contact information");
        ContactInfoResponse response = contactInfoService.updateContactInfo(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si la información de contacto está configurada (admin)
     * GET /api/contact-info/status
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> isConfigured() {
        log.info("GET /api/contact-info/status - Checking configuration status");
        boolean configured = contactInfoService.isConfigured();
        return ResponseEntity.ok(configured);
    }
}
