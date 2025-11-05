# 📚 Fundamentos de Java y Spring Boot - Completo

Guía exhaustiva de Java, Spring Boot, Maven, Testing, Clean Code y principios SOLID.

---

## 📋 Tabla de Contenidos

1. [Estructura de Java](#estructura-de-java)
2. [Java Avanzado](#java-avanzado)
3. [Maven y Gestión de Proyectos](#maven-y-gestión-de-proyectos)
4. [Spring Framework](#spring-framework)
5. [Spring Boot](#spring-boot)
6. [Testing en Java](#testing-en-java)
7. [Clean Code](#clean-code)
8. [Principios SOLID](#principios-solid)
9. [Patrones de Diseño](#patrones-de-diseño)
10. [Buenas Prácticas](#buenas-prácticas)

---

## 🏗️ Estructura de Java

### Anatomía de un Archivo Java

```java
// 1. Declaración del paquete
package com.babycash.models;

// 2. Imports
import java.util.List;
import java.util.ArrayList;

// 3. Comentarios de documentación (JavaDoc)
/**
 * Clase que representa un producto en el sistema Baby Cash.
 * @author Juan Pérez
 * @version 1.0
 */
// 4. Declaración de la clase
public class Product {
    
    // 5. Atributos (variables de instancia)
    private Long id;
    private String name;
    private double price;
    
    // 6. Constructores
    public Product() {
        // Constructor vacío
    }
    
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    // 7. Métodos
    public void displayInfo() {
        System.out.println("Producto: " + name + ", Precio: $" + price);
    }
    
    // 8. Getters y Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
```

### Paquetes (Packages)

**Propósito**: Organizar clases en grupos lógicos y evitar conflictos de nombres.

```
com.babycash
├── controllers      # Controladores REST
│   ├── ProductController.java
│   └── OrderController.java
├── services         # Lógica de negocio
│   ├── ProductService.java
│   └── OrderService.java
├── repositories     # Acceso a base de datos
│   ├── ProductRepository.java
│   └── OrderRepository.java
├── models          # Entidades de dominio
│   ├── Product.java
│   └── Order.java
└── dto             # Data Transfer Objects
    ├── ProductDTO.java
    └── OrderDTO.java
```

### Modificadores de Acceso

```java
public class Ejemplo {
    
    // PUBLIC: Accesible desde cualquier lugar
    public String publico = "Todos pueden acceder";
    
    // PRIVATE: Solo accesible dentro de la clase
    private String privado = "Solo esta clase";
    
    // PROTECTED: Accesible en la clase, subclases y mismo paquete
    protected String protegido = "Clase, hijos y paquete";
    
    // DEFAULT (sin modificador): Accesible solo en el mismo paquete
    String porDefecto = "Solo en este paquete";
}
```

**Tabla de acceso**:

| Modificador | Misma Clase | Mismo Paquete | Subclase | Otros |
|-------------|-------------|---------------|----------|-------|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| default | ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

### Modificadores No-Acceso

```java
// STATIC: Pertenece a la clase, no a instancias
public class Contador {
    private static int total = 0;  // Compartido entre todas las instancias
    
    public Contador() {
        total++;
    }
    
    public static int getTotal() {
        return total;  // Método estático
    }
}

// FINAL: No se puede modificar/sobrescribir
public class Constantes {
    public static final double PI = 3.14159;  // Constante
    public static final String NOMBRE_APP = "Baby Cash";
}

// ABSTRACT: Clase o método abstracto
public abstract class Animal {
    public abstract void hacerSonido();  // Sin implementación
}

// SYNCHRONIZED: Thread-safe (para concurrencia)
public synchronized void metodoSeguro() {
    // Solo un thread puede ejecutar esto a la vez
}
```

---

## 🚀 Java Avanzado

### Interfaces

**Definición**: Contrato que define QUÉ debe hacer una clase, no CÓMO.

```java
// Interface
public interface Calculable {
    double calcular();
    void mostrarResultado();
    
    // Java 8+: Métodos default
    default void saludar() {
        System.out.println("Hola desde interface");
    }
    
    // Java 8+: Métodos estáticos
    static double PI() {
        return 3.14159;
    }
}

// Implementación
public class Circulo implements Calculable {
    private double radio;
    
    @Override
    public double calcular() {
        return Calculable.PI() * radio * radio;
    }
    
    @Override
    public void mostrarResultado() {
        System.out.println("Área: " + calcular());
    }
}
```

**Interface vs Clase Abstracta**:

| Interface | Clase Abstracta |
|-----------|-----------------|
| Solo constantes | Variables de instancia |
| Métodos abstractos por defecto | Puede tener métodos concretos |
| Herencia múltiple | Herencia simple |
| `implements` | `extends` |

### Clases Anidadas

```java
public class Externa {
    private String dato = "Externo";
    
    // Clase interna
    public class Interna {
        public void acceder() {
            System.out.println(dato);  // Puede acceder a miembros privados
        }
    }
    
    // Clase estática anidada
    public static class EstaticaAnidada {
        public void metodo() {
            // No puede acceder a miembros de instancia de Externa
        }
    }
}

// Uso
Externa externa = new Externa();
Externa.Interna interna = externa.new Interna();
Externa.EstaticaAnidada estatica = new Externa.EstaticaAnidada();
```

### Genéricos (Generics)

```java
// Clase genérica
public class Caja<T> {
    private T contenido;
    
    public void guardar(T item) {
        this.contenido = item;
    }
    
    public T obtener() {
        return contenido;
    }
}

// Uso
Caja<String> cajaTexto = new Caja<>();
cajaTexto.guardar("Hola");
String texto = cajaTexto.obtener();

Caja<Integer> cajaNumero = new Caja<>();
cajaNumero.guardar(42);
Integer numero = cajaNumero.obtener();

// Método genérico
public <T> void imprimirArray(T[] array) {
    for (T elemento : array) {
        System.out.println(elemento);
    }
}
```

### Enums

```java
// Enum simple
public enum DiaSemana {
    LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
}

// Enum con propiedades
public enum EstadoOrden {
    PENDIENTE("Pendiente de pago", 1),
    PAGADA("Pagada", 2),
    ENVIADA("Enviada", 3),
    ENTREGADA("Entregada", 4),
    CANCELADA("Cancelada", -1);
    
    private final String descripcion;
    private final int codigo;
    
    EstadoOrden(String descripcion, int codigo) {
        this.descripcion = descripcion;
        this.codigo = codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public int getCodigo() {
        return codigo;
    }
}

// Uso
EstadoOrden estado = EstadoOrden.PAGADA;
System.out.println(estado.getDescripcion());  // "Pagada"
```

### Anotaciones (Annotations)

```java
// Anotaciones integradas
@Override           // Indica que sobrescribe un método
@Deprecated         // Marca como obsoleto
@SuppressWarnings("unchecked")  // Suprime advertencias

// Anotaciones de Spring
@Component          // Marca como componente de Spring
@Service            // Marca como servicio
@Repository         // Marca como repositorio
@Controller         // Marca como controlador
@RestController     // Controlador REST
@Autowired          // Inyección de dependencias
@GetMapping         // Mapeo GET
@PostMapping        // Mapeo POST

// Ejemplo
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }
}
```

### Lambdas y Stream API (Java 8+)

```java
// Lambda: Función anónima
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5);

// Antes (Java 7)
for (Integer num : numeros) {
    System.out.println(num * 2);
}

// Con lambda (Java 8+)
numeros.forEach(num -> System.out.println(num * 2));

// Stream API
List<Integer> pares = numeros.stream()
    .filter(n -> n % 2 == 0)      // Filtrar pares
    .map(n -> n * 2)               // Multiplicar por 2
    .collect(Collectors.toList()); // Convertir a lista

// Ejemplo con objetos
List<Product> productos = getProductos();

// Filtrar productos caros y obtener nombres
List<String> productosCaros = productos.stream()
    .filter(p -> p.getPrice() > 50)
    .map(Product::getName)
    .sorted()
    .collect(Collectors.toList());

// Calcular precio promedio
double promedio = productos.stream()
    .mapToDouble(Product::getPrice)
    .average()
    .orElse(0.0);
```

### Optional

```java
// Evitar NullPointerException
Optional<Product> productoOpt = productRepository.findById(1L);

// Forma antigua
Product producto = productoOpt.orElse(null);
if (producto != null) {
    System.out.println(producto.getName());
}

// Forma moderna
productoOpt.ifPresent(p -> System.out.println(p.getName()));

// Con valor por defecto
Product producto = productoOpt.orElse(new Product());

// Lanzar excepción si no existe
Product producto = productoOpt.orElseThrow(
    () -> new RuntimeException("Producto no encontrado")
);

// Encadenar operaciones
String nombre = productoOpt
    .map(Product::getName)
    .map(String::toUpperCase)
    .orElse("SIN NOMBRE");
```

---

## 📦 Maven y Gestión de Proyectos

### Estructura de Proyecto Maven

```
baby-cash-backend/
├── src/
│   ├── main/
│   │   ├── java/              # Código fuente
│   │   │   └── com/babycash/
│   │   │       ├── BabyCashApplication.java
│   │   │       ├── controllers/
│   │   │       ├── services/
│   │   │       ├── repositories/
│   │   │       └── models/
│   │   └── resources/         # Recursos
│   │       ├── application.properties
│   │       ├── static/        # Archivos estáticos
│   │       └── templates/     # Plantillas
│   └── test/
│       └── java/              # Tests
│           └── com/babycash/
│               └── BabyCashApplicationTests.java
├── target/                    # Archivos compilados (generado)
├── pom.xml                    # Configuración de Maven
└── mvnw                       # Maven Wrapper
```

### pom.xml Explicado

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    
    <!-- Información del proyecto -->
    <groupId>com.babycash</groupId>
    <artifactId>backend</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <name>Baby Cash Backend</name>
    <description>E-commerce backend para Baby Cash</description>
    
    <!-- Proyecto padre (Spring Boot) -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.7</version>
    </parent>
    
    <!-- Propiedades -->
    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
    </properties>
    
    <!-- Dependencias -->
    <dependencies>
        <!-- Spring Boot Web (REST APIs) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring Data JPA (Base de datos) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <!-- PostgreSQL Driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Lombok (reduce boilerplate) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <!-- Build plugins -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Ciclo de Vida de Maven

```bash
# 1. clean: Limpia archivos compilados anteriores
mvn clean

# 2. validate: Valida que el proyecto esté correcto
mvn validate

# 3. compile: Compila código fuente
mvn compile

# 4. test: Ejecuta tests unitarios
mvn test

# 5. package: Empaqueta en JAR/WAR
mvn package

# 6. verify: Ejecuta verificaciones adicionales
mvn verify

# 7. install: Instala en repositorio local
mvn install

# 8. deploy: Despliega a repositorio remoto
mvn deploy

# Comandos compuestos
mvn clean install       # Limpiar y compilar todo
mvn clean package       # Limpiar y empaquetar
mvn spring-boot:run     # Ejecutar aplicación Spring Boot
```

### Scopes de Dependencias

```xml
<!-- COMPILE: Por defecto, disponible en todas las fases -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <scope>compile</scope>  <!-- Opcional, es el default -->
</dependency>

<!-- PROVIDED: Proporcionado por el servidor (Tomcat, etc.) -->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>servlet-api</artifactId>
    <scope>provided</scope>
</dependency>

<!-- RUNTIME: Solo necesario en ejecución, no en compilación -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- TEST: Solo para tests -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 🌱 Spring Framework

### ¿Qué es Spring?

Spring es un **framework de desarrollo de aplicaciones Java** que proporciona:
- Inversión de Control (IoC)
- Inyección de Dependencias (DI)
- Programación Orientada a Aspectos (AOP)
- Gestión de transacciones
- Integración con otras tecnologías

### Inversión de Control (IoC)

**Concepto**: El framework controla el flujo del programa, no tu código.

```java
// Sin IoC: Tú creas las dependencias
public class ProductService {
    private ProductRepository repository = new ProductRepository();  // Acoplamiento fuerte
}

// Con IoC: Spring crea las dependencias
@Service
public class ProductService {
    private final ProductRepository repository;
    
    @Autowired  // Spring inyecta automáticamente
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
}
```

### Contenedor IoC

**Spring Container** es el núcleo de Spring. Gestiona el ciclo de vida de los objetos (beans).

```java
// Configuración con anotaciones
@Configuration
public class AppConfig {
    
    @Bean
    public ProductService productService() {
        return new ProductService();
    }
}

// Spring crea y gestiona automáticamente
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
ProductService service = context.getBean(ProductService.class);
```

### Inyección de Dependencias (DI)

**Tres formas de inyección**:

#### 1. Constructor (RECOMENDADO)

```java
@Service
public class ProductService {
    private final ProductRepository repository;
    
    @Autowired  // Opcional si solo hay un constructor
    public class ProductService(ProductRepository repository) {
        this.repository = repository;
    }
}
```

**Ventajas**:
- ✅ Inmutabilidad (final)
- ✅ Fácil de testear
- ✅ Dependencias obligatorias claras

#### 2. Setter

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

#### 3. Campo (NO RECOMENDADO)

```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;  // Difícil de testear
}
```

### Estereotipos de Spring

```java
// @Component: Componente genérico
@Component
public class EmailSender {
    public void send(String email) { }
}

// @Service: Lógica de negocio
@Service
public class ProductService {
    public Product create(Product product) { }
}

// @Repository: Acceso a datos
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> { }

// @Controller: Controlador MVC (devuelve vistas)
@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";  // Vista
    }
}

// @RestController: Controlador REST (devuelve JSON)
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();  // JSON
    }
}
```

---

## 🚀 Spring Boot

### ¿Qué es Spring Boot?

Spring Boot **simplifica** Spring Framework:
- Configuración automática
- Servidor embebido (Tomcat)
- Starter POMs
- Sin XML

### Aplicación Spring Boot Básica

```java
@SpringBootApplication  // Incluye @Configuration, @EnableAutoConfiguration, @ComponentScan
public class BabyCashApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BabyCashApplication.class, args);
    }
}
```

### Arquitectura en Capas

```
┌─────────────────────────────────────┐
│     Controller (API REST)           │  ← Recibe peticiones HTTP
├─────────────────────────────────────┤
│     Service (Lógica de negocio)     │  ← Procesa lógica
├─────────────────────────────────────┤
│     Repository (Acceso a BD)        │  ← Consulta base de datos
├─────────────────────────────────────┤
│     Model/Entity (Dominio)          │  ← Representa datos
└─────────────────────────────────────┘
```

### Ejemplo Completo: CRUD de Productos

#### 1. Entity (Modelo)

```java
@Entity
@Table(name = "products")
@Data  // Lombok: genera getters, setters, toString, etc.
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    private String description;
    
    @Column(nullable = false)
    private Integer stock = 0;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

#### 2. Repository

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Métodos CRUD básicos ya incluidos:
    // - save(product)
    // - findById(id)
    // - findAll()
    // - deleteById(id)
    
    // Métodos custom (Spring Data genera la consulta automáticamente)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByPriceLessThan(BigDecimal price);
    
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findInStock();
    
    @Query(value = "SELECT * FROM products WHERE price BETWEEN ?1 AND ?2", 
           nativeQuery = true)
    List<Product> findByPriceRange(double min, double max);
}
```

#### 3. Service

```java
@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // Obtener todos
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    // Obtener por ID
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }
    
    // Crear
    public Product create(Product product) {
        // Validaciones
        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio debe ser positivo");
        }
        return productRepository.save(product);
    }
    
    // Actualizar
    public Product update(Long id, Product productDetails) {
        Product product = findById(id);
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setStock(productDetails.getStock());
        return productRepository.save(product);
    }
    
    // Eliminar
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
    
    // Buscar por nombre
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
```

#### 4. Controller

```java
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")  // CORS
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // GET /api/products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }
    
    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    }
    
    // POST /api/products
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        Product created = productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid Product product) {
        Product updated = productService.update(id, product);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    // GET /api/products/search?name=pañal
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String name) {
        List<Product> products = productService.searchByName(name);
        return ResponseEntity.ok(products);
    }
}
```

#### 5. Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
```

