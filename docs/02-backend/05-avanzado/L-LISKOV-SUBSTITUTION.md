# L - LISKOV SUBSTITUTION PRINCIPLE (Principio de Sustitución de Liskov)

## 📚 Definición

> **"Los objetos de una clase derivada deben poder reemplazar objetos de la clase base sin alterar el correcto funcionamiento del programa"**
> 
> — Barbara Liskov (1987)

---

## 🤔 ¿Qué Significa?

### Para Principiantes (Analogía)

Imagina que tienes un **control remoto** para TV:

**✅ CUMPLE Liskov:**
- El control remoto funciona con cualquier TV Samsung
- TV Samsung LED → funciona
- TV Samsung QLED → funciona  
- TV Samsung 4K → funciona
- **Todas responden a los mismos botones** (ON/OFF, volumen, canales)

**❌ VIOLA Liskov:**
- Compras una "TV Samsung" pero resulta que es un ventilador
- Presionas "cambiar canal" y el ventilador explota
- **No se comporta como esperabas**

### Para Programadores

```
Si S es un subtipo de T, entonces los objetos de tipo T pueden ser
reemplazados por objetos de tipo S sin romper el programa.
```

**En simple:**
- Si una clase `Perro` extiende `Animal`
- Donde uses `Animal`, deberías poder usar `Perro`
- Sin que nada se rompa

---

## ❌ Violando el Principio

### Ejemplo Clásico: El Pinguino que No Vuela

```java
// ❌ VIOLACIÓN del principio de Liskov

public class Ave {
    public void volar() {
        System.out.println("El ave está volando");
    }
    
    public void comer() {
        System.out.println("El ave está comiendo");
    }
}

public class Aguila extends Ave {
    @Override
    public void volar() {
        System.out.println("El águila vuela alto");
    }
}

public class Pinguino extends Ave {
    @Override
    public void volar() {
        // ❌ Los pingüinos NO vuelan!
        throw new UnsupportedOperationException("Los pingüinos no pueden volar");
    }
}

// Código que usa Ave
public class AviarIO {
    public void hacerVolarAve(Ave ave) {
        ave.volar(); // ❌ ¡Explota si le pasas un Pingüino!
    }
}

// Uso
AviarIO aviario = new AviarIO();
aviario.hacerVolarAve(new Aguila());    // ✅ Funciona
aviario.hacerVolarAve(new Pinguino());  // ❌ ¡BOOM! Exception
```

**Problema:** 
- `Pinguino` hereda de `Ave`
- Pero NO puede sustituir a `Ave` correctamente
- **Viola Liskov Substitution Principle** ❌

---

## ✅ Aplicando el Principio

### Solución: Jerarquía Correcta

```java
// ✅ CORRECTO: Jerarquía bien diseñada

public abstract class Ave {
    public void comer() {
        System.out.println("El ave está comiendo");
    }
    
    public abstract void desplazarse();
}

// Aves que vuelan
public abstract class AveVoladora extends Ave {
    @Override
    public void desplazarse() {
        volar();
    }
    
    public void volar() {
        System.out.println("Volando por el cielo");
    }
}

// Aves que nadan
public abstract class AveNadadora extends Ave {
    @Override
    public void desplazarse() {
        nadar();
    }
    
    public void nadar() {
        System.out.println("Nadando en el agua");
    }
}

// Implementaciones concretas
public class Aguila extends AveVoladora {
    @Override
    public void volar() {
        System.out.println("Águila volando alto");
    }
}

public class Pinguino extends AveNadadora {
    @Override
    public void nadar() {
        System.out.println("Pingüino nadando rápido");
    }
}

// Ahora el código funciona correctamente
public class Aviario {
    public void hacerDesplazarAve(Ave ave) {
        ave.desplazarse(); // ✅ Funciona para TODAS las aves
    }
    
    public void hacerVolarAves(List<AveVoladora> aves) {
        for (AveVoladora ave : aves) {
            ave.volar(); // ✅ Solo acepta aves que vuelan
        }
    }
}
```

**Ahora SÍ cumple Liskov:**
- Puedes sustituir `Ave` por `Aguila` o `Pinguino`
- Cada uno se comporta como se espera
- Sin excepciones ni sorpresas

---

## 🏢 Ejemplos Reales de Baby Cash

### Ejemplo 1: Sistema de Pagos

