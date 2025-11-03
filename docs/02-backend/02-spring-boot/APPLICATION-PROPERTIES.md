# ⚙️ APPLICATION.PROPERTIES

## 🎯 ¿Para Qué Sirve?

**Explicación Simple:**
`application.properties` es el **archivo de configuración** de Spring Boot. Es como el **panel de control** donde defines cómo debe funcionar tu aplicación.

**Ubicación:**
```
src/main/resources/application.properties
```

---

## 📋 Estructura Básica

```properties
# Comentario (línea ignorada)
clave=valor
clave.subclave=valor
```

---

## 🔧 Configuraciones del Proyecto

### 1. Servidor

```properties
# Puerto del servidor (default: 8080)
server.port=8080

# Context path (prefijo de todas las URLs)
# server.context-path=/api
```

**¿Qué hace?**
- `server.port=8080`: La app se ejecuta en `http://localhost:8080`
- `server.port=3000`: La app se ejecuta en `http://localhost:3000`

---

### 2. Base de Datos (PostgreSQL)

```properties
# URL de conexión
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash

# Usuario
spring.datasource.username=postgres

# Contraseña
spring.datasource.password=${DB_PASSWORD}

# Driver (se detecta automáticamente, pero se puede especificar)
spring.datasource.driver-class-name=org.postgresql.Driver
```

**¿Qué significa cada parte?**

```
jdbc:postgresql://localhost:5432/babycash
│    │            │         │     │
│    │            │         │     └─ Nombre de la BD
│    │            │         └─ Puerto de PostgreSQL
│    │            └─ Host (localhost = misma máquina)
│    └─ Tipo de BD (postgresql)
└─ Protocolo JDBC
```

---

### 3. JPA / Hibernate

```properties
# Crear/actualizar tablas automáticamente
spring.jpa.hibernate.ddl-auto=update

# Mostrar SQL en consola (útil para debug)
spring.jpa.show-sql=true

# Formatear SQL (más legible)
spring.jpa.properties.hibernate.format_sql=true

# Dialecto de PostgreSQL
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**¿Qué hace `ddl-auto`?**

| Valor | Qué Hace |
|-------|----------|
| `none` | No hace nada con las tablas |
| `validate` | Solo valida que las tablas coincidan con entities |
| `update` | Actualiza tablas (agrega columnas si faltan) ✅ |
| `create` | Elimina y recrea tablas cada vez (⚠️ pierde datos) |
| `create-drop` | Crea al iniciar, elimina al cerrar |

**Recomendado:** `update` en desarrollo, `validate` en producción.

---

### 4. JWT

```properties
# Clave secreta para firmar JWT (debe ser larga y compleja)
app.jwt.secret=${JWT_SECRET}

# Tiempo de expiración en milisegundos (86400000 = 24 horas)
app.jwt.expiration-ms=86400000

# Refresh token expiration (30 días)
app.jwt.refresh-expiration-ms=2592000000
```

**¿Por qué `${JWT_SECRET}`?**
Lee el valor desde **variable de entorno** (archivo `.env`). Más seguro que escribir la clave directamente.

---

### 5. Email (SMTP)

```properties
# Servidor SMTP
spring.mail.host=smtp.gmail.com

# Puerto
spring.mail.port=587

# Usuario (email)
spring.mail.username=${EMAIL_USERNAME}

# Contraseña de aplicación
spring.mail.password=${EMAIL_PASSWORD}

# Propiedades adicionales
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com

# Email del remitente
app.email.from=noreply@babycash.com
```

---

### 6. Logging

```properties
# Nivel de log general (ERROR, WARN, INFO, DEBUG, TRACE)
logging.level.root=INFO

# Nivel de log para paquetes específicos
logging.level.com.babycash.backend=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# Guardar logs en archivo
logging.file.name=logs/babycash.log
```

---

### 7. CORS

```properties
# Orígenes permitidos (frontend)
app.cors.allowed-origins=http://localhost:5173,http://localhost:3000
```

---

### 8. Multipart (Subida de Archivos)

```properties
# Tamaño máximo por archivo (10MB)
spring.servlet.multipart.max-file-size=10MB

