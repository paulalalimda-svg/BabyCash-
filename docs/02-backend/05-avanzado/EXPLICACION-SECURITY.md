# 🔐 EXPLICACIÓN DE SEGURIDAD (SECURITY) - BACKEND

## 📌 ¿Qué es la Seguridad en la Aplicación?

### 🎯 Explicación Simple
La **seguridad** es como el **sistema de vigilancia** de un edificio:
- Verifica tu identidad (¿quién eres?)
- Controla el acceso (¿a dónde puedes entrar?)
- Registra todo (¿quién hizo qué?)
- Protege información sensible (passwords, datos personales)

### 🔧 Explicación Técnica
El sistema de seguridad implementa:
- **Autenticación**: Verificar la identidad del usuario (JWT)
- **Autorización**: Verificar los permisos del usuario (Roles: USER/ADMIN)
- **Encriptación**: Proteger passwords (BCrypt)
- **CORS**: Controlar acceso desde el frontend
- **Rate Limiting**: Prevenir ataques DDoS
- **Auditoría**: Registro de acciones críticas

---

## 📂 Archivos de Seguridad

```
backend/src/main/java/com/babycash/backend/
│
├── 📂 security/                        # Seguridad principal (JWT)
│   ├── JwtUtil.java                    # Generar y validar tokens JWT
│   ├── JwtAuthenticationFilter.java    # Filtro para validar JWT en cada petición
│   └── CustomUserDetailsService.java   # Cargar detalles del usuario
│
├── 📂 config/security/                 # Configuración de seguridad
│   ├── SecurityConfig.java             # Configuración principal de Spring Security
│   ├── AuditAspect.java                # Auditoría automática
│   ├── RateLimitConfig.java            # Configuración de rate limiting
│   ├── RateLimitFilter.java            # Filtro para limitar peticiones
│   ├── SecurityHeadersFilter.java      # Headers de seguridad HTTP
│   └── SecurityScheduledTasks.java     # Tareas programadas de seguridad
│
└── 📂 model/enums/
    └── UserRole.java                   # Roles: USER, ADMIN
```

---

## 🎫 1. JWT (JSON Web Token)

### 📍 ¿Qué es JWT?

**Explicación Simple:**
JWT es como una **tarjeta de identificación digital** que te dan cuando haces login. Esta tarjeta contiene:
- Tu email
- Tu rol (USER o ADMIN)
- Fecha de expiración (24 horas)

**Explicación Técnica:**
JWT es un estándar (RFC 7519) para crear tokens de acceso que permiten la autenticación stateless. Consiste en 3 partes separadas por puntos:

```
eyJhbGci.eyJzdWIi.SflKxwRJ  ← Ejemplo de JWT
│        │        │
Header   Payload  Signature
```

### 📄 Estructura de un JWT

```json
// HEADER (Algoritmo)
{
  "alg": "HS256",
  "typ": "JWT"
}

// PAYLOAD (Datos)
{
  "sub": "maria@example.com",  // Subject (usuario)
  "role": "USER",
  "iat": 1698700800,            // Issued at (fecha creación)
  "exp": 1698787200             // Expiration (fecha expiración)
}

// SIGNATURE (Firma)
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secretKey
)
```

---

## 🛠️ 2. JwtUtil.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/security/JwtUtil.java`

### 🎯 ¿Qué hace?
Contiene utilidades para **generar** y **validar** tokens JWT.

### 🔧 Código Explicado

```java
@Component
public class JwtUtil {
    
    @Value("${app.jwt.secret}")
    private String secretKey;  // Clave secreta desde .env
    
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;  // 86400000 = 24 horas
    
    // ========================================
    // GENERAR TOKEN
    // ========================================
    
    public String generateToken(String email) {
        // Fecha actual
        Date now = new Date();
        
        // Fecha de expiración (ahora + 24 horas)
        Date expiryDate = new Date(now.getTime() + expirationMs);
        
        // Crear JWT
        return Jwts.builder()
            .setSubject(email)                    // Email del usuario
            .setIssuedAt(now)                     // Fecha de creación
            .setExpiration(expiryDate)            // Fecha de expiración
            .signWith(SignatureAlgorithm.HS256, secretKey)  // Firmar con clave secreta
            .compact();
    }
    
    // ========================================
    // EXTRAER EMAIL DEL TOKEN
    // ========================================
    
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
        
        return claims.getSubject();  // Retorna el email
    }
    
    // ========================================
    // VALIDAR TOKEN
    // ========================================
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
            
            return true;  // Token válido
            
        } catch (SignatureException ex) {
            log.error("Firma JWT inválida");
            return false;
            
        } catch (MalformedJwtException ex) {
            log.error("Token JWT malformado");
            return false;
            
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT expirado");
            return false;
            
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT no soportado");
            return false;
            
        } catch (IllegalArgumentException ex) {
            log.error("Claims JWT vacío");
            return false;
        }
    }
    
    // ========================================
    // VERIFICAR SI TOKEN EXPIRÓ
    // ========================================
    
    public boolean isTokenExpired(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
        
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
```

