# NOMBRES SIGNIFICATIVOS

## 🎯 Regla de Oro

**El nombre debe revelar la intención.**

Si necesitas un comentario para explicar qué hace una variable, el nombre está mal.

---

## ❓ ¿Por Qué Importan Los Nombres?

### Piensa en esto:

```java
❌ MAL:
int d = 7;
```

**¿Qué es `d`?**
- ¿Días?
- ¿Distancia?
- ¿Descuento?
- ¿Dinero?

```java
✅ BIEN:
int daysSinceLastLogin = 7;
```

**Ahora es OBVIO** qué representa.

---

## 📚 Tipos de Nombres

### 1️⃣ Variables

#### ❌ Nombres Malos

```java
int d;  // ¿d de qué?
String s;  // ¿s de qué?
boolean f;  // ¿f de qué?
List<Product> list;  // ¿lista de qué?
```

#### ✅ Nombres Buenos

```java
int daysSinceCreation;
String customerEmail;
boolean isProductAvailable;
List<Product> activeProducts;
```

---

### 2️⃣ Constantes

#### ❌ Nombres Malos

```java
final int MAX = 100;  // ¿Máximo de qué?
final String URL = "https://api.com";  // ¿URL de qué?
```

#### ✅ Nombres Buenos

```java
final int MAX_LOGIN_ATTEMPTS = 3;
final String API_BASE_URL = "https://api.com";
final BigDecimal MINIMUM_ORDER_AMOUNT = new BigDecimal("10.00");
```

**Convención en Java:**
- Constantes: `MAYUSCULAS_CON_GUIONES`
- Variables: `camelCase`

---

### 3️⃣ Métodos

#### ❌ Nombres Malos

```java
void proc();  // ¿Procesar qué?
int calc(int x);  // ¿Calcular qué?
String get();  // ¿Obtener qué?
void do();  // ¿Hacer qué?
```

#### ✅ Nombres Buenos

```java
void processOrder();
int calculateTotalPrice(int basePrice);
String getUserEmail();
void sendConfirmationEmail();
```

**Convención:**
- Métodos que hacen algo: verbos (`createOrder`, `sendEmail`)
- Métodos que devuelven booleanos: `is`, `has`, `can` (`isAvailable`, `hasStock`)

---

### 4️⃣ Clases

#### ❌ Nombres Malos

```java
class Manager;  // ¿Manager de qué?
class Data;  // ¿Data de qué?
class Helper;  // ¿Helper de qué?
class Utils;  // ¿Utils de qué?
```

#### ✅ Nombres Buenos

```java
class OrderManager;
class UserData;
class EmailHelper;
class StringUtils;
```

**Convención:**
- Clases: sustantivos (`Product`, `Order`, `User`)
- Servicios: sustantivo + `Service` (`ProductService`, `OrderService`)
- Controladores: sustantivo + `Controller` (`ProductController`)

---

## 🏗️ Nombres en Baby Cash

### ✅ Entities (Entidades)

```java
// ✅ Nombres claros y descriptivos
@Entity
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stock;
    private Boolean enabled;
    private String imageUrl;
}
```

**Observa:**
- `price` (no `p`)
- `discountPrice` (no `dp`)
- `enabled` (no `e` o `flag`)
- `imageUrl` (no `img` o `url`)

---

### ✅ Services (Servicios)

```java
@Service
public class ProductService {
    
    // ✅ Métodos con nombres descriptivos
    public List<ProductResponse> getAllActiveProducts() { }
    
    public ProductResponse getProductById(Long id) { }
    
    public ProductResponse createProduct(CreateProductRequest request) { }
    
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) { }
    
    public void deleteProduct(Long id) { }
}
```

---

### ✅ Controllers (Controladores)

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    // ✅ Endpoints descriptivos
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() { }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) { }
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) { }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable Long id,
        @RequestBody UpdateProductRequest request
    ) { }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) { }
}
```

---

### ✅ DTOs (Data Transfer Objects)

```java
// ✅ Nombres que indican propósito
public class CreateOrderRequest { }
public class UpdateOrderRequest { }
public class OrderResponse { }

public class CreateProductRequest { }
public class UpdateProductRequest { }
public class ProductResponse { }
```

**Patrón:**
- Request de creación: `Create{Entidad}Request`
- Request de actualización: `Update{Entidad}Request`
- Response: `{Entidad}Response`

---

## 🚫 Nombres a Evitar

### 1️⃣ Abreviaturas

```java
❌ MAL:
String usr;
int qty;
BigDecimal amt;
boolean flg;

