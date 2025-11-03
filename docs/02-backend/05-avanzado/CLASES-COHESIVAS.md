# CLASES COHESIVAS (Alta Cohesión, Bajo Acoplamiento)

## 🎯 Regla de Oro

**Una clase debe tener métodos y atributos relacionados entre sí.**

Cohesión = qué tan relacionadas están las partes de una clase.

---

## ❓ ¿Qué es Cohesión?

### Analogía: Caja de Herramientas

```
❌ BAJA COHESIÓN (caja con cosas al azar):
- Martillo
- Tornillos
- Libro de cocina
- Zapatos
- USB

✅ ALTA COHESIÓN (caja de herramientas):
- Martillo
- Destornilladores
- Llaves inglesas
- Alicates
- Cinta métrica
```

**Una clase cohesiva** agrupa cosas relacionadas.

---

## 🔍 Cohesión en Clases

### ❌ BAJA COHESIÓN

```java
// ❌ Clase que hace muchas cosas no relacionadas
public class User {
    
    // Datos del usuario
    private String name;
    private String email;
    private String password;
    
    // ❌ Validación de email (no relacionado directamente)
    public boolean isValidEmail() {
        return email.contains("@");
    }
    
    // ❌ Envío de email (¿por qué User envía emails?)
    public void sendWelcomeEmail() {
        // Lógica de envío de email
    }
    
    // ❌ Cálculo de impuestos (¿qué tiene que ver con User?)
    public BigDecimal calculateTax(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.16"));
    }
    
    // ❌ Generación de reportes (¿por qué User genera reportes?)
    public String generateReport() {
        return "Report for " + name;
    }
}
```

**Problema:** User tiene **5 responsabilidades diferentes**. Baja cohesión.

---

### ✅ ALTA COHESIÓN

```java
// ✅ User: Solo datos del usuario
@Entity
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
    
    // ✅ Solo getters/setters relacionados con User
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    // ...
}

// ✅ EmailValidator: Solo validación de email
public class EmailValidator {
    public boolean isValid(String email) {
        return email != null && email.contains("@");
    }
}

// ✅ EmailService: Solo envío de emails
@Service
public class EmailService {
    public void sendWelcomeEmail(String email) {
        // Lógica de envío
    }
}

// ✅ TaxCalculator: Solo cálculo de impuestos
@Service
public class TaxCalculator {
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.16"));
    }
}

// ✅ ReportGenerator: Solo generación de reportes
@Service
public class ReportGenerator {
    public String generateUserReport(User user) {
        return "Report for " + user.getName();
    }
}
```

**Ventaja:** Cada clase tiene **UNA responsabilidad**. Alta cohesión.

---

## 🔗 Acoplamiento

**Acoplamiento** = qué tan dependiente es una clase de otras.

### ❌ ALTO ACOPLAMIENTO (malo)

```java
// ❌ OrderService conoce detalles internos de muchas clases
@Service
public class OrderService {
    
    public void createOrder(Order order) {
        // ❌ Accede directamente a la base de datos (acoplado a JDBC)
        String sql = "INSERT INTO orders (user_id, total) VALUES (?, ?)";
        jdbcTemplate.update(sql, order.getUserId(), order.getTotal());
        
        // ❌ Conoce detalles de envío de email (acoplado a JavaMailSender)
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(order.getUser().getEmail());
        helper.setSubject("Order Confirmed");
        mailSender.send(message);
        
        // ❌ Conoce detalles de pago (acoplado a Stripe API)
        Stripe.apiKey = "sk_test_...";
        PaymentIntent paymentIntent = PaymentIntent.create(params);
    }
}
```

**Problema:** Si cambias base de datos, email o pago, debes cambiar `OrderService`.

---

### ✅ BAJO ACOPLAMIENTO (bueno)

```java
// ✅ OrderService solo conoce interfaces, no implementaciones
@Service
@RequiredArgsConstructor
public class OrderService {
    
    // ✅ Depende de abstracciones (interfaces)
    private final OrderRepository orderRepository;  // Interfaz
    private final EmailService emailService;         // Interfaz
    private final PaymentService paymentService;     // Interfaz
    
    public void createOrder(Order order) {
        // ✅ No conoce detalles de implementación
        orderRepository.save(order);
        emailService.sendOrderConfirmation(order);
        paymentService.processPayment(order);
    }
}
```

