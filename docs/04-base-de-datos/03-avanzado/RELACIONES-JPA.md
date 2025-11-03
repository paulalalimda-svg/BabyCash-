# 🔗 RELACIONES JPA

## 🎯 Tipos de Relaciones

| Relación | Descripción | Ejemplo |
|----------|-------------|---------|
| **@OneToOne** | 1 a 1 | User ↔ Cart |
| **@OneToMany** | 1 a muchos | Cart → CartItems |
| **@ManyToOne** | Muchos a 1 | CartItem → Cart |
| **@ManyToMany** | Muchos a muchos | Products ↔ Categories |

---

## 1️⃣ @OneToOne (Uno a Uno)

### Ejemplo: User ↔ Cart

**Cada usuario tiene un solo carrito.**

```java
// User.java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String name;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;
    
    // getters y setters
}

// Cart.java
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    // getters y setters
}
```

### Tabla SQL

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100),
    name VARCHAR(50)
);

CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Propiedades

```java
@OneToOne(
    mappedBy = "user",      // Campo en la otra clase
    cascade = CascadeType.ALL,  // Operaciones en cascada
    orphanRemoval = true,   // Eliminar si se desvincula
    fetch = FetchType.LAZY  // Carga perezosa
)
```

### Uso

```java
// Crear usuario con carrito
User user = new User();
user.setEmail("maria@gmail.com");

Cart cart = new Cart();
cart.setUser(user);
user.setCart(cart);

userRepository.save(user);  // Guarda user y cart automáticamente
```

---

## 🔢 @OneToMany (Uno a Muchos)

### Ejemplo: Cart → CartItems

**Un carrito tiene muchos items.**

```java
// Cart.java
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    // Método auxiliar
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }
    
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }
    
    // getters y setters
}

// CartItem.java
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    // getters y setters
}
```

### Tabla SQL

```sql
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT
);

CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### Uso

```java
// Agregar item al carrito
Cart cart = cartRepository.findById(1L).orElse(null);
Product product = productRepository.findById(5L).orElse(null);

CartItem item = new CartItem();
item.setProduct(product);
item.setQuantity(2);

cart.addItem(item);  // Usa el método auxiliar
cartRepository.save(cart);  // Guarda cart y items automáticamente
```

---

## 🔁 @ManyToOne (Muchos a Uno)

### Ejemplo: CartItem → Cart

**Muchos items pertenecen a un carrito.**

```java
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    // getters y setters
}
```

---

## 🔄 @ManyToMany (Muchos a Muchos)

### Ejemplo: Products ↔ Categories

**Un producto puede tener varias categorías, y una categoría puede tener varios productos.**

```java
// Product.java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToMany
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
    
    // getters y setters
}

// Category.java
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();
    
    // getters y setters
}
```

### Tabla SQL

```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE
);

