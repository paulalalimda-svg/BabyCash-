# 🔒 ENCRIPTACIÓN DE PASSWORDS

## 📖 ¿Qué es la Encriptación de Passwords?

La **encriptación de passwords** es el proceso de **convertir una contraseña legible en un texto ilegible** para protegerla.

---

## 🎭 Analogía Simple

### Caja Fuerte 🔐

Imagina que guardas dinero en tu casa:

**❌ SIN Encriptación (MAL):**
```
Dejas el dinero en una mesa
Cualquiera que entre puede verlo
Si alguien roba, ven todo
```

**✅ CON Encriptación (BIEN):**
```
Metes el dinero en una caja fuerte con combinación
Solo tú conoces la combinación
Si alguien roba la caja, no pueden abrirla
```

**Encriptar password = Meter en caja fuerte** 🔐

---

## ⚠️ ¿Por qué NUNCA guardar passwords en texto plano?

### Ejemplo: Base de datos comprometida

**❌ SIN Encriptación:**

```sql
SELECT * FROM users;

| id | email              | password     | role  |
|----|-------------------|--------------|-------|
| 1  | maria@gmail.com   | password123  | USER  |
| 2  | juan@gmail.com    | qwerty456    | USER  |
| 3  | admin@babycash.com| admin123     | ADMIN |
```

**Si un hacker accede a la base de datos:**
- ✅ Puede ver todos los passwords
- ✅ Puede hacer login como cualquier usuario
- ✅ Puede acceder como ADMIN
- 💀 **DESASTRE TOTAL**

---

**✅ CON Encriptación:**

```sql
SELECT * FROM users;

| id | email              | password                                                      | role  |
|----|-------------------|--------------------------------------------------------------|-------|
| 1  | maria@gmail.com   | $2a$10$xQhR5Z8Z3Y2Z8Z8Z8Z8Z8OqM7Qb1Z2Z3Z4Z5Z6Z7Z8Z9Z0Z1Z2    | USER  |
| 2  | juan@gmail.com    | $2a$10$aB3C4d5E6f7G8h9I0j1K2L3M4N5O6P7Q8R9S0T1U2V3W4X5Y6Z7   | USER  |
| 3  | admin@babycash.com| $2a$10$yZ1x2W3v4U5t6S7r8Q9p0O1n2M3l4K5j6I7h8G9f0E1d2C3b4A5   | ADMIN |
```

**Si un hacker accede:**
- ❌ NO puede ver los passwords originales
- ❌ NO puede hacer login (los hash no funcionan como passwords)
- ✅ Base de datos segura
- 😌 **PROTEGIDO**

---

## 🔐 Hash vs Encriptación

### Encriptación (Reversible)

**Puedes desencriptar para obtener el original.**

```
Texto original:  "password123"
       ↓ Encriptar con llave
Texto encriptado: "x7Ks9mPq2..."
       ↓ Desencriptar con llave
Texto original:  "password123"  ← Recuperado
```

**Ejemplo:** AES, RSA

---

### Hash (Irreversible) ✅

**NO puedes obtener el original, es de un solo sentido.**

```
Texto original: "password123"
       ↓ Hash
Hash: "$2a$10$xQhR5Z8Z3Y2Z8Z8Z8Z8Z8O..."
       ↓ ❌ NO se puede revertir
```

**Para validar:**
```
Usuario ingresa: "password123"
       ↓ Hash
Resultado: "$2a$10$xQhR5Z8Z3Y2Z8Z8Z8Z8Z8O..."
       ↓ Comparar
Hash guardado: "$2a$10$xQhR5Z8Z3Y2Z8Z8Z8Z8Z8O..."
       ↓
✅ SON IGUALES → Password correcto
```

---

## 🧂 BCrypt: El Mejor Hash para Passwords

**BCrypt** es un algoritmo de hash diseñado específicamente para passwords.

### Características

1. **Lento a propósito**: Dificulta ataques de fuerza bruta
2. **Salt automático**: Cada password tiene un salt único
3. **Configurable**: Puedes ajustar la dificultad
4. **Seguro**: Usado por empresas grandes

---

### Estructura de un Hash BCrypt

