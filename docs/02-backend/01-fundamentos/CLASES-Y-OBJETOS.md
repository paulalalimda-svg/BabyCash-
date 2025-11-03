# 🏗️ CLASES Y OBJETOS EN JAVA

## 🎯 ¿Qué es una Clase?

**Explicación Simple:**
Una clase es como un **plano o plantilla** para crear cosas. Es la **receta** que define cómo debe ser algo.

```
Plano de una casa (CLASE)
↓
Casa real construida (OBJETO)
```

**Explicación Técnica:**
Una clase es una **estructura que define atributos (datos) y métodos (comportamiento)** de un tipo de objeto.

---

## 🎯 ¿Qué es un Objeto?

**Explicación Simple:**
Un objeto es una **instancia** (copia real) creada a partir de una clase.

```
Clase: Molde de galleta 🍪
Objetos: Las galletas individuales 🍪🍪🍪
```

**Ejemplo:**
```
Clase User (plantilla)
↓
Objeto: María (usuario específico)
Objeto: Juan (otro usuario específico)
Objeto: Ana (otro usuario específico)
```

---

## 📝 Sintaxis de una Clase

```java
public class NombreClase {
    // ATRIBUTOS (características)
    private tipo atributo1;
    private tipo atributo2;
    
    // CONSTRUCTOR (cómo crear objetos)
    public NombreClase(parametros) {
        // inicialización
    }
    
    // MÉTODOS (acciones)
    public tipoRetorno nombreMetodo(parametros) {
        // código
    }
}
```

---

## 🔧 Ejemplo Simple: Clase Product

### Definir la Clase

```java
public class Product {
    // ===== ATRIBUTOS =====
    private Long id;
    private String name;
    private BigDecimal price;
    private int stock;
    
    // ===== CONSTRUCTOR =====
    public Product(String name, BigDecimal price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    
    // ===== MÉTODOS =====
    
    // Reducir stock
    public void reduceStock(int quantity) {
        this.stock = this.stock - quantity;
    }
    
    // Verificar disponibilidad
    public boolean isAvailable() {
        return this.stock > 0;
    }
    
    // Calcular precio con descuento
    public BigDecimal getPriceWithDiscount(int discountPercent) {
        BigDecimal discount = this.price.multiply(
            BigDecimal.valueOf(discountPercent / 100.0)
        );
        return this.price.subtract(discount);
    }
}
```

### Crear y Usar Objetos

```java
// Crear 3 productos (3 objetos de la clase Product)
Product producto1 = new Product("Pañales", new BigDecimal("45000"), 50);
Product producto2 = new Product("Leche", new BigDecimal("15000"), 100);
Product producto3 = new Product("Toallitas", new BigDecimal("8000"), 30);

// Usar métodos
System.out.println(producto1.isAvailable()); // true (hay stock)

producto1.reduceStock(10); // Ahora tiene 40 unidades

BigDecimal precioConDescuento = producto1.getPriceWithDiscount(10);
System.out.println(precioConDescuento); // 40500 (10% descuento)
```

---

## 🏗️ Componentes de una Clase

### 1. Atributos (Variables de Instancia)

**¿Qué son?**
Características o propiedades del objeto.

```java
public class User {
    private Long id;           // Identificador único
    private String name;       // Nombre
    private String email;      // Correo
    private boolean active;    // ¿Está activo?
}
```

**private vs public:**
- `private`: Solo accesible dentro de la clase (recomendado)
- `public`: Accesible desde cualquier lugar (no recomendado para atributos)

### 2. Constructor

**¿Qué es?**
Método especial que se ejecuta al crear un objeto.

```java
public class User {
    private String name;
    private String email;
    
    // Constructor
    public User(String name, String email) {
        this.name = name;      // "this" se refiere al objeto actual
        this.email = email;
    }
}

// Crear objeto
User maria = new User("María", "maria@gmail.com");
```

**¿Qué es "this"?**
`this` se refiere al **objeto actual**. Se usa para diferenciar atributos de parámetros:

```java
public User(String name, String email) {
    this.name = name;  // this.name = atributo | name = parámetro
}
```

