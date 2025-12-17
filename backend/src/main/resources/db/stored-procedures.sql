-- ==============================================
-- BabyCash - Stored Procedures
-- PostgreSQL Procedimientos Almacenados
-- ==============================================

-- Procedure: Get Dashboard Statistics
CREATE OR REPLACE FUNCTION get_dashboard_stats()
RETURNS TABLE (
    total_users BIGINT,
    total_orders BIGINT,
    total_revenue NUMERIC,
    total_products BIGINT,
    pending_orders BIGINT,
    active_users_last_30_days BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        (SELECT COUNT(*) FROM users) AS total_users,
        (SELECT COUNT(*) FROM orders) AS total_orders,
        (SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status != 'CANCELLED') AS total_revenue,
        (SELECT COUNT(*) FROM products) AS total_products,
        (SELECT COUNT(*) FROM orders WHERE status = 'PENDING') AS pending_orders,
        (SELECT COUNT(DISTINCT user_id) FROM orders WHERE created_at >= CURRENT_DATE - INTERVAL '30 days') AS active_users_last_30_days;
END;
$$ LANGUAGE plpgsql;

-- Procedure: Get Sales Report by Period
CREATE OR REPLACE FUNCTION get_sales_by_period(
    p_start_date DATE,
    p_end_date DATE
)
RETURNS TABLE (
    date DATE,
    total_orders BIGINT,
    total_revenue NUMERIC,
    avg_order_value NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        o.created_at::DATE AS date,
        COUNT(o.id) AS total_orders,
        COALESCE(SUM(o.total_amount), 0) AS total_revenue,
        COALESCE(AVG(o.total_amount), 0) AS avg_order_value
    FROM orders o
    WHERE o.created_at::DATE BETWEEN p_start_date AND p_end_date
        AND o.status != 'CANCELLED'
    GROUP BY o.created_at::DATE
    ORDER BY date DESC;
END;
$$ LANGUAGE plpgsql;

-- Procedure: Get Top Selling Products
CREATE OR REPLACE FUNCTION get_top_selling_products(p_limit INTEGER DEFAULT 10)
RETURNS TABLE (
    product_id BIGINT,
    product_name VARCHAR,
    total_quantity BIGINT,
    total_revenue NUMERIC,
    order_count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        oi.product_id,
        oi.product_name,
        SUM(oi.quantity) AS total_quantity,
        SUM(oi.subtotal) AS total_revenue,
        COUNT(DISTINCT oi.order_id) AS order_count
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.id
    WHERE o.status != 'CANCELLED'
    GROUP BY oi.product_id, oi.product_name
    ORDER BY total_revenue DESC
    LIMIT p_limit;
END;
$$ LANGUAGE plpgsql;

-- Procedure: Get User Activity Summary
CREATE OR REPLACE FUNCTION get_user_activity_summary(p_user_id BIGINT)
RETURNS TABLE (
    total_orders BIGINT,
    total_spent NUMERIC,
    loyalty_points INTEGER,
    member_since DATE,
    last_order_date TIMESTAMP,
    favorite_category VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(o.id) AS total_orders,
        COALESCE(SUM(o.total_amount), 0) AS total_spent,
        COALESCE(lp.total_points, 0) AS loyalty_points,
        u.created_at::DATE AS member_since,
        MAX(o.created_at) AS last_order_date,
        (
            SELECT p.category
            FROM order_items oi
            JOIN orders o2 ON oi.order_id = o2.id
            JOIN products p ON oi.product_id = p.id
            WHERE o2.user_id = p_user_id
            GROUP BY p.category
            ORDER BY COUNT(*) DESC
            LIMIT 1
        ) AS favorite_category
    FROM users u
    LEFT JOIN orders o ON u.id = o.user_id AND o.status != 'CANCELLED'
    LEFT JOIN loyalty_points lp ON u.id = lp.user_id
    WHERE u.id = p_user_id
    GROUP BY u.id, u.created_at, lp.total_points;
END;
$$ LANGUAGE plpgsql;

-- Procedure: Calculate Loyalty Points for Order
CREATE OR REPLACE FUNCTION calculate_loyalty_points(p_order_amount NUMERIC)
RETURNS INTEGER AS $$
BEGIN
    -- 1 point per 1000 COP spent
    RETURN FLOOR(p_order_amount / 1000)::INTEGER;
END;
$$ LANGUAGE plpgsql;

-- Procedure: Get Monthly Revenue Trend
CREATE OR REPLACE FUNCTION get_monthly_revenue_trend(p_months INTEGER DEFAULT 12)
RETURNS TABLE (
    month DATE,
    total_revenue NUMERIC,
    total_orders BIGINT,
    avg_order_value NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        DATE_TRUNC('month', o.created_at)::DATE AS month,
        COALESCE(SUM(o.total_amount), 0) AS total_revenue,
        COUNT(o.id) AS total_orders,
        COALESCE(AVG(o.total_amount), 0) AS avg_order_value
    FROM orders o
    WHERE o.created_at >= CURRENT_DATE - (p_months || ' months')::INTERVAL
        AND o.status != 'CANCELLED'
    GROUP BY DATE_TRUNC('month', o.created_at)
    ORDER BY month DESC;
END;
$$ LANGUAGE plpgsql;

-- Procedure: Get Order Status Distribution
CREATE OR REPLACE FUNCTION get_order_status_distribution()
RETURNS TABLE (
    status VARCHAR,
    count BIGINT,
    percentage NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    WITH total_count AS (
        SELECT COUNT(*)::NUMERIC AS total FROM orders
    )
    SELECT
        o.status::VARCHAR,
        COUNT(o.id) AS count,
        ROUND((COUNT(o.id)::NUMERIC / tc.total * 100), 2) AS percentage
    FROM orders o
    CROSS JOIN total_count tc
    GROUP BY o.status, tc.total
    ORDER BY count DESC;
END;
$$ LANGUAGE plpgsql;

-- Procedure: Cleanup Expired Verification Tokens
CREATE OR REPLACE FUNCTION cleanup_expired_tokens()
RETURNS TABLE (
    cleaned_users INTEGER,
    cleaned_reset_tokens INTEGER
) AS $$
DECLARE
    v_cleaned_users INTEGER;
    v_cleaned_reset_tokens INTEGER;
BEGIN
    -- Clear expired verification tokens (older than 7 days)
    UPDATE users
    SET verification_token = NULL
    WHERE verification_token IS NOT NULL
        AND created_at < CURRENT_TIMESTAMP - INTERVAL '7 days'
        AND email_verified = false;
    GET DIAGNOSTICS v_cleaned_users = ROW_COUNT;

    -- Clear expired password reset tokens
    UPDATE users
    SET reset_password_token = NULL,
        reset_password_expiry = NULL
    WHERE reset_password_token IS NOT NULL
        AND reset_password_expiry < CURRENT_TIMESTAMP;
    GET DIAGNOSTICS v_cleaned_reset_tokens = ROW_COUNT;

    RETURN QUERY SELECT v_cleaned_users, v_cleaned_reset_tokens;
END;
$$ LANGUAGE plpgsql;

-- ===================================================================
-- Usage Examples:
-- ===================================================================
-- SELECT * FROM get_dashboard_stats();
-- SELECT * FROM get_sales_by_period('2024-01-01', '2024-12-31');
-- SELECT * FROM get_top_selling_products(10);
-- SELECT * FROM get_user_activity_summary(1);
-- SELECT calculate_loyalty_points(150000);
-- SELECT * FROM get_monthly_revenue_trend(6);
-- SELECT * FROM get_order_status_distribution();
-- SELECT * FROM cleanup_expired_tokens();
