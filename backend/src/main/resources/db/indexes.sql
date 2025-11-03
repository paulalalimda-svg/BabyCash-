-- =========================================
-- INDICES PARA OPTIMIZACIÓN DE PERFORMANCE
-- BabyCash Backend
-- =========================================

-- Índice único en email de usuarios (mejora login y búsqueda)
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email 
ON users(email);

-- Índice en categoría de productos (mejora filtros por categoría)
CREATE INDEX IF NOT EXISTS idx_products_category 
ON products(category) 
WHERE enabled = true;

-- Índice en productos destacados (mejora queries de featured)
CREATE INDEX IF NOT EXISTS idx_products_featured 
ON products(featured, enabled) 
WHERE featured = true AND enabled = true;

-- Índice en stock de productos (para búsquedas de disponibilidad)
CREATE INDEX IF NOT EXISTS idx_products_stock 
ON products(stock) 
WHERE enabled = true AND stock > 0;

-- Índice único en número de orden (mejora búsqueda por orderNumber)
CREATE UNIQUE INDEX IF NOT EXISTS idx_orders_order_number 
ON orders(order_number);

-- Índice compuesto en órdenes por usuario y fecha (mejora historial)
CREATE INDEX IF NOT EXISTS idx_orders_user_created 
ON orders(user_id, created_at DESC);

-- Índice en estado de órdenes (mejora filtros por status)
CREATE INDEX IF NOT EXISTS idx_orders_status 
ON orders(status);

-- Índice en order_id de payments (mejora búsqueda de pagos)
CREATE INDEX IF NOT EXISTS idx_payments_order_id 
ON payments(order_id);

-- Índice en user_id de carritos (mejora acceso a carrito por usuario)
CREATE INDEX IF NOT EXISTS idx_carts_user_id 
ON carts(user_id);

-- Índice compuesto en items del carrito (mejora operaciones de carrito)
CREATE INDEX IF NOT EXISTS idx_cart_items_cart_product 
ON cart_items(cart_id, product_id);

-- Índice en order_id de order_items (mejora carga de items de orden)
CREATE INDEX IF NOT EXISTS idx_order_items_order_id 
ON order_items(order_id);

-- =========================================
-- ESTADÍSTICAS Y ANÁLISIS
-- =========================================

-- Actualizar estadísticas de las tablas para mejorar el plan de ejecución
ANALYZE users;
ANALYZE products;
ANALYZE orders;
ANALYZE order_items;
ANALYZE payments;
ANALYZE carts;
ANALYZE cart_items;

-- =========================================
-- VERIFICACIÓN DE ÍNDICES
-- =========================================

-- Query para verificar los índices creados
-- SELECT 
--     schemaname,
--     tablename,
--     indexname,
--     indexdef
-- FROM pg_indexes
-- WHERE schemaname = 'public'
-- ORDER BY tablename, indexname;
