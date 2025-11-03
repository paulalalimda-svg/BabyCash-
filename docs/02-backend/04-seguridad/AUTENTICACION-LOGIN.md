# 🔐 AUTENTICACIÓN - LOGIN

## 📖 ¿Qué es la Autenticación?

**Autenticación** es el proceso de **verificar la identidad** de un usuario.

**Pregunta que responde**: "¿Quién eres?"

---

## 🎭 Analogía Simple

### Entrar a tu Casa 🏠

**1. Llegas a la puerta:**
- Dices: "Soy María"

**2. Demuestras que eres María:**
- Usas tu llave (password)
- La puerta verifica que la llave es correcta
- Si es correcta → te deja entrar ✅
- Si es incorrecta → no entras ❌

**3. Una vez adentro:**
- Puedes moverte libremente
- No necesitas demostrar quién eres en cada habitación
- Tu gafete (token) prueba que ya entraste

---

## 🔄 Flujo de Autenticación en BabyCash

```
Frontend                         Backend                      Base de Datos
   |                                |                               |
   |--- 1. POST /auth/login ------->|                               |
   |    { email, password }         |                               |
   |                                |--- 2. Buscar usuario -------->|
   |                                |<------ Usuario --------------|
   |                                |                               |
   |                                |--- 3. Verificar password      |
   |                                |    (BCrypt.matches())         |
   |                                |                               |
   |                                |--- 4. Generar JWT token       |
   |                                |                               |
   |<-- 5. { token, user } ---------|                               |
   |                                |                               |
   |--- 6. Guardar en localStorage  |                               |
   |                                |                               |
   |--- 7. Requests con token ----->|                               |
   |    Authorization: Bearer ...   |                               |
```

---

## 📝 Paso 1: Registro de Usuario

Antes de hacer login, el usuario debe registrarse.

### Frontend (React)

```javascript
// RegisterForm.jsx
import { useState } from 'react';
import axios from 'axios';

const RegisterForm = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    name: '',
    phone: '',
    address: ''
  });
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const response = await axios.post(
        'http://localhost:8080/api/auth/register',
        formData
      );
      
      // Guardar token
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user));
      
      // Redirigir
      window.location.href = '/';
      
    } catch (error) {
      if (error.response.status === 409) {
        alert('El email ya está registrado');
      } else {
        alert('Error al registrar');
      }
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <input
        type="email"
        placeholder="Email"
        value={formData.email}
        onChange={(e) => setFormData({...formData, email: e.target.value})}
        required
      />
      <input
        type="password"
        placeholder="Password"
        value={formData.password}
        onChange={(e) => setFormData({...formData, password: e.target.value})}
        required
      />
      <input
        type="text"
        placeholder="Nombre"
        value={formData.name}
        onChange={(e) => setFormData({...formData, name: e.target.value})}
        required
      />
      <button type="submit">Registrarse</button>
    </form>
  );
};
```

---

### Backend - Controller

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
        @RequestBody @Valid RegisterRequestDTO request
    ) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

---

### Backend - DTO

```java
@Data
public class RegisterRequestDTO {
    
    @NotBlank(message = "Email requerido")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "Password requerido")
    @Size(min = 6, message = "Password debe tener mínimo 6 caracteres")
    private String password;
    
    @NotBlank(message = "Nombre requerido")
    private String name;
    
    private String phone;
    private String address;
}

@Data
@Builder
public class AuthResponseDTO {
    private String token;
    private String refreshToken;
    private UserDTO user;
}

@Data
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String role;
    private Boolean active;
}
```

---

### Backend - Service

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        
        // 1. Validar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("El email ya está registrado");
        }
        
        // 2. Crear usuario
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))  // Encriptar password
            .name(request.getName())
            .phone(request.getPhone())
            .address(request.getAddress())
            .role(Role.USER)  // Por defecto USER
            .active(true)
            .build();
        
        // 3. Guardar en BD
        User savedUser = userRepository.save(user);
        
        // 4. Generar tokens
        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        
        // 5. Retornar respuesta
        return AuthResponseDTO.builder()
            .token(token)
            .refreshToken(refreshToken)
            .user(UserMapper.toDTO(savedUser))
            .build();
    }
}
```

---

## 🔑 Paso 2: Login de Usuario

### Frontend (React)

```javascript
// LoginForm.jsx
import { useState } from 'react';
import axios from 'axios';

