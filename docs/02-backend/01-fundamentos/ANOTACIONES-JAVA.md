# 📝 ANOTACIONES EN JAVA

## 🎯 ¿Qué son las Anotaciones?

**Explicación Simple:**
Las anotaciones son **etiquetas especiales** que empiezan con `@` y le dan **instrucciones** al compilador o al framework. Es como poner **notas adhesivas** en tu código.

**Explicación Técnica:**
Las anotaciones son **metadatos** que proporcionan información sobre el código sin afectar su ejecución directa.

---

## 📋 Sintaxis

```java
@NombreAnotacion
public class MiClase { }

@NombreAnotacion(parametro = "valor")
public void miMetodo() { }

@NombreAnotacion(param1 = "valor1", param2 = 123)
private String atributo;
```

---

## 🔧 Anotaciones Básicas de Java

### @Override

**¿Qué hace?**
Indica que un método **sobrescribe** un método de la clase padre.

```java
public class User {
    @Override
    public String toString() {
        return "User: " + email;
    }
    
    @Override
    public boolean equals(Object obj) {
        // Comparación personalizada
    }
}
```

---

## 🎯 Anotaciones JPA (Base de Datos)

### @Entity

**¿Qué hace?**
Marca una clase como una **tabla de la base de datos**.

```java
@Entity
public class Product {
    // Esta clase = tabla 'product' en PostgreSQL
}
```

### @Table

**¿Qué hace?**
Especifica el **nombre de la tabla**.

```java
@Entity
@Table(name = "products")  // Tabla se llamará 'products' en vez de 'product'
public class Product {
}
```

### @Id

**¿Qué hace?**
Marca el campo como **clave primaria**.

```java
@Entity
public class User {
    @Id
    private Long id;  // Este es el PRIMARY KEY
}
```

### @GeneratedValue

**¿Qué hace?**
Indica cómo se genera el ID automáticamente.

```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PostgreSQL genera IDs automáticamente (1, 2, 3...)
}
```

### @Column

**¿Qué hace?**
Configura propiedades de la columna.

```java
@Entity
public class User {
    @Column(unique = true, nullable = false, length = 100)
    private String email;  // Email único, obligatorio, máximo 100 caracteres
    
    @Column(name = "full_name")
    private String name;  // En BD se llama 'full_name' pero en Java 'name'
}
```

### @OneToOne, @OneToMany, @ManyToOne

**¿Qué hacen?**
Definen **relaciones** entre tablas.

```java
// Usuario tiene UN carrito (1:1)
@Entity
public class User {
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;
}

// Carrito pertenece a UN usuario
@Entity
public class Cart {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // Carrito tiene MUCHOS items (1:N)
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> items;
}

// Item pertenece a UN carrito (N:1)
@Entity
public class CartItem {
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
```

### @Enumerated

**¿Qué hace?**
Guarda un enum en la base de datos.

```java
@Entity
public class User {
    @Enumerated(EnumType.STRING)  // Guarda "USER" o "ADMIN" (texto)
    private UserRole role;
}

// Sin EnumType.STRING guardaría 0 o 1 (número) - no recomendado
```

### @CreationTimestamp, @UpdateTimestamp

**¿Qué hacen?**
Agregan fecha automáticamente.

```java
@Entity
public class Product {
    @CreationTimestamp
    private LocalDateTime createdAt;  // Se llena automáticamente al crear
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;  // Se actualiza automáticamente al modificar
}
```

---

## 🌟 Anotaciones de Spring

### @Component

**¿Qué hace?**
Marca una clase como un **bean** de Spring (Spring la gestiona).

```java
@Component
public class EmailHelper {
    // Spring crea y gestiona esta clase
}
```

### @Service

**¿Qué hace?**
Igual que `@Component` pero indica que es un **servicio** (lógica de negocio).

```java
@Service
public class ProductService {
    // Lógica de negocio para productos
}
```

### @Repository

**¿Qué hace?**
Igual que `@Component` pero indica que accede a **base de datos**.

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Acceso a la tabla 'users'
}
```

### @Controller vs @RestController

**@Controller:** Para vistas HTML (Thymeleaf).
**@RestController:** Para APIs REST (retorna JSON).

```java
@RestController  // API REST
@RequestMapping("/api/products")
public class ProductController {
    // Retorna JSON
}
```

---

## 🛣️ Anotaciones de Routing (Rutas)

### @RequestMapping

**¿Qué hace?**
Define la **ruta base** del controller.

```java
@RestController
@RequestMapping("/api/products")  // Todas las rutas empiezan con /api/products
public class ProductController {
}
```

### @GetMapping, @PostMapping, @PutMapping, @DeleteMapping

**¿Qué hacen?**
Definen endpoints HTTP.

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    // GET /api/products
    @GetMapping
    public List<Product> getAll() { }
    
    // GET /api/products/5
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) { }
    
    // POST /api/products
    @PostMapping
    public Product create(@RequestBody ProductRequest request) { }
    
    // PUT /api/products/5
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody ProductRequest request) { }
    
    // DELETE /api/products/5
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { }
}
```

---

## 📦 Anotaciones de Parámetros

### @PathVariable

**¿Qué hace?**
Obtiene un valor de la **URL**.

```java
// GET /api/products/5
@GetMapping("/{id}")
public Product getById(@PathVariable Long id) {
    // id = 5
}

// GET /api/users/maria@gmail.com/orders
@GetMapping("/{email}/orders")
public List<Order> getOrders(@PathVariable String email) {
    // email = "maria@gmail.com"
}
```

### @RequestBody

