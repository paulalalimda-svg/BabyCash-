-- ==============================================
-- BabyCash - Database Views
-- PostgreSQL Vistas para optimización
-- ==============================================

-- View: Dashboard Overview
CREATE OR REPLACE VIEW v_dashboard_overview AS
SELECT
    (SELECT COUNT(*) FROM users) AS total_users,
    (SELECT COUNT(*) FROM users WHERE created_at >= CURRENT_DATE - INTERVAL '30 days') AS new_users_this_month,
    (SELECT COUNT(*) FROM orders) AS total_orders,
    (SELECT COUNT(*) FROM orders WHERE status = 'PENDING') AS pending_orders,
    (SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status != 'CANCELLED') AS total_revenue,
    (SELECT COUNT(*) FROM products) AS total_products,
    (SELECT COUNT(*) FROM products WHERE stock < 10) AS low_stock_products;

-- View: Sales Summary
CREATE OR REPLACE VIEW v_sales_summary AS
SELECT
    DATE_TRUNC('day', o.created_at) AS sale_date,
    COUNT(o.id) AS total_orders,
    COALESCE(SUM(o.total_amount), 0) AS total_revenue,
    COALESCE(AVG(o.total_amount), 0) AS avg_order_value,
    COUNT(DISTINCT o.user_id) AS unique_customers
FROM orders o
WHERE o.status != 'CANCELLED'
GROUP BY DATE_TRUNC('day', o.created_at)
ORDER BY sale_date DESC;

-- View: Top Products
CREATE OR REPLACE VIEW v_top_products AS
SELECT
    p.id AS product_id,
    p.name AS product_name,
    p.category,
    p.price,
    p.stock,
    COALESCE(SUM(oi.quantity), 0) AS total_sold,
    COALESCE(SUM(oi.subtotal), 0) AS total_revenue,
    COUNT(DISTINCT oi.order_id) AS order_count
FROM products p
LEFT JOIN order_items oi ON p.id = oi.product_id
LEFT JOIN orders o ON oi.order_id = o.id AND o.status != 'CANCELLED'
GROUP BY p.id, p.name, p.category, p.price, p.stock
ORDER BY total_revenue DESC;

-- View: User Activity
CREATE OR REPLACE VIEW v_user_activity AS
SELECT
    u.id AS user_id,
    u.email,
    u.first_name,
    u.last_name,
    u.created_at AS member_since,
    u.email_verified,
    COUNT(o.id) AS total_orders,
    COALESCE(SUM(o.total_amount), 0) AS total_spent,
    COALESCE(lp.total_points, 0) AS loyalty_points,
    MAX(o.created_at) AS last_order_date
FROM users u
LEFT JOIN orders o ON u.id = o.user_id AND o.status != 'CANCELLED'
LEFT JOIN loyalty_points lp ON u.id = lp.user_id
GROUP BY u.id, u.email, u.first_name, u.last_name, u.created_at, u.email_verified, lp.total_points
ORDER BY total_spent DESC;

-- View: Order Details
CREATE OR REPLACE VIEW v_order_details AS
SELECT
    o.id AS order_id,
    o.order_number,
    o.created_at AS order_date,
    o.status,
    o.total_amount,
    o.shipping_address,
    o.notes,
    u.id AS user_id,
    u.email AS user_email,
    u.first_name AS user_first_name,
    u.last_name AS user_last_name,
    COUNT(oi.id) AS item_count,
    STRING_AGG(DISTINCT p.category, ', ') AS categories
FROM orders o
JOIN users u ON o.user_id = u.id
LEFT JOIN order_items oi ON o.id = oi.order_id
LEFT JOIN products p ON oi.product_id = p.id
GROUP BY o.id, o.order_number, o.created_at, o.status, o.total_amount,
         o.shipping_address, o.notes, u.id, u.email, u.first_name, u.last_name
ORDER BY o.created_at DESC;

-- View: Product Inventory
CREATE OR REPLACE VIEW v_product_inventory AS
SELECT
    p.id,
    p.name,
    p.category,
    p.price,
    p.discount_price,
    p.stock,
    p.featured,
    CASE
        WHEN p.stock = 0 THEN 'OUT_OF_STOCK'
        WHEN p.stock < 10 THEN 'LOW_STOCK'
        WHEN p.stock < 50 THEN 'MEDIUM_STOCK'
        ELSE 'GOOD_STOCK'
    END AS stock_status,
    COALESCE(SUM(oi.quantity), 0) AS units_sold_last_30_days