-- Tabla intermedia
CREATE TABLE product_categories (
    product_id BIGINT,
    category_id BIGINT,
    PRIMARY KEY (product_id, category_id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

### Uso

```java
// Crear producto con categorías
Product product = new Product();
product.setName("Pañales Huggies");

Category category1 = categoryRepository.findByName("Bebé");
Category category2 = categoryRepository.findByName("Higiene");

product.getCategories().add(category1);
product.getCategories().add(category2);

productRepository.save(product);
```

---

## 🔄 CascadeType

### Tipos de Cascade

```java
@OneToMany(
    mappedBy = "cart",
    cascade = {
        CascadeType.PERSIST,   // save()
        CascadeType.MERGE,     // update()
        CascadeType.REMOVE,    // delete()
        CascadeType.REFRESH,   // refresh()
        CascadeType.DETACH,    // detach()
        CascadeType.ALL        // Todos los anteriores
    }
)
private List<CartItem> items;
```

### Ejemplo

```java
// Con cascade = CascadeType.ALL
Cart cart = new Cart();
CartItem item1 = new CartItem();
CartItem item2 = new CartItem();
cart.addItem(item1);
cart.addItem(item2);

cartRepository.save(cart);  // Guarda cart, item1 e item2 automáticamente ✅

// Sin cascade
cartRepository.save(cart);  // Solo guarda cart
cartItemRepository.save(item1);  // Debes guardar cada item manualmente ❌
cartItemRepository.save(item2);
```

---

## 🔍 FetchType

### Tipos de Fetch

```java
// LAZY: Carga solo cuando se accede
@ManyToOne(fetch = FetchType.LAZY)
private Cart cart;

// EAGER: Carga inmediatamente
@ManyToOne(fetch = FetchType.EAGER)
private Cart cart;
```

### Ejemplo

```java
// LAZY
CartItem item = cartItemRepository.findById(1L).orElse(null);
// SELECT * FROM cart_items WHERE id = 1

item.getCart().getId();  // Ahora sí: SELECT * FROM carts WHERE id = X

// EAGER
CartItem item = cartItemRepository.findById(1L).orElse(null);
// SELECT * FROM cart_items ci 
// LEFT JOIN carts c ON ci.cart_id = c.id 
// WHERE ci.id = 1
```

**⚠️ Usa LAZY siempre que puedas. EAGER puede causar problemas de performance.**

---

## 🗑️ orphanRemoval

### ¿Qué hace?

Elimina entidades "huérfanas" (sin padre).

```java
@OneToMany(mappedBy = "cart", orphanRemoval = true)
private List<CartItem> items;
```

### Ejemplo

```java
Cart cart = cartRepository.findById(1L).orElse(null);
CartItem item = cart.getItems().get(0);

cart.getItems().remove(item);  // Remueve del carrito
cartRepository.save(cart);

// Con orphanRemoval = true
// → item se ELIMINA de la BD ✅

// Con orphanRemoval = false
// → item.cart_id se pone en NULL ❌
```

---

## 🎯 Ejemplo Completo: BabyCash

### User ↔ Cart (1:1)

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;
}

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
}
```

### Cart → CartItems (1:N)

```java
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
}
```

### User → Orders (1:N)

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;
    
    @Column(nullable = false)
    private BigDecimal total;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}
```

### Order → OrderItems (1:N)

```java
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private BigDecimal price;  // Precio histórico
}
```

---

## 📊 Diagrama de Relaciones BabyCash

```
┌─────────────┐
│    User     │
└──────┬──────┘
       │ 1:1
       ▼
┌─────────────┐
│    Cart     │
└──────┬──────┘
       │ 1:N
       ▼
┌─────────────┐       N:1        ┌─────────────┐
│  CartItem   │──────────────────▶│   Product   │
└─────────────┘                   └─────────────┘

┌─────────────┐
│    User     │
└──────┬──────┘
       │ 1:N
       ▼
┌─────────────┐
│   Order     │
└──────┬──────┘
       │ 1:N
       ▼
┌─────────────┐       N:1        ┌─────────────┐
│  OrderItem  │──────────────────▶│   Product   │
└─────────────┘                   └─────────────┘
```

---

## ⚠️ Problema N+1

### ❌ Problema

```java
List<Cart> carts = cartRepository.findAll();  // 1 query

for (Cart cart : carts) {
    cart.getItems().size();  // N queries (1 por cada cart)
}

// Total: 1 + N queries
```

### ✅ Solución: JOIN FETCH

```java
@Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items")
List<Cart> findAllWithItems();

// 1 solo query:
// SELECT c.*, i.* FROM carts c LEFT JOIN cart_items i ON c.id = i.cart_id
```

---

## 🛠️ Métodos Auxiliares

### Agregar/Remover Items

```java
@Entity
public class Cart {
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    // Método auxiliar para agregar
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);  // Sincroniza bidireccional
    }
    
    // Método auxiliar para remover
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);  // Sincroniza bidireccional
    }
    
    // Calcular total
    public BigDecimal calculateTotal() {
        return items.stream()
            .map(item -> item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

---

## 📋 Resumen

| Relación | Anotación | Ejemplo BabyCash |
|----------|-----------|------------------|
| **1:1** | `@OneToOne` | User ↔ Cart |
| **1:N** | `@OneToMany` | Cart → CartItems |
| **N:1** | `@ManyToOne` | CartItem → Cart |
| **N:M** | `@ManyToMany` | Products ↔ Categories |

### Propiedades Clave

| Propiedad | Descripción |
|-----------|-------------|
| `mappedBy` | Campo en la otra clase (lado no propietario) |
| `cascade` | Operaciones en cascada (PERSIST, REMOVE, ALL) |
| `orphanRemoval` | Eliminar huérfanos automáticamente |
| `fetch` | LAZY (perezoso) o EAGER (inmediato) |
| `@JoinColumn` | Nombre de la columna FK |

---

**Última actualización**: Octubre 2025