**¿Qué hace?**
Obtiene datos del **cuerpo de la petición** (JSON).

```java
// POST /api/auth/register
// Body: { "email": "maria@gmail.com", "password": "123" }
@PostMapping("/register")
public AuthResponse register(@RequestBody RegisterRequest request) {
    // request.getEmail() = "maria@gmail.com"
    // request.getPassword() = "123"
}
```

### @RequestParam

**¿Qué hace?**
Obtiene parámetros de la **query string** (?param=value).

```java
// GET /api/products?category=baby&minPrice=10000
@GetMapping
public List<Product> search(
    @RequestParam String category,
    @RequestParam BigDecimal minPrice
) {
    // category = "baby"
    // minPrice = 10000
}

// Parámetro opcional
@GetMapping
public List<Product> search(
    @RequestParam(required = false) String category
) {
    // category puede ser null
}
```

---

## 🔐 Anotaciones de Seguridad

### @PreAuthorize

**¿Qué hace?**
Restringe acceso según **rol**.

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    // Solo usuarios con rol ADMIN pueden acceder
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) { }
}
```

---

## ⚙️ Anotaciones de Configuración

### @Configuration

**¿Qué hace?**
Marca una clase como **archivo de configuración**.

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### @Bean

**¿Qué hace?**
Crea un **objeto gestionado por Spring**.

```java
@Configuration
public class AppConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();  // Spring gestiona este objeto
    }
}
```

### @Value

**¿Qué hace?**
Inyecta valores desde `application.properties` o `.env`.

```java
@Component
public class JwtUtil {
    
    @Value("${app.jwt.secret}")
    private String secretKey;  // Lee JWT_SECRET del .env
    
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;  // Lee JWT_EXPIRATION_MS
}
```

---

## 🔄 Anotaciones de Transacciones

### @Transactional

**¿Qué hace?**
Asegura que todo se ejecute **correctamente o nada**.

```java
@Service
public class OrderService {
    
    @Transactional  // Si algo falla, se revierte TODO
    public Order createOrder(CreateOrderRequest request) {
        // 1. Crear orden
        // 2. Reducir stock
        // 3. Crear pago
        // 4. Limpiar carrito
        
        // Si el paso 3 falla, los pasos 1 y 2 se revierten automáticamente
    }
}
```

---

## ⚡ Anotaciones de Programación Asíncrona

### @Async

**¿Qué hace?**
Ejecuta el método en **otro hilo** (no bloquea).

```java
@Service
public class EmailService {
    
    @Async  // No espera a que termine el envío
    public void sendWelcomeEmail(String to, String name) {
        // Enviar email (puede tardar 2-3 segundos)
    }
}
```

---

## 🎓 Ejemplo Completo: ProductController

```java
@RestController                           // 1. Es un controller REST (retorna JSON)
@RequestMapping("/api/products")          // 2. Ruta base: /api/products
@CrossOrigin(origins = "*")               // 3. Permitir CORS desde cualquier origen
public class ProductController {
    
    @Autowired                            // 4. Inyectar dependencia
    private ProductService productService;
    
    // GET /api/products
    @GetMapping                           // 5. Endpoint GET
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    // GET /api/products/5
    @GetMapping("/{id}")                  // 6. Endpoint con variable de ruta
    public ResponseEntity<ProductResponse> getProductById(
        @PathVariable Long id             // 7. Obtener ID de la URL
    ) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    // POST /api/admin/products (requiere ADMIN)
    @PreAuthorize("hasRole('ADMIN')")     // 8. Solo ADMIN
    @PostMapping                          // 9. Endpoint POST
    public ResponseEntity<ProductResponse> createProduct(
        @RequestBody ProductRequest request  // 10. Obtener datos del body JSON
    ) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    // GET /api/products/search?name=pañal
    @GetMapping("/search")                // 11. Endpoint con query params
    public ResponseEntity<List<ProductResponse>> searchProducts(
        @RequestParam String name         // 12. Obtener parámetro ?name=
    ) {
        List<ProductResponse> products = productService.searchByName(name);
        return ResponseEntity.ok(products);
    }
}
```

---

## 📋 Anotaciones Más Usadas en el Proyecto

| Anotación | Dónde | Para Qué |
|-----------|-------|----------|
| `@Entity` | Entities | Marcar tabla de BD |
| `@Service` | Services | Lógica de negocio |
| `@RestController` | Controllers | API REST |
| `@Autowired` | Todos | Inyectar dependencias |
| `@GetMapping` | Controllers | Endpoint GET |
| `@PostMapping` | Controllers | Endpoint POST |
| `@PathVariable` | Controllers | Variable de URL |
| `@RequestBody` | Controllers | Datos JSON del body |
| `@Transactional` | Services | Transacción BD |
| `@PreAuthorize` | Controllers | Seguridad por rol |

---

## 📊 Resumen

**Anotaciones Java:**
- `@Override` - Sobrescribir método

**Anotaciones JPA:**
- `@Entity` - Tabla BD
- `@Id` - Primary key
- `@GeneratedValue` - ID auto
- `@Column` - Config columna
- `@OneToOne`, `@OneToMany`, `@ManyToOne` - Relaciones

**Anotaciones Spring:**
- `@Component`, `@Service`, `@Repository` - Beans de Spring
- `@RestController` - Controller REST
- `@RequestMapping` - Ruta base
- `@GetMapping`, `@PostMapping` - Endpoints HTTP
- `@Autowired` - Inyección
- `@Transactional` - Transacción
- `@Async` - Asíncrono

---

**Última actualización**: Octubre 2025
