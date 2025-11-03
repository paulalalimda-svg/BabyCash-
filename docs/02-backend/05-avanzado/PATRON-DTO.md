# PATRÓN DTO (Data Transfer Object)

## 🎯 Definición

**DTO** es un objeto simple usado para **transferir datos** entre capas o sistemas.

Es como un **sobre**: contiene información, pero NO tiene lógica de negocio.

---

## ❓ ¿Para Qué Sirve?

### Problema: Exponer Entities Directamente

```java
❌ MAL: Controller devuelve Entity directamente
@RestController
public class UserController {
    
    @GetMapping("/api/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);  // ❌ Devuelve Entity
    }
}

// Entity con información sensible
@Entity
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;  // ❌ ¡No queremos exponer esto!
    private String ssn;       // ❌ ¡Datos sensibles!
    private Set<Order> orders;  // ❌ Puede ser enorme
}
```

**Problemas:**
- ❌ Expone datos sensibles (password, SSN)
- ❌ Puede causar lazy loading exceptions
- ❌ JSON puede ser gigante (relaciones cargadas)
- ❌ Acoplamiento entre API y DB

---

## ✅ Con DTO

```java
// ✅ DTO: Solo datos necesarios para el cliente
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    // ✅ NO incluye password ni ssn
}

// ✅ Controller devuelve DTO
@RestController
public class UserController {
    
    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);  // ✅ Devuelve DTO
    }
}

// ✅ Service mapea Entity a DTO
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return mapToResponse(user);  // ✅ Mapeo
    }
    
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        // ✅ NO incluye password
        return response;
    }
}
```

**Ventajas:**
- ✅ No expone datos sensibles
- ✅ Control sobre lo que se envía
- ✅ Sin lazy loading exceptions
- ✅ JSON limpio y pequeño

---

## 🏗️ Tipos de DTOs

### 1️⃣ **Request DTOs** (Cliente → Servidor)

```java
// ✅ DTO para crear usuario
public class CreateUserRequest {
    @NotBlank
    private String name;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    @Size(min = 8)
    private String password;
    
    // Getters y setters
}

// ✅ DTO para actualizar usuario
public class UpdateUserRequest {
    private String name;
    private String email;
    // ✅ NO incluye password (actualización separada)
    
    // Getters y setters
}
```

---

### 2️⃣ **Response DTOs** (Servidor → Cliente)

```java
// ✅ DTO para respuesta de usuario
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    
    // Getters y setters
}

// ✅ DTO para respuesta de producto
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String imageUrl;
    private CategoryResponse category;  // ✅ DTO anidado
    
    // Getters y setters
}
```

---

## 🏗️ DTOs en Baby Cash

### ✅ CreateProductRequest

```java
@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;
    
    @DecimalMin(value = "0.00", message = "Discount price cannot be negative")
    private BigDecimal discountPrice;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
    
    private String imageUrl;
    
    @NotNull(message = "Category is required")
    private Long categoryId;
}
```

---

### ✅ ProductResponse

```java
@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stock;
    private Boolean enabled;
    private String imageUrl;
    private String slug;
    private CategoryResponse category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

### ✅ CreateOrderRequest

```java
@Data
public class CreateOrderRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotEmpty(message = "Order must have items")
    private List<OrderItemRequest> items;
    
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    private String billingAddress;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}

@Data
public class OrderItemRequest {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
```

---

### ✅ OrderResponse

```java
@Data
@Builder
public class OrderResponse {
    private Long id;
    private UserResponse user;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private LocalDateTime createdAt;
}

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
```

---

## 📊 Mapeo Entity ↔ DTO

### ✅ Manual (Método mapToResponse)

```java
@Service
public class ProductService {
    
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .discountPrice(product.getDiscountPrice())
            .stock(product.getStock())
            .enabled(product.getEnabled())
            .imageUrl(product.getImageUrl())
            .slug(product.getSlug())
            .category(mapCategoryToResponse(product.getCategory()))
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }
    
    private Product mapToEntity(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setEnabled(true);
        return product;
    }
}
```

---

### ✅ Con ModelMapper (Automático)

```java
@Configuration
public class MapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

@Service
public class ProductService {
    
    @Autowired
    private ModelMapper modelMapper;
    
    private ProductResponse mapToResponse(Product product) {
        return modelMapper.map(product, ProductResponse.class);
    }
    
    private Product mapToEntity(CreateProductRequest request) {
        return modelMapper.map(request, Product.class);
    }
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es un DTO?"**

> "DTO (Data Transfer Object) es un objeto simple usado para transferir datos entre capas o sistemas. No tiene lógica de negocio, solo getters/setters. Se usa para separar la representación externa (API) de la interna (Entity), evitando exponer datos sensibles y controlando qué información se envía al cliente."

---

**2. "¿Por qué no devolver Entities directamente?"**

> "Por varias razones:
> - **Seguridad**: Entity puede tener campos sensibles (password, SSN)
> - **Lazy loading**: Puede causar excepciones si se acceden relaciones no cargadas
> - **JSON gigante**: Relaciones pueden cargar datos innecesarios
> - **Acoplamiento**: API queda acoplada a estructura de DB
> 
> DTO permite controlar exactamente qué se envía."

---

**3. "¿Dónde usas DTOs en Baby Cash?"**

> "En toda la API:
> - **Request DTOs**: `CreateProductRequest`, `UpdateProductRequest`, `CreateOrderRequest`
> - **Response DTOs**: `ProductResponse`, `OrderResponse`, `UserResponse`
> 
> Controllers reciben Request DTOs y devuelven Response DTOs. Services mapean entre Entity y DTO."

---

**4. "¿Cómo mapeas Entity a DTO?"**

> "De dos formas:
> 1. **Manual**: Método `mapToResponse()` con Builder o setters
> 2. **Automático**: ModelMapper o MapStruct
> 
> En Baby Cash uso mapeo manual con `@Builder` de Lombok porque tengo control total sobre el mapeo."

---

## 📝 Checklist de DTO

```
✅ Clases simples (solo datos, no lógica)
✅ Request DTOs para entrada (CreateXRequest, UpdateXRequest)
✅ Response DTOs para salida (XResponse)
✅ Validaciones con Bean Validation (@NotNull, @NotBlank)
✅ Mapeo entre Entity y DTO en Service
✅ NO exponer datos sensibles
```

---

## 🏆 Ventajas y Desventajas

### ✅ Ventajas

```
✅ No expone datos sensibles
✅ Control sobre datos enviados
✅ Evita lazy loading exceptions
✅ JSON limpio y optimizado
✅ Desacopla API de DB
✅ Fácil versionar API
```

---

### ❌ Desventajas

```
❌ Más clases (Request + Response por Entity)
❌ Código de mapeo (manual o librería)
❌ Puede parecer boilerplate
```

---

## 🚀 Conclusión

**DTO:**
- ✅ Transferencia de datos segura
- ✅ Control sobre información expuesta
- ✅ Desacopla API de DB

**En Baby Cash, TODA la API usa DTOs (Request y Response).**

---

**Ahora lee:** `PATRON-DEPENDENCY-INJECTION.md` para el siguiente patrón. 🚀
