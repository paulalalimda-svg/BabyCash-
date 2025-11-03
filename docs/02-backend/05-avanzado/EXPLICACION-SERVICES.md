# 💼 EXPLICACIÓN DE SERVICIOS (SERVICES) - BACKEND

## 📌 ¿Qué son los Services?

### 🎯 Explicación Simple
Los **Services** son como el "cerebro" de la aplicación. Contienen toda la **lógica de negocio**, es decir, las reglas de cómo funciona la aplicación.

**Ejemplo del mundo real:**
Imagina una tienda física:
- El **Controller** es el cajero que atiende al cliente
- El **Service** es el gerente que toma decisiones (¿hay stock? ¿se puede hacer descuento?)
- El **Repository** es el almacén donde se guardan los productos

### 🔧 Explicación Técnica
Los Services implementan el patrón **Service Layer** que:
- Encapsula la lógica de negocio
- Coordina operaciones entre múltiples repositorios
- Aplica validaciones de negocio
- Maneja transacciones
- Es independiente de la capa de presentación (Controllers)

---

## 📂 Ubicación de Services

```
backend/src/main/java/com/babycash/backend/service/
├── AuthService.java                  # Autenticación y registro
├── UserService.java                  # Gestión de usuarios
├── ProductService.java               # Lógica de productos
├── CartService.java                  # Lógica del carrito
├── OrderService.java                 # Procesamiento de órdenes
├── PaymentService.java               # Procesamiento de pagos
├── BlogPostService.java              # Gestión de blog
├── IBlogPostService.java             # Interfaz de BlogPostService
├── BlogCommentService.java           # Comentarios del blog
├── TestimonialService.java           # Gestión de testimonios
├── ContactMessageService.java        # Mensajes de contacto
├── ContactInfoService.java           # Información de contacto
├── LoyaltyService.java               # Sistema de puntos
├── ILoyaltyService.java              # Interfaz de LoyaltyService
├── EmailService.java                 # Envío de emails
├── RefreshTokenService.java          # Tokens de refresco
└── AuditService.java                 # Registro de auditoría
```

---

## 🔐 1. AuthService.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/service/AuthService.java`

### 🎯 ¿Qué hace?
Maneja todo lo relacionado con **autenticación y autorización** (login, registro, recuperación de contraseña).

### 📝 Explicación Simple
Es como el **sistema de seguridad** de un edificio:
- Verifica tu identidad cuando entras (login)
- Te da una tarjeta de acceso (JWT token)
- Te registra cuando eres nuevo (registro)
- Te ayuda si olvidaste tu contraseña

### 🔧 Métodos Principales

#### 1️⃣ `register()` - Registrar Usuario

**Explicación Simple:**
Crea una cuenta nueva para un usuario.

**Explicación Técnica:**
```java
public AuthResponse register(RegisterRequest request) {
    // 1. Validar que el email no esté registrado
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new BusinessException("El email ya está registrado");
    }
    
    // 2. Encriptar la contraseña (BCrypt)
    String hashedPassword = passwordEncoder.encode(request.getPassword());
    
    // 3. Crear entidad User
    User user = User.builder()
        .email(request.getEmail())
        .password(hashedPassword)
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .role(UserRole.USER)  // Rol por defecto
        .createdAt(LocalDateTime.now())
        .build();
    
    // 4. Guardar en base de datos
    User savedUser = userRepository.save(user);
    
    // 5. Crear carrito vacío para el usuario
    Cart cart = Cart.builder()
        .user(savedUser)
        .items(new ArrayList<>())
        .build();
    cartRepository.save(cart);
    
    // 6. Generar token JWT
    String token = jwtUtil.generateToken(savedUser.getEmail());
    
    // 7. Enviar email de bienvenida (asíncrono)
    emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());
    
    // 8. Retornar respuesta con token
    return AuthResponse.builder()
        .token(token)
        .email(savedUser.getEmail())
        .firstName(savedUser.getFirstName())
        .role(savedUser.getRole().name())
        .build();
}
```

**Flujo:**
```
Cliente envía:
{
  "email": "maria@example.com",
  "password": "123456",
  "firstName": "María",
  "lastName": "García"
}

↓ AuthService.register()

1. ✅ Verifica que email no exista
2. 🔒 Encripta password: "123456" → "$2a$10$xKJ..."
3. 💾 Guarda usuario en BD
4. 🛒 Crea carrito vacío
5. 🎫 Genera JWT token
6. 📧 Envía email de bienvenida

↓ Retorna:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "email": "maria@example.com",
  "firstName": "María",
  "role": "USER"
}
```

---

#### 2️⃣ `login()` - Iniciar Sesión

**Explicación Simple:**
Verifica tu email y contraseña, y te da acceso.

