# COMENTARIOS: BUENOS VS MALOS

## 🎯 Regla de Oro

**El mejor comentario es el que NO necesitas escribir.**

El código debe ser **autoexplicativo**.

---

## ❓ ¿Por Qué Evitar Comentarios?

### Piensa en esto:

```java
❌ MAL:
// Incrementa el contador
counter++;
```

**Problema:** El comentario es obvio. El código ya dice "incrementa counter".

```java
✅ BIEN (sin comentario):
counter++;
```

---

## 🤔 ¿Cuándo SÍ Comentar?

### 1️⃣ Explicar "Por Qué", No "Qué"

```java
❌ MAL (explica "qué"):
// Divide por 2
int half = total / 2;

✅ BIEN (explica "por qué"):
// Dividimos el costo entre el comprador y el vendedor
int sharedCost = total / 2;
```

---

### 2️⃣ Workarounds y TODOs

```java
✅ BIEN:
// WORKAROUND: API externa devuelve null en lugar de lista vacía
// TODO: Reportar bug al proveedor (#ticket-123)
List<Product> products = apiClient.fetchProducts();
if (products == null) {
    products = new ArrayList<>();
}
```

**Aquí el comentario es útil** porque explica algo no obvio.

---

### 3️⃣ Advertencias Importantes

```java
✅ BIEN:
// ADVERTENCIA: No modificar este método sin actualizar la migración DB
// Ver: migration-20240115-update-orders.sql
public void updateOrderStatus(Order order, OrderStatus status) {
    // ...
}
```

---

### 4️⃣ Documentación de API Pública

```java
✅ BIEN:
/**
 * Crea una nueva orden.
 * 
 * @param request Datos de la orden
 * @return Orden creada con ID generado
 * @throws InsufficientStockException Si no hay stock suficiente
 * @throws ResourceNotFoundException Si el producto no existe
 */
public OrderResponse createOrder(CreateOrderRequest request) {
    // ...
}
```

**JavaDoc** para APIs públicas está bien.

---

## 🚫 Comentarios Malos

### 1️⃣ Comentarios Obvios

```java
❌ MAL:
// Obtiene el ID del usuario
Long userId = user.getId();

// Valida el email
if (!email.contains("@")) {
    throw new IllegalArgumentException("Invalid email");
}

// Guarda el producto
productRepository.save(product);
```

**Problema:** El código **YA** dice qué hace. Los comentarios no agregan valor.

---

### 2️⃣ Comentarios que Repiten el Código

```java
❌ MAL:
// Crea un nuevo producto con nombre, precio y stock
Product product = new Product();
product.setName(name);
product.setPrice(price);
product.setStock(stock);
```

**Problema:** Estamos leyendo el código. No necesitamos que nos lo repitan.

---

### 3️⃣ Comentarios Desactualizados

```java
❌ MAL:
// Envía email al usuario
public void notifyUser(User user) {
    // Código cambió a SMS pero comentario sigue diciendo "email"
    smsService.send(user.getPhone(), "Notification");
}
```

**Problema:** El comentario **miente**. Es peor que no tener comentario.

---

### 4️⃣ Comentarios de Código Viejo

```java
❌ MAL:
public void processOrder(Order order) {
    calculateTotal(order);
    
    // Old code:
    // BigDecimal discount = calculateDiscount(order);
    // order.setTotal(order.getTotal().subtract(discount));
    
    applyDiscount(order);
    saveOrder(order);
}
```

**Problema:** Usa Git para el historial, no comentarios.

---

### 5️⃣ Comentarios de "Ruido"

```java
❌ MAL:
/**
 * Constructor por defecto.
 */
public Product() {
}

/**
 * Getter para id.
 * @return el id
 */
public Long getId() {
    return id;
}

/**
 * Setter para id.
 * @param id el id a establecer
 */
public void setId(Long id) {
    this.id = id;
}
```

**Problema:** Comentarios inútiles que no agregan información.

---

### 6️⃣ Comentarios que Deberían Ser Funciones