### application.properties

```properties
# Configuración del servidor
server.port=8080

# Configuración de base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuración de JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Logging
logging.level.root=INFO
logging.level.com.babycash=DEBUG
logging.file.name=logs/babycash.log
```

---

## 🧪 Testing en Java

### Pirámide de Testing

```
       /\
      /UI\ ← Tests E2E (pocos, lentos)
     /────\
    / API  \ ← Tests de integración (medianos)
   /────────\
  /  UNIT    \ ← Tests unitarios (muchos, rápidos)
 /────────────\
```

### JUnit 5 Básico

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CalculadoraTest {
    
    private Calculadora calc;
    
    @BeforeEach  // Se ejecuta antes de cada test
    void setUp() {
        calc = new Calculadora();
    }
    
    @Test
    void testSuma() {
        int resultado = calc.sumar(2, 3);
        assertEquals(5, resultado);
    }
    
    @Test
    void testDivision() {
        double resultado = calc.dividir(10, 2);
        assertEquals(5.0, resultado, 0.001);
    }
    
    @Test
    void testDivisionPorCero() {
        assertThrows(ArithmeticException.class, () -> {
            calc.dividir(10, 0);
        });
    }
    
    @AfterEach  // Se ejecuta después de cada test
    void tearDown() {
        calc = null;
    }
}
```

### Assertions Comunes

```java
// Igualdad
assertEquals(expected, actual);
assertNotEquals(value1, value2);

