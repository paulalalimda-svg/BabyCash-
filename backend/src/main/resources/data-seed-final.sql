-- ============================================================================
-- BABYCASH - Datos de Prueba (PostgreSQL - Final)
-- ============================================================================

-- Limpiar datos existentes
TRUNCATE TABLE order_items, orders, blog_comments, blog_posts, testimonials, contact_messages, products, users RESTART IDENTITY CASCADE;

-- ============================================================================
-- 1. USUARIOS
-- ============================================================================

INSERT INTO users (first_name, last_name, email, password, phone, address, role, enabled, email_verified, created_at, updated_at) VALUES
('Admin', 'Principal', '202215.clv@gmail.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 300 1234567', 'Calle 123, Bogotá', 'ADMIN', true, true, NOW(), NOW()),
('María', 'García', 'maria.garcia@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 310 2345678', 'Carrera 15, Medellín', 'USER', true, true, NOW(), NOW()),
('Juan', 'Pérez', 'juan.perez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 320 3456789', 'Avenida 68, Bogotá', 'USER', true, true, NOW(), NOW()),
('Ana', 'Martínez', 'ana.martinez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 315 4567890', 'Calle 80, Cali', 'USER', true, true, NOW(), NOW()),
('Carlos', 'López', 'carlos.lopez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 311 5678901', 'Carrera 7, Barranquilla', 'USER', true, true, NOW(), NOW()),
('Laura', 'Rodríguez', 'laura.rodriguez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5YeYqZ7v7h5YvG5GjN5Y5Y5Ye', '+57 312 6789012', 'Calle 100, Bogotá', 'USER', true, true, NOW(), NOW());

-- ============================================================================
-- 2. PRODUCTOS (18 productos variados)
-- ============================================================================

INSERT INTO products (name, description, price, discount_price, category, stock, image_url, enabled, featured, rating, review_count, created_at, updated_at) VALUES
-- Pañales y Cuidado
('Pañales Premium Talla M', 'Pañales ultra absorbentes. Paquete de 80 unidades. Para bebés de 6-10 kg.', 89900, 79900, 'DIAPERS', 150, 'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4', true, true, 4.8, 45, NOW(), NOW()),
('Toallitas Húmedas x3', 'Toallitas suaves con aloe vera. Sin alcohol. 3 paquetes de 80 unidades.', 45000, 39900, 'HEALTH', 200, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.7, 38, NOW(), NOW()),
('Crema para Rozaduras', 'Protección 24 horas. Con óxido de zinc. 100g.', 28000, 24900, 'HEALTH', 120, 'https://images.unsplash.com/photo-1576426863848-c21f53c60b19', true, false, 4.9, 56, NOW(), NOW()),

-- Baño
('Shampoo y Jabón 2 en 1', 'Fórmula suave hipoalergénica. Sin parabenos. 500ml.', 32000, NULL, 'BATH', 85, 'https://images.unsplash.com/photo-1556228852-80a8d4b2d0e8', true, false, 4.6, 22, NOW(), NOW()),
('Aceite Corporal para Bebé', 'Con vitamina E y aceite de almendras. 200ml.', 35000, NULL, 'BATH', 65, 'https://images.unsplash.com/photo-1608181297027-f1d0e5e85a57', true, false, 4.5, 18, NOW(), NOW()),

-- Ropa
('Body Pack x5 unidades', 'Algodón 100%. Tallas 0-3 meses. Colores variados.', 65000, 54900, 'CLOTHING', 80, 'https://images.unsplash.com/photo-1519689373023-dd07c7988603', true, true, 4.8, 62, NOW(), NOW()),
('Conjunto 3 piezas Niña', 'Vestido, bloomers y diadema. Rosa pastel. 0-6 meses.', 78000, 69900, 'CLOTHING', 45, 'https://images.unsplash.com/photo-1519238263530-99bdd11df2ea', true, true, 4.9, 41, NOW(), NOW()),

