# S - SINGLE RESPONSIBILITY PRINCIPLE (Principio de Responsabilidad Única)

## 📚 Definición

> **"Una clase debe tener UNA, y solo UNA razón para cambiar"**
> 
> — Robert C. Martin (Uncle Bob)

---

## 🤔 ¿Qué Significa?

### Para Principiantes (Analogía)

Imagina que trabajas en una tienda:

**❌ SIN Single Responsibility:**
- Una persona que: atiende clientes, hace la contabilidad, limpia, cocina, maneja redes sociales, y hace reparaciones.
- Si esta persona falta, **TODO se detiene**.
- Si necesitas cambiar cómo se atiende, afectas la contabilidad también.

**✅ CON Single Responsibility:**
- Cajero → Solo cobra
- Contador → Solo maneja finanzas
- Community Manager → Solo redes sociales
- Cada uno es experto en SU trabajo.
- Si el cajero falta, la contabilidad sigue funcionando.

### Para Programadores

```
Una clase debe tener UNA sola responsabilidad.
Una clase debe tener UNA sola razón para cambiar.
```

**Preguntas clave:**
- ¿Esta clase hace más de una cosa?
- ¿Hay múltiples razones por las que necesitaría modificarla?
- ¿Puedo describir qué hace en UNA frase simple?

---

## ❌ Violando el Principio

### Ejemplo 1: Clase que hace TODO

```java
// ❌ VIOLACIÓN: Esta clase tiene MUCHAS responsabilidades

public class User {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    
    // RESPONSABILIDAD 1: Validación
    public boolean isValidEmail() {
        return email != null && email.contains("@");
    }
    
    public boolean isValidPassword() {
        return password != null && password.length() >= 8;
    }
    
    // RESPONSABILIDAD 2: Persistencia en Base de Datos
    public void saveToDatabase() {
        Connection conn = DriverManager.getConnection("jdbc:...");
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO users VALUES (?, ?, ?, ?)"
        );
        stmt.setString(1, email);
        stmt.setString(2, password);
        stmt.execute();
    }
    
    // RESPONSABILIDAD 3: Envío de Emails
    public void sendWelcomeEmail() {
        EmailClient client = new EmailClient();
        client.send(email, "Bienvenido!", "Gracias por registrarte");
    }
    
    // RESPONSABILIDAD 4: Generación de Reportes
    public String generateUserReport() {
        return "Usuario: " + firstName + " " + lastName + 
               "\nEmail: " + email +
               "\nRegistrado: " + new Date();
    }
    
    // RESPONSABILIDAD 5: Autenticación
    public boolean authenticate(String inputPassword) {
        return BCrypt.checkpw(inputPassword, this.password);
    }
    
    // RESPONSABILIDAD 6: Formateo
    public String toJSON() {
        return "{\"email\":\"" + email + "\",\"firstName\":\"" + firstName + "\"}";
    }
}
```

### ¿Cuántas razones para cambiar?

1. Si cambias las **reglas de validación** → Debes modificar `User`
2. Si cambias de **PostgreSQL a MySQL** → Debes modificar `User`
3. Si cambias el **proveedor de email** → Debes modificar `User`
4. Si cambias el **formato del reporte** → Debes modificar `User`
5. Si cambias el **algoritmo de encriptación** → Debes modificar `User`
6. Si cambias el **formato JSON** → Debes modificar `User`

**Esta clase tiene 6 razones para cambiar!** ❌

### Problemas:
- **Difícil de entender**: Hace demasiadas cosas
- **Difícil de testear**: Necesitas DB, email, etc.
- **Difícil de mantener**: Cambiar algo puede romper otra cosa
- **Difícil de reutilizar**: No puedes usar solo la validación
- **Acoplamiento alto**: Todo está mezclado

---

## ✅ Aplicando el Principio

### Solución: Separar Responsabilidades