### 📊 Ejemplo de Uso

```java
// GENERAR TOKEN
String token = jwtUtil.generateToken("maria@example.com");
// Retorna: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

// VALIDAR TOKEN
boolean isValid = jwtUtil.validateToken(token);
// Retorna: true (si es válido)

// EXTRAER EMAIL
String email = jwtUtil.getEmailFromToken(token);
// Retorna: "maria@example.com"
```

---

## 🔍 3. JwtAuthenticationFilter.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/security/JwtAuthenticationFilter.java`

### 🎯 ¿Qué hace?
**Filtro** que se ejecuta en **CADA petición HTTP** para validar el JWT.

### 📝 Explicación Simple
Es como el **guardia de seguridad** en la entrada de un edificio:
- Revisa tu tarjeta de identificación (JWT)
- Si es válida, te deja pasar
- Si no, te bloquea

### 🔧 Flujo del Filtro

```
1. Cliente hace petición HTTP
   GET /api/products
   Header: Authorization: Bearer eyJhbGci...

2. JwtAuthenticationFilter intercepta
   ↓
3. Extrae token del header "Authorization"
   ↓
4. Valida token con JwtUtil
   ↓
5. Si es válido:
   - Extrae email del token
   - Carga datos del usuario
   - Configura SecurityContext
   - ✅ Permite acceso
   ↓
6. Si NO es válido:
   - ❌ Retorna 401 Unauthorized
```

### 🔧 Código Explicado

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            // 1. OBTENER TOKEN DEL HEADER
            String jwt = getJwtFromRequest(request);
            
            // 2. VALIDAR TOKEN
            if (jwt != null && jwtUtil.validateToken(jwt)) {
                
                // 3. EXTRAER EMAIL
                String email = jwtUtil.getEmailFromToken(jwt);
                
                // 4. CARGAR DETALLES DEL USUARIO
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                
                // 5. CREAR AUTENTICACIÓN
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()  // Roles
                    );
                
                // 6. CONFIGURAR CONTEXTO DE SEGURIDAD
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            
        } catch (Exception ex) {
            log.error("No se pudo configurar autenticación: {}", ex.getMessage());
        }
        
        // 7. CONTINUAR CON LA CADENA DE FILTROS
        filterChain.doFilter(request, response);
    }
    
    // Extraer token del header "Authorization: Bearer <token>"
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Quitar "Bearer "
        }
        
        return null;
    }
}
```

### 📊 Ejemplo de Petición

```http
GET /api/cart HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

↓ JwtAuthenticationFilter

1. Extrae: "eyJhbGci..."
2. Valida con secretKey
3. Extrae email: "maria@example.com"
4. Carga usuario desde BD
5. Configura SecurityContext
6. ✅ Permite acceso al endpoint
```

---

## 👤 4. CustomUserDetailsService.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/security/CustomUserDetailsService.java`

### 🎯 ¿Qué hace?
Carga los detalles del usuario desde la base de datos para Spring Security.

