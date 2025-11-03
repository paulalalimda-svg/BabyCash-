# 🔒 MODIFICADORES DE ACCESO EN JAVA

## 🎯 ¿Qué son los Modificadores de Acceso?

**Explicación Simple:**
Son **palabras clave** que controlan **quién puede ver y usar** tus clases, métodos y atributos. Es como poner **cerraduras de diferentes niveles** en las puertas.

**Explicación Técnica:**
Son keywords que definen el nivel de **visibilidad** y **accesibilidad** de clases, métodos y atributos.

---

## 🔑 Los 4 Modificadores

| Modificador | Acceso Desde | Uso Principal |
|-------------|--------------|---------------|
| **public** | Cualquier lugar | APIs, métodos expuestos |
| **private** | Solo dentro de la clase | Atributos, métodos auxiliares |
| **protected** | Clase + subclases + mismo paquete | Herencia |
| **default** (sin modificador) | Solo mismo paquete | Clases internas |

---

## 1️⃣ public (Público)

### ¿Cuándo Usar?
- ✅ Clases principales
- ✅ Métodos de servicios (API pública)
- ✅ Constructores
- ✅ Getters y Setters

### Ejemplo

```java
// Clase pública - Cualquiera puede usarla
public class User {
    
    private String email;
    
    // Constructor público - Cualquiera puede crear usuarios
    public User(String email) {
        this.email = email;
    }
    
    // Getter público - Cualquiera puede leer el email
    public String getEmail() {
        return email;
    }
    
    // Setter público - Cualquiera puede modificar el email
    public void setEmail(String email) {
        this.email = email;
    }
}

// Servicio con métodos públicos
@Service
public class ProductService {
    
    // Método público - Controllers pueden llamarlo
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Método público - Controllers pueden llamarlo
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }
}
```

---

## 2️⃣ private (Privado)

### ¿Cuándo Usar?
- ✅ Atributos de una clase (siempre)
- ✅ Métodos auxiliares internos
- ✅ Datos sensibles

### Ejemplo

```java
public class User {
    
    // ❌ MAL - Atributos públicos
    public String email;
    public String password;
    
    // ✅ BIEN - Atributos privados
    private String email;
    private String password;
    
    // Método privado auxiliar - Solo se usa dentro de User
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    
    // Método público que usa el método privado
    public void setEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = email;
    }
}
```

### Ejemplo del Proyecto: CartService

```java
@Service
public class CartService {
    
    // Atributos privados (inyección de dependencias)
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    
    // Constructor público
    public CartService(CartRepository cartRepository, 
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }
    
    // Método público - Controllers pueden llamarlo
    public CartResponse addToCart(String userEmail, Long productId, int quantity) {
        Cart cart = getCartByEmail(userEmail);
        Product product = getProductById(productId);
        
        validateStock(product, quantity);
        
        CartItem item = findOrCreateItem(cart, product, quantity);
        cart = cartRepository.save(cart);
        
        return convertToResponse(cart);
    }
    
    // Métodos privados - Solo se usan internamente
    
    private Cart getCartByEmail(String email) {
        return cartRepository.findByUserEmail(email)
            .orElseThrow(() -> new NotFoundException("Carrito no encontrado"));
    }
    
    private Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }
    
    private void validateStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new BadRequestException("Stock insuficiente");
        }
    }
    
    private CartItem findOrCreateItem(Cart cart, Product product, int quantity) {
        // Lógica interna...
        return item;
    }
    
    private CartResponse convertToResponse(Cart cart) {
        // Conversión interna...
        return response;
    }
}
```

**¿Por qué privados?**
- `getCartByEmail()`: Solo CartService necesita buscar carritos
- `validateStock()`: Validación interna, no debe ser llamada desde fuera
- `convertToResponse()`: Conversión interna

---

## 3️⃣ protected (Protegido)

### ¿Cuándo Usar?
- ✅ Herencia (cuando una clase extiende otra)
- ✅ Métodos que las subclases pueden sobrescribir

### Ejemplo

```java
// Clase padre
public class BaseEntity {
    
    protected Long id;
    protected LocalDateTime createdAt;
    
    // Constructor protegido - Solo subclases pueden usarlo
    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Método protegido - Subclases pueden usarlo
    protected void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

// Clase hija
public class User extends BaseEntity {
    
    private String email;
    
    public User(String email) {
        super(); // Llama al constructor protegido de BaseEntity
        this.email = email;
        // Puede acceder a 'id' y 'createdAt' porque son protected
    }
    
    public void resetCreatedAt() {
        // Puede usar setCreatedAt() porque es protected
        setCreatedAt(LocalDateTime.now());
    }
}
```

**Nota:** En este proyecto no usamos mucho `protected` porque no hay mucha herencia.

---

## 4️⃣ default (Sin Modificador)

### ¿Cuándo Usar?
- Clases internas del mismo paquete
- Poco común en proyectos Spring Boot

### Ejemplo

```java
// Sin modificador = package-private
class InternalHelper {
    void doSomething() {
        // Solo accesible dentro del mismo package
    }
}
```

