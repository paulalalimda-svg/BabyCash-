# 📨 REQUEST Y RESPONSE

## 📖 ¿Qué son Request y Response?

**Request (Petición):** Es lo que el cliente **envía** al servidor.  
**Response (Respuesta):** Es lo que el servidor **retorna** al cliente.

---

## 🎭 Analogía Simple

Imagina que le pides pizza por WhatsApp:

**Request (Tu mensaje):**
```
Hola, quiero ordenar:
- 1 pizza grande de pepperoni
- 2 gaseosas
- Dirección: Calle 123 #45-67
- Pago: Tarjeta
```

**Response (Respuesta del restaurante):**
```
¡Listo! Tu orden #1234 está confirmada.
Total: $50.000
Llegará en 30 minutos ✅
```

---

## 📤 REQUEST (Petición)

### Estructura de un Request HTTP

```http
POST /api/cart/add HTTP/1.1                    ← Línea de request
Host: localhost:8080                            ← Headers
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...   ← Headers
Content-Type: application/json                  ← Headers
                                                ← Línea en blanco
{                                               ← Body
  "productId": 1,
  "quantity": 2
}
```

### Componentes de un Request

1. **Método HTTP**: GET, POST, PUT, DELETE, etc.
2. **URL**: `/api/cart/add`
3. **Headers**: Información adicional (token, tipo de contenido)
4. **Body**: Datos que envías (solo POST, PUT, PATCH)
5. **Query Parameters**: `?name=pañal&minPrice=10000`
6. **Path Variables**: `/api/products/{id}`

---

## 📥 RESPONSE (Respuesta)

### Estructura de un Response HTTP

```http
HTTP/1.1 200 OK                                 ← Status Line
Content-Type: application/json                  ← Headers
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...   ← Headers
                                                ← Línea en blanco
{                                               ← Body
  "id": 1,
  "userId": 1,
  "items": [...],
  "total": 90000
}
```

### Componentes de un Response

1. **Status Code**: 200, 201, 400, 404, 500, etc.
2. **Headers**: Información adicional
3. **Body**: Datos que retorna el servidor

---

## 🔄 Anotaciones en Spring Boot

### @RequestBody

**¿Qué hace?**: Captura el **body (cuerpo)** del request y lo convierte a un objeto Java.

**Cuándo se usa**: POST, PUT, PATCH

**Ejemplo:**

**Request:**
```http
POST /api/auth/register HTTP/1.1
Content-Type: application/json

{
  "email": "maria@gmail.com",
  "password": "password123",
  "name": "María García"
}
```

**Controller:**
```java
@PostMapping("/auth/register")
public ResponseEntity<AuthResponseDTO> register(
    @RequestBody RegisterRequestDTO request  // ← Captura el body
) {
    // request.getEmail() → "maria@gmail.com"
    // request.getPassword() → "password123"
    // request.getName() → "María García"
    
    AuthResponseDTO response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

**DTO:**
```java
@Data
public class RegisterRequestDTO {
    @NotBlank(message = "Email requerido")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "Password requerido")
    @Size(min = 6, message = "Mínimo 6 caracteres")
    private String password;
    
    @NotBlank(message = "Nombre requerido")
    private String name;
}
```

---

### @RequestParam

**¿Qué hace?**: Captura **query parameters** de la URL.

**Cuándo se usa**: GET con filtros

**Ejemplo:**

**Request:**
```http
GET /api/products/search?name=pañal&minPrice=10000&maxPrice=50000 HTTP/1.1
```

**Controller:**
```java
@GetMapping("/products/search")
public ResponseEntity<List<ProductDTO>> searchProducts(
    @RequestParam(required = false) String name,          // ← "pañal"
    @RequestParam(required = false) Double minPrice,      // ← 10000.0
    @RequestParam(required = false) Double maxPrice       // ← 50000.0
) {
    List<ProductDTO> products = productService.search(name, minPrice, maxPrice);
    return ResponseEntity.ok(products);
}
```

**Parámetros opcionales vs obligatorios:**

```java
// Obligatorio (error si falta)
@RequestParam String name

// Opcional
@RequestParam(required = false) String name

