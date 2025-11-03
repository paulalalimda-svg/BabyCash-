# D - DEPENDENCY INVERSION PRINCIPLE (Principio de Inversión de Dependencias)

## 📚 Definición

> **"1. Los módulos de alto nivel no deben depender de módulos de bajo nivel. Ambos deben depender de abstracciones.**
> 
> **2. Las abstracciones no deben depender de detalles. Los detalles deben depender de abstracciones."**
> 
> — Robert C. Martin (Uncle Bob)

---

## 🤔 ¿Qué Significa?

### Para Principiantes (Analogía)

Imagina que tienes un **cargador de celular**:

**❌ SIN Dependency Inversion:**
- Tu celular tiene un cable soldado permanentemente
- Solo funciona con ESE cable específico
- Si el cable se daña → celular inútil
- Si quieres cambiar de cable → imposible

**✅ CON Dependency Inversion:**
- Tu celular tiene un **puerto USB-C** (abstracción)
- Puedes usar CUALQUIER cable USB-C
- Si un cable se daña → usas otro
- Puedes cambiar cables sin cambiar el celular
- **El celular depende del PUERTO (abstracción), no del CABLE (detalle)**

### Para Programadores

```
NO dependas de clases concretas.
Depende de INTERFACES o CLASES ABSTRACTAS.
```

**Ejemplo Visual:**

```
❌ MAL:
OrderService → PostgreSQLOrderRepository (clase concreta)

✅ BIEN:
OrderService → IOrderRepository (interfaz)
                     ↑
                     |
           PostgreSQLOrderRepository (implementación)
```

---

## ❌ Violando el Principio

### Ejemplo 1: Dependencia Directa

```java
// ❌ VIOLACIÓN: Dependencia de clase concreta

// Clase de bajo nivel (detalle de implementación)
public class MySQLDatabase {
    public void saveUser(User user) {
        System.out.println("Guardando en MySQL: " + user.getEmail());
        // Conexión directa a MySQL
        Connection conn = DriverManager.getConnection("jdbc:mysql://...");
        // ... código específico de MySQL
    }
    
    public User getUser(Long id) {
        System.out.println("Obteniendo de MySQL: " + id);
        // ... código específico de MySQL
        return new User();
    }
}

// Clase de alto nivel
public class UserService {
    // ❌ Dependencia DIRECTA de clase concreta
    private MySQLDatabase database = new MySQLDatabase();
    
    public void registerUser(User user) {
        // Validaciones
        database.saveUser(user);  // ❌ Acoplado a MySQL
    }
    
    public User getUserById(Long id) {
        return database.getUser(id);  // ❌ Acoplado a MySQL
    }
}
```

### Problemas:

1. **Acoplamiento fuerte**: `UserService` está atado a MySQL
2. **No puedes cambiar a PostgreSQL** sin modificar `UserService`
3. **No puedes testear** sin una BD real
4. **No puedes reutilizar** `UserService` en otro proyecto con otra BD
5. **Difícil mantener**: Cambios en MySQL rompen UserService

---

## ✅ Aplicando el Principio

### Solución: Invertir la Dependencia con Interfaces

