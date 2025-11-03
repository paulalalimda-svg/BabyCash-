# I - INTERFACE SEGREGATION PRINCIPLE (Principio de Segregación de Interfaces)

## 📚 Definición

> **"Los clientes no deberían verse forzados a depender de interfaces que no usan"**
> 
> — Robert C. Martin (Uncle Bob)

---

## 🤔 ¿Qué Significa?

### Para Principiantes (Analogía)

Imagina un **control remoto universal**:

**❌ SIN Interface Segregation:**
- UN SOLO control remoto gigante con 200 botones
- Para la TV: solo usas 10 botones
- Para el aire acondicionado: solo usas 8 botones
- Para el equipo de sonido: solo usas 15 botones
- **Tienes 200 botones pero solo usas unos pocos**
- Confuso, difícil de usar, botones innecesarios

**✅ CON Interface Segregation:**
- Control remoto de TV → Solo botones de TV
- Control remoto de Aire → Solo botones de Aire
- Control remoto de Sonido → Solo botones de Sonido
- **Cada control tiene SOLO lo que necesita**
- Simple, claro, fácil de usar

### Para Programadores

```
Es mejor tener MUCHAS interfaces pequeñas y específicas
que UNA interfaz grande y genérica
```

---

## ❌ Violando el Principio

### Ejemplo 1: Interfaz Gorda (Fat Interface)

```java
// ❌ VIOLACIÓN: Interfaz demasiado grande

public interface Worker {
    void work();
    void eat();
    void sleep();
    void getSalary();
    void payTaxes();
    void takeVacation();
    void attendMeeting();
    void submitReport();
    void receiveBenefits();
}

// Robot tiene que implementar TODOS los métodos
public class Robot implements Worker {
    @Override
    public void work() {
        System.out.println("Robot trabajando");
    }
    
    @Override
    public void eat() {
        // ❌ Los robots NO comen!
        throw new UnsupportedOperationException("Robots no comen");
    }
    
    @Override
    public void sleep() {
        // ❌ Los robots NO duermen!
        throw new UnsupportedOperationException("Robots no duermen");
    }
    
    @Override
    public void takeVacation() {
        // ❌ Los robots NO toman vacaciones!
        throw new UnsupportedOperationException("Robots no tienen vacaciones");
    }
    
    @Override
    public void receiveBenefits() {
        // ❌ Los robots NO reciben beneficios!
        throw new UnsupportedOperationException("Robots no reciben beneficios");
    }
    
    // ... resto de métodos que no aplican
}

// Empleado humano
public class HumanEmployee implements Worker {
    @Override
    public void work() {
        System.out.println("Humano trabajando");
    }
    
    @Override
    public void eat() {
        System.out.println("Humano comiendo");
    }
    
    @Override
    public void sleep() {
        System.out.println("Humano durmiendo");
    }
    
    // ... implementa TODOS, pero tiene sentido
}
```

**Problemas:**
- Robot debe implementar métodos que NO usa
- Muchas excepciones `UnsupportedOperationException`
- Interfaz confusa y difícil de mantener
- **Viola Interface Segregation** ❌

---

## ✅ Aplicando el Principio

### Solución: Interfaces Pequeñas y Específicas

```java
// ✅ CORRECTO: Interfaces segregadas (separadas)

// Interfaz base
public interface Workable {
    void work();
}

// Interfaces específicas
public interface Eatable {
    void eat();
}

public interface Sleepable {
    void sleep();
}

public interface Payable {
    void getSalary();
    void payTaxes();
}

public interface BenefitReceiver {
    void receiveBenefits();
    void takeVacation();
}

public interface MeetingAttendee {
    void attendMeeting();
}

public interface ReportSubmitter {
    void submitReport();
}

// Robot solo implementa lo que necesita
public class Robot implements Workable {
    @Override
    public void work() {
        System.out.println("Robot trabajando 24/7");
    }
    // ✅ Solo implementa work(), nada más
}

// Empleado humano implementa todo lo que necesita
public class HumanEmployee implements 
    Workable, Eatable, Sleepable, Payable, BenefitReceiver, 
    MeetingAttendee, ReportSubmitter {
    
    @Override
    public void work() {
        System.out.println("Humano trabajando");
    }
    
    @Override
    public void eat() {
        System.out.println("Humano en hora de almuerzo");
    }
    
    @Override
    public void sleep() {
        System.out.println("Humano descansando");
    }
    
    @Override
    public void getSalary() {
        System.out.println("Recibiendo salario");
    }
    
    // ... resto de métodos que SÍ usa
}

// Contratista (solo algunas interfaces)
public class Contractor implements Workable, Payable, MeetingAttendee {
    @Override
    public void work() {
        System.out.println("Contratista trabajando");
    }
    
    @Override
    public void getSalary() {
        System.out.println("Contratista facturando");
    }
    
    @Override
    public void payTaxes() {
        System.out.println("Contratista pagando impuestos como independiente");
    }
    
    @Override
    public void attendMeeting() {
        System.out.println("Contratista en reunión");
    }
    // ✅ No implementa sleep, eat, benefits (no las necesita)
}
```

