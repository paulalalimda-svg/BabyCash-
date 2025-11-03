# 🚦 CÓDIGOS DE ESTADO HTTP

## 📖 ¿Qué son los Códigos de Estado?

Los **códigos de estado HTTP** son como **semáforos** que el servidor usa para decirte si tu petición fue exitosa o si hubo algún problema.

---

## 🎭 Analogía Simple

Imagina que pides una pizza por teléfono:

- **200 OK**: "Tu pizza está lista y en camino" ✅
- **201 Created**: "Creamos tu orden, llegará en 30 minutos" ✅
- **400 Bad Request**: "No entendí tu pedido, ¿puedes repetir?" ❌
- **401 Unauthorized**: "Necesito tu nombre y dirección para entregarte" ❌
- **404 Not Found**: "No tenemos esa pizza en el menú" ❌
- **500 Internal Server Error**: "Se quemó el horno, no podemos hacer tu pizza" ❌

---

## 📊 Categorías de Códigos

| Rango | Categoría | Significado |
|-------|-----------|-------------|
| **1xx** | Informativo | "Estoy procesando tu petición..." |
| **2xx** | Éxito | "Todo salió bien ✅" |
| **3xx** | Redirección | "Búscalo en otro lugar 🔄" |
| **4xx** | Error del Cliente | "Tú cometiste un error ❌" |
| **5xx** | Error del Servidor | "Yo (servidor) tengo un problema 💥" |

---

## ✅ Códigos 2xx - Éxito

### 200 OK

**Significado:** La petición fue exitosa.

**Cuándo se usa:** GET, PUT, PATCH exitosos

**Ejemplo en BabyCash:**

```http
GET /api/products HTTP/1.1

HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 1,
    "name": "Pañales Huggies",
    "price": 45000
  }
]
```

**En Spring Boot:**
```java
@GetMapping("/products")
public ResponseEntity<List<ProductDTO>> getAllProducts() {
    List<ProductDTO> products = productService.findAll();
    return ResponseEntity.ok(products); // 200 OK
}
```

---

### 201 Created

**Significado:** Se creó un nuevo recurso exitosamente.

**Cuándo se usa:** POST exitoso

**Ejemplo en BabyCash:**

```http
POST /api/auth/register HTTP/1.1
Content-Type: application/json

{
  "email": "maria@gmail.com",
  "password": "password123"
}

HTTP/1.1 201 Created
Location: /api/users/1
Content-Type: application/json

{
  "id": 1,
  "email": "maria@gmail.com",
  "name": "María García"
}
```

**En Spring Boot:**
```java
@PostMapping("/auth/register")
public ResponseEntity<AuthResponseDTO> register(
    @RequestBody @Valid RegisterRequestDTO request
) {
    AuthResponseDTO response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created
}
```

---

### 204 No Content

**Significado:** Operación exitosa, pero no hay contenido para retornar.

**Cuándo se usa:** DELETE exitoso, PUT sin respuesta

**Ejemplo en BabyCash:**

```http
DELETE /api/cart/items/1 HTTP/1.1
Authorization: Bearer {token}

HTTP/1.1 204 No Content
```

**En Spring Boot:**
```java
@DeleteMapping("/cart/items/{itemId}")
public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId) {
    cartService.removeItem(itemId);
    return ResponseEntity.noContent().build(); // 204 No Content
}
```

---

## ❌ Códigos 4xx - Errores del Cliente

### 400 Bad Request

**Significado:** La petición está mal formada o tiene datos inválidos.

**Cuándo se usa:**
- Datos faltantes
- Formato JSON inválido
- Validaciones fallidas

**Ejemplo en BabyCash:**

```http
POST /api/cart/add HTTP/1.1
Content-Type: application/json

{
  "productId": "abc",  ❌ Debe ser número
  "quantity": -5       ❌ Debe ser positivo
}

HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "error": "Bad Request",
  "message": "Datos inválidos",
  "errors": [
    "productId debe ser un número",
    "quantity debe ser mayor a 0"
  ]
}
```

**En Spring Boot:**
```java
@PostMapping("/cart/add")
public ResponseEntity<CartDTO> addToCart(
    @RequestBody @Valid AddToCartDTO dto
) {
    // Si @Valid falla, Spring retorna automáticamente 400
    CartDTO cart = cartService.addToCart(dto);
    return ResponseEntity.ok(cart);
}
```

