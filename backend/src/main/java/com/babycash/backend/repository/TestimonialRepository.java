package com.babycash.backend.repository;

import com.babycash.backend.model.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestión de testimonios.
 * Proporciona métodos de consulta optimizados para diferentes casos de uso.
 */
@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {

    /**
     * Obtiene todos los testimonios aprobados ordenados por fecha de creación (más recientes primero)
     */
    List<Testimonial> findByApprovedTrueOrderByCreatedAtDesc();

    /**
     * Obtiene testimonios destacados y aprobados para mostrar en homepage
     */
    List<Testimonial> findByApprovedTrueAndFeaturedTrueOrderByCreatedAtDesc();

    /**
     * Obtiene testimonios pendientes de aprobación
     */
    List<Testimonial> findByApprovedFalseOrderByCreatedAtDesc();

    /**
     * Cuenta testimonios aprobados
     */
    long countByApprovedTrue();

    /**
     * Cuenta testimonios pendientes de aprobación
     */
    long countByApprovedFalse();

    /**
     * Obtiene testimonios aprobados con rating específico
     */
    List<Testimonial> findByApprovedTrueAndRatingOrderByCreatedAtDesc(Integer rating);

    /**
     * Obtiene los últimos N testimonios aprobados
     */
    @Query("SELECT t FROM Testimonial t WHERE t.approved = true ORDER BY t.createdAt DESC")
    List<Testimonial> findLatestApproved();

    /**
     * Obtiene testimonios aprobados con rating mayor o igual al especificado
     */
    @Query("SELECT t FROM Testimonial t WHERE t.approved = true AND t.rating >= :minRating ORDER BY t.rating DESC, t.createdAt DESC")
    List<Testimonial> findByApprovedTrueAndRatingGreaterThanEqual(Integer minRating);
}
