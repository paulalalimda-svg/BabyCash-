# PATRÓN BUILDER

## 🎯 Definición

**Builder** permite construir objetos complejos **paso a paso**, separando la construcción de la representación.

Es como armar un LEGO: agregas piezas una a una hasta tener el objeto completo.

---

## ❓ ¿Para Qué Sirve?

### Problema: Constructor con Muchos Parámetros

```java
❌ MAL (Constructor Telescópico):
public class User {
    public User(
        String name,
        String email,
        String password,
        String phone,
        String address,
        String city,
        String zipCode,
        String country,
        LocalDate birthDate,
        Boolean newsletterEnabled
    ) {
        // 10 parámetros = confuso
    }
}

// Uso
User user = new User(
    "John",
    "john@example.com",
    "password123",
    "555-1234",
    "123 Main St",
    "New York",
    "10001",
    "USA",
    LocalDate.of(1990, 1, 1),
    true
);
// ❌ ¿Cuál parámetro es cuál?
```

---

## ✅ CON Builder

```java
// ✅ BIEN (Builder):
public class User {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String city;
    private String zipCode;
    private String country;
    private LocalDate birthDate;
    private Boolean newsletterEnabled;
    
    // Constructor privado
    private User() { }
    
    // ✅ Builder class
    public static class Builder {
        private User user = new User();
        
        public Builder name(String name) {
            user.name = name;
            return this;  // ✅ Retorna this para encadenar
        }
        
        public Builder email(String email) {
            user.email = email;
            return this;
        }
        
        public Builder password(String password) {
            user.password = password;
            return this;
        }
        
        public Builder phone(String phone) {
            user.phone = phone;
            return this;
        }
        
        public Builder address(String address) {
            user.address = address;
            return this;
        }
        
        public Builder city(String city) {
            user.city = city;
            return this;
        }
        
        public Builder zipCode(String zipCode) {
            user.zipCode = zipCode;
            return this;
        }
        
        public Builder country(String country) {
            user.country = country;
            return this;
        }
        
        public Builder birthDate(LocalDate birthDate) {
            user.birthDate = birthDate;
            return this;
        }
        
        public Builder newsletterEnabled(Boolean newsletterEnabled) {
            user.newsletterEnabled = newsletterEnabled;
            return this;
        }
        
        public User build() {
            // Validaciones aquí
            if (user.email == null) {
                throw new IllegalStateException("Email is required");
            }
            return user;
        }
    }
}

// ✅ Uso (claro y legible)
User user = new User.Builder()
    .name("John")
    .email("john@example.com")
    .password("password123")
    .phone("555-1234")
    .address("123 Main St")
    .city("New York")
    .zipCode("10001")
    .country("USA")
    .birthDate(LocalDate.of(1990, 1, 1))
    .newsletterEnabled(true)
    .build();
```

**Ventajas:**
- ✅ Legible
- ✅ Parámetros opcionales
- ✅ Validación centralizada en `build()`

---

## 🏗️ Builder con Lombok

Lombok simplifica Builder:

```java
@Builder
@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stock;
    private Boolean enabled;
    private String imageUrl;
    private Category category;
}

// ✅ Uso (Lombok genera Builder automáticamente)
Product product = Product.builder()
    .name("Baby Bottle")
    .description("BPA-free bottle")
    .price(new BigDecimal("15.99"))
    .discountPrice(new BigDecimal("12.99"))
    .stock(100)
    .enabled(true)
    .imageUrl("/images/bottle.jpg")
    .build();
```

---

## 🏗️ Builder en Baby Cash

### ✅ Ejemplo: Crear Producto

```java
// Sin Builder (tedioso)
Product product = new Product();
product.setName("Baby Bottle");
product.setDescription("BPA-free bottle");
product.setPrice(new BigDecimal("15.99"));
product.setDiscountPrice(new BigDecimal("12.99"));
product.setStock(100);
product.setEnabled(true);

// ✅ Con Builder (limpio)
Product product = Product.builder()
    .name("Baby Bottle")
    .description("BPA-free bottle")
    .price(new BigDecimal("15.99"))
    .discountPrice(new BigDecimal("12.99"))
    .stock(100)
    .enabled(true)
    .build();
```

