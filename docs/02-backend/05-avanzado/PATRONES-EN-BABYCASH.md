# PATRONES DE DISEÑO EN BABY CASH

## 🎯 Análisis Completo del Proyecto

Este documento muestra **TODOS los patrones de diseño** usados en Baby Cash y **cómo trabajan juntos**.

---

## 📊 Resumen de Patrones

### ✅ Patrones Creacionales

| Patrón | Dónde | Para Qué |
|--------|-------|----------|
| **Singleton** | Todos los `@Service`, `@Repository`, `@Component` | Una instancia por bean |
| **Factory** | `@Bean` methods en `@Configuration` | Crear objetos complejos (PasswordEncoder, JavaMailSender) |
| **Builder** | `@Builder` de Lombok en Entities | Construir objetos complejos (Product, Order) |

---

### ✅ Patrones Estructurales

| Patrón | Dónde | Para Qué |
|--------|-------|----------|
| **Repository** | `ProductRepository`, `OrderRepository`, `UserRepository` | Abstracción de acceso a datos |
| **DTO** | `CreateProductRequest`, `ProductResponse`, `OrderResponse` | Transferir datos entre capas |
| **Decorator** | Spring Security filters, Spring AOP `@Aspect` | Agregar funcionalidad (logging, seguridad) |
| **Proxy** | Spring AOP, `@Transactional` | Interceptar llamadas a métodos |

---

### ✅ Patrones Comportamentales

| Patrón | Dónde | Para Qué |
|--------|-------|----------|
| **Strategy** | `DiscountStrategy`, `NotificationStrategy` | Algoritmos intercambiables |
| **Observer** | Spring Events (`OrderCreatedEvent`, `UserRegisteredEvent`) | Notificaciones uno-a-muchos |
| **Template Method** | `JpaRepository` | Definir esqueleto de algoritmo |

---

### ✅ Patrones Arquitectónicos

| Patrón | Dónde | Para Qué |
|--------|-------|----------|
| **MVC** | Controller → Service → Repository | Separación de responsabilidades |
| **Dependency Injection** | `@Autowired`, constructor injection | Inversión de control |
| **Layered Architecture** | Presentation → Business → Persistence → Database | Separación en capas |

---

## 🏗️ Arquitectura Completa de Baby Cash

```
┌───────────────────────────────────────────────────────────┐
│                  FRONTEND (React)                         │
│  - Components (ProductCard, Navbar, Footer)               │
│  - Pages (Home, ProductDetail, Cart, Checkout)            │
│  - Hooks (useAuth, useAdminCrud)                          │
│  - API Client (axios)                                     │
└────────────────────────┬──────────────────────────────────┘
                         │ HTTP REST API
                         ▼
┌───────────────────────────────────────────────────────────┐
│               CONTROLLER LAYER                            │
│  @RestController                                          │
│  - ProductController                                      │
│  - OrderController                                        │
│  - UserController                                         │
│  - AuthController                                         │
│  - CategoryController                                     │
│                                                           │
│  Patrones:                                                │
│  ✅ MVC (recibe requests, devuelve responses)             │
│  ✅ DTO (valida CreateXRequest, devuelve XResponse)       │
│  ✅ Dependency Injection (inyecta services)               │
└────────────────────────┬──────────────────────────────────┘
                         │
                         ▼
┌───────────────────────────────────────────────────────────┐
│               SERVICE LAYER                               │
│  @Service                                                 │
│  - ProductService                                         │
│  - OrderService                                           │
│  - UserService                                            │
│  - AuthService                                            │
│  - EmailService                                           │
│                                                           │
│  Patrones:                                                │
│  ✅ Singleton (Spring gestiona una instancia)             │
│  ✅ MVC (lógica de negocio)                               │
│  ✅ Strategy (DiscountStrategy, NotificationStrategy)     │
│  ✅ Observer (publica eventos: OrderCreatedEvent)         │
│  ✅ Dependency Injection (inyecta repositories)           │
│  ✅ Decorator (LoggingAspect envuelve métodos)            │
│  ✅ Builder (construye entities con .builder())           │
└────────────────────────┬──────────────────────────────────┘
                         │
                         ▼
┌───────────────────────────────────────────────────────────┐
│               REPOSITORY LAYER                            │
│  @Repository (extends JpaRepository)                      │
│  - ProductRepository                                      │
│  - OrderRepository                                        │
│  - UserRepository                                         │
│  - CategoryRepository                                     │
│                                                           │
│  Patrones:                                                │
│  ✅ Singleton (Spring gestiona una instancia)             │
│  ✅ Repository (abstracción de acceso a datos)            │
│  ✅ Template Method (JpaRepository define esqueleto)      │
│  ✅ Proxy (Spring genera implementación automática)       │
└────────────────────────┬──────────────────────────────────┘
                         │
                         ▼
┌───────────────────────────────────────────────────────────┐
│               DATABASE LAYER                              │
│  PostgreSQL                                               │
│  - products                                               │
│  - orders                                                 │
│  - users                                                  │
│  - categories                                             │
└───────────────────────────────────────────────────────────┘
```