**Ventajas:**
- Cada clase implementa SOLO lo que necesita
- Sin excepciones `UnsupportedOperationException`
- Interfaces claras y específicas
- **Cumple Interface Segregation** ✅

---

## 🏢 Ejemplos Reales de Baby Cash

### Ejemplo 1: Entidades con Diferentes Capacidades

```java
// ❌ VIOLACIÓN: Una interfaz para todo

public interface Entity {
    Long getId();
    void setId(Long id);
    
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);
    
    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);
    
    String getCreatedBy();
    void setCreatedBy(String createdBy);
    
    String getUpdatedBy();
    void setUpdatedBy(String updatedBy);
    
    boolean isActive();
    void setActive(boolean active);
    
    boolean isDeleted();
    void setDeleted(boolean deleted);
}

// ❌ Product debe implementar TODO, aunque no use audit
public class Product implements Entity {
    // Debe tener createdBy, updatedBy aunque no los use
}
```

### ✅ Solución Correcta

```java
// ✅ CORRECTO: Interfaces segregadas

// Interfaz base mínima
public interface Identifiable {
    Long getId();
    void setId(Long id);
}

// Capacidad de timestamps
public interface Timestamped {
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);
    
    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);
}

// Capacidad de auditoría
public interface Auditable {
    String getCreatedBy();
    void setCreatedBy(String createdBy);
    
    String getUpdatedBy();
    void setUpdatedBy(String updatedBy);
}

// Capacidad de soft delete
public interface SoftDeletable {
    boolean isDeleted();
    void setDeleted(boolean deleted);
    LocalDateTime getDeletedAt();
}

// Capacidad de activación/desactivación
public interface Activatable {
    boolean isActive();
    void setActive(boolean active);
}

// Ahora las entidades implementan solo lo que necesitan

@Entity
public class Product implements Identifiable, Timestamped, Activatable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    
    // ✅ Solo implementa lo que necesita
    // NO tiene auditoría ni soft delete
}

@Entity
public class Order implements Identifiable, Timestamped, Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    // ✅ Necesita auditoría pero NO soft delete
}

@Entity
public class User implements Identifiable, Timestamped, Auditable, SoftDeletable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private boolean deleted;
    private LocalDateTime deletedAt;
    
    // ✅ Implementa todo porque lo necesita
}
```

---

### Ejemplo 2: Repositorios con Diferentes Operaciones

```java
// ❌ VIOLACIÓN: Todos los repos tienen TODAS las operaciones

public interface CrudRepository<T, ID> {
    T save(T entity);
    T findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    void deleteAll(); // ❌ Peligroso!
    void updateById(ID id, T entity);
    long count();
    boolean existsById(ID id);
    // ... 20+ métodos más
}

// ❌ ReadOnlyRepository debe tener métodos que NO usa
public interface ProductStatisticsRepository extends CrudRepository<ProductStats, Long> {
    // Esta tabla es READ-ONLY (solo lectura)
    // ❌ Pero debe implementar save(), delete(), etc.
}
```

### ✅ Solución Correcta

