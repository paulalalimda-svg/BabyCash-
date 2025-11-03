# FUNCIONES Y MÉTODOS LIMPIOS

## 🎯 Regla de Oro

**Una función debe hacer UNA SOLA COSA, hacerla bien, y SOLO eso.**

---

## ❓ ¿Qué es una Función Limpia?

### Analogía: Receta de Cocina

```
❌ MAL (función que hace muchas cosas):
"Preparar cena":
1. Compra ingredientes
2. Cocina pasta
3. Prepara ensalada
4. Hornea pan
5. Lava platos
6. Sirve la mesa

✅ BIEN (funciones específicas):
"Cocinar pasta": Solo cocina pasta
"Preparar ensalada": Solo ensalada
"Hornear pan": Solo pan
```

---

## 📏 Tamaño de Funciones

### Regla: Máximo 20 Líneas

```java
❌ MAL (función gigante de 100 líneas):
public void processOrder(Order order) {
    // Validación (20 líneas)
    if (order == null) throw new Exception();
    if (order.getItems().isEmpty()) throw new Exception();
    // ... más validaciones
    
    // Cálculo de descuentos (30 líneas)
    BigDecimal discount = BigDecimal.ZERO;
    if (order.getUser().isVip()) {
        discount = order.getTotal().multiply(new BigDecimal("0.1"));
    }
    // ... más cálculos
    
    // Actualización de stock (30 líneas)
    for (OrderItem item : order.getItems()) {
        Product product = productRepository.findById(item.getProductId()).get();
        product.setStock(product.getStock() - item.getQuantity());
        productRepository.save(product);
    }
    // ... más actualizaciones
    
    // Envío de emails (20 líneas)
    String email = order.getUser().getEmail();
    emailService.sendOrderConfirmation(email);
    // ... más emails
}

✅ BIEN (funciones pequeñas):
public void processOrder(Order order) {
    validateOrder(order);
    BigDecimal discount = calculateDiscount(order);
    updateStock(order);
    sendConfirmationEmail(order);
}

private void validateOrder(Order order) {
    if (order == null) {
        throw new IllegalArgumentException("Order cannot be null");
    }
    if (order.getItems().isEmpty()) {
        throw new IllegalArgumentException("Order must have items");
    }
}

private BigDecimal calculateDiscount(Order order) {
    if (order.getUser().isVip()) {
        return order.getTotal().multiply(new BigDecimal("0.1"));
    }
    return BigDecimal.ZERO;
}

private void updateStock(Order order) {
    for (OrderItem item : order.getItems()) {
        Product product = getProduct(item.getProductId());
        reduceStock(product, item.getQuantity());
    }
}

private void sendConfirmationEmail(Order order) {
    String email = order.getUser().getEmail();
    emailService.sendOrderConfirmation(email);
}
```

---

## 🎯 Una Función, Una Responsabilidad

### ❌ MAL: Función que hace 3 cosas

```java
public User createUser(String email, String password) {
    // 1. Validar
    if (email == null || !email.contains("@")) {
        throw new IllegalArgumentException("Invalid email");
    }
    
    // 2. Encriptar password
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    
    // 3. Guardar
    User user = new User(email, hashedPassword);
    userRepository.save(user);
    
    // 4. Enviar email
    emailService.sendWelcomeEmail(email);
    
    return user;
}
```

---

### ✅ BIEN: Una función por responsabilidad

```java
public User createUser(String email, String password) {
    validateEmail(email);
    String hashedPassword = encryptPassword(password);
    User user = saveUser(email, hashedPassword);
    sendWelcomeEmail(user);
    return user;
}

private void validateEmail(String email) {
    if (email == null || !email.contains("@")) {
        throw new IllegalArgumentException("Invalid email");
    }
}

private String encryptPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
}

private User saveUser(String email, String hashedPassword) {
    User user = new User(email, hashedPassword);
    return userRepository.save(user);
}

private void sendWelcomeEmail(User user) {
    emailService.sendWelcomeEmail(user.getEmail());
}
```

**Ventajas:**
- ✅ Cada función se puede probar independientemente
- ✅ Fácil de entender
- ✅ Fácil de modificar (cambiar validación no afecta encriptación)

---

## 📊 Parámetros

### Regla: Máximo 3 Parámetros

#### ❌ MAL: Demasiados parámetros

```java
public Order createOrder(
    Long userId,
    String address,
    String city,
    String zipCode,
    String country,
    String phoneNumber,
    List<OrderItem> items,
    String paymentMethod,
    String shippingMethod,
    String couponCode
) {
    // 10 parámetros = confuso
}
```

---

#### ✅ BIEN: Agrupar en objetos

```java
public Order createOrder(Long userId, OrderDetails details, PaymentInfo payment) {
    // 3 parámetros = claro
}

// Objetos agrupan datos relacionados
public class OrderDetails {
    private String address;
    private String city;
    private String zipCode;
    private String country;
    private String phoneNumber;
    private List<OrderItem> items;
}

public class PaymentInfo {
    private String paymentMethod;
    private String shippingMethod;
    private String couponCode;
}
```

