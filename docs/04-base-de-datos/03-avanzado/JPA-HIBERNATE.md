# 🗄️ JPA Y HIBERNATE

## 🎯 Definición Simple

**JPA** = Forma estándar de convertir objetos Java en tablas SQL (y viceversa)  
**Hibernate** = Herramienta que hace el trabajo de JPA

---

## 📚 ¿Qué es JPA?

**JPA** = **J**ava **P**ersistence **A**PI

Es una **especificación** (un conjunto de reglas) que define cómo mapear objetos Java a bases de datos relacionales.

### Analogía

JPA es como un **plano de construcción**:
- Define **qué** debe hacer
- NO define **cómo** hacerlo

---

## 🔨 ¿Qué es Hibernate?

Hibernate es una **implementación** de JPA. Es la herramienta que **realmente hace el trabajo**.

### Analogía

- **JPA** = Plano arquitectónico ("debe tener 3 habitaciones")
- **Hibernate** = Constructor que construye la casa

### Otras Implementaciones de JPA

| Implementación | Compañía | Uso |
|----------------|----------|-----|
| **Hibernate** | Red Hat | **Más popular (90%)** |
| EclipseLink | Eclipse Foundation | Usado por Oracle |
| OpenJPA | Apache | Menos común |

---

## 🔄 ORM: Mapeo Objeto-Relacional

**ORM** = **O**bject **R**elational **M**apping

Es la técnica de convertir:
- **Objetos Java** ↔ **Tablas SQL**

### Sin ORM (JDBC)

```java
// ❌ Código manual y repetitivo
public User findById(Long id) {
    String sql = "SELECT * FROM users WHERE id = ?";
    PreparedStatement stmt = connection.prepareStatement(sql);
    stmt.setLong(1, id);
    ResultSet rs = stmt.executeQuery();
    
    User user = new User();
    if (rs.next()) {
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
    }
    return user;
}
```

### Con ORM (JPA/Hibernate)

```java
// ✅ Simple y elegante
public User findById(Long id) {
    return userRepository.findById(id).orElse(null);
}
```

---

## 🎓 Ejemplo: Clase Java ↔ Tabla SQL

### Clase Java

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String password;
    
    private String role;
    private Boolean active;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // getters y setters
}
```

### Tabla SQL (generada por Hibernate)

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    active BOOLEAN,
    created_at TIMESTAMP
);
```

---

## 🔍 Cómo Funciona Hibernate

### 1. Entity Manager

Hibernate usa un **EntityManager** para gestionar objetos:

```java
// Guardar
User user = new User();
user.setEmail("maria@gmail.com");
user.setName("María");
entityManager.persist(user);  // INSERT INTO users...

// Buscar
User user = entityManager.find(User.class, 1L);  // SELECT * FROM users WHERE id = 1

// Actualizar
user.setName("María García");
entityManager.merge(user);  // UPDATE users SET name = 'María García' WHERE id = 1

// Eliminar
entityManager.remove(user);  // DELETE FROM users WHERE id = 1
```

### 2. Sesión (Session)

Hibernate mantiene una **sesión** que rastrea objetos:

```java
User user = entityManager.find(User.class, 1L);
user.setName("Nuevo Nombre");
// No necesitas llamar a save() o update()
// Hibernate detecta el cambio automáticamente ✅
```

### 3. Caché de Primer Nivel

```java
User user1 = entityManager.find(User.class, 1L);  // SELECT * FROM users WHERE id = 1
User user2 = entityManager.find(User.class, 1L);  // No ejecuta SQL, usa caché ✅

System.out.println(user1 == user2);  // true (mismo objeto)
```

---

## 📊 Arquitectura JPA/Hibernate

```
┌─────────────────────────────────────────┐
│         Aplicación Spring Boot          │
│  (Controller → Service → Repository)    │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│        Spring Data JPA                  │
│  (UserRepository extends JpaRepository) │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│              JPA API                    │
│  (EntityManager, @Entity, etc.)         │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│            Hibernate                    │
│  (Genera SQL, gestiona sesiones)        │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│          JDBC Driver                    │
│       (PostgreSQL Driver)               │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│          PostgreSQL                     │
└─────────────────────────────────────────┘
```

---

## 🎯 Ventajas de JPA/Hibernate

### 1. Menos Código

```java
// ❌ Sin JPA (50 líneas de JDBC)
public List<User> findAll() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users";
    PreparedStatement stmt = connection.prepareStatement(sql);
    ResultSet rs = stmt.executeQuery();
    while (rs.next()) {
        User user = new User();
        user.setId(rs.getLong("id"));
        // ... mapear 10 campos más
        users.add(user);
    }
    return users;
}

// ✅ Con JPA (1 línea)
public List<User> findAll() {
    return userRepository.findAll();
}
```

### 2. Independencia de Base de Datos

```java
// El mismo código funciona con PostgreSQL, MySQL, Oracle, etc.
User user = userRepository.findById(1L).orElse(null);

// Hibernate genera SQL específico para cada BD:
// PostgreSQL: SELECT * FROM users WHERE id = 1;
// SQL Server: SELECT * FROM users WHERE id = 1;
// Oracle: SELECT * FROM users WHERE id = 1;
```

### 3. Relaciones Automáticas

```java
@Entity
public class Cart {
    @OneToMany(mappedBy = "cart")
    private List<CartItem> items;  // Hibernate carga esto automáticamente
}

Cart cart = cartRepository.findById(1L).orElse(null);
cart.getItems().size();  // ✅ Hibernate ejecuta JOIN automáticamente
```

### 4. Lazy Loading

