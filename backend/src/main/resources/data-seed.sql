-- ============================================================================
-- BABYCASH - Script de Datos de Prueba
-- ============================================================================
-- Este script inserta datos de prueba para demostrar todas las funcionalidades
-- del sistema. Incluye usuarios, productos, órdenes, blog, testimonios y más.
-- ============================================================================

-- ============================================================================
-- 1. USUARIOS (passwords: todos tienen "password123" encriptado con BCrypt)
-- ============================================================================

-- Admin principal (202215.clv@gmail.com)
INSERT INTO users (id, full_name, email, password, phone, address, role, points, created_at) VALUES
(1, 'Administrador Principal', '202215.clv@gmail.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5Y5YqZ7v7h5YvG5GjN5Y5Y5Y5', '+57 300 1234567', 'Calle 123 #45-67, Bogotá', 'ADMIN', 0, NOW()),
(2, 'María García', 'maria.garcia@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5Y5YqZ7v7h5YvG5GjN5Y5Y5Y5', '+57 310 2345678', 'Carrera 15 #89-12, Medellín', 'USER', 1250, NOW()),
(3, 'Juan Pérez', 'juan.perez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5Y5YqZ7v7h5YvG5GjN5Y5Y5Y5', '+57 320 3456789', 'Avenida 68 #34-56, Bogotá', 'USER', 3500, NOW()),
(4, 'Ana Martínez', 'ana.martinez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5Y5YqZ7v7h5YvG5GjN5Y5Y5Y5', '+57 315 4567890', 'Calle 80 #12-34, Cali', 'USER', 750, NOW()),
(5, 'Carlos López', 'carlos.lopez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5Y5YqZ7v7h5YvG5GjN5Y5Y5Y5', '+57 311 5678901', 'Carrera 7 #45-67, Barranquilla', 'USER', 2100, NOW()),
(6, 'Laura Rodríguez', 'laura.rodriguez@example.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5Y5YqZ7v7h5YvG5GjN5Y5Y5Y5', '+57 312 6789012', 'Calle 100 #15-23, Bogotá', 'USER', 890, NOW()),
(7, 'Moderador Sistema', 'moderador@babycash.com', '$2a$10$YqZ7v7h5YvG5GjN5Y5Y5Y5YqZ7v7h5YvG5GjN5Y5Y5Y5', '+57 301 7890123', 'Centro Comercial, Bogotá', 'MODERATOR', 0, NOW());

-- ============================================================================
-- 2. PRODUCTOS (50 productos variados)
-- ============================================================================