---

### ✅ Ejemplo: Tests

```java
@Test
public void shouldCreateProduct() {
    // ✅ Builder es perfecto para tests
    Product product = Product.builder()
        .name("Test Product")
        .price(new BigDecimal("10.00"))
        .stock(50)
        .enabled(true)
        .build();
    
    assertNotNull(product);
    assertEquals("Test Product", product.getName());
}
```

---

## 📊 Cuándo Usar Builder

### ✅ Casos de Uso

```
✅ Objetos con muchos atributos (más de 4-5)
✅ Muchos parámetros opcionales
✅ Construcción paso a paso
✅ Objetos inmutables (campos final)
✅ Tests (crear objetos de prueba)
```

---

### ❌ Cuándo NO Usar

```
❌ Objetos simples con 2-3 atributos
❌ Todos los parámetros son obligatorios
❌ No hay lógica de validación
```

---

## 🎯 Builder vs Constructor

### Constructor Simple

```java
// ✅ OK para objetos simples
public class Point {
    private int x;
    private int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

Point point = new Point(10, 20);  // ✅ Claro
```

---

### Builder para Objetos Complejos

```java
// ✅ Builder para objetos complejos
@Builder
public class Order {
    private Long id;
    private User user;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private LocalDateTime createdAt;
}

Order order = Order.builder()
    .user(user)
    .items(items)
    .totalAmount(total)
    .shippingAddress("123 Main St")
    .billingAddress("123 Main St")
    .paymentMethod(PaymentMethod.CREDIT_CARD)
    .status(OrderStatus.PENDING)
    .createdAt(LocalDateTime.now())
    .build();
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es el patrón Builder?"**

> "Es un patrón creacional que permite construir objetos complejos paso a paso. En lugar de un constructor con 10 parámetros, usas métodos encadenados para establecer cada atributo. Es especialmente útil cuando tienes muchos parámetros opcionales."

---

**2. "¿Dónde usas Builder en Baby Cash?"**

> "Uso Lombok `@Builder` en:
> - Entities como `Product`, `Order`
> - DTOs como `ProductResponse`, `OrderResponse`
> - Tests para crear objetos de prueba
> 
> Ejemplo: `Product.builder().name('Bottle').price(15.99).build()`"

---

**3. "¿Cuál es la ventaja de Builder sobre constructores?"**

> "Legibilidad y flexibilidad:
> - **Constructor**: `new User('John', 'john@mail.com', '123', '555-1234', null, null, null, null, null, true)` → confuso
> - **Builder**: `User.builder().name('John').email('john@mail.com').password('123').phone('555-1234').newsletterEnabled(true).build()` → claro
> 
> Además, Builder permite parámetros opcionales sin sobrecarga de constructores."

---

## 📝 Checklist de Builder

```
✅ Constructor privado (solo Builder puede crear)
✅ Métodos que retornan this (encadenamiento)
✅ Método build() que valida y retorna objeto
✅ Campos finales para inmutabilidad (opcional)
✅ Lombok @Builder para simplificar
```

---

## 🏆 Ventajas y Desventajas

### ✅ Ventajas

```
✅ Legible (nombres claros para cada atributo)
✅ Flexible (parámetros opcionales)
✅ Validación centralizada en build()
✅ Inmutabilidad (campos final)
✅ Perfecto para tests
```

---

### ❌ Desventajas

```
❌ Más código (sin Lombok)
❌ Overkill para objetos simples
```

---

## 🚀 Conclusión

**Builder:**
- ✅ Construye objetos complejos paso a paso
- ✅ Legible y flexible
- ✅ Lombok simplifica con `@Builder`

**En Baby Cash, usamos `@Builder` extensivamente.**

---

**Ahora lee:** `PATRON-STRATEGY.md` para el siguiente patrón. 🚀