**Handler de excepciones:**
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
    MethodArgumentNotValidException ex
) {
    ErrorResponse error = new ErrorResponse(
        "Bad Request",
        "Datos inválidos",
        extractErrors(ex)
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
}
```

---

### 401 Unauthorized

**Significado:** No estás autenticado (no has iniciado sesión).

**Cuándo se usa:**
- Token faltante
- Token inválido
- Token expirado

**Ejemplo en BabyCash:**

```http
GET /api/cart HTTP/1.1

HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "error": "Unauthorized",
  "message": "Token de autenticación requerido"
}
```

**Con token inválido:**
```http
GET /api/cart HTTP/1.1
Authorization: Bearer token_invalido_123

HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "error": "Unauthorized",
  "message": "Token inválido o expirado"
}
```

**En Spring Boot:**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(...) {
        try {
            String token = extractToken(request);
            if (token == null || !jwtService.isTokenValid(token)) {
                // Spring Security retorna automáticamente 401
                throw new UnauthorizedException("Token inválido");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
```

---

### 403 Forbidden

**Significado:** Estás autenticado, pero NO tienes permiso para esta acción.

**Cuándo se usa:**
- Usuario normal intenta acceder a funciones de ADMIN
- Usuario intenta modificar datos de otro usuario

**Ejemplo en BabyCash:**

```http
DELETE /api/products/1 HTTP/1.1
Authorization: Bearer {user_token}  ← Token de USER normal

HTTP/1.1 403 Forbidden
Content-Type: application/json

{
  "error": "Forbidden",
  "message": "No tienes permiso para eliminar productos"
}
```

**En Spring Boot:**
```java
@DeleteMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")  // Solo ADMIN
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}

// Si un USER intenta acceder, Spring retorna 403 automáticamente
```

**Diferencia 401 vs 403:**

| Código | Problema | Solución |
|--------|----------|----------|
| **401** | No has iniciado sesión | Inicia sesión (login) |
| **403** | No tienes permiso | Necesitas un rol diferente |

---

### 404 Not Found

**Significado:** El recurso solicitado no existe.

**Cuándo se usa:**
- ID no encontrado
- URL incorrecta
- Recurso eliminado

**Ejemplo en BabyCash:**

```http
GET /api/products/999 HTTP/1.1

HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "error": "Not Found",
  "message": "Producto con ID 999 no encontrado"
}
```

**En Spring Boot:**
```java
@GetMapping("/products/{id}")
public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
    ProductDTO product = productService.findById(id)
        .orElseThrow(() -> new NotFoundException(
            "Producto con ID " + id + " no encontrado"
        ));
    return ResponseEntity.ok(product);
}

@ExceptionHandler(NotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFoundException(
    NotFoundException ex
) {
    ErrorResponse error = new ErrorResponse(
        "Not Found",
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}
```

---

### 409 Conflict

**Significado:** Hay un conflicto con el estado actual del recurso.

**Cuándo se usa:**
- Email duplicado en registro
- Producto sin stock
- Conflicto de versiones

**Ejemplo en BabyCash:**

```http
POST /api/auth/register HTTP/1.1
Content-Type: application/json

{
  "email": "maria@gmail.com",  ← Este email ya existe
  "password": "password123"
}

HTTP/1.1 409 Conflict
Content-Type: application/json

{
  "error": "Conflict",
  "message": "El email maria@gmail.com ya está registrado"
}
```

**En Spring Boot:**
```java
@PostMapping("/auth/register")
public ResponseEntity<AuthResponseDTO> register(
    @RequestBody @Valid RegisterRequestDTO request
) {
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new ConflictException(
            "El email " + request.getEmail() + " ya está registrado"
        );
    }
    
    AuthResponseDTO response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

@ExceptionHandler(ConflictException.class)
public ResponseEntity<ErrorResponse> handleConflictException(
    ConflictException ex
) {
    ErrorResponse error = new ErrorResponse("Conflict", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
}
```

**Stock insuficiente:**
```http
POST /api/cart/add HTTP/1.1
Content-Type: application/json

{
  "productId": 1,
  "quantity": 100  ← Solo hay 50 en stock
}

HTTP/1.1 409 Conflict
Content-Type: application/json

{
  "error": "Conflict",
  "message": "Stock insuficiente. Disponible: 50, Solicitado: 100"
}
```