```java
❌ MAL:
public void processOrder(Order order) {
    // Validar que el usuario existe y está activo
    User user = userRepository.findById(order.getUserId()).orElseThrow();
    if (!user.getEnabled()) {
        throw new UserNotActiveException();
    }
    
    // Calcular el total con descuentos
    BigDecimal total = BigDecimal.ZERO;
    for (OrderItem item : order.getItems()) {
        BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        if (item.hasDiscount()) {
            itemTotal = itemTotal.multiply(new BigDecimal("0.9"));
        }
        total = total.add(itemTotal);
    }
    
    // ...
}

✅ BIEN (sin comentarios, con funciones):
public void processOrder(Order order) {
    validateUserIsActive(order.getUserId());
    BigDecimal total = calculateTotalWithDiscounts(order);
    // ...
}

private void validateUserIsActive(Long userId) {
    User user = userRepository.findById(userId).orElseThrow();
    if (!user.getEnabled()) {
        throw new UserNotActiveException();
    }
}

private BigDecimal calculateTotalWithDiscounts(Order order) {
    return order.getItems().stream()
        .map(this::calculateItemTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}

private BigDecimal calculateItemTotal(OrderItem item) {
    BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    if (item.hasDiscount()) {
        itemTotal = itemTotal.multiply(new BigDecimal("0.9"));
    }
    return itemTotal;
}
```

**Ventaja:** El **nombre de la función** explica qué hace. No necesitas comentario.

---

## ✅ Código Autoexplicativo

### ❌ Antes (necesita comentarios):

```java
public void process(Order o) {
    // Verifica si el usuario es VIP
    if (o.getUser().getRole().equals("VIP")) {
        // Aplica descuento del 20%
        BigDecimal d = o.getTotal().multiply(new BigDecimal("0.2"));
        o.setTotal(o.getTotal().subtract(d));
    }
}
```

---

### ✅ Después (sin comentarios):

```java
public void applyVipDiscount(Order order) {
    if (isVipUser(order.getUser())) {
        BigDecimal discount = calculateVipDiscount(order.getTotal());
        order.setTotal(order.getTotal().subtract(discount));
    }
}

private boolean isVipUser(User user) {
    return user.getRole().equals("VIP");
}

private BigDecimal calculateVipDiscount(BigDecimal total) {
    BigDecimal vipDiscountPercentage = new BigDecimal("0.20");
    return total.multiply(vipDiscountPercentage);
}
```

**Observa:** Sin comentarios, pero **100% claro**.

---

## 🏗️ Baby Cash y Comentarios

### ✅ Ejemplo: ProductService (sin comentarios innecesarios)

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    // ✅ Código autoexplicativo, sin comentarios
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
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setEnabled(true);
        return product;
    }
}
```

**Observa:**
- ✅ Sin comentarios innecesarios
- ✅ Nombres de métodos descriptivos
- ✅ Código claro y directo

---

### ✅ Ejemplo: Comentarios Útiles en EmailService

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.admin-email}")
    private String adminEmail;
    
    // ✅ COMENTARIO ÚTIL: Explica comportamiento asíncrono
    /**
     * Envía email de forma asíncrona.
     * No bloquea la ejecución del código principal.
     */
    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Baby Cash!");
            helper.setText(buildWelcomeEmailBody(name), true);
            
            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Error sending welcome email to: {}", toEmail, e);
            // ✅ COMENTARIO ÚTIL: Explica por qué no lanzamos excepción
            // No lanzamos excepción porque el email es secundario
            // El usuario ya fue creado exitosamente
        }
    }
    
    // ✅ COMENTARIO ÚTIL: Explica workaround
    /**
     * WORKAROUND: GMail requiere HTML válido o rechaza el email.
     * Versión futura: usar plantillas Thymeleaf.
     * TODO: Migrar a sistema de plantillas (#ticket-456)
     */
    private String buildWelcomeEmailBody(String name) {
        return String.format(
            "<html><body><h1>Welcome %s!</h1></body></html>",
            name
        );
    }
}
```

**Observa:**
- ✅ Comentarios explican **por qué**, no **qué**
- ✅ TODOs con números de ticket
- ✅ Advertencias importantes

---

## 📊 Reglas de Comentarios

### ✅ Comentarios Buenos

| Tipo | Ejemplo |
|------|---------|
| **JavaDoc de API pública** | `/** Crea orden. @param request ... */` |
| **Advertencias** | `// ADVERTENCIA: No cambiar sin migración DB` |
| **TODOs** | `// TODO: Optimizar query (#ticket-123)` |
| **Workarounds** | `// WORKAROUND: API devuelve null` |
| **Explicar "por qué"** | `// Retry 3 veces porque API es inestable` |

---

### ❌ Comentarios Malos

