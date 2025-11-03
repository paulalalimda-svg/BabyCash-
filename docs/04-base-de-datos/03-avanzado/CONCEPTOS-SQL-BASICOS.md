# 📚 CONCEPTOS BÁSICOS DE SQL

## 🎯 ¿Qué es SQL?

**SQL** = **S**tructured **Q**uery **L**anguage (Lenguaje de Consultas Estructuradas)

Es el **lenguaje** que usamos para comunicarnos con bases de datos relacionales.

---

## 📊 Tablas, Filas y Columnas

### Tabla

Es como una **hoja de Excel**. Guarda información de un tipo específico.

```
Tabla: users
+----+-----------------+----------+-------+
| id | email           | name     | age   |
+----+-----------------+----------+-------+
| 1  | maria@gmail.com | María    | 25    |
| 2  | juan@gmail.com  | Juan     | 30    |
| 3  | ana@gmail.com   | Ana      | 28    |
+----+-----------------+----------+-------+
```

### Columna (Campo)

Es una **característica** de la tabla. Cada columna tiene un **nombre** y un **tipo de dato**.

```
Columnas de la tabla users:
- id (número entero)
- email (texto)
- name (texto)
- age (número entero)
```

### Fila (Registro)

Es un **elemento individual**. Una fila = un usuario, un producto, una orden, etc.

```
Fila 1: { id: 1, email: "maria@gmail.com", name: "María", age: 25 }
Fila 2: { id: 2, email: "juan@gmail.com", name: "Juan", age: 30 }
```

---

## 🔢 Tipos de Datos en PostgreSQL

### Números Enteros

| Tipo | Rango | Uso |
|------|-------|-----|
| `SMALLINT` | -32,768 a 32,767 | Números pequeños |
| `INTEGER` | -2 mil millones a 2 mil millones | **Más usado** |
| `BIGINT` | -9 trillones a 9 trillones | IDs muy grandes |

**Ejemplo:**
```sql
CREATE TABLE products (
    id BIGINT,
    stock INTEGER,
    views SMALLINT
);
```

### Números Decimales

| Tipo | Descripción | Uso |
|------|-------------|-----|
| `DECIMAL(p, s)` | Precisión exacta | **Dinero** |
| `NUMERIC(p, s)` | Igual que DECIMAL | **Dinero** |
| `REAL` | 6 dígitos decimales | Cálculos científicos |
| `DOUBLE PRECISION` | 15 dígitos decimales | Cálculos científicos |

**Ejemplo:**
```sql
CREATE TABLE products (
    price DECIMAL(10, 2)  -- 10 dígitos, 2 decimales: 12345678.90
);
```

**⚠️ Para dinero, SIEMPRE usa DECIMAL o NUMERIC.**

### Texto

| Tipo | Longitud | Uso |
|------|----------|-----|
| `CHAR(n)` | Fija (n caracteres) | Códigos fijos |
| `VARCHAR(n)` | Variable (máximo n) | **Más usado** |
| `TEXT` | Ilimitado | Descripciones largas |

**Ejemplo:**
```sql
CREATE TABLE users (
    email VARCHAR(100),     -- Máximo 100 caracteres
    name VARCHAR(50),
    bio TEXT                -- Sin límite
);
```

### Fecha y Hora

| Tipo | Qué Guarda | Ejemplo |
|------|------------|---------|
| `DATE` | Solo fecha | `2025-10-30` |
| `TIME` | Solo hora | `19:30:00` |
| `TIMESTAMP` | Fecha + hora | `2025-10-30 19:30:00` |
| `TIMESTAMPTZ` | Fecha + hora + zona | `2025-10-30 19:30:00-05` |

**Ejemplo:**
```sql
CREATE TABLE orders (
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Booleano

```sql
CREATE TABLE users (
    active BOOLEAN DEFAULT TRUE
);

INSERT INTO users (active) VALUES (TRUE);
INSERT INTO users (active) VALUES (FALSE);
```

### Otros Tipos (PostgreSQL)

```sql
-- JSON
CREATE TABLE products (
    metadata JSON
);

-- Array
CREATE TABLE users (
    tags TEXT[]
);

-- UUID
CREATE TABLE sessions (
    id UUID DEFAULT gen_random_uuid()
);
```

---

## 🔑 Primary Key (Clave Primaria)

### ¿Qué es?

La **columna que identifica de forma única** cada fila. No puede repetirse.

### Ejemplo

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,    -- ← Primary Key
    email VARCHAR(100),
    name VARCHAR(50)
);

INSERT INTO users (id, email, name) VALUES (1, 'maria@gmail.com', 'María');
INSERT INTO users (id, email, name) VALUES (2, 'juan@gmail.com', 'Juan');
INSERT INTO users (id, email, name) VALUES (1, 'ana@gmail.com', 'Ana');  -- ❌ ERROR: id=1 ya existe
```

### Auto-Incremento

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,  -- Se genera automáticamente: 1, 2, 3, ...
    email VARCHAR(100),
    name VARCHAR(50)
);

