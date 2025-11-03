# SQL - CONCEPTOS BÁSICOS

## 📊 ¿Qué es SQL?

**Structured Query Language** - Lenguaje para interactuar con bases de datos relacionales.

### Analogía: Biblioteca 📚

```
Base de Datos = Biblioteca
Tabla = Estantería
Fila = Libro
Columna = Característica (título, autor, año)
SQL = Preguntas al bibliotecario
```

---

## 🗂️ Estructura de una Base de Datos

### Tabla: products

| id | name | price | stock | created_at |
|----|------|-------|-------|------------|
| 1 | Pañales | 45000 | 100 | 2024-01-15 |
| 2 | Biberón | 25000 | 50 | 2024-01-16 |
| 3 | Cuna | 350000 | 10 | 2024-01-17 |

**Componentes:**
- **Tabla:** products (colección de datos relacionados)
- **Columna:** name, price, stock (atributo)
- **Fila:** Cada producto (registro)
- **Primary Key:** id (identificador único)

---

## 📖 SELECT - Leer Datos

### Seleccionar Todo

```sql
-- Todos los productos
SELECT * FROM products;

-- Columnas específicas
SELECT name, price FROM products;
```

### WHERE - Filtrar

```sql
-- Productos con precio menor a 50000
SELECT * FROM products
WHERE price < 50000;

-- Producto específico
SELECT * FROM products
WHERE id = 1;

-- Nombre exacto
SELECT * FROM products
WHERE name = 'Pañales';

-- Búsqueda parcial
SELECT * FROM products
WHERE name LIKE '%Pañ%'; -- Contiene "Pañ"

-- Múltiples condiciones
SELECT * FROM products
WHERE price > 20000 AND stock > 10;

SELECT * FROM products
WHERE category_id = 1 OR category_id = 2;
```

### ORDER BY - Ordenar

```sql
-- Precio ascendente
SELECT * FROM products
ORDER BY price ASC;

-- Precio descendente
SELECT * FROM products
ORDER BY price DESC;

-- Múltiples columnas
SELECT * FROM products
ORDER BY category_id ASC, price DESC;
```

### LIMIT - Limitar Resultados

```sql
-- Primeros 10 productos
SELECT * FROM products
LIMIT 10;

-- Paginación (página 2, 10 por página)
SELECT * FROM products
LIMIT 10 OFFSET 10;
```

---

## ➕ INSERT - Crear Datos

```sql
-- Insertar un producto
INSERT INTO products (name, price, stock, category_id)
VALUES ('Pañales Premium', 55000, 50, 1);

-- Insertar múltiples
INSERT INTO products (name, price, stock)
VALUES
  ('Producto 1', 10000, 20),
  ('Producto 2', 15000, 30),
  ('Producto 3', 20000, 40);

-- Con todas las columnas (incluye id auto-generado)
INSERT INTO products
VALUES (DEFAULT, 'Producto', 10000, 'Descripción', 50, 1, CURRENT_TIMESTAMP);
```

---

## ✏️ UPDATE - Actualizar Datos

```sql
-- Actualizar precio de un producto
UPDATE products
SET price = 48000
WHERE id = 1;

-- Actualizar múltiples columnas
UPDATE products
SET price = 50000, stock = 80
WHERE id = 1;

-- Actualizar múltiples productos
UPDATE products
SET price = price * 1.1 -- Aumentar 10%
WHERE category_id = 1;

-- ⚠️ Sin WHERE actualiza TODAS las filas
UPDATE products
SET stock = 0; -- ¡Todos los productos sin stock!
```

---

## 🗑️ DELETE - Eliminar Datos

```sql
-- Eliminar un producto
DELETE FROM products
WHERE id = 1;

-- Eliminar múltiples
DELETE FROM products
WHERE stock = 0;

-- ⚠️ Sin WHERE elimina TODO
DELETE FROM products; -- ¡Elimina todos los productos!

-- Mejor usar TRUNCATE para vaciar tabla
TRUNCATE TABLE products;
```

---

## 🔗 JOIN - Unir Tablas

### INNER JOIN

```sql
-- Productos con su categoría
SELECT
  products.name,
  products.price,
  categories.name AS category_name
FROM products
INNER JOIN categories ON products.category_id = categories.id;
```

**Resultado:**

| name | price | category_name |
|------|-------|---------------|
| Pañales | 45000 | Bebé |
| Biberón | 25000 | Bebé |
| Juguete | 35000 | Juguetes |

### LEFT JOIN

```sql
-- Todos los productos, aunque no tengan categoría
SELECT
  products.name,
  categories.name AS category_name
FROM products
LEFT JOIN categories ON products.category_id = categories.id;
```

