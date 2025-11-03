package com.babycash.backend.service;

import com.babycash.backend.dto.response.ProductResponse;
import com.babycash.backend.exception.custom.ResourceNotFoundException;
import com.babycash.backend.model.entity.Product;
import com.babycash.backend.model.enums.ProductCategory;
import com.babycash.backend.repository.ProductRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .id(1L)
                .name("Baby Stroller")
                .description("Comfortable 3-wheel stroller")
                .price(new BigDecimal("299.99"))
                .discountPrice(new BigDecimal("249.99"))
                .category(ProductCategory.FURNITURE)
                .stock(15)
                .enabled(true)
                .featured(true)
                .rating(new BigDecimal("4.5"))
                .reviewCount(120)
                .imageUrl("http://example.com/stroller.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Organic Baby Food")
                .description("Stage 1 puree variety pack")
                .price(new BigDecimal("12.99"))
                .category(ProductCategory.FOOD)
                .stock(50)
                .enabled(true)
                .featured(false)
                .rating(new BigDecimal("4.8"))
                .reviewCount(85)
                .imageUrl("http://example.com/food.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        pageable = PageRequest.of(0, 12);
    }

    @Test
    @DisplayName("Should return all enabled products with pagination")
    void shouldGetAllProductsSuccessfully() {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        when(productRepository.findByEnabledTrue(any(Pageable.class))).thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.getAllProducts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Baby Stroller");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Organic Baby Food");

        verify(productRepository).findByEnabledTrue(pageable);
    }

    @Test
    @DisplayName("Should return products by category")
    void shouldGetProductsByCategorySuccessfully() {
        // Given
        List<Product> products = Arrays.asList(product1);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);
        when(productRepository.findByCategoryAndEnabledTrue(any(ProductCategory.class), any(Pageable.class)))
                .thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.getProductsByCategory(ProductCategory.FURNITURE, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("FURNITURE");

        verify(productRepository).findByCategoryAndEnabledTrue(ProductCategory.FURNITURE, pageable);
    }

    @Test
    @DisplayName("Should search products by query")
    void shouldSearchProductsSuccessfully() {
        // Given
        String query = "baby";
        List<Product> products = Arrays.asList(product1, product2);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        when(productRepository.searchProducts(anyString(), any(Pageable.class))).thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.searchProducts(query, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(productRepository).searchProducts(query.toLowerCase(), pageable);
    }

    @Test
    @DisplayName("Should get product by id")
    void shouldGetProductByIdSuccessfully() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product1));

        // When
        ProductResponse result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Baby Stroller");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("299.99"));

        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(productRepository).findById(999L);
    }

    @Test
    @DisplayName("Should return only featured products")
    void shouldGetFeaturedProductsSuccessfully() {
        // Given
        List<Product> featuredProducts = Arrays.asList(product1);
        when(productRepository.findByFeaturedTrueAndEnabledTrue()).thenReturn(featuredProducts);

        // When
        List<ProductResponse> result = productService.getFeaturedProducts();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFeatured()).isTrue();
        assertThat(result.get(0).getName()).isEqualTo("Baby Stroller");

        verify(productRepository).findByFeaturedTrueAndEnabledTrue();
    }

    @Test
    @DisplayName("Should return empty list when no featured products")
    void shouldReturnEmptyListWhenNoFeaturedProducts() {
        // Given
        when(productRepository.findByFeaturedTrueAndEnabledTrue()).thenReturn(Arrays.asList());

        // When
        List<ProductResponse> result = productService.getFeaturedProducts();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(productRepository).findByFeaturedTrueAndEnabledTrue();
    }

    @Test
    @DisplayName("Should map product entity to response correctly")
    void shouldMapProductEntityToResponseCorrectly() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product1));

        // When
        ProductResponse result = productService.getProductById(1L);

        // Then
        assertThat(result.getId()).isEqualTo(product1.getId());
        assertThat(result.getName()).isEqualTo(product1.getName());
        assertThat(result.getDescription()).isEqualTo(product1.getDescription());
        assertThat(result.getPrice()).isEqualTo(product1.getPrice());
        assertThat(result.getDiscountPrice()).isEqualTo(product1.getDiscountPrice());
        assertThat(result.getCategory()).isEqualTo(product1.getCategory().name());
        assertThat(result.getStock()).isEqualTo(product1.getStock());
        assertThat(result.getImageUrl()).isEqualTo(product1.getImageUrl());
        assertThat(result.getFeatured()).isEqualTo(product1.getFeatured());
        assertThat(result.getRating()).isEqualTo(product1.getRating());
        assertThat(result.getReviewCount()).isEqualTo(product1.getReviewCount());
    }
}
