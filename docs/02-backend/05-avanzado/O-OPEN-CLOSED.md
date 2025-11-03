# O - OPEN/CLOSED PRINCIPLE (Principio Abierto/Cerrado)

## 📚 Definición

> **"Las entidades de software deben estar ABIERTAS para extensión, pero CERRADAS para modificación"**
> 
> — Bertrand Meyer (1988)

---

## 🤔 ¿Qué Significa?

### Para Principiantes (Analogía)

Imagina un teléfono móvil:

**✅ ABIERTO para extensión:**
- Puedes agregar nuevas APPS sin modificar el sistema operativo
- Cada app extiende la funcionalidad
- Instagram, WhatsApp, juegos → se agregan sin tocar Android/iOS

**🔒 CERRADO para modificación:**
- No modificas el código fuente del sistema operativo
- El sistema base permanece estable
- Las apps nuevas no rompen las antiguas

###Para Programadores

```
Debes poder AGREGAR nueva funcionalidad SIN modificar el código existente
```

**Cómo:**
- Usando **herencia**
- Usando **interfaces**
- Usando **composición**
- Usando **patrones de diseño**

---

## ❌ Violando el Principio

### Ejemplo 1: Modificar Clase Existente

```java
// ❌ VIOLACIÓN: Debes modificar esta clase para agregar nuevos tipos

public class PaymentProcessor {
    
    public void processPayment(Order order, String paymentType) {
        if (paymentType.equals("CREDIT_CARD")) {
            // Lógica tarjeta de crédito
            System.out.println("Procesando con tarjeta de crédito");
            // ... código específico
            
        } else if (paymentType.equals("PAYPAL")) {
            // Lógica PayPal
            System.out.println("Procesando con PayPal");
            // ... código específico
            
        } else if (paymentType.equals("BITCOIN")) {
            // Lógica Bitcoin
            System.out.println("Procesando con Bitcoin");
            // ... código específico
        }
        
        // ¿Qué pasa si quieres agregar "WOMPI" o "MERCADO_PAGO"?
        // → Debes MODIFICAR esta clase agregando más if-else
        // → VIOLA Open/Closed Principle ❌
    }
}
```

### Problemas:
1. **Cada nuevo método de pago** requiere modificar `PaymentProcessor`
2. **Riesgo de romper** código existente
3. **Clase crece** indefinidamente
4. **Tests existentes** pueden fallar
5. **Múltiples desarrolladores** editan el mismo archivo (conflictos)

---

## ✅ Aplicando el Principio

### Solución 1: Usando Interfaces

```java
// ✅ CORRECTO: Define una interfaz

public interface PaymentMethod {
    void processPayment(Order order);
    boolean validate(PaymentInfo info);
    String getPaymentType();
}

// Implementación 1: Tarjeta de Crédito
@Component
public class CreditCardPayment implements PaymentMethod {
    
    @Override
    public void processPayment(Order order) {
        System.out.println("Procesando con tarjeta de crédito");
        // Lógica específica de tarjeta
        validateCard();
        chargeCard(order.getTotal());
        sendConfirmation();
    }
    
    @Override
    public boolean validate(PaymentInfo info) {
        return info.getCardNumber() != null && 
               info.getCvv() != null;
    }
    
    @Override
    public String getPaymentType() {
        return "CREDIT_CARD";
    }
    
    private void validateCard() { /* ... */ }
    private void chargeCard(BigDecimal amount) { /* ... */ }
    private void sendConfirmation() { /* ... */ }
}

// Implementación 2: PayPal
@Component
public class PayPalPayment implements PaymentMethod {
    
    @Override
    public void processPayment(Order order) {
        System.out.println("Procesando con PayPal");
        // Lógica específica de PayPal
        redirectToPayPal();
        handleCallback();
    }
    
    @Override
    public boolean validate(PaymentInfo info) {
        return info.getPayPalEmail() != null;
    }
    
    @Override
    public String getPaymentType() {
        return "PAYPAL";
    }
    
    private void redirectToPayPal() { /* ... */ }
    private void handleCallback() { /* ... */ }
}

// Implementación 3: Bitcoin
@Component
public class BitcoinPayment implements PaymentMethod {
    
    @Override
    public void processPayment(Order order) {
        System.out.println("Procesando con Bitcoin");
        // Lógica específica de Bitcoin
        generateWalletAddress();
        waitForConfirmations();
    }
    
    @Override
    public boolean validate(PaymentInfo info) {
        return info.getWalletAddress() != null;
    }
    
    @Override
    public String getPaymentType() {
        return "BITCOIN";
    }
    
    private void generateWalletAddress() { /* ... */ }
    private void waitForConfirmations() { /* ... */ }
}

// Procesador que usa las implementaciones
@Service
public class PaymentProcessor {
    
    private final Map<String, PaymentMethod> paymentMethods;
    
    @Autowired
    public PaymentProcessor(List<PaymentMethod> methods) {
        // Spring inyecta automáticamente TODAS las implementaciones
        this.paymentMethods = methods.stream()
            .collect(Collectors.toMap(
                PaymentMethod::getPaymentType,
                Function.identity()
            ));
    }
    
    public void processPayment(Order order, String paymentType) {
        PaymentMethod method = paymentMethods.get(paymentType);
        
        if (method == null) {
            throw new UnsupportedPaymentMethodException(paymentType);
        }
        
        if (!method.validate(order.getPaymentInfo())) {
            throw new InvalidPaymentInfoException();
        }
        
        method.processPayment(order);
    }
}
```

