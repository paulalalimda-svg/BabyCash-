# ¿QUÉ SON LOS PATRONES DE DISEÑO?

## 🎯 Definición

**Patrones de Diseño** son **soluciones probadas** a problemas comunes en desarrollo de software.

Son como **recetas de cocina** para programadores.

---

## 📚 Origen: Gang of Four (GoF)

En 1994, cuatro autores publicaron el libro:
**"Design Patterns: Elements of Reusable Object-Oriented Software"**

**Autores (Gang of Four):**
- Erich Gamma
- Richard Helm
- Ralph Johnson
- John Vlissides

Este libro definió **23 patrones de diseño clásicos**.

---

## ❓ ¿Por Qué Usar Patrones?

### Imagina esto:

Estás construyendo una casa. Necesitas una puerta.

```
❌ SIN PATRÓN:
Inventas cómo hacer una puerta desde cero.
Pruebas diferentes diseños.
Cometes errores.
Tardas meses.

✅ CON PATRÓN:
Usas el diseño estándar de puertas.
Ya está probado y funciona.
Tardas días.
```

**Lo mismo en software.**

---

## 🎨 Tipos de Patrones

Los patrones se dividen en **3 categorías**:

### 1️⃣ **Patrones Creacionales** (Cómo crear objetos)

```
Singleton     → Solo UNA instancia
Factory       → Crea objetos sin exponer lógica
Builder       → Construye objetos complejos paso a paso
Prototype     → Clona objetos existentes
Abstract Factory → Familias de objetos relacionados
```

---

### 2️⃣ **Patrones Estructurales** (Cómo organizar clases)

```
Adapter       → Adapta una interfaz a otra
Decorator     → Agrega funcionalidad dinámicamente
Proxy         → Controla acceso a un objeto
Composite     → Árbol de objetos
Facade        → Interfaz simplificada
Bridge        → Separa abstracción de implementación
Flyweight     → Comparte objetos para ahorrar memoria
```

---

### 3️⃣ **Patrones de Comportamiento** (Cómo interactúan objetos)

```
Strategy      → Familia de algoritmos intercambiables
Observer      → Notifica cambios a múltiples objetos
Command       → Encapsula una acción como objeto
Template Method → Define estructura, subclases implementan pasos
Iterator      → Recorre colecciones
State         → Cambia comportamiento según estado
Chain of Responsibility → Cadena de manejadores
Mediator      → Centraliza comunicación
Memento       → Guarda y restaura estado
Visitor       → Operaciones sobre estructura de objetos
Interpreter   → Interpreta lenguaje o expresiones
```

---

## 🏗️ Patrones en Baby Cash

Baby Cash usa **principalmente estos patrones**:

### ✅ Patrones Usados

```
1. Singleton          → Spring Beans (una instancia por servicio)
2. Factory            → Spring Factory para beans
3. Builder            → Lombok @Builder para DTOs
4. Strategy           → Descuentos, pagos (interfaces con múltiples implementaciones)
5. Observer           → Spring Events (eventos de dominio)
6. Decorator          → Spring Security (filtros)
7. Repository         → JpaRepository (acceso a datos)
8. DTO (Data Transfer Object) → Separación de entities y responses
9. Dependency Injection → @Autowired, @RequiredArgsConstructor
10. MVC (Model-View-Controller) → Arquitectura del proyecto
11. Template Method   → JpaRepository (métodos base)
12. Proxy             → Spring AOP (transacciones, seguridad)
```

---

## 🎓 ¿Para Qué Sirven?

### 1. **Comunicación**

```
Decir "uso el patrón Singleton" es más claro que:
"Tengo una clase que solo permite crear una instancia y la reutiliza en todo el proyecto"
```

---

### 2. **Soluciones Probadas**

```
Los patrones ya fueron probados por millones de desarrolladores.
No reinventas la rueda.
```

---

### 3. **Mantenibilidad**

```
Código con patrones es más fácil de entender y mantener.
```

---

### 4. **Escalabilidad**

```
Patrones facilitan agregar funcionalidades sin romper código existente.
```

---

## 📖 Ejemplo Simple: Singleton

### ❌ SIN PATRÓN

```java
// Problema: Múltiples instancias de configuración
public class DatabaseConfig {
    private String url;
    private String username;
    
    public DatabaseConfig() {
        // Se crea cada vez
    }
}

// Uso
DatabaseConfig config1 = new DatabaseConfig();  // Nueva instancia
DatabaseConfig config2 = new DatabaseConfig();  // Otra instancia
// config1 y config2 son diferentes objetos
```

---

### ✅ CON PATRÓN SINGLETON

```java
public class DatabaseConfig {
    
    // ✅ Solo UNA instancia
    private static DatabaseConfig instance;
    
    private String url;
    private String username;
    
    // ✅ Constructor privado (no se puede crear desde fuera)
    private DatabaseConfig() {
        this.url = "jdbc:postgresql://localhost:5432/babycash";
        this.username = "admin";
    }
    
    // ✅ Método para obtener la única instancia
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
}

// Uso
DatabaseConfig config1 = DatabaseConfig.getInstance();
DatabaseConfig config2 = DatabaseConfig.getInstance();
// config1 y config2 son EL MISMO objeto ✅
```

---

## 🚀 Patrones en Spring Boot

Spring Boot usa patrones **automáticamente**:

### ✅ Singleton