### 🔧 Código

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscar usuario en BD
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> 
                new UsernameNotFoundException("Usuario no encontrado: " + email)
            );
        
        // 2. Verificar que esté activo
        if (!user.isActive()) {
            throw new UsernameNotFoundException("Usuario desactivado");
        }
        
        // 3. Convertir rol a GrantedAuthority
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        
        // 4. Retornar UserDetails
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            user.isActive(),
            true,  // accountNonExpired
            true,  // credentialsNonExpired
            true,  // accountNonLocked
            authorities
        );
    }
}
```

### 📊 Ejemplo

```
loadUserByUsername("maria@example.com")
↓
1. SELECT * FROM users WHERE email = 'maria@example.com'
2. user.role = "USER"
3. authorities = ["ROLE_USER"]
4. Retorna UserDetails con password hash y rol
```

---

## ⚙️ 5. SecurityConfig.java

### 📍 Ubicación
`/backend/src/main/java/com/babycash/backend/config/security/SecurityConfig.java`

### 🎯 ¿Qué hace?
**Configuración principal** de Spring Security. Define:
- Qué endpoints requieren autenticación
- Qué endpoints son públicos
- Qué roles pueden acceder a qué rutas

### 🔧 Código Explicado

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ========================================
            // DESHABILITAR CSRF (no necesario con JWT)
            // ========================================
            .csrf(csrf -> csrf.disable())
            
            // ========================================
            // CONFIGURAR CORS
            // ========================================
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // ========================================
            // AUTORIZACIÓN DE ENDPOINTS
            // ========================================
            .authorizeHttpRequests(auth -> auth
                
                // PÚBLICOS (sin autenticación)
                .requestMatchers(
                    "/api/auth/**",           // Login, registro
                    "/api/products",          // Listar productos
                    "/api/products/{id}",     // Ver producto
                    "/api/blog",              // Ver blogs
                    "/api/testimonials",      // Ver testimonios
                    "/api/contact/messages",  // Enviar mensaje
                    "/api/health",            // Health check
                    "/swagger-ui/**",         // Documentación API
                    "/v3/api-docs/**"
                ).permitAll()
                
                // ADMIN (requiere rol ADMIN)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // RESTO (requiere autenticación)
                .anyRequest().authenticated()
            )
            
            // ========================================
            // CONFIGURAR SESIONES (STATELESS con JWT)
            // ========================================
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // ========================================
            // AGREGAR FILTRO JWT
            // ========================================
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );
        
        return http.build();
    }
    
    // ========================================
    // PASSWORD ENCODER (BCrypt)
    // ========================================
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // ========================================
    // AUTHENTICATION MANAGER
    // ========================================
    
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### 📊 Tabla de Autorización

| Endpoint | Autenticación | Rol Requerido |
|----------|---------------|---------------|
| `POST /api/auth/login` | ❌ No | - |
| `POST /api/auth/register` | ❌ No | - |
| `GET /api/products` | ❌ No | - |
| `GET /api/cart` | ✅ Sí | USER |
| `POST /api/orders` | ✅ Sí | USER |
| `POST /api/admin/products` | ✅ Sí | ADMIN |
| `GET /api/admin/orders` | ✅ Sí | ADMIN |

---

## 🔒 6. BCrypt (Encriptación de Contraseñas)

### 🎯 ¿Qué es BCrypt?

**Explicación Simple:**
BCrypt es una **máquina trituradora** de contraseñas. Convierte:
- `"123456"` → `"$2a$10$xKJ9eF7..."` (irreversible)

**Características:**
- **One-way**: No se puede "desencriptar"
- **Salt**: Agrega datos aleatorios únicos
- **Slow**: Intencionalmente lento para prevenir ataques de fuerza bruta

### 🔧 Uso en el Código

```java
@Autowired
private PasswordEncoder passwordEncoder;

// ========================================
// AL REGISTRAR USUARIO
// ========================================

String plainPassword = "123456";
String hashedPassword = passwordEncoder.encode(plainPassword);
// Retorna: "$2a$10$xKJ9eF7bPq3LMN..."

user.setPassword(hashedPassword);
userRepository.save(user);

// ========================================
// AL HACER LOGIN
// ========================================

String inputPassword = "123456";  // Lo que ingresa el usuario
String storedHash = user.getPassword();  // Hash de la BD

boolean matches = passwordEncoder.matches(inputPassword, storedHash);
// Retorna: true (si coincide)
```

### 📊 Ejemplo Completo

```
REGISTRO:
Usuario ingresa: "MiPassword123"
↓ BCrypt encode
Guardado en BD: "$2a$10$N9qo8uLOickgx2ZMRZoMye1yTfGlY/..."

LOGIN:
Usuario ingresa: "MiPassword123"
↓ BCrypt matches
Compara con: "$2a$10$N9qo8uLOickgx2ZMRZoMye1yTfGlY/..."
✅ Match → Login exitoso

Usuario ingresa: "PasswordIncorrecta"
↓ BCrypt matches
❌ No match → Login fallido
```

---

## 🛡️ 7. CORS (Cross-Origin Resource Sharing)

### 🎯 ¿Qué es CORS?

**Explicación Simple:**
CORS es como una **lista de invitados** para tu API. Solo permite que ciertos sitios web (el frontend) puedan hacer peticiones.

**Problema sin CORS:**
```
Frontend (localhost:5173) intenta llamar API (localhost:8080)
❌ Bloqueado por el navegador
```

**Solución con CORS:**
```
Backend dice: "Permito peticiones desde localhost:5173"
✅ Navegador permite la petición
```

### 🔧 Configuración CORS

```java
@Configuration
public class CorsConfig {
    
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;  // http://localhost:5173,http://localhost:3000
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Orígenes permitidos
        configuration.setAllowedOrigins(
            Arrays.asList(allowedOrigins.split(","))
        );
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        
        // Headers permitidos
        configuration.setAllowedHeaders(
            Arrays.asList("Authorization", "Content-Type", "Accept")
        );
        