### Ventajas:

**🎉 Para agregar Wompi:**
```java
// Solo creas una NUEVA clase, SIN tocar las existentes

@Component
public class WompiPayment implements PaymentMethod {
    
    @Override
    public void processPayment(Order order) {
        System.out.println("Procesando con Wompi");
        // Lógica de Wompi
    }
    
    @Override
    public boolean validate(PaymentInfo info) {
        return info.getWompiToken() != null;
    }
    
    @Override
    public String getPaymentType() {
        return "WOMPI";
    }
}

// ¡Automáticamente Spring lo detecta y lo agrega!
// NO modificaste PaymentProcessor ✅
// NO modificaste las otras implementaciones ✅
```

**✅ Beneficios:**
- PaymentProcessor está **CERRADO** para modificación
- Sistema está **ABIERTO** para agregar nuevos métodos de pago
- Sin riesgo de romper código existente
- Tests antiguos siguen funcionando

---

## 🏢 Ejemplos Reales de Baby Cash

### Ejemplo 1: Estrategias de Descuento

```java
// ✅ Interfaz para descuentos

public interface DiscountStrategy {
    BigDecimal applyDiscount(Order order);
    boolean isApplicable(Order order);
    String getDescription();
}

// Implementación 1: Descuento por monto
@Component
public class AmountDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal applyDiscount(Order order) {
        if (order.getTotal().compareTo(new BigDecimal("100000")) >= 0) {
            return order.getTotal().multiply(new BigDecimal("0.10")); // 10% descuento
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public boolean isApplicable(Order order) {
        return order.getTotal().compareTo(new BigDecimal("100000")) >= 0;
    }
    
    @Override
    public String getDescription() {
        return "10% de descuento en compras mayores a $100,000";
    }
}

// Implementación 2: Descuento por código
@Component
public class CouponDiscountStrategy implements DiscountStrategy {
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Override
    public BigDecimal applyDiscount(Order order) {
        if (order.getCouponCode() != null) {
            Coupon coupon = couponRepository.findByCode(order.getCouponCode())
                .orElse(null);
            if (coupon != null && coupon.isValid()) {
                return order.getTotal().multiply(
                    new BigDecimal(coupon.getDiscountPercentage()).divide(new BigDecimal(100))
                );
            }
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public boolean isApplicable(Order order) {
        return order.getCouponCode() != null;
    }
    
    @Override
    public String getDescription() {
        return "Descuento por cupón";
    }
}

// Implementación 3: Descuento por primera compra
@Component
public class FirstOrderDiscountStrategy implements DiscountStrategy {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public BigDecimal applyDiscount(Order order) {
        List<Order> previousOrders = orderRepository.findByUser(order.getUser());
        if (previousOrders.isEmpty()) {
            return order.getTotal().multiply(new BigDecimal("0.15")); // 15% primera compra
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public boolean isApplicable(Order order) {
        List<Order> previousOrders = orderRepository.findByUser(order.getUser());
        return previousOrders.isEmpty();
    }
    
    @Override
    public String getDescription() {
        return "15% de descuento en tu primera compra";
    }
}

// Servicio que aplica descuentos
@Service
public class DiscountService {
    
    private final List<DiscountStrategy> strategies;
    
    @Autowired
    public DiscountService(List<DiscountStrategy> strategies) {
        this.strategies = strategies;
    }
    
    public BigDecimal calculateTotalDiscount(Order order) {
        return strategies.stream()
            .filter(strategy -> strategy.isApplicable(order))
            .map(strategy -> strategy.applyDiscount(order))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public List<String> getApplicableDiscounts(Order order) {
        return strategies.stream()
            .filter(strategy -> strategy.isApplicable(order))
            .map(DiscountStrategy::getDescription)
            .collect(Collectors.toList());
    }
}
```