-- Alimentación
('Biberones Anticólicos x3', 'Sistema anticólicos. 260ml cada uno. BPA free.', 78000, 69900, 'FEEDING', 90, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.9, 78, NOW(), NOW()),
('Esterilizador UV Eléctrico', 'Elimina 99.9% de gérmenes. Capacidad 6 biberones.', 185000, 159900, 'FEEDING', 20, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 5.0, 34, NOW(), NOW()),
('Extractor de Leche Eléctrico', 'Doble bomba. 10 niveles de succión. Silencioso.', 295000, 269900, 'FEEDING', 15, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.8, 29, NOW(), NOW()),

-- Juguetes
('Gimnasio de Actividades', 'Con música y luces. Estimula desarrollo motor. 0+ meses.', 145000, 129900, 'TOYS', 40, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.9, 52, NOW(), NOW()),
('Andador Musical 3 en 1', 'Convierte en mesa de juegos. Luces y sonidos.', 225000, 199900, 'TOYS', 20, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.7, 36, NOW(), NOW()),

-- Muebles
('Cuna Convertible 4 en 1', 'Se convierte en cama junior. Madera maciza. Colchón incluido.', 895000, 799900, 'FURNITURE', 12, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 5.0, 28, NOW(), NOW()),
('Cómoda Cambiador', '5 cajones. Superficie cambiador acolchada. Blanco.', 485000, 439900, 'FURNITURE', 18, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.8, 19, NOW(), NOW()),
('Coche Travel System', 'Incluye silla de auto. Desde nacimiento. Plegado compacto.', 1285000, 1199900, 'FURNITURE', 8, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.9, 15, NOW(), NOW()),
('Silla de Auto 0-36kg', 'Isofix. Gira 360°. 4 posiciones reclinables.', 785000, 729900, 'FURNITURE', 20, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.9, 42, NOW(), NOW()),

-- Seguridad y Otros
('Monitor de Bebé con Cámara', 'Visión nocturna. Talk-back. Pantalla 5 pulgadas.', 365000, 329900, 'SAFETY', 22, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, true, 4.8, 67, NOW(), NOW()),
('Humidificador Ultrasónico', 'Luz nocturna. Difusor de aromas. 3L capacidad.', 125000, 109900, 'ACCESSORIES', 35, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, false, 4.6, 31, NOW(), NOW());

-- ============================================================================
-- 3. TESTIMONIOS (15 testimonios: 5 destacados + 10 aprobados)
-- ============================================================================

INSERT INTO testimonials (name, message, rating, avatar, location, approved, featured, created_at, updated_at) VALUES
-- 5 DESTACADOS
('María García', 'Excelente servicio y productos de alta calidad. Mi bebé está encantado con todos los juguetes que compré. La entrega fue rápida y todo llegó en perfecto estado. ¡100% recomendado!', 5, 'https://i.pravatar.cc/150?img=1', 'Medellín', true, true, NOW() - INTERVAL '35 days', NOW()),
('Juan Pérez', 'La cuna convertible que compré es perfecta. Hermoso diseño y muy resistente. El servicio al cliente respondió todas mis dudas. La mejor inversión para mi bebé.', 5, 'https://i.pravatar.cc/150?img=2', 'Bogotá', true, true, NOW() - INTERVAL '30 days', NOW()),
('Ana Martínez', 'Los pañales premium son los mejores que he probado. Súper absorbentes y sin irritaciones. Además el precio con descuento es excelente. Seguiré comprando aquí.', 5, 'https://i.pravatar.cc/150?img=3', 'Cali', true, true, NOW() - INTERVAL '28 days', NOW()),
('Carlos López', 'El monitor de bebé con cámara es increíble. La calidad de imagen es excelente incluso de noche. Me da mucha tranquilidad poder ver a mi bebé desde mi celular.', 5, 'https://i.pravatar.cc/150?img=4', 'Barranquilla', true, true, NOW() - INTERVAL '25 days', NOW()),
('Laura Rodríguez', 'Compré el gimnasio de actividades y mi bebé pasa horas jugando. Estimula mucho su desarrollo. El envío fue rápido y el empaque muy seguro.', 5, 'https://i.pravatar.cc/150?img=5', 'Bogotá', true, true, NOW() - INTERVAL '20 days', NOW()),

