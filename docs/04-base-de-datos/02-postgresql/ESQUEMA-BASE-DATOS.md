# ESQUEMA DE BASE DE DATOS - BABY CASH

## 🎯 Visión General

La base de datos de Baby Cash usa **PostgreSQL** con un esquema relacional normalizado que garantiza:
- Integridad referencial
- Consistencia de datos
- Optimización de consultas
- Escalabilidad

---

## 🗄️ Motor de Base de Datos

**PostgreSQL 14+**

**¿Por qué PostgreSQL?**
- ✅ Open source y gratuito
- ✅ ACID compliance (transacciones seguras)
- ✅ Soporta JSON (flexibilidad)
- ✅ Excelente rendimiento
- ✅ Gran comunidad y documentación

---

## 📊 Diagrama Entidad-Relación (ER)

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   USERS     │────────>│   ORDERS     │────────>│ ORDER_ITEMS │
│             │ 1     * │              │ 1     * │             │
│ - id (PK)   │         │ - id (PK)    │         │ - id (PK)   │
│ - name      │         │ - user_id FK │         │ - order_id FK│
│ - email     │         │ - total      │         │ - product_id│
│ - password  │         │ - status     │         │ - quantity  │
│ - role      │         │ - created_at │         │ - price     │
└─────────────┘         └──────────────┘         └─────────────┘
                                                         │
                                                         │ *
                                                         │
                                                         ↓ 1
┌──────────────┐         ┌─────────────┐         ┌─────────────┐
│ CATEGORIES   │────────>│  PRODUCTS   │<────────│ ORDER_ITEMS │
│              │ 1     * │             │         │             │
│ - id (PK)    │         │ - id (PK)   │         └─────────────┘
│ - name       │         │ - name      │
│ - slug       │         │ - description│
│ - icon       │         │ - price     │
└──────────────┘         │ - stock     │
                         │ - category_id│
                         │ - image_url │
                         │ - discount  │
                         └─────────────┘
```

---

## 📋 Tablas Principales

### 1️⃣ USERS (Usuarios)

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Campos:**
- `id`: Identificador único (auto-incremental)
- `name`: Nombre completo del usuario
- `email`: Email único para login
- `password`: Contraseña hasheada con BCrypt
- `role`: Rol del usuario (USER o ADMIN)
- `created_at`: Fecha de creación
- `updated_at`: Fecha de última actualización

**Índices:**
```sql
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
```

---

### 2️⃣ CATEGORIES (Categorías)

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

**Campos:**
- `id`: Identificador único
- `name`: Nombre de la categoría
- `slug`: URL-friendly identifier (ej: "ropa-bebe")
- `description`: Descripción de la categoría
- `icon`: Emoji o icono

**Índices:**
```sql
CREATE INDEX idx_categories_slug ON categories(slug);
```

---

### 3️⃣ PRODUCTS (Productos)

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
    
    CONSTRAINT fk_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE RESTRICT
);
```

**Campos:**
- `id`: Identificador único
- `name`: Nombre del producto
- `description`: Descripción detallada
- `price`: Precio en pesos colombianos (DECIMAL para precisión)
- `stock`: Cantidad disponible
- `category_id`: FK a categories
- `image_url`: URL de la imagen
- `discount`: Porcentaje de descuento (0-100)
- `active`: Producto activo o desactivado

**Constraints:**
- `CHECK (price >= 0)`: Precio no puede ser negativo
- `CHECK (stock >= 0)`: Stock no puede ser negativo
- `CHECK (discount >= 0 AND discount <= 100)`: Descuento entre 0 y 100%
- `ON DELETE RESTRICT`: No permite eliminar categoría si tiene productos

**Índices:**
```sql
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_active ON products(active);
CREATE INDEX idx_products_name ON products(name);
```

---

### 4️⃣ ORDERS (Órdenes)

```sql
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    total DECIMAL(10, 2) NOT NULL CHECK (total >= 0),
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
```

**Campos:**
- `id`: Identificador único
- `user_id`: FK a users (quien hizo la orden)
- `total`: Total de la orden
- `status`: Estado (PENDING, COMPLETED, CANCELLED)
- `created_at`: Fecha de creación
- `updated_at`: Fecha de última actualización

**Estados posibles:**
- `PENDING`: Orden creada, pendiente de procesar
- `COMPLETED`: Orden completada y entregada
- `CANCELLED`: Orden cancelada

**Índices:**
```sql
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
```

---

