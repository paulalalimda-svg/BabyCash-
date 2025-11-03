# 📦 DTOs - DATA TRANSFER OBJECTS

## 🎯 ¿Qué es un DTO?

**DTO** = **D**ata **T**ransfer **O**bject

Es un objeto simple que **transfiere datos** entre capas de la aplicación (especialmente entre Backend y Frontend).

### Analogía

Es como un **paquete de envío**:
- Contiene solo lo necesario (datos)
- No tiene lógica compleja
- Optimizado para transporte
- Seguro (no expone información sensible)

---

## 🆚 Entity vs DTO

### Entity (Modelo de Base de Datos)

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
    private String password;  // ❌ NO debe exponerse al frontend
    
    private String name;
    private String phone;
    private String address;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private Boolean active;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Relaciones
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;  // ❌ Puede causar JSON infinito
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;  // ❌ Puede cargar MILES de registros
    
    // getters y setters
}
```

### DTO (Transferencia de Datos)

```java
public class UserDTO {
    private Long id;
    private String email;
    // ✅ NO incluye password
    private String name;
    private String phone;
    private String address;
    private String role;
    private Boolean active;
    
    // ✅ NO incluye relaciones complejas
    
    // getters y setters
}
```

---

## ❌ Problemas de Usar Entities Directamente

### Problema 1: Exposición de Datos Sensibles

```java
// ❌ MAL - Devuelve Entity directamente
@GetMapping("/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userService.getUser(id);
    return ResponseEntity.ok(user);
}

// Respuesta JSON:
{
  "id": 1,
  "email": "maria@gmail.com",
  "password": "$2a$10$abcdef...",  // ❌ EXPONE PASSWORD
  "name": "María",
  "role": "USER"
}
```

### Problema 2: JSON Infinito

```java
// ❌ MAL - Relaciones bidireccionales causan loop infinito
@Entity
public class User {
    @OneToOne(mappedBy = "user")
    private Cart cart;
}

@Entity
public class Cart {
    @OneToOne
    private User user;
}

// JSON:
{
  "id": 1,
  "name": "María",
  "cart": {
    "id": 1,
    "user": {
      "id": 1,
      "cart": {
        "id": 1,
        "user": {
          // ❌ LOOP INFINITO
```

### Problema 3: Cargar Datos Innecesarios

```java
// ❌ MAL - Carga TODAS las órdenes del usuario
@GetMapping("/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userService.getUser(id);
    return ResponseEntity.ok(user);
}

// Si el usuario tiene 1000 órdenes, ¡carga las 1000! ❌
```

### Problema 4: Acoplamiento con Base de Datos

```java
// ❌ MAL - Si cambias la Entity, cambias el JSON del API
@Entity
public class User {
    private String email;
    // Si agregas una columna aquí, se expone automáticamente en el API ❌
}
```

---

## ✅ Solución: Usar DTOs

### Ventajas

1. ✅ **Seguridad**: No expone datos sensibles
2. ✅ **Control**: Tú decides qué campos devolver
3. ✅ **Desacoplamiento**: Entity y API independientes
4. ✅ **Performance**: Solo transfiere lo necesario
5. ✅ **Documentación**: DTO muestra claramente qué espera el API

---

## 🎯 Ejemplo: UserDTO

### Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String password;  // Encriptado con BCrypt
    private String name;
    private String phone;
    private String address;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
    
    @OneToOne(mappedBy = "user")
    private Cart cart;
    
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
    
    // getters y setters
}
```

### DTO (Respuesta)

```java
public class UserDTO {
    private Long id;
    private String email;
    // ✅ NO incluye password
    private String name;
    private String phone;
    private String address;
    private String role;
    private Boolean active;
    
    // Constructor, getters y setters
}
```

### DTO (Registro)

```java
public class RegisterUserDTO {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    private String phone;
    private String address;
    
    // getters y setters
}
```

### DTO (Login)

```java
public class LoginDTO {
    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    // getters y setters
}
```

---

## 🎯 Ejemplo: ProductDTO

### Entity

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @ManyToMany
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;
    
    // getters y setters
}
```

### DTO (Simple)

```java
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Boolean available;
    
    // ✅ NO incluye createdAt, updatedAt
    // ✅ NO incluye relaciones complejas (categories)
    
    // getters y setters
}
```

### DTO (Con Categorías)

```java
public class ProductWithCategoriesDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Boolean available;
    
    private List<String> categoryNames;  // ✅ Solo nombres, no objetos completos
    
    // getters y setters
}
```

### DTO (Crear Producto)

```java
public class CreateProductDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0)
    private Integer stock;
    
    private String imageUrl;
    
    private List<Long> categoryIds;  // IDs de categorías
    
    // getters y setters
}
```

---

## 🎯 Ejemplo: OrderDTO

### Entity

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String orderNumber;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private BigDecimal total;
    private OrderStatus status;
    private String shippingAddress;
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    // getters y setters
}
```