-- Cuidado del Bebé (BABY_CARE)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, featured, created_at) VALUES
(1, 'Pañales Premium Talla M', 'Pañales ultra absorbentes con tecnología de gel. Paquete de 80 unidades. Perfectos para bebés de 6-10 kg.', 89900, 79900, 'BABY_CARE', 150, 'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4', true, NOW()),
(2, 'Toallitas Húmedas x 3 paquetes', 'Toallitas suaves con aloe vera. Sin alcohol. 3 paquetes de 80 unidades cada uno.', 45000, 39900, 'BABY_CARE', 200, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(3, 'Shampoo y Jabón Líquido 2 en 1', 'Fórmula suave hipoalergénica. Sin parabenos. 500ml.', 32000, NULL, 'BABY_CARE', 85, 'https://images.unsplash.com/photo-1556228852-80a8d4b2d0e8', false, NOW()),
(4, 'Crema para Rozaduras', 'Protección 24 horas. Con óxido de zinc. 100g.', 28000, 24900, 'BABY_CARE', 120, 'https://images.unsplash.com/photo-1576426863848-c21f53c60b19', false, NOW()),
(5, 'Aceite Corporal para Bebé', 'Con vitamina E y aceite de almendras. 200ml.', 35000, NULL, 'BABY_CARE', 65, 'https://images.unsplash.com/photo-1608181297027-f1d0e5e85a57', false, NOW()),
(6, 'Talco Absorbente', 'Mantiene la piel seca y fresca. Sin fragancia. 200g.', 18000, 15900, 'BABY_CARE', 90, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(7, 'Set de Cuidado Completo', 'Incluye shampoo, jabón, crema y aceite. Kit básico.', 125000, 99900, 'BABY_CARE', 45, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(8, 'Cepillo y Peine para Bebé', 'Cerdas suaves. Diseño ergonómico. Set de 2 piezas.', 22000, NULL, 'BABY_CARE', 110, 'https://images.unsplash.com/photo-1616429898485-6f94ed7b68a5', false, NOW()),
(9, 'Termómetro Digital', 'Lectura rápida en 10 segundos. Punta flexible.', 42000, 37900, 'BABY_CARE', 55, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(10, 'Aspirador Nasal Eléctrico', 'Seguro y efectivo. 3 niveles de succión. USB recargable.', 89000, NULL, 'BABY_CARE', 30, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW());

-- Ropa (CLOTHING)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, featured, created_at) VALUES
(11, 'Bodys Pack x 5 unidades', 'Algodón 100%. Tallas 0-3 meses. Colores variados.', 65000, 54900, 'CLOTHING', 80, 'https://images.unsplash.com/photo-1519689373023-dd07c7988603', true, NOW()),
(12, 'Pijama de Terciopelo', 'Super suave y abrigada. Con pies. Talla 6-12 meses.', 48000, NULL, 'CLOTHING', 60, 'https://images.unsplash.com/photo-1522771930-78848d9293e8', false, NOW()),
(13, 'Conjunto 3 piezas Niña', 'Vestido, bloomers y diadema. Rosa pastel. 0-6 meses.', 78000, 69900, 'CLOTHING', 45, 'https://images.unsplash.com/photo-1519238263530-99bdd11df2ea', true, NOW()),
(14, 'Sacos de Lana x 2', 'Tejidos a mano. Colores neutros. Talla única.', 55000, NULL, 'CLOTHING', 35, 'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4', false, NOW()),
(15, 'Calcetines Antideslizantes x 6', 'Con dibujos animados. Tallas 0-12 meses.', 28000, 24900, 'CLOTHING', 120, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(16, 'Gorro y Mitones Set', 'Algodón suave. Protección para recién nacidos.', 32000, NULL, 'CLOTHING', 70, 'https://images.unsplash.com/photo-1522771930-78848d9293e8', false, NOW()),
(17, 'Vestido de Ceremonia', 'Elegante con encaje. Para ocasiones especiales. 6-12 meses.', 125000, 109900, 'CLOTHING', 25, 'https://images.unsplash.com/photo-1519238263530-99bdd11df2ea', false, NOW()),
(18, 'Mameluco Deportivo', 'Con capucha. 100% algodón. Unisex. 3-6 meses.', 42000, NULL, 'CLOTHING', 55, 'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4', false, NOW());

-- Alimentación (FEEDING)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, featured, created_at) VALUES
(19, 'Biberones Anticólicos x 3', 'Sistema anticólicos. 260ml cada uno. BPA free.', 78000, 69900, 'FEEDING', 90, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(20, 'Esterilizador UV Eléctrico', 'Elimina 99.9% de gérmenes. Capacidad 6 biberones.', 185000, 159900, 'FEEDING', 20, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(21, 'Calienta Biberones Digital', 'Temperatura precisa. Auto apagado. Pantalla LCD.', 98000, 89900, 'FEEDING', 35, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(22, 'Set de Platos y Cubiertos', 'Silicona grado alimenticio. 5 piezas. Sin BPA.', 52000, NULL, 'FEEDING', 65, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(23, 'Baberos Impermeables x 5', 'Con bolsillo recolector. Fácil limpieza.', 35000, 29900, 'FEEDING', 110, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(24, 'Termo para Alimentos', 'Mantiene temperatura 6 horas. 300ml. Acero inoxidable.', 45000, NULL, 'FEEDING', 50, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(25, 'Extractor de Leche Eléctrico', 'Doble bomba. 10 niveles de succión. Silencioso.', 295000, 269900, 'FEEDING', 15, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(26, 'Bolsas de Almacenamiento x 30', 'Para leche materna. Esterilizadas. 250ml.', 42000, 37900, 'FEEDING', 80, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(27, 'Silla de Comer Portátil', 'Plegable. Ajustable. Fácil limpieza. Hasta 15kg.', 189000, 169900, 'FEEDING', 25, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(28, 'Licuadora para Papillas', 'Cocina y tritura. 4 en 1. Capacidad 500ml.', 165000, NULL, 'FEEDING', 30, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW());

-- Juguetes (TOYS)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, featured, created_at) VALUES
(29, 'Gimnasio de Actividades', 'Con música y luces. Estimula desarrollo motor. 0+ meses.', 145000, 129900, 'TOYS', 40, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(30, 'Móvil Musical para Cuna', 'Proyector de estrellas. 12 melodías. Control remoto.', 98000, 89900, 'TOYS', 55, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(31, 'Set de Sonajeros x 5', 'Colores brillantes. Diferentes texturas. BPA free.', 38000, NULL, 'TOYS', 100, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(32, 'Peluche Musical Elefante', 'Luz nocturna. Sensor de llanto. 30 min de música.', 78000, 69900, 'TOYS', 60, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(33, 'Cubos Apilables x 10', 'Números y letras. Fácil agarre. Goma suave.', 42000, 36900, 'TOYS', 85, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(34, 'Andador Musical 3 en 1', 'Convierte en mesa de juegos. Luces y sonidos.', 225000, 199900, 'TOYS', 20, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(35, 'Alfombra de Juegos Acolchada', 'Doble cara. 180x150cm. Plegable. Impermeable.', 135000, NULL, 'TOYS', 35, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(36, 'Libro de Tela Sensorial', 'Diferentes texturas. Con mordedor. Lavable.', 32000, 28900, 'TOYS', 90, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(37, 'Carrito Andador', 'Madera resistente. Ayuda a caminar. 12+ meses.', 158000, 139900, 'TOYS', 28, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(38, 'Mesa de Actividades', 'Bilingüe. +40 canciones. Luces LED. 6+ meses.', 195000, 179900, 'TOYS', 30, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW());

-- Muebles (FURNITURE)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, featured, created_at) VALUES
(39, 'Cuna Convertible 4 en 1', 'Se convierte en cama junior. Madera maciza. Colchón incluido.', 895000, 799900, 'FURNITURE', 12, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(40, 'Cómoda Cambiador', '5 cajones. Superficie cambiador acolchada. Blanco.', 485000, 439900, 'FURNITURE', 18, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(41, 'Mecedora para Amamantar', 'Tapizada. Reposapiés incluido. Reclinación suave.', 565000, NULL, 'FURNITURE', 15, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(42, 'Corral de Juegos Portátil', 'Plegable. Con colchoneta. 120x120cm. Fácil transporte.', 285000, 259900, 'FURNITURE', 25, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(43, 'Organizador de Juguetes', '12 compartimientos. Estructura metálica. Multicolor.', 145000, 129900, 'FURNITURE', 30, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(44, 'Coche Travel System', 'Incluye silla de auto. Desde nacimiento. Plegado compacto.', 1285000, 1199900, 'FURNITURE', 8, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(45, 'Silla de Auto 0-36kg', 'Isofix. Gira 360°. 4 posiciones reclinables.', 785000, 729900, 'FURNITURE', 20, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW());

-- Otros (OTHER)
INSERT INTO products (id, name, description, price, discount_price, category, stock, image_url, featured, created_at) VALUES
(46, 'Monitor de Bebé con Cámara', 'Visión nocturna. Talk-back. Pantalla 5 pulgadas.', 365000, 329900, 'OTHER', 22, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', true, NOW()),
(47, 'Humidificador Ultrasónico', 'Luz nocturna. Difusor de aromas. 3L capacidad.', 125000, 109900, 'OTHER', 35, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(48, 'Protectores de Esquinas x 12', 'Adhesivos. Transparentes. Seguridad para el hogar.', 18000, 15900, 'OTHER', 150, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(49, 'Pañalera Mochila Premium', 'USB para cargar. Impermeable. 15 compartimientos.', 185000, 169900, 'OTHER', 40, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW()),
(50, 'Báscula Digital para Bebé', 'Precisión 10g. Pantalla LCD. Memoria de peso.', 165000, 149900, 'OTHER', 28, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b', false, NOW());

-- ============================================================================
-- 3. ÓRDENES (15 órdenes con diferentes estados)
-- ============================================================================

INSERT INTO orders (id, user_id, total, status, shipping_address, created_at) VALUES
(1, 2, 159800, 'DELIVERED', 'Carrera 15 #89-12, Medellín', DATE_SUB(NOW(), INTERVAL 30 DAY)),
(2, 3, 299700, 'DELIVERED', 'Avenida 68 #34-56, Bogotá', DATE_SUB(NOW(), INTERVAL 25 DAY)),
(3, 4, 89900, 'DELIVERED', 'Calle 80 #12-34, Cali', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(4, 5, 489800, 'DELIVERED', 'Carrera 7 #45-67, Barranquilla', DATE_SUB(NOW(), INTERVAL 18 DAY)),
(5, 2, 259900, 'DELIVERED', 'Carrera 15 #89-12, Medellín', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(6, 6, 139800, 'SHIPPED', 'Calle 100 #15-23, Bogotá', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(7, 3, 729900, 'SHIPPED', 'Avenida 68 #34-56, Bogotá', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(8, 4, 329900, 'SHIPPED', 'Calle 80 #12-34, Cali', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(9, 5, 199800, 'PROCESSING', 'Carrera 7 #45-67, Barranquilla', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(10, 2, 539800, 'PROCESSING', 'Carrera 15 #89-12, Medellín', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(11, 6, 269900, 'PROCESSING', 'Calle 100 #15-23, Bogotá', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(12, 3, 799900, 'PENDING', 'Avenida 68 #34-56, Bogotá', NOW()),
(13, 4, 169900, 'PENDING', 'Calle 80 #12-34, Cali', NOW()),
(14, 5, 1199900, 'PENDING', 'Carrera 7 #45-67, Barranquilla', NOW()),
(15, 6, 389700, 'PENDING', 'Calle 100 #15-23, Bogotá', NOW());

-- Items de órdenes (simplificado - algunos productos por orden)
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 2, 79900), (2, 20, 1, 159900), (2, 19, 2, 69900),
(3, 2, 1, 39900), (3, 7, 1, 99900), (3, 15, 2, 24900),
(4, 39, 1, 799900), (5, 25, 1, 269900),
(6, 19, 2, 69900), (7, 45, 1, 729900),
(8, 46, 1, 329900), (9, 29, 1, 129900), (9, 19, 1, 69900),
(10, 40, 1, 439900), (10, 7, 1, 99900),
(11, 25, 1, 269900), (12, 39, 1, 799900),
(13, 20, 1, 159900), (14, 44, 1, 1199900),
(15, 46, 1, 329900), (15, 23, 2, 29900);

-- ============================================================================
-- 4. BLOG POSTS (15 artículos de calidad)
-- ============================================================================

INSERT INTO blog_posts (id, title, slug, excerpt, content, image_url, author_id, published, created_at, views) VALUES
(1, 'Los primeros 100 días del bebé: Guía completa', 'primeros-100-dias-bebe-guia-completa', 
'Descubre todo lo que necesitas saber sobre los primeros meses de vida de tu bebé. Tips esenciales para padres primerizos.',
'## Introducción\n\nLos primeros 100 días son cruciales para el desarrollo del bebé. En este artículo te compartimos todo lo que necesitas saber.\n\n## Alimentación\n\nLa lactancia materna exclusiva es recomendada durante los primeros 6 meses.\n\n## Sueño\n\nLos recién nacidos duermen entre 16-17 horas al día.\n\n## Desarrollo\n\nCada bebé se desarrolla a su propio ritmo.',
'https://images.unsplash.com/photo-1555252333-9f8e92e65df9', 1, true, DATE_SUB(NOW(), INTERVAL 45 DAY), 1250),

(2, 'Alimentación complementaria: Cuándo y cómo empezar', 'alimentacion-complementaria-cuando-como-empezar',
'Todo lo que necesitas saber sobre la introducción de alimentos sólidos en la dieta de tu bebé.',
'## ¿Cuándo empezar?\n\nLa OMS recomienda iniciar a los 6 meses.\n\n## Primeros alimentos\n\nFrutas, verduras y cereales son ideales para comenzar.\n\n## Señales de preparación\n\n- Puede sentarse con apoyo\n- Muestra interés en la comida\n- Ha perdido el reflejo de extrusión',
'https://images.unsplash.com/photo-1543076659-9380cdf10613', 1, true, DATE_SUB(NOW(), INTERVAL 40 DAY), 980),

(3, 'Rutinas de sueño para bebés de 0-6 meses', 'rutinas-sueno-bebes-0-6-meses',
'Establece hábitos de sueño saludables desde temprano. Métodos probados y consejos prácticos.',
'## Importancia de las rutinas\n\nLas rutinas ayudan al bebé a sentirse seguro.\n\n## Rutina nocturna sugerida\n\n1. Baño tibio\n2. Masaje suave\n3. Alimentación\n4. Canción de cuna\n\n## Consejos\n\n- Ambiente oscuro y tranquilo\n- Temperatura adecuada (20-22°C)',
'https://images.unsplash.com/photo-1555252333-9f8e92e65df9', 1, true, DATE_SUB(NOW(), INTERVAL 35 DAY), 1520),

(4, 'Juguetes educativos por edad: 0-3 años', 'juguetes-educativos-por-edad-0-3-anos',
'Guía completa de juguetes apropiados para cada etapa de desarrollo de tu bebé.',
'## 0-6 meses\n\nSonajeros, móviles, gimnasios de actividades.\n\n## 6-12 meses\n\nCubos apilables, juguetes de arrastre, libros de tela.\n\n## 1-2 años\n\nRompecabezas simples, bloques de construcción, juguetes musicales.\n\n## 2-3 años\n\nJuegos de encaje, triciclos, crayones grandes.',
'https://images.unsplash.com/photo-1558060370-d644479cb6f7', 1, true, DATE_SUB(NOW(), INTERVAL 30 DAY), 875),

(5, 'Cuidado de la piel del recién nacido', 'cuidado-piel-recien-nacido',
'La piel del bebé es delicada y requiere cuidados especiales. Aprende a protegerla.',
'## Características de la piel del bebé\n\nMás delgada y sensible que la de los adultos.\n\n## Productos recomendados\n\n- Jabones suaves pH neutro\n- Cremas hipoalergénicas\n- Aceites naturales\n\n## Qué evitar\n\n- Perfumes fuertes\n- Alcohol\n- Parabenos',
'https://images.unsplash.com/photo-1583946099379-f9c9cb8bc030', 1, true, DATE_SUB(NOW(), INTERVAL 28 DAY), 1100),

(6, 'Preparando la habitación del bebé', 'preparando-habitacion-bebe',
'Tips de decoración y seguridad para crear el espacio perfecto para tu pequeño.',
'## Elementos esenciales\n\n- Cuna segura\n- Cambiador\n- Iluminación tenue\n- Armario organizado\n\n## Seguridad\n\n- Protectores de esquinas\n- Monitores de bebé\n- Temperatura adecuada\n\n## Decoración\n\nColores suaves y estímulos visuales apropiados.',
'https://images.unsplash.com/photo-1522771930-78848d9293e8', 1, true, DATE_SUB(NOW(), INTERVAL 25 DAY), 950),

(7, 'Lactancia materna: Mitos y realidades', 'lactancia-materna-mitos-realidades',
'Desmitificamos las creencias más comunes sobre la lactancia y te damos información basada en evidencia.',
'## Mito 1: La lactancia duele\n\nRealidad: Con técnica correcta no debería doler.\n\n## Mito 2: Hay que dar ambos pechos en cada toma\n\nRealidad: El bebé decide cuándo está satisfecho.\n\n## Beneficios comprobados\n\n- Fortalece el sistema inmune\n- Crea vínculo madre-hijo\n- Nutrición perfecta',
'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4', 1, true, DATE_SUB(NOW(), INTERVAL 22 DAY), 1680),

(8, 'Desarrollo motor: Hitos del primer año', 'desarrollo-motor-hitos-primer-ano',
'Conoce los hitos del desarrollo motor y cuándo consultar con el pediatra.',
'## 0-3 meses\n\n- Levanta la cabeza\n- Sigue objetos con la mirada\n- Sonrisa social\n\n## 4-6 meses\n\n- Gira sobre sí mismo\n- Se sienta con apoyo\n- Agarra objetos\n\n## 7-12 meses\n\n- Gatea\n- Se pone de pie\n- Primeros pasos',
'https://images.unsplash.com/photo-1555252333-9f8e92e65df9', 1, true, DATE_SUB(NOW(), INTERVAL 20 DAY), 1340),

(9, 'Masajes para bebés: Beneficios y técnicas', 'masajes-bebes-beneficios-tecnicas',
'El masaje infantil fortalece el vínculo y mejora el bienestar del bebé.',
'## Beneficios\n\n- Mejora el sueño\n- Alivia cólicos\n- Fortalece vínculo\n- Estimula desarrollo\n\n## Técnicas básicas\n\n1. Ambiente cálido y tranquilo\n2. Aceite apto para bebés\n3. Movimientos suaves\n4. Respetar señales del bebé',
'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4', 1, true, DATE_SUB(NOW(), INTERVAL 18 DAY), 890),

(10, 'Viajando con bebés: Guía práctica', 'viajando-con-bebes-guia-practica',
'Consejos para que tus viajes con el bebé sean seguros y placenteros.',
'## Documentación necesaria\n\n- Registro civil\n- Carné de vacunación\n- Autorizaciones si viaja sin ambos padres\n\n## Equipaje del bebé\n\n- Pañales suficientes\n- Cambios de ropa\n- Medicamentos básicos\n- Juguetes favoritos\n\n## En el avión\n\n- Dar pecho/biberón en despegue y aterrizaje\n- Llevar snacks\n- Entretenimiento',
'https://images.unsplash.com/photo-1555252333-9f8e92e65df9', 1, true, DATE_SUB(NOW(), INTERVAL 15 DAY), 765),

(11, 'Introducción al control de esfínteres', 'introduccion-control-esfinteres',
'Cuándo y cómo iniciar el proceso de dejar los pañales de forma respetuosa.',
'## Señales de preparación\n\n- Avisa cuando está sucio\n- Interés en el baño\n- Permanece seco por períodos\n- Puede seguir instrucciones simples\n\n## Edad promedio\n\nEntre 18 y 36 meses.\n\n## Método\n\n- Sin presiones\n- Paciencia\n- Celebrar logros\n- Normalizar accidentes',
'https://images.unsplash.com/photo-1519689373023-dd07c7988603', 1, true, DATE_SUB(NOW(), INTERVAL 12 DAY), 1020),

(12, 'Estimulación temprana en casa', 'estimulacion-temprana-casa',
'Actividades sencillas para promover el desarrollo integral de tu bebé.',
'## 0-6 meses\n\n- Tiempo boca abajo\n- Hablar y cantar\n- Masajes\n- Juegos de mirada\n\n## 6-12 meses\n\n- Juegos de esconder\n- Lectura de libros\n- Música y baile\n- Exploración de texturas\n\n## Principios\n\n- Seguir el interés del bebé\n- No sobreestimular\n- Crear rutinas',
'https://images.unsplash.com/photo-1558060370-d644479cb6f7', 1, true, DATE_SUB(NOW(), INTERVAL 10 DAY), 1150),

(13, 'Prevención de accidentes en el hogar', 'prevencion-accidentes-hogar',
'Medidas de seguridad esenciales para proteger a tu bebé en casa.',
'## Cocina\n\n- Protectores de cocina\n- Mangos hacia dentro\n- Productos de limpieza seguros\n\n## Baño\n\n- Temperatura del agua\n- Nunca dejar solo\n- Tapetes antideslizantes\n\n## Dormitorio\n\n- Cuna segura\n- Sin objetos sueltos\n- Monitor de bebé',
'https://images.unsplash.com/photo-1522771930-78848d9293e8', 1, true, DATE_SUB(NOW(), INTERVAL 7 DAY), 895),

(14, 'Primeros auxilios para bebés', 'primeros-auxilios-bebes',
'Conocimientos básicos que todo padre debe tener para emergencias.',
'## Atragantamiento\n\n- Maniobra de Heimlich adaptada\n- Llamar emergencias\n- NO golpear la espalda fuerte\n\n## Fiebre\n\n- Temperatura normal: 36.5-37.5°C\n- Consultar si es menor de 3 meses\n- Hidratación\n\n## Caídas\n\n- Observar comportamiento\n- Aplicar frío\n- Consultar si vomita o está somnoliento',
'https://images.unsplash.com/photo-1583946099379-f9c9cb8bc030', 1, true, DATE_SUB(NOW(), INTERVAL 5 DAY), 1420),

(15, 'Desarrollo del lenguaje de 0-3 años', 'desarrollo-lenguaje-0-3-anos',
'Etapas del desarrollo del lenguaje y cómo estimularlo en casa.',
'## Etapa prelingüística (0-12 meses)\n\n- Llanto diferenciado\n- Arrullos\n- Balbuceo\n- Jerga\n\n## Primeras palabras (12-18 meses)\n\n- Mamá, papá\n- 5-20 palabras\n\n## Explosión léxica (18-24 meses)\n\n- Frases de 2 palabras\n- 50-200 palabras\n\n## Cómo estimular\n\n- Hablar constantemente\n- Leer cuentos\n- Canciones\n- No usar lenguaje infantilizado',
'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4', 1, true, DATE_SUB(NOW(), INTERVAL 2 DAY), 980);

-- ============================================================================
-- 5. COMENTARIOS DEL BLOG (comentarios y respuestas)
-- ============================================================================

INSERT INTO blog_comments (id, blog_post_id, user_id, content, parent_comment_id, created_at) VALUES
-- Comentarios en post 1
(1, 1, 2, 'Excelente artículo! Me ayudó mucho con mi primera hija. ¿Podrían hacer uno sobre cólicos?', NULL, DATE_SUB(NOW(), INTERVAL 44 DAY)),
(2, 1, 1, 'Gracias María! Tomaremos en cuenta tu sugerencia para un próximo artículo sobre cólicos.', 1, DATE_SUB(NOW(), INTERVAL 43 DAY)),
(3, 1, 3, 'Muy útil, aunque mi bebé duerme menos de lo que mencionan. ¿Es normal?', NULL, DATE_SUB(NOW(), INTERVAL 42 DAY)),
(4, 1, 1, 'Cada bebé es diferente. Si tu pediatra dice que está sano, no hay problema. Pero consulta siempre tus dudas con el médico.', 3, DATE_SUB(NOW(), INTERVAL 42 DAY)),

-- Comentarios en post 3
(5, 3, 4, 'Las rutinas realmente funcionan! Mi bebé ahora duerme toda la noche.', NULL, DATE_SUB(NOW(), INTERVAL 33 DAY)),
(6, 3, 5, '¿A qué edad empezaron la rutina? Mi bebé tiene 2 meses.', NULL, DATE_SUB(NOW(), INTERVAL 32 DAY)),
(7, 3, 4, 'Nosotros empezamos alrededor de los 3 meses y fue mágico.', 6, DATE_SUB(NOW(), INTERVAL 31 DAY)),

-- Comentarios en post 7
(8, 7, 6, 'Gracias por desmitificar estos temas. La lactancia es hermosa pero necesita apoyo.', NULL, DATE_SUB(NOW(), INTERVAL 21 DAY)),
(9, 7, 2, 'Totalmente de acuerdo. Tuve problemas al principio pero con ayuda lo logré.', 8, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(10, 7, 1, 'Me alegra que les haya servido. El apoyo es fundamental en la lactancia.', 8, DATE_SUB(NOW(), INTERVAL 20 DAY)),

-- Comentarios en post 8
(11, 8, 3, 'Mi bebé ya gatea a los 6 meses! ¿Es muy adelantado?', NULL, DATE_SUB(NOW(), INTERVAL 19 DAY)),
(12, 8, 1, 'Cada bebé tiene su propio ritmo. Algunos gatean antes, otros después. ¡Disfruta cada etapa!', 11, DATE_SUB(NOW(), INTERVAL 18 DAY)),

-- Comentarios en post 14
(13, 14, 5, 'Este artículo debería ser obligatorio para todos los padres. Muy importante.', NULL, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(14, 14, 6, '¿Recomiendan algún curso presencial de primeros auxilios?', NULL, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(15, 14, 1, 'Sí! La Cruz Roja ofrece cursos excelentes de primeros auxilios pediátricos.', 14, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- ============================================================================
-- 6. TESTIMONIOS (20 testimonios con diferentes estados)
-- ============================================================================

INSERT INTO testimonials (id, user_id, rating, content, status, featured, created_at) VALUES
-- Testimonios APROBADOS y DESTACADOS
(1, 2, 5, 'Excelente servicio y productos de alta calidad. Mi bebé está encantado con todos los juguetes que compré. La entrega fue rápida y todo llegó en perfecto estado. ¡100% recomendado!', 'APPROVED', true, DATE_SUB(NOW(), INTERVAL 35 DAY)),
(2, 3, 5, 'La cuna convertible que compré es perfecta. Hermoso diseño y muy resistente. El servicio al cliente respondió todas mis dudas. La mejor inversión para mi bebé.', 'APPROVED', true, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(3, 4, 5, 'Los pañales premium son los mejores que he probado. Súper absorbentes y sin irritaciones. Además el precio con descuento es excelente. Seguiré comprando aquí.', 'APPROVED', true, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(4, 5, 5, 'El monitor de bebé con cámara es increíble. La calidad de imagen es excelente incluso de noche. Me da mucha tranquilidad poder ver a mi bebé desde mi celular. Totalmente recomendado.', 'APPROVED', true, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(5, 6, 5, 'Compré el gimnasio de actividades y mi bebé pasa horas jugando. Estimula mucho su desarrollo. El envío fue rápido y el empaque muy seguro. Excelente experiencia de compra.', 'APPROVED', true, DATE_SUB(NOW(), INTERVAL 20 DAY)),

-- Testimonios APROBADOS (no destacados)
(6, 2, 4, 'Buenos productos en general. El biberón anticólico realmente funciona. Solo me hubiera gustado más variedad de colores.', 'APPROVED', false, DATE_SUB(NOW(), INTERVAL 18 DAY)),
(7, 3, 5, 'La silla de auto es súper segura y cómoda. Mi bebé viaja tranquilo en viajes largos. Instalación sencilla con Isofix.', 'APPROVED', false, DATE_SUB(NOW(), INTERVAL 16 DAY)),
(8, 4, 5, 'El set de cuidado completo trae productos de excelente calidad. Huelen delicioso y no irritan la piel de mi bebé.', 'APPROVED', false, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(9, 5, 4, 'El coche travel system es práctico aunque un poco pesado. Pero la calidad es buena y mi bebé va muy cómodo.', 'APPROVED', false, DATE_SUB(NOW(), INTERVAL 14 DAY)),
(10, 6, 5, 'Compré varios bodys y la calidad del algodón es excelente. Se mantienen suaves después de varios lavados.', 'APPROVED', false, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(11, 2, 5, 'El esterilizador UV es lo mejor que he comprado. Súper rápido y efectivo. Ya no tengo que hervir los biberones.', 'APPROVED', false, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(12, 3, 4, 'La licuadora para papillas funciona bien. Hace todo en uno: cocina y tritura. Ahorra mucho tiempo.', 'APPROVED', false, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(13, 4, 5, 'El extractor de leche es silencioso y eficiente. Los 10 niveles de succión permiten encontrar el ideal. Muy cómodo.', 'APPROVED', false, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(14, 5, 5, 'Las toallitas húmedas son suaves y no resecan. Me encanta que traigan aloe vera. Excelente relación calidad-precio.', 'APPROVED', false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(15, 6, 4, 'La cómoda cambiador es hermosa y funcional. Los cajones son amplios. Llegó bien empacada sin ningún daño.', 'APPROVED', false, DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- Testimonios PENDIENTES (necesitan aprobación)
(16, 2, 5, 'Acabo de recibir mi pedido y estoy muy contenta! El humidificador funciona perfecto y la luz nocturna es hermosa. Mi bebé duerme mejor.', 'PENDING', false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(17, 3, 4, 'Buenos productos pero el envío tardó un poco más de lo esperado. Igual todo llegó en buen estado.', 'PENDING', false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(18, 4, 5, 'La alfombra de juegos es perfecta! Grande, acolchada y fácil de limpiar. Mi bebé tiene mucho espacio para jugar seguro.', 'PENDING', false, NOW()),
(19, 5, 5, 'Primera vez que compro aquí y quedé encantado. El andador musical es de excelente calidad y mi bebé lo adora. Definitivamente volveré a comprar.', 'PENDING', false, NOW()),
(20, 6, 5, 'Me encantó el servicio y la atención. Respondieron mis mensajes muy rápido. Los productos llegaron perfectos y antes de lo esperado.', 'PENDING', false, NOW());

-- ============================================================================
-- 7. MENSAJES DE CONTACTO (12 mensajes con diferentes estados)
-- ============================================================================

INSERT INTO contact_messages (id, name, email, phone, subject, message, status, ip_address, created_at) VALUES
-- Mensajes RESPONDIDOS
(1, 'Patricia Gómez', 'patricia.gomez@example.com', '+57 315 1112233', 'Consulta sobre envíos', 
'Hola, quisiera saber si hacen envíos a todo el país y cuánto demora a Cartagena. Gracias!', 
'REPLIED', '192.168.1.10', DATE_SUB(NOW(), INTERVAL 15 DAY)),

(2, 'Roberto Silva', 'roberto.silva@example.com', '+57 316 2223344', 'Devolución de producto', 
'Necesito información sobre la política de devoluciones. Compré una silla de auto que no es compatible con mi vehículo.', 
'REPLIED', '192.168.1.11', DATE_SUB(NOW(), INTERVAL 12 DAY)),

(3, 'Camila Torres', 'camila.torres@example.com', '+57 317 3334455', 'Garantía de productos', 
'¿Qué garantía tienen los productos? Estoy interesada en comprar una cuna convertible.', 
'REPLIED', '192.168.1.12', DATE_SUB(NOW(), INTERVAL 10 DAY)),

(4, 'Diego Ramírez', 'diego.ramirez@example.com', NULL, 'Medios de pago', 
'Quisiera saber qué medios de pago aceptan y si hay opción de pago contraentrega.', 
'REPLIED', '192.168.1.13', DATE_SUB(NOW(), INTERVAL 8 DAY)),

-- Mensajes LEÍDOS (en proceso)
(5, 'Valentina Castro', 'valentina.castro@example.com', '+57 318 4445566', 'Alianzas corporativas', 
'Buenas tardes, represento a una empresa y estamos interesados en hacer compras corporativas. ¿Manejan precios especiales por volumen?', 
'READ', '192.168.1.14', DATE_SUB(NOW(), INTERVAL 5 DAY)),

(6, 'Andrés Moreno', 'andres.moreno@example.com', '+57 319 5556677', 'Producto sin stock', 
'Vi que el monitor de bebé que quiero está agotado. ¿Cuándo volverá a estar disponible?', 
'READ', '192.168.1.15', DATE_SUB(NOW(), INTERVAL 3 DAY)),

(7, 'Isabella Cruz', 'isabella.cruz@example.com', '+57 320 6667788', 'Recomendación de productos', 
'Hola! Es mi primer bebé y no sé qué productos son realmente necesarios. ¿Me podrían asesorar? El bebé nacerá en 2 meses.', 
'READ', '192.168.1.16', DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- Mensajes NUEVOS (sin leer)
(8, 'Sebastián Vargas', 'sebastian.vargas@example.com', NULL, 'Modificar orden', 
'Hice un pedido hace una hora pero me equivoqué en la dirección de envío. ¿Aún puedo cambiarla? Mi orden es #12345', 
'NEW', '192.168.1.17', DATE_SUB(NOW(), INTERVAL 1 DAY)),

(9, 'Daniela Ortiz', 'daniela.ortiz@example.com', '+57 321 7778899', 'Consulta sobre tallas', 
'Necesito comprar ropa para mi sobrina pero no sé qué talla. Ella tiene 8 meses y pesa 9kg. ¿Me pueden ayudar?', 
'NEW', '192.168.1.18', NOW()),

(10, 'Miguel Ángel Soto', 'miguel.soto@example.com', '+57 322 8889900', 'Colaboración influencer', 
'Hola, soy papá blogger con 50k seguidores. Me gustaría colaborar con ustedes. ¿Podemos conversar?', 
'NEW', '192.168.1.19', NOW()),

(11, 'Sofía Mendoza', 'sofia.mendoza@example.com', '+57 323 9990011', 'Productos alérgicos', 
'Mi bebé tiene piel muy sensible. ¿Los productos de cuidado personal que venden son hipoalergénicos y sin perfumes?', 
'NEW', '192.168.1.20', NOW()),

(12, 'Luisa Hernández', 'luisa.hernandez@example.com', NULL, 'Sugerencia de productos', 
'Sería genial que tuvieran más opciones de juguetes Montessori. Muchos padres los buscamos pero son difíciles de conseguir.', 
'NEW', '192.168.1.21', NOW());

-- ============================================================================
-- 8. INFORMACIÓN DE CONTACTO (singleton)
-- ============================================================================

INSERT INTO contact_info (id, phone, email, whatsapp, address, facebook, instagram, youtube) VALUES
(1, '+57 601 234 5678', '202215.clv@gmail.com', '+57 300 1234567', 
'Calle 72 #10-34, Bogotá D.C., Colombia', 
'https://facebook.com/babycash', 
'https://instagram.com/babycash', 
'https://youtube.com/@babycash');

-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================
-- 
-- RESUMEN DE DATOS INSERTADOS:
-- - 7 usuarios (1 admin, 1 moderador, 5 clientes)
-- - 50 productos (en diferentes categorías)
-- - 15 órdenes (con diferentes estados)
-- - 15 artículos de blog
-- - 15 comentarios en blog (con respuestas)
-- - 20 testimonios (5 destacados, 15 aprobados, 5 pendientes)
-- - 12 mensajes de contacto (4 respondidos, 3 leídos, 5 nuevos)
-- - 1 registro de información de contacto
--
-- Total: ~150 registros de datos de prueba
-- ============================================================================
