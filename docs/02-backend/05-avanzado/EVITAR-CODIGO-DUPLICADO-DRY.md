# EVITAR CÓDIGO DUPLICADO - DRY (Don't Repeat Yourself)

## 🎯 Regla de Oro

**Si copias y pegas código, estás haciendo algo mal.**

DRY = **Don't Repeat Yourself** (No Te Repitas)

---

## ❓ ¿Por Qué Evitar Duplicación?

### Imagina esto:

Tienes una receta de galletas escrita en 5 lugares diferentes. Un día descubres que la temperatura del horno estaba mal. Ahora debes **cambiar las 5 recetas**.

Si tuvieras UNA sola receta, cambiarías UN solo lugar.

---

### Lo Mismo con Código:

```java
❌ MAL (código duplicado):
// En UserService
public void sendWelcomeEmail(String email) {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(email);
    helper.setSubject("Welcome!");
    helper.setText("Welcome to Baby Cash");
    mailSender.send(message);
}

// En OrderService
public void sendOrderConfirmationEmail(String email) {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(email);
    helper.setSubject("Order Confirmed");
    helper.setText("Your order is confirmed");
    mailSender.send(message);
}

// En AuthService
public void sendPasswordResetEmail(String email) {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(email);
    helper.setSubject("Password Reset");
    helper.setText("Reset your password");
    mailSender.send(message);
}
```

**Problema:** Si cambias cómo enviar emails, debes modificar **3 lugares**.

---

### ✅ BIEN (sin duplicación):

```java
// EmailService centralizado
@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    public void sendWelcomeEmail(String email) {
        sendEmail(email, "Welcome!", "Welcome to Baby Cash");
    }
    
    public void sendOrderConfirmationEmail(String email) {
        sendEmail(email, "Order Confirmed", "Your order is confirmed");
    }
    
    public void sendPasswordResetEmail(String email) {
        sendEmail(email, "Password Reset", "Reset your password");
    }
    
    // ✅ Lógica de envío en UN solo lugar
    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error sending email to: {}", to, e);
        }
    }
}
```

**Ventaja:** Si cambias cómo enviar emails, solo modificas **1 lugar**.

---

## 🔍 Detectar Código Duplicado

### Señales de Código Duplicado:

1. **Copias y pegas código**
2. **Dos funciones casi idénticas** (solo cambian valores)
3. **Misma lógica en múltiples lugares**
4. **Cambias algo y debes actualizarlo en varios lugares**

---

## 🏗️ Baby Cash: Aplicación de DRY

### ✅ Ejemplo 1: Validaciones Centralizadas

#### ❌ ANTES (duplicado):

```java
// En ProductService
public void createProduct(CreateProductRequest request) {
    if (request.getName() == null || request.getName().isBlank()) {
        throw new IllegalArgumentException("Name is required");
    }
    if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Price must be positive");
    }
    // ...
}

// En OrderService
public void createOrder(CreateOrderRequest request) {
    if (request.getUserId() == null) {
        throw new IllegalArgumentException("User ID is required");
    }
    if (request.getItems() == null || request.getItems().isEmpty()) {
        throw new IllegalArgumentException("Items are required");
    }
    // ...
}

// En UserService
public void createUser(CreateUserRequest request) {
    if (request.getEmail() == null || request.getEmail().isBlank()) {
        throw new IllegalArgumentException("Email is required");
    }
    if (!request.getEmail().contains("@")) {
        throw new IllegalArgumentException("Invalid email format");
    }
    // ...
}
```

---

#### ✅ DESPUÉS (sin duplicación):

```java
// ValidationUtil centralizado
public class ValidationUtil {
    
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
    
    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
    
    public static void validatePositive(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }
    
    public static void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}

// Uso en ProductService
public void createProduct(CreateProductRequest request) {
    ValidationUtil.validateNotBlank(request.getName(), "Name");
    ValidationUtil.validatePositive(request.getPrice(), "Price");
    // ...
}

// Uso en OrderService
public void createOrder(CreateOrderRequest request) {
    ValidationUtil.validateNotNull(request.getUserId(), "User ID");
    ValidationUtil.validateNotNull(request.getItems(), "Items");
    // ...
}

// Uso en UserService
public void createUser(CreateUserRequest request) {
    ValidationUtil.validateNotBlank(request.getEmail(), "Email");
    ValidationUtil.validateEmail(request.getEmail());
    // ...
}
```

