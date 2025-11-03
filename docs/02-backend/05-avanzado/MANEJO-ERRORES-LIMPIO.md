# MANEJO DE ERRORES LIMPIO

## 🎯 Regla de Oro

**Usa excepciones, NO códigos de error.**

Las excepciones hacen el código más limpio y expresivo.

---

## ❓ ¿Por Qué Usar Excepciones?

### ❌ MAL: Códigos de Error

```java
public int createUser(User user) {
    if (user.getEmail() == null) {
        return -1;  // ¿Qué significa -1?
    }
    if (userRepository.existsByEmail(user.getEmail())) {
        return -2;  // ¿Y -2?
    }
    userRepository.save(user);
    return 1;  // ¿Y 1?
}

// Uso
int result = createUser(user);
if (result == -1) {
    System.out.println("Email is null");
} else if (result == -2) {
    System.out.println("Email already exists");
} else if (result == 1) {
    System.out.println("User created");
}
```

**Problemas:**
- ❌ Códigos mágicos (-1, -2, 1)
- ❌ Necesitas documentación para entenderlos
- ❌ Fácil olvidar validar el código de retorno

---

### ✅ BIEN: Excepciones

```java
public void createUser(User user) {
    if (user.getEmail() == null) {
        throw new IllegalArgumentException("Email is required");
    }
    if (userRepository.existsByEmail(user.getEmail())) {
        throw new DuplicateEmailException("Email already exists");
    }
    userRepository.save(user);
}

// Uso
try {
    createUser(user);
    System.out.println("User created successfully");
} catch (IllegalArgumentException e) {
    System.out.println("Validation error: " + e.getMessage());
} catch (DuplicateEmailException e) {
    System.out.println("Duplicate error: " + e.getMessage());
}
```

**Ventajas:**
- ✅ Nombres descriptivos (`IllegalArgumentException`, `DuplicateEmailException`)
- ✅ Mensajes claros
- ✅ Obligatorio manejar errores (compilador avisa)

---

## 🚫 NO Devolver `null`

### ❌ MAL: Devolver `null`

```java
public User getUserById(Long id) {
    Optional<User> userOpt = userRepository.findById(id);
    if (userOpt.isPresent()) {
        return userOpt.get();
    }
    return null;  // ❌ Devuelve null
}

// Uso
User user = getUserById(1L);
if (user != null) {  // ❌ Fácil olvidar este if
    System.out.println(user.getName());
} else {
    System.out.println("User not found");
}
```

**Problema:** Si olvidas el `if (user != null)`, obtienes **NullPointerException**.

---

### ✅ BIEN: Lanzar Excepción

```java
public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
}

// Uso
try {
    User user = getUserById(1L);
    System.out.println(user.getName());  // ✅ Nunca es null
} catch (ResourceNotFoundException e) {
    System.out.println("Error: " + e.getMessage());
}
```

---

### ✅ ALTERNATIVA: Optional

```java
public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
}

// Uso
Optional<User> userOpt = getUserById(1L);
userOpt.ifPresent(user -> System.out.println(user.getName()));
```

**Ventaja:** Explícito que puede no haber resultado.

---

## 🎨 Jerarquía de Excepciones

### Baby Cash: Excepciones Personalizadas

```java
// Excepción base
public class BabyCashException extends RuntimeException {
    public BabyCashException(String message) {
        super(message);
    }
}

// Excepciones específicas
public class ResourceNotFoundException extends BabyCashException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class DuplicateEmailException extends BabyCashException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}

public class InsufficientStockException extends BabyCashException {
    public InsufficientStockException(String message) {
        super(message);
    }
}

public class InvalidCredentialsException extends BabyCashException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
```

**Ventajas:**
- ✅ Nombres descriptivos
- ✅ Fácil de extender
- ✅ Puedes capturar todas con `BabyCashException`

---

## 🏗️ Manejo de Errores en Baby Cash

### ✅ Ejemplo: ProductService

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }
    
    public ProductResponse createProduct(CreateProductRequest request) {
        validateProductRequest(request);  // ✅ Puede lanzar IllegalArgumentException
        
        Product product = buildProduct(request);
        product = productRepository.save(product);
        
        return mapToResponse(product);
    }
    
    private void validateProductRequest(CreateProductRequest request) {
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (request.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }
}
```

---

### ✅ Ejemplo: OrderService

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // ✅ Validar usuario
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<OrderItem> items = new ArrayList<>();
        
        for (OrderItemRequest itemRequest : request.getItems()) {
            // ✅ Validar producto
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
            // ✅ Validar stock
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                    "Not enough stock for product: " + product.getName()
                );
            }
            
            // ✅ Reducir stock
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
            
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            items.add(item);
        }
        
        Order order = new Order();
        order.setUser(user);
        order.setItems(items);
        order.setStatus(OrderStatus.PENDING);
        
        order = orderRepository.save(order);
        
        return mapToResponse(order);
    }
}
```

---

