package com.babycash.backend.repository;

import com.babycash.backend.model.entity.Product;
import com.babycash.backend.model.enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByEnabledTrue(Pageable pageable);
    
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);
    
    Page<Product> findByCategoryAndEnabledTrue(ProductCategory category, Pageable pageable);
    
    List<Product> findByFeaturedTrueAndEnabledTrue();
    
    @Query("SELECT p FROM Product p WHERE p.enabled = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);
    
    long countByEnabledTrue();
    
    long countByCategory(ProductCategory category);
}
