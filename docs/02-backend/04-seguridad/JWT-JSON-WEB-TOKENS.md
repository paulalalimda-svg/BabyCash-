# 🎫 JWT - JSON WEB TOKENS

## 📖 ¿Qué es un JWT?

**JWT (JSON Web Token)** es como un **gafete digital** que prueba quién eres y qué permisos tienes.

---

## 🎭 Analogía Simple

### Gafete de Eventos 🎟️

Imagina que vas a un concierto:

**1. Compras tu boleta (Login):**
- Das tu nombre e ID en taquilla
- Te dan un **gafete con tu información**
- El gafete dice: "María García, VIP, Evento #123"

**2. Usas tu gafete para entrar:**
- En cada puerta, muestras tu gafete
- El guardia lo escanea y verifica:
  - ✅ Es un gafete válido (no falsificado)
  - ✅ Es para este evento
  - ✅ No está vencido
  - ✅ Tienes acceso VIP
- Te dejan pasar

**3. El gafete expira:**
- Al final del día, el gafete ya no sirve
- Debes comprar uno nuevo si vuelves mañana

**JWT = Ese gafete digital** 🎫

---

## 🔑 ¿Por qué JWT y no Sesiones?

### Sesiones Tradicionales ❌

```
Cliente                    Servidor
  |                           |
  |------ Login ------------->|
  |                           | 1. Validar credenciales
  |                           | 2. Crear sesión en memoria
  |                           | 3. Guardar sessionID
  |<---- Session Cookie ------|
  |                           |
  |---- Request + Cookie ---->|
  |                           | 1. Buscar sesión en memoria
  |                           | 2. Validar que exista
  |<------- Response ---------|
```

**Problemas:**
- 📦 El servidor debe **guardar** cada sesión en memoria
- 🔄 No funciona bien con múltiples servidores
- 💾 Consume memoria en el servidor

---

### JWT (Stateless) ✅

```
Cliente                    Servidor
  |                           |
  |------ Login ------------->|
  |                           | 1. Validar credenciales
  |                           | 2. Generar JWT (token)
  |<------- JWT Token --------|
  |                           |
  |--- Request + JWT Token -->|
  |                           | 1. Validar JWT
  |                           | 2. Extraer info del token
  |<------- Response ---------|
```

**Ventajas:**
- ✅ El servidor **NO guarda** nada en memoria
- ✅ Funciona perfecto con múltiples servidores
- ✅ El token tiene toda la información necesaria
- ✅ **Stateless** (sin estado)

---

## 🧩 Estructura de un JWT

Un JWT tiene **3 partes** separadas por puntos:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJtYXJpYUBnbWFpbC5jb20iLCJyb2xlIjoiVVNFUiIsImlhdCI6MTYzMDAwMDAwMCwiZXhwIjoxNjMwMDA4NjAwfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

│                    HEADER                   │                    PAYLOAD                    │           SIGNATURE           │
```

### 1. Header (Encabezado)

Información sobre el tipo de token y algoritmo de encriptación.

**Base64 decodificado:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

- `alg`: Algoritmo de encriptación (HS256 = HMAC SHA-256)
- `typ`: Tipo de token (JWT)

---

### 2. Payload (Carga útil)

**La información del usuario y sus permisos.**

**Base64 decodificado:**
```json
{
  "sub": "maria@gmail.com",
  "role": "USER",
  "userId": 1,
  "iat": 1698765432,
  "exp": 1698769032
}
```

**Claims (Reclamaciones):**

| Claim | Significado | Ejemplo |
|-------|-------------|---------|
| `sub` | Subject (email del usuario) | `maria@gmail.com` |
| `role` | Rol del usuario | `USER`, `ADMIN` |
| `userId` | ID del usuario | `1` |
| `iat` | Issued At (cuándo se creó) | `1698765432` |
| `exp` | Expiration (cuándo expira) | `1698769032` (1 hora después) |

**⚠️ IMPORTANTE:** El payload **NO está encriptado**, solo codificado en Base64. Cualquiera puede decodificarlo y leer su contenido. **NUNCA pongas información sensible** (passwords, tarjetas, etc.).

---

### 3. Signature (Firma)

**Garantiza que el token NO fue modificado.**

```javascript
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  SECRET_KEY
)
```

**¿Cómo funciona?**

1. Toma el header y payload
2. Los junta con un punto
3. Los encripta con una **llave secreta** que solo el servidor conoce
4. Genera la firma

**Si alguien modifica el payload:**
- La firma ya no coincide
- El servidor detecta la manipulación
- Rechaza el token ❌

---

## 🔐 ¿Cómo funciona JWT en BabyCash?

### 1. Generar Token (Login)

```java
@Service
@RequiredArgsConstructor
public class JwtService {
    