**Explicación Técnica:**
```java
public AuthResponse login(LoginRequest request) {
    // 1. Buscar usuario por email
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new AuthenticationException("Credenciales inválidas"));
    
    // 2. Verificar contraseña (compara hash)
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new AuthenticationException("Credenciales inválidas");
    }
    
    // 3. Verificar que la cuenta esté activa
    if (!user.isActive()) {
        throw new BusinessException("Cuenta desactivada");
    }
    
    // 4. Actualizar última fecha de login
    user.setLastLoginAt(LocalDateTime.now());
    userRepository.save(user);
    
    // 5. Generar nuevo token JWT
    String token = jwtUtil.generateToken(user.getEmail());
    
    // 6. Generar refresh token (para renovar el token principal)
    String refreshToken = refreshTokenService.createRefreshToken(user.getId());
    
    // 7. Registrar login en auditoría
    auditService.logLogin(user.getId(), request.getIpAddress());
    
    // 8. Retornar respuesta
    return AuthResponse.builder()
        .token(token)
        .refreshToken(refreshToken)
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .role(user.getRole().name())
        .build();
}
```

**Flujo:**
```
Cliente envía:
{
  "email": "maria@example.com",
  "password": "123456"
}

↓ AuthService.login()

1. 🔍 Busca usuario en BD
2. 🔐 Verifica password: "123456" vs "$2a$10$xKJ..."
3. ✅ Verifica que cuenta esté activa
4. 📅 Actualiza lastLoginAt
5. 🎫 Genera JWT token (expira en 24h)
6. 🔄 Genera refresh token (expira en 7 días)
7. 📝 Registra en audit_logs

↓ Retorna:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "550e8400-e29b-41d4-a716...",
  "email": "maria@example.com",
  "role": "USER"
}
```

---

#### 3️⃣ `forgotPassword()` - Recuperar Contraseña

**Explicación Simple:**
Si olvidas tu contraseña, te envía un email con un link para crear una nueva.

**Explicación Técnica:**
```java
public void forgotPassword(ForgotPasswordRequest request) {
    // 1. Buscar usuario por email
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    // 2. Generar token aleatorio (UUID)
    String resetToken = UUID.randomUUID().toString();
    
    // 3. Guardar token en BD con expiración (1 hora)
    user.setPasswordResetToken(resetToken);
    user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
    userRepository.save(user);
    
    // 4. Crear URL de reseteo
    String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
    
    // 5. Enviar email con el link (asíncrono)
    emailService.sendPasswordResetEmail(
        user.getEmail(),
        user.getFirstName(),
        resetUrl,
        resetToken
    );
    
    // 6. Registrar acción
    auditService.logPasswordResetRequest(user.getId());
}
```

**Flujo:**
```
Cliente envía:
{ "email": "maria@example.com" }

↓ AuthService.forgotPassword()

1. 🔍 Busca usuario
2. 🎲 Genera token: "550e8400-e29b-41d4..."
3. 💾 Guarda token en BD (expira en 1h)
4. 🔗 Crea URL: "http://localhost:5173/reset-password?token=550e8400..."
5. 📧 Envía email:
   
   Hola María,
   
   Haz clic aquí para restablecer tu contraseña:
   [Restablecer Contraseña]
   
   Este link expira en 1 hora.

6. 📝 Registra en audit_logs

↓ Retorna:
{ "message": "Email enviado si la cuenta existe" }
```

---

#### 4️⃣ `resetPassword()` - Restablecer Contraseña

**Explicación Simple:**
Cambia tu contraseña usando el token que recibiste por email.

**Explicación Técnica:**
```java
public void resetPassword(ResetPasswordRequest request) {
    // 1. Buscar usuario por token
    User user = userRepository.findByPasswordResetToken(request.getToken())
        .orElseThrow(() -> new BusinessException("Token inválido o expirado"));
    
    // 2. Verificar que el token no haya expirado
    if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
        throw new BusinessException("El token ha expirado");
    }
    
    // 3. Encriptar nueva contraseña
    String hashedPassword = passwordEncoder.encode(request.getNewPassword());
    
    // 4. Actualizar contraseña y limpiar token
    user.setPassword(hashedPassword);
    user.setPasswordResetToken(null);
    user.setPasswordResetTokenExpiry(null);
    userRepository.save(user);
    
    // 5. Invalidar todos los tokens JWT activos (seguridad)
    refreshTokenService.revokeAllUserTokens(user.getId());
    
    // 6. Enviar email de confirmación
    emailService.sendPasswordChangedEmail(user.getEmail(), user.getFirstName());
    
    // 7. Registrar cambio
    auditService.logPasswordChange(user.getId());
}
```

---

## 👤 2. UserService.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/service/UserService.java`

### 🎯 ¿Qué hace?
Gestiona la información de los usuarios (perfil, actualización de datos, estadísticas).

### 📝 Explicación Simple
Es como el **departamento de recursos humanos**:
- Administra la información personal
- Actualiza datos de contacto
- Genera reportes de actividad

### 🔧 Métodos Principales

#### 1️⃣ `getUserProfile()` - Obtener Perfil

