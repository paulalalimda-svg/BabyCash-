# 📚 Conceptos Técnicos Fundamentales - Baby Cash

Documento técnico con conceptos, tecnologías y herramientas utilizadas en el proyecto Baby Cash. Cada concepto incluye una descripción breve para entender su propósito y justificar su uso.

---

## 📋 Tabla de Contenidos

1. [Backend - Java y Spring Boot](#backend---java-y-spring-boot)
2. [Frontend - React y TypeScript](#frontend---react-y-typescript)
3. [Base de Datos - PostgreSQL](#base-de-datos---postgresql)
4. [Herramientas de Desarrollo](#herramientas-de-desarrollo)
5. [Control de Versiones y CI/CD](#control-de-versiones-y-cicd)

---

## 🔧 Backend - Java y Spring Boot

### Java

**¿Qué es Java?**
Java es un lenguaje de programación de propósito general, orientado a objetos y de alto nivel. Fue creado por Sun Microsystems (ahora Oracle) en 1995 y sigue siendo uno de los lenguajes más populares del mundo.

**Características principales:**

- **Multiplataforma**: "Write Once, Run Anywhere" (WORA)
- **Orientado a objetos**: Basado en clases y objetos
- **Tipado estático**: Los tipos de datos se verifican en tiempo de compilación
- **Gestión automática de memoria**: Garbage Collector elimina objetos no utilizados
- **Seguro**: Sistema de seguridad robusto incorporado

**¿Por qué usar Java en Baby Cash?**

- Estabilidad y madurez para aplicaciones empresariales
- Gran ecosistema de librerías y frameworks (Spring Boot)
- Excelente rendimiento para e-commerce
- Comunidad grande y soporte a largo plazo

---

### JVM (Java Virtual Machine)

**¿Qué es la JVM?**
La Java Virtual Machine es una máquina virtual que ejecuta código Java compilado (bytecode). Es el corazón del principio "Write Once, Run Anywhere" de Java.

**¿Cómo funciona?**

1. **Código fuente** (.java) → Escrito por el programador
2. **Compilación** → Compilador `javac` convierte a bytecode (.class)
3. **JVM** → Interpreta/ejecuta el bytecode en cualquier plataforma
4. **JIT Compiler** → Optimiza bytecode a código máquina en tiempo de ejecución

**Componentes de la JVM:**

- **Class Loader**: Carga las clases en memoria
- **Memory Areas**: Heap, Stack, Method Area, etc.
- **Execution Engine**: Ejecuta el bytecode
- **Garbage Collector**: Limpia memoria no utilizada

**Beneficio para Baby Cash:**

- Portabilidad: El mismo código funciona en Linux, Windows, macOS
- Rendimiento: JIT optimiza el código durante la ejecución
- Seguridad: Sandbox que aísla la aplicación del sistema operativo

---

### Lenguaje Compilado vs Interpretado

**Lenguaje Compilado:**
El código fuente se traduce completamente a código máquina antes de ejecutarse.

- **Ejemplos**: C, C++, Rust
- **Ventajas**: Ejecución muy rápida, detección temprana de errores
- **Desventajas**: Necesita recompilación para cada plataforma

**Lenguaje Interpretado:**
El código se traduce y ejecuta línea por línea en tiempo de ejecución.

- **Ejemplos**: Python, JavaScript (antes de JIT), Ruby
- **Ventajas**: Multiplataforma sin recompilar, desarrollo rápido
- **Desventajas**: Más lento que código compilado

**Java: Híbrido (Compilado + Interpretado)**

1. **Compilado a bytecode**: `javac` compila .java → .class (bytecode)
2. **Interpretado por JVM**: JVM interpreta bytecode
3. **JIT Compilation**: Durante ejecución, JIT compila bytecode → código máquina nativo

**Resultado**: Java combina lo mejor de ambos mundos:

- Portabilidad (bytecode multiplataforma)
- Rendimiento (JIT optimiza a código nativo)

---

### Lenguaje de Alto Nivel

**¿Qué es un lenguaje de alto nivel?**
Un lenguaje de programación que abstrae los detalles de bajo nivel del hardware, permitiendo escribir código más cercano al lenguaje humano.

**Niveles de lenguajes:**

| Nivel           | Tipo                 | Ejemplos                 | Características                      |
| --------------- | -------------------- | ------------------------ | ------------------------------------ |
| **Bajo nivel**  | Lenguaje máquina     | Binario (0s y 1s)        | Directamente ejecutado por CPU       |
| **Bajo nivel**  | Ensamblador          | Assembly                 | Mnemónicos para instrucciones de CPU |
| **Medio nivel** | Lenguajes como C     | C, C++                   | Control de memoria + abstracción     |
| **Alto nivel**  | Abstracción completa | Java, Python, JavaScript | Gestión automática de memoria        |

**Java como lenguaje de alto nivel:**

- **Abstracción de memoria**: No necesitas malloc/free (Garbage Collector)
- **Sintaxis legible**: `String nombre = "Baby Cash";` vs ensamblador
- **Estructuras complejas**: Clases, interfaces, herencia, polimorfismo
- **Librerías estándar**: API rica para operaciones comunes

**Ventaja en Baby Cash:**
Desarrollo más rápido y mantenible sin sacrificar rendimiento.

---

### Maven

**¿Qué es Maven?**
Maven es una herramienta de gestión y construcción de proyectos Java. Automatiza la compilación, gestión de dependencias, testing y empaquetado.

**Funciones principales:**

- **Gestión de dependencias**: Descarga automática de librerías desde repositorios
- **Build lifecycle**: Define fases estándar (compile, test, package, install)
- **Estructura de proyecto**: Convención sobre configuración
- **Plugins**: Extensible con plugins para diversas tareas

**¿Por qué Maven en Baby Cash?**

- Gestiona Spring Boot y todas sus dependencias automáticamente
- Estándar en la industria para proyectos Java
- Reproducibilidad: Mismo resultado en cualquier máquina
- Integración con IDEs (IntelliJ, Eclipse, VS Code)

**Comandos Maven usados:**

```bash
./mvnw clean install    # Compilar y empaquetar
./mvnw spring-boot:run  # Ejecutar aplicación
./mvnw test             # Ejecutar tests
./mvnw package          # Crear JAR/WAR
```

---

### pom.xml (Project Object Model)

**¿Qué es pom.xml?**
Es el archivo de configuración central de Maven. Define la estructura del proyecto, dependencias, plugins y configuración de build.

**Estructura del pom.xml:**

```xml
<project>
  <!-- Información del proyecto -->
  <groupId>com.babycash</groupId>          <!-- Organización -->
  <artifactId>backend</artifactId>         <!-- Nombre del proyecto -->
  <version>1.0.0</version>                 <!-- Versión -->

  <!-- Propiedades -->
  <properties>
    <java.version>21</java.version>        <!-- Versión de Java -->
    <spring.version>3.5.7</spring.version> <!-- Versión de Spring -->
  </properties>

  <!-- Dependencias -->
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
  </dependencies>

  <!-- Plugins -->
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

**Secciones importantes en Baby Cash:**

- **Dependencies**: Spring Boot, PostgreSQL JDBC, JWT, Lombok, etc.
- **Parent POM**: Hereda de `spring-boot-starter-parent`
- **Plugins**: Checkstyle (linting), Spotless (formatting), Spring Boot
- **Profiles**: Configuraciones para dev/prod

**Beneficio:**
Todas las librerías se descargan automáticamente con sus versiones compatibles.

---

### Spring Boot

**¿Qué es Spring Boot?**
Spring Boot es un framework de Java que simplifica el desarrollo de aplicaciones empresariales basadas en Spring Framework. Proporciona configuración automática y servidores embebidos.

**Características principales:**

- **Configuración automática**: Detecta librerías y configura automáticamente
- **Servidor embebido**: Tomcat/Jetty incluido, no necesitas servidor externo
- **Starter POMs**: Dependencias pre-agrupadas (web, data-jpa, security)
- **Producción lista**: Métricas, health checks, y monitoreo incluidos

**Componentes de Spring Boot en Baby Cash:**

| Componente            | Propósito                    | Starter                        |
| --------------------- | ---------------------------- | ------------------------------ |
| **Spring MVC**        | Crear API REST               | `spring-boot-starter-web`      |
| **Spring Data JPA**   | Acceso a base de datos       | `spring-boot-starter-data-jpa` |
| **Spring Security**   | Autenticación y autorización | `spring-boot-starter-security` |
| **PostgreSQL Driver** | Conexión a PostgreSQL        | `postgresql`                   |
| **Lombok**            | Reducir boilerplate          | `lombok`                       |
| **JWT**               | Tokens de autenticación      | `jjwt`                         |

**Ventajas en Baby Cash:**

- Desarrollo rápido de API REST
- Configuración mínima (application.properties)
- Seguridad robusta con JWT
- ORM (JPA/Hibernate) para base de datos

---

### API REST (RESTful API)

**¿Qué es REST?**
REST (Representational State Transfer) es un estilo arquitectónico para diseñar servicios web. Una API REST usa HTTP para realizar operaciones CRUD.

**Principios REST:**

1. **Cliente-Servidor**: Separación de responsabilidades
2. **Sin estado**: Cada petición es independiente
3. **Cacheable**: Las respuestas pueden ser cacheadas
4. **Interfaz uniforme**: URLs y métodos HTTP estándar
5. **Sistema en capas**: Arquitectura escalable

**Métodos HTTP en Baby Cash:**

| Método     | Operación           | Ejemplo                  | Descripción                   |
| ---------- | ------------------- | ------------------------ | ----------------------------- |
| **GET**    | Leer                | `GET /api/products`      | Obtener lista de productos    |
| **POST**   | Crear               | `POST /api/products`     | Crear nuevo producto          |
| **PUT**    | Actualizar completo | `PUT /api/products/1`    | Actualizar producto completo  |
| **PATCH**  | Actualizar parcial  | `PATCH /api/products/1`  | Actualizar campos específicos |
| **DELETE** | Eliminar            | `DELETE /api/products/1` | Eliminar producto             |

**Códigos de estado HTTP:**

| Código                 | Significado        | Uso en Baby Cash         |
| ---------------------- | ------------------ | ------------------------ |
| **200 OK**             | Éxito              | Petición exitosa         |
| **201 Created**        | Creado             | Producto/orden creado    |
| **204 No Content**     | Sin contenido      | Eliminación exitosa      |
| **400 Bad Request**    | Petición inválida  | Datos mal formados       |
| **401 Unauthorized**   | No autenticado     | Token faltante/inválido  |
| **403 Forbidden**      | Sin permisos       | Usuario sin autorización |
| **404 Not Found**      | No encontrado      | Producto no existe       |
| **500 Internal Error** | Error del servidor | Error no manejado        |

**Ejemplo de endpoint en Baby Cash:**

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        // GET /api/products
        return ResponseEntity.ok(productService.findAll());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // POST /api/products
        return ResponseEntity.status(201).body(productService.save(product));
    }
}
```

---

### JPA (Java Persistence API)

**¿Qué es JPA?**
JPA es una especificación de Java para mapear objetos Java a tablas de bases de datos relacionales (ORM - Object-Relational Mapping).

**¿Por qué JPA?**

- **Abstracción**: Trabajas con objetos Java en lugar de SQL
- **Portabilidad**: Cambia de base de datos sin cambiar código
- **Productividad**: Menos código boilerplate
- **Type-safe**: Errores en tiempo de compilación, no runtime

**Hibernate (Implementación de JPA):**
Hibernate es la implementación más popular de JPA. Spring Boot lo usa por defecto.

**Ejemplo en Baby Cash:**

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
```

**Anotaciones JPA:**

| Anotación         | Propósito                       |
| ----------------- | ------------------------------- |
| `@Entity`         | Marca clase como entidad de BD  |
| `@Table`          | Especifica nombre de tabla      |
| `@Id`             | Marca campo como clave primaria |
| `@GeneratedValue` | Auto-incremento de ID           |
| `@Column`         | Configuración de columna        |
| `@ManyToOne`      | Relación muchos-a-uno           |
| `@OneToMany`      | Relación uno-a-muchos           |
| `@ManyToMany`     | Relación muchos-a-muchos        |

**Repositorios JPA:**

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByPriceLessThan(BigDecimal price);
}
```

Spring Data JPA genera automáticamente las consultas SQL.

---

### JWT (JSON Web Token)

**¿Qué es JWT?**
JWT es un estándar abierto (RFC 7519) para crear tokens de acceso que permiten autenticar usuarios de forma segura y sin estado (stateless).

**Estructura de un JWT:**
Un JWT tiene 3 partes separadas por puntos (`.`):

```
header.payload.signature
```

**Ejemplo:**

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**1. Header (Encabezado):**

```json
{
  "alg": "HS256", // Algoritmo de firma
  "typ": "JWT" // Tipo de token
}
```

**2. Payload (Carga útil):**

```json
{
  "sub": "user123", // Subject (ID de usuario)
  "name": "Juan Pérez", // Nombre
  "email": "juan@example.com", // Email
  "role": "ADMIN", // Rol
  "iat": 1516239022, // Issued At (timestamp)
  "exp": 1516242622 // Expiration (timestamp)
}
```

**3. Signature (Firma):**

```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

**Flujo de autenticación en Baby Cash:**

```
1. Login
   Client → POST /api/auth/login {email, password}
   Server → Valida credenciales
   Server → Genera JWT
   Server → Responde con token

2. Peticiones autenticadas
   Client → GET /api/orders
            Header: Authorization: Bearer <JWT_TOKEN>
   Server → Valida token
   Server → Extrae usuario del payload
   Server → Procesa petición
   Server → Responde con datos

3. Token expirado
   Client → GET /api/orders (token expirado)
   Server → 401 Unauthorized
   Client → Redirige a login
```

**Ventajas de JWT en Baby Cash:**

- **Sin estado**: No necesitas guardar sesiones en el servidor
- **Escalable**: Funciona con múltiples servidores
- **Seguro**: Firma criptográfica previene alteraciones
- **Portátil**: Funciona entre diferentes dominios (CORS)

---

### Lombok

**¿Qué es Lombok?**
Lombok es una librería de Java que reduce el código boilerplate mediante anotaciones. Genera automáticamente getters, setters, constructores, etc., durante la compilación.

**Problema sin Lombok:**

```java
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;

    // Constructor sin argumentos
    public Product() {}

    // Constructor con argumentos
    public Product(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }

    // equals, hashCode, toString...
    // +50 líneas más
}
```

**Con Lombok:**

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
}
```

**Anotaciones Lombok usadas en Baby Cash:**

| Anotación             | Genera                                       |
| --------------------- | -------------------------------------------- |
| `@Data`               | Getters, setters, toString, equals, hashCode |
| `@Getter`             | Solo getters                                 |
| `@Setter`             | Solo setters                                 |
| `@NoArgsConstructor`  | Constructor sin argumentos                   |
| `@AllArgsConstructor` | Constructor con todos los argumentos         |
| `@Builder`            | Patrón Builder para crear objetos            |
| `@Slf4j`              | Logger (log.info(), log.error())             |

**Beneficio:**
Código más limpio y mantenible, menos líneas de código.

---

## 🎨 Frontend - React y TypeScript

### React

**¿Qué es React?**
React es una librería de JavaScript para construir interfaces de usuario interactivas. Fue creada por Facebook (Meta) en 2013 y es una de las más populares del mundo.

**Características principales:**

- **Basado en componentes**: UI dividida en piezas reutilizables
- **Virtual DOM**: Renderizado eficiente y rápido
- **Declarativo**: Describes cómo se ve la UI, React actualiza el DOM
- **Unidireccional**: Flujo de datos en una dirección (one-way binding)
- **JSX**: Sintaxis que mezcla JavaScript y HTML

**Conceptos fundamentales:**

**1. Componentes:**

```jsx
// Componente funcional
function ProductCard({ product }) {
  return (
    <div className="card">
      <h3>{product.name}</h3>
      <p>${product.price}</p>
      <button>Agregar al carrito</button>
    </div>
  );
}
```

**2. Props (Propiedades):**
Datos que pasan de componente padre a hijo.

```jsx
<ProductCard product={productData} />
```

**3. State (Estado):**
Datos que cambian con el tiempo.

```jsx
const [count, setCount] = useState(0);
```

**4. Hooks:**
Funciones que permiten usar estado y otras características de React.

| Hook          | Propósito                       |
| ------------- | ------------------------------- |
| `useState`    | Manejar estado local            |
| `useEffect`   | Efectos secundarios (API calls) |
| `useContext`  | Compartir datos globalmente     |
| `useReducer`  | Estado complejo                 |
| `useMemo`     | Memorizar cálculos costosos     |
| `useCallback` | Memorizar funciones             |

**¿Por qué React en Baby Cash?**

- Desarrollo rápido de UI interactiva
- Componentización: Reutilizar código (Button, Card, Modal)
- Ecosistema enorme: React Router, Redux, etc.
- Rendimiento: Virtual DOM optimiza actualizaciones
- Comunidad grande: Fácil encontrar soluciones

---

### TypeScript

**¿Qué es TypeScript?**
TypeScript es un superconjunto de JavaScript que agrega tipado estático. Es compilado a JavaScript puro. Fue desarrollado por Microsoft.

**JavaScript vs TypeScript:**

```javascript
// JavaScript (sin tipos)
function addToCart(product, quantity) {
  return {
    product: product,
    quantity: quantity,
    total: product.price * quantity,
  };
}

// ¿Qué pasa si paso strings en lugar de números? Error en runtime
```

```typescript
// TypeScript (con tipos)
interface Product {
  id: number;
  name: string;
  price: number;
}

interface CartItem {
  product: Product;
  quantity: number;
  total: number;
}

function addToCart(product: Product, quantity: number): CartItem {
  return {
    product: product,
    quantity: quantity,
    total: product.price * quantity,
  };
}

// Si pasas tipos incorrectos, error en tiempo de compilación
```

**Características de TypeScript:**

- **Tipado estático**: Detecta errores antes de ejecutar
- **IntelliSense**: Autocompletado en el IDE
- **Refactoring seguro**: Renombrar variables con confianza
- **Interfaces y tipos**: Define contratos de datos
- **Compatibilidad**: Todo JavaScript es TypeScript válido

**Tipos básicos:**

| Tipo                 | Ejemplo            | Descripción                  |
| -------------------- | ------------------ | ---------------------------- |
| `string`             | `"Baby Cash"`      | Texto                        |
| `number`             | `42`, `3.14`       | Números                      |
| `boolean`            | `true`, `false`    | Verdadero/falso              |
| `array`              | `number[]`         | Array de números             |
| `object`             | `{ name: string }` | Objeto                       |
| `any`                | Cualquier cosa     | ⚠️ Evitar (pierde seguridad) |
| `void`               | Sin retorno        | Funciones que no retornan    |
| `null` / `undefined` | Nulos              | Valores ausentes             |

**Tipos avanzados:**

```typescript
// Union types
type Status = "pending" | "shipped" | "delivered";

// Interfaces
interface User {
  id: number;
  name: string;
  email: string;
  role: "ADMIN" | "USER";
}

// Generics
interface ApiResponse<T> {
  data: T;
  status: number;
  message: string;
}

// Type alias
type CartItems = CartItem[];
```

**Beneficios en Baby Cash:**

- Menos bugs: Errores detectados antes de runtime
- Mejor mantenibilidad: Código auto-documentado
- IDE poderoso: Autocompletado y refactoring
- Escalabilidad: Facilita trabajo en equipo grande

---

### Vite

**¿Qué es Vite?**
Vite es una herramienta de build ultra-rápida para aplicaciones web modernas. Fue creada por Evan You (creador de Vue.js).

**¿Por qué Vite es rápido?**

- **ESM (ES Modules)**: Usa módulos nativos del navegador
- **Hot Module Replacement (HMR)**: Actualiza solo lo que cambió
- **Optimización con esbuild**: Compilador escrito en Go (10-100x más rápido)

**Vite vs Webpack:**

| Característica | Webpack               | Vite            |
| -------------- | --------------------- | --------------- |
| Inicio en dev  | ~30-60s               | ~1-2s ⚡        |
| HMR            | Lento en apps grandes | Instantáneo     |
| Build prod     | Lento                 | Rápido (Rollup) |
| Configuración  | Compleja              | Simple          |

**Características de Vite:**

- **Dev server rápido**: Inicia en milisegundos
- **TypeScript**: Soporte nativo sin configuración
- **CSS**: CSS Modules, Sass, Less, PostCSS
- **Optimización**: Code splitting automático
- **Plugins**: Extensible con plugins

**Comandos Vite en Baby Cash:**

```bash
npm run dev      # Servidor de desarrollo (http://localhost:5173)
npm run build    # Build para producción
npm run preview  # Preview del build
```

**Configuración (vite.config.ts):**

```typescript
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": "http://localhost:8080", // Proxy al backend
    },
  },
});
```

---

### React Router

**¿Qué es React Router?**
React Router es una librería para gestionar navegación en aplicaciones React de una sola página (SPA - Single Page Application).

**¿Por qué React Router?**

- **Navegación sin recargar**: Cambia de página sin reload
- **URLs limpias**: `/products/123` en lugar de `/#products-123`
- **Protección de rutas**: Rutas privadas que requieren login
- **Parámetros**: Capturar datos de la URL

**Conceptos:**

```jsx
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";

function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Home</Link>
        <Link to="/products">Productos</Link>
        <Link to="/cart">Carrito</Link>
      </nav>

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/products" element={<ProductList />} />
        <Route path="/products/:id" element={<ProductDetail />} />
        <Route path="/cart" element={<Cart />} />
        <Route
          path="/admin"
          element={
            <ProtectedRoute>
              <Admin />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}
```

**Hooks de React Router:**

| Hook              | Propósito                     |
| ----------------- | ----------------------------- |
| `useNavigate`     | Navegar programáticamente     |
| `useParams`       | Obtener parámetros de URL     |
| `useLocation`     | Información de la ruta actual |
| `useSearchParams` | Query params (?search=baby)   |

---

### Tailwind CSS

**¿Qué es Tailwind CSS?**
Tailwind es un framework de CSS "utility-first" que proporciona clases utilitarias para construir diseños personalizados sin escribir CSS.

**CSS tradicional vs Tailwind:**

```html
<!-- CSS tradicional -->
<style>
  .button {
    background-color: blue;
    color: white;
    padding: 12px 24px;
    border-radius: 8px;
    font-weight: bold;
  }

  .button:hover {
    background-color: darkblue;
  }
</style>
<button class="button">Comprar</button>

<!-- Tailwind CSS -->
<button
  class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg"
>
  Comprar
</button>
```

**Ventajas de Tailwind:**

- **Desarrollo rápido**: No nombrar clases, usar utilitarias
- **Consistencia**: Sistema de diseño predefinido (colores, espaciado)
- **Responsive**: Clases para diferentes tamaños de pantalla
- **Tree-shaking**: Solo incluye CSS usado (build pequeño)
- **Personalizable**: Configurable con `tailwind.config.js`

**Clases Tailwind comunes:**

| Categoría      | Ejemplos                                       |
| -------------- | ---------------------------------------------- |
| **Colores**    | `bg-blue-500`, `text-white`, `border-gray-300` |
| **Espaciado**  | `p-4` (padding), `m-2` (margin), `gap-3`       |
| **Flexbox**    | `flex`, `justify-center`, `items-center`       |
| **Grid**       | `grid`, `grid-cols-3`, `gap-4`                 |
| **Tipografía** | `text-xl`, `font-bold`, `text-center`          |
| **Responsive** | `md:text-lg`, `lg:grid-cols-4`                 |

**Responsive en Tailwind:**

```jsx
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
  {/* 1 columna móvil, 2 en tablet, 4 en desktop */}
</div>
```

---

## 🗄️ Base de Datos - PostgreSQL

### PostgreSQL

**¿Qué es PostgreSQL?**
PostgreSQL (o Postgres) es un sistema de gestión de bases de datos relacional de código abierto. Es conocido por su robustez, escalabilidad y cumplimiento de estándares SQL.

**Historia:**

- Creado en 1986 en UC Berkeley
- Código abierto desde 1996
- Actualmente una de las BD más populares

**Características principales:**

- **ACID**: Atomicidad, Consistencia, Aislamiento, Durabilidad
- **Open Source**: Gratuito y de código abierto
- **Extensible**: Tipos de datos personalizados, funciones
- **JSON**: Soporte nativo para JSON y JSONB
- **Full-text search**: Búsqueda de texto avanzada
- **Replicación**: Master-slave, multi-master
- **Índices**: B-tree, Hash, GiST, GIN, etc.

**¿Por qué PostgreSQL en Baby Cash?**

- Gratuito y open source (vs Oracle, SQL Server)
- Robusto para e-commerce (transacciones ACID)
- Excelente rendimiento con índices
- Soporte para JSON (flexibilidad)
- Comunidad activa y documentación extensa

---

### SQL (Structured Query Language)

**¿Qué es SQL?**
SQL es el lenguaje estándar para interactuar con bases de datos relacionales. Permite crear, leer, actualizar y eliminar datos (CRUD).

**Tipos de comandos SQL:**

**1. DDL (Data Definition Language) - Estructura:**

```sql
-- Crear tabla
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Modificar tabla
ALTER TABLE products ADD COLUMN description TEXT;

-- Eliminar tabla
DROP TABLE products;
```

**2. DML (Data Manipulation Language) - Datos:**

```sql
-- Insertar
INSERT INTO products (name, price, stock)
VALUES ('Pañales Baby Cash', 29.99, 100);

-- Actualizar
UPDATE products
SET price = 24.99
WHERE id = 1;

-- Eliminar
DELETE FROM products
WHERE stock = 0;

-- Consultar
SELECT * FROM products WHERE price < 50;
```

**3. DQL (Data Query Language) - Consultas:**

```sql
-- SELECT básico
SELECT name, price FROM products;

-- WHERE (filtro)
SELECT * FROM products WHERE price > 20 AND stock > 0;

-- ORDER BY (ordenar)
SELECT * FROM products ORDER BY price DESC;

-- JOIN (unir tablas)
SELECT p.name, c.name AS category
FROM products p
INNER JOIN categories c ON p.category_id = c.id;

-- GROUP BY (agrupar)
SELECT category_id, COUNT(*) AS total
FROM products
GROUP BY category_id;
```

**4. DCL (Data Control Language) - Permisos:**

```sql
-- Dar permisos
GRANT SELECT ON products TO usuario;

-- Revocar permisos
REVOKE SELECT ON products FROM usuario;
```

---

### Modelo Relacional

**¿Qué es el modelo relacional?**
El modelo relacional organiza datos en tablas (relaciones) con filas (tuplas) y columnas (atributos). Las tablas se relacionan mediante claves.

**Conceptos clave:**

**1. Tabla (Entidad):**
Representa un concepto del dominio (productos, usuarios, órdenes).

**2. Fila (Tupla/Registro):**
Una instancia específica (un producto, un usuario).

**3. Columna (Atributo):**
Una propiedad de la entidad (nombre, precio, email).

**4. Clave Primaria (Primary Key):**
Identificador único de cada fila.

```sql
id SERIAL PRIMARY KEY
```

**5. Clave Foránea (Foreign Key):**
Referencia a la clave primaria de otra tabla.

```sql
category_id INTEGER REFERENCES categories(id)
```

**Ejemplo de modelo relacional en Baby Cash:**

```
USERS                    ORDERS                   ORDER_ITEMS
┌──────────┐            ┌──────────┐            ┌──────────────┐
│ id (PK)  │───────┐    │ id (PK)  │───────┐    │ id (PK)      │
│ name     │       │    │ user_id  │       │    │ order_id (FK)│
│ email    │       └───→│ (FK)     │       └───→│ product_id   │
│ password │            │ total    │            │ (FK)         │
│ role     │            │ status   │            │ quantity     │
└──────────┘            │ date     │            │ price        │
                        └──────────┘            └──────────────┘
                                                        │
                        PRODUCTS                        │
                        ┌──────────────┐              │
                        │ id (PK)      │←─────────────┘
                        │ name         │
                        │ price        │
                        │ stock        │
                        │ category_id  │
                        │ (FK)         │
                        └──────────────┘
```

---

### Relaciones entre tablas

**Tipos de relaciones:**

**1. Uno a Muchos (1:N)**
Un registro en tabla A relacionado con muchos en tabla B.

Ejemplo: Un usuario puede tener muchas órdenes.

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),  -- FK
    total DECIMAL(10, 2)
);
```

**2. Muchos a Muchos (N:M)**
Muchos registros en A relacionados con muchos en B.

Ejemplo: Una orden tiene muchos productos, un producto está en muchas órdenes.

```sql
CREATE TABLE orders (
    id SERIAL PRIMARY KEY
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY
);

-- Tabla intermedia (junction table)
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER REFERENCES orders(id),
    product_id INTEGER REFERENCES products(id),
    quantity INTEGER,
    price DECIMAL(10, 2)
);
```

**3. Uno a Uno (1:1)**
Un registro en A relacionado con uno en B.

Ejemplo: Un usuario tiene un perfil.

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255)
);

CREATE TABLE profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE REFERENCES users(id),  -- UNIQUE = 1:1
    phone VARCHAR(20),
    address TEXT
);
```

---

### Índices

**¿Qué son los índices?**
Los índices son estructuras de datos que aceleran las consultas a base de datos. Funcionan como el índice de un libro.

**Sin índice:**

```sql
SELECT * FROM products WHERE name = 'Pañales';
-- Escanea TODAS las filas (slow)
```

**Con índice:**

```sql
CREATE INDEX idx_products_name ON products(name);
SELECT * FROM products WHERE name = 'Pañales';
-- Va directamente a las filas con ese nombre (fast)
```

**Tipos de índices en PostgreSQL:**

| Tipo       | Uso                   | Ejemplo           |
| ---------- | --------------------- | ----------------- |
| **B-tree** | Por defecto, ordenado | Nombres, IDs      |
| **Hash**   | Igualdad exacta       | Búsqueda de email |
| **GiST**   | Datos geométricos     | Coordenadas       |
| **GIN**    | Búsqueda full-text    | Búsqueda en texto |

**Cuándo usar índices:**

- ✅ Columnas en WHERE frecuentes
- ✅ Columnas en JOIN
- ✅ Columnas en ORDER BY
- ❌ Tablas pequeñas (overhead)
- ❌ Columnas que cambian mucho (mantenimiento)

**Ejemplo en Baby Cash:**

```sql
-- Índice para búsquedas por categoría
CREATE INDEX idx_products_category ON products(category_id);

-- Índice compuesto para búsquedas complejas
CREATE INDEX idx_orders_user_status ON orders(user_id, status);

-- Índice para búsqueda de texto
CREATE INDEX idx_products_name_text ON products USING GIN(to_tsvector('spanish', name));
```

---

### Transacciones (ACID)

**¿Qué es una transacción?**
Una transacción es un conjunto de operaciones que se ejecutan como una unidad atómica: todas se completan o ninguna.

**Propiedades ACID:**

**A - Atomicidad:**
Todo o nada. Si falla una operación, se revierten todas.

```sql
BEGIN;
  UPDATE products SET stock = stock - 1 WHERE id = 1;
  INSERT INTO orders (product_id, quantity) VALUES (1, 1);
  -- Si falla, se revierte UPDATE
COMMIT;
```

**C - Consistencia:**
Los datos siempre cumplen las reglas (constraints).

```sql
-- No puedes insertar price negativo si hay CHECK constraint
ALTER TABLE products ADD CONSTRAINT price_positive CHECK (price >= 0);
```

**I - Aislamiento (Isolation):**
Las transacciones concurrentes no interfieren entre sí.

```sql
-- Niveles de aislamiento
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
```

**D - Durabilidad:**
Una vez confirmada, la transacción persiste aunque falle el sistema.

**Ejemplo en Baby Cash (Proceso de compra):**

```sql
BEGIN;
  -- 1. Reducir stock
  UPDATE products SET stock = stock - 1 WHERE id = 1 AND stock > 0;

  -- 2. Crear orden
  INSERT INTO orders (user_id, total) VALUES (5, 29.99);

  -- 3. Agregar items
  INSERT INTO order_items (order_id, product_id, quantity, price)
  VALUES (LASTVAL(), 1, 1, 29.99);

  -- Si alguno falla, rollback automático
COMMIT;
```

---

## 🛠️ Herramientas de Desarrollo

### Git

**¿Qué es Git?**
Git es un sistema de control de versiones distribuido. Permite rastrear cambios en el código, colaborar con otros y mantener historial completo.

**Conceptos clave:**

**Repository (Repositorio):**
Proyecto con historial de cambios.

**Commit:**
Snapshot del código en un momento específico.

```bash
git commit -m "feat: agregar función de búsqueda"
```

**Branch (Rama):**
Línea de desarrollo independiente.

```bash
git branch feature/cart
git checkout feature/cart
```

**Merge:**
Combinar cambios de una rama a otra.

```bash
git checkout master
git merge feature/cart
```

**Remote:**
Repositorio en servidor (GitHub, GitLab).

```bash
git push origin master
git pull origin master
```

**Comandos esenciales:**

```bash
# Configuración inicial
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"

# Clonar repositorio
git clone https://github.com/usuario/babycash-java.git

# Ver estado
git status

# Agregar cambios
git add .
git add archivo.java

# Commit
git commit -m "mensaje descriptivo"

# Ver historial
git log --oneline

# Crear rama
git checkout -b feature/nueva-funcionalidad

# Cambiar de rama
git checkout master

# Actualizar desde remoto
git pull

# Subir cambios
git push origin master
```

**Workflow en Baby Cash:**

```
master (producción)
  ↑
  merge ←── develop (desarrollo)
              ↑
              merge ←── feature/cart (nueva funcionalidad)
              ↑
              merge ←── fix/bug-login (corrección de bug)
```

---

### ESLint

**¿Qué es ESLint?**
ESLint es una herramienta de linting para JavaScript/TypeScript que analiza código para encontrar problemas y mantener estilo consistente.

**¿Qué hace ESLint?**

- Detecta errores de sintaxis
- Encuentra bugs potenciales
- Enforza estilo de código
- Mejora legibilidad

**Reglas configuradas en Baby Cash:**

```javascript
// eslint.config.js
rules: {
  'no-console': 'warn',           // Advertir sobre console.log
  'no-unused-vars': 'error',      // Error en variables no usadas
  'no-debugger': 'error',         // Prohibir debugger
  '@typescript-eslint/no-explicit-any': 'error',  // Prohibir any
}
```

**Ejemplo:**

```typescript
// ❌ Error: 'product' is assigned but never used
const product = getProduct();

// ❌ Error: Unexpected any
function process(data: any) {}

// ✅ Correcto
const product = getProduct();
console.log(product);

function process(data: Product) {}
```

**Comandos:**

```bash
npm run lint       # Ver errores
npm run lint:fix   # Auto-corregir
```

---

### Prettier

**¿Qué es Prettier?**
Prettier es un formateador de código opinionado. Formatea código automáticamente para mantener estilo consistente.

**¿Por qué Prettier?**

- Sin discusiones sobre estilo: Prettier decide
- Formato automático al guardar
- Integración con ESLint

**Configuración en Baby Cash:**

```json
{
  "semi": true, // Punto y coma al final
  "singleQuote": true, // Comillas simples
  "tabWidth": 2, // 2 espacios de indentación
  "trailingComma": "es5", // Coma al final
  "printWidth": 100 // Máx 100 caracteres por línea
}
```

**Antes vs Después:**

```typescript
// Antes (desordenado)
const user = { name: "Juan", email: "juan@example.com", role: "ADMIN" };

// Después (formateado)
const user = {
  name: "Juan",
  email: "juan@example.com",
  role: "ADMIN",
};
```

---

### Checkstyle

**¿Qué es Checkstyle?**
Checkstyle es una herramienta de linting para Java que verifica que el código siga estándares de codificación (Google Java Style, Sun Code Conventions).

**Reglas configuradas:**

- Convenciones de nombres (CamelCase, UPPER_CASE)
- Longitud de línea (máx 120 caracteres)
- Complejidad ciclomática (máx 10)
- Imports ordenados
- Javadoc en clases públicas

**Ejemplo:**

```java
// ❌ Error: Name 'product_name' must match pattern '^[a-z][a-zA-Z0-9]*$'
String product_name = "Pañales";

// ❌ Error: Line is longer than 120 characters
String description = "Lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";

// ✅ Correcto
String productName = "Pañales";

String description = "Lorem ipsum dolor sit amet consectetur adipiscing elit "
    + "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua";
```

---

### Husky + lint-staged

**¿Qué es Husky?**
Husky gestiona Git hooks, permitiendo ejecutar scripts automáticamente antes de commits, push, etc.

**¿Qué es lint-staged?**
lint-staged ejecuta linters solo en archivos staged (añadidos con `git add`), no en todo el proyecto.

**Pre-commit hook en Baby Cash:**

```bash
# .husky/pre-commit
echo "🔍 Verificando código antes del commit..."

npx lint-staged --relative

if [ $? -ne 0 ]; then
  echo "❌ Commit bloqueado: Errores de linting encontrados"
  exit 1
fi

echo "✅ Código verificado exitosamente"
```

**Configuración lint-staged:**

```json
{
  "lint-staged": {
    "frontend/src/**/*.{ts,tsx}": ["eslint --fix", "prettier --write"]
  }
}
```

**Flujo:**

```
git add archivo.tsx
git commit -m "mensaje"
  ↓
Husky intercepta commit
  ↓
lint-staged ejecuta ESLint + Prettier
  ↓
¿Hay errores?
  ├─ Sí → ❌ Commit bloqueado
  └─ No → ✅ Commit permitido
```

---

## 🚀 Control de Versiones y CI/CD

### GitHub

**¿Qué es GitHub?**
GitHub es una plataforma de hosting para repositorios Git. Proporciona colaboración, code review, issues, CI/CD y más.

**Características principales:**

- **Repositorios**: Almacena código con historial
- **Pull Requests**: Proponer cambios y revisión de código
- **Issues**: Seguimiento de bugs y tareas
- **GitHub Actions**: CI/CD automatizado
- **GitHub Pages**: Hosting gratuito de sitios estáticos

**Workflow en Baby Cash:**

```
1. Developer
   - Crea rama: git checkout -b feature/nueva-funcion
   - Escribe código
   - Commit: git commit -m "mensaje"
   - Push: git push origin feature/nueva-funcion

2. Pull Request
   - Abre PR en GitHub
   - Code review por equipo
   - CI/CD ejecuta tests automáticamente
   - Aprobación necesaria

3. Merge
   - Merge a master
   - Deploy automático a producción
```

---

### CI/CD (Continuous Integration / Continuous Deployment)

**¿Qué es CI/CD?**
CI/CD es la práctica de automatizar build, testing y deployment del código.

**Continuous Integration (CI):**
Integrar cambios frecuentemente y ejecutar tests automáticamente.

**Continuous Deployment (CD):**
Desplegar automáticamente a producción después de pasar tests.

**Ejemplo de GitHub Actions:**

```yaml
name: Baby Cash CI/CD

on:
  push:
    branches: [master]
  pull_request:
    branches: [master]

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v2

      - name: Set up Java
        uses: actions/setup-java@v2
        with:
          java-version: "21"

      - name: Run backend tests
        run: cd backend && ./mvnw test

      - name: Run frontend tests
        run: cd frontend && npm install && npm test

  deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/master'

    steps:
      - name: Deploy to production
        run: ./deploy.sh
```

**Beneficios:**

- Detección temprana de bugs
- Despliegues más frecuentes y seguros
- Feedback rápido a desarrolladores
- Reduce trabajo manual repetitivo

---

## 📖 Glosario de Términos Adicionales

| Término          | Definición                                                               |
| ---------------- | ------------------------------------------------------------------------ |
| **API**          | Application Programming Interface - Interfaz para comunicar aplicaciones |
| **CRUD**         | Create, Read, Update, Delete - Operaciones básicas de base de datos      |
| **ORM**          | Object-Relational Mapping - Mapeo objetos ↔ tablas BD                    |
| **DTO**          | Data Transfer Object - Objeto para transferir datos entre capas          |
| **Endpoint**     | URL específica de una API (ej: `/api/products`)                          |
| **Middleware**   | Software que actúa entre dos capas (ej: autenticación)                   |
| **Schema**       | Estructura de la base de datos (tablas, columnas, relaciones)            |
| **Migration**    | Script para modificar estructura de BD (agregar tabla, columna)          |
| **Seed**         | Datos iniciales para poblar BD (usuarios de prueba, productos)           |
| **Token**        | Cadena de texto que representa credenciales de usuario (JWT)             |
| **Hash**         | Función que convierte texto a formato irreversible (password)            |
| **Salt**         | Valor aleatorio añadido antes de hashear (seguridad)                     |
| **CORS**         | Cross-Origin Resource Sharing - Permite peticiones entre dominios        |
| **Environment**  | Configuración específica (development, production, test)                 |
| **Deploy**       | Publicar aplicación a servidor de producción                             |
| **Localhost**    | Tu propia computadora (127.0.0.1)                                        |
| **Port**         | Número que identifica servicio (8080 backend, 5173 frontend)             |
| **Payload**      | Datos enviados en petición HTTP (body)                                   |
| **Query params** | Parámetros en URL (`?search=baby&sort=price`)                            |
| **Path params**  | Parámetros en ruta (`/products/123`)                                     |
| **Status code**  | Código HTTP de respuesta (200 OK, 404 Not Found)                         |
| **Boilerplate**  | Código repetitivo necesario                                              |
| **Refactoring**  | Mejorar código sin cambiar funcionalidad                                 |
| **Tech stack**   | Conjunto de tecnologías usadas en proyecto                               |

---

## ✅ Resumen Ejecutivo

Este documento cubre los conceptos técnicos fundamentales utilizados en Baby Cash:

### Backend (Java + Spring Boot)

- **Java 21**: Lenguaje híbrido (compilado + interpretado) de alto nivel
- **JVM**: Máquina virtual que ejecuta bytecode Java
- **Maven**: Gestión de dependencias y build
- **pom.xml**: Configuración central del proyecto
- **Spring Boot**: Framework para desarrollo rápido de APIs
- **JPA/Hibernate**: ORM para mapear objetos ↔ base de datos
- **JWT**: Autenticación stateless con tokens
- **Lombok**: Reduce boilerplate code

### Frontend (React + TypeScript)

- **React**: Librería para interfaces interactivas basadas en componentes
- **TypeScript**: JavaScript con tipado estático
- **Vite**: Build tool ultra-rápido
- **React Router**: Navegación en SPA
- **Tailwind CSS**: Framework CSS utility-first

### Base de Datos (PostgreSQL)

- **PostgreSQL**: RDBMS open source robusto
- **SQL**: Lenguaje para manipular datos
- **Modelo relacional**: Datos organizados en tablas relacionadas
- **Índices**: Aceleran consultas
- **Transacciones ACID**: Garantizan integridad de datos

### Herramientas

- **Git**: Control de versiones
- **ESLint/Prettier**: Linting y formateo de código
- **Checkstyle**: Linting para Java
- **Husky**: Git hooks automatizados
- **GitHub**: Hosting y colaboración
- **CI/CD**: Automatización de testing y deployment

---

**Fecha de creación**: 4 de Noviembre de 2025
**Versión**: 1.0
**Autor**: GitHub Copilot
**Proyecto**: Baby Cash - E-commerce
