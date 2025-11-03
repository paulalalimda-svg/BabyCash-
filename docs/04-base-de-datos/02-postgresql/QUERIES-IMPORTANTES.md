# QUERIES IMPORTANTES - BABY CASH

## 🎯 Visión General

Este documento contiene las **queries SQL más importantes** para Baby Cash, organizadas por funcionalidad. Estas queries son las que usa el backend para las operaciones CRUD.

---

## 👤 Queries de USERS

### 1️⃣ Registrar Usuario

```sql
INSERT INTO users (name, email, password, role)
VALUES ('Juan Pérez', 'juan@email.com', '$2a$10$hashedpassword...', 'USER')
RETURNING id, name, email, role, created_at;
```

**Uso:** Al registrarse un nuevo usuario.

---

### 2️⃣ Buscar Usuario por Email (Login)

```sql
SELECT id, name, email, password, role, created_at
FROM users
WHERE email = 'juan@email.com';
```

**Uso:** Al iniciar sesión. Backend compara password hasheado.

---

### 3️⃣ Actualizar Datos de Usuario

```sql
UPDATE users
SET name = 'Juan Carlos Pérez',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1
RETURNING id, name, email, role;
```

---

### 4️⃣ Contar Usuarios por Rol

```sql
SELECT role, COUNT(*) as total
FROM users
GROUP BY role;
```

**Resultado:**
```
role  | total
------|------
USER  | 145
ADMIN | 2
```

---

## 📂 Queries de CATEGORIES

### 1️⃣ Obtener Todas las Categorías

```sql
SELECT id, name, slug, description, icon
FROM categories
ORDER BY name;
```

---

### 2️⃣ Buscar Categoría por Slug

```sql
SELECT id, name, slug, description
FROM categories
WHERE slug = 'ropa';
```

---

### 3️⃣ Categorías con Conteo de Productos

```sql
SELECT 
    c.id,
    c.name,
    c.slug,
    COUNT(p.id) as total_products
FROM categories c
LEFT JOIN products p ON c.id = p.category_id AND p.active = TRUE
GROUP BY c.id, c.name, c.slug
ORDER BY c.name;
```

**Resultado:**
```
id | name         | slug         | total_products
---|--------------|--------------|---------------
1  | Ropa         | ropa         | 23
2  | Juguetes     | juguetes     | 15
3  | Alimentación | alimentacion | 18
4  | Higiene      | higiene      | 12
```

---

## 🛍️ Queries de PRODUCTS

### 1️⃣ Obtener Todos los Productos Activos

```sql
SELECT 
    p.id,
    p.name,
    p.description,
    p.price,
    p.stock,
    p.image_url,
    p.discount,
    c.id as category_id,
    c.name as category_name,
    c.slug as category_slug
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE p.active = TRUE
ORDER BY p.created_at DESC;
```

---

### 2️⃣ Buscar Producto por ID

```sql
SELECT 
    p.*,
    c.name as category_name,
    c.slug as category_slug
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE p.id = 1 AND p.active = TRUE;
```

---

### 3️⃣ Buscar Productos (Search)

```sql
SELECT 
    p.id,
    p.name,
    p.description,
    p.price,
    p.stock,
    p.image_url,
    p.discount,
    c.name as category_name
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE p.active = TRUE
  AND (
    LOWER(p.name) LIKE LOWER('%biberon%') OR
    LOWER(p.description) LIKE LOWER('%biberon%')
  )
ORDER BY p.name;
```

**Uso:** Búsqueda en barra de navegación.

---

### 4️⃣ Filtrar Productos por Categoría

```sql
SELECT 
    p.id,
    p.name,
    p.price,
    p.stock,
    p.image_url,
    p.discount
FROM products p
WHERE p.category_id = 1
  AND p.active = TRUE
ORDER BY p.name;
```

---

### 5️⃣ Productos con Descuento

```sql
SELECT 
    p.id,
    p.name,
    p.price,
    p.discount,
    ROUND(p.price * (1 - p.discount / 100.0), 2) as discounted_price,
    c.name as category_name
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE p.discount > 0
  AND p.active = TRUE
ORDER BY p.discount DESC;
```