```java
public UserProfileResponse getUserProfile(String email) {
    // 1. Buscar usuario
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    // 2. Obtener puntos de lealtad
    LoyaltyPoint loyaltyPoints = loyaltyPointRepository.findByUserId(user.getId())
        .orElse(LoyaltyPoint.builder().points(0).build());
    
    // 3. Contar órdenes
    long totalOrders = orderRepository.countByUserId(user.getId());
    
    // 4. Retornar perfil completo
    return UserProfileResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phone(user.getPhone())
        .address(user.getAddress())
        .loyaltyPoints(loyaltyPoints.getPoints())
        .totalOrders(totalOrders)
        .memberSince(user.getCreatedAt())
        .build();
}
```

---

#### 2️⃣ `updateProfile()` - Actualizar Perfil

```java
public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
    // 1. Buscar usuario
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    // 2. Actualizar campos (solo si vienen en el request)
    if (request.getFirstName() != null) {
        user.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
        user.setLastName(request.getLastName());
    }
    if (request.getPhone() != null) {
        user.setPhone(request.getPhone());
    }
    if (request.getAddress() != null) {
        user.setAddress(request.getAddress());
    }
    
    // 3. Guardar cambios
    User updatedUser = userRepository.save(user);
    
    // 4. Registrar cambio
    auditService.logProfileUpdate(user.getId());
    
    // 5. Retornar perfil actualizado
    return getUserProfile(email);
}
```

---

## 🛒 3. ProductService.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/service/ProductService.java`

### 🎯 ¿Qué hace?
Maneja toda la lógica relacionada con productos.

### 📝 Explicación Simple
Es como el **inventario de la tienda**:
- Muestra los productos disponibles
- Filtra por categoría
- Busca productos
- Verifica stock

### 🔧 Métodos Principales

#### 1️⃣ `getAllProducts()` - Listar Productos

```java
public List<ProductResponse> getAllProducts() {
    // 1. Obtener todos los productos activos
    List<Product> products = productRepository.findByActiveTrue();
    
    // 2. Convertir a DTO (ProductResponse)
    return products.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
}

private ProductResponse mapToResponse(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .imageUrl(product.getImageUrl())
        .category(product.getCategory().name())
        .stock(product.getStock())
        .active(product.isActive())
        .build();
}
```

---

#### 2️⃣ `getProductById()` - Obtener Producto

```java
public ProductResponse getProductById(Long id) {
    // 1. Buscar producto
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    
    // 2. Verificar que esté activo
    if (!product.isActive()) {
        throw new BusinessException("Producto no disponible");
    }
    
    // 3. Incrementar contador de vistas (estadísticas)
    product.setViews(product.getViews() + 1);
    productRepository.save(product);
    
    // 4. Retornar DTO
    return mapToResponse(product);
}
```

---

#### 3️⃣ `createProduct()` - Crear Producto (ADMIN)

```java
@PreAuthorize("hasRole('ADMIN')")
public ProductResponse createProduct(ProductRequest request) {
    // 1. Validar datos
    if (request.getPrice() <= 0) {
        throw new BusinessException("El precio debe ser mayor a 0");
    }
    if (request.getStock() < 0) {
        throw new BusinessException("El stock no puede ser negativo");
    }
    
    // 2. Crear entidad Product
    Product product = Product.builder()
        .name(request.getName())
        .description(request.getDescription())
        .price(request.getPrice())
        .imageUrl(request.getImageUrl())
        .category(ProductCategory.valueOf(request.getCategory()))
        .stock(request.getStock())
        .active(true)
        .createdAt(LocalDateTime.now())
        .views(0L)
        .build();
    
    // 3. Guardar en BD
    Product savedProduct = productRepository.save(product);
    
    // 4. Registrar creación
    auditService.logProductCreation(savedProduct.getId());
    
    // 5. Retornar DTO
    return mapToResponse(savedProduct);
}
```

---

#### 4️⃣ `searchProducts()` - Buscar Productos

```java
public List<ProductResponse> searchProducts(String query) {
    // 1. Buscar por nombre o descripción (case-insensitive)
    List<Product> products = productRepository
        .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    
    // 2. Filtrar solo activos
    List<Product> activeProducts = products.stream()
        .filter(Product::isActive)
        .collect(Collectors.toList());
    
    // 3. Ordenar por relevancia (nombre primero)
    activeProducts.sort((p1, p2) -> {
        boolean p1NameMatch = p1.getName().toLowerCase().contains(query.toLowerCase());
        boolean p2NameMatch = p2.getName().toLowerCase().contains(query.toLowerCase());
        
        if (p1NameMatch && !p2NameMatch) return -1;
        if (!p1NameMatch && p2NameMatch) return 1;
        return p1.getName().compareTo(p2.getName());
    });
    
    // 4. Convertir a DTO
    return activeProducts.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
}
```

---

## 🛒 4. CartService.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/service/CartService.java`

### 🎯 ¿Qué hace?
Gestiona el carrito de compras del usuario.