// Verdadero/Falso
assertTrue(condition);
assertFalse(condition);

// Nulo
assertNull(object);
assertNotNull(object);

// Mismo objeto
assertSame(expected, actual);
assertNotSame(object1, object2);

// Arrays
assertArrayEquals(expectedArray, actualArray);

// Excepciones
assertThrows(Exception.class, () -> metodoQueFalla());

// Timeout
assertTimeout(Duration.ofSeconds(1), () -> metodoLento());

// Múltiples assertions
assertAll(
    () -> assertEquals(1, actual1),
    () -> assertEquals(2, actual2),
    () -> assertTrue(condition)
);
```

### Mockito (Mocking)

```java
import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;  // Mock del repositorio
    
    @InjectMocks
    private ProductService productService;  // Inyecta los mocks
    
    @Test
    void testFindById() {
        // Arrange (preparar)
        Product mockProduct = new Product(1L, "Pañales", new BigDecimal("29.99"));
        when(productRepository.findById(1L))
            .thenReturn(Optional.of(mockProduct));
        
        // Act (actuar)
        Product result = productService.findById(1L);
        
        // Assert (verificar)
        assertNotNull(result);
        assertEquals("Pañales", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }
    
    @Test
    void testFindByIdNotFound() {
        when(productRepository.findById(999L))
            .thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(999L);
        });
    }
}
```

### Tests de Integración (Spring Boot)

```java
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    void testCreateProduct() throws Exception {
        Product product = new Product(null, "Nuevo Producto", new BigDecimal("19.99"));
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Nuevo Producto"));
    }
}
```

---

## 🧹 Clean Code

### Nombres Significativos

```java
// ❌ MAL
int d;  // días transcurridos
String s;  // nombre
List<int[]> list1;

