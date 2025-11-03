# 🔐 SPRING SECURITY - INTRODUCCIÓN

## 📖 ¿Qué es Spring Security?

**Spring Security** es como el **guardia de seguridad** de tu aplicación. Se encarga de:

- ✅ **Autenticación**: "¿Quién eres?" (Login)
- ✅ **Autorización**: "¿Qué puedes hacer?" (Permisos)
- ✅ **Protección**: Evitar ataques y accesos no autorizados

---

## 🎭 Analogía Simple

Imagina un edificio con seguridad:

### 🏢 Edificio = Aplicación BabyCash

**1. Recepción (Autenticación):**
- Llegas al edificio
- El guardia te pide identificación
- Si tu ID es válido, te da un **gafete** (token)
- Con el gafete puedes entrar

**2. Ascensores (Autorización):**
- Con tu gafete, intentas usar el ascensor
- El gafete tiene tu **nivel de acceso**:
  - 👤 **Empleado normal**: Solo pisos 1-5
  - 👑 **Gerente**: Todos los pisos
- El ascensor lee tu gafete y decide si te deja pasar

**3. Seguridad 24/7 (Filtros):**
- Hay cámaras en todos lados
- Guardias revisan cada puerta
- Si intentas algo sospechoso, te detienen

---

## 🔑 Conceptos Clave

### 1. Autenticación (Authentication)

**¿Quién eres?**

Proceso de **verificar tu identidad**.

**Ejemplo:**
```
Usuario: "Soy María García"
Sistema: "Demuéstralo con email y password"
Usuario: maria@gmail.com / password123
Sistema: "✅ Correcto, eres María"
Sistema: "Aquí está tu token (gafete)"
```

---

### 2. Autorización (Authorization)

**¿Qué puedes hacer?**

Proceso de **verificar tus permisos**.

**Ejemplo:**
```
María: "Quiero eliminar un producto"
Sistema: "Veamos tu token..."
Sistema: "Tu rol es USER"
Sistema: "❌ Solo ADMIN puede eliminar productos"
```

---

### 3. Principal

**Tu identidad dentro del sistema.**

Después de autenticarte, el sistema sabe quién eres en todo momento.

```java
@GetMapping("/cart")
public ResponseEntity<CartDTO> getCart(
    @AuthenticationPrincipal UserDetails userDetails
) {
    // userDetails.getUsername() → "maria@gmail.com"
    // userDetails.getAuthorities() → [ROLE_USER]
}
```

---

### 4. Authorities (Autoridades)

**Tus permisos.**

Son los roles o permisos que tienes.

```java
// En BabyCash:
ROLE_USER    → Usuario normal
ROLE_ADMIN   → Administrador
ROLE_MODERATOR → Moderador
```

---

## 🛡️ ¿Por qué necesitamos Spring Security?

### Sin Spring Security ❌

```java
@GetMapping("/products")
public List<Product> getProducts() {
    // ⚠️ CUALQUIERA puede ver los productos
    // ⚠️ No sabemos quién es el usuario
    // ⚠️ No podemos restringir acceso
    return productRepository.findAll();
}

@DeleteMapping("/products/{id}")
public void deleteProduct(@PathVariable Long id) {
    // ⚠️ CUALQUIERA puede eliminar productos
    // ⚠️ Incluso usuarios NO registrados
    // ⚠️ ¡PELIGRO!
    productRepository.deleteById(id);
}
```

### Con Spring Security ✅

```java
@GetMapping("/products")
public List<Product> getProducts() {
    // ✅ Cualquiera puede ver productos (público)
    return productRepository.findAll();
}

@DeleteMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")  // ← Solo ADMIN
public void deleteProduct(@PathVariable Long id) {
    // ✅ Solo ADMIN puede eliminar
    // ✅ Si no eres ADMIN → 403 Forbidden
    productRepository.deleteById(id);
}

@GetMapping("/cart")
@PreAuthorize("isAuthenticated()")  // ← Requiere login
public CartDTO getCart(
    @AuthenticationPrincipal UserDetails userDetails
) {
    // ✅ Solo usuarios logueados
    // ✅ Sabemos quién es el usuario
    String email = userDetails.getUsername();
    return cartService.getCart(email);
}
```

---

## 🔄 Flujo de Spring Security en BabyCash

### 1. Usuario hace Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "maria@gmail.com",
  "password": "password123"
}
```

### 2. Sistema valida credenciales

```java
@Service
public class AuthService {
    
    public AuthResponseDTO login(LoginRequestDTO request) {
        // 1. Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        
        // 2. Verificar password (encriptado)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        
        // 3. Generar token JWT
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // 4. Retornar token
        return AuthResponseDTO.builder()
            .token(token)
            .refreshToken(refreshToken)
            .user(UserMapper.toDTO(user))
            .build();
    }
}
```

### 3. Usuario recibe token

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "maria@gmail.com",
    "role": "USER"
  }
}
```

### 4. Usuario usa token en requests

```http
GET /api/cart
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 5. Spring Security valida token

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
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
        
        String token = authHeader.substring(7);
        
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
        
        // 6. Guardar autenticación en el contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 7. Continuar con el request
        filterChain.doFilter(request, response);
    }
}
```

