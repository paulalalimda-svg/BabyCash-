# FORMATEO DE CÓDIGO

## 🎯 Regla de Oro

**El código debe verse limpio, ordenado y consistente.**

Como un libro bien diseñado, el código debe ser fácil de leer.

---

## ❓ ¿Por Qué Importa el Formateo?

### Imagina leer esto:

```
❌ MAL (sin formateo):
Esteesunejemplodeuntextosinformateodondetodaslaspalabrasjuntashacenquesesumaméntedifícildeleeryveryentenderelcontenidodeloquesesteintentacomunicar.

✅ BIEN (con formateo):
Este es un ejemplo de un texto con formateo,
donde las palabras están separadas,
haciendo que sea sumamente fácil de leer
y entender el contenido de lo que se intenta comunicar.
```

**Lo mismo aplica al código.**

---

## 📐 Reglas de Formateo

### 1️⃣ Indentación Consistente

#### ❌ MAL

```java
public class Product {
private Long id;
private String name;
public Long getId() {
return id;
}
public void setId(Long id) {
this.id = id;
}}
```

#### ✅ BIEN

```java
public class Product {
    private Long id;
    private String name;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
}
```

**Regla:** 4 espacios por nivel de indentación (o 2, pero consistente).

---

### 2️⃣ Líneas en Blanco para Separar Conceptos

#### ❌ MAL (todo junto)

```java
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    public OrderResponse createOrder(CreateOrderRequest request) {
        validateOrder(request);
        Order order = buildOrder(request);
        orderRepository.save(order);
        emailService.sendConfirmation(order);
        return mapToResponse(order);
    }
    private void validateOrder(CreateOrderRequest request) {
        if (request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have items");
        }
    }
    private Order buildOrder(CreateOrderRequest request) {
        return new Order(request);
    }
}
```

#### ✅ BIEN (conceptos separados)

```java
public class OrderService {
    
    // Dependencias
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    
    // Métodos públicos
    public OrderResponse createOrder(CreateOrderRequest request) {
        validateOrder(request);
        Order order = buildOrder(request);
        orderRepository.save(order);
        emailService.sendConfirmation(order);
        return mapToResponse(order);
    }
    
    // Métodos privados
    private void validateOrder(CreateOrderRequest request) {
        if (request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have items");
        }
    }
    
    private Order buildOrder(CreateOrderRequest request) {
        return new Order(request);
    }
}
```

---

### 3️⃣ Máximo 120 Caracteres por Línea

#### ❌ MAL (línea muy larga)

```java
public OrderResponse createOrder(Long userId, String address, String city, String zipCode, List<OrderItemRequest> items, String paymentMethod, String couponCode) {
    // ...
}
```

#### ✅ BIEN (líneas cortas)

```java
public OrderResponse createOrder(
    Long userId,
    String address,
    String city,
    String zipCode,
    List<OrderItemRequest> items,
    String paymentMethod,
    String couponCode
) {
    // ...
}
```

---

### 4️⃣ Orden de Elementos en una Clase

#### ✅ Orden Estándar

```java
public class ProductService {
    
    // 1. Constantes
    private static final int MAX_PRODUCTS = 100;
    
    // 2. Variables de instancia (fields)
    private final ProductRepository productRepository;
    private final EmailService emailService;
    
    // 3. Constructores
    public ProductService(ProductRepository productRepository, EmailService emailService) {
        this.productRepository = productRepository;
        this.emailService = emailService;
    }
    
    // 4. Métodos públicos
    public List<ProductResponse> getAllProducts() {
        // ...
    }
    
    public ProductResponse getProductById(Long id) {
        // ...
    }
    
    // 5. Métodos privados
    private Product findProductOrThrow(Long id) {
        // ...
    }
    
    private ProductResponse mapToResponse(Product product) {
        // ...
    }
}
```

---

### 5️⃣ Espacios Alrededor de Operadores

#### ❌ MAL

```java
int total=basePrice+tax-discount;
boolean isValid=age>=18&&hasLicense;
```

#### ✅ BIEN

```java
int total = basePrice + tax - discount;
boolean isValid = age >= 18 && hasLicense;
```

---

### 6️⃣ Llaves en la Misma Línea (Estilo Java)

#### ❌ MAL (estilo C#)

```java
public void createOrder(Order order)
{
    if (order == null)
    {
        throw new IllegalArgumentException();
    }
}
```

#### ✅ BIEN (estilo Java)

```java
public void createOrder(Order order) {
    if (order == null) {
        throw new IllegalArgumentException();
    }
}
```

---

## 🏗️ Baby Cash y Formateo

### ✅ Ejemplo: ProductController

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    // ✅ Línea en blanco después de declaraciones
    private final ProductService productService;
    
    // ✅ Métodos separados por líneas en blanco
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
        @RequestBody CreateProductRequest request
    ) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.created(URI.create("/api/products/" + product.getId()))
            .body(product);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable Long id,
        @RequestBody UpdateProductRequest request
    ) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
