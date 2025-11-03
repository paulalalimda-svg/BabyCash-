package com.babycash.backend.repository;

import com.babycash.backend.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for CartItem entity
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
