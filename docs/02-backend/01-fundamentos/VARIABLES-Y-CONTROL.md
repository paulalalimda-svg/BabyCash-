# 📦 VARIABLES Y TIPOS DE DATOS EN JAVA

## 🎯 ¿Qué es una Variable?

**Explicación Simple:**
Una variable es como una **caja con etiqueta** donde guardas información.

```
┌─────────────────┐
│  nombre: "María" │  ← Variable llamada "nombre" que guarda el texto "María"
└─────────────────┘

┌─────────────┐
│  edad: 25   │  ← Variable llamada "edad" que guarda el número 25
└─────────────┘
```

**Explicación Técnica:**
Una variable es un **espacio en memoria** con un nombre que almacena un valor de un tipo específico.

---

## 📊 Tipos de Datos en Java

### 1. Tipos Primitivos (tipos básicos)

| Tipo | Qué Guarda | Tamaño | Ejemplo |
|------|------------|--------|---------|
| `int` | Números enteros | 4 bytes | `25`, `-100`, `0` |
| `long` | Números enteros grandes | 8 bytes | `123456789L` |
| `double` | Números decimales | 8 bytes | `19.99`, `3.14159` |
| `float` | Números decimales (menos precisión) | 4 bytes | `19.99f` |
| `boolean` | Verdadero/Falso | 1 bit | `true`, `false` |
| `char` | Un solo carácter | 2 bytes | `'A'`, `'5'`, `'@'` |

### 2. Tipos de Referencia (objetos)

| Tipo | Qué Guarda | Ejemplo |
|------|------------|---------|
| `String` | Texto (cadena de caracteres) | `"Hola Mundo"` |
| `LocalDateTime` | Fecha y hora | `2025-10-30T19:30:00` |
| `BigDecimal` | Números decimales precisos (para dinero) | `new BigDecimal("45000.50")` |

---

## ✍️ Declarar Variables

### Sintaxis

```java
tipo nombreVariable = valor;
```

### Ejemplos

```java
// Números enteros
int edad = 25;
int stock = 100;
int descuento = -10;

// Números decimales
double precio = 45000.50;
double impuesto = 0.19;

// Texto
String nombre = "María García";
String email = "maria@gmail.com";

// Verdadero/Falso
boolean activo = true;
boolean disponible = false;

// Un carácter
char inicial = 'M';
char signo = '@';
```

---

## 🔄 Tipos de Datos en el Proyecto

### Ejemplo Real: User.java

```java
public class User {
    // Identificador único (número grande)
    private Long id;
    
    // Textos
    private String name;
    private String email;
    private String password;
    
    // Fecha y hora
    private LocalDateTime createdAt;
    
    // Verdadero/Falso
    private boolean active;
    
    // Enum (valor de una lista fija)
    private UserRole role; // USER o ADMIN
}
```

### Ejemplo Real: Product.java

```java
public class Product {
    private Long id;
    
    // Textos
    private String name;
    private String description;
    private String imageUrl;
    
    // Precio (BigDecimal para dinero, NO double)
    private BigDecimal price;
    
    // Número entero
    private int stock;
    
    // Verdadero/Falso
    private boolean available;
    
    // Fecha
    private LocalDateTime createdAt;
}
```

**¿Por qué BigDecimal para precio y NO double?**

```java
// PROBLEMA con double:
double precio1 = 0.1;
double precio2 = 0.2;
double suma = precio1 + precio2;
System.out.println(suma); // Imprime: 0.30000000000000004 ❌

// SOLUCIÓN con BigDecimal:
BigDecimal precio1 = new BigDecimal("0.1");
BigDecimal precio2 = new BigDecimal("0.2");
BigDecimal suma = precio1.add(precio2);
System.out.println(suma); // Imprime: 0.3 ✅
```

**Regla:** Para dinero, SIEMPRE usa `BigDecimal`.

---

## 🎨 Nomenclatura de Variables

### camelCase (Java usa esto)

```java
// ✅ Correcto
String nombreCompleto = "María García";
int numeroDePedido = 12345;
boolean estaActivo = true;

// ❌ Incorrecto (no uses snake_case en Java)
String nombre_completo = "María García";
int numero_de_pedido = 12345;
```

