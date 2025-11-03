# PATRÓN OBSERVER

## 🎯 Definición

**Observer** define una dependencia de **uno a muchos** entre objetos, de modo que cuando un objeto cambia su estado, todos sus dependientes son **notificados automáticamente**.

Es como un sistema de **suscripciones y notificaciones**.

---

## ❓ ¿Para Qué Sirve?

### Analogía: Canal de YouTube

```
✅ Canal de YouTube (Subject):
- Publica videos

✅ Suscriptores (Observers):
- Reciben notificación cuando hay nuevo video
- Pueden suscribirse o desuscribirse

Cuando el canal publica video:
→ Notifica a TODOS los suscriptores automáticamente
```

---

## 🏗️ Implementación

### ✅ Observer Clásico

```java
// ✅ 1. Interfaz Observer
public interface Observer {
    void update(String message);
}

// ✅ 2. Subject (Observable)
public class YoutubeChannel {
    
    private List<Observer> subscribers = new ArrayList<>();
    private String channelName;
    
    public YoutubeChannel(String channelName) {
        this.channelName = channelName;
    }
    
    // Suscribirse
    public void subscribe(Observer observer) {
        subscribers.add(observer);
    }
    
    // Desuscribirse
    public void unsubscribe(Observer observer) {
        subscribers.remove(observer);
    }
    
    // Notificar a todos
    public void uploadVideo(String videoTitle) {
        System.out.println(channelName + " uploaded: " + videoTitle);
        notifyObservers("New video: " + videoTitle);
    }
    
    private void notifyObservers(String message) {
        for (Observer observer : subscribers) {
            observer.update(message);
        }
    }
}

// ✅ 3. Observers concretos
public class EmailSubscriber implements Observer {
    
    private String email;
    
    public EmailSubscriber(String email) {
        this.email = email;
    }
    
    @Override
    public void update(String message) {
        System.out.println("Email sent to " + email + ": " + message);
    }
}

public class MobileSubscriber implements Observer {
    
    private String phoneNumber;
    
    public MobileSubscriber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public void update(String message) {
        System.out.println("Push notification to " + phoneNumber + ": " + message);
    }
}

// ✅ Uso
YoutubeChannel channel = new YoutubeChannel("Baby Cash TV");

Observer emailSub = new EmailSubscriber("user1@example.com");
Observer mobileSub = new MobileSubscriber("555-1234");

channel.subscribe(emailSub);
channel.subscribe(mobileSub);

channel.uploadVideo("How to use Baby Cash");
// Output:
// Baby Cash TV uploaded: How to use Baby Cash
// Email sent to user1@example.com: New video: How to use Baby Cash
// Push notification to 555-1234: New video: How to use Baby Cash
```

---

## 🏗️ Observer en Spring Boot

Spring tiene **Events** basados en Observer:

### ✅ Spring Events

```java
// ✅ 1. Evento (lo que se publica)
public class OrderCreatedEvent {
    
    private Order order;
    
    public OrderCreatedEvent(Order order) {
        this.order = order;
    }
    
    public Order getOrder() {
        return order;
    }
}

// ✅ 2. Publisher (publica eventos)
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;  // ✅ Spring inyecta
    
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = buildOrder(request);
        order = orderRepository.save(order);
        
        // ✅ Publicar evento
        eventPublisher.publishEvent(new OrderCreatedEvent(order));
        
        return mapToResponse(order);
    }
}

// ✅ 3. Listeners (observan eventos)
@Component
public class EmailNotificationListener {
    
    @Autowired
    private EmailService emailService;
    
    @EventListener  // ✅ Spring detecta este método automáticamente
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        emailService.sendOrderConfirmation(order);
        System.out.println("Email sent for order: " + order.getId());
    }
}

@Component
public class LoyaltyPointsListener {
    
    @Autowired
    private LoyaltyService loyaltyService;
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        loyaltyService.addPoints(order.getUser(), order.getTotalAmount());
        System.out.println("Loyalty points added for order: " + order.getId());
    }
}

@Component
public class InventoryListener {
    
    @Autowired
    private ProductRepository productRepository;
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        // Actualizar inventario
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId()).get();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
        System.out.println("Inventory updated for order: " + order.getId());
    }
}
```