### DTO (Lista de Órdenes)

```java
public class OrderSummaryDTO {
    private Long id;
    private String orderNumber;
    private BigDecimal total;
    private String status;
    private LocalDateTime createdAt;
    
    // ✅ NO incluye items completos
    private Integer itemCount;  // Solo la cantidad
    
    // getters y setters
}
```

### DTO (Detalle de Orden)

```java
public class OrderDetailDTO {
    private Long id;
    private String orderNumber;
    private BigDecimal total;
    private String status;
    private String shippingAddress;
    private LocalDateTime createdAt;
    
    // ✅ Información básica del usuario
    private UserSummaryDTO user;
    
    // ✅ Items completos
    private List<OrderItemDTO> items;
    
    // getters y setters
}

public class UserSummaryDTO {
    private Long id;
    private String name;
    private String email;
    // ✅ Solo lo necesario
}

public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    
    // getters y setters
}
```

### DTO (Crear Orden)

```java
public class CreateOrderDTO {
    @NotNull(message = "El ID del carrito es obligatorio")
    private Long cartId;
    
    @NotBlank(message = "La dirección de envío es obligatoria")
    private String shippingAddress;
    
    private String paymentMethod;
    
    // getters y setters
}
```

---

## 🎯 DTOs en BabyCash

### Estructura de Carpetas

```
src/main/java/com/babycash/
├── model/              # Entities
│   ├── User.java
│   ├── Product.java
│   └── Order.java
├── dto/                # DTOs
│   ├── user/
│   │   ├── UserDTO.java
│   │   ├── RegisterUserDTO.java
│   │   └── LoginDTO.java
│   ├── product/
│   │   ├── ProductDTO.java
│   │   ├── CreateProductDTO.java
│   │   └── UpdateProductDTO.java
│   ├── order/
│   │   ├── OrderSummaryDTO.java
│   │   ├── OrderDetailDTO.java
│   │   └── CreateOrderDTO.java
│   ├── cart/
│   │   ├── CartDTO.java
│   │   └── AddToCartDTO.java
│   └── auth/
│       ├── AuthResponseDTO.java
│       └── RefreshTokenDTO.java
```

---

## 📋 Convenciones de Nombres

| Tipo de DTO | Nombre | Propósito |
|-------------|--------|-----------|
| **Respuesta simple** | `ProductDTO` | Devolver datos básicos |
| **Respuesta completa** | `ProductDetailDTO` | Devolver con relaciones |
| **Resumen** | `ProductSummaryDTO` | Lista con pocos campos |
| **Crear** | `CreateProductDTO` | Recibir datos para crear |
| **Actualizar** | `UpdateProductDTO` | Recibir datos para actualizar |
| **Request** | `LoginRequestDTO` | Petición específica |
| **Response** | `AuthResponseDTO` | Respuesta específica |

---

## 🔄 Conversión Entity ↔ DTO

### En el Service

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        return convertToDTO(product);  // Entity → DTO
    }
    
    public ProductDTO createProduct(CreateProductDTO createDTO) {
        Product product = convertToEntity(createDTO);  // DTO → Entity
        Product saved = productRepository.save(product);
        return convertToDTO(saved);  // Entity → DTO
    }
    
    // Conversión manual
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        dto.setAvailable(product.getAvailable());
        return dto;
    }
    
    private Product convertToEntity(CreateProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setAvailable(true);
        return product;
    }
}
```

---

## ✅ Validaciones en DTOs

```java
public class CreateProductDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal price;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 10000, message = "El stock no puede exceder 10,000")
    private Integer stock;
    
    @Pattern(regexp = "^https?://.*", message = "La URL de la imagen debe comenzar con http:// o https://")
    private String imageUrl;
    
    // getters y setters
}
```

---

## 📋 Resumen

| Concepto | Descripción |
|----------|-------------|
| **DTO** | Objeto simple para transferir datos |
| **Entity** | Modelo de base de datos con relaciones |
| **¿Por qué DTOs?** | Seguridad, control, performance, desacoplamiento |
| **Conversión** | Service convierte Entity ↔ DTO |
| **Validaciones** | DTOs usan anotaciones de validación |
| **Nombrado** | `ProductDTO`, `CreateProductDTO`, `ProductSummaryDTO` |

### Entity vs DTO

| Característica | Entity | DTO |
|----------------|--------|-----|
| **Anotaciones JPA** | ✅ Sí (`@Entity`, `@Table`) | ❌ No |
| **Relaciones** | ✅ Sí (`@OneToMany`, etc.) | ❌ No (o simplificadas) |
| **Datos sensibles** | ✅ Sí (password) | ❌ No |
| **Uso** | Base de datos | Transferencia (API) |
| **Validaciones** | ❌ Raramente | ✅ Sí (`@NotNull`, `@Size`) |

---

**Última actualización**: Octubre 2025
