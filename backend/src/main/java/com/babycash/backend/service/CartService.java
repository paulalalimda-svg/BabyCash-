package com.babycash.backend.service;

import com.babycash.backend.dto.request.AddToCartRequest;
import com.babycash.backend.dto.response.CartResponse;
import com.babycash.backend.exception.custom.BusinessException;
import com.babycash.backend.exception.custom.ResourceNotFoundException;
import com.babycash.backend.model.entity.Cart;
import com.babycash.backend.model.entity.CartItem;
import com.babycash.backend.model.entity.Product;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.repository.CartRepository;
import com.babycash.backend.repository.ProductRepository;
import com.babycash.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Cart service with business logic
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        User user = getCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getEnabled()) {
            throw new BusinessException("Product is not available");
        }

        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException("Insufficient stock");
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });

        // Check if product already in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        cart = cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart() {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> Cart.builder().user(user).build());
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Long itemId, Integer quantity) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            if (item.getProduct().getStock() < quantity) {
                throw new BusinessException("Insufficient stock");
            }
            item.setQuantity(quantity);
        }

        cart = cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Transactional
    public void removeFromCart(Long itemId) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart() {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartResponse mapToResponse(Cart cart) {
        BigDecimal totalAmount = cart.getItems().stream()
                .map(item -> {
                    BigDecimal price = item.getProduct().getDiscountPrice() != null
                            ? item.getProduct().getDiscountPrice()
                            : item.getProduct().getPrice();
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(cart.getItems().stream()
                        .map(item -> {
                            BigDecimal price = item.getProduct().getDiscountPrice() != null
                                    ? item.getProduct().getDiscountPrice()
                                    : item.getProduct().getPrice();
                            return CartResponse.CartItemResponse.builder()
                                    .id(item.getId())
                                    .productId(item.getProduct().getId())
                                    .productName(item.getProduct().getName())
                                    .productPrice(price)
                                    .productImage(item.getProduct().getImageUrl())
                                    .quantity(item.getQuantity())
                                    .subtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())))
                                    .build();
                        })
                        .toList())
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