---

### ✅ Ejemplo Real: Baby Cash

```java
// ✅ BIEN: 1 parámetro (DTO agrupa todo)
@PostMapping
public ResponseEntity<OrderResponse> createOrder(
    @RequestBody CreateOrderRequest request
) {
    OrderResponse order = orderService.createOrder(request);
    return ResponseEntity.ok(order);
}
```

---

## 🔄 Funciones sin Efectos Secundarios

### ❌ MAL: Efecto secundario oculto

```java
public boolean checkPassword(String username, String password) {
    User user = userRepository.findByUsername(username);
    if (passwordEncoder.matches(password, user.getPassword())) {
        // ❌ EFECTO SECUNDARIO: modifica sesión
        session.initialize(user);
        return true;
    }
    return false;
}
```

**Problema:** El nombre dice "check" (verificar), pero también **inicializa sesión**. Esto es un efecto secundario oculto.

---

### ✅ BIEN: Sin efectos secundarios

```java
// ✅ Solo verifica
public boolean checkPassword(String username, String password) {
    User user = userRepository.findByUsername(username);
    return passwordEncoder.matches(password, user.getPassword());
}

// ✅ Función separada para inicializar sesión
public void initializeSession(User user) {
    session.initialize(user);
}

// ✅ Uso explícito
if (checkPassword(username, password)) {
    User user = userRepository.findByUsername(username);
    initializeSession(user);
}
```

---

## 📝 Nombres Descriptivos

### ❌ Nombres Malos

```java
void proc();  // ¿Procesar qué?
int calc(int x);  // ¿Calcular qué?
String get();  // ¿Obtener qué?
void do();  // ¿Hacer qué?
```

---

### ✅ Nombres Buenos

```java
void processOrder();
int calculateTotalPrice(int basePrice);
String getUserEmail();
void sendConfirmationEmail();
```

**Regla:** Si no puedes poner un nombre descriptivo, la función hace demasiadas cosas.

---

## 🚫 Evitar Banderas Booleanas

### ❌ MAL: Bandera booleana

```java
public void sendEmail(String email, boolean isUrgent) {
    if (isUrgent) {
        // Lógica para urgente
        sendUrgentEmail(email);
    } else {
        // Lógica para normal
        sendNormalEmail(email);
    }
}
```

**Problema:** La función hace **DOS COSAS** dependiendo del booleano.

---

### ✅ BIEN: Dos funciones separadas

```java
public void sendUrgentEmail(String email) {
    // Lógica para urgente
}

public void sendNormalEmail(String email) {
    // Lógica para normal
}
```

**Ventaja:** Intención clara desde el nombre.

---

## 🎨 Niveles de Abstracción

### ❌ MAL: Mezcla niveles de abstracción

```java
public void processOrder(Order order) {
    // ALTO NIVEL
    validateOrder(order);
    
    // BAJO NIVEL (detalles de implementación)
    String sql = "UPDATE products SET stock = stock - ? WHERE id = ?";
    jdbcTemplate.update(sql, order.getQuantity(), order.getProductId());
    
    // ALTO NIVEL
    sendConfirmationEmail(order);
}
```

---

### ✅ BIEN: Un solo nivel de abstracción

```java
public void processOrder(Order order) {
    // TODO EN ALTO NIVEL
    validateOrder(order);
    updateStock(order);
    sendConfirmationEmail(order);
}

// Detalles de implementación ocultos en función separada
private void updateStock(Order order) {
    String sql = "UPDATE products SET stock = stock - ? WHERE id = ?";
    jdbcTemplate.update(sql, order.getQuantity(), order.getProductId());
}
```

---

## 🏗️ Funciones en Baby Cash

### ✅ Ejemplo: ProductService

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    // ✅ Funciones pequeñas y específicas
    
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
    
    // ✅ Funciones auxiliares privadas
    
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
        // ... más setters
        return product;
    }
    
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        // ... más setters
        return response;
    }
}
```

**Observa:**
- ✅ Funciones públicas: coordinan la lógica
- ✅ Funciones privadas: detalles de implementación
- ✅ Cada función hace UNA cosa
- ✅ Nombres descriptivos

---

### ✅ Ejemplo: OrderService

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ILoyaltyService loyaltyService;
    
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // ✅ Función de coordinación (alto nivel)
        validateOrderRequest(request);
        List<OrderItem> items = buildOrderItems(request.getItems());
        BigDecimal total = calculateTotal(items);
        Order order = saveOrder(request, items, total);
        addLoyaltyPoints(order);
        return mapToResponse(order);
    }
    
    // ✅ Funciones auxiliares (detalles de implementación)
    
    private void validateOrderRequest(CreateOrderRequest request) {
        if (request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have items");
        }
    }
    
    private List<OrderItem> buildOrderItems(List<OrderItemRequest> itemRequests) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : itemRequests) {
            Product product = getProduct(itemRequest.getProductId());
            validateStock(product, itemRequest.getQuantity());
            OrderItem item = new OrderItem(product, itemRequest.getQuantity());
            items.add(item);
        }
        return items;
    }
    
    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
    
    private void validateStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Not enough stock");
        }
    }
    
    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private Order saveOrder(CreateOrderRequest request, List<OrderItem> items, BigDecimal total) {
        Order order = new Order();
        order.setUser(getUser(request.getUserId()));
        order.setItems(items);
        order.setTotal(total);
        return orderRepository.save(order);
    }
    
    private void addLoyaltyPoints(Order order) {
        loyaltyService.addPoints(order.getUser(), order.getTotal());
    }
    
    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(order);
    }
}
```

