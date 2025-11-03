# 🏗️ ESTRUCTURA DE PROYECTO SPRING BOOT

## 🎯 Estructura Estándar

```
babycash-backend/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── babycash/
│   │   │           └── backend/
│   │   │               ├── BabyCashApplication.java    # Clase principal
│   │   │               ├── controller/                 # Controllers (API)
│   │   │               ├── service/                    # Services (lógica)
│   │   │               ├── repository/                 # Repositories (BD)
│   │   │               ├── model/                      # Entities, DTOs
│   │   │               ├── security/                   # JWT, filtros
│   │   │               ├── config/                     # Configuraciones
│   │   │               ├── exception/                  # Excepciones
│   │   │               └── util/                       # Utilidades
│   │   │
│   │   └── resources/
│   │       ├── application.properties                  # Configuración
│   │       ├── static/                                 # CSS, JS, imágenes
│   │       └── templates/                              # HTML (Thymeleaf)
│   │
│   └── test/
│       └── java/                                       # Tests unitarios
│
├── target/                                             # Archivos compilados
├── .env                                                # Variables de entorno
├── .gitignore                                          # Archivos ignorados por Git
├── pom.xml                                             # Dependencias Maven
└── README.md                                           # Documentación
```

---

## 📂 Descripción de Carpetas

### src/main/java/

Código fuente Java.

#### com.babycash.backend/

Paquete base (raíz) del proyecto.

**Convención:** `com.empresa.proyecto`

---

### 🎮 controller/

**¿Qué es?**
Capa de **presentación**. Recibe peticiones HTTP y retorna respuestas.

**Archivos:**
```
controller/
├── AuthController.java           # Login, registro
├── ProductController.java        # CRUD productos
├── CartController.java           # Carrito de compras
├── OrderController.java          # Órdenes
├── PaymentController.java        # Pagos
├── UserController.java           # Perfil de usuario
├── BlogController.java           # Blog
├── ContactController.java        # Formulario contacto
└── AdminController.java          # Panel admin
```

**Ejemplo:**
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public List<ProductResponse> getAll() {
        return productService.getAllProducts();
    }
}
```

---

### ⚙️ service/

**¿Qué es?**
Capa de **lógica de negocio**. Procesa datos, aplica reglas, coordina operaciones.

**Archivos:**
```
service/
├── AuthService.java              # Autenticación
├── ProductService.java           # Lógica de productos
├── CartService.java              # Lógica de carrito
├── OrderService.java             # Procesamiento de órdenes
├── PaymentService.java           # Procesamiento de pagos
├── EmailService.java             # Envío de emails
├── UserService.java              # Gestión de usuarios
└── LoyaltyService.java           # Puntos de lealtad
```

**Ejemplo:**
```java
@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }
}
```

---

### 💾 repository/

**¿Qué es?**
Capa de **persistencia**. Accede a la base de datos.

**Archivos:**
```
repository/
├── UserRepository.java
├── ProductRepository.java
├── CartRepository.java
├── CartItemRepository.java
├── OrderRepository.java
├── OrderItemRepository.java
├── PaymentRepository.java
├── BlogPostRepository.java
├── BlogCommentRepository.java
├── TestimonialRepository.java
└── RefreshTokenRepository.java
```

**Ejemplo:**
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory(String category);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    Optional<Product> findBySlug(String slug);
}
```

---

### 📦 model/

**¿Qué es?**
Modelos de datos (Entities, DTOs, Enums).

**Estructura:**
```
model/
├── entity/                       # Entidades (tablas BD)
│   ├── User.java
│   ├── Product.java
│   ├── Cart.java
│   ├── Order.java
│   └── Payment.java
│
├── dto/                          # Data Transfer Objects
│   ├── request/                  # DTOs de entrada
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── ProductRequest.java
│   │   └── CreateOrderRequest.java
│   │
│   └── response/                 # DTOs de salida
│       ├── AuthResponse.java
│       ├── ProductResponse.java
│       ├── CartResponse.java
│       └── OrderResponse.java
│
└── enums/                        # Enumeraciones
    ├── UserRole.java             # USER, ADMIN
    ├── OrderStatus.java          # PENDING, COMPLETED, CANCELLED
    └── PaymentStatus.java        # PENDING, COMPLETED, FAILED
```

**Ejemplo Entity:**
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private BigDecimal price;
}
```

**Ejemplo DTO Request:**
```java
public class ProductRequest {
    private String name;
    private BigDecimal price;
    private int stock;
    
    // Getters y Setters
}
```

---

### 🔐 security/

**¿Qué es?**
Componentes de seguridad (JWT, filtros, autenticación).

**Archivos:**
```
security/
├── JwtUtil.java                  # Generar y validar JWT
├── JwtAuthenticationFilter.java  # Filtro JWT en cada petición
└── CustomUserDetailsService.java # Cargar usuario para Spring Security
```

---

### ⚙️ config/

**¿Qué es?**
Clases de configuración de Spring Boot.

**Archivos:**
```
config/
├── SecurityConfig.java           # Configuración de Spring Security
├── CorsConfig.java               # Configuración CORS
├── EmailConfig.java              # Configuración de email
└── AsyncConfig.java              # Configuración async (@Async)
```

**Ejemplo:**
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // Configuración...
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

### ⚠️ exception/

**¿Qué es?**
Excepciones personalizadas y manejador global.

**Archivos:**
```
exception/
├── NotFoundException.java        # 404
├── BadRequestException.java      # 400
├── UnauthorizedException.java    # 401
├── ForbiddenException.java       # 403
└── GlobalExceptionHandler.java   # Manejador global (@ControllerAdvice)
```

**Ejemplo:**
```java
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
```

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(404).body(
            new ErrorResponse(ex.getMessage())
        );
    }
}
```