    @Value("${jwt.secret}")
    private String SECRET_KEY;  // Llave secreta del application.properties
    
    @Value("${jwt.expiration}")
    private Long JWT_EXPIRATION;  // 3600000 (1 hora en milisegundos)
    
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        
        return Jwts.builder()
            .setClaims(claims)                                    // Payload
            .setSubject(user.getEmail())                          // Email del usuario
            .setIssuedAt(new Date())                              // Fecha de creación
            .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))  // Expiración
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // Firma con llave secreta
            .compact();                                           // Generar string
    }
    
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

**application.properties:**
```properties
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=3600000
jwt.refresh-expiration=604800000
```

---

### 2. Validar Token

```java
@Service
public class JwtService {
    
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSignInKey())  // Usa la llave secreta
                .build()
                .parseClaimsJws(token);         // Parsea y valida el token
            
            return true;  // Token válido ✅
            
        } catch (ExpiredJwtException e) {
            // Token expirado ❌
            return false;
        } catch (Exception e) {
            // Token inválido o manipulado ❌
            return false;
        }
    }
    
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
```

---

### 3. Extraer Información del Token

```java
@Service
public class JwtService {
    
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }
    
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
```

---

## 🔄 Flujo Completo en BabyCash

### Paso 1: Login

**Request:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "maria@gmail.com",
  "password": "password123"
}
```

**Backend:**
```java
@PostMapping("/auth/login")
public ResponseEntity<AuthResponseDTO> login(
    @RequestBody LoginRequestDTO request
) {
    // 1. Validar credenciales
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
    
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new UnauthorizedException("Credenciales inválidas");
    }
    
    // 2. Generar token JWT
    String token = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    
    // 3. Retornar token
    return ResponseEntity.ok(
        AuthResponseDTO.builder()
            .token(token)
            .refreshToken(refreshToken)
            .user(UserMapper.toDTO(user))
            .build()
    );
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGUiOiJVU0VSIiwic3ViIjoibWFyaWFAZ21haWwuY29tIiwiaWF0IjoxNjk4NzY1NDMyLCJleHAiOjE2OTg3NjkwMzJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "maria@gmail.com",
    "name": "María García",
    "role": "USER"
  }
}
```

---

### Paso 2: Frontend guarda el token

```javascript
// React
const login = async (email, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  
  const data = await response.json();
  
  // Guardar token en localStorage
  localStorage.setItem('token', data.token);
  localStorage.setItem('refreshToken', data.refreshToken);
  localStorage.setItem('user', JSON.stringify(data.user));
};
```

---

### Paso 3: Frontend envía token en requests

```javascript
// React
const getCart = async () => {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8080/api/cart', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  if (response.status === 401) {
    // Token expirado, hacer logout
    localStorage.clear();
    window.location.href = '/login';
  }
  
  return await response.json();
};
```

---

### Paso 4: Backend valida token

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Extraer token del header
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = authHeader.substring(7);  // Quitar "Bearer "
        
        try {
            // 2. Validar token
            if (!jwtService.isTokenValid(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            // 3. Extraer email del token
            String email = jwtService.extractEmail(token);
            
            // 4. Cargar usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            
            // 5. Crear autenticación
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
            
            // 6. Guardar en contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // 7. Continuar con el request
        filterChain.doFilter(request, response);
    }
}
```

---

### Paso 5: Controller usa información del token

```java
@GetMapping("/cart")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<CartDTO> getCart(
    @AuthenticationPrincipal UserDetails userDetails  // ← Info del token
) {
    // userDetails.getUsername() → "maria@gmail.com"
    // userDetails.getAuthorities() → [ROLE_USER]
    
    String email = userDetails.getUsername();
    CartDTO cart = cartService.getCart(email);
    return ResponseEntity.ok(cart);
}
```

---

## ⏰ Expiración y Refresh Token

### Access Token (Token de Acceso)

