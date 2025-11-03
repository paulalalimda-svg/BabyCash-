# 🍃 ¿QUÉ ES SPRING BOOT?

## 🎯 Definición Simple

Spring Boot es un **framework** (conjunto de herramientas) que facilita crear aplicaciones Java, especialmente **APIs REST** y **aplicaciones web**.

Es como tener una **caja de herramientas pre-construida** en vez de hacer todo desde cero.

---

## 🔧 Framework vs Librería

### Librería

**¿Qué es?**
Una **librería** es un conjunto de funciones que **TÚ llamas**.

```java
// TÚ controlas el flujo
import java.util.ArrayList;

List<String> nombres = new ArrayList<>();  // Tú creas
nombres.add("María");                       // Tú llamas los métodos
nombres.remove(0);                          // Tú decides cuándo
```

**Ejemplos:** Gson (JSON), Apache Commons, Guava

### Framework

**¿Qué es?**
Un **framework** es una estructura donde **ÉL te llama** (inversión de control).

```java
// Spring Boot controla el flujo
@RestController
public class ProductController {
    
    @GetMapping("/products")
    public List<Product> getAll() {
        // Spring Boot LLAMA ESTE MÉTODO cuando llega GET /products
        return productService.getAll();
    }
}
```

**Ejemplos:** Spring Boot, Django, Ruby on Rails

### Comparación

| Librería | Framework |
|----------|-----------|
| TÚ la llamas | ÉL te llama |
| Control tuyo | Control del framework |
| Flexible | Estructurado |
| Ejemplo: Gson | Ejemplo: Spring Boot |

---

## ✅ ¿Por Qué Spring Boot?

### Sin Spring Boot (Java puro)

Para crear un servidor HTTP simple necesitarías:

```java
// ~200 líneas de configuración manual
ServerSocket serverSocket = new ServerSocket(8080);
Socket socket = serverSocket.accept();
BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
// ... configurar threading manualmente
// ... parsear HTTP requests manualmente
// ... conectar a base de datos manualmente
// ... configurar seguridad manualmente
// ... etc.
```

### Con Spring Boot

```java
@SpringBootApplication
public class BabyCashApplication {
    public static void main(String[] args) {
        SpringApplication.run(BabyCashApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping
    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
```

**¡Listo!** Ya tienes:
- ✅ Servidor HTTP (Tomcat embebido)
- ✅ Conexión a PostgreSQL automática
- ✅ JSON parsing automático
- ✅ Seguridad básica
- ✅ Logs
- ✅ Hot reload

---

## 🎁 Características de Spring Boot

### 1. Auto-Configuración

Spring Boot detecta qué librerías tienes y se configura solo.

```xml
<!-- Agregas esta dependencia en pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Spring Boot automáticamente:
- ✅ Configura conexión a BD
- ✅ Crea EntityManager
- ✅ Configura transacciones
- ✅ Escanea @Entity

### 2. Servidor Embebido

No necesitas instalar Tomcat/Jetty. Spring Boot lo trae incluido.

```bash
# Ejecutar aplicación
mvn spring-boot:run

# O generar JAR ejecutable
mvn clean package
java -jar babycash-backend.jar
```

### 3. Starter Dependencies

En vez de agregar 20 dependencias, agregas 1 "starter":

```xml
<!-- Sin Spring Boot -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
</dependency>
<!-- ... 15 más -->

<!-- Con Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- Incluye las 20 dependencias automáticamente -->
</dependency>
```

### 4. Production-Ready

Spring Boot incluye:
- **Health checks**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Info**: `/actuator/info`

---

## 🏗️ Arquitectura General de Spring Boot

```
┌─────────────────────────────────────────────────────────────┐
│                    SPRING BOOT APPLICATION                   │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  CONTROLLER  │    │   SERVICE    │    │  REPOSITORY  │
│   (API)      │───▶│  (Business   │───▶│  (Database)  │
│              │    │   Logic)     │    │              │
└──────────────┘    └──────────────┘    └──────────────┘
        │                   │                   │
        │                   │                   │
        ▼                   ▼                   ▼
    Recibe HTTP      Procesa datos      Accede PostgreSQL
```

### Capas

1. **Controller Layer** (Presentación)
   - Recibe peticiones HTTP
   - Valida datos
   - Retorna respuestas JSON

2. **Service Layer** (Lógica de Negocio)
   - Procesa datos
   - Aplica reglas de negocio
   - Coordina operaciones

3. **Repository Layer** (Persistencia)
   - Accede a base de datos
   - CRUD operations
   - Queries

---

## 🔄 Flujo de una Petición

```
1. Cliente → HTTP Request
   GET /api/products/5

2. Spring Boot → Enruta al Controller
   ProductController.getProductById()

3. Controller → Llama Service
   productService.getProductById(5)

4. Service → Llama Repository
   productRepository.findById(5)

5. Repository → Query SQL
   SELECT * FROM products WHERE id = 5

6. PostgreSQL → Retorna datos
   { id: 5, name: "Pañales", price: 45000 }

7. Repository → Convierte a Entity
   Product object

