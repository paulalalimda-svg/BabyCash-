package com.babycash.backend.repository;

import com.babycash.backend.model.entity.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para información de contacto.
 * Como es singleton, solo habrá un registro.
 */
@Repository
public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long> {

    /**
     * Obtiene la información de contacto (singleton)
     * Siempre retorna el primer y único registro
     */
    @Query("SELECT c FROM ContactInfo c ORDER BY c.id ASC")
    Optional<ContactInfo> findFirst();
}
