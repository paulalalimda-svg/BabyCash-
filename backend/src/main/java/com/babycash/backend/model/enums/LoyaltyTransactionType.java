package com.babycash.backend.model.enums;

/**
 * Types of loyalty point transactions
 * Open/Closed Principle - easy to extend with new types
 */
public enum LoyaltyTransactionType {
    EARNED("Ganados"),
    REDEEMED("Canjeados"),
    EXPIRED("Expirados"),
    BONUS("Bono"),
    REFUND("Reembolso");

    private final String displayName;

    LoyaltyTransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