// ✅ BIEN
int daysSinceCreation;
String customerName;
List<Product> activeProducts;
```

### Funciones Pequeñas

```java
// ❌ MAL: Función hace muchas cosas
public void processOrder(Order order) {
    // Validar orden
    if (order == null) throw new Exception();
    if (order.getItems().isEmpty()) throw new Exception();
    
    // Calcular total
    double total = 0;
    for (Item item : order.getItems()) {
        total += item.getPrice() * item.getQuantity();
    }
    
    // Aplicar descuento
    if (total > 100) {
        total *= 0.9;
    }
    
    // Guardar en BD
    orderRepository.save(order);
    
    // Enviar email
    emailService.send(order.getCustomer().getEmail());
}

// ✅ BIEN: Una función, una responsabilidad
public void processOrder(Order order) {
    validateOrder(order);
    double total = calculateTotal(order);
    total = applyDiscount(total);
    saveOrder(order);
    sendConfirmationEmail(order);
}

private void validateOrder(Order order) {
    if (order == null || order.getItems().isEmpty()) {
        throw new InvalidOrderException();
    }
}

private double calculateTotal(Order order) {
    return order.getItems().stream()
        .mapToDouble(item -> item.getPrice() * item.getQuantity())
        .sum();
}
```

### Comentarios

```java
// ❌ MAL: Comentarios obvios
// Incrementa i
i++;

