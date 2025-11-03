package com.babycash.backend.service;

import com.babycash.backend.dto.request.ProductRequest;
import com.babycash.backend.dto.response.ProductResponse;
import com.babycash.backend.exception.custom.ResourceNotFoundException;
import com.babycash.backend.model.entity.Product;
import com.babycash.backend.model.enums.ProductCategory;
import com.babycash.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de productos con cache para optimizar performance
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByEnabledTrue(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByCategoryAndEnabledTrue(category, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        return productRepository.searchProducts(query, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "featuredProducts")
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndEnabledTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Delete a product (Admin only)
     */
    @Transactional
    @CacheEvict(value = {"products", "featuredProducts"}, allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    /**
     * Toggle featured status of a product (Admin only)
     */
    @Transactional
    @CacheEvict(value = {"products", "featuredProducts"}, allEntries = true)
    public ProductResponse toggleFeatured(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setFeatured(!product.getFeatured());
        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    /**
     * Toggle enabled status of a product (Admin only)
     */
    @Transactional
    @CacheEvict(value = {"products", "featuredProducts"}, allEntries = true)
    public ProductResponse toggleEnabled(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setEnabled(!product.getEnabled());
        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    /**
     * Create a new product (Admin only)
     */
    @Transactional
    @CacheEvict(value = {"products", "featuredProducts"}, allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .category(request.getCategory())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .rating(BigDecimal.ZERO)
                .reviewCount(0)
                .build();

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    /**
     * Update an existing product (Admin only)
     */
    @Transactional
    @CacheEvict(value = {"products", "featuredProducts"}, allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        
        if (request.getFeatured() != null) {
            product.setFeatured(request.getFeatured());
        }
        if (request.getEnabled() != null) {
            product.setEnabled(request.getEnabled());
        }

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .category(product.getCategory().name())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .featured(product.getFeatured())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