-- 10 APROBADOS
('Patricia Gómez', 'Buenos productos en general. El biberón anticólico realmente funciona. Solo me hubiera gustado más variedad de colores.', 4, 'https://i.pravatar.cc/150?img=6', 'Cartagena', true, false, NOW() - INTERVAL '18 days', NOW()),
('Roberto Silva', 'La silla de auto es súper segura y cómoda. Mi bebé viaja tranquilo en viajes largos. Instalación sencilla con Isofix.', 5, 'https://i.pravatar.cc/150?img=7', 'Pereira', true, false, NOW() - INTERVAL '16 days', NOW()),
('Camila Torres', 'El set de cuidado completo trae productos de excelente calidad. Huelen delicioso y no irritan la piel de mi bebé.', 5, 'https://i.pravatar.cc/150?img=8', 'Bucaramanga', true, false, NOW() - INTERVAL '15 days', NOW()),
('Diego Ramírez', 'El coche travel system es práctico aunque un poco pesado. Pero la calidad es buena y mi bebé va muy cómodo.', 4, 'https://i.pravatar.cc/150?img=9', 'Manizales', true, false, NOW() - INTERVAL '14 days', NOW()),
('Valentina Castro', 'Compré varios bodys y la calidad del algodón es excelente. Se mantienen suaves después de varios lavados.', 5, 'https://i.pravatar.cc/150?img=10', 'Cúcuta', true, false, NOW() - INTERVAL '12 days', NOW()),
('Andrés Moreno', 'El esterilizador UV es lo mejor que he comprado. Súper rápido y efectivo. Ya no tengo que hervir los biberones.', 5, 'https://i.pravatar.cc/150?img=11', 'Ibagué', true, false, NOW() - INTERVAL '10 days', NOW()),
('Isabella Cruz', 'La licuadora para papillas funciona bien. Hace todo en uno: cocina y tritura. Ahorra mucho tiempo.', 4, 'https://i.pravatar.cc/150?img=12', 'Santa Marta', true, false, NOW() - INTERVAL '8 days', NOW()),
('Sebastián Vargas', 'El extractor de leche es silencioso y eficiente. Los 10 niveles de succión permiten encontrar el ideal. Muy cómodo.', 5, 'https://i.pravatar.cc/150?img=13', 'Villavicencio', true, false, NOW() - INTERVAL '6 days', NOW()),
('Daniela Ortiz', 'Las toallitas húmedas son suaves y no resecan. Me encanta que traigan aloe vera. Excelente relación calidad-precio.', 5, 'https://i.pravatar.cc/150?img=14', 'Armenia', true, false, NOW() - INTERVAL '5 days', NOW()),
('Miguel Soto', 'La cómoda cambiador es hermosa y funcional. Los cajones son amplios. Llegó bien empacada sin ningún daño.', 4, 'https://i.pravatar.cc/150?img=15', 'Pasto', true, false, NOW() - INTERVAL '3 days', NOW());

-- ============================================================================
-- 4. BLOG POSTS (5 artículos)
-- ============================================================================

INSERT INTO blog_posts (title, slug, excerpt, content, image_url, author_id, published, featured, view_count, published_at, created_at, updated_at) VALUES
('Los primeros 100 días del bebé: Guía completa', 'primeros-100-dias-bebe-guia-completa', 
'Descubre todo lo que necesitas saber sobre los primeros meses de vida de tu bebé. Tips esenciales para padres primerizos.',
'## Introducción

Los primeros 100 días son cruciales para el desarrollo del bebé. En este artículo te compartimos todo lo que necesitas saber.

## Alimentación

La lactancia materna exclusiva es recomendada durante los primeros 6 meses.

## Sueño

Los recién nacidos duermen entre 16-17 horas al día.

## Desarrollo

Cada bebé se desarrolla a su propio ritmo.',
'https://images.unsplash.com/photo-1555252333-9f8e92e65df9', 1, true, true, 1250, NOW() - INTERVAL '45 days', NOW() - INTERVAL '45 days', NOW()),

