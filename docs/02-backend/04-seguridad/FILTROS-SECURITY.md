# 🔍 FILTROS DE SEGURIDAD

## 📖 ¿Qué son los Filtros de Seguridad?

Los **filtros de seguridad** son como **guardias de seguridad** que revisan cada request **antes** de que llegue a tu controller.

---

## 🎭 Analogía Simple

### Aeropuerto ✈️

Imagina que vas a viajar en avión:

**1. Punto de Control (Filter):**
- Llegas al aeropuerto
- Hay varios **checkpoints** antes de abordar

**2. Verificación de Documentos (JwtAuthenticationFilter):**
- Guardia 1: "Muéstrame tu pasaporte" (token)
- Verifica que sea válido
- Si es válido → sigues adelante
- Si NO → te detienen ❌

**3. Seguridad (Authorization Filter):**
- Guardia 2: "¿Tu boleto es para clase ejecutiva o económica?" (rol)
- Verifica tus permisos
- Si tienes acceso → pasas
- Si NO → te redirigen

**4. Abordas el Avión (Controller):**
- Ya pasaste todos los filtros
- Puedes abordar (ejecutar el endpoint)

**Filtros de Spring Security = Esos checkpoints** 🛂

---

## 🔗 Filter Chain (Cadena de Filtros)

Spring Security usa una **cadena de filtros** que procesa cada request en orden.

### Flujo Completo

```
Request → [Filter 1] → [Filter 2] → [Filter 3] → Controller
                ↓          ↓          ↓
            Si falla    Si falla    Si falla
                ↓          ↓          ↓
            Response   Response   Response
             (401)      (401)      (403)
```

---

### Diagrama Detallado

```
Cliente
   │
   │ GET /api/cart
   │ Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
   │
   ▼
┌─────────────────────────────────┐
│  1. CORS Filter                 │  ← Permite requests desde frontend
│     - Verifica origen           │
│     - Agrega headers CORS       │
└──────────┬──────────────────────┘
           │ ✅ Permitido
           ▼
┌─────────────────────────────────┐
│  2. JwtAuthenticationFilter     │  ← Valida token JWT
│     - Extrae token del header   │
│     - Valida firma              │
│     - Extrae email del token    │
│     - Carga usuario             │
│     - Crea autenticación        │
└──────────┬──────────────────────┘
           │ ✅ Token válido
           │ ❌ Token inválido → 401
           ▼
┌─────────────────────────────────┐
│  3. Authorization Filter        │  ← Verifica permisos
│     - Lee @PreAuthorize         │
│     - Verifica rol del usuario  │
└──────────┬──────────────────────┘
           │ ✅ Tiene permiso
           │ ❌ Sin permiso → 403
           ▼
┌─────────────────────────────────┐
│  4. Exception Handler Filter    │  ← Maneja errores
└──────────┬──────────────────────┘
           │
           ▼
┌─────────────────────────────────┐
│  CartController                 │  ← Tu código
│  @GetMapping("/cart")           │
└─────────────────────────────────┘
```

---

## 🔐 JwtAuthenticationFilter (Filtro Principal)

Este es el filtro más importante en BabyCash. Valida el token JWT en cada request.