### 📝 Explicación Simple
Es como el **carrito físico** en un supermercado:
- Agregas productos
- Cambias cantidades
- Quitas productos
- Ves el total

### 🔧 Métodos Principales

#### 1️⃣ `addToCart()` - Agregar al Carrito

**Explicación Simple:**
Pones un producto en tu carrito.

**Explicación Técnica:**
```java
public CartResponse addToCart(String email, AddToCartRequest request) {
    // 1. Obtener usuario
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    // 2. Obtener o crear carrito
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseGet(() -> {
            Cart newCart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build();
            return cartRepository.save(newCart);
        });
    
    // 3. Obtener producto
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    
    // 4. Validar stock disponible
    if (product.getStock() < request.getQuantity()) {
        throw new BusinessException("Stock insuficiente. Disponible: " + product.getStock());
    }
    
    // 5. Verificar si el producto ya está en el carrito
    Optional<CartItem> existingItem = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(product.getId()))
        .findFirst();
    
    if (existingItem.isPresent()) {
        // Actualizar cantidad
        CartItem item = existingItem.get();
        int newQuantity = item.getQuantity() + request.getQuantity();
        
        // Validar stock nuevamente
        if (product.getStock() < newQuantity) {
            throw new BusinessException("Stock insuficiente");
        }
        
        item.setQuantity(newQuantity);
        item.setSubtotal(product.getPrice() * newQuantity);
        cartItemRepository.save(item);
    } else {
        // Crear nuevo item
        CartItem newItem = CartItem.builder()
            .cart(cart)
            .product(product)
            .quantity(request.getQuantity())
            .subtotal(product.getPrice() * request.getQuantity())
            .build();
        
        cart.getItems().add(newItem);
        cartItemRepository.save(newItem);
    }
    
    // 6. Calcular total del carrito
    double total = cart.getItems().stream()
        .mapToDouble(CartItem::getSubtotal)
        .sum();
    
    cart.setTotal(total);
    cart.setUpdatedAt(LocalDateTime.now());
    cartRepository.save(cart);
    
    // 7. Retornar carrito actualizado
    return mapToCartResponse(cart);
}
```

**Flujo:**
```
Cliente envía:
{
  "productId": 5,
  "quantity": 2
}

↓ CartService.addToCart()

1. 🔍 Busca usuario por email (del JWT)
2. 🛒 Obtiene carrito o lo crea
3. 📦 Busca producto ID=5
4. ✅ Verifica stock (debe haber al menos 2)
5. 🔎 Busca si el producto ya está en carrito
   
   Caso A: Ya existe
   - Suma cantidades: 1 + 2 = 3
   - Actualiza subtotal: $50,000 × 3 = $150,000
   
   Caso B: No existe
   - Crea nuevo CartItem
   - Subtotal: $50,000 × 2 = $100,000

6. 💰 Calcula total del carrito: $250,000
7. 💾 Guarda cambios

↓ Retorna:
{
  "id": 1,
  "items": [
    {
      "productId": 5,
      "productName": "Pañales Huggies",
      "quantity": 2,
      "price": 50000,
      "subtotal": 100000
    }
  ],
  "total": 100000
}
```

---

#### 2️⃣ `getCart()` - Obtener Carrito

```java
public CartResponse getCart(String email) {
    // 1. Obtener usuario
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    // 2. Obtener carrito
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseGet(() -> {
            // Crear carrito vacío si no existe
            Cart newCart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .total(0.0)
                .build();
            return cartRepository.save(newCart);
        });
    
    // 3. Retornar DTO
    return mapToCartResponse(cart);
}
```

---

#### 3️⃣ `updateCartItem()` - Actualizar Cantidad

```java
public CartResponse updateCartItem(String email, Long itemId, int quantity) {
    // 1. Validar cantidad
    if (quantity <= 0) {
        throw new BusinessException("La cantidad debe ser mayor a 0");
    }
    
    // 2. Buscar item
    CartItem item = cartItemRepository.findById(itemId)
        .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));
    
    // 3. Verificar que el item pertenezca al usuario
    if (!item.getCart().getUser().getEmail().equals(email)) {
        throw new BusinessException("No tienes permiso para modificar este item");
    }
    
    // 4. Verificar stock
    if (item.getProduct().getStock() < quantity) {
        throw new BusinessException("Stock insuficiente");
    }
    
    // 5. Actualizar cantidad y subtotal
    item.setQuantity(quantity);
    item.setSubtotal(item.getProduct().getPrice() * quantity);
    cartItemRepository.save(item);
    
    // 6. Recalcular total del carrito
    Cart cart = item.getCart();
    double total = cart.getItems().stream()
        .mapToDouble(CartItem::getSubtotal)
        .sum();
    
    cart.setTotal(total);
    cart.setUpdatedAt(LocalDateTime.now());
    cartRepository.save(cart);
    
    // 7. Retornar carrito actualizado
    return mapToCartResponse(cart);
}
```

---

