# 👑 AUTORIZACIÓN - ROLES Y PERMISOS

## 📖 ¿Qué es la Autorización?

**Autorización** es el proceso de **verificar qué puede hacer** un usuario autenticado.

**Pregunta que responde**: "¿Qué permisos tienes?"

---

## 🎭 Diferencia: Autenticación vs Autorización

### Autenticación ✅

**¿Quién eres?**

```
Usuario: "Soy María"
Sistema: "Demuéstralo con email y password"
Usuario: maria@gmail.com / password123
Sistema: "✅ Correcto, eres María"
```

### Autorización 👑

**¿Qué puedes hacer?**

```
María: "Quiero eliminar un producto"
Sistema: "Veamos... tu rol es USER"
Sistema: "❌ Solo ADMIN puede eliminar productos"

Admin: "Quiero eliminar un producto"
Sistema: "Veamos... tu rol es ADMIN"
Sistema: "✅ Adelante, puedes hacerlo"
```

---

## 🎭 Analogía Simple

### Edificio con Niveles de Acceso 🏢

Imagina un edificio de oficinas:

**👤 Empleado Normal (USER):**
- ✅ Puede entrar al edificio
- ✅ Puede usar su escritorio
- ✅ Puede ir al comedor
- ❌ NO puede entrar a la sala de servidores
- ❌ NO puede acceder a finanzas

**👔 Gerente (MODERATOR):**
- ✅ Todo lo del empleado normal
- ✅ Puede acceder a reportes
- ✅ Puede aprobar solicitudes
- ❌ NO puede entrar a la sala de servidores

**👑 CEO (ADMIN):**
- ✅ Acceso TOTAL
- ✅ Puede entrar a todos los pisos
- ✅ Puede acceder a la sala de servidores
- ✅ Puede contratar/despedir

---

## 🔑 Roles en BabyCash

### 1. USER (Usuario Normal)

**Qué puede hacer:**
- ✅ Ver productos
- ✅ Agregar al carrito
- ✅ Crear órdenes
- ✅ Ver sus propias órdenes
- ✅ Ver su perfil
- ✅ Actualizar su perfil

**Qué NO puede hacer:**
- ❌ Crear productos
- ❌ Eliminar productos
- ❌ Ver órdenes de otros usuarios
- ❌ Ver lista de todos los usuarios

---

### 2. ADMIN (Administrador)

**Qué puede hacer:**
- ✅ TODO lo que puede hacer USER
- ✅ Crear productos
- ✅ Actualizar productos
- ✅ Eliminar productos
- ✅ Ver todas las órdenes
- ✅ Actualizar estado de órdenes
- ✅ Ver todos los usuarios
- ✅ Crear posts de blog
- ✅ Eliminar posts

**Qué NO puede hacer:**
- (Tiene acceso total en BabyCash)

---

### 3. MODERATOR (Moderador) - Opcional

**Qué puede hacer:**
- ✅ TODO lo que puede hacer USER
- ✅ Ver todas las órdenes
- ✅ Actualizar estado de órdenes
- ✅ Crear posts de blog
- ❌ NO puede crear/eliminar productos

---

## 📦 Entidad Role en Base de Datos

### Enum Role

```java
public enum Role {
    USER,
    ADMIN,
    MODERATOR
}
```

---

### Entidad User con Role

```java
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String name;
    private String phone;
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // ← USER, ADMIN, MODERATOR
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

---

## 🛡️ Implementar Autorización con @PreAuthorize

### 1. Habilitar Method Security

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // ← Habilita @PreAuthorize
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

### 2. Uso Básico de @PreAuthorize

#### Requiere Autenticación

```java
@GetMapping("/cart")
@PreAuthorize("isAuthenticated()")  // ← Requiere estar logueado
public ResponseEntity<CartDTO> getCart(
    @AuthenticationPrincipal UserDetails userDetails
) {
    String email = userDetails.getUsername();
    CartDTO cart = cartService.getCart(email);
    return ResponseEntity.ok(cart);
}
```

---

#### Requiere Rol ADMIN

```java
@PostMapping("/products")
@PreAuthorize("hasRole('ADMIN')")  // ← Solo ADMIN
public ResponseEntity<ProductDTO> createProduct(
    @RequestBody @Valid ProductDTO dto
) {
    ProductDTO created = productService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}

@DeleteMapping("/products/{id}")
@PreAuthorize("hasRole('ADMIN')")  // ← Solo ADMIN
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

---

#### Requiere ADMIN o MODERATOR