**Resultado:**
```
id | name                | price     | discount | discounted_price | category_name
---|---------------------|-----------|----------|------------------|---------------
3  | Peluche Oso         | 35000.00  | 15       | 29750.00         | Juguetes
1  | Biberón Avent 260ml | 45000.00  | 10       | 40500.00         | Alimentación
```

---

### 6️⃣ Productos con Stock Bajo

```sql
SELECT id, name, stock, price
FROM products
WHERE stock > 0 AND stock <= 5
  AND active = TRUE
ORDER BY stock ASC;
```

**Uso:** Alertas de inventario en panel admin.

---

### 7️⃣ Actualizar Stock Después de Venta

```sql
UPDATE products
SET stock = stock - 3,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1
RETURNING id, name, stock;
```

**Uso:** Al crear orden, se reduce stock de productos.

---

### 8️⃣ Crear Producto (Admin)

```sql
INSERT INTO products (
    name, description, price, stock, 
    category_id, image_url, discount
)
VALUES (
    'Pañales Huggies x40',
    'Pañales ultra absorbentes',
    35000,
    100,
    4,
    'https://example.com/image.jpg',
    0
)
RETURNING id, name, price, stock;
```

---

## 📦 Queries de ORDERS

### 1️⃣ Crear Orden

```sql
-- Primero crear la orden
INSERT INTO orders (user_id, total, status)
VALUES (2, 90000, 'PENDING')
RETURNING id, user_id, total, status, created_at;

-- Luego crear los items (en transacción)
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES 
    (1, 1, 2, 45000),  -- 2 biberones
    (1, 2, 1, 28000);  -- 1 body
```

---

### 2️⃣ Obtener Órdenes de Usuario

```sql
SELECT 
    o.id,
    o.total,
    o.status,
    o.created_at,
    COUNT(oi.id) as total_items
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
WHERE o.user_id = 2
GROUP BY o.id, o.total, o.status, o.created_at
ORDER BY o.created_at DESC;
```

**Resultado:**
```
id | total     | status  | created_at          | total_items
---|-----------|---------|---------------------|------------
3  | 63000.00  | PENDING | 2025-10-31 14:30:00 | 2
1  | 90000.00  | PENDING | 2025-10-30 10:15:00 | 3
```

---

### 3️⃣ Obtener Detalle Completo de Orden

```sql
SELECT 
    o.id as order_id,
    o.total as order_total,
    o.status,
    o.created_at,
    u.name as customer_name,
    u.email as customer_email,
    oi.id as item_id,
    p.id as product_id,
    p.name as product_name,
    p.image_url,
    oi.quantity,
    oi.price,
    (oi.quantity * oi.price) as item_subtotal
FROM orders o
INNER JOIN users u ON o.user_id = u.id
INNER JOIN order_items oi ON o.id = oi.order_id
INNER JOIN products p ON oi.product_id = p.id
WHERE o.id = 1;
```

**Resultado:**
```
order_id | order_total | status  | customer_name  | product_name         | quantity | price     | item_subtotal
---------|-------------|---------|----------------|---------------------|----------|-----------|---------------
1        | 90000.00    | PENDING | María González | Biberón Avent 260ml | 2        | 45000.00  | 90000.00
1        | 90000.00    | PENDING | María González | Body Algodón Azul   | 1        | 28000.00  | 28000.00
```

---

### 4️⃣ Órdenes Pendientes (Admin)

```sql
SELECT 
    o.id,
    u.name as customer_name,
    u.email,
    o.total,
    o.created_at,
    COUNT(oi.id) as total_items
FROM orders o
INNER JOIN users u ON o.user_id = u.id
LEFT JOIN order_items oi ON o.id = oi.order_id
WHERE o.status = 'PENDING'
GROUP BY o.id, u.name, u.email, o.total, o.created_at
ORDER BY o.created_at ASC;
```

---

### 5️⃣ Actualizar Estado de Orden

