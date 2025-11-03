# 🎤 SCRIPT DE PRESENTACIÓN - EVALUACIÓN SENA

## 🎯 Guía Completa para la Presentación

Este documento te guía **paso a paso** sobre qué decir y hacer durante la evaluación del SENA.

---

## 📋 Antes de la Presentación

### Checklist Pre-Evaluación

```
✅ Laptop cargada
✅ Proyecto funcionando (frontend + backend)
✅ PostgreSQL corriendo
✅ Base de datos con datos de prueba
✅ Navegador con tabs preparadas:
   - http://localhost:5173 (frontend)
   - Documentación abierta
✅ IDE abierto (VS Code o IntelliJ)
✅ Agua/café preparado
✅ Documentación impresa (opcional)
✅ Respirar profundo 😊
```

---

## 🎬 Estructura de la Presentación (15-20 minutos)

### Parte 1: Introducción del Proyecto (3 minutos)

**Qué decir:**

> "Buenos días/tardes. Mi nombre es [TU NOMBRE] y voy a presentar el proyecto **Baby Cash**, una tienda online de productos para bebés.
> 
> El proyecto está construido con:
> - **Frontend**: React con TypeScript y Tailwind CSS
> - **Backend**: Spring Boot con Java
> - **Base de Datos**: PostgreSQL
> - **Arquitectura**: REST API con autenticación JWT
> 
> Voy a mostrarles cómo el proyecto aplica principios SOLID, Clean Code y múltiples Design Patterns."

**Qué mostrar:**
- Pantalla principal del frontend funcionando
- Catálogo de productos
- Carrito de compras

---

### Parte 2: Demostración Funcional (5 minutos)

**Qué hacer:**

#### 1️⃣ Usuario Normal

```
1. Mostrar página principal
2. Click en un producto → Ver detalle
3. Agregar al carrito
4. Ver carrito
5. Proceder al checkout
```

**Qué decir:**

> "Como usuario, puedo navegar por el catálogo, ver detalles de productos, agregar al carrito y realizar pedidos. La aplicación valida datos, gestiona sesiones con JWT y proporciona feedback visual."

---

#### 2️⃣ Usuario Administrador

```
1. Login como admin
2. Ir a panel de administración
3. Crear un producto
4. Editar un producto
5. Ver órdenes
```

**Qué decir:**

> "Los administradores tienen un panel donde pueden gestionar productos, categorías y órdenes. El sistema valida permisos usando Spring Security."

---

### Parte 3: SOLID Principles (4 minutos)

**Qué mostrar:**
Abrir IDE con código de `ProductService.java`

**Qué decir:**

> "El proyecto aplica los 5 principios SOLID:
> 
> **S - Single Responsibility**: Cada clase tiene una responsabilidad.
> Por ejemplo, `ProductController` solo maneja requests HTTP.
> `ProductService` solo tiene lógica de negocio.
> `ProductRepository` solo accede a la base de datos."

```java
// Mostrar en pantalla
@RestController  // ✅ Solo maneja HTTP
public class ProductController { ... }

@Service  // ✅ Solo lógica de negocio
public class ProductService { ... }

@Repository  // ✅ Solo acceso a datos
public interface ProductRepository { ... }
```

---

> "**O - Open/Closed**: El código está abierto a extensión pero cerrado a modificación.
> Por ejemplo, puedo agregar nuevas estrategias de descuento sin cambiar `OrderService`."

```java
// Mostrar Strategy pattern
public interface DiscountStrategy {
    BigDecimal calculateDiscount(BigDecimal amount);
}

public class PercentageDiscountStrategy implements DiscountStrategy { ... }
public class FixedAmountDiscountStrategy implements DiscountStrategy { ... }
// ✅ Puedo agregar más sin cambiar OrderService
```

---

> "**D - Dependency Inversion**: Las clases dependen de abstracciones (interfaces), no implementaciones concretas."

```java
// Mostrar Dependency Injection
@Service
public class OrderService {
    // ✅ Depende de interfaz, no implementación
    private final OrderRepository orderRepository;
    private final EmailSender emailSender;  // Interfaz
}
```

---

### Parte 4: Clean Code (3 minutos)

**Qué mostrar:**
Código de `ProductService.java` o `OrderService.java`

**Qué decir:**

> "El proyecto sigue prácticas de Clean Code:
> 
> **1. Nombres significativos**: Los métodos tienen nombres claros que explican qué hacen."

```java
// ✅ BIEN: Nombre descriptivo
public ProductResponse getProductById(Long id) { ... }

// ❌ MAL: Nombre confuso
public ProductResponse get(Long x) { ... }
```

---

> "**2. Funciones pequeñas**: Cada método hace una sola cosa y es fácil de entender."

```java
public ProductResponse createProduct(CreateProductRequest request) {
    Category category = getCategoryById(request.getCategoryId());
    Product product = mapToEntity(request);
    product.setCategory(category);
    Product savedProduct = productRepository.save(product);
    return mapToResponse(savedProduct);
}
// ✅ Máximo 20 líneas, fácil de leer
```

---

