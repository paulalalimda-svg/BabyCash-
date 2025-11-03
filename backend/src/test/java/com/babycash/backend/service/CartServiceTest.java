package com.babycash.backend.service;

import com.babycash.backend.dto.request.AddToCartRequest;
import com.babycash.backend.dto.response.CartResponse;
import com.babycash.backend.exception.custom.BusinessException;
import com.babycash.backend.exception.custom.ResourceNotFoundException;
import com.babycash.backend.model.entity.Cart;
import com.babycash.backend.model.entity.CartItem;
import com.babycash.backend.model.entity.Product;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.ProductCategory;
import com.babycash.backend.model.enums.UserRole;
import com.babycash.backend.repository.CartRepository;
import com.babycash.backend.repository.ProductRepository;
import com.babycash.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CartService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CartService cartService;

    private User mockUser;
    private Product mockProduct;
    private Cart mockCart;
    private AddToCartRequest addToCartRequest;

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

        // Setup mock product
        mockProduct = Product.builder()
                .id(1L)
                .name("Baby Stroller")
                .price(new BigDecimal("299.99"))
                .discountPrice(null)
                .category(ProductCategory.FURNITURE)
                .stock(10)
                .enabled(true)
                .build();

        // Setup mock cart
        mockCart = Cart.builder()
                .id(1L)
                .user(mockUser)
                .items(new ArrayList<>())
                .build();

        // Setup request
        addToCartRequest = new AddToCartRequest();
        addToCartRequest.setProductId(1L);
        addToCartRequest.setQuantity(2);

        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
    }

    @Test
    @DisplayName("Should add product to empty cart successfully")
    void shouldAddProductToEmptyCartSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            cart.setId(1L);
            return cart;
        });

        // When
        CartResponse response = cartService.addToCart(addToCartRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getProductId()).isEqualTo(1L);
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(response.getTotalItems()).isEqualTo(2);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("599.98"));

        verify(productRepository).findById(1L);
        verify(cartRepository, times(2)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should add product to existing cart successfully")
    void shouldAddProductToExistingCartSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        // When
        CartResponse response = cartService.addToCart(addToCartRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should increment quantity when adding existing product")
    void shouldIncrementQuantityWhenAddingExistingProduct() {
        // Given
        CartItem existingItem = CartItem.builder()
                .id(1L)
                .cart(mockCart)
                .product(mockProduct)
                .quantity(3)
                .build();
        mockCart.getItems().add(existingItem);

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        // When
        CartResponse response = cartService.addToCart(addToCartRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(5); // 3 + 2
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cartService.addToCart(addToCartRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(productRepository).findById(1L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw exception when product is disabled")
    void shouldThrowExceptionWhenProductIsDisabled() {
        // Given
        mockProduct.setEnabled(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        // When & Then
        assertThatThrownBy(() -> cartService.addToCart(addToCartRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Product is not available");

        verify(productRepository).findById(1L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock")
    void shouldThrowExceptionWhenInsufficientStock() {
        // Given
        mockProduct.setStock(1);
        addToCartRequest.setQuantity(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        // When & Then
        assertThatThrownBy(() -> cartService.addToCart(addToCartRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");

        verify(productRepository).findById(1L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should get empty cart when cart not exists")
    void shouldGetEmptyCartWhenCartNotExists() {
        // Given
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.empty());

        // When
        CartResponse response = cartService.getCart();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotalItems()).isEqualTo(0);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(cartRepository).findByUser(mockUser);
    }

    @Test
    @DisplayName("Should get cart with items successfully")
    void shouldGetCartWithItemsSuccessfully() {
        // Given
        CartItem item = CartItem.builder()
                .id(1L)
                .cart(mockCart)
                .product(mockProduct)
                .quantity(2)
                .build();
        mockCart.getItems().add(item);

        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));

        // When
        CartResponse response = cartService.getCart();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getTotalItems()).isEqualTo(2);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("599.98"));

        verify(cartRepository).findByUser(mockUser);
    }

    @Test
    @DisplayName("Should update cart item quantity successfully")
    void shouldUpdateCartItemQuantitySuccessfully() {
        // Given
        CartItem item = CartItem.builder()
                .id(1L)
                .cart(mockCart)
                .product(mockProduct)
                .quantity(2)
                .build();
        mockCart.getItems().add(item);

        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        // When
        CartResponse response = cartService.updateCartItem(1L, 5);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(5);

        verify(cartRepository).findByUser(mockUser);
        verify(cartRepository).save(mockCart);
    }

    @Test
    @DisplayName("Should remove item when updating quantity to zero")
    void shouldRemoveItemWhenUpdatingQuantityToZero() {
        // Given
        CartItem item = CartItem.builder()
                .id(1L)
                .cart(mockCart)
                .product(mockProduct)
                .quantity(2)
                .build();
        mockCart.getItems().add(item);

        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        // When
        CartResponse response = cartService.updateCartItem(1L, 0);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();

        verify(cartRepository).save(mockCart);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent item")
    void shouldThrowExceptionWhenUpdatingNonExistentItem() {
        // Given
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));

        // When & Then
        assertThatThrownBy(() -> cartService.updateCartItem(999L, 5))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cart item not found");

        verify(cartRepository).findByUser(mockUser);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw exception when updating quantity exceeds stock")
    void shouldThrowExceptionWhenUpdatingQuantityExceedsStock() {
        // Given
        CartItem item = CartItem.builder()
                .id(1L)
                .cart(mockCart)
                .product(mockProduct)
                .quantity(2)
                .build();
        mockCart.getItems().add(item);
        mockProduct.setStock(5);

        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));

        // When & Then
        assertThatThrownBy(() -> cartService.updateCartItem(1L, 10))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");

        verify(cartRepository).findByUser(mockUser);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should remove item from cart successfully")
    void shouldRemoveItemFromCartSuccessfully() {
        // Given
        CartItem item = CartItem.builder()
                .id(1L)
                .cart(mockCart)
                .product(mockProduct)
                .quantity(2)
                .build();
        mockCart.getItems().add(item);

        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        // When
        cartService.removeFromCart(1L);

        // Then
        assertThat(mockCart.getItems()).isEmpty();

        verify(cartRepository).findByUser(mockUser);
        verify(cartRepository).save(mockCart);
    }

    @Test
    @DisplayName("Should clear cart successfully")
    void shouldClearCartSuccessfully() {
        // Given
        CartItem item1 = CartItem.builder()
                .id(1L)
                .cart(mockCart)
                .product(mockProduct)
                .quantity(2)
                .build();
        CartItem item2 = CartItem.builder()
                .id(2L)
                .cart(mockCart)
                .product(mockProduct)
                .quantity(3)
                .build();
        mockCart.getItems().add(item1);
        mockCart.getItems().add(item2);

        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        // When
        cartService.clearCart();

        // Then
        assertThat(mockCart.getItems()).isEmpty();

        verify(cartRepository).findByUser(mockUser);
        verify(cartRepository).save(mockCart);
    }

    @Test
    @DisplayName("Should calculate total with discount price when available")
    void shouldCalculateTotalWithDiscountPriceWhenAvailable() {
        // Given
        mockProduct.setDiscountPrice(new BigDecimal("249.99"));
        CartItem item = CartItem.builder()
                .id(1L)
                .cart(mockCart)
                .product(mockProduct)
                .quantity(2)
                .build();
        mockCart.getItems().add(item);

        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(mockCart));

        // When
        CartResponse response = cartService.getCart();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("499.98")); // 249.99 * 2

        verify(cartRepository).findByUser(mockUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cartService.getCart())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("test@example.com");
    }
}