```java
// ✅ CORRECTO: Cada clase tiene UNA responsabilidad

// 1. MODELO - Solo estructura de datos
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    
    // Solo getters y setters
    // Sin lógica de negocio
}

// 2. VALIDACIÓN - Solo validar datos
@Component
public class UserValidator {
    public void validate(User user) {
        if (!isValidEmail(user.getEmail())) {
            throw new ValidationException("Email inválido");
        }
        if (!isValidPassword(user.getPassword())) {
            throw new ValidationException("Contraseña debe tener mínimo 8 caracteres");
        }
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }
}

// 3. REPOSITORIO - Solo acceso a datos
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

// 4. SERVICIO DE EMAIL - Solo enviar emails
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendWelcomeEmail(String toEmail, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Bienvenido a Baby Cash!");
        message.setText("Hola " + firstName + ", gracias por registrarte.");
        
        mailSender.send(message);
    }
}

// 5. SERVICIO DE REPORTES - Solo generar reportes
@Service
public class ReportService {
    public String generateUserReport(User user) {
        return String.format(
            "Usuario: %s %s\nEmail: %s\nRegistrado: %s",
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}

// 6. SERVICIO DE AUTENTICACIÓN - Solo autenticar
@Service
public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public boolean authenticate(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}

// 7. SERVICIO PRINCIPAL - Solo coordinar (lógica de negocio)
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserValidator userValidator;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(RegisterRequest request) {
        // Crear usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        // Validar
        userValidator.validate(user);
        
        // Guardar
        User savedUser = userRepository.save(user);
        
        // Enviar email de bienvenida
        emailService.sendWelcomeEmail(
            savedUser.getEmail(),
            savedUser.getFirstName()
        );
        
        return savedUser;
    }
}
```

### Ahora cada clase tiene UNA razón para cambiar:

1. **User** → Cambia si el esquema de BD cambia
2. **UserValidator** → Cambia si las reglas de validación cambian
3. **UserRepository** → Cambia si el acceso a datos cambia
4. **EmailService** → Cambia si el proveedor de email cambia
5. **ReportService** → Cambia si el formato de reporte cambia
6. **AuthService** → Cambia si el algoritmo de autenticación cambia
7. **UserService** → Cambia si la lógica de negocio cambia

**¡Responsabilidades separadas!** ✅

---

## 🏢 Ejemplos Reales de Baby Cash

### Ejemplo 1: ProductService

```java
// ✅ BIEN: Solo maneja lógica de productos

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    // Solo operaciones relacionadas con productos
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActive(true);
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    public Product createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException());
            
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        
        return productRepository.save(product);
    }
}
```

**Responsabilidad:** Solo lógica de negocio de productos.
- NO envía emails → `EmailService` lo hace
- NO maneja HTTP → `ProductController` lo hace
- NO accede directo a BD → `ProductRepository` lo hace

---

### Ejemplo 2: EmailService

```java
// ✅ BIEN: Solo envía emails

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    
    @Value("${app.mail.from-email}")
    private String fromEmail;
    
    // Solo métodos relacionados con envío de emails
    public void sendWelcomeEmail(String toEmail, String name) {
        MimeMessage message = mailSender.createMimeMessage();
        // ... configuración del email
        mailSender.send(message);
    }
    
    public void sendOrderConfirmationEmail(Order order) {
        // ... enviar email de confirmación
    }
    
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        // ... enviar email de reset
    }
}
```

**Responsabilidad:** Solo enviar emails.
- NO valida usuarios
- NO guarda en base de datos
- NO procesa pagos

---

### Ejemplo 3: OrderService

```java
// ✅ BIEN: Solo maneja lógica de órdenes

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final EmailService emailService;
    private final PaymentService paymentService;
    
    @Transactional
    public Order createOrder(CreateOrderRequest request, User user) {
        // 1. Validar productos disponibles
        validateProductsAvailability(request.getItems());
        
        // 2. Calcular total
        BigDecimal total = calculateTotal(request.getItems());
        
        // 3. Crear orden
        Order order = buildOrder(request, user, total);
        Order savedOrder = orderRepository.save(order);
        
        // 4. Procesar pago (delega a PaymentService)
        paymentService.processPayment(savedOrder, request.getPaymentInfo());
        
        // 5. Enviar confirmación (delega a EmailService)
        emailService.sendOrderConfirmationEmail(savedOrder);
        
        return savedOrder;
    }
    
    // Métodos privados de ayuda (solo para lógica interna)
    private void validateProductsAvailability(List<OrderItemRequest> items) {
        // ...
    }
    
    private BigDecimal calculateTotal(List<OrderItemRequest> items) {
        // ...
    }
}
```

