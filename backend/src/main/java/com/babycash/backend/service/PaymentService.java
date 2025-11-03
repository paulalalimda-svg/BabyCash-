package com.babycash.backend.service;

import com.babycash.backend.dto.request.ProcessPaymentRequest;
import com.babycash.backend.dto.response.PaymentResponse;
import com.babycash.backend.exception.custom.BusinessException;
import com.babycash.backend.exception.custom.ResourceNotFoundException;
import com.babycash.backend.model.entity.Order;
import com.babycash.backend.model.entity.Payment;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.OrderStatus;
import com.babycash.backend.model.enums.PaymentStatus;
import com.babycash.backend.repository.OrderRepository;
import com.babycash.backend.repository.PaymentRepository;
import com.babycash.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Payment service - Simulated for demo purposes
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        User user = getCurrentUser();
        
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Access denied");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Order cannot be paid in status: " + order.getStatus());
        }

        // Simulate payment processing (for demo - always succeeds)
        String transactionId = "TXN-" + UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .order(order)
                .method(request.getPaymentMethod())
                .status(PaymentStatus.COMPLETED)
                .amount(order.getTotalAmount())
                .transactionId(transactionId)
                .metadata("{\"cardLast4\":\"****\",\"demo\":true}")
                .build();

        payment = paymentRepository.save(payment);

        // Update order status
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);

        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        User user = getCurrentUser();
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Access denied");
        }

        Payment payment = paymentRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .orderNumber(payment.getOrder().getOrderNumber())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
