package com.babycash.backend.service;

import com.babycash.backend.dto.contact.ContactInfoRequest;
import com.babycash.backend.dto.contact.ContactInfoResponse;
import com.babycash.backend.model.entity.ContactInfo;
import com.babycash.backend.repository.ContactInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestión de información de contacto.
 * Implementa patrón Singleton para la información de contacto.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ContactInfoService {

    private final ContactInfoRepository contactInfoRepository;

    /**
     * Obtiene la información de contacto actual
     * Si no existe, crea una con valores por defecto
     */
    @Transactional(readOnly = true)
    public ContactInfoResponse getContactInfo() {
        log.info("Fetching contact information");
        
        ContactInfo contactInfo = contactInfoRepository.findFirst()
                .orElseGet(this::createDefaultContactInfo);
        
        return mapToResponse(contactInfo);
    }

    /**
     * Actualiza la información de contacto
     * Si no existe, la crea
     */
    @Transactional
    public ContactInfoResponse updateContactInfo(ContactInfoRequest request) {
        log.info("Updating contact information");
        
        ContactInfo contactInfo = contactInfoRepository.findFirst()
                .orElseGet(() -> ContactInfo.builder().build());

        // Actualizar todos los campos
        contactInfo.setCompanyName(request.getCompanyName());
        contactInfo.setPhone(request.getPhone());
        contactInfo.setEmail(request.getEmail());
        contactInfo.setAddress(request.getAddress());
        contactInfo.setCity(request.getCity());
        contactInfo.setCountry(request.getCountry());
        contactInfo.setFacebook(request.getFacebook());
        contactInfo.setInstagram(request.getInstagram());
        contactInfo.setTwitter(request.getTwitter());
        contactInfo.setWhatsapp(request.getWhatsapp());
        contactInfo.setBusinessHours(request.getBusinessHours());
        contactInfo.setBusinessHoursDetails(request.getBusinessHoursDetails());
        contactInfo.setLatitude(request.getLatitude());
        contactInfo.setLongitude(request.getLongitude());
        contactInfo.setDescription(request.getDescription());

        ContactInfo saved = contactInfoRepository.save(contactInfo);
        log.info("Contact information updated successfully");

        return mapToResponse(saved);
    }

    /**
     * Verifica si la información de contacto está configurada
     */
    @Transactional(readOnly = true)
    public boolean isConfigured() {
        return contactInfoRepository.findFirst()
                .map(ContactInfo::isComplete)
                .orElse(false);
    }

    // Helper methods

    /**
     * Crea información de contacto por defecto
     */
    private ContactInfo createDefaultContactInfo() {
        log.info("Creating default contact information");
        
        ContactInfo defaultInfo = ContactInfo.builder()
                .companyName("Baby Cash")
                .phone("+57 300 000 0000")
                .email("contacto@babycash.com")
                .address("Calle 123 #45-67")
                .city("Bogotá")
                .country("Colombia")
                .businessHours("Lun - Vie: 8:00 AM - 6:00 PM")
                .businessHoursDetails("Sábados: 9:00 AM - 1:00 PM")
                .description("Tienda especializada en productos para bebés")
                .build();

        return contactInfoRepository.save(defaultInfo);
    }

    /**
     * Mapea entidad a DTO de respuesta
     */
    private ContactInfoResponse mapToResponse(ContactInfo contactInfo) {
        return ContactInfoResponse.builder()
                .id(contactInfo.getId())
                .companyName(contactInfo.getCompanyName())
                .phone(contactInfo.getPhone())
                .email(contactInfo.getEmail())
                .address(contactInfo.getAddress())
                .city(contactInfo.getCity())
                .country(contactInfo.getCountry())
                .facebook(contactInfo.getFacebook())
                .instagram(contactInfo.getInstagram())
                .twitter(contactInfo.getTwitter())
                .whatsapp(contactInfo.getWhatsapp())
                .businessHours(contactInfo.getBusinessHours())
                .businessHoursDetails(contactInfo.getBusinessHoursDetails())
                .latitude(contactInfo.getLatitude())
                .longitude(contactInfo.getLongitude())
                .description(contactInfo.getDescription())
                .createdAt(contactInfo.getCreatedAt())
                .updatedAt(contactInfo.getUpdatedAt())
                .build();
    }
}