#### 4️⃣ `removeFromCart()` - Eliminar del Carrito

```java
public CartResponse removeFromCart(String email, Long itemId) {
    // 1. Buscar item
    CartItem item = cartItemRepository.findById(itemId)
        .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));
    
    // 2. Verificar permisos
    if (!item.getCart().getUser().getEmail().equals(email)) {
        throw new BusinessException("No autorizado");
    }
    
    // 3. Obtener carrito antes de eliminar
    Cart cart = item.getCart();
    
    // 4. Eliminar item
    cart.getItems().remove(item);
    cartItemRepository.delete(item);
    
    // 5. Recalcular total
    double total = cart.getItems().stream()
        .mapToDouble(CartItem::getSubtotal)
        .sum();
    
    cart.setTotal(total);
    cart.setUpdatedAt(LocalDateTime.now());
    cartRepository.save(cart);
    
    // 6. Retornar carrito actualizado
    return mapToCartResponse(cart);
}
```

---

#### 5️⃣ `clearCart()` - Vaciar Carrito

```java
public void clearCart(String email) {
    // 1. Obtener usuario
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    // 2. Obtener carrito
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
    
    // 3. Eliminar todos los items
    cartItemRepository.deleteAll(cart.getItems());
    
    // 4. Limpiar lista de items y resetear total
    cart.getItems().clear();
    cart.setTotal(0.0);
    cart.setUpdatedAt(LocalDateTime.now());
    cartRepository.save(cart);
}
```

---

## 📦 5. OrderService.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/service/OrderService.java`

### 🎯 ¿Qué hace?
Procesa las órdenes de compra.

### 📝 Explicación Simple
Es como el **departamento de pedidos**:
- Crea la orden cuando decides comprar
- Descuenta el stock
- Guarda el pedido
- Envía confirmación por email

### 🔧 Método Principal: `createOrder()`

```java
@Transactional  // Asegura que todo se ejecute o nada
public OrderResponse createOrder(String email, CreateOrderRequest request) {
    // 1. Obtener usuario
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    // 2. Obtener carrito
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
    
    // 3. Validar que el carrito no esté vacío
    if (cart.getItems().isEmpty()) {
        throw new BusinessException("El carrito está vacío");
    }
    
    // 4. Validar stock de todos los productos
    for (CartItem item : cart.getItems()) {
        Product product = item.getProduct();
        if (product.getStock() < item.getQuantity()) {
            throw new BusinessException(
                "Stock insuficiente para: " + product.getName()
            );
        }
    }
    
    // 5. Crear orden
    Order order = Order.builder()
        .user(user)
        .orderNumber(generateOrderNumber())  // Ej: ORD-2025-10-30-001
        .status(OrderStatus.PENDING)
        .total(cart.getTotal())
        .shippingAddress(request.getShippingAddress())
        .city(request.getCity())
        .phone(request.getPhone())
        .notes(request.getNotes())
        .items(new ArrayList<>())
        .createdAt(LocalDateTime.now())
        .build();
    
    // 6. Guardar orden (para obtener ID)
    Order savedOrder = orderRepository.save(order);
    
    // 7. Crear items de la orden y descontar stock
    for (CartItem cartItem : cart.getItems()) {
        Product product = cartItem.getProduct();
        
        // Crear OrderItem
        OrderItem orderItem = OrderItem.builder()
            .order(savedOrder)
            .product(product)
            .quantity(cartItem.getQuantity())
            .price(product.getPrice())
            .subtotal(cartItem.getSubtotal())
            .build();
        
        savedOrder.getItems().add(orderItem);
        orderItemRepository.save(orderItem);
        
        // Descontar stock
        product.setStock(product.getStock() - cartItem.getQuantity());
        productRepository.save(product);
    }
    
    // 8. Vaciar carrito
    cartItemRepository.deleteAll(cart.getItems());
    cart.getItems().clear();
    cart.setTotal(0.0);
    cartRepository.save(cart);
    
    // 9. Enviar email de confirmación (asíncrono)
    emailService.sendOrderConfirmationEmail(
        user.getEmail(),
        user.getFirstName(),
        savedOrder.getOrderNumber(),
        savedOrder.getTotal()
    );
    
    // 10. Registrar en auditoría
    auditService.logOrderCreation(savedOrder.getId(), user.getId());
    
    // 11. Retornar DTO
    return mapToOrderResponse(savedOrder);
}
```

