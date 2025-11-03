-- =====================================================
-- REFRESH TOKENS TABLE - Token Rotation System
-- =====================================================
-- Purpose: Store refresh tokens with automatic rotation for enhanced security
-- Features: Token rotation, revocation, IP tracking, automatic cleanup
-- Created: 2025-10-28
-- =====================================================

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- INDICES FOR PERFORMANCE
-- =====================================================

-- Index for token lookup (most frequent operation)
CREATE INDEX IF NOT EXISTS idx_refresh_token ON refresh_tokens(token);

-- Index for user token queries
CREATE INDEX IF NOT EXISTS idx_refresh_user ON refresh_tokens(user_id);

-- Index for cleanup queries by expiry date
CREATE INDEX IF NOT EXISTS idx_refresh_expiry ON refresh_tokens(expiry_date);

-- Composite index for active tokens queries
CREATE INDEX IF NOT EXISTS idx_refresh_active ON refresh_tokens(user_id, revoked, expiry_date);

-- =====================================================
-- COMMENTS FOR DOCUMENTATION
-- =====================================================

COMMENT ON TABLE refresh_tokens IS 'Stores refresh tokens with automatic rotation for secure JWT authentication';
COMMENT ON COLUMN refresh_tokens.token IS 'UUID refresh token string (512 chars for future-proofing)';
COMMENT ON COLUMN refresh_tokens.user_id IS 'Foreign key to users table';
COMMENT ON COLUMN refresh_tokens.expiry_date IS 'Token expiration timestamp (default 7 days)';
COMMENT ON COLUMN refresh_tokens.revoked IS 'Token revocation status (for logout and rotation)';
COMMENT ON COLUMN refresh_tokens.created_at IS 'Token creation timestamp';
COMMENT ON COLUMN refresh_tokens.revoked_at IS 'Timestamp when token was revoked';
COMMENT ON COLUMN refresh_tokens.ip_address IS 'Client IP address (supports IPv6 - 45 chars)';
COMMENT ON COLUMN refresh_tokens.user_agent IS 'Client User-Agent string for tracking';

-- =====================================================
-- EXAMPLE QUERIES
-- =====================================================

-- Find all active tokens for a user
-- SELECT * FROM refresh_tokens 
-- WHERE user_id = 1 AND revoked = FALSE AND expiry_date > NOW();

-- Revoke all tokens for a user (logout all sessions)
-- UPDATE refresh_tokens 
-- SET revoked = TRUE, revoked_at = NOW() 
-- WHERE user_id = 1 AND revoked = FALSE;

-- Cleanup old tokens (30+ days)
-- DELETE FROM refresh_tokens 
-- WHERE expiry_date < NOW() - INTERVAL '30 days' 
--    OR (revoked = TRUE AND revoked_at < NOW() - INTERVAL '30 days');

-- Count active tokens per user
-- SELECT user_id, COUNT(*) as active_tokens 
-- FROM refresh_tokens 
-- WHERE revoked = FALSE AND expiry_date > NOW() 
-- GROUP BY user_id;

-- Find suspicious activity (multiple IPs for same user)
-- SELECT user_id, COUNT(DISTINCT ip_address) as ip_count
-- FROM refresh_tokens
-- WHERE created_at > NOW() - INTERVAL '24 hours'
-- GROUP BY user_id
-- HAVING COUNT(DISTINCT ip_address) > 3;