```
$2a$10$xQhR5Z8Z3Y2Z8Z8Z8Z8Z8OqM7Qb1Z2Z3Z4Z5Z6Z7Z8Z9Z0Z1Z2
│ │ │  │                                              │
│ │ │  │                                              └─ Hash (31 caracteres)
│ │ │  └─ Salt (22 caracteres)
│ │ └─ Cost factor (número de rondas: 2^10 = 1024)
│ └─ Versión del algoritmo
└─ Identificador BCrypt
```

---

### ¿Qué es el Salt? 🧂

**Salt** es un valor aleatorio agregado al password antes de hacer hash.

**SIN Salt (INSEGURO):**
```
password123 → hash → $2a$10$ABC...
password123 → hash → $2a$10$ABC...  ← Siempre el mismo hash
```

**Si dos usuarios tienen el mismo password:**
```sql
| email              | password (sin salt)              |
|-------------------|----------------------------------|
| maria@gmail.com   | $2a$10$ABC...                   |
| juan@gmail.com    | $2a$10$ABC...  ← MISMO HASH     |
```

💀 **Problema:** Hacker sabe que ambos tienen el mismo password.

---

**CON Salt (SEGURO):**
```
password123 + salt1 → hash → $2a$10$XYZ...
password123 + salt2 → hash → $2a$10$QWE...  ← Diferente hash
```

**Aunque tengan el mismo password:**
```sql
| email              | password (con salt)              |
|-------------------|----------------------------------|
| maria@gmail.com   | $2a$10$XYZ...                   |
| juan@gmail.com    | $2a$10$QWE...  ← DIFERENTE      |
```

✅ **Cada hash es único** incluso con el mismo password.

---

## 🔧 Implementación en BabyCash

### 1. Dependencia (ya incluida en Spring Security)

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Spring Security incluye BCrypt automáticamente.

---

### 2. Configurar PasswordEncoder

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // Por defecto usa cost factor 10
    }
    
    // Con cost factor personalizado
    @Bean
    public PasswordEncoder passwordEncoderCustom() {
        return new BCryptPasswordEncoder(12);  // Más seguro pero más lento
    }
}
```

**Cost Factor:**
- `10` = 2^10 = 1,024 rondas (por defecto)
- `12` = 2^12 = 4,096 rondas (más seguro)
- Cada +1 duplica el tiempo de procesamiento

---

### 3. Encriptar Password en Registro

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // ← Inyectado
    
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        
        // 1. Validar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email ya registrado");
        }
        
        // 2. Crear usuario
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))  // ← ENCRIPTAR
            .name(request.getName())
            .phone(request.getPhone())
            .address(request.getAddress())
            .role(Role.USER)
            .active(true)
            .build();
        
        // 3. Guardar en BD
        User savedUser = userRepository.save(user);
        
        // 4. Generar token y retornar
        String token = jwtService.generateToken(savedUser);
        return AuthResponseDTO.builder()
            .token(token)
            .user(UserMapper.toDTO(savedUser))
            .build();
    }
}
```

**Ejemplo:**
```
Usuario ingresa:  "password123"
       ↓ passwordEncoder.encode()
Se guarda en BD:  "$2a$10$xQhR5Z8Z3Y2Z8Z8Z8Z8Z8OqM7Qb1Z2Z3Z4Z5Z6Z7Z8Z9Z0Z1Z2"
```

---

### 4. Validar Password en Login

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthResponseDTO login(LoginRequestDTO request) {
        
        // 1. Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        
        // 2. Validar que esté activo
        if (!user.getActive()) {
            throw new UnauthorizedException("Usuario inactivo");
        }
        
        // 3. Verificar password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        
        // 4. Generar token
        String token = jwtService.generateToken(user);
        
        // 5. Retornar
        return AuthResponseDTO.builder()
            .token(token)
            .user(UserMapper.toDTO(user))
            .build();
    }
}
```

**Ejemplo:**
```
Usuario ingresa: "password123"
       ↓ passwordEncoder.matches(input, hashGuardado)
       ↓ Hace hash de "password123" con el mismo salt
       ↓ Compara: hash nuevo == hash guardado
       ↓
✅ SON IGUALES → Credenciales válidas
```

---

## 🎪 Ejemplo Completo: Flujo de Registro y Login

### Registro

```java
// Frontend
POST /api/auth/register
{
  "email": "maria@gmail.com",
  "password": "password123",  ← Texto plano
  "name": "María García"
}