        // Permitir credenciales (cookies, JWT)
        configuration.setAllowCredentials(true);
        
        // Aplicar a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
```

---

## 🚦 8. Rate Limiting (Limitar Peticiones)

### 🎯 ¿Qué es Rate Limiting?

**Explicación Simple:**
Es como un **límite de velocidad** en una carretera. Previene que alguien haga:
- 1000 peticiones por segundo (ataque DDoS)
- Muchos intentos de login fallidos

### 🔧 Implementación

```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    
    // Mapa: IP → Contador de peticiones
    private final Map<String, RateLimitInfo> requestCounts = new ConcurrentHashMap<>();
    
    // Máximo 100 peticiones por minuto por IP
    private static final int MAX_REQUESTS = 100;
    private static final long TIME_WINDOW_MS = 60000;  // 1 minuto
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Obtener IP del cliente
        String clientIp = getClientIp(request);
        
        // 2. Obtener o crear contador
        RateLimitInfo info = requestCounts.computeIfAbsent(
            clientIp,
            k -> new RateLimitInfo()
        );
        
        // 3. Verificar límite
        if (info.isLimitExceeded()) {
            // Bloquear petición
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Demasiadas peticiones. Intenta más tarde.");
            return;
        }
        
        // 4. Incrementar contador
        info.incrementCount();
        
        // 5. Continuar
        filterChain.doFilter(request, response);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

class RateLimitInfo {
    private int count = 0;
    private long windowStart = System.currentTimeMillis();
    
    public synchronized boolean isLimitExceeded() {
        long now = System.currentTimeMillis();
        
        // Resetear ventana de tiempo si pasó 1 minuto
        if (now - windowStart > 60000) {
            count = 0;
            windowStart = now;
        }
        
        return count >= 100;
    }
    
    public synchronized void incrementCount() {
        count++;
    }
}
```

### 📊 Ejemplo

```
IP: 192.168.1.100

Petición 1 (10:00:00): ✅ Permitida (contador: 1/100)
Petición 2 (10:00:01): ✅ Permitida (contador: 2/100)
...
Petición 100 (10:00:10): ✅ Permitida (contador: 100/100)
Petición 101 (10:00:11): ❌ BLOQUEADA (límite excedido)
...
Petición 102 (10:01:01): ✅ Permitida (ventana reseteada)
```

---

## 📝 9. Auditoría (AuditAspect)

### 🎯 ¿Qué es la Auditoría?

**Explicación Simple:**
Es como una **cámara de seguridad** que registra todo lo que pasa:
- Quién hizo login
- Quién modificó un producto
- Quién procesó un pago

### 🔧 Implementación con AOP (Aspect-Oriented Programming)

```java
@Aspect
@Component
public class AuditAspect {
    
    @Autowired
    private AuditService auditService;
    
    // Registrar todas las creaciones de órdenes
    @AfterReturning(
        pointcut = "execution(* com.babycash.backend.service.OrderService.createOrder(..))",
        returning = "result"
    )
    public void auditOrderCreation(JoinPoint joinPoint, OrderResponse result) {
        // Obtener usuario actual
        String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        
        // Registrar en audit_logs
        auditService.log(
            "ORDER_CREATED",
            email,
            "Orden creada: " + result.getOrderNumber()
        );
    }
    
    // Registrar actualizaciones de productos (ADMIN)
    @AfterReturning(
        pointcut = "execution(* com.babycash.backend.service.ProductService.updateProduct(..))"
    )
    public void auditProductUpdate(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long productId = (Long) args[0];
        
        String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        
        auditService.log(
            "PRODUCT_UPDATED",
            email,
            "Producto actualizado: ID=" + productId
        );
    }
}
```

### 📊 Tabla audit_logs

| id | action | user_email | description | created_at |
|----|--------|------------|-------------|------------|
| 1 | LOGIN | maria@gmail.com | Login exitoso | 2025-10-30 19:30 |
| 2 | ORDER_CREATED | maria@gmail.com | Orden creada: ORD-001 | 2025-10-30 19:45 |
| 3 | PRODUCT_UPDATED | admin@babycash.com | Producto actualizado: ID=5 | 2025-10-30 20:00 |
| 4 | PASSWORD_CHANGE | juan@gmail.com | Contraseña actualizada | 2025-10-30 20:15 |

---

## 🔐 10. Roles y Permisos

### 📍 UserRole.java

```java
public enum UserRole {
    USER,   // Usuario normal (cliente)
    ADMIN   // Administrador
}
```

### 🔧 Uso de @PreAuthorize

```java
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    
    // Solo ADMIN puede crear productos
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        // ...
    }
    
    // Solo ADMIN puede eliminar productos
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // ...
    }
}
```

### 📊 Flujo de Autorización

```
Usuario con rol USER intenta:
POST /api/admin/products

