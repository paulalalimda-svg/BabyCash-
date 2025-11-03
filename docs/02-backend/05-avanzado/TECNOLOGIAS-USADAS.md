# 🛠️ TECNOLOGÍAS USADAS EN BABYCASH

## 📋 Lista Completa

### Backend
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 17 | Lenguaje de programación principal |
| Spring Boot | 3.1.5 | Framework para backend |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | Acceso a base de datos |
| PostgreSQL | 14 | Base de datos relacional |
| Maven | 3.8+ | Gestor de dependencias |
| JWT (jjwt) | 0.11.5 | Tokens de autenticación |
| BCrypt | Incluido | Encriptación de contraseñas |
| Jakarta Mail | 2.0 | Envío de emails |
| Lombok | 1.18.30 | Reducir código boilerplate |

### Frontend
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| React | 18.2.0 | Librería UI |
| TypeScript | 5.x | Tipado estático para JavaScript |
| Vite | 4.x | Build tool y dev server |
| React Router | 6.x | Navegación entre páginas |
| Axios | 1.x | Cliente HTTP |
| React Hook Form | 7.x | Manejo de formularios |
| Tailwind CSS | 3.x | Estilos CSS |

---

## 🎯 ¿Por Qué Cada Tecnología?

### Java 17

**¿Qué es?**
Java es un **lenguaje de programación compilado** y **orientado a objetos**.

**Diferencia compilado vs interpretado:**
- **Compilado** (Java): Código se traduce a bytecode antes de ejecutar → Más rápido
- **Interpretado** (Python, JavaScript): Código se lee línea por línea mientras se ejecuta → Más lento

**¿Por qué Java?**
- ✅ **Estabilidad**: Usado por bancos, empresas grandes (Netflix, Amazon)
- ✅ **Tipado fuerte**: Detecta errores antes de ejecutar
- ✅ **Multiplataforma**: "Write once, run anywhere"
- ✅ **Ecosistema maduro**: Spring Framework es estándar en la industria
- ✅ **Performance**: Más rápido que Python/PHP para aplicaciones grandes
- ✅ **Comunidad**: Millones de desarrolladores y documentación

**Comparación con otros lenguajes:**

```java
// JAVA (tipado fuerte)
String nombre = "María";
int edad = 25;
edad = "veinticinco"; // ❌ ERROR: No se puede asignar String a int

// JAVASCRIPT (tipado débil)
let nombre = "María";
let edad = 25;
edad = "veinticinco"; // ✅ OK: JavaScript permite esto (puede causar bugs)
```

---

### Spring Boot 3

**¿Qué es?**
Spring Boot es un **framework** (conjunto de herramientas) que simplifica crear aplicaciones Java.

**¿Qué es un framework?**
Es como una **caja de herramientas pre-construida**. En vez de hacer todo desde cero, Spring Boot ya tiene:
- Sistema de seguridad listo
- Conexión a base de datos automática
- Servidor web incluido
- Inyección de dependencias

**¿Por qué Spring Boot?**
- ✅ **Convención sobre configuración**: Menos código, más productividad
- ✅ **Auto-configuración**: Detecta dependencias y configura automáticamente
- ✅ **Embebido**: Servidor Tomcat incluido (no necesitas instalarlo aparte)
- ✅ **Producción-ready**: Metrics, health checks, seguridad incorporada
- ✅ **Estándar de la industria**: Usado por la mayoría de empresas

**Ejemplo de simplicidad:**

```java
// SIN Spring Boot (necesitas ~50 líneas de configuración XML)
// CON Spring Boot:

@SpringBootApplication
public class BabyCashApplication {
    public static void main(String[] args) {
        SpringApplication.run(BabyCashApplication.class, args);
    }
}
// ¡Listo! Ya tienes servidor web, base de datos, seguridad configurados.
```

---

### PostgreSQL 14

**¿Qué es?**
PostgreSQL es una **base de datos relacional** (SQL).

**¿Qué es una base de datos relacional?**
Es como un conjunto de **tablas de Excel conectadas**:

```
Tabla: users
+----+-----------------+----------+
| id | email           | role     |
+----+-----------------+----------+
| 1  | maria@gmail.com | USER     |
| 2  | admin@baby.com  | ADMIN    |
+----+-----------------+----------+

Tabla: orders (relacionada con users)
+----+---------+--------+
| id | user_id | total  |
+----+---------+--------+
| 1  | 1       | 150000 |
| 2  | 1       | 80000  |
+----+---------+--------+
```

**¿Por qué PostgreSQL?**
- ✅ **Gratuito y open source**: No pagas licencias (vs Oracle, SQL Server)
- ✅ **ACID compliant**: Transacciones seguras (crítico para pagos)
- ✅ **Potente**: Soporta JSON, búsquedas full-text, geolocalización
- ✅ **Escalable**: Puede manejar millones de registros
- ✅ **Confiable**: Usado por Instagram, Spotify, Apple

**Alternativas y por qué NO las usamos:**
- **MySQL**: Menos features avanzados
- **MongoDB** (NoSQL): No relacional, no garantiza consistencia de datos (malo para e-commerce)
- **SQLite**: Solo para proyectos pequeños

---

### Spring Security 6

