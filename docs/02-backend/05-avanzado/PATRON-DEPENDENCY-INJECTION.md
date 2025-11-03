# PATRÓN DEPENDENCY INJECTION (Inyección de Dependencias)

## 🎯 Definición

**Dependency Injection (DI)** es un patrón donde los **objetos reciben sus dependencias desde afuera** en lugar de crearlas internamente.

Es como un **restaurante**: el chef NO fabrica sus propios cuchillos, el restaurante se los proporciona.

---

## ❓ ¿Para Qué Sirve?

### Sin Dependency Injection (Problema)

```java
❌ MAL: Clase crea sus propias dependencias
public class OrderService {
    
    // ❌ Crea su propio repository (acoplamiento fuerte)
    private OrderRepository orderRepository = new JpaOrderRepository();
    
    // ❌ Crea su propio email sender
    private EmailSender emailSender = new GmailEmailSender();
    
    public void createOrder(Order order) {
        orderRepository.save(order);
        emailSender.send(order.getUserEmail(), "Order created!");
    }
}
```

**Problemas:**
- ❌ **Acoplamiento fuerte**: OrderService depende de implementaciones concretas
- ❌ **Difícil de testear**: No puedo mockear dependencies
- ❌ **Difícil de cambiar**: Si quiero MockEmailSender, debo modificar código
- ❌ **Violates Dependency Inversion**: Depende de concretos, no abstracciones

---

## ✅ Con Dependency Injection

```java
// ✅ BIEN: Dependencies inyectadas desde afuera
public class OrderService {
    
    // ✅ Dependencias declaradas (interfaces, no implementaciones)
    private final OrderRepository orderRepository;
    private final EmailSender emailSender;
    
    // ✅ Constructor recibe dependencias
    public OrderService(OrderRepository orderRepository, EmailSender emailSender) {
        this.orderRepository = orderRepository;
        this.emailSender = emailSender;
    }
    
    public void createOrder(Order order) {
        orderRepository.save(order);
        emailSender.send(order.getUserEmail(), "Order created!");
    }
}

// ✅ Alguien externo crea las dependencias
OrderRepository repo = new JpaOrderRepository();
EmailSender sender = new GmailEmailSender();
OrderService service = new OrderService(repo, sender);  // ✅ Inyección
```

**Ventajas:**
- ✅ **Desacoplamiento**: OrderService depende de interfaces
- ✅ **Fácil de testear**: Puedo inyectar mocks
- ✅ **Fácil de cambiar**: Puedo inyectar implementaciones diferentes
- ✅ **Cumple Dependency Inversion**: Depende de abstracciones

---

## 🏗️ Tipos de Dependency Injection

### 1️⃣ **Constructor Injection** (Recomendado)

```java
// ✅ Constructor Injection
@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    // ✅ Constructor recibe dependencias
    public ProductService(
        ProductRepository productRepository,
        CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
}

// ✅ Con Lombok @RequiredArgsConstructor (genera constructor automáticamente)
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    // ✅ Constructor generado automáticamente
}
```

**Ventajas:**
- ✅ Dependencias inmutables (final)
- ✅ Fácil de testear
- ✅ Falla rápido si falta dependencia

---

### 2️⃣ **Setter Injection**

```java
@Service
public class ProductService {
    
    private ProductRepository productRepository;
    
    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}
```

**Desventajas:**
- ❌ Dependencias mutables
- ❌ Puede ser null si no se llama el setter

---

### 3️⃣ **Field Injection** (No recomendado)

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;  // ❌ Field injection
}
```

**Desventajas:**
- ❌ No se puede usar final
- ❌ Difícil de testear (necesita Spring context)
- ❌ Rompe encapsulación

---

## 🏗️ Dependency Injection en Spring

Spring maneja **Inversion of Control (IoC) Container**:

```java
// ✅ Spring crea y gestiona beans automáticamente
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmailSender emailSender;
    
    // ✅ Spring inyecta automáticamente al crear el bean
}

// ✅ Spring encuentra implementaciones automáticamente
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}

@Service
public class GmailEmailSender implements EmailSender {
    // ✅ Spring crea bean automáticamente
}
```

---

## 📊 IoC Container de Spring

```
┌────────────────────────────────────────┐
│         Spring IoC Container           │
│                                        │
│  ┌──────────────┐  ┌──────────────┐  │
│  │ OrderService │  │ProductService│  │
│  └──────────────┘  └──────────────┘  │
│         ▲                 ▲           │
│         │                 │           │
│  ┌──────┴───────┐  ┌─────┴──────┐   │
│  │OrderRepository│  │ProductRepo │   │
│  └──────────────┘  └────────────┘   │
│                                        │
│  Spring crea y gestiona todos los     │
│  beans automáticamente                │
└────────────────────────────────────────┘
```

---

## 🏗️ Dependency Injection en Baby Cash

### ✅ ProductService

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    // ✅ Dependencies inyectadas por constructor
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    
    public ProductResponse createProduct(CreateProductRequest request) {
        // ✅ Usa dependencies sin saber cómo se crearon
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        Product product = mapToEntity(request);
        product.setCategory(category);
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created: {}", savedProduct.getId());
        
        return mapToResponse(savedProduct);
    }
}
```