**Ventaja:** Si cambias implementación de email, `OrderService` NO cambia.

---

## 🏗️ Baby Cash: Alta Cohesión

### ✅ Ejemplo: ProductService

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    // ✅ Dependencias relacionadas con productos
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    // ✅ Todos los métodos relacionados con productos
    
    public List<ProductResponse> getAllActiveProducts() {
        List<Product> products = productRepository.findByEnabled(true);
        return products.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return mapToResponse(product);
    }
    
    public ProductResponse createProduct(CreateProductRequest request) {
        validateProductRequest(request);
        Product product = buildProduct(request);
        product = productRepository.save(product);
        return mapToResponse(product);
    }
    
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);
        updateProductFields(product, request);
        product = productRepository.save(product);
        return mapToResponse(product);
    }
    
    public void deleteProduct(Long id) {
        Product product = findProductOrThrow(id);
        product.setEnabled(false);  // Soft delete
        productRepository.save(product);
    }
    
    // ✅ Métodos auxiliares privados, todos relacionados con productos
    
    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
    
    private void validateProductRequest(CreateProductRequest request) {
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }
    
    private Product buildProduct(CreateProductRequest request) {
        // ...
    }
    
    private ProductResponse mapToResponse(Product product) {
        // ...
    }
}
```

**Observa:**
- ✅ **Alta cohesión**: Todos los métodos relacionados con productos
- ✅ **Bajo acoplamiento**: Depende de interfaces (`ProductRepository`)
- ✅ **Sin responsabilidades ajenas**: NO envía emails, NO calcula impuestos

---

### ✅ Ejemplo: EmailService

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    // ✅ Dependencias relacionadas con email
    private final JavaMailSender mailSender;
    
    @Value("${app.admin-email}")
    private String adminEmail;
    
    // ✅ Todos los métodos relacionados con envío de emails
    
    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        sendEmail(toEmail, "Welcome!", buildWelcomeBody(name));
    }
    
    @Async
    public void sendOrderConfirmationEmail(Order order) {
        String body = buildOrderConfirmationBody(order);
        sendEmail(order.getUser().getEmail(), "Order Confirmed", body);
    }
    
    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        String body = buildPasswordResetBody(token);
        sendEmail(toEmail, "Password Reset", body);
    }
    
    // ✅ Métodos auxiliares, todos relacionados con email
    
    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Error sending email to: {}", to, e);
        }
    }
    
    private String buildWelcomeBody(String name) {
        return "<h1>Welcome " + name + "!</h1>";
    }
    
    private String buildOrderConfirmationBody(Order order) {
        return "<h1>Order #" + order.getId() + " confirmed!</h1>";
    }
    
    private String buildPasswordResetBody(String token) {
        return "<a href='/reset?token=" + token + "'>Reset Password</a>";
    }
}
```

**Observa:**
- ✅ **Alta cohesión**: Todo relacionado con emails
- ✅ **Sin responsabilidades ajenas**: NO gestiona usuarios, NO procesa pagos

---

## 📊 Métricas de Cohesión

### ✅ Señales de Alta Cohesión

```
✅ Todos los métodos usan la mayoría de los fields
✅ Nombre de clase describe exactamente qué hace
✅ Fácil de explicar en una frase
✅ Métodos relacionados entre sí
✅ Si cambias una parte, probablemente cambies otras (relacionadas)
```

---

### ❌ Señales de Baja Cohesión

```
❌ Métodos no usan los mismos fields
❌ Nombre genérico (Manager, Helper, Utils)
❌ Difícil de explicar en una frase
❌ Métodos no relacionados
❌ Si cambias una parte, NO afecta a otras
```

---

## 🎯 Ejemplo: Clase con Baja Cohesión

```java
❌ MAL (baja cohesión):
public class UserManager {
    
    // Fields no relacionados
    private UserRepository userRepository;
    private EmailService emailService;
    private TaxCalculator taxCalculator;
    private ReportGenerator reportGenerator;
    private FileUploader fileUploader;
    
    // Métodos no relacionados
    public void createUser() { }
    public void sendEmail() { }
    public BigDecimal calculateTax() { }
    public String generateReport() { }
    public void uploadFile() { }
}
```

**Problema:** ¿Qué hace `UserManager`? **TODO**. Baja cohesión.

---

## ✅ Ejemplo: Clases con Alta Cohesión

