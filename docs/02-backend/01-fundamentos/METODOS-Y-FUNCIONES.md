# 🔧 MÉTODOS Y FUNCIONES EN JAVA

## 🎯 ¿Qué es un Método?

**Explicación Simple:**
Un método es un **bloque de código que realiza una tarea específica**. Es como una **receta** que puedes usar varias veces.

**Explicación Técnica:**
Un método es una función asociada a una clase que encapsula comportamiento reutilizable.

---

## 📝 Sintaxis

```java
modificador tipoRetorno nombreMetodo(parametros) {
    // código
    return valor; // si tipoRetorno no es void
}
```

---

## 🎓 Ejemplo Básico

```java
public class Calculator {
    
    // Método que suma dos números
    public int sumar(int a, int b) {
        return a + b;
    }
    
    // Método sin retorno (void)
    public void imprimirMensaje(String mensaje) {
        System.out.println(mensaje);
    }
    
    // Método sin parámetros
    public String obtenerSaludo() {
        return "Hola Mundo";
    }
}

// Usar los métodos
Calculator calc = new Calculator();
int resultado = calc.sumar(5, 3);      // 8
calc.imprimirMensaje("Hola");          // Imprime: Hola
String saludo = calc.obtenerSaludo();  // "Hola Mundo"
```

---

## 📋 Componentes de un Método

### 1. Modificador de Acceso
```java
public    // Accesible desde cualquier lugar
private   // Solo dentro de la clase
protected // Dentro de la clase y subclases
```

### 2. Tipo de Retorno
```java
void              // No retorna nada
int               // Retorna número entero
String            // Retorna texto
BigDecimal        // Retorna decimal
List<Product>     // Retorna lista de productos
boolean           // Retorna verdadero/falso
```

### 3. Nombre del Método
- Debe empezar con minúscula
- Usa camelCase: `calculateTotal`, `getUserById`
- Debe describir qué hace: `sendEmail`, `validateStock`

### 4. Parámetros (opcionales)
```java
public void metodoSinParametros() { }

public void metodoConUnParametro(String nombre) { }

public void metodoConVariosParametros(String nombre, int edad, boolean activo) { }
```

### 5. Cuerpo del Método
```java
public int sumar(int a, int b) {
    int resultado = a + b;  // Cuerpo del método
    return resultado;
}
```

---

## 🔄 Tipos de Métodos

### 1. Métodos con Retorno

```java
public class ProductService {
    
    // Retorna un producto
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }
    
    // Retorna una lista
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Retorna un booleano
    public boolean hasStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        return product.getStock() >= quantity;
    }
    
    // Retorna BigDecimal
    public BigDecimal calculateDiscount(BigDecimal price, int percent) {
        return price.multiply(BigDecimal.valueOf(percent / 100.0));
    }
}
```

### 2. Métodos sin Retorno (void)

```java
public class EmailService {
    
    // No retorna nada, solo envía email
    public void sendWelcomeEmail(String to, String name) {
        String subject = "Bienvenido a BabyCash";
        String body = "Hola " + name + ", gracias por registrarte.";
        mailSender.send(to, subject, body);
    }
    
    // No retorna nada, solo guarda en BD
    public void logAction(String action, String userEmail) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setUserEmail(userEmail);
        auditRepository.save(log);
    }
}
```

---

## 📊 Parámetros

### Parámetros Simples

```java
public void ejemploParametros(
    int edad,                    // Primitivo
    String nombre,               // Objeto
    boolean activo,              // Primitivo
    LocalDateTime fecha          // Objeto
) {
    // Usar parámetros
    System.out.println(nombre + " tiene " + edad + " años");
}
```

### Parámetros Complejos (Objetos)

```java
public class OrderService {
    
    // Recibe objeto CreateOrderRequest
    public OrderResponse createOrder(CreateOrderRequest request, String userEmail) {
        // Acceder a propiedades del objeto
        String address = request.getShippingAddress();
        String paymentMethod = request.getPaymentMethod();
        
        // Lógica...
        return orderResponse;
    }
}
```

### Múltiples Parámetros del Mismo Tipo

```java
// ❌ Confuso
public void crearUsuario(String a, String b, String c) {
    // ¿Qué es a, b, c?
}

// ✅ Claro
public void crearUsuario(String nombre, String email, String password) {
    // Se entiende perfectamente
}
```

