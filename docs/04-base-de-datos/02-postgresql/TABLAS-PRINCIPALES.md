# TABLAS PRINCIPALES - BABY CASH

## 🎯 Visión General

Este documento detalla cada tabla de la base de datos con:
- Estructura completa
- Tipos de datos
- Constraints y validaciones
- Datos de ejemplo
- Queries comunes

---

## 👤 Tabla: USERS

### Definición

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Descripción de Campos

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| `id` | SERIAL | Identificador único | PRIMARY KEY, auto-incremental |
| `name` | VARCHAR(100) | Nombre completo | NOT NULL |
| `email` | VARCHAR(150) | Email para login | NOT NULL, UNIQUE |
| `password` | VARCHAR(255) | Contraseña hasheada (BCrypt) | NOT NULL |
| `role` | VARCHAR(20) | Rol del usuario | DEFAULT 'USER', CHECK |
| `created_at` | TIMESTAMP | Fecha de registro | DEFAULT CURRENT_TIMESTAMP |
| `updated_at` | TIMESTAMP | Última actualización | DEFAULT CURRENT_TIMESTAMP |

### Datos de Ejemplo

```sql
INSERT INTO users (name, email, password, role) VALUES
('Admin Baby Cash', 'admin@babycash.com', '$2a$10$...', 'ADMIN'),
('María González', 'maria@email.com', '$2a$10$...', 'USER'),
('Juan Pérez', 'juan@email.com', '$2a$10$...', 'USER');
```

### Queries Comunes

```sql
-- Buscar usuario por email
SELECT * FROM users WHERE email = 'maria@email.com';

-- Contar usuarios por rol
SELECT role, COUNT(*) FROM users GROUP BY role;

-- Usuarios registrados hoy
SELECT * FROM users WHERE DATE(created_at) = CURRENT_DATE;

-- Actualizar contraseña
UPDATE users SET password = '$2a$10$...', updated_at = CURRENT_TIMESTAMP
WHERE id = 1;
```

---

## 📂 Tabla: CATEGORIES

### Definición

```sql
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Descripción de Campos

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| `id` | SERIAL | Identificador único | PRIMARY KEY |
| `name` | VARCHAR(50) | Nombre de la categoría | NOT NULL, UNIQUE |
| `slug` | VARCHAR(50) | URL-friendly name | NOT NULL, UNIQUE |
| `description` | TEXT | Descripción detallada | NULL permitido |
| `icon` | VARCHAR(10) | Emoji o icono | NULL permitido |
| `created_at` | TIMESTAMP | Fecha de creación | DEFAULT CURRENT_TIMESTAMP |

### Datos de Ejemplo

```sql
INSERT INTO categories (name, slug, description, icon) VALUES
('Ropa', 'ropa', 'Ropa cómoda y adorable para bebés', '👕'),
('Juguetes', 'juguetes', 'Juguetes seguros y educativos', '🧸'),
('Alimentación', 'alimentacion', 'Biberones y accesorios de alimentación', '🍼'),
('Higiene', 'higiene', 'Productos de cuidado e higiene', '🛁');
```

### Queries Comunes

```sql
-- Todas las categorías
SELECT * FROM categories ORDER BY name;

-- Buscar por slug
SELECT * FROM categories WHERE slug = 'ropa';