```java
// ✅ CORRECTO: Interfaces segregadas por capacidades

// Interfaz de lectura
public interface ReadRepository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    long count();
    boolean existsById(ID id);
}

// Interfaz de escritura
public interface WriteRepository<T, ID> {
    T save(T entity);
    void deleteById(ID id);
}

// Interfaz de búsqueda
public interface SearchRepository<T> {
    List<T> search(String query);
    List<T> findByFilters(Map<String, Object> filters);
}

// Interfaz de paginación
public interface PageableRepository<T> {
    Page<T> findAll(Pageable pageable);
}

// Interfaces completas para diferentes casos

// 1. Repositorio completo (CRUD)
public interface ProductRepository extends 
    ReadRepository<Product, Long>,
    WriteRepository<Product, Long>,
    SearchRepository<Product>,
    PageableRepository<Product> {
    
    // ✅ Tiene todas las capacidades
    List<Product> findByCategory(String category);
}

// 2. Repositorio de solo lectura
public interface ProductStatisticsRepository extends 
    ReadRepository<ProductStats, Long> {
    
    // ✅ Solo lectura, NO puede escribir
    List<ProductStats> findTopSelling();
}

// 3. Repositorio sin delete
public interface AuditLogRepository extends 
    ReadRepository<AuditLog, Long>,
    WriteRepository<AuditLog, Long>,
    PageableRepository<AuditLog> {
    
    // ✅ Puede leer y escribir, pero NO borrar (audit trail)
    // NO implementa deleteById()
}
```

---

### Ejemplo 3: Servicios de Notificación

```java
// ❌ VIOLACIÓN: Una interfaz para todo tipo de notificación

public interface NotificationService {
    void sendEmail(String to, String subject, String body);
    void sendSms(String phone, String message);
    void sendPush(String deviceToken, String notification);
    void sendWhatsApp(String phone, String message);
    void sendSlack(String channel, String message);
    void sendTelegram(String chatId, String message);
}

// ❌ EmailOnlyService debe implementar TODO
public class EmailOnlyService implements NotificationService {
    @Override
    public void sendEmail(String to, String subject, String body) {
        // ✅ Implementado
    }
    
    @Override
    public void sendSms(String phone, String message) {
        // ❌ No tengo servicio de SMS
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void sendPush(String deviceToken, String notification) {
        // ❌ No tengo servicio de Push
        throw new UnsupportedOperationException();
    }
    
    // ... resto de métodos que NO usa
}
```

### ✅ Solución Correcta

```java
// ✅ CORRECTO: Interfaces separadas

public interface EmailNotifier {
    void sendEmail(String to, String subject, String body);
}

public interface SmsNotifier {
    void sendSms(String phone, String message);
}

public interface PushNotifier {
    void sendPush(String deviceToken, String notification);
}

public interface WhatsAppNotifier {
    void sendWhatsApp(String phone, String message);
}

// Implementaciones específicas

@Service
public class EmailService implements EmailNotifier {
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void sendEmail(String to, String subject, String body) {
        // ✅ Solo implementa email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}

@Service
public class TwilioService implements SmsNotifier, WhatsAppNotifier {
    @Autowired
    private TwilioClient twilioClient;
    
    @Override
    public void sendSms(String phone, String message) {
        // ✅ Twilio puede SMS
        twilioClient.sendSms(phone, message);
    }
    
    @Override
    public void sendWhatsApp(String phone, String message) {
        // ✅ Twilio también puede WhatsApp
        twilioClient.sendWhatsAppMessage(phone, message);
    }
}

@Service
public class FirebaseService implements PushNotifier {
    @Autowired
    private FirebaseMessaging firebaseMessaging;
    
    @Override
    public void sendPush(String deviceToken, String notification) {
        // ✅ Solo implementa push notifications
        Message message = Message.builder()
            .setToken(deviceToken)
            .setNotification(Notification.builder()
                .setBody(notification)
                .build())
            .build();
        firebaseMessaging.send(message);
    }
}

// Servicio coordinador que usa las interfaces
@Service
public class NotificationCoordinator {
    @Autowired(required = false)
    private EmailNotifier emailNotifier;
    
    @Autowired(required = false)
    private SmsNotifier smsNotifier;
    
    @Autowired(required = false)
    private PushNotifier pushNotifier;
    
    public void notifyUser(User user, String message) {
        // Usa solo los servicios disponibles
        if (emailNotifier != null && user.getEmail() != null) {
            emailNotifier.sendEmail(user.getEmail(), "Notificación", message);
        }
        
        if (smsNotifier != null && user.getPhone() != null) {
            smsNotifier.sendSms(user.getPhone(), message);
        }
        
        if (pushNotifier != null && user.getDeviceToken() != null) {
            pushNotifier.sendPush(user.getDeviceToken(), message);
        }
    }
}
```

