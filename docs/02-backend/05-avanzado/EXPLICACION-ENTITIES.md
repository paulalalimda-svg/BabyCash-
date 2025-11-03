# 📊 EXPLICACIÓN DE ENTIDADES (ENTITIES) - BACKEND

## 📌 ¿Qué son las Entities?

### 🎯 Explicación Simple
Las **Entities** son como **plantillas** que representan las **tablas** de la base de datos. Cada entidad es una tabla, y cada objeto de esa entidad es una fila en la tabla.

**Ejemplo del mundo real:**
Imagina una biblioteca:
- **Tabla "Libros"** → Entidad `Book`
- **Fila del libro "Cien años de soledad"** → Objeto `Book` con datos específicos

### 🔧 Explicación Técnica
Las Entities son clases Java anotadas con **JPA (Java Persistence API)** que:
- Definen la estructura de las tablas en la base de datos
- Mapean objetos Java a filas de tablas (ORM - Object-Relational Mapping)
- Establecen relaciones entre tablas (@OneToMany, @ManyToOne, etc.)
- Son gestionadas por Hibernate (implementación de JPA)

---

## 📂 Ubicación de Entities

```
backend/src/main/java/com/babycash/backend/model/entity/
├── User.java                # Usuarios del sistema
├── Product.java             # Productos del catálogo
├── Cart.java                # Carritos de compra
├── CartItem.java            # Items dentro del carrito
├── Order.java               # Órdenes de compra
├── OrderItem.java           # Items dentro de la orden
├── Payment.java             # Pagos procesados
├── BlogPost.java            # Publicaciones del blog
├── BlogComment.java         # Comentarios en el blog
├── Testimonial.java         # Testimonios de clientes
├── ContactMessage.java      # Mensajes de contacto
├── ContactInfo.java         # Información de contacto de la empresa
├── LoyaltyPoint.java        # Puntos de lealtad por usuario
└── RefreshToken.java        # Tokens de refresco JWT
```

---

## 👤 1. User.java (Usuarios)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/User.java`

### 🎯 ¿Qué representa?
La tabla `users` que almacena información de todos los usuarios (clientes y administradores).

### 📝 Estructura de la Tabla

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,       -- Hash BCrypt
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    role VARCHAR(20) NOT NULL,            -- USER o ADMIN
    active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false,
    password_reset_token VARCHAR(255),
    password_reset_token_expiry TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    last_login_at TIMESTAMP
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "users")
@Data  // Lombok: genera getters, setters, toString, etc.
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id  // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incremento
    private Long id;
    
    @Column(unique = true, nullable = false)  // Email único y obligatorio
    private String email;
    
    @Column(nullable = false)
    private String password;  // Hash BCrypt: $2a$10$xKJ...
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Enumerated(EnumType.STRING)  // Guarda "USER" o "ADMIN" como texto
    @Column(nullable = false)
    private UserRole role;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(name = "email_verified")
    private boolean emailVerified = false;
    
    @Column(name = "password_reset_token")
    private String passwordResetToken;
    
    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    // RELACIONES
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;  // Un usuario tiene muchas órdenes
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;  // Un usuario tiene un carrito
}
```

### 📊 Ejemplo de Datos

| id | email | password | first_name | last_name | role | active | created_at |
|----|-------|----------|------------|-----------|------|--------|------------|
| 1 | admin@babycash.com | $2a$10$... | Admin | Sistema | ADMIN | true | 2025-10-01 |
| 2 | maria@gmail.com | $2a$10$... | María | García | USER | true | 2025-10-15 |
| 3 | juan@gmail.com | $2a$10$... | Juan | Pérez | USER | true | 2025-10-20 |

---

## 📦 2. Product.java (Productos)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/Product.java`