```java
@Entity
public class User {
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}

User user = userRepository.findById(1L).orElse(null);  // SELECT users
// Los orders NO se cargan todavía ✅

user.getOrders().size();  // Ahora sí: SELECT orders WHERE user_id = 1
```

### 5. Transacciones

```java
@Transactional
public void createOrder(Order order) {
    // 1. Guardar orden
    orderRepository.save(order);
    
    // 2. Reducir stock
    for (OrderItem item : order.getItems()) {
        Product product = item.getProduct();
        product.setStock(product.getStock() - item.getQuantity());
        productRepository.save(product);
    }
    
    // Si algo falla, TODO se revierte automáticamente ✅
}
```

---

## ⚙️ Configuración de Hibernate

### application.properties

```properties
# Dialecto (sintaxis SQL específica)
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Mostrar SQL generado
spring.jpa.show-sql=true

# Formatear SQL
spring.jpa.properties.hibernate.format_sql=true

# Estrategia de generación de esquema
spring.jpa.hibernate.ddl-auto=update

# Mostrar parámetros de SQL
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Estadísticas de Hibernate (performance)
spring.jpa.properties.hibernate.generate_statistics=true

# Tamaño del batch (insertar múltiples filas a la vez)
spring.jpa.properties.hibernate.jdbc.batch_size=20

# Caché de segundo nivel (opcional)
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
```

---

## 🔄 Ciclo de Vida de una Entity

### Estados

```java
// 1. TRANSIENT (no gestionado por Hibernate)
User user = new User();
user.setEmail("maria@gmail.com");

// 2. PERSISTENT (gestionado por Hibernate)
userRepository.save(user);  // INSERT INTO users...
// Cualquier cambio se sincroniza automáticamente

// 3. DETACHED (ya no gestionado)
entityManager.detach(user);
user.setName("Nuevo Nombre");  // ❌ No se actualiza en BD

// 4. REMOVED (marcado para eliminar)
userRepository.delete(user);  // DELETE FROM users...
```

### Diagrama

```
┌──────────────┐
│  TRANSIENT   │  ← new User()
└──────┬───────┘
       │ save()
       ▼
┌──────────────┐
│  PERSISTENT  │  ← Cambios se sincronizan
└──────┬───────┘
       │ detach()
       ▼
┌──────────────┐
│  DETACHED    │  ← No se sincronizan cambios
└──────┬───────┘
       │ remove()
       ▼
┌──────────────┐
│   REMOVED    │  ← Marcado para eliminar
└──────────────┘
```

---

## 🛠️ Operaciones con JPA

### Guardar

```java
User user = new User();
user.setEmail("maria@gmail.com");
user.setName("María");
userRepository.save(user);  // INSERT

// Hibernate genera:
// INSERT INTO users (email, name) VALUES ('maria@gmail.com', 'María')
```

### Buscar

```java
// Por ID
User user = userRepository.findById(1L).orElse(null);

// Todos
List<User> users = userRepository.findAll();

// Por campo
User user = userRepository.findByEmail("maria@gmail.com");

// Con condiciones
List<User> users = userRepository.findByActiveTrue();
```

### Actualizar

```java
User user = userRepository.findById(1L).orElse(null);
user.setName("María García");
userRepository.save(user);  // UPDATE

// Hibernate genera:
// UPDATE users SET name = 'María García' WHERE id = 1
```

### Eliminar

```java
userRepository.deleteById(1L);

// Hibernate genera:
// DELETE FROM users WHERE id = 1
```

---

## 🎓 Ejemplo Completo: BabyCash

### Entity

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock;
    
    private Boolean available = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // getters y setters
}
```

### Repository

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByAvailableTrue();
    List<Product> findByPriceLessThan(BigDecimal price);
    List<Product> findByNameContainingIgnoreCase(String keyword);
}
```

### Service

```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
    
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Transactional
    public void reduceStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente");
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
```

---

## ⚠️ Problemas Comunes

### Problema N+1

```java
// ❌ MAL - Ejecuta N+1 queries
List<Cart> carts = cartRepository.findAll();  // 1 query
for (Cart cart : carts) {
    cart.getItems().size();  // N queries (1 por cada cart)
}

// ✅ BIEN - 1 solo query
@Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items")
List<Cart> findAllWithItems();
```

### LazyInitializationException

```java
// ❌ ERROR
@GetMapping("/carts/{id}")
public Cart getCart(@PathVariable Long id) {
    Cart cart = cartRepository.findById(id).orElse(null);
    // La sesión de Hibernate se cerró aquí
    return cart;  // Al serializar a JSON, cart.getItems() falla
}

// ✅ SOLUCIÓN 1: Eager loading
@OneToMany(fetch = FetchType.EAGER)
private List<CartItem> items;

// ✅ SOLUCIÓN 2: @Transactional
@GetMapping("/carts/{id}")
@Transactional
public Cart getCart(@PathVariable Long id) {
    Cart cart = cartRepository.findById(id).orElse(null);
    cart.getItems().size();  // Forzar carga dentro de transacción
    return cart;
}
```

---

## 📋 Resumen

| Concepto | Definición |
|----------|------------|
| **JPA** | Especificación para mapear objetos a BD |
| **Hibernate** | Implementación de JPA (la más popular) |
| **ORM** | Técnica de mapear objetos ↔ tablas |
| **EntityManager** | Gestiona el ciclo de vida de entities |
| **Sesión** | Contexto donde se rastrean objetos |
| **Lazy Loading** | Cargar datos solo cuando se necesitan |
| **@Transactional** | Todo o nada (rollback automático) |

---

**Última actualización**: Octubre 2025
