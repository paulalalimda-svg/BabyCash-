-- =============================================================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS - BabyCash
-- =============================================================================
-- Descripción: Script SQL para crear la base de datos y todas las tablas
-- Versión: 1.0
-- Fecha: 2025-11-23
-- Base de datos: PostgreSQL
-- =============================================================================

-- Crear la base de datos (ejecutar primero si no existe)
-- CREATE DATABASE babycash;

-- Conectarse a la base de datos babycash
-- Nota: Conéctate a la base de datos `babycash` desde pgAdmin antes de ejecutar
-- este script. La línea psql "\c babycash" se ha eliminado porque no funciona
-- dentro del Query Tool de pgAdmin.

-- =============================================================================
-- TABLA: users
-- Descripción: Almacena información de usuarios del sistema
-- =============================================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(500),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_token VARCHAR(255),
    reset_password_token VARCHAR(255),
    reset_password_expiry TIMESTAMP,
    refresh_token TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('USER', 'ADMIN'))
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_reset_token ON users(reset_password_token);

-- =============================================================================
-- TABLA: products
-- Descripción: Almacena el catálogo de productos
-- =============================================================================
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    discount_price DECIMAL(10, 2),
    category VARCHAR(50) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    rating DECIMAL(3, 2) DEFAULT 0.00,
    review_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_product_category CHECK (category IN ('ROPA', 'JUGUETES', 'ALIMENTACION', 'HIGIENE', 'ACCESORIOS', 'OTROS'))
);

-- Índices
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_featured ON products(featured);
CREATE INDEX idx_products_enabled ON products(enabled);

-- =============================================================================
-- TABLA: carts
-- Descripción: Carritos de compras de los usuarios
-- =============================================================================
CREATE TABLE IF NOT EXISTS carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_carts_user ON carts(user_id);

-- =============================================================================
-- TABLA: cart_items
-- Descripción: Items individuales en el carrito de compras
-- =============================================================================
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product ON cart_items(product_id);

-- =============================================================================
-- TABLA: orders
-- Descripción: Órdenes de compra realizadas por los usuarios
-- =============================================================================
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL,
    shipping_address TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'))
);

-- Índices
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_number ON orders(order_number);

-- =============================================================================
-- TABLA: order_items
-- Descripción: Items individuales de cada orden
-- =============================================================================
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

-- =============================================================================
-- TABLA: payments
-- Descripción: Pagos asociados a las órdenes
-- =============================================================================
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(10, 2) NOT NULL,
    transaction_id VARCHAR(255) UNIQUE,
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT chk_payment_method CHECK (method IN ('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CASH')),
    CONSTRAINT chk_payment_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'))
);

-- Índices
CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_transaction ON payments(transaction_id);

-- =============================================================================
-- TABLA: blog_posts
-- Descripción: Publicaciones del blog
-- =============================================================================
CREATE TABLE IF NOT EXISTS blog_posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(250) NOT NULL UNIQUE,
    excerpt TEXT,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    image_url VARCHAR(500),
    published BOOLEAN NOT NULL DEFAULT FALSE,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    view_count BIGINT NOT NULL DEFAULT 0,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_blog_post_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_blog_posts_author ON blog_posts(author_id);
CREATE INDEX idx_blog_posts_slug ON blog_posts(slug);
CREATE INDEX idx_blog_posts_published ON blog_posts(published);
CREATE INDEX idx_blog_posts_featured ON blog_posts(featured);

