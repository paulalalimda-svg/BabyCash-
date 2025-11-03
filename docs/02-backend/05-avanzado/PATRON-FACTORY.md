# PATRÓN FACTORY

## 🎯 Definición

**Factory** proporciona una interfaz para crear objetos **sin exponer la lógica de creación** al cliente.

El cliente pide "dame un objeto de tipo X" y el Factory decide **cómo crearlo**.

---

## ❓ ¿Para Qué Sirve?

### Analogía: Restaurante

```
❌ SIN FACTORY:
Cliente: Voy a la cocina, busco ingredientes, cocino mi hamburguesa.

✅ CON FACTORY:
Cliente: "Quiero una hamburguesa"
Factory (Cocina): "Aquí está" (la cocina decide cómo hacerla)
```

El cliente NO conoce los detalles de preparación.

---

## 🏗️ Implementación

### ❌ SIN Factory (Problema)

```java
// Cliente debe conocer TODAS las clases concretas
public class PaymentProcessor {
    
    public void processPayment(String paymentType, BigDecimal amount) {
        if (paymentType.equals("CREDIT_CARD")) {
            CreditCardPayment payment = new CreditCardPayment();  // ❌ Expuesto
            payment.process(amount);
        } else if (paymentType.equals("PAYPAL")) {
            PayPalPayment payment = new PayPalPayment();  // ❌ Expuesto
            payment.process(amount);
        } else if (paymentType.equals("BITCOIN")) {
            BitcoinPayment payment = new BitcoinPayment();  // ❌ Expuesto
            payment.process(amount);
        }
    }
}
```

**Problema:** Cliente conoce todas las clases. Si agregas nuevo pago, modificas cliente.

---

### ✅ CON Factory

```java
// Interfaz común
public interface Payment {
    void process(BigDecimal amount);
}

// Implementaciones concretas
public class CreditCardPayment implements Payment {
    public void process(BigDecimal amount) {
        System.out.println("Processing $" + amount + " via Credit Card");
    }
}

public class PayPalPayment implements Payment {
    public void process(BigDecimal amount) {
        System.out.println("Processing $" + amount + " via PayPal");
    }
}

public class BitcoinPayment implements Payment {
    public void process(BigDecimal amount) {
        System.out.println("Processing $" + amount + " via Bitcoin");
    }
}

// ✅ FACTORY
public class PaymentFactory {
    
    public static Payment createPayment(String paymentType) {
        switch (paymentType) {
            case "CREDIT_CARD":
                return new CreditCardPayment();
            case "PAYPAL":
                return new PayPalPayment();
            case "BITCOIN":
                return new BitcoinPayment();
            default:
                throw new IllegalArgumentException("Unknown payment type");
        }
    }
}

// ✅ Cliente usa Factory
public class PaymentProcessor {
    
    public void processPayment(String paymentType, BigDecimal amount) {
        Payment payment = PaymentFactory.createPayment(paymentType);  // ✅ Simple
        payment.process(amount);
    }
}
```

**Ventaja:** Cliente NO conoce clases concretas. Solo usa la interfaz `Payment`.

---

## 🏗️ Factory en Spring Boot

Spring Boot usa Factory **automáticamente**:

### ✅ Spring BeanFactory

```java
@Configuration
public class AppConfig {
    
    // ✅ Factory method para crear bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Factory decide implementación
    }
    
    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        return mailSender;
    }
}
```

**Spring es el Factory.** Crea y gestiona beans.

---

### ✅ Profiles con Factory

```java
@Configuration
public class EmailConfig {
    
    // ✅ Factory retorna implementación según perfil
    @Bean
    @Profile("dev")
    public EmailSender devEmailSender() {
        return new MockEmailSender();  // Mock en desarrollo
    }
    
    @Bean
    @Profile("prod")
    public EmailSender prodEmailSender() {
        return new GmailEmailSender();  // Real en producción
    }
}

// Cliente usa la interfaz
@Service
public class NotificationService {
    
    private final EmailSender emailSender;  // ✅ No sabe cuál implementación
    
    public void sendNotification(String email, String message) {
        emailSender.send(email, message);  // ✅ Factory decide cuál usar
    }
}
```

---

## 📊 Tipos de Factory

### 1️⃣ Simple Factory

```java
public class AnimalFactory {
    
    public static Animal createAnimal(String type) {
        if (type.equals("DOG")) {
            return new Dog();
        } else if (type.equals("CAT")) {
            return new Cat();
        }
        return null;
    }
}
```

---

### 2️⃣ Factory Method

```java
// Clase abstracta con factory method
public abstract class Document {
    
    // ✅ Factory method abstracto
    public abstract Page createPage();
    
    public void print() {
        Page page = createPage();  // Subclase decide qué página crear
        page.render();
    }
}

// Implementaciones concretas
public class PDFDocument extends Document {
    public Page createPage() {
        return new PDFPage();  // ✅ PDF decide crear PDFPage
    }
}

public class WordDocument extends Document {
    public Page createPage() {
        return new WordPage();  // ✅ Word decide crear WordPage
    }
}
```

---

### 3️⃣ Abstract Factory

```java
// Factory de factories
public interface UIFactory {
    Button createButton();
    TextBox createTextBox();
}

public class WindowsUIFactory implements UIFactory {
    public Button createButton() {
        return new WindowsButton();
    }
    
    public TextBox createTextBox() {
        return new WindowsTextBox();
    }
}

public class MacUIFactory implements UIFactory {
    public Button createButton() {
        return new MacButton();
    }
    
    public TextBox createTextBox() {
        return new MacTextBox();
    }
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es el patrón Factory?"**

> "Es un patrón creacional que proporciona una interfaz para crear objetos sin exponer la lógica de creación al cliente. El Factory decide cómo y qué objeto crear según los parámetros. Esto desacopla el cliente de las clases concretas."

---

**2. "¿Dónde usas Factory en Baby Cash?"**

> "Spring Boot ES un Factory gigante:
> - `@Bean` methods son Factory methods
> - `@Configuration` classes son Factories
> - Spring decide qué beans crear según `@Profile`
> - `@Autowired` usa Factory para inyectar dependencias
> 
> Ejemplo: `PasswordEncoder` bean es creado por Factory method en `SecurityConfig`."

---

**3. "¿Cuál es la diferencia entre Factory y new?"**

> "`new` expone la clase concreta directamente. Factory oculta la creación:
> - `new CreditCardPayment()` → Cliente conoce la clase
> - `PaymentFactory.create('CREDIT_CARD')` → Cliente NO conoce la clase
> 
> Factory permite cambiar implementaciones sin modificar cliente."

---

## 📝 Checklist de Factory

```
✅ Interfaz común para productos
✅ Factory method que retorna la interfaz
✅ Clientes usan Factory, no `new`
✅ Fácil agregar nuevos productos
✅ Cliente desacoplado de clases concretas
```

---

## 🏆 Ventajas y Desventajas

### ✅ Ventajas

```
✅ Desacopla cliente de clases concretas
✅ Fácil agregar nuevos productos
✅ Centraliza lógica de creación
✅ Cumple Open/Closed Principle
```

---

### ❌ Desventajas

```
❌ Más clases (puede ser overkill para casos simples)
❌ Complejidad adicional
```

---

## 🚀 Conclusión

**Factory:**
- ✅ Crea objetos sin exponer lógica
- ✅ Desacopla cliente de implementaciones
- ✅ Spring Boot lo usa extensivamente

**En Baby Cash, Spring es el Factory principal.**

---

**Ahora lee:** `PATRON-BUILDER.md` para el siguiente patrón. 🚀
