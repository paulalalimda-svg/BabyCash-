# ⚙️ CONFIGURACIÓN DE SPRING SECURITY

## 📖 ¿Qué es SecurityConfig?

**SecurityConfig** es la clase principal donde configuras **toda la seguridad** de tu aplicación:

- 🔐 Qué rutas son públicas y cuáles protegidas
- 🎫 Cómo validar tokens JWT
- 🌐 Configuración de CORS
- 🔒 Filtros de seguridad
- 🚫 Manejo de errores (401, 403)

---

## 🎭 Analogía Simple

### Edificio de Oficinas 🏢

Imagina que eres el **jefe de seguridad** de un edificio:

**Tu trabajo es configurar:**

1. **Puertas abiertas al público** (rutas públicas)
   - Lobby
   - Baños del primer piso
   - Cafetería

2. **Puertas con tarjeta** (requieren autenticación)
   - Oficinas
   - Salas de reuniones

3. **Puertas solo para gerentes** (requieren rol específico)
   - Sala de servidores
   - Oficina de finanzas

4. **Guardias en cada puerta** (filtros)
   - Verifican tarjetas
   - Validan permisos

**SecurityConfig = Tu manual de seguridad del edificio** 📋

---

## 🔧 Clase SecurityConfig Completa

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
            // 1. Deshabilitar CSRF
            .csrf(csrf -> csrf.disable())
            
            // 2. Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 3. Configurar autorización de rutas
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (sin autenticación)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
                .requestMatchers("/api/testimonials").permitAll()
                
                // Rutas protegidas (requieren autenticación)
                .anyRequest().authenticated()
            )
            
            // 4. Sin estado (stateless)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 5. Manejadores de excepciones
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )
            
            // 6. Agregar filtro JWT
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

## 🔍 Explicación Detallada

### 1. Anotaciones de Clase

```java
@Configuration  // Indica que es una clase de configuración
@EnableWebSecurity  // Habilita Spring Security
@EnableMethodSecurity  // Permite usar @PreAuthorize en controllers
@RequiredArgsConstructor  // Inyección de dependencias con Lombok
public class SecurityConfig {
```

**¿Qué hacen?**
- `@Configuration`: Spring carga esta clase al iniciar
- `@EnableWebSecurity`: Activa Spring Security
- `@EnableMethodSecurity`: Permite `@PreAuthorize("hasRole('ADMIN')")`

---

### 2. Deshabilitar CSRF

```java
.csrf(csrf -> csrf.disable())
```

**¿Qué es CSRF?**
- Cross-Site Request Forgery (Falsificación de solicitud entre sitios)
- Ataque donde un sitio malicioso ejecuta acciones en tu nombre

**¿Por qué deshabilitarlo?**
- En APIs REST con JWT, **NO necesitas CSRF**
- CSRF protege aplicaciones con sesiones/cookies
- Con JWT (stateless), el token en el header es suficiente

**Cuándo NO deshabilitar:**
- Aplicaciones con sesiones (session-based)
- Aplicaciones que usan cookies

---

### 3. Configurar CORS

```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

**¿Qué es CORS?**
- Cross-Origin Resource Sharing
- Política de seguridad del navegador
- Bloquea requests de dominios diferentes

**Ejemplo del problema:**

```
Frontend (localhost:5173) intenta llamar a API (localhost:8080)

❌ SIN CORS:
Browser: "¡Alto! Dominio diferente, bloqueado"
```

```
✅ CON CORS:
Browser: "OK, el servidor permite este dominio"
```

**Configuración CORS:**

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Orígenes permitidos
    configuration.setAllowedOrigins(List.of(
        "http://localhost:5173",       // Frontend local
        "https://babycash.com"         // Frontend producción
    ));
    
    // Métodos HTTP permitidos
    configuration.setAllowedMethods(List.of(
        "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
    ));
    
    // Headers permitidos
    configuration.setAllowedHeaders(List.of("*"));  // Todos los headers
    
    // Permitir credenciales (cookies, Authorization header)
    configuration.setAllowCredentials(true);
    
    // Headers expuestos al frontend
    configuration.setExposedHeaders(List.of("Authorization"));
    
    // Tiempo de caché de configuración CORS (1 hora)
    configuration.setMaxAge(3600L);
    
    // Aplicar a todas las rutas
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

### 4. Autorización de Rutas

```java
.authorizeHttpRequests(auth -> auth
    // Rutas públicas
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
    
    // Todo lo demás requiere autenticación
    .anyRequest().authenticated()
)
```

**Tipos de configuración:**

#### Permitir TODO el endpoint

```java
.requestMatchers("/api/auth/**").permitAll()
```

Permite:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`

