package com.babycash.backend.controller;

import com.babycash.backend.dto.testimonial.TestimonialRequest;
import com.babycash.backend.dto.testimonial.TestimonialResponse;
import com.babycash.backend.service.TestimonialService;
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

/**
 * Controlador REST para gestión de testimonios.
 * Expone endpoints públicos y de administración siguiendo REST best practices.
 */
@RestController
@RequestMapping("/api/testimonials")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TestimonialController {

    private final TestimonialService testimonialService;

    // ========== ENDPOINTS PÚBLICOS ==========

    /**
     * GET /api/testimonials - Obtiene testimonios aprobados
     */
    @GetMapping
    public ResponseEntity<List<TestimonialResponse>> getApprovedTestimonials() {
        log.info("GET /api/testimonials - Fetching approved testimonials");
        List<TestimonialResponse> testimonials = testimonialService.getAllApprovedTestimonials();
        return ResponseEntity.ok(testimonials);
    }

    /**
     * GET /api/testimonials/featured - Obtiene testimonios destacados
     */
    @GetMapping("/featured")
    public ResponseEntity<List<TestimonialResponse>> getFeaturedTestimonials() {
        log.info("GET /api/testimonials/featured - Fetching featured testimonials");
        List<TestimonialResponse> testimonials = testimonialService.getFeaturedTestimonials();
        return ResponseEntity.ok(testimonials);
    }

    /**
     * GET /api/testimonials/{id} - Obtiene un testimonio por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TestimonialResponse> getTestimonialById(@PathVariable Long id) {
        log.info("GET /api/testimonials/{} - Fetching testimonial", id);
        TestimonialResponse testimonial = testimonialService.getTestimonialById(id);
        return ResponseEntity.ok(testimonial);
    }

    /**
     * POST /api/testimonials - Crea un nuevo testimonio (público, requiere aprobación)
     */
    @PostMapping
    public ResponseEntity<TestimonialResponse> createTestimonial(
            @Valid @RequestBody TestimonialRequest request
    ) {
        log.info("POST /api/testimonials - Creating testimonial for: {}", request.getName());
        TestimonialResponse testimonial = testimonialService.createTestimonial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(testimonial);
    }

    // ========== ENDPOINTS DE ADMINISTRACIÓN ==========

    /**
     * GET /api/testimonials/admin/all - Obtiene todos los testimonios (admin)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TestimonialResponse>> getAllTestimonials() {
        log.info("GET /api/testimonials/admin/all - Fetching all testimonials");
        List<TestimonialResponse> testimonials = testimonialService.getAllTestimonials();
        return ResponseEntity.ok(testimonials);
    }

    /**
     * GET /api/testimonials/admin/paged - Obtiene testimonios paginados (admin)
     */
    @GetMapping("/admin/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TestimonialResponse>> getAllTestimonialsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/testimonials/admin/paged - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TestimonialResponse> testimonials = testimonialService.getAllTestimonialsPaged(pageable);
        return ResponseEntity.ok(testimonials);
    }

    /**
     * GET /api/testimonials/admin/pending - Obtiene testimonios pendientes
     */
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TestimonialResponse>> getPendingTestimonials() {
        log.info("GET /api/testimonials/admin/pending - Fetching pending testimonials");
        List<TestimonialResponse> testimonials = testimonialService.getPendingTestimonials();
        return ResponseEntity.ok(testimonials);
    }

    /**
     * GET /api/testimonials/admin/stats - Obtiene estadísticas
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestimonialService.TestimonialStats> getStats() {
        log.info("GET /api/testimonials/admin/stats - Fetching statistics");
        TestimonialService.TestimonialStats stats = testimonialService.getStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * PUT /api/testimonials/admin/{id} - Actualiza un testimonio
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestimonialResponse> updateTestimonial(
            @PathVariable Long id,
            @Valid @RequestBody TestimonialRequest request
    ) {
        log.info("PUT /api/testimonials/admin/{} - Updating testimonial", id);
        TestimonialResponse testimonial = testimonialService.updateTestimonial(id, request);
        return ResponseEntity.ok(testimonial);
    }

    /**
     * DELETE /api/testimonials/admin/{id} - Elimina un testimonio
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTestimonial(@PathVariable Long id) {
        log.info("DELETE /api/testimonials/admin/{} - Deleting testimonial", id);
        testimonialService.deleteTestimonial(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/testimonials/admin/{id}/approve - Aprueba un testimonio
     */
    @PostMapping("/admin/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestimonialResponse> approveTestimonial(@PathVariable Long id) {
        log.info("POST /api/testimonials/admin/{}/approve - Approving testimonial", id);
        TestimonialResponse testimonial = testimonialService.approveTestimonial(id);
        return ResponseEntity.ok(testimonial);
    }

    /**
     * POST /api/testimonials/admin/{id}/reject - Rechaza un testimonio
     */
    @PostMapping("/admin/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestimonialResponse> rejectTestimonial(@PathVariable Long id) {
        log.info("POST /api/testimonials/admin/{}/reject - Rejecting testimonial", id);
        TestimonialResponse testimonial = testimonialService.rejectTestimonial(id);
        return ResponseEntity.ok(testimonial);
    }

    /**
     * POST /api/testimonials/admin/{id}/toggle-featured - Marca/desmarca como destacado
     */
    @PostMapping("/admin/{id}/toggle-featured")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestimonialResponse> toggleFeatured(@PathVariable Long id) {
        log.info("POST /api/testimonials/admin/{}/toggle-featured - Toggling featured status", id);
        TestimonialResponse testimonial = testimonialService.toggleFeatured(id);
        return ResponseEntity.ok(testimonial);
    }
}