// Declara variable nombre
String nombre;

// ✅ BIEN: Comentarios que explican el POR QUÉ
// Usamos ThreadLocal para evitar race conditions en ambientes multi-thread
private ThreadLocal<DateFormat> formatter = ThreadLocal.withInitial(() -> 
    new SimpleDateFormat("yyyy-MM-dd")
);

// Workaround: La API de pagos tiene un límite de 5 reintentos
private static final int MAX_RETRIES = 5;
```

### Manejo de Errores

```java
// ❌ MAL
try {
    processPayment();
} catch (Exception e) {
    // Ignorar error
}

// ✅ BIEN
try {
    processPayment();
} catch (PaymentException e) {
    log.error("Error procesando pago: {}", e.getMessage());
    throw new PaymentProcessingException("No se pudo procesar el pago", e);
}
```

### DRY (Don't Repeat Yourself)

```java
// ❌ MAL: Código duplicado
public double calculateDiscountForRegularCustomer(double total) {
    if (total > 100) {
        return total * 0.9;
    }
    return total;
}

public double calculateDiscountForVIPCustomer(double total) {
    if (total > 100) {
        return total * 0.8;
    }
    return total;
}

// ✅ BIEN: Eliminar duplicación
public double calculateDiscount(double total, double discountRate) {
    if (total > 100) {
        return total * (1 - discountRate);
    }
    return total;
}

