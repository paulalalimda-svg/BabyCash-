package com.babycash.backend.service;

import com.babycash.backend.dto.request.CreateOrderRequest;
import com.babycash.backend.dto.response.OrderResponse;
import com.babycash.backend.exception.custom.BusinessException;
import com.babycash.backend.exception.custom.ResourceNotFoundException;
import com.babycash.backend.model.entity.Order;
import com.babycash.backend.model.entity.OrderItem;
import com.babycash.backend.model.entity.Product;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.OrderStatus;
import com.babycash.backend.model.enums.ProductCategory;
import com.babycash.backend.model.enums.UserRole;
import com.babycash.backend.repository.OrderRepository;
import com.babycash.backend.repository.ProductRepository;
import com.babycash.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderService orderService;

    private User mockUser;
    private Product mockProduct1;
    private Product mockProduct2;
    private CreateOrderRequest createOrderRequest;
    private Order mockOrder;

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

        // Setup mock products
        mockProduct1 = Product.builder()
                .id(1L)
                .name("Baby Stroller")
                .price(new BigDecimal("299.99"))
                .discountPrice(new BigDecimal("249.99"))
                .category(ProductCategory.FURNITURE)
                .stock(10)
                .enabled(true)
                .build();

        mockProduct2 = Product.builder()
                .id(2L)
                .name("Baby Monitor")
                .price(new BigDecimal("149.99"))
                .discountPrice(null)
                .category(ProductCategory.ACCESSORIES)
                .stock(5)
                .enabled(true)
                .build();

        // Setup create order request
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setShippingAddress("123 Main St, City, Country");
        createOrderRequest.setNotes("Please deliver in the morning");
        
        CreateOrderRequest.OrderItemRequest item1 = new CreateOrderRequest.OrderItemRequest();
        item1.setProductId(1L);
        item1.setQuantity(2);
        
        CreateOrderRequest.OrderItemRequest item2 = new CreateOrderRequest.OrderItemRequest();
        item2.setProductId(2L);
        item2.setQuantity(1);
        
        createOrderRequest.setItems(List.of(item1, item2));

        // Setup mock order
        mockOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-20251028-0001")
                .user(mockUser)
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("649.97"))
                .shippingAddress("123 Main St, City, Country")
                .notes("Please deliver in the morning")
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(mockProduct2));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            order.setOrderNumber("ORD-20251028-0001");
            order.setCreatedAt(LocalDateTime.now());
            return order;
        });

        // When
        OrderResponse response = orderService.createOrder(createOrderRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderNumber()).isEqualTo("ORD-20251028-0001");
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("649.97")); // 249.99*2 + 149.99
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getShippingAddress()).isEqualTo("123 Main St, City, Country");

        verify(productRepository).findById(1L);
        verify(productRepository).findById(2L);
        verify(productRepository, times(2)).save(any(Product.class));
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should reduce product stock when creating order")
    void shouldReduceProductStockWhenCreatingOrder() {
        // Given
        int initialStock1 = mockProduct1.getStock();
        int initialStock2 = mockProduct2.getStock();

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(mockProduct2));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        orderService.createOrder(createOrderRequest);

        // Then
        assertThat(mockProduct1.getStock()).isEqualTo(initialStock1 - 2);
        assertThat(mockProduct2.getStock()).isEqualTo(initialStock2 - 1);

        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(createOrderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(productRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when product is disabled")
    void shouldThrowExceptionWhenProductIsDisabled() {
        // Given
        mockProduct1.setEnabled(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct1));

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(createOrderRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Product is not available");

        verify(productRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock")
    void shouldThrowExceptionWhenInsufficientStock() {
        // Given
        mockProduct1.setStock(1); // Not enough for quantity 2
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct1));

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(createOrderRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");

        verify(productRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should calculate total using discount price when available")
    void shouldCalculateTotalUsingDiscountPriceWhenAvailable() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(mockProduct2));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        OrderResponse response = orderService.createOrder(createOrderRequest);

        // Then
        // Product1: 249.99 (discount) * 2 = 499.98
        // Product2: 149.99 (regular) * 1 = 149.99
        // Total: 649.97
        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("649.97"));
    }

    @Test
    @DisplayName("Should get my orders successfully")
    void shouldGetMyOrdersSuccessfully() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = List.of(mockOrder);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, 1);

        when(orderRepository.findByUser(mockUser, pageable)).thenReturn(orderPage);

        // When
        Page<OrderResponse> response = orderService.getMyOrders(pageable);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);

        verify(orderRepository).findByUser(mockUser, pageable);
    }

    @Test
    @DisplayName("Should get order by id successfully")
    void shouldGetOrderByIdSuccessfully() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // When
        OrderResponse response = orderService.getOrderById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderNumber()).isEqualTo("ORD-20251028-0001");

        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting order from another user")
    void shouldThrowExceptionWhenGettingOrderFromAnotherUser() {
        // Given
        User anotherUser = User.builder()
                .id(2L)
                .email("another@example.com")
                .build();
        mockOrder.setUser(anotherUser);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // When & Then
        assertThatThrownBy(() -> orderService.getOrderById(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Access denied");

        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get order by order number successfully")
    void shouldGetOrderByOrderNumberSuccessfully() {
        // Given
        when(orderRepository.findByOrderNumber("ORD-20251028-0001")).thenReturn(Optional.of(mockOrder));

        // When
        OrderResponse response = orderService.getOrderByNumber("ORD-20251028-0001");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOrderNumber()).isEqualTo("ORD-20251028-0001");

        verify(orderRepository).findByOrderNumber("ORD-20251028-0001");
    }

    @Test
    @DisplayName("Should throw exception when order number not found")
    void shouldThrowExceptionWhenOrderNumberNotFound() {
        // Given
        when(orderRepository.findByOrderNumber("ORD-INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.getOrderByNumber("ORD-INVALID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");

        verify(orderRepository).findByOrderNumber("ORD-INVALID");
    }

    @Test
    @DisplayName("Should cancel order successfully")
    void shouldCancelOrderSuccessfully() {
        // Given
        OrderItem item1 = OrderItem.builder()
                .id(1L)
                .order(mockOrder)
                .product(mockProduct1)
                .quantity(2)
                .unitPrice(new BigDecimal("249.99"))
                .subtotal(new BigDecimal("499.98"))
                .build();

        mockOrder.getItems().add(item1);
        mockProduct1.setStock(8); // Already reduced

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        OrderResponse response = orderService.cancelOrder(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(mockProduct1.getStock()).isEqualTo(10); // Restored from 8 to 10

        verify(orderRepository).findById(1L);
        verify(productRepository).save(mockProduct1);
        verify(orderRepository).save(mockOrder);
    }

    @Test
    @DisplayName("Should throw exception when cancelling order from another user")
    void shouldThrowExceptionWhenCancellingOrderFromAnotherUser() {
        // Given
        User anotherUser = User.builder()
                .id(2L)
                .email("another@example.com")
                .build();
        mockOrder.setUser(anotherUser);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // When & Then
        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Access denied");

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when cancelling non-pending order")
    void shouldThrowExceptionWhenCancellingNonPendingOrder() {
        // Given
        mockOrder.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // When & Then
        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot cancel order in status");

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should restore stock for all items when cancelling order")
    void shouldRestoreStockForAllItemsWhenCancellingOrder() {
        // Given
        OrderItem item1 = OrderItem.builder()
                .id(1L)
                .order(mockOrder)
                .product(mockProduct1)
                .quantity(2)
                .build();

        OrderItem item2 = OrderItem.builder()
                .id(2L)
                .order(mockOrder)
                .product(mockProduct2)
                .quantity(1)
                .build();

        mockOrder.getItems().add(item1);
        mockOrder.getItems().add(item2);

        mockProduct1.setStock(8);
        mockProduct2.setStock(4);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        orderService.cancelOrder(1L);

        // Then
        assertThat(mockProduct1.getStock()).isEqualTo(10); // 8 + 2
        assertThat(mockProduct2.getStock()).isEqualTo(5);  // 4 + 1

        verify(productRepository, times(2)).save(any(Product.class));
    }
}
