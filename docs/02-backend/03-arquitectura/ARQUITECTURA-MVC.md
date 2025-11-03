# 🏗️ ARQUITECTURA MVC

## 🎯 ¿Qué es MVC?

**MVC** = **M**odel **V**iew **C**ontroller

Es un **patrón de diseño** que separa la aplicación en 3 capas:

```
┌──────────────┐
│     VIEW     │  ← Interfaz de usuario (Frontend)
│  (React UI)  │
└──────┬───────┘
       │
┌──────▼───────┐
│  CONTROLLER  │  ← Recibe peticiones, devuelve respuestas
│ (ProductController)
└──────┬───────┘
       │
┌──────▼───────┐
│   SERVICE    │  ← Lógica de negocio
│ (ProductService)
└──────┬───────┘
       │
┌──────▼───────┐
│  REPOSITORY  │  ← Acceso a datos
│ (ProductRepository)
└──────┬───────┘
       │
┌──────▼───────┐
│    MODEL     │  ← Base de datos
│ (Product entity)
└──────────────┘
```

---

## 🎓 En Spring Boot: Controller → Service → Repository

### Arquitectura en Capas

```
┌─────────────────────────────────────────┐
│         FRONTEND (React)                │
│  Componente ProductList.tsx             │
└──────────────┬──────────────────────────┘
               │ HTTP Request
               │ GET /api/products
┌──────────────▼──────────────────────────┐
│      CONTROLLER (Capa de Presentación)  │
│  ProductController.java                 │
│  - Recibe peticiones HTTP               │
│  - Valida datos de entrada              │
│  - Llama al Service                     │
│  - Devuelve respuestas JSON             │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      SERVICE (Capa de Negocio)          │
│  ProductService.java                    │
│  - Lógica de negocio                    │
│  - Validaciones complejas               │
│  - Transacciones                        │
│  - Orquesta múltiples repositorios      │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│    REPOSITORY (Capa de Persistencia)    │
│  ProductRepository.java                 │
│  - Acceso a base de datos               │
│  - Consultas SQL (a través de JPA)      │
│  - CRUD básico                          │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│         BASE DE DATOS (PostgreSQL)      │
│  Tabla: products                        │
└─────────────────────────────────────────┘
```

---

## 🎯 Separación de Responsabilidades

### Controller (Presentación)

**Responsabilidad:** Manejar HTTP

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO created = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

**Qué hace:**
- ✅ Recibe peticiones HTTP
- ✅ Valida datos de entrada (con `@Valid`)
- ✅ Llama al Service
- ✅ Devuelve respuestas HTTP (JSON)

**Qué NO hace:**
- ❌ Lógica de negocio
- ❌ Acceso a base de datos
- ❌ Cálculos complejos

---

### Service (Negocio)

**Responsabilidad:** Lógica de negocio

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findByAvailableTrue();
        return products.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        // Validación de negocio
        if (productRepository.existsByName(productDTO.getName())) {
            throw new RuntimeException("Producto ya existe");
        }
        
        // Conversión DTO → Entity
        Product product = convertToEntity(productDTO);
        
        // Guardar
        Product saved = productRepository.save(product);
        
        // Conversión Entity → DTO
        return convertToDTO(saved);
    }
    
    @Transactional
    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
```

**Qué hace:**
- ✅ Lógica de negocio (validaciones, cálculos)
- ✅ Transacciones (`@Transactional`)
- ✅ Orquesta múltiples repositorios
- ✅ Conversión Entity ↔ DTO

**Qué NO hace:**
- ❌ Manejar HTTP directamente
- ❌ Consultas SQL directas

---

### Repository (Persistencia)

**Responsabilidad:** Acceso a datos

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByAvailableTrue();
    
    Optional<Product> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT p FROM Product p WHERE p.stock < :threshold")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
}
```

**Qué hace:**
- ✅ Acceso a base de datos
- ✅ Consultas (JPQL o SQL nativo)
- ✅ CRUD básico (save, findById, delete)

**Qué NO hace:**
- ❌ Lógica de negocio
- ❌ Validaciones complejas
- ❌ Conversión Entity ↔ DTO

---

## 🔄 Flujo de Datos Completo

### Ejemplo 1: Obtener Todos los Productos