**¿Qué es?**
Framework de seguridad que maneja:
- **Autenticación**: Verificar quién eres (login)
- **Autorización**: Verificar qué puedes hacer (permisos)

**¿Por qué Spring Security?**
- ✅ **Integración perfecta**: Hecho para Spring Boot
- ✅ **Seguridad robusta**: Protección contra ataques comunes (CSRF, XSS)
- ✅ **Flexible**: Soporta JWT, OAuth2, LDAP, etc.
- ✅ **Probado**: Usado por bancos y gobierno

---

### JWT (JSON Web Tokens)

**¿Qué es?**
Sistema de **autenticación sin sesiones** (stateless).

**Diferencia con sesiones tradicionales:**

```
SESIONES TRADICIONALES (stateful):
1. Usuario hace login
2. Servidor guarda sesión en memoria
3. Servidor envía cookie al navegador
4. Cada petición: navegador envía cookie
❌ Problema: Servidor debe recordar todas las sesiones (consume RAM)

JWT (stateless):
1. Usuario hace login
2. Servidor genera token JWT (no guarda nada)
3. Navegador guarda token
4. Cada petición: navegador envía token
✅ Ventaja: Servidor no guarda nada (escalable)
```

**¿Por qué JWT?**
- ✅ **Stateless**: No consume memoria del servidor
- ✅ **Escalable**: Funciona en múltiples servidores
- ✅ **Seguro**: Token firmado criptográficamente
- ✅ **Estándar**: Usado por Google, Facebook, GitHub

---

### Maven

**¿Qué es?**
**Gestor de dependencias** y **build tool**.

**¿Qué es una dependencia?**
Es una **librería externa** que usamos. Ejemplo: para enviar emails, usamos `jakarta.mail`.

**¿Qué hace Maven?**
1. **Descarga librerías**: Lee `pom.xml` y descarga automáticamente
2. **Compila**: Convierte `.java` a `.class` (bytecode)
3. **Empaqueta**: Crea `.jar` (archivo ejecutable)

**Sin Maven:**
```
❌ Descargar 20 librerías manualmente
❌ Copiar .jar files a carpeta
❌ Configurar classpath
❌ Resolver conflictos de versiones
```

**Con Maven:**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- Maven descarga esto + 15 dependencias relacionadas automáticamente -->
```

---

### React 18

**¿Qué es?**
**Librería JavaScript** para construir interfaces de usuario.

**¿Qué es una librería?**
Es un conjunto de código pre-hecho. React te da:
- Componentes reutilizables
- Sistema de estados
- Renderizado eficiente (Virtual DOM)

**¿Por qué React?**
- ✅ **Componentes**: Código reutilizable
- ✅ **Virtual DOM**: Actualiza solo lo que cambió (rápido)
- ✅ **Ecosistema**: Miles de librerías compatibles
- ✅ **Demanda laboral**: React es el framework más usado

**Ejemplo de componente:**
```jsx
function ProductCard({ name, price }) {
  return (
    <div className="card">
      <h3>{name}</h3>
      <p>${price}</p>
    </div>
  );
}

// Reutilizar 100 veces
<ProductCard name="Pañales" price={45000} />
<ProductCard name="Leche" price={15000} />
```

---

### TypeScript

**¿Qué es?**
JavaScript con **tipos estáticos**. Es JavaScript + validaciones.

**Diferencia:**
```javascript
// JAVASCRIPT
function sumar(a, b) {
  return a + b;
}
sumar(5, "10"); // ⚠️ Retorna "510" (bug!)

// TYPESCRIPT
function sumar(a: number, b: number): number {
  return a + b;
}
sumar(5, "10"); // ❌ ERROR: "10" no es un number
```

**¿Por qué TypeScript?**
- ✅ **Menos bugs**: Detecta errores al escribir código
- ✅ **Autocompletado**: IDE sabe qué propiedades existen
- ✅ **Refactoring seguro**: Puedes renombrar variables sin miedo
- ✅ **Documentación implícita**: Los tipos explican el código

---

### Vite

**¿Qué es?**
**Build tool** (herramienta de construcción) para frontend.

**¿Qué hace?**
1. **Dev server**: Servidor local super rápido (localhost:5173)
2. **Hot reload**: Actualiza la página automáticamente al guardar
3. **Build**: Empaqueta código para producción (optimizado)

**¿Por qué Vite?**
- ✅ **Rápido**: 10x más rápido que Create React App
- ✅ **Simple**: Configuración mínima
- ✅ **Moderno**: Usa ES modules nativos

---

## 📊 Resumen de Decisiones Técnicas

| Decisión | Alternativa | Por Qué Elegimos |
|----------|-------------|------------------|
| Java 17 | Python, Node.js | Performance, tipado fuerte, ecosistema empresarial |
| Spring Boot | Jakarta EE, Quarkus | Más simple, auto-configuración, comunidad |
| PostgreSQL | MySQL, MongoDB | Features avanzados, ACID, confiabilidad |
| JWT | Sesiones | Stateless, escalable |
| React | Vue, Angular | Ecosistema, demanda laboral, componentes |
| TypeScript | JavaScript | Tipado estático, menos bugs |

---

**Última actualización**: Octubre 2025