```java
@Service  // ✅ Spring crea UNA sola instancia
public class ProductService {
    // ...
}
```

---

### ✅ Factory

```java
// ✅ Spring Factory crea beans automáticamente
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

### ✅ Dependency Injection

```java
@Service
@RequiredArgsConstructor  // ✅ Inyecta dependencias automáticamente
public class OrderService {
    private final OrderRepository orderRepository;
}
```

---

### ✅ Repository

```java
// ✅ Spring Data JPA implementa el patrón Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

---

### ✅ MVC

```java
// ✅ Arquitectura MVC
@RestController  // Controller
public class ProductController {
    
    @GetMapping("/api/products")
    public List<ProductResponse> getAllProducts() {  // View (JSON)
        return productService.getAllProducts();  // Model (Service + Entity)
    }
}
```

---

## 📊 Beneficios de Patrones

### 1. **Código Reutilizable**

```
Un patrón resuelve un problema.
Puedes usarlo en múltiples proyectos.
```

---

### 2. **Fácil de Entender**

```
Desarrolladores conocen los patrones.
Ven "Singleton" y saben qué significa.
```

---

### 3. **Mantenible**

```
Patrones hacen el código más organizado.
```

---

### 4. **Escalable**

```
Fácil agregar funcionalidades.
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué son los patrones de diseño?"**

> "Son soluciones probadas a problemas comunes en desarrollo de software. Como recetas de cocina para programadores. Fueron documentados por el Gang of Four en 1994 y se dividen en 3 categorías: Creacionales (cómo crear objetos), Estructurales (cómo organizarlos) y de Comportamiento (cómo interactúan)."

---

**2. "¿Qué patrones usas en Baby Cash?"**

> "Uso principalmente:
> - **Singleton**: Spring Beans (una instancia por servicio)
> - **Repository**: JpaRepository para acceso a datos
> - **DTO**: Separación de entities y responses
> - **Dependency Injection**: @RequiredArgsConstructor
> - **MVC**: Arquitectura Controller-Service-Repository
> - **Strategy**: Descuentos y pagos con interfaces
> - **Factory**: Spring crea beans automáticamente"

---

**3. "¿Por qué usar patrones?"**

> "Porque son soluciones probadas. No reinvento la rueda. Si necesito un Singleton, uso el patrón Singleton. Si necesito acceso a datos, uso Repository. Los patrones hacen el código más mantenible, escalable y fácil de entender."

---

**4. "¿Dónde aprendiste patrones?"**

> "En el libro 'Design Patterns' del Gang of Four, documentación de Spring Boot, y aplicándolos en proyectos. Spring Boot usa muchos patrones automáticamente, así que al usar Spring ya estás aplicando patrones sin darte cuenta."

---

## 📝 Checklist de Patrones

```
✅ Conoces los 23 patrones del GoF
✅ Entiendes las 3 categorías (Creacionales, Estructurales, Comportamiento)
✅ Identificas patrones en tu código
✅ Usas patrones apropiadamente (no over-engineering)
✅ Conoces patrones específicos de Spring (DI, MVC, Repository)
```

---

## 🏆 Patrones Más Usados

### Top 10 en Desarrollo Web:

```
1. Singleton          → Servicios, configuración
2. Factory            → Creación de objetos complejos
3. Repository         → Acceso a datos
4. DTO                → Transferencia de datos
5. Dependency Injection → Desacoplamiento
6. MVC                → Arquitectura web
7. Strategy           → Algoritmos intercambiables
8. Observer           → Eventos y notificaciones
9. Decorator          → Funcionalidad dinámica
10. Builder           → Construcción de objetos complejos
```

---

## 📈 Niveles de Conocimiento

### Nivel 1: Principiante 🟡

```
Conoces algunos patrones de nombre.
Usas patrones que vienen con frameworks (Spring).
```

---

### Nivel 2: Intermedio 🟠

```
Identificas patrones en código existente.
Aplicas patrones conscientemente.
Conoces 10-15 patrones.
```

---

### Nivel 3: Avanzado 🟢

```
Conoces los 23 patrones del GoF.
Decides qué patrón usar en cada situación.
Combinas patrones apropiadamente.
No haces over-engineering.
```

---

## 🚀 Conclusión

**Patrones de Diseño:**
- ✅ Soluciones probadas
- ✅ Facilitan comunicación
- ✅ Hacen código mantenible
- ✅ Son estándar de la industria

**Baby Cash usa patrones modernos de Spring Boot.**

---

## 📚 Próximos Pasos

Lee los siguientes archivos para entender cada patrón:

1. `PATRON-SINGLETON.md` → Una instancia
2. `PATRON-FACTORY.md` → Creación de objetos
3. `PATRON-BUILDER.md` → Construcción paso a paso
4. `PATRON-STRATEGY.md` → Algoritmos intercambiables
5. `PATRON-OBSERVER.md` → Eventos y notificaciones
6. `PATRON-DECORATOR.md` → Funcionalidad dinámica
7. `PATRON-REPOSITORY.md` → Acceso a datos
8. `PATRON-DTO.md` → Transferencia de datos
9. `PATRON-DEPENDENCY-INJECTION.md` → Desacoplamiento
10. `PATRON-MVC.md` → Arquitectura web
11. `PATRONES-EN-BABYCASH.md` → Análisis completo del proyecto

---

**¡Empieza con `PATRON-SINGLETON.md`!** 🚀
