# 💉 INYECCIÓN DE DEPENDENCIAS

## 🎯 ¿Qué es Dependency Injection (DI)?

**Explicación Simple:**
En vez de crear objetos manualmente (`new`), Spring Boot los **crea y te los entrega** automáticamente.

**Explicación Técnica:**
Es un patrón de diseño donde las dependencias de una clase son **provistas desde afuera** en vez de ser creadas internamente.

---

## ❌ Sin Inyección de Dependencias

```java
@Service
public class ProductService {
    
    // ❌ MAL - Crear dependencia manualmente
    private ProductRepository productRepository = new ProductRepository();
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
```

**Problemas:**
- ❌ Acoplamiento fuerte
- ❌ Difícil de testear
- ❌ No puedes cambiar la implementación fácilmente

---

## ✅ Con Inyección de Dependencias

```java
@Service
public class ProductService {
    
    // ✅ BIEN - Spring inyecta la dependencia
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
```

**Ventajas:**
- ✅ Bajo acoplamiento
- ✅ Fácil de testear
- ✅ Flexible
- ✅ Spring gestiona el ciclo de vida

---

## 🔧 @Autowired

### ¿Qué hace?

Le dice a Spring que **inyecte** una dependencia automáticamente.

---

## 📝 3 Formas de Inyectar Dependencias

### 1. Constructor Injection (✅ RECOMENDADO)

```java
@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final EmailService emailService;
    
    // Constructor con todas las dependencias
    public OrderService(
        OrderRepository orderRepository,
        ProductService productService,
        EmailService emailService
    ) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.emailService = emailService;
    }
}
```

**Ventajas:**
- ✅ Dependencias **inmutables** (final)
- ✅ Fácil de testear
- ✅ No necesitas `@Autowired` (Spring lo detecta automáticamente)
- ✅ Falla rápido si falta una dependencia

### 2. Field Injection (⚠️ NO RECOMENDADO)

```java
@Service
public class ProductService {
    
    @Autowired  // Inyección directa en el campo
    private ProductRepository productRepository;
    
    @Autowired
    private EmailService emailService;
}
```

**Problemas:**
- ❌ Difícil de testear (necesitas reflexión)
- ❌ No puedes usar `final`
- ❌ Oculta dependencias

**Cuándo usar:** Nunca en producción, solo en demos rápidas.

### 3. Setter Injection (⚠️ RARA VEZ)

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

**Cuándo usar:** Dependencias opcionales (raro).

---

## 📊 Comparación

| Método | Ventajas | Desventajas | Recomendación |
|--------|----------|-------------|---------------|
| **Constructor** | Inmutable, testeable, claro | Más líneas | ✅ USAR |
| **Field** | Menos líneas | Difícil de testear | ❌ EVITAR |
| **Setter** | Dependencias opcionales | Mutable | ⚠️ RARO |

---

## 🎓 Ejemplos del Proyecto

### AuthService.java

```java
@Service
public class AuthService {
    
    // Dependencias como final
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    
    // Constructor Injection (Spring inyecta automáticamente)
    public AuthService(
        UserRepository userRepository,
        CartRepository cartRepository,
        PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil,
        EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }
    
    public AuthResponse register(RegisterRequest request) {
        // Usar las dependencias inyectadas
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        user = userRepository.save(user);
        
        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
        
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        return buildAuthResponse(user, token);
    }
}
```

### OrderService.java

```java
@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final LoyaltyService loyaltyService;
    
    public OrderService(
        OrderRepository orderRepository,
        CartRepository cartRepository,
        ProductRepository productRepository,
        PaymentService paymentService,
        EmailService emailService,
        LoyaltyService loyaltyService
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.paymentService = paymentService;
        this.emailService = emailService;
        this.loyaltyService = loyaltyService;
    }
    
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String userEmail) {
        // Todas las dependencias ya están inyectadas
        Cart cart = cartRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new NotFoundException("Carrito no encontrado"));
        
        // ... crear orden, procesar pago, etc.
        
        return response;
    }
}
```

### ProductController.java

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    // Constructor Injection
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}
```

---

## 🔄 ¿Cómo Funciona Internamente?

```
1. Spring Boot inicia
   ↓
2. Escanea @Component, @Service, @Repository, @Controller
   ↓
3. Detecta ProductRepository (bean)
   ↓
4. Detecta ProductService (bean)
   - Constructor requiere ProductRepository
   ↓
5. Spring crea ProductRepository primero
   ↓
6. Spring crea ProductService
   - Inyecta ProductRepository en el constructor
   ↓
7. Detecta ProductController (bean)
   - Constructor requiere ProductService
   ↓
8. Spring crea ProductController
   - Inyecta ProductService en el constructor
   ↓
9. Todos los beans listos
```

---

## 🧪 Testing con Constructor Injection

### Ventaja: Fácil de Testear

```java
// Test unitario
class ProductServiceTest {
    
    @Test
    void testGetAllProducts() {
        // 1. Mock del repository (no necesitas Spring)
        ProductRepository mockRepository = mock(ProductRepository.class);
        
        // 2. Crear service manualmente (constructor injection lo permite)
        ProductService service = new ProductService(mockRepository);
        
        // 3. Configurar mock
        when(mockRepository.findAll()).thenReturn(List.of(
            new Product("Pañales", new BigDecimal("45000"))
        ));
        
        // 4. Ejecutar método
        List<Product> products = service.getAllProducts();
        
        // 5. Verificar
        assertEquals(1, products.size());
        assertEquals("Pañales", products.get(0).getName());
    }
}
```

### Con Field Injection (Difícil)

```java
// ❌ Con field injection necesitas reflexión o Spring Test Context
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
}

// Test complicado
@SpringBootTest  // Necesitas levantar Spring completo (lento)
class ProductServiceTest {
    @Autowired
    private ProductService service;
    
    @MockBean
    private ProductRepository repository;
    
    // ...
}
```

---

## 💡 @Autowired Opcional (desde Spring 4.3)

Si la clase tiene **un solo constructor**, `@Autowired` es **opcional**.

```java
// Estas 2 formas son equivalentes:

// Con @Autowired (opcional)
@Service
public class ProductService {
    private final ProductRepository productRepository;
    
    @Autowired  // Opcional
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}

// Sin @Autowired (Spring lo detecta automáticamente)
@Service
public class ProductService {
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}
```

---

## ⚠️ Errores Comunes

### 1. Dependencia Circular

```java
// ❌ CIRCULAR DEPENDENCY
@Service
public class ServiceA {
    private final ServiceB serviceB;
    
    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}

@Service
public class ServiceB {
    private final ServiceA serviceA;  // ❌ A depende de B y B depende de A
    
    public ServiceB(ServiceA serviceA) {
        this.serviceA = serviceA;
    }
}
```

**Solución:** Refactorizar para romper el ciclo (crear un ServiceC que ambos usen).

### 2. Bean No Encontrado

```java
// ❌ ERROR: No qualifying bean of type 'ProductRepository'

// Causa: Falta @Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}

// ✅ Solución: Agregar @Repository
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

---

## 📋 Resumen

| Concepto | Definición | Ejemplo |
|----------|------------|---------|
| **Dependency Injection** | Spring crea y entrega objetos | Constructor injection |
| **@Autowired** | Marca dependencia para inyectar | Opcional en constructor único |
| **Constructor Injection** | ✅ Mejor forma de inyectar | `public Service(Repo repo)` |
| **Field Injection** | ❌ No recomendado | `@Autowired private Repo repo;` |
| **Bean** | Objeto gestionado por Spring | `@Service`, `@Repository` |

---

**Última actualización**: Octubre 2025
