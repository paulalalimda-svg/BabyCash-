# PATRÓN MVC (Model-View-Controller)

## 🎯 Definición

**MVC** es un patrón arquitectónico que separa la aplicación en **3 capas**:
- **Model**: Datos y lógica de negocio
- **View**: Presentación (UI)
- **Controller**: Intermediario entre Model y View

Es como un **restaurante**:
- **Chef (Model)**: Prepara comida (lógica de negocio)
- **Mesero (Controller)**: Recibe pedido, comunica con chef, sirve comida
- **Cliente (View)**: Ve menú, hace pedido, recibe comida

---

## ❓ ¿Para Qué Sirve?

### Sin MVC (Problema)

```java
❌ MAL: Todo en una clase
public class UserServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        // ❌ Lógica de negocio en servlet
        Connection conn = DriverManager.getConnection("jdbc:...");
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
        stmt.setLong(1, userId);
        ResultSet rs = stmt.executeQuery();
        
        User user = new User();
        if (rs.next()) {
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
        }
        
        // ❌ HTML en código Java
        resp.getWriter().write("<html><body>");
        resp.getWriter().write("<h1>User: " + user.getName() + "</h1>");
        resp.getWriter().write("</body></html>");
    }
}
```

**Problemas:**
- ❌ Servlet hace TODO (SQL, lógica, HTML)
- ❌ Difícil de mantener
- ❌ Difícil de testear
- ❌ No se puede reutilizar lógica

---

## ✅ Con MVC

```
┌──────────────────────────────────────────────┐
│              CLIENT (Browser)                │
└───────────────────┬──────────────────────────┘
                    │ HTTP Request
                    ▼
┌──────────────────────────────────────────────┐
│              CONTROLLER                      │
│  @RestController                             │
│  - Recibe request                            │
│  - Valida datos                              │
│  - Llama service                             │
│  - Devuelve response                         │
└───────────────────┬──────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────┐
│              MODEL/SERVICE                   │
│  @Service                                    │
│  - Lógica de negocio                         │
│  - Transacciones                             │
│  - Llama repository                          │
└───────────────────┬──────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────┐
│              REPOSITORY                      │
│  @Repository                                 │
│  - Acceso a datos                            │
│  - SQL queries                               │
└──────────────────────────────────────────────┘
```

---

## 🏗️ MVC en Spring Boot

### ✅ Controller

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;  // ✅ Delega a Service
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody CreateProductRequest request
    ) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
}
```

**Responsabilidades:**
- ✅ Recibir HTTP request
- ✅ Validar datos (@Valid)
- ✅ Llamar service
- ✅ Devolver HTTP response

---

### ✅ Service (Model)

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return mapToResponse(product);
    }
    
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        Product product = mapToEntity(request);
        product.setCategory(category);
        product.setEnabled(true);
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created: {}", savedProduct.getId());
        
        return mapToResponse(savedProduct);
    }
}
```

**Responsabilidades:**
- ✅ Lógica de negocio
- ✅ Transacciones
- ✅ Validaciones complejas
- ✅ Mapeo Entity ↔ DTO

---

### ✅ Repository (Model)

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByEnabled(Boolean enabled);
    
    Optional<Product> findBySlug(String slug);
    
    List<Product> findByCategory(Category category);
    
    @Query("SELECT p FROM Product p WHERE p.enabled = true AND p.stock > 0")
    List<Product> findAvailableProducts();
}
```

**Responsabilidades:**
- ✅ Acceso a datos
- ✅ SQL queries
- ✅ CRUD operations

---

### ✅ Entity (Model)

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
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discountPrice;
    
    @Column(nullable = false)
    private Integer stock;
    
    private Boolean enabled;
    
    private String imageUrl;
    
    private String slug;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

**Responsabilidades:**
- ✅ Representar tabla de DB
- ✅ Relaciones entre entidades

---

## 🏗️ Arquitectura en Capas de Baby Cash

```
┌──────────────────────────────────────────────┐
│         FRONTEND (React)                     │
│  - Components                                │
│  - Pages                                     │
│  - API calls                                 │
└───────────────────┬──────────────────────────┘
                    │ HTTP
                    ▼
