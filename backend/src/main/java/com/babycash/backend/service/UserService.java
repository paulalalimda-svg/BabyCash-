package com.babycash.backend.service;

import com.babycash.backend.dto.UpdateProfileRequest;
import com.babycash.backend.dto.UserStatsResponse;
import com.babycash.backend.model.entity.Order;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.repository.OrderRepository;
import com.babycash.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional
    public User updateProfile(String email, UpdateProfileRequest request) {
        User user = getUserByEmail(email);
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        
        User savedUser = userRepository.save(user);
        log.info("Perfil actualizado para usuario: {}", email);
        
        return savedUser;
    }

    public UserStatsResponse getUserStats(String email) {
        User user = getUserByEmail(email);
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        
        Long totalOrders = (long) orders.size();
        Long totalProducts = orders.stream()
                .mapToLong(order -> order.getItems().size())
                .sum();
        
        Double totalSpent = orders.stream()
                .map(Order::getTotalAmount)
                .mapToDouble(amount -> amount != null ? amount.doubleValue() : 0.0)
                .sum();
        
        String memberSince = String.valueOf(user.getCreatedAt().getYear());
        
        return UserStatsResponse.builder()
                .totalOrders(totalOrders)
                .totalProducts(totalProducts)
                .totalSpent(totalSpent)
                .memberSince(memberSince)
                .build();
    }

    /**
     * Get total number of users (Admin only)
     */
    public long getTotalUsers() {
        return userRepository.count();
    }
}