const LoginForm = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    try {
      const response = await axios.post(
        'http://localhost:8080/api/auth/login',
        { email, password }
      );
      
      // Guardar token y usuario en localStorage
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      localStorage.setItem('user', JSON.stringify(response.data.user));
      
      // Redirigir al inicio
      window.location.href = '/';
      
    } catch (error) {
      if (error.response?.status === 401) {
        setError('Email o password incorrectos');
      } else {
        setError('Error al iniciar sesión');
      }
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <h2>Iniciar Sesión</h2>
      
      {error && <div className="error">{error}</div>}
      
      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />
      
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />
      
      <button type="submit">Entrar</button>
    </form>
  );
};

export default LoginForm;
```

---

### Backend - Controller

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
        @RequestBody @Valid LoginRequestDTO request
    ) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
```

---

### Backend - DTO

```java
@Data
public class LoginRequestDTO {
    
    @NotBlank(message = "Email requerido")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "Password requerido")
    private String password;
}
```

---

### Backend - Service

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponseDTO login(LoginRequestDTO request) {
        
        // 1. Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        
        // 2. Validar que el usuario esté activo
        if (!user.getActive()) {
            throw new UnauthorizedException("Usuario inactivo");
        }
        
        // 3. Verificar password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        
        // 4. Autenticar con Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        // 5. Generar tokens
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // 6. Retornar respuesta
        return AuthResponseDTO.builder()
            .token(token)
            .refreshToken(refreshToken)
            .user(UserMapper.toDTO(user))
            .build();
    }
}
```

---

## 🎫 Paso 3: Usar el Token en Requests

### Axios Interceptor (React)

```javascript
// api/axios.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// Interceptor para agregar token automáticamente
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar errores 401
api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;
    
    // Si es 401 y no hemos intentado renovar
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Intentar renovar token
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post(
          'http://localhost:8080/api/auth/refresh',
          { refreshToken }
        );
        
        // Guardar nuevos tokens
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('refreshToken', response.data.refreshToken);
        
        // Reintentar request original
        originalRequest.headers.Authorization = `Bearer ${response.data.token}`;
        return api(originalRequest);
        
      } catch (refreshError) {
        // Refresh token también expiró, hacer logout
        localStorage.clear();
        window.location.href = '/login';
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

---

### Usar API en Componentes

```javascript
// pages/CartPage.jsx
import { useEffect, useState } from 'react';
import api from '../api/axios';

const CartPage = () => {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    const fetchCart = async () => {
      try {
        // El interceptor agrega el token automáticamente
        const response = await api.get('/cart');
        setCart(response.data);
      } catch (error) {
        console.error('Error al cargar carrito:', error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchCart();
  }, []);
  
  if (loading) return <div>Cargando...</div>;
  
  return (
    <div>
      <h1>Mi Carrito</h1>
      {cart?.items.map(item => (
        <div key={item.id}>
          <h3>{item.productName}</h3>
          <p>Cantidad: {item.quantity}</p>
          <p>Subtotal: ${item.subtotal}</p>
        </div>
      ))}
      <h2>Total: ${cart?.total}</h2>
    </div>
  );
};

export default CartPage;
```

---

## 🔓 Paso 4: Logout

### Frontend (React)

```javascript
// components/Navbar.jsx
import { useNavigate } from 'react-router-dom';

const Navbar = () => {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user'));
  
  const handleLogout = () => {
    // Limpiar localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    
    // Redirigir a login
    navigate('/login');
  };
  
  return (
    <nav>
      <div>BabyCash</div>
      
      {user ? (
        <>
          <span>Hola, {user.name}</span>
          <button onClick={handleLogout}>Cerrar Sesión</button>
        </>
      ) : (
        <>
          <a href="/login">Iniciar Sesión</a>
          <a href="/register">Registrarse</a>
        </>
      )}
    </nav>
  );
};

export default Navbar;
```

---

## 🛡️ Protected Routes (Rutas Protegidas)

### Componente ProtectedRoute

```javascript
// components/ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  
  if (!token) {
    // No hay token, redirigir a login
    return <Navigate to="/login" replace />;
  }
  
  // Hay token, mostrar componente
  return children;
};

export default ProtectedRoute;
```

---

### Uso en App.jsx

```javascript
// App.jsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import CartPage from './pages/CartPage';
import OrdersPage from './pages/OrdersPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rutas públicas */}
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
        {/* Rutas protegidas */}
        <Route 
          path="/cart" 
          element={
            <ProtectedRoute>
              <CartPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/orders" 
          element={
            <ProtectedRoute>
              <OrdersPage />
            </ProtectedRoute>
          } 
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
```

---

## 🎯 Context API para Estado Global

### AuthContext

```javascript
// context/AuthContext.jsx
import { createContext, useState, useEffect } from 'react';
import axios from 'axios';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Cargar usuario del localStorage al inicio
  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);
  
  // Login
  const login = async (email, password) => {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      email,
      password
    });
    
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('refreshToken', response.data.refreshToken);
    localStorage.setItem('user', JSON.stringify(response.data.user));
    
    setUser(response.data.user);
  };
  
  // Logout
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    setUser(null);
  };
  
  // Register
  const register = async (formData) => {
    const response = await axios.post('http://localhost:8080/api/auth/register', formData);
    
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('refreshToken', response.data.refreshToken);
    localStorage.setItem('user', JSON.stringify(response.data.user));
    
    setUser(response.data.user);
  };
  
  return (
    <AuthContext.Provider value={{ user, login, logout, register, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
```

---

### Usar AuthContext

```javascript
// LoginPage.jsx
import { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const LoginPage = () => {
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    try {
      await login(email, password);
      navigate('/');
    } catch (error) {
      setError('Credenciales inválidas');
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      {error && <div className="error">{error}</div>}
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />
      <button type="submit">Entrar</button>
    </form>
  );
};
```

---

## 🔐 Validación en Backend

### JwtAuthenticationFilter

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
        
        String token = authHeader.substring(7);
        
        try {
            // 2. Validar token
            if (jwtService.isTokenValid(token)) {
                
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
            }
            
        } catch (Exception e) {
            log.error("Error al validar token", e);
        }
        
        // 7. Continuar con el request
        filterChain.doFilter(request, response);
    }
}
```

---

## 📊 Diagrama Completo

```
┌─────────────┐
│   Frontend  │
│   (React)   │
└──────┬──────┘
       │
       │ 1. POST /auth/login
       │    { email, password }
       ▼
