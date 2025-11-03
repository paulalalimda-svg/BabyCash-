# PATRÓN STRATEGY

## 🎯 Definición

**Strategy** define una **familia de algoritmos**, los encapsula y los hace **intercambiables**.

Permite cambiar el algoritmo en tiempo de ejecución sin modificar el cliente.

---

## ❓ ¿Para Qué Sirve?

### Analogía: Formas de Pago

```
Tienes una tienda. Clientes pueden pagar de diferentes formas:
- Tarjeta de crédito
- PayPal
- Bitcoin
- Transferencia bancaria

❌ SIN STRATEGY:
if-else gigante que maneja todos los pagos

✅ CON STRATEGY:
Cada forma de pago es una Strategy diferente
```

---

## 🏗️ Implementación

### ❌ SIN Strategy (Problema)

```java
public class PaymentProcessor {
    
    public void processPayment(String paymentType, BigDecimal amount) {
        if (paymentType.equals("CREDIT_CARD")) {
            // Lógica de tarjeta
            System.out.println("Processing credit card payment: $" + amount);
            // ... código específico de tarjeta
        } else if (paymentType.equals("PAYPAL")) {
            // Lógica de PayPal
            System.out.println("Processing PayPal payment: $" + amount);
            // ... código específico de PayPal
        } else if (paymentType.equals("BITCOIN")) {
            // Lógica de Bitcoin
            System.out.println("Processing Bitcoin payment: $" + amount);
            // ... código específico de Bitcoin
        } else if (paymentType.equals("BANK_TRANSFER")) {
            // Lógica de transferencia
            System.out.println("Processing bank transfer: $" + amount);
            // ... código específico de transferencia
        }
    }
}
```

**Problemas:**
- ❌ If-else gigante
- ❌ Violates Open/Closed Principle
- ❌ Difícil agregar nuevos pagos
- ❌ Difícil de testear

---

### ✅ CON Strategy

```java
// ✅ 1. Interfaz Strategy
public interface PaymentStrategy {
    void pay(BigDecimal amount);
}

// ✅ 2. Strategies concretas
public class CreditCardStrategy implements PaymentStrategy {
    private String cardNumber;
    private String cvv;
    
    public CreditCardStrategy(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }
    
    @Override
    public void pay(BigDecimal amount) {
        System.out.println("Paying $" + amount + " with Credit Card: " + cardNumber);
        // Lógica específica de tarjeta
    }
}

public class PayPalStrategy implements PaymentStrategy {
    private String email;
    
    public PayPalStrategy(String email) {
        this.email = email;
    }
    
    @Override
    public void pay(BigDecimal amount) {
        System.out.println("Paying $" + amount + " via PayPal: " + email);
        // Lógica específica de PayPal
    }
}

public class BitcoinStrategy implements PaymentStrategy {
    private String walletAddress;
    
    public BitcoinStrategy(String walletAddress) {
        this.walletAddress = walletAddress;
    }
    
    @Override
    public void pay(BigDecimal amount) {
        System.out.println("Paying $" + amount + " via Bitcoin: " + walletAddress);
        // Lógica específica de Bitcoin
    }
}

// ✅ 3. Context (usa Strategy)
public class PaymentProcessor {
    
    private PaymentStrategy strategy;
    
    public PaymentProcessor(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void processPayment(BigDecimal amount) {
        strategy.pay(amount);  // ✅ Delega a la Strategy
    }
}

// ✅ Uso
PaymentStrategy creditCard = new CreditCardStrategy("1234-5678-9012-3456", "123");
PaymentProcessor processor = new PaymentProcessor(creditCard);
processor.processPayment(new BigDecimal("100.00"));

// Cambiar estrategia en tiempo de ejecución
PaymentStrategy paypal = new PayPalStrategy("user@example.com");
processor.setStrategy(paypal);
processor.processPayment(new BigDecimal("50.00"));
```

**Ventajas:**
- ✅ Sin if-else
- ✅ Fácil agregar nuevas strategies
- ✅ Cumple Open/Closed Principle
- ✅ Fácil de testear

---

## 🏗️ Strategy en Baby Cash

### ✅ Ejemplo: Descuentos

```java
// ✅ Interfaz Strategy
public interface DiscountStrategy {
    BigDecimal applyDiscount(BigDecimal originalPrice);
}

// ✅ Strategy: Descuento por monto fijo
@Component
public class FixedAmountDiscountStrategy implements DiscountStrategy {
    
    private BigDecimal discountAmount = new BigDecimal("10.00");
    
    @Override
    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        BigDecimal result = originalPrice.subtract(discountAmount);
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }
}

// ✅ Strategy: Descuento por porcentaje
@Component
public class PercentageDiscountStrategy implements DiscountStrategy {
    
    private BigDecimal percentage = new BigDecimal("0.10");  // 10%
    
    @Override
    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        BigDecimal discount = originalPrice.multiply(percentage);
        return originalPrice.subtract(discount);
    }
}

// ✅ Strategy: Descuento por cupón
@Component
public class CouponDiscountStrategy implements DiscountStrategy {
    
    @Autowired
    private CouponRepository couponRepository;
    
    private String couponCode;
    
    public CouponDiscountStrategy(String couponCode) {
        this.couponCode = couponCode;
    }
    
    @Override
    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        Coupon coupon = couponRepository.findByCode(couponCode)
            .orElseThrow(() -> new CouponNotFoundException("Invalid coupon"));
        
        BigDecimal discount = originalPrice.multiply(coupon.getDiscountPercentage());
        return originalPrice.subtract(discount);
    }
}

// ✅ Service usa Strategy
@Service
public class OrderService {
    
    public BigDecimal calculateFinalPrice(BigDecimal originalPrice, DiscountStrategy strategy) {
        return strategy.applyDiscount(originalPrice);
    }
}

// ✅ Uso
BigDecimal originalPrice = new BigDecimal("100.00");

// Descuento fijo
DiscountStrategy fixedDiscount = new FixedAmountDiscountStrategy();
BigDecimal price1 = orderService.calculateFinalPrice(originalPrice, fixedDiscount);
// $90.00

// Descuento porcentual
DiscountStrategy percentageDiscount = new PercentageDiscountStrategy();
BigDecimal price2 = orderService.calculateFinalPrice(originalPrice, percentageDiscount);
// $90.00 (10% de $100)

// Descuento con cupón
DiscountStrategy couponDiscount = new CouponDiscountStrategy("SAVE20");
BigDecimal price3 = orderService.calculateFinalPrice(originalPrice, couponDiscount);
// $80.00 (20% de $100)
```