```
1. Frontend (React)
   ↓
   axios.get('/api/products')
   
2. Controller
   ↓
   @GetMapping
   public ResponseEntity<List<ProductDTO>> getAllProducts() {
       List<ProductDTO> products = productService.getAllProducts();
       return ResponseEntity.ok(products);
   }
   
3. Service
   ↓
   public List<ProductDTO> getAllProducts() {
       List<Product> products = productRepository.findByAvailableTrue();
       return products.stream()
           .map(this::convertToDTO)
           .collect(Collectors.toList());
   }
   
4. Repository
   ↓
   List<Product> findByAvailableTrue();
   
5. Base de Datos
   ↓
   SELECT * FROM products WHERE available = TRUE
   
6. Respuesta (sube de vuelta)
   ↓
   Base de Datos → Repository → Service → Controller → Frontend
```

### Ejemplo 2: Crear un Producto

```
1. Frontend
   ↓
   axios.post('/api/products', {
       name: 'Pañales Huggies',
       price: 45000,
       stock: 50
   })
   
2. Controller
   ↓
   @PostMapping
   public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
       ProductDTO created = productService.createProduct(productDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(created);
   }
   
3. Service
   ↓
   @Transactional
   public ProductDTO createProduct(ProductDTO productDTO) {
       // Validación de negocio
       if (productRepository.existsByName(productDTO.getName())) {
           throw new RuntimeException("Producto ya existe");
       }
       
       // Convertir DTO → Entity
       Product product = new Product();
       product.setName(productDTO.getName());
       product.setPrice(productDTO.getPrice());
       product.setStock(productDTO.getStock());
       
       // Guardar
       Product saved = productRepository.save(product);
       
       // Convertir Entity → DTO
       return convertToDTO(saved);
   }
   
4. Repository
   ↓
   productRepository.save(product)
   
5. Base de Datos
   ↓
   INSERT INTO products (name, price, stock) VALUES ('Pañales Huggies', 45000, 50)
   
6. Respuesta
   ↓
   { id: 1, name: 'Pañales Huggies', price: 45000, stock: 50 }
```

### Ejemplo 3: Crear Orden (Múltiples Operaciones)

```
1. Frontend
   ↓
   axios.post('/api/orders', { cartId: 1, address: '...' })
   
2. Controller
   ↓
   @PostMapping
   public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
       OrderDTO order = orderService.createOrder(request);
       return ResponseEntity.status(HttpStatus.CREATED).body(order);
   }
   
3. Service (orquesta múltiples repositorios)
   ↓
   @Transactional
   public OrderDTO createOrder(CreateOrderRequest request) {
       // 1. Obtener carrito
       Cart cart = cartRepository.findById(request.getCartId())
           .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
       
       // 2. Validar stock
       for (CartItem item : cart.getItems()) {
           Product product = item.getProduct();
           if (product.getStock() < item.getQuantity()) {
               throw new RuntimeException("Stock insuficiente: " + product.getName());
           }
       }
       
       // 3. Crear orden
       Order order = new Order();
       order.setUser(cart.getUser());
       order.setOrderNumber("ORD-" + System.currentTimeMillis());
       order.setTotal(cart.calculateTotal());
       order.setShippingAddress(request.getAddress());
       
       // 4. Crear items de orden
       for (CartItem cartItem : cart.getItems()) {
           OrderItem orderItem = new OrderItem();
           orderItem.setOrder(order);
           orderItem.setProduct(cartItem.getProduct());
           orderItem.setQuantity(cartItem.getQuantity());
           orderItem.setPrice(cartItem.getProduct().getPrice());
           order.getItems().add(orderItem);
       }
       
       // 5. Reducir stock
       for (CartItem item : cart.getItems()) {
           Product product = item.getProduct();
           product.setStock(product.getStock() - item.getQuantity());
           productRepository.save(product);
       }
       
       // 6. Guardar orden
       Order saved = orderRepository.save(order);
       
       // 7. Limpiar carrito
       cart.getItems().clear();
       cartRepository.save(cart);
       
       // 8. Convertir a DTO
       return convertToDTO(saved);
   }
   
4. Repositories (múltiples)
   ↓
   - cartRepository.findById()
   - productRepository.save() (múltiples veces)
   - orderRepository.save()
   - cartRepository.save()
   
5. Base de Datos (transacción ACID)
   ↓
   BEGIN TRANSACTION;
   - INSERT INTO orders ...
   - INSERT INTO order_items ...
   - UPDATE products SET stock = stock - X ...
   - DELETE FROM cart_items ...
   COMMIT;
```

---

## ✅ Ventajas de la Arquitectura en Capas