┌─────────────────────────┐
│   AuthController        │
│   @PostMapping("/login")│
└──────┬──────────────────┘
       │
       │ 2. authService.login()
       ▼
┌──────────────────────────────────┐
│   AuthService                    │
│   - Buscar usuario por email     │
│   - Verificar password (BCrypt)  │
│   - Generar JWT token            │
│   - Retornar token + user        │
└──────┬───────────────────────────┘
       │
       │ 3. { token, user }
       ▼
┌─────────────┐
│   Frontend  │
│   - Guardar token en localStorage│
│   - Guardar user en localStorage │
└──────┬──────┘
       │
       │ 4. GET /cart
       │    Authorization: Bearer {token}
       ▼
┌────────────────────────────┐
│   JwtAuthenticationFilter  │
│   - Extraer token          │
│   - Validar token          │
│   - Autenticar usuario     │
└──────┬─────────────────────┘
       │
       │ 5. Usuario autenticado
       ▼
┌─────────────────┐
│   CartController│
│   @GetMapping   │
└──────┬──────────┘
       │
       │ 6. { cart data }
       ▼
┌─────────────┐
│   Frontend  │
│   Mostrar   │
└─────────────┘
```

---

## 🎯 Resumen

| Paso | Acción | Frontend | Backend |
|------|--------|----------|---------|
| **1** | Registro | POST /auth/register | Crear usuario, encriptar password, generar token |
| **2** | Login | POST /auth/login | Validar credenciales, generar token |
| **3** | Guardar token | localStorage.setItem() | - |
| **4** | Requests | Authorization: Bearer {token} | Validar token en filtro |
| **5** | Logout | localStorage.clear() | - |

---

**Última actualización**: Octubre 2025