# Tamaño máximo de la petición (10MB)
spring.servlet.multipart.max-request-size=10MB
```

---

## 🔐 Variables de Entorno (.env)

### ¿Por Qué Usar Variables de Entorno?

**Problema:**
```properties
# ❌ MAL - Contraseña en texto plano
spring.datasource.password=miPasswordSecreto123
app.jwt.secret=claveSecretaSuperSegura
```

Si subes esto a GitHub, **todos ven tu contraseña**.

**Solución:**
```properties
# ✅ BIEN - Lee desde variable de entorno
spring.datasource.password=${DB_PASSWORD}
app.jwt.secret=${JWT_SECRET}
```

### Archivo .env

```bash
# .env (en la raíz del proyecto backend)
DB_PASSWORD=miPasswordSecreto123
JWT_SECRET=claveSecretaSuperSegura123456789012345678901234567890
EMAIL_USERNAME=babycashnoreply@gmail.com
EMAIL_PASSWORD=pcsguuqqlmfvjhaf
```

**⚠️ Importante:** Agregar `.env` al `.gitignore`

```
# .gitignore
.env
```

---

## 📊 Configuración Completa del Proyecto

```properties
# ============================================
# SERVIDOR
# ============================================
server.port=8080

# ============================================
# BASE DE DATOS
# ============================================
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# ============================================
# JPA / HIBERNATE
# ============================================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# ============================================
# JWT
# ============================================
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=86400000
app.jwt.refresh-expiration-ms=2592000000

# ============================================
# EMAIL (GMAIL SMTP)
# ============================================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
app.email.from=noreply@babycash.com

# ============================================
# CORS
# ============================================
app.cors.allowed-origins=http://localhost:5173,http://localhost:3000

# ============================================
# LOGGING
# ============================================
logging.level.root=INFO
logging.level.com.babycash.backend=DEBUG
logging.file.name=logs/babycash.log

# ============================================
# MULTIPART (Upload)
# ============================================
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

---

## 🎓 Cómo Usar Valores en el Código

### Con @Value

```java
@Component
public class JwtUtil {
    
    // Inyectar valor desde application.properties
    @Value("${app.jwt.secret}")
    private String secretKey;
    
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;
    
    public String generateToken(String email) {
        return Jwts.builder()
            .setSubject(email)
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
}
```

### Con @ConfigurationProperties

```java
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {
    
    private String secret;
    private long expirationMs;
    private long refreshExpirationMs;
    
    // Getters y Setters
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    // ... más getters/setters
}
```

---

## 🔄 Perfiles (Profiles)

### ¿Qué son?

Diferentes configuraciones para diferentes entornos (desarrollo, producción).

### Archivos

```
application.properties              # Configuración base
application-dev.properties          # Desarrollo
application-prod.properties         # Producción
```

### Activar Perfil

```properties
# application.properties
spring.profiles.active=dev
```

O al ejecutar:

```bash
java -jar babycash.jar --spring.profiles.active=prod
```

### Ejemplo

**application-dev.properties**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash_dev
spring.jpa.show-sql=true
logging.level.root=DEBUG
```

**application-prod.properties**
```properties
spring.datasource.url=jdbc:postgresql://db.produccion.com:5432/babycash
spring.jpa.show-sql=false
logging.level.root=WARN
```

---

## ⚠️ Errores Comunes

### 1. Valor No Encontrado

```
Error: Could not resolve placeholder 'app.jwt.secret'
```

**Causa:** La clave no existe en `application.properties` o `.env`.

**Solución:**
```properties
# Agregar en application.properties
app.jwt.secret=${JWT_SECRET:defaultSecretKey}
                           └─ Valor por defecto
```

### 2. Puerto Ocupado

```
Error: Port 8080 already in use
```

**Solución:**
```properties
# Cambiar puerto
server.port=8081
```

### 3. Conexión a BD Falla

```
Error: Connection refused: localhost:5432
```

**Causa:** PostgreSQL no está corriendo.

**Solución:**
```bash
sudo systemctl start postgresql
```

---

## 📋 Resumen

| Configuración | Para Qué | Ejemplo |
|---------------|----------|---------|
| `server.port` | Puerto del servidor | `8080` |
| `spring.datasource.url` | URL de PostgreSQL | `jdbc:postgresql://...` |
| `spring.jpa.hibernate.ddl-auto` | Manejo de tablas | `update` |
| `app.jwt.secret` | Clave JWT | `${JWT_SECRET}` |
| `spring.mail.host` | Servidor SMTP | `smtp.gmail.com` |
| `logging.level.root` | Nivel de logs | `INFO` |

---

**Última actualización**: Octubre 2025