```

**Observa:**
- ✅ Indentación consistente (4 espacios)
- ✅ Líneas en blanco entre métodos
- ✅ Parámetros en múltiples líneas cuando son largos
- ✅ Espacios alrededor de operadores

---

### ✅ Ejemplo: ProductService

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    
    // ✅ Dependencias agrupadas
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    // ✅ Métodos públicos primero
    public List<ProductResponse> getAllActiveProducts() {
        List<Product> products = productRepository.findByEnabled(true);
        return products.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return mapToResponse(product);
    }
    
    public ProductResponse createProduct(CreateProductRequest request) {
        validateProductRequest(request);
        Product product = buildProduct(request);
        product = productRepository.save(product);
        return mapToResponse(product);
    }
    
    // ✅ Métodos privados al final
    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
    
    private void validateProductRequest(CreateProductRequest request) {
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
    }
    
    private Product buildProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setEnabled(true);
        return product;
    }
    
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        return response;
    }
}
```

---

## 📊 Reglas de Formateo en Baby Cash

### ✅ Java Backend

```java
// Indentación: 4 espacios
public class Example {
    private String field;
    
    public void method() {
        if (condition) {
            // código
        }
    }
}

// Llaves: misma línea
public void method() {
    // código
}

// Línea máxima: 120 caracteres
public void methodWithManyParameters(
    String param1,
    String param2,
    String param3
) {
    // código
}
```

---

### ✅ TypeScript/React Frontend

```typescript
// Indentación: 2 espacios
export const ProductCard: React.FC<ProductCardProps> = ({ product }) => {
  const [isHovered, setIsHovered] = useState(false);
  
  return (
    <div className="product-card">
      <h3>{product.name}</h3>
      <p>{product.price}</p>
    </div>
  );
};
```

---

## 🛠️ Herramientas de Formateo

### ✅ Java

**IntelliJ IDEA:**
- `Ctrl + Alt + L` (Windows/Linux)
- `Cmd + Option + L` (Mac)

**VS Code:**
- `Shift + Alt + F` (Windows/Linux)
- `Shift + Option + F` (Mac)

**Configuración (`.editorconfig`):**

```
[*.java]
indent_style = space
indent_size = 4
max_line_length = 120
```

---

### ✅ TypeScript/JavaScript

**Prettier:**

```json
{
  "semi": true,
  "singleQuote": false,
  "tabWidth": 2,
  "printWidth": 100
}
```

---

## 📝 Checklist de Formateo

```
✅ Indentación consistente (4 espacios en Java, 2 en TS)
✅ Líneas en blanco para separar conceptos
✅ Máximo 120 caracteres por línea
✅ Orden: constantes → fields → constructores → públicos → privados
✅ Espacios alrededor de operadores (=, +, -, &&, ||)
✅ Llaves en la misma línea (estilo Java)
✅ Sin líneas en blanco innecesarias (máximo 1)
✅ Formateo automático antes de commit
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Por qué el formateo es importante?"**

> "Porque el código se lee muchas más veces de las que se escribe. Un código bien formateado es fácil de leer, entender y mantener. Es como escribir con buena ortografía y puntuación."

---

**2. "¿Cómo garantizas formateo consistente?"**

> "Uso herramientas automáticas:
> - IntelliJ IDEA para formatear Java automáticamente
> - Prettier para formatear TypeScript/React
> - `.editorconfig` para configuración compartida
> - Pre-commit hooks para validar formateo antes de commit"

---

**3. "¿Tu código sigue estándares de formateo?"**

> "Sí:
> - ✅ Java: 4 espacios de indentación, llaves en misma línea
> - ✅ TypeScript: 2 espacios, Prettier con configuración estándar
> - ✅ Máximo 120 caracteres por línea
> - ✅ Orden consistente: constantes → fields → métodos públicos → privados
> - ✅ Líneas en blanco para separar conceptos
> - ✅ Espacios alrededor de operadores"

---

**4. "¿Qué pasa si cada desarrollador formatea diferente?"**

> "Se crea inconsistencia y dificulta la revisión de código. Por eso usamos herramientas automáticas y configuración compartida (`.editorconfig`, Prettier). Todos formatean igual con un solo comando."

---

## 🏆 Beneficios del Buen Formateo

### 1. **Legibilidad**

Código ordenado es fácil de leer.

---

### 2. **Consistencia**

Todo el proyecto se ve igual.

---

### 3. **Menos Errores**

Código organizado reduce confusiones.

---

### 4. **Profesionalismo**

Empresas valoran código bien formateado.

---

## 📈 Antes y Después

### ❌ ANTES (mal formateado)

```java
@Service
public class OrderService{
private final OrderRepository orderRepository;private final ProductRepository productRepository;
public OrderResponse createOrder(CreateOrderRequest request){
List<Product>products=new ArrayList<>();for(OrderItemRequest item:request.getItems()){Product product=productRepository.findById(item.getProductId()).orElseThrow();products.add(product);}
Order order=new Order();order.setProducts(products);orderRepository.save(order);return new OrderResponse(order);}}
```

---

### ✅ DESPUÉS (bien formateado)

```java
@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    
    public OrderResponse createOrder(CreateOrderRequest request) {
        List<Product> products = new ArrayList<>();
        
        for (OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            products.add(product);
        }
        
        Order order = new Order();
        order.setProducts(products);
        orderRepository.save(order);
        
        return new OrderResponse(order);
    }
}
```

---

## 🚀 Conclusión

**El formateo es fundamental para código profesional.**

Código bien formateado:
- ✅ Es fácil de leer
- ✅ Es consistente
- ✅ Reduce errores
- ✅ Demuestra profesionalismo

**Baby Cash usa formateo automático en todo el proyecto.**

---

**Ahora lee:** `MANEJO-ERRORES-LIMPIO.md` para el siguiente principio. 🚀