**🎉 Para agregar descuento por cumpleaños:**
```java
// Solo creas una nueva clase
@Component
public class BirthdayDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal applyDiscount(Order order) {
        if (isBirthday(order.getUser())) {
            return order.getTotal().multiply(new BigDecimal("0.20")); // 20% cumpleaños
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public boolean isApplicable(Order order) {
        return isBirthday(order.getUser());
    }
    
    @Override
    public String getDescription() {
        return "¡Feliz cumpleaños! 20% de descuento";
    }
    
    private boolean isBirthday(User user) {
        LocalDate today = LocalDate.now();
        LocalDate birthDate = user.getBirthDate();
        return birthDate.getMonth() == today.getMonth() &&
               birthDate.getDayOfMonth() == today.getDayOfMonth();
    }
}
// ¡Sin modificar DiscountService ni otras estrategias! ✅
```

---

### Ejemplo 2: Notificaciones

```java
// ✅ Sistema extensible de notificaciones

public interface NotificationChannel {
    void send(String recipient, String message);
    boolean supports(String channelType);
}

@Component
public class EmailNotificationChannel implements NotificationChannel {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void send(String recipient, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipient);
        email.setSubject("Notificación Baby Cash");
        email.setText(message);
        mailSender.send(email);
    }
    
    @Override
    public boolean supports(String channelType) {
        return "EMAIL".equalsIgnoreCase(channelType);
    }
}

@Component
public class SmsNotificationChannel implements NotificationChannel {
    
    @Autowired
    private TwilioClient twilioClient;
    
    @Override
    public void send(String recipient, String message) {
        twilioClient.sendSms(recipient, message);
    }
    
    @Override
    public boolean supports(String channelType) {
        return "SMS".equalsIgnoreCase(channelType);
    }
}

@Component
public class PushNotificationChannel implements NotificationChannel {
    
    @Autowired
    private FirebaseMessaging firebaseMessaging;
    
    @Override
    public void send(String recipient, String message) {
        Message fcmMessage = Message.builder()
            .setToken(recipient)
            .setNotification(Notification.builder()
                .setBody(message)
                .build())
            .build();
        firebaseMessaging.send(fcmMessage);
    }
    
    @Override
    public boolean supports(String channelType) {
        return "PUSH".equalsIgnoreCase(channelType);
    }
}

@Service
public class NotificationService {
    
    private final List<NotificationChannel> channels;
    
    @Autowired
    public NotificationService(List<NotificationChannel> channels) {
        this.channels = channels;
    }
    
    public void notify(String recipient, String message, String channelType) {
        channels.stream()
            .filter(channel -> channel.supports(channelType))
            .findFirst()
            .ifPresent(channel -> channel.send(recipient, message));
    }
    
    public void notifyAll(String recipient, String message) {
        channels.forEach(channel -> {
            try {
                channel.send(recipient, message);
            } catch (Exception e) {
                log.error("Error enviando notificación: {}", e.getMessage());
            }
        });
    }
}
```

**🎉 Para agregar WhatsApp:**
```java
@Component
public class WhatsAppNotificationChannel implements NotificationChannel {
    
    @Autowired
    private TwilioWhatsAppClient whatsAppClient;
    
    @Override
    public void send(String recipient, String message) {
        whatsAppClient.sendMessage(recipient, message);
    }
    
    @Override
    public boolean supports(String channelType) {
        return "WHATSAPP".equalsIgnoreCase(channelType);
    }
}
// ¡Listo! Sin modificar NotificationService ✅
```

