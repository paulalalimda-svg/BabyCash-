# 🌟 ANOTACIONES DE SPRING

## 🎯 ¿Qué Hacen las Anotaciones de Spring?

Le dicen a Spring Boot **qué rol cumple cada clase** en la aplicación (Controller, Service, Repository, etc.).

---

## 🚀 @SpringBootApplication

### ¿Qué hace?

Marca la **clase principal** de la aplicación. Es la que contiene `main()`.

### Código

```java
@SpringBootApplication
public class BabyCashApplication {
    public static void main(String[] args) {
        SpringApplication.run(BabyCashApplication.class, args);
    }
}
```

### ¿Qué incluye?

Es una **combinación de 3 anotaciones**:

```java
@SpringBootApplication
= @Configuration + @EnableAutoConfiguration + @ComponentScan
```

1. **@Configuration**: Clase de configuración
2. **@EnableAutoConfiguration**: Auto-configura según dependencias
3. **@ComponentScan**: Escanea @Component, @Service, @Controller, etc.

---

## 🧩 @Component

### ¿Qué hace?

Marca una clase como **bean de Spring** (Spring la gestiona).

### Ejemplo

```java
@Component
public class EmailHelper {
    
    public String formatEmail(String name) {
        return "Hola " + name;
    }
}
```

### ¿Cuándo usar?

Cuando la clase no es Controller, Service ni Repository, pero quieres que Spring la gestione.

---

## ⚙️ @Service

### ¿Qué hace?

Marca una clase como **servicio** (lógica de negocio). Es igual que `@Component` pero más específico.

### Ejemplo

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }
}
```

### ¿Por qué usar @Service en vez de @Component?

- ✅ **Claridad**: Indica que es lógica de negocio
- ✅ **Convención**: Estándar de la industria
- ✅ **Futuro**: Spring puede agregar comportamiento específico para @Service

---

## 💾 @Repository

### ¿Qué hace?

Marca una **interfaz** como repositorio (acceso a base de datos).

### Ejemplo

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByActiveTrue();
}
```

### ¿Por qué usar @Repository?

- ✅ **Auto-implementación**: Spring Data JPA genera la implementación
- ✅ **Traducción de excepciones**: Convierte excepciones SQL a Spring DataAccessException

---

## 🎮 @Controller

### ¿Qué hace?

Marca una clase como **controlador MVC** (retorna vistas HTML).

### Ejemplo

```java
@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Bienvenido");
        return "home"; // Retorna vista home.html
    }
}
```

### ¿Cuándo usar?

Cuando usas **Thymeleaf** o **JSP** (vistas del lado del servidor).

---

## 🌐 @RestController

### ¿Qué hace?

Marca una clase como **controlador REST** (retorna JSON/XML).

### Ejemplo

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
    
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
    
    @PostMapping
    public ProductResponse createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }
}
```

### ¿Qué incluye?

```java
@RestController = @Controller + @ResponseBody
```

- **@Controller**: Marca como controlador
- **@ResponseBody**: Convierte automáticamente el retorno a JSON

### Diferencia @Controller vs @RestController

```java
// @Controller - Retorna VISTAS HTML
@Controller
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "home";  // Busca archivo home.html
    }
}

// @RestController - Retorna JSON
@RestController
public class ProductController {
    @GetMapping("/products")
    public List<Product> getAll() {
        return products;  // Se convierte a JSON automáticamente
    }
}
```

---

## 🔄 Jerarquía de Anotaciones

```
@Component
    ├── @Service      (lógica de negocio)
    ├── @Repository   (acceso a datos)
    └── @Controller   (controlador MVC)
            └── @RestController (controlador REST)
```

Todas heredan de `@Component`.

---

## 📊 Comparación

| Anotación | Tipo | Uso | Retorna |
|-----------|------|-----|---------|
| `@Component` | Clase | Componente genérico | - |
| `@Service` | Clase | Lógica de negocio | Objetos Java |
| `@Repository` | Interfaz | Acceso a BD | Entities |
| `@Controller` | Clase | MVC (vistas) | HTML |
| `@RestController` | Clase | API REST | JSON |

---

## 🎓 Ejemplo Completo

### Repository

```java
package com.babycash.backend.repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
}
```

### Service

```java
package com.babycash.backend.service;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
```

### Controller

```java
package com.babycash.backend.controller;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<Product>> getByCategory(
        @RequestParam String category
    ) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
}
```

---

## ✅ Buenas Prácticas

### 1. Usa la Anotación Correcta

```java
// ❌ MAL - Service marcado como @Component
@Component
public class ProductService { }

// ✅ BIEN - Service marcado como @Service
@Service
public class ProductService { }
```

### 2. Un Archivo = Una Clase

```java
// ✅ ProductService.java contiene solo ProductService
@Service
public class ProductService {
    // ...
}
```

### 3. Nombre Descriptivo

```java
// ✅ BIEN
@Service
public class ProductService { }

@RestController
public class ProductController { }

@Repository
public interface ProductRepository { }
```

---

## 🔍 ¿Cómo Spring Detecta las Anotaciones?

```
1. @SpringBootApplication inicia

2. @ComponentScan escanea el paquete base
   com.babycash.backend

3. Busca todas las clases con:
   @Component, @Service, @Repository, @Controller

4. Crea instancias (beans)

5. Inyecta dependencias (@Autowired)

6. Aplicación lista
```

---

## 📋 Resumen

| Anotación | Uso | Ejemplo |
|-----------|-----|---------|
| `@SpringBootApplication` | Clase principal | `BabyCashApplication.java` |
| `@Component` | Componente genérico | `EmailHelper.java` |
| `@Service` | Lógica de negocio | `ProductService.java` |
| `@Repository` | Acceso a BD | `ProductRepository.java` |
| `@Controller` | MVC (vistas) | `HomeController.java` |
| `@RestController` | API REST | `ProductController.java` |

---

**Última actualización**: Octubre 2025