✅ BIEN:
String username;
int quantity;
BigDecimal amount;
boolean isEnabled;
```

---

### 2️⃣ Números en Nombres

```java
❌ MAL:
String email1;
String email2;
String email3;

✅ BIEN:
String primaryEmail;
String secondaryEmail;
String recoveryEmail;
```

---

### 3️⃣ Nombres Genéricos

```java
❌ MAL:
String data;
int value;
Object obj;
List<Product> list;

✅ BIEN:
String customerData;
int totalPrice;
Product product;
List<Product> activeProducts;
```

---

### 4️⃣ Nombres con Prefijos Húngaros

```java
❌ MAL (Notación Húngara):
String strEmail;
int intAge;
boolean bIsActive;

✅ BIEN:
String email;
int age;
boolean isActive;
```

**Nota:** La notación húngara era común en C/C++, pero en Java es innecesaria.

---

## ✅ Convenciones en Baby Cash

### Variables Booleanas

```java
// ✅ Usar is, has, can, should
private Boolean isAvailable;
private Boolean hasStock;
private Boolean canBeRefunded;
private Boolean shouldSendEmail;

// ❌ Evitar
private Boolean available;  // No es claro que es boolean
private Boolean stock;  // Confuso
```

---

### Listas y Colecciones

```java
// ✅ Usar plural
List<Product> products;
Set<String> emails;
Map<Long, User> usersById;

// ❌ Evitar
List<Product> productList;  // Redundante
Set<String> emailSet;  // Redundante
```

---

### IDs

```java
// ✅ Usar sufijo Id
private Long userId;
private Long productId;
private Long orderId;

// ❌ Evitar
private Long user;  // Confuso
private Long product;  // Confuso
```

---

## 🎨 Nombres Consistentes

### Ejemplo: Operaciones CRUD

```java
// ✅ Baby Cash usa el mismo patrón en todos los servicios

// ProductService
public ProductResponse createProduct(CreateProductRequest request) { }
public ProductResponse getProductById(Long id) { }
public ProductResponse updateProduct(Long id, UpdateProductRequest request) { }
public void deleteProduct(Long id) { }

// OrderService
public OrderResponse createOrder(CreateOrderRequest request) { }
public OrderResponse getOrderById(Long id) { }
public OrderResponse updateOrder(Long id, UpdateOrderRequest request) { }
public void deleteOrder(Long id) { }

// UserService
public UserResponse createUser(CreateUserRequest request) { }
public UserResponse getUserById(Long id) { }
public UserResponse updateUser(Long id, UpdateUserRequest request) { }
public void deleteUser(Long id) { }
```

**Beneficio:** Si sabes cómo funciona `ProductService`, sabes cómo funciona `OrderService`.

---

## 📏 Longitud de Nombres

### Variables Locales

```java
// ✅ BIEN: Variables de bucle cortas
for (int i = 0; i < products.size(); i++) {
    Product product = products.get(i);
}

// ✅ BIEN: Variables descriptivas
int totalPriceWithDiscount = calculatePrice(basePrice, discountPercentage);
```

**Regla:**
- Variables de bucle: 1 letra (`i`, `j`, `k`) está bien
- Variables de negocio: descriptivas

---

### Parámetros de Métodos

```java
// ✅ BIEN: Nombres descriptivos
public OrderResponse createOrder(Long userId, List<OrderItemRequest> items) {
    // ...
}

// ❌ MAL: Nombres cortos
public OrderResponse createOrder(Long u, List<OrderItemRequest> i) {
    // Confuso
}
```

---

## 🧪 Nombres en Tests

```java
@Test
public void shouldReturnProductWhenIdExists() {
    // ✅ Nombre del test describe qué hace
}

@Test
public void shouldThrowExceptionWhenProductNotFound() {
    // ✅ Nombre describe el comportamiento esperado
}

// ❌ MAL
@Test
public void test1() {
    // ¿Qué prueba?
}
```

**Convención:**
- `should{AcciónEsperada}When{Condición}`

---

## 📊 Comparación

### Ejemplo Real: Baby Cash OrderService

#### ❌ Si tuviera malos nombres:

```java
@Service
public class OS {
    private final OR or;
    private final PR pr;
    private final UR ur;
    