**Ventajas:**
- ✅ `OrderService` NO conoce a los listeners
- ✅ Fácil agregar nuevos listeners (solo crear clase con `@EventListener`)
- ✅ Desacoplado

---

## 🏗️ Observer Async en Spring

Listeners pueden ser **asíncronos**:

```java
@Configuration
@EnableAsync  // ✅ Habilitar async
public class AsyncConfig {
    
    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}

@Component
public class EmailNotificationListener {
    
    @Async  // ✅ Ejecuta en thread separado
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Enviar email (puede tardar)
        emailService.sendOrderConfirmation(event.getOrder());
    }
}
```

**Ventaja:** El email se envía en background, no bloquea `createOrder()`.

---

## 📊 Observer en Baby Cash

### ✅ Ejemplo: Eventos de Usuario

```java
// Evento: Usuario registrado
public class UserRegisteredEvent {
    private User user;
    
    public UserRegisteredEvent(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}

// Publisher
@Service
public class AuthService {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public User register(RegisterRequest request) {
        User user = buildUser(request);
        user = userRepository.save(user);
        
        // ✅ Publicar evento
        eventPublisher.publishEvent(new UserRegisteredEvent(user));
        
        return user;
    }
}

// Listeners
@Component
public class WelcomeEmailListener {
    
    @Async
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event.getUser().getEmail());
    }
}

@Component
public class AccountSetupListener {
    
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        // Crear configuración inicial
        userPreferencesService.createDefaultPreferences(event.getUser());
    }
}

@Component
public class AnalyticsListener {
    
    @Async
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        // Registrar en analytics
        analyticsService.trackNewUser(event.getUser());
    }
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es el patrón Observer?"**

> "Es un patrón de comportamiento que define una relación de uno a muchos, donde cuando un objeto (Subject) cambia, notifica automáticamente a todos sus dependientes (Observers). Es como un sistema de suscripciones: te suscribes a un evento y recibes notificaciones cuando ocurre."

---

**2. "¿Dónde usas Observer en Baby Cash?"**

> "Uso Spring Events:
> - **Orden creada**: Notifica a EmailListener (envía confirmación), LoyaltyListener (agrega puntos), InventoryListener (actualiza stock)
> - **Usuario registrado**: Notifica a WelcomeEmailListener, AccountSetupListener, AnalyticsListener
> 
> Esto desacopla el servicio de las acciones secundarias. `OrderService` solo crea la orden, los listeners hacen el resto."

---

**3. "¿Cuál es la ventaja de Observer?"**

> "Desacoplamiento. `OrderService` NO necesita saber que se envía email, se agregan puntos o se actualiza inventario. Solo publica el evento. Si necesito agregar nueva funcionalidad (ej: enviar SMS), solo creo nuevo listener. No toco `OrderService`."

---

## 📝 Checklist de Observer

```
✅ Subject (publica eventos)
✅ Observers (escuchan eventos)
✅ Subscribe/unsubscribe mechanism
✅ Notificación automática
✅ Desacoplado (Subject no conoce Observers)
```

---

## 🏆 Ventajas y Desventajas

### ✅ Ventajas

```
✅ Desacoplamiento (Subject no conoce Observers)
✅ Fácil agregar nuevos Observers
✅ Cumple Open/Closed Principle
✅ Notificación automática
✅ Async support en Spring
```

---

### ❌ Desventajas

```
❌ Orden de notificación no garantizado (sin configuración)
❌ Memory leaks si no se desuscriben
❌ Debugging complejo (flujo no lineal)
```

---

## 🚀 Conclusión

**Observer:**
- ✅ Notificaciones automáticas
- ✅ Desacoplamiento
- ✅ Spring Events facilita implementación

**En Baby Cash, usamos Spring Events para notificaciones de órdenes y usuarios.**

---

**Ahora lee:** `PATRON-DECORATOR.md` para el siguiente patrón. 🚀