---

## 🎯 Cómo Identificar Violaciones

### Señales de Alerta 🚨

#### 1. Métodos que Lanzan UnsupportedOperationException
```java
// ❌ Señal clara de violación
@Override
public void deleteAll() {
    throw new UnsupportedOperationException("Esta operación no está soportada");
}
```

#### 2. Implementaciones Vacías
```java
// ❌ Método que no hace nada
@Override
public void audit() {
    // No hace nada, esta clase no necesita auditoría
}
```

#### 3. Interfaces con Muchos Métodos
```java
// ❌ Interfaz con 20+ métodos
public interface SuperService {
    // 30 métodos diferentes
}
```

#### 4. Implementaciones Parciales
```java
// ❌ Solo usa 3 de 15 métodos
public class MyClass implements BigInterface {
    // Implementa 3 métodos que usa
    // Implementa 12 métodos que NO usa con throws/empty
}
```

---

## ✅ Buenas Prácticas

### 1. Interfaces Pequeñas y Cohesivas

```java
// ✅ Cada interfaz tiene un propósito claro
public interface Searchable {
    List<Product> search(String query);
}

public interface Filterable {
    List<Product> filter(Map<String, Object> filters);
}

public interface Sortable {
    List<Product> sort(String field, String order);
}
```

### 2. Composición de Interfaces

```java
// ✅ Componer interfaces pequeñas
public interface AdvancedSearchRepository extends 
    Searchable, Filterable, Sortable, Pageable {
    // Hereda todos los métodos de las interfaces pequeñas
}
```

### 3. Interfaces de Rol

```java
// ✅ Interfaces según el ROL
public interface EmailSender {
    void send(Email email);
}

public interface EmailValidator {
    boolean isValid(String email);
}

public interface EmailFormatter {
    String format(EmailTemplate template, Map<String, Object> data);
}

// Servicio completo compone los roles
@Service
public class EmailService implements EmailSender, EmailValidator, EmailFormatter {
    // Implementa todos porque los necesita
}
```

---

## 🧪 Testing Más Fácil

```java
// ✅ Mockear solo lo que necesitas

@Test
public void testEmailNotification() {
    // Solo necesitas mockear EmailNotifier
    EmailNotifier emailNotifier = mock(EmailNotifier.class);
    
    NotificationService service = new NotificationService(emailNotifier);
    service.notifyByEmail("test@example.com", "Hello");
    
    verify(emailNotifier).sendEmail(eq("test@example.com"), any(), any());
    // ✅ No necesitas mockear SMS, Push, etc.
}
```

---

## 🎓 Para la Evaluación del SENA

### Pregunta: "¿Por qué tantas interfaces pequeñas?"

**Respuesta:**
> "Apliqué el Principio de Segregación de Interfaces. En lugar de una interfaz grande que fuerce a implementar métodos innecesarios, creé interfaces pequeñas y específicas. Por ejemplo:
> - `EmailNotifier` solo para envío de emails
> - `SmsNotifier` solo para SMS
> - `PushNotifier` solo para notificaciones push
> 
> Cada servicio implementa solo las interfaces que necesita, evitando métodos vacíos o excepciones UnsupportedOperationException."

---

## 📝 Resumen

```
Interface Segregation Principle (ISP)

Regla:
"No fuerces a los clientes a depender de métodos que no usan"

Cómo Aplicar:
✅ Interfaces pequeñas y específicas
✅ Múltiples interfaces > Una interfaz grande
✅ Composición de interfaces
✅ Interfaces por capacidad/rol

Qué Evitar:
❌ Interfaces con 10+ métodos
❌ throw UnsupportedOperationException
❌ Implementaciones vacías
❌ Métodos que no se usan

Beneficios:
✅ Clases más simples
✅ Tests más fáciles
✅ Código más flexible
✅ Menos acoplamiento

En Baby Cash:
✅ Identifiable, Timestamped, Auditable separados
✅ EmailNotifier, SmsNotifier, PushNotifier
✅ ReadRepository, WriteRepository separados
```

---

**Siguiente:** Lee `D-DEPENDENCY-INVERSION.md` 🚀