↓ SecurityConfig verifica

1. ¿Tiene JWT? ✅ Sí
2. JWT válido? ✅ Sí
3. ¿Tiene rol ADMIN? ❌ No (tiene rol USER)

↓ Spring Security bloquea

HTTP 403 Forbidden
{ "error": "Acceso denegado" }
```

---

## 🔑 FLUJO COMPLETO DE AUTENTICACIÓN

### 1. Registro

```
1. Usuario envía:
   POST /api/auth/register
   { "email": "maria@gmail.com", "password": "123456" }

2. AuthService:
   - Valida que email no exista
   - Encripta password con BCrypt
   - Guarda en BD
   - Genera JWT
   - Envía email de bienvenida

3. Retorna:
   {
     "token": "eyJhbGci...",
     "email": "maria@gmail.com",
     "role": "USER"
   }
```

### 2. Login

```
1. Usuario envía:
   POST /api/auth/login
   { "email": "maria@gmail.com", "password": "123456" }

2. AuthService:
   - Busca usuario en BD
   - Verifica password con BCrypt
   - Genera JWT
   - Genera Refresh Token
   - Registra login en audit_logs

3. Retorna:
   {
     "token": "eyJhbGci...",
     "refreshToken": "550e8400...",
     "email": "maria@gmail.com",
     "role": "USER"
   }
```

### 3. Petición Autenticada

```
1. Frontend envía:
   GET /api/cart
   Header: Authorization: Bearer eyJhbGci...

2. JwtAuthenticationFilter:
   - Extrae token del header
   - Valida con JwtUtil
   - Extrae email
   - Carga usuario
   - Configura SecurityContext

3. CartController:
   - Obtiene email del SecurityContext
   - Busca carrito del usuario
   - Retorna datos

4. Retorna:
   {
     "id": 1,
     "items": [...],
     "total": 150000
   }
```

### 4. Renovar Token (Refresh)

```
1. Frontend detecta que JWT está por expirar (23 horas)

2. Frontend envía:
   POST /api/auth/refresh-token
   { "refreshToken": "550e8400..." }

3. RefreshTokenService:
   - Valida refresh token
   - Verifica que no esté revocado
   - Verifica que no haya expirado
   - Genera nuevo JWT

4. Retorna:
   {
     "token": "eyJhbGci...",  // Nuevo JWT
     "refreshToken": "550e8400..."  // Mismo refresh token
   }
```

---

## 📊 RESUMEN DE SEGURIDAD

| Componente | Función | Importancia |
|------------|---------|-------------|
| **JWT** | Token de autenticación | 🔴 Crítica |
| **BCrypt** | Encriptar passwords | 🔴 Crítica |
| **JwtAuthenticationFilter** | Validar JWT en cada petición | 🔴 Crítica |
| **SecurityConfig** | Configurar permisos | 🔴 Crítica |
| **CORS** | Permitir frontend | 🟡 Media |
| **Rate Limiting** | Prevenir ataques | 🟡 Media |
| **Auditoría** | Registrar acciones | 🟢 Baja |

---

## 🔑 CONCEPTOS CLAVE

### 1. **Autenticación vs Autorización**

- **Autenticación**: ¿Quién eres? (Login con email/password)
- **Autorización**: ¿Qué puedes hacer? (USER vs ADMIN)

### 2. **Stateless con JWT**

```
Sin JWT (stateful):
- Servidor guarda sesión en memoria
- Requiere cookies
- Difícil de escalar

Con JWT (stateless):
- Servidor NO guarda sesión
- Cliente guarda token
- Fácil de escalar
```

### 3. **Why BCrypt?**

```
MD5 (MALO):
"123456" → "e10adc3949ba59abbe56e057f20f883e"
- Siempre da el mismo resultado
- Vulnerable a rainbow tables

BCrypt (BUENO):
"123456" → "$2a$10$xKJ9eF7..."
"123456" → "$2a$10$N9qo8uL..."  (diferente!)
- Cada encode es único (salt)
- Lento intencionalmente
```

---

**Última actualización**: Octubre 2025
**Versión**: 1.0
