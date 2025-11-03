package com.babycash.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for loyalty points summary
 * Data Transfer Object Pattern - decouples API from domain model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPointsResponse {
    private Integer totalPoints;
    private Integer earnedThisMonth;
    private Integer earnedTotal;
    private Integer redeemedTotal;
    private Integer expiringSoon; // Points expiring in next 30 days
    private String memberSince;
    private String tier; // BRONZE, SILVER, GOLD based on total earned
    private Integer availableDiscountPercent; // 5% per 1000 points (max 50%)
    private Integer pointsForNextDiscount; // Points needed for next 5% tier
}