### 3. Getters y Setters

**¿Qué son?**
Métodos para **obtener** (get) y **modificar** (set) atributos privados.

```java
public class Product {
    private String name;
    private BigDecimal price;
    
    // GETTER - Obtener valor
    public String getName() {
        return this.name;
    }
    
    // SETTER - Modificar valor
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getPrice() {
        return this.price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

// Usar getters y setters
Product producto = new Product();
producto.setName("Pañales");        // Setter
producto.setPrice(new BigDecimal("45000"));

String nombre = producto.getName(); // Getter
System.out.println(nombre);         // "Pañales"
```

**¿Por qué no hacer atributos públicos?**

```java
// ❌ MAL (atributos públicos)
public class Product {
    public BigDecimal price;
}

Product p = new Product();
p.price = new BigDecimal("-1000"); // ❌ Precio negativo!

// ✅ BIEN (atributos privados + setter con validación)
public class Product {
    private BigDecimal price;
    
    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo");
        }
        this.price = price;
    }
}
```

### 4. Métodos (Comportamiento)

**¿Qué son?**
Acciones que puede realizar el objeto.

```java
public class Cart {
    private List<CartItem> items;
    
    // Método: Agregar item
    public void addItem(Product product, int quantity) {
        CartItem item = new CartItem(product, quantity);
        items.add(item);
    }
    
    // Método: Calcular total
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
    
    // Método: Limpiar carrito
    public void clear() {
        items.clear();
    }
}
```

---

## 🎓 Ejemplo Completo del Proyecto: User.java

```java
@Entity  // Indica que es una tabla en la BD
@Table(name = "users")
public class User {
    
    // ===== ATRIBUTOS =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    private String phone;
    
    @Enumerated(EnumType.STRING)
    private UserRole role; // USER o ADMIN
    
    private boolean active = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Relaciones
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;
    
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
    
    // ===== CONSTRUCTORES =====
    
    // Constructor vacío (requerido por JPA)
    public User() {}
    
    // Constructor con parámetros
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = UserRole.USER;
        this.active = true;
    }
    
    // ===== GETTERS Y SETTERS =====
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    // ===== MÉTODOS ÚTILES =====
    
    // Verificar si es admin
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
    
    // Desactivar usuario
    public void deactivate() {
        this.active = false;
    }
    
    // Activar usuario
    public void activate() {
        this.active = true;
    }
}
```

### Usar la Clase User

```java
// 1. CREAR UN USUARIO
User maria = new User("maria@gmail.com", "password123", "María García");

// 2. USAR GETTERS
String email = maria.getEmail();  // "maria@gmail.com"
String nombre = maria.getName();  // "María García"
boolean activo = maria.isActive(); // true

// 3. USAR SETTERS
maria.setPhone("3001234567");
maria.setRole(UserRole.ADMIN);

// 4. USAR MÉTODOS
boolean esAdmin = maria.isAdmin(); // true (porque le pusimos rol ADMIN)

maria.deactivate(); // Desactivar usuario
boolean activo2 = maria.isActive(); // false

// 5. GUARDAR EN BASE DE DATOS (con Repository)
userRepository.save(maria);
```

---

## 🔗 Relaciones Entre Clases

### 1. Asociación Simple

Una clase **usa** otra clase.

```java
public class Order {
    private User user;        // Orden tiene un Usuario
    private List<Product> products; // Orden tiene Productos
}
```

### 2. Composición

Una clase **contiene** otra clase y es **dueña** de su ciclo de vida.

```java
public class Cart {
    private List<CartItem> items; // Cart es dueño de CartItems
    
    // Si eliminas el Cart, los CartItems también se eliminan
}
```

### 3. Herencia (Extends)

Una clase **hereda** de otra.

```java
// Clase padre
public class Person {
    protected String name;
    protected String email;
    
    public void sendEmail(String message) {
        // ...
    }
}

// Clase hija (hereda de Person)
public class User extends Person {
    private String password;
    private UserRole role;
    
    // User tiene: name, email (de Person) + password, role
}
```

---

## 🎯 Ejemplo Completo del Proyecto: CartService

