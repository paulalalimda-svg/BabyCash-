package com.babycash.backend.repository;

import com.babycash.backend.model.entity.Order;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Order con EntityGraph para optimizar carga de items
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    @EntityGraph(attributePaths = {"items", "items.product"})
    Page<Order> findByUser(User user, Pageable pageable);
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Order> findById(Long id);
    
    long countByStatus(OrderStatus status);
    
    long countByUser(User user);
}