### ✅ Manejo Global de Excepciones

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // ✅ Excepción de recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            e.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    // ✅ Excepción de validación
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    // ✅ Excepción de stock insuficiente
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException e) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    // ✅ Excepción genérica
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// DTO de respuesta de error
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
```

**Ventajas:**
- ✅ Manejo centralizado de errores
- ✅ Respuestas consistentes
- ✅ Código de servicio más limpio (no necesita try-catch en cada método)

---

## 📊 Tipos de Excepciones

### ✅ Checked vs Unchecked

#### Checked (obligatorio catch)

```java
// ❌ NO recomendado para lógica de negocio
public void sendEmail(String email) throws MessagingException {
    // Si falla, DEBES capturar MessagingException
}

// Uso
try {
    sendEmail("test@example.com");
} catch (MessagingException e) {
    // Obligatorio
}
```

#### Unchecked (opcional catch)

```java
// ✅ Recomendado para lógica de negocio
public void createUser(User user) {
    if (user.getEmail() == null) {
        throw new IllegalArgumentException("Email is required");  // RuntimeException
    }
}

// Uso
createUser(user);  // ✅ No requiere try-catch explícito
```

**Recomendación:** Usa **RuntimeException** (unchecked) para errores de lógica de negocio.

---

## 🚫 Anti-Patrones

### 1️⃣ Catch Vacío

```java
❌ MAL:
try {
    createOrder(order);
} catch (Exception e) {
    // No hace nada
}
```

**Problema:** Error silencioso, imposible de debuggear.

```java
✅ BIEN:
try {
    createOrder(order);
} catch (Exception e) {
    log.error("Error creating order", e);
    throw new OrderCreationException("Failed to create order", e);
}
```

---

### 2️⃣ Catch Genérico

```java
❌ MAL:
try {
    createOrder(order);
} catch (Exception e) {
    return "Error";
}
```

**Problema:** Captura TODO, incluso errores que no deberías capturar.

```java
✅ BIEN:
try {
    createOrder(order);
} catch (InsufficientStockException e) {
    return "Not enough stock";
} catch (ResourceNotFoundException e) {
    return "Product not found";
}
```

---

### 3️⃣ Excepciones para Control de Flujo

```java
❌ MAL:
try {
    User user = getUserById(id);
    // ...
} catch (ResourceNotFoundException e) {
    // Usa excepción como if
    user = createDefaultUser();
}
```

**Problema:** Excepciones son para errores, NO para lógica normal.

```java
✅ BIEN:
Optional<User> userOpt = userRepository.findById(id);
User user = userOpt.orElseGet(() -> createDefaultUser());
```

---

## 📝 Checklist de Manejo de Errores

```
✅ Usar excepciones, no códigos de error
✅ Excepciones con nombres descriptivos
✅ Mensajes de error claros
✅ No devolver null (usar Optional o lanzar excepción)
✅ Excepciones personalizadas por dominio
✅ Manejo global de excepciones (@RestControllerAdvice)
✅ Log de errores importantes
✅ No capturar Exception genérico
✅ No catch vacío
✅ No usar excepciones para control de flujo
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Cómo manejas errores en tu aplicación?"**

> "Uso excepciones personalizadas con nombres descriptivos (`ResourceNotFoundException`, `InsufficientStockException`). Tengo un manejador global de excepciones (`@RestControllerAdvice`) que convierte excepciones en respuestas HTTP consistentes. Esto centraliza el manejo de errores y hace el código más limpio."

---

**2. "¿Por qué no usas códigos de error?"**

> "Porque las excepciones son más expresivas. Un código `-1` no dice nada, pero `IllegalArgumentException` o `ResourceNotFoundException` son autoexplicativos. Además, las excepciones obligan a manejar errores, mientras que los códigos se pueden ignorar fácilmente."

---

**3. "¿Tu código maneja errores correctamente?"**

> "Sí:
> - ✅ Excepciones personalizadas por dominio
> - ✅ Mensajes descriptivos
> - ✅ Manejo global con `@RestControllerAdvice`
> - ✅ No devuelvo `null`, lanzo excepciones o uso `Optional`
> - ✅ Log de errores con Lombok `@Slf4j`
> - ✅ Respuestas HTTP consistentes (`ErrorResponse`)"

---

**4. "¿Qué haces cuando falla una operación externa (como enviar email)?"**

> "Depende del caso. Si el email es crítico, lanzo excepción. Si es secundario (como notificación de bienvenida), logueo el error pero no fallo la operación principal. Por ejemplo, si crear usuario funciona pero enviar email falla, el usuario se crea igual y solo logueo el error del email."

---

## 🏆 Beneficios de Manejo Limpio de Errores

### 1. **Código Claro**

Excepciones descriptivas hacen obvio qué salió mal.

---

### 2. **Fácil de Debuggear**

Logs y stack traces muestran exactamente dónde falló.

---

### 3. **Mantenible**

Manejo centralizado en `@RestControllerAdvice`.

---

### 4. **Profesional**

Respuestas HTTP consistentes y bien estructuradas.

---

## 🚀 Conclusión

**Manejo limpio de errores:**
- ✅ Excepciones, no códigos de error
- ✅ Nombres descriptivos
- ✅ Manejo centralizado
- ✅ Log de errores
- ✅ Respuestas consistentes

**Baby Cash maneja errores de forma profesional y clara.**

---

**Ahora lee:** `EVITAR-CODIGO-DUPLICADO-DRY.md` para el siguiente principio. 🚀