---

### ✅ Ejemplo 2: Mapeo de Entidades a DTOs

#### ❌ ANTES (duplicado):

```java
// En ProductController
@GetMapping("/{id}")
public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
    Product product = productService.getProductById(id);
    
    // ❌ Mapeo manual duplicado
    ProductResponse response = new ProductResponse();
    response.setId(product.getId());
    response.setName(product.getName());
    response.setPrice(product.getPrice());
    response.setDescription(product.getDescription());
    
    return ResponseEntity.ok(response);
}

@GetMapping
public ResponseEntity<List<ProductResponse>> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    
    // ❌ Mismo mapeo duplicado
    List<ProductResponse> responses = new ArrayList<>();
    for (Product product : products) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        responses.add(response);
    }
    
    return ResponseEntity.ok(responses);
}
```

---

#### ✅ DESPUÉS (sin duplicación):

```java
// ProductService
@Service
public class ProductService {
    
    public ProductResponse getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return mapToResponse(product);  // ✅ Mapeo centralizado
    }
    
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
            .map(this::mapToResponse)  // ✅ Reutiliza mapeo
            .collect(Collectors.toList());
    }
    
    // ✅ Método de mapeo reutilizable
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        return response;
    }
}
```

---

### ✅ Ejemplo 3: Repositorios con JpaRepository

#### ❌ ANTES (sin DRY):

```java
// Implementación manual duplicada para cada entidad
public class ProductRepositoryImpl {
    public Product save(Product product) { /* SQL */ }
    public Optional<Product> findById(Long id) { /* SQL */ }
    public List<Product> findAll() { /* SQL */ }
    public void deleteById(Long id) { /* SQL */ }
}

public class OrderRepositoryImpl {
    public Order save(Order order) { /* SQL */ }
    public Optional<Order> findById(Long id) { /* SQL */ }
    public List<Order> findAll() { /* SQL */ }
    public void deleteById(Long id) { /* SQL */ }
}
```

---

#### ✅ DESPUÉS (con JpaRepository - DRY):

```java
// ✅ Spring JPA genera implementaciones automáticamente
public interface ProductRepository extends JpaRepository<Product, Long> {
    // ✅ Sin duplicación: métodos heredados (save, findById, findAll, etc.)
}

public interface OrderRepository extends JpaRepository<Order, Long> {
    // ✅ Sin duplicación: métodos heredados
}

public interface UserRepository extends JpaRepository<User, Long> {
    // ✅ Sin duplicación: métodos heredados
}
```

**Ventaja:** Spring genera implementaciones, sin código duplicado.

---

## 📊 Estrategias para Aplicar DRY

### 1️⃣ Extraer a Funciones

```java
❌ ANTES:
public void processOrder1() {
    BigDecimal tax = total.multiply(new BigDecimal("0.16"));
    BigDecimal totalWithTax = total.add(tax);
    // ...
}

public void processOrder2() {
    BigDecimal tax = total.multiply(new BigDecimal("0.16"));
    BigDecimal totalWithTax = total.add(tax);
    // ...
}

✅ DESPUÉS:
public void processOrder1() {
    BigDecimal totalWithTax = calculateTotalWithTax(total);
    // ...
}

public void processOrder2() {
    BigDecimal totalWithTax = calculateTotalWithTax(total);
    // ...
}

private BigDecimal calculateTotalWithTax(BigDecimal total) {
    BigDecimal tax = total.multiply(new BigDecimal("0.16"));
    return total.add(tax);
}
```

---

### 2️⃣ Crear Servicios Reutilizables

```java
✅ EmailService: Reutilizable en todo el proyecto
@Service
public class EmailService {
    public void sendEmail(String to, String subject, String body) { }
}

// Usado en UserService, OrderService, AuthService, etc.
```

---

### 3️⃣ Usar Herencia o Interfaces

```java
// Base abstracta con lógica común
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ✅ Todos heredan estos campos
}

// Entidades específicas
@Entity
public class Product extends BaseEntity {
    // Solo campos específicos de Product
}

@Entity
public class Order extends BaseEntity {
    // Solo campos específicos de Order
}
```