8. Service → Procesa (si es necesario)
   Aplica lógica de negocio

9. Controller → Convierte a JSON
   ProductResponse

10. Spring Boot → HTTP Response
    200 OK { "id": 5, "name": "Pañales", ... }
```

---

## 🎓 Ejemplo Mínimo de Spring Boot

### 1. Clase Principal

```java
@SpringBootApplication
public class BabyCashApplication {
    public static void main(String[] args) {
        SpringApplication.run(BabyCashApplication.class, args);
    }
}
```

**¿Qué hace `@SpringBootApplication`?**
Es una combinación de 3 anotaciones:
- `@Configuration`: Clase de configuración
- `@EnableAutoConfiguration`: Habilita auto-configuración
- `@ComponentScan`: Escanea componentes (@Service, @Controller, etc.)

### 2. Controller

```java
@RestController
@RequestMapping("/api/hello")
public class HelloController {
    
    @GetMapping
    public String hello() {
        return "Hola desde Spring Boot!";
    }
}
```

### 3. Ejecutar

```bash
mvn spring-boot:run
```

### 4. Probar

```bash
curl http://localhost:8080/api/hello
# Respuesta: "Hola desde Spring Boot!"
```

---

## 📦 Starters Principales en el Proyecto

### spring-boot-starter-web

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**Incluye:**
- Spring MVC (controladores REST)
- Tomcat embebido (servidor)
- Jackson (JSON)
- Validación

### spring-boot-starter-data-jpa

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

**Incluye:**
- Hibernate (ORM)
- Spring Data JPA (repositorios)
- Transacciones

### spring-boot-starter-security

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Incluye:**
- Spring Security
- Autenticación
- Autorización

### spring-boot-starter-mail

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

**Incluye:**
- JavaMail API
- SMTP configuración

---

## ⚙️ application.properties

Archivo de configuración central:

```properties
# Servidor
server.port=8080

# Base de Datos
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash
spring.datasource.username=postgres
spring.datasource.password=password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=86400000
```

Spring Boot lee esto automáticamente.

---

## 🔄 Ciclo de Vida de Spring Boot

```
1. main() ejecuta
   SpringApplication.run()

2. Spring Boot inicia
   - Lee application.properties
   - Escanea @Component, @Service, @Repository
   - Auto-configura según dependencias

3. Crea ApplicationContext
   - Instancia todos los beans
   - Inyecta dependencias (@Autowired)

4. Inicia servidor Tomcat
   - Puerto 8080 (por defecto)

5. Mapea endpoints
   - Lee @RequestMapping, @GetMapping, etc.

6. Aplicación lista
   Started BabyCashApplication in 3.5 seconds

7. Espera peticiones HTTP
```

---

## 📊 Spring vs Spring Boot

| Spring Framework | Spring Boot |
|------------------|-------------|
| Framework base | Extensión de Spring |
| Configuración manual (XML/Java) | Auto-configuración |
| Servidor externo (Tomcat) | Servidor embebido |
| Muchas dependencias | Starters (1 dependencia) |
| Más flexible | Más conveniente |
| Más complejo | Más simple |

**Spring Boot = Spring Framework + Conveniones + Auto-configuración**

---

## ✅ Ventajas de Spring Boot

1. ✅ **Rápido de desarrollar**: Menos configuración
2. ✅ **Opinionado**: Decisiones tomadas por ti
3. ✅ **Microservicios**: Ideal para arquitectura moderna
4. ✅ **Comunidad**: Enorme ecosistema
5. ✅ **Documentación**: Extensa y clara
6. ✅ **Production-ready**: Metrics, health checks incluidos
7. ✅ **Testing**: Herramientas de testing integradas

---

## ❌ Desventajas

1. ❌ **Curva de aprendizaje**: Muchas "anotaciones mágicas"
2. ❌ **Peso**: Aplicación ocupa ~30-50 MB (vs 5 MB en Node.js)
3. ❌ **Startup lento**: 3-5 segundos (vs instant en Node.js)
4. ❌ **Memoria**: Usa más RAM que lenguajes interpretados

**Pero:** Las ventajas superan las desventajas para aplicaciones empresariales.

---

## 🎯 ¿Por Qué Spring Boot para Este Proyecto?

1. ✅ **E-commerce**: Transacciones, seguridad, escalabilidad
2. ✅ **Tipado fuerte**: Menos bugs en producción
3. ✅ **Estándar industria**: Usado por bancos y empresas grandes
4. ✅ **Ecosistema**: Spring Security, Spring Data JPA
5. ✅ **Empleabilidad**: Alta demanda laboral

---

## 📋 Resumen

| Concepto | Definición |
|----------|------------|
| **Spring Boot** | Framework para crear aplicaciones Java rápidamente |
| **Framework** | Estructura que controla el flujo (inversión de control) |
| **Starter** | Dependencia que incluye múltiples librerías |
| **Auto-configuración** | Spring Boot se configura solo según dependencias |
| **Servidor embebido** | Tomcat incluido, no necesitas instalarlo |

---

**Última actualización**: Octubre 2025