-- =============================================================================
-- TABLA: blog_post_tags
-- Descripción: Etiquetas de las publicaciones del blog
-- =============================================================================
CREATE TABLE IF NOT EXISTS blog_post_tags (
    blog_post_id BIGINT NOT NULL,
    tag VARCHAR(255) NOT NULL,
    CONSTRAINT fk_blog_tag_post FOREIGN KEY (blog_post_id) REFERENCES blog_posts(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_blog_tags_post ON blog_post_tags(blog_post_id);
CREATE INDEX idx_blog_tags_tag ON blog_post_tags(tag);

-- =============================================================================
-- TABLA: blog_comments
-- Descripción: Comentarios en las publicaciones del blog
-- =============================================================================
CREATE TABLE IF NOT EXISTS blog_comments (
    id BIGSERIAL PRIMARY KEY,
    blog_post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_comment_id BIGINT,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_blog_comment_post FOREIGN KEY (blog_post_id) REFERENCES blog_posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_blog_comment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_blog_comment_parent FOREIGN KEY (parent_comment_id) REFERENCES blog_comments(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_blog_comments_post ON blog_comments(blog_post_id);
CREATE INDEX idx_blog_comments_user ON blog_comments(user_id);
CREATE INDEX idx_blog_comments_approved ON blog_comments(approved);

-- =============================================================================
-- TABLA: loyalty_points
-- Descripción: Sistema de puntos de lealtad
-- =============================================================================
CREATE TABLE IF NOT EXISTS loyalty_points (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    points INTEGER NOT NULL,
    amount_spent DECIMAL(10, 2),
    order_id BIGINT,
    description VARCHAR(500),
    expires_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loyalty_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_loyalty_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    CONSTRAINT chk_loyalty_transaction_type CHECK (transaction_type IN ('EARNED', 'REDEEMED', 'EXPIRED', 'BONUS'))
);

-- Índices
CREATE INDEX idx_loyalty_points_user ON loyalty_points(user_id);
CREATE INDEX idx_loyalty_points_order ON loyalty_points(order_id);
CREATE INDEX idx_loyalty_points_active ON loyalty_points(active);

-- =============================================================================
-- TABLA: refresh_tokens
-- Descripción: Tokens de actualización para JWT
-- =============================================================================
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

-- Índices
CREATE INDEX idx_refresh_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_expiry ON refresh_tokens(expiry_date);

-- =============================================================================
-- TABLA: contact_messages
-- Descripción: Mensajes del formulario de contacto
-- =============================================================================
CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    subject VARCHAR(150) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    read_at TIMESTAMP,
    replied_at TIMESTAMP,
    admin_notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_contact_status CHECK (status IN ('NEW', 'READ', 'REPLIED', 'ARCHIVED'))
);

-- Índices
CREATE INDEX idx_contact_messages_status ON contact_messages(status);
CREATE INDEX idx_contact_messages_email ON contact_messages(email);

-- =============================================================================
-- TABLA: testimonials
-- Descripción: Testimonios de clientes
-- =============================================================================
CREATE TABLE IF NOT EXISTS testimonials (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    rating INTEGER NOT NULL DEFAULT 5,
    avatar VARCHAR(500),
    location VARCHAR(100),
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_testimonial_rating CHECK (rating >= 1 AND rating <= 5)
);

-- Índices
CREATE INDEX idx_testimonials_approved ON testimonials(approved);
CREATE INDEX idx_testimonials_featured ON testimonials(featured);

-- =============================================================================
-- TABLA: contact_info
-- Descripción: Información de contacto de la empresa (singleton)
-- =============================================================================
CREATE TABLE IF NOT EXISTS contact_info (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100),
    country VARCHAR(100),
    facebook VARCHAR(200),
    instagram VARCHAR(200),
    twitter VARCHAR(200),
    whatsapp VARCHAR(200),
    business_hours VARCHAR(100),
    business_hours_details TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- =============================================================================
-- INSERTAR DATOS INICIALES
-- =============================================================================

-- Insertar usuario administrador por defecto
-- Contraseña: admin123 (debe ser hasheada en producción)
INSERT INTO users (email, password, first_name, last_name, role, enabled, email_verified)
VALUES ('admin@babycash.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J0tSfXOGCx0WqP/3rkuJtl8w/J9xUG', 'Admin', 'BabyCash', 'ADMIN', TRUE, TRUE)
ON CONFLICT (email) DO NOTHING;

-- Insertar información de contacto por defecto
-- Insertar información de contacto por defecto sólo si no existe aún (singleton)
INSERT INTO contact_info (company_name, phone, email, address, city, country, description)
SELECT
    'BabyCash',
    '+57 123 456 7890',
    'contacto@babycash.com',
    'Calle Principal 123',
    'Bogotá',
    'Colombia',
    'Tu tienda de confianza para productos de bebés y niños'
WHERE NOT EXISTS (SELECT 1 FROM contact_info);

-- =============================================================================
-- FIN DEL SCRIPT
-- =============================================================================
COMMIT;

-- Mensaje de confirmación
SELECT 'Base de datos BabyCash creada exitosamente' AS mensaje;
