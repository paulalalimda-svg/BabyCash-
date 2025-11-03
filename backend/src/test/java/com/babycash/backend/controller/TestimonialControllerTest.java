package com.babycash.backend.controller;

import com.babycash.backend.dto.testimonial.TestimonialRequest;
import com.babycash.backend.model.entity.Testimonial;
import com.babycash.backend.repository.TestimonialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para TestimonialController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TestimonialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestimonialRepository testimonialRepository;

    @BeforeEach
    void setUp() {
        testimonialRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/testimonials - Debe retornar lista vacía si no hay testimonios aprobados")
    void shouldReturnEmptyListWhenNoApprovedTestimonials() throws Exception {
        mockMvc.perform(get("/api/testimonials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/testimonials - Debe retornar solo testimonios aprobados")
    void shouldReturnOnlyApprovedTestimonials() throws Exception {
        // Given
        Testimonial approved = Testimonial.builder()
                .name("María López")
                .message("Excelente servicio")
                .rating(5)
                .approved(true)
                .build();
        
        Testimonial pending = Testimonial.builder()
                .name("Juan Pérez")
                .message("Muy buena atención")
                .rating(4)
                .approved(false)
                .build();
        
        testimonialRepository.save(approved);
        testimonialRepository.save(pending);

        // When & Then
        mockMvc.perform(get("/api/testimonials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("María López"))
                .andExpect(jsonPath("$[0].approved").value(true));
    }

    @Test
    @DisplayName("POST /api/testimonials - Debe crear testimonio (pendiente de aprobación)")
    void shouldCreateTestimonialPendingApproval() throws Exception {
        // Given
        TestimonialRequest request = TestimonialRequest.builder()
                .name("Ana García")
                .message("Los productos son de excelente calidad y la atención es muy buena.")
                .rating(5)
                .location("Bogotá, Colombia")
                .build();

        // When & Then
        mockMvc.perform(post("/api/testimonials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ana García"))
                .andExpect(jsonPath("$.message").value(containsString("excelente calidad")))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.approved").value(false))
                .andExpect(jsonPath("$.featured").value(false));
    }

    @Test
    @DisplayName("POST /api/testimonials - Debe validar rating entre 1 y 5")
    void shouldValidateRatingRange() throws Exception {
        // Given - Rating inválido (6)
        TestimonialRequest request = TestimonialRequest.builder()
                .name("Test User")
                .message("Test message with at least 10 characters")
                .rating(6) // Inválido
                .build();

        // When & Then
        mockMvc.perform(post("/api/testimonials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/testimonials - Debe validar longitud mínima del mensaje")
    void shouldValidateMessageMinLength() throws Exception {
        // Given
        TestimonialRequest request = TestimonialRequest.builder()
                .name("Test User")
                .message("Short") // Menos de 10 caracteres
                .rating(5)
                .build();

        // When & Then
        mockMvc.perform(post("/api/testimonials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/testimonials/featured - Debe retornar solo testimonios destacados")
    void shouldReturnOnlyFeaturedTestimonials() throws Exception {
        // Given
        Testimonial featured = Testimonial.builder()
                .name("Cliente Destacado")
                .message("Testimonio destacado muy positivo")
                .rating(5)
                .approved(true)
                .featured(true)
                .build();
        
        Testimonial regular = Testimonial.builder()
                .name("Cliente Regular")
                .message("Testimonio regular positivo")
                .rating(4)
                .approved(true)
                .featured(false)
                .build();
        
        testimonialRepository.save(featured);
        testimonialRepository.save(regular);

        // When & Then
        mockMvc.perform(get("/api/testimonials/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Cliente Destacado"))
                .andExpect(jsonPath("$[0].featured").value(true));
    }

    @Test
    @DisplayName("GET /api/testimonials/{id} - Debe retornar testimonio aprobado por ID")
    void shouldReturnApprovedTestimonialById() throws Exception {
        // Given
        Testimonial testimonial = Testimonial.builder()
                .name("Test User")
                .message("Test message content")
                .rating(5)
                .approved(true)
                .build();
        Testimonial saved = testimonialRepository.save(testimonial);

        // When & Then
        mockMvc.perform(get("/api/testimonials/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @DisplayName("GET /api/testimonials/admin/all - Debe requerir autenticación")
    void shouldRequireAuthForAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/testimonials/admin/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/testimonials - Debe aceptar campos opcionales")
    void shouldAcceptOptionalFields() throws Exception {
        // Given - Sin location ni avatar
        TestimonialRequest request = TestimonialRequest.builder()
                .name("Simple User")
                .message("Simple testimonial message without optional fields")
                .rating(4)
                .build();

        // When & Then
        mockMvc.perform(post("/api/testimonials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Simple User"))
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    @DisplayName("POST /api/testimonials - Debe validar campos requeridos")
    void shouldValidateRequiredFields() throws Exception {
        // Given - Sin campos requeridos
        TestimonialRequest request = TestimonialRequest.builder()
                .build();

        // When & Then
        mockMvc.perform(post("/api/testimonials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