```java
✅ BIEN (alta cohesión):

// User management
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public User createUser() { }
    public User getUserById() { }
    public User updateUser() { }
}

// Email management
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    
    public void sendEmail() { }
    public void sendBulkEmail() { }
}

// Tax calculation
@Service
public class TaxCalculator {
    public BigDecimal calculateTax() { }
    public BigDecimal calculateVAT() { }
}

// Report generation
@Service
public class ReportGenerator {
    public String generateUserReport() { }
    public String generateSalesReport() { }
}

// File upload
@Service
public class FileUploader {
    public String upload() { }
    public void delete() { }
}
```

**Ventaja:** Cada clase tiene **una responsabilidad clara**.

---

## 📝 Checklist de Cohesión

```
✅ Clase tiene un propósito claro (descrito en una frase)
✅ Todos los métodos relacionados entre sí
✅ Todos los métodos usan la mayoría de los fields
✅ Nombre de clase específico (ProductService, no Manager)
✅ Sin métodos "huérfanos" (métodos no relacionados)
✅ Fácil de testear (porque hace una cosa)
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es cohesión?"**

> "Cohesión es qué tan relacionadas están las partes de una clase. Alta cohesión significa que todos los métodos y atributos de la clase están relacionados con el mismo propósito. Por ejemplo, `ProductService` tiene alta cohesión porque todo se relaciona con productos."

---

**2. "¿Qué es acoplamiento?"**

> "Acoplamiento es qué tan dependiente es una clase de otras. Bajo acoplamiento significa que una clase depende de interfaces (abstracciones), no de implementaciones concretas. Por ejemplo, `OrderService` depende de `OrderRepository` (interfaz), no de `OrderRepositoryImpl` (implementación)."

---

**3. "¿Tu código tiene alta cohesión y bajo acoplamiento?"**

> "Sí:
> - ✅ **Alta cohesión**: Cada servicio tiene una responsabilidad (`ProductService` solo productos, `EmailService` solo emails)
> - ✅ **Bajo acoplamiento**: Servicios dependen de interfaces (`JpaRepository`, `ILoyaltyService`)
> - ✅ Inyección de dependencias por constructor
> - ✅ Sin dependencias hardcodeadas"

---

**4. "¿Cómo decides qué va en cada clase?"**

> "Pregunto: '¿Este método está relacionado con la responsabilidad principal de la clase?'. Si la respuesta es NO, va en otra clase. Por ejemplo, si `ProductService` necesita enviar email, NO lo hace directamente. Llama a `EmailService`. Así mantengo alta cohesión y bajo acoplamiento."

---

## 🏆 Beneficios

### 1. **Fácil de Entender**

Clase cohesiva hace UNA cosa, fácil de entender.

---

### 2. **Fácil de Mantener**

Cambios en una responsabilidad NO afectan otras.

---

### 3. **Fácil de Testear**

Clase cohesiva = tests simples y enfocados.

---

### 4. **Reutilizable**

Clases específicas son más fáciles de reutilizar.

---

## 📈 Niveles de Cohesión

### Nivel 1: Baja Cohesión 🔴

```java
public class Manager {
    // Hace de todo: usuarios, emails, pagos, reportes
}
```

---

### Nivel 2: Cohesión Media 🟡

```java
public class UserService {
    // Gestiona usuarios, pero también envía emails
}
```

---

### Nivel 3: Alta Cohesión 🟢

```java
public class UserService {
    // Solo gestiona usuarios
}

public class EmailService {
    // Solo envía emails
}
```

---

## 🚀 Conclusión

**Alta cohesión + Bajo acoplamiento = Código profesional**

Código limpio:
- ✅ Clases cohesivas (una responsabilidad)
- ✅ Bajo acoplamiento (depende de interfaces)
- ✅ Fácil de mantener
- ✅ Fácil de testear

**Baby Cash tiene alta cohesión y bajo acoplamiento en toda su arquitectura.**

---

## 🎉 ¡Felicidades!

Has completado todos los principios de **Clean Code**:

1. ✅ Nombres significativos
2. ✅ Funciones pequeñas
3. ✅ Comentarios buenos vs malos
4. ✅ Formateo consistente
5. ✅ Manejo de errores limpio
6. ✅ DRY (Don't Repeat Yourself)
7. ✅ Clases cohesivas

---

**Ahora lee:** `../patrones-diseño/QUE-SON-PATRONES-DISEÑO.md` para continuar con Design Patterns. 🚀
