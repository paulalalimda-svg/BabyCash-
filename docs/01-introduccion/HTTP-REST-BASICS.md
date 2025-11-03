# HTTP Y REST - CONCEPTOS BÁSICOS

## 🌐 ¿Qué es HTTP?

**HyperText Transfer Protocol** - Protocolo de comunicación entre cliente y servidor.

### Analogía: Restaurante 🍽️

```
Cliente (Frontend)     →  Mesero (HTTP)  →  Cocina (Backend)
"Quiero una pizza"     →  Pedido         →  Prepara pizza
                       ←  Pizza lista    ←  
```

---

## 📨 Anatomía de una Petición HTTP

### Request (Petición)

```http
POST /api/products HTTP/1.1
Host: api.babycash.com
Content-Type: application/json
Authorization: Bearer eyJhbGc...

{
  "name": "Pañales",
  "price": 45000
}
```

**Componentes:**
1. **Método:** POST (crear producto)
2. **URL:** /api/products
3. **Headers:** Metadatos (tipo de contenido, token)
4. **Body:** Datos (JSON)

### Response (Respuesta)

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 123,
  "name": "Pañales",
  "price": 45000,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

**Componentes:**
1. **Status Code:** 201 (creado exitosamente)
2. **Headers:** Tipo de respuesta
3. **Body:** Datos creados

---

## 🔤 Métodos HTTP

| Método | Acción | Ejemplo | SQL Equivalente |
|--------|--------|---------|-----------------|
| **GET** | Leer/Obtener | Obtener productos | SELECT |
| **POST** | Crear | Crear producto | INSERT |
| **PUT** | Actualizar completo | Actualizar producto | UPDATE |
| **PATCH** | Actualizar parcial | Cambiar precio | UPDATE (parcial) |
| **DELETE** | Eliminar | Eliminar producto | DELETE |

### GET - Obtener Datos

```typescript
// Frontend
const response = await fetch('/api/products');
const products = await response.json();
```

```java
// Backend
@GetMapping("/api/products")
public List<Product> getAllProducts() {
    return productService.findAll();
}
```

**Características:**
- ❌ No tiene body
- ✅ Puede tener query params: `/api/products?category=bebe&sort=price`
- ✅ Idempotente (llamarlo múltiples veces = mismo resultado)
- ✅ Cacheable

### POST - Crear Datos

```typescript
// Frontend
const response = await fetch('/api/products', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    name: 'Pañales',
    price: 45000
  })
});
```

```java
// Backend
@PostMapping("/api/products")
public Product createProduct(@RequestBody Product product) {
    return productService.save(product);
}
```

**Características:**
- ✅ Tiene body (datos a crear)
- ❌ No idempotente (crear 2 veces = 2 productos)
- ❌ No cacheable

### PUT - Actualizar Completo

```typescript
// Frontend - Actualizar TODO el producto
await fetch('/api/products/123', {
  method: 'PUT',
  body: JSON.stringify({
    name: 'Pañales Premium',
    price: 55000,
    description: 'Nueva descripción',
    stock: 100
  })
});
```

```java
// Backend
@PutMapping("/api/products/{id}")
public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
    return productService.update(id, product);
}
```

### PATCH - Actualizar Parcial

```typescript
// Frontend - Solo cambiar precio
await fetch('/api/products/123', {
  method: 'PATCH',
  body: JSON.stringify({
    price: 48000
  })
});
```

```java
// Backend
@PatchMapping("/api/products/{id}")
public Product patchProduct(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
    return productService.partialUpdate(id, updates);
}
```

### DELETE - Eliminar

```typescript
// Frontend
await fetch('/api/products/123', {
  method: 'DELETE'
});
```

```java
// Backend
@DeleteMapping("/api/products/{id}")
public void deleteProduct(@PathVariable Long id) {
    productService.delete(id);
}
```

---

## 📊 Status Codes

### 2xx - Éxito ✅

| Código | Significado | Cuándo Usar |
|--------|-------------|-------------|
| **200 OK** | Éxito general | GET, PUT, PATCH exitoso |
| **201 Created** | Recurso creado | POST exitoso |
| **204 No Content** | Éxito sin contenido | DELETE exitoso |

### 3xx - Redirección 🔄

| Código | Significado | Cuándo Usar |
|--------|-------------|-------------|
| **301 Moved Permanently** | Recurso movido permanentemente | URL antigua → nueva |
| **304 Not Modified** | No ha cambiado | Cache válido |

### 4xx - Error del Cliente ❌