```java
// ✅ CORRECTO: Abstracción (interfaz)

public interface UserRepository {
    void save(User user);
    User findById(Long id);
    List<User> findAll();
    void delete(Long id);
}

// Implementación concreta 1: MySQL
public class MySQLUserRepository implements UserRepository {
    
    @Override
    public void save(User user) {
        System.out.println("Guardando en MySQL: " + user.getEmail());
        // Código específico de MySQL
    }
    
    @Override
    public User findById(Long id) {
        System.out.println("Buscando en MySQL: " + id);
        // Código específico de MySQL
        return new User();
    }
    
    @Override
    public List<User> findAll() {
        // Código específico de MySQL
        return new ArrayList<>();
    }
    
    @Override
    public void delete(Long id) {
        // Código específico de MySQL
    }
}

// Implementación concreta 2: PostgreSQL
public class PostgreSQLUserRepository implements UserRepository {
    
    @Override
    public void save(User user) {
        System.out.println("Guardando en PostgreSQL: " + user.getEmail());
        // Código específico de PostgreSQL
    }
    
    @Override
    public User findById(Long id) {
        System.out.println("Buscando en PostgreSQL: " + id);
        // Código específico de PostgreSQL
        return new User();
    }
    
    @Override
    public List<User> findAll() {
        // Código específico de PostgreSQL
        return new ArrayList<>();
    }
    
    @Override
    public void delete(Long id) {
        // Código específico de PostgreSQL
    }
}

// Implementación concreta 3: En Memoria (para tests)
public class InMemoryUserRepository implements UserRepository {
    private Map<Long, User> users = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public void save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
    }
    
    @Override
    public User findById(Long id) {
        return users.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}

// ✅ Clase de alto nivel depende de ABSTRACCIÓN
@Service
public class UserService {
    
    // ✅ Depende de la INTERFAZ, no de la implementación concreta
    private final UserRepository userRepository;
    
    // Constructor Injection (recomendado)
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public void registerUser(User user) {
        // Validaciones
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Email requerido");
        }
        
        // ✅ Guarda usando la abstracción
        // NO sabe si es MySQL, PostgreSQL, o en memoria
        userRepository.save(user);
    }
    
    public User getUserById(Long id) {
        // ✅ Obtiene usando la abstracción
        return userRepository.findById(id);
    }
}

// Configuración de Spring (decide qué implementación usar)
@Configuration
public class AppConfig {
    
    @Bean
    public UserRepository userRepository() {
        // ✅ Aquí decides qué implementación usar
        // Cambias SOLO esta línea para cambiar de BD
        return new PostgreSQLUserRepository();
        // O: return new MySQLUserRepository();
        // O: return new InMemoryUserRepository();
    }
}
```

### Ventajas:

1. **Desacoplado**: `UserService` NO conoce la implementación
2. **Flexible**: Cambias de BD modificando SOLO la configuración
3. **Testeable**: Usas `InMemoryUserRepository` en tests
4. **Reutilizable**: `UserService` funciona con cualquier implementación
5. **Mantenible**: Cambios en BD NO afectan UserService

---

## 🏢 Ejemplos Reales de Baby Cash

### Ejemplo 1: Repositorios con Spring Data JPA

```java
// ✅ CORRECTO: Baby Cash usa interfaces

// Abstracción
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByActive(boolean active);
    Optional<Product> findBySlug(String slug);
}

// Servicio depende de la ABSTRACCIÓN
@Service
@RequiredArgsConstructor
public class ProductService {
    
    // ✅ Inyección por constructor
    private final ProductRepository productRepository;
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}

// Spring JPA crea la implementación automáticamente
// Puedes cambiar de Hibernate a EclipseLink sin tocar ProductService
```

---

### Ejemplo 2: Servicio de Email

```java
// ✅ Abstracción del servicio de email

public interface EmailSender {
    void sendEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String htmlContent);
}

// Implementación 1: Gmail
@Service
@Profile("prod")
public class GmailEmailSender implements EmailSender {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
    
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        // Implementación con MimeMessage
    }
}

// Implementación 2: Mock para desarrollo
@Service
@Profile("dev")
public class MockEmailSender implements EmailSender {
    
    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("=== EMAIL MOCK ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("==================");
    }
    
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        System.out.println("=== HTML EMAIL MOCK ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("HTML: " + htmlContent);
        System.out.println("======================");
    }
}

// Servicio que usa el email sender
@Service
@RequiredArgsConstructor
public class OrderService {
    
    // ✅ Depende de la abstracción
    private final EmailSender emailSender;
    
    public void createOrder(Order order) {
        // Crear orden
        order = saveOrder(order);
        
        // ✅ Enviar confirmación (no sabe si es Gmail o Mock)
        emailSender.sendEmail(
            order.getUser().getEmail(),
            "Confirmación de Orden #" + order.getId(),
            "Tu orden ha sido creada exitosamente"
        );
    }
}

// En desarrollo: usa MockEmailSender (no envía emails reales)
// En producción: usa GmailEmailSender (envía emails reales)
```