### 🎯 ¿Qué representa?
La tabla `products` que almacena todos los productos del catálogo.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,     -- Ej: 45000.00
    image_url VARCHAR(500),
    category VARCHAR(50) NOT NULL,      -- PAÑALES, BIBERONES, etc.
    stock INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN DEFAULT true,
    views BIGINT DEFAULT 0,             -- Contador de vistas
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;  // "Pañales Huggies Etapa 3"
    
    @Column(columnDefinition = "TEXT")
    private String description;  // "Pañales super absorbentes..."
    
    @Column(nullable = false)
    private Double price;  // 45000.0
    
    @Column(name = "image_url")
    private String imageUrl;  // "/productos/panales-huggies.jpg"
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;  // PAÑALES
    
    @Column(nullable = false)
    private Integer stock;  // 100
    
    @Column(nullable = false)
    private boolean active = true;  // Producto visible en tienda
    
    private Long views = 0L;  // Contador de vistas
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // RELACIONES
    
    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;  // Producto puede estar en varios carritos
    
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;  // Producto puede estar en varias órdenes
}
```

### 📊 Ejemplo de Datos

| id | name | price | category | stock | active | views |
|----|------|-------|----------|-------|--------|-------|
| 1 | Pañales Huggies Etapa 3 | 45000.00 | PAÑALES | 100 | true | 523 |
| 2 | Biberón Avent 260ml | 35000.00 | BIBERONES | 50 | true | 342 |
| 3 | Cuna Portátil | 250000.00 | CUNAS | 10 | true | 189 |

---

## 🛒 3. Cart.java (Carrito de Compras)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/Cart.java`

### 🎯 ¿Qué representa?
La tabla `carts` que almacena los carritos de compra de los usuarios.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,      -- Cada usuario tiene un carrito
    total DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // RELACIÓN: Cart pertenece a un User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(nullable = false)
    private Double total = 0.0;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // RELACIÓN: Cart tiene muchos CartItems
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
}
```

### 🔗 Relación con User

```
User (1) ←→ (1) Cart
- Un usuario tiene un carrito
- Un carrito pertenece a un usuario
```

### 📊 Ejemplo de Datos

| id | user_id | total | updated_at |
|----|---------|-------|------------|
| 1 | 2 | 150000.00 | 2025-10-30 19:30 |
| 2 | 3 | 0.00 | 2025-10-25 14:20 |

---

## 📦 4. CartItem.java (Items del Carrito)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/CartItem.java`

### 🎯 ¿Qué representa?
La tabla `cart_items` que almacena los productos dentro de cada carrito.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    subtotal DECIMAL(10, 2) NOT NULL,    -- price * quantity
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "cart_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // RELACIÓN: CartItem pertenece a un Cart
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    // RELACIÓN: CartItem hace referencia a un Product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;  // 2
    
    @Column(nullable = false)
    private Double subtotal;  // price * quantity = 90000.0
}
```

### 🔗 Relaciones

```
Cart (1) ←→ (N) CartItem
- Un carrito tiene muchos items

Product (1) ←→ (N) CartItem
- Un producto puede estar en muchos carritos
```

### 📊 Ejemplo de Datos

| id | cart_id | product_id | quantity | subtotal |
|----|---------|------------|----------|----------|
| 1 | 1 | 1 | 2 | 90000.00 |
| 2 | 1 | 2 | 1 | 35000.00 |
| 3 | 1 | 5 | 3 | 75000.00 |

**Interpretación:**
```
Carrito ID=1 (María) contiene:
- 2x Pañales Huggies = $90,000
- 1x Biberón Avent = $35,000
- 3x Producto ID=5 = $75,000
Total: $200,000
```

---

## 📋 5. Order.java (Órdenes de Compra)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/Order.java`

