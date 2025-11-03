package com.babycash.backend.repository;

import com.babycash.backend.model.entity.LoyaltyPoint;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.LoyaltyTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for LoyaltyPoint entity
 * Interface Segregation Principle - only methods needed
 */
@Repository
public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Long> {

    /**
     * Find all active points for a user ordered by creation date
     */
    List<LoyaltyPoint> findByUserAndActiveOrderByCreatedAtDesc(User user, Boolean active);

    /**
     * Find paginated history for a user
     */
    Page<LoyaltyPoint> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * Calculate total active points for a user
     */
    @Query("SELECT COALESCE(SUM(lp.points), 0) FROM LoyaltyPoint lp " +
           "WHERE lp.user = :user " +
           "AND lp.active = true " +
           "AND (lp.expiresAt IS NULL OR lp.expiresAt > :now)")
    Integer calculateTotalActivePoints(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Find expired but still active points (for cleanup job)
     */
    @Query("SELECT lp FROM LoyaltyPoint lp " +
           "WHERE lp.active = true " +
           "AND lp.expiresAt IS NOT NULL " +
           "AND lp.expiresAt <= :now")
    List<LoyaltyPoint> findExpiredActivePoints(@Param("now") LocalDateTime now);

    /**
     * Find points by transaction type for a user
     */
    List<LoyaltyPoint> findByUserAndTransactionTypeOrderByCreatedAtDesc(
            User user, 
            LoyaltyTransactionType transactionType
    );

    /**
     * Count transactions by type for a user
     */
    long countByUserAndTransactionType(User user, LoyaltyTransactionType transactionType);
}