---

## 🎯 Patrones de Diseño que Implementan OCP

### 1. Strategy Pattern
```java
// Define familia de algoritmos intercambiables
interface SortingStrategy {
    void sort(List<Product> products);
}

class PriceSortStrategy implements SortingStrategy { }
class NameSortStrategy implements SortingStrategy { }
class PopularitySortStrategy implements SortingStrategy { }
```

### 2. Template Method Pattern
```java
// Define esqueleto del algoritmo
abstract class ReportGenerator {
    public final void generateReport() {
        loadData();
        formatData();
        exportReport();
    }
    
    protected abstract void loadData();
    protected abstract void formatData();
    protected abstract void exportReport();
}

class PdfReportGenerator extends ReportGenerator { }
class ExcelReportGenerator extends ReportGenerator { }
```

### 3. Decorator Pattern
```java
// Agrega funcionalidad dinámicamente
interface Coffee {
    double cost();
}

class SimpleCoffee implements Coffee { }
class MilkDecorator implements Coffee { }
class SugarDecorator implements Coffee { }
```

---

## ✅ Buenas Prácticas

### 1. Usa Abstracciones

```java
// ✅ Define interfaces claras
public interface Authenticator {
    boolean authenticate(String username, String password);
}

// Luego implementa
public class JwtAuthenticator implements Authenticator { }
public class OAuth2Authenticator implements Authenticator { }
public class LdapAuthenticator implements Authenticator { }
```

### 2. Inyecta Dependencias

```java
// ✅ Spring inyecta todas las implementaciones
@Service
public class MyService {
    
    private final List<MyInterface> implementations;
    
    @Autowired
    public MyService(List<MyInterface> implementations) {
        this.implementations = implementations;
    }
}
```

### 3. Usa Enums para Extensibilidad

```java
public enum OrderStatus {
    PENDING(new PendingOrderHandler()),
    CONFIRMED(new ConfirmedOrderHandler()),
    SHIPPED(new ShippedOrderHandler()),
    DELIVERED(new DeliveredOrderHandler());
    
    private final OrderHandler handler;
    
    OrderStatus(OrderHandler handler) {
        this.handler = handler;
    }
    
    public void handle(Order order) {
        handler.handle(order);
    }
}
```

---

## 🧪 Testing Más Fácil

```java
// ✅ Fácil mockear implementaciones específicas

@Test
public void testPaymentProcessor() {
    PaymentMethod mockMethod = mock(PaymentMethod.class);
    when(mockMethod.getPaymentType()).thenReturn("TEST");
    when(mockMethod.processPayment(any())).thenReturn(true);
    
    PaymentProcessor processor = new PaymentProcessor(List.of(mockMethod));
    
    assertTrue(processor.processPayment(order, "TEST"));
}
```

---

## 🎓 Para la Evaluación del SENA

### Pregunta: "¿Cómo agregarías un nuevo método de pago?"

**Respuesta:**
> "Gracias al principio Open/Closed, solo creo una nueva clase que implemente la interfaz `PaymentMethod`:
> 
> ```java
> @Component
> public class NequiPayment implements PaymentMethod {
>     // Implementación específica de Nequi
> }
> ```
> 
> Spring automáticamente detecta esta nueva implementación y `PaymentProcessor` la puede usar sin ninguna modificación. Esto mantiene el código existente estable y sin riesgo de romper funcionalidad que ya funciona."

---

## 📝 Resumen

```
Open/Closed Principle (OCP)

Regla:
"ABIERTO para extensión, CERRADO para modificación"

Cómo Aplicar:
✅ Usa interfaces y clases abstractas
✅ Implementa patrones Strategy, Template, Decorator
✅ Spring inyecta todas las implementaciones
✅ Agrega funcionalidad sin modificar código existente

Beneficios:
✅ Sin riesgo de romper código que funciona
✅ Fácil agregar nuevas features
✅ Tests existentes siguen pasando
✅ Múltiples devs sin conflictos

En Baby Cash:
✅ PaymentMethod → Múltiples formas de pago
✅ DiscountStrategy → Múltiples descuentos
✅ NotificationChannel → Múltiples canales
✅ Extensible sin modificar servicios base
```

---

**Siguiente:** Lee `L-LISKOV-SUBSTITUTION.md` 🚀