### 🎯 ¿Qué representa?
La tabla `orders` que almacena las órdenes de compra confirmadas.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(50) UNIQUE NOT NULL,  -- ORD-2025-10-30-001
    status VARCHAR(20) NOT NULL,                -- PENDING, CONFIRMED, SHIPPED, etc.
    total DECIMAL(10, 2) NOT NULL,
    shipping_address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // RELACIÓN: Order pertenece a un User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;  // "ORD-2025-10-30-001"
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;  // PENDING, CONFIRMED, SHIPPED, DELIVERED
    
    @Column(nullable = false)
    private Double total;
    
    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;  // "Calle 123 #45-67, Apto 301"
    
    @Column(nullable = false)
    private String city;  // "Bogotá"
    
    @Column(nullable = false)
    private String phone;  // "3001234567"
    
    @Column(columnDefinition = "TEXT")
    private String notes;  // "Entregar en la mañana"
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // RELACIONES
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;  // Items de la orden
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;  // Pago asociado
}
```

### 🔗 Relaciones

```
User (1) ←→ (N) Order
- Un usuario puede tener muchas órdenes

Order (1) ←→ (N) OrderItem
- Una orden contiene muchos items

Order (1) ←→ (1) Payment
- Una orden tiene un pago
```

### 📊 Ejemplo de Datos

| id | user_id | order_number | status | total | city | created_at |
|----|---------|--------------|--------|-------|------|------------|
| 1 | 2 | ORD-2025-10-30-001 | CONFIRMED | 150000.00 | Bogotá | 2025-10-30 19:45 |
| 2 | 3 | ORD-2025-10-29-015 | SHIPPED | 89000.00 | Medellín | 2025-10-29 14:20 |

---

## 📦 6. OrderItem.java (Items de la Orden)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/OrderItem.java`

### 🎯 ¿Qué representa?
La tabla `order_items` que almacena los productos comprados en cada orden.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,       -- Precio al momento de la compra
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // RELACIÓN: OrderItem pertenece a una Order
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    // RELACIÓN: OrderItem hace referencia a un Product
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Double price;  // Precio al momento de la compra (histórico)
    
    @Column(nullable = false)
    private Double subtotal;
}
```

### 💡 ¿Por qué guardar el precio?

**Importante:** Se guarda el precio del producto **al momento de la compra** porque:
- El precio del producto puede cambiar en el futuro
- Necesitamos mantener un registro histórico exacto
- Evita discrepancias en reportes financieros

**Ejemplo:**
```
Hoy: Usuario compra "Pañales" a $45,000
→ OrderItem guarda price = 45000

Mañana: Admin cambia precio a $50,000
→ Product.price = 50000

Reporte de orden original: Sigue mostrando $45,000 ✓
```

---

## 💳 7. Payment.java (Pagos)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/Payment.java`

### 🎯 ¿Qué representa?
La tabla `payments` que almacena los pagos procesados.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,     -- CREDIT_CARD, DEBIT_CARD, etc.
    status VARCHAR(20) NOT NULL,              -- PENDING, COMPLETED, FAILED
    transaction_id VARCHAR(255) UNIQUE,       -- ID de la pasarela de pago
    error_message TEXT,
    processed_at TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // RELACIÓN: Payment pertenece a una Order (1:1)
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;
    
    @Column(nullable = false)
    private Double amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;  // CREDIT_CARD, DEBIT_CARD, PSE
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;  // PENDING, COMPLETED, FAILED
    
    @Column(name = "transaction_id", unique = true)
    private String transactionId;  // "550e8400-e29b-41d4..."
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;  // Si falló: "Fondos insuficientes"
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
```

### 📊 Ejemplo de Datos

| id | order_id | amount | payment_method | status | transaction_id | processed_at |
|----|----------|--------|----------------|--------|----------------|--------------|
| 1 | 1 | 150000.00 | CREDIT_CARD | COMPLETED | 550e8400-e29b... | 2025-10-30 19:47 |
| 2 | 2 | 89000.00 | PSE | COMPLETED | 661f9511-f30c... | 2025-10-29 14:25 |

---

## 📝 8. BlogPost.java (Publicaciones del Blog)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/BlogPost.java`

