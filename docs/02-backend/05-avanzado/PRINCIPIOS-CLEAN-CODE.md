# PRINCIPIOS DE CLEAN CODE (CÓDIGO LIMPIO)

## ¿Qué es Clean Code?

**Código Limpio** es código que:
- ✅ Se lee fácilmente
- ✅ Se entiende rápidamente
- ✅ Se puede modificar sin miedo a romper todo
- ✅ Está bien organizado
- ✅ No tiene "trucos" raros

---

## 📚 Origen: Uncle Bob

**Robert C. Martin** (Uncle Bob) escribió el libro "Clean Code" en 2008.

Es el **manual de buenas prácticas** más importante del mundo de desarrollo de software.

---

## ❓ ¿Por Qué Importa?

### 🤔 Imagina esto:

Estás haciendo un proyecto de cocina. Encuentras una receta que dice:

```
❌ MAL:
Agarra la cosa y mézcala con lo otro.
Ponle un poco de eso y cocina hasta que esté.
```

```
✅ BIEN:
1. Mezcla 2 tazas de harina con 1 taza de azúcar.
2. Agrega 3 huevos y bate por 5 minutos.
3. Hornea a 180°C durante 30 minutos.
```

---

**Lo mismo pasa con el código:**

```java
❌ CÓDIGO SUCIO:
public void p(String x) {
    int y = x.length();
    if (y > 10) {
        System.out.println("Error");
    }
}
```

```java
✅ CÓDIGO LIMPIO:
public void validateUsername(String username) {
    int usernameLength = username.length();
    int maxLength = 10;
    
    if (usernameLength > maxLength) {
        System.out.println("Username too long");
    }
}
```

---

## 🎯 Principios Fundamentales

### 1️⃣ **Nombres Significativos**

El nombre debe decir **qué es** y **para qué sirve**.

```java
❌ MAL:
int d;  // ¿d de qué?
String s;  // ¿s de qué?
boolean f;  // ¿f de qué?

✅ BIEN:
int daysSinceCreation;
String customerEmail;
boolean isProductAvailable;
```

---

### 2️⃣ **Funciones Pequeñas**

Una función debe hacer **UNA SOLA COSA** y hacerla bien.

```java
❌ MAL (hace muchas cosas):
public void processOrder(Order order) {
    // Valida
    if (order.getTotal() < 0) throw new Exception();
    
    // Calcula descuento
    BigDecimal discount = order.getTotal().multiply(new BigDecimal("0.1"));
    
    // Actualiza stock
    for (OrderItem item : order.getItems()) {
        Product p = productRepository.findById(item.getProductId());
        p.setStock(p.getStock() - item.getQuantity());
        productRepository.save(p);
    }
    
    // Envía email
    emailService.send(order.getUserEmail(), "Order confirmed");
    
    // Guarda en DB
    orderRepository.save(order);
}

✅ BIEN (una cosa por función):
public void processOrder(Order order) {
    validateOrder(order);
    applyDiscount(order);
    updateStock(order);
    sendConfirmationEmail(order);
    saveOrder(order);
}
```

---

### 3️⃣ **Comentarios Solo Cuando Sea Necesario**

El código debe ser **autoexplicativo**. Los comentarios son para casos especiales.

```java
❌ MAL (comentario innecesario):
// Suma dos números
public int add(int a, int b) {
    return a + b;  // Devuelve la suma
}

✅ BIEN (código autoexplicativo):
public int calculateTotalPrice(int basePrice, int taxAmount) {
    return basePrice + taxAmount;
}

✅ BIEN (comentario necesario):
// Workaround: API externa devuelve null en lugar de lista vacía
// TODO: Reportar bug al proveedor
public List<Product> getProducts() {
    List<Product> products = apiClient.fetchProducts();
    return products != null ? products : new ArrayList<>();
}
```

---

### 4️⃣ **Formateo Consistente**

El código debe verse ordenado y seguir el mismo estilo en todo el proyecto.