---

### ✅ OrderService

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    // ✅ Dependencies inyectadas por constructor
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // ✅ Usa dependencies
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);
        
        // Crear items
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(product.getPrice());
            order.addItem(item);
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // ✅ Publica evento (dependency inyectada)
        eventPublisher.publishEvent(new OrderCreatedEvent(savedOrder));
        
        return mapToResponse(savedOrder);
    }
}
```

---

### ✅ AuthService

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    // ✅ Dependencies inyectadas por constructor
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        // ✅ Usa dependencies
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }
    
    public LoginResponse login(LoginRequest request) {
        // ✅ Usa dependencies
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        String token = jwtTokenProvider.generateToken(authentication);
        
        return new LoginResponse(token);
    }
}
```

---

## 🧪 Testing con Dependency Injection

```java
// ✅ Test fácil con DI (mocks inyectados)
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    void createProduct_ShouldReturnProduct() {
        // Given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Baby Bottle");
        request.setCategoryId(1L);
        
        Category category = new Category();
        category.setId(1L);
        
        Product product = new Product();
        product.setId(1L);
        product.setName("Baby Bottle");
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        // When
        ProductResponse response = productService.createProduct(request);
        
        // Then
        assertNotNull(response);
        assertEquals("Baby Bottle", response.getName());
        verify(productRepository).save(any(Product.class));
    }
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es Dependency Injection?"**

> "Es un patrón donde los objetos reciben sus dependencias desde afuera en lugar de crearlas internamente. Es como un restaurante que proporciona herramientas al chef en lugar de que el chef las fabrique. En Spring, uso constructor injection con `@RequiredArgsConstructor` de Lombok para inyectar repositories, services, etc."

---

**2. "¿Por qué usar Dependency Injection?"**

> "Por desacoplamiento y testabilidad:
> - **Sin DI**: `OrderService` crea su propio `new JpaOrderRepository()` → acoplamiento fuerte, difícil de testear
> - **Con DI**: Spring inyecta `OrderRepository` → desacoplado, puedo inyectar mocks en tests
> 
> Además, cumple Dependency Inversion (dependo de interfaces, no implementaciones)."

---

**3. "¿Cómo funciona DI en Baby Cash?"**

> "Uso Spring IoC Container:
> 1. Declaro dependencies en constructor: `private final ProductRepository productRepository;`
> 2. Uso `@RequiredArgsConstructor` de Lombok para generar constructor
> 3. Spring crea beans automáticamente (`@Service`, `@Repository`)
> 4. Spring inyecta dependencies al crear cada bean
> 
> Yo NO creo objetos con `new`, Spring lo hace por mí."

---

**4. "¿Qué tipo de DI usas?"**

> "Constructor Injection porque:
> - ✅ Dependencies son `final` (inmutables)
> - ✅ Fácil de testear (paso mocks en constructor)
> - ✅ Falla rápido si falta dependency
> 
> `@RequiredArgsConstructor` de Lombok genera el constructor automáticamente para todos los campos `final`."

---

## 📝 Checklist de Dependency Injection

```
✅ Constructor Injection (recomendado)
✅ Dependencies declaradas como final
✅ @RequiredArgsConstructor de Lombok
✅ Dependencias como interfaces (no implementaciones)
✅ Spring crea y gestiona beans (@Service, @Repository)
✅ No usar new para crear dependencies
```

---

## 🏆 Ventajas y Desventajas

### ✅ Ventajas

```
✅ Desacoplamiento (depende de interfaces)
✅ Fácil de testear (inyectar mocks)
✅ Fácil de cambiar implementaciones
✅ Cumple Dependency Inversion
✅ Spring gestiona ciclo de vida
✅ Singleton automático
```

---

### ❌ Desventajas

```
❌ Requiere framework (Spring) o configuración manual
❌ Curva de aprendizaje
❌ "Magia" de Spring puede confundir
```

---

## 🚀 Conclusión

**Dependency Injection:**
- ✅ Objetos reciben dependencies, no las crean
- ✅ Spring IoC Container gestiona todo
- ✅ Constructor Injection + @RequiredArgsConstructor

**En Baby Cash, TODOS los services usan DI con constructor injection.**

---

**Ahora lee:** `PATRON-MVC.md` para el siguiente patrón. 🚀
