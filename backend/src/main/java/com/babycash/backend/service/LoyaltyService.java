package com.babycash.backend.service;

import com.babycash.backend.dto.LoyaltyPointsResponse;
import com.babycash.backend.dto.LoyaltyTransactionResponse;
import com.babycash.backend.model.entity.LoyaltyPoint;
import com.babycash.backend.model.entity.Order;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.LoyaltyTransactionType;
import com.babycash.backend.repository.LoyaltyPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * Service for managing loyalty points
 * Single Responsibility: All loyalty business logic in one place
 * Clean Code: Clear method names, focused responsibilities
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyService implements ILoyaltyService {

    private final LoyaltyPointRepository loyaltyPointRepository;

    // Business rule: 1 point per $1000 COP spent
    private static final BigDecimal THOUSAND = new BigDecimal("1000");

    // Points expire after 1 year
    private static final int EXPIRATION_MONTHS = 12;

    // Tier thresholds
    private static final int SILVER_THRESHOLD = 1000;
    private static final int GOLD_THRESHOLD = 5000;

    /**
     * Award points to a user based on purchase amount
     * Calculates points, creates transaction, sets expiration
     */
    @Override
    @Transactional
    public Integer awardPointsForPurchase(User user, Order order, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid amount for loyalty points: {}", amount);
            return 0;
        }

        // Calculate points: amount / 1000 (rounded down)
        Integer pointsToAward = amount.divide(THOUSAND, 0, java.math.RoundingMode.DOWN).intValue();

        if (pointsToAward > 0) {
            LoyaltyPoint loyaltyPoint = LoyaltyPoint.builder()
                    .user(user)
                    .transactionType(LoyaltyTransactionType.EARNED)
                    .points(pointsToAward)
                    .amountSpent(amount)
                    .order(order)
                    .description(String.format("Puntos ganados por compra #%s", order.getOrderNumber()))
                    .expiresAt(LocalDateTime.now().plusMonths(EXPIRATION_MONTHS))
                    .active(true)
                    .build();

            loyaltyPointRepository.save(loyaltyPoint);
            log.info("Awarded {} points to user {} for order {}", pointsToAward, user.getEmail(), order.getOrderNumber());
        }

        return pointsToAward;
    }

    /**
     * Redeem points for a user
     * Validates balance, creates negative transaction
     */
    @Override
    @Transactional
    public boolean redeemPoints(User user, Integer points) {
        if (points == null || points <= 0) {
            log.warn("Invalid points to redeem: {}", points);
            return false;
        }

        // Check if user has enough points
        Integer currentBalance = loyaltyPointRepository.calculateTotalActivePoints(user, LocalDateTime.now());
        if (currentBalance < points) {
            log.warn("Insufficient points. User {} has {} but tried to redeem {}", 
                    user.getEmail(), currentBalance, points);
            return false;
        }

        // Create redemption transaction (negative points)
        LoyaltyPoint redemption = LoyaltyPoint.builder()
                .user(user)
                .transactionType(LoyaltyTransactionType.REDEEMED)
                .points(-points)
                .description("Puntos canjeados por recompensa")
                .active(true)
                .build();

        loyaltyPointRepository.save(redemption);
        log.info("User {} redeemed {} points. New balance: {}", user.getEmail(), points, currentBalance - points);

        return true;
    }

    /**
     * Get loyalty points summary for a user
     * Calculates all statistics and tier
     */
    @Override
    @Transactional(readOnly = true)
    public LoyaltyPointsResponse getUserLoyaltyPoints(User user) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get total active points
        Integer totalPoints = loyaltyPointRepository.calculateTotalActivePoints(user, now);

        // Get all transactions for calculations
        List<LoyaltyPoint> allTransactions = loyaltyPointRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged()).getContent();

        // Calculate earned total
        Integer earnedTotal = allTransactions.stream()
                .filter(lp -> lp.getTransactionType() == LoyaltyTransactionType.EARNED || 
                             lp.getTransactionType() == LoyaltyTransactionType.BONUS)
                .mapToInt(LoyaltyPoint::getPoints)
                .sum();

        // Calculate earned this month
        YearMonth currentMonth = YearMonth.now();
        Integer earnedThisMonth = allTransactions.stream()
                .filter(lp -> (lp.getTransactionType() == LoyaltyTransactionType.EARNED || 
                              lp.getTransactionType() == LoyaltyTransactionType.BONUS))
                .filter(lp -> {
                    YearMonth txMonth = YearMonth.from(lp.getCreatedAt());
                    return txMonth.equals(currentMonth);
                })
                .mapToInt(LoyaltyPoint::getPoints)
                .sum();

        // Calculate redeemed total
        Integer redeemedTotal = allTransactions.stream()
                .filter(lp -> lp.getTransactionType() == LoyaltyTransactionType.REDEEMED)
                .mapToInt(lp -> Math.abs(lp.getPoints()))
                .sum();

        // Calculate expiring soon (next 30 days)
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);
        Integer expiringSoon = allTransactions.stream()
                .filter(lp -> lp.getActive())
                .filter(lp -> lp.getExpiresAt() != null && 
                             lp.getExpiresAt().isAfter(now) && 
                             lp.getExpiresAt().isBefore(thirtyDaysFromNow))
                .mapToInt(LoyaltyPoint::getPoints)
                .sum();

        // Calculate tier
        String tier = calculateTier(earnedTotal);

        // Get member since
        String memberSince = String.valueOf(user.getCreatedAt().getYear());

        // Calculate available discount: 5% per 1000 points (max 50%)
        Integer availableDiscountPercent = calculateDiscountPercent(totalPoints);
        
        // Calculate points needed for next discount tier
        Integer pointsForNextDiscount = calculatePointsForNextDiscount(totalPoints);

        return LoyaltyPointsResponse.builder()
                .totalPoints(totalPoints)
                .earnedThisMonth(earnedThisMonth)
                .earnedTotal(earnedTotal)
                .redeemedTotal(redeemedTotal)
                .expiringSoon(expiringSoon)
                .memberSince(memberSince)
                .tier(tier)
                .availableDiscountPercent(availableDiscountPercent)
                .pointsForNextDiscount(pointsForNextDiscount)
                .build();
    }

    /**
     * Get paginated transaction history for a user
     * Maps entity to DTO with computed fields
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LoyaltyTransactionResponse> getUserTransactionHistory(User user, Pageable pageable) {
        Page<LoyaltyPoint> transactions = loyaltyPointRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        return transactions.map(this::mapToTransactionResponse);
    }

    /**
     * Process expired points (scheduled job)
     * Finds expired active points, deactivates them, creates audit records
     */
    @Override
    @Transactional
    public void processExpiredPoints() {
        LocalDateTime now = LocalDateTime.now();
        List<LoyaltyPoint> expiredPoints = loyaltyPointRepository.findExpiredActivePoints(now);

        log.info("Processing {} expired loyalty points", expiredPoints.size());

        for (LoyaltyPoint expiredPoint : expiredPoints) {
            // Deactivate the point
            expiredPoint.deactivate();
            loyaltyPointRepository.save(expiredPoint);

            // Create audit record
            LoyaltyPoint expirationRecord = LoyaltyPoint.builder()
                    .user(expiredPoint.getUser())
                    .transactionType(LoyaltyTransactionType.EXPIRED)
                    .points(-expiredPoint.getPoints())
                    .description(String.format("Puntos expirados de transacción #%d", expiredPoint.getId()))
                    .active(true)
                    .build();

            loyaltyPointRepository.save(expirationRecord);
        }

        log.info("Expired {} loyalty point transactions", expiredPoints.size());
    }

    /**
     * Calculate tier based on total earned points
     * Business rule: BRONZE < 1000, SILVER < 5000, GOLD >= 5000
     */
    private String calculateTier(Integer totalEarned) {
        if (totalEarned >= GOLD_THRESHOLD) {
            return "GOLD";
        } else if (totalEarned >= SILVER_THRESHOLD) {
            return "SILVER";
        } else {
            return "BRONZE";
        }
    }

    /**
     * Calculate available discount percentage
     * Business rule: 5% per 1000 points, max 50% (10,000 points)
     */
    private Integer calculateDiscountPercent(Integer totalPoints) {
        if (totalPoints == null || totalPoints < 1000) {
            return 0;
        }
        
        // 5% por cada 1000 puntos
        int discountTiers = totalPoints / 1000;
        int discountPercent = discountTiers * 5;
        
        // Máximo 50% de descuento
        return Math.min(discountPercent, 50);
    }

    /**
     * Calculate points needed to reach next discount tier
     * Returns 0 if already at max discount (50%)
     */
    private Integer calculatePointsForNextDiscount(Integer totalPoints) {
        if (totalPoints == null) {
            return 1000; // First tier
        }
        
        // Si ya tiene 10,000+ puntos (50% descuento), ya llegó al máximo
        if (totalPoints >= 10000) {
            return 0;
        }
        
        // Calcular puntos para el siguiente múltiplo de 1000
        int nextTier = ((totalPoints / 1000) + 1) * 1000;
        return nextTier - totalPoints;
    }

    /**
     * Map LoyaltyPoint entity to LoyaltyTransactionResponse DTO
     * Includes computed fields for frontend convenience
     */
    private LoyaltyTransactionResponse mapToTransactionResponse(LoyaltyPoint loyaltyPoint) {
        return LoyaltyTransactionResponse.builder()
                .id(loyaltyPoint.getId())
                .transactionType(loyaltyPoint.getTransactionType())
                .transactionTypeName(loyaltyPoint.getTransactionType().getDisplayName())
                .points(loyaltyPoint.getPoints())
                .amountSpent(loyaltyPoint.getAmountSpent())
                .orderId(loyaltyPoint.getOrder() != null ? loyaltyPoint.getOrder().getId() : null)
                .orderNumber(loyaltyPoint.getOrder() != null ? loyaltyPoint.getOrder().getOrderNumber() : null)
                .description(loyaltyPoint.getDescription())
                .createdAt(loyaltyPoint.getCreatedAt())
                .expiresAt(loyaltyPoint.getExpiresAt())
                .active(loyaltyPoint.getActive())
                .expired(loyaltyPoint.isExpired())
                .build();
    }
}