| Tipo | Ejemplo |
|------|---------|
| **Obvios** | `// Incrementa contador` |
| **Ruido** | `// Constructor por defecto` |
| **Código viejo** | `// Old code: ...` |
| **Desactualizados** | `// Envía email` (pero envía SMS) |
| **Repiten código** | `// Crea producto con nombre y precio` |

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Por qué tu código tiene pocos comentarios?"**

> "Porque aplico Clean Code. El código debe ser autoexplicativo. Uso nombres descriptivos (`createOrder`, `validateStock`) en lugar de nombres cortos con comentarios. Los comentarios pueden quedar desactualizados, pero el código siempre está actualizado."

---

**2. "¿No es mejor tener más comentarios?"**

> "No necesariamente. Un comentario que dice `// Incrementa contador` no agrega valor. Es mejor escribir código claro. Solo comento cuando explico 'por qué' hago algo no obvio, como workarounds o decisiones de diseño."

---

**3. "¿Tu código sigue principios de comentarios limpios?"**

> "Sí:
> - ✅ Código autoexplicativo (nombres descriptivos)
> - ✅ Comentarios solo cuando son necesarios
> - ✅ Explican 'por qué', no 'qué'
> - ✅ TODOs con números de ticket
> - ✅ JavaDoc en APIs públicas
> - ✅ Sin comentarios obvios o desactualizados"

---

**4. "¿Qué haces cuando necesitas explicar código complejo?"**

> "Lo refactorizo en funciones más pequeñas con nombres descriptivos. Si una sección necesita comentario, probablemente debería ser una función separada. El nombre de la función explica qué hace."

---

## 📝 Checklist de Comentarios

```
✅ Código autoexplicativo (nombres claros)
✅ Comentarios explican "por qué", no "qué"
✅ TODOs con números de ticket
✅ JavaDoc en APIs públicas
✅ Advertencias importantes documentadas
✅ Sin comentarios obvios
✅ Sin código viejo comentado (usa Git)
✅ Sin comentarios desactualizados
```

---

## 🏆 Jerarquía de Soluciones

Cuando necesitas explicar algo:

```
1️⃣ MEJOR: Refactorizar código para que sea claro
   ↓
2️⃣ BUENO: Extraer a función con nombre descriptivo
   ↓
3️⃣ ACEPTABLE: Agregar comentario que explica "por qué"
   ↓
4️⃣ MAL: Agregar comentario que explica "qué"
```

---

## 📈 Antes y Después

### ❌ ANTES: Código con muchos comentarios

```java
public void processOrder(Order order) {
    // Validar orden
    if (order == null) {
        throw new IllegalArgumentException("Order cannot be null");
    }
    
    // Obtener items
    List<OrderItem> items = order.getItems();
    
    // Validar items
    if (items.isEmpty()) {
        throw new IllegalArgumentException("Order must have items");
    }
    
    // Calcular total
    BigDecimal total = BigDecimal.ZERO;
    for (OrderItem item : items) {
        // Obtener precio
        BigDecimal price = item.getPrice();
        // Obtener cantidad
        int quantity = item.getQuantity();
        // Calcular subtotal
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
        // Sumar al total
        total = total.add(subtotal);
    }
    
    // Guardar orden
    orderRepository.save(order);
}
```

---

### ✅ DESPUÉS: Código sin comentarios innecesarios

```java
public void processOrder(Order order) {
    validateOrder(order);
    validateOrderItems(order.getItems());
    BigDecimal total = calculateTotal(order.getItems());
    order.setTotal(total);
    orderRepository.save(order);
}

private void validateOrder(Order order) {
    if (order == null) {
        throw new IllegalArgumentException("Order cannot be null");
    }
}

private void validateOrderItems(List<OrderItem> items) {
    if (items.isEmpty()) {
        throw new IllegalArgumentException("Order must have items");
    }
}

private BigDecimal calculateTotal(List<OrderItem> items) {
    return items.stream()
        .map(this::calculateItemSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}

private BigDecimal calculateItemSubtotal(OrderItem item) {
    return item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
}
```

**Observa:** Sin comentarios, pero **más claro**.

---

## 🚀 Conclusión

**El mejor comentario es el que NO necesitas escribir.**

Código limpio:
- ✅ Nombres descriptivos
- ✅ Funciones pequeñas
- ✅ Autoexplicativo
- ✅ Comentarios solo cuando son necesarios

**Baby Cash tiene código autoexplicativo con mínimos comentarios.**

---

**Ahora lee:** `FORMATEO-CODIGO.md` para el siguiente principio. 🚀