---

## 🎯 Ejemplos del Proyecto

### 1. CartService - addToCart()

```java
@Service
public class CartService {
    
    /**
     * Agrega un producto al carrito
     * 
     * @param userEmail - Email del usuario
     * @param productId - ID del producto
     * @param quantity - Cantidad a agregar
     * @return CartResponse - Carrito actualizado
     */
    public CartResponse addToCart(String userEmail, Long productId, int quantity) {
        // 1. Buscar carrito
        Cart cart = cartRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new NotFoundException("Carrito no encontrado"));
        
        // 2. Buscar producto
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        
        // 3. Validar stock (llamada a otro método)
        validateStock(product, quantity);
        
        // 4. Agregar o actualizar item
        CartItem item = findOrCreateCartItem(cart, product, quantity);
        
        // 5. Guardar
        cart = cartRepository.save(cart);
        
        // 6. Retornar respuesta
        return convertToResponse(cart);
    }
    
    // Método privado auxiliar (solo se usa dentro de CartService)
    private void validateStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new BadRequestException("Stock insuficiente");
        }
    }
    
    // Método privado auxiliar
    private CartItem findOrCreateCartItem(Cart cart, Product product, int quantity) {
        // Buscar si el producto ya está en el carrito
        CartItem existingItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(product.getId()))
            .findFirst()
            .orElse(null);
        
        if (existingItem != null) {
            // Ya existe, aumentar cantidad
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return existingItem;
        } else {
            // No existe, crear nuevo
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
            return newItem;
        }
    }
    
    // Método privado de conversión
    private CartResponse convertToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setTotal(calculateTotal(cart));
        // ... más conversiones
        return response;
    }
    
    // Método para calcular total
    private BigDecimal calculateTotal(Cart cart) {
        return cart.getItems().stream()
            .map(item -> item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

**Conceptos:**
- `addToCart()`: Método público (puede ser llamado desde fuera)
- `validateStock()`: Método privado (solo se usa internamente)
- Parámetros: `userEmail`, `productId`, `quantity`
- Retorno: `CartResponse`

### 2. AuthService - register()

```java
@Service
public class AuthService {
    
    /**
     * Registra un nuevo usuario
     */
    public AuthResponse register(RegisterRequest request) {
        // 1. Validar que email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email ya registrado");
        }
        
        // 2. Crear usuario
        User user = createUserFromRequest(request);
        
        // 3. Encriptar contraseña
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // 4. Guardar usuario
        user = userRepository.save(user);
        
        // 5. Crear carrito para el usuario
        createCartForUser(user);
        
        // 6. Generar JWT
        String token = jwtUtil.generateToken(user.getEmail());
        
        // 7. Enviar email de bienvenida
        sendWelcomeEmail(user);
        
        // 8. Retornar respuesta
        return buildAuthResponse(user, token);
    }
    
    // Métodos auxiliares privados
    
    private User createUserFromRequest(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.USER);
        user.setActive(true);
        return user;
    }
    
    private void createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
    }
    
    private void sendWelcomeEmail(User user) {
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
    }
    
    private AuthResponse buildAuthResponse(User user, String token) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole().toString());
        return response;
    }
}
```

### 3. OrderService - createOrder()

```java
@Service
public class OrderService {
    
    /**
     * Crea una orden a partir del carrito del usuario
     */
    @Transactional  // Asegura que todo se ejecute o nada
    public OrderResponse createOrder(CreateOrderRequest request, String userEmail) {
        // 1. Obtener carrito
        Cart cart = getCartByUserEmail(userEmail);
        
        // 2. Validar que tenga items
        validateCartNotEmpty(cart);
        
        // 3. Validar stock de todos los productos
        validateAllProductsStock(cart);
        
        // 4. Crear orden
        Order order = buildOrder(cart, request);
        
        // 5. Crear order items
        List<OrderItem> orderItems = createOrderItems(cart, order);
        order.setItems(orderItems);
        
        // 6. Calcular total
        BigDecimal total = calculateOrderTotal(orderItems);
        order.setTotal(total);
        
        // 7. Guardar orden
        order = orderRepository.save(order);
        
        // 8. Reducir stock de productos
        reduceProductsStock(cart);
        
        // 9. Crear pago
        Payment payment = createPayment(order, request.getPaymentMethod());
        
        // 10. Agregar puntos de lealtad
        addLoyaltyPoints(userEmail, total);
        
        // 11. Limpiar carrito
        clearCart(cart);
        
        // 12. Enviar email de confirmación
        sendOrderConfirmationEmail(order);
        
        // 13. Retornar respuesta
        return convertToOrderResponse(order);
    }
    