// Con valor por defecto
@RequestParam(defaultValue = "0") Double minPrice
```

---

### @PathVariable

**¿Qué hace?**: Captura variables **dentro de la URL**.

**Cuándo se usa**: GET, PUT, PATCH, DELETE con ID

**Ejemplo:**

**Request:**
```http
GET /api/products/1 HTTP/1.1
```

**Controller:**
```java
@GetMapping("/products/{id}")
public ResponseEntity<ProductDTO> getProduct(
    @PathVariable Long id  // ← Captura el "1" de la URL
) {
    ProductDTO product = productService.findById(id)
        .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    
    return ResponseEntity.ok(product);
}
```

**Múltiples path variables:**

```http
GET /api/users/1/orders/5 HTTP/1.1
```

```java
@GetMapping("/users/{userId}/orders/{orderId}")
public ResponseEntity<OrderDTO> getUserOrder(
    @PathVariable Long userId,    // ← 1
    @PathVariable Long orderId    // ← 5
) {
    OrderDTO order = orderService.findByUserAndOrder(userId, orderId);
    return ResponseEntity.ok(order);
}
```

---

### @RequestHeader

**¿Qué hace?**: Captura **headers** del request.

**Cuándo se usa**: Tokens, idioma, versión de API

**Ejemplo:**

**Request:**
```http
GET /api/cart HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Accept-Language: es
```

**Controller:**
```java
@GetMapping("/cart")
public ResponseEntity<CartDTO> getCart(
    @RequestHeader("Authorization") String authHeader,  // ← Token
    @RequestHeader(value = "Accept-Language", defaultValue = "en") String language
) {
    String token = authHeader.replace("Bearer ", "");
    // Usar token...
    
    return ResponseEntity.ok(cart);
}
```

**En BabyCash usamos `@AuthenticationPrincipal` en lugar de capturar el header manualmente:**

```java
@GetMapping("/cart")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<CartDTO> getCart(
    @AuthenticationPrincipal UserDetails userDetails  // ← Spring lo extrae del token
) {
    String email = userDetails.getUsername();
    CartDTO cart = cartService.getCart(email);
    return ResponseEntity.ok(cart);
}
```

---

## 📤 ResponseEntity

**¿Qué es?**: Clase de Spring que te permite controlar **completamente** la respuesta HTTP.

### Control del Status Code

```java
// 200 OK
return ResponseEntity.ok(data);

// 201 Created
return ResponseEntity.status(HttpStatus.CREATED).body(data);

// 204 No Content
return ResponseEntity.noContent().build();

// 400 Bad Request
return ResponseEntity.badRequest().body(error);

// 404 Not Found
return ResponseEntity.notFound().build();
```

### Con Headers

```java
@PostMapping("/auth/login")
public ResponseEntity<AuthResponseDTO> login(
    @RequestBody LoginRequestDTO request
) {
    AuthResponseDTO response = authService.login(request);
    
    // Agregar header personalizado
    return ResponseEntity.ok()
        .header("X-Auth-Token", response.getToken())
        .header("X-Refresh-Token", response.getRefreshToken())
        .body(response);
}
```

### Ejemplos Completos

**Éxito (200):**
```java
@GetMapping("/products")
public ResponseEntity<List<ProductDTO>> getAllProducts() {
    List<ProductDTO> products = productService.findAll();
    return ResponseEntity.ok(products);
}
```

**Creado (201):**
```java
@PostMapping("/products")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ProductDTO> createProduct(
    @RequestBody @Valid ProductDTO dto
) {
    ProductDTO created = productService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

**Sin contenido (204):**
```java
@DeleteMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

**Error (404):**
```java
@GetMapping("/products/{id}")
public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
    Optional<ProductDTO> product = productService.findById(id);
    
    if (product.isEmpty()) {
        return ResponseEntity.notFound().build();  // 404
    }
    
    return ResponseEntity.ok(product.get());  // 200
}
```

---

## 🎯 Ejemplos Completos de BabyCash

### 1. Registro (POST con @RequestBody)

**Request:**
```http
POST /api/auth/register HTTP/1.1
Content-Type: application/json

