# 🔤 VERBOS HTTP

## 📖 ¿Qué son los Verbos HTTP?

Los **verbos HTTP** (también llamados **métodos HTTP**) son como las **acciones** que le dices al servidor que quieres hacer.

---

## 🎭 Analogía Simple

Imagina que estás en una biblioteca:

- **GET**: "Quiero VER un libro" (solo mirar, no modificar)
- **POST**: "Quiero CREAR un libro nuevo" (agregar al catálogo)
- **PUT**: "Quiero REEMPLAZAR completamente este libro" (cambiar todo)
- **PATCH**: "Quiero MODIFICAR solo algunas páginas" (cambio parcial)
- **DELETE**: "Quiero ELIMINAR este libro" (quitar del catálogo)

---

## 🟢 GET - Obtener/Consultar

### ¿Qué hace?
Obtiene información del servidor **SIN modificar nada**.

### Características
- ✅ **Seguro**: No modifica datos
- ✅ **Idempotente**: Puedes hacerlo 1000 veces y obtienes el mismo resultado
- ✅ **Cacheable**: Los navegadores pueden guardar la respuesta
- ❌ No debe tener body (cuerpo) en el request

### Ejemplo en BabyCash

```http
GET /api/products HTTP/1.1
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Pañales Huggies",
    "price": 45000
  }
]
```

### En Spring Boot

```java
@GetMapping("/products")
public ResponseEntity<List<ProductDTO>> getAllProducts() {
    List<ProductDTO> products = productService.findAll();
    return ResponseEntity.ok(products);
}
```

### Con Query Parameters

```http
GET /api/products/search?name=pañal&minPrice=10000
```

```java
@GetMapping("/products/search")
public ResponseEntity<List<ProductDTO>> searchProducts(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) Double minPrice
) {
    // Buscar productos
}
```

---

## 🔵 POST - Crear

### ¿Qué hace?
Crea un **nuevo recurso** en el servidor.

### Características
- ❌ **NO es seguro**: Modifica datos
- ❌ **NO es idempotente**: Cada vez que lo haces, crea un nuevo recurso
- ✅ Tiene body (cuerpo) en el request
- ✅ Retorna el recurso creado (generalmente con status 201)

### Ejemplo en BabyCash

```http
POST /api/auth/register HTTP/1.1
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
  "name": "María García",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### En Spring Boot

```java
@PostMapping("/auth/register")
public ResponseEntity<AuthResponseDTO> register(
    @RequestBody @Valid RegisterRequestDTO request
) {
    AuthResponseDTO response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### POST para agregar al carrito

```http
POST /api/cart/add HTTP/1.1
Authorization: Bearer {token}
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

---

## 🟡 PUT - Actualizar Completo

### ¿Qué hace?
**Reemplaza completamente** un recurso existente.

### Características
- ❌ **NO es seguro**: Modifica datos
- ✅ **Idempotente**: Puedes hacerlo múltiples veces con el mismo resultado
- ✅ Reemplaza **TODOS** los campos
- ✅ Requiere enviar el objeto completo

### Ejemplo en BabyCash

```http
PUT /api/products/1 HTTP/1.1
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "Pañales Huggies Supreme",
  "description": "Pañales premium para bebés",
  "price": 50000,
  "stock": 30,
  "available": true
}
```

**Response: 200 OK**
```json
{
  "id": 1,
  "name": "Pañales Huggies Supreme",
  "description": "Pañales premium para bebés",
  "price": 50000,
  "stock": 30,
  "available": true
}
```

### En Spring Boot

```java
@PutMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ProductDTO> updateProduct(
    @PathVariable Long id,
    @RequestBody @Valid ProductDTO productDTO
) {
    ProductDTO updated = productService.update(id, productDTO);
    return ResponseEntity.ok(updated);
}
```

---

## 🟠 PATCH - Actualizar Parcial

### ¿Qué hace?
Modifica **solo algunos campos** de un recurso, sin afectar los demás.

### Características
- ❌ **NO es seguro**: Modifica datos
- ✅ **Idempotente**: Mismo resultado al repetir
- ✅ Solo envías los campos que quieres cambiar
- ✅ Más eficiente que PUT para cambios pequeños

### Ejemplo en BabyCash

```http
PATCH /api/products/1 HTTP/1.1
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "price": 48000,
  "stock": 40
}
```

**Response: 200 OK**
```json
{
  "id": 1,
  "name": "Pañales Huggies",  // NO cambió
  "description": "...",         // NO cambió
  "price": 48000,               // ✅ CAMBIÓ
  "stock": 40,                  // ✅ CAMBIÓ
  "available": true             // NO cambió
}
```

### En Spring Boot

```java
@PatchMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ProductDTO> partialUpdateProduct(
    @PathVariable Long id,
    @RequestBody Map<String, Object> updates
) {
    ProductDTO updated = productService.partialUpdate(id, updates);
    return ResponseEntity.ok(updated);
}
```

### Actualizar cantidad en carrito

```http
PATCH /api/cart/items/1 HTTP/1.1
Authorization: Bearer {token}
Content-Type: application/json

