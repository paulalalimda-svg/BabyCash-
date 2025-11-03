package com.babycash.backend.dto;

import com.babycash.backend.model.enums.LoyaltyTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for loyalty transaction history
 * Immutable data transfer object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyTransactionResponse {
    private Long id;
    private LoyaltyTransactionType transactionType;
    private String transactionTypeName;
    private Integer points;
    private BigDecimal amountSpent;
    private Long orderId;
    private String orderNumber;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean active;
    private Boolean expired;
}