{
  "email": "maria@gmail.com",
  "password": "password123",
  "name": "María García",
  "phone": "3001234567",
  "address": "Calle 123 #45-67"
}
```

**Controller:**
```java
@PostMapping("/auth/register")
public ResponseEntity<AuthResponseDTO> register(
    @RequestBody @Valid RegisterRequestDTO request
) {
    AuthResponseDTO response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

**Response:**
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "maria@gmail.com",
    "name": "María García",
    "role": "USER"
  }
}
```

---

### 2. Buscar Productos (GET con @RequestParam)

**Request:**
```http
GET /api/products/search?name=pañal&minPrice=10000&maxPrice=50000 HTTP/1.1
```

**Controller:**
```java
@GetMapping("/products/search")
public ResponseEntity<List<ProductDTO>> searchProducts(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) Double minPrice,
    @RequestParam(required = false) Double maxPrice
) {
    List<ProductDTO> products = productService.search(name, minPrice, maxPrice);
    return ResponseEntity.ok(products);
}
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 1,
    "name": "Pañales Huggies",
    "price": 45000,
    "stock": 50
  },
  {
    "id": 3,
    "name": "Pañalera Premium",
    "price": 35000,
    "stock": 20
  }
]
```

---

### 3. Ver Producto (GET con @PathVariable)

**Request:**
```http
GET /api/products/1 HTTP/1.1
```

**Controller:**
```java
@GetMapping("/products/{id}")
public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
    ProductDTO product = productService.findById(id)
        .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    
    return ResponseEntity.ok(product);
}
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "name": "Pañales Huggies",
  "description": "Pañales para bebés recién nacidos",
  "price": 45000,
  "stock": 50,
  "imageUrl": "https://...",
  "available": true
}
```

---

### 4. Agregar al Carrito (POST con @RequestBody + Token)

**Request:**
```http
POST /api/cart/add HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

**Controller:**
```java
@PostMapping("/cart/add")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<CartDTO> addToCart(
    @RequestBody @Valid AddToCartDTO dto,
    @AuthenticationPrincipal UserDetails userDetails
) {
    String email = userDetails.getUsername();
    CartDTO cart = cartService.addToCart(email, dto);
    return ResponseEntity.ok(cart);
}
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "userId": 1,
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Pañales Huggies",
      "productPrice": 45000,
      "quantity": 2,
      "subtotal": 90000
    }
  ],
  "total": 90000
}
```

---

### 5. Actualizar Producto (PUT con @PathVariable + @RequestBody)

**Request:**
```http
PUT /api/products/1 HTTP/1.1
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "Pañales Huggies Supreme",
  "description": "Pañales premium",
  "price": 50000,
  "stock": 30,
  "available": true
}
```

**Controller:**
```java
@PutMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ProductDTO> updateProduct(
    @PathVariable Long id,
    @RequestBody @Valid ProductDTO dto
) {
    ProductDTO updated = productService.update(id, dto);
    return ResponseEntity.ok(updated);
}
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "name": "Pañales Huggies Supreme",
  "description": "Pañales premium",
  "price": 50000,
  "stock": 30,
  "available": true
}
```

---

### 6. Actualizar Cantidad en Carrito (PATCH con @PathVariable + @RequestBody)

**Request:**
```http
PATCH /api/cart/items/1 HTTP/1.1
Authorization: Bearer {token}
Content-Type: application/json

{
  "quantity": 5
}
```

**Controller:**
```java
@PatchMapping("/cart/items/{itemId}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<CartItemDTO> updateQuantity(
    @PathVariable Long itemId,
    @RequestBody UpdateQuantityDTO dto
) {
    CartItemDTO item = cartService.updateQuantity(itemId, dto.getQuantity());
    return ResponseEntity.ok(item);
}
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "productId": 1,
  "productName": "Pañales Huggies",
  "quantity": 5,
  "subtotal": 225000
}
```

---

### 7. Eliminar Producto (DELETE con @PathVariable)

**Request:**
```http
DELETE /api/products/1 HTTP/1.1
Authorization: Bearer {admin_token}
```