public double calculateDiscountForRegularCustomer(double total) {
    return calculateDiscount(total, 0.1);  // 10%
}

public double calculateDiscountForVIPCustomer(double total) {
    return calculateDiscount(total, 0.2);  // 20%
}
```

---

## 🎯 Principios SOLID

### S - Single Responsibility (Responsabilidad Única)

**Una clase debe tener una sola razón para cambiar.**

```java
// ❌ MAL: Clase hace muchas cosas
public class User {
    private String name;
    private String email;
    
    public void saveToDatabase() { }
    public void sendEmail() { }
    public void generateReport() { }
}

// ✅ BIEN: Cada clase una responsabilidad
public class User {
    private String name;
    private String email;
    // Solo datos y comportamiento relacionado con User
}

public class UserRepository {
    public void save(User user) { }  // Solo persistencia
}

public class EmailService {
    public void send(String email) { }  // Solo emails
}

public class ReportGenerator {
    public void generate(User user) { }  // Solo reportes
}
```

### O - Open/Closed (Abierto/Cerrado)

**Abierto para extensión, cerrado para modificación.**

```java
// ❌ MAL: Modificar la clase para agregar nuevos tipos
public class DiscountCalculator {
    public double calculate(String customerType, double amount) {
        if (customerType.equals("REGULAR")) {
            return amount * 0.9;
        } else if (customerType.equals("VIP")) {
            return amount * 0.8;
        } else if (customerType.equals("PREMIUM")) {  // Modificando código existente
            return amount * 0.7;
        }
        return amount;
    }
}