---

## 📊 Tabla de Visibilidad

| Modificador | Misma Clase | Mismo Package | Subclase | Otro Package |
|-------------|-------------|---------------|----------|--------------|
| **public** | ✅ | ✅ | ✅ | ✅ |
| **protected** | ✅ | ✅ | ✅ | ❌ |
| **default** | ✅ | ✅ | ❌ | ❌ |
| **private** | ✅ | ❌ | ❌ | ❌ |

---

## 🎯 Reglas de Uso en el Proyecto

### Atributos: SIEMPRE private

```java
public class Product {
    // ✅ CORRECTO
    private Long id;
    private String name;
    private BigDecimal price;
    
    // ❌ INCORRECTO
    public Long id;
    public String name;
}
```

**¿Por qué?**
- Encapsulación
- Control sobre cómo se modifican
- Validaciones en setters

### Constructores: public

```java
public class User {
    // ✅ Constructor público
    public User(String email) {
        this.email = email;
    }
}
```

### Getters/Setters: public

```java
public class Product {
    private BigDecimal price;
    
    // ✅ Getter público
    public BigDecimal getPrice() {
        return price;
    }
    
    // ✅ Setter público con validación
    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo");
        }
        this.price = price;
    }
}
```

### Métodos de Service: public (API) y private (auxiliares)

```java
@Service
public class OrderService {
    
    // ✅ Público - Es parte de la API del servicio
    public OrderResponse createOrder(CreateOrderRequest request) {
        // ...
    }
    
    // ✅ Público - Es parte de la API del servicio
    public OrderResponse getOrderById(Long id) {
        // ...
    }
    
    // ✅ Privado - Método auxiliar interno
    private void validateStock(Cart cart) {
        // ...
    }
    
    // ✅ Privado - Conversión interna
    private OrderResponse convertToResponse(Order order) {
        // ...
    }
}
```

---

## 🔍 Ejemplos Reales del Proyecto

### AuthService.java

```java
@Service
public class AuthService {
    
    // ATRIBUTOS PRIVADOS
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    // CONSTRUCTOR PÚBLICO
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    
    // MÉTODOS PÚBLICOS (API del servicio)
    
    public AuthResponse register(RegisterRequest request) {
        validateEmailNotExists(request.getEmail());
        
        User user = createUser(request);
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        return buildAuthResponse(user, token);
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = getUserByEmail(request.getEmail());
        
        validatePassword(request.getPassword(), user.getPassword());
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        return buildAuthResponse(user, token);
    }
    
    // MÉTODOS PRIVADOS (auxiliares internos)
    
    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email ya registrado");
        }
    }
    
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
    }
    
    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
    }
    
    private User createUser(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(UserRole.USER);
        user.setActive(true);
        return user;
    }
    
    private AuthResponse buildAuthResponse(User user, String token) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole().toString());
        return response;
    }
}
```

### Product.java (Entity)

```java
@Entity
public class Product {
    
    // TODOS LOS ATRIBUTOS PRIVADOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private boolean available;
    
    // CONSTRUCTOR PÚBLICO
    public Product() {}
    
    public Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }
    
    // GETTERS Y SETTERS PÚBLICOS
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    // ... más getters y setters
    
    // MÉTODOS ÚTILES PÚBLICOS
    
    public boolean hasStock(int quantity) {
        return this.stock >= quantity;
    }
    
    public void reduceStock(int quantity) {
        if (!hasStock(quantity)) {
            throw new IllegalStateException("Stock insuficiente");
        }
        this.stock -= quantity;
    }
}
```

---

## ✅ Buenas Prácticas

### 1. Principio de Mínimo Privilegio

```java
// ✅ BIEN - Empieza con private, cambia a public solo si es necesario
private void validateStock() { }

// Si más tarde necesitas usarlo desde fuera, cámbialo a public
public void validateStock() { }
```

### 2. Encapsulación

```java
// ❌ MAL
public class User {
    public String password; // Cualquiera puede leer/modificar
}

// ✅ BIEN
public class User {
    private String password;
    
    public void setPassword(String password) {
        // Validar y encriptar
        this.password = passwordEncoder.encode(password);
    }
}
```

### 3. API Limpia

```java
@Service
public class ProductService {
    
    // ✅ Público - Es lo que otros componentes necesitan
    public List<Product> getAllProducts() { }
    public Product getProductById(Long id) { }
    
    // ✅ Privado - Implementación interna
    private List<Product> sortByPrice(List<Product> products) { }
    private boolean isAvailable(Product product) { }
}
```

---

## 📋 Resumen

| Modificador | Uso | Ejemplo |
|-------------|-----|---------|
| **public** | API pública, métodos principales | `public void createOrder()` |
| **private** | Atributos, métodos auxiliares | `private void validate()` |
| **protected** | Herencia (poco usado en este proyecto) | `protected void init()` |
| **default** | Clases internas (raro) | `class Helper { }` |

---

**Última actualización**: Octubre 2025
