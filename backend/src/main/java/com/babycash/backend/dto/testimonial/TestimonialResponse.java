package com.babycash.backend.dto.testimonial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para un testimonio.
 * Contiene toda la información necesaria para el cliente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialResponse {

    private Long id;
    private String name;
    private String message;
    private Integer rating;
    private String avatar;
    private String location;
    private Boolean approved;
    private Boolean featured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