    // Métodos auxiliares (todos privados)
    
    private Cart getCartByUserEmail(String email) {
        return cartRepository.findByUserEmail(email)
            .orElseThrow(() -> new NotFoundException("Carrito no encontrado"));
    }
    
    private void validateCartNotEmpty(Cart cart) {
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }
    }
    
    private void validateAllProductsStock(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new BadRequestException(
                    "Stock insuficiente para: " + product.getName()
                );
            }
        }
    }
    
    private Order buildOrder(Cart cart, CreateOrderRequest request) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderNumber(generateOrderNumber());
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }
    
    private List<OrderItem> createOrderItems(Cart cart, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice()); // Precio histórico
            orderItems.add(orderItem);
        }
        
        return orderItems;
    }
    
    private BigDecimal calculateOrderTotal(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getPrice().multiply(
                BigDecimal.valueOf(item.getQuantity())
            ))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private void reduceProductsStock(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
    }
    
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
}
```

---

## 📚 Buenas Prácticas

### 1. Un Método = Una Responsabilidad

```java
// ❌ MAL - Hace demasiadas cosas
public void processOrder(Order order) {
    validateOrder(order);
    calculateTotal(order);
    saveOrder(order);
    sendEmail(order);
    updateStock(order);
    createInvoice(order);
}

// ✅ BIEN - Divide en métodos pequeños
public OrderResponse processOrder(CreateOrderRequest request) {
    Order order = createOrder(request);
    processPayment(order);
    notifyCustomer(order);
    return convertToResponse(order);
}
```

### 2. Nombres Descriptivos

```java
// ❌ MAL
public void p(int id) { }
public String get() { }
public void doStuff() { }

// ✅ BIEN
public void deleteProduct(int productId) { }
public String getUserEmail() { }
public void validateStock() { }
```

### 3. Métodos Cortos

```java
// ❌ MAL - Método de 200 líneas

// ✅ BIEN - Métodos de 10-20 líneas máximo
// Divide en métodos auxiliares privados
```

### 4. Evitar Efectos Secundarios

```java
// ❌ MAL - Modifica cosas inesperadas
public int calculateTotal(Cart cart) {
    cart.setLastUpdated(LocalDateTime.now()); // ⚠️ Efecto secundario
    return cart.getItems().size();
}

// ✅ BIEN - Solo calcula y retorna
public int calculateTotal(Cart cart) {
    return cart.getItems().size();
}
```

---

## 🔄 Sobrecarga de Métodos (Overloading)

Puedes tener **varios métodos con el mismo nombre** pero **diferentes parámetros**:

```java
public class ProductService {
    
    // Buscar todos los productos
    public List<Product> getProducts() {
        return productRepository.findAll();
    }
    
    // Buscar productos por categoría
    public List<Product> getProducts(String category) {
        return productRepository.findByCategory(category);
    }
    
    // Buscar productos con paginación
    public List<Product> getProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size))
            .getContent();
    }
    
    // Buscar productos por categoría con paginación
    public List<Product> getProducts(String category, int page, int size) {
        return productRepository.findByCategory(
            category, 
            PageRequest.of(page, size)
        ).getContent();
    }
}
```

---

## 📋 Resumen

| Concepto | Descripción | Ejemplo |
|----------|-------------|---------|
| **Método** | Bloque de código reutilizable | `public void sendEmail() {...}` |
| **Parámetro** | Valor que recibe el método | `(String email, int age)` |
| **Retorno** | Valor que devuelve el método | `return "Hola";` |
| **void** | Método sin retorno | `public void log() {...}` |
| **public** | Accesible desde fuera | Usado en Services |
| **private** | Solo dentro de la clase | Métodos auxiliares |

---

**Última actualización**: Octubre 2025
