package com.babycash.backend.dto.response;

import com.babycash.backend.model.enums.PaymentMethod;
import com.babycash.backend.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private PaymentMethod method;
    private PaymentStatus status;
    private BigDecimal amount;
    private String transactionId;
    private LocalDateTime createdAt;
}
