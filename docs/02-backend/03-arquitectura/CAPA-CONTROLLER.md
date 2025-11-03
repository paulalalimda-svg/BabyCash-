# 🎮 CAPA CONTROLLER

## 🎯 ¿Qué es un Controller?

El **Controller** es la **puerta de entrada** de tu aplicación. Recibe peticiones HTTP del frontend y devuelve respuestas.

### Analogía

Es como el **recepcionista** de un hotel:
- Recibe a los clientes (peticiones HTTP)
- Verifica que todo esté correcto
- Delega el trabajo al personal (Service)
- Entrega la respuesta al cliente

---

## 📦 @RestController

### Anotación Principal

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    // Métodos aquí
}
```

### ¿Qué hace @RestController?

**Combina 2 anotaciones:**

```java
@Controller        // Marca la clase como Controller
@ResponseBody      // Convierte respuestas a JSON automáticamente
```

### @Controller vs @RestController

```java
// ❌ @Controller (devuelve HTML)
@Controller
public class ProductController {
    
    @GetMapping("/products")
    public String getProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";  // Renderiza products.html
    }
}

// ✅ @RestController (devuelve JSON)
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping
    public List<ProductDTO> getProducts() {
        return productService.getAllProducts();  // Devuelve JSON automáticamente
    }
}
```

---

## 🗺️ @RequestMapping

### Mapeo de Rutas

```java
@RestController
@RequestMapping("/api/products")  // Prefijo para todos los métodos
public class ProductController {
    
    @GetMapping  // → GET /api/products
    public List<ProductDTO> getAll() { }
    
    @GetMapping("/{id}")  // → GET /api/products/5
    public ProductDTO getById(@PathVariable Long id) { }
    
    @PostMapping  // → POST /api/products
    public ProductDTO create(@RequestBody ProductDTO dto) { }
}
```

### Múltiples Rutas

```java
@RequestMapping(value = {"/api/products", "/api/v1/products"}, method = RequestMethod.GET)
public List<ProductDTO> getProducts() { }
```

---

## 🔤 Métodos HTTP

### GET - Obtener Datos

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    // GET /api/products
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    // GET /api/products/5
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    // GET /api/products?name=pañal&minPrice=10000
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) BigDecimal minPrice
    ) {
        List<ProductDTO> products = productService.searchProducts(name, minPrice);
        return ResponseEntity.ok(products);
    }
}
```

**Respuesta JSON:**

```json
[
  {
    "id": 1,
    "name": "Pañales Huggies",
    "price": 45000,
    "stock": 50
  },
  {
    "id": 2,
    "name": "Leche NAN",
    "price": 15000,
    "stock": 100
  }
]
```

---

### POST - Crear Datos

```java
@PostMapping
public ResponseEntity<ProductDTO> createProduct(
    @RequestBody @Valid ProductDTO productDTO
) {
    ProductDTO created = productService.createProduct(productDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

**Petición (Frontend):**

```javascript
axios.post('/api/products', {
    name: 'Pañales Huggies',
    price: 45000,
    stock: 50
})
```

**Respuesta:**

```json
HTTP 201 Created

{
  "id": 1,
  "name": "Pañales Huggies",
  "price": 45000,
  "stock": 50
}
```

---

### PUT - Actualizar Completo

```java
@PutMapping("/{id}")
public ResponseEntity<ProductDTO> updateProduct(
    @PathVariable Long id,
    @RequestBody @Valid ProductDTO productDTO
) {
    ProductDTO updated = productService.updateProduct(id, productDTO);
    return ResponseEntity.ok(updated);
}
```

**Petición:**

```javascript
axios.put('/api/products/1', {
    name: 'Pañales Huggies Supreme',
    price: 50000,
    stock: 40,
    available: true
})
```

---

### PATCH - Actualizar Parcial

```java
@PatchMapping("/{id}/stock")
public ResponseEntity<ProductDTO> updateStock(
    @PathVariable Long id,
    @RequestBody UpdateStockRequest request
) {
    ProductDTO updated = productService.updateStock(id, request.getStock());
    return ResponseEntity.ok(updated);
}
```

**Petición:**

```javascript
axios.patch('/api/products/1/stock', {
    stock: 30
})
```

---

### DELETE - Eliminar

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
}
```

**Petición:**

```javascript
axios.delete('/api/products/1')
```

**Respuesta:**

```
HTTP 204 No Content
```

---

## 📥 Parámetros de Entrada

### @PathVariable - Parámetros en la URL

```java
// GET /api/products/5
@GetMapping("/{id}")
public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
    ProductDTO product = productService.getProductById(id);
    return ResponseEntity.ok(product);
}

// GET /api/users/maria@gmail.com/orders/10
@GetMapping("/{email}/orders/{orderId}")
public ResponseEntity<OrderDTO> getOrder(
    @PathVariable String email,
    @PathVariable Long orderId
) {
    OrderDTO order = orderService.getOrder(email, orderId);
    return ResponseEntity.ok(order);
}
```

### @RequestParam - Query Parameters

```java
// GET /api/products?name=pañal&minPrice=10000&page=0&size=10
@GetMapping
public ResponseEntity<List<ProductDTO>> searchProducts(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) BigDecimal minPrice,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
) {
    List<ProductDTO> products = productService.searchProducts(name, minPrice, page, size);
    return ResponseEntity.ok(products);
}
```

**Frontend:**

```javascript
axios.get('/api/products', {
    params: {
        name: 'pañal',
        minPrice: 10000,
        page: 0,
        size: 10
    }
})
```

### @RequestBody - Cuerpo de la Petición

