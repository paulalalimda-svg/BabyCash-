package com.babycash.backend.service;

import com.babycash.backend.dto.LoyaltyPointsResponse;
import com.babycash.backend.dto.LoyaltyTransactionResponse;
import com.babycash.backend.model.entity.Order;
import com.babycash.backend.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Interface for Loyalty Service
 * Dependency Inversion Principle - depend on abstractions, not concretions
 * Allows for multiple implementations and easier testing
 */
public interface ILoyaltyService {

    /**
     * Award points to a user based on purchase amount
     * @param user User to award points to
     * @param order Order associated with the points
     * @param amount Purchase amount
     * @return Points awarded
     */
    Integer awardPointsForPurchase(User user, Order order, BigDecimal amount);

    /**
     * Redeem points for a user
     * @param user User redeeming points
     * @param points Points to redeem
     * @return Success status
     */
    boolean redeemPoints(User user, Integer points);

    /**
     * Get loyalty points summary for a user
     * @param user User to get summary for
     * @return Loyalty points summary
     */
    LoyaltyPointsResponse getUserLoyaltyPoints(User user);

    /**
     * Get paginated transaction history for a user
     * @param user User to get history for
     * @param pageable Pagination parameters
     * @return Page of transactions
     */
    Page<LoyaltyTransactionResponse> getUserTransactionHistory(User user, Pageable pageable);

    /**
     * Process expired points (scheduled job)
     */
    void processExpiredPoints();
}
