package com.babycash.backend.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para información de contacto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfoResponse {

    private Long id;
    private String companyName;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String country;
    private String facebook;
    private String instagram;
    private String twitter;
    private String whatsapp;
    private String businessHours;
    private String businessHoursDetails;
    private Double latitude;
    private Double longitude;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