    public ORsp c(CORq r) {
        U u = ur.f(r.getU());
        List<OI> is = new ArrayList<>();
        for (OIRq ir : r.getIs()) {
            P p = pr.f(ir.getP()).orElseThrow();
            is.add(new OI(p, ir.getQ()));
        }
        O o = new O(u, is);
        or.s(o);
        return new ORsp(o);
    }
}
```

**Imposible de entender.** 🤯

---

#### ✅ Con buenos nombres (real en Baby Cash):

```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            items.add(new OrderItem(product, itemRequest.getQuantity()));
        }
        
        Order order = new Order(user, items);
        orderRepository.save(order);
        return new OrderResponse(order);
    }
}
```

**Completamente claro.** ✅

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Por qué usas nombres tan largos?"**

> "Porque el código se lee muchas más veces de las que se escribe. `calculateTotalPriceWithDiscount` es más largo que `calc`, pero es 100% claro qué hace. En 6 meses, cuando vuelva a leer el código, lo entenderé inmediatamente."

---

**2. "¿Cómo decides los nombres?"**

> "Sigo estas reglas:
> - ✅ Variables: sustantivos descriptivos (`userEmail`, `totalPrice`)
> - ✅ Métodos: verbos que describen la acción (`createOrder`, `sendEmail`)
> - ✅ Booleanos: `is`, `has`, `can` (`isAvailable`, `hasStock`)
> - ✅ Clases: sustantivos (`Product`, `OrderService`)
> - ✅ Constantes: MAYÚSCULAS_CON_GUIONES (`MAX_LOGIN_ATTEMPTS`)"

---

**3. "¿Tu código sigue convenciones de nombres?"**

> "Sí, en todo Baby Cash:
> - ✅ Entities: `Product`, `Order`, `User`
> - ✅ Services: `ProductService`, `OrderService`
> - ✅ Controllers: `ProductController`, `OrderController`
> - ✅ DTOs: `CreateProductRequest`, `ProductResponse`
> - ✅ Repositories: `ProductRepository`, `OrderRepository`
> 
> Todo sigue el mismo patrón consistentemente."

---

**4. "¿Qué haces si no encuentras un buen nombre?"**

> "Eso significa que la clase o método hace demasiadas cosas. Lo divido en partes más pequeñas, cada una con una responsabilidad clara. Ahí los nombres se vuelven obvios."

---

## 📝 Checklist de Nombres

Antes de entregar código, verifica:

```
✅ Variables tienen nombres descriptivos (no abreviaturas)
✅ Métodos usan verbos (createOrder, sendEmail)
✅ Booleanos usan is/has/can (isAvailable, hasStock)
✅ Clases son sustantivos (Product, OrderService)
✅ Constantes en MAYÚSCULAS_CON_GUIONES
✅ Nombres consistentes en todo el proyecto
✅ Sin notación húngara (strEmail ❌)
✅ Sin números en nombres (email1, email2 ❌)
```

---

## 🏆 Beneficios de Buenos Nombres

### 1. **Código Autoexplicativo**

No necesitas comentarios si los nombres son claros.

```java
// ✅ No necesita comentario
BigDecimal totalPriceWithDiscount = calculateTotalPriceWithDiscount(basePrice, discountPercentage);

// ❌ Necesita comentario
BigDecimal t = calc(p, d);  // Calcular total con descuento
```

---

### 2. **Menos Bugs**

Nombres claros → menos confusiones → menos errores.

---

### 3. **Onboarding Rápido**

Nuevos desarrolladores entienden el código rápidamente.

---

### 4. **Código Profesional**

Empresas valoran código con nombres bien pensados.

---

## 📈 Niveles de Nombres

### Nivel 1: Código Ilegible 🔴

```java
int d = 7;
String s = "test";
boolean f = true;
```

---

### Nivel 2: Código Aceptable 🟡

```java
int days = 7;
String str = "test";
boolean flag = true;
```

---

### Nivel 3: Código Profesional 🟢

```java
int daysSinceLastLogin = 7;
String customerEmail = "test@example.com";
boolean isProductAvailable = true;
```

---

## 🚀 Conclusión

**Los nombres son el 70% del código limpio.**

Un buen nombre:
- ✅ Revela la intención
- ✅ Es descriptivo
- ✅ Es consistente
- ✅ No necesita comentarios

**Baby Cash usa nombres significativos en todo el proyecto.**

---

**Ahora lee:** `FUNCIONES-METODOS-LIMPIOS.md` para el siguiente principio. 🚀