---

### ✅ Ejemplo: Notificaciones

```java
// ✅ Interfaz Strategy
public interface NotificationStrategy {
    void send(String recipient, String message);
}

// ✅ Strategy: Email
@Component
public class EmailNotificationStrategy implements NotificationStrategy {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void send(String recipient, String message) {
        // Enviar email
        System.out.println("Sending email to " + recipient + ": " + message);
    }
}

// ✅ Strategy: SMS
@Component
public class SmsNotificationStrategy implements NotificationStrategy {
    
    @Override
    public void send(String recipient, String message) {
        // Enviar SMS
        System.out.println("Sending SMS to " + recipient + ": " + message);
    }
}

// ✅ Strategy: Push Notification
@Component
public class PushNotificationStrategy implements NotificationStrategy {
    
    @Override
    public void send(String recipient, String message) {
        // Enviar push
        System.out.println("Sending push to " + recipient + ": " + message);
    }
}

// ✅ Service usa Strategy
@Service
public class NotificationService {
    
    public void notifyUser(User user, String message, NotificationStrategy strategy) {
        strategy.send(user.getEmail(), message);
    }
}
```

---

## 📊 Strategy con Spring

Spring facilita Strategy con **auto-discovery**:

```java
@Service
public class OrderService {
    
    // ✅ Spring inyecta TODAS las implementaciones de PaymentStrategy
    @Autowired
    private List<PaymentStrategy> paymentStrategies;
    
    public void processPayment(String paymentType, BigDecimal amount) {
        // Buscar estrategia apropiada
        PaymentStrategy strategy = paymentStrategies.stream()
            .filter(s -> s.supports(paymentType))
            .findFirst()
            .orElseThrow(() -> new UnsupportedPaymentException());
        
        strategy.pay(amount);
    }
}

// Interfaz con método helper
public interface PaymentStrategy {
    void pay(BigDecimal amount);
    boolean supports(String paymentType);
}

@Component
public class CreditCardStrategy implements PaymentStrategy {
    
    @Override
    public void pay(BigDecimal amount) {
        // ...
    }
    
    @Override
    public boolean supports(String paymentType) {
        return "CREDIT_CARD".equals(paymentType);
    }
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es el patrón Strategy?"**

> "Es un patrón de comportamiento que define una familia de algoritmos, los encapsula en clases separadas y los hace intercambiables. Permite cambiar el algoritmo en tiempo de ejecución sin modificar el código cliente. Por ejemplo, diferentes formas de pago o diferentes estrategias de descuento."

---

**2. "¿Dónde usas Strategy en Baby Cash?"**

> "En varios lugares:
> - **Descuentos**: `FixedAmountDiscountStrategy`, `PercentageDiscountStrategy`, `CouponDiscountStrategy`
> - **Pagos**: `CreditCardStrategy`, `PayPalStrategy` (si los implementáramos)
> - **Notificaciones**: `EmailStrategy`, `SmsStrategy`, `PushStrategy`
> 
> Cada estrategia implementa la misma interfaz pero con lógica diferente."

---

**3. "¿Cuál es la ventaja de Strategy sobre if-else?"**

> "Cumple Open/Closed Principle:
> - **If-else**: Agregar nuevo pago requiere modificar el if-else existente
> - **Strategy**: Agregar nuevo pago solo requiere crear nueva clase que implemente la interfaz
> 
> Además, cada estrategia es independiente y fácil de testear."

---

## 📝 Checklist de Strategy

```
✅ Interfaz Strategy común
✅ Múltiples implementaciones concretas
✅ Context que usa la Strategy
✅ Puede cambiar Strategy en runtime
✅ Sin if-else para seleccionar algoritmo
```

---

## 🏆 Ventajas y Desventajas

### ✅ Ventajas

```
✅ Cumple Open/Closed Principle
✅ Elimina if-else gigantes
✅ Fácil agregar nuevas strategies
✅ Cada estrategia es independiente y testeable
✅ Cliente puede cambiar strategy en runtime
```

---

### ❌ Desventajas

```
❌ Más clases (una por estrategia)
❌ Cliente debe conocer las diferentes strategies
```

---

## 🚀 Conclusión

**Strategy:**
- ✅ Familia de algoritmos intercambiables
- ✅ Elimina if-else
- ✅ Cumple Open/Closed

**En Baby Cash, usamos Strategy para descuentos, pagos y notificaciones.**

---

**Ahora lee:** `PATRON-OBSERVER.md` para el siguiente patrón. 🚀