**Controller:**
```java
@DeleteMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

**Response:**
```http
HTTP/1.1 204 No Content
```

---

## 📊 Comparación de Anotaciones

| Anotación | Dónde Captura | Ejemplo URL | Uso |
|-----------|---------------|-------------|-----|
| `@RequestBody` | Body (cuerpo) del request | - | POST, PUT, PATCH |
| `@RequestParam` | Query parameters | `?name=pañal&price=10000` | GET con filtros |
| `@PathVariable` | Dentro de la URL | `/products/{id}` | GET, PUT, DELETE con ID |
| `@RequestHeader` | Headers | `Authorization: Bearer ...` | Tokens, configuración |

---

## 🎪 Ejemplo Completo: Crear Orden

### Request

```http
POST /api/orders HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
Accept: application/json

{
  "shippingAddress": "Calle 123 #45-67, Bogotá",
  "paymentMethod": "Credit Card"
}
```

### Controller

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDTO> createOrder(
        @RequestBody @Valid CreateOrderDTO dto,           // ← Body
        @AuthenticationPrincipal UserDetails userDetails  // ← Token
    ) {
        String email = userDetails.getUsername();
        OrderDTO order = orderService.createOrder(email, dto);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(order);
    }
}
```

### DTOs

```java
@Data
public class CreateOrderDTO {
    @NotBlank(message = "Dirección requerida")
    private String shippingAddress;
    
    @NotBlank(message = "Método de pago requerido")
    private String paymentMethod;
}

@Data
@Builder
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Double total;
    private String status;
    private String shippingAddress;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;
}
```

### Service

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    
    @Transactional
    public OrderDTO createOrder(String userEmail, CreateOrderDTO dto) {
        // 1. Obtener usuario
        User user = userService.findByEmail(userEmail);
        
        // 2. Obtener carrito
        Cart cart = cartService.getCartByUser(user);
        
        // 3. Validar que el carrito no esté vacío
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }
        
        // 4. Crear orden
        Order order = Order.builder()
            .user(user)
            .orderNumber("ORD-" + System.currentTimeMillis())
            .total(cart.getTotal())
            .status(OrderStatus.PENDING)
            .shippingAddress(dto.getShippingAddress())
            .paymentMethod(dto.getPaymentMethod())
            .build();
        
        // 5. Copiar items del carrito a la orden
        List<OrderItem> orderItems = cart.getItems().stream()
            .map(cartItem -> OrderItem.builder()
                .order(order)
                .product(cartItem.getProduct())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getProduct().getPrice())
                .build())
            .toList();
        
        order.setItems(orderItems);
        
        // 6. Guardar orden
        Order savedOrder = orderRepository.save(order);
        
        // 7. Vaciar carrito
        cartService.clearCart(user);
        
        // 8. Convertir a DTO y retornar
        return OrderMapper.toDTO(savedOrder);
    }
}
```

### Response

```http
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/orders/1

{
  "id": 1,
  "orderNumber": "ORD-1698765432000",
  "total": 135000,
  "status": "PENDING",
  "shippingAddress": "Calle 123 #45-67, Bogotá",
  "paymentMethod": "Credit Card",
  "createdAt": "2025-10-30T19:30:00",
  "items": [
    {
      "productId": 1,
      "productName": "Pañales Huggies",
      "quantity": 2,
      "price": 45000,
      "subtotal": 90000
    },
    {
      "productId": 2,
      "productName": "Leche NAN",
      "quantity": 3,
      "price": 15000,
      "subtotal": 45000
    }
  ]
}
```

---

## 🎯 Resumen

| Componente | Request | Response |
|------------|---------|----------|
| **Método HTTP** | GET, POST, PUT, DELETE | - |
| **URL** | `/api/products/1` | - |
| **Headers** | `Authorization: Bearer ...` | `Content-Type: application/json` |
| **Body** | `{ "name": "...", "price": ... }` | `{ "id": 1, "name": "..." }` |
| **Status Code** | - | `200`, `201`, `400`, `404`, etc. |

| Anotación | Captura | Ejemplo |
|-----------|---------|---------|
| `@RequestBody` | Body del request | `{ "name": "María" }` |
| `@RequestParam` | Query parameters | `?name=María&age=25` |
| `@PathVariable` | URL path | `/users/{id}` → `id=1` |
| `@RequestHeader` | Headers | `Authorization: Bearer ...` |

---

**Última actualización**: Octubre 2025