```java
// ❌ VIOLACIÓN

public abstract class Payment {
    protected BigDecimal amount;
    
    public abstract void processPayment();
    
    public void refund() {
        // Devolver dinero
        System.out.println("Reembolsando " + amount);
    }
}

public class CreditCardPayment extends Payment {
    @Override
    public void processPayment() {
        System.out.println("Procesando tarjeta de crédito");
    }
}

public class GiftCardPayment extends Payment {
    @Override
    public void processPayment() {
        System.out.println("Procesando tarjeta de regalo");
    }
    
    @Override
    public void refund() {
        // ❌ Las gift cards NO se pueden reembolsar!
        throw new UnsupportedOperationException("Gift cards no se reembolsan");
    }
}

// ❌ Código que falla
public void procesarYReembolsar(Payment payment) {
    payment.processPayment();
    payment.refund(); // ❌ Falla si es GiftCard
}
```

### ✅ Solución Correcta

```java
// ✅ CORRECTO: Separar responsabilidades

public interface Payable {
    void processPayment();
}

public interface Refundable {
    void refund();
}

// Pagos reembolsables
public class CreditCardPayment implements Payable, Refundable {
    private BigDecimal amount;
    
    @Override
    public void processPayment() {
        System.out.println("Procesando tarjeta de crédito: " + amount);
    }
    
    @Override
    public void refund() {
        System.out.println("Reembolsando a tarjeta: " + amount);
    }
}

public class PayPalPayment implements Payable, Refundable {
    private BigDecimal amount;
    
    @Override
    public void processPayment() {
        System.out.println("Procesando PayPal: " + amount);
    }
    
    @Override
    public void refund() {
        System.out.println("Reembolsando a PayPal: " + amount);
    }
}

// Pagos NO reembolsables
public class GiftCardPayment implements Payable {
    private BigDecimal amount;
    
    @Override
    public void processPayment() {
        System.out.println("Procesando Gift Card: " + amount);
    }
    
    // ✅ NO implementa Refundable, por lo tanto NO se puede reembolsar
}

// Servicio que usa los pagos
@Service
public class PaymentService {
    
    public void process(Payable payment) {
        payment.processPayment(); // ✅ Funciona para TODOS
    }
    
    public void processWithRefund(Payable payment) {
        payment.processPayment();
        
        // ✅ Solo reembolsa si es posible
        if (payment instanceof Refundable) {
            ((Refundable) payment).refund();
        }
    }
}
```

**Ahora cumple Liskov:**
- No forzamos `GiftCardPayment` a implementar `refund()`
- El comportamiento es predecible
- Sin excepciones inesperadas

---

### Ejemplo 2: Usuarios y Permisos

```java
// ❌ VIOLACIÓN

public class User {
    protected String email;
    protected String password;
    
    public void login() {
        System.out.println("Usuario logueado");
    }
    
    public void accessAdminPanel() {
        System.out.println("Accediendo al panel de administración");
    }
}

public class AdminUser extends User {
    @Override
    public void accessAdminPanel() {
        System.out.println("Admin accediendo al panel");
    }
}

public class RegularUser extends User {
    @Override
    public void accessAdminPanel() {
        // ❌ Usuario regular NO puede acceder
        throw new UnauthorizedException("No tienes permisos");
    }
}

// ❌ Este código falla
public void permitirAcceso(User user) {
    user.accessAdminPanel(); // ❌ Explota si es RegularUser
}
```

### ✅ Solución Correcta

```java
// ✅ CORRECTO: Separar por capacidades

public abstract class User {
    protected String email;
    protected String password;
    protected Set<String> roles;
    
    public void login() {
        System.out.println("Usuario logueado: " + email);
    }
    
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}

public class AdminUser extends User {
    public AdminUser(String email, String password) {
        this.email = email;
        this.password = password;
        this.roles = Set.of("ADMIN", "USER");
    }
}

public class RegularUser extends User {
    public RegularUser(String email, String password) {
        this.email = email;
        this.password = password;
        this.roles = Set.of("USER");
    }
}

// Servicio que verifica permisos
@Service
public class AuthService {
    
    public void accessAdminPanel(User user) {
        // ✅ Verifica permisos antes de permitir acceso
        if (user.hasRole("ADMIN")) {
            System.out.println("Acceso permitido al panel admin");
        } else {
            throw new UnauthorizedException("No tienes permisos de admin");
        }
    }
    
    public void accessUserProfile(User user) {
        // ✅ Todos los usuarios pueden acceder a su perfil
        if (user.hasRole("USER")) {
            System.out.println("Accediendo al perfil de usuario");
        }
    }
}
```

---

## 🎯 Reglas para Cumplir Liskov

### 1. Precondiciones No Pueden Ser Más Fuertes

