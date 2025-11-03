package com.babycash.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private Long totalOrders;
    private Long totalProducts;
    private Double totalSpent;
    private String memberSince;
}