---

## 🔄 Flujo Completo: Crear Orden

Veamos cómo **todos los patrones trabajan juntos** en un flujo real:

### 1️⃣ Frontend (React)

```tsx
// ✅ Usuario hace click en "Comprar"
const handleCheckout = async () => {
  const request = {
    userId: user.id,
    items: cartItems.map(item => ({
      productId: item.productId,
      quantity: item.quantity,
    })),
    shippingAddress: "123 Main St",
    paymentMethod: "CREDIT_CARD",
  };
  
  // ✅ Llama API
  const response = await axios.post('/api/orders', request);
  console.log('Order created:', response.data);
};
```

---

### 2️⃣ Controller

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor  // ✅ Dependency Injection
public class OrderController {
    
    private final OrderService orderService;  // ✅ Singleton
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
        @Valid @RequestBody CreateOrderRequest request  // ✅ DTO
    ) {
        // ✅ MVC: Controller recibe request, valida, llama service
        OrderResponse order = orderService.createOrder(request);
        
        // ✅ DTO: Devuelve OrderResponse
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
```

---

### 3️⃣ Service

```java
@Service
@RequiredArgsConstructor  // ✅ Dependency Injection
@Slf4j
public class OrderService {
    
    // ✅ Dependency Injection: Dependencies inyectadas
    private final OrderRepository orderRepository;  // ✅ Repository pattern
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;  // ✅ Observer pattern
    
    @Transactional  // ✅ Proxy pattern (Spring intercepta)
    public OrderResponse createOrder(CreateOrderRequest request) {
        // ✅ Repository: Buscar usuario
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // ✅ Builder: Construir orden
        Order order = Order.builder()
            .user(user)
            .shippingAddress(request.getShippingAddress())
            .billingAddress(request.getBillingAddress())
            .paymentMethod(request.getPaymentMethod())
            .status(OrderStatus.PENDING)
            .build();
        
        // ✅ Builder + Repository: Crear items
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
            OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(itemReq.getQuantity())
                .price(product.getPrice())
                .build();
            
            order.addItem(item);
        }
        
        // ✅ Strategy: Aplicar descuento (si hay)
        if (request.getCouponCode() != null) {
            DiscountStrategy discountStrategy = getDiscountStrategy(request.getCouponCode());
            BigDecimal discount = discountStrategy.calculateDiscount(order.getTotalAmount());
            order.setDiscount(discount);
        }
        
        // ✅ Repository: Guardar orden
        Order savedOrder = orderRepository.save(order);
        
        // ✅ Observer: Publicar evento
        eventPublisher.publishEvent(new OrderCreatedEvent(savedOrder));
        
        log.info("Order created: {}", savedOrder.getId());  // ✅ Decorator (LoggingAspect)
        
        // ✅ DTO: Mapear Entity → Response
        return mapToResponse(savedOrder);
    }
}
```

---

### 4️⃣ Event Listeners (Observer)

```java
// ✅ Observer: EmailListener escucha OrderCreatedEvent
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {
    
    private final EmailService emailService;  // ✅ Singleton
    
    @EventListener
    @Async  // ✅ Ejecuta en background
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        
        // ✅ Strategy: Email notification strategy
        NotificationStrategy strategy = new EmailNotificationStrategy(emailService);
        strategy.send(
            order.getUser().getEmail(),
            "Order Confirmation",
            "Your order #" + order.getId() + " has been created!"
        );
    }
}

// ✅ Observer: LoyaltyListener escucha OrderCreatedEvent
@Component
@RequiredArgsConstructor
public class LoyaltyPointsListener {
    
    private final UserRepository userRepository;
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        User user = order.getUser();
        
        // ✅ Lógica de puntos
        int points = order.getTotalAmount().intValue() / 10;
        user.addLoyaltyPoints(points);
        
        userRepository.save(user);
    }
}
```

---

### 5️⃣ Repository

```java
// ✅ Repository: Abstracción de acceso a datos
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // ✅ Template Method: JpaRepository define esqueleto
    // ✅ Proxy: Spring genera implementación automática
    
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus status);
}
```

---

### 6️⃣ Database

```sql
-- ✅ PostgreSQL ejecuta INSERT
INSERT INTO orders (user_id, shipping_address, payment_method, status, total_amount)
VALUES (1, '123 Main St', 'CREDIT_CARD', 'PENDING', 99.99);

INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES (1, 1, 2, 15.99);
```

---

## 🎯 Patrones en Acción: Tabla Completa

| Paso | Patrón | Cómo se Usa |
|------|--------|-------------|
| Frontend → Backend | **MVC** | Controller recibe request |
| Controller valida | **DTO** | `@Valid CreateOrderRequest` |
| Controller → Service | **Dependency Injection** | `private final OrderService` |
| Service es único | **Singleton** | Spring gestiona una instancia |
| Service busca datos | **Repository** | `userRepository.findById()` |
| Service construye orden | **Builder** | `Order.builder().user().build()` |
| Service aplica descuento | **Strategy** | `discountStrategy.calculateDiscount()` |
| Service guarda orden | **Repository** | `orderRepository.save()` |
| Service guarda con transacción | **Proxy** | `@Transactional` intercepta |
| Service registra log | **Decorator** | `LoggingAspect` envuelve método |
| Service publica evento | **Observer** | `eventPublisher.publishEvent()` |
| Listeners escuchan | **Observer** | `@EventListener` reacciona |
| Email enviado | **Strategy** | `EmailNotificationStrategy` |
| Repository accede DB | **Template Method** | `JpaRepository` define esqueleto |
| Service → Controller | **DTO** | `return OrderResponse` |

---

## 🏆 Ventajas de Usar Patrones

### ✅ Mantenibilidad

```
Sin patrones:
- Todo en una clase gigante
- Cambiar algo rompe todo
- Difícil entender código

Con patrones:
- Cada clase tiene responsabilidad clara
- Cambiar discount strategy no afecta orden
- Fácil entender flujo
```

---

### ✅ Escalabilidad

```
Sin patrones:
- Agregar notificación SMS requiere cambiar OrderService
- Agregar payment method requiere cambiar lógica

Con patrones:
- Agregar SmsNotificationStrategy sin tocar OrderService
- Agregar BitcoinPaymentStrategy sin cambiar lógica existente
```

---

### ✅ Testabilidad

```
Sin patrones:
- Service con new JpaOrderRepository() → difícil mockear

Con patrones:
- Service con DI → fácil inyectar mocks
- @InjectMocks OrderService + @Mock OrderRepository
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué patrones usas en Baby Cash?"**

> "Baby Cash usa 12 patrones principales:
> - **Creacionales**: Singleton, Factory, Builder
> - **Estructurales**: Repository, DTO, Decorator, Proxy
> - **Comportamentales**: Strategy, Observer, Template Method
> - **Arquitectónicos**: MVC, Dependency Injection
> 
> Todos trabajan juntos para hacer el código mantenible, escalable y testeable."

---

**2. "¿Cómo trabajan los patrones juntos?"**

> "Ejemplo: Crear orden
> 1. **MVC**: Controller recibe request, llama service
> 2. **DTO**: Request validado, response devuelto
> 3. **Dependency Injection**: Service recibe repositories inyectados
> 4. **Singleton**: Todos los services/repositories son únicos
> 5. **Repository**: Service accede DB sin conocer SQL
> 6. **Builder**: Service construye orden con fluent API
> 7. **Strategy**: Service aplica descuento según estrategia
> 8. **Observer**: Service publica evento, listeners reaccionan
> 9. **Decorator**: Logging aspect registra todo automáticamente
> 
> Todos los patrones cooperan para un flujo limpio."

---

**3. "¿Por qué usar tantos patrones?"**

> "Cada patrón resuelve un problema específico:
> - **Singleton**: Evita crear múltiples instancias innecesarias
> - **Repository**: Desacopla service de DB
> - **DTO**: Evita exponer datos sensibles
> - **Strategy**: Elimina if-else gigantes
> - **Observer**: Desacopla orden de notificaciones
> 
> Sin patrones, el código sería un gigante monolito difícil de mantener."

---

**4. "¿Cuál es el patrón más importante?"**

> "Todos son importantes, pero si debo elegir 3:
> 1. **MVC**: Separa responsabilidades (Controller, Service, Repository)
> 2. **Dependency Injection**: Desacopla clases, facilita testing
> 3. **Repository**: Abstrae acceso a datos
> 
> Estos 3 son la base de la arquitectura limpia de Baby Cash."

---

## 📝 Checklist de Patrones en Baby Cash

```
✅ Singleton: @Service, @Repository, @Component
✅ Factory: @Bean methods en @Configuration
✅ Builder: @Builder de Lombok en Entities
✅ Repository: ProductRepository extends JpaRepository
✅ DTO: CreateXRequest, XResponse
✅ Decorator: Spring Security filters, @Aspect
✅ Proxy: @Transactional, Spring AOP
✅ Strategy: DiscountStrategy, NotificationStrategy
✅ Observer: Spring Events, @EventListener
✅ Template Method: JpaRepository
✅ MVC: Controller → Service → Repository
✅ Dependency Injection: @Autowired, constructor injection
```

---

## 🚀 Conclusión

**Baby Cash usa 12 patrones de diseño** que trabajan juntos para:
- ✅ Separar responsabilidades (MVC, Repository, DTO)
- ✅ Desacoplar clases (Dependency Injection, Strategy, Observer)
- ✅ Facilitar testing (DI, Repository, DTO)
- ✅ Agregar funcionalidad sin modificar código (Decorator, Observer)
- ✅ Construir objetos complejos (Builder, Factory)
- ✅ Gestionar instancias (Singleton)

**Resultado: Código limpio, mantenible, escalable y testeable.**

---

**Ahora lee:** `README-PRIMERO.md` para guía de navegación. 🚀