| Código | Significado | Cuándo Usar |
|--------|-------------|-------------|
| **400 Bad Request** | Petición inválida | Datos mal formados |
| **401 Unauthorized** | No autenticado | Sin token o token inválido |
| **403 Forbidden** | Sin permisos | Usuario no admin |
| **404 Not Found** | No encontrado | Producto no existe |
| **422 Unprocessable Entity** | Validación fallida | Email ya existe |

### 5xx - Error del Servidor 💥

| Código | Significado | Cuándo Usar |
|--------|-------------|-------------|
| **500 Internal Server Error** | Error interno | Excepción no manejada |
| **502 Bad Gateway** | Gateway error | Servicio externo caído |
| **503 Service Unavailable** | Servicio no disponible | Mantenimiento |

### Ejemplos en Baby Cash

```java
// 200 OK - Producto obtenido
@GetMapping("/api/products/{id}")
public ResponseEntity<Product> getProduct(@PathVariable Long id) {
    return productService.findById(id)
        .map(ResponseEntity::ok) // 200
        .orElse(ResponseEntity.notFound().build()); // 404
}

// 201 Created - Producto creado
@PostMapping("/api/products")
public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    Product saved = productService.save(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201
}

// 204 No Content - Producto eliminado
@DeleteMapping("/api/products/{id}")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build(); // 204
}

// 400 Bad Request - Validación fallida
@PostMapping("/api/products")
public ResponseEntity<?> createProduct(@Valid @RequestBody Product product) {
    // Si @Valid falla, Spring devuelve 400 automáticamente
    return ResponseEntity.ok(productService.save(product));
}

// 401 Unauthorized - No autenticado
@GetMapping("/api/admin/products")
public ResponseEntity<List<Product>> adminProducts(Authentication auth) {
    if (auth == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
    }
    return ResponseEntity.ok(productService.findAll());
}

// 403 Forbidden - Sin permisos
@DeleteMapping("/api/products/{id}")
public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication auth) {
    if (!hasRole(auth, "ADMIN")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
    }
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

---

## 🏗️ ¿Qué es REST?

**REpresentational State Transfer** - Estilo arquitectónico para APIs.

### Principios REST

#### 1. Client-Server

Cliente y servidor separados:
```
Frontend (React)  ←→  Backend (Spring Boot)
```

#### 2. Stateless (Sin Estado)

Cada petición contiene toda la información necesaria:
```http
GET /api/products
Authorization: Bearer eyJhbGc...
```

❌ Backend NO guarda sesión (no: "usuario123 ya hizo login")
✅ Backend verifica token en CADA petición

#### 3. Cacheable

Respuestas pueden ser cacheadas:
```http
HTTP/1.1 200 OK
Cache-Control: max-age=3600

[productos...]
```

#### 4. Uniform Interface

URLs predecibles:
```
GET    /api/products     - Obtener todos
GET    /api/products/123 - Obtener uno
POST   /api/products     - Crear
PUT    /api/products/123 - Actualizar
DELETE /api/products/123 - Eliminar
```

---

## 📝 Estructura de URLs REST

### Naming Conventions

✅ **Buenas prácticas:**
```
/api/products           - Plural, lowercase
/api/products/123       - ID numérico
/api/products/123/reviews  - Relación
/api/users/me           - Recurso especial
```

❌ **Malas prácticas:**
```
/api/getProducts        - No verbos en URL
/api/Products           - No mayúsculas
/api/product            - Plural, no singular
/api/products/delete/123 - Verbo en URL (usar DELETE)
```

### Query Parameters

Para filtrar, ordenar, paginar:
```
/api/products?category=bebe
/api/products?sort=price&order=asc
/api/products?page=2&limit=20
/api/products?search=pañales&minPrice=10000&maxPrice=50000
```

### Ejemplos en Baby Cash

```typescript
// Frontend
// Obtener productos con filtros
const response = await fetch('/api/products?category=bebe&sort=price');

// Buscar productos
const response = await fetch('/api/products?search=pañales');

// Paginación
const response = await fetch('/api/products?page=2&limit=20');

// Obtener reviews de un producto
const response = await fetch('/api/products/123/reviews');
```

```java
// Backend
@GetMapping("/api/products")
public List<Product> getProducts(
    @RequestParam(required = false) String category,
    @RequestParam(required = false) String sort,
    @RequestParam(required = false) String search,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int limit
) {
    return productService.findWithFilters(category, sort, search, page, limit);
}