{
  "quantity": 5
}
```

---

## 🔴 DELETE - Eliminar

### ¿Qué hace?
Elimina un recurso del servidor.

### Características
- ❌ **NO es seguro**: Modifica datos (elimina)
- ✅ **Idempotente**: Eliminar 1000 veces = eliminar 1 vez
- ❌ Generalmente NO tiene body
- ✅ Retorna 204 No Content (sin cuerpo de respuesta)

### Ejemplo en BabyCash

```http
DELETE /api/products/1 HTTP/1.1
Authorization: Bearer {admin_token}
```

**Response: 204 No Content**

### En Spring Boot

```java
@DeleteMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

### Eliminar item del carrito

```http
DELETE /api/cart/items/1 HTTP/1.1
Authorization: Bearer {token}
```

### Vaciar carrito completo

```http
DELETE /api/cart/clear HTTP/1.1
Authorization: Bearer {token}
```

---

## 📊 Comparación de Verbos

| Verbo | Acción | Modifica Datos | Idempotente | Tiene Body | Retorna |
|-------|--------|----------------|-------------|------------|---------|
| **GET** | Consultar | ❌ No | ✅ Sí | ❌ No | 200 + Datos |
| **POST** | Crear | ✅ Sí | ❌ No | ✅ Sí | 201 + Recurso |
| **PUT** | Actualizar Todo | ✅ Sí | ✅ Sí | ✅ Sí | 200 + Recurso |
| **PATCH** | Actualizar Parcial | ✅ Sí | ✅ Sí | ✅ Sí | 200 + Recurso |
| **DELETE** | Eliminar | ✅ Sí | ✅ Sí | ❌ No | 204 Sin contenido |

---

## 🎯 ¿Cuándo usar cada uno?

### GET
✅ Ver lista de productos  
✅ Ver detalle de un producto  
✅ Ver mi carrito  
✅ Ver mis órdenes  
✅ Buscar productos  

### POST
✅ Registrar usuario  
✅ Login  
✅ Agregar producto al carrito  
✅ Crear orden  
✅ Crear producto nuevo (admin)  

### PUT
✅ Actualizar producto completo (admin)  
✅ Actualizar perfil completo  

### PATCH
✅ Cambiar solo el precio de un producto  
✅ Cambiar solo la cantidad en el carrito  
✅ Actualizar estado de una orden  

### DELETE
✅ Eliminar producto (admin)  
✅ Eliminar item del carrito  
✅ Vaciar carrito  

---

## 🔍 Idempotencia Explicada

### ¿Qué significa Idempotente?

Que si ejecutas la misma operación **múltiples veces**, el resultado es el **mismo** que si la ejecutas **una sola vez**.

### Ejemplos

**✅ GET es idempotente:**
```
GET /api/products/1  → Obtiene producto 1
GET /api/products/1  → Obtiene producto 1 (mismo resultado)
GET /api/products/1  → Obtiene producto 1 (mismo resultado)
```

**❌ POST NO es idempotente:**
```
POST /api/products   → Crea producto ID 1
POST /api/products   → Crea producto ID 2 (diferente!)
POST /api/products   → Crea producto ID 3 (diferente!)
```

**✅ PUT es idempotente:**
```
PUT /api/products/1 { price: 50000 }  → Actualiza precio a 50000
PUT /api/products/1 { price: 50000 }  → Precio sigue siendo 50000
PUT /api/products/1 { price: 50000 }  → Precio sigue siendo 50000
```

**✅ DELETE es idempotente:**
```
DELETE /api/products/1  → Elimina producto 1
DELETE /api/products/1  → Producto ya no existe (mismo resultado)
DELETE /api/products/1  → Producto ya no existe (mismo resultado)
```

---

## 💡 Convenciones REST

### Recursos y Verbos

| Operación | Verbo | URL |
|-----------|-------|-----|
| Listar productos | GET | `/api/products` |
| Ver producto | GET | `/api/products/{id}` |
| Crear producto | POST | `/api/products` |
| Actualizar producto | PUT | `/api/products/{id}` |
| Actualizar parcial | PATCH | `/api/products/{id}` |
| Eliminar producto | DELETE | `/api/products/{id}` |