**Regla:** 
- Primera palabra en minúscula
- Siguientes palabras con mayúscula inicial
- Sin espacios ni guiones

---

## 🔄 Operadores

### 1. Operadores Aritméticos

```java
int a = 10;
int b = 3;

int suma = a + b;        // 13
int resta = a - b;       // 7
int multiplicacion = a * b; // 30
int division = a / b;    // 3 (división entera)
int modulo = a % b;      // 1 (residuo)
```

**Ejemplo en el Proyecto:**

```java
// CartService.java - Calcular subtotal
public BigDecimal calculateSubtotal(CartItem item) {
    BigDecimal price = item.getProduct().getPrice();
    int quantity = item.getQuantity();
    
    return price.multiply(BigDecimal.valueOf(quantity));
}
```

### 2. Operadores de Comparación

```java
int edad = 25;

boolean esMayor = edad > 18;      // true
boolean esMenor = edad < 18;      // false
boolean esIgual = edad == 25;     // true
boolean esDiferente = edad != 30; // true
boolean mayorOIgual = edad >= 25; // true
boolean menorOIgual = edad <= 20; // false
```

**Ejemplo en el Proyecto:**

```java
// ProductService.java - Verificar stock
if (product.getStock() > 0) {
    product.setAvailable(true);
} else {
    product.setAvailable(false);
}
```

### 3. Operadores Lógicos

```java
boolean esAdulto = edad >= 18;
boolean tieneLicencia = true;

// AND (&&) - Ambos deben ser true
boolean puedeConducir = esAdulto && tieneLicencia; // true

// OR (||) - Al menos uno debe ser true
boolean puedeEntrar = esAdulto || tienePermiso; // true si cualquiera es true

// NOT (!) - Invierte el valor
boolean esNiño = !esAdulto; // false (porque esAdulto es true)
```

**Ejemplo en el Proyecto:**

```java
// AuthService.java - Validar login
if (user != null && passwordEncoder.matches(password, user.getPassword())) {
    // Login exitoso
}
```

### 4. Operador Ternario (if corto)

```java
// Sintaxis: condicion ? valorSiTrue : valorSiFalse

int edad = 20;
String mensaje = edad >= 18 ? "Adulto" : "Menor de edad";
// mensaje = "Adulto"
```

**Ejemplo en el Proyecto:**

```java
// ProductService.java
product.setAvailable(product.getStock() > 0 ? true : false);
```

---

## 🧮 Estructuras de Control

### 1. if / else if / else

```java
int stock = 5;

if (stock > 10) {
    System.out.println("Stock disponible");
} else if (stock > 0) {
    System.out.println("Pocas unidades");
} else {
    System.out.println("Agotado");
}
```

**Ejemplo en el Proyecto:**

```java
// OrderService.java
if (cart.getItems().isEmpty()) {
    throw new BadRequestException("El carrito está vacío");
}

if (product.getStock() < quantity) {
    throw new BadRequestException("Stock insuficiente");
}
```

### 2. switch

```java
String role = "ADMIN";

switch (role) {
    case "ADMIN":
        System.out.println("Acceso total");
        break;
    case "USER":
        System.out.println("Acceso limitado");
        break;
    default:
        System.out.println("Sin acceso");
}
```

**Ejemplo en el Proyecto:**

```java
// PaymentService.java
switch (payment.getStatus()) {
    case PENDING:
        // Procesar pago
        break;
    case COMPLETED:
        // Enviar confirmación
        break;
    case FAILED:
        // Reembolsar
        break;
}
```

### 3. for (ciclo con contador)

```java
// Imprimir números del 1 al 5
for (int i = 1; i <= 5; i++) {
    System.out.println(i);
}
```

**Ejemplo en el Proyecto:**

```java
// OrderService.java - Crear items de la orden
List<OrderItem> orderItems = new ArrayList<>();

for (CartItem cartItem : cart.getItems()) {
    OrderItem orderItem = new OrderItem();
    orderItem.setProduct(cartItem.getProduct());
    orderItem.setQuantity(cartItem.getQuantity());
    orderItem.setPrice(cartItem.getProduct().getPrice());
    
    orderItems.add(orderItem);
}
```