-- Contar productos por categoría
SELECT c.name, COUNT(p.id) as total_products
FROM categories c
LEFT JOIN products p ON c.id = p.category_id
GROUP BY c.id, c.name;
```

---

## 🛍️ Tabla: PRODUCTS

### Definición

```sql
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    category_id INTEGER NOT NULL,
    image_url VARCHAR(500),
    discount INTEGER DEFAULT 0 CHECK (discount >= 0 AND discount <= 100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_category FOREIGN KEY (category_id) 
        REFERENCES categories(id) ON DELETE RESTRICT
);
```

### Descripción de Campos

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| `id` | SERIAL | Identificador único | PRIMARY KEY |
| `name` | VARCHAR(200) | Nombre del producto | NOT NULL |
| `description` | TEXT | Descripción detallada | NOT NULL |
| `price` | DECIMAL(10,2) | Precio en pesos | NOT NULL, CHECK >= 0 |
| `stock` | INTEGER | Cantidad disponible | NOT NULL, CHECK >= 0 |
| `category_id` | INTEGER | FK a categories | NOT NULL, FK |
| `image_url` | VARCHAR(500) | URL de imagen | NULL permitido |
| `discount` | INTEGER | Descuento 0-100% | DEFAULT 0, CHECK |
| `active` | BOOLEAN | Activo/Inactivo | DEFAULT TRUE |
| `created_at` | TIMESTAMP | Fecha de creación | DEFAULT CURRENT_TIMESTAMP |
| `updated_at` | TIMESTAMP | Última actualización | DEFAULT CURRENT_TIMESTAMP |

### Datos de Ejemplo

```sql
INSERT INTO products (name, description, price, stock, category_id, image_url, discount) VALUES
('Biberón Avent 260ml', 'Biberón anticólicos con tetina suave', 45000, 50, 3, 'https://...', 10),
('Body Algodón Azul', 'Body 100% algodón talla 6-12 meses', 28000, 30, 1, 'https://...', 0),
('Peluche Oso', 'Peluche suave y seguro para bebés', 35000, 20, 2, 'https://...', 15);
```

### Queries Comunes

```sql
-- Productos activos por categoría
SELECT * FROM products 
WHERE category_id = 1 AND active = TRUE
ORDER BY name;

-- Productos con descuento
SELECT name, price, discount, 
       (price * (1 - discount/100.0)) as discounted_price
FROM products
WHERE discount > 0 AND active = TRUE;

-- Productos con stock bajo
SELECT * FROM products 
WHERE stock > 0 AND stock <= 5 AND active = TRUE;

-- Buscar productos
SELECT * FROM products 
WHERE LOWER(name) LIKE LOWER('%biberon%') 
   OR LOWER(description) LIKE LOWER('%biberon%');

-- Actualizar stock después de venta
UPDATE products 
SET stock = stock - 3, updated_at = CURRENT_TIMESTAMP
WHERE id = 1;
```

---

## 📦 Tabla: ORDERS

### Definición

```sql
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    total DECIMAL(10, 2) NOT NULL CHECK (total >= 0),
    status VARCHAR(20) DEFAULT 'PENDING' 
        CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
);
```

### Descripción de Campos

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| `id` | SERIAL | Identificador único | PRIMARY KEY |
| `user_id` | INTEGER | FK a users | NOT NULL, FK |
| `total` | DECIMAL(10,2) | Total de la orden | NOT NULL, CHECK >= 0 |
| `status` | VARCHAR(20) | Estado de la orden | DEFAULT 'PENDING', CHECK |
| `created_at` | TIMESTAMP | Fecha de creación | DEFAULT CURRENT_TIMESTAMP |
| `updated_at` | TIMESTAMP | Última actualización | DEFAULT CURRENT_TIMESTAMP |

### Estados de Orden

| Estado | Descripción |
|--------|-------------|
| `PENDING` | Orden creada, pendiente de procesar |
| `COMPLETED` | Orden completada y entregada |
| `CANCELLED` | Orden cancelada por usuario o admin |

### Datos de Ejemplo

```sql
INSERT INTO orders (user_id, total, status) VALUES
(2, 90000, 'PENDING'),
(3, 145000, 'COMPLETED'),
(2, 63000, 'PENDING');
```

### Queries Comunes

```sql
-- Órdenes de un usuario
SELECT * FROM orders 
WHERE user_id = 2 
ORDER BY created_at DESC;

-- Órdenes pendientes
SELECT o.id, u.name, o.total, o.created_at
FROM orders o
JOIN users u ON o.user_id = u.id
WHERE o.status = 'PENDING'
ORDER BY o.created_at;

-- Total de ventas completadas
SELECT SUM(total) as total_sales
FROM orders
WHERE status = 'COMPLETED';

-- Ventas por mes
SELECT 
    DATE_TRUNC('month', created_at) as month,
    COUNT(*) as total_orders,
    SUM(total) as total_sales
FROM orders
WHERE status = 'COMPLETED'
GROUP BY DATE_TRUNC('month', created_at)
ORDER BY month DESC;