FROM products p
LEFT JOIN order_items oi ON p.id = oi.product_id
LEFT JOIN orders o ON oi.order_id = o.id
    AND o.created_at >= CURRENT_DATE - INTERVAL '30 days'
    AND o.status != 'CANCELLED'
GROUP BY p.id, p.name, p.category, p.price, p.discount_price, p.stock, p.featured
ORDER BY units_sold_last_30_days DESC;

-- View: Monthly Revenue
CREATE OR REPLACE VIEW v_monthly_revenue AS
SELECT
    DATE_TRUNC('month', o.created_at) AS month,
    TO_CHAR(DATE_TRUNC('month', o.created_at), 'YYYY-MM') AS month_label,
    COUNT(o.id) AS total_orders,
    COALESCE(SUM(o.total_amount), 0) AS total_revenue,
    COALESCE(AVG(o.total_amount), 0) AS avg_order_value,
    COUNT(DISTINCT o.user_id) AS unique_customers
FROM orders o
WHERE o.status != 'CANCELLED'
  AND o.created_at >= DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '12 months'
GROUP BY DATE_TRUNC('month', o.created_at)
ORDER BY month DESC;

-- View: Testimonials Summary
CREATE OR REPLACE VIEW v_testimonials_summary AS
SELECT
    t.id,
    t.name,
    t.message,
    t.rating,
    t.location,
    t.approved,
    t.featured,
    t.created_at,
    CASE
        WHEN t.approved = TRUE THEN 'APPROVED'
        ELSE 'PENDING'
    END AS status
FROM testimonials t
ORDER BY t.created_at DESC;

-- View: Blog Posts Summary
CREATE OR REPLACE VIEW v_blog_posts_summary AS
SELECT
    bp.id,
    bp.title,
    bp.slug,
    bp.excerpt,
    bp.published,
    bp.featured,
    bp.view_count,
    bp.created_at,
    bp.published_at,
    u.first_name || ' ' || u.last_name AS author_name,
    u.email AS author_email,
    (SELECT COUNT(*) FROM blog_comments bc WHERE bc.blog_post_id = bp.id AND bc.approved = TRUE) AS comment_count
FROM blog_posts bp
JOIN users u ON bp.author_id = u.id
ORDER BY bp.created_at DESC;

-- View: Loyalty Points Summary
CREATE OR REPLACE VIEW v_loyalty_summary AS
SELECT
    lp.user_id,
    u.email,
    u.first_name,
    u.last_name,
    lp.total_points,
    lp.tier,
    CASE lp.tier
        WHEN 'BRONZE' THEN '🥉 Bronze'
        WHEN 'SILVER' THEN '🥈 Silver'
        WHEN 'GOLD' THEN '🥇 Gold'
        ELSE '⭐ Member'
    END AS tier_display,
    FLOOR(lp.total_points / 1000) * 5 AS available_discount_percent,
    (SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE user_id = lp.user_id AND status != 'CANCELLED') AS total_spent
FROM loyalty_points lp
JOIN users u ON lp.user_id = u.id
ORDER BY lp.total_points DESC;

-- ===================================================================
-- Usage Examples:
-- ===================================================================
-- SELECT * FROM v_dashboard_overview;
-- SELECT * FROM v_sales_summary LIMIT 30;
-- SELECT * FROM v_top_products LIMIT 10;
-- SELECT * FROM v_user_activity WHERE total_orders > 0;
-- SELECT * FROM v_order_details WHERE status = 'PENDING';
-- SELECT * FROM v_product_inventory WHERE stock_status IN ('LOW_STOCK', 'OUT_OF_STOCK');
-- SELECT * FROM v_monthly_revenue;
-- SELECT * FROM v_testimonials_summary WHERE status = 'PENDING';
-- SELECT * FROM v_blog_posts_summary WHERE published = TRUE;
-- SELECT * FROM v_loyalty_summary WHERE tier = 'GOLD';
