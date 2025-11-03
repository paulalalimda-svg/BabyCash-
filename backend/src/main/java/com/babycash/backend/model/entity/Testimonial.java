package com.babycash.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un testimonio de un cliente.
 * Almacena información sobre experiencias positivas de usuarios con la plataforma.
 */
@Entity
@Table(name = "testimonials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    @Builder.Default
    private Integer rating = 5; // Rating de 1 a 5

    @Column(length = 500)
    private String avatar; // URL del avatar

    @Column(length = 100)
    private String location; // Ciudad o ubicación del cliente

    @Column(nullable = false)
    @Builder.Default
    private Boolean approved = false; // Requiere aprobación del admin

    @Column(nullable = false)
    @Builder.Default
    private Boolean featured = false; // Destacado en homepage

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Validar rating
        if (this.rating < 1 || this.rating > 5) {
            this.rating = 5;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        // Validar rating
        if (this.rating < 1 || this.rating > 5) {
            this.rating = 5;
        }
    }

    // Business logic methods

    /**
     * Verifica si el testimonio está aprobado
     */
    public boolean isApproved() {
        return this.approved;
    }

    /**
     * Verifica si el testimonio está destacado
     */
    public boolean isFeatured() {
        return this.featured;
    }

    /**
     * Aprueba el testimonio
     */
    public void approve() {
        this.approved = true;
    }

    /**
     * Rechaza el testimonio
     */
    public void reject() {
        this.approved = false;
    }

    /**
     * Marca como destacado
     */
    public void markAsFeatured() {
        this.featured = true;
    }

    /**
     * Desmarca como destacado
     */
    public void unmarkAsFeatured() {
        this.featured = false;
    }

    /**
     * Valida que el rating esté en rango válido
     */
    public boolean hasValidRating() {
        return this.rating >= 1 && this.rating <= 5;
    }
}