// ✅ BIEN: Extensión sin modificación
public interface DiscountStrategy {
    double applyDiscount(double amount);
}

public class RegularDiscount implements DiscountStrategy {
    public double applyDiscount(double amount) {
        return amount * 0.9;
    }
}

public class VIPDiscount implements DiscountStrategy {
    public double applyDiscount(double amount) {
        return amount * 0.8;
    }
}

// Agregar nuevo tipo sin modificar código existente
public class PremiumDiscount implements DiscountStrategy {
    public double applyDiscount(double amount) {
        return amount * 0.7;
    }
}

public class DiscountCalculator {
    private DiscountStrategy strategy;
    
    public double calculate(double amount) {
        return strategy.applyDiscount(amount);
    }
}
```

### L - Liskov Substitution (Sustitución de Liskov)

**Los objetos de una subclase deben poder reemplazar objetos de la superclase.**

```java
// ❌ MAL: Square rompe el contrato de Rectangle
public class Rectangle {
    protected int width;
    protected int height;
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // Rompe el comportamiento esperado
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height;
    }
}

// Test que falla
Rectangle rect = new Square();
rect.setWidth(5);
rect.setHeight(4);
assertEquals(20, rect.getArea());  // Falla: devuelve 16

// ✅ BIEN: Usar interfaces apropiadas
public interface Shape {
    double getArea();
}

public class Rectangle implements Shape {
    private int width;
    private int height;
    
    public double getArea() {
        return width * height;
    }
}

public class Square implements Shape {
    private int side;
    
    public double getArea() {
        return side * side;
    }
}
```

### I - Interface Segregation (Segregación de Interfaces)

**Muchas interfaces específicas son mejores que una interfaz general.**

```java
// ❌ MAL: Interface muy grande
public interface Worker {
    void work();
    void eat();
    void sleep();
    void getPaid();
}

public class Robot implements Worker {
    public void work() { }
    public void eat() { }  // ❌ Los robots no comen
    public void sleep() { }  // ❌ Los robots no duermen
    public void getPaid() { }  // ❌ Los robots no cobran
}

// ✅ BIEN: Interfaces segregadas
public interface Workable {
    void work();
}

public interface Eatable {
    void eat();
}

public interface Sleepable {
    void sleep();
}

public interface Payable {
    void getPaid();
}

public class Human implements Workable, Eatable, Sleepable, Payable {
    public void work() { }
    public void eat() { }
    public void sleep() { }
    public void getPaid() { }
}

public class Robot implements Workable {
    public void work() { }  // Solo implementa lo que necesita
}
```

### D - Dependency Inversion (Inversión de Dependencias)

**Depender de abstracciones, no de implementaciones concretas.**

```java
// ❌ MAL: Depende de implementación concreta
public class ProductService {
    private MySQLProductRepository repository = new MySQLProductRepository();  // Acoplamiento
    
    public Product findById(Long id) {
        return repository.findById(id);
    }
}

// ✅ BIEN: Depende de abstracción
public interface ProductRepository {
    Product findById(Long id);
    List<Product> findAll();
    Product save(Product product);
}