```java
@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    // ===== MÉTODO: Agregar al carrito =====
    public CartResponse addToCart(String userEmail, Long productId, int quantity) {
        // 1. Buscar el carrito del usuario
        Cart cart = cartRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new NotFoundException("Carrito no encontrado"));
        
        // 2. Buscar el producto
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        
        // 3. Verificar stock
        if (product.getStock() < quantity) {
            throw new BadRequestException("Stock insuficiente");
        }
        
        // 4. Verificar si el producto ya está en el carrito
        CartItem existingItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElse(null);
        
        if (existingItem != null) {
            // Si ya existe, aumentar cantidad
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Si no existe, crear nuevo item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }
        
        // 5. Guardar en BD
        cart = cartRepository.save(cart);
        
        // 6. Convertir a DTO y retornar
        return convertToResponse(cart);
    }
}
```

**Conceptos usados:**
- ✅ **Clase**: `CartService`
- ✅ **Objetos**: `cart`, `product`, `existingItem`, `newItem`
- ✅ **Atributos**: `cartRepository`, `productRepository`
- ✅ **Métodos**: `addToCart()`, `findByUserEmail()`, `getStock()`
- ✅ **Relaciones**: Cart tiene CartItems, CartItem tiene Product

---

## 📊 Diferencias: Clase vs Objeto

| Clase | Objeto |
|-------|--------|
| Plantilla/Molde | Instancia/Copia |
| Abstracta (no existe en memoria) | Concreta (existe en memoria) |
| Se define una vez | Se crean muchos |
| `class User { ... }` | `User maria = new User();` |

**Analogía:**
```
Clase = Plano de una casa
Objeto = Casa construida real

Puedes tener:
- 1 plano (clase)
- 100 casas construidas (100 objetos)
```

---

## 🎓 Ejemplo Final: Order y OrderItem

```java
// ===== CLASE: Order =====
@Entity
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    
    private String orderNumber;
    private BigDecimal total;
    private LocalDateTime createdAt;
    
    @ManyToOne
    private User user;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    // Constructor
    public Order(User user) {
        this.user = user;
        this.orderNumber = generateOrderNumber();
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>();
    }
    
    // Método: Calcular total
    public void calculateTotal() {
        this.total = items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Método: Agregar item
    public void addItem(Product product, int quantity) {
        OrderItem item = new OrderItem(this, product, quantity);
        items.add(item);
    }
}

// ===== CLASE: OrderItem =====
@Entity
public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private Order order;
    
    @ManyToOne
    private Product product;
    
    private int quantity;
    private BigDecimal price;
    
    // Constructor
    public OrderItem(Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = product.getPrice(); // Guardar precio histórico
    }
    
    // Método: Calcular subtotal
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}

// ===== USAR LAS CLASES =====
User maria = userRepository.findByEmail("maria@gmail.com").get();
Product pañales = productRepository.findById(1L).get();
Product leche = productRepository.findById(2L).get();

// Crear orden
Order orden = new Order(maria);

// Agregar items
orden.addItem(pañales, 2);
orden.addItem(leche, 3);

// Calcular total
orden.calculateTotal();

// Guardar
orderRepository.save(orden);

System.out.println("Orden: " + orden.getOrderNumber());
System.out.println("Total: " + orden.getTotal());
System.out.println("Items: " + orden.getItems().size());
```

---

## 📋 Resumen

| Concepto | Definición | Ejemplo |
|----------|------------|---------|
| **Clase** | Plantilla para crear objetos | `class User {...}` |
| **Objeto** | Instancia de una clase | `User maria = new User();` |
| **Atributo** | Variable dentro de una clase | `private String name;` |
| **Constructor** | Método para crear objetos | `public User(String name) {...}` |
| **Método** | Función dentro de una clase | `public void deactivate() {...}` |
| **Getter** | Obtener valor de atributo | `public String getName() {...}` |
| **Setter** | Modificar valor de atributo | `public void setName(String n) {...}` |
| **this** | Referencia al objeto actual | `this.name = name;` |

---

**Última actualización**: Octubre 2025
