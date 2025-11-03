# RELACIONES DE BASE DE DATOS - BABY CASH

## 🎯 Visión General

Las **relaciones** conectan tablas entre sí mediante **claves foráneas (Foreign Keys)**. En Baby Cash tenemos 4 relaciones principales que mantienen la integridad referencial.

---

## 🔗 Tipos de Relaciones

### 1️⃣ Uno a Muchos (1:N)
Una fila en tabla A se relaciona con muchas filas en tabla B.

**Ejemplo:** Un usuario (1) puede tener muchas órdenes (N).

---

### 2️⃣ Muchos a Muchos (N:M)
Muchas filas en tabla A se relacionan con muchas filas en tabla B.

**Ejemplo:** Muchos productos están en muchas órdenes → tabla intermedia `order_items`.

---

## 📊 Diagrama de Relaciones

```
┌─────────────────┐
│     USERS       │
│   id (PK)       │
│   name          │
│   email         │
│   password      │
│   role          │
└────────┬────────┘
         │
         │ 1:N (Un usuario, muchas órdenes)
         │
         ↓
┌─────────────────┐
│    ORDERS       │
│   id (PK)       │
│   user_id (FK)  │◄────────────┐
│   total         │             │
│   status        │             │
└────────┬────────┘             │
         │                       │
         │ 1:N (Una orden,      │
         │      muchos items)   │ Referencia
         │                       │
         ↓                       │
┌─────────────────┐             │
│  ORDER_ITEMS    │             │
│   id (PK)       │             │
│   order_id (FK) │─────────────┘
│   product_id FK │─────────────┐
│   quantity      │             │
│   price         │             │
└─────────────────┘             │
                                 │
                                 │ Referencia
                                 │
                                 ↓
┌─────────────────┐        ┌────────────────┐
│   PRODUCTS      │        │  CATEGORIES    │
│   id (PK)       │◄───────│   id (PK)      │
│   name          │   FK   │   name         │
│   price         │        │   slug         │
│   stock         │        └────────────────┘
│   category_id FK│             1:N
└─────────────────┘      (Una categoría,
                          muchos productos)
```

---

## 🔗 Relación 1: Users → Orders (1:N)

### Descripción
- **Un usuario** puede crear **muchas órdenes**
- **Una orden** pertenece a **un solo usuario**

### Implementación

```sql
-- En tabla orders
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    
    -- Definir la relación
    CONSTRAINT fk_user FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE
);
```

### Cómo Funciona

```sql
-- Usuario con id=2
SELECT * FROM users WHERE id = 2;
-- Resultado: María González

-- Órdenes de María (user_id=2)
SELECT * FROM orders WHERE user_id = 2;
-- Resultado: Orden 1 ($90.000), Orden 3 ($63.000)
```

### JOIN para Obtener Datos Completos

```sql
SELECT 
    o.id as order_id,
    u.name as customer_name,
    u.email,
    o.total,
    o.status,
    o.created_at
FROM orders o
INNER JOIN users u ON o.user_id = u.id
WHERE u.id = 2;
```

**Resultado:**
```
order_id | customer_name    | email           | total    | status  | created_at
---------|------------------|-----------------|----------|---------|------------
1        | María González   | maria@email.com | 90000.00 | PENDING | 2025-10-30
3        | María González   | maria@email.com | 63000.00 | PENDING | 2025-10-31
```

### ON DELETE CASCADE

```sql
-- Si elimino usuario con id=2
DELETE FROM users WHERE id = 2;

-- ✅ Automáticamente elimina:
-- - Orden 1 (user_id=2)
-- - Orden 3 (user_id=2)
-- - Todos los order_items de esas órdenes

-- Esto previene órdenes huérfanas
```

---

## 🔗 Relación 2: Orders → Order_Items (1:N)

### Descripción
- **Una orden** contiene **muchos items**
- **Un item** pertenece a **una sola orden**

### Implementación

```sql
-- En tabla order_items
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    
    -- Definir la relación
    CONSTRAINT fk_order FOREIGN KEY (order_id) 
        REFERENCES orders(id) 
        ON DELETE CASCADE
);
```

### Ejemplo Completo