### 🎯 ¿Qué representa?
La tabla `blog_posts` que almacena las publicaciones del blog.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE blog_posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,    -- URL amigable: "consejos-primer-ano"
    content TEXT NOT NULL,
    excerpt TEXT,                          -- Resumen corto
    image_url VARCHAR(500),
    author_id BIGINT NOT NULL,
    published BOOLEAN DEFAULT false,
    views BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    published_at TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "blog_posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;  // "10 Consejos para el Primer Año"
    
    @Column(unique = true, nullable = false)
    private String slug;  // "10-consejos-primer-ano"
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // Contenido completo (HTML)
    
    @Column(columnDefinition = "TEXT")
    private String excerpt;  // "En este artículo compartimos..."
    
    @Column(name = "image_url")
    private String imageUrl;
    
    // RELACIÓN: BlogPost creado por un User (autor)
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @Column(nullable = false)
    private boolean published = false;  // Publicado o borrador
    
    private Long views = 0L;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    // RELACIÓN: BlogPost tiene muchos comentarios
    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL)
    private List<BlogComment> comments;
}
```

---

## 💬 9. BlogComment.java (Comentarios del Blog)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/BlogComment.java`

### 🎯 ¿Qué representa?
La tabla `blog_comments` que almacena comentarios en las publicaciones.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE blog_comments (
    id BIGSERIAL PRIMARY KEY,
    blog_post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    approved BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (blog_post_id) REFERENCES blog_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "blog_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "blog_post_id", nullable = false)
    private BlogPost blogPost;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private boolean approved = false;  // Moderación
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

---

## ⭐ 10. Testimonial.java (Testimonios)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/Testimonial.java`

### 🎯 ¿Qué representa?
La tabla `testimonials` que almacena testimonios de clientes.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE testimonials (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    approved BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "testimonials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Testimonial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // "Excelente servicio, muy satisfecha"
    
    @Column(nullable = false)
    private Integer rating;  // 1-5 estrellas
    
    @Column(nullable = false)
    private boolean approved = false;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

---

## 📧 11. ContactMessage.java (Mensajes de Contacto)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/ContactMessage.java`

### 🎯 ¿Qué representa?
La tabla `contact_messages` que almacena mensajes del formulario de contacto.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE contact_messages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    subject VARCHAR(255),
    message TEXT NOT NULL,
    read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW()
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "contact_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;
    
    private String phone;
    
    private String subject;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(nullable = false)
    private boolean read = false;  // Marcado como leído por admin
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

---

## 🎁 12. LoyaltyPoint.java (Puntos de Lealtad)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/LoyaltyPoint.java`

### 🎯 ¿Qué representa?
La tabla `loyalty_points` que almacena el balance de puntos de cada usuario.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE loyalty_points (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    points INTEGER NOT NULL DEFAULT 0,
    total_earned INTEGER NOT NULL DEFAULT 0,
    total_redeemed INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "loyalty_points")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(nullable = false)
    private Integer points = 0;  // Puntos actuales
    
    @Column(name = "total_earned", nullable = false)
    private Integer totalEarned = 0;  // Total ganado (histórico)
    
    @Column(name = "total_redeemed", nullable = false)
    private Integer totalRedeemed = 0;  // Total canjeado (histórico)
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### 📊 Ejemplo de Datos

| id | user_id | points | total_earned | total_redeemed | updated_at |
|----|---------|--------|--------------|----------------|------------|
| 1 | 2 | 350 | 500 | 150 | 2025-10-30 19:50 |
| 2 | 3 | 89 | 89 | 0 | 2025-10-29 14:30 |

**Interpretación:**
- Usuario 2: Tiene 350 puntos, ha ganado 500, ha canjeado 150
- Usuario 3: Tiene 89 puntos, nunca ha canjeado

---

## 🔄 13. RefreshToken.java (Tokens de Refresco)

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/model/entity/RefreshToken.java`

### 🎯 ¿Qué representa?
La tabla `refresh_tokens` que almacena tokens para renovar el JWT principal.

### 📝 Estructura de la Tabla

```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 🔧 Código Java

```java
@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(unique = true, nullable = false)
    private String token;  // UUID
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;  // Expira en 7 días
    
    @Column(nullable = false)
    private boolean revoked = false;  // Token revocado (logout)
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

### 💡 ¿Para qué sirve?

**Problema:**
- JWT tokens expiran rápido (24 horas)
- Usuario tendría que hacer login todos los días

**Solución con Refresh Token:**
```
1. Usuario hace login
   → Recibe: JWT (expira en 24h) + Refresh Token (expira en 7 días)

2. Después de 23 horas:
   → JWT está por expirar

3. Frontend envía Refresh Token
   → Backend valida y genera nuevo JWT

4. Usuario sigue autenticado sin hacer login
```

---

## 🔗 DIAGRAMA DE RELACIONES

```
┌─────────┐
│  USER   │
└────┬────┘
     │
     ├─────(1:1)────→ Cart
     ├─────(1:N)────→ Order
     ├─────(1:N)────→ BlogPost (como autor)
     ├─────(1:N)────→ BlogComment
     ├─────(1:N)────→ Testimonial
     └─────(1:N)────→ RefreshToken

┌─────────┐
│ PRODUCT │
└────┬────┘
     │
     ├─────(1:N)────→ CartItem
     └─────(1:N)────→ OrderItem

┌──────┐
│ CART │
└───┬──┘
    │
    └─────(1:N)────→ CartItem

┌───────┐
│ ORDER │
└───┬───┘
    │
    ├─────(1:N)────→ OrderItem
    └─────(1:1)────→ Payment

┌───────────┐
│ BLOG_POST │
└─────┬─────┘
      │
      └─────(1:N)────→ BlogComment
```

---

## 📊 RESUMEN DE ENTIDADES

| Entidad | Tabla BD | Propósito | Relaciones |
|---------|----------|-----------|------------|
| **User** | users | Usuarios del sistema | Cart, Order, BlogPost, BlogComment, Testimonial |
| **Product** | products | Catálogo de productos | CartItem, OrderItem |
| **Cart** | carts | Carrito de compras | User (1:1), CartItem (1:N) |
| **CartItem** | cart_items | Items en el carrito | Cart (N:1), Product (N:1) |
| **Order** | orders | Órdenes de compra | User (N:1), OrderItem (1:N), Payment (1:1) |
| **OrderItem** | order_items | Items de la orden | Order (N:1), Product (N:1) |
| **Payment** | payments | Pagos procesados | Order (1:1) |
| **BlogPost** | blog_posts | Publicaciones del blog | User (N:1), BlogComment (1:N) |
| **BlogComment** | blog_comments | Comentarios del blog | BlogPost (N:1), User (N:1) |
| **Testimonial** | testimonials | Testimonios de clientes | User (N:1) |
| **ContactMessage** | contact_messages | Mensajes de contacto | Ninguna |
| **LoyaltyPoint** | loyalty_points | Puntos de lealtad | User (1:1) |
| **RefreshToken** | refresh_tokens | Tokens de refresco | User (N:1) |

---

## 🔑 CONCEPTOS CLAVE

### 1. **@Entity**
Marca la clase como una entidad JPA (tabla de BD).

### 2. **@Id y @GeneratedValue**
```java
@Id  // Clave primaria
@GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incremento
private Long id;
```

### 3. **@Column**
```java
@Column(unique = true, nullable = false, length = 255)
private String email;
```

### 4. **Relaciones**

#### @OneToOne (1:1)
```java
// User tiene un Cart
@OneToOne
@JoinColumn(name = "cart_id")
private Cart cart;
```

#### @OneToMany (1:N)
```java
// User tiene muchas órdenes
@OneToMany(mappedBy = "user")
private List<Order> orders;
```

#### @ManyToOne (N:1)
```java
// Order pertenece a un User
@ManyToOne
@JoinColumn(name = "user_id")
private User user;
```

---

**Última actualización**: Octubre 2025
**Versión**: 1.0
