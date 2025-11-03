# PATRÓN SINGLETON

## 🎯 Definición

**Singleton** asegura que una clase tenga **UNA SOLA INSTANCIA** en toda la aplicación, y proporciona un punto de acceso global a ella.

---

## ❓ ¿Para Qué Sirve?

### Imagina esto:

Tienes una aplicación con 100 servicios. Cada uno necesita configuración de base de datos.

```
❌ SIN SINGLETON:
100 objetos DatabaseConfig diferentes
Desperdicio de memoria
Configuraciones inconsistentes

✅ CON SINGLETON:
1 solo objeto DatabaseConfig
Todos lo comparten
Consistencia garantizada
```

---

## 🏗️ Implementación Clásica

### ❌ SIN Singleton (Problema)

```java
public class DatabaseConfig {
    private String url;
    private String username;
    
    public DatabaseConfig() {
        this.url = "jdbc:postgresql://localhost:5432/babycash";
        this.username = "admin";
    }
}

// Uso
DatabaseConfig config1 = new DatabaseConfig();  // Nueva instancia
DatabaseConfig config2 = new DatabaseConfig();  // Otra instancia
DatabaseConfig config3 = new DatabaseConfig();  // Otra más

// Problema: 3 objetos diferentes, desperdicio de memoria
```

---

### ✅ CON Singleton

```java
public class DatabaseConfig {
    
    // ✅ 1. Variable estática que contiene la única instancia
    private static DatabaseConfig instance;
    
    private String url;
    private String username;
    
    // ✅ 2. Constructor privado (no se puede crear desde fuera)
    private DatabaseConfig() {
        this.url = "jdbc:postgresql://localhost:5432/babycash";
        this.username = "admin";
    }
    
    // ✅ 3. Método público para obtener la instancia
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    // Getters
    public String getUrl() {
        return url;
    }
    
    public String getUsername() {
        return username;
    }
}

// Uso
DatabaseConfig config1 = DatabaseConfig.getInstance();
DatabaseConfig config2 = DatabaseConfig.getInstance();
DatabaseConfig config3 = DatabaseConfig.getInstance();

// ✅ config1, config2 y config3 son EL MISMO objeto
```

---

## 🔐 Singleton Thread-Safe

Si múltiples hilos acceden al Singleton, puede haber problemas:

### ❌ NO Thread-Safe

```java
public static DatabaseConfig getInstance() {
    if (instance == null) {  // ❌ Dos hilos pueden entrar aquí simultáneamente
        instance = new DatabaseConfig();
    }
    return instance;
}
```

---

### ✅ Thread-Safe (Double-Check Locking)

```java
public class DatabaseConfig {
    
    // ✅ volatile asegura visibilidad entre hilos
    private static volatile DatabaseConfig instance;
    
    private DatabaseConfig() { }
    
    public static DatabaseConfig getInstance() {
        if (instance == null) {  // Primera verificación (sin lock)
            synchronized (DatabaseConfig.class) {  // Lock
                if (instance == null) {  // Segunda verificación (con lock)
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }
}
```

---

### ✅ MEJOR: Enum Singleton (Recomendado por Joshua Bloch)

```java
public enum DatabaseConfig {
    INSTANCE;
    
    private String url;
    private String username;
    
    DatabaseConfig() {
        this.url = "jdbc:postgresql://localhost:5432/babycash";
        this.username = "admin";
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getUsername() {
        return username;
    }
}

// Uso
DatabaseConfig config = DatabaseConfig.INSTANCE;
```

**Ventajas:**
- ✅ Thread-safe automáticamente
- ✅ Protección contra serialización
- ✅ Más simple

---

## 🏗️ Singleton en Spring Boot

Spring Boot hace Singleton **automáticamente**:

### ✅ Spring Bean = Singleton por Defecto

```java
@Service  // ✅ Spring crea UNA sola instancia
public class ProductService {
    // ...
}

@Configuration
public class AppConfig {
    
    @Bean  // ✅ Este bean es Singleton
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Spring maneja:**
- ✅ Creación de UNA instancia
- ✅ Thread-safety
- ✅ Ciclo de vida
- ✅ Inyección de dependencias

---

### Baby Cash: Todos los @Service son Singleton

```java
// ✅ Spring crea UNA instancia de ProductService
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    
    public List<ProductResponse> getAllProducts() {
        // ...
    }
}

// ✅ Spring crea UNA instancia de OrderService
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    
    public OrderResponse createOrder(CreateOrderRequest request) {
        // ...
    }
}