('Rutinas de sueño para bebés de 0-6 meses', 'rutinas-sueno-bebes-0-6-meses',
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
'https://images.unsplash.com/photo-1555252333-9f8e92e65df9', 1, true, true, 1520, NOW() - INTERVAL '35 days', NOW() - INTERVAL '35 days', NOW()),

('Lactancia materna: Mitos y realidades', 'lactancia-materna-mitos-realidades',
'Desmitificamos las creencias más comunes sobre la lactancia y te damos información basada en evidencia.',
'## Mito 1: La lactancia duele

Realidad: Con técnica correcta no debería doler.

## Mito 2: Hay que dar ambos pechos en cada toma

Realidad: El bebé decide cuándo está satisfecho.

## Beneficios comprobados

- Fortalece el sistema inmune
- Crea vínculo madre-hijo
- Nutrición perfecta',
'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4', 1, true, false, 1680, NOW() - INTERVAL '22 days', NOW() - INTERVAL '22 days', NOW()),

('Desarrollo motor: Hitos del primer año', 'desarrollo-motor-hitos-primer-ano',
'Conoce los hitos del desarrollo motor y cuándo consultar con el pediatra.',
'## 0-3 meses

- Levanta la cabeza
- Sigue objetos con la mirada
- Sonrisa social

## 4-6 meses

- Gira sobre sí mismo
- Se sienta con apoyo
- Agarra objetos

## 7-12 meses

- Gatea
- Se pone de pie
- Primeros pasos',
'https://images.unsplash.com/photo-1555252333-9f8e92e65df9', 1, true, false, 1340, NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days', NOW()),

('Primeros auxilios para bebés', 'primeros-auxilios-bebes',
'Conocimientos básicos que todo padre debe tener para emergencias.',
'## Atragantamiento

- Maniobra de Heimlich adaptada
- Llamar emergencias
- NO golpear la espalda fuerte

## Fiebre

- Temperatura normal: 36.5-37.5°C
- Consultar si es menor de 3 meses
- Hidratación

## Caídas

- Observar comportamiento
- Aplicar frío
- Consultar si vomita o está somnoliento',
'https://images.unsplash.com/photo-1583946099379-f9c9cb8bc030', 1, true, false, 1420, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', NOW());

-- ============================================================================
-- 5. COMENTARIOS DEL BLOG
-- ============================================================================

INSERT INTO blog_comments (blog_post_id, user_id, content, parent_comment_id, approved, created_at, updated_at) VALUES
(1, 2, 'Excelente artículo! Me ayudó mucho con mi primera hija. ¿Podrían hacer uno sobre cólicos?', NULL, true, NOW() - INTERVAL '44 days', NOW() - INTERVAL '44 days'),
(1, 1, 'Gracias María! Tomaremos en cuenta tu sugerencia para un próximo artículo sobre cólicos.', 1, true, NOW() - INTERVAL '43 days', NOW() - INTERVAL '43 days'),
(1, 3, 'Muy útil, aunque mi bebé duerme menos de lo que mencionan. ¿Es normal?', NULL, true, NOW() - INTERVAL '42 days', NOW() - INTERVAL '42 days'),
(2, 4, 'Las rutinas realmente funcionan! Mi bebé ahora duerme toda la noche.', NULL, true, NOW() - INTERVAL '33 days', NOW() - INTERVAL '33 days'),
(3, 5, 'Gracias por desmitificar estos temas. La lactancia es hermosa pero necesita apoyo.', NULL, true, NOW() - INTERVAL '21 days', NOW() - INTERVAL '21 days'),
(5, 6, 'Este artículo debería ser obligatorio para todos los padres. Muy importante.', NULL, true, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days');

-- ============================================================================
-- 6. ÓRDENES (10 órdenes con diferentes estados)
-- ============================================================================

INSERT INTO orders (order_number, user_id, status, total_amount, shipping_address, created_at, updated_at) VALUES
('ORD-2025-001', 2, 'DELIVERED', 159800, 'Carrera 15 #89-12, Medellín', NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days'),
('ORD-2025-002', 3, 'DELIVERED', 299700, 'Avenida 68 #34-56, Bogotá', NOW() - INTERVAL '25 days', NOW() - INTERVAL '20 days'),
('ORD-2025-003', 4, 'DELIVERED', 89900, 'Calle 80 #12-34, Cali', NOW() - INTERVAL '20 days', NOW() - INTERVAL '15 days'),
('ORD-2025-004', 5, 'DELIVERED', 799900, 'Carrera 7 #45-67, Barranquilla', NOW() - INTERVAL '18 days', NOW() - INTERVAL '12 days'),
('ORD-2025-005', 2, 'DELIVERED', 269900, 'Carrera 15 #89-12, Medellín', NOW() - INTERVAL '15 days', NOW() - INTERVAL '10 days'),
('ORD-2025-006', 6, 'SHIPPED', 139800, 'Calle 100 #15-23, Bogotá', NOW() - INTERVAL '10 days', NOW() - INTERVAL '9 days'),
('ORD-2025-007', 3, 'SHIPPED', 729900, 'Avenida 68 #34-56, Bogotá', NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days'),
('ORD-2025-008', 4, 'PROCESSING', 329900, 'Calle 80 #12-34, Cali', NOW() - INTERVAL '5 days', NOW() - INTERVAL '4 days'),
('ORD-2025-009', 5, 'PROCESSING', 199800, 'Carrera 7 #45-67, Barranquilla', NOW() - INTERVAL '3 days', NOW() - INTERVAL '2 days'),
('ORD-2025-010', 2, 'PENDING', 799900, 'Carrera 15 #89-12, Medellín', NOW(), NOW());

-- Items de órdenes
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
(1, 1, 2, 79900, 159800),
(2, 9, 1, 159900, 159900), (2, 8, 2, 69900, 139800),
(3, 2, 1, 39900, 39900), (3, 4, 2, 24900, 49800),
(4, 13, 1, 799900, 799900),
(5, 10, 1, 269900, 269900),
(6, 8, 2, 69900, 139800),
(7, 16, 1, 729900, 729900),
(8, 17, 1, 329900, 329900),
(9, 11, 1, 129900, 129900), (9, 8, 1, 69900, 69900),
(10, 13, 1, 799900, 799900);

-- ============================================================================
-- 7. INFORMACIÓN DE CONTACTO
-- ============================================================================

INSERT INTO contact_info (company_name, phone, email, whatsapp, address, city, country, description, business_hours, facebook, instagram, created_at, updated_at) VALUES
('BabyCash', '+57 601 234 5678', '202215.clv@gmail.com', '+57 300 1234567', 
'Calle 72 #10-34', 'Bogotá D.C.', 'Colombia',
'Tu tienda online de confianza para productos de bebé y maternidad. Ofrecemos productos de alta calidad para el cuidado y desarrollo de tu bebé.',
'Lunes a Viernes: 8:00 AM - 6:00 PM | Sábados: 9:00 AM - 2:00 PM | Domingos: Cerrado',
'https://facebook.com/babycash', 
'https://instagram.com/babycash',
NOW(), NOW());

-- ============================================================================
-- RESUMEN
-- ============================================================================

SELECT 
    '✅ Datos cargados exitosamente!' as status,
    (SELECT COUNT(*) FROM users) || ' usuarios' as usuarios,
    (SELECT COUNT(*) FROM products) || ' productos' as productos,
    (SELECT COUNT(*) FROM testimonials) || ' testimonios' as testimonios,
    (SELECT COUNT(*) || ' (' || SUM(CASE WHEN featured THEN 1 ELSE 0 END) || ' destacados)' FROM testimonials) as detalle_testimonios,
    (SELECT COUNT(*) FROM blog_posts) || ' artículos' as blog,
    (SELECT COUNT(*) FROM orders) || ' órdenes' as ordenes,
    (SELECT COUNT(*) FROM order_items) || ' items' as items_ordenes;
