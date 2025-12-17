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
import org.springframework.security.core.context.SecurityContextHolder;
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
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        log.info("🔹 Attempting to register user with email: {}", normalizedEmail);

        // Verify email is not already registered (normalize before checking)
        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("❌ Registration failed: Email {} already exists", normalizedEmail);
            throw new BusinessException("El email ya está registrado");
        }

        // Generate 6-digit verification code
        String verificationCode = String.format("%06d", new java.util.Random().nextInt(1000000));

        // Create new user entity
        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(UserRole.USER) // Default role for new registrations
                .enabled(true)
                .emailVerified(false)
                .verificationToken(verificationCode)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("✅ User registered successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        // Send verification email with 6-digit code
        emailService.sendEmailVerificationCode(
            savedUser.getEmail(),
            savedUser.getFirstName(),
            verificationCode
        );
        log.info("📧 Verification email sent to: {}", savedUser.getEmail());

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
                .emailVerified(savedUser.getEmailVerified())
                .build();
    }

    /**
     * Authenticate user with email and password
     *
     * @param request Login credentials (email, password)
     * @return AuthResponse with JWT tokens and user info
     * @throws AuthenticationException if credentials are invalid
     */
    @Transactional(noRollbackFor = {BadCredentialsException.class, AuthenticationException.class, com.babycash.backend.exception.custom.AuthenticationException.class, BusinessException.class})
    public AuthResponse login(LoginRequest request) {
        log.info("🔹 Attempting login for user: {}", request.getEmail());

        String loginEmail = request.getEmail().trim().toLowerCase();

        // Check if user exists first
        User user = userRepository.findByEmail(loginEmail)
                .orElse(null);

        // If user exists, check if account is locked
        if (user != null) {
            // Auto-unlock if lock period has expired
            if (user.getAccountLockedUntil() != null &&
                user.getAccountLockedUntil().isBefore(LocalDateTime.now())) {
                user.setAccountLockedUntil(null);
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
                log.info("🔓 Account auto-unlocked for user: {}", loginEmail);
            }

            // Check if still locked
            if (user.getAccountLockedUntil() != null &&
                user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
                long minutesLeft = java.time.Duration.between(
                    LocalDateTime.now(),
                    user.getAccountLockedUntil()
                ).toMinutes();
                log.warn("🔒 Login attempt blocked - account locked for user: {}", loginEmail);
                throw new BusinessException(
                    "Cuenta bloqueada por múltiples intentos fallidos. " +
                    "Intenta nuevamente en " + minutesLeft + " minutos."
                );
            }
        }

        try {
            // Authenticate with Spring Security
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginEmail,
                    request.getPassword()
                )
            );

            // Get authenticated user (we know it exists now)
            user = userRepository.findByEmail(loginEmail)
                    .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));

            // Reset failed login attempts on successful login
            if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0) {
                user.setFailedLoginAttempts(0);
                user.setAccountLockedUntil(null);
                userRepository.save(user);
                log.info("🔄 Reset failed login attempts for user: {}", loginEmail);
            }

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
                    .emailVerified(user.getEmailVerified())
                    .build();

        } catch (BadCredentialsException e) {
            // Increment failed login attempts
            if (user != null) {
                int attempts = (user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1;
                user.setFailedLoginAttempts(attempts);

                if (attempts >= 3) {
                    // Lock account for 15 minutes
                    user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(15));
                    userRepository.save(user);
                    log.warn("🔒 Account locked due to {} failed attempts for user: {}", attempts, loginEmail);
                    throw new BusinessException(
                        "Cuenta bloqueada por múltiples intentos fallidos. " +
                        "Intenta nuevamente en 15 minutos."
                    );
                } else {
                    userRepository.save(user);
                    log.warn("❌ Login failed for user {} - attempt {}/3", loginEmail, attempts);
                    throw new AuthenticationException(
                        "Credenciales inválidas. Intentos restantes: " + (3 - attempts)
                    );
                }
            } else {
                // User doesn't exist - don't reveal this information
                log.warn("❌ Login failed - user not found: {}", loginEmail);
                throw new AuthenticationException("Credenciales inválidas");
            }
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
     * Generate password reset code and send email
     *
     * @param email User's email address
     * @throws BusinessException if user not found
     */
    @Transactional
    public void forgotPassword(String email, String baseUrl) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        log.info("🔹 Password reset requested for email: {}", normalizedEmail);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BusinessException("No existe una cuenta con este email"));

        // Generate 6-digit random code
        String resetCode = String.format("%06d", new java.util.Random().nextInt(1000000));

        // Set expiry to 15 minutes from now (codes expire faster than tokens)
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        user.setResetPasswordToken(resetCode);
        user.setResetPasswordExpiry(expiry);
        userRepository.save(user);

        log.info("✅ Reset code generated for user: {}, expires at: {}", email, expiry);

        // Send email with 6-digit code
        emailService.sendPasswordResetCodeEmail(
            user.getEmail(),
            user.getFirstName(),
            resetCode
        );

        log.info("✅ Password reset email sent to: {}", normalizedEmail);
    }

    /**
     * Validate reset code
     *
     * @param code Reset code from email (6 digits)
     * @return User if code is valid
     * @throws BusinessException if code is invalid or expired
     */
    @Transactional(readOnly = true)
    public User validateResetCode(String code) {
        log.info("🔹 Validating reset code");

        User user = userRepository.findByResetPasswordToken(code)
                .orElseThrow(() -> new BusinessException("Código inválido"));

        if (user.getResetPasswordExpiry() == null ||
            LocalDateTime.now().isAfter(user.getResetPasswordExpiry())) {
            log.warn("❌ Reset code expired for user: {}", user.getEmail());
            throw new BusinessException("El código ha expirado. Solicita uno nuevo.");
        }

        log.info("✅ Reset code validated for user: {}", user.getEmail());
        return user;
    }

    /**
     * Reset user password using valid code
     *
     * @param code Reset code (6 digits)
     * @param newPassword New password
     * @throws BusinessException if code is invalid or expired
     */
    @Transactional
    public void resetPassword(String code, String newPassword) {
        log.info("🔹 Attempting password reset with code");

        User user = validateResetCode(code);

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Invalidate reset code
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);

        userRepository.save(user);

        log.info("✅ Password reset successful for user: {}", user.getEmail());

        // Send confirmation email
        emailService.sendPasswordChangedEmail(user.getEmail(), user.getFirstName());
    }

    /**
     * Verify user email using the 6-digit code
     *
     * @param code Verification code (6 digits)
     * @throws BusinessException if code is invalid
     */
    @Transactional
    public void verifyEmail(String code) {
        log.info("🔹 Attempting email verification with code");

        User user = userRepository.findByVerificationToken(code)
                .orElseThrow(() -> new BusinessException("Código de verificación inválido"));

        if (user.getEmailVerified()) {
            log.info("ℹ️ Email already verified for user: {}", user.getEmail());
            return; // Already verified, no need to throw error
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null); // Invalidate the code after use
        userRepository.save(user);

        log.info("✅ Email verified successfully for user: {}", user.getEmail());
    }

    /**
     * Resend verification code to user email
     *
     * @param email User's email address
     * @throws BusinessException if user not found or already verified
     */
    @Transactional
    public void resendVerificationCode(String email) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        log.info("🔹 Resending verification code for email: {}", normalizedEmail);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BusinessException("No existe una cuenta con este email"));

        if (user.getEmailVerified()) {
            throw new BusinessException("El correo electrónico ya está verificado");
        }

        // Generate new 6-digit verification code
        String verificationCode = String.format("%06d", new java.util.Random().nextInt(1000000));
        user.setVerificationToken(verificationCode);
        userRepository.save(user);

        // Send verification email
        emailService.sendEmailVerificationCode(
            user.getEmail(),
            user.getFirstName(),
            verificationCode
        );

        log.info("📧 Verification code resent to: {}", normalizedEmail);
    }

    /**
     * Logout user from all devices by revoking all refresh tokens
     */
    @Transactional
    public void logoutAllDevices() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("🔹 Logging out all devices for user: {}", currentUserEmail);

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        refreshTokenService.revokeAllUserTokens(user);

        log.info("✅ All devices logged out for user: {}", currentUserEmail);
    }

    /**
     * Request account deletion - generates code and sends email
     */
    @Transactional
    public void requestAccountDeletion() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("🔹 Account deletion requested for user: {}", currentUserEmail);

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // Generate 6-digit code
        String deletionCode = String.format("%06d", new java.util.Random().nextInt(1000000));

        // Set expiry for 15 minutes
        user.setAccountDeletionToken(deletionCode);
        user.setAccountDeletionExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Send email with code
        emailService.sendAccountDeletionCode(user.getEmail(), user.getFirstName(), deletionCode);

        log.info("📧 Account deletion code sent to: {}", currentUserEmail);
    }

    /**
     * Delete account after validating code and password
     */
    @Transactional
    public void deleteAccount(String code, String confirmPassword) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("🔹 Attempting account deletion for user: {}", currentUserEmail);

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // Verify deletion code
        if (user.getAccountDeletionToken() == null ||
            !user.getAccountDeletionToken().equals(code)) {
            log.warn("❌ Invalid deletion code for user: {}", currentUserEmail);
            throw new BusinessException("Código de verificación inválido");
        }

        // Check expiry
        if (user.getAccountDeletionExpiry() == null ||
            user.getAccountDeletionExpiry().isBefore(LocalDateTime.now())) {
            log.warn("❌ Expired deletion code for user: {}", currentUserEmail);
            throw new BusinessException("El código de verificación ha expirado");
        }

        // Verify password
        if (!passwordEncoder.matches(confirmPassword, user.getPassword())) {
            log.warn("❌ Invalid password confirmation for user: {}", currentUserEmail);
            throw new BusinessException("Contraseña incorrecta");
        }

        // Revoke all refresh tokens first
        refreshTokenService.revokeAllUserTokens(user);

        // Delete user (cascade will handle related entities)
        userRepository.delete(user);

        log.info("✅ Account deleted for user: {}", currentUserEmail);
    }
}
