package com.babycash.backend.controller;

import com.babycash.backend.dto.contact.ContactMessageRequest;
import com.babycash.backend.model.entity.ContactMessage;
import com.babycash.backend.repository.ContactMessageRepository;
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
 * Tests de integración para ContactMessageController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ContactMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @BeforeEach
    void setUp() {
        contactMessageRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/contact/send - Debe enviar mensaje de contacto")
    void shouldSendContactMessage() throws Exception {
        // Given
        ContactMessageRequest request = ContactMessageRequest.builder()
                .name("Juan Pérez")
                .email("juan@example.com")
                .phone("+57 300 123 4567")
                .subject("Consulta sobre productos")
                .message("Hola, me gustaría saber más sobre sus productos para bebés.")
                .build();

        // When & Then
        mockMvc.perform(post("/api/contact/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.subject").value("Consulta sobre productos"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("POST /api/contact/send - Debe validar email inválido")
    void shouldValidateInvalidEmail() throws Exception {
        // Given
        ContactMessageRequest request = ContactMessageRequest.builder()
                .name("Test User")
                .email("invalid-email") // Email inválido
                .subject("Test Subject")
                .message("Test message content")
                .build();

        // When & Then
        mockMvc.perform(post("/api/contact/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/contact/send - Debe validar campos requeridos")
    void shouldValidateRequiredFields() throws Exception {
        // Given - Request con campos vacíos
        ContactMessageRequest request = ContactMessageRequest.builder()
                .name("") // Vacío
                .email("")
                .subject("")
                .message("")
                .build();

        // When & Then
        mockMvc.perform(post("/api/contact/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/contact/send - Debe validar longitud mínima del mensaje")
    void shouldValidateMessageMinLength() throws Exception {
        // Given
        ContactMessageRequest request = ContactMessageRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .subject("Test Subject")
                .message("Short") // Menos de 10 caracteres
                .build();

        // When & Then
        mockMvc.perform(post("/api/contact/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/contact/send - Debe aceptar mensaje sin teléfono (campo opcional)")
    void shouldAcceptMessageWithoutPhone() throws Exception {
        // Given
        ContactMessageRequest request = ContactMessageRequest.builder()
                .name("Ana García")
                .email("ana@example.com")
                .subject("Pregunta general")
                .message("Este es un mensaje de prueba sin número de teléfono.")
                .build();

        // When & Then
        mockMvc.perform(post("/api/contact/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phone").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/contact/admin/messages - Debe requerir autenticación")
    void shouldRequireAuthForAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/contact/admin/messages"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/contact/send - Debe capturar información de la petición")
    void shouldCaptureRequestInfo() throws Exception {
        // Given
        ContactMessageRequest request = ContactMessageRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .phone("+57 300 111 2222")
                .subject("Test Subject")
                .message("This is a test message to verify request info capture.")
                .build();

        // When & Then
        mockMvc.perform(post("/api/contact/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("User-Agent", "Mozilla/5.0 Test Browser"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ipAddress").exists());
    }

    @Test
    @DisplayName("POST /api/contact/send - Debe validar formato de teléfono")
    void shouldValidatePhoneFormat() throws Exception {
        // Given
        ContactMessageRequest request = ContactMessageRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .phone("abc123") // Formato inválido
                .subject("Test Subject")
                .message("Test message with invalid phone format")
                .build();

        // When & Then
        mockMvc.perform(post("/api/contact/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