```sql
-- Orden con id=1
SELECT * FROM orders WHERE id = 1;
-- Resultado: user_id=2, total=$90.000

-- Items de orden 1
SELECT * FROM order_items WHERE order_id = 1;
-- Resultado:
-- Item 1: product_id=1, quantity=2, price=$45.000 → Subtotal: $90.000
-- Item 2: product_id=2, quantity=1, price=$28.000 → Subtotal: $28.000
-- TOTAL: $118.000 (pero order.total es $90.000 si hubo descuento)
```

### JOIN para Ver Detalles

```sql
SELECT 
    o.id as order_id,
    p.name as product_name,
    oi.quantity,
    oi.price,
    (oi.quantity * oi.price) as subtotal
FROM orders o
INNER JOIN order_items oi ON o.id = oi.order_id
INNER JOIN products p ON oi.product_id = p.id
WHERE o.id = 1;
```

**Resultado:**
```
order_id | product_name         | quantity | price     | subtotal
---------|---------------------|----------|-----------|----------
1        | Biberón Avent 260ml | 2        | 45000.00  | 90000.00
1        | Body Algodón Azul   | 1        | 28000.00  | 28000.00
```

---

## 🔗 Relación 3: Products → Order_Items (1:N)

### Descripción
- **Un producto** puede estar en **muchos items de órdenes**
- **Un item** referencia a **un solo producto**

### Implementación

```sql
-- En tabla order_items
CONSTRAINT fk_product FOREIGN KEY (product_id) 
    REFERENCES products(id) 
    ON DELETE RESTRICT
```

### ON DELETE RESTRICT

```sql
-- Producto con id=1 (Biberón)
SELECT * FROM products WHERE id = 1;

-- Está en orden_items
SELECT * FROM order_items WHERE product_id = 1;
-- Resultado: Item en orden 1, Item en orden 3

-- Intento eliminar producto
DELETE FROM products WHERE id = 1;

-- ❌ ERROR: Cannot delete because order_items references it
-- Esto PREVIENE eliminar productos que ya fueron vendidos
```

**¿Por qué RESTRICT?**
- No queremos perder historial de ventas
- Si eliminamos producto, items de órdenes quedarían sin referencia
- Con RESTRICT, primero debes eliminar/modificar order_items

### Productos Más Vendidos

```sql
SELECT 
    p.name,
    SUM(oi.quantity) as total_sold,
    COUNT(DISTINCT oi.order_id) as number_of_orders
FROM products p
INNER JOIN order_items oi ON p.id = oi.product_id
INNER JOIN orders o ON oi.order_id = o.id
WHERE o.status = 'COMPLETED'
GROUP BY p.id, p.name
ORDER BY total_sold DESC
LIMIT 5;
```

---

## 🔗 Relación 4: Categories → Products (1:N)

### Descripción
- **Una categoría** tiene **muchos productos**
- **Un producto** pertenece a **una sola categoría**

### Implementación

```sql
-- En tabla products
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    
    -- Definir la relación
    CONSTRAINT fk_category FOREIGN KEY (category_id) 
        REFERENCES categories(id) 
        ON DELETE RESTRICT
);
```

### Ejemplo

```sql
-- Categoría "Alimentación" (id=3)
SELECT * FROM categories WHERE id = 3;
-- Resultado: name='Alimentación', slug='alimentacion'

-- Productos de esa categoría
SELECT * FROM products WHERE category_id = 3;
-- Resultado:
-- Biberón Avent 260ml
-- Tetero Medela
-- Plato Infantil
```

### JOIN para Ver Productos con Categoría

```sql
SELECT 
    c.name as category_name,
    p.name as product_name,
    p.price,
    p.stock
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE c.slug = 'alimentacion';
```

### ON DELETE RESTRICT

```sql
-- Categoría "Alimentación" tiene productos
SELECT COUNT(*) FROM products WHERE category_id = 3;
-- Resultado: 15 productos

-- Intento eliminar categoría
DELETE FROM categories WHERE id = 3;

-- ❌ ERROR: Cannot delete because products reference it
-- Primero debes reasignar o eliminar productos
```

---

## 🔗 Relación N:M: Products ↔ Orders (Muchos a Muchos)

### Descripción
- **Muchos productos** están en **muchas órdenes**
- **Muchas órdenes** contienen **muchos productos**

### Implementación con Tabla Intermedia