### 1. Separación de Responsabilidades

```java
// ❌ MAL - Todo en el Controller
@RestController
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping("/products")
    public List<Product> getProducts() {
        return productRepository.findAll();  // Expone Entity directamente
    }
    
    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        if (productRepository.existsByName(product.getName())) {
            throw new RuntimeException("Ya existe");  // Validación en Controller
        }
        return productRepository.save(product);
    }
}

// ✅ BIEN - Separado en capas
@RestController
public class ProductController {
    @Autowired
    private ProductService productService;
    
    @GetMapping("/products")
    public List<ProductDTO> getProducts() {
        return productService.getAllProducts();  // Devuelve DTOs
    }
    
    @PostMapping("/products")
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) {
        return productService.createProduct(productDTO);  // Validación en Service
    }
}
```

### 2. Reutilización de Código

```java
// Service puede ser usado por múltiples Controllers
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public List<ProductDTO> getProducts() {
        return productService.getAllProducts();
    }
}

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    @Autowired
    private ProductService productService;  // Mismo Service
    
    @GetMapping("/low-stock")
    public List<ProductDTO> getLowStock() {
        return productService.getLowStockProducts();
    }
}
```

### 3. Testabilidad

```java
// Fácil de testear Service sin HTTP
@Test
public void testCreateProduct() {
    // Mock repository
    ProductRepository mockRepo = mock(ProductRepository.class);
    when(mockRepo.existsByName("Test")).thenReturn(false);
    
    // Service con mock
    ProductService service = new ProductService(mockRepo);
    
    // Test
    ProductDTO dto = new ProductDTO();
    dto.setName("Test");
    
    ProductDTO result = service.createProduct(dto);
    assertNotNull(result);
}
```

### 4. Mantenibilidad

```java
// Cambiar lógica de negocio sin tocar Controller
@Service
public class ProductService {
    
    public List<ProductDTO> getAllProducts() {
        // Antes: findAll()
        // Ahora: solo productos disponibles
        return productRepository.findByAvailableTrue()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}

// Controller NO cambia
@GetMapping
public List<ProductDTO> getProducts() {
    return productService.getAllProducts();  // Misma llamada
}
```

### 5. Transacciones

```java
// Service puede manejar transacciones complejas
@Service
public class OrderService {
    
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        // 1. Crear orden
        // 2. Reducir stock
        // 3. Limpiar carrito
        // Si algo falla, TODO se revierte ✅
    }
}
```

---

## 🎯 Ejemplo Completo: BabyCash

### Product Entity

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Boolean available;
    
    // getters y setters
}
```

### Product DTO

```java
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Boolean available;
    
    // getters y setters
}
```

### Product Repository

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByAvailableTrue();
    boolean existsByName(String name);
    List<Product> findByStockLessThan(Integer threshold);
}
```

### Product Service

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<ProductDTO> getAllProducts() {
        return productRepository.findByAvailableTrue()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertToDTO(product);
    }
    
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productRepository.existsByName(productDTO.getName())) {
            throw new RuntimeException("Producto ya existe");
        }
        
        Product product = convertToEntity(productDTO);
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }
    
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setAvailable(productDTO.getAvailable());
        
        Product updated = productRepository.save(product);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado");
        }
        productRepository.deleteById(id);
    }
    
    @Transactional
    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
    
    public List<ProductDTO> getLowStockProducts() {
        return productRepository.findByStockLessThan(10)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setAvailable(product.getAvailable());
        return dto;
    }
    
    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setAvailable(dto.getAvailable());
        return product;
    }
}
```

### Product Controller

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        ProductDTO created = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
        @PathVariable Long id,
        @RequestBody @Valid ProductDTO productDTO
    ) {
        ProductDTO updated = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        List<ProductDTO> products = productService.getLowStockProducts();
        return ResponseEntity.ok(products);
    }
}
```

---

## 📋 Resumen

| Capa | Responsabilidad | Anotación | Ejemplo |
|------|----------------|-----------|---------|
| **Controller** | Manejar HTTP | `@RestController` | `ProductController` |
| **Service** | Lógica de negocio | `@Service` | `ProductService` |
| **Repository** | Acceso a datos | `@Repository` | `ProductRepository` |
| **Entity** | Modelo de datos | `@Entity` | `Product` |
| **DTO** | Transferencia de datos | - | `ProductDTO` |

---

**Última actualización**: Octubre 2025
