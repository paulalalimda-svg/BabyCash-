-- ============================================================================
-- BABYCASH - Script de Datos de Prueba (PostgreSQL - Corregido)
-- ============================================================================

-- Limpiar datos existentes (en orden inverso de dependencias)
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM blog_comments;
DELETE FROM blog_posts;
DELETE FROM testimonials;
DELETE FROM contact_messages;
DELETE FROM products;
DELETE FROM users;

-- ============================================================================
-- 1. USUARIOS
-- ============================================================================

INSERT INTO users (id, first_name, last_name, email, password, phone, address, role, enabled, email_verified, created_at, updated_at) VALUES
(1, 'Administrador', 'Principal', '202215.clv@gmail.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 300 1234567', 'Calle 123 #45-67, Bogotá', 'ADMIN', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'María', 'García', 'maria.garcia@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 310 2345678', 'Carrera 15 #89-12, Medellín', 'USER', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Juan', 'Pérez', 'juan.perez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 320 3456789', 'Avenida 68 #34-56, Bogotá', 'USER', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Ana', 'Martínez', 'ana.martinez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 315 4567890', 'Calle 80 #12-34, Cali', 'USER', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Carlos', 'López', 'carlos.lopez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 311 5678901', 'Carrera 7 #45-67, Barranquilla', 'USER', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Laura', 'Rodríguez', 'laura.rodriguez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 312 6789012', 'Calle 100 #15-23, Bogotá', 'USER', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Moderador', 'Sistema', 'moderador@babycash.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 301 7890123', 'Centro Comercial, Bogotá', 'MODERATOR', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- ============================================================================
-- 2. PRODUCTOS (50 productos)
-- ============================================================================

-- Cuidado del Bebé (BABY_CARE)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, enabled, featured, rating, review_count, created_at, updated_at) VALUES
(1, 'Pañales Premium Talla M', 'Pañales ultra absorbentes con tecnología de gel. Paquete de 80 unidades. Perfectos para bebés de 6-10 kg.', 89900, 79900, 'BABY_CARE', 150, 'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4', true, true, 4.8, 45, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Toallitas Húmedas x 3 paquetes', 'Toallitas suaves con aloe vera. Sin alcohol. 3 paquetes de 80 unidades cada uno.', 45000, 39900, 'BABY_CARE', 200, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.7, 38, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Shampoo y Jabón Líquido 2 en 1', 'Fórmula suave hipoalergénica. Sin parabenos. 500ml.', 32000, NULL, 'BABY_CARE', 85, 'https://images.unsplash.com/photo-1556228852-80a8d4b2d0e8', true, false, 4.6, 22, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Crema para Rozaduras', 'Protección 24 horas. Con óxido de zinc. 100g.', 28000, 24900, 'BABY_CARE', 120, 'https://images.unsplash.com/photo-1576426863848-c21f53c60b19', true, false, 4.9, 56, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Aceite Corporal para Bebé', 'Con vitamina E y aceite de almendras. 200ml.', 35000, NULL, 'BABY_CARE', 65, 'https://images.unsplash.com/photo-1608181297027-f1d0e5e85a57', true, false, 4.5, 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Ropa (CLOTHING)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, enabled, featured, rating, review_count, created_at, updated_at) VALUES
(6, 'Bodys Pack x 5 unidades', 'Algodón 100%. Tallas 0-3 meses. Colores variados.', 65000, 54900, 'CLOTHING', 80, 'https://images.unsplash.com/photo-1519689373023-dd07c7988603', true, true, 4.8, 62, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Conjunto 3 piezas Niña', 'Vestido, bloomers y diadema. Rosa pastel. 0-6 meses.', 78000, 69900, 'CLOTHING', 45, 'https://images.unsplash.com/photo-1519238263530-99bdd11df2ea', true, true, 4.9, 41, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Alimentación (FEEDING)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, enabled, featured, rating, review_count, created_at, updated_at) VALUES
(8, 'Biberones Anticólicos x 3', 'Sistema anticólicos. 260ml cada uno. BPA free.', 78000, 69900, 'FEEDING', 90, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.9, 78, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'Esterilizador UV Eléctrico', 'Elimina 99.9% de gérmenes. Capacidad 6 biberones.', 185000, 159900, 'FEEDING', 20, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 5.0, 34, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Extractor de Leche Eléctrico', 'Doble bomba. 10 niveles de succión. Silencioso.', 295000, 269900, 'FEEDING', 15, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.8, 29, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Juguetes (TOYS)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, enabled, featured, rating, review_count, created_at, updated_at) VALUES
(11, 'Gimnasio de Actividades', 'Con música y luces. Estimula desarrollo motor. 0+ meses.', 145000, 129900, 'TOYS', 40, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.9, 52, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'Andador Musical 3 en 1', 'Convierte en mesa de juegos. Luces y sonidos.', 225000, 199900, 'TOYS', 20, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.7, 36, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Muebles (FURNITURE)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, enabled, featured, rating, review_count, created_at, updated_at) VALUES
(13, 'Cuna Convertible 4 en 1', 'Se convierte en cama junior. Madera maciza. Colchón incluido.', 895000, 799900, 'FURNITURE', 12, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 5.0, 28, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'Cómoda Cambiador', '5 cajones. Superficie cambiador acolchada. Blanco.', 485000, 439900, 'FURNITURE', 18, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.8, 19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 'Coche Travel System', 'Incluye silla de auto. Desde nacimiento. Plegado compacto.', 1285000, 1199900, 'FURNITURE', 8, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.9, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 'Silla de Auto 0-36kg', 'Isofix. Gira 360°. 4 posiciones reclinables.', 785000, 729900, 'FURNITURE', 20, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.9, 42, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Otros (OTHER)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, enabled, featured, rating, review_count, created_at, updated_at) VALUES
(17, 'Monitor de Bebé con Cámara', 'Visión nocturna. Talk-back. Pantalla 5 pulgadas.', 365000, 329900, 'OTHER', 22, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.8, 67, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 'Humidificador Ultrasónico', 'Luz nocturna. Difusor de aromas. 3L capacidad.', 125000, 109900, 'OTHER', 35, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, false, 4.6, 31, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));

-- ============================================================================
-- 3. ÓRDENES (15 órdenes con diferentes estados)
-- ============================================================================

INSERT INTO orders (id, order_number, user_id, status, total_amount, shipping_address, created_at, updated_at) VALUES
(1, 'ORD-2025-001', 2, 'DELIVERED', 159800, 'Carrera 15 #89-12, Medellín', CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '25 days'),
(2, 'ORD-2025-002', 3, 'DELIVERED', 299700, 'Avenida 68 #34-56, Bogotá', CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '20 days'),
(3, 'ORD-2025-003', 4, 'DELIVERED', 89900, 'Calle 80 #12-34, Cali', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4, 'ORD-2025-004', 5, 'DELIVERED', 799900, 'Carrera 7 #45-67, Barranquilla', CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '12 days'),
(5, 'ORD-2025-005', 2, 'DELIVERED', 269900, 'Carrera 15 #89-12, Medellín', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '10 days'),
(6, 'ORD-2025-006', 6, 'SHIPPED', 139800, 'Calle 100 #15-23, Bogotá', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '9 days'),
(7, 'ORD-2025-007', 3, 'SHIPPED', 729900, 'Avenida 68 #34-56, Bogotá', CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '7 days'),
(8, 'ORD-2025-008', 4, 'SHIPPED', 329900, 'Calle 80 #12-34, Cali', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
(9, 'ORD-2025-009', 5, 'PROCESSING', 199800, 'Carrera 7 #45-67, Barranquilla', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(10, 'ORD-2025-010', 2, 'PROCESSING', 539800, 'Carrera 15 #89-12, Medellín', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(11, 'ORD-2025-011', 6, 'PROCESSING', 269900, 'Calle 100 #15-23, Bogotá', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
(12, 'ORD-2025-012', 3, 'PENDING', 799900, 'Avenida 68 #34-56, Bogotá', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'ORD-2025-013', 4, 'PENDING', 169900, 'Calle 80 #12-34, Cali', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'ORD-2025-014', 5, 'PENDING', 1199900, 'Carrera 7 #45-67, Barranquilla', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 'ORD-2025-015', 6, 'PENDING', 389700, 'Calle 100 #15-23, Bogotá', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));

-- Items de órdenes
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(1, 1, 2, 79900, 159800),
(2, 9, 1, 159900, 159900), (2, 8, 2, 69900, 139800),
(3, 2, 1, 39900, 39900), (3, 3, 1, 32000, 32000),
(4, 13, 1, 799900, 799900),
(5, 10, 1, 269900, 269900),
(6, 8, 2, 69900, 139800),
(7, 16, 1, 729900, 729900),
(8, 17, 1, 329900, 329900),
(9, 11, 1, 129900, 129900), (9, 8, 1, 69900, 69900),
(10, 14, 1, 439900, 439900),
(11, 10, 1, 269900, 269900),
(12, 13, 1, 799900, 799900),
(13, 9, 1, 159900, 159900),
(14, 15, 1, 1199900, 1199900),
(15, 17, 1, 329900, 329900);

-- ============================================================================
-- 4. BLOG POSTS (15 artículos)
-- ============================================================================

INSERT INTO blog_posts (id, title, slug, excerpt, content, image_url, author_id, published, featured, view_count, published_at, created_at, updated_at) VALUES
(1, 'Los primeros 100 días del bebé: Guía completa', 'primeros-100-dias-bebe-guia-completa', 
'Descubre todo lo que necesitas saber sobre los primeros meses de vida de tu bebé. Tips esenciales para padres primerizos.',
'## Introducción

Los primeros 100 días son cruciales para el desarrollo del bebé. En este artículo te compartimos todo lo que necesitas saber.

## Alimentación

La lactancia materna exclusiva es recomendada durante los primeros 6 meses.

## Sueño

Los recién nacidos duermen entre 16-17 horas al día.

## Desarrollo

Cada bebé se desarrolla a su propio ritmo.',
'https://images.unsplash.com/photo-1555252333-9f8e92e65df9', 1, true, true, 1250, CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP),

(2, 'Rutinas de sueño para bebés de 0-6 meses', 'rutinas-sueno-bebes-0-6-meses',
'Establece hábitos de sueño saludables desde temprano. Métodos probados y consejos prácticos.',
'## Importancia de las rutinas

Las rutinas ayudan al bebé a sentirse seguro.

## Rutina nocturna sugerida

1. Baño tibio
2. Masaje suave
3. Alimentación
4. Canción de cuna

## Consejos

- Ambiente oscuro y tranquilo
- Temperatura adecuada (20-22°C)',
'https://images.unsplash.com/photo-1555252333-9f8e92e65df9', 1, true, true, 1520, CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP),

(3, 'Lactancia materna: Mitos y realidades', 'lactancia-materna-mitos-realidades',
'Desmitificamos las creencias más comunes sobre la lactancia y te damos información basada en evidencia.',
'## Mito 1: La lactancia duele

Realidad: Con técnica correcta no debería doler.

## Mito 2: Hay que dar ambos pechos en cada toma

Realidad: El bebé decide cuándo está satisfecho.

## Beneficios comprobados

- Fortalece el sistema inmune
- Crea vínculo madre-hijo
- Nutrición perfecta',
'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4', 1, true, false, 1680, CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP);

SELECT setval('blog_posts_id_seq', (SELECT MAX(id) FROM blog_posts));

-- ============================================================================
-- 5. COMENTARIOS DEL BLOG
-- ============================================================================

INSERT INTO blog_comments (id, blog_post_id, user_id, content, parent_comment_id, approved, created_at, updated_at) VALUES
(1, 1, 2, 'Excelente artículo! Me ayudó mucho con mi primera hija. ¿Podrían hacer uno sobre cólicos?', NULL, true, CURRENT_TIMESTAMP - INTERVAL '44 days', CURRENT_TIMESTAMP - INTERVAL '44 days'),
(2, 1, 1, 'Gracias María! Tomaremos en cuenta tu sugerencia para un próximo artículo sobre cólicos.', 1, true, CURRENT_TIMESTAMP - INTERVAL '43 days', CURRENT_TIMESTAMP - INTERVAL '43 days'),
(3, 1, 3, 'Muy útil, aunque mi bebé duerme menos de lo que mencionan. ¿Es normal?', NULL, true, CURRENT_TIMESTAMP - INTERVAL '42 days', CURRENT_TIMESTAMP - INTERVAL '42 days'),
(4, 2, 4, 'Las rutinas realmente funcionan! Mi bebé ahora duerme toda la noche.', NULL, true, CURRENT_TIMESTAMP - INTERVAL '33 days', CURRENT_TIMESTAMP - INTERVAL '33 days'),
(5, 3, 6, 'Gracias por desmitificar estos temas. La lactancia es hermosa pero necesita apoyo.', NULL, true, CURRENT_TIMESTAMP - INTERVAL '21 days', CURRENT_TIMESTAMP - INTERVAL '21 days');

SELECT setval('blog_comments_id_seq', (SELECT MAX(id) FROM blog_comments));

-- ============================================================================
-- 6. TESTIMONIOS (20 testimonios con diferentes estados)
-- ============================================================================

INSERT INTO testimonials (id, name, message, rating, avatar, location, approved, featured, created_at, updated_at) VALUES
-- 5 DESTACADOS (APPROVED + FEATURED)
(1, 'María García', 'Excelente servicio y productos de alta calidad. Mi bebé está encantado con todos los juguetes que compré. La entrega fue rápida y todo llegó en perfecto estado. ¡100% recomendado!', 5, 'https://i.pravatar.cc/150?img=1', 'Medellín', true, true, CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP - INTERVAL '35 days'),
(2, 'Juan Pérez', 'La cuna convertible que compré es perfecta. Hermoso diseño y muy resistente. El servicio al cliente respondió todas mis dudas. La mejor inversión para mi bebé.', 5, 'https://i.pravatar.cc/150?img=2', 'Bogotá', true, true, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '30 days'),
(3, 'Ana Martínez', 'Los pañales premium son los mejores que he probado. Súper absorbentes y sin irritaciones. Además el precio con descuento es excelente. Seguiré comprando aquí.', 5, 'https://i.pravatar.cc/150?img=3', 'Cali', true, true, CURRENT_TIMESTAMP - INTERVAL '28 days', CURRENT_TIMESTAMP - INTERVAL '28 days'),
(4, 'Carlos López', 'El monitor de bebé con cámara es increíble. La calidad de imagen es excelente incluso de noche. Me da mucha tranquilidad poder ver a mi bebé desde mi celular.', 5, 'https://i.pravatar.cc/150?img=4', 'Barranquilla', true, true, CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP - INTERVAL '25 days'),
(5, 'Laura Rodríguez', 'Compré el gimnasio de actividades y mi bebé pasa horas jugando. Estimula mucho su desarrollo. El envío fue rápido y el empaque muy seguro.', 5, 'https://i.pravatar.cc/150?img=5', 'Bogotá', true, true, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP - INTERVAL '20 days'),

-- 10 APROBADOS (no destacados)
(6, 'Patricia Gómez', 'Buenos productos en general. El biberón anticólico realmente funciona. Solo me hubiera gustado más variedad de colores.', 4, 'https://i.pravatar.cc/150?img=6', 'Cartagena', true, false, CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP - INTERVAL '18 days'),
(7, 'Roberto Silva', 'La silla de auto es súper segura y cómoda. Mi bebé viaja tranquilo en viajes largos. Instalación sencilla con Isofix.', 5, 'https://i.pravatar.cc/150?img=7', 'Pereira', true, false, CURRENT_TIMESTAMP - INTERVAL '16 days', CURRENT_TIMESTAMP - INTERVAL '16 days'),
(8, 'Camila Torres', 'El set de cuidado completo trae productos de excelente calidad. Huelen delicioso y no irritan la piel de mi bebé.', 5, 'https://i.pravatar.cc/150?img=8', 'Bucaramanga', true, false, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '15 days'),
(9, 'Diego Ramírez', 'El coche travel system es práctico aunque un poco pesado. Pero la calidad es buena y mi bebé va muy cómodo.', 4, 'https://i.pravatar.cc/150?img=9', 'Manizales', true, false, CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP - INTERVAL '14 days'),
(10, 'Valentina Castro', 'Compré varios bodys y la calidad del algodón es excelente. Se mantienen suaves después de varios lavados.', 5, 'https://i.pravatar.cc/150?img=10', 'Cúcuta', true, false, CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP - INTERVAL '12 days'),
(11, 'Andrés Moreno', 'El esterilizador UV es lo mejor que he comprado. Súper rápido y efectivo. Ya no tengo que hervir los biberones.', 5, 'https://i.pravatar.cc/150?img=11', 'Ibagué', true, false, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '10 days'),
(12, 'Isabella Cruz', 'La licuadora para papillas funciona bien. Hace todo en uno: cocina y tritura. Ahorra mucho tiempo.', 4, 'https://i.pravatar.cc/150?img=12', 'Santa Marta', true, false, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '8 days'),
(13, 'Sebastián Vargas', 'El extractor de leche es silencioso y eficiente. Los 10 niveles de succión permiten encontrar el ideal. Muy cómodo.', 5, 'https://i.pravatar.cc/150?img=13', 'Villavicencio', true, false, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '6 days'),
(14, 'Daniela Ortiz', 'Las toallitas húmedas son suaves y no resecan. Me encanta que traigan aloe vera. Excelente relación calidad-precio.', 5, 'https://i.pravatar.cc/150?img=14', 'Armenia', true, false, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(15, 'Miguel Soto', 'La cómoda cambiador es hermosa y funcional. Los cajones son amplios. Llegó bien empacada sin ningún daño.', 4, 'https://i.pravatar.cc/150?img=15', 'Pasto', true, false, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),

-- 5 PENDIENTES (necesitan aprobación)
(16, 'Sofía Mendoza', 'Acabo de recibir mi pedido y estoy muy contenta! El humidificador funciona perfecto y la luz nocturna es hermosa.', 5, 'https://i.pravatar.cc/150?img=16', 'Neiva', false, false, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(17, 'Luisa Hernández', 'Buenos productos pero el envío tardó un poco más de lo esperado. Igual todo llegó en buen estado.', 4, 'https://i.pravatar.cc/150?img=17', 'Popayán', false, false, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(18, 'David Castillo', 'La alfombra de juegos es perfecta! Grande, acolchada y fácil de limpiar. Mi bebé tiene mucho espacio para jugar.', 5, 'https://i.pravatar.cc/150?img=18', 'Tunja', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 'Carolina Rojas', 'Primera vez que compro aquí y quedé encantada. El andador musical es de excelente calidad y mi bebé lo adora.', 5, 'https://i.pravatar.cc/150?img=19', 'Montería', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 'Felipe Gómez', 'Me encantó el servicio y la atención. Respondieron mis mensajes muy rápido. Los productos llegaron perfectos.', 5, 'https://i.pravatar.cc/150?img=20', 'Valledupar', false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT setval('testimonials_id_seq', (SELECT MAX(id) FROM testimonials));

-- ============================================================================
-- 7. INFORMACIÓN DE CONTACTO
-- ============================================================================

INSERT INTO contact_info (id, company_name, phone, email, whatsapp, address, city, country, description, business_hours, facebook, instagram, twitter, created_at, updated_at) VALUES
(1, 'BabyCash', '+57 601 234 5678', '202215.clv@gmail.com', '+57 300 1234567', 
'Calle 72 #10-34', 'Bogotá D.C.', 'Colombia',
'Tu tienda online de confianza para productos de bebé y maternidad',
'Lunes a Viernes: 8:00 AM - 6:00 PM, Sábados: 9:00 AM - 2:00 PM',
'https://facebook.com/babycash', 
'https://instagram.com/babycash', 
'https://twitter.com/babycash',
CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================