// Backend - AuthService
User user = User.builder()
    .email("maria@gmail.com")
    .password(passwordEncoder.encode("password123"))  // ← Encriptar
    .name("María García")
    .role(Role.USER)
    .build();

userRepository.save(user);

// Base de Datos
INSERT INTO users (email, password, name, role)
VALUES (
  'maria@gmail.com',
  '$2a$10$xQhR5Z8Z3Y2Z8Z8Z8Z8Z8OqM7Qb1Z2Z3Z4Z5Z6Z7Z8Z9Z0Z1Z2',  ← Hash
  'María García',
  'USER'
);
```

---

### Login

```java
// Frontend
POST /api/auth/login
{
  "email": "maria@gmail.com",
  "password": "password123"  ← Texto plano
}

// Backend - AuthService
User user = userRepository.findByEmail("maria@gmail.com");
// user.getPassword() = "$2a$10$xQhR5Z8Z3Y2Z8Z8Z8Z8Z8O..."

boolean isValid = passwordEncoder.matches(
    "password123",  // Password ingresado
    user.getPassword()  // Hash guardado en BD
);

if (!isValid) {
    throw new UnauthorizedException("Credenciales inválidas");
}

// ✅ Password correcto, generar token
String token = jwtService.generateToken(user);
```

---

## 🔍 Método matches() Explicado

```java
passwordEncoder.matches(rawPassword, encodedPassword)
```

**¿Cómo funciona internamente?**

```java
public boolean matches(String rawPassword, String encodedPassword) {
    // 1. Extraer salt del hash guardado
    String salt = extractSalt(encodedPassword);
    
    // 2. Hacer hash del password ingresado con el mismo salt
    String newHash = hash(rawPassword, salt);
    
    // 3. Comparar los dos hashes
    return newHash.equals(encodedPassword);
}
```

**Ejemplo:**
```
Password ingresado: "password123"
Hash guardado:      "$2a$10$ABC123...XYZ789"
                           ↓
Extraer salt:       "ABC123..."
                           ↓
Hash "password123" 
con salt "ABC123...": "$2a$10$ABC123...XYZ789"
                           ↓
Comparar:
  Nuevo hash: "$2a$10$ABC123...XYZ789"
  Hash guardado: "$2a$10$ABC123...XYZ789"
                           ↓
                    ✅ SON IGUALES
```

---

## 🛡️ Seguridad Adicional

### 1. Validar Fortaleza del Password

```java
@Data
public class RegisterRequestDTO {
    
    @NotBlank(message = "Password requerido")
    @Size(min = 8, message = "Password debe tener mínimo 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password debe contener mayúsculas, minúsculas, números y caracteres especiales"
    )
    private String password;
}
```

**Validaciones:**
- ✅ Mínimo 8 caracteres
- ✅ Al menos 1 mayúscula
- ✅ Al menos 1 minúscula
- ✅ Al menos 1 número
- ✅ Al menos 1 carácter especial

---

### 2. Custom Password Validator

```java
@Component
public class PasswordValidator {
    
    public void validate(String password) {
        
        if (password.length() < 8) {
            throw new BadRequestException("Password debe tener mínimo 8 caracteres");
        }
        
        if (!password.matches(".*[A-Z].*")) {
            throw new BadRequestException("Password debe contener al menos una mayúscula");
        }
        
        if (!password.matches(".*[a-z].*")) {
            throw new BadRequestException("Password debe contener al menos una minúscula");
        }
        
        if (!password.matches(".*\\d.*")) {
            throw new BadRequestException("Password debe contener al menos un número");
        }
        
        if (!password.matches(".*[@$!%*?&].*")) {
            throw new BadRequestException("Password debe contener al menos un carácter especial");
        }
        
        // Verificar que no sea un password común
        List<String> commonPasswords = List.of(
            "password", "12345678", "qwerty", "abc123", "password123"
        );
        
        if (commonPasswords.contains(password.toLowerCase())) {
            throw new BadRequestException("Password demasiado común, elige uno más seguro");
        }
    }
}
```

---

### 3. Cambiar Password

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public void changePassword(
        String email, 
        String currentPassword, 
        String newPassword
    ) {
        
        // 1. Buscar usuario
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        // 2. Verificar password actual
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("Password actual incorrecto");
        }
        
        // 3. Verificar que el nuevo password sea diferente
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException("El nuevo password debe ser diferente al actual");
        }
        
        // 4. Encriptar nuevo password
        user.setPassword(passwordEncoder.encode(newPassword));
        
        // 5. Guardar
        userRepository.save(user);
    }
}
```