### 4. while

```java
int intentos = 0;

while (intentos < 3) {
    System.out.println("Intento " + intentos);
    intentos++;
}
```

### 5. for-each (iterar colecciones)

```java
List<String> nombres = List.of("María", "Juan", "Ana");

for (String nombre : nombres) {
    System.out.println(nombre);
}
```

**Ejemplo en el Proyecto:**

```java
// CartService.java - Calcular total del carrito
BigDecimal total = BigDecimal.ZERO;

for (CartItem item : cart.getItems()) {
    BigDecimal subtotal = item.getProduct().getPrice()
        .multiply(BigDecimal.valueOf(item.getQuantity()));
    total = total.add(subtotal);
}

return total;
```

---

## 🎯 Conversión de Tipos (Casting)

### Conversión Automática (implícita)

```java
int numero = 10;
double decimal = numero; // 10.0 (automático)
```

### Conversión Manual (explícita)

```java
double decimal = 10.99;
int numero = (int) decimal; // 10 (pierde los decimales)
```

### String a Number

```java
String textoNumero = "123";
int numero = Integer.parseInt(textoNumero); // 123

String textoDecimal = "45.99";
double decimal = Double.parseDouble(textoDecimal); // 45.99
```

### Number a String

```java
int numero = 123;
String texto = String.valueOf(numero); // "123"

// O más simple:
String texto2 = numero + ""; // "123"
```

---

## 📋 Constantes (final)

```java
// Variable normal (se puede cambiar)
int edad = 25;
edad = 26; // ✅ OK

// Constante (NO se puede cambiar)
final int EDAD_MINIMA = 18;
EDAD_MINIMA = 20; // ❌ ERROR
```

**Convención:** Constantes en MAYÚSCULAS con guiones bajos.

**Ejemplo en el Proyecto:**

```java
// JwtUtil.java
private static final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos
```

---

## 🎓 Ejemplo Completo en el Proyecto

### Calcular Total de un Carrito

```java
public BigDecimal calculateCartTotal(Cart cart) {
    // 1. Variable para acumular el total
    BigDecimal total = BigDecimal.ZERO;
    
    // 2. Verificar que el carrito no esté vacío
    if (cart.getItems().isEmpty()) {
        return total; // Retorna 0
    }
    
    // 3. Iterar cada item del carrito
    for (CartItem item : cart.getItems()) {
        // 4. Obtener precio y cantidad
        BigDecimal price = item.getProduct().getPrice();
        int quantity = item.getQuantity();
        
        // 5. Calcular subtotal (precio × cantidad)
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
        
        // 6. Sumar al total
        total = total.add(subtotal);
    }
    
    // 7. Aplicar descuento si existe
    if (cart.getDiscountPercent() > 0) {
        BigDecimal discount = total.multiply(
            BigDecimal.valueOf(cart.getDiscountPercent() / 100.0)
        );
        total = total.subtract(discount);
    }
    
    // 8. Retornar total
    return total;
}
```

**Conceptos usados:**
- ✅ Variables (`total`, `price`, `quantity`, `subtotal`)
- ✅ Tipos primitivos (`int`)
- ✅ Tipos de referencia (`BigDecimal`, `Cart`, `CartItem`)
- ✅ Operadores (`*`, `/`)
- ✅ Estructuras de control (`if`, `for`)
- ✅ Métodos (`multiply()`, `add()`, `subtract()`)

---

## 📊 Resumen

| Concepto | Sintaxis | Ejemplo |
|----------|----------|---------|
| Variable int | `int nombre = valor;` | `int edad = 25;` |
| Variable String | `String nombre = "texto";` | `String email = "user@mail.com";` |
| Constante | `final tipo NOMBRE = valor;` | `final int MAX = 100;` |
| If/Else | `if (condicion) {...} else {...}` | `if (edad >= 18) {...}` |
| For | `for (int i=0; i<10; i++) {...}` | `for (int i=1; i<=5; i++) {...}` |
| For-Each | `for (Tipo var : coleccion) {...}` | `for (String name : names) {...}` |

---

**Última actualización**: Octubre 2025