```sql
UPDATE orders
SET status = 'COMPLETED',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1
RETURNING id, status, updated_at;
```

---

### 6️⃣ Cancelar Orden

```sql
UPDATE orders
SET status = 'CANCELLED',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1 AND user_id = 2  -- Solo el dueño puede cancelar
RETURNING id, status;
```

---

## 📊 Queries de REPORTES

### 1️⃣ Total de Ventas

```sql
SELECT 
    COUNT(*) as total_orders,
    SUM(total) as total_sales,
    AVG(total) as average_order
FROM orders
WHERE status = 'COMPLETED';
```

**Resultado:**
```
total_orders | total_sales  | average_order
-------------|--------------|---------------
342          | 15430000.00  | 45117.65
```

---

### 2️⃣ Ventas por Mes

```sql
SELECT 
    DATE_TRUNC('month', created_at) as month,
    COUNT(*) as total_orders,
    SUM(total) as monthly_sales
FROM orders
WHERE status = 'COMPLETED'
GROUP BY DATE_TRUNC('month', created_at)
ORDER BY month DESC
LIMIT 12;
```

**Resultado:**
```
month       | total_orders | monthly_sales
------------|--------------|---------------
2025-10-01  | 89           | 4230000.00
2025-09-01  | 102          | 4890000.00
2025-08-01  | 95           | 4510000.00
```

---

### 3️⃣ Productos Más Vendidos

```sql
SELECT 
    p.id,
    p.name,
    c.name as category,
    SUM(oi.quantity) as total_sold,
    SUM(oi.quantity * oi.price) as total_revenue
FROM products p
INNER JOIN categories c ON p.category_id = c.id
INNER JOIN order_items oi ON p.id = oi.product_id
INNER JOIN orders o ON oi.order_id = o.id
WHERE o.status = 'COMPLETED'
GROUP BY p.id, p.name, c.name
ORDER BY total_sold DESC
LIMIT 10;
```

**Resultado:**
```
id | name                | category      | total_sold | total_revenue
---|---------------------|---------------|------------|---------------
1  | Biberón Avent 260ml | Alimentación  | 156        | 7020000.00
3  | Peluche Oso         | Juguetes      | 89         | 3115000.00
2  | Body Algodón Azul   | Ropa          | 67         | 1876000.00
```

---

### 4️⃣ Clientes con Más Compras

```sql
SELECT 
    u.id,
    u.name,
    u.email,
    COUNT(o.id) as total_orders,
    SUM(o.total) as total_spent
FROM users u
INNER JOIN orders o ON u.id = o.user_id
WHERE o.status = 'COMPLETED'
GROUP BY u.id, u.name, u.email
ORDER BY total_spent DESC
LIMIT 10;
```

---

### 5️⃣ Inventario Actual

```sql
SELECT 
    c.name as category,
    COUNT(p.id) as total_products,
    SUM(p.stock) as total_stock,
    SUM(p.stock * p.price) as inventory_value
FROM categories c
LEFT JOIN products p ON c.id = p.category_id AND p.active = TRUE
GROUP BY c.id, c.name
ORDER BY inventory_value DESC;
```

**Resultado:**
```
category      | total_products | total_stock | inventory_value
--------------|----------------|-------------|------------------
Alimentación  | 18             | 450         | 22500000.00
Ropa          | 23             | 380         | 18900000.00
Juguetes      | 15             | 290         | 14500000.00
Higiene       | 12             | 220         | 11000000.00
```

---

## 🔍 Queries de BÚSQUEDA AVANZADA

### 1️⃣ Buscar con Múltiples Filtros

```sql
SELECT 
    p.id,
    p.name,
    p.price,
    p.stock,
    p.discount,
    ROUND(p.price * (1 - p.discount / 100.0), 2) as final_price,
    c.name as category
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE p.active = TRUE
  AND (p.category_id = 1 OR 1 = 0)  -- Filtro por categoría (opcional)
  AND p.price BETWEEN 20000 AND 50000  -- Filtro por rango de precio
  AND (
    LOWER(p.name) LIKE LOWER('%baby%') OR
    LOWER(p.description) LIKE LOWER('%baby%')
  )  -- Filtro por búsqueda
ORDER BY final_price ASC;
```

