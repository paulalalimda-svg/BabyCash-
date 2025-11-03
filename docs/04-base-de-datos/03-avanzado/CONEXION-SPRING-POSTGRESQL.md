# 🔌 CONEXIÓN SPRING BOOT - POSTGRESQL

## 🎯 Visión General

Para que Spring Boot se comunique con PostgreSQL, necesitamos:
1. **Driver JDBC** (el "traductor")
2. **Configuración** en `application.properties`
3. **Dependencias** en `pom.xml`

---

## 📦 1. Dependencias en pom.xml

### Spring Data JPA

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

**¿Qué hace?**
- Incluye JPA (Java Persistence API)
- Incluye Hibernate (implementación de JPA)
- Permite mapear clases Java a tablas SQL

### Driver PostgreSQL

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**¿Qué hace?**
- Es el **JDBC Driver**: permite que Java hable con PostgreSQL
- Solo se usa en runtime (no en compilación)

### pom.xml Completo (Fragmento)

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA (incluye Hibernate) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- (otras dependencias...) -->
</dependencies>
```

---

## ⚙️ 2. Configuración en application.properties

### Configuración Completa

```properties
# ========================================
# CONFIGURACIÓN DE BASE DE DATOS
# ========================================

# URL de conexión
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash

# Usuario y contraseña
spring.datasource.username=babycash_user
spring.datasource.password=tu_password_seguro

# Driver (opcional, Spring Boot lo detecta automáticamente)
spring.datasource.driver-class-name=org.postgresql.Driver

# ========================================
# CONFIGURACIÓN JPA / HIBERNATE
# ========================================

# Dialecto de PostgreSQL
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Mostrar SQL en consola (para desarrollo)
spring.jpa.show-sql=true

# Formatear SQL en consola
spring.jpa.properties.hibernate.format_sql=true

# Estrategia de creación de tablas
spring.jpa.hibernate.ddl-auto=update
```

---

## 🔍 3. Desglose de Configuración

### URL de Conexión

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash
```

**Estructura:**
```
jdbc:postgresql://[host]:[puerto]/[nombre_base_datos]
```

**Partes:**
- `jdbc:postgresql://` → Protocolo JDBC para PostgreSQL
- `localhost` → Servidor (tu computadora)
- `5432` → Puerto por defecto de PostgreSQL
- `babycash` → Nombre de la base de datos

**Ejemplos:**

```properties
# Base de datos local
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash

# Base de datos remota
spring.datasource.url=jdbc:postgresql://192.168.1.100:5432/babycash

# Railway / Render
spring.datasource.url=jdbc:postgresql://containers-us-west-123.railway.app:5432/railway

# Heroku
spring.datasource.url=${DATABASE_URL}
```

### Credenciales

```properties
spring.datasource.username=babycash_user
spring.datasource.password=tu_password_seguro
```

**⚠️ Seguridad:**

```properties
# ❌ MAL - Contraseña en texto plano
spring.datasource.password=mypassword123

# ✅ BIEN - Variable de entorno
spring.datasource.password=${DB_PASSWORD}
```

### Dialecto de Hibernate

```properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**¿Para qué?**
Hibernate genera SQL diferente para cada base de datos:

```sql
-- PostgreSQL
SELECT * FROM users LIMIT 10;

-- MySQL
SELECT * FROM users LIMIT 10;

-- SQL Server
SELECT TOP 10 * FROM users;

-- Oracle
SELECT * FROM users WHERE ROWNUM <= 10;
```

El dialecto le dice a Hibernate **qué sintaxis usar**.

### Mostrar SQL

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Ejemplo de salida:**

```
Hibernate: 
    select
        user0_.id as id1_0_,
        user0_.email as email2_0_,
        user0_.name as name3_0_ 
    from
        users user0_ 
    where
        user0_.email=?
```

**⚠️ Solo para desarrollo. En producción, usa `false`.**

### DDL Auto

```properties
spring.jpa.hibernate.ddl-auto=update
```

**Opciones:**

| Valor | Qué Hace | Cuándo Usar |
|-------|----------|-------------|
| `none` | No hace nada | Producción (con Flyway/Liquibase) |
| `validate` | Solo valida esquema | Producción |
| `update` | Actualiza tablas (no elimina) | **Desarrollo** |
| `create` | Elimina y crea tablas | Testing |
| `create-drop` | Crea y elimina al cerrar app | Testing |

**Ejemplo:**

```java
@Entity
public class User {
    @Id
    private Long id;
    private String email;
    // private String name;  // ← Si comentas esto...
}
```

- `update`: La columna `name` se mantiene (no se elimina)
- `create`: Se elimina y recrea la tabla (pierdes datos)

---

## 🔐 4. Variables de Entorno

### ¿Por Qué?

```properties
# ❌ MAL - Contraseña en código
spring.datasource.password=mypassword123
# Si subes esto a GitHub, ¡todos ven tu contraseña!
```

### Solución: Variables de Entorno

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/babycash}
spring.datasource.username=${DB_USERNAME:babycash_user}
spring.datasource.password=${DB_PASSWORD:}
```

**Sintaxis:**
```
${VARIABLE_NAME:valor_por_defecto}
```

