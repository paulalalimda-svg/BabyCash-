package com.babycash.backend.dto.contact;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar información de contacto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfoRequest {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String companyName;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9+\\-() ]{7,20}$", message = "Formato de teléfono inválido")
    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String city;

    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String country;

    @Size(max = 200, message = "URL de Facebook no puede exceder 200 caracteres")
    private String facebook;

    @Size(max = 200, message = "URL de Instagram no puede exceder 200 caracteres")
    private String instagram;

    @Size(max = 200, message = "URL de Twitter no puede exceder 200 caracteres")
    private String twitter;

    @Size(max = 200, message = "Número de WhatsApp no puede exceder 200 caracteres")
    private String whatsapp;

    @Size(max = 100, message = "Horario no puede exceder 100 caracteres")
    private String businessHours;

    private String businessHoursDetails;

    @DecimalMin(value = "-90.0", message = "Latitud mínima es -90")
    @DecimalMax(value = "90.0", message = "Latitud máxima es 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitud mínima es -180")
    @DecimalMax(value = "180.0", message = "Longitud máxima es 180")
    private Double longitude;

    private String description;
}
