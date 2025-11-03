# SPRING BOOT - CONCEPTOS BÁSICOS

## 🍃 ¿Qué es Spring Boot?

**Framework de Java para crear aplicaciones backend de forma rápida y sencilla.**

### Sin Spring Boot (tradicional):
```xml
<!-- Configurar servidor -->
<!-- Configurar base de datos -->
<!-- Configurar seguridad -->
<!-- Configurar... TODO manualmente -->
```

### Con Spring Boot:
```java
@SpringBootApplication
public class BabyCashApplication {
    public static void main(String[] args) {
        SpringApplication.run(BabyCashApplication.class, args);
    }
}
// ✅ ¡Listo! Servidor corriendo con todo configurado
```

---

## 🎯 Características Principales

### 1. **Auto-Configuration** (Configuración Automática)
Spring Boot configura automáticamente según las dependencias:

```xml
<!-- Agregar dependency -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- ✅ Spring Boot automáticamente configura:
- EntityManagerFactory
- TransactionManager
- DataSource
-->
```

### 2. **Starter Dependencies**
Paquetes pre-configurados:

```xml
<!-- Web (REST API) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Database (JPA + Hibernate) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 3. **Embedded Server**
Servidor incluido (Tomcat por defecto):

```bash
# Correr aplicación
./mvnw spring-boot:run

# ✅ Servidor en http://localhost:8080
```

### 4. **Production-Ready Features**
Herramientas para producción:
- Health checks
- Metrics
- Logging
- Monitoring

---

## 🏗️ Arquitectura en Capas

```
┌─────────────────────────────────┐
│      Controller Layer           │  ← REST API endpoints
│  @RestController, @GetMapping   │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│       Service Layer             │  ← Lógica de negocio
│  @Service                       │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│     Repository Layer            │  ← Acceso a datos
│  @Repository, JpaRepository     │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│         Database                │  ← PostgreSQL
└─────────────────────────────────┘
```

---

## 📦 Dependency Injection (Inyección de Dependencias)

### ¿Qué es?

**Analogía:** Enchufar aparatos sin preocuparse por la electricidad

```java
// ❌ Sin inyección (crear manualmente)
public class ProductService {
    private ProductRepository repository = new ProductRepository(); // Manual
}

// ✅ Con inyección (Spring lo hace)
@Service
public class ProductService {
    @Autowired // Spring automáticamente inyecta
    private ProductRepository repository;
}
```

### Formas de Inyección

#### 1. Constructor Injection (Recomendado)
```java
@Service
public class ProductService {
    private final ProductRepository repository;
    
    // Spring inyecta automáticamente
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
}
```

#### 2. Field Injection
```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
}
```

#### 3. Setter Injection
```java
@Service
public class ProductService {
    private ProductRepository repository;
    
    @Autowired
    public void setRepository(ProductRepository repository) {
        this.repository = repository;
    }
}
```

---

## 🎭 Annotations Principales

### @SpringBootApplication

```java
@SpringBootApplication
public class BabyCashApplication {
    public static void main(String[] args) {
        SpringApplication.run(BabyCashApplication.class, args);
    }
}
```

**Combina 3 annotations:**
- `@Configuration`: Clase de configuración
- `@EnableAutoConfiguration`: Activa auto-configuración
- `@ComponentScan`: Escanea componentes

### @RestController

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }
}
```

**Combina:**
- `@Controller`: Marca como controlador
- `@ResponseBody`: Respuesta automáticamente en JSON

### @Service

```java
@Service
public class ProductService {
    // Lógica de negocio
}
```

Marca clase como servicio (lógica de negocio).

### @Repository

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Spring implementa automáticamente
}
```

Marca clase como repositorio (acceso a datos).

### @Entity

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Double price;
}
```

Marca clase como entidad de base de datos.

---

## 🗺️ Request Mapping

### @GetMapping (Leer)

```java
@GetMapping("/products")
public List<Product> getAll() {
    return productService.findAll();
}

@GetMapping("/products/{id}")
public Product getById(@PathVariable Long id) {
    return productService.findById(id);
}
```

### @PostMapping (Crear)

```java
@PostMapping("/products")
public Product create(@RequestBody Product product) {
    return productService.save(product);
}
```

### @PutMapping (Actualizar)

```java
@PutMapping("/products/{id}")
public Product update(@PathVariable Long id, @RequestBody Product product) {
    return productService.update(id, product);
}
```

### @DeleteMapping (Eliminar)

```java
@DeleteMapping("/products/{id}")
public void delete(@PathVariable Long id) {
    productService.delete(id);
}
```

---

## ⚙️ application.properties

```properties
# Servidor
server.port=8080

# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Logging
logging.level.root=INFO
logging.level.com.babycash=DEBUG
```

---

## 🎓 Para la Evaluación del SENA

**1. "¿Qué es Spring Boot?"**

> "Framework de Java para crear aplicaciones backend rápidamente. Incluye servidor, configuración automática y dependencias pre-empaquetadas (starters)."

**2. "¿Qué es Dependency Injection?"**

> "Spring automáticamente crea e inyecta objetos. Usa @Autowired o constructor. Ejemplo: ProductService recibe ProductRepository sin crearlo manualmente."

**3. "¿Qué hacen las annotations principales?"**

> "@RestController (API endpoints), @Service (lógica negocio), @Repository (acceso datos), @Entity (tabla BD)."

---

**Siguiente:** `JPA-HIBERNATE-BASICS.md` 🚀