┌──────────────────────────────────────────────┐
│         CONTROLLER LAYER                     │
│  @RestController                             │
│  - ProductController                         │
│  - OrderController                           │
│  - UserController                            │
│  - AuthController                            │
└───────────────────┬──────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────┐
│         SERVICE LAYER                        │
│  @Service                                    │
│  - ProductService                            │
│  - OrderService                              │
│  - UserService                               │
│  - AuthService                               │
└───────────────────┬──────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────┐
│         REPOSITORY LAYER                     │
│  @Repository                                 │
│  - ProductRepository                         │
│  - OrderRepository                           │
│  - UserRepository                            │
└───────────────────┬──────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────┐
│         DATABASE (PostgreSQL)                │
│  - products                                  │
│  - orders                                    │
│  - users                                     │
└──────────────────────────────────────────────┘
```

---

## 🏗️ Ejemplo Completo: Crear Producto

### 1️⃣ Frontend (View)

```tsx
// CreateProductForm.tsx
const handleSubmit = async (e: FormEvent) => {
  e.preventDefault();
  
  const request = {
    name: productName,
    description: productDescription,
    price: productPrice,
    categoryId: selectedCategoryId,
  };
  
  // ✅ Llama API
  const response = await axios.post('/api/products', request);
  console.log('Product created:', response.data);
};
```

---

### 2️⃣ Controller

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody CreateProductRequest request
    ) {
        // ✅ Valida request
        // ✅ Llama service
        ProductResponse product = productService.createProduct(request);
        
        // ✅ Devuelve response
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
}
```

---

### 3️⃣ Service (Model)

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // ✅ Lógica de negocio
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        // ✅ Mapeo DTO → Entity
        Product product = Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .discountPrice(request.getDiscountPrice())
            .stock(request.getStock())
            .imageUrl(request.getImageUrl())
            .category(category)
            .enabled(true)
            .slug(generateSlug(request.getName()))
            .build();
        
        // ✅ Llama repository
        Product savedProduct = productRepository.save(product);
        log.info("Product created: {}", savedProduct.getId());
        
        // ✅ Mapeo Entity → DTO
        return mapToResponse(savedProduct);
    }
}
```

---

### 4️⃣ Repository (Model)

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // ✅ Spring genera implementación automáticamente
}
```

---

### 5️⃣ Database

```sql
-- ✅ PostgreSQL ejecuta INSERT
INSERT INTO products (name, description, price, category_id, enabled, slug)
VALUES ('Baby Bottle', 'Glass bottle', 15.99, 1, true, 'baby-bottle');
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es MVC?"**

> "MVC (Model-View-Controller) es un patrón arquitectónico que separa la aplicación en 3 capas:
> - **Controller**: Recibe HTTP requests, valida, llama service, devuelve response
> - **Model/Service**: Lógica de negocio, transacciones, mapeo DTO ↔ Entity
> - **Repository**: Acceso a datos, SQL queries
> - **View**: Frontend (React en Baby Cash)
> 
> Esta separación mejora mantenibilidad y testabilidad."

---

**2. "¿Cómo funciona MVC en Baby Cash?"**

> "Baby Cash usa arquitectura en capas:
> 1. **Frontend (React)**: Usuario hace click, llama API
> 2. **Controller**: Recibe request, valida, llama service
> 3. **Service**: Lógica de negocio, llama repository
> 4. **Repository**: Acceso a datos, SQL
> 5. **Database (PostgreSQL)**: Almacena datos
> 
> Ejemplo: Crear producto → Frontend POST → ProductController → ProductService → ProductRepository → DB"

---

**3. "¿Qué hace cada capa?"**

> "- **Controller**: Recibir request, validar, llamar service, devolver response. NO tiene lógica de negocio.
> - **Service**: Lógica de negocio, transacciones, validaciones complejas, mapeo DTO ↔ Entity
> - **Repository**: CRUD operations, SQL queries. NO tiene lógica de negocio.
> 
> Esto es Single Responsibility: cada capa tiene una responsabilidad específica."

---

**4. "¿Por qué separar en capas?"**

> "Por mantenibilidad y testabilidad:
> - **Mantenibilidad**: Si cambio DB, solo cambio repository. Si cambio validaciones, solo cambio service.
> - **Testabilidad**: Puedo testear service sin controller ni DB (usando mocks)
> - **Reutilización**: Service puede ser usado por múltiples controllers
> - **Single Responsibility**: Cada capa tiene una responsabilidad"

---

## 📝 Checklist de MVC

```
✅ Controller recibe requests, valida, llama service
✅ Service contiene lógica de negocio
✅ Repository accede a datos
✅ DTO para transferir datos entre capas
✅ Entity representa tabla de DB
✅ Separación clara de responsabilidades
```

---

## 🏆 Ventajas y Desventajas

### ✅ Ventajas

```
✅ Separación de responsabilidades
✅ Fácil de mantener
✅ Fácil de testear
✅ Reutilización de lógica
✅ Escalabilidad
✅ Múltiples desarrolladores pueden trabajar en paralelo
```

---

### ❌ Desventajas

```
❌ Más clases (puede parecer overkill para apps simples)
❌ Curva de aprendizaje
```

---

## 🚀 Conclusión

**MVC:**
- ✅ Separa aplicación en capas (Controller, Service, Repository)
- ✅ Cada capa tiene responsabilidad específica
- ✅ Mejora mantenibilidad y testabilidad

**Baby Cash usa arquitectura en capas con MVC.**

---

**Ahora lee:** `PATRONES-EN-BABYCASH.md` para ver todos los patrones juntos. 🚀
