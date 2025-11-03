package com.babycash.backend.service;

import com.babycash.backend.dto.testimonial.TestimonialRequest;
import com.babycash.backend.dto.testimonial.TestimonialResponse;
import com.babycash.backend.model.entity.Testimonial;
import com.babycash.backend.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de testimonios.
 * Implementa la lógica de negocio siguiendo principios SOLID y Clean Code.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TestimonialService {

    private final TestimonialRepository testimonialRepository;

    /**
     * Obtiene todos los testimonios aprobados
     */
    @Transactional(readOnly = true)
    public List<TestimonialResponse> getAllApprovedTestimonials() {
        log.info("Fetching all approved testimonials");
        return testimonialRepository.findByApprovedTrueOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene testimonios destacados para homepage
     */
    @Transactional(readOnly = true)
    public List<TestimonialResponse> getFeaturedTestimonials() {
        log.info("Fetching featured testimonials");
        return testimonialRepository.findByApprovedTrueAndFeaturedTrueOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los testimonios (admin)
     */
    @Transactional(readOnly = true)
    public List<TestimonialResponse> getAllTestimonials() {
        log.info("Fetching all testimonials (admin view)");
        return testimonialRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los testimonios paginados (admin)
     */
    @Transactional(readOnly = true)
    public Page<TestimonialResponse> getAllTestimonialsPaged(Pageable pageable) {
        log.info("Fetching all testimonials paged (admin view)");
        Page<Testimonial> testimonials = testimonialRepository.findAll(pageable);
        return testimonials.map(this::mapToResponse);
    }

    /**
     * Obtiene testimonios pendientes de aprobación
     */
    @Transactional(readOnly = true)
    public List<TestimonialResponse> getPendingTestimonials() {
        log.info("Fetching pending testimonials");
        return testimonialRepository.findByApprovedFalseOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un testimonio por ID
     */
    @Transactional(readOnly = true)
    public TestimonialResponse getTestimonialById(Long id) {
        log.info("Fetching testimonial with ID: {}", id);
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Testimonial not found with ID: " + id));
        return mapToResponse(testimonial);
    }

    /**
     * Crea un nuevo testimonio
     */
    @Transactional
    public TestimonialResponse createTestimonial(TestimonialRequest request) {
        log.info("Creating new testimonial for: {}", request.getName());

        validateTestimonialRequest(request);

        Testimonial testimonial = Testimonial.builder()
                .name(request.getName())
                .message(request.getMessage())
                .rating(request.getRating())
                .avatar(request.getAvatar())
                .location(request.getLocation())
                .approved(false) // Requiere aprobación por defecto
                .featured(false)
                .build();

        Testimonial saved = testimonialRepository.save(testimonial);
        log.info("Testimonial created successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    /**
     * Actualiza un testimonio existente
     */
    @Transactional
    public TestimonialResponse updateTestimonial(Long id, TestimonialRequest request) {
        log.info("Updating testimonial with ID: {}", id);

        validateTestimonialRequest(request);

        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Testimonial not found with ID: " + id));

        testimonial.setName(request.getName());
        testimonial.setMessage(request.getMessage());
        testimonial.setRating(request.getRating());
        testimonial.setAvatar(request.getAvatar());
        testimonial.setLocation(request.getLocation());

        Testimonial updated = testimonialRepository.save(testimonial);
        log.info("Testimonial updated successfully: {}", id);

        return mapToResponse(updated);
    }

    /**
     * Elimina un testimonio
     */
    @Transactional
    public void deleteTestimonial(Long id) {
        log.info("Deleting testimonial with ID: {}", id);

        if (!testimonialRepository.existsById(id)) {
            throw new IllegalArgumentException("Testimonial not found with ID: " + id);
        }

        testimonialRepository.deleteById(id);
        log.info("Testimonial deleted successfully: {}", id);
    }

    /**
     * Aprueba un testimonio
     */
    @Transactional
    public TestimonialResponse approveTestimonial(Long id) {
        log.info("Approving testimonial with ID: {}", id);

        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Testimonial not found with ID: " + id));

        testimonial.approve();
        Testimonial saved = testimonialRepository.save(testimonial);

        log.info("Testimonial approved successfully: {}", id);
        return mapToResponse(saved);
    }

    /**
     * Rechaza un testimonio
     */
    @Transactional
    public TestimonialResponse rejectTestimonial(Long id) {
        log.info("Rejecting testimonial with ID: {}", id);

        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Testimonial not found with ID: " + id));

        testimonial.reject();
        Testimonial saved = testimonialRepository.save(testimonial);

        log.info("Testimonial rejected successfully: {}", id);
        return mapToResponse(saved);
    }

    /**
     * Marca/desmarca un testimonio como destacado
     */
    @Transactional
    public TestimonialResponse toggleFeatured(Long id) {
        log.info("Toggling featured status for testimonial with ID: {}", id);

        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Testimonial not found with ID: " + id));

        if (testimonial.isFeatured()) {
            testimonial.unmarkAsFeatured();
            log.info("Testimonial unmarked as featured: {}", id);
        } else {
            testimonial.markAsFeatured();
            log.info("Testimonial marked as featured: {}", id);
        }

        Testimonial saved = testimonialRepository.save(testimonial);
        return mapToResponse(saved);
    }

    /**
     * Obtiene estadísticas de testimonios
     */
    @Transactional(readOnly = true)
    public TestimonialStats getStats() {
        log.info("Fetching testimonial statistics");
        
        long totalApproved = testimonialRepository.countByApprovedTrue();
        long totalPending = testimonialRepository.countByApprovedFalse();
        long totalFeatured = testimonialRepository.findByApprovedTrueAndFeaturedTrueOrderByCreatedAtDesc().size();

        return TestimonialStats.builder()
                .totalApproved(totalApproved)
                .totalPending(totalPending)
                .totalFeatured(totalFeatured)
                .total(totalApproved + totalPending)
                .build();
    }

    // Helper methods

    /**
     * Mapea una entidad Testimonial a su DTO de respuesta
     */
    private TestimonialResponse mapToResponse(Testimonial testimonial) {
        return TestimonialResponse.builder()
                .id(testimonial.getId())
                .name(testimonial.getName())
                .message(testimonial.getMessage())
                .rating(testimonial.getRating())
                .avatar(testimonial.getAvatar())
                .location(testimonial.getLocation())
                .approved(testimonial.getApproved())
                .featured(testimonial.getFeatured())
                .createdAt(testimonial.getCreatedAt())
                .updatedAt(testimonial.getUpdatedAt())
                .build();
    }

    /**
     * Valida el request de testimonio
     */
    private void validateTestimonialRequest(TestimonialRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
    }

    /**
     * Clase interna para estadísticas de testimonios
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TestimonialStats {
        private long total;
        private long totalApproved;
        private long totalPending;
        private long totalFeatured;
    }
}