---

### Ejemplo 3: Procesamiento de Pagos

```java
// ✅ Abstracción del procesador de pagos

public interface PaymentProcessor {
    PaymentResult processPayment(PaymentRequest request);
    boolean refund(String transactionId);
    PaymentStatus getPaymentStatus(String transactionId);
}

// Implementación 1: Stripe
@Service
@ConditionalOnProperty(name = "payment.provider", havingValue = "stripe")
public class StripePaymentProcessor implements PaymentProcessor {
    
    @Autowired
    private StripeClient stripeClient;
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Lógica específica de Stripe
        Charge charge = stripeClient.createCharge(
            request.getAmount(),
            request.getCurrency(),
            request.getCardToken()
        );
        
        return PaymentResult.builder()
            .success(charge.getStatus().equals("succeeded"))
            .transactionId(charge.getId())
            .build();
    }
    
    @Override
    public boolean refund(String transactionId) {
        Refund refund = stripeClient.createRefund(transactionId);
        return refund.getStatus().equals("succeeded");
    }
    
    @Override
    public PaymentStatus getPaymentStatus(String transactionId) {
        Charge charge = stripeClient.getCharge(transactionId);
        return mapStripeStatus(charge.getStatus());
    }
}

// Implementación 2: PayPal
@Service
@ConditionalOnProperty(name = "payment.provider", havingValue = "paypal")
public class PayPalPaymentProcessor implements PaymentProcessor {
    
    @Autowired
    private PayPalClient paypalClient;
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Lógica específica de PayPal
        Payment payment = paypalClient.createPayment(request);
        
        return PaymentResult.builder()
            .success(payment.getState().equals("approved"))
            .transactionId(payment.getId())
            .build();
    }
    
    @Override
    public boolean refund(String transactionId) {
        Refund refund = paypalClient.refundPayment(transactionId);
        return refund.getState().equals("completed");
    }
    
    @Override
    public PaymentStatus getPaymentStatus(String transactionId) {
        Payment payment = paypalClient.getPayment(transactionId);
        return mapPayPalStatus(payment.getState());
    }
}

// Servicio que procesa órdenes
@Service
@RequiredArgsConstructor
public class CheckoutService {
    
    // ✅ Depende de la abstracción, no de Stripe o PayPal específicamente
    private final PaymentProcessor paymentProcessor;
    
    public Order processCheckout(CheckoutRequest request) {
        // Crear orden
        Order order = createOrder(request);
        
        // ✅ Procesar pago (no sabe si es Stripe o PayPal)
        PaymentRequest paymentRequest = buildPaymentRequest(order);
        PaymentResult result = paymentProcessor.processPayment(paymentRequest);
        
        if (result.isSuccess()) {
            order.setStatus(OrderStatus.PAID);
            order.setTransactionId(result.getTransactionId());
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        }
        
        return orderRepository.save(order);
    }
}

// Configuración (application.properties)
// payment.provider=stripe  (en producción)
// payment.provider=paypal  (si cambias de proveedor)
```

---

## 🎯 Inyección de Dependencias en Spring

### Tipos de Inyección

#### 1. Constructor Injection (✅ RECOMENDADO)

```java
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    // ✅ MEJOR: Inyección por constructor
    @Autowired  // Opcional desde Spring 4.3
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}

// O con Lombok
@Service
@RequiredArgsConstructor  // Genera constructor automáticamente
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
}
```

**Ventajas:**
- Dependencias inmutables (`final`)
- Fácil de testear (puedes instanciar sin Spring)
- Falla rápido si faltan dependencias
- Es obvio qué dependencias tiene la clase

#### 2. Field Injection (❌ NO RECOMENDADO)

```java
@Service
public class UserService {
    
    // ❌ EVITAR: Inyección por campo
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
}
```

**Desventajas:**
- No puedes hacer `final`
- Difícil de testear
- Oculta dependencias
- Acoplado a Spring

#### 3. Setter Injection (🤔 OCASIONAL)