```java
@GetMapping("/orders/all")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")  // ← ADMIN o MODERATOR
public ResponseEntity<List<OrderDTO>> getAllOrders() {
    List<OrderDTO> orders = orderService.findAll();
    return ResponseEntity.ok(orders);
}

@PutMapping("/orders/{id}/status")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")  // ← ADMIN o MODERATOR
public ResponseEntity<OrderDTO> updateOrderStatus(
    @PathVariable Long id,
    @RequestBody UpdateStatusDTO dto
) {
    OrderDTO updated = orderService.updateStatus(id, dto.getStatus());
    return ResponseEntity.ok(updated);
}
```

---

### 3. Expresiones Avanzadas

#### Usuario es dueño del recurso

```java
@GetMapping("/users/{id}")
@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
    // ADMIN puede ver cualquier usuario
    // USER solo puede ver su propio perfil
    UserDTO user = userService.findById(id);
    return ResponseEntity.ok(user);
}
```

---

#### Combinaciones complejas

```java
@PutMapping("/orders/{orderId}")
@PreAuthorize(
    "hasRole('ADMIN') or " +
    "(hasRole('USER') and @orderService.isOrderOwner(#orderId, authentication.principal.username))"
)
public ResponseEntity<OrderDTO> updateOrder(
    @PathVariable Long orderId,
    @RequestBody UpdateOrderDTO dto
) {
    // ADMIN puede actualizar cualquier orden
    // USER solo puede actualizar sus propias órdenes
    OrderDTO updated = orderService.update(orderId, dto);
    return ResponseEntity.ok(updated);
}
```

---

## 🎯 Ejemplos Completos por Rol