**Flujo con Ejemplo:**
```
Cliente tiene en carrito:
- Pañales Huggies × 2 = $100,000
- Biberón Avent × 1 = $50,000
Total: $150,000

Cliente envía:
{
  "shippingAddress": "Calle 123 #45-67",
  "city": "Bogotá",
  "phone": "3001234567",
  "notes": "Entregar en la mañana"
}

↓ OrderService.createOrder()

1. 🔍 Busca usuario (maria@example.com)
2. 🛒 Obtiene carrito
3. ✅ Valida que no esté vacío
4. 📦 Verifica stock:
   - Pañales: Stock=100, Pedido=2 ✅
   - Biberón: Stock=50, Pedido=1 ✅
5. 📝 Crea orden:
   - orderNumber: "ORD-2025-10-30-001"
   - status: PENDING
   - total: $150,000
6. 💾 Guarda orden en BD (ID=45)
7. 📦 Crea items:
   - OrderItem 1: Pañales × 2
   - OrderItem 2: Biberón × 1
8. 📉 Descuenta stock:
   - Pañales: 100 → 98
   - Biberón: 50 → 49
9. 🗑️ Vacía carrito
10. 📧 Envía email:
    
    Hola María,
    
    Tu orden ORD-2025-10-30-001 ha sido creada.
    Total: $150,000
    
    [Ver Orden]

11. 📝 Registra en audit_logs

↓ Retorna:
{
  "id": 45,
  "orderNumber": "ORD-2025-10-30-001",
  "status": "PENDING",
  "total": 150000,
  "items": [
    {
      "productName": "Pañales Huggies",
      "quantity": 2,
      "price": 50000,
      "subtotal": 100000
    },
    {
      "productName": "Biberón Avent",
      "quantity": 1,
      "price": 50000,
      "subtotal": 50000
    }
  ],
  "createdAt": "2025-10-30T19:45:00"
}
```

---

## 💳 6. PaymentService.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/service/PaymentService.java`

### 🎯 ¿Qué hace?
Procesa los pagos de las órdenes.

### 📝 Explicación Simple
Es como la **caja registradora**:
- Procesa el pago
- Confirma la orden
- Genera recibo
- Otorga puntos de lealtad

### 🔧 Método Principal: `processPayment()`

```java
@Transactional
public PaymentResponse processPayment(ProcessPaymentRequest request) {
    // 1. Obtener orden
    Order order = orderRepository.findById(request.getOrderId())
        .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));
    
    // 2. Validar que la orden esté pendiente
    if (order.getStatus() != OrderStatus.PENDING) {
        throw new BusinessException("Esta orden ya fue procesada");
    }
    
    // 3. Validar monto
    if (!request.getAmount().equals(order.getTotal())) {
        throw new BusinessException("El monto no coincide con el total de la orden");
    }
    
    // 4. Simular procesamiento de pago (en producción integrar con pasarela real)
    boolean paymentSuccessful = simulatePaymentGateway(request);
    
    if (!paymentSuccessful) {
        // Crear registro de pago fallido
        Payment failedPayment = Payment.builder()
            .order(order)
            .amount(request.getAmount())
            .paymentMethod(request.getPaymentMethod())
            .status(PaymentStatus.FAILED)
            .transactionId(UUID.randomUUID().toString())
            .errorMessage("Pago rechazado por el banco")
            .processedAt(LocalDateTime.now())
            .build();
        
        paymentRepository.save(failedPayment);
        
        throw new BusinessException("Pago rechazado. Intente con otro método.");
    }
    
    // 5. Crear registro de pago exitoso
    Payment payment = Payment.builder()
        .order(order)
        .amount(request.getAmount())
        .paymentMethod(request.getPaymentMethod())
        .status(PaymentStatus.COMPLETED)
        .transactionId(UUID.randomUUID().toString())
        .processedAt(LocalDateTime.now())
        .build();
    
    Payment savedPayment = paymentRepository.save(payment);
    
    // 6. Actualizar estado de la orden
    order.setStatus(OrderStatus.CONFIRMED);
    order.setPayment(savedPayment);
    orderRepository.save(order);
    
    // 7. Otorgar puntos de lealtad (1 punto por cada $1000)
    int pointsToAdd = (int) (order.getTotal() / 1000);
    loyaltyService.addPoints(
        order.getUser().getId(),
        pointsToAdd,
        "Compra orden: " + order.getOrderNumber()
    );
    
    // 8. Enviar email de confirmación de pago
    emailService.sendPaymentConfirmationEmail(
        order.getUser().getEmail(),
        order.getUser().getFirstName(),
        order.getOrderNumber(),
        savedPayment.getAmount(),
        savedPayment.getTransactionId()
    );
    
    // 9. Registrar en auditoría
    auditService.logPayment(savedPayment.getId(), order.getUser().getId());
    
    // 10. Retornar respuesta
    return PaymentResponse.builder()
        .id(savedPayment.getId())
        .orderId(order.getId())
        .orderNumber(order.getOrderNumber())
        .amount(savedPayment.getAmount())
        .paymentMethod(savedPayment.getPaymentMethod().name())
        .status(savedPayment.getStatus().name())
        .transactionId(savedPayment.getTransactionId())
        .processedAt(savedPayment.getProcessedAt())
        .build();
}

private boolean simulatePaymentGateway(ProcessPaymentRequest request) {
    // Simulación simple: 95% de éxito
    // En producción: integrar con Mercado Pago, PayU, Stripe, etc.
    return Math.random() < 0.95;
}
```