```java
@Service
public class UserService {
    
    private UserRepository userRepository;
    
    // 🤔 USO OCASIONAL: Para dependencias opcionales
    @Autowired(required = false)
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

---

## ✅ Inversión de Control (IoC)

### ¿Qué es IoC?

**Sin IoC (tu código controla las dependencias):**
```java
public class UserService {
    private UserRepository repo = new MySQLUserRepository(); // ❌ Tú controlas
}
```

**Con IoC (framework controla las dependencias):**
```java
public class UserService {
    private final UserRepository repo;
    
    public UserService(UserRepository repo) {
        this.repo = repo; // ✅ Spring controla (inyecta)
    }
}
```

**Spring Container:**
```
Spring Container
├── UserService (bean)
├── UserRepository (bean)
├── EmailService (bean)
└── ... otros beans

Spring automáticamente:
1. Crea los beans
2. Resuelve dependencias
3. Inyecta en constructores
```

---

## 🧪 Testing Más Fácil

```java
// ✅ Test sin Spring (Unit Test)

@Test
public void testRegisterUser() {
    // Crear mock de la dependencia
    UserRepository mockRepo = mock(UserRepository.class);
    EmailService mockEmail = mock(EmailService.class);
    
    // Instanciar el servicio con mocks
    UserService service = new UserService(mockRepo, mockEmail);
    
    // Test
    User user = new User("test@example.com", "password");
    service.registerUser(user);
    
    // Verificar
    verify(mockRepo).save(user);
    verify(mockEmail).sendWelcomeEmail(user.getEmail());
}

// ✅ Test con Spring (Integration Test)

@SpringBootTest
public class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @MockBean  // Mock automático en el contexto de Spring
    private EmailService emailService;
    
    @Test
    public void testRegisterUser() {
        User user = new User("test@example.com", "password");
        userService.registerUser(user);
        
        verify(emailService).sendWelcomeEmail(user.getEmail());
    }
}
```

---

## 🎓 Para la Evaluación del SENA

### Pregunta: "¿Por qué usas interfaces en vez de clases directamente?"

**Respuesta:**
> "Aplico el Principio de Inversión de Dependencias. Los servicios de alto nivel (como `ProductService`) dependen de abstracciones (interfaces como `ProductRepository`), no de implementaciones concretas.
> 
> Esto me permite:
> 1. Cambiar la implementación sin modificar el servicio (ej: de PostgreSQL a MySQL)
> 2. Testear con implementaciones en memoria sin necesitar una BD real
> 3. Desacoplar el código de detalles de implementación
> 
> Spring se encarga de inyectar la implementación correcta automáticamente."

### Pregunta: "¿Qué es la inyección de dependencias?"

**Respuesta:**
> "Es cuando el framework (Spring) es responsable de crear los objetos y pasarlos a las clases que los necesitan. En lugar de que `UserService` instancie su `UserRepository` con `new`, Spring lo inyecta a través del constructor.
> 
> Esto invierte el control: antes mi código controlaba sus dependencias, ahora Spring las controla (Inversion of Control - IoC)."

---

## 📝 Resumen

```
Dependency Inversion Principle (DIP)

Regla:
"Depende de abstracciones, NO de implementaciones concretas"

Cómo Aplicar:
✅ Usa interfaces en vez de clases concretas
✅ Inyección de dependencias por constructor
✅ Spring maneja el ciclo de vida de los beans
✅ Tests usan mocks de las interfaces

Arquitectura:
Alto Nivel (Services) → Abstracción (Interfaces)
                              ↑
                    Bajo Nivel (Implementaciones)

Beneficios:
✅ Código desacoplado
✅ Fácil de testear
✅ Fácil de cambiar implementaciones
✅ Código reutilizable

En Baby Cash:
✅ Services dependen de Repository (interfaces)
✅ Spring inyecta implementaciones JPA automáticamente
✅ @Autowired en constructores
✅ @RequiredArgsConstructor de Lombok
```

---

**Siguiente:** Lee `SOLID-EN-BABYCASH.md` para ver análisis completo del proyecto 🚀