```java
// ❌ VIOLA Liskov
public class Rectangle {
    public void setWidth(int width) {
        if (width < 0) throw new IllegalArgumentException();
        this.width = width;
    }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        // ❌ Precondición más fuerte (requiere que sea positivo Y mayor a 10)
        if (width < 0 || width < 10) {
            throw new IllegalArgumentException("Debe ser mayor a 10");
        }
        this.width = width;
        this.height = width;
    }
}

// ✅ CUMPLE Liskov
public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        // ✅ Misma precondición que Rectangle
        if (width < 0) throw new IllegalArgumentException();
        this.width = width;
        this.height = width; // Mantiene cuadrado
    }
}
```

### 2. Postcondiciones No Pueden Ser Más Débiles

```java
// ❌ VIOLA Liskov
public class BankAccount {
    protected BigDecimal balance;
    
    public void withdraw(BigDecimal amount) {
        // Postcondición: balance siempre >= 0
        if (balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
        }
    }
}

public class OverdraftAccount extends BankAccount {
    @Override
    public void withdraw(BigDecimal amount) {
        // ❌ Postcondición más débil (permite balance negativo)
        balance = balance.subtract(amount); // Puede quedar negativo
    }
}

// ✅ CUMPLE Liskov  
public class OverdraftAccount extends BankAccount {
    private BigDecimal overdraftLimit;
    
    @Override
    public void withdraw(BigDecimal amount) {
        // ✅ Mantiene postcondición (balance >= -overdraftLimit)
        BigDecimal minBalance = overdraftLimit.negate();
        if (balance.subtract(amount).compareTo(minBalance) >= 0) {
            balance = balance.subtract(amount);
        }
    }
}
```

### 3. Invariantes Deben Preservarse

```java
// ✅ CUMPLE Liskov
public class Product {
    protected BigDecimal price;
    
    // Invariante: price siempre > 0
    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Precio debe ser positivo");
        }
        this.price = price;
    }
}

public class DiscountedProduct extends Product {
    private BigDecimal discount;
    
    @Override
    public void setPrice(BigDecimal price) {
        // ✅ Preserva invariante (precio final > 0)
        super.setPrice(price);
        
        BigDecimal finalPrice = price.subtract(discount);
        if (finalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Precio con descuento debe ser positivo");
        }
    }
}
```

---

## ✅ Señales de Cumplimiento

### Tu código CUMPLE Liskov si:

1. **No lanza excepciones** inesperadas en subclases
2. **No requiere instanceof** para distinguir comportamiento
3. **Tests de la clase base** pasan para subclases
4. **Documentación** describe comportamiento consistente
5. **Sustitución** funciona sin modificar código cliente

```java
// ✅ Test que verifica Liskov
@Test
public void testLiskovSubstitution() {
    // Si funciona con clase base
    User user1 = new RegularUser("user@test.com", "pass");
    assertDoesNotThrow(() -> user1.login());
    
    // Debe funcionar con subclase
    User user2 = new AdminUser("admin@test.com", "pass");
    assertDoesNotThrow(() -> user2.login());
}
```

---

## 🎓 Para la Evaluación del SENA

### Pregunta: "¿Por qué no todos los usuarios pueden acceder al admin?"

**Respuesta:**
> "Para cumplir con el Principio de Sustitución de Liskov, no forcé a que todos los `User` tengan método `accessAdminPanel()`. En su lugar, uso un sistema de roles donde verifico permisos antes de permitir acceso. Así, cualquier tipo de `User` (Admin o Regular) puede sustituirse sin romper el código, y el comportamiento es predecible según sus roles."

### Pregunta: "¿Cómo garantizas que las subclases se comporten correctamente?"

**Respuesta:**
> "Sigo tres reglas de Liskov:
> 1. **Precondiciones** no son más fuertes en subclases
> 2. **Postcondiciones** no son más débiles  
> 3. **Invariantes** se preservan en toda la jerarquía
> 
> Por ejemplo, si `Product` requiere `price > 0`, todas las subclases como `DiscountedProduct` mantienen esa regla."

---

## 📝 Resumen

```
Liskov Substitution Principle (LSP)

Regla:
"Las subclases deben poder sustituir a la clase base
sin alterar el correcto funcionamiento del programa"

Cómo Aplicar:
✅ Subclases NO lanzan excepciones inesperadas
✅ NO usar instanceof para distinguir tipos
✅ Precondiciones NO más fuertes
✅ Postcondiciones NO más débiles
✅ Invariantes preservados

Qué Evitar:
❌ throw UnsupportedOperationException
❌ Cambiar contratos de métodos
❌ Comportamiento inesperado en subclases

En Baby Cash:
✅ User con roles (no herencia para permisos)
✅ Payment separado de Refundable
✅ Jerarquías bien diseñadas
```

---

**Siguiente:** Lee `I-INTERFACE-SEGREGATION.md` 🚀