---

## 💥 Códigos 5xx - Errores del Servidor

### 500 Internal Server Error

**Significado:** El servidor tuvo un error inesperado.

**Cuándo se usa:**
- Excepciones no controladas
- Errores de base de datos
- Errores de programación

**Ejemplo en BabyCash:**

```http
GET /api/products HTTP/1.1

HTTP/1.1 500 Internal Server Error
Content-Type: application/json

{
  "error": "Internal Server Error",
  "message": "Ocurrió un error inesperado",
  "timestamp": "2025-10-30T19:30:00"
}
```

**En Spring Boot:**
```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    // Loguear el error completo para debugging
    log.error("Error inesperado", ex);
    
    // Retornar mensaje genérico al cliente
    ErrorResponse error = new ErrorResponse(
        "Internal Server Error",
        "Ocurrió un error inesperado",
        LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
}
```

---

### 503 Service Unavailable

**Significado:** El servidor no está disponible temporalmente.

**Cuándo se usa:**
- Base de datos caída
- Mantenimiento
- Servidor sobrecargado

**Ejemplo:**

```http
GET /api/products HTTP/1.1

HTTP/1.1 503 Service Unavailable
Content-Type: application/json

{
  "error": "Service Unavailable",
  "message": "Servicio en mantenimiento. Intenta más tarde."
}
```

---

## 📋 Tabla Resumen de Códigos Más Usados

| Código | Nombre | Significado | Ejemplo BabyCash |
|--------|--------|-------------|------------------|
| **200** | OK | Éxito | GET productos exitoso |
| **201** | Created | Recurso creado | Registro exitoso |
| **204** | No Content | Éxito sin respuesta | Eliminar del carrito |
| **400** | Bad Request | Datos inválidos | Cantidad negativa |
| **401** | Unauthorized | No autenticado | Sin token |
| **403** | Forbidden | Sin permiso | USER intenta eliminar producto |
| **404** | Not Found | No encontrado | Producto ID 999 no existe |
| **409** | Conflict | Conflicto | Email duplicado |
| **500** | Internal Server Error | Error del servidor | Bug en código |
| **503** | Service Unavailable | Servidor no disponible | Base de datos caída |

---

## 🎯 Uso en BabyCash

### Flujo de Registro

```java
@PostMapping("/auth/register")
public ResponseEntity<AuthResponseDTO> register(
    @RequestBody @Valid RegisterRequestDTO request
) {
    // 400 - Si datos inválidos (@Valid falla)
    
    // 409 - Si email ya existe
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new ConflictException("Email ya registrado");
    }
    
    // 201 - Registro exitoso
    AuthResponseDTO response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
    
    // 500 - Si hay error inesperado
}
```

---

### Flujo de Login

```java
@PostMapping("/auth/login")
public ResponseEntity<AuthResponseDTO> login(
    @RequestBody @Valid LoginRequestDTO request
) {
    // 400 - Si datos inválidos
    
    // 401 - Si credenciales incorrectas
    if (!authService.validateCredentials(request)) {
        throw new UnauthorizedException("Credenciales inválidas");
    }
    
    // 200 - Login exitoso
    AuthResponseDTO response = authService.login(request);
    return ResponseEntity.ok(response);
}
```

---

### Flujo de Productos

```java
@GetMapping("/products")
public ResponseEntity<List<ProductDTO>> getAllProducts() {
    // 200 - Éxito
    List<ProductDTO> products = productService.findAll();
    return ResponseEntity.ok(products);
    
    // 500 - Si hay error de BD
}

@GetMapping("/products/{id}")
public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
    // 404 - Si no existe
    ProductDTO product = productService.findById(id)
        .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    
    // 200 - Si existe
    return ResponseEntity.ok(product);
}

@PostMapping("/products")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ProductDTO> createProduct(
    @RequestBody @Valid ProductDTO dto
) {
    // 401 - Si no hay token
    // 403 - Si no es ADMIN
    // 400 - Si datos inválidos
    
    // 201 - Creado exitosamente
    ProductDTO created = productService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

---

### Flujo de Carrito

```java
@GetMapping("/cart")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<CartDTO> getCart(
    @AuthenticationPrincipal UserDetails userDetails
) {
    // 401 - Si no hay token
    
    // 200 - Éxito
    CartDTO cart = cartService.getCart(userDetails.getUsername());
    return ResponseEntity.ok(cart);
}