> "**3. DRY (Don't Repeat Yourself)**: No repito código, uso métodos reutilizables."

```java
// ✅ Método reutilizable
private ProductResponse mapToResponse(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        // ...
        .build();
}

// Usado en múltiples lugares
public ProductResponse getProductById(Long id) {
    Product product = findProductOrThrow(id);
    return mapToResponse(product);  // ✅ Reutiliza
}
```

---

### Parte 5: Design Patterns (5 minutos)

**Qué decir:**

> "El proyecto usa 12 Design Patterns. Voy a mostrar los más importantes:"

---

#### Pattern 1: MVC (Arquitectura)

```java
// Mostrar flujo completo
@RestController  // CONTROLLER: Recibe HTTP request
public class ProductController {
    private final ProductService productService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}

@Service  // MODEL: Lógica de negocio
public class ProductService {
    private final ProductRepository productRepository;
    
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)...;
        return mapToResponse(product);
    }
}

@Repository  // MODEL: Acceso a datos
public interface ProductRepository extends JpaRepository<Product, Long> { }
```

**Qué decir:**

> "Uso MVC para separar responsabilidades:
> - Controller recibe requests y devuelve responses
> - Service contiene lógica de negocio
> - Repository accede a la base de datos
> 
> Esta separación facilita mantenimiento y testing."

---

#### Pattern 2: Repository

```java
// Mostrar Repository
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByEnabled(Boolean enabled);
    Optional<Product> findBySlug(String slug);
}
```

**Qué decir:**

> "Repository abstrae el acceso a datos. El servicio NO conoce SQL ni detalles de la base de datos. Solo usa métodos como `findById()` o `save()`. Esto desacopla la lógica de negocio de la persistencia."

---

#### Pattern 3: DTO (Data Transfer Object)

```java
// Mostrar DTO
public class CreateProductRequest {  // DTO de entrada
    private String name;
    private BigDecimal price;
    private Long categoryId;
}

public class ProductResponse {  // DTO de salida
    private Long id;
    private String name;
    private BigDecimal price;
    // ✅ NO incluye datos sensibles
}
```

**Qué decir:**

> "Uso DTOs para transferir datos entre frontend y backend. Esto permite:
> - Controlar qué datos se exponen (no envío contraseñas)
> - Validar datos de entrada con Bean Validation
> - Desacoplar API de la estructura de la base de datos"

---

#### Pattern 4: Dependency Injection

```java
// Mostrar DI
@Service
@RequiredArgsConstructor  // Lombok genera constructor
public class OrderService {
    // ✅ Dependencies inyectadas por Spring
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmailSender emailSender;
    
    // Spring inyecta automáticamente al crear el bean
}
```

**Qué decir:**

> "Uso Dependency Injection con Spring. Las dependencias se inyectan automáticamente, NO creo objetos con `new`. Esto facilita testing porque puedo inyectar mocks fácilmente."

---

#### Pattern 5: Strategy

```java
// Mostrar Strategy
public interface DiscountStrategy {
    BigDecimal calculateDiscount(BigDecimal amount);
}

public class PercentageDiscountStrategy implements DiscountStrategy {
    public BigDecimal calculateDiscount(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(0.10));
    }
}

public class FixedAmountDiscountStrategy implements DiscountStrategy {
    public BigDecimal calculateDiscount(BigDecimal amount) {
        return BigDecimal.valueOf(10);
    }
}
```

**Qué decir:**

> "Strategy permite tener algoritmos intercambiables. En lugar de if-else gigantes, tengo estrategias de descuento independientes. Esto cumple Open/Closed: puedo agregar nuevas estrategias sin modificar código existente."

---

#### Pattern 6: Observer

```java
// Mostrar Observer
@Service
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;
    
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = // ... crear orden
        orderRepository.save(order);
        
        // ✅ Publica evento
        eventPublisher.publishEvent(new OrderCreatedEvent(order));
        
        return mapToResponse(order);
    }
}

// ✅ Listener escucha evento
@Component
public class EmailNotificationListener {
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Enviar email de confirmación
    }
}
```

**Qué decir:**

> "Observer desacopla componentes. Cuando se crea una orden, `OrderService` publica un evento. Múltiples listeners reaccionan (enviar email, actualizar puntos de lealtad) sin que `OrderService` los conozca. Esto facilita agregar funcionalidad sin modificar código existente."

---

### Parte 6: Testing (Opcional, 2 minutos)