### Configurar Variables de Entorno

#### Linux / macOS

```bash
# En terminal
export DB_URL="jdbc:postgresql://localhost:5432/babycash"
export DB_USERNAME="babycash_user"
export DB_PASSWORD="tu_password_seguro"

# Ejecutar app
./mvnw spring-boot:run
```

#### Windows (PowerShell)

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/babycash"
$env:DB_USERNAME="babycash_user"
$env:DB_PASSWORD="tu_password_seguro"

# Ejecutar app
.\mvnw spring-boot:run
```

#### IntelliJ IDEA

1. Run → Edit Configurations
2. Environment variables:
   ```
   DB_URL=jdbc:postgresql://localhost:5432/babycash;
   DB_USERNAME=babycash_user;
   DB_PASSWORD=tu_password_seguro
   ```

#### Docker

```dockerfile
ENV DB_URL=jdbc:postgresql://db:5432/babycash
ENV DB_USERNAME=babycash_user
ENV DB_PASSWORD=tu_password_seguro
```

---

## 🧪 5. Verificar Conexión

### Método 1: Logs de Spring Boot

Al iniciar la aplicación, busca:

```
✅ ÉXITO:
2025-10-30 19:30:00.123  INFO 12345 --- [main] o.h.e.t.j.p.i.JtaPlatformInitiator      : HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
2025-10-30 19:30:00.234  INFO 12345 --- [main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'

❌ ERROR:
PSQLException: FATAL: password authentication failed for user "babycash_user"
```

### Método 2: Crear una Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String name;
    
    // getters y setters
}
```

Si la app inicia sin errores, **la tabla `users` se creó automáticamente**.

### Método 3: Verificar en psql

```bash
psql -U babycash_user -d babycash

# Listar tablas
\dt

# Deberías ver:
#  Schema |   Name   | Type  |     Owner
# --------+----------+-------+----------------
#  public | users    | table | babycash_user
```

---

## 🛠️ 6. Errores Comunes

### Error 1: Driver no encontrado

```
java.lang.ClassNotFoundException: org.postgresql.Driver
```

**Solución:**
Agregar dependencia en `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

Luego:
```bash
./mvnw clean install
```

### Error 2: Contraseña incorrecta

```
PSQLException: FATAL: password authentication failed for user "babycash_user"
```

**Solución:**
Verificar usuario y contraseña en PostgreSQL:
```bash
psql -U postgres

ALTER USER babycash_user WITH PASSWORD 'nueva_password';
```

### Error 3: Base de datos no existe

```
PSQLException: FATAL: database "babycash" does not exist
```

**Solución:**
Crear base de datos:
```bash
psql -U postgres

CREATE DATABASE babycash;
GRANT ALL PRIVILEGES ON DATABASE babycash TO babycash_user;
```

### Error 4: Puerto incorrecto

```
ConnectException: Connection refused (Connection refused)
```

**Solución:**
Verificar que PostgreSQL esté corriendo:
```bash
# Linux
sudo systemctl status postgresql

# Si no está corriendo
sudo systemctl start postgresql

# macOS
brew services list
brew services start postgresql
```

### Error 5: Dialecto incorrecto

```
HHH000400: Using dialect: org.hibernate.dialect.MySQL5Dialect
```

**Solución:**
Especificar dialecto PostgreSQL:
```properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## 🎯 7. Configuración BabyCash (Completa)

### application.properties

```properties
# ========================================
# SERVER
# ========================================
server.port=8080
server.servlet.context-path=/api

# ========================================
# DATABASE
# ========================================
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/babycash}
spring.datasource.username=${DB_USERNAME:babycash_user}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=org.postgresql.Driver

# ========================================
# JPA / HIBERNATE
# ========================================
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=${SHOW_SQL:true}
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}

# ========================================
# LOGGING
# ========================================
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### pom.xml (Dependencias de BD)

```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Validación (opcional) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## 📊 Flujo de Conexión

```
1. Spring Boot inicia
   ↓
2. Lee application.properties
   ↓
3. Carga Driver JDBC (postgresql.jar)
   ↓
4. Establece conexión con PostgreSQL
   |  URL: jdbc:postgresql://localhost:5432/babycash
   |  User: babycash_user
   |  Password: tu_password_seguro
   ↓
5. Hibernate escanea @Entity
   ↓
6. Compara entities con tablas en BD
   ↓
7. Si ddl-auto=update:
   - Crea tablas faltantes
   - Agrega columnas faltantes
   ↓
8. Aplicación lista ✅
```

---

## 📋 Resumen

| Componente | Ubicación | Propósito |
|------------|-----------|-----------|
| **PostgreSQL Driver** | `pom.xml` | Permite comunicación Java ↔ PostgreSQL |
| **Spring Data JPA** | `pom.xml` | Framework para mapear objetos a BD |
| **URL** | `application.properties` | Dirección de la base de datos |
| **Username/Password** | `application.properties` | Credenciales de acceso |
| **Dialect** | `application.properties` | Sintaxis SQL específica de PostgreSQL |
| **DDL Auto** | `application.properties` | Creación/actualización automática de tablas |

---

**Última actualización**: Octubre 2025