---

#### Permitir solo método específico

```java
.requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
```

Permite:
- ✅ `GET /api/products`
- ✅ `GET /api/products/1`
- ❌ `POST /api/products` (requiere autenticación)
- ❌ `DELETE /api/products/1` (requiere autenticación)

---

#### Requerir autenticación para todo lo demás

```java
.anyRequest().authenticated()
```

Todas las rutas NO especificadas requieren autenticación.

---

#### Requerir rol específico

```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
```

Solo usuarios con rol ADMIN.

---

#### Múltiples roles

```java
.requestMatchers("/api/orders/all").hasAnyRole("ADMIN", "MODERATOR")
```

ADMIN o MODERATOR.

---

### 5. Política de Sesiones

```java
.sessionManagement(session -> 
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

**Opciones:**

| Política | Descripción | Uso |
|----------|-------------|-----|
| **STATELESS** | Sin sesiones, solo tokens | APIs REST con JWT ✅ |
| **ALWAYS** | Siempre crear sesión | Apps tradicionales |
| **IF_REQUIRED** | Crear si necesario | Por defecto |
| **NEVER** | Nunca crear, usar existente | - |

**En BabyCash usamos STATELESS:**
- No guardamos sesiones en memoria
- Cada request es independiente
- Token JWT contiene toda la info necesaria

---

### 6. Manejadores de Excepciones

```java
.exceptionHandling(exception -> exception
    .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 401
    .accessDeniedHandler(jwtAccessDeniedHandler)            // 403
)
```

#### JwtAuthenticationEntryPoint (401)

Se ejecuta cuando:
- No hay token
- Token inválido
- Token expirado

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
            "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
            "Token inválido o expirado",
            LocalDateTime.now()
        );
        
        response.getWriter().write(jsonResponse);
    }
}
```

---

#### JwtAccessDeniedHandler (403)

Se ejecuta cuando:
- Usuario autenticado pero sin permiso
- USER intenta acceder a ruta de ADMIN

```java
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
            "{\"error\":\"Forbidden\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
            "No tienes permiso para acceder a este recurso",
            LocalDateTime.now()
        );
        
        response.getWriter().write(jsonResponse);
    }
}
```

---

### 7. Agregar Filtro JWT

```java
.addFilterBefore(
    jwtAuthenticationFilter,
    UsernamePasswordAuthenticationFilter.class
)
```

**¿Qué hace?**
- Agrega nuestro filtro JWT **ANTES** del filtro de autenticación de Spring
- Orden: `JwtAuthenticationFilter` → `UsernamePasswordAuthenticationFilter`

**Cadena de filtros:**
```
Request → JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter → Controller
```

---

### 8. PasswordEncoder Bean

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**¿Para qué?**
- Encriptar passwords al registrar
- Verificar passwords al hacer login

**Uso:**
```java
// Encriptar
String hash = passwordEncoder.encode("password123");

// Verificar
boolean valid = passwordEncoder.matches("password123", hash);
```

---

### 9. AuthenticationManager Bean

```java
@Bean
public AuthenticationManager authenticationManager(
    AuthenticationConfiguration config
) throws Exception {
    return config.getAuthenticationManager();
}
```

**¿Para qué?**
- Necesario para hacer login programáticamente
- Spring Security lo usa internamente

**Uso en AuthService:**
```java
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(email, password)
);
```

---

## 🎯 Ejemplos de Configuración por Escenario