**Flujo:**
```
Cliente envía:
{
  "orderId": 45,
  "amount": 150000,
  "paymentMethod": "CREDIT_CARD",
  "cardNumber": "****1234"
}

↓ PaymentService.processPayment()

1. 🔍 Busca orden ID=45
2. ✅ Verifica que status=PENDING
3. ✅ Valida monto: $150,000 = $150,000
4. 💳 Procesa pago con pasarela:
   - Envía datos al banco
   - Espera respuesta
   - Resultado: ✅ APROBADO
5. 💾 Guarda Payment:
   - status: COMPLETED
   - transactionId: "550e8400-e29b-41d4..."
6. ✅ Actualiza orden:
   - status: PENDING → CONFIRMED
7. 🎁 Otorga puntos:
   - $150,000 / $1,000 = 150 puntos
8. 📧 Envía email:
   
   Hola María,
   
   Tu pago ha sido confirmado.
   Orden: ORD-2025-10-30-001
   Monto: $150,000
   Transacción: 550e8400-e29b-41d4...
   
   Ganaste 150 puntos! 🎉

9. 📝 Registra en audit_logs

↓ Retorna:
{
  "id": 78,
  "orderId": 45,
  "orderNumber": "ORD-2025-10-30-001",
  "amount": 150000,
  "paymentMethod": "CREDIT_CARD",
  "status": "COMPLETED",
  "transactionId": "550e8400-e29b-41d4...",
  "processedAt": "2025-10-30T19:47:00"
}
```

---

## 📧 7. EmailService.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/service/EmailService.java`

### 🎯 ¿Qué hace?
Envía emails automatizados a los usuarios.

### 📝 Explicación Simple
Es como el **departamento de comunicaciones**:
- Envía bienvenida a nuevos usuarios
- Notifica confirmación de órdenes
- Envía links de recuperación de contraseña
- Manda recibos de pago

### 🔧 Características
```java
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.mail.from-email}")
    private String fromEmail;  // babycashnoreply@gmail.com
    
    @Value("${app.frontend.url}")
    private String frontendUrl;  // http://localhost:5173
    
    // Todos los métodos usan @Async para no bloquear
    // el hilo principal (el usuario no tiene que esperar)
    
    @Async
    public void sendWelcomeEmail(String toEmail, String firstName) {
        String subject = "¡Bienvenido a BabyCash!";
        
        String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>¡Hola %s!</h2>
                <p>Gracias por registrarte en BabyCash.</p>
                <p>Estamos emocionados de tenerte con nosotros.</p>
                <p>Explora nuestro catálogo y encuentra lo mejor para tu bebé.</p>
                <a href="%s/productos" style="
                    background-color: #4CAF50;
                    color: white;
                    padding: 10px 20px;
                    text-decoration: none;
                    border-radius: 5px;
                ">Ver Productos</a>
            </body>
            </html>
            """.formatted(firstName, frontendUrl);
        
        sendHtmlEmail(toEmail, subject, htmlContent);
    }
    
    @Async
    public void sendPasswordResetEmail(String toEmail, String firstName, 
                                      String resetUrl, String token) {
        String subject = "Restablecer tu contraseña - BabyCash";
        
        String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>Hola %s,</h2>
                <p>Recibimos una solicitud para restablecer tu contraseña.</p>
                <p>Haz clic en el siguiente enlace para crear una nueva contraseña:</p>
                <a href="%s" style="
                    background-color: #2196F3;
                    color: white;
                    padding: 10px 20px;
                    text-decoration: none;
                    border-radius: 5px;
                ">Restablecer Contraseña</a>
                <p><small>Este enlace expira en 1 hora.</small></p>
                <p>Si no solicitaste este cambio, ignora este email.</p>
            </body>
            </html>
            """.formatted(firstName, resetUrl);
        
        sendHtmlEmail(toEmail, subject, htmlContent);
    }
    
    @Async
    public void sendOrderConfirmationEmail(String toEmail, String firstName,
                                          String orderNumber, double total) {
        String subject = "Confirmación de Orden - " + orderNumber;
        
        String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>¡Gracias por tu compra, %s!</h2>
                <p>Tu orden ha sido recibida exitosamente.</p>
                <div style="
                    background-color: #f5f5f5;
                    padding: 15px;
                    border-radius: 5px;
                    margin: 20px 0;
                ">
                    <p><strong>Número de orden:</strong> %s</p>
                    <p><strong>Total:</strong> $%,.0f</p>
                </div>
                <p>Te notificaremos cuando tu pedido sea enviado.</p>
                <a href="%s/orders/%s">Ver Detalles de la Orden</a>
            </body>
            </html>
            """.formatted(firstName, orderNumber, total, frontendUrl, orderNumber);
        
        sendHtmlEmail(toEmail, subject, htmlContent);
    }
    
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);  // true = HTML
            
            mailSender.send(message);
            
            log.info("✅ Email enviado a: {}", to);
        } catch (Exception e) {
            log.error("❌ Error enviando email a {}: {}", to, e.getMessage());
        }
    }
}
```

