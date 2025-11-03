# PATRÓN DECORATOR

## 🎯 Definición

**Decorator** permite **agregar funcionalidad** a un objeto de forma dinámica **sin modificar su estructura**.

Es como agregar toppings a un helado: puedes agregar chispas, sirope, cerezas... sin cambiar el helado base.

---

## ❓ ¿Para Qué Sirve?

### Analogía: Café

```
✅ Café simple: $2
✅ Café + Leche: $2.50
✅ Café + Leche + Caramelo: $3
✅ Café + Leche + Caramelo + Crema: $3.50

Cada agregado es un Decorator que envuelve el café anterior.
```

---

## 🏗️ Implementación

### ❌ SIN Decorator (Problema)

```java
// ❌ Explosión de clases
public class SimpleCoffee { }
public class CoffeeWithMilk { }
public class CoffeeWithMilkAndCaramel { }
public class CoffeeWithMilkAndCaramelAndCream { }
// ... infinitas combinaciones
```

---

### ✅ CON Decorator

```java
// ✅ 1. Interfaz común
public interface Coffee {
    String getDescription();
    BigDecimal getCost();
}

// ✅ 2. Componente base
public class SimpleCoffee implements Coffee {
    
    @Override
    public String getDescription() {
        return "Simple Coffee";
    }
    
    @Override
    public BigDecimal getCost() {
        return new BigDecimal("2.00");
    }
}

// ✅ 3. Decorator abstracto
public abstract class CoffeeDecorator implements Coffee {
    
    protected Coffee coffee;  // Café que vamos a decorar
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

// ✅ 4. Decorators concretos
public class MilkDecorator extends CoffeeDecorator {
    
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }
    
    @Override
    public BigDecimal getCost() {
        return coffee.getCost().add(new BigDecimal("0.50"));
    }
}

public class CaramelDecorator extends CoffeeDecorator {
    
    public CaramelDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Caramel";
    }
    
    @Override
    public BigDecimal getCost() {
        return coffee.getCost().add(new BigDecimal("0.50"));
    }
}

public class CreamDecorator extends CoffeeDecorator {
    
    public CreamDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Cream";
    }
    
    @Override
    public BigDecimal getCost() {
        return coffee.getCost().add(new BigDecimal("0.50"));
    }
}

// ✅ Uso
Coffee coffee = new SimpleCoffee();
System.out.println(coffee.getDescription() + " = $" + coffee.getCost());
// Simple Coffee = $2.00

coffee = new MilkDecorator(coffee);
System.out.println(coffee.getDescription() + " = $" + coffee.getCost());
// Simple Coffee, Milk = $2.50

coffee = new CaramelDecorator(coffee);
System.out.println(coffee.getDescription() + " = $" + coffee.getCost());
// Simple Coffee, Milk, Caramel = $3.00

coffee = new CreamDecorator(coffee);
System.out.println(coffee.getDescription() + " = $" + coffee.getCost());
// Simple Coffee, Milk, Caramel, Cream = $3.50
```

---

## 🏗️ Decorator en Spring Security

Spring Security usa Decorator extensivamente:

### ✅ Filter Chain

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // ✅ Decorator: deshabilita CSRF
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )  // ✅ Decorator: autorización
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )  // ✅ Decorator: sesiones stateless
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            // ✅ Decorator: agrega filtro JWT
        
        return http.build();
    }
}
```

Cada método es un Decorator que agrega funcionalidad.

---

## 📊 Decorator en Baby Cash

### ✅ Ejemplo: Logs en Servicios

```java
// Interfaz
public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
}

// Implementación base
@Service
@Primary
public class OrderServiceImpl implements OrderService {
    
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Lógica de creación
        return new OrderResponse();
    }
}

// ✅ Decorator: Agrega logging
@Service
public class LoggingOrderServiceDecorator implements OrderService {
    
    @Autowired
    @Qualifier("orderServiceImpl")
    private OrderService orderService;
    
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());
        
        OrderResponse response = orderService.createOrder(request);  // ✅ Delega
        
        log.info("Order created with ID: {}", response.getId());
        return response;
    }
}

// ✅ Decorator: Agrega cache
@Service
public class CachingOrderServiceDecorator implements OrderService {
    
    @Autowired
    private OrderService orderService;
    
    private Map<Long, OrderResponse> cache = new HashMap<>();
    
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        Long userId = request.getUserId();
        
        if (cache.containsKey(userId)) {
            log.info("Returning cached order for user: {}", userId);
            return cache.get(userId);
        }
        
        OrderResponse response = orderService.createOrder(request);
        cache.put(userId, response);
        
        return response;
    }
}
```

---

### ✅ Spring AOP como Decorator

```java
// ✅ Aspect = Decorator automático
@Aspect
@Component
public class LoggingAspect {
    
    // Decora TODOS los métodos de servicios con logging
    @Around("execution(* com.babycash.backend.service.*.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        
        log.info("Executing: {}", methodName);
        
        Object result = joinPoint.proceed();  // ✅ Ejecuta método original
        
        log.info("Finished: {}", methodName);
        
        return result;
    }
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es el patrón Decorator?"**

> "Es un patrón estructural que permite agregar funcionalidad a un objeto dinámicamente sin modificar su estructura. Es como envolver un regalo: cada capa agrega algo nuevo. Por ejemplo, agregar logging, caching o validación a un servicio sin modificar el servicio original."

---

**2. "¿Dónde usas Decorator en Baby Cash?"**

> "En varios lugares:
> - **Spring Security**: Filter chain que agrega funcionalidad (CSRF, autenticación, autorización)
> - **Spring AOP**: `@Aspect` que agrega logging automáticamente a todos los servicios
> - **Transacciones**: `@Transactional` decora métodos con gestión de transacciones
> 
> Cada uno agrega funcionalidad sin modificar el código original."

---

**3. "¿Cuál es la ventaja de Decorator?"**

> "Cumple Open/Closed Principle. Puedo agregar funcionalidad (logging, caching, validación) sin modificar la clase original. Además, puedo combinar decorators: `LoggingDecorator` + `CachingDecorator` + `ValidationDecorator`."

---

## 📝 Checklist de Decorator

```
✅ Interfaz común (Component)
✅ Implementación base (ConcreteComponent)
✅ Decorator abstracto (mantiene referencia al Component)
✅ Decorators concretos (agregan funcionalidad)
✅ Decorators son transparentes (misma interfaz)
```

---

## 🏆 Ventajas y Desventajas

### ✅ Ventajas

```
✅ Cumple Open/Closed Principle
✅ Agrega funcionalidad sin modificar código existente
✅ Flexible (combinar múltiples decorators)
✅ Responsabilidad única (cada decorator hace una cosa)
```

---

### ❌ Desventajas

```
❌ Muchas clases pequeñas
❌ Debugging complejo (muchas capas)
❌ Orden de decorators importa
```

---

## 🚀 Conclusión

**Decorator:**
- ✅ Agrega funcionalidad dinámicamente
- ✅ Sin modificar código existente
- ✅ Spring lo usa extensivamente

**En Baby Cash, Spring Security y AOP usan Decorator.**

---

**Ahora lee:** `PATRON-REPOSITORY.md` para el siguiente patrón. 🚀
