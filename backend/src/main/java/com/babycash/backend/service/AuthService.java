package com.babycash.backend.service;

import com.babycash.backend.dto.request.LoginRequest;
import com.babycash.backend.dto.request.RegisterRequest;
import com.babycash.backend.dto.response.AuthResponse;
import com.babycash.backend.exception.custom.AuthenticationException;
import com.babycash.backend.exception.custom.BusinessException;
import com.babycash.backend.model.entity.RefreshToken;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.enums.UserRole;
import com.babycash.backend.repository.UserRepository;
import com.babycash.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsible for user authentication and registration
 * Handles JWT token generation and refresh token management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    /**
     * Register a new user in the system
     *
     * @param request Registration data (email, password, name)
     * @return AuthResponse with JWT tokens and user info
     * @throws BusinessException if email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("🔹 Attempting to register user with email: {}", request.getEmail());

        // Verify email is not already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("❌ Registration failed: Email {} already exists", request.getEmail());
            throw new BusinessException("El email ya está registrado");
        }

        // Create new user entity
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(UserRole.USER) // Default role for new registrations
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("✅ User registered successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        // Generate JWT tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole().name())
                .build();
    }

    /**
     * Authenticate user with email and password
     *
     * @param request Login credentials (email, password)
     * @return AuthResponse with JWT tokens and user info
     * @throws AuthenticationException if credentials are invalid
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("🔹 Attempting login for user: {}", request.getEmail());

        try {
            // Authenticate with Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Get authenticated user
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));

            log.info("✅ Login successful for user: {} with role: {}", user.getEmail(), user.getRole());

            // Generate JWT tokens
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String accessToken = jwtUtil.generateToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return AuthResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole().name())
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("❌ Login failed for user {}: Invalid credentials", request.getEmail());
            throw new AuthenticationException("Credenciales inválidas");
        }
    }

    /**
     * Refresh access token using refresh token
     *
     * @param refreshTokenString Refresh token from client
     * @return AuthResponse with new access token
     * @throws AuthenticationException if refresh token is invalid
     */
    @Transactional
    public AuthResponse refreshToken(String refreshTokenString) {
        log.info("🔹 Attempting to refresh access token");

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenString);
        User user = refreshToken.getUser();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtUtil.generateToken(userDetails);

        log.info("✅ Access token refreshed for user: {}", user.getEmail());

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Logout user by invalidating refresh token
     *
     * @param refreshTokenString Refresh token to invalidate
     * @throws AuthenticationException if token not found
     */
    @Transactional
    public void logout(String refreshTokenString) {
        log.info("🔹 Attempting logout");
        refreshTokenService.revokeRefreshToken(refreshTokenString);
        log.info("✅ Logout successful - Refresh token invalidated");
    }

    /**
     * Generate password reset token and send email
     *
     * @param email User's email address
     * @throws BusinessException if user not found
     */
    @Transactional
    public void forgotPassword(String email, String baseUrl) {
        log.info("🔹 Password reset requested for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("No existe una cuenta con este email"));

        // Generate secure token (UUID)
        String resetToken = java.util.UUID.randomUUID().toString();
        
        // Set expiry to 1 hour from now
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpiry(expiry);
        userRepository.save(user);

        log.info("✅ Reset token generated for user: {}, expires at: {}", email, expiry);

        // Send email with reset link
        emailService.sendPasswordResetEmail(
            user.getEmail(),
            user.getFirstName(),
            resetToken,
            baseUrl
        );

        log.info("✅ Password reset email sent to: {}", email);
    }

    /**
     * Validate reset token
     *
     * @param token Reset token from email
     * @return User if token is valid
     * @throws BusinessException if token is invalid or expired
     */
    @Transactional(readOnly = true)
    public User validateResetToken(String token) {
        log.info("🔹 Validating reset token");

        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new BusinessException("Token inválido"));

        if (user.getResetPasswordExpiry() == null || 
            LocalDateTime.now().isAfter(user.getResetPasswordExpiry())) {
            log.warn("❌ Reset token expired for user: {}", user.getEmail());
            throw new BusinessException("El token ha expirado. Solicita uno nuevo.");
        }

        log.info("✅ Reset token validated for user: {}", user.getEmail());
        return user;
    }

    /**
     * Reset user password using valid token
     *
     * @param token Reset token
     * @param newPassword New password
     * @throws BusinessException if token is invalid or expired
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        log.info("🔹 Attempting password reset with token");

        User user = validateResetToken(token);

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        
        // Invalidate reset token
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);
        
        userRepository.save(user);

        log.info("✅ Password reset successful for user: {}", user.getEmail());

        // Send confirmation email
        emailService.sendPasswordChangedEmail(user.getEmail(), user.getFirstName());
    }
}