**Responsabilidad:** Coordinar la creación de órdenes.
- Delega el pago a `PaymentService`
- Delega el email a `EmailService`
- Delega la persistencia a `OrderRepository`

---

## 🎯 Cómo Identificar Violaciones

### Señales de Alerta 🚨

#### 1. Nombres Genéricos
```java
// ❌ Nombres que indican múltiples responsabilidades
UserManager     // ¿Qué "maneja" exactamente?
DataHandler     // ¿Qué "maneja"?
HelperUtil      // ¿Ayuda en qué?
CommonService   // ¿Qué hace?
```

#### 2. Clases Largas
```java
// ❌ Clase con 50+ métodos públicos
public class UserManager {
    // 100+ líneas de métodos
    // Probablemente hace MUCHAS cosas
}
```

#### 3. Múltiples Imports de Diferentes Áreas
```java
// ❌ Señal de muchas responsabilidades
import java.sql.*;              // Base de datos
import javax.mail.*;            // Email
import com.stripe.*;            // Pagos
import org.apache.poi.*;        // Excel
import com.amazonaws.s3.*;      // Storage
```

#### 4. Demasiados @Autowired
```java
// ❌ Dependencias de muchas áreas diferentes
@Service
public class UserManager {
    @Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService;
    @Autowired private PaymentService paymentService;
    @Autowired private ReportService reportService;
    @Autowired private NotificationService notificationService;
    @Autowired private AuditService auditService;
    @Autowired private CacheService cacheService;
    @Autowired private StorageService storageService;
    // ... 10+ más
}
```

#### 5. Difícil Explicar Qué Hace
Si no puedes explicar qué hace una clase en **UNA frase**, probablemente viola SRP.

```java
// ❌ "Esta clase maneja usuarios, envía emails, procesa pagos, genera reportes..."
// ✅ "Esta clase valida datos de entrada"
// ✅ "Esta clase envía emails"
// ✅ "Esta clase accede a la base de datos de productos"
```

---

## ✅ Buenas Prácticas

### 1. Una Responsabilidad por Capa

```
Controller  → Solo maneja HTTP (request/response)
Service     → Solo lógica de negocio
Repository  → Solo acceso a datos
Validator   → Solo validación
Mapper      → Solo conversión Entity ↔ DTO
```

### 2. Nombres Descriptivos

```java
// ✅ BIEN: Nombres que dicen exactamente qué hacen
UserValidator        // Valida usuarios
EmailSender          // Envía emails
ProductRepository    // Accede a productos en BD
OrderCalculator      // Calcula totales de órdenes
PdfReportGenerator   // Genera PDFs
```

### 3. Métodos Pequeños y Enfocados

```java
// ✅ Cada método hace UNA cosa

@Service
public class UserService {
    
    public User createUser(RegisterRequest request) {
        validateRequest(request);           // 1. Validar
        User user = buildUser(request);     // 2. Construir
        encryptPassword(user);              // 3. Encriptar
        User saved = saveUser(user);        // 4. Guardar
        sendWelcomeEmail(saved);            // 5. Email
        return saved;
    }
    
    private void validateRequest(RegisterRequest request) {
        // Solo valida
    }
    
    private User buildUser(RegisterRequest request) {
        // Solo construye
    }
    
    private void encryptPassword(User user) {
        // Solo encripta
    }
    
    private User saveUser(User user) {
        // Solo guarda
    }
    
    private void sendWelcomeEmail(User user) {
        // Solo delega a EmailService
    }
}
```