```java
❌ MAL (desordenado):
public class Product{
private Long id;private String name;
    private BigDecimal price;
public Product(){
}
        public Long getId(){return id;}
public void setId(Long id){this.id=id;}}

✅ BIEN (ordenado):
public class Product {
    
    private Long id;
    private String name;
    private BigDecimal price;
    
    public Product() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
}
```

---

### 5️⃣ **Manejo de Errores Limpio**

No uses códigos de error. Usa excepciones descriptivas.

```java
❌ MAL:
public int createUser(User user) {
    if (user.getEmail() == null) {
        return -1;  // ¿Qué significa -1?
    }
    if (userRepository.existsByEmail(user.getEmail())) {
        return -2;  // ¿Y -2?
    }
    userRepository.save(user);
    return 1;  // ¿Y 1?
}

✅ BIEN:
public void createUser(User user) {
    if (user.getEmail() == null) {
        throw new IllegalArgumentException("Email is required");
    }
    if (userRepository.existsByEmail(user.getEmail())) {
        throw new DuplicateEmailException("Email already exists");
    }
    userRepository.save(user);
}
```

---

### 6️⃣ **DRY - Don't Repeat Yourself**

No repitas código. Si algo se repite, extráelo a una función.

```java
❌ MAL (código repetido):
public void sendWelcomeEmail(String email) {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(email);
    helper.setSubject("Welcome!");
    helper.setText("Welcome to Baby Cash");
    mailSender.send(message);
}

public void sendOrderConfirmationEmail(String email) {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(email);
    helper.setSubject("Order Confirmed");
    helper.setText("Your order is confirmed");
    mailSender.send(message);
}

✅ BIEN (sin repetición):
public void sendWelcomeEmail(String email) {
    sendEmail(email, "Welcome!", "Welcome to Baby Cash");
}

public void sendOrderConfirmationEmail(String email) {
    sendEmail(email, "Order Confirmed", "Your order is confirmed");
}

private void sendEmail(String to, String subject, String body) {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(body);
    mailSender.send(message);
}
```

---

### 7️⃣ **Clases Cohesivas**

Una clase debe tener métodos y atributos relacionados entre sí.

```java
❌ MAL (baja cohesión):
public class User {
    private String name;
    private String email;
    
    // ❌ ¿Por qué User envía emails?
    public void sendEmail() { }
    
    // ❌ ¿Por qué User calcula impuestos?
    public BigDecimal calculateTax() { }
}

✅ BIEN (alta cohesión):
public class User {
    private String name;
    private String email;
    
    // ✅ Todo relacionado con datos del usuario
    public String getName() { }
    public void setName(String name) { }
    public String getEmail() { }
    public void setEmail(String email) { }
}

public class EmailService {
    public void sendEmail(User user, String message) { }
}

public class TaxCalculator {
    public BigDecimal calculateTax(BigDecimal amount) { }
}
```

---

## 🏗️ Baby Cash y Clean Code

### ✅ Nombres Significativos

```java
// Baby Cash usa nombres descriptivos
ProductService productService;
OrderRepository orderRepository;
UserResponse userResponse;
CreateOrderRequest createOrderRequest;
```

---

### ✅ Funciones Pequeñas

```java
// OrderService hace coordinación, delega a funciones pequeñas
@Service
public class OrderService {
    
    public OrderResponse createOrder(CreateOrderRequest request) {
        validateOrder(request);  // ✅ Función pequeña
        List<OrderItem> items = buildOrderItems(request);  // ✅ Función pequeña
        Order order = saveOrder(items);  // ✅ Función pequeña
        sendConfirmationEmail(order);  // ✅ Función pequeña
        return mapToResponse(order);  // ✅ Función pequeña
    }
}
```

---

### ✅ Manejo de Errores con Excepciones

```java
// Baby Cash usa excepciones descriptivas
public Product getProductById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
}
```

---

### ✅ DRY con Servicios Reutilizables

```java
// EmailService se reutiliza en todo el proyecto
@Service
public class EmailService {
    public void sendWelcomeEmail(String email) { }
    public void sendOrderConfirmationEmail(Order order) { }
    public void sendPasswordResetEmail(String email) { }
}
```

