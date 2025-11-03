package com.babycash.backend.service;

import com.babycash.backend.dto.request.ProcessPaymentRequest;
import com.babycash.backend.dto.response.PaymentResponse;
import com.babycash.backend.exception.custom.BusinessException;
import com.babycash.backend.exception.custom.ResourceNotFoundException;
import com.babycash.backend.model.entity.Order;
import com.babycash.backend.model.entity.Payment;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.OrderStatus;
import com.babycash.backend.model.enums.PaymentMethod;
import com.babycash.backend.model.enums.PaymentStatus;
import com.babycash.backend.model.enums.UserRole;
import com.babycash.backend.repository.OrderRepository;
import com.babycash.backend.repository.PaymentRepository;
import com.babycash.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PaymentService paymentService;

    private User mockUser;
    private Order mockOrder;
    private Payment mockPayment;
    private ProcessPaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        // Setup mock user
        mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.USER)
                .enabled(true)
                .build();

        // Setup mock order
        mockOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-20251028-0001")
                .user(mockUser)
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("649.97"))
                .shippingAddress("123 Main St, City, Country")
                .createdAt(LocalDateTime.now())
                .build();

        // Setup mock payment
        mockPayment = Payment.builder()
                .id(1L)
                .order(mockOrder)
                .method(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .amount(new BigDecimal("649.97"))
                .transactionId("TXN-12345-ABCDE")
                .metadata("{\"cardLast4\":\"****\",\"demo\":true}")
                .createdAt(LocalDateTime.now())
                .build();

        // Setup payment request
        paymentRequest = new ProcessPaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
    }

    @Test
    @DisplayName("Should process payment successfully")
    void shouldProcessPaymentSuccessfully() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1L);
            payment.setCreatedAt(LocalDateTime.now());
            return payment;
        });
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        PaymentResponse response = paymentService.processPayment(paymentRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderId()).isEqualTo(1L);
        assertThat(response.getOrderNumber()).isEqualTo("ORD-20251028-0001");
        assertThat(response.getMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("649.97"));
        assertThat(response.getTransactionId()).startsWith("TXN-");

        verify(orderRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository).save(mockOrder);
    }

    @Test
    @DisplayName("Should generate unique transaction ID")
    void shouldGenerateUniqueTransactionId() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        when(paymentRepository.save(paymentCaptor.capture())).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1L);
            return payment;
        });
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        PaymentResponse response1 = paymentService.processPayment(paymentRequest);
        
        // Reset mocks and order status for second payment
        mockOrder.setStatus(OrderStatus.PENDING); // Reset order status to allow second payment
        reset(paymentRepository);
        when(paymentRepository.save(paymentCaptor.capture())).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(2L);
            return payment;
        });
        
        PaymentResponse response2 = paymentService.processPayment(paymentRequest);

        // Then
        assertThat(response1.getTransactionId()).isNotNull();
        assertThat(response2.getTransactionId()).isNotNull();
        assertThat(response1.getTransactionId()).isNotEqualTo(response2.getTransactionId());
        assertThat(response1.getTransactionId()).matches("TXN-[0-9a-f-]+");
        assertThat(response2.getTransactionId()).matches("TXN-[0-9a-f-]+");
    }

    @Test
    @DisplayName("Should update order status to PROCESSING after payment")
    void shouldUpdateOrderStatusToProcessingAfterPayment() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1L);
            return payment;
        });
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(orderCaptor.capture())).thenReturn(mockOrder);

        // When
        paymentService.processPayment(paymentRequest);

        // Then
        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> paymentService.processPayment(paymentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");

        verify(orderRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw exception when processing payment for another user's order")
    void shouldThrowExceptionWhenProcessingPaymentForAnotherUsersOrder() {
        // Given
        User anotherUser = User.builder()
                .id(2L)
                .email("another@example.com")
                .build();
        mockOrder.setUser(anotherUser);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // When & Then
        assertThatThrownBy(() -> paymentService.processPayment(paymentRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Access denied");

        verify(orderRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw exception when order is not in PENDING status")
    void shouldThrowExceptionWhenOrderIsNotInPendingStatus() {
        // Given
        mockOrder.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // When & Then
        assertThatThrownBy(() -> paymentService.processPayment(paymentRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Order cannot be paid in status");

        verify(orderRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should not allow payment for cancelled order")
    void shouldNotAllowPaymentForCancelledOrder() {
        // Given
        mockOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // When & Then
        assertThatThrownBy(() -> paymentService.processPayment(paymentRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Order cannot be paid in status: CANCELLED");

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should save payment with correct amount")
    void shouldSavePaymentWithCorrectAmount() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        when(paymentRepository.save(paymentCaptor.capture())).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1L);
            return payment;
        });
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        paymentService.processPayment(paymentRequest);

        // Then
        Payment savedPayment = paymentCaptor.getValue();
        assertThat(savedPayment.getAmount()).isEqualByComparingTo(mockOrder.getTotalAmount());
        assertThat(savedPayment.getMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(savedPayment.getOrder()).isEqualTo(mockOrder);
    }

    @Test
    @DisplayName("Should get payment by order ID successfully")
    void shouldGetPaymentByOrderIdSuccessfully() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));

        // When
        PaymentResponse response = paymentService.getPaymentByOrderId(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderId()).isEqualTo(1L);
        assertThat(response.getTransactionId()).isEqualTo("TXN-12345-ABCDE");

        verify(orderRepository).findById(1L);
        verify(paymentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting payment from another user's order")
    void shouldThrowExceptionWhenGettingPaymentFromAnotherUsersOrder() {
        // Given
        User anotherUser = User.builder()
                .id(2L)
                .email("another@example.com")
                .build();
        mockOrder.setUser(anotherUser);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // When & Then
        assertThatThrownBy(() -> paymentService.getPaymentByOrderId(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Access denied");

        verify(orderRepository).findById(1L);
        verify(paymentRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when payment not found")
    void shouldThrowExceptionWhenPaymentNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> paymentService.getPaymentByOrderId(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found");

        verify(orderRepository).findById(1L);
        verify(paymentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> paymentService.processPayment(paymentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("test@example.com");
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