### Escenario 1: API Completamente Pública

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()  // Todo público
        );
    
    return http.build();
}
```

---

### Escenario 2: Todo Requiere Autenticación

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated()  // Todo protegido
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

---

### Escenario 3: Mixto (BabyCash)

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // Público
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
            
            // Protegido
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

---

### Escenario 4: Con Roles Específicos

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // Público
            .requestMatchers("/api/auth/**").permitAll()
            
            // Solo ADMIN
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            
            // ADMIN o MODERATOR
            .requestMatchers("/api/orders/all").hasAnyRole("ADMIN", "MODERATOR")
            
            // Resto requiere autenticación
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

---

## 🌐 CORS: Configuraciones Comunes

### Desarrollo Local

```java
configuration.setAllowedOrigins(List.of(
    "http://localhost:5173",
    "http://localhost:3000"
));
```

---

### Producción

```java
configuration.setAllowedOrigins(List.of(
    "https://babycash.com",
    "https://www.babycash.com"
));
```

---

### Desarrollo + Producción

```java
@Value("${app.cors.allowed-origins}")
private String allowedOrigins;

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Leer desde application.properties
    configuration.setAllowedOrigins(
        Arrays.asList(allowedOrigins.split(","))
    );
    
    // ... resto de configuración
}
```

**application.properties:**
```properties
# Desarrollo
app.cors.allowed-origins=http://localhost:5173,http://localhost:3000

# Producción
app.cors.allowed-origins=https://babycash.com,https://www.babycash.com
```

---

### Permitir Cualquier Origen (NO RECOMENDADO EN PRODUCCIÓN)

```java
configuration.setAllowedOriginPatterns(List.of("*"));
```

⚠️ Solo usar en desarrollo.

---

## 📦 Estructura Completa del Paquete Security

```
src/main/java/com/babycash/security/
│
├── config/
│   └── SecurityConfig.java           ← Configuración principal
│
├── filter/
│   └── JwtAuthenticationFilter.java  ← Filtro JWT
│
├── handler/
│   ├── JwtAuthenticationEntryPoint.java  ← Maneja 401
│   └── JwtAccessDeniedHandler.java       ← Maneja 403
│
├── service/
│   ├── JwtService.java                   ← Genera/valida tokens
│   └── CustomUserDetailsService.java    ← Carga usuarios
│
└── util/
    └── SecurityUtils.java                ← Utilidades
```

---

## 🎪 Clase JwtService

```java
@Service
@RequiredArgsConstructor
public class JwtService {
    
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    
    @Value("${jwt.expiration}")
    private Long JWT_EXPIRATION;
    
    @Value("${jwt.refresh-expiration}")
    private Long REFRESH_EXPIRATION;
    
    // Generar token
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    // Generar refresh token
    public String generateRefreshToken(User user) {
        return Jwts.builder()
            .setSubject(user.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    // Validar token
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Extraer email
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    // Extraer claim
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
    
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

---

## 🎪 Clase CustomUserDetailsService

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            user.getActive(),
            true,  // accountNonExpired
            true,  // credentialsNonExpired
            true,  // accountNonLocked
            getAuthorities(user)
        );
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
```

---

## ⚙️ application.properties

```properties
# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=3600000
jwt.refresh-expiration=604800000

# CORS
app.cors.allowed-origins=http://localhost:5173

# Logging
logging.level.org.springframework.security=DEBUG
```

---

## 🎯 Resumen

| Componente | Función | Ubicación |
|------------|---------|-----------|
| **SecurityConfig** | Configuración principal | `config/SecurityConfig.java` |
| **JwtAuthenticationFilter** | Valida tokens | `filter/JwtAuthenticationFilter.java` |
| **JwtService** | Genera/valida JWT | `service/JwtService.java` |
| **CustomUserDetailsService** | Carga usuarios | `service/CustomUserDetailsService.java` |
| **JwtAuthenticationEntryPoint** | Maneja 401 | `handler/JwtAuthenticationEntryPoint.java` |
| **JwtAccessDeniedHandler** | Maneja 403 | `handler/JwtAccessDeniedHandler.java` |

---

## ✅ Checklist de Configuración

- ✅ `@EnableWebSecurity` en SecurityConfig
- ✅ `@EnableMethodSecurity` para @PreAuthorize
- ✅ CSRF deshabilitado
- ✅ CORS configurado
- ✅ Rutas públicas definidas
- ✅ SessionManagement STATELESS
- ✅ JwtAuthenticationFilter agregado
- ✅ Manejadores de excepciones (401, 403)
- ✅ PasswordEncoder Bean
- ✅ AuthenticationManager Bean
- ✅ JWT secret en application.properties
- ✅ CustomUserDetailsService implementado

---

**Última actualización**: Octubre 2025
