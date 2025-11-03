package com.babycash.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Request DTO for creating/updating blog posts
 * Validation with Jakarta Bean Validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    private String title;

    @Size(max = 500, message = "El extracto no puede exceder 500 caracteres")
    private String excerpt;

    @NotBlank(message = "El contenido es obligatorio")
    @Size(min = 50, message = "El contenido debe tener al menos 50 caracteres")
    private String content;

    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imageUrl;

    @Builder.Default
    private Boolean published = false;

    @Builder.Default
    private Boolean featured = false;

    @Builder.Default
    private List<String> tags = new ArrayList<>();
}