### 4. Composición sobre Herencia

```java
// ✅ Usar servicios especializados

@Service
public class OrderService {
    private final EmailService emailService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    
    // Cada servicio tiene SU responsabilidad
}
```

---

## 🧪 Testing Más Fácil

### Con SRP, los tests son simples:

```java
// ✅ Test de UserValidator (solo valida)
@Test
public void shouldRejectInvalidEmail() {
    UserValidator validator = new UserValidator();
    User user = new User();
    user.setEmail("invalid-email");
    
    assertThrows(ValidationException.class, () -> {
        validator.validate(user);
    });
}

// ✅ Test de EmailService (solo emails)
@Test
public void shouldSendWelcomeEmail() {
    EmailService emailService = new EmailService(mockMailSender);
    
    emailService.sendWelcomeEmail("test@example.com", "John");
    
    verify(mockMailSender).send(any(MimeMessage.class));
}
```

**Sin SRP, tendrías que mockear: BD, email, pagos, etc. todo en un test.** ❌

---

## 🎓 Para la Evaluación del SENA

### Pregunta Típica:
**"¿Por qué separaste en tantas clases?"**

**Respuesta:**
> "Apliqué el principio de Responsabilidad Única de SOLID. Cada clase tiene una sola responsabilidad:
> - `ProductService` solo maneja la lógica de productos
> - `EmailService` solo envía emails
> - `ProductRepository` solo accede a la base de datos
> 
> Esto hace el código más mantenible, testeable y fácil de entender. Si necesito cambiar cómo se envían emails, solo modifico `EmailService` sin tocar las demás clases."

### Pregunta Típica:
**"¿Qué pasa si necesitas agregar una nueva funcionalidad?"**

**Respuesta:**
> "Gracias al SRP, puedo crear un nuevo servicio especializado sin modificar los existentes. Por ejemplo, si necesito agregar reportes en PDF, creo un `PdfReportService` que solo se encarga de generar PDFs, sin tocar `ProductService` o `OrderService`."

---

## 📊 Comparación Antes/Después

### ❌ ANTES (Sin SRP)
```
UserManager.java (1000 líneas)
├── validar()
├── guardarBD()
├── enviarEmail()
├── procesarPago()
├── generarReporte()
├── autenticar()
├── resetPassword()
└── ... 20+ métodos más

Problema:
- 1000 líneas en un archivo
- Difícil de entender
- Cambiar algo rompe otras cosas
- Tests complejos
```

### ✅ DESPUÉS (Con SRP)
```
UserValidator.java (50 líneas)      ← Solo valida
UserRepository.java (20 líneas)     ← Solo BD
EmailService.java (100 líneas)      ← Solo emails
PaymentService.java (150 líneas)    ← Solo pagos
ReportService.java (80 líneas)      ← Solo reportes
AuthService.java (120 líneas)       ← Solo autenticación
UserService.java (200 líneas)       ← Solo coordina

Ventajas:
- Cada archivo es pequeño y enfocado
- Fácil de entender cada parte
- Cambios aislados
- Tests simples por clase
```

---

## 📝 Resumen

```
Single Responsibility Principle (SRP)

Regla:
"Una clase, UNA responsabilidad"
"Una clase, UNA razón para cambiar"

Cómo Aplicar:
✅ Separar en capas (Controller, Service, Repository)
✅ Crear servicios especializados (EmailService, PaymentService)
✅ Clases pequeñas y enfocadas
✅ Nombres descriptivos
✅ Cada método hace UNA cosa

Beneficios:
✅ Código más fácil de entender
✅ Tests más simples
✅ Cambios sin romper otras partes
✅ Reutilización de código

En Baby Cash:
✅ Controllers → Solo HTTP
✅ Services → Solo lógica de negocio
✅ Repositories → Solo acceso a datos
✅ Validators → Solo validación
✅ Mappers → Solo conversión
```

---

**Siguiente:** Lee `O-OPEN-CLOSED.md` para aprender el siguiente principio SOLID. 🚀