### USER - ProductController

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    // ✅ Público - Cualquiera puede ver
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.findAll();
        return ResponseEntity.ok(products);
    }
    
    // ✅ Público - Cualquiera puede ver
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }
    
    // ❌ Solo ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
        @RequestBody @Valid ProductDTO dto
    ) {
        ProductDTO created = productService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // ❌ Solo ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
        @PathVariable Long id,
        @RequestBody @Valid ProductDTO dto
    ) {
        ProductDTO updated = productService.update(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    // ❌ Solo ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### USER - CartController

```java
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    // ✅ Requiere autenticación (USER, ADMIN, MODERATOR)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDTO> getCart(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        CartDTO cart = cartService.getCart(email);
        return ResponseEntity.ok(cart);
    }
    
    // ✅ Requiere autenticación
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDTO> addToCart(
        @RequestBody @Valid AddToCartDTO dto,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        CartDTO cart = cartService.addToCart(email, dto);
        return ResponseEntity.ok(cart);
    }
    
    // ✅ Requiere autenticación
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
```

---

### USER + ADMIN - OrderController

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    // ✅ USER: Ve solo sus órdenes
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDTO>> getMyOrders(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        List<OrderDTO> orders = orderService.findByUser(email);
        return ResponseEntity.ok(orders);
    }
    
    // ✅ ADMIN o MODERATOR: Ve todas las órdenes
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }
    
    // ✅ USER: Crea su propia orden
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDTO> createOrder(
        @RequestBody @Valid CreateOrderDTO dto,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        OrderDTO order = orderService.createOrder(email, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    // ❌ Solo ADMIN o MODERATOR: Actualiza estado
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
        @PathVariable Long id,
        @RequestBody UpdateStatusDTO dto
    ) {
        OrderDTO updated = orderService.updateStatus(id, dto.getStatus());
        return ResponseEntity.ok(updated);
    }
}
```

---

## 🔐 UserDetailsService con Roles

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Buscar usuario
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        // 2. Crear UserDetails con rol
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            user.getActive(),      // enabled
            true,                   // accountNonExpired
            true,                   // credentialsNonExpired
            true,                   // accountNonLocked
            getAuthorities(user)    // ← Roles/Autoridades
        );
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // Convertir Role a GrantedAuthority
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        // USER → ROLE_USER
        // ADMIN → ROLE_ADMIN
    }
}
```

---

## 🎪 Crear Usuario Admin

### Script SQL

```sql
-- Insertar admin en base de datos
INSERT INTO users (email, password, name, role, active, created_at)
VALUES (
    'admin@babycash.com',
    '$2a$10$xQhR5Z8Z3Y2Z8Z8Z8Z8Z8O',  -- password: admin123 (encriptado con BCrypt)
    'Administrador',
    'ADMIN',
    true,
    NOW()
);
```

---

### Código Java (DataInitializer)

```java
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        
        // Crear admin si no existe
        if (!userRepository.existsByEmail("admin@babycash.com")) {
            
            User admin = User.builder()
                .email("admin@babycash.com")
                .password(passwordEncoder.encode("admin123"))
                .name("Administrador")
                .role(Role.ADMIN)
                .active(true)
                .build();
            
            userRepository.save(admin);
            
            System.out.println("✅ Usuario ADMIN creado:");
            System.out.println("   Email: admin@babycash.com");
            System.out.println("   Password: admin123");
        }
    }
}
```

---

## 🎯 Frontend: Ocultar Botones según Rol

### Verificar Rol en React

```javascript
// utils/auth.js
export const getUser = () => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
};

export const isAdmin = () => {
  const user = getUser();
  return user?.role === 'ADMIN';
};

export const isModerator = () => {
  const user = getUser();
  return user?.role === 'MODERATOR';
};

export const isUser = () => {
  const user = getUser();
  return user?.role === 'USER';
};

export const hasRole = (...roles) => {
  const user = getUser();
  return roles.includes(user?.role);
};
```

---

### Componente con Roles

```javascript
// pages/ProductDetailPage.jsx
import { isAdmin } from '../utils/auth';

const ProductDetailPage = ({ product }) => {
  
  return (
    <div>
      <h1>{product.name}</h1>
      <p>{product.description}</p>
      <p>Precio: ${product.price}</p>
      
      {/* Botones visibles solo para ADMIN */}
      {isAdmin() && (
        <div className="admin-actions">
          <button onClick={() => handleEdit(product.id)}>
            Editar
          </button>
          <button onClick={() => handleDelete(product.id)}>
            Eliminar
          </button>
        </div>
      )}
      
      {/* Botón visible para todos los usuarios autenticados */}
      <button onClick={() => handleAddToCart(product.id)}>
        Agregar al Carrito
      </button>
    </div>
  );
};
```

---

### Rutas Protegidas por Rol

```javascript
// components/AdminRoute.jsx
import { Navigate } from 'react-router-dom';
import { isAdmin } from '../utils/auth';

const AdminRoute = ({ children }) => {
  if (!isAdmin()) {
    return <Navigate to="/" replace />;
  }
  
  return children;
};

export default AdminRoute;
```

```javascript
// App.jsx
import AdminRoute from './components/AdminRoute';
import AdminDashboard from './pages/AdminDashboard';

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      
      {/* Ruta solo para ADMIN */}
      <Route 
        path="/admin" 
        element={
          <AdminRoute>
            <AdminDashboard />
          </AdminRoute>
        } 
      />
    </Routes>
  );
}
```

---

## 🚨 Manejo de Errores 403 Forbidden

### Backend

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
        AccessDeniedException ex
    ) {
        ErrorResponse error = new ErrorResponse(
            "Forbidden",
            "No tienes permiso para realizar esta acción",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
```

---

### Frontend

```javascript
// api/axios.js
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 403) {
      alert('No tienes permiso para realizar esta acción');
    }
    return Promise.reject(error);
  }
);
```

---

## 📊 Tabla Resumen de Permisos

| Endpoint | USER | MODERATOR | ADMIN |
|----------|------|-----------|-------|
| **Products** |
| GET /api/products | ✅ | ✅ | ✅ |
| GET /api/products/{id} | ✅ | ✅ | ✅ |
| POST /api/products | ❌ | ❌ | ✅ |
| PUT /api/products/{id} | ❌ | ❌ | ✅ |
| DELETE /api/products/{id} | ❌ | ❌ | ✅ |
| **Cart** |
| GET /api/cart | ✅ | ✅ | ✅ |
| POST /api/cart/add | ✅ | ✅ | ✅ |
| DELETE /api/cart/items/{id} | ✅ | ✅ | ✅ |
| **Orders** |
| GET /api/orders | ✅ (propias) | ✅ (todas) | ✅ (todas) |
| POST /api/orders | ✅ | ✅ | ✅ |
| PUT /api/orders/{id}/status | ❌ | ✅ | ✅ |
| **Users** |
| GET /api/users/me | ✅ | ✅ | ✅ |
| PUT /api/users/me | ✅ | ✅ | ✅ |
| GET /api/users | ❌ | ❌ | ✅ |
| **Blog** |
| GET /api/blog | ✅ | ✅ | ✅ |
| POST /api/blog | ❌ | ✅ | ✅ |
| DELETE /api/blog/{id} | ❌ | ❌ | ✅ |

---

## 🎯 Resumen

| Concepto | Significado | Ejemplo |
|----------|-------------|---------|
| **Autorización** | ¿Qué puedes hacer? | Solo ADMIN elimina productos |
| **Role** | Nivel de acceso | USER, ADMIN, MODERATOR |
| **@PreAuthorize** | Anotación para proteger endpoints | `@PreAuthorize("hasRole('ADMIN')")` |
| **hasRole()** | Verificar un rol | `hasRole('ADMIN')` |
| **hasAnyRole()** | Verificar múltiples roles | `hasAnyRole('ADMIN', 'MODERATOR')` |
| **isAuthenticated()** | Usuario logueado | Cualquier usuario con token |
| **403 Forbidden** | Sin permiso | USER intenta eliminar producto |

---

**Última actualización**: Octubre 2025