---

## 📊 Reglas de Código Limpio

### ✅ Variables

| ❌ MAL | ✅ BIEN |
|--------|---------|
| `int d` | `int daysSinceCreation` |
| `String s` | `String customerEmail` |
| `boolean f` | `boolean isProductAvailable` |

---

### ✅ Funciones

| ❌ MAL | ✅ BIEN |
|--------|---------|
| `void proc()` | `void processOrder()` |
| `int calc(int x)` | `int calculateTotalPrice(int basePrice)` |
| `String get()` | `String getUserEmail()` |

---

### ✅ Clases

| ❌ MAL | ✅ BIEN |
|--------|---------|
| `class Manager` | `class OrderManager` |
| `class Helper` | `class EmailHelper` |
| `class Data` | `class UserData` |

---

## 🚀 Beneficios de Clean Code

### 1. **Fácil de Leer**

Tu compañero puede entender el código en minutos, no en horas.

---

### 2. **Fácil de Mantener**

Cuando necesitas cambiar algo, sabes exactamente dónde buscar.

---

### 3. **Menos Bugs**

Código claro = menos errores.

---

### 4. **Trabajo en Equipo**

Todos entienden el código, no solo tú.

---

### 5. **Código Profesional**

Las empresas valoran código limpio y bien organizado.

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es Clean Code?"**

> "Es código que se lee fácilmente, se entiende rápido y se puede modificar sin romper todo. Usa nombres descriptivos, funciones pequeñas y está bien organizado."

---

**2. "¿Por qué usas nombres tan largos?"**

> "Porque el código se lee muchas más veces de las que se escribe. Es mejor escribir `calculateTotalPriceWithDiscount` que `calc` y luego no entender qué hace."

---

**3. "¿Tu código sigue principios de Clean Code?"**

> "Sí:
> - ✅ Nombres descriptivos (`ProductService`, `createOrder`)
> - ✅ Funciones pequeñas (cada método hace una cosa)
> - ✅ Sin código repetido (servicios reutilizables)
> - ✅ Excepciones claras (`ResourceNotFoundException`)
> - ✅ Código formateado consistentemente"

---

**4. "¿Cómo garantizas que tu código sea mantenible?"**

> "Aplicando Clean Code: código autoexplicativo, funciones con una responsabilidad, nombres que dicen qué hacen, y sin repeticiones. Esto hace que cualquier desarrollador pueda entender y modificar el código."

---

## 📝 Checklist de Código Limpio

Antes de entregar código, verifica:

```
✅ Nombres descriptivos (no abreviaturas raras)
✅ Funciones pequeñas (máximo 20 líneas)
✅ Comentarios solo cuando sean necesarios
✅ Código formateado consistentemente
✅ Excepciones descriptivas (no códigos de error)
✅ Sin código repetido (DRY)
✅ Clases cohesivas (métodos relacionados)
```

---

## 📈 Niveles de Código

### Nivel 1: Código Sucio 🔴

```java
public void p(String x) {
    if (x != null && x.length() > 0) {
        System.out.println("OK");
    }
}
```

---

### Nivel 2: Código Normal 🟡

```java
public void processUsername(String username) {
    if (username != null && username.length() > 0) {
        System.out.println("Username is valid");
    }
}
```

---

### Nivel 3: Código Limpio 🟢

```java
public void validateUsername(String username) {
    if (isValidUsername(username)) {
        System.out.println("Username is valid");
    }
}

private boolean isValidUsername(String username) {
    return username != null && !username.isEmpty();
}
```

---

## 🏆 Conclusión

**Clean Code NO es opcional**, es **fundamental** para:

- ✅ Trabajo en equipo
- ✅ Mantenibilidad
- ✅ Escalabilidad
- ✅ Profesionalismo

**Baby Cash aplica Clean Code en toda su arquitectura.**

---

**Ahora lee:** `NOMBRES-SIGNIFICATIVOS.md` para profundizar en el primer principio. 🚀