---

## 🧪 Testing

### Test de Encriptación

```java
@SpringBootTest
class PasswordEncoderTest {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    void testPasswordEncoding() {
        String rawPassword = "password123";
        
        // Encriptar
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // Verificar que NO es el mismo
        assertNotEquals(rawPassword, encodedPassword);
        
        // Verificar que comienza con $2a$ (BCrypt)
        assertTrue(encodedPassword.startsWith("$2a$"));
        
        // Verificar longitud (60 caracteres)
        assertEquals(60, encodedPassword.length());
    }
    
    @Test
    void testPasswordMatching() {
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // Password correcto
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        
        // Password incorrecto
        assertFalse(passwordEncoder.matches("wrongpassword", encodedPassword));
    }
    
    @Test
    void testSamePasswordDifferentHashes() {
        String rawPassword = "password123";
        
        String hash1 = passwordEncoder.encode(rawPassword);
        String hash2 = passwordEncoder.encode(rawPassword);
        
        // Hashes son diferentes (por el salt)
        assertNotEquals(hash1, hash2);
        
        // Pero ambos son válidos
        assertTrue(passwordEncoder.matches(rawPassword, hash1));
        assertTrue(passwordEncoder.matches(rawPassword, hash2));
    }
}
```

---

## 📊 Comparación de Algoritmos

| Algoritmo | Seguridad | Velocidad | Uso Recomendado |
|-----------|-----------|-----------|-----------------|
| **MD5** | ❌ Muy baja | ⚡ Muy rápido | ❌ Nunca para passwords |
| **SHA-1** | ❌ Baja | ⚡ Rápido | ❌ Nunca para passwords |
| **SHA-256** | ⚠️ Media | ⚡ Rápido | ⚠️ Solo con salt |
| **BCrypt** | ✅ Alta | 🐌 Lento (a propósito) | ✅ Passwords |
| **Argon2** | ✅ Muy alta | 🐌 Lento | ✅ Passwords (más moderno) |

**Para passwords, siempre usar BCrypt o Argon2.**

---

## ⚠️ Errores Comunes

### ❌ Error 1: Guardar password en texto plano

```java
// MAL ❌
User user = User.builder()
    .email("maria@gmail.com")
    .password(request.getPassword())  // ← Texto plano
    .build();
```

```java
// BIEN ✅
User user = User.builder()
    .email("maria@gmail.com")
    .password(passwordEncoder.encode(request.getPassword()))  // ← Encriptado
    .build();
```

---

### ❌ Error 2: Comparar passwords con equals()

```java
// MAL ❌
if (request.getPassword().equals(user.getPassword())) {
    // Esto NUNCA será true
}
```

```java
// BIEN ✅
if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    // Correcto
}
```

---

### ❌ Error 3: Usar MD5 o SHA-256 sin salt

```java
// MAL ❌
String hash = DigestUtils.md5Hex(password);  // Inseguro
```

```java
// BIEN ✅
String hash = passwordEncoder.encode(password);  // BCrypt con salt
```

---

## 🎯 Resumen

| Concepto | Significado | Ejemplo |
|----------|-------------|---------|
| **Hash** | Conversión irreversible | `password123` → `$2a$10$ABC...` |
| **Salt** | Valor aleatorio agregado | Hace que cada hash sea único |
| **BCrypt** | Algoritmo de hash seguro | Usado en BabyCash |
| **encode()** | Encriptar password | Al registrar usuario |
| **matches()** | Verificar password | Al hacer login |
| **Cost Factor** | Número de rondas | 10 = 1,024 rondas |

---

## 🔐 Buenas Prácticas

1. ✅ **SIEMPRE** encripta passwords con BCrypt
2. ✅ **NUNCA** guardes passwords en texto plano
3. ✅ Usa `matches()` para comparar, NO `equals()`
4. ✅ Valida fortaleza del password
5. ✅ Usa cost factor 10-12
6. ✅ Passwords mínimo 8 caracteres
7. ✅ Requiere mayúsculas, minúsculas, números y símbolos

---

**Última actualización**: Octubre 2025