### JOIN con Múltiples Tablas

```sql
-- Pedidos con usuario y productos
SELECT
  orders.id AS order_id,
  users.email,
  products.name AS product_name,
  order_items.quantity,
  order_items.price
FROM orders
INNER JOIN users ON orders.user_id = users.id
INNER JOIN order_items ON orders.id = order_items.order_id
INNER JOIN products ON order_items.product_id = products.id;
```

---

## 📊 Funciones Agregadas

### COUNT

```sql
-- Contar productos
SELECT COUNT(*) FROM products;

-- Contar por categoría
SELECT category_id, COUNT(*) AS total
FROM products
GROUP BY category_id;
```

### SUM

```sql
-- Valor total del inventario
SELECT SUM(price * stock) AS total_value
FROM products;
```

### AVG

```sql
-- Precio promedio
SELECT AVG(price) AS average_price
FROM products;
```

### MAX / MIN

```sql
-- Producto más caro
SELECT MAX(price) AS max_price FROM products;

-- Producto más barato
SELECT MIN(price) AS min_price FROM products;
```

### GROUP BY

```sql
-- Productos por categoría
SELECT
  category_id,
  COUNT(*) AS total_products,
  AVG(price) AS avg_price,
  SUM(stock) AS total_stock
FROM products
GROUP BY category_id;

-- Con HAVING (filtrar grupos)
SELECT
  category_id,
  COUNT(*) AS total
FROM products
GROUP BY category_id
HAVING COUNT(*) > 5; -- Categorías con más de 5 productos
```

---

## 🔑 Constraints (Restricciones)

### PRIMARY KEY

```sql
CREATE TABLE products (
  id SERIAL PRIMARY KEY, -- Auto-incremento y único
  name VARCHAR(100)
);
```

### FOREIGN KEY

```sql
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  category_id INTEGER,
  FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

### UNIQUE

```sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE -- No duplicados
);
```

### NOT NULL

```sql
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL, -- Obligatorio
  price NUMERIC NOT NULL
);
```

### CHECK

```sql
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  price NUMERIC CHECK (price > 0), -- Precio positivo
  stock INTEGER CHECK (stock >= 0) -- Stock no negativo
);
```

### DEFAULT

```sql
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  stock INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🎯 Queries Comunes en Baby Cash

### Productos por Categoría

```sql
SELECT p.*, c.name AS category_name
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE c.id = 1
ORDER BY p.price ASC;
```

### Búsqueda de Productos

```sql
SELECT * FROM products
WHERE name ILIKE '%pañal%' -- Case-insensitive
   OR description ILIKE '%pañal%'
ORDER BY created_at DESC;
```

### Top 10 Productos Más Vendidos

```sql
SELECT
  p.name,
  SUM(oi.quantity) AS total_sold
FROM products p
INNER JOIN order_items oi ON p.id = oi.product_id
GROUP BY p.id, p.name
ORDER BY total_sold DESC
LIMIT 10;
```

### Pedidos de un Usuario

```sql
SELECT
  o.id,
  o.created_at,
  o.status,
  o.total,
  COUNT(oi.id) AS total_items
FROM orders o
INNER JOIN order_items oi ON o.id = oi.order_id
WHERE o.user_id = 123
GROUP BY o.id
ORDER BY o.created_at DESC;
```

### Productos Agotados

```sql
SELECT * FROM products
WHERE stock = 0
ORDER BY name;
```

### Ventas por Mes

```sql
SELECT
  TO_CHAR(created_at, 'YYYY-MM') AS month,
  COUNT(*) AS total_orders,
  SUM(total) AS total_revenue
FROM orders
WHERE status = 'DELIVERED'
GROUP BY month
ORDER BY month DESC;
```

---

## 🎓 Para la Evaluación del SENA

**1. "¿Qué es SQL?"**

> "Structured Query Language. Lenguaje para interactuar con bases de datos relacionales. Permite SELECT (leer), INSERT (crear), UPDATE (actualizar), DELETE (eliminar)."

**2. "¿Para qué sirve WHERE?"**

> "Filtrar resultados. Ejemplo: `SELECT * FROM products WHERE price < 50000` - Solo productos con precio menor a 50000."

**3. "¿Qué es un JOIN?"**

> "Unir datos de múltiples tablas. Ejemplo: `SELECT * FROM products INNER JOIN categories ON products.category_id = categories.id` - Productos con su categoría."

**4. "¿Qué hace GROUP BY?"**

> "Agrupar filas y aplicar funciones agregadas. Ejemplo: `SELECT category_id, COUNT(*) FROM products GROUP BY category_id` - Contar productos por categoría."

---

**Siguiente:** Comandos del Proyecto 🚀