```sql
-- order_items es la tabla intermedia (join table)
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,     -- FK a orders
    product_id INTEGER NOT NULL,   -- FK a products
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    
    CONSTRAINT fk_order FOREIGN KEY (order_id) 
        REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_product FOREIGN KEY (product_id) 
        REFERENCES products(id) ON DELETE RESTRICT
);
```

### Diagrama N:M

```
PRODUCTS                ORDER_ITEMS              ORDERS
┌────────────┐         ┌─────────────┐         ┌────────────┐
│ id=1       │◄────────│ product_id  │         │ id=1       │
│ Biberón    │         │ order_id    │────────►│ User: María│
└────────────┘         │ quantity    │         └────────────┘
                       └─────────────┘
                       
┌────────────┐         ┌─────────────┐         ┌────────────┐
│ id=2       │◄────────│ product_id  │         │ id=2       │
│ Body       │         │ order_id    │────────►│ User: Juan │
└────────────┘         │ quantity    │         └────────────┘
                       └─────────────┘

Producto 1 está en Orden 1 y Orden 3
Orden 1 tiene Producto 1 y Producto 2
```

### Query para Ver Relación Completa

```sql
SELECT 
    o.id as order_id,
    u.name as customer,
    p.name as product,
    oi.quantity,
    oi.price,
    (oi.quantity * oi.price) as subtotal
FROM orders o
INNER JOIN users u ON o.user_id = u.id
INNER JOIN order_items oi ON o.id = oi.order_id
INNER JOIN products p ON oi.product_id = p.id
WHERE o.id = 1;
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es una Foreign Key (FK)?"**

> "Es un campo que referencia la Primary Key de otra tabla:
> - `orders.user_id` es FK que apunta a `users.id`
> - Garantiza integridad: no puedes crear orden con user_id que no existe
> - Ejemplo: Si intentas `INSERT INTO orders (user_id) VALUES (999)` y no existe user con id=999, la base de datos rechaza la inserción."

---

**2. "¿Cuál es la diferencia entre ON DELETE CASCADE y RESTRICT?"**

> "**ON DELETE CASCADE:**
> - Elimina registros relacionados automáticamente
> - Ejemplo: Eliminar user elimina sus orders
> - Úsalo cuando dependencia es fuerte
> 
> **ON DELETE RESTRICT:**
> - Previene eliminación si hay registros relacionados
> - Ejemplo: No puedes eliminar product si está en order_items
> - Úsalo cuando quieres preservar historial"

---

**3. "¿Cómo funciona un JOIN?"**

> "`JOIN` combina filas de múltiples tablas basándose en relación:
> ```sql
> SELECT * 
> FROM orders o
> INNER JOIN users u ON o.user_id = u.id
> ```
> - `INNER JOIN`: Solo filas que coinciden en ambas tablas
> - `ON o.user_id = u.id`: Condición de unión (FK = PK)
> - Resultado: Datos de orders + datos de users en una sola fila"

---

**4. "¿Por qué usar tabla intermedia para N:M?"**

> "Sin tabla intermedia, tendrías que:
> - Guardar múltiples products en una orden como array (mala práctica)
> - O duplicar órdenes para cada producto (datos redundantes)
> 
> Con `order_items`:
> - Cada combinación orden-producto es una fila
> - Puedes agregar campos adicionales (quantity, price)
> - Fácil de consultar con JOINs
> - Normalizado (sin redundancia)"

---

## 📝 Resumen de Relaciones

| Relación | Tipo | FK en Tabla | ON DELETE |
|----------|------|-------------|-----------|
| Users → Orders | 1:N | orders.user_id | CASCADE |
| Orders → Order_Items | 1:N | order_items.order_id | CASCADE |
| Products → Order_Items | 1:N | order_items.product_id | RESTRICT |
| Categories → Products | 1:N | products.category_id | RESTRICT |
| Products ↔ Orders | N:M | order_items (intermedia) | - |

---

## 🚀 Conclusión

**Relaciones en Baby Cash:**
- ✅ 4 relaciones principales (3 directas + 1 N:M)
- ✅ Foreign Keys garantizan integridad
- ✅ ON DELETE controla comportamiento en eliminaciones
- ✅ JOINs permiten consultas complejas

**Las relaciones mantienen los datos consistentes y relacionados.**

---

**Ahora lee:** `QUERIES-IMPORTANTES.md` para queries útiles. 🚀
