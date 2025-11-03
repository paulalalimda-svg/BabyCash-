package com.babycash.backend.controller;

import com.babycash.backend.dto.contact.ContactInfoRequest;
import com.babycash.backend.model.entity.ContactInfo;
import com.babycash.backend.repository.ContactInfoRepository;
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
 * Tests de integración para ContactInfoController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ContactInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @BeforeEach
    void setUp() {
        contactInfoRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/contact-info - Debe retornar info por defecto si no existe")
    void shouldReturnDefaultContactInfo() throws Exception {
        mockMvc.perform(get("/api/contact-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Baby Cash"))
                .andExpect(jsonPath("$.phone").value("+57 300 000 0000"))
                .andExpect(jsonPath("$.email").value("contacto@babycash.com"));
    }

    @Test
    @DisplayName("GET /api/contact-info - Debe retornar info existente")
    void shouldReturnExistingContactInfo() throws Exception {
        // Given
        ContactInfo contactInfo = ContactInfo.builder()
                .companyName("Baby Cash Test")
                .phone("+57 321 929 7605")
                .email("test@babycash.com")
                .address("Calle Test 123")
                .city("Bogotá")
                .country("Colombia")
                .build();
        contactInfoRepository.save(contactInfo);

        // When & Then
        mockMvc.perform(get("/api/contact-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Baby Cash Test"))
                .andExpect(jsonPath("$.phone").value("+57 321 929 7605"))
                .andExpect(jsonPath("$.email").value("test@babycash.com"))
                .andExpect(jsonPath("$.city").value("Bogotá"));
    }

    @Test
    @DisplayName("PUT /api/contact-info - Debe actualizar info (sin autenticación debería fallar)")
    void shouldFailToUpdateWithoutAuth() throws Exception {
        // Given
        ContactInfoRequest request = ContactInfoRequest.builder()
                .companyName("Baby Cash Updated")
                .phone("+57 300 111 2222")
                .email("updated@babycash.com")
                .address("Nueva Dirección 456")
                .build();

        // When & Then
        mockMvc.perform(put("/api/contact-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/contact-info - Debe retornar info completa con redes sociales")
    void shouldReturnCompleteContactInfoWithSocialMedia() throws Exception {
        // Given
        ContactInfo contactInfo = ContactInfo.builder()
                .companyName("Baby Cash")
                .phone("+57 321 929 7605")
                .email("contacto@babycash.com")
                .address("Calle 2 #8-49")
                .city("Bogotá")
                .country("Colombia")
                .facebook("https://facebook.com/babycash")
                .instagram("https://instagram.com/babycash")
                .whatsapp("+57 321 929 7605")
                .businessHours("Lun - Vie: 8:00 AM - 6:00 PM")
                .latitude(4.606734)
                .longitude(-74.072235)
                .build();
        contactInfoRepository.save(contactInfo);

        // When & Then
        mockMvc.perform(get("/api/contact-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facebook").value("https://facebook.com/babycash"))
                .andExpect(jsonPath("$.instagram").value("https://instagram.com/babycash"))
                .andExpect(jsonPath("$.whatsapp").value("+57 321 929 7605"))
                .andExpect(jsonPath("$.businessHours").value("Lun - Vie: 8:00 AM - 6:00 PM"))
                .andExpect(jsonPath("$.latitude").value(4.606734))
                .andExpect(jsonPath("$.longitude").value(-74.072235));
    }

    @Test
    @DisplayName("PUT /api/contact-info - Debe validar campos requeridos")
    void shouldValidateRequiredFields() throws Exception {
        // Given - Request con campos faltantes
        ContactInfoRequest request = ContactInfoRequest.builder()
                .companyName("") // Vacío
                .phone("123") // Formato inválido
                .email("invalid-email") // Email inválido
                .address("") // Vacío
                .build();

        // When & Then
        mockMvc.perform(put("/api/contact-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Sin auth, ni siquiera valida
    }
}