```java
@PostMapping
public ResponseEntity<ProductDTO> createProduct(
    @RequestBody @Valid ProductDTO productDTO
) {
    ProductDTO created = productService.createProduct(productDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

**Frontend:**

```javascript
axios.post('/api/products', {
    name: 'Pañales Huggies',
    price: 45000,
    stock: 50
})
```

### @RequestHeader - Headers

```java
@GetMapping
public ResponseEntity<List<ProductDTO>> getProducts(
    @RequestHeader("Authorization") String token
) {
    // Extraer token
    String jwt = token.replace("Bearer ", "");
    
    // Validar y obtener productos
    List<ProductDTO> products = productService.getAllProducts();
    return ResponseEntity.ok(products);
}
```

---

## 📤 ResponseEntity

### ¿Qué es?

`ResponseEntity` permite controlar:
- **Status HTTP** (200, 201, 404, 500, etc.)
- **Headers** (Content-Type, Authorization, etc.)
- **Body** (JSON)

### Ejemplos

```java
// 200 OK
return ResponseEntity.ok(product);

// 201 Created
return ResponseEntity.status(HttpStatus.CREATED).body(product);

// 204 No Content
return ResponseEntity.noContent().build();

// 404 Not Found
return ResponseEntity.notFound().build();

// 400 Bad Request
return ResponseEntity.badRequest().body("Email inválido");

// 500 Internal Server Error
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor");

// Con headers personalizados
return ResponseEntity.ok()
    .header("X-Total-Count", String.valueOf(products.size()))
    .body(products);
```

### Status HTTP Comunes

| Código | Significado | Cuándo Usar |
|--------|-------------|-------------|
| **200 OK** | Éxito | GET, PUT exitoso |
| **201 Created** | Creado | POST exitoso |
| **204 No Content** | Sin contenido | DELETE exitoso |
| **400 Bad Request** | Petición inválida | Validación falló |
| **401 Unauthorized** | No autenticado | Token inválido |
| **403 Forbidden** | Sin permisos | Usuario no autorizado |
| **404 Not Found** | No encontrado | Recurso no existe |
| **409 Conflict** | Conflicto | Email duplicado |
| **500 Internal Server Error** | Error del servidor | Error inesperado |

---

## ✅ Validación con @Valid

### DTO con Validaciones

```java
public class ProductDTO {
    
    @NotNull(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal price;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    // getters y setters
}
```

### Controller con Validación

```java
@PostMapping
public ResponseEntity<ProductDTO> createProduct(
    @RequestBody @Valid ProductDTO productDTO  // ← @Valid activa validación
) {
    ProductDTO created = productService.createProduct(productDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

### Manejo de Errores de Validación

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        
        return ResponseEntity.badRequest().body(errors);
    }
}
```

**Respuesta de Error:**

```json
HTTP 400 Bad Request

{
  "name": "El nombre es obligatorio",
  "price": "El precio debe ser mayor a 0"
}
```

---

## 🎯 Ejemplo Completo: ProductController

```java
package com.babycash.controller;

import com.babycash.dto.ProductDTO;
import com.babycash.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")  // Permitir frontend
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    // GET /api/products
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    // GET /api/products/5
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    // GET /api/products/search?name=pañal&minPrice=10000
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice
    ) {
        List<ProductDTO> products = productService.searchProducts(name, minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }
    
    // GET /api/products/paginated?page=0&size=10
    @GetMapping("/paginated")
    public ResponseEntity<Page<ProductDTO>> getProductsPaginated(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductDTO> products = productService.getProductsPaginated(page, size);
        return ResponseEntity.ok(products);
    }
    
    // POST /api/products
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
        @RequestBody @Valid ProductDTO productDTO
    ) {
        ProductDTO created = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // PUT /api/products/5
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
        @PathVariable Long id,
        @RequestBody @Valid ProductDTO productDTO
    ) {
        ProductDTO updated = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updated);
    }
    
    // PATCH /api/products/5/stock
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(
        @PathVariable Long id,
        @RequestParam Integer stock
    ) {
        ProductDTO updated = productService.updateStock(id, stock);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE /api/products/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    // GET /api/products/low-stock?threshold=10
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts(
        @RequestParam(defaultValue = "10") Integer threshold
    ) {
        List<ProductDTO> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }
}
```

---

## 🛡️ Manejo de Errores

### Exception Handler Global

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
```

### ErrorResponse DTO

```java
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    
    // Constructor, getters y setters
}
```

---

## 📋 Resumen

| Anotación | Propósito | Ejemplo |
|-----------|-----------|---------|
| `@RestController` | Marca clase como Controller REST | `@RestController` |
| `@RequestMapping` | Mapea ruta base | `@RequestMapping("/api/products")` |
| `@GetMapping` | GET (obtener) | `@GetMapping("/{id}")` |
| `@PostMapping` | POST (crear) | `@PostMapping` |
| `@PutMapping` | PUT (actualizar completo) | `@PutMapping("/{id}")` |
| `@PatchMapping` | PATCH (actualizar parcial) | `@PatchMapping("/{id}/stock")` |
| `@DeleteMapping` | DELETE (eliminar) | `@DeleteMapping("/{id}")` |
| `@PathVariable` | Parámetro en URL | `@PathVariable Long id` |
| `@RequestParam` | Query parameter | `@RequestParam String name` |
| `@RequestBody` | Cuerpo de petición | `@RequestBody ProductDTO dto` |
| `@Valid` | Validar DTO | `@Valid ProductDTO dto` |
| `ResponseEntity` | Control de respuesta HTTP | `ResponseEntity.ok(data)` |

---

**Última actualización**: Octubre 2025