---

### 2️⃣ Productos Relacionados (Misma Categoría)

```sql
SELECT 
    p.id,
    p.name,
    p.price,
    p.image_url,
    p.discount
FROM products p
WHERE p.category_id = (SELECT category_id FROM products WHERE id = 1)
  AND p.id != 1  -- Excluir el producto actual
  AND p.active = TRUE
ORDER BY RANDOM()
LIMIT 4;
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es un JOIN y por qué lo usas?"**

> "`JOIN` combina datos de múltiples tablas:
> ```sql
> FROM orders o
> INNER JOIN users u ON o.user_id = u.id
> ```
> - `orders` tiene `user_id` (solo el número)
> - `users` tiene todos los datos del usuario
> - JOIN trae datos completos en una consulta
> - Sin JOIN, necesitarías 2 queries separadas"

---

**2. "¿Qué hace GROUP BY?"**

> "`GROUP BY` agrupa filas con mismo valor:
> ```sql
> SELECT category_id, COUNT(*)
> FROM products
> GROUP BY category_id
> ```
> - Agrupa productos por categoría
> - Cuenta cuántos hay en cada grupo
> - Útil para reportes y estadísticas"

---

**3. "¿Para qué sirve RETURNING?"**

> "`RETURNING` devuelve datos después de INSERT/UPDATE/DELETE:
> ```sql
> INSERT INTO users (name, email) VALUES ('Juan', 'juan@email.com')
> RETURNING id, name, created_at;
> ```
> - Evita hacer SELECT después de INSERT
> - Más eficiente (una sola query)
> - PostgreSQL lo soporta nativamente"

---

**4. "¿Qué es una transacción?"**

> "Transacción agrupa múltiples queries en una operación atómica:
> ```sql
> BEGIN;
>   INSERT INTO orders (...) VALUES (...);
>   INSERT INTO order_items (...) VALUES (...);
>   UPDATE products SET stock = stock - 1 WHERE id = 1;
> COMMIT;
> ```
> - Si una falla, todas se revierten (ROLLBACK)
> - Garantiza consistencia (ACID)
> - Crítico para operaciones relacionadas"

---

## 📝 Resumen de Queries

| Categoría | Queries Principales |
|-----------|---------------------|
| **Users** | Register, Login, Update, Count by Role |
| **Categories** | Get All, Get by Slug, Count Products |
| **Products** | Get All, Search, Filter, Get by ID, Update Stock |
| **Orders** | Create, Get by User, Get Detail, Update Status |
| **Reports** | Total Sales, Monthly Sales, Top Products, Top Customers |

---

## 🚀 Conclusión

**Queries de Baby Cash:**
- ✅ CRUD completo para cada entidad
- ✅ JOINs para datos relacionados
- ✅ Reportes y estadísticas
- ✅ Búsquedas y filtros avanzados

**Estas queries son el corazón de la aplicación.**

---

## 🎉 BASE DE DATOS COMPLETAMENTE DOCUMENTADA

**4 archivos de Base de Datos:**
- ✅ ESQUEMA-BASE-DATOS.md (estructura, tablas, relaciones)
- ✅ TABLAS-PRINCIPALES.md (detalle de cada tabla)
- ✅ RELACIONES-DB.md (FK, JOIN, integridad)
- ✅ QUERIES-IMPORTANTES.md (SQL práctico)

**DOCUMENTACIÓN COMPLETA DE BABY CASH:**
- ✅ Backend: 29 archivos (SOLID, Clean Code, Patrones)
- ✅ Frontend: 14 archivos (Fundamentos, Páginas, Componentes)
- ✅ Base de Datos: 4 archivos (Esquema, Tablas, Relaciones, Queries)
- ✅ Guías: 2 archivos (README, Script Presentación)

**TOTAL: 49 archivos de documentación técnica completa** 📚🎉