@GetMapping("/api/products/{id}/reviews")
public List<Review> getProductReviews(@PathVariable Long id) {
    return reviewService.findByProductId(id);
}
```

---

## 🔗 Headers HTTP

### Request Headers

```http
GET /api/products HTTP/1.1
Host: api.babycash.com
Content-Type: application/json
Authorization: Bearer eyJhbGc...
Accept: application/json
User-Agent: Mozilla/5.0
Accept-Language: es-ES
```

**Comunes:**
- `Content-Type`: Tipo de datos enviados (JSON, XML, form-data)
- `Authorization`: Token de autenticación
- `Accept`: Tipo de datos aceptados en respuesta
- `User-Agent`: Navegador/cliente
- `Accept-Language`: Idioma preferido

### Response Headers

```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 1234
Cache-Control: max-age=3600
Access-Control-Allow-Origin: *
X-RateLimit-Remaining: 98
```

**Comunes:**
- `Content-Type`: Tipo de datos en respuesta
- `Content-Length`: Tamaño en bytes
- `Cache-Control`: Política de cache
- `Access-Control-Allow-Origin`: CORS
- `X-RateLimit-*`: Rate limiting

### Configurar Headers en Baby Cash

```typescript
// Frontend
const response = await axios.get('/api/products', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Accept-Language': 'es-ES'
  }
});
```

```java
// Backend
@GetMapping("/api/products")
public ResponseEntity<List<Product>> getProducts() {
    List<Product> products = productService.findAll();
    
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Total-Count", String.valueOf(products.size()));
    headers.add("Cache-Control", "max-age=3600");
    
    return new ResponseEntity<>(products, headers, HttpStatus.OK);
}
```

---

## 🎯 CRUD REST en Baby Cash

### Products API

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // CREATE
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product saved = productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // READ (All)
    @GetMapping
    public ResponseEntity<List<Product>> getAll(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String search
    ) {
        List<Product> products = productService.findAll(category, search);
        return ResponseEntity.ok(products);
    }

    // READ (One)
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
        @PathVariable Long id,
        @Valid @RequestBody Product product
    ) {
        return productService.update(id, product)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Frontend Consumption

```typescript
// src/services/productService.ts
import axios from 'axios';

const API_URL = '/api/products';

export const productService = {
  // CREATE
  create: async (product: ProductFormData) => {
    const response = await axios.post(API_URL, product);
    return response.data;
  },

  // READ (All)
  getAll: async (filters?: { category?: string; search?: string }) => {
    const response = await axios.get(API_URL, { params: filters });
    return response.data;
  },

  // READ (One)
  getById: async (id: number) => {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
  },

  // UPDATE
  update: async (id: number, product: ProductFormData) => {
    const response = await axios.put(`${API_URL}/${id}`, product);
    return response.data;
  },

  // DELETE
  delete: async (id: number) => {
    await axios.delete(`${API_URL}/${id}`);
  }
};
```

---

## 🎓 Para la Evaluación del SENA

**1. "¿Qué es HTTP?"**

> "HyperText Transfer Protocol. Protocolo de comunicación cliente-servidor.
> 
> **Analogía:** Como pedir comida en un restaurante.
> - Cliente hace pedido (request)
> - Servidor prepara y entrega (response)
> 
> Baby Cash: Frontend (React) envía requests al Backend (Spring Boot)."

---

**2. "¿Cuáles son los métodos HTTP principales?"**

> "GET (leer), POST (crear), PUT (actualizar completo), PATCH (actualizar parcial), DELETE (eliminar).
> 
> **Ejemplo Baby Cash:**
> - GET `/api/products` - Ver productos
> - POST `/api/products` - Crear producto
> - PUT `/api/products/123` - Actualizar producto
> - DELETE `/api/products/123` - Eliminar producto"

---

**3. "¿Qué significa REST?"**

> "REpresentational State Transfer. Estilo arquitectónico para APIs.
> 
> **Principios:**
> - URLs predecibles (`/api/products`)
> - Métodos HTTP estándar (GET, POST, etc.)
> - Stateless (sin sesión en servidor)
> - Respuestas cacheables
> 
> Baby Cash: API REST completa con todas las operaciones CRUD."

---

**4. "¿Qué significan los status codes?"**

> "Códigos que indican resultado de la petición:
> 
> - **2xx:** Éxito (200 OK, 201 Created)
> - **3xx:** Redirección (301 Moved)
> - **4xx:** Error del cliente (400 Bad Request, 404 Not Found)
> - **5xx:** Error del servidor (500 Internal Error)
> 
> Baby Cash: Usa todos los códigos apropiados para cada operación."

---

## 🚀 Conclusión

**HTTP + REST en Baby Cash:**
- ✅ Métodos HTTP estándar
- ✅ Status codes apropiados
- ✅ URLs RESTful predecibles
- ✅ Stateless (JWT tokens)
- ✅ Headers configurados correctamente

**Siguiente:** `SPRING-BOOT-BASICS.md` 🚀
