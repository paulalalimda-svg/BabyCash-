package com.babycash.backend.controller;

import com.babycash.backend.dto.LoyaltyPointsResponse;
import com.babycash.backend.dto.LoyaltyTransactionResponse;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.repository.UserRepository;
import com.babycash.backend.service.ILoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Loyalty Program endpoints
 * Thin controller - delegates business logic to service
 */
@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LoyaltyController {

    private final ILoyaltyService loyaltyService;
    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND = "Usuario no encontrado";

    /**
     * Get loyalty points summary for authenticated user
     * GET /api/loyalty/points
     */
    @GetMapping("/points")
    public ResponseEntity<LoyaltyPointsResponse> getUserLoyaltyPoints(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        LoyaltyPointsResponse response = loyaltyService.getUserLoyaltyPoints(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Get transaction history for authenticated user
     * GET /api/loyalty/history?page=0&size=10
     */
    @GetMapping("/history")
    public ResponseEntity<Page<LoyaltyTransactionResponse>> getTransactionHistory(
            Authentication authentication,
            Pageable pageable) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        Page<LoyaltyTransactionResponse> history = loyaltyService.getUserTransactionHistory(user, pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * Redeem points for authenticated user
     * POST /api/loyalty/redeem
     * Body: { "points": 100 }
     */
    @PostMapping("/redeem")
    public ResponseEntity<String> redeemPoints(
            Authentication authentication,
            @RequestBody RedeemPointsRequest request) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        boolean success = loyaltyService.redeemPoints(user, request.points());
        
        if (success) {
            return ResponseEntity.ok("Puntos canjeados exitosamente");
        } else {
            return ResponseEntity.badRequest().body("No tienes suficientes puntos");
        }
    }

    /**
     * Request DTO for redeeming points
     */
    public record RedeemPointsRequest(Integer points) {}
}