INSERT INTO users (email, name) VALUES ('maria@gmail.com', 'María');  -- id = 1
INSERT INTO users (email, name) VALUES ('juan@gmail.com', 'Juan');    -- id = 2
```

---

## 🔗 Foreign Key (Clave Foránea)

### ¿Qué es?

Una **columna que referencia la Primary Key de otra tabla**. Crea relaciones.

### Ejemplo

```sql
-- Tabla padre
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50)
);

-- Tabla hija
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,                    -- ← Foreign Key
    total DECIMAL(10, 2),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insertar datos
INSERT INTO users (id, name) VALUES (1, 'María');
INSERT INTO orders (id, user_id, total) VALUES (1, 1, 150000);  -- ✅ OK
INSERT INTO orders (id, user_id, total) VALUES (2, 99, 80000);  -- ❌ ERROR: user_id=99 no existe
```

### Restricciones de Foreign Key

```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE      -- Si se elimina el user, eliminar sus orders
        ON UPDATE CASCADE      -- Si se actualiza user.id, actualizar user_id
);
```

| Opción | Qué Hace |
|--------|----------|
| `CASCADE` | Elimina/actualiza en cascada |
| `SET NULL` | Pone NULL si se elimina |
| `RESTRICT` | No permite eliminar si hay referencias |
| `NO ACTION` | Igual que RESTRICT |

---

## 🎓 Ejemplo: Base de Datos BabyCash

### Crear Tablas

```sql
-- Tabla users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla products
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla carts (1:1 con users)
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla cart_items (N:1 con carts, N:1 con products)
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Tabla orders (N:1 con users)
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    shipping_address TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla order_items (N:1 con orders, N:1 con products)
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,  -- Precio histórico
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### Insertar Datos

```sql
-- Insertar usuario
INSERT INTO users (email, password, name, role) 
VALUES ('maria@gmail.com', '$2a$10$...', 'María García', 'USER');

-- Insertar productos
INSERT INTO products (name, price, stock) VALUES 
('Pañales Huggies', 45000, 50),
('Leche NAN', 15000, 100),
('Toallitas Húmedas', 8000, 30);

-- Crear carrito para usuario
INSERT INTO carts (user_id) VALUES (1);

-- Agregar items al carrito
INSERT INTO cart_items (cart_id, product_id, quantity) VALUES 
(1, 1, 2),  -- 2 pañales
(1, 2, 3);  -- 3 leches
```

### Consultar Datos

```sql
-- Ver todos los usuarios
SELECT * FROM users;

-- Ver productos con precio > 10000
SELECT * FROM products WHERE price > 10000;

-- Ver carrito de usuario 1 con productos
SELECT 
    p.name,
    ci.quantity,
    p.price,
    (ci.quantity * p.price) AS subtotal
FROM cart_items ci
JOIN products p ON ci.product_id = p.id
WHERE ci.cart_id = 1;

-- Ver órdenes de usuario con total
SELECT 
    o.order_number,
    o.total,
    o.status,
    o.created_at
FROM orders o
WHERE o.user_id = 1
ORDER BY o.created_at DESC;
```

---

## 📐 Restricciones (Constraints)

### NOT NULL

```sql
CREATE TABLE users (
    email VARCHAR(100) NOT NULL  -- No puede ser NULL
);
```

### UNIQUE

```sql
CREATE TABLE users (
    email VARCHAR(100) UNIQUE  -- No puede repetirse
);
```

### CHECK

```sql
CREATE TABLE products (
    price DECIMAL(10, 2) CHECK (price > 0),  -- Precio debe ser > 0
    stock INTEGER CHECK (stock >= 0)         -- Stock no puede ser negativo
);
```

### DEFAULT

```sql
CREATE TABLE users (
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 📊 Resumen de Comandos SQL Básicos

### DDL (Data Definition Language)

```sql
-- Crear tabla
CREATE TABLE users (...);

-- Modificar tabla
ALTER TABLE users ADD COLUMN phone VARCHAR(20);

-- Eliminar tabla
DROP TABLE users;
```

### DML (Data Manipulation Language)

```sql
-- Insertar
INSERT INTO users (email, name) VALUES ('test@test.com', 'Test');

-- Consultar
SELECT * FROM users WHERE active = TRUE;

-- Actualizar
UPDATE users SET active = FALSE WHERE id = 1;

-- Eliminar
DELETE FROM users WHERE id = 1;
```

---

## 📋 Resumen

| Concepto | Definición | Ejemplo |
|----------|------------|---------|
| **Tabla** | Colección de datos | `users`, `products` |
| **Columna** | Atributo de la tabla | `id`, `email`, `name` |
| **Fila** | Registro individual | Usuario con id=1 |
| **Primary Key** | Identificador único | `id BIGSERIAL PRIMARY KEY` |
| **Foreign Key** | Relación entre tablas | `user_id REFERENCES users(id)` |
| **NOT NULL** | Campo obligatorio | `email VARCHAR(100) NOT NULL` |
| **UNIQUE** | No puede repetirse | `email VARCHAR(100) UNIQUE` |

---

**Última actualización**: Octubre 2025