// ✅ Spring crea UNA instancia de EmailService
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    
    public void sendEmail(String to, String subject, String body) {
        // ...
    }
}
```

**Todos comparten la MISMA instancia de cada servicio.**

---

## 📊 Cuándo Usar Singleton

### ✅ Casos de Uso

```
✅ Configuración de aplicación (DatabaseConfig, AppConfig)
✅ Gestión de conexiones (ConnectionPool)
✅ Caché (CacheManager)
✅ Logger (Log4j, SLF4J)
✅ Servicios sin estado (ProductService, OrderService)
```

---

### ❌ Cuándo NO Usar

```
❌ Objetos con estado mutable compartido (race conditions)
❌ DTOs (cada request necesita su propio DTO)
❌ Entities (cada registro es una instancia diferente)
❌ Controllers con estado (Spring los hace Singleton, pero sin estado)
```

---

## 🚫 Anti-Patrón: Singleton con Estado Mutable

### ❌ MAL (Race Condition)

```java
@Service  // ✅ Singleton
public class OrderService {
    
    // ❌ Estado mutable compartido entre todos los requests
    private int orderCount = 0;
    
    public void createOrder(Order order) {
        orderCount++;  // ❌ PELIGRO: múltiples hilos modifican esto
        System.out.println("Order #" + orderCount);
        // ...
    }
}
```

**Problema:** Si 2 requests llaman `createOrder()` simultáneamente:
- Request 1: lee `orderCount = 0`, incrementa a 1
- Request 2: lee `orderCount = 0` (antes de que Request 1 termine), incrementa a 1
- **Resultado:** Ambos piensan que son orden #1

---

### ✅ BIEN (Sin Estado o Thread-Safe)

```java
@Service  // ✅ Singleton
public class OrderService {
    
    // ✅ Sin estado mutable
    private final OrderRepository orderRepository;
    
    public void createOrder(Order order) {
        // ✅ Usa base de datos (thread-safe)
        order = orderRepository.save(order);
        System.out.println("Order #" + order.getId());
    }
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es el patrón Singleton?"**

> "Es un patrón creacional que asegura que una clase tenga UNA SOLA INSTANCIA en toda la aplicación, y proporciona acceso global a ella. Se usa para configuración, gestión de recursos, servicios sin estado."

---

**2. "¿Cómo implementas Singleton en Java?"**

> "Hay varias formas:
> 1. Clásica: Constructor privado + método `getInstance()` estático
> 2. Thread-safe: Double-check locking con `synchronized`
> 3. Enum: Más simple y seguro (recomendado por Joshua Bloch)
> 4. En Spring: `@Service`, `@Component`, `@Bean` (automático)"

---

**3. "¿Dónde usas Singleton en Baby Cash?"**

> "En todos los servicios:
> - `ProductService`: UNA instancia para gestionar productos
> - `OrderService`: UNA instancia para gestionar órdenes
> - `EmailService`: UNA instancia para enviar emails
> 
> Spring Boot los hace Singleton automáticamente con `@Service`. Todos los controllers, services y repositories son Singleton."

---

**4. "¿Cuál es el problema con Singleton?"**

> "El problema principal es el estado mutable compartido. Si un Singleton tiene variables que cambian y múltiples hilos las modifican, puede haber race conditions. Por eso en Baby Cash, los servicios NO tienen estado mutable. Solo tienen dependencias finales (inmutables)."

---

## 📝 Checklist de Singleton

```
✅ Constructor privado (no se puede crear desde fuera)
✅ Variable estática para guardar la instancia
✅ Método estático getInstance() para acceder
✅ Thread-safe si hay concurrencia
✅ Sin estado mutable compartido
✅ En Spring: usa @Service, @Component, @Bean
```

---

## 🏆 Ventajas y Desventajas

### ✅ Ventajas

```
✅ Ahorra memoria (una sola instancia)
✅ Acceso global
✅ Inicialización diferida (lazy initialization)
✅ Thread-safe (con implementación correcta)
```

---

### ❌ Desventajas

```
❌ Dificulta testing (acoplamiento global)
❌ Puede ocultar dependencias
❌ Estado global puede causar problemas
❌ Difícil de extender
```

---

## 📈 Evolución del Patrón

### Nivel 1: Singleton Clásico

```java
public class Singleton {
    private static Singleton instance;
    
    private Singleton() { }
    
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

---

### Nivel 2: Thread-Safe

```java
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() { }
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

---

### Nivel 3: Enum (Moderno)

```java
public enum Singleton {
    INSTANCE;
    
    public void doSomething() {
        // ...
    }
}
```

---

### Nivel 4: Spring (Recomendado)

```java
@Service
public class MyService {
    // Spring maneja Singleton automáticamente
}
```

---

## 🚀 Conclusión

**Singleton:**
- ✅ Una sola instancia
- ✅ Acceso global
- ✅ Ahorra memoria
- ✅ Spring lo hace automáticamente

**En Baby Cash, todos los @Service son Singleton.**

---

**Ahora lee:** `PATRON-FACTORY.md` para el siguiente patrón. 🚀
