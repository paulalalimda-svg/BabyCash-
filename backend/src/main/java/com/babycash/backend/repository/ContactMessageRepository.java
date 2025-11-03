package com.babycash.backend.repository;

import com.babycash.backend.model.entity.ContactMessage;
import com.babycash.backend.model.entity.ContactMessage.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para mensajes de contacto
 */
@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    /**
     * Encuentra mensajes por estado
     */
    List<ContactMessage> findByStatusOrderByCreatedAtDesc(MessageStatus status);

    /**
     * Encuentra mensajes nuevos (no leídos)
     */
    List<ContactMessage> findByStatusOrderByCreatedAtDesc(MessageStatus status, org.springframework.data.domain.Pageable pageable);

    /**
     * Cuenta mensajes por estado
     */
    long countByStatus(MessageStatus status);

    /**
     * Encuentra mensajes recientes
     */
    @Query("SELECT cm FROM ContactMessage cm WHERE cm.createdAt >= :since ORDER BY cm.createdAt DESC")
    List<ContactMessage> findRecentMessages(LocalDateTime since);

    /**
     * Encuentra mensajes por email
     */
    List<ContactMessage> findByEmailOrderByCreatedAtDesc(String email);

    /**
     * Encuentra todos los mensajes ordenados por fecha
     */
    List<ContactMessage> findAllByOrderByCreatedAtDesc();

    /**
     * Cuenta mensajes nuevos
     */
    @Query("SELECT COUNT(cm) FROM ContactMessage cm WHERE cm.status = 'NEW'")
    long countNewMessages();
}