**Si hay tiempo, mostrar tests:**

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    void getProductById_ShouldReturnProduct() {
        // Given
        Product product = Product.builder()
            .id(1L)
            .name("Baby Bottle")
            .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        // When
        ProductResponse response = productService.getProductById(1L);
        
        // Then
        assertNotNull(response);
        assertEquals("Baby Bottle", response.getName());
    }
}
```

**Qué decir:**

> "Gracias a Dependency Injection y patrones como Repository, el código es fácil de testear. Uso Mockito para crear mocks de repositories y puedo testear la lógica de negocio de forma aislada."

---

## ❓ Preguntas Frecuentes y Respuestas

### "¿Por qué elegiste estas tecnologías?"

> "Elegí React para frontend porque es el framework más popular y tiene gran ecosistema. TypeScript agrega type safety que previene errores. Para backend, Spring Boot es el estándar de Java enterprise, con excelente soporte para REST APIs, seguridad y acceso a datos. PostgreSQL es una base de datos robusta y open source."

---

### "¿Cómo gestionas la seguridad?"

> "Uso Spring Security con JWT. Cuando un usuario hace login, el backend genera un token JWT. El frontend envía este token en cada request con header `Authorization: Bearer <token>`. El backend valida el token y extrae el usuario. Las contraseñas se hashean con BCrypt antes de guardarse."

---

### "¿Cómo manejas errores?"

> "Tengo un `@RestControllerAdvice` que captura todas las excepciones y devuelve responses consistentes. Por ejemplo, si un producto no existe, lanzo `ResourceNotFoundException` y el handler devuelve `404 Not Found` con mensaje claro."

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
}
```

---

### "¿Cómo aseguras la calidad del código?"

> "Aplico principios SOLID para mantener código limpio y desacoplado. Uso Clean Code para nombres descriptivos y funciones pequeñas. Implemento Design Patterns para resolver problemas comunes de forma profesional. Además, uso Lombok para reducir boilerplate y mantengo documentación completa."

---

### "¿Qué fue lo más difícil del proyecto?"

> "Lo más desafiante fue implementar la gestión de órdenes con múltiples items y cálculo de totales, asegurando transaccionalidad. Usé `@Transactional` para que, si algo falla, toda la operación se revierta. También fue complejo implementar autenticación JWT y gestión de roles."

---

### "¿Cómo escalaría el proyecto?"

> "Para escalar:
> - **Caching**: Usar Redis para cachear productos frecuentes
> - **Paginación**: Implementar en listados grandes
> - **Async**: Usar `@Async` en operaciones lentas (email)
> - **Microservicios**: Separar en servicios independientes (productos, órdenes, usuarios)
> - **Load Balancing**: Múltiples instancias del backend
> - **CDN**: Para imágenes y assets estáticos"

---

### "¿Tienes tests?"

> "Sí, tengo tests unitarios para services usando Mockito. Mockeo repositories y teseo la lógica de negocio de forma aislada. También tengo tests de integración para repositories que validan queries contra base de datos en memoria (H2)."

---

## 🎯 Consejos Finales

### ✅ DO (Haz esto):

```
✅ Habla claro y pausado
✅ Muestra confianza (conoces tu código)
✅ Usa términos técnicos correctos
✅ Explica POR QUÉ hiciste cada cosa
✅ Conecta teoría con práctica (muestra código)
✅ Respira y toma agua si necesitas
✅ Admite si no sabes algo y ofrece investigar
```

---

### ❌ DON'T (NO hagas esto):

```
❌ Leer documentación durante presentación
❌ Disculparte por código ("esto está mal hecho")
❌ Inventar respuestas
❌ Hablar demasiado rápido
❌ Pasar rápido por slides
❌ Decir "no sé nada"
❌ Complicar explicaciones innecesariamente
```

---

## 📝 Checklist Durante Presentación

```
✅ Introducción del proyecto
✅ Demo funcional (usuario + admin)
✅ Explicación de SOLID
✅ Ejemplos de Clean Code
✅ Mostrar Design Patterns (MVC, Repository, DTO, DI, Strategy, Observer)
✅ Responder preguntas con seguridad
✅ Agradecer al final
```

---

## 🏆 Cierre de Presentación

**Qué decir:**

> "En resumen, Baby Cash es un proyecto profesional que aplica:
> - **SOLID**: Para código mantenible y flexible
> - **Clean Code**: Para código legible y comprensible
> - **Design Patterns**: Para resolver problemas comunes de forma profesional
> 
> El resultado es una aplicación escalable, testeable y fácil de mantener.
> 
> Muchas gracias por su atención. ¿Tienen alguna pregunta?"

---

## 💡 Último Consejo

**Recuerda:**
- Has trabajado duro en este proyecto
- Has aprendido mucho
- Tienes documentación completa que respalda tu conocimiento
- El proyecto funciona y está bien estructurado

**¡Confía en ti mismo! 💪**

---

## 📞 Preparación Final

### Día Antes

```
✅ Revisar documentación (SOLID, Clean Code, Patterns)
✅ Practicar presentación en voz alta
✅ Probar proyecto (frontend + backend)
✅ Cargar laptop
✅ Preparar agua
✅ Dormir bien
```

---

### 1 Hora Antes

```
✅ Llegar temprano
✅ Configurar laptop
✅ Levantar frontend y backend
✅ Abrir tabs necesarias
✅ Respirar profundo
✅ Repasar puntos clave
```

---

## 🎉 ¡Buena Suerte!

Has preparado un proyecto excelente con documentación completa. Confía en tu trabajo y demuestra lo que has aprendido.

**¡Vas a hacerlo increíble! 🚀**

---

**Fecha:** 31 de octubre de 2025
**Versión:** 1.0 (Fase 1 - Mínimo Indispensable)
**Estado:** Listo para evaluación SENA