- ⏱️ **Vida corta**: 1 hora
- 🎯 **Uso**: Acceder a recursos protegidos
- 🔄 **Cuando expira**: Usar refresh token para obtener uno nuevo

### Refresh Token (Token de Refresco)

- ⏱️ **Vida larga**: 7 días
- 🎯 **Uso**: Obtener un nuevo access token
- 🔄 **Cuando expira**: Usuario debe hacer login de nuevo

---

### Generar Refresh Token

```java
@Service
public class JwtService {
    
    @Value("${jwt.refresh-expiration}")
    private Long REFRESH_EXPIRATION;  // 604800000 (7 días)
    
    public String generateRefreshToken(User user) {
        return Jwts.builder()
            .setSubject(user.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}
```

---

### Renovar Access Token

**Request:**
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Backend:**
```java
@PostMapping("/auth/refresh")
public ResponseEntity<AuthResponseDTO> refreshToken(
    @RequestBody RefreshTokenRequestDTO request
) {
    // 1. Validar refresh token
    if (!jwtService.isTokenValid(request.getRefreshToken())) {
        throw new UnauthorizedException("Refresh token inválido o expirado");
    }
    
    // 2. Extraer email
    String email = jwtService.extractEmail(request.getRefreshToken());
    
    // 3. Buscar usuario
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    
    // 4. Generar nuevos tokens
    String newToken = jwtService.generateToken(user);
    String newRefreshToken = jwtService.generateRefreshToken(user);
    
    // 5. Retornar
    return ResponseEntity.ok(
        AuthResponseDTO.builder()
            .token(newToken)
            .refreshToken(newRefreshToken)
            .build()
    );
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // Nuevo
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."  // Nuevo
}
```

---

### Manejo Automático en Frontend

```javascript
// React - Interceptor de axios
axios.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;
    
    // Si es 401 y no hemos intentado renovar
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Intentar renovar token
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post('/api/auth/refresh', { refreshToken });
        
        // Guardar nuevo token
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('refreshToken', response.data.refreshToken);
        
        // Reintentar request original con nuevo token
        originalRequest.headers['Authorization'] = `Bearer ${response.data.token}`;
        return axios(originalRequest);
        
      } catch (refreshError) {
        // Refresh token también expiró, hacer logout
        localStorage.clear();
        window.location.href = '/login';
      }
    }
    
    return Promise.reject(error);
  }
);
```

---

## 🛡️ Seguridad del JWT

### ✅ Buenas Prácticas

1. **Llave Secreta Fuerte:**
```properties
# ❌ MAL
jwt.secret=mysecret

# ✅ BIEN
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

2. **Expiraciones Cortas:**
```properties
jwt.expiration=3600000          # 1 hora
jwt.refresh-expiration=604800000 # 7 días
```

3. **HTTPS en Producción:**
```
❌ http://api.babycash.com
✅ https://api.babycash.com
```

4. **No guardar info sensible en el payload:**
```json
// ❌ MAL
{
  "email": "maria@gmail.com",
  "password": "password123",      // ¡NUNCA!
  "creditCard": "1234-5678-9012"  // ¡NUNCA!
}

// ✅ BIEN
{
  "sub": "maria@gmail.com",
  "userId": 1,
  "role": "USER"
}
```

---

### ⚠️ Vulnerabilidades Comunes

1. **Llave secreta débil**: Puede ser crackeada
2. **Sin expiración**: Token válido para siempre
3. **Guardar en localStorage**: Vulnerable a XSS
4. **Sin HTTPS**: Token puede ser interceptado

---

## 🎯 Resumen

| Concepto | Explicación | Ejemplo BabyCash |
|----------|-------------|------------------|
| **JWT** | Token con información del usuario | `eyJhbGciOiJIUzI1NiIs...` |
| **Header** | Tipo y algoritmo | `{"alg": "HS256", "typ": "JWT"}` |
| **Payload** | Datos del usuario | `{"sub": "maria@gmail.com", "role": "USER"}` |
| **Signature** | Garantiza integridad | Encriptado con llave secreta |
| **Access Token** | Token de acceso (1 hora) | Para requests normales |
| **Refresh Token** | Token de refresco (7 días) | Para renovar access token |
| **Stateless** | Sin estado en servidor | No guarda sesiones |

---

## 📦 Dependencias JWT

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

---

**Última actualización**: Octubre 2025