public class MySQLProductRepository implements ProductRepository {
    public Product findById(Long id) { }
    public List<Product> findAll() { }
    public Product save(Product product) { }
}

public class ProductService {
    private final ProductRepository repository;  // Abstracción
    
    public ProductService(ProductRepository repository) {
        this.repository = repository;  // Inyección de dependencia
    }
    
    public Product findById(Long id) {
        return repository.findById(id);
    }
}
```

---

## 🎨 Patrones de Diseño

### Singleton

**Una sola instancia de la clase en toda la aplicación.**

```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() { }  // Constructor privado
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}

// Spring: Singleton por defecto
@Component
public class MyService {
    // Spring gestiona la instancia única
}
```

### Factory

**Crear objetos sin especificar la clase exacta.**

```java
public interface Payment {
    void processPayment(double amount);
}

public class CreditCardPayment implements Payment {
    public void processPayment(double amount) { }
}

public class PayPalPayment implements Payment {
    public void processPayment(double amount) { }
}

// Factory
public class PaymentFactory {
    public static Payment createPayment(String type) {
        switch (type) {
            case "CREDIT_CARD":
                return new CreditCardPayment();
            case "PAYPAL":
                return new PayPalPayment();
            default:
                throw new IllegalArgumentException("Tipo inválido");
        }
    }
}

// Uso
Payment payment = PaymentFactory.createPayment("CREDIT_CARD");
payment.processPayment(100.0);
```

### Strategy

**Seleccionar algoritmo en tiempo de ejecución.**

```java
public interface ShippingStrategy {
    double calculateCost(double weight);
}

public class StandardShipping implements ShippingStrategy {
    public double calculateCost(double weight) {
        return weight * 5.0;
    }
}

public class ExpressShipping implements ShippingStrategy {
    public double calculateCost(double weight) {
        return weight * 10.0;
    }
}

public class Order {
    private ShippingStrategy shippingStrategy;
    
    public void setShippingStrategy(ShippingStrategy strategy) {
        this.shippingStrategy = strategy;
    }
    
    public double calculateShippingCost(double weight) {
        return shippingStrategy.calculateCost(weight);
    }
}
```

### Observer

**Notificar a múltiples objetos cuando cambia el estado.**

```java
public interface Observer {
    void update(String message);
}

public class Customer implements Observer {
    private String name;
    
    public void update(String message) {
        System.out.println(name + " recibió: " + message);
    }
}

public class ProductStock {
    private List<Observer> observers = new ArrayList<>();
    private int stock;
    
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    public void setStock(int stock) {
        this.stock = stock;
        if (stock > 0) {
            notifyObservers("Producto disponible");
        }
    }
    
    private void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
```

---

## ✅ Resumen y Mejores Prácticas

### Checklist de Código Limpio

- [ ] Nombres descriptivos
- [ ] Funciones pequeñas (< 20 líneas)
- [ ] Una responsabilidad por clase/método
- [ ] Sin código duplicado (DRY)
- [ ] Comentarios solo cuando necesario
- [ ] Tests para todo el código
- [ ] Manejo apropiado de errores
- [ ] Sin "magic numbers" (usar constantes)
- [ ] Formato consistente
- [ ] Principios SOLID aplicados

### Mejores Prácticas Spring Boot

- ✅ Usar inyección por constructor
- ✅ Validar inputs con `@Valid`
- ✅ Usar DTOs para APIs
- ✅ Manejar excepciones con `@RestControllerAdvice`
- ✅ Configurar CORS apropiadamente
- ✅ Usar `Optional` para evitar `null`
- ✅ Logs estructurados
- ✅ Tests de integración
- ✅ Documentar API (Swagger/OpenAPI)
- ✅ Usar profiles (dev, prod)

---

**Documento creado**: 4 de Noviembre de 2025  
**Propósito**: Fundamentos completos de Java y Spring Boot  
**Proyecto**: Baby Cash - SENA
