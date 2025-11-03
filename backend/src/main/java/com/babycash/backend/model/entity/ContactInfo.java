package com.babycash.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad singleton para información de contacto de la empresa.
 * Solo debe existir un registro en la base de datos.
 */
@Entity
@Table(name = "contact_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Información básica
    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String country;

    // Redes sociales
    @Column(length = 200)
    private String facebook;

    @Column(length = 200)
    private String instagram;

    @Column(length = 200)
    private String twitter;

    @Column(length = 200)
    private String whatsapp;

    // Horarios
    @Column(length = 100)
    private String businessHours;

    @Column(columnDefinition = "TEXT")
    private String businessHoursDetails;

    // Coordenadas para mapa
    @Column
    private Double latitude;

    @Column
    private Double longitude;

    // Información adicional
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

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

    // Business logic methods

    /**
     * Valida que la información de contacto esté completa
     */
    public boolean isComplete() {
        return companyName != null && !companyName.trim().isEmpty()
                && phone != null && !phone.trim().isEmpty()
                && email != null && !email.trim().isEmpty()
                && address != null && !address.trim().isEmpty();
    }

    /**
     * Verifica si tiene redes sociales configuradas
     */
    public boolean hasSocialMedia() {
        return (facebook != null && !facebook.trim().isEmpty())
                || (instagram != null && !instagram.trim().isEmpty())
                || (twitter != null && !twitter.trim().isEmpty())
                || (whatsapp != null && !whatsapp.trim().isEmpty());
    }

    /**
     * Verifica si tiene coordenadas para mapa
     */
    public boolean hasMapCoordinates() {
        return latitude != null && longitude != null;
    }
}