### 5️⃣ ORDER_ITEMS (Items de Orden)

```sql
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    
    CONSTRAINT fk_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,
    
    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE RESTRICT
);
```

**Campos:**
- `id`: Identificador único
- `order_id`: FK a orders
- `product_id`: FK a products
- `quantity`: Cantidad de productos en este item
- `price`: Precio al momento de la compra (histórico)

**¿Por qué guardar price?**
- Precios pueden cambiar con el tiempo
- Necesitamos el precio histórico al momento de la compra
- Garantiza consistencia en reportes

**Constraints:**
- `ON DELETE CASCADE` en order_id: Si se elimina orden, se eliminan sus items
- `ON DELETE RESTRICT` en product_id: No permite eliminar producto si está en órdenes

**Índices:**
```sql
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);
```

---

## 🔗 Relaciones

### 1️⃣ Users → Orders (1:N)
- Un usuario puede tener muchas órdenes
- Una orden pertenece a un solo usuario

```sql
users.id ←─── orders.user_id
```

---

### 2️⃣ Orders → Order_Items (1:N)
- Una orden puede tener muchos items
- Un item pertenece a una sola orden

```sql
orders.id ←─── order_items.order_id
```

---

### 3️⃣ Products → Order_Items (1:N)
- Un producto puede estar en muchos items
- Un item referencia a un solo producto

```sql
products.id ←─── order_items.product_id
```

---

### 4️⃣ Categories → Products (1:N)
- Una categoría puede tener muchos productos
- Un producto pertenece a una sola categoría

```sql
categories.id ←─── products.category_id
```

---

## 📏 Normalización

### Primera Forma Normal (1NF)
✅ Todos los campos son atómicos (no hay arrays o listas)

### Segunda Forma Normal (2NF)
✅ Todos los campos dependen completamente de la clave primaria

### Tercera Forma Normal (3NF)
✅ No hay dependencias transitivas

**Ejemplo:**
En lugar de guardar `user_name` en orders, guardamos `user_id` y hacemos JOIN con users.

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es una clave primaria (PK)?"**

> "Es un campo que identifica únicamente cada registro en una tabla:
> - En `users`, `id` es PK (cada usuario tiene id único)
> - No puede ser NULL
> - No puede repetirse
> - Usamos `SERIAL` (auto-incremental en PostgreSQL)
> 
> Ejemplo: user con id=1 es único, no puede haber otro id=1 en users."

---

**2. "¿Qué es una clave foránea (FK)?"**

> "Es un campo que referencia la PK de otra tabla:
> - En `orders`, `user_id` es FK que referencia `users.id`
> - Garantiza integridad: no puedes crear orden con user_id que no existe
> - Permite hacer JOINs
> 
> Ejemplo: Si order tiene user_id=5, debe existir user con id=5."

---

**3. "¿Por qué usar DECIMAL para precios?"**

> "Porque `DECIMAL(10, 2)` garantiza precisión exacta:
> - 10 dígitos en total
> - 2 después del punto decimal
> - Ejemplo: 12345678.90
> 
> Si usáramos `FLOAT`, habría errores de redondeo:
> - 99.99 podría guardarse como 99.98999...
> - En dinero, esto es inaceptable."

---

**4. "¿Qué es ON DELETE CASCADE?"**

> "`ON DELETE CASCADE` elimina registros relacionados automáticamente:
> ```sql
> FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
> ```
> - Si elimino order con id=10
> - Automáticamente se eliminan todos order_items con order_id=10
> - Previene registros huérfanos
> 
> `ON DELETE RESTRICT` previene eliminación si hay registros relacionados."

---

## 📝 Script de Creación Completo

```sql
-- Crear base de datos
CREATE DATABASE babycash;

-- Conectar a la base de datos
\c babycash;

-- Tabla users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla categories
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla products
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
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);

-- Tabla orders
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    total DECIMAL(10, 2) NOT NULL CHECK (total >= 0),
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabla order_items
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);

-- Índices para optimización
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_active ON products(active);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);
```

---

## 🚀 Conclusión

**Esquema de Baby Cash:**
- ✅ 5 tablas principales normalizadas
- ✅ Relaciones claras con FK
- ✅ Constraints para integridad
- ✅ Índices para performance
- ✅ PostgreSQL como motor

**Es la base sólida para toda la aplicación.**

---

**Ahora lee:** `TABLAS-PRINCIPALES.md` para detalles de cada tabla. 🚀