### 6. Controller recibe usuario autenticado

```java
@GetMapping("/cart")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<CartDTO> getCart(
    @AuthenticationPrincipal UserDetails userDetails
) {
    // ✅ userDetails ya está disponible
    // ✅ Spring Security lo inyectó automáticamente
    String email = userDetails.getUsername();
    CartDTO cart = cartService.getCart(email);
    return ResponseEntity.ok(cart);
}
```

---

## 🔗 Filter Chain (Cadena de Filtros)

Spring Security funciona con **filtros** que procesan cada request antes de llegar al controller.

### Diagrama de Flujo

```
Cliente → [1. CORS Filter]
       → [2. JWT Authentication Filter]
       → [3. Authorization Filter]
       → [4. Exception Handler Filter]
       → Controller
```

### 1. CORS Filter

Permite requests desde el frontend.

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

### 2. JWT Authentication Filter

Valida el token y autentica al usuario.

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Código mostrado anteriormente
}
```

### 3. Authorization Filter

Verifica que el usuario tenga permisos.

```java
@DeleteMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")  // ← Verifica aquí
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

### 4. Exception Handler Filter

Maneja errores de seguridad.

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        // Retorna 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"error\":\"Unauthorized\",\"message\":\"Token inválido o expirado\"}"
        );
    }
}
```

---

## 📦 Dependencias en BabyCash

### pom.xml

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
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

## 🎯 Rutas Públicas vs Protegidas en BabyCash

### Rutas Públicas (No requieren token)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/api/auth/**").permitAll()      // Login, Register
                .requestMatchers("/api/products/**").permitAll()  // Ver productos
                .requestMatchers("/api/blog/**").permitAll()      // Ver blog
                
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
```

### Rutas Protegidas (Requieren token)

```java
// Requiere estar logueado
@GetMapping("/api/cart")
@PreAuthorize("isAuthenticated()")
public CartDTO getCart() { }

@PostMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
public OrderDTO createOrder() { }

// Requiere rol ADMIN
@PostMapping("/api/products")
@PreAuthorize("hasRole('ADMIN')")
public ProductDTO createProduct() { }

@DeleteMapping("/api/products/{id}")
@PreAuthorize("hasRole('ADMIN')")
public void deleteProduct() { }
```

---

## 🔐 Componentes Principales

### 1. SecurityConfig

Configuración principal de Spring Security.

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Configurar rutas, filtros, CORS, etc.
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

### 2. JwtService

Maneja la generación y validación de tokens JWT.

```java
@Service
public class JwtService {
    
    public String generateToken(User user) {
        // Genera token JWT
    }
    
    public boolean isTokenValid(String token) {
        // Valida token
    }
    
    public String extractEmail(String token) {
        // Extrae email del token
    }
}
```

---

### 3. UserDetailsService

Carga información del usuario para Spring Security.

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            getAuthorities(user)
        );
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
```

---

### 4. JwtAuthenticationFilter

Filtra cada request para validar el token.

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) {
        // Validar token en cada request
    }
}
```

---

## 🎪 Ejemplo Completo: Flujo de Login

### 1. Frontend envía credenciales

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
};
```

### 2. Backend valida y genera token

```java
@PostMapping("/auth/login")
public ResponseEntity<AuthResponseDTO> login(
    @RequestBody LoginRequestDTO request
) {
    // Validar credenciales
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
    
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new UnauthorizedException("Credenciales inválidas");
    }
    
    // Generar token
    String token = jwtService.generateToken(user);
    
    return ResponseEntity.ok(
        AuthResponseDTO.builder()
            .token(token)
            .user(UserMapper.toDTO(user))
            .build()
    );
}
```

### 3. Frontend usa token en requests

```javascript
// React
const getCart = async () => {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8080/api/cart', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
};
```

### 4. Backend valida token y procesa request

```java
@GetMapping("/cart")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<CartDTO> getCart(
    @AuthenticationPrincipal UserDetails userDetails
) {
    // Spring Security ya validó el token
    // userDetails contiene la información del usuario
    String email = userDetails.getUsername();
    CartDTO cart = cartService.getCart(email);
    return ResponseEntity.ok(cart);
}
```

---

## 🎯 Resumen

| Concepto | Significado | Ejemplo BabyCash |
|----------|-------------|------------------|
| **Autenticación** | ¿Quién eres? | Login con email/password |
| **Autorización** | ¿Qué puedes hacer? | Solo ADMIN elimina productos |
| **Token JWT** | Gafete digital | Se envía en cada request |
| **Filter Chain** | Guardias de seguridad | Validan token antes del controller |
| **Principal** | Tu identidad | `@AuthenticationPrincipal UserDetails` |
| **Authorities** | Tus permisos | `ROLE_USER`, `ROLE_ADMIN` |

---

## 📚 Siguientes Temas

1. **JWT (JSON Web Tokens)**: Cómo funcionan los tokens
2. **Autenticación (Login)**: Proceso completo de login
3. **Autorización (Roles)**: USER vs ADMIN
4. **Filtros**: JwtAuthenticationFilter explicado
5. **Encriptación**: BCrypt para passwords
6. **Configuración**: SecurityConfig completo

---

**Última actualización**: Octubre 2025