-- Actualizar estado
UPDATE orders 
SET status = 'COMPLETED', updated_at = CURRENT_TIMESTAMP
WHERE id = 1;
```

---

## 📋 Tabla: ORDER_ITEMS

### Definición

```sql
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    
    CONSTRAINT fk_order FOREIGN KEY (order_id) 
        REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_product FOREIGN KEY (product_id) 
        REFERENCES products(id) ON DELETE RESTRICT
);
```

### Descripción de Campos

| Campo | Tipo | Descripción | Constraints |
|-------|------|-------------|-------------|
| `id` | SERIAL | Identificador único | PRIMARY KEY |
| `order_id` | INTEGER | FK a orders | NOT NULL, FK |
| `product_id` | INTEGER | FK a products | NOT NULL, FK |
| `quantity` | INTEGER | Cantidad comprada | NOT NULL, CHECK > 0 |
| `price` | DECIMAL(10,2) | Precio histórico | NOT NULL, CHECK >= 0 |

### Datos de Ejemplo

```sql
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 2, 45000),  -- 2 biberones
(1, 2, 1, 28000),  -- 1 body
(2, 3, 3, 35000),  -- 3 peluches
(3, 1, 1, 40500);  -- 1 biberón con descuento
```

### Queries Comunes

```sql
-- Items de una orden
SELECT 
    oi.quantity,
    p.name,
    oi.price,
    (oi.quantity * oi.price) as subtotal
FROM order_items oi
JOIN products p ON oi.product_id = p.id
WHERE oi.order_id = 1;

-- Productos más vendidos
SELECT 
    p.name,
    SUM(oi.quantity) as total_sold
FROM order_items oi
JOIN products p ON oi.product_id = p.id
JOIN orders o ON oi.order_id = o.id
WHERE o.status = 'COMPLETED'
GROUP BY p.id, p.name
ORDER BY total_sold DESC
LIMIT 10;

-- Verificar total de orden
SELECT 
    o.id,
    o.total as order_total,
    SUM(oi.quantity * oi.price) as calculated_total
FROM orders o
JOIN order_items oi ON o.id = oi.order_id
WHERE o.id = 1
GROUP BY o.id, o.total;
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Por qué usar CHECK constraints?"**

> "`CHECK` valida datos al insertarlos:
> ```sql
> CHECK (price >= 0)
> ```
> - No permite precios negativos
> - Validación a nivel de base de datos
> - Más seguro que validar solo en backend
> - Garantiza integridad incluso si insertas datos manualmente"

---

**2. "¿Por qué guardar price en order_items?"**

> "Para preservar precios históricos:
> - Producto cuesta $45.000 hoy
> - Usuario compra hoy
> - Mañana cambio precio a $50.000
> - Sin price en order_items, orden mostraría $50.000 (incorrecto)
> - Con price guardado, orden siempre muestra $45.000 (correcto)
> 
> Es auditoría: saber cuánto pagó en ese momento."

---

**3. "¿Qué hace ON DELETE CASCADE?"**

> "Elimina registros relacionados automáticamente:
> ```sql
> ON DELETE CASCADE
> ```
> - Si elimino order con id=1
> - Automáticamente elimina todos order_items con order_id=1
> - Previene datos huérfanos
> - Solo úsalo cuando eliminación debe propagarse"

---

**4. "¿Para qué sirven los índices?"**

> "Aceleran búsquedas:
> ```sql
> CREATE INDEX idx_users_email ON users(email);
> ```
> - Sin índice: base de datos lee toda la tabla (lento)
> - Con índice: base de datos usa estructura optimizada (rápido)
> - Como índice de libro: encuentras tema sin leer todo
> - Especialmente importante en tablas grandes"

---

## 📝 Resumen de Tablas

| Tabla | Registros Típicos | Relaciones |
|-------|-------------------|------------|
| `users` | 100 - 10,000 | → orders |
| `categories` | 5 - 20 | → products |
| `products` | 50 - 1,000 | → order_items |
| `orders` | 1,000 - 100,000 | → order_items, ← users |
| `order_items` | 3,000 - 300,000 | ← orders, ← products |

---

## 🚀 Conclusión

**Tablas de Baby Cash:**
- ✅ 5 tablas con campos bien definidos
- ✅ Constraints para validación
- ✅ Datos de ejemplo realistas
- ✅ Queries comunes documentados

**Es la estructura completa de la BD.**

---

**Ahora lee:** `RELACIONES-DB.md` para entender las relaciones. 🚀
