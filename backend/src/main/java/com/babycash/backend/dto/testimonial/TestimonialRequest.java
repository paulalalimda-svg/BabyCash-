package com.babycash.backend.dto.testimonial;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear o actualizar un testimonio.
 * Incluye validaciones según reglas de negocio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(min = 10, max = 1000, message = "El mensaje debe tener entre 10 y 1000 caracteres")
    private String message;

    @NotNull(message = "El rating es obligatorio")
    @Min(value = 1, message = "El rating mínimo es 1")
    @Max(value = 5, message = "El rating máximo es 5")
    private Integer rating;

    @Size(max = 500, message = "La URL del avatar no puede exceder 500 caracteres")
    private String avatar;

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    private String location;
}