---

## 📊 Estructura de Función Ideal

```java
public ReturnType functionName(Parameters) {
    // 1. Validación (si es necesaria)
    validateInput();
    
    // 2. Lógica principal
    Result result = processData();
    
    // 3. Return
    return result;
}
```

**Máximo 20 líneas**, idealmente **10 líneas**.

---

## 🚫 Anti-Patrones

### 1️⃣ Función Dios (hace todo)

```java
❌ MAL:
public void processEverything(Order order) {
    // Validar (10 líneas)
    // Calcular (20 líneas)
    // Guardar (15 líneas)
    // Enviar email (10 líneas)
    // Actualizar stock (20 líneas)
    // Generar reporte (30 líneas)
    // ... 200 líneas totales
}
```

---

### 2️⃣ Función con Demasiados Parámetros

```java
❌ MAL:
public Order createOrder(
    Long userId,
    String name,
    String email,
    String address,
    String city,
    String zipCode,
    List<Long> productIds,
    List<Integer> quantities,
    String paymentMethod
) {
    // ...
}
```

---

### 3️⃣ Función con Lógica Condicional Compleja

```java
❌ MAL:
public BigDecimal calculatePrice(Order order) {
    if (order.getUser().isVip()) {
        if (order.getTotal().compareTo(new BigDecimal("100")) > 0) {
            if (order.hasCoupon()) {
                return order.getTotal().multiply(new BigDecimal("0.7"));
            } else {
                return order.getTotal().multiply(new BigDecimal("0.8"));
            }
        } else {
            return order.getTotal().multiply(new BigDecimal("0.9"));
        }
    } else {
        if (order.hasCoupon()) {
            return order.getTotal().multiply(new BigDecimal("0.95"));
        } else {
            return order.getTotal();
        }
    }
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Por qué tantas funciones pequeñas?"**

> "Porque cada función hace UNA cosa. Si `createOrder` tuviera 200 líneas, sería imposible de entender, mantener y probar. Con funciones pequeñas, cada una se puede testear independientemente, y el código es más fácil de leer."

---

**2. "¿No es más lento tener tantas funciones?"**

> "No. El compilador optimiza las llamadas a funciones. La diferencia de rendimiento es insignificante, pero la mejora en legibilidad y mantenibilidad es enorme."

---

**3. "¿Tu código sigue principios de funciones limpias?"**

> "Sí:
> - ✅ Funciones pequeñas (máximo 20 líneas)
> - ✅ Una responsabilidad por función
> - ✅ Nombres descriptivos (`createOrder`, `validateStock`)
> - ✅ Máximo 3 parámetros (usamos DTOs)
> - ✅ Sin efectos secundarios ocultos
> - ✅ Un solo nivel de abstracción"

---

**4. "¿Cómo decides cuándo extraer una función?"**

> "Si una función tiene más de 20 líneas, o si parte de la lógica se puede reutilizar, o si necesito comentar qué hace una sección, la extraigo a una función separada con nombre descriptivo."

---

## 📝 Checklist de Funciones Limpias

```
✅ Máximo 20 líneas por función
✅ Una responsabilidad por función
✅ Nombres descriptivos (verbos)
✅ Máximo 3 parámetros
✅ Sin efectos secundarios ocultos
✅ Un solo nivel de abstracción
✅ Sin banderas booleanas
✅ Código DRY (sin repeticiones)
```

---

## 🏆 Beneficios

### 1. **Fácil de Entender**

Lees el nombre de la función y sabes qué hace.

---

### 2. **Fácil de Probar**

Cada función se prueba independientemente.

---

### 3. **Fácil de Mantener**

Cambiar una función no afecta a las demás.

---

### 4. **Reutilizable**

Funciones pequeñas se pueden usar en múltiples lugares.

---

## 🚀 Conclusión

**Funciones limpias:**
- ✅ Pequeñas (máximo 20 líneas)
- ✅ Hacen UNA cosa
- ✅ Nombres descriptivos
- ✅ Pocos parámetros
- ✅ Sin efectos secundarios

**Baby Cash aplica estos principios en todos sus servicios.**

---

**Ahora lee:** `COMENTARIOS-BUENOS-VS-MALOS.md` para el siguiente principio. 🚀