### Código Completo

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Extraer header Authorization
        String authHeader = request.getHeader("Authorization");
        
        // 2. Verificar que exista y sea Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No hay token, continuar sin autenticación
            filterChain.doFilter(request, response);
            return;
        }
        
        // 3. Extraer token (quitar "Bearer ")
        String token = authHeader.substring(7);
        
        try {
            // 4. Validar que el token sea válido
            if (jwtService.isTokenValid(token)) {
                
                // 5. Extraer email del token
                String email = jwtService.extractEmail(token);
                
                // 6. Verificar si ya está autenticado
                Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                
                if (email != null && existingAuth == null) {
                    
                    // 7. Cargar detalles del usuario
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    
                    // 8. Crear objeto de autenticación
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,              // Principal (usuario)
                            null,                     // Credentials (no necesarias)
                            userDetails.getAuthorities()  // Roles (ROLE_USER, ROLE_ADMIN)
                        );
                    
                    // 9. Agregar detalles adicionales
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // 10. Guardar autenticación en SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            
        } catch (Exception e) {
            // Token inválido o expirado
            log.error("Error al validar token: {}", e.getMessage());
        }
        
        // 11. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
```

---

### Explicación Paso a Paso

#### Paso 1: Extraer Header

```java
String authHeader = request.getHeader("Authorization");
// Ejemplo: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

#### Paso 2: Verificar formato Bearer

```java
if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    filterChain.doFilter(request, response);
    return;
}
```

**¿Por qué continuar sin autenticación?**
- Hay rutas públicas (productos, blog)
- El filtro no bloquea, solo autentica si hay token
- La autorización se maneja después con `@PreAuthorize`

---

#### Paso 3: Extraer token

```java
String token = authHeader.substring(7);  // Quitar "Bearer "
// Resultado: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

#### Paso 4-5: Validar token y extraer email

```java
if (jwtService.isTokenValid(token)) {
    String email = jwtService.extractEmail(token);
    // email = "maria@gmail.com"
}
```

---

#### Paso 6: Verificar si ya está autenticado

```java
Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

if (email != null && existingAuth == null) {
    // Solo autenticar si no está ya autenticado
}
```

**¿Por qué?**
- Evitar cargar el usuario múltiples veces
- OncePerRequestFilter ya garantiza una ejecución por request

---

#### Paso 7: Cargar usuario

```java
UserDetails userDetails = userDetailsService.loadUserByUsername(email);
```

Llama a `CustomUserDetailsService.loadUserByUsername()`:

```java
@Override
public UserDetails loadUserByUsername(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
    );
}
```

---

#### Paso 8: Crear autenticación

```java
UsernamePasswordAuthenticationToken authentication = 
    new UsernamePasswordAuthenticationToken(
        userDetails,                      // Principal
        null,                             // Credentials
        userDetails.getAuthorities()      // Authorities (roles)
    );
```

---

#### Paso 9-10: Guardar en SecurityContext

```java
SecurityContextHolder.getContext().setAuthentication(authentication);
```

**Ahora el usuario está autenticado** y puede usar:

```java
@GetMapping("/cart")
public ResponseEntity<CartDTO> getCart(
    @AuthenticationPrincipal UserDetails userDetails  // ← Disponible aquí
) {
    String email = userDetails.getUsername();
    // ...
}
```

---

#### Paso 11: Continuar cadena

```java
filterChain.doFilter(request, response);
```

Pasa al siguiente filtro o al controller.

---

## 🚨 Exception Handler Filter

Maneja errores de autenticación y autorización.

### JwtAuthenticationEntryPoint

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        
        // Se ejecuta cuando:
        // - No hay token
        // - Token inválido
        // - Token expirado
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
            "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
            authException.getMessage(),
            LocalDateTime.now()
        );
        
        response.getWriter().write(jsonResponse);
    }
}
```

---

### JwtAccessDeniedHandler

```java
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {
        
        // Se ejecuta cuando:
        // - Usuario autenticado pero sin permiso (403)
        // - USER intenta acceder a endpoint de ADMIN
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
            "{\"error\":\"Forbidden\",\"message\":\"No tienes permiso para acceder a este recurso\",\"timestamp\":\"%s\"}",
            LocalDateTime.now()
        );
        
        response.getWriter().write(jsonResponse);
    }
}
```

---

## 🔧 Configuración de Filtros

### SecurityConfig

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
            // Deshabilitar CSRF (no necesario para API REST con JWT)
            .csrf(csrf -> csrf.disable())
            
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configurar autorización de rutas
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
                
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Sin estado (stateless) - No usar sesiones
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Manejadores de excepciones
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 401
                .accessDeniedHandler(jwtAccessDeniedHandler)            // 403
            )
            
            // Agregar filtro JWT ANTES del filtro de autenticación de Spring
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

## 🎪 Orden de Ejecución de Filtros

### Filtros de Spring Security (orden)

```
1. SecurityContextPersistenceFilter
   ↓
2. CorsFilter  ← Configurado en SecurityConfig
   ↓
3. JwtAuthenticationFilter  ← Nuestro filtro personalizado
   ↓
4. UsernamePasswordAuthenticationFilter
   ↓
5. FilterSecurityInterceptor  ← Verifica @PreAuthorize
   ↓
6. ExceptionTranslationFilter  ← Maneja excepciones
   ↓
Controller
```

---

## 🔍 Debugging: Ver qué filtros se ejecutan