---

## 🚫 Cuándo NO Aplicar DRY

### ⚠️ Duplicación Accidental

```java
// Parecen iguales, pero tienen propósitos diferentes
public BigDecimal calculateOrderTotal(Order order) {
    return order.getItems().stream()
        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}

public BigDecimal calculateCartTotal(Cart cart) {
    return cart.getItems().stream()
        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

**¿Son realmente iguales?**
- Si en el futuro `Order` aplica descuentos pero `Cart` no, necesitas funciones separadas.
- Si siempre hacen lo mismo, entonces sí, aplica DRY.

**Regla:** Si dos cosas cambian por razones diferentes, NO las combines (aunque se vean iguales).

---

## 📝 Checklist DRY

```
✅ Sin código copiado y pegado
✅ Lógica común en funciones reutilizables
✅ Servicios centralizados (EmailService, ValidationUtil)
✅ Herencia para campos comunes (BaseEntity)
✅ JpaRepository para evitar SQL duplicado
✅ Mapeo centralizado (mapToResponse)
✅ Constantes en lugar de valores mágicos repetidos
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es DRY?"**

> "DRY significa 'Don't Repeat Yourself' (No Te Repitas). Es el principio de evitar código duplicado. Si algo se repite, lo extraes a una función, servicio o clase reutilizable. Esto hace el código más fácil de mantener porque cambias UN solo lugar en lugar de múltiples."

---

**2. "¿Cómo aplicas DRY en Baby Cash?"**

> "De varias formas:
> - ✅ `EmailService` centralizado: toda la lógica de emails en un solo lugar
> - ✅ `ValidationUtil`: validaciones reutilizables
> - ✅ `JpaRepository`: Spring genera métodos CRUD, sin duplicación
> - ✅ `mapToResponse`: mapeo de entidades a DTOs en un solo método
> - ✅ `BaseEntity`: campos comunes (id, createdAt) heredados por todas las entidades"

---

**3. "¿Por qué es importante DRY?"**

> "Porque facilita el mantenimiento. Si tienes la misma lógica en 5 lugares y necesitas cambiarla, debes modificar 5 lugares y es fácil olvidar uno. Con DRY, cambias UN solo lugar y todo funciona. También reduce bugs porque la lógica es consistente."

---

**4. "¿Siempre debes aplicar DRY?"**

> "No siempre. Si dos cosas SE VEN iguales pero cambian por razones diferentes, no las combines. Por ejemplo, si `calculateOrderTotal` y `calculateCartTotal` son iguales HOY pero en el futuro `Order` puede tener descuentos y `Cart` no, es mejor dejarlas separadas. DRY es para lógica que SIEMPRE cambia junta."

---

## 🏆 Beneficios de DRY

### 1. **Fácil de Mantener**

Cambias UN lugar, no múltiples.

---

### 2. **Menos Bugs**

Lógica consistente en todo el proyecto.

---

### 3. **Código Más Corto**

Menos líneas de código = más fácil de entender.

---

### 4. **Profesionalismo**

Empresas valoran código sin duplicación.

---

## 📈 Antes y Después

### ❌ ANTES (duplicado)

```java
// 200 líneas de código duplicado en 3 servicios
UserService: sendEmail logic (70 líneas)
OrderService: sendEmail logic (70 líneas)
AuthService: sendEmail logic (70 líneas)
```

---

### ✅ DESPUÉS (DRY)

```java
// 70 líneas en EmailService, reutilizadas por 3 servicios
EmailService: sendEmail logic (70 líneas)
UserService: emailService.send() (1 línea)
OrderService: emailService.send() (1 línea)
AuthService: emailService.send() (1 línea)

Total: 73 líneas (en lugar de 210)
Ahorro: 65% menos código
```

---

## 🚀 Conclusión

**DRY = Don't Repeat Yourself**

Código limpio:
- ✅ Sin duplicación
- ✅ Lógica reutilizable
- ✅ Servicios centralizados
- ✅ Fácil de mantener

**Baby Cash aplica DRY en toda su arquitectura.**

---

**Ahora lee:** `CLASES-COHESIVAS.md` para el último principio de Clean Code. 🚀