---

### 🛠️ util/

**¿Qué es?**
Clases de utilidad, helpers.

**Archivos:**
```
util/
├── DateUtil.java                 # Utilidades de fechas
├── StringUtil.java               # Utilidades de strings
└── ValidationUtil.java           # Validaciones comunes
```

---

## 📄 src/main/resources/

### application.properties

Configuración de la aplicación.

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash
```

### static/

Archivos estáticos (CSS, JS, imágenes). Accesibles desde `/static/`.

```
static/
├── css/
├── js/
└── images/
```

### templates/

Plantillas HTML (Thymeleaf). Solo si usas vistas del lado del servidor.

---

## 🧪 src/test/java/

Tests unitarios y de integración.

```
test/java/com/babycash/backend/
├── service/
│   ├── ProductServiceTest.java
│   ├── CartServiceTest.java
│   └── OrderServiceTest.java
│
└── controller/
    └── ProductControllerTest.java
```

**Ejemplo:**
```java
@SpringBootTest
class ProductServiceTest {
    
    @Autowired
    private ProductService productService;
    
    @Test
    void testGetAllProducts() {
        List<Product> products = productService.getAllProducts();
        assertNotNull(products);
    }
}
```

---

## 📋 pom.xml

Archivo de Maven con dependencias.

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Más dependencias... -->
</dependencies>
```

---

## 🏛️ Arquitectura en Capas

```
┌─────────────────────────────────────────┐
│         CONTROLLER LAYER                 │  ← Recibe HTTP
│         (Presentación)                   │
└─────────────────────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────┐
│         SERVICE LAYER                    │  ← Lógica de negocio
│         (Business Logic)                 │
└─────────────────────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────┐
│         REPOSITORY LAYER                 │  ← Acceso a BD
│         (Persistencia)                   │
└─────────────────────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────┐
│         DATABASE                         │  ← PostgreSQL
└─────────────────────────────────────────┘
```

---

## 🔄 Flujo de Datos

### Crear un Producto (ejemplo completo)

```
1. Cliente → HTTP POST /api/products
   Body: { "name": "Pañales", "price": 45000 }

2. ProductController recibe petición
   @PostMapping

3. ProductController → ProductService
   productService.createProduct(request)

4. ProductService valida datos
   if (price <= 0) throw new BadRequestException()

5. ProductService → ProductRepository
   productRepository.save(product)

6. ProductRepository → PostgreSQL
   INSERT INTO products (name, price) VALUES ('Pañales', 45000)

7. PostgreSQL retorna producto guardado
   { id: 5, name: "Pañales", price: 45000 }

8. ProductRepository → ProductService
   Product entity

9. ProductService → ProductController
   ProductResponse DTO

10. ProductController → Cliente
    HTTP 201 Created
    { "id": 5, "name": "Pañales", "price": 45000 }
```

---

## 📐 Convenciones de Nombres

### Clases

| Tipo | Sufijo | Ejemplo |
|------|--------|---------|
| Controller | `Controller` | `ProductController.java` |
| Service | `Service` | `ProductService.java` |
| Repository | `Repository` | `ProductRepository.java` |
| Entity | Sin sufijo | `Product.java` |
| DTO Request | `Request` | `ProductRequest.java` |
| DTO Response | `Response` | `ProductResponse.java` |
| Exception | `Exception` | `NotFoundException.java` |
| Config | `Config` | `SecurityConfig.java` |

### Métodos de Controller

```java
@GetMapping           // getAll(), getById()
@PostMapping          // create(), add()
@PutMapping           // update(), edit()
@DeleteMapping        // delete(), remove()
```

### Métodos de Service

```java
getAllProducts()
getProductById(Long id)
createProduct(ProductRequest request)
updateProduct(Long id, ProductRequest request)
deleteProduct(Long id)
```

### Métodos de Repository

```java
findAll()
findById(Long id)
save(Product product)
deleteById(Long id)
findByName(String name)
existsByEmail(String email)
```

---

## 📊 Resumen

| Capa | Package | Responsabilidad | Anotación |
|------|---------|-----------------|-----------|
| **Controller** | `controller/` | Recibir HTTP, validar, retornar JSON | `@RestController` |
| **Service** | `service/` | Lógica de negocio, procesamiento | `@Service` |
| **Repository** | `repository/` | Acceso a base de datos | `@Repository` |
| **Entity** | `model/entity/` | Tablas de BD | `@Entity` |
| **DTO** | `model/dto/` | Transferencia de datos | - |
| **Config** | `config/` | Configuraciones | `@Configuration` |

---

**Última actualización**: Octubre 2025
