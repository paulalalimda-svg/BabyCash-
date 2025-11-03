package com.babycash.backend.controller;

import com.babycash.backend.dto.UpdateProfileRequest;
import com.babycash.backend.dto.UserStatsResponse;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        User updatedUser = userService.updateProfile(email, request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(Authentication authentication) {
        String email = authentication.getName();
        UserStatsResponse stats = userService.getUserStats(email);
        return ResponseEntity.ok(stats);
    }
}