@PostMapping("/cart/add")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<CartDTO> addToCart(
    @RequestBody @Valid AddToCartDTO dto
) {
    // 401 - Si no hay token
    // 400 - Si datos inválidos
    // 404 - Si producto no existe
    // 409 - Si stock insuficiente
    
    // 200 - Agregado exitosamente
    CartDTO cart = cartService.addToCart(dto);
    return ResponseEntity.ok(cart);
}
```

---

## 🛡️ Manejo Global de Errores

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // 400 - Validaciones
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            "Bad Request",
            "Datos inválidos",
            extractErrors(ex)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    // 401 - No autenticado
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
        UnauthorizedException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            "Unauthorized",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    // 403 - Sin permiso
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
        AccessDeniedException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            "Forbidden",
            "No tienes permiso para esta acción"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    // 404 - No encontrado
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
        NotFoundException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            "Not Found",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    // 409 - Conflicto
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
        ConflictException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            "Conflict",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    // 500 - Error general
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Error inesperado", ex);
        ErrorResponse error = new ErrorResponse(
            "Internal Server Error",
            "Ocurrió un error inesperado"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

## 📦 Clase ErrorResponse

```java
@Data
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private List<String> errors;
    
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.errors = new ArrayList<>();
    }
    
    public ErrorResponse(String error, String message, List<String> errors) {
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }
}
```

---

## 🎪 Ejemplos Completos con Postman

### Registro Exitoso (201)

**Request:**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "maria@gmail.com",
  "password": "password123",
  "name": "María García"
}
```

**Response: 201 Created**
```json
{
  "id": 1,
  "email": "maria@gmail.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### Email Duplicado (409)

**Request:**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "maria@gmail.com",  ← Ya existe
  "password": "password123"
}
```

**Response: 409 Conflict**
```json
{
  "error": "Conflict",
  "message": "El email maria@gmail.com ya está registrado",
  "timestamp": "2025-10-30T19:30:00"
}
```

---

### Datos Inválidos (400)

**Request:**
```http
POST http://localhost:8080/api/cart/add
Authorization: Bearer {token}
Content-Type: application/json

{
  "productId": "abc",
  "quantity": -5
}
```

**Response: 400 Bad Request**
```json
{
  "error": "Bad Request",
  "message": "Datos inválidos",
  "errors": [
    "productId debe ser un número",
    "quantity debe ser mayor a 0"
  ],
  "timestamp": "2025-10-30T19:30:00"
}
```

---

### Sin Token (401)

**Request:**
```http
GET http://localhost:8080/api/cart
```

**Response: 401 Unauthorized**
```json
{
  "error": "Unauthorized",
  "message": "Token de autenticación requerido",
  "timestamp": "2025-10-30T19:30:00"
}
```

---

### Sin Permiso (403)

**Request:**
```http
DELETE http://localhost:8080/api/products/1
Authorization: Bearer {user_token}  ← Token de USER
```

**Response: 403 Forbidden**
```json
{
  "error": "Forbidden",
  "message": "No tienes permiso para eliminar productos",
  "timestamp": "2025-10-30T19:30:00"
}
```

---

### Producto No Encontrado (404)

**Request:**
```http
GET http://localhost:8080/api/products/999
```

**Response: 404 Not Found**
```json
{
  "error": "Not Found",
  "message": "Producto con ID 999 no encontrado",
  "timestamp": "2025-10-30T19:30:00"
}
```

---

## 🎯 Resumen

| Código | Cuándo Usar | Retornar |
|--------|-------------|----------|
| **200** | GET/PUT/PATCH exitoso | Datos |
| **201** | POST exitoso | Recurso creado |
| **204** | DELETE exitoso | Sin contenido |
| **400** | Datos inválidos | Errores de validación |
| **401** | No autenticado | "Token requerido" |
| **403** | Sin permiso | "No autorizado" |
| **404** | No encontrado | "Recurso no existe" |
| **409** | Conflicto | "Email duplicado", "Sin stock" |
| **500** | Error del servidor | "Error inesperado" |

---

**Última actualización**: Octubre 2025