### ❌ Errores Comunes

**MAL:**
```http
GET /api/createProduct    ❌ GET no debe crear
POST /api/getProducts     ❌ POST no debe solo consultar
DELETE /api/products      ❌ No eliminar colección completa
```

**BIEN:**
```http
POST /api/products        ✅ POST para crear
GET /api/products         ✅ GET para consultar
DELETE /api/products/1    ✅ DELETE con ID específico
```

---

## 🛡️ Seguridad por Verbo

### GET
```java
@GetMapping("/products")
public ResponseEntity<List<ProductDTO>> getProducts() {
    // No requiere autenticación
}
```

### POST con Autenticación
```java
@PostMapping("/cart/add")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<CartDTO> addToCart(@RequestBody AddToCartDTO dto) {
    // Requiere estar logueado
}
```

### PUT/PATCH con Rol ADMIN
```java
@PutMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ProductDTO> updateProduct(
    @PathVariable Long id,
    @RequestBody ProductDTO dto
) {
    // Solo ADMIN puede actualizar productos
}
```

### DELETE con Rol ADMIN
```java
@DeleteMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    // Solo ADMIN puede eliminar productos
}
```

---

## 🎪 Ejemplos Completos de BabyCash

### 1. Flujo de Registro (POST)

```http
POST /api/auth/register HTTP/1.1
Content-Type: application/json

{
  "email": "maria@gmail.com",
  "password": "password123",
  "name": "María García"
}
```

```java
@PostMapping("/auth/register")
public ResponseEntity<AuthResponseDTO> register(
    @RequestBody @Valid RegisterRequestDTO request
) {
    AuthResponseDTO response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

---

### 2. Flujo de Login (POST)

```http
POST /api/auth/login HTTP/1.1
Content-Type: application/json

{
  "email": "maria@gmail.com",
  "password": "password123"
}
```

```java
@PostMapping("/auth/login")
public ResponseEntity<AuthResponseDTO> login(
    @RequestBody @Valid LoginRequestDTO request
) {
    AuthResponseDTO response = authService.login(request);
    return ResponseEntity.ok(response);
}
```

---

### 3. Ver Productos (GET)

```http
GET /api/products HTTP/1.1
```

```java
@GetMapping("/products")
public ResponseEntity<List<ProductDTO>> getAllProducts() {
    List<ProductDTO> products = productService.findAll();
    return ResponseEntity.ok(products);
}
```

---

### 4. Agregar al Carrito (POST)

```http
POST /api/cart/add HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

```java
@PostMapping("/cart/add")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<CartDTO> addToCart(
    @RequestBody @Valid AddToCartDTO dto,
    @AuthenticationPrincipal UserDetails userDetails
) {
    CartDTO cart = cartService.addToCart(userDetails.getUsername(), dto);
    return ResponseEntity.ok(cart);
}
```

---

### 5. Actualizar Cantidad en Carrito (PATCH)

```http
PATCH /api/cart/items/1 HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "quantity": 5
}
```

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

---

### 6. Eliminar del Carrito (DELETE)

```http
DELETE /api/cart/items/1 HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

```java
@DeleteMapping("/cart/items/{itemId}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId) {
    cartService.removeItem(itemId);
    return ResponseEntity.noContent().build();
}
```

---

### 7. Crear Orden (POST)

```http
POST /api/orders HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "shippingAddress": "Calle 123 #45-67, Bogotá",
  "paymentMethod": "Credit Card"
}
```

```java
@PostMapping("/orders")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<OrderDTO> createOrder(
    @RequestBody @Valid CreateOrderDTO dto,
    @AuthenticationPrincipal UserDetails userDetails
) {
    OrderDTO order = orderService.createOrder(userDetails.getUsername(), dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(order);
}
```

---

## 🎯 Resumen

| Verbo | Usa para | Ejemplo BabyCash |
|-------|----------|------------------|
| **GET** | Consultar sin modificar | Ver productos, ver carrito |
| **POST** | Crear nuevos recursos | Registrar, login, agregar al carrito, crear orden |
| **PUT** | Reemplazar completamente | Actualizar producto completo |
| **PATCH** | Modificar parcialmente | Cambiar cantidad en carrito, actualizar precio |
| **DELETE** | Eliminar recursos | Quitar del carrito, eliminar producto |

---

**Última actualización**: Octubre 2025