### Habilitar logs de Spring Security

```properties
# application.properties
logging.level.org.springframework.security=DEBUG
```

**Salida en consola:**

```
Security filter chain: [
  DisableEncodeUrlFilter
  WebAsyncManagerIntegrationFilter
  SecurityContextHolderFilter
  HeaderWriterFilter
  CorsFilter
  CsrfFilter
  LogoutFilter
  JwtAuthenticationFilter        ← Nuestro filtro
  RequestCacheAwareFilter
  SecurityContextHolderAwareRequestFilter
  AnonymousAuthenticationFilter
  ExceptionTranslationFilter
  AuthorizationFilter
]
```

---

## 🎯 Casos de Uso

### Caso 1: Request sin token (Ruta pública)

```http
GET /api/products HTTP/1.1
```

**Flujo:**
1. JwtAuthenticationFilter detecta que no hay token
2. Continúa sin autenticación
3. Controller ejecuta (ruta pública)
4. Retorna 200 OK ✅

---

### Caso 2: Request sin token (Ruta protegida)

```http
GET /api/cart HTTP/1.1
```

**Flujo:**
1. JwtAuthenticationFilter detecta que no hay token
2. Continúa sin autenticación
3. Authorization Filter ve `@PreAuthorize("isAuthenticated()")`
4. Usuario NO está autenticado
5. Llama a JwtAuthenticationEntryPoint
6. Retorna 401 Unauthorized ❌

---

### Caso 3: Request con token válido

```http
GET /api/cart HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Flujo:**
1. JwtAuthenticationFilter extrae token
2. Valida token ✅
3. Extrae email del token
4. Carga usuario
5. Crea autenticación
6. Guarda en SecurityContext
7. Controller recibe usuario autenticado
8. Retorna 200 OK ✅

---

### Caso 4: Request con token inválido

```http
GET /api/cart HTTP/1.1
Authorization: Bearer token_invalido_123
```

**Flujo:**
1. JwtAuthenticationFilter extrae token
2. Intenta validar token ❌
3. Token inválido, catch exception
4. NO crea autenticación
5. Continúa sin autenticación
6. Authorization Filter detecta que no está autenticado
7. Retorna 401 Unauthorized ❌

---

### Caso 5: Request con token válido pero sin permiso

```http
DELETE /api/products/1 HTTP/1.1
Authorization: Bearer {user_token}  ← Token de USER
```

**Flujo:**
1. JwtAuthenticationFilter valida token ✅
2. Usuario autenticado como USER
3. Authorization Filter ve `@PreAuthorize("hasRole('ADMIN')")`
4. Usuario es USER, NO ADMIN ❌
5. Llama a JwtAccessDeniedHandler
6. Retorna 403 Forbidden ❌

---

## 🛠️ Testing de Filtros

### Test con MockMvc

```java
@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)  // Deshabilitar filtros para test
class CartControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CartService cartService;
    
    @Test
    void testGetCart_WithoutToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/cart"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "maria@gmail.com", roles = {"USER"})
    void testGetCart_WithToken_Returns200() throws Exception {
        CartDTO cart = new CartDTO();
        when(cartService.getCart(any())).thenReturn(cart);
        
        mockMvc.perform(get("/api/cart"))
            .andExpect(status().isOk());
    }
}
```

---

## 🎯 Resumen

| Componente | Función | Cuándo se ejecuta |
|------------|---------|-------------------|
| **JwtAuthenticationFilter** | Valida token JWT | En cada request |
| **JwtAuthenticationEntryPoint** | Maneja errores 401 | Token inválido/faltante |
| **JwtAccessDeniedHandler** | Maneja errores 403 | Sin permiso |
| **SecurityFilterChain** | Configura filtros | Al iniciar aplicación |
| **@PreAuthorize** | Verifica permisos | Antes del controller |

---

## 🔗 Flujo Completo Resumido

```
Request con token
      ↓
JwtAuthenticationFilter
  ├─ Extraer token
  ├─ Validar token
  ├─ Cargar usuario
  └─ Guardar en SecurityContext
      ↓
Authorization Filter
  ├─ Verificar @PreAuthorize
  └─ Verificar rol
      ↓
Controller
  └─ Ejecutar método
      ↓
Response
```

---

**Última actualización**: Octubre 2025
