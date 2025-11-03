package com.babycash.backend.service;

import com.babycash.backend.entity.AuditLog;
import com.babycash.backend.exception.custom.BusinessException;
import com.babycash.backend.model.entity.RefreshToken;
import com.babycash.backend.model.entity.User;
import com.babycash.backend.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing refresh tokens with automatic rotation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditService auditService;

    @Value("${app.jwt.refresh-expiration-ms:604800000}") // 7 days default
    private Long refreshTokenDurationMs;

    @Value("${app.jwt.max-active-tokens:5}") // Max 5 active tokens per user
    private Integer maxActiveTokensPerUser;

    /**
     * Create a new refresh token for a user
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Check if user has too many active tokens
        long activeTokenCount = refreshTokenRepository.countActiveTokensByUser(user, LocalDateTime.now());
        if (activeTokenCount >= maxActiveTokensPerUser) {
            // Revoke oldest token
            List<RefreshToken> activeTokens = refreshTokenRepository.findActiveTokensByUser(user, LocalDateTime.now());
            if (!activeTokens.isEmpty()) {
                RefreshToken oldestToken = activeTokens.stream()
                        .min((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                        .orElseThrow();
                oldestToken.revoke();
                refreshTokenRepository.save(oldestToken);
                log.info("Revoked oldest refresh token for user {} due to max limit", user.getEmail());
            }
        }

        // Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusNanos(refreshTokenDurationMs * 1_000_000);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .revoked(false)
                .ipAddress(getClientIP())
                .userAgent(getUserAgent())
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);

        auditService.logAction(
                AuditLog.ActionType.LOGIN,
                "RefreshToken",
                refreshToken.getId(),
                "Refresh token creado para usuario: " + user.getEmail()
        );

        log.debug("Created refresh token for user: {}", user.getEmail());
        return refreshToken;
    }

    /**
     * Validate and rotate refresh token
     * Returns the same user but revokes the old token
     */
    @Transactional
    public RefreshToken rotateRefreshToken(String token) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token not found"));

        // Validate token
        if (!oldToken.isValid()) {
            if (oldToken.getRevoked()) {
                // Possible token reuse attack - revoke all user tokens
                refreshTokenRepository.revokeAllUserTokens(oldToken.getUser(), LocalDateTime.now());
                auditService.logSecurityEvent(
                        "Intento de reutilización de refresh token",
                        "Token revocado usado por: " + oldToken.getUser().getEmail() + ", IP: " + getClientIP()
                );
                log.warn("Refresh token reuse detected for user: {}", oldToken.getUser().getEmail());
                throw new BusinessException("Invalid refresh token - all sessions revoked for security");
            }
            throw new BusinessException("Refresh token expired");
        }

        // Revoke old token
        oldToken.revoke();
        refreshTokenRepository.save(oldToken);

        // Create new token
        RefreshToken newToken = createRefreshToken(oldToken.getUser());

        auditService.logAction(
                AuditLog.ActionType.LOGIN,
                "RefreshToken",
                newToken.getId(),
                "Token rotado para usuario: " + oldToken.getUser().getEmail()
        );

        log.debug("Rotated refresh token for user: {}", oldToken.getUser().getEmail());
        return newToken;
    }

    /**
     * Verify if refresh token is valid
     */
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token not found"));

        if (!refreshToken.isValid()) {
            throw new BusinessException("Refresh token expired or revoked");
        }

        return refreshToken;
    }

    /**
     * Revoke refresh token (logout)
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token not found"));

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

        auditService.logAction(
                AuditLog.ActionType.LOGOUT,
                "RefreshToken",
                refreshToken.getId(),
                "Token revocado para usuario: " + refreshToken.getUser().getEmail()
        );

        log.debug("Revoked refresh token for user: {}", refreshToken.getUser().getEmail());
    }

    /**
     * Revoke all refresh tokens for a user (logout from all devices)
     */
    @Transactional
    public void revokeAllUserTokens(User user) {
        int revokedCount = refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now());

        auditService.logAction(
                AuditLog.ActionType.LOGOUT,
                "User",
                user.getId(),
                "Todos los tokens revocados para usuario: " + user.getEmail() + " (" + revokedCount + " tokens)"
        );

        log.info("Revoked {} refresh tokens for user: {}", revokedCount, user.getEmail());
    }

    /**
     * Cleanup expired and revoked tokens older than 30 days
     */
    @Transactional
    public int cleanupOldTokens() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        int deletedCount = refreshTokenRepository.deleteExpiredAndRevokedTokens(cutoffDate);
        log.info("Cleaned up {} old refresh tokens", deletedCount);
        return deletedCount;
    }

    /**
     * Get client IP address from request
     */
    private String getClientIP() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                    return xRealIp;
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("Could not get client IP: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * Get user agent from request
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.debug("Could not get user agent: {}", e.getMessage());
        }
        return "unknown";
    }
}