---

## 🎁 8. LoyaltyService.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/service/LoyaltyService.java`

### 🎯 ¿Qué hace?
Gestiona el sistema de puntos de lealtad.

### 📝 Explicación Simple
Es como una **tarjeta de puntos**:
- Ganas puntos con cada compra
- Puedes canjearlos por descuentos
- Ves tu historial de puntos

### 🔧 Métodos Principales

```java
public void addPoints(Long userId, int points, String description) {
    // 1. Obtener o crear registro de puntos
    LoyaltyPoint loyaltyPoint = loyaltyPointRepository.findByUserId(userId)
        .orElseGet(() -> {
            LoyaltyPoint newLP = LoyaltyPoint.builder()
                .userId(userId)
                .points(0)
                .totalEarned(0)
                .totalRedeemed(0)
                .build();
            return loyaltyPointRepository.save(newLP);
        });
    
    // 2. Agregar puntos
    loyaltyPoint.setPoints(loyaltyPoint.getPoints() + points);
    loyaltyPoint.setTotalEarned(loyaltyPoint.getTotalEarned() + points);
    
    // 3. Crear registro de transacción
    LoyaltyTransaction transaction = LoyaltyTransaction.builder()
        .userId(userId)
        .points(points)
        .type(LoyaltyTransactionType.EARNED)
        .description(description)
        .createdAt(LocalDateTime.now())
        .build();
    
    // 4. Guardar
    loyaltyPointRepository.save(loyaltyPoint);
    loyaltyTransactionRepository.save(transaction);
}

public void redeemPoints(Long userId, int points) {
    // 1. Verificar que tenga puntos suficientes
    LoyaltyPoint loyaltyPoint = loyaltyPointRepository.findByUserId(userId)
        .orElseThrow(() -> new ResourceNotFoundException("No tienes puntos"));
    
    if (loyaltyPoint.getPoints() < points) {
        throw new BusinessException("Puntos insuficientes");
    }
    
    // 2. Descontar puntos
    loyaltyPoint.setPoints(loyaltyPoint.getPoints() - points);
    loyaltyPoint.setTotalRedeemed(loyaltyPoint.getTotalRedeemed() + points);
    
    // 3. Crear transacción
    LoyaltyTransaction transaction = LoyaltyTransaction.builder()
        .userId(userId)
        .points(points)
        .type(LoyaltyTransactionType.REDEEMED)
        .description("Canjeados por descuento")
        .createdAt(LocalDateTime.now())
        .build();
    
    // 4. Guardar
    loyaltyPointRepository.save(loyaltyPoint);
    loyaltyTransactionRepository.save(transaction);
}
```

---

## 📊 RESUMEN DE SERVICES

| Service | Responsabilidad Principal | Complejidad |
|---------|---------------------------|-------------|
| **AuthService** | Autenticación, JWT, passwords | 🔴 Alta |
| **UserService** | Gestión de perfiles | 🟢 Baja |
| **ProductService** | CRUD de productos | 🟡 Media |
| **CartService** | Gestión del carrito | 🟡 Media |
| **OrderService** | Crear y gestionar órdenes | 🔴 Alta |
| **PaymentService** | Procesar pagos | 🔴 Alta |
| **EmailService** | Enviar emails | 🟡 Media |
| **LoyaltyService** | Sistema de puntos | 🟢 Baja |
| **BlogPostService** | Gestión de blog | 🟢 Baja |
| **TestimonialService** | Gestión de testimonios | 🟢 Baja |

---

## 🔑 CONCEPTOS CLAVE

### 1. **@Transactional**
Asegura que todas las operaciones se completen o ninguna.

**Ejemplo:**
```java
@Transactional
public void createOrder() {
    // Si alguna de estas operaciones falla,
    // TODAS se revierten (rollback)
    
    1. Crear orden
    2. Crear items
    3. Descontar stock    ← Si falla aquí...
    4. Vaciar carrito     ← Esto no se ejecuta
    
    // Y se revierten los pasos 1 y 2
}
```

### 2. **@Async**
Ejecuta el método en otro hilo (no bloquea).

**Sin @Async:**
```
Usuario hace compra
↓ (espera 5 segundos mientras se envía email)
Servidor retorna respuesta
```

**Con @Async:**
```
Usuario hace compra
↓ (retorna respuesta inmediata)
↓ (email se envía en segundo plano)
✓ Usuario ve confirmación rápido
```

### 3. **DTO Mapping**
Convertir entidades de BD a objetos para el frontend.

```java
// Entidad (BD)
Product product = productRepository.findById(1);
// Tiene: id, name, price, createdAt, updatedAt, etc.

// DTO (Frontend)
ProductResponse response = mapToResponse(product);
// Solo tiene: id, name, price, imageUrl
// (oculta campos internos)
```

---

**Última actualización**: Octubre 2025
**Versión**: 1.0
